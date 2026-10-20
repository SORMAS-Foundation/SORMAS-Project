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

import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.backend.epipulse.sql.EpipulseAstSql.AstDrugField;

/**
 * Disease-specific SQL for SALM.
 *
 * <p>
 * Defines SALM's two CTEs and three joins. {@code epidata} is joined directly (1:1 by case FK),
 * while AST and reference-lab specimen data require CTEs.
 */
public final class SalmSql {

	private SalmSql() {
	}

	/**
	 * Declares SALM CTEs and joins:
	 * {@code epidata}, {@code salm_ast_data}, {@code salm_reference_lab}.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(buildSalmAstDataCte(), buildSalmReferenceLabCte())
			.joins(
				// Imported, ModeOfTransmission, SuspectedVehicle
				"LEFT JOIN epidata salm_epidata ON c.epidata_id = salm_epidata.id",
				"LEFT JOIN salm_ast_data salm_ast ON salm_ast.case_id = c.id",
				"LEFT JOIN salm_reference_lab salm_lab ON salm_lab.case_id = c.id")
			.build();
	}

	/**
	 * AST panel CTE for SALM {@code SIR_*} columns.
	 *
	 * <p>
	 * SALM declares more drugs than SORMAS stores in {@code drugsusceptibility}; this CTE includes
	 * only available columns (AMP, CAZ, CTX, CIP, SXT). Remaining SALM drugs are exported blank in
	 * the field model.
	 */
	private static String buildSalmAstDataCte() {
		return EpipulseAstSql.buildAstDataCte(
			"salm_ast_data",
			Arrays.asList(
				new AstDrugField("ampicillinmic", "ampicillinsusceptibility", "amp"),
				new AstDrugField("ceftazidimemic", "ceftazidimesusceptibility", "caz"),
				new AstDrugField("cefotaximemic", "cefotaximesusceptibility", "ctx"),
				new AstDrugField("ciprofloxacinmic", "ciprofloxacinsusceptibility", "cip"),
				new AstDrugField("trimethoprimsulfamethoxazolemic", "trimethoprimsulfamethoxazolesusceptibility", "sxt")));
	}

	/**
	 * Reference-lab specimen CTE for {@code DateOfReceiptReferenceLab} and {@code Specimen}.
	 *
	 * <p>
	 * Selects the earliest qualifying test per case (oldest by
	 * {@code COALESCE(testdatetime, reportdate, creationdate)}, then {@code pt.id}) from undeleted
	 * sample/test rows where test is positive, verified, and explicitly marked
	 * {@code performedbyreferencelaboratory = true}. Both output columns are taken from that one
	 * selected sample row.
	 */
	private static String buildSalmReferenceLabCte() {
		//@formatter:off
		return "salm_reference_lab AS (" +
			   "    SELECT DISTINCT ON (c.id) c.id as case_id," +
			   "           CAST(s.receiveddate AS date) as reference_lab_receipt_date," +
			   "           CAST(s.samplematerial AS text) as reference_lab_specimen " +
			   "    FROM filtered_cases c " +
			   "    JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "    WHERE pt.performedbyreferencelaboratory = true " +
			   "      AND pt.testresult = '" + PathogenTestResultType.POSITIVE.name() + "' " +
			   "      AND pt.testresultverified = true " +
			   "    ORDER BY c.id, COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) ASC, pt.id ASC)";
		//@formatter:on
	}
}
