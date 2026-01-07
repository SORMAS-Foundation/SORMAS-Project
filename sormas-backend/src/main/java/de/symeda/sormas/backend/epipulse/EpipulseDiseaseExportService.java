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

package de.symeda.sormas.backend.epipulse;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.epidata.CaseImportedStatus;
import de.symeda.sormas.api.epidata.ClusterType;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePathogenTestTypeRef;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class EpipulseDiseaseExportService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public EpipulseDiseaseExportResult exportPertussisCaseBased(EpipulseExportDto exportDto, String serverCountryLocale, String serverCountryName)
		throws SQLException, IllegalStateException, IllegalArgumentException {

		EpipulseDiseaseExportResult exportResult = new EpipulseDiseaseExportResult();

		try {
			//lookup reporting country
			//@formatter:off
            String reportingCountryQuery =
                    "select code as reporting_country " +
                            "from epipulse_location_configuration " +
                            "where type='Country' and country_iso2_code = :countryIso2Code";
            //@formatter:on

			@SuppressWarnings("unchecked")
			String reportingCountry = (String) em.createNativeQuery(reportingCountryQuery)
				.setParameter("countryIso2Code", serverCountryLocale)
				.getResultStream()
				.filter(java.util.Objects::nonNull)
				.findFirst()
				.orElse(null);

			if (StringUtils.isBlank(reportingCountry)) {
				throw new IllegalArgumentException("Invalid server country code: " + serverCountryLocale);
			}

			//lookup server country nuts code
			//@formatter:off
            String serverCountryQuery =
                    "select nutscode " +
                            "from country " +
                            "where lower(defaultname) = :countryName";
            //@formatter:on

			@SuppressWarnings("unchecked")
			String serverCountryNutsCode = (String) em.createNativeQuery(serverCountryQuery)
				.setParameter("countryName", serverCountryName.toLowerCase())
				.getResultStream()
				.filter(java.util.Objects::nonNull)
				.findFirst()
				.orElse(null);

			//get subject code
			//@formatter:off
            String subjectCodeQuery =
                    "select subjectcode " +
                            "from epipulse_subjectcode_configuration " +
                            "where disease=:disease and aggregatedreporting='No'";
            //@formatter:on

			@SuppressWarnings("unchecked")
			String subjectCode = (String) em.createNativeQuery(subjectCodeQuery)
				.setParameter("disease", exportDto.getSubjectCode().name())
				.getResultStream()
				.filter(java.util.Objects::nonNull)
				.findFirst()
				.orElse(null);

			if (StringUtils.isBlank(subjectCode)) {
				throw new IllegalStateException("Subject code is empty");
			}

			//@formatter:off
            String diseaseExportQuery =
                    "WITH variables AS (SELECT         :disease         AS disease," +
                            "                          :subjectCode     AS subject_code," +
                            "                          :countryLocale   AS country_locale," +
                            "                          CAST(:startDate AS date) AS start_date," +
                            "                          CAST(:endDate AS date)   AS end_date)," +
                            "     config_data AS (SELECT v.subject_code," +
                            "                            (SELECT epl.code" +
                            "                             FROM epipulse_location_configuration epl" +
                            "                             WHERE epl.type = 'Country'" +
                            "                               AND epl.country_iso2_code = v.country_locale) as reporting_country," +
                            "                            (SELECT epd.datasource" +
                            "                             FROM epipulse_datasource_configuration epd" +
                            "                             WHERE epd.country_iso2_code = v.country_locale" +
                            "                               AND epd.subjectcode = v.subject_code)         as datasource" +
                            "                     FROM variables v)," +
                            "     filtered_cases AS (SELECT c.id," +
                            "                               c.uuid," +
                            "                               c.deleted," +
                            "                               c.reportdate," +
                            "                               c.caseclassification," +
                            "                               c.outcome," +
                            "                               c.person_id," +
                            "                               c.symptoms_id," +
                            "                               c.hospitalization_id," +
                            "                               c.responsibleregion_id," +
                            "                               c.responsibledistrict_id," +
                            "                               c.responsiblecommunity_id" +
                            "                        FROM cases c" +
                            "                                 CROSS JOIN variables v" +
                            "                        WHERE c.disease = v.disease" +
                            "                          AND c.reportdate >= v.start_date" +
                            "                          AND c.reportdate < (v.end_date + interval '1 day'))," +
                            "     case_all_prev_hsp_from_latest AS (SELECT prev_hsp.hospitalization_id," +
                            "                                              STRING_AGG(CONCAT_WS('|'," +
                            "                                                                   COALESCE(prev_hsp.admittedtohealthfacility, '')," +
                            "                                                                   COALESCE(prev_hsp.hospitalizationreason, '')," +
                            "                                                                   COALESCE(" +
                            "                                                                           TO_CHAR(prev_hsp.admissiondate, 'YYYY-MM-DD')," +
                            "                                                                           '')," +
                            "                                                                   COALESCE(" +
                            "                                                                           TO_CHAR(prev_hsp.dischargedate, 'YYYY-MM-DD')," +
                            "                                                                           '')" +
                            "                                                         ), '#'" +
                            "                                                         ORDER BY prev_hsp.admissiondate DESC) as all_prev_hsp_from_latest" +
                            "                                       FROM previoushospitalization as prev_hsp" +
                            "                                       WHERE hospitalization_id IN (SELECT hospitalization_id" +
                            "                                                                    FROM filtered_cases" +
                            "                                                                    WHERE hospitalization_id IS NOT NULL)" +
                            "                                       GROUP BY prev_hsp.hospitalization_id)," +
                            "     case_all_samples_from_latest AS (SELECT samples.associatedcase_id," +
                            "                                             ARRAY_AGG(samples.id ORDER BY samples.sampledatetime DESC) as all_sample_ids_from_latest" +
                            "                                      FROM samples" +
                            "                                      WHERE samples.associatedcase_id IN (SELECT id FROM filtered_cases)" +
                            "                                      GROUP BY samples.associatedcase_id)," +
                            "     sample_all_pathogen_tests_from_latest AS (SELECT pathogentest.sample_id," +
                            "                                                      STRING_AGG(CONCAT_WS('|'," +
                            "                                                                           pathogentest.testtype," +
                            "                                                                           pathogentest.testresult" +
                            "                                                                 ), '#'" +
                            "                                                                 ORDER BY pathogentest.testdatetime DESC) AS all_pathogen_tests_from_latest" +
                            "                                               FROM pathogentest" +
                            "                                                        INNER JOIN case_all_samples_from_latest" +
                            "                                                                   ON pathogentest.sample_id = ANY" +
                            "                                                                      (case_all_samples_from_latest.all_sample_ids_from_latest)" +
                            "                                               GROUP BY pathogentest.sample_id)," +
                            "case_all_immunizations AS (SELECT i.person_id," +
                            "                                       STRING_AGG(CONCAT_WS('|'," +
                            "                                                            COALESCE(to_char(i.startdate, 'YYYY-MM-DD'), '')," +
                            "                                                            COALESCE(to_char(i.enddate, 'YYYY-MM-DD'), '')," +
                            "                                                            COALESCE(i.meansofimmunization, '')," +
                            "                                                            COALESCE(CAST(i.numberofdoses as text), '')), '#'" +
                            "                                                  ORDER BY i.startdate DESC) as all_immunizations_from_latest" +
                            "                                FROM immunization i" +
                            "                                         CROSS JOIN variables v" +
                            "                                where i.person_id IN (SELECT person_id FROM filtered_cases)" +
                            "                                  and i.disease = v.disease" +
                            "                                  and i.meansofimmunization IN (:meansOfImmVaccination, :meansOfImmVaccinationRecovery)" +
                            "                                GROUP BY i.person_id)," +
                            "case_all_vaccinations AS (SELECT i.person_id," +
                            "                                      STRING_AGG(CONCAT_WS('|'," +
                            "                                                           COALESCE(to_char(v.vaccinationdate, 'YYYY-MM-DD'), '')," +
                            "                                                           COALESCE(v.vaccinedose, '')), '#'" +
                            "                                                 ORDER BY v.vaccinationdate DESC) as all_vaccinations_from_latest" +
                            "                               FROM immunization i" +
                            "                                        INNER JOIN vaccination v ON i.id = v.immunization_id" +
                            "                                        CROSS JOIN variables" +
                            "                               WHERE i.person_id IN (SELECT person_id FROM filtered_cases)" +
                            "                                 and i.disease = variables.disease" +
                            "                                 and i.meansofimmunization IN (:meansOfImmVaccination, :meansOfImmVaccinationRecovery)" +
                            "                               GROUP BY i.person_id) " +
                            "SELECT cd.reporting_country," +
                            "       c.deleted," +
                            "       cd.subject_code," +
                            "       c.uuid                            as case_uuid," +
                            "       cd.datasource," +
                            "       cast(c.reportdate as date)                as case_reportdate," +
                            "       person.birthdate_yyyy," +
                            "       person.birthdate_mm," +
                            "       person.birthdate_dd," +
                            "       cast(symptom.onsetdate as date)           as symptom_onsetdate," +
                            "       person.sex," +
                            "       person_address_community.nutscode as address_community_nutscode," +
                            "       person_address_district.nutscode  as address_district_nutscode," +
                            "       person_address_region.nutscode    as address_region_nutscode," +
                            "       person_address_country.nutscode   as address_country_nutscode," +
                            "       responsible_community.nutscode    as responsible_community_nutscode," +
                            "       responsible_district.nutscode     as responsible_district_nutscode," +
                            "       responsible_region.nutscode       as responsible_region_nutscode," +
                            "       c.caseclassification," +
                            "       hospitalization.admittedtohealthfacility," +
                            "       hospitalization.hospitalizationreason," +
                            "       cast(hospitalization.admissiondate as date) as admissiondate," +
                            "       cast(hospitalization.dischargedate as date) as dischargedate," +
                            "       c.outcome                         as case_outcome," +
                            "       case_all_prev_hsp_from_latest.all_prev_hsp_from_latest," +
                            "       sample_all_pathogen_tests_from_latest.all_pathogen_tests_from_latest," +
                            "       case_all_immunizations.all_immunizations_from_latest," +
                            "       case_all_vaccinations.all_vaccinations_from_latest " +
                            "FROM filtered_cases c" +
                            "         CROSS JOIN config_data cd" +
                            "         LEFT JOIN region responsible_region ON c.responsibleregion_id = responsible_region.id" +
                            "         LEFT JOIN district responsible_district ON c.responsibledistrict_id = responsible_district.id" +
                            "         LEFT JOIN community responsible_community ON c.responsiblecommunity_id = responsible_community.id" +
                            "         LEFT JOIN person ON c.person_id = person.id" +
                            "         LEFT JOIN location person_address ON person.address_id = person_address.id" +
                            "         LEFT JOIN country person_address_country ON person_address.country_id = person_address_country.id" +
                            "         LEFT JOIN region person_address_region ON person_address.region_id = person_address_region.id" +
                            "         LEFT JOIN district person_address_district ON person_address.district_id = person_address_district.id" +
                            "         LEFT JOIN community person_address_community ON person_address.community_id = person_address_community.id" +
                            "         LEFT JOIN symptoms symptom ON c.symptoms_id = symptom.id" +
                            "         LEFT JOIN hospitalization ON c.hospitalization_id = hospitalization.id" +
                            "         LEFT JOIN case_all_prev_hsp_from_latest ON (" +
                            "    hospitalization.id = case_all_prev_hsp_from_latest.hospitalization_id" +
                            "    )" +
                            "         LEFT JOIN case_all_samples_from_latest ON (" +
                            "    c.id = case_all_samples_from_latest.associatedcase_id" +
                            "    )" +
                            "         LEFT JOIN sample_all_pathogen_tests_from_latest ON (" +
                            "    sample_all_pathogen_tests_from_latest.sample_id = ANY (case_all_samples_from_latest.all_sample_ids_from_latest)" +
                            "    )" +
                            "         LEFT JOIN case_all_immunizations ON (" +
                            "    case_all_immunizations.person_id = c.person_id" +
                            "    )" +
                            "         LEFT JOIN case_all_vaccinations ON (" +
                            "    case_all_vaccinations.person_id = c.person_id" +
                            "    ) " +
                            "ORDER BY c.reportdate";
            //@formatter:on
			Query query = em.createNativeQuery(diseaseExportQuery);
			query.setParameter("disease", exportDto.getSubjectCode().getDisease().name());
			query.setParameter("subjectCode", subjectCode);
			query.setParameter("countryLocale", serverCountryLocale);
			query.setParameter("startDate", DateHelper.convertDateToDbFormat(exportDto.getStartDate()));
			query.setParameter("endDate", DateHelper.convertDateToDbFormat(exportDto.getEndDate()));
			query.setParameter("meansOfImmVaccination", MeansOfImmunization.VACCINATION.name());
			query.setParameter("meansOfImmVaccinationRecovery", MeansOfImmunization.VACCINATION_RECOVERY.name());

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = query.getResultList();

			List<EpipulseDiseaseExportEntryDto> exportEntryList = new ArrayList<>();
			EpipulseDiseaseExportEntryDto dto = null;
			int maxPathogenTests = 0;
			int maxImmunizations = 0;
			int pathogenTestCount = 0;
			int immunizationCount = 0;

			List<PathogenTestType> subjectCodePathogenTestTypes =
				EpipulsePathogenTestTypeRef.getPathogenTestTypesByDisease(exportDto.getSubjectCode());

			int index;
			for (Object[] row : resultList) {
				index = -1;

				dto = new EpipulseDiseaseExportEntryDto();
				dto.setReportingCountry((String) row[++index]);
				dto.setDeleted((Boolean) row[++index]);

				String subjectCodeFromDb = (String) row[++index];
				if (!StringUtils.isBlank(subjectCodeFromDb)) {
					dto.setSubjectCode(EpipulseSubjectCode.valueOf(subjectCodeFromDb));
				}

				dto.setNationalRecordId((String) row[++index]);

				dto.setDataSource((String) row[++index]);
				dto.setReportDate((Date) row[++index]);
				dto.setYearOfBirth((Integer) row[++index]);
				dto.setMonthOfBirth((Integer) row[++index]);
				dto.setDayOfBirth((Integer) row[++index]);
				dto.setSymptomOnsetDate((Date) row[++index]);

				String sex = (String) row[++index];
				if (!StringUtils.isBlank(sex)) {
					dto.setSex(Sex.valueOf(sex));
				}

				dto.setAddressCommunityNutsCode((String) row[++index]);
				dto.setAddressDistrictNutsCode((String) row[++index]);
				dto.setAddressRegionNutsCode((String) row[++index]);
				dto.setAddressCountryNutsCode((String) row[++index]);

				dto.setResponsibleCommunityNutsCode((String) row[++index]);
				dto.setResponsibleDistrictNutsCode((String) row[++index]);
				dto.setResponsibleRegionNutsCode((String) row[++index]);

				dto.setServerCountryNutsCode(serverCountryNutsCode);

				String caseClassification = (String) row[++index];
				if (!StringUtils.isBlank(caseClassification)) {
					dto.setCaseClassification(CaseClassification.valueOf(caseClassification));
				}

				String admittedToHealthFacility = (String) row[++index];
				if (!StringUtils.isBlank(admittedToHealthFacility)) {
					dto.setAdmittedToHealthFacility(YesNoUnknown.valueOf(admittedToHealthFacility));
				}

				String hospitalizationReason = (String) row[++index];
				if (!StringUtils.isBlank(hospitalizationReason)) {
					dto.setHospitalizationReason(HospitalizationReasonType.valueOf(hospitalizationReason));
				}

				dto.setAdmissionDate((Date) row[++index]);
				dto.setDischargeDate((Date) row[++index]);

				String caseOutcome = (String) row[++index];
				if (!StringUtils.isBlank(caseOutcome)) {
					dto.setCaseOutcome(CaseOutcome.valueOf(caseOutcome));
				}

				dto.setPreviousHospitalizations(dto.parsePreviousHospitalizationChecks((String) row[++index]));
				dto.setPathogenTests(dto.parsePathogenTestChecks((String) row[++index], subjectCodePathogenTestTypes));
				dto.setImmunizations(dto.parseImmunizationChecks((String) row[++index]));
				dto.setVaccinations(dto.parseVaccinations((String) row[++index]));

				dto.calculateAge();

				pathogenTestCount = dto.getPathogenTests().size();
				if (pathogenTestCount > maxPathogenTests) {
					maxPathogenTests = pathogenTestCount;
				}

				immunizationCount = dto.getImmunizations().size();
				if (immunizationCount > maxImmunizations) {
					maxImmunizations = immunizationCount;
				}

				exportEntryList.add(dto);
			}

			exportResult.setMaxPathogenTests(maxPathogenTests);
			exportResult.setMaxImmunizations(maxImmunizations);
			exportResult.setExportEntryList(exportEntryList);
		} catch (Exception e) {
			logger.error("Error while exporting case based " + exportDto.getSubjectCode() + ":" + e.getMessage());
			throw e;
		}

		return exportResult;
	}

	public EpipulseDiseaseExportResult exportMeaslesCaseBased(EpipulseExportDto exportDto, String serverCountryLocale, String serverCountryName)
		throws SQLException, IllegalStateException, IllegalArgumentException {

		EpipulseDiseaseExportResult exportResult = new EpipulseDiseaseExportResult();

		try {
			//lookup reporting country
			//@formatter:off
            String reportingCountryQuery =
                    "select code as reporting_country " +
                            "from epipulse_location_configuration " +
                            "where type='Country' and country_iso2_code = :countryIso2Code";
            //@formatter:on

			@SuppressWarnings("unchecked")
			String reportingCountry = (String) em.createNativeQuery(reportingCountryQuery)
				.setParameter("countryIso2Code", serverCountryLocale)
				.getResultStream()
				.filter(java.util.Objects::nonNull)
				.findFirst()
				.orElse(null);

			if (StringUtils.isBlank(reportingCountry)) {
				throw new IllegalArgumentException("Invalid server country code: " + serverCountryLocale);
			}

			//lookup server country nuts code
			//@formatter:off
            String serverCountryQuery =
                    "select nutscode " +
                            "from country " +
                            "where lower(defaultname) = :countryName";
            //@formatter:on

			@SuppressWarnings("unchecked")
			String serverCountryNutsCode = (String) em.createNativeQuery(serverCountryQuery)
				.setParameter("countryName", serverCountryName.toLowerCase())
				.getResultStream()
				.filter(java.util.Objects::nonNull)
				.findFirst()
				.orElse(null);

			//get subject code
			//@formatter:off
            String subjectCodeQuery =
                    "select subjectcode " +
                            "from epipulse_subjectcode_configuration " +
                            "where disease=:disease and aggregatedreporting='No'";
            //@formatter:on

			@SuppressWarnings("unchecked")
			String subjectCode = (String) em.createNativeQuery(subjectCodeQuery)
				.setParameter("disease", exportDto.getSubjectCode().name())
				.getResultStream()
				.filter(java.util.Objects::nonNull)
				.findFirst()
				.orElse(null);

			if (StringUtils.isBlank(subjectCode)) {
				throw new IllegalStateException("Subject code is empty");
			}

			//@formatter:off
            String diseaseExportQuery =
                    "WITH variables AS (SELECT         :disease         AS disease," +
                            "                          :subjectCode     AS subject_code," +
                            "                          :countryLocale   AS country_locale," +
                            "                          CAST(:startDate AS date) AS start_date," +
                            "                          CAST(:endDate AS date)   AS end_date)," +
                            "     config_data AS (SELECT v.subject_code," +
                            "                            (SELECT epl.code" +
                            "                             FROM epipulse_location_configuration epl" +
                            "                             WHERE epl.type = 'Country'" +
                            "                               AND epl.country_iso2_code = v.country_locale) as reporting_country," +
                            "                            (SELECT epd.datasource" +
                            "                             FROM epipulse_datasource_configuration epd" +
                            "                             WHERE epd.country_iso2_code = v.country_locale" +
                            "                               AND epd.subjectcode = v.subject_code)         as datasource" +
                            "                     FROM variables v)," +
                            "     filtered_cases AS (SELECT c.id," +
                            "                               c.uuid," +
                            "                               c.deleted," +
                            "                               c.reportdate," +
                            "                               c.caseclassification," +
                            "                               c.outcome," +
                            "                               c.person_id," +
                            "                               c.symptoms_id," +
                            "                               c.hospitalization_id," +
                            "                               c.responsibleregion_id," +
                            "                               c.responsibledistrict_id," +
                            "                               c.responsiblecommunity_id," +
                            "                               c.epidata_id," +
                            "                               c.investigateddate," +
                            "                               c.clinicalconfirmation" +
                            "                        FROM cases c" +
                            "                                 CROSS JOIN variables v" +
                            "                        WHERE c.disease = v.disease" +
                            "                          AND c.reportdate >= v.start_date" +
                            "                          AND c.reportdate < (v.end_date + interval '1 day'))," +
                            "     case_all_prev_hsp_from_latest AS (SELECT prev_hsp.hospitalization_id," +
                            "                                              STRING_AGG(CONCAT_WS('|'," +
                            "                                                                   COALESCE(prev_hsp.admittedtohealthfacility, '')," +
                            "                                                                   COALESCE(prev_hsp.hospitalizationreason, '')," +
                            "                                                                   COALESCE(" +
                            "                                                                           TO_CHAR(prev_hsp.admissiondate, 'YYYY-MM-DD')," +
                            "                                                                           '')," +
                            "                                                                   COALESCE(" +
                            "                                                                           TO_CHAR(prev_hsp.dischargedate, 'YYYY-MM-DD')," +
                            "                                                                           '')" +
                            "                                                         ), '#'" +
                            "                                                         ORDER BY prev_hsp.admissiondate DESC) as all_prev_hsp_from_latest" +
                            "                                       FROM previoushospitalization as prev_hsp" +
                            "                                       WHERE hospitalization_id IN (SELECT hospitalization_id" +
                            "                                                                    FROM filtered_cases" +
                            "                                                                    WHERE hospitalization_id IS NOT NULL)" +
                            "                                       GROUP BY prev_hsp.hospitalization_id)," +
                            "     case_all_samples_from_latest AS (SELECT samples.associatedcase_id," +
                            "                                             ARRAY_AGG(samples.id ORDER BY samples.sampledatetime DESC) as all_sample_ids_from_latest" +
                            "                                      FROM samples" +
                            "                                      WHERE samples.associatedcase_id IN (SELECT id FROM filtered_cases)" +
                            "                                      GROUP BY samples.associatedcase_id)," +
                            "     sample_all_pathogen_tests_from_latest AS (SELECT pathogentest.sample_id," +
                            "                                                      STRING_AGG(CONCAT_WS('|'," +
                            "                                                                           pathogentest.testtype," +
                            "                                                                           pathogentest.testresult" +
                            "                                                                 ), '#'" +
                            "                                                                 ORDER BY pathogentest.testdatetime DESC) AS all_pathogen_tests_from_latest" +
                            "                                               FROM pathogentest" +
                            "                                                        INNER JOIN case_all_samples_from_latest" +
                            "                                                                   ON pathogentest.sample_id = ANY" +
                            "                                                                      (case_all_samples_from_latest.all_sample_ids_from_latest)" +
                            "                                               GROUP BY pathogentest.sample_id)," +
                            "case_all_immunizations AS (SELECT i.person_id," +
                            "                                       STRING_AGG(CONCAT_WS('|'," +
                            "                                                            COALESCE(to_char(i.startdate, 'YYYY-MM-DD'), '')," +
                            "                                                            COALESCE(to_char(i.enddate, 'YYYY-MM-DD'), '')," +
                            "                                                            COALESCE(i.meansofimmunization, '')," +
                            "                                                            COALESCE(CAST(i.numberofdoses as text), '')), '#'" +
                            "                                                  ORDER BY i.startdate DESC) as all_immunizations_from_latest" +
                            "                                FROM immunization i" +
                            "                                         CROSS JOIN variables v" +
                            "                                where i.person_id IN (SELECT person_id FROM filtered_cases)" +
                            "                                  and i.disease = v.disease" +
                            "                                  and i.meansofimmunization IN (:meansOfImmVaccination, :meansOfImmVaccinationRecovery)" +
                            "                                GROUP BY i.person_id)," +
                            "case_all_vaccinations AS (SELECT i.person_id," +
                            "                                      STRING_AGG(CONCAT_WS('|'," +
                            "                                                           COALESCE(to_char(v.vaccinationdate, 'YYYY-MM-DD'), '')," +
                            "                                                           COALESCE(v.vaccinedose, '')), '#'" +
                            "                                                 ORDER BY v.vaccinationdate DESC) as all_vaccinations_from_latest" +
                            "                               FROM immunization i" +
                            "                                        INNER JOIN vaccination v ON i.id = v.immunization_id" +
                            "                                        CROSS JOIN variables" +
                            "                               WHERE i.person_id IN (SELECT person_id FROM filtered_cases)" +
                            "                                 and i.disease = variables.disease" +
                            "                                 and i.meansofimmunization IN (:meansOfImmVaccination, :meansOfImmVaccinationRecovery)" +
                            "                               GROUP BY i.person_id), " +
                            "sample_data AS (SELECT c.id as case_id," +
                            "                       MIN(s.sampledatetime) as first_specimen_date," +
                            "                       STRING_AGG(DISTINCT CAST(s2.samplematerial AS text), ',' ORDER BY CAST(s2.samplematerial AS text)) as specimen_types_virus," +
                            "                       STRING_AGG(DISTINCT CAST(s3.samplematerial AS text), ',' ORDER BY CAST(s3.samplematerial AS text)) as specimen_types_serology " +
                            "                FROM filtered_cases c " +
                            "                LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
                            "                LEFT JOIN samples s2 ON s2.associatedcase_id = c.id AND s2.deleted = false AND s2.samplematerial IS NOT NULL " +
                            "                LEFT JOIN (SELECT DISTINCT s_sero.id, s_sero.associatedcase_id, s_sero.samplematerial " +
                            "                           FROM samples s_sero " +
                            "                           JOIN pathogentest pt_sero ON pt_sero.sample_id = s_sero.id " +
                            "                           WHERE s_sero.deleted = false " +
                            "                             AND pt_sero.testtype IN ('IGG_SERUM_ANTIBODY', 'IGM_SERUM_ANTIBODY', 'SEROLOGY')) s3 " +
                            "                          ON s3.associatedcase_id = c.id " +
                            "                GROUP BY c.id), " +
                            "virus_detection_data AS (SELECT c.id as case_id," +
                            "                                 MIN(pt.testdatetime) as lab_result_date," +
                            "                                 (SELECT pt2.testresult " +
                            "                                  FROM samples s2 " +
                            "                                  JOIN pathogentest pt2 ON pt2.sample_id = s2.id " +
                            "                                  WHERE s2.associatedcase_id = c.id " +
                            "                                    AND s2.deleted = false " +
                            "                                    AND pt2.testtype IN ('PCR_RT_PCR', 'CULTURE', 'ISOLATION', 'DIRECT_FLUORESCENT_ANTIBODY', 'INDIRECT_FLUORESCENT_ANTIBODY') " +
                            "                                    AND pt2.testresultverified = true " +
                            "                                  ORDER BY pt2.testdatetime ASC " +
                            "                                  LIMIT 1) as virus_detection_result," +
                            "                                 (SELECT COALESCE(pt3.typingid, pt3.genotyperesult) " +
                            "                                  FROM samples s3 " +
                            "                                  JOIN pathogentest pt3 ON pt3.sample_id = s3.id " +
                            "                                  WHERE s3.associatedcase_id = c.id " +
                            "                                    AND s3.deleted = false " +
                            "                                    AND (pt3.typingid IS NOT NULL OR pt3.genotyperesult IS NOT NULL) " +
                            "                                  ORDER BY pt3.testdatetime ASC " +
                            "                                  LIMIT 1) as genotype_raw " +
                            "                          FROM filtered_cases c " +
                            "                          LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
                            "                          LEFT JOIN pathogentest pt ON pt.sample_id = s.id " +
                            "                              AND pt.testtype IN ('PCR_RT_PCR', 'CULTURE', 'ISOLATION', 'DIRECT_FLUORESCENT_ANTIBODY', 'INDIRECT_FLUORESCENT_ANTIBODY') " +
                            "                              AND pt.testresultverified = true " +
                            "                          GROUP BY c.id), " +
                            "igg_serology_data AS (SELECT c.id as case_id," +
                            "                              (SELECT CASE " +
                            "                                          WHEN pt_igg.fourfoldincreaseantibodytiter = 'YES' THEN 'POSITIVE' " +
                            "                                          ELSE pt_igg.testresult " +
                            "                                      END " +
                            "                               FROM samples s_igg " +
                            "                               JOIN pathogentest pt_igg ON pt_igg.sample_id = s_igg.id " +
                            "                               WHERE s_igg.associatedcase_id = c.id " +
                            "                                 AND s_igg.deleted = false " +
                            "                                 AND pt_igg.testtype = 'IGG_SERUM_ANTIBODY' " +
                            "                               ORDER BY pt_igg.testdatetime ASC " +
                            "                               LIMIT 1) as igg_result " +
                            "                      FROM filtered_cases c), " +
                            "igm_serology_data AS (SELECT c.id as case_id," +
                            "                              (SELECT pt_igm.testresult " +
                            "                               FROM samples s_igm " +
                            "                               JOIN pathogentest pt_igm ON pt_igm.sample_id = s_igm.id " +
                            "                               WHERE s_igm.associatedcase_id = c.id " +
                            "                                 AND s_igm.deleted = false " +
                            "                                 AND pt_igm.testtype = 'IGM_SERUM_ANTIBODY' " +
                            "                               ORDER BY pt_igm.testdatetime ASC " +
                            "                               LIMIT 1) as igm_result " +
                            "                      FROM filtered_cases c), " +
                            "epidata_cluster AS (SELECT c.id as case_id," +
                            "                            epi.clusterrelated," +
                            "                            epi.clustertypetext," +
                            "                            epi.clustertype," +
                            "                            epi.caseimportedstatus " +
                            "                     FROM filtered_cases c " +
                            "                     LEFT JOIN epidata epi ON c.epidata_id = epi.id), " +
                            "exposure_locations AS (SELECT e.epidata_id," +
                            "                              STRING_AGG(" +
                            "                                  CASE " +
                            "                                      WHEN co.defaultname IS NOT NULL THEN co.defaultname " +
                            "                                      WHEN l.city IS NOT NULL THEN l.city " +
                            "                                      ELSE l.details " +
                            "                                  END, " +
                            "                                  '; ' " +
                            "                                  ORDER BY e.startdate DESC" +
                            "                              ) as infection_locations " +
                            "                       FROM exposures e " +
                            "                       JOIN location l ON e.location_id = l.id " +
                            "                       LEFT JOIN country co ON l.country_id = co.id " +
                            "                       WHERE e.epidata_id IN (SELECT epidata_id FROM filtered_cases WHERE epidata_id IS NOT NULL) " +
                            "                       GROUP BY e.epidata_id), " +
                            "complications_data AS (SELECT c.id as case_id," +
                            "                              s.acuteencephalitis," +
                            "                              s.diarrhea," +
                            "                              s.otitismedia," +
                            "                              s.othercomplications " +
                            "                       FROM filtered_cases c " +
                            "                       LEFT JOIN symptoms s ON c.symptoms_id = s.id) " +
                            "SELECT cd.reporting_country," +
                            "       c.deleted," +
                            "       cd.subject_code," +
                            "       c.uuid                            as case_uuid," +
                            "       cd.datasource," +
                            "       cast(c.reportdate as date)                as case_reportdate," +
                            "       person.birthdate_yyyy," +
                            "       person.birthdate_mm," +
                            "       person.birthdate_dd," +
                            "       cast(symptom.onsetdate as date)           as symptom_onsetdate," +
                            "       person.sex," +
                            "       person_address_community.nutscode as address_community_nutscode," +
                            "       person_address_district.nutscode  as address_district_nutscode," +
                            "       person_address_region.nutscode    as address_region_nutscode," +
                            "       person_address_country.nutscode   as address_country_nutscode," +
                            "       responsible_community.nutscode    as responsible_community_nutscode," +
                            "       responsible_district.nutscode     as responsible_district_nutscode," +
                            "       responsible_region.nutscode       as responsible_region_nutscode," +
                            "       c.caseclassification," +
                            "       hospitalization.admittedtohealthfacility," +
                            "       hospitalization.hospitalizationreason," +
                            "       cast(hospitalization.admissiondate as date) as admissiondate," +
                            "       cast(hospitalization.dischargedate as date) as dischargedate," +
                            "       c.outcome                         as case_outcome," +
                            "       case_all_prev_hsp_from_latest.all_prev_hsp_from_latest," +
                            "       sample_all_pathogen_tests_from_latest.all_pathogen_tests_from_latest," +
                            "       case_all_immunizations.all_immunizations_from_latest," +
                            "       case_all_vaccinations.all_vaccinations_from_latest," +
                            "       sd.first_specimen_date," +
                            "       vd.lab_result_date," +
                            "       sd.specimen_types_virus," +
                            "       vd.virus_detection_result," +
                            "       vd.genotype_raw," +
                            "       sd.specimen_types_serology," +
                            "       igg.igg_result," +
                            "       igm.igm_result," +
                            "       cast(c.investigateddate as date) as investigated_date," +
                            "       ec.clusterrelated," +
                            "       ec.clustertypetext," +
                            "       ec.clustertype," +
                            "       ec.caseimportedstatus," +
                            "       comp.acuteencephalitis," +
                            "       comp.diarrhea," +
                            "       comp.otitismedia," +
                            "       comp.othercomplications," +
                            "       c.clinicalconfirmation," +
                            "       el.infection_locations," +
                            "       person.causeofdeathdetails " +
                            "FROM filtered_cases c" +
                            "         CROSS JOIN config_data cd" +
                            "         LEFT JOIN region responsible_region ON c.responsibleregion_id = responsible_region.id" +
                            "         LEFT JOIN district responsible_district ON c.responsibledistrict_id = responsible_district.id" +
                            "         LEFT JOIN community responsible_community ON c.responsiblecommunity_id = responsible_community.id" +
                            "         LEFT JOIN person ON c.person_id = person.id" +
                            "         LEFT JOIN location person_address ON person.address_id = person_address.id" +
                            "         LEFT JOIN country person_address_country ON person_address.country_id = person_address_country.id" +
                            "         LEFT JOIN region person_address_region ON person_address.region_id = person_address_region.id" +
                            "         LEFT JOIN district person_address_district ON person_address.district_id = person_address_district.id" +
                            "         LEFT JOIN community person_address_community ON person_address.community_id = person_address_community.id" +
                            "         LEFT JOIN symptoms symptom ON c.symptoms_id = symptom.id" +
                            "         LEFT JOIN hospitalization ON c.hospitalization_id = hospitalization.id" +
                            "         LEFT JOIN case_all_prev_hsp_from_latest ON (" +
                            "    hospitalization.id = case_all_prev_hsp_from_latest.hospitalization_id" +
                            "    )" +
                            "         LEFT JOIN case_all_samples_from_latest ON (" +
                            "    c.id = case_all_samples_from_latest.associatedcase_id" +
                            "    )" +
                            "         LEFT JOIN sample_all_pathogen_tests_from_latest ON (" +
                            "    sample_all_pathogen_tests_from_latest.sample_id = ANY (case_all_samples_from_latest.all_sample_ids_from_latest)" +
                            "    )" +
                            "         LEFT JOIN case_all_immunizations ON (" +
                            "    case_all_immunizations.person_id = c.person_id" +
                            "    )" +
                            "         LEFT JOIN case_all_vaccinations ON (" +
                            "    case_all_vaccinations.person_id = c.person_id" +
                            "    )" +
                            "         LEFT JOIN sample_data sd ON sd.case_id = c.id" +
                            "         LEFT JOIN virus_detection_data vd ON vd.case_id = c.id" +
                            "         LEFT JOIN igg_serology_data igg ON igg.case_id = c.id" +
                            "         LEFT JOIN igm_serology_data igm ON igm.case_id = c.id" +
                            "         LEFT JOIN epidata_cluster ec ON ec.case_id = c.id" +
                            "         LEFT JOIN exposure_locations el ON el.epidata_id = c.epidata_id" +
                            "         LEFT JOIN complications_data comp ON comp.case_id = c.id " +
                            "ORDER BY c.reportdate";
            //@formatter:on
			Query query = em.createNativeQuery(diseaseExportQuery);
			query.setParameter("disease", exportDto.getSubjectCode().getDisease().name());
			query.setParameter("subjectCode", subjectCode);
			query.setParameter("countryLocale", serverCountryLocale);
			query.setParameter("startDate", DateHelper.convertDateToDbFormat(exportDto.getStartDate()));
			query.setParameter("endDate", DateHelper.convertDateToDbFormat(exportDto.getEndDate()));
			query.setParameter("meansOfImmVaccination", MeansOfImmunization.VACCINATION.name());
			query.setParameter("meansOfImmVaccinationRecovery", MeansOfImmunization.VACCINATION_RECOVERY.name());

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = query.getResultList();

			List<EpipulseDiseaseExportEntryDto> exportEntryList = new ArrayList<>();
			EpipulseDiseaseExportEntryDto dto = null;
			int maxPathogenTests = 0;
			int maxImmunizations = 0;
			int pathogenTestCount = 0;
			int immunizationCount = 0;

			List<PathogenTestType> subjectCodePathogenTestTypes =
				EpipulsePathogenTestTypeRef.getPathogenTestTypesByDisease(exportDto.getSubjectCode());

			int index;
			for (Object[] row : resultList) {
				index = -1;

				dto = new EpipulseDiseaseExportEntryDto();
				dto.setReportingCountry((String) row[++index]);
				dto.setDeleted((Boolean) row[++index]);

				String subjectCodeFromDb = (String) row[++index];
				if (!StringUtils.isBlank(subjectCodeFromDb)) {
					dto.setSubjectCode(EpipulseSubjectCode.valueOf(subjectCodeFromDb));
				}

				dto.setNationalRecordId((String) row[++index]);

				dto.setDataSource((String) row[++index]);
				dto.setReportDate((Date) row[++index]);
				dto.setYearOfBirth((Integer) row[++index]);
				dto.setMonthOfBirth((Integer) row[++index]);
				dto.setDayOfBirth((Integer) row[++index]);
				dto.setSymptomOnsetDate((Date) row[++index]);

				String sex = (String) row[++index];
				if (!StringUtils.isBlank(sex)) {
					dto.setSex(Sex.valueOf(sex));
				}

				dto.setAddressCommunityNutsCode((String) row[++index]);
				dto.setAddressDistrictNutsCode((String) row[++index]);
				dto.setAddressRegionNutsCode((String) row[++index]);
				dto.setAddressCountryNutsCode((String) row[++index]);

				dto.setResponsibleCommunityNutsCode((String) row[++index]);
				dto.setResponsibleDistrictNutsCode((String) row[++index]);
				dto.setResponsibleRegionNutsCode((String) row[++index]);

				dto.setServerCountryNutsCode(serverCountryNutsCode);

				String caseClassification = (String) row[++index];
				if (!StringUtils.isBlank(caseClassification)) {
					dto.setCaseClassification(CaseClassification.valueOf(caseClassification));
				}

				String admittedToHealthFacility = (String) row[++index];
				if (!StringUtils.isBlank(admittedToHealthFacility)) {
					dto.setAdmittedToHealthFacility(YesNoUnknown.valueOf(admittedToHealthFacility));
				}

				String hospitalizationReason = (String) row[++index];
				if (!StringUtils.isBlank(hospitalizationReason)) {
					dto.setHospitalizationReason(HospitalizationReasonType.valueOf(hospitalizationReason));
				}

				dto.setAdmissionDate((Date) row[++index]);
				dto.setDischargeDate((Date) row[++index]);

				String caseOutcome = (String) row[++index];
				if (!StringUtils.isBlank(caseOutcome)) {
					dto.setCaseOutcome(CaseOutcome.valueOf(caseOutcome));
				}

				dto.setPreviousHospitalizations(dto.parsePreviousHospitalizationChecks((String) row[++index]));
				dto.setPathogenTests(dto.parsePathogenTestChecks((String) row[++index], subjectCodePathogenTestTypes));
				dto.setImmunizations(dto.parseImmunizationChecks((String) row[++index]));
				dto.setVaccinations(dto.parseVaccinations((String) row[++index]));

				// Phase 2: Laboratory data population for MEAS export
				dto.setDateOfSpecimen((Date) row[++index]);
				dto.setDateOfLaboratoryResult((Date) row[++index]);

				String specimenTypesVirusRaw = (String) row[++index];
				if (!StringUtils.isBlank(specimenTypesVirusRaw)) {
					List<String> specimenTypesVirus = new ArrayList<>();
					for (String specimenType : specimenTypesVirusRaw.split(",")) {
						specimenTypesVirus.add(
							EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.valueOf(specimenType.trim())));
					}
					dto.setTypeOfSpecimenCollected(specimenTypesVirus);
				}

				String virusDetectionResultRaw = (String) row[++index];
				if (!StringUtils.isBlank(virusDetectionResultRaw)) {
					dto.setResultOfVirusDetection(
						EpipulseLaboratoryMapper.mapTestResultToEpipulseCode(PathogenTestResultType.valueOf(virusDetectionResultRaw)));
				}

				String genotypeRaw = (String) row[++index];
				if (!StringUtils.isBlank(genotypeRaw)) {
					dto.setGenotype(EpipulseLaboratoryMapper.normalizeGenotypeForEpipulse(genotypeRaw));
				}

				String specimenTypesSerologyRaw = (String) row[++index];
				if (!StringUtils.isBlank(specimenTypesSerologyRaw)) {
					List<String> specimenTypesSerology = new ArrayList<>();
					for (String specimenType : specimenTypesSerologyRaw.split(",")) {
						specimenTypesSerology.add(
							EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.valueOf(specimenType.trim())));
					}
					dto.setTypeOfSpecimenSerology(specimenTypesSerology);
				}

				String iggResultRaw = (String) row[++index];
				if (!StringUtils.isBlank(iggResultRaw)) {
					dto.setResultIgG(EpipulseLaboratoryMapper.mapTestResultToEpipulseCode(PathogenTestResultType.valueOf(iggResultRaw)));
				}

				String igmResultRaw = (String) row[++index];
				if (!StringUtils.isBlank(igmResultRaw)) {
					dto.setResultIgM(EpipulseLaboratoryMapper.mapTestResultToEpipulseCode(PathogenTestResultType.valueOf(igmResultRaw)));
				}

				// Phase 3: Clinical and epidemiology data population for MEAS export
				dto.setDateOfInvestigation((Date) row[++index]);

				Boolean clusterRelated = (Boolean) row[++index];
				dto.setClusterRelated(clusterRelated);

				dto.setClusterIdentification((String) row[++index]);

				String clusterTypeRaw = (String) row[++index];
				if (!StringUtils.isBlank(clusterTypeRaw)) {
					// ClusterSetting is repeatable, but typically only one value per case
					List<String> clusterSettings = new ArrayList<>();
					clusterSettings.add(
						EpipulseLaboratoryMapper.mapClusterTypeToEpipulseCode(ClusterType.valueOf(clusterTypeRaw)));
					dto.setClusterSetting(clusterSettings);
				}

				String caseImportedStatusRaw = (String) row[++index];
				if (!StringUtils.isBlank(caseImportedStatusRaw)) {
					dto.setImportedStatus(
						EpipulseLaboratoryMapper.mapCaseImportedStatusToEpipulseCode(CaseImportedStatus.valueOf(caseImportedStatusRaw)));
				}

				// Complications mapping
				String acuteEncephalitisRaw = (String) row[++index];
				String diarrheaRaw = (String) row[++index];
				String otitisMediaRaw = (String) row[++index];
				String otherComplicationsRaw = (String) row[++index];

				SymptomState acuteEncephalitis = parseSymptomState(acuteEncephalitisRaw);
				SymptomState diarrhea = parseSymptomState(diarrheaRaw);
				SymptomState otitisMedia = parseSymptomState(otitisMediaRaw);
				SymptomState otherComplications = parseSymptomState(otherComplicationsRaw);

				dto.setComplicationDiagnosis(
					EpipulseLaboratoryMapper.mapSymptomsToComplicationCodes(
						acuteEncephalitis,
						diarrhea,
						otitisMedia,
						otherComplications));

				// Clinical criteria status
				String clinicalConfirmationRaw = (String) row[++index];
				if (!StringUtils.isBlank(clinicalConfirmationRaw)) {
					dto.setClinicalCriteriaStatus(
						EpipulseLaboratoryMapper.deriveClinicalCriteriaStatus(YesNoUnknown.valueOf(clinicalConfirmationRaw)));
				}

				// Place of infection (exposure locations - semicolon-separated from SQL)
				String placeOfInfectionRaw = (String) row[++index];
				if (!StringUtils.isBlank(placeOfInfectionRaw)) {
					List<String> placesOfInfection = new ArrayList<>();
					for (String place : placeOfInfectionRaw.split(";")) {
						if (!place.trim().isEmpty()) {
							placesOfInfection.add(place.trim());
						}
					}
					dto.setPlaceOfInfection(placesOfInfection);
				}

				// Cause of death
				dto.setCauseOfDeath((String) row[++index]);

				dto.calculateAge();

				pathogenTestCount = dto.getPathogenTests().size();
				if (pathogenTestCount > maxPathogenTests) {
					maxPathogenTests = pathogenTestCount;
				}

				immunizationCount = dto.getImmunizations().size();
				if (immunizationCount > maxImmunizations) {
					maxImmunizations = immunizationCount;
				}

				exportEntryList.add(dto);
			}

			// Track max counts for MEAS repeatable fields
			int maxComplicationDiagnosis = 0;
			int maxClusterSettings = 0;
			int maxPlaceOfInfection = 0;
			int maxSpecimenVirDetect = 0;
			int maxSpecimenSero = 0;

			for (EpipulseDiseaseExportEntryDto entry : exportEntryList) {
				if (entry.getComplicationDiagnosis() != null && entry.getComplicationDiagnosis().size() > maxComplicationDiagnosis) {
					maxComplicationDiagnosis = entry.getComplicationDiagnosis().size();
				}
				if (entry.getClusterSetting() != null && entry.getClusterSetting().size() > maxClusterSettings) {
					maxClusterSettings = entry.getClusterSetting().size();
				}
				if (entry.getPlaceOfInfection() != null && entry.getPlaceOfInfection().size() > maxPlaceOfInfection) {
					maxPlaceOfInfection = entry.getPlaceOfInfection().size();
				}
				if (entry.getTypeOfSpecimenCollected() != null && entry.getTypeOfSpecimenCollected().size() > maxSpecimenVirDetect) {
					maxSpecimenVirDetect = entry.getTypeOfSpecimenCollected().size();
				}
				if (entry.getTypeOfSpecimenSerology() != null && entry.getTypeOfSpecimenSerology().size() > maxSpecimenSero) {
					maxSpecimenSero = entry.getTypeOfSpecimenSerology().size();
				}
			}

			exportResult.setMaxPathogenTests(maxPathogenTests);
			exportResult.setMaxImmunizations(maxImmunizations);
			exportResult.setMaxComplicationDiagnosis(maxComplicationDiagnosis);
			exportResult.setMaxClusterSettings(maxClusterSettings);
			exportResult.setMaxPlaceOfInfection(maxPlaceOfInfection);
			exportResult.setMaxSpecimenVirDetect(maxSpecimenVirDetect);
			exportResult.setMaxSpecimenSero(maxSpecimenSero);
			exportResult.setExportEntryList(exportEntryList);
		} catch (Exception e) {
			logger.error("Error while exporting case based " + exportDto.getSubjectCode() + ":" + e.getMessage());
			throw e;
		}

		return exportResult;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateStatusForBackgroundProcess(
		String exportUuid,
		EpipulseExportStatus newStatus,
		Integer totalRecords,
		String exportFileName,
		BigDecimal exportFileSizeBytes) {

		try {
			// If current status is CANCELLED, do not proceed
			@SuppressWarnings("unchecked")
			String currentStatusStr = (String) em.createNativeQuery("SELECT status FROM epipulse_export WHERE uuid = :uuid")
				.setParameter("uuid", exportUuid)
				.getSingleResult();

			EpipulseExportStatus currentStatus = EpipulseExportStatus.valueOf(currentStatusStr);
			if (currentStatus == EpipulseExportStatus.CANCELLED) {
				logger.info("Export {} is already cancelled, skipping status update", exportUuid);
				return;
			}

			// Validate that if status is COMPLETED, all file information must be non null
			if (newStatus == EpipulseExportStatus.COMPLETED) {
				if (totalRecords == null || exportFileName == null || exportFileSizeBytes == null) {
					throw new IllegalArgumentException(
						"When status is COMPLETED, totalRecords, exportFileName, and exportFileSizeBytes must not be null");
				}
			}

			//@formatter:off
			StringBuilder sql = new StringBuilder("UPDATE epipulse_export SET ")
                    .append("status = :status")
                    .append(", status_change_date = now()")
                    .append(", changedate = now()");
            //@formatter:on

			// Only update file metadata if status is COMPLETED
			if (newStatus == EpipulseExportStatus.COMPLETED) {
				sql.append(", total_records = :totalRecords");
				sql.append(", export_file_name = :exportFileName");
				sql.append(", export_file_size = :exportFileSize");
			}

			sql.append(" WHERE uuid = :uuid");

			Query q = em.createNativeQuery(sql.toString()).setParameter("status", newStatus.name()).setParameter("uuid", exportUuid);

			// Set parameters only if status is COMPLETED
			if (newStatus == EpipulseExportStatus.COMPLETED) {
				q.setParameter("totalRecords", totalRecords);
				q.setParameter("exportFileName", exportFileName);
				q.setParameter("exportFileSize", exportFileSizeBytes);
			}

			int updated = q.executeUpdate();

			em.flush();

			if (updated > 0) {
				logger.info("Updated export {} to status {}", exportUuid, newStatus);
			} else {
				logger.warn("No export found with uuid {} to update", exportUuid);
			}

		} catch (Exception e) {
			logger.error("CRITICAL: Failed to update export status in new transaction for uuid {}: {}", exportUuid, e.getMessage(), e);
			throw e;
		} finally {
			em.clear();
		}
	}

	public String generateDownloadFileName(EpipulseExportDto exportDto, Long exportId) {
		return exportDto.getSubjectCode().name() + "_" + StringUtils.replace(DateHelper.convertDateToDbFormat(exportDto.getStartDate()), "-", "")
			+ "_" + StringUtils.replace(DateHelper.convertDateToDbFormat(exportDto.getEndDate()), "-", "") + "_" + exportId + "_"
			+ (System.currentTimeMillis()) + ".csv";
	}

	private SymptomState parseSymptomState(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return SymptomState.valueOf(value);
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid SymptomState value '{}', treating as null", value);
			return null;
		}
	}
}
