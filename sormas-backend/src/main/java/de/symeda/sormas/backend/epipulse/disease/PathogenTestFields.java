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

package de.symeda.sormas.backend.epipulse.disease;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.epipulse.EpipulsePathogentTestCheckDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;

/**
 * PathogenDetectionMethod column derivation. Not in CommonFields: five of seventeen subject codes
 * declare it (PERT, CHIK, DENGUE, WNF, PNEU); requires merging EpipulsePathogenTestSql.spec() into
 * query model. PNEU omitted here (uses serotyping technique from PneuSql instead). Produces non-CSV
 * column: DTO receives parsed tests; EpipulseMapping.pathogenDetectionMethods converts to distinct codes.
 */
final class PathogenTestFields {

	private PathogenTestFields() {
	}

	static void addPathogenTestFields(List<ValueDef> fields) {

		fields.add(
			ValueDef.sqlOnly(
				"all_pathogen_tests_from_latest",
				"sample_all_pathogen_tests_from_latest.all_pathogen_tests_from_latest",
				(dto, row) -> dto.setPathogenTests(
					parsePathogenTestChecks((String) row.get("all_pathogen_tests_from_latest"), row.context().getSubjectCodePathogenTestTypes()))));
	}

	/**
	 * Unflattens aggregate (format: testType|testResult per record, # separated). Field order contracts
	 * with EpipulsePathogenTestSql only. Filters twice: CTE drops deleted/unverified,
	 * this applies subject-code filter (positive result, test type in subject code's list).
	 */
	private static List<EpipulsePathogentTestCheckDto> parsePathogenTestChecks(
		String dbPathogenTestStr,
		List<PathogenTestType> subjectCodePathogenTestTypes) {

		return FieldReaders.parseRecords(dbPathogenTestStr, 2, pathogenTestArr -> {
			String testTypeStr = pathogenTestArr[0];
			String testResultStr = pathogenTestArr[1];

			PathogenTestType testType = null;
			if (!StringUtils.isBlank(testTypeStr)) {
				testType = PathogenTestType.fromLegacyName(testTypeStr);
			}

			PathogenTestResultType testResultType = null;
			if (!StringUtils.isBlank(testResultStr)) {
				testResultType = PathogenTestResultType.valueOf(testResultStr);
			}

			if (!subjectCodePathogenTestTypes.contains(testType) || testResultType != PathogenTestResultType.POSITIVE) {
				return null;
			}

			EpipulsePathogentTestCheckDto dto = new EpipulsePathogentTestCheckDto();
			dto.setTestType(testType);
			dto.setTestResult(testResultType);

			return dto;
		});
	}
}
