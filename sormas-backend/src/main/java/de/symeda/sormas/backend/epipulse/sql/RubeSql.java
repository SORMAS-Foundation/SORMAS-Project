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

import static de.symeda.sormas.backend.epipulse.sql.EpipulseAggregateFormat.SQL_COLLECTION_SEPARATOR;
import static de.symeda.sormas.backend.epipulse.sql.EpipulseCommonSql.exportDiseaseTest;

/**
 * Disease-specific SQL for rubella.
 *
 * <p>
 * Adds five CTEs plus one direct {@code epidata} join. Rubella exports laboratory variables
 * similar to measles but with different derivations (e.g. lab-result date rule, specimen for
 * virus detection, serology precedence), so this remains a separate implementation from
 * {@link MeasSql}.
 *
 * <p>
 * As in other disease fragments, aliases used by the field model are defined here and must match.
 */
public final class RubeSql {

	/**
	 * Test types used for {@code ResultVirDetect}.
	 *
	 * <p>
	 * Copied (not shared) from measles result-test types as current tracker guidance; kept local so
	 * rubella can diverge later if needed.
	 */
	private static final String VIRUS_DETECTION_TEST_TYPES =
		"('PCR_RT_PCR', 'CULTURE', 'ISOLATION', 'SEQUENCING', 'GENOTYPING', 'SANGER_SEQUENCING')";

	/**
	 * Serology test types used by {@code ResultIgG}, {@code ResultIgM}, and {@code SpecimenSero}.
	 */
	private static final String IGG_TEST_TYPE = "'IGG_SERUM_ANTIBODY'";
	private static final String IGM_TEST_TYPE = "'IGM_SERUM_ANTIBODY'";

	private RubeSql() {
	}

	/**
	 * Declares rubella CTEs and joins.
	 *
	 * <p>
	 * {@code PlaceOfInfection}, hospitalization, and vaccination are merged from shared fragments.
	 * {@code ComplicationDiagnosis} and {@code Pregnancy} are already available from common-query
	 * joins/columns. {@code WeekOfGestation} and {@code IgGAvidityTest} stay blank by design (no
	 * matching SORMAS data field).
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(buildSpecimenCte(), buildVirusDetectionCte(), buildGenotypeCte(), buildLabResultCte(), buildSerologyCte())
			.joins(
				// ClusterRelated, ClusterId, ClusterSetting, ImportedStatus
				"LEFT JOIN epidata rube_epidata ON c.epidata_id = rube_epidata.id",
				"LEFT JOIN rube_specimen ON rube_specimen.case_id = c.id",
				"LEFT JOIN rube_virus_detection ON rube_virus_detection.case_id = c.id",
				"LEFT JOIN rube_genotype ON rube_genotype.case_id = c.id",
				"LEFT JOIN rube_lab_result ON rube_lab_result.case_id = c.id",
				"LEFT JOIN rube_serology ON rube_serology.case_id = c.id")
			.build();
	}

	/**
	 * Builds {@code DateOfSpecimen} and {@code SpecimenSero}.
	 *
	 * <p>
	 * Date uses all undeleted samples; serology specimen types are restricted to samples that carry
	 * IgG/IgM tests. Untested samples are therefore dated but not labeled serology.
	 */
	private static String buildSpecimenCte() {
		//@formatter:off
		return "rube_specimen AS (SELECT c.id as case_id," +
			   "                         MIN(s.sampledatetime) as first_specimen_date," +
			   "                         STRING_AGG(DISTINCT CAST(s_sero.samplematerial AS text), " + SQL_COLLECTION_SEPARATOR + " ORDER BY CAST(s_sero.samplematerial AS text)) as specimen_types_serology " +
			   "                  FROM filtered_cases c " +
			   "                  LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "                  LEFT JOIN (SELECT DISTINCT s2.associatedcase_id, s2.samplematerial " +
			   "                             FROM samples s2 " +
			   "                             JOIN pathogentest pt2 ON pt2.sample_id = s2.id AND " + exportDiseaseTest("pt2") + " " +
			   "                                 AND pt2.testtype IN (" + IGG_TEST_TYPE + ", " + IGM_TEST_TYPE + ") " +
			   "                             WHERE s2.deleted = false " +
			   "                               AND s2.samplematerial IS NOT NULL " +
			   "                               AND s2.associatedcase_id IN (SELECT id FROM filtered_cases)) s_sero " +
			   "                            ON s_sero.associatedcase_id = c.id " +
			   "                  GROUP BY c.id)";
		//@formatter:on
	}

	/**
	 * Builds {@code ResultVirDetect} and {@code SpecimenVirDetect} from one selected test row.
	 *
	 * <p>
	 * Uses {@code DISTINCT ON (c.id)} over verified, undeleted tests in
	 * {@link #VIRUS_DETECTION_TEST_TYPES}, ordered by
	 * {@code COALESCE(testdatetime, reportdate, creationdate)} then {@code pt.id}. The selected
	 * row provides both result and specimen material.
	 */
	private static String buildVirusDetectionCte() {
		//@formatter:off
		return "rube_virus_detection AS (SELECT DISTINCT ON (c.id) c.id as case_id," +
			   "                                pt.testresult as virus_detection_result," +
			   "                                CAST(s.samplematerial AS text) as virus_detection_specimen " +
			   "                         FROM filtered_cases c " +
			   "                         JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "                         JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "                             AND pt.testresultverified = true " +
			   "                             AND pt.testtype IN " + VIRUS_DETECTION_TEST_TYPES + " " +
			   "                         ORDER BY c.id, COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) ASC, pt.id ASC)";
		//@formatter:on
	}

	/**
	 * Builds {@code Genotype} independently of virus-detection result selection.
	 *
	 * <p>
	 * Picks the earliest non-null genotype from undeleted tests, ordered by
	 * {@code COALESCE(testdatetime, reportdate, creationdate)} and {@code pt.id}. Separate CTE keeps
	 * genotype available even when no verified virus-detection result exists.
	 */
	private static String buildGenotypeCte() {
		//@formatter:off
		return "rube_genotype AS (SELECT c.id as case_id," +
			   "                         (SELECT pt.genotype " +
			   "                          FROM samples s " +
			   "                          JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "                          WHERE s.associatedcase_id = c.id " +
			   "                            AND s.deleted = false " +
			   "                            AND pt.genotype IS NOT NULL " +
			   "                          ORDER BY COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) ASC, pt.id ASC " +
			   "                          LIMIT 1) as genotype_raw " +
			   "                  FROM filtered_cases c)";
		//@formatter:on
	}

	/**
	 * Builds {@code DateOfLabResult} as the oldest positive, verified test date (any test type).
	 *
	 * <p>
	 * Filters are applied in the join so every case remains in the result set; cases without a
	 * qualifying test return null date.
	 */
	private static String buildLabResultCte() {
		//@formatter:off
		return "rube_lab_result AS (SELECT c.id as case_id," +
			   "                           MIN(COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate)) as lab_result_date " +
			   "                    FROM filtered_cases c " +
			   "                    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "                    LEFT JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "                        AND pt.testresultverified = true " +
			   "                        AND pt.testresult = 'POSITIVE' " +
			   "                    GROUP BY c.id)";
		//@formatter:on
	}

	/**
	 * Builds serology counts used to derive {@code ResultIgG} and {@code ResultIgM}.
	 *
	 * <p>
	 * This CTE provides counts only; precedence logic is applied in {@code RubeFieldModel}. IgG is
	 * counted both for all tests and for paired-sample indicators
	 * ({@code fourfoldincreaseantibodytiter OR seroconversion}) to distinguish "not tested" from
	 * "tested but no paired-rise evidence". IgM uses standard positive/negative counts. Only
	 * verified tests are counted; nullable flags are coalesced to false.
	 */
	private static String buildSerologyCte() {
		//@formatter:off
		String pairedSample = "(COALESCE(pt.fourfoldincreaseantibodytiter, false) OR COALESCE(pt.seroconversion, false))";

		return "rube_serology AS (SELECT c.id as case_id," +
			   "                         COUNT(*) FILTER (WHERE pt.testtype = " + IGG_TEST_TYPE + ") as igg_test_count," +
			   "                         COUNT(*) FILTER (WHERE pt.testtype = " + IGG_TEST_TYPE + " AND " + pairedSample + ") as igg_paired_count," +
			   "                         COUNT(*) FILTER (WHERE pt.testtype = " + IGG_TEST_TYPE + " AND " + pairedSample + " AND pt.testresult = 'POSITIVE') as igg_paired_positive," +
			   "                         COUNT(*) FILTER (WHERE pt.testtype = " + IGG_TEST_TYPE + " AND " + pairedSample + " AND pt.testresult = 'NEGATIVE') as igg_paired_negative," +
			   "                         COUNT(*) FILTER (WHERE pt.testtype = " + IGM_TEST_TYPE + ") as igm_test_count," +
			   "                         COUNT(*) FILTER (WHERE pt.testtype = " + IGM_TEST_TYPE + " AND pt.testresult = 'POSITIVE') as igm_positive," +
			   "                         COUNT(*) FILTER (WHERE pt.testtype = " + IGM_TEST_TYPE + " AND pt.testresult = 'NEGATIVE') as igm_negative " +
			   "                  FROM filtered_cases c " +
			   "                  LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "                  LEFT JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "                      AND pt.testresultverified = true " +
			   "                  GROUP BY c.id)";
		//@formatter:on
	}
}
