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

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.epipulse.referencevalue.EpipulseShigPathogenRef;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.backend.epipulse.sql.EpipulseAstSql.AstDrugField;

/**
 * Disease-specific SQL for SHIG.
 *
 * <p>
 * Adds three CTEs plus direct {@code epidata} join. As in other diseases, {@code epidata} is a
 * 1:1 FK join while CTEs are used where aggregation or deterministic row-picking is required.
 *
 * <p>
 * SHIG specimen logic intentionally differs from SALM reference-lab logic: SHIG picks the oldest
 * positive verified diagnostic test regardless of laboratory.
 */
public final class ShigSql {

	private ShigSql() {
	}

	/**
	 * Declares SHIG CTEs and joins.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(buildShigAstDataCte(), buildShigSpecimenCte(), buildShigPathogenCte())
			.joins(
				// Imported, ModeOfTransmission, SuspectedVehicle
				"LEFT JOIN epidata shig_epidata ON c.epidata_id = shig_epidata.id",
				"LEFT JOIN shig_ast_data shig_ast ON shig_ast.case_id = c.id",
				"LEFT JOIN shig_specimen ON shig_specimen.case_id = c.id",
				"LEFT JOIN shig_pathogen ON shig_pathogen.case_id = c.id")
			.build();
	}

	/**
	 * AST panel CTE for SHIG {@code SIR_*} columns.
	 *
	 * <p>
	 * Uses all five SHIG-relevant susceptibility columns available in
	 * {@code drugsusceptibility} (AMP, CAZ, CTX, CIP, SXT). MIC columns are carried by shared AST
	 * CTE shape but not exported by SHIG. {@code ECOFF_AZM} is intentionally not derived here.
	 */
	private static String buildShigAstDataCte() {
		return EpipulseAstSql.buildAstDataCte(
			"shig_ast_data",
			Arrays.asList(
				new AstDrugField("ampicillinmic", "ampicillinsusceptibility", "amp"),
				new AstDrugField("ceftazidimemic", "ceftazidimesusceptibility", "caz"),
				new AstDrugField("cefotaximemic", "cefotaximesusceptibility", "ctx"),
				new AstDrugField("ciprofloxacinmic", "ciprofloxacinsusceptibility", "cip"),
				new AstDrugField("trimethoprimsulfamethoxazolemic", "trimethoprimsulfamethoxazolesusceptibility", "sxt")));
	}

	/**
	 * Diagnostic specimen CTE for {@code Specimen}.
	 *
	 * <p>
	 * Picks the oldest undeleted, positive, verified test per case (ordered by
	 * {@code COALESCE(testdatetime, reportdate, creationdate)} then {@code pt.id}) and reports that
	 * sample's material.
	 */
	private static String buildShigSpecimenCte() {
		//@formatter:off
		return "shig_specimen AS (" +
			   "    SELECT DISTINCT ON (c.id) c.id as case_id," +
			   "           CAST(s.samplematerial AS text) as diagnostic_specimen " +
			   "    FROM filtered_cases c " +
			   "    JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "    WHERE pt.testresult = '" + PathogenTestResultType.POSITIVE.name() + "' " +
			   "      AND pt.testresultverified = true " +
			   "    ORDER BY c.id, COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) ASC, pt.id ASC)";
		//@formatter:on
	}

	/**
	 * Pathogen species CTE for {@code Pathogen}, from {@code pathogentest.specie}.
	 *
	 * <p>
	 * Kept separate from specimen CTE so species selection is independent of the diagnostic-test row.
	 * Selects the oldest undeleted, positive, verified test whose species has a SHIG code, so a
	 * placeholder such as {@code UNKNOWN} recorded first does not hide a species identified later.
	 * Scalar subquery keeps a row per case even when no typed test exists.
	 */
	private static String buildShigPathogenCte() {

		String mappedSpecies = Stream.of(EpipulseShigPathogenRef.values())
			.map(ref -> "'" + ref.getSpecie().name() + "'")
			.collect(Collectors.joining(", "));

		//@formatter:off
		return "shig_pathogen AS (" +
			   "    SELECT c.id as case_id," +
			   "           (SELECT CAST(pt.specie AS text) " +
			   "            FROM samples s " +
			   "            JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "            WHERE s.associatedcase_id = c.id AND s.deleted = false " +
			   "              AND pt.testresult = '" + PathogenTestResultType.POSITIVE.name() + "' " +
			   "              AND pt.testresultverified = true " +
			   "              AND pt.specie IN (" + mappedSpecies + ") " +
			   "            ORDER BY COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) ASC, pt.id ASC " +
			   "            LIMIT 1) as pathogen_specie " +
			   "    FROM filtered_cases c)";
		//@formatter:on
	}
}
