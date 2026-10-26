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

import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;

/**
 * PlaceOfInfection column (28 of 37 EpiPulse subject codes declare it). Optional fragment: models
 * opt-in and merge EpipulsePlaceOfInfectionSql.spec(). Width decided per-model (MENI floors to >= 1;
 * MEAS can emit 0 when no case carries a place).
 */
final class PlaceOfInfectionFields {

	private PlaceOfInfectionFields() {
	}

	static void addPlaceOfInfectionSource(List<ValueDef> fields) {

		fields.add(
			ValueDef.sqlOnly(
				"places_of_infection",
				"poi.places_of_infection",
				(dto, row) -> dto.setPlaceOfInfection(FieldReaders.splitCollection((String) row.get("places_of_infection")))));
	}
}
