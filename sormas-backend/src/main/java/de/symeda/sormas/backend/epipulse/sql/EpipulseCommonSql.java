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

package de.symeda.sormas.backend.epipulse.sql;

import de.symeda.sormas.api.epipulse.EpipulseCaseSubset;

/**
 * SQL shared by all EpiPulse disease exports.
 *
 * <p>
 * This class defines the common CTE chain and base joins. Disease-specific classes contribute
 * additional CTEs/joins via {@code common.plus(disease)}.
 *
 * <p>
 * It is also the only place that defines the shared query root ({@code FROM filtered_cases c});
 * disease fragments are add-ons and must not introduce a second {@code FROM}.
 */
public final class EpipulseCommonSql {

	private EpipulseCommonSql() {
	}

	/**
	 * Builds the full common SQL model in dependency order.
	 *
	 * <p>
	 * Order matters: later CTEs depend on earlier ones ({@code variables -> filtered_cases ->
	 * aggregates}), and disease CTEs are merged after this chain.
	 *
	 * @param excludeNonReportableCases
	 *            whether to exclude cases flagged {@code excludefromreporting}
	 * @param caseSubset
	 *            optional subset discriminator for subject codes that split one disease
	 */
	public static SqlQueryModel spec(boolean excludeNonReportableCases, EpipulseCaseSubset caseSubset) {

		SqlQueryModel aggregates = SqlQueryModel.builder()
			.ctes(buildCaseReferenceDatesCte())
			.from("filtered_cases c")
			.joins(
				"CROSS JOIN config_data cd",
				"LEFT JOIN region responsible_region ON c.responsibleregion_id = responsible_region.id",
				"LEFT JOIN district responsible_district ON c.responsibledistrict_id = responsible_district.id",
				"LEFT JOIN community responsible_community ON c.responsiblecommunity_id = responsible_community.id",
				"LEFT JOIN person ON c.person_id = person.id",
				"LEFT JOIN location person_address ON person.address_id = person_address.id",
				"LEFT JOIN country person_address_country ON person_address.country_id = person_address_country.id",
				"LEFT JOIN region person_address_region ON person_address.region_id = person_address_region.id",
				"LEFT JOIN district person_address_district ON person_address.district_id = person_address_district.id",
				"LEFT JOIN community person_address_community ON person_address.community_id = person_address_community.id",
				"LEFT JOIN symptoms symptom ON c.symptoms_id = symptom.id",
				"LEFT JOIN case_reference_dates ON (c.id = case_reference_dates.case_id)")
			.build();

		return baseCtes(excludeNonReportableCases, caseSubset).plus(aggregates);
	}

	/**
	 * Builds the base CTE set used by all other common and disease CTEs:
	 * {@code variables}, {@code config_data}, and {@code filtered_cases}.
	 */
	public static SqlQueryModel baseCtes(boolean excludeNonReportableCases, EpipulseCaseSubset caseSubset) {

		return SqlQueryModel.builder()
			.ctes(buildVariablesCte(), buildConfigDataCte(), buildFilteredCasesCte(excludeNonReportableCases, caseSubset))
			.build();
	}

	/**
	 * Export parameters CTE shared by downstream SQL.
	 */
	private static String buildVariablesCte() {
		//@formatter:off
		return "variables AS (SELECT      :disease         AS disease," +
			   "                          :subjectCode     AS subject_code," +
			   "                          :countryLocale   AS country_locale," +
			   "                          CAST(:startDate AS date) AS start_date," +
			   "                          CAST(:endDate AS date)   AS end_date," +
			   // NULL means "incubation window unknown", not zero days. Casted params must be
			   // bound with type information.
			   "                          CAST(:minIncubationDays AS integer) AS min_incubation_days," +
			   "                          CAST(:maxIncubationDays AS integer) AS max_incubation_days)";
		//@formatter:on
	}

	/**
	 * Reporting metadata CTE: reporting country code and datasource by locale/subject code.
	 */
	private static String buildConfigDataCte() {
		//@formatter:off
		return "config_data AS (SELECT v.subject_code," +
			   "                            (SELECT epl.code" +
			   "                             FROM epipulse_location_configuration epl" +
			   "                             WHERE epl.type = 'Country'" +
			   "                               AND epl.country_iso2_code = v.country_locale) as reporting_country," +
			   "                            (SELECT epd.datasource" +
			   "                             FROM epipulse_datasource_configuration epd" +
			   "                             WHERE epd.country_iso2_code = v.country_locale" +
			   "                               AND epd.subjectcode = v.subject_code)         as datasource" +
			   "                     FROM variables v)";
		//@formatter:on
	}

	/**
	 * Cases in export scope (disease + period + optional subset), with one stable column set for all
	 * diseases.
	 *
	 * <p>
	 * Soft-deleted cases are intentionally included here: export status logic needs them for
	 * retraction handling. Downstream joins still filter soft-deletable child tables.
	 *
	 * <p>
	 * If {@code caseSubset} is provided, an enum-provided case column is interpolated and compared
	 * against bound value {@code :caseSubset}.
	 */
	private static String buildFilteredCasesCte(boolean excludeNonReportableCases, EpipulseCaseSubset caseSubset) {
		StringBuilder cte = new StringBuilder();
		//@formatter:off
		cte.append("filtered_cases AS (SELECT c.id,")
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
		   .append("                               c.responsiblecommunity_id,")
		   .append("                               c.epidata_id,")
		   .append("                               c.healthconditions_id,")
		   .append("                               c.investigateddate,")
		   .append("                               c.pregnant,")
		   .append("                               c.clinicalconfirmation")
		   .append("                        FROM cases c")
		   .append("                                 CROSS JOIN variables v")
		   .append("                        WHERE c.disease = v.disease")
		   .append("                          AND c.reportdate >= v.start_date")
		   .append("                          AND c.reportdate < (v.end_date + interval '1 day')");


		if (caseSubset != null) {
			cte.append("                          AND c.").append(caseSubset.getCaseColumn()).append(" = :caseSubset");
		}

		appendReportingExclusionClauseIfAdequate(cte, excludeNonReportableCases);
		//@formatter:on

		return cte.toString();
	}

	/**
	 * Finalizes {@code filtered_cases}, optionally adding exclusion of
	 * {@code excludefromreporting = true}.
	 */
	private static void appendReportingExclusionClauseIfAdequate(StringBuilder cte, boolean excludeNonReportableCases) {

		if (excludeNonReportableCases) {
			cte.append("                          AND c.excludefromreporting IS NOT TRUE)");
		} else {
			// closing CTE parenthesis
			cte.append(")");
		}
	}

	/**
	 * The condition every CTE reading a case's pathogen tests applies to them: the test is not
	 * deleted, and it was done for the disease being exported.
	 *
	 * <p>
	 * Whether the result must be verified, or positive, depends on the variable and stays with
	 * each CTE; this is only the part they all share. Used in the {@code JOIN ... ON} clause, so it
	 * keeps a {@code LEFT JOIN}'s case row when no test qualifies.
	 *
	 * @param alias
	 *            the alias the CTE gives {@code pathogentest}
	 */
	static String exportDiseaseTest(String alias) {
		return alias + ".deleted = false AND " + alias + ".testeddisease = (SELECT disease FROM variables)";
	}

	/**
	 * Per-case reference dates used by Java date-selection logic ({@code EpipulseCaseDates}).
	 * 
	 * <p>
	 * Emits the first verified positive test date for the export's disease, earliest doctor
	 * diagnosis date, and earliest doctor/lab notification report date.
	 */
	private static String buildCaseReferenceDatesCte() {
		//@formatter:off
		return "case_reference_dates AS (SELECT c.id as case_id," +
			   "                                 CAST((SELECT MIN(COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate))" +
			   "                                       FROM samples s" +
			   "                                                JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") +
			   "                                       WHERE s.associatedcase_id = c.id" +
			   "                                         AND s.deleted = false" +
			   "                                         AND pt.testresultverified = true" +
			   "                                         AND pt.testresult = 'POSITIVE') AS date)" +
			   "                                     as first_positive_test_date," +
			   "                                 CAST((SELECT MIN(sr.dateofdiagnosis)" +
			   "                                       FROM surveillancereports sr" +
			   "                                       WHERE sr.caze_id = c.id" +
			   "                                         AND sr.reportingtype = 'DOCTOR') AS date)" +
			   "                                     as doctor_date_of_diagnosis," +
			   "                                 CAST((SELECT MIN(sr.reportdate)" +
			   "                                       FROM surveillancereports sr" +
			   "                                       WHERE sr.caze_id = c.id" +
			   "                                         AND sr.reportingtype IN ('DOCTOR', 'LABORATORY')) AS date)" +
			   "                                     as first_notification_report_date" +
			   "                          FROM filtered_cases c)";
		//@formatter:on
	}

}
