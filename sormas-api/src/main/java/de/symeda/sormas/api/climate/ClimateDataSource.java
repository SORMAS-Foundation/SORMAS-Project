/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.climate;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Where the current values of a climate observation came from. When data is saved, MANUAL outranks IMPORT, which outranks
 * OPENWEATHER; see {@link #outranks(ClimateDataSource)}.
 */
public enum ClimateDataSource {

	MANUAL(3),
	IMPORT(2),
	OPENWEATHER(1);

	private final int rank;

	ClimateDataSource(int rank) {
		this.rank = rank;
	}

	/**
	 * @return true when data from this source may replace a row whose current source is {@code current}. A source never
	 *         outranks itself; whether an import overwrites an earlier import is decided by the import, not here.
	 */
	public boolean outranks(ClimateDataSource current) {
		return current == null || rank > current.rank;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
