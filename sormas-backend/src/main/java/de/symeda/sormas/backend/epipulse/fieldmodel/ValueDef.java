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

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseVariable;

/**
 * One field of a disease export: where its value comes from in SQL, how it reaches the DTO, how it leaves as CSV.
 * 
 * Definitions come in four kinds: Direct (SELECT + CSV), SQL-only (SELECT no CSV), Calculated (CSV no SELECT),
 * Context-only (neither). Position is not stored; {@link FieldModel} derives it from list position with counters.
 * 
 * @see FieldModel
 */
public final class ValueDef {

	private final String alias;
	private final String selectExpression;
	private final BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> reader;
	private final EpipulseVariable variable;
	private final Function<EpipulseDiseaseExportEntryDto, String> csvValue;
	private final Function<EpipulseDiseaseExportEntryDto, List<String>> csvValues;

	private ValueDef(
		String alias,
		String selectExpression,
		BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> reader,
		EpipulseVariable variable,
		Function<EpipulseDiseaseExportEntryDto, String> csvValue,
		Function<EpipulseDiseaseExportEntryDto, List<String>> csvValues) {

		this.alias = alias;
		this.selectExpression = selectExpression;
		this.reader = reader;
		this.variable = variable;
		this.csvValue = csvValue;
		this.csvValues = csvValues;
	}

	/** One SELECT column that becomes one CSV column. */
	public static ValueDef direct(
		String alias,
		String selectExpression,
		BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> reader,
		EpipulseVariable variable,
		Function<EpipulseDiseaseExportEntryDto, String> csvValue) {

		return new ValueDef(alias, selectExpression, reader, variable, csvValue, null);
	}

	/** SELECT column feeding DTO state, no CSV column emitted. */
	public static ValueDef sqlOnly(String alias, String selectExpression, BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> reader) {
		return new ValueDef(alias, selectExpression, reader, null, null, null);
	}

	/**
	 * DTO state from the export context, not from the row; neither SELECT nor CSV column.
	 * 
	 * For export-level values like the server's own NUTS code. Keeping it in the field list ensures
	 * it is declared once and cannot be forgotten.
	 */
	public static ValueDef contextOnly(BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> reader) {
		return new ValueDef(null, null, reader, null, null, null);
	}

	/** CSV column derived from DTO state, no SELECT column. */
	public static ValueDef calculated(EpipulseVariable variable, Function<EpipulseDiseaseExportEntryDto, String> csvValue) {

		return new ValueDef(null, null, null, variable, csvValue, null);
	}

	/**
	 * Empty CSV column for a declared variable with no SORMAS source.
	 * 
	 * EpiPulse requires declared variables to be present; omitting one silently changes the file's shape.
	 * This is the honest placeholder: an empty column says "no answer", not "missing".
	 */
	public static ValueDef blank(EpipulseVariable variable) {
		return calculated(variable, entry -> null);
	}

	/**
	 * CSV column group sized to the widest case in the export; short cases padded at write time.
	 * 
	 * Width is measured once across all cases; short cases are padded, not the column group hidden.
	 * Use {@link #atLeastOneColumn} to keep one blank column when all cases are empty.
	 */
	public static ValueDef repeated(EpipulseVariable variable, Function<EpipulseDiseaseExportEntryDto, List<String>> csvValues) {
		return new ValueDef(null, null, null, variable, null, csvValues);
	}

	/** Ensures a repeatable group keeps one blank column even when all cases are empty. */
	public static List<String> atLeastOneColumn(List<String> values) {
		return values == null || values.isEmpty() ? Collections.singletonList("") : values;
	}

	boolean hasSelectColumn() {
		return selectExpression != null;
	}

	boolean hasCsvColumn() {
		return variable != null;
	}

	String getAlias() {
		return alias;
	}

	String getSelectExpression() {
		return selectExpression;
	}

	/** The EpiPulse variable this column reports, or null if no CSV column is emitted. */
	public EpipulseVariable getVariable() {
		return variable;
	}

	/** CSV column header: the variable's EpiPulse name exactly as spelled. */
	String getEpiPulseFieldName() {
		return variable.getVariableName();
	}

	/** Whether this is a repeatable field with export-decided width. */
	boolean isRepeated() {
		return csvValues != null;
	}

	void read(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {
		if (reader != null) {
			reader.accept(dto, row);
		}
	}

	/** This case's values for a repeatable field (never null, empty list if none). */
	List<String> csvValues(EpipulseDiseaseExportEntryDto dto) {

		List<String> values = csvValues.apply(dto);

		return values == null ? Collections.emptyList() : values;
	}

	/**
	 * This case's value for a fixed field (never null; empty string if absent).
	 * 
	 * Blanking is the layout's job: CSVUtils.SafeCSVWriter skips nulls, making null != "".
	 */
	String csvValue(EpipulseDiseaseExportEntryDto dto) {

		String value = csvValue.apply(dto);

		return value == null ? "" : value;
	}
}
