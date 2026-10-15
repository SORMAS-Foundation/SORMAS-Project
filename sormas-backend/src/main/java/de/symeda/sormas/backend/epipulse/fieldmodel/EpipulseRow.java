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

import java.util.Map;

/**
 * Query result row accessed by column alias instead of position.
 * 
 * Allows readers to consult multiple columns at once (e.g., DosePCV1 reads both date_pcv1 and brand_pcv1).
 * Alias map built from the same definitions that generated the SELECT, so map and query stay in sync.
 */
public final class EpipulseRow {

	private final Object[] values;
	private final Map<String, Integer> positionByAlias;
	private final ExportContext context;

	EpipulseRow(Object[] values, Map<String, Integer> positionByAlias, ExportContext context) {
		this.values = values;
		this.positionByAlias = positionByAlias;
		this.context = context;
	}

	/** Export-level context (server NUTS code, subject-code test types). */
	public ExportContext context() {
		return context;
	}

	/**
	 * Gets a column value by its declared alias.
	 * 
	 * @throws IllegalArgumentException
	 *             if the alias is not declared
	 */
	public Object get(String alias) {

		Integer position = positionByAlias.get(alias);
		if (position == null) {
			throw new IllegalArgumentException("No column aliased '" + alias + "' in this export. Known aliases: " + positionByAlias.keySet());
		}

		return values[position];
	}
}
