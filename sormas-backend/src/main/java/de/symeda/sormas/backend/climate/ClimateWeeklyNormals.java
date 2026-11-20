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

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.utils.EpiWeek;

/**
 * The weekly normal ranges of one district or region, for every week number of the year. Humidity has one set per
 * measure, so a noon reading is only ever compared with noon-reading normals.
 */
public class ClimateWeeklyNormals {

	private final Map<Integer, ClimateRangeDto> rainfall = new HashMap<>();
	private final Map<Integer, ClimateRangeDto> temperature = new HashMap<>();
	private final Map<HumidityMeasure, Map<Integer, ClimateRangeDto>> humidity = new EnumMap<>(HumidityMeasure.class);

	/**
	 * Computes the normals from the weeks of the reference years.
	 *
	 * @param weeks
	 *            the week figures of the reference years; a week without figures is a gap
	 */
	public static ClimateWeeklyNormals compute(Map<EpiWeek, ClimateWeekFigures> weeks, Collection<Integer> referenceYears, int minYears) {

		ClimateWeeklyNormals normals = new ClimateWeeklyNormals();
		for (int week = 1; week <= 53; week++) {
			put(normals.rainfall, week, pool(week, weeks, referenceYears, ClimateWeekFigures::getRainfall, minYears));
			put(normals.temperature, week, pool(week, weeks, referenceYears, ClimateWeekFigures::getTemperatureMean, minYears));
			for (HumidityMeasure measure : new HumidityMeasure[] {
				HumidityMeasure.DAILY_MEAN,
				HumidityMeasure.NOON_READING }) {
				put(
					normals.humidity.computeIfAbsent(measure, m -> new HashMap<>()),
					week,
					pool(week, weeks, referenceYears, figures -> figures.getHumidityMeasure() == measure ? figures.getHumidity() : null, minYears));
			}
		}
		return normals;
	}

	private static ClimateRangeDto pool(
		int week,
		Map<EpiWeek, ClimateWeekFigures> weeks,
		Collection<Integer> referenceYears,
		Function<ClimateWeekFigures, Double> value,
		int minYears) {

		return ClimateStatistics.poolWeeks(week, referenceYears, epiWeek -> {
			ClimateWeekFigures figures = weeks.get(epiWeek);
			return figures != null ? value.apply(figures) : null;
		}).toRange(minYears);
	}

	private static void put(Map<Integer, ClimateRangeDto> map, int week, ClimateRangeDto range) {
		if (range != null) {
			map.put(week, range);
		}
	}

	public ClimateRangeDto getRainfall(int week) {
		return rainfall.get(week);
	}

	public ClimateRangeDto getTemperature(int week) {
		return temperature.get(week);
	}

	public ClimateRangeDto getHumidity(int week, HumidityMeasure measure) {
		Map<Integer, ClimateRangeDto> byWeek = humidity.get(measure);
		return byWeek != null ? byWeek.get(week) : null;
	}

	/**
	 * @return the measure whose normal a week without humidity is drawn against: daily means when they have a normal for
	 *         the week, as for the weekly value, else noon readings; null when neither has one
	 */
	public HumidityMeasure getHumidityMeasureForGap(int week) {
		if (getHumidity(week, HumidityMeasure.DAILY_MEAN) != null) {
			return HumidityMeasure.DAILY_MEAN;
		}
		return getHumidity(week, HumidityMeasure.NOON_READING) != null ? HumidityMeasure.NOON_READING : null;
	}
}
