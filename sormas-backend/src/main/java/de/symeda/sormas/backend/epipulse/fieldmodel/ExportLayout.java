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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;

/**
 * CSV form for one export: the measured width of every repeatable field group.
 * 
 * The constructor measures once, up front: repeatable fields cannot be in a CSV unless their width is known,
 * and all cases must share that width. Methods {@link #columnNames()} and {@link #row} cannot be reached
 * before measurement is complete.
 * 
 * Per-export state, built fresh each time (unlike the immutable, shared FieldModel).
 */
public final class ExportLayout {

	private final List<ValueDef> fields;
	private final Map<String, Integer> widths;
	private final List<String> columnNames;

	ExportLayout(List<ValueDef> fields, List<EpipulseDiseaseExportEntryDto> entries) {

		this.fields = fields;
		this.widths = new HashMap<>();

		for (ValueDef field : fields) {
			if (field.isRepeated()) {
				widths.put(field.getEpiPulseFieldName(), widest(field, entries));
			}
		}

		List<String> names = new ArrayList<>();
		for (ValueDef field : fields) {
			if (field.hasCsvColumn()) {
				String name = field.getEpiPulseFieldName();
				for (int i = 0; i < columnCount(field); i++) {
					names.add(name);
				}
			}
		}
		this.columnNames = Collections.unmodifiableList(names);
	}

	private static int widest(ValueDef field, List<EpipulseDiseaseExportEntryDto> entries) {

		int widest = 0;
		for (EpipulseDiseaseExportEntryDto entry : entries) {
			widest = Math.max(widest, field.csvValues(entry).size());
		}

		return widest;
	}

	private int columnCount(ValueDef field) {
		return field.isRepeated() ? widths.get(field.getEpiPulseFieldName()) : 1;
	}

	/** CSV header, each repeatable field's name repeated to its measured width. */
	public List<String> columnNames() {
		return columnNames;
	}

	/**
	 * One case as a CSV row, padded to match {@link #columnNames()} width.
	 * 
	 * Padding is the layout's job; getValue on the DTO returns only what the case has.
	 */
	public String[] row(EpipulseDiseaseExportEntryDto dto) {

		String[] line = new String[columnNames.size()];

		int index = 0;
		for (ValueDef field : fields) {
			if (!field.hasCsvColumn()) {
				continue;
			}

			if (!field.isRepeated()) {
				line[index++] = field.csvValue(dto);
				continue;
			}

			List<String> values = field.csvValues(dto);
			int count = columnCount(field);
			for (int i = 0; i < count; i++) {
				line[index++] = i < values.size() ? values.get(i) : "";
			}
		}

		return line;
	}
}
