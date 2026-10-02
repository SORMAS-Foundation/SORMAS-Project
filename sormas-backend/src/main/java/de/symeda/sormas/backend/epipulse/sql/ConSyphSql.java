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
 * Disease-specific SQL for CONSYPH.
 *
 * <p>
 * Adds the mother-related country fields by joining {@code epidata} and then two
 * {@code country} rows (mother country of birth and citizenship). These attributes are stored on
 * case epidemiological data, not on a separate mother entity.
 *
 * <p>
 * Kept as a dedicated fragment because these two variables are CONSYPH-only.
 */
public final class ConSyphSql {

	private ConSyphSql() {
	}

	public static SqlQueryModel spec() {

		//@formatter:off
		return SqlQueryModel.builder()
			.joins(
				"LEFT JOIN epidata consyph_epidata ON c.epidata_id = consyph_epidata.id",
				"LEFT JOIN country consyph_mother_birth_country ON consyph_epidata.mothercountryofbirth_id = consyph_mother_birth_country.id",
				"LEFT JOIN country consyph_mother_citizenship ON consyph_epidata.mothercitizenship_id = consyph_mother_citizenship.id")
			.build();
		//@formatter:on
	}
}
