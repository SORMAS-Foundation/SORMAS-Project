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

import de.symeda.sormas.api.epipulse.referencevalue.EpipulseTubePathogenRef;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.backend.epipulse.sql.EpipulseAstSql.AstDrugField;

/**
 * Disease-specific SQL for TUBE.
 *
 * <p>
 * Declares six CTEs plus a 1:1 health-conditions join. AST uses the shared builder; culture,
 * microscopy, nucleic acid and histopathology results are aggregated per test family instead of
 * selecting one test row.
 */
public final class TubeSql {

	/**
	 * The 14 TUBE {@code SIR_*} drugs, mapped to {@code drugsusceptibility} fields.
	 * Aliases match the keys consumed by {@code TubeFieldModel}.
	 */
	private static final AstDrugField[] TUBE_DRUGS = {
		new AstDrugField("amikacinmic", "amikacinsusceptibility", "amk"),
		new AstDrugField("bedaquilinemic", "bedaquilinesusceptibility", "bdq"),
		new AstDrugField("clofaziminemic", "clofaziminesusceptibility", "clf"),
		new AstDrugField("delamanidmic", "delamanidsusceptibility", "dlm"),
		new AstDrugField("ethambutolmic", "ethambutolsusceptibility", "eth"),
		new AstDrugField("ethionamidemic", "ethionamidesusceptibility", "eti"),
		new AstDrugField("isoniazidmic", "isoniazidsusceptibility", "inh"),
		new AstDrugField("linezolidmic", "linezolidsusceptibility", "lnz"),
		new AstDrugField("levofloxacinmic", "levofloxacinsusceptibility", "lvx"),
		new AstDrugField("moxifloxacinmic", "moxifloxacinsusceptibility", "mfx"),
		new AstDrugField("pretomanidmic", "pretomanidsusceptibility", "ptm"),
		new AstDrugField("pyrazinamidemic", "pyrazinamidesusceptibility", "pza"),
		new AstDrugField("rifampicinmic", "rifampicinsusceptibility", "rif"),
		new AstDrugField("streptomycinmic", "streptomycinsusceptibility", "str") };

	private TubeSql() {
	}

	/**
	 * Declares TUBE CTEs and joins.
	 */
	public static SqlQueryModel spec() {

		return SqlQueryModel.builder()
			.ctes(
				EpipulseAstSql.buildAstDataCte("tube_ast_data", Arrays.asList(TUBE_DRUGS)),
				buildPathogenCte(),
				buildTestResultCte("tube_culture", PathogenTestType.CULTURE, PathogenTestType.BACTERIAL_CULTURE),
				buildTestResultCte("tube_microscopy", PathogenTestType.MICROSCOPY),
				buildTestResultCte("tube_nucleic_acid", PathogenTestType.PCR_RT_PCR),
				buildTestResultCte("tube_histopathology", PathogenTestType.HISTOPATHOLOGY))
			.joins(
				// HIVStatus, PrevDiagnosisYear, PrevTreatment, PrevTreatmentCompletion
				"LEFT JOIN healthconditions tube_health ON c.healthconditions_id = tube_health.id",
				"LEFT JOIN tube_ast_data tube_ast ON tube_ast.case_id = c.id",
				"LEFT JOIN tube_pathogen ON tube_pathogen.case_id = c.id",
				"LEFT JOIN tube_culture ON tube_culture.case_id = c.id",
				"LEFT JOIN tube_microscopy ON tube_microscopy.case_id = c.id",
				"LEFT JOIN tube_nucleic_acid ON tube_nucleic_acid.case_id = c.id",
				"LEFT JOIN tube_histopathology ON tube_histopathology.case_id = c.id")
			.build();
	}

	/**
	 * Species CTE for {@code Pathogen}, derived from spoligotyping.
	 *
	 * <p>
	 * Only {@code SPOLIGOTYPING} is considered, and only species with a TUBE code are eligible.
	 * Newest qualifying test wins; scalar subquery keeps one row per case.
	 */
	private static String buildPathogenCte() {

		String mappedSpecies =
			Stream.of(EpipulseTubePathogenRef.values()).map(ref -> "'" + ref.getSpecie().name() + "'").collect(Collectors.joining(", "));

		//@formatter:off
		return "tube_pathogen AS (" +
			   "    SELECT c.id as case_id," +
			   "           (SELECT CAST(pt.specie AS text) " +
			   "            FROM samples s " +
			   "            JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " " +
			   "            WHERE s.associatedcase_id = c.id AND s.deleted = false " +
			   "              AND pt.testresultverified = true " +
			   "              AND pt.testtype = '" + PathogenTestType.SPOLIGOTYPING.name() + "' " +
			   "              AND pt.specie IN (" + mappedSpecies + ") " +
			   "            ORDER BY COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) DESC NULLS LAST, pt.id DESC " +
			   "            LIMIT 1) as pathogen_specie " +
			   "    FROM filtered_cases c)";
		//@formatter:on
	}

	/**
	 * Summarizes verified tests of the given types to one {@code POS}/{@code NEG}/{@code NOTEST}
	 * value for the case.
	 *
	 * <p>
	 * Used directly for culture/microscopy and as input to {@code ResultOtherTest} for nucleic
	 * acid/histopathology. Non-positive/non-negative outcomes (e.g. pending) resolve to
	 * {@code NOTEST}.
	 */
	private static String buildTestResultCte(String cteName, PathogenTestType... testTypes) {

		String types = Stream.of(testTypes).map(type -> "'" + type.name() + "'").collect(Collectors.joining(", "));

		//@formatter:off
		return cteName + " AS (" +
			   "    SELECT c.id as case_id," +
			   "           CASE" +
			   "               WHEN COUNT(*) FILTER (WHERE pt.testresult = '" + PathogenTestResultType.POSITIVE.name() + "') > 0 THEN 'POS'" +
			   "               WHEN COUNT(*) FILTER (WHERE pt.testresult = '" + PathogenTestResultType.NEGATIVE.name() + "') > 0 THEN 'NEG'" +
			   "               ELSE 'NOTEST'" +
			   "           END as test_result " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    LEFT JOIN pathogentest pt ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") +
			   "        AND pt.testresultverified = true AND pt.testtype IN (" + types + ") " +
			   "    GROUP BY c.id)";
		//@formatter:on
	}

}
