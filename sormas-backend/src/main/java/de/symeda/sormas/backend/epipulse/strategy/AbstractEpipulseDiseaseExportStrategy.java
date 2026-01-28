/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.epipulse.strategy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePathogenTestTypeRef;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.epipulse.EpipulseCommonDtoMapper;
import de.symeda.sormas.backend.epipulse.EpipulseConfigurationLookupService;
import de.symeda.sormas.backend.epipulse.EpipulseSqlCteBuilder;
import de.symeda.sormas.backend.epipulse.util.EpipulseConfigurationContext;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * Abstract base class implementing the Template Method pattern for Epipulse disease exports.
 * This class defines the overall export algorithm while allowing subclasses to customize
 * disease-specific behavior through abstract methods.
 * <p>
 * The template method {@link #export(EpipulseExportDto, String, String)} orchestrates:
 * 1. Configuration lookup (common)
 * 2. SQL query building (disease-specific hook)
 * 3. Query execution (common)
 * 4. DTO mapping (combination of common + disease-specific)
 * 5. Result building with max counts (common + disease-specific)
 */
public abstract class AbstractEpipulseDiseaseExportStrategy {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	protected EpipulseConfigurationLookupService configLookupService;

	@EJB
	protected EpipulseSqlCteBuilder sqlCteBuilder;

	/**
	 * Template method that orchestrates the entire export process.
	 * This method defines the algorithm structure and delegates disease-specific
	 * variations to abstract methods implemented by subclasses.
	 * <p>
	 * Note: This method should not be overridden by subclasses (template method pattern).
	 * The 'final' modifier was removed to allow EJB proxy generation.
	 *
	 * @param exportDto
	 *            the export request containing subject code, date range, etc.
	 * @param serverCountryLocale
	 *            the server country ISO2 code (e.g., "LU")
	 * @param serverCountryName
	 *            the server country name (e.g., "Luxembourg")
	 * @return the export result containing all case entries and max counts
	 * @throws SQLException
	 *             if database query execution fails
	 * @throws IllegalStateException
	 *             if configuration lookup fails
	 * @throws IllegalArgumentException
	 *             if parameters are invalid
	 */
	public EpipulseDiseaseExportResult export(EpipulseExportDto exportDto, String serverCountryLocale, String serverCountryName)
		throws SQLException, IllegalStateException, IllegalArgumentException {

		EpipulseDiseaseExportResult exportResult = new EpipulseDiseaseExportResult();

		try {
			// Step 1: Lookup configuration (common)
			EpipulseConfigurationContext config = configLookupService.lookupConfiguration(exportDto, serverCountryLocale, serverCountryName);

			// Step 2: Build disease-specific query (template method hook)
			String queryString = buildDiseaseExportQuery();

			// Step 3: Execute query with parameters (common)
			Query query = em.createNativeQuery(queryString);
			setQueryParameters(query, exportDto, config, serverCountryLocale);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = query.getResultList();

			// Step 4: Map results to DTOs (combination of common + disease-specific)
			List<EpipulseDiseaseExportEntryDto> exportEntryList = mapResultsToEntryDtos(resultList, exportDto, config.getServerCountryNutsCode());

			// Step 5: Calculate max counts and build result
			EpipulseCommonDtoMapper.calculateCommonMaxCounts(exportEntryList, exportResult);
			calculateDiseaseSpecificMaxCounts(exportEntryList, exportResult);

			exportResult.setExportEntryList(exportEntryList);

		} catch (Exception e) {
			logger.error("Error while exporting case based " + exportDto.getSubjectCode() + ":" + e.getMessage(), e);
			throw e;
		}

		return exportResult;
	}

	/**
	 * Builds the complete SQL query for the disease export.
	 * Subclasses must implement this to construct the full query including:
	 * - Common CTEs (via sqlCteBuilder)
	 * - Disease-specific CTEs
	 * - Main SELECT clause
	 *
	 * @return the complete SQL query string
	 */
	protected abstract String buildDiseaseExportQuery();

	/**
	 * Maps disease-specific fields from the database row to the DTO.
	 * This method is called after common fields have been mapped.
	 *
	 * @param dto
	 *            the DTO to populate
	 * @param row
	 *            the database result row
	 * @param startIndex
	 *            the index where disease-specific fields start (typically 27)
	 */
	protected abstract void mapDiseaseSpecificFields(EpipulseDiseaseExportEntryDto dto, Object[] row, int startIndex);

	/**
	 * Calculates disease-specific max counts for repeatable fields.
	 * Common max counts (pathogenTests, immunizations) are handled by EpipulseCommonDtoMapper.
	 *
	 * @param entries
	 *            the list of export entries
	 * @param result
	 *            the result object to populate with max counts
	 */
	protected abstract void calculateDiseaseSpecificMaxCounts(List<EpipulseDiseaseExportEntryDto> entries, EpipulseDiseaseExportResult result);

	/**
	 * Sets common query parameters on the prepared query.
	 * Parameter binding is identical for all diseases.
	 * This method is private to prevent overriding by subclasses.
	 *
	 * @param query
	 *            the query to set parameters on
	 * @param exportDto
	 *            the export request DTO
	 * @param config
	 *            the configuration context
	 */
	private void setQueryParameters(Query query, EpipulseExportDto exportDto, EpipulseConfigurationContext config, String serverCountryLocale) {
		query.setParameter("disease", exportDto.getSubjectCode().getDisease().name());
		query.setParameter("subjectCode", config.getSubjectCode());
		query.setParameter("countryLocale", serverCountryLocale); // Use ISO2 country code (e.g., "LU"), not full subject code
		query.setParameter("startDate", DateHelper.convertDateToDbFormat(exportDto.getStartDate()));
		query.setParameter("endDate", DateHelper.convertDateToDbFormat(exportDto.getEndDate()));
		query.setParameter("meansOfImmVaccination", MeansOfImmunization.VACCINATION.name());
		query.setParameter("meansOfImmVaccinationRecovery", MeansOfImmunization.VACCINATION_RECOVERY.name());
	}

	/**
	 * Maps database result rows to export entry DTOs.
	 * This method combines common field mapping with disease-specific field mapping.
	 * This method is private to prevent overriding by subclasses.
	 *
	 * @param resultList
	 *            the list of database result rows
	 * @param exportDto
	 *            the export request DTO
	 * @param serverCountryNutsCode
	 *            the server country NUTS code
	 * @return the list of mapped export entry DTOs
	 */
	private List<EpipulseDiseaseExportEntryDto> mapResultsToEntryDtos(
		List<Object[]> resultList,
		EpipulseExportDto exportDto,
		String serverCountryNutsCode) {

		List<EpipulseDiseaseExportEntryDto> exportEntryList = new ArrayList<>();
		List<PathogenTestType> subjectCodePathogenTestTypes = EpipulsePathogenTestTypeRef.getPathogenTestTypesByDisease(exportDto.getSubjectCode());

		for (Object[] row : resultList) {
			EpipulseDiseaseExportEntryDto dto = new EpipulseDiseaseExportEntryDto();

			// Map common fields (indices 0-27, returns last index used)
			int nextIndex = EpipulseCommonDtoMapper.mapCommonFields(dto, row, serverCountryNutsCode, subjectCodePathogenTestTypes);

			// Map disease-specific fields starting at nextIndex
			mapDiseaseSpecificFields(dto, row, nextIndex);

			// Calculate age (common to all diseases)
			dto.calculateAge();

			exportEntryList.add(dto);
		}

		return exportEntryList;
	}
}
