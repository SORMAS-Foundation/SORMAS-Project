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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.sample.PathogenTestType;

/**
 * Shared builder for disease-specific {@code Serogroup} CTEs.
 *
 * <p>
 * Selects newest qualifying {@code pathogentest.serogroupspecification}; disease-specific code
 * mapping happens in field models.
 */
public final class EpipulseSerogroupSql {

	private EpipulseSerogroupSql() {
	}

	/**
	 * Builds one-row-per-case serogroup CTE for the provided test types.
	 */
	public static String buildSerogroupCte(String cteName, PathogenTestType... testTypes) {

		if (testTypes.length == 0) {
			throw new IllegalArgumentException(cteName + " needs at least one pathogen test type");
		}

		String testTypeList = Stream.of(testTypes).map(type -> "'" + type.name() + "'").collect(Collectors.joining(", "));

		//@formatter:off
		return cteName + " AS (" +
			   "    SELECT c.id as case_id," +
			   "           (SELECT pt.serogroupspecification " +
			   "            FROM samples s " +
			   "            JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "            WHERE s.associatedcase_id = c.id " +
			   "              AND s.deleted = false " +
			   "              AND pt.testtype IN (" + testTypeList + ") " +
			   "              AND pt.serogroupspecification IS NOT NULL " +
			   "            ORDER BY COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) DESC NULLS LAST, pt.id DESC " +
			   "            LIMIT 1) as serogroup " +
			   "    FROM filtered_cases c" +
			   ")";
		//@formatter:on
	}
}
