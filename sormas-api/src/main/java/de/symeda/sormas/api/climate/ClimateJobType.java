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
 * What a climate job does. Fetch, backfill and top-up jobs call the provider; relink entries only record a district move.
 */
public enum ClimateJobType {

	/** The nightly fetch: yesterday and the catch-up days before it, for every location in use. */
	DAILY_FETCH,
	/** "Fetch now", started by a user for chosen districts and dates; writes like the nightly fetch. */
	FETCH,
	/** History for all districts, a region or a district, started by a user after confirming the calls. */
	BACKFILL,
	/** History for one new location, back to the earliest day the district locations hold. */
	TOP_UP,
	/** Record of a district that moved to another climate location with imported or manual rows copied. */
	RELINK;

	/**
	 * @return true for jobs that fill history: they write only days without a row, are charged, and run only after the
	 *         nightly fetch of the current UTC day
	 */
	public boolean isHistory() {
		return this == BACKFILL || this == TOP_UP;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
