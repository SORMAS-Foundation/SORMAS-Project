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

/**
 * Call counts for fetching climate data from OpenWeather One Call 4.0, shown before charged calls are made. SORMAS shows
 * calls only, never money: OpenWeather's price is not available through its API, and a stored price would go stale.
 */
public final class ClimateCosts {

	/** Days returned by one One Call 4.0 timeline/1day call. */
	public static final int OPENWEATHER_DAYS_PER_CALL = 10;

	private ClimateCosts() {
	}

	/**
	 * @return calls needed to fetch {@code days} consecutive days for one location
	 */
	public static long callsPerLocation(long days) {
		return days <= 0 ? 0 : (days + OPENWEATHER_DAYS_PER_CALL - 1) / OPENWEATHER_DAYS_PER_CALL;
	}

	/**
	 * @return calls needed to fetch {@code years} years of history for {@code locations} locations that hold no data yet
	 */
	public static long historyCalls(long locations, int years) {
		return locations * callsPerLocation(Math.round(years * 365.25));
	}
}
