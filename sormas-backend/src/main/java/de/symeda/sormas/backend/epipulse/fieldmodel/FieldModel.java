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

package de.symeda.sormas.backend.epipulse.fieldmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.backend.epipulse.sql.SqlQueryModel;

/**
 * Ordered list of {@link ValueDef} for one disease: single source of truth for SELECT order, column positions,
 * CSV header order, and CSV row order.
 * 
 * Reading and writing both walk the same list with running counters, so they cannot diverge.
 * The model owns the whole query: common fields first, then disease-specific columns.
 */
public final class FieldModel {

	private final List<ValueDef> fields;
	private final SqlQueryModel sqlQueryModel;
	private final Map<String, Integer> positionByAlias;

	/**
	 * @param fields
	 *            the disease's fields, in output order
	 * @param sqlQueryModel
	 *            the CTEs and joins its SELECT columns depend on
	 * @param selectOffset
	 *            position of this model's first SELECT column within the result row; zero unless
	 *            some other producer contributes columns ahead of it
	 */
	public FieldModel(List<ValueDef> fields, SqlQueryModel sqlQueryModel, int selectOffset) {

		this.fields = new ArrayList<>(fields);
		this.sqlQueryModel = sqlQueryModel;
		this.positionByAlias = new HashMap<>();

		int position = selectOffset;
		for (ValueDef field : this.fields) {
			if (!field.hasSelectColumn()) {
				continue;
			}
			if (positionByAlias.put(field.getAlias(), position++) != null) {
				throw new IllegalStateException(
					"Duplicate column alias '" + field.getAlias() + "'. Aliases must be unique for row access to be unambiguous.");
			}
		}
	}

	public SqlQueryModel getSqlQueryModel() {
		return sqlQueryModel;
	}

	/**
	 * Finds the definition emitting a variable, allowing name-based instead of position-based column access.
	 * 
	 * Package-private; used by tests to read {@code definitionOf(AGE).csvValue(entry)} without indexing.
	 * 
	 * @throws IllegalArgumentException
	 *             if no definition emits the variable
	 * @throws IllegalStateException
	 *             if multiple definitions emit it
	 */
	ValueDef definitionOf(EpipulseVariable variable) {

		ValueDef found = null;
		for (ValueDef field : fields) {
			if (field.hasCsvColumn() && field.getVariable() == variable) {
				if (found != null) {
					throw new IllegalStateException("More than one definition emits " + variable);
				}
				found = field;
			}
		}

		if (found == null) {
			throw new IllegalArgumentException("No definition emits " + variable);
		}

		return found;
	}

	/** Disease-specific SELECT columns, in list order with aliases; empty string if none. */
	public String buildSelectColumns() {

		StringJoiner columns = new StringJoiner(", ");
		for (ValueDef field : fields) {
			if (field.hasSelectColumn()) {
				columns.add(field.getSelectExpression() + " AS " + field.getAlias());
			}
		}

		return columns.toString();
	}

	/** Copies model columns from a query result row onto the entry DTO. */
	public void readInto(EpipulseDiseaseExportEntryDto dto, Object[] row, ExportContext context) {

		EpipulseRow namedRow = new EpipulseRow(row, positionByAlias, context);
		for (ValueDef field : fields) {
			field.read(dto, namedRow);
		}
	}

	/**
	 * Measures repeatable field widths across all cases and returns the export's CSV shape.
	 * 
	 * Call once per export before writing; width measurement cannot be read separately.
	 */
	public ExportLayout layout(List<EpipulseDiseaseExportEntryDto> rows) {
		return new ExportLayout(fields, rows);
	}
}
