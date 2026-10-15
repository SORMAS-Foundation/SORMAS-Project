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

import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.backend.epipulse.sql.EpipulseAstSql.AstDrugField;

/**
 * Disease-specific SQL for MENI.
 *
 * <p>
 * Declares all MENI CTEs and aliases consumed by {@link MeniFieldModel}. Alias conflicts fail
 * fast (duplicate alias checks or SQL errors).
 */
public final class MeniSql {

	private MeniSql() {
	}

	/**
	 * Declares MENI CTEs and joins.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(buildMeniSerogroupCte(), buildMeniMolecularTypingCte(), buildMeniEpidataCte(), buildMeniAstDataCte())
			.joins(
				"LEFT JOIN meni_serogroup meni_sero ON meni_sero.case_id = c.id",
				"LEFT JOIN meni_molecular_typing meni_mol ON meni_mol.case_id = c.id",
				"LEFT JOIN meni_epidata meni_epi ON meni_epi.case_id = c.id",
				"LEFT JOIN meni_ast_data meni_ast ON meni_ast.case_id = c.id")
			.build();
	}

	/**
	 * Serogroup from the newest qualifying grouping test, using shared serogroup CTE logic.
	 * MENI accepts {@code SEROGROUPING} and {@code SLIDE_AGGLUTINATION} test types.
	 */
	private static String buildMeniSerogroupCte() {
		return EpipulseSerogroupSql.buildSerogroupCte("meni_serogroup", PathogenTestType.SEROGROUPING, PathogenTestType.SLIDE_AGGLUTINATION);
	}

	/**
	 * Molecular typing CTE for {@code PorA1}, {@code PorA2}, and {@code FetVR}.
	 *
	 * <p>
	 * Values are parsed from free-text {@code pathogentest.typingid} on MLST/sequencing tests,
	 * classified by pattern: {@code P1.*} (PorA1), numeric or non-P1/F/ST text (PorA2), and
	 * {@code F*} except literal {@code FALSE} (FetVR). Each field falls back to {@code NST} when
	 * sequencing is {@code INDETERMINATE}.
	 *
	 * <p>
	 * Free-text parsing is intentional here because these MENI-only outputs have no enum/reference
	 * mapping in SORMAS.
	 */
	private static String buildMeniMolecularTypingCte() {
		//@formatter:off
		return "meni_molecular_typing AS (" +
			   "    SELECT c.id as case_id," +
			   // PorA1: parsed value, otherwise NST when sequencing was indeterminate
			   "           COALESCE(" +
			   "               MAX(CASE WHEN pt.typingid LIKE 'P1.%' THEN pt.typingid END)," +
			   "               MAX(CASE WHEN pt.testtype IN ('SEQUENCING', 'WHOLE_GENOME_SEQUENCING') AND pt.testresult = 'INDETERMINATE' THEN 'NST' END)" +
			   "           ) as pora1," +
			   // PorA2: numeric/other non-P1-F-ST value, otherwise NST fallback
			   "           COALESCE(" +
			   "               MAX(CASE WHEN pt.typingid ~ '^[0-9]+$' OR (pt.typingid NOT LIKE 'P1.%' AND pt.typingid NOT LIKE 'F%' AND pt.typingid NOT LIKE 'ST-%' AND pt.typingid IS NOT NULL) THEN pt.typingid END)," +
			   "               MAX(CASE WHEN pt.testtype IN ('SEQUENCING', 'WHOLE_GENOME_SEQUENCING') AND pt.testresult = 'INDETERMINATE' THEN 'NST' END)" +
			   "           ) as pora2," +
			   // FetVR: F* value (except FALSE), otherwise NST fallback
			   "           COALESCE(" +
			   "               MAX(CASE WHEN pt.typingid LIKE 'F%' AND pt.typingid NOT LIKE 'FALSE' THEN pt.typingid END)," +
			   "               MAX(CASE WHEN pt.testtype IN ('SEQUENCING', 'WHOLE_GENOME_SEQUENCING') AND pt.testresult = 'INDETERMINATE' THEN 'NST' END)" +
			   "           ) as fetvr " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    LEFT JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "        AND pt.testtype IN ('MULTILOCUS_SEQUENCE_TYPING', 'SEQUENCING', 'WHOLE_GENOME_SEQUENCING') " +
			   "    GROUP BY c.id" +
			   ")";
		//@formatter:on
	}

	/**
	 * MENI's direct {@code epidata} export: imported status.
	 */
	private static String buildMeniEpidataCte() {
		//@formatter:off
		return "meni_epidata AS (" +
			   "    SELECT c.id as case_id," +
			   "           CAST(e.caseimportedstatus AS text) as imported_status " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN epidata e ON c.epidata_id = e.id" +
			   ")";
		//@formatter:on
	}

	/**
	 * AST CTE via shared builder, for MENI drugs CIP, CTX, PEN, and RIF.
	 */
	private static String buildMeniAstDataCte() {
		return EpipulseAstSql.buildAstDataCte(
			"meni_ast_data",
			Arrays.asList(
				new AstDrugField("ciprofloxacinmic", "ciprofloxacinsusceptibility", "cip"),
				new AstDrugField("ceftriaxonemic", "ceftriaxonesusceptibility", "ctx"),
				new AstDrugField("penicillinmic", "penicillinsusceptibility", "pen"),
				new AstDrugField("rifampicinmic", "rifampicinsusceptibility", "rif")));
	}
}
