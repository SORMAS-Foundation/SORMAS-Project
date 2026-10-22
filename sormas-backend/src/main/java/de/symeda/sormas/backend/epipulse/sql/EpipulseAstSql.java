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

import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.sample.PathogenTestType;

/**
 * Generator for disease-specific AST CTEs.
 *
 * <p>
 * Unlike fixed {@code Epipulse...Sql} fragments, this class builds a CTE shape from provided drug
 * field definitions, so callers can produce different AST panels (for example MENI vs PNEU).
 */
public final class EpipulseAstSql {

	private EpipulseAstSql() {
	}

	/**
	 * Builds an AST CTE selecting the newest verified antibiotic-susceptibility test per case that
	 * carries a MIC or susceptibility for at least one of {@code drugs}.
	 *
	 * <p>
	 * The panel check sits inside the join, before the newest test is picked: a newer AST test
	 * without results for this panel would otherwise hide an older one that has them.
	 *
	 * <p>
	 * Includes caller-defined MIC/susceptibility columns plus {@code drugsusceptibility_id}, so
	 * callers can join the exact selected row for extra fields without reimplementing selection.
	 *
	 * @param cteName
	 *            CTE name (for example {@code meni_ast_data})
	 * @param drugs
	 *            drug field mapping definitions
	 * @return SQL text for the generated CTE
	 */
	public static String buildAstDataCte(String cteName, List<AstDrugField> drugs) {
		String drugColumns = drugs.stream()
			.map(
				drug -> "           ds." + drug.micColumn + "," + "           CAST(ds." + drug.susceptibilityColumn + " AS text) as " + drug.alias
					+ "_susceptibility")
			.collect(Collectors.joining(","));
		String panelHasData = drugs.stream()
			// MIC is free text, so an empty string is no result either
			.map(drug -> "ds." + drug.micColumn + " <> '' OR ds." + drug.susceptibilityColumn + " IS NOT NULL")
			.collect(Collectors.joining(" OR "));

		//@formatter:off
		return cteName + " AS (" +
			   "    SELECT DISTINCT ON (c.id) c.id as case_id," +
			   "           ds.id as drugsusceptibility_id," +
			   drugColumns + " " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    LEFT JOIN (pathogentest pt " +
			   "        JOIN drugsusceptibility ds ON pt.drugsusceptibility_id = ds.id AND (" + panelHasData + ")) " +
			   "        ON pt.sample_id = s.id AND " + exportDiseaseTest("pt") + " AND pt.testresultverified = true " +
			   "        AND pt.testtype = '" + PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY.name() + "' " +
			   "    ORDER BY c.id, COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) DESC NULLS LAST, pt.id DESC" +
			   ")";
		//@formatter:on
	}

	/**
	 * Drug column mapping used by {@link #buildAstDataCte(String, List)}.
	 */
	public static class AstDrugField {

		public final String micColumn;
		public final String susceptibilityColumn;
		public final String alias;

		public AstDrugField(String micColumn, String susceptibilityColumn, String alias) {
			this.micColumn = micColumn;
			this.susceptibilityColumn = susceptibilityColumn;
			this.alias = alias;
		}
	}
}
