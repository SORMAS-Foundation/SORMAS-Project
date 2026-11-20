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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Day arithmetic for climate jobs. Observation days are calendar days, stored as {@link Date} at the start of the day in
 * the server's time zone.
 */
public final class ClimateCalendar {

	private ClimateCalendar() {
	}

	public static LocalDate toLocalDate(Date date) {
		if (date == null) {
			return null;
		}
		if (date instanceof java.sql.Date) {
			return ((java.sql.Date) date).toLocalDate();
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static Date toDate(LocalDate day) {
		return day == null ? null : Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * Splits a period into the fewest calls that cover every day without data: each call starts at the next day without a
	 * row and covers {@code daysPerCall} days from there. Days already held are not paid for again.
	 *
	 * @return the first day of each call, in order
	 */
	public static List<LocalDate> windowStarts(LocalDate from, LocalDate to, Set<LocalDate> heldDays, int daysPerCall) {

		List<LocalDate> starts = new ArrayList<>();
		LocalDate day = from;
		while (!day.isAfter(to)) {
			if (heldDays.contains(day)) {
				day = day.plusDays(1);
			} else {
				starts.add(day);
				day = day.plusDays(daysPerCall);
			}
		}
		return starts;
	}
}
