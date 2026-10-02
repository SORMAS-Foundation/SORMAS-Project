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

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseVariable;

/**
 * Reads one export entry's CSV values by EpiPulse variable, for tests to assert on.
 *
 * 
 */
public final class EpipulseValues {

	private final FieldModel fieldModel;

	public EpipulseValues(FieldModel fieldModel) {
		this.fieldModel = fieldModel;
	}

	/**
	 * The single CSV value this entry reports for {@code variable}.
	 *
	 * @throws IllegalArgumentException
	 *             if the model emits no column for the variable
	 */
	public String value(EpipulseDiseaseExportEntryDto entry, EpipulseVariable variable) {
		return fieldModel.definitionOf(variable).csvValue(entry);
	}

	/**
	 * Every CSV value this entry reports for {@code variable}.
	 *
	 * <p>
	 * For a repeatable variable these are the values themselves, not the columns: a group measured
	 * three wide across the export but carrying two values here returns two entries, where the
	 * written row would carry a trailing blank. Assertions about that padding belong with the
	 * layout.
	 *
	 * @throws IllegalArgumentException
	 *             if the model emits no column for the variable
	 */
	public List<String> values(EpipulseDiseaseExportEntryDto entry, EpipulseVariable variable) {

		ValueDef definition = fieldModel.definitionOf(variable);

		return definition.isRepeated() ? definition.csvValues(entry) : Collections.singletonList(definition.csvValue(entry));
	}

	/**
	 * Whether this variable's column is a repeatable group rather than a single column.
	 *
	 * <p>
	 * The EpiPulse metadata carries a {@code Repeatable} flag per (subject code, variable) that is
	 * not generated into code, so a model can only be checked against it by a test that names the
	 * expectation.
	 */
	public boolean isRepeated(EpipulseVariable variable) {
		return fieldModel.definitionOf(variable).isRepeated();
	}

	/**
	 * Whether this model emits a column for {@code variable} at all - for asserting that a subject
	 * code does <em>not</em> report something, which is as much a part of the contract as what it
	 * does report.
	 */
	public boolean emits(EpipulseVariable variable) {
		try {
			fieldModel.definitionOf(variable);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
