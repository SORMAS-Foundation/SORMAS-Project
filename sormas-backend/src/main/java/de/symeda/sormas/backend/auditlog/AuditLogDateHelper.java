/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.auditlog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;

public final class AuditLogDateHelper {

	private AuditLogDateHelper() {
		// Hide Utility Class Constructor
	}

	/**
	 * From this year on the conversion via Time API works without the shift of 6:32 minutes or 2 days.
	 */
	private static final int CONSISTENT_YEARS_START = 1894;

	/**
	 * Pattern used to parse {@link LocalDate} to {@link Date}.
	 */
	private static final String LOCAL_DATE_STRING_PATTERN = "yyyy-MM-dd";

	/**
	 * Pattern used to parse {@link LocalDateTime} to {@link Date}.
	 */
	private static final String LOCAL_DATE_TIME_STRING_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	/**
	 * Transforms a {@link LocalDateTime} to a {@link Date}.
	 * 
	 * @param localDateTime
	 * @return <code>null</code> if <code>localDateTime == null</code>.
	 */
	public static Date from(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		final Date utilDate;
		if (localDateTime.getYear() >= CONSISTENT_YEARS_START) {
			Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
			utilDate = Date.from(instant);
		} else {

			utilDate = parse(localDateTime, LOCAL_DATE_TIME_STRING_PATTERN);
		}

		return utilDate;
	}

	/**
	 * Transforms a {@link LocalDate} to a {@link Date} at 00:00h.
	 * 
	 * @param localDate
	 * @return <code>null</code> if <code>localDate == null</code>.
	 */
	public static Date from(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}

		final Date utilDate;
		if (localDate.getYear() >= CONSISTENT_YEARS_START) {
			Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
			utilDate = Date.from(instant);
		} else {
			utilDate = parse(localDate, LOCAL_DATE_STRING_PATTERN);
		}

		return utilDate;
	}

	public static Date of(int year, Month month, int dayOfMonth) {
		LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
		return from(localDate);
	}

	private static Date parse(Temporal temporal, final String pattern) {
		try {
			String temporalFormatted = DateTimeFormatter.ofPattern(pattern).format(temporal);
			Date utilDateParsed = new SimpleDateFormat(pattern).parse(temporalFormatted);
			return utilDateParsed;
		} catch (ParseException e) {
			throw new DateTimeException(String.format("Unexpected error while trying to parse %s to Date", temporal.getClass().getSimpleName()), e);
		}
	}
}
