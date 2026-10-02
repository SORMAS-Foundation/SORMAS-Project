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

import de.symeda.sormas.api.epipulse.referencevalue.EpipulseDengueSerotypeRef;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;

/**
 * Disease-specific SQL for DENGUE.
 *
 * <p>
 * Adds two pathogen-test CTEs ({@code dengue_serotype}, {@code dengue_seroconversion}) and a
 * direct {@code epidata} join. This is separate from {@link EpipulsePathogenTestSql}, which
 * aggregates test types for a different variable.
 *
 * <p>
 * Clinical criteria fields come from already selected common-query columns.
 */
public final class DengueSql {

	/**
	 * Test types on which dengue serotype is considered valid in SORMAS UI logic.
	 */
	private static final PathogenTestType[] SEROTYPING_TEST_TYPES = {
		PathogenTestType.NAAT,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.NEUTRALIZING_ANTIBODIES };

	private DengueSql() {
	}

	/**
	 * Declares DENGUE CTEs and joins.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(buildDengueSerotypeCte(), buildDengueSeroconversionCte())
			.joins(
				// Imported, ModeOfTransmission, ClusterId
				"LEFT JOIN epidata dengue_epidata ON c.epidata_id = dengue_epidata.id",
				"LEFT JOIN dengue_serotype ON dengue_serotype.case_id = c.id",
				"LEFT JOIN dengue_seroconversion ON dengue_seroconversion.case_id = c.id")
			.build();
	}

	/**
	 * Builds {@code Serotype} from the newest qualifying test.
	 *
	 * <p>
	 * Eligible tests are undeleted, positive, verified, have a {@code serotype} DENGUE has a code
	 * for, and have a test type in {@link #SEROTYPING_TEST_TYPES}. Selection is latest by
	 * {@code COALESCE(testdatetime, reportdate, creationdate)} with {@code pt.id} tie-break.
	 */
	private static String buildDengueSerotypeCte() {

		String testTypeList = Stream.of(SEROTYPING_TEST_TYPES).map(type -> "'" + type.name() + "'").collect(Collectors.joining(", "));
		// only serotypes DENGUE has a code for, so a later UNKNOWN does not hide an earlier DENV result
		String mappedSerotypes = Stream.of(EpipulseDengueSerotypeRef.values())
			.map(ref -> "'" + ref.getSerotype().name() + "'")
			.collect(Collectors.joining(", "));

		//@formatter:off
		return "dengue_serotype AS (" +
			   "    SELECT c.id as case_id," +
			   "           (SELECT pt.serotype " +
			   "            FROM samples s " +
			   "            JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "            WHERE s.associatedcase_id = c.id " +
			   "              AND s.deleted = false " +
			   "              AND pt.testtype IN (" + testTypeList + ") " +
			   "              AND pt.testresult = '" + PathogenTestResultType.POSITIVE.name() + "' " +
			   "              AND pt.testresultverified = true " +
			   "              AND pt.serotype IN (" + mappedSerotypes + ") " +
			   "            ORDER BY COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) DESC NULLS LAST, pt.id DESC " +
			   "            LIMIT 1) as serotype " +
			   "    FROM filtered_cases c)";
		//@formatter:on
	}

	/**
	 * Builds case-level {@code SCONV} signal: true when any qualifying test records either
	 * fourfold titre rise or seroconversion.
	 *
	 * <p>
	 * Aggregates positive, verified, undeleted tests; nullable {@code seroconversion} is coalesced
	 * to false.
	 */
	private static String buildDengueSeroconversionCte() {
		//@formatter:off
		return "dengue_seroconversion AS (" +
			   "    SELECT c.id as case_id," +
			   "           BOOL_OR(pt.fourfoldincreaseantibodytiter OR COALESCE(pt.seroconversion, false)) as seroconversion_or_titre_rise " +
			   "    FROM filtered_cases c " +
			   "    JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "    WHERE pt.testresult = '" + PathogenTestResultType.POSITIVE.name() + "' " +
			   "      AND pt.testresultverified = true " +
			   "    GROUP BY c.id)";
		//@formatter:on
	}
}
