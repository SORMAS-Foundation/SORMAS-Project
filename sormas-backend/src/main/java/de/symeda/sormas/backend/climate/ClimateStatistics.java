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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.utils.EpiWeek;

/**
 * The arithmetic behind the climate figures, with no database access: percentiles, SORMAS epi weeks, the weekly roll-up
 * and the windows of normal ranges. Normal windows wrap inside each reference year (decision 66).
 */
public final class ClimateStatistics {

	/** Days a week needs for a value; a missing day is not zero (decision 9). */
	public static final int MIN_DAYS_PER_WEEK = 6;
	/** Days on either side of a day in its daily normal window (15 days in all). */
	public static final int DAILY_WINDOW_HALF_DAYS = 7;
	/** Days of its 15-day window a reference year needs to count for a daily normal. */
	public static final int MIN_DAYS_PER_DAILY_WINDOW = 10;

	private ClimateStatistics() {
	}

	/**
	 * @return the percentile with linear interpolation between the closest ranks, as PostgreSQL's
	 *         {@code percentile_cont}; null for no values
	 */
	public static Double percentile(Collection<Double> values, double fraction) {

		if (values.isEmpty()) {
			return null;
		}
		double[] sorted = values.stream().mapToDouble(Double::doubleValue).sorted().toArray();
		double position = fraction * (sorted.length - 1);
		int lower = (int) Math.floor(position);
		int upper = (int) Math.ceil(position);
		return sorted[lower] + (position - lower) * (sorted[upper] - sorted[lower]);
	}

	/**
	 * @return p10, p50 and p90 of the values, or null for no values
	 */
	public static ClimateRangeDto range(Collection<Double> values) {
		return values.isEmpty() ? null : new ClimateRangeDto(percentile(values, 0.1), percentile(values, 0.5), percentile(values, 0.9));
	}

	/**
	 * @return the Monday that starts epi week 1 of the year: the week that holds 1 January, even when it begins in December
	 */
	public static LocalDate firstDayOfEpiYear(int year) {
		return LocalDate.of(year, 1, 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}

	/**
	 * @return 52 or 53
	 */
	public static int weeksInEpiYear(int year) {
		return (int) (ChronoUnit.DAYS.between(firstDayOfEpiYear(year), firstDayOfEpiYear(year + 1)) / 7);
	}

	/**
	 * @return the epi week of the day, as {@link de.symeda.sormas.api.utils.DateHelper#getEpiWeek(java.util.Date)} gives it
	 */
	public static EpiWeek epiWeekOf(LocalDate day) {

		int year = day.getYear();
		if (!day.isBefore(firstDayOfEpiYear(year + 1))) {
			year++;
		} else if (day.isBefore(firstDayOfEpiYear(year))) {
			year--;
		}
		return new EpiWeek(year, (int) (ChronoUnit.DAYS.between(firstDayOfEpiYear(year), day) / 7) + 1);
	}

	/**
	 * @return the Monday of the epi week
	 */
	public static LocalDate firstDay(EpiWeek week) {
		return firstDayOfEpiYear(week.getYear()).plusWeeks(week.getWeek() - 1L);
	}

	/**
	 * @return the epi weeks from {@code from} to {@code to}, both included
	 */
	public static List<EpiWeek> weeksBetween(EpiWeek from, EpiWeek to) {

		List<EpiWeek> weeks = new ArrayList<>();
		LocalDate last = firstDay(to);
		for (LocalDate monday = firstDay(from); !monday.isAfter(last); monday = monday.plusWeeks(1)) {
			weeks.add(epiWeekOf(monday));
		}
		return weeks;
	}

	/**
	 * @return the same epi week a year earlier, or null for week 53 when the year before has no week 53 (decision 69)
	 */
	public static EpiWeek sameWeekLastYear(EpiWeek week) {
		int year = week.getYear() - 1;
		return week.getWeek() <= weeksInEpiYear(year) ? new EpiWeek(year, week.getWeek()) : null;
	}

	/**
	 * The weeks of epi year {@code year} that a normal for week {@code week} takes: the week and its two neighbours,
	 * wrapping inside the year (week 1's neighbours are week 2 and the year's last week). Week 53 is pooled with weeks 52
	 * and 1, so a year without a week 53 contributes those two.
	 */
	public static List<EpiWeek> weekWindow(int year, int week) {

		int weeks = weeksInEpiYear(year);
		List<Integer> numbers;
		if (week > weeks) {
			numbers = Arrays.asList(52, 1);
		} else {
			numbers = Arrays.asList(week == 1 ? weeks : week - 1, week, week == weeks ? 1 : week + 1);
		}
		List<EpiWeek> window = new ArrayList<>();
		numbers.forEach(number -> window.add(new EpiWeek(year, number)));
		return window;
	}

	/**
	 * @return the day in {@code year}, with 29 February moved to 28 February in years without it
	 */
	public static LocalDate sameDayIn(int year, MonthDay day) {
		return day.isValidYear(year) ? day.atYear(year) : MonthDay.of(2, 28).atYear(year);
	}

	/**
	 * The 15 days of calendar year {@code year} that a daily normal for {@code day} takes: 7 on either side, wrapping inside
	 * the year (1 January takes 25 to 31 December of the same year).
	 */
	public static List<LocalDate> dayWindow(int year, MonthDay day) {

		LocalDate centre = sameDayIn(year, day);
		List<LocalDate> window = new ArrayList<>();
		for (int offset = -DAILY_WINDOW_HALF_DAYS; offset <= DAILY_WINDOW_HALF_DAYS; offset++) {
			LocalDate date = centre.plusDays(offset);
			window.add(date.getYear() == year ? date : date.withYear(year));
		}
		return window;
	}

	/**
	 * Pools the values of a week's window in each reference year. A year counts when at least one of its window weeks has
	 * a value.
	 *
	 * @param valueOfWeek
	 *            the value of a week, or null for a gap
	 */
	public static PooledValues poolWeeks(int week, Collection<Integer> referenceYears, Function<EpiWeek, Double> valueOfWeek) {

		PooledValues pooled = new PooledValues();
		for (int year : referenceYears) {
			boolean counted = false;
			for (EpiWeek windowWeek : weekWindow(year, week)) {
				Double value = valueOfWeek.apply(windowWeek);
				if (value != null) {
					pooled.values.add(value);
					counted = true;
				}
			}
			if (counted) {
				pooled.years++;
			}
		}
		return pooled;
	}

	/**
	 * Pools the values of a day's window in each reference year. A year counts when at least
	 * {@link #MIN_DAYS_PER_DAILY_WINDOW} of its 15 days have a value; the values of a year that does not count are left out.
	 */
	public static PooledValues poolDays(MonthDay day, Collection<Integer> referenceYears, Function<LocalDate, Double> valueOfDay) {

		PooledValues pooled = new PooledValues();
		for (int year : referenceYears) {
			List<Double> yearValues = new ArrayList<>();
			for (LocalDate date : dayWindow(year, day)) {
				Double value = valueOfDay.apply(date);
				if (value != null) {
					yearValues.add(value);
				}
			}
			if (yearValues.size() >= MIN_DAYS_PER_DAILY_WINDOW) {
				pooled.values.addAll(yearValues);
				pooled.years++;
			}
		}
		return pooled;
	}

	/**
	 * Rolls a location's daily rows up into one epi week: rainfall is the sum, temperatures and humidity the mean of the
	 * daily values, each only when at least {@link #MIN_DAYS_PER_WEEK} days have it. Humidity is taken from daily means
	 * when enough days have them, else from noon readings, never from a mix.
	 */
	public static ClimateWeekFigures rollUp(EpiWeek week, Map<LocalDate, ClimateDay> days) {

		List<Double> rainfall = new ArrayList<>();
		List<Double> mean = new ArrayList<>();
		List<Double> min = new ArrayList<>();
		List<Double> max = new ArrayList<>();
		List<Double> dailyMeanHumidity = new ArrayList<>();
		List<Double> noonHumidity = new ArrayList<>();

		LocalDate monday = firstDay(week);
		for (int i = 0; i < 7; i++) {
			ClimateDay day = days.get(monday.plusDays(i));
			if (day == null) {
				continue;
			}
			addIfPresent(rainfall, day.getRainfall());
			addIfPresent(mean, day.getTemperatureMean());
			addIfPresent(min, day.getTemperatureMin());
			addIfPresent(max, day.getTemperatureMax());
			if (day.getHumidityMeasure() == HumidityMeasure.DAILY_MEAN) {
				addIfPresent(dailyMeanHumidity, day.getRelativeHumidity());
			} else if (day.getHumidityMeasure() == HumidityMeasure.NOON_READING) {
				addIfPresent(noonHumidity, day.getRelativeHumidity());
			}
		}

		HumidityMeasure humidityMeasure = null;
		List<Double> humidity = Collections.emptyList();
		if (dailyMeanHumidity.size() >= MIN_DAYS_PER_WEEK) {
			humidityMeasure = HumidityMeasure.DAILY_MEAN;
			humidity = dailyMeanHumidity;
		} else if (noonHumidity.size() >= MIN_DAYS_PER_WEEK) {
			humidityMeasure = HumidityMeasure.NOON_READING;
			humidity = noonHumidity;
		}

		return new ClimateWeekFigures(
			week,
			rainfall.size() >= MIN_DAYS_PER_WEEK ? sum(rainfall) : null,
			meanOfWeek(mean),
			meanOfWeek(min),
			meanOfWeek(max),
			humidityMeasure != null ? mean(humidity) : null,
			humidityMeasure,
			rainfall.size(),
			mean.size(),
			humidityMeasure != null ? humidity.size() : Math.max(dailyMeanHumidity.size(), noonHumidity.size()));
	}

	/**
	 * Combines the weeks of a region's districts into the region's week (decision 65): each value is the mean over the
	 * districts with a complete week, and a gap when fewer than half of the {@code districtCount} districts have one.
	 * Humidity uses daily means when at least half the districts have them, else noon readings by the same test. The day
	 * counts of the result are numbers of districts.
	 *
	 * @param districtWeeks
	 *            the week of each district that holds data; districts without data are left out but counted in
	 *            {@code districtCount}
	 */
	public static ClimateWeekFigures combine(EpiWeek week, Collection<ClimateWeekFigures> districtWeeks, int districtCount) {

		List<Double> rainfall = new ArrayList<>();
		List<Double> mean = new ArrayList<>();
		List<Double> min = new ArrayList<>();
		List<Double> max = new ArrayList<>();
		List<Double> dailyMeanHumidity = new ArrayList<>();
		List<Double> noonHumidity = new ArrayList<>();
		for (ClimateWeekFigures district : districtWeeks) {
			addIfPresent(rainfall, district.getRainfall());
			addIfPresent(mean, district.getTemperatureMean());
			addIfPresent(min, district.getTemperatureMin());
			addIfPresent(max, district.getTemperatureMax());
			if (district.getHumidityMeasure() == HumidityMeasure.DAILY_MEAN) {
				dailyMeanHumidity.add(district.getHumidity());
			} else if (district.getHumidityMeasure() == HumidityMeasure.NOON_READING) {
				noonHumidity.add(district.getHumidity());
			}
		}

		HumidityMeasure humidityMeasure = null;
		List<Double> humidity = Collections.emptyList();
		if (isEnoughDistricts(dailyMeanHumidity, districtCount)) {
			humidityMeasure = HumidityMeasure.DAILY_MEAN;
			humidity = dailyMeanHumidity;
		} else if (isEnoughDistricts(noonHumidity, districtCount)) {
			humidityMeasure = HumidityMeasure.NOON_READING;
			humidity = noonHumidity;
		}

		return new ClimateWeekFigures(
			week,
			isEnoughDistricts(rainfall, districtCount) ? mean(rainfall) : null,
			isEnoughDistricts(mean, districtCount) ? mean(mean) : null,
			isEnoughDistricts(min, districtCount) ? mean(min) : null,
			isEnoughDistricts(max, districtCount) ? mean(max) : null,
			humidityMeasure != null ? mean(humidity) : null,
			humidityMeasure,
			rainfall.size(),
			mean.size(),
			humidityMeasure != null ? humidity.size() : Math.max(dailyMeanHumidity.size(), noonHumidity.size()));
	}

	private static boolean isEnoughDistricts(List<Double> values, int districtCount) {
		return !values.isEmpty() && values.size() * 2 >= districtCount;
	}

	public static double sum(Collection<Double> values) {
		return values.stream().mapToDouble(Double::doubleValue).sum();
	}

	/**
	 * @return the mean, or null for no values
	 */
	public static Double mean(Collection<Double> values) {
		return values.isEmpty() ? null : sum(values) / values.size();
	}

	private static Double meanOfWeek(List<Double> values) {
		return values.size() >= MIN_DAYS_PER_WEEK ? mean(values) : null;
	}

	private static void addIfPresent(List<Double> values, Double value) {
		if (value != null) {
			values.add(value);
		}
	}

	/**
	 * Values pooled from the reference years for one normal, and how many reference years contributed.
	 */
	public static class PooledValues {

		private final List<Double> values = new ArrayList<>();
		private int years;

		public List<Double> getValues() {
			return values;
		}

		public int getYears() {
			return years;
		}

		/**
		 * @return the normal range, or null when fewer than {@code minYears} reference years contributed
		 */
		public ClimateRangeDto toRange(int minYears) {
			return years >= minYears ? range(values) : null;
		}
	}
}
