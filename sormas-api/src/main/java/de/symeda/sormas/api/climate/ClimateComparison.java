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
 * What a summary card compares the last 7 days with.
 */
public enum ClimateComparison {

	/** Fewer than 6 of the 7 days have data. */
	NOT_ENOUGH_DATA,
	/** The daily normal ranges of the same days. */
	NORMAL,
	/** The same days a year earlier, before normal ranges exist. */
	LAST_YEAR,
	/** Nothing to compare with yet: values only. */
	VALUES_ONLY;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
