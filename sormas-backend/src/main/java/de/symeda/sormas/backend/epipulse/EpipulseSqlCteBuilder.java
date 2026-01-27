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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Service for building SQL Common Table Expressions (CTEs) for Epipulse disease exports.
 * This service extracts the common SQL query fragments that are shared between different
 * disease export strategies (Pertussis, Measles, etc.).
 */
@Stateless
@LocalBean
public class EpipulseSqlCteBuilder {

	/**
	 * Builds the variables CTE that defines the export parameters.
	 * This CTE is used by all other CTEs to access the disease, subject code, dates, etc.
	 *
	 * @return the variables CTE SQL fragment
	 */
	public String buildVariablesCte() {
		//@formatter:off
		return "WITH variables AS (SELECT         :disease         AS disease," +
			   "                          :subjectCode     AS subject_code," +
			   "                          :countryLocale   AS country_locale," +
			   "                          CAST(:startDate AS date) AS start_date," +
			   "                          CAST(:endDate AS date)   AS end_date),";
		//@formatter:on
	}

	/**
	 * Builds the config_data CTE that looks up reporting country and data source.
	 *
	 * @return the config_data CTE SQL fragment
	 */
	public String buildConfigDataCte() {
		//@formatter:off
		return "     config_data AS (SELECT v.subject_code," +
			   "                            (SELECT epl.code" +
			   "                             FROM epipulse_location_configuration epl" +
			   "                             WHERE epl.type = 'Country'" +
			   "                               AND epl.country_iso2_code = v.country_locale) as reporting_country," +
			   "                            (SELECT epd.datasource" +
			   "                             FROM epipulse_datasource_configuration epd" +
			   "                             WHERE epd.country_iso2_code = v.country_locale" +
			   "                               AND epd.subjectcode = v.subject_code)         as datasource" +
			   "                     FROM variables v),";
		//@formatter:on
	}

	/**
	 * Builds the filtered_cases CTE that selects all cases matching the export criteria.
	 *
	 * @param includeEpidataFields
	 *            if true, includes additional fields required for diseases with epidemiology data
	 *            (epidata_id, investigateddate, clinicalconfirmation)
	 * @return the filtered_cases CTE SQL fragment
	 */
	public String buildFilteredCasesCte(boolean includeEpidataFields) {
		StringBuilder cte = new StringBuilder();
		//@formatter:off
		cte.append("     filtered_cases AS (SELECT c.id,")
		   .append("                               c.uuid,")
		   .append("                               c.deleted,")
		   .append("                               c.reportdate,")
		   .append("                               c.caseclassification,")
		   .append("                               c.outcome,")
		   .append("                               c.person_id,")
		   .append("                               c.symptoms_id,")
		   .append("                               c.hospitalization_id,")
		   .append("                               c.responsibleregion_id,")
		   .append("                               c.responsibledistrict_id,")
		   .append("                               c.responsiblecommunity_id");

		if (includeEpidataFields) {
			cte.append(",")
			   .append("                               c.epidata_id,")
			   .append("                               c.investigateddate,")
			   .append("                               c.clinicalconfirmation");
		}

		cte.append("                        FROM cases c")
		   .append("                                 CROSS JOIN variables v")
		   .append("                        WHERE c.disease = v.disease")
		   .append("                          AND c.reportdate >= v.start_date")
		   .append("                          AND c.reportdate < (v.end_date + interval '1 day')),");
		//@formatter:on

		return cte.toString();
	}

	/**
	 * Builds the case_all_prev_hsp_from_latest CTE that aggregates previous hospitalizations.
	 *
	 * @return the previous hospitalizations CTE SQL fragment
	 */
	public String buildPreviousHospitalizationsCte() {
		//@formatter:off
		return "     case_all_prev_hsp_from_latest AS (SELECT prev_hsp.hospitalization_id," +
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
			   "                                       GROUP BY prev_hsp.hospitalization_id),";
		//@formatter:on
	}

	/**
	 * Builds the case_all_samples_from_latest CTE that aggregates all samples for filtered cases.
	 *
	 * @return the samples CTE SQL fragment
	 */
	public String buildSamplesCte() {
		//@formatter:off
		return "     case_all_samples_from_latest AS (SELECT samples.associatedcase_id," +
			   "                                             ARRAY_AGG(samples.id ORDER BY samples.sampledatetime DESC) as all_sample_ids_from_latest" +
			   "                                      FROM samples" +
			   "                                      WHERE samples.associatedcase_id IN (SELECT id FROM filtered_cases)" +
			   "                                      GROUP BY samples.associatedcase_id),";
		//@formatter:on
	}

	/**
	 * Builds the sample_all_pathogen_tests_from_latest CTE that aggregates pathogen test results.
	 * Groups by associatedcase_id to prevent duplicate rows when a case has multiple samples.
	 *
	 * @return the pathogen tests CTE SQL fragment
	 */
	public String buildPathogenTestsCte() {
		//@formatter:off
		return "     sample_all_pathogen_tests_from_latest AS (SELECT case_all_samples_from_latest.associatedcase_id," +
			   "                                                      STRING_AGG(CONCAT_WS('|'," +
			   "                                                                           pathogentest.testtype," +
			   "                                                                           pathogentest.testresult" +
			   "                                                                 ), '#'" +
			   "                                                                 ORDER BY pathogentest.testdatetime DESC) AS all_pathogen_tests_from_latest" +
			   "                                               FROM pathogentest" +
			   "                                                        INNER JOIN case_all_samples_from_latest" +
			   "                                                                   ON pathogentest.sample_id = ANY" +
			   "                                                                      (case_all_samples_from_latest.all_sample_ids_from_latest)" +
			   "                                               GROUP BY case_all_samples_from_latest.associatedcase_id),";
		//@formatter:on
	}

	/**
	 * Builds the case_all_immunizations CTE that aggregates immunization records.
	 *
	 * @return the immunizations CTE SQL fragment
	 */
	public String buildImmunizationsCte() {
		//@formatter:off
		return "case_all_immunizations AS (SELECT i.person_id," +
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
			   "                                GROUP BY i.person_id),";
		//@formatter:on
	}

	/**
	 * Builds the case_all_vaccinations CTE that aggregates vaccination records.
	 *
	 * @return the vaccinations CTE SQL fragment
	 */
	public String buildVaccinationsCte() {
		//@formatter:off
		return "case_all_vaccinations AS (SELECT i.person_id," +
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
			   "                               GROUP BY i.person_id) ";
		//@formatter:on
	}

	/**
	 * Builds just the common SELECT field list (without the SELECT keyword or FROM clause).
	 * This allows strategies to append disease-specific fields before the FROM clause.
	 *
	 * @return the common SELECT fields as a comma-separated list
	 */
	public String buildCommonSelectFields() {
		//@formatter:off
		return "cd.reporting_country," +
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
		       "       case_all_vaccinations.all_vaccinations_from_latest";
		//@formatter:on
	}

	/**
	 * Builds the common FROM and JOIN clauses.
	 * This includes all joins that are common to all disease exports.
	 *
	 * @return the FROM and JOIN clauses SQL fragment
	 */
	public String buildCommonFromAndJoins() {
		StringBuilder joins = new StringBuilder();
		//@formatter:off
		joins.append(" FROM filtered_cases c")
		      .append("         CROSS JOIN config_data cd")
		      .append("         LEFT JOIN region responsible_region ON c.responsibleregion_id = responsible_region.id")
		      .append("         LEFT JOIN district responsible_district ON c.responsibledistrict_id = responsible_district.id")
		      .append("         LEFT JOIN community responsible_community ON c.responsiblecommunity_id = responsible_community.id")
		      .append("         LEFT JOIN person ON c.person_id = person.id")
		      .append("         LEFT JOIN location person_address ON person.address_id = person_address.id")
		      .append("         LEFT JOIN country person_address_country ON person_address.country_id = person_address_country.id")
		      .append("         LEFT JOIN region person_address_region ON person_address.region_id = person_address_region.id")
		      .append("         LEFT JOIN district person_address_district ON person_address.district_id = person_address_district.id")
		      .append("         LEFT JOIN community person_address_community ON person_address.community_id = person_address_community.id")
		      .append("         LEFT JOIN symptoms symptom ON c.symptoms_id = symptom.id")
		      .append("         LEFT JOIN hospitalization ON c.hospitalization_id = hospitalization.id")
		      .append("         LEFT JOIN case_all_prev_hsp_from_latest ON (")
		      .append("    hospitalization.id = case_all_prev_hsp_from_latest.hospitalization_id")
		      .append("    )")
		      .append("         LEFT JOIN case_all_samples_from_latest ON (")
		      .append("    c.id = case_all_samples_from_latest.associatedcase_id")
		      .append("    )")
		      .append("         LEFT JOIN sample_all_pathogen_tests_from_latest ON (")
		      .append("    c.id = sample_all_pathogen_tests_from_latest.associatedcase_id")
		      .append("    )")
		      .append("         LEFT JOIN case_all_immunizations ON (")
		      .append("    case_all_immunizations.person_id = c.person_id")
		      .append("    )")
		      .append("         LEFT JOIN case_all_vaccinations ON (")
		      .append("    case_all_vaccinations.person_id = c.person_id")
		      .append("    )");
		//@formatter:on

		return joins.toString();
	}

	/**
	 * Builds the main SELECT clause with all common fields and joins.
	 * This is a convenience method for diseases that don't need additional fields.
	 *
	 * @return the complete SELECT statement with FROM, JOINs, and ORDER BY
	 */
	public String buildMainSelectClause() {
		return "SELECT " + buildCommonSelectFields() + buildCommonFromAndJoins() + " ORDER BY c.reportdate";
	}
}
