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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Describes one SQL query: its CTEs, FROM table, and joins.
 * 
 * {@code EpipulseCommonSql} produces the shared half (with FROM table);
 * disease-specific {@code ...Sql} classes produce their own half.
 * {@link #plus(SqlQueryModel)} concatenates them for full query assembly.
 * 
 * Order is load-bearing: PostgreSQL resolves WITH items only against those before it,
 * so {@code config_data} must follow {@code variables}, and disease CTEs follow {@code filtered_cases}.
 * Common-then-disease concatenation satisfies both without sorting.
 * 
 * Diseases with no additions use {@link #none()}.
 */
public final class SqlQueryModel {

	private static final SqlQueryModel NONE = new SqlQueryModel(Collections.emptyList(), null, Collections.emptyList());

	private final List<String> ctes;
	private final String from;
	private final List<String> joins;

	private SqlQueryModel(List<String> ctes, String from, List<String> joins) {
		this.ctes = ctes;
		this.from = from;
		this.joins = joins;
	}

	/** For diseases whose fields all come from the common query (expected to be the majority). */
	public static SqlQueryModel none() {
		return NONE;
	}

	/**
	 * Returns this model's CTEs and joins, then the other's.
	 * 
	 * The FROM table comes from whichever model declares one; disease models don't,
	 * because they select from already-filtered cases. Both declaring a table is an error.
	 */
	public SqlQueryModel plus(SqlQueryModel other) {

		if (from != null && other.from != null) {
			throw new IllegalArgumentException("Both query models declare a FROM table: '" + from + "' and '" + other.from + "'");
		}

		List<String> mergedCtes = new ArrayList<>(ctes);
		mergedCtes.addAll(other.ctes);

		List<String> mergedJoins = new ArrayList<>(joins);
		mergedJoins.addAll(other.joins);

		return new SqlQueryModel(mergedCtes, from != null ? from : other.from, mergedJoins);
	}

	/**
	 * Returns the complete WITH clause (comma-separated), or empty string if no CTEs.
	 */
	public String withClause() {

		if (ctes.isEmpty()) {
			return "";
		}

		StringJoiner joiner = new StringJoiner(", ", "WITH ", " ");
		for (String cte : ctes) {
			if (cte != null && !cte.trim().isEmpty()) {
				joiner.add(cte.trim());
			}
		}
		return joiner.toString();
	}

	/**
	 * Returns the FROM clause and all joins in declaration order.
	 * 
	 * @throws IllegalStateException
	 *             if no model declared a FROM table
	 */
	public String fromAndJoins() {

		if (from == null) {
			throw new IllegalStateException("No FROM table: a disease query model has to be merged onto the common one");
		}

		StringBuilder sql = new StringBuilder(" FROM ").append(from);
		for (String join : joins) {
			sql.append(' ').append(join);
		}
		return sql.toString();
	}

	public List<String> getCtes() {
		return Collections.unmodifiableList(ctes);
	}

	public List<String> getJoins() {
		return Collections.unmodifiableList(joins);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private final List<String> ctes = new ArrayList<>();
		private final List<String> joins = new ArrayList<>();
		private String from;

		/**
		 * Adds CTEs in dependency order; each may reference those already added, nothing after it.
		 */
		public Builder ctes(String... cteFragments) {
			ctes.addAll(Arrays.asList(cteFragments));
			return this;
		}

		/** FROM table, declared by the common model only. */
		public Builder from(String fromTable) {
			this.from = fromTable;
			return this;
		}

		public Builder joins(String... joinClauses) {
			joins.addAll(Arrays.asList(joinClauses));
			return this;
		}

		public SqlQueryModel build() {
			return new SqlQueryModel(ctes, from, joins);
		}
	}
}
