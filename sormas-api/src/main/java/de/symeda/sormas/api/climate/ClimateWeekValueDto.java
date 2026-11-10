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
 * One climate variable in one epi week: its value, how much data it rests on, the normal range for that week and the
 * comparison with the same week last year.
 */
@AuditedClass
public class ClimateWeekValueDto implements Serializable {

	private static final long serialVersionUID = -6262317001954870317L;

	private final Double value;
	private final Integer days;
	private final Integer districts;
	private final ClimateRangeDto normal;
	private final Double lastYear;
	private final boolean unusual;

	public ClimateWeekValueDto(Double value, Integer days, Integer districts, ClimateRangeDto normal, Double lastYear, boolean unusual) {
		this.value = value;
		this.days = days;
		this.districts = districts;
		this.normal = normal;
		this.lastYear = lastYear;
		this.unusual = unusual;
	}

	/**
	 * @return the week's value, or null for a gap (fewer than 6 of 7 days; for a region, fewer than half of its districts)
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @return the days of the week with data, for a district; null for a region
	 */
	public Integer getDays() {
		return days;
	}

	/**
	 * @return the districts with a complete week, for a region; null for a district
	 */
	public Integer getDistricts() {
		return districts;
	}

	/**
	 * @return the normal range for the week, or null when the reference years hold too few complete weeks
	 */
	public ClimateRangeDto getNormal() {
		return normal;
	}

	/**
	 * @return the value of the same week a year earlier when both weeks are complete, else null
	 */
	public Double getLastYear() {
		return lastYear;
	}

	/**
	 * @return true when the week is above the normal range (rainfall: and above the minimum weekly rainfall)
	 */
	public boolean isUnusual() {
		return unusual;
	}
}
