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
package de.symeda.sormas.backend.epipulse.disease;

import java.util.List;
import java.util.function.Function;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.sample.SeroGroupSpecification;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;

/**
 * Shared field wiring for {@code Serogroup}.
 *
 * <p>
 * SQL provides a {@link SeroGroupSpecification} name (from {@code EpipulseSerogroupSql}); this
 * class maps it to subject-code-specific EpiPulse reference codes via caller-provided lookup.
 */
final class SerogroupFields {

	private SerogroupFields() {
	}

	/**
	 * Adds {@code Serogroup} from the given SQL expression.
	 *
	 * @param toEpipulseCode
	 *            subject-code lookup; returns {@code null} when no mapping exists
	 */
	static void addSerogroup(List<ValueDef> fields, String selectExpression, Function<SeroGroupSpecification, String> toEpipulseCode) {

		fields.add(
			ValueDef.direct(
				"serogroup",
				selectExpression,
				(dto, row) -> readSerogroup(dto, row, toEpipulseCode),
				EpipulseVariable.SEROGROUP,
				EpipulseMapping::serogroup));
	}

	/**
	 * Parses and maps SQL serogroup value to EpiPulse code.
	 *
	 * <p>
	 * Missing, unmapped, or no-longer-valid enum values are exported as blank.
	 */
	private static void readSerogroup(EpipulseDiseaseExportEntryDto dto, EpipulseRow row, Function<SeroGroupSpecification, String> toEpipulseCode) {

		SeroGroupSpecification seroGroup = FieldReaders.parseEnum(SeroGroupSpecification.class, (String) row.get("serogroup"));
		if (seroGroup == null) {
			return;
		}

		String code = toEpipulseCode.apply(seroGroup);
		if (code != null) {
			dto.setSerogroup(code);
		}
	}
}
