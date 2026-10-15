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
import static de.symeda.sormas.backend.epipulse.sql.EpipulseAggregateFormat.SQL_RECORD_SEPARATOR;
import static de.symeda.sormas.backend.epipulse.sql.EpipulseCommonSql.exportDiseaseTest;

/**
 * The SQL behind {@code PathogenDetectionMethod}: every pathogen test for the case, aggregated
 * newest first.
 *
 * <p>
 * The narrowest of the shared fragments. EpiPulse declares {@code PathogenDetectionMethod} for
 * five of the seventeen enrolled subject codes - PERT, CHIK, DENGUE, WNF and PNEU, PNEU does
 * not use these CTEs.
 *
 * <p>
 * The two go together and in this order: the aggregate joins pathogen tests onto the sample ids the
 * first CTE collected, so neither is useful alone.
 */
public final class EpipulsePathogenTestSql {

	private EpipulsePathogenTestSql() {
	}

	/**
	 * The samples for each case, then the tests on those samples.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(buildSamplesCte(), buildPathogenTestsCte())
			.joins(
				"LEFT JOIN case_all_samples_from_latest ON (c.id = case_all_samples_from_latest.associatedcase_id)",
				"LEFT JOIN sample_all_pathogen_tests_from_latest ON (c.id = sample_all_pathogen_tests_from_latest.associatedcase_id)")
			.build();
	}

	/**
	 * Builds the case_all_samples_from_latest CTE that aggregates all samples for filtered cases.
	 *
	 * @return the samples CTE SQL fragment
	 */
	private static String buildSamplesCte() {
		//@formatter:off
		return "case_all_samples_from_latest AS (SELECT samples.associatedcase_id," +
			   "                                             ARRAY_AGG(samples.id ORDER BY samples.sampledatetime DESC) as all_sample_ids_from_latest" +
			   "                                      FROM samples" +
			   "                                      WHERE samples.associatedcase_id IN (SELECT id FROM filtered_cases)" +
			   "                                        AND samples.deleted = false" +
			   "                                      GROUP BY samples.associatedcase_id)";
		//@formatter:on
	}

	/**
	 * Builds the sample_all_pathogen_tests_from_latest CTE that aggregates pathogen test results.
	 * Groups by associatedcase_id to prevent duplicate rows when a case has multiple samples.
	 *
	 * @return the pathogen tests CTE SQL fragment
	 */
	private static String buildPathogenTestsCte() {
		//@formatter:off
		return "sample_all_pathogen_tests_from_latest AS (SELECT case_all_samples_from_latest.associatedcase_id," +
			   "                                                      STRING_AGG(CONCAT_WS(" + SQL_RECORD_SEPARATOR + "," +
			   "                                                                           pathogentest.testtype," +
			   "                                                                           pathogentest.testresult" +
			   "                                                                 ), " + SQL_COLLECTION_SEPARATOR +
			   "                                                                 ORDER BY COALESCE(pathogentest.testdatetime, pathogentest.reportdate, pathogentest.creationdate) DESC) AS all_pathogen_tests_from_latest" +
			   "                                               FROM pathogentest" +
			   "                                                        INNER JOIN case_all_samples_from_latest" +
			   "                                                                   ON pathogentest.sample_id = ANY" +
			   "                                                                      (case_all_samples_from_latest.all_sample_ids_from_latest)" +
			   "                                               WHERE " + exportDiseaseTest("pathogentest") +
			   // A result nobody verified is not evidence the pathogen was found, NULL is not true, so an unset flag excludes the test.
			   "                                                 AND pathogentest.testresultverified = true" +
			   "                                               GROUP BY case_all_samples_from_latest.associatedcase_id)";
		//@formatter:on
	}
}
