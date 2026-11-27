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

import static de.symeda.sormas.backend.epipulse.sql.EpipulseCommonSql.exportDiseaseTest;

import de.symeda.sormas.api.sample.PathogenTestResultType;

/**
 * Shared SQL fragment for {@code DateOfLabResult}: "date when laboratory results become available
 * (first validated result to confirm or invalidate the case)".
 *
 * <p>
 * The earliest verified test on the case, of any test type, whose result is
 * {@code POSITIVE} (confirms) or {@code NEGATIVE} (invalidates).
 * Each test is dated by the first of {@code testdatetime}, {@code reportdate},
 * {@code creationdate} that is set.
 */
public final class EpipulseLabResultSql {

	private EpipulseLabResultSql() {
	}

	public static SqlQueryModel spec() {

		return SqlQueryModel.builder().ctes(buildLabResultCte()).joins("LEFT JOIN case_lab_result ON case_lab_result.case_id = c.id").build();
	}

	/**
	 * Filters are applied in the join so every case remains in the result set; a case without a
	 * qualifying test has a null date.
	 */
	private static String buildLabResultCte() {
		//@formatter:off
		return "case_lab_result AS (SELECT c.id as case_id," +
			   "                           MIN(COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate)) as lab_result_date " +
			   "                    FROM filtered_cases c " +
			   "                    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "                    LEFT JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "                        AND pt.testresultverified = true " +
			   "                        AND pt.testresult IN ('" + PathogenTestResultType.POSITIVE.name() + "', '" + PathogenTestResultType.NEGATIVE.name() + "') " +
			   "                    GROUP BY c.id)";
		//@formatter:on
	}
}
