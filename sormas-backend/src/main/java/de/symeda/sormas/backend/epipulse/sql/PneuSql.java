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

import java.util.Arrays;

import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.backend.epipulse.sql.EpipulseAstSql.AstDrugField;

/**
 * Disease-specific SQL for PNEU.
 *
 * <p>
 * Defines PNEU CTEs/aliases consumed by {@link PneuFieldModel}. Alias mismatches fail fast
 * through duplicate checks or SQL errors.
 */
public final class PneuSql {

	private PneuSql() {
	}

	/**
	 * Declares PNEU's four CTEs and joins.
	 *
	 * <p>
	 * {@code nrl_data_cte} remains local because only PNEU uses it.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(buildNrlDataCte(), buildPneuSerotypingMethodCte(), buildPneuAstDataCte(), buildPneuAstMethodCte())
			.joins(
				"LEFT JOIN nrl_data_cte nrl ON nrl.case_id = c.id",
				"LEFT JOIN pneu_serotyping_method pneu_sero ON pneu_sero.case_id = c.id",
				"LEFT JOIN pneu_ast_data pneu_ast ON pneu_ast.case_id = c.id",
				"LEFT JOIN pneu_ast_method pneu_astm ON pneu_astm.case_id = c.id")
			.build();
	}

	/**
	 * Builds {@code NRLData}: true if at least one undeleted test was performed by a reference
	 * laboratory ({@code pathogentest.performedbyreferencelaboratory = true}); otherwise false.
	 */
	private static String buildNrlDataCte() {
		//@formatter:off
		return "nrl_data_cte AS (" +
			   "    SELECT c.id as case_id," +
			   "           EXISTS (" +
			   "               SELECT 1 FROM samples s" +
			   "               JOIN pathogentest pt ON pt.sample_id = s.id" +
			   "               WHERE s.associatedcase_id = c.id" +
			   "                 AND s.deleted = false" +
			   "                 AND " + exportDiseaseTest("pt") +
			   "                 AND pt.performedbyreferencelaboratory = true" +
			   "           ) as nrl_data" +
			   "    FROM filtered_cases c" +
			   ")";
		//@formatter:on
	}

	/**
	 * Builds serotyping methods for {@code PathogenDetectionMethod}.
	 *
	 * <p>
	 * Aggregates distinct {@code pathogentest.serotypingmethod} values from positive, verified,
	 * undeleted tests of type {@code SEROGROUPING} or {@code SEROTYPING}, as a {@code #}-separated
	 * list. This reports actual methods used for serotyping, not generic test types.
	 */
	private static String buildPneuSerotypingMethodCte() {
		//@formatter:off
		return "pneu_serotyping_method AS (" +
			   "    SELECT c.id as case_id," +
			   "           STRING_AGG(DISTINCT CAST(pt.serotypingmethod AS text), " + SQL_COLLECTION_SEPARATOR +
			   "                      ORDER BY CAST(pt.serotypingmethod AS text)) as serotyping_methods " +
			   "    FROM filtered_cases c " +
			   "    JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "    WHERE pt.testtype IN ('" + PathogenTestType.SEROGROUPING.name() + "', '" + PathogenTestType.SEROTYPING.name() + "') " +
			   "      AND pt.testresult = '" + PathogenTestResultType.POSITIVE.name() + "' " +
			   "      AND pt.testresultverified = true " +
			   "      AND pt.serotypingmethod IS NOT NULL " +
			   "    GROUP BY c.id)";
		//@formatter:on
	}

	/**
	 * AST result CTE via shared builder for CTX, ERY, and PEN.
	 */
	private static String buildPneuAstDataCte() {
		return EpipulseAstSql.buildAstDataCte(
			"pneu_ast_data",
			Arrays.asList(
				new AstDrugField("ceftriaxonemic", "ceftriaxonesusceptibility", "ctx"),
				new AstDrugField("erythromycinmic", "erythromycinsusceptibility", "ery"),
				new AstDrugField("penicillinmic", "penicillinsusceptibility", "pen")));
	}

	/**
	 * Builds {@code ASTMethod} columns by joining the selected {@code pneu_ast_data}
	 * {@code drugsusceptibility_id} to {@code drugsusceptibility}.
	 *
	 * <p>
	 * This keeps MIC/susceptibility and method columns on the same AST row; no AST means no row.
	 */
	private static String buildPneuAstMethodCte() {
		//@formatter:off
		return "pneu_ast_method AS (" +
			   "    SELECT a.case_id," +
			   "           CAST(ds.ceftriaxonemethod AS text) as ctx_method," +
			   "           CAST(ds.erythromycinmethod AS text) as ery_method," +
			   "           CAST(ds.penicillinmethod AS text) as pen_method " +
			   "    FROM pneu_ast_data a " +
			   "    JOIN drugsusceptibility ds ON ds.id = a.drugsusceptibility_id" +
			   ")";
		//@formatter:on
	}
}
