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

import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;

/**
 * Shared {@code CountryOfNationality} field block.
 *
 * <p>
 * Reads {@code Person.citizenship} as required by EpiPulse's citizenship definition. Models add
 * this together with the matching SQL fragment.
 *
 * @see CountryOfBirthFields
 */
final class CountryOfNationalityFields {

	private CountryOfNationalityFields() {
	}

	static void addCountryOfNationality(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"citizenship_nutscode",
				"person_citizenship.nutscode",
				(dto, row) -> dto.setCitizenshipCountryNutsCode((String) row.get("citizenship_nutscode")),
				EpipulseVariable.COUNTRY_OF_NATIONALITY,
				EpipulseMapping::countryOfNationality));
	}
}
