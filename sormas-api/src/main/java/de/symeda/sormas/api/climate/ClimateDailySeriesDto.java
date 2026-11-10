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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;

/**
 * The daily climate of an environment for the Climate data tab: its own location's, or its district's when it has no
 * usable GPS, with daily normals and the summary cards for the last 7 days.
 */
@AuditedClass
public class ClimateDailySeriesDto implements Serializable {

	private static final long serialVersionUID = -7360478815021513722L;

	private final EnvironmentReferenceDto environment;
	private final DistrictReferenceDto district;
	private final boolean districtFallback;
	private final boolean districtNormals;
	private final LocalDate historyFrom;
	private final int minHistoryYears;
	private final int firstReferenceYear;
	private final int lastReferenceYear;
	private final List<ClimateDayDto> days = new ArrayList<>();
	private final List<ClimateSummaryCardDto> cards = new ArrayList<>();

	public ClimateDailySeriesDto(
		EnvironmentReferenceDto environment,
		DistrictReferenceDto district,
		boolean districtFallback,
		boolean districtNormals,
		LocalDate historyFrom,
		int minHistoryYears,
		int firstReferenceYear,
		int lastReferenceYear) {

		this.environment = environment;
		this.district = district;
		this.districtFallback = districtFallback;
		this.districtNormals = districtNormals;
		this.historyFrom = historyFrom;
		this.minHistoryYears = minHistoryYears;
		this.firstReferenceYear = firstReferenceYear;
		this.lastReferenceYear = lastReferenceYear;
	}

	public EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	/**
	 * @return true when the environment has no usable GPS, so the values and normals are its district's
	 */
	public boolean isDistrictFallback() {
		return districtFallback;
	}

	/**
	 * @return true when the normal ranges are the district's ("Normal range: Bagaces district"), because they cover more
	 *         of the days shown than the environment's own
	 */
	public boolean isDistrictNormals() {
		return districtNormals;
	}

	/**
	 * @return the earliest day held by the location the values come from, or null without data
	 */
	public LocalDate getHistoryFrom() {
		return historyFrom;
	}

	public int getMinHistoryYears() {
		return minHistoryYears;
	}

	/**
	 * @return the first of the calendar years the daily normals come from
	 */
	public int getFirstReferenceYear() {
		return firstReferenceYear;
	}

	public int getLastReferenceYear() {
		return lastReferenceYear;
	}

	/**
	 * @return every day of the period, oldest first, days without data included
	 */
	public List<ClimateDayDto> getDays() {
		return days;
	}

	/**
	 * @return rainfall, temperature and humidity, in that order
	 */
	public List<ClimateSummaryCardDto> getCards() {
		return cards;
	}
}
