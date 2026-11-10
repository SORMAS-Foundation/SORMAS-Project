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
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

/**
 * How much climate history a district's location holds, and which years come from which source, so a mix of imported
 * and OpenWeather years is visible.
 */
@AuditedClass
public class ClimateHistoryCoverageDto extends ClimateDistrictDto {

	private static final long serialVersionUID = 8251006236815870324L;

	public static final String I18N_PREFIX = "ClimateHistoryCoverage";

	public static final String HAS_POINT = "hasPoint";
	public static final String HISTORY_FROM = "historyFrom";
	public static final String DAYS_HELD = "daysHeld";
	public static final String COMPLETENESS = "completeness";
	public static final String STATUS = "status";
	public static final String SOURCES = "sources";

	private boolean hasPoint;
	private LocalDate historyFrom;
	private int daysHeld;
	private final Map<Integer, Map<ClimateDataSource, Integer>> daysByYearAndSource = new TreeMap<>();

	public ClimateHistoryCoverageDto(RegionReferenceDto region, DistrictReferenceDto district, boolean hasPoint) {
		super(region, district);
		this.hasPoint = hasPoint;
	}

	/**
	 * @return true when the district's location has coordinates, so OpenWeather can be fetched for it
	 */
	public boolean isHasPoint() {
		return hasPoint;
	}

	/**
	 * @return the earliest day held, from any source, or null when the location holds nothing
	 */
	public LocalDate getHistoryFrom() {
		return historyFrom;
	}

	public int getDaysHeld() {
		return daysHeld;
	}

	/**
	 * Counts {@code days} days held in {@code year} from {@code source}, and moves the history start back to
	 * {@code earliestDay} when it is earlier.
	 */
	public void addDays(int year, ClimateDataSource source, int days, LocalDate earliestDay) {
		daysByYearAndSource.computeIfAbsent(year, y -> new EnumMap<>(ClimateDataSource.class)).merge(source, days, Integer::sum);
		daysHeld += days;
		if (historyFrom == null || earliestDay.isBefore(historyFrom)) {
			historyFrom = earliestDay;
		}
	}

	/**
	 * @return the time from the first day held to {@code today}: how much history has built up
	 */
	public Period getHeldPeriod(LocalDate today) {
		return historyFrom == null || !historyFrom.isBefore(today) ? Period.ZERO : Period.between(historyFrom, today);
	}

	/**
	 * @return true when the history reaches back at least {@code minYears} years
	 */
	public boolean hasEnoughHistory(LocalDate today, int minYears) {
		return getHeldPeriod(today).getYears() >= minYears;
	}

	/**
	 * @return the share of days from the first day held to yesterday that have a value, from 0 to 1; null without history
	 */
	public Double getCompleteness(LocalDate today) {
		if (historyFrom == null || !historyFrom.isBefore(today)) {
			return null;
		}
		return Math.min(1.0, daysHeld / (double) ChronoUnit.DAYS.between(historyFrom, today));
	}

	/**
	 * @return the years held, oldest first, with consecutive years from the same single source merged into one span: for
	 *         example 2021–2023 Import, 2024 Import and OpenWeather, 2025–2026 OpenWeather
	 */
	public List<SourceSpan> getSourceSpans() {
		List<SourceSpan> spans = new ArrayList<>();
		for (Map.Entry<Integer, Map<ClimateDataSource, Integer>> year : daysByYearAndSource.entrySet()) {
			SourceSpan last = spans.isEmpty() ? null : spans.get(spans.size() - 1);
			if (last != null
				&& last.toYear == year.getKey() - 1
				&& last.isSingleSource()
				&& year.getValue().size() == 1
				&& last.daysBySource.keySet().equals(year.getValue().keySet())) {
				last.toYear = year.getKey();
				year.getValue().forEach((source, days) -> last.daysBySource.merge(source, days, Integer::sum));
			} else {
				spans.add(new SourceSpan(year.getKey(), year.getValue()));
			}
		}
		return spans;
	}

	/**
	 * Years held, from one source or several, with the days each source holds in them.
	 */
	public static class SourceSpan implements Serializable {

		private static final long serialVersionUID = -1254587000736409876L;

		private final int fromYear;
		private int toYear;
		private final Map<ClimateDataSource, Integer> daysBySource = new EnumMap<>(ClimateDataSource.class);

		SourceSpan(int year, Map<ClimateDataSource, Integer> daysBySource) {
			this.fromYear = year;
			this.toYear = year;
			this.daysBySource.putAll(daysBySource);
		}

		public int getFromYear() {
			return fromYear;
		}

		public int getToYear() {
			return toYear;
		}

		/**
		 * @return the days each source holds in these years, in source order (Manual, Import, OpenWeather)
		 */
		public Map<ClimateDataSource, Integer> getDaysBySource() {
			return Collections.unmodifiableMap(daysBySource);
		}

		public boolean isSingleSource() {
			return daysBySource.size() == 1;
		}
	}
}
