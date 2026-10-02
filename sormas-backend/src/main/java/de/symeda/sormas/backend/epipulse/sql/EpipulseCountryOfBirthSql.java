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
 * The join behind {@code CountryOfBirth}, for the subject codes that report it.
 * 
 */
public final class EpipulseCountryOfBirthSql {

	private EpipulseCountryOfBirthSql() {
	}

	public static SqlQueryModel spec() {

		return SqlQueryModel.builder().joins("LEFT JOIN country person_birth_country ON person.birthcountry_id = person_birth_country.id").build();
	}
}
