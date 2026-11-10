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

package de.symeda.sormas.backend.climate;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.HumidityMeasure;

/**
 * The daily normal ranges of one location for every day of the year: days d−7 to d+7 of each reference calendar year.
 * Humidity has one set per measure.
 */
public class ClimateDailyNormals {

	/** A leap year, so that 29 February gets its own normal. */
	private static final int LEAP_YEAR = 2024;

	private final Map<MonthDay, ClimateRangeDto> rainfall = new HashMap<>();
	private final Map<MonthDay, ClimateRangeDto> temperature = new HashMap<>();
	private final Map<HumidityMeasure, Map<MonthDay, ClimateRangeDto>> humidity = new EnumMap<>(HumidityMeasure.class);

	public static ClimateDailyNormals compute(Map<LocalDate, ClimateDay> days, Collection<Integer> referenceYears, int minYears) {

		ClimateDailyNormals normals = new ClimateDailyNormals();
		for (LocalDate date = LocalDate.of(LEAP_YEAR, 1, 1); date.getYear() == LEAP_YEAR; date = date.plusDays(1)) {
			MonthDay day = MonthDay.from(date);
			put(normals.rainfall, day, pool(day, days, referenceYears, ClimateDay::getRainfall, minYears));
			put(normals.temperature, day, pool(day, days, referenceYears, ClimateDay::getTemperatureMean, minYears));
			for (HumidityMeasure measure : new HumidityMeasure[] {
				HumidityMeasure.DAILY_MEAN,
				HumidityMeasure.NOON_READING }) {
				put(
					normals.humidity.computeIfAbsent(measure, m -> new HashMap<>()),
					day,
					pool(day, days, referenceYears, row -> row.getHumidityMeasure() == measure ? row.getRelativeHumidity() : null, minYears));
			}
		}
		return normals;
	}

	private static ClimateRangeDto pool(
		MonthDay day,
		Map<LocalDate, ClimateDay> days,
		Collection<Integer> referenceYears,
		Function<ClimateDay, Double> value,
		int minYears) {

		return ClimateStatistics.poolDays(day, referenceYears, date -> {
			ClimateDay row = days.get(date);
			return row != null ? value.apply(row) : null;
		}).toRange(minYears);
	}

	private static void put(Map<MonthDay, ClimateRangeDto> map, MonthDay day, ClimateRangeDto range) {
		if (range != null) {
			map.put(day, range);
		}
	}

	public ClimateRangeDto getRainfall(LocalDate date) {
		return rainfall.get(MonthDay.from(date));
	}

	public ClimateRangeDto getTemperature(LocalDate date) {
		return temperature.get(MonthDay.from(date));
	}

	public ClimateRangeDto getHumidity(LocalDate date, HumidityMeasure measure) {
		Map<MonthDay, ClimateRangeDto> byDay = measure != null ? humidity.get(measure) : null;
		return byDay != null ? byDay.get(MonthDay.from(date)) : null;
	}

	/**
	 * @return the measure a day without humidity is drawn against: daily means when they have a normal, else noon readings
	 */
	public HumidityMeasure getHumidityMeasureForGap(LocalDate date) {
		if (getHumidity(date, HumidityMeasure.DAILY_MEAN) != null) {
			return HumidityMeasure.DAILY_MEAN;
		}
		return getHumidity(date, HumidityMeasure.NOON_READING) != null ? HumidityMeasure.NOON_READING : null;
	}

	/**
	 * @return true when the day has a normal range for at least one variable
	 */
	public boolean hasAny(LocalDate date) {
		return getRainfall(date) != null || getTemperature(date) != null || getHumidityMeasureForGap(date) != null;
	}
}
