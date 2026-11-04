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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;

class ClimateStatisticsTest {

	private static final double DELTA = 1e-9;

	@Test
	void testPercentilesMatchPercentileCont() {

		// PostgreSQL: percentile_cont(0.1 / 0.5 / 0.9) WITHIN GROUP (ORDER BY v) over 1..10 gives 1.9, 5.5 and 9.1
		ClimateRangeDto range = ClimateStatistics.range(Arrays.asList(10.0, 1.0, 9.0, 2.0, 8.0, 3.0, 7.0, 4.0, 6.0, 5.0));
		assertEquals(1.9, range.getP10(), DELTA);
		assertEquals(5.5, range.getP50(), DELTA);
		assertEquals(9.1, range.getP90(), DELTA);

		assertEquals(4.0, ClimateStatistics.percentile(Collections.singletonList(4.0), 0.9), DELTA);
		assertEquals(2.4, ClimateStatistics.percentile(Arrays.asList(0.0, 3.0), 0.8), DELTA);
		assertNull(ClimateStatistics.range(Collections.emptyList()));
		assertEquals(55.0, range.scaled(10).getP50(), DELTA);
	}

	@Test
	void testEpiWeeksMatchDateHelper() {

		for (LocalDate day = LocalDate.of(2015, 12, 1); day.isBefore(LocalDate.of(2031, 1, 10)); day = day.plusDays(1)) {
			EpiWeek expected = DateHelper.getEpiWeek(ClimateCalendar.toDate(day));
			assertEquals(expected, ClimateStatistics.epiWeekOf(day), day.toString());
		}
		// 1 January 2026 is a Thursday, so epi week 1 of 2026 starts on Monday 29 December 2025
		assertEquals(LocalDate.of(2025, 12, 29), ClimateStatistics.firstDay(new EpiWeek(2026, 1)));
		assertEquals(53, ClimateStatistics.weeksInEpiYear(2023), "2023 runs from 26 Dec 2022 to 31 Dec 2023");
		assertEquals(52, ClimateStatistics.weeksInEpiYear(2024));
		assertEquals(
			Arrays.asList(new EpiWeek(2023, 52), new EpiWeek(2023, 53), new EpiWeek(2024, 1)),
			ClimateStatistics.weeksBetween(new EpiWeek(2023, 52), new EpiWeek(2024, 1)));
	}

	@Test
	void testWeekWindowsWrapInsideTheYear() {

		assertEquals(weeks(2024, 52, 1, 2), ClimateStatistics.weekWindow(2024, 1), "week 1 wraps to the same year's last week");
		assertEquals(weeks(2023, 53, 1, 2), ClimateStatistics.weekWindow(2023, 1));
		assertEquals(weeks(2024, 51, 52, 1), ClimateStatistics.weekWindow(2024, 52));
		assertEquals(weeks(2023, 51, 52, 53), ClimateStatistics.weekWindow(2023, 52));
		assertEquals(weeks(2023, 52, 53, 1), ClimateStatistics.weekWindow(2023, 53));
		assertEquals(weeks(2024, 52, 1), ClimateStatistics.weekWindow(2024, 53), "a year without week 53 contributes weeks 52 and 1");
		assertEquals(weeks(2024, 19, 20, 21), ClimateStatistics.weekWindow(2024, 20));

		assertEquals(new EpiWeek(2023, 53), ClimateStatistics.sameWeekLastYear(new EpiWeek(2024, 53)));
		assertNull(ClimateStatistics.sameWeekLastYear(new EpiWeek(2023, 53)), "2022 has no week 53");
		assertEquals(new EpiWeek(2024, 10), ClimateStatistics.sameWeekLastYear(new EpiWeek(2025, 10)));
	}

	@Test
	void testDayWindowsWrapInsideTheYearAndMoveLeapDays() {

		List<LocalDate> window = ClimateStatistics.dayWindow(2023, MonthDay.of(1, 3));
		assertEquals(15, window.size());
		assertEquals(LocalDate.of(2023, 12, 27), window.get(0), "3 January takes 27 to 31 December of the same year");
		assertEquals(LocalDate.of(2023, 1, 10), window.get(14));
		assertEquals(LocalDate.of(2023, 2, 28), ClimateStatistics.sameDayIn(2023, MonthDay.of(2, 29)));
		assertEquals(LocalDate.of(2024, 2, 29), ClimateStatistics.sameDayIn(2024, MonthDay.of(2, 29)));
		assertEquals(LocalDate.of(2023, 2, 21), ClimateStatistics.dayWindow(2023, MonthDay.of(2, 29)).get(0));
	}

	@Test
	void testPoolingCountsYearsWithEnoughData() {

		Map<EpiWeek, Double> weekly = new HashMap<>();
		weekly.put(new EpiWeek(2021, 10), 1.0);
		weekly.put(new EpiWeek(2022, 9), 2.0);
		weekly.put(new EpiWeek(2022, 11), 3.0);
		weekly.put(new EpiWeek(2023, 12), 99.0);
		ClimateStatistics.PooledValues pooled = ClimateStatistics.poolWeeks(10, Arrays.asList(2021, 2022, 2023), weekly::get);
		assertEquals(2, pooled.getYears(), "2023 has nothing in weeks 9 to 11");
		assertEquals(Arrays.asList(1.0, 2.0, 3.0), pooled.getValues());
		assertNull(pooled.toRange(3));
		assertEquals(2.0, pooled.toRange(2).getP50(), DELTA);

		Map<LocalDate, Double> daily = new HashMap<>();
		for (LocalDate day = LocalDate.of(2022, 6, 8); !day.isAfter(LocalDate.of(2022, 6, 22)); day = day.plusDays(1)) {
			daily.put(day, 1.0);
		}
		for (LocalDate day = LocalDate.of(2023, 6, 8); day.isBefore(LocalDate.of(2023, 6, 17)); day = day.plusDays(1)) {
			daily.put(day, 5.0);
		}
		ClimateStatistics.PooledValues pooledDays = ClimateStatistics.poolDays(MonthDay.of(6, 15), Arrays.asList(2022, 2023), daily::get);
		assertEquals(1, pooledDays.getYears(), "9 of 15 days in 2023 is too few");
		assertEquals(15, pooledDays.getValues().size(), "values of a year that does not count are left out");
	}

	@Test
	void testRollUpNeedsSixOfSevenDaysPerVariable() {

		EpiWeek week = new EpiWeek(2024, 20);
		LocalDate monday = ClimateStatistics.firstDay(week);
		Map<LocalDate, ClimateDay> days = new HashMap<>();
		for (int i = 0; i < 6; i++) {
			days.put(
				monday.plusDays(i),
				new ClimateDay(monday.plusDays(i), 20.0, 30.0, i < 5 ? 25.0 + i : null, 2.0, 80.0, HumidityMeasure.NOON_READING));
		}
		days.put(monday.plusDays(6), new ClimateDay(monday.plusDays(6), 20.0, 30.0, 25.0, 0.0, 60.0, HumidityMeasure.DAILY_MEAN));

		ClimateWeekFigures figures = ClimateStatistics.rollUp(week, days);
		assertEquals(12.0, figures.getRainfall(), DELTA, "a dry day counts as 0, a missing day not at all");
		assertEquals(7, figures.getRainfallDays());
		assertEquals(160.0 / 6, figures.getTemperatureMean(), DELTA, "the day without a mean is left out");
		assertEquals(6, figures.getTemperatureDays());
		assertEquals(20.0, figures.getTemperatureMin(), DELTA);
		assertEquals(30.0, figures.getTemperatureMax(), DELTA);
		assertEquals(80.0, figures.getHumidity(), DELTA, "six noon readings; the one daily mean is not mixed in");
		assertEquals(HumidityMeasure.NOON_READING, figures.getHumidityMeasure());

		days.remove(monday);
		days.remove(monday.plusDays(1));
		ClimateWeekFigures gap = ClimateStatistics.rollUp(week, days);
		assertNull(gap.getRainfall());
		assertNull(gap.getHumidity());
		assertNull(gap.getHumidityMeasure());
		assertEquals(4, gap.getHumidityDays());
		assertEquals(Arrays.asList(5, 4), Arrays.asList(gap.getRainfallDays(), gap.getHumidityDays()));
	}

	private static List<EpiWeek> weeks(int year, Integer... numbers) {
		return Arrays.stream(numbers).map(number -> new EpiWeek(year, number)).collect(Collectors.toList());
	}
}
