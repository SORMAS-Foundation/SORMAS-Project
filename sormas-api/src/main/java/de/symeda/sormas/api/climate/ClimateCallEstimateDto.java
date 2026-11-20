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

import java.io.Serializable;

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * What a fetch or backfill would cost before it is started: the locations it covers, the days it asks OpenWeather for
 * and the calls that takes. Numbers of calls, never money (decision 37).
 */
@AuditedClass
public class ClimateCallEstimateDto implements Serializable {

	private static final long serialVersionUID = 1882590040346726133L;

	private final int locations;
	private final long days;
	private final long calls;

	public ClimateCallEstimateDto(int locations, long days, long calls) {
		this.locations = locations;
		this.days = days;
		this.calls = calls;
	}

	public int getLocations() {
		return locations;
	}

	/**
	 * @return the location days to fetch, summed over all locations; days already held are left out of a backfill
	 */
	public long getDays() {
		return days;
	}

	public long getCalls() {
		return calls;
	}
}
