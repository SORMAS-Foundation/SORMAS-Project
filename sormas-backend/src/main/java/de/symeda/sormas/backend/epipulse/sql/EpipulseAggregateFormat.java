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

/**
 * The text format of the aggregate columns the CTEs flatten child rows into, shared by the SQL that
 * writes them and the field readers that unflatten them, so the two sides cannot disagree.
 *
 * <p>
 * An aggregate is a {@code STRING_AGG} of values separated by {@link #COLLECTION_SEPARATOR}. Where a
 * value is a record of several fields, it is a {@code CONCAT_WS} of fields separated by
 * {@link #RECORD_SEPARATOR}. Dates inside a record are written with {@code TO_CHAR} in
 * {@link #SQL_DATE_FORMAT} and read back with {@link #DATE_PATTERN}.
 */
public final class EpipulseAggregateFormat {

	/** Separates the values of a {@code STRING_AGG} aggregate. */
	public static final String COLLECTION_SEPARATOR = "#";
	/** Separates the fields of a {@code CONCAT_WS} record. */
	public static final String RECORD_SEPARATOR = "|";
	/** The {@code SimpleDateFormat} pattern reading back dates written in {@link #SQL_DATE_FORMAT}. */
	public static final String DATE_PATTERN = "yyyy-MM-dd";

	/** {@link #COLLECTION_SEPARATOR} as an SQL string literal. */
	static final String SQL_COLLECTION_SEPARATOR = sqlLiteral(COLLECTION_SEPARATOR);
	/** {@link #RECORD_SEPARATOR} as an SQL string literal. */
	static final String SQL_RECORD_SEPARATOR = sqlLiteral(RECORD_SEPARATOR);
	/** The {@code TO_CHAR} format, as an SQL string literal, matching {@link #DATE_PATTERN}. */
	static final String SQL_DATE_FORMAT = sqlLiteral("YYYY-MM-DD");

	private EpipulseAggregateFormat() {
	}

	private static String sqlLiteral(String value) {
		return "'" + value + "'";
	}
}
