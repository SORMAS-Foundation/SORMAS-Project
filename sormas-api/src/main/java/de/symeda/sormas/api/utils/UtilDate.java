package de.symeda.sormas.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class to instantiate or convert from/to {@link Date}s with Java Time API introduced in Java 8.<br />
 * The naming and parameters of the methods are kept similar to java.time classes.
 * 
 * @author Stefan Kock
 */
public final class UtilDate {

	/**
	 * Starting with this year the conversion per java.time API works without deviations of 6:32 min / 2 days.
	 */
	private static final int CONSISTENT_YEARS_START = 1894;

	/**
	 * Parsing pattern from {@link LocalDate} to {@link Date}.
	 */
	private static final String LOCAL_DATE_STRING_PATTERN = "yyyy-MM-dd";

	/**
	 * Parsing pattern from {@link LocalDateTime} to {@link Date}.
	 */
	private static final String LOCAL_DATE_TIME_STRING_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	/**
	 * Parsing pattern from {@link YearMonth} to {@link Date}.
	 */
	private static final String YEAR_MONTH_STRING_PATTERN = "yyyy-MM";

	/**
	 * Year to which the {@link Date} is set when converting from {@link LocalTime}.
	 */
	private static final int LOCAL_TIME_BASE_YEAR = 1970;

	/**
	 * Nanoseconds per millisecond.
	 */
	static final int NANOS_PER_MILLISECOND = 1_000_000;

	private UtilDate() {
		// Hide Utility Class Constructor
	}

	/**
	 * @see LocalDateTime#now()
	 * @return Current date and time.
	 */
	public static Date now() {
		return from(LocalDateTime.now());
	}

	/**
	 * Converts a {@link LocalDate} to {@link Date} at 00:00h.
	 * 
	 * @return {@code null} if {@code localDate == null}.
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

	/**
	 * Converts a {@link LocalDateTime} to {@link Date}.
	 * 
	 * @return {@code null} if {@code localDateTime == null}.
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
	 * Converts a {@link LocalTime} to {@link Date} (date: 1970-01-01).
	 * 
	 * @return {@code null} if {@code localTime == null}.
	 */
	public static Date from(LocalTime localTime) {

		if (localTime == null) {
			return null;
		}

		LocalDateTime ldt = localTime.atDate(LocalDate.of(LOCAL_TIME_BASE_YEAR, 1, 1));
		return from(ldt);
	}

	/**
	 * Converts a {@link YearMonth} to {@link Date} at the first day of month at 00:00.
	 * 
	 * @return {@code null} if {@code yearMonth == null}.
	 */
	public static Date from(YearMonth yearMonth) {

		if (yearMonth == null) {
			return null;
		}

		final Date utilDate;
		if (yearMonth.getYear() >= CONSISTENT_YEARS_START) {
			Instant instant = yearMonth.atDay(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
			utilDate = Date.from(instant);
		} else {
			utilDate = parse(yearMonth, YEAR_MONTH_STRING_PATTERN);
		}

		return utilDate;
	}

	private static Date parse(Temporal temporal, final String pattern) {

		try {
			String temporalFormatted = DateTimeFormatter.ofPattern(pattern).format(temporal);
			Date utilDateParsed = new SimpleDateFormat(pattern).parse(temporalFormatted);
			return utilDateParsed;
		} catch (ParseException e) {
			// Should not happen ...
			throw new DateTimeException(String.format("Unexpected parsing problem from %s to Date", temporal.getClass().getSimpleName()), e);
		}
	}

	/**
	 * Creates a {@link Date} at given date to 00:00 h.
	 * 
	 * @param year
	 *            Complete year number (for example 2014, because 14 will be interpreted as 0014).
	 * @param month
	 *            The month.
	 * @param dayOfMonth
	 *            Valid day of given month.
	 * @return A {@link Date} at given date to 00:00 h.
	 */
	public static Date of(int year, Month month, int dayOfMonth) {

		LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
		return from(localDate);
	}

	/**
	 * Creates a {@link Date} at "today" to 00:00 h.
	 * 
	 * @return A {@link Date} at "today" to 00:00 h.
	 */
	public static Date today() {

		return from(LocalDate.now());
	}

	/**
	 * Creates a {@link Date} at "tomorrow" to 00:00 h.
	 * 
	 * @return A {@link Date} at "tomorrow" to 00:00 h.
	 */
	public static Date tomorrow() {

		return from(LocalDate.now().plusDays(1));
	}

	/**
	 * Creates a {@link Date} at "yesterday" to 00:00 h.
	 * 
	 * @return A {@link Date} at "yesterday" to 00:00 h.
	 */
	public static Date yesterday() {

		return from(LocalDate.now().minusDays(1));
	}

	/**
	 * Converts a {@link Date} to {@link LocalDate}.
	 * 
	 * @return {@code null}, if {@code utilDate == null}.
	 */
	public static LocalDate toLocalDate(Date utilDate) {

		return toLocalDate(utilDate, ZoneId.systemDefault());
	}

	/**
	 * Converts a {@link Date} to {@link LocalDate}.
	 * 
	 * @return {@code null}, if {@code utilDate == null}.
	 */
	public static LocalDate toLocalDate(Date utilDate, ZoneId zone) {

		if (utilDate == null) {
			return null;
		}

		final LocalDate localDate;
		if (utilDate.before(of(CONSISTENT_YEARS_START, Month.JANUARY, 1))) {
			localDate = LocalDate.from(parseToTemporal(utilDate, LOCAL_DATE_STRING_PATTERN));
		} else {
			localDate = instantOf(utilDate, zone).toLocalDate();
		}
		return localDate;
	}

	/**
	 * Converts a {@link Date} to {@link LocalDateTime}.
	 * 
	 * @return {@code null}, if {@code utilDate == null}.
	 */
	public static LocalDateTime toLocalDateTime(Date utilDate) {

		return toLocalDateTime(utilDate, ZoneId.systemDefault());
	}

	/**
	 * Converts a {@link Date} to {@link LocalDateTime}.
	 * 
	 * @return {@code null}, if {@code utilDate == null}.
	 */
	public static LocalDateTime toLocalDateTime(Date utilDate, ZoneId zone) {

		if (utilDate == null) {
			return null;
		}

		final LocalDateTime localDateTime;
		if (utilDate.before(of(CONSISTENT_YEARS_START, Month.JANUARY, 1))) {
			localDateTime = LocalDateTime.from(parseToTemporal(utilDate, LOCAL_DATE_TIME_STRING_PATTERN));
		} else {
			localDateTime = instantOf(utilDate, zone).toLocalDateTime();
		}
		return localDateTime;
	}

	/**
	 * Converts a {@link Date} to {@link LocalTime}.
	 * 
	 * @return {@code null}, if {@code utilDate == null}.
	 */
	public static LocalTime toLocalTime(Date utilDate) {

		if (utilDate == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(utilDate);

		final int hour = cal.get(Calendar.HOUR_OF_DAY);
		final int minute = cal.get(Calendar.MINUTE);
		final int second = cal.get(Calendar.SECOND);

		final int nanoSecond;
		if (utilDate instanceof java.sql.Timestamp) {
			// java.sql.Timestamp has Nanoseconds as presicion
			nanoSecond = ((java.sql.Timestamp) utilDate).getNanos();
		} else {
			// Convert milliseconds to nanoseconds
			nanoSecond = cal.get(Calendar.MILLISECOND) * NANOS_PER_MILLISECOND;
		}

		LocalTime localTime = LocalTime.of(hour, minute, second, nanoSecond);
		return localTime;
	}

	/**
	 * Converts a {@link Date} to {@link YearMonth}.
	 * 
	 * @return {@code null}, if {@code utilDate == null}.
	 */
	public static YearMonth toYearMonth(Date utilDate) {

		return toYearMonth(utilDate, ZoneId.systemDefault());
	}

	/**
	 * Converts a {@link Date} to {@link YearMonth}.
	 * 
	 * @return {@code null}, if {@code utilDate == null}.
	 */
	public static YearMonth toYearMonth(Date utilDate, ZoneId zone) {

		if (utilDate == null) {
			return null;
		}

		final YearMonth yearMonth;
		if (utilDate.before(of(CONSISTENT_YEARS_START, Month.JANUARY, 1))) {
			yearMonth = YearMonth.from(parseToTemporal(utilDate, YEAR_MONTH_STRING_PATTERN));
		} else {
			yearMonth = YearMonth.from(instantOf(utilDate, zone));
		}
		return yearMonth;
	}

	/**
	 * Creates {@link ZonedDateTime} from the given {@link Date} and {@link ZoneId}.
	 */
	private static ZonedDateTime instantOf(Date utilDate, ZoneId zone) {

		return Instant.ofEpochMilli(utilDate.getTime()).atZone(zone);
	}

	/**
	 * Create a {@link TemporalAccessor} from a {@link Date} with the {@code formatPattern}.
	 * 
	 * @param utilDate
	 *            The {@link Date} to convert (not null).
	 * @param formatPattern
	 *            The {@code formatPattern} is used to parse the {@link Date} into a {@link TemporalAccessor}.
	 * @return The time information parsed from {@code utilDate} .
	 * @see SimpleDateFormat#format(Date)
	 * @see DateTimeFormatter#ofPattern(String)
	 */
	private static TemporalAccessor parseToTemporal(Date utilDate, String formatPattern) {

		String utilDateAsString = new SimpleDateFormat(formatPattern).format(utilDate);
		TemporalAccessor temporal = DateTimeFormatter.ofPattern(formatPattern).parse(utilDateAsString);
		return temporal;
	}
}
