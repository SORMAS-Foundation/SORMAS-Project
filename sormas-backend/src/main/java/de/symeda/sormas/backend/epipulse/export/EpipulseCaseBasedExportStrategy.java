/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package de.symeda.sormas.backend.epipulse.export;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epipulse.EpipulseCaseSubset;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePathogenTestTypeRef;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;
import de.symeda.sormas.backend.epipulse.fieldmodel.ExportContext;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.sql.EpipulseCommonSql;
import de.symeda.sormas.backend.epipulse.sql.SqlQueryModel;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * Runs one case-based EpiPulse export for any subject code.
 *
 * <p>
 * Disease-specific behaviour lives in {@link FieldModel}; this strategy executes the assembled SQL,
 * binds shared parameters, and maps rows to export DTOs.
 */
@Stateless
@LocalBean
public class EpipulseCaseBasedExportStrategy {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private EpipulseConfigurationLookupService configLookupService;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;

	/**
	 * Executes one export query and maps result rows to entry DTOs.
	 */
	public EpipulseDiseaseExportResult export(
		FieldModel fieldModel,
		EpipulseExportDto exportDto,
		String serverCountryLocale,
		String serverCountryName)
		throws SQLException, IllegalStateException, IllegalArgumentException {

		EpipulseDiseaseExportResult exportResult = new EpipulseDiseaseExportResult();

		try {
			EpipulseConfigurationContext config = configLookupService.lookupConfiguration(exportDto, serverCountryLocale, serverCountryName);

			// Deliberately Object[] rather than using Tuple[], though named access is what the readers actually want.
			// The Tuple[] results in usage of two LinkedHashMaps and
			// lowercases every column label once per *row*; FieldModel builds one alias-to-position
			// map once per bean and reuses it for the whole export.
			// Tuple also keys off the column label the database returns,
			// not the alias a ValueDef declares - PostgreSQL folds unquoted aliases to lower case,
			// so the first alias written with a capital in it would compile and then fail at runtime;
			// it resolves a duplicate alias by last-one-wins where FieldModel's constructor throws.
			//
			// The safety Tuple[] would buy is already structural here: the map is derived from the
			// very list that generates the SELECT, so the two cannot disagree. That holds only
			// while the field model owns every column in the query, which is what
			// FieldModel's selectOffset exists to relax. If some other producer ever contributes
			// columns ahead of the model's, revisit this.
			Query query = em.createNativeQuery(buildQuery(fieldModel, exportDto.getSubjectCode().getCaseSubset()));
			setQueryParameters(query, exportDto, config, serverCountryLocale);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = query.getResultList();

			exportResult.setExportEntryList(mapResultsToEntryDtos(fieldModel, resultList, exportDto, config.getServerCountryNutsCode()));

		} catch (Exception e) {
			// A mechanism should be introduced to report the errors in the UI or output to the resulting CSV (existing pattern already in sormas).
			// Currenlty the user can only see that an export failed but not know the cause.
			logger.error("Error while exporting case based " + exportDto.getSubjectCode() + ":" + e.getMessage(), e);
			throw e;
		}

		return exportResult;
	}

	/**
	 * Builds full SQL from common and disease query models plus model select columns.
	 */
	String buildQuery(FieldModel fieldModel, EpipulseCaseSubset caseSubset) {

		SqlQueryModel sql = EpipulseCommonSql.spec(configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG), caseSubset)
			.plus(fieldModel.getSqlQueryModel());

		return sql.withClause() + "SELECT " + fieldModel.buildSelectColumns() + sql.fromAndJoins() + " ORDER BY c.reportdate";
	}

	/**
	 * Binds shared export parameters.
	 */
	private void setQueryParameters(Query query, EpipulseExportDto exportDto, EpipulseConfigurationContext config, String serverCountryLocale) {
		query.setParameter("disease", exportDto.getSubjectCode().getDisease().name());

		// Bound only when the query carries the placeholder - filtered_cases omits it for a subject
		// code that reports its whole disease, and binding a parameter the statement does not
		// declare is an error rather than a no-op.
		EpipulseCaseSubset caseSubset = exportDto.getSubjectCode().getCaseSubset();
		if (caseSubset != null) {
			query.setParameter("caseSubset", caseSubset.getValue());
		}

		query.setParameter("subjectCode", config.getSubjectCode());
		query.setParameter("countryLocale", serverCountryLocale); // Use ISO2 country code (e.g., "LU"), not full subject code
		setTypedParameter(query, "startDate", DateHelper.convertDateToDbFormat(exportDto.getStartDate()), StandardBasicTypes.STRING);
		setTypedParameter(query, "endDate", DateHelper.convertDateToDbFormat(exportDto.getEndDate()), StandardBasicTypes.STRING);

		// The incubation window PlaceOfInfection filters exposures by. 
		// Taken from the facade rather than from the Disease enum so that an instance which tuned the period in
		// diseaseconfiguration is honoured; null when the disease has no period configured, which
		// the SQL reads as "window unknown" and skips.
		Disease disease = exportDto.getSubjectCode().getDisease();
		boolean incubationPeriodKnown = diseaseConfigurationFacade.isIncubationPeriodEnabled(disease);
		setTypedParameter(
			query,
			"minIncubationDays",
			incubationPeriodKnown ? diseaseConfigurationFacade.getMinIncubationPeriod(disease) : null,
			StandardBasicTypes.INTEGER);
		setTypedParameter(
			query,
			"maxIncubationDays",
			incubationPeriodKnown ? diseaseConfigurationFacade.getMaxIncubationPeriod(disease) : null,
			StandardBasicTypes.INTEGER);
	}

	/**
	 * Binds a parameter with explicit SQL type for nullable values.
	 */
	private static void setTypedParameter(Query query, String name, Object value, Type type) {
		query.unwrap(NativeQuery.class).setParameter(name, value, type);
	}

	/**
	 * Maps raw query rows to export entry DTOs via the field model.
	 */
	private List<EpipulseDiseaseExportEntryDto> mapResultsToEntryDtos(
		FieldModel fieldModel,
		List<Object[]> resultList,
		EpipulseExportDto exportDto,
		String serverCountryNutsCode) {

		List<EpipulseDiseaseExportEntryDto> exportEntryList = new ArrayList<>();
		List<PathogenTestType> subjectCodePathogenTestTypes = EpipulsePathogenTestTypeRef.getPathogenTestTypesByDisease(exportDto.getSubjectCode());

		ExportContext context = new ExportContext(serverCountryNutsCode, subjectCodePathogenTestTypes);

		for (Object[] row : resultList) {
			EpipulseDiseaseExportEntryDto dto = new EpipulseDiseaseExportEntryDto();
			fieldModel.readInto(dto, row, context);
			exportEntryList.add(dto);
		}

		return exportEntryList;
	}
}
