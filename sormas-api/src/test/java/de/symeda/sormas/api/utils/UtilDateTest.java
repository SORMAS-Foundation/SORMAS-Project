package de.symeda.sormas.api.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

/**
 * @see UtilDate
 * @author Stefan Kock
 */
public class UtilDateTest {

	@Test
	public void testNow() {

		long timeMillis = System.currentTimeMillis();
		Date result = UtilDate.now();

		// Ignore deviation up to 10ms
		BigDecimal error = new BigDecimal(10);
		assertThat(new BigDecimal(result.getTime()), closeTo(new BigDecimal(timeMillis), error));
	}

	@Test
	public void testFromLocalDate() {

		Date result;
		Date expected;

		result = UtilDate.from((LocalDate) null);
		expected = null;
		assertEquals(expected, result);

		// 4-digit current year
		result = UtilDate.from(LocalDate.of(2015, Month.APRIL, 1));
		expected = new GregorianCalendar(2015, Calendar.APRIL, 1).getTime();
		assertDatesEquals(expected, result);

		// 2-digit year
		result = UtilDate.from(LocalDate.of(15, Month.MARCH, 23));
		expected = new GregorianCalendar(15, Calendar.MARCH, 23).getTime();
		assertDatesEquals(expected, result);

		// 3-digit year
		result = UtilDate.from(LocalDate.of(933, Month.DECEMBER, 25));
		expected = new GregorianCalendar(933, Calendar.DECEMBER, 25).getTime();
		assertDatesEquals(expected, result);

		// All time artifacts are before 1894-01-01
		result = UtilDate.from(LocalDate.of(1893, Month.DECEMBER, 31));
		expected = new GregorianCalendar(1893, Calendar.DECEMBER, 31).getTime();
		assertDatesEquals(expected, result);
		result = UtilDate.of(1894, Month.JANUARY, 1);
		expected = new GregorianCalendar(1894, Calendar.JANUARY, 1).getTime();
		assertDatesEquals(expected, result);

		// 4-digit year in the past
		result = UtilDate.from(LocalDate.of(1900, Month.DECEMBER, 6));
		expected = new GregorianCalendar(1900, Calendar.DECEMBER, 6).getTime();
		assertDatesEquals(expected, result);

		// 4-digit year far in the future
		result = UtilDate.from(LocalDate.of(3015, Month.JANUARY, 6));
		expected = new GregorianCalendar(3015, Calendar.JANUARY, 6).getTime();
		assertDatesEquals(expected, result);

		// 5-digit year far in the future
		result = UtilDate.from(LocalDate.of(10215, Month.JANUARY, 6));
		expected = new GregorianCalendar(10215, Calendar.JANUARY, 6).getTime();
		assertDatesEquals(expected, result);
	}

	@Test
	public void testFromLocalDateTime() {

		Date result;
		Date expected;

		result = UtilDate.from((LocalDateTime) null);
		expected = null;
		assertEquals(expected, result);

		// 4-digit current year
		result = UtilDate.from(LocalDateTime.of(2015, Month.APRIL, 1, 12, 30));
		expected = new GregorianCalendar(2015, Calendar.APRIL, 1, 12, 30).getTime();
		assertDatesEquals(expected, result);

		// All time artifacts are before 1894-01-01
		result = UtilDate.from(LocalDateTime.of(1893, Month.DECEMBER, 31, 12, 31));
		expected = new GregorianCalendar(1893, Calendar.DECEMBER, 31, 12, 31).getTime();
		assertDatesEquals(expected, result);

		// Precision up to milliseconds (before and after timeline break)
		result = UtilDate.from(LocalDateTime.of(2016, Month.OCTOBER, 12, 7, 11, 13, 123_456_789));
		expected = new Date(new GregorianCalendar(2016, Calendar.OCTOBER, 12, 7, 11, 13).getTime().getTime() + 123);
		assertDatesEquals(expected, result);
		result = UtilDate.from(LocalDateTime.of(1492, Month.OCTOBER, 12, 7, 11, 13, 123_456_789));
		expected = new Date(new GregorianCalendar(1492, Calendar.OCTOBER, 12, 7, 11, 13).getTime().getTime() + 123);
		assertDatesEquals(expected, result);

		// Precision up to milliseconds, nanoseconds are trimmed
		result = UtilDate.from(LocalDateTime.of(2016, Month.OCTOBER, 12, 7, 11, 13, 999_999_999));
		expected = new Date(new GregorianCalendar(2016, Calendar.OCTOBER, 12, 7, 11, 13).getTime().getTime() + 999);
		assertDatesEquals(expected, result);
	}

	@Test
	public void testFromLocalTime() {

		LocalTime localTime;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		// 0. null: null
		localTime = null;
		assertNull(UtilDate.from(localTime));

		// hour
		localTime = LocalTime.of(10, 0);
		assertEqualFormatted(localTime, dateFormat, "1970-01-01 10:00:00.000");

		// hour, minute
		localTime = LocalTime.of(10, 13);
		assertEqualFormatted(localTime, dateFormat, "1970-01-01 10:13:00.000");

		// hour, minute, second
		localTime = LocalTime.of(10, 13, 37);
		assertEqualFormatted(localTime, dateFormat, "1970-01-01 10:13:37.000");

		// hour, minute, second: nanos -> millies
		localTime = LocalTime.of(10, 13, 37, 12345678);
		assertEqualFormatted(localTime, dateFormat, "1970-01-01 10:13:37.012");

		// Several hours
		localTime = LocalTime.of(0, 13);
		assertEqualFormatted(localTime, dateFormat, "1970-01-01 00:13:00.000");
		localTime = LocalTime.of(12, 13);
		assertEqualFormatted(localTime, dateFormat, "1970-01-01 12:13:00.000");
		localTime = LocalTime.of(23, 13);
		assertEqualFormatted(localTime, dateFormat, "1970-01-01 23:13:00.000");
	}

	private static void assertEqualFormatted(LocalTime localTime, DateFormat dateFormat, String expected) {

		assertThat(dateFormat.format(UtilDate.from(localTime)), equalTo(expected));
	}

	@Test
	public void testFromYearMonth() {

		Date result;
		Date expected;

		result = UtilDate.from((YearMonth) null);
		expected = null;
		assertEquals(expected, result);

		// 4-digit current year
		result = UtilDate.from(YearMonth.of(2016, 3));
		expected = new GregorianCalendar(2016, Calendar.MARCH, 1, 0, 0).getTime();
		assertDatesEquals(expected, result);

		// All time artifacts are before 1894-01-01
		result = UtilDate.from(YearMonth.of(1893, Month.DECEMBER));
		expected = new GregorianCalendar(1893, Calendar.DECEMBER, 1).getTime();
		assertDatesEquals(expected, result);
	}

	@Test
	public void testOf() {

		Date result;
		Date expected;

		result = UtilDate.of(2015, Month.JULY, 5);
		expected = new GregorianCalendar(2015, Calendar.JULY, 5).getTime();
		assertDatesEquals(expected, result);

		result = UtilDate.of(2016, Month.FEBRUARY, 29);
		expected = new GregorianCalendar(2016, Calendar.FEBRUARY, 29).getTime();
		assertDatesEquals(expected, result);

		// 2-digit year
		result = UtilDate.of(15, Month.MARCH, 23);
		expected = new GregorianCalendar(15, Calendar.MARCH, 23).getTime();
		assertDatesEquals(expected, result);

		// 3-digit year
		result = UtilDate.of(933, Month.DECEMBER, 25);
		expected = new GregorianCalendar(933, Calendar.DECEMBER, 25).getTime();
		assertDatesEquals(expected, result);

		// 4-digit year in the past
		result = UtilDate.of(1900, Month.DECEMBER, 6);
		expected = new GregorianCalendar(1900, Calendar.DECEMBER, 6).getTime();
		assertDatesEquals(expected, result);

		// test current time without timezone
		LocalDateTime ldtNow = LocalDateTime.now();
		result = UtilDate.of(ldtNow.getYear(), ldtNow.getMonth(), ldtNow.getDayOfMonth());
		expected = new GregorianCalendar(ldtNow.getYear(), ldtNow.getMonth().getValue() - 1, ldtNow.getDayOfMonth()).getTime();
		assertDatesEquals(expected, result);

		// test current time with timezone
		ZonedDateTime zdtNow = ZonedDateTime.now();
		result = UtilDate.of(zdtNow.getYear(), zdtNow.getMonth(), zdtNow.getDayOfMonth());
		expected = new GregorianCalendar(zdtNow.getYear(), zdtNow.getMonth().getValue() - 1, zdtNow.getDayOfMonth()).getTime();
		assertDatesEquals(expected, result);
	}

	@Test(expected = DateTimeException.class)
	public void testOfInvalidDayOfMonth() {

		UtilDate.of(2015, Month.APRIL, 31);
	}

	@Test
	public void testToday() {

		Date result = UtilDate.today();
		Calendar now = Calendar.getInstance();
		Date expected = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).getTime();
		assertDatesEquals(expected, result);
	}

	@Test
	public void testTomorrow() {

		Date result = UtilDate.tomorrow();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date expected = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).getTime();
		assertDatesEquals(expected, result);
	}

	@Test
	public void testYesterday() {

		Date result = UtilDate.yesterday();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date expected = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).getTime();
		assertDatesEquals(expected, result);
	}

	/**
	 * Prüft, ob die übergebenen Dates equals sind und dieselben timeMillies haben.
	 * 
	 * @param expected
	 * @param result
	 */
	private static void assertDatesEquals(Date expected, Date result) {

		assertEquals(expected, result);
		assertEquals(expected.getTime(), result.getTime());
	}

	@Test
	public void testToLocalDate() {

		Date utilDate;
		LocalDate result;

		// null as parameter is supported
		utilDate = null;
		assertNull(UtilDate.toLocalDate(utilDate));

		utilDate = new GregorianCalendar(2015, Calendar.JULY, 31).getTime();
		result = UtilDate.toLocalDate(utilDate);
		assertEquals(2015, result.getYear());
		assertEquals(Month.JULY, result.getMonth());
		assertEquals(31, result.getDayOfMonth());

		utilDate = new GregorianCalendar(2015, Calendar.JULY, 31, 23, 59, 59).getTime();
		result = UtilDate.toLocalDate(utilDate);
		assertEquals(2015, result.getYear());
		assertEquals(Month.JULY, result.getMonth());
		assertEquals(31, result.getDayOfMonth());

		utilDate = new GregorianCalendar(2015, Calendar.AUGUST, 1, 0, 0, 0).getTime();
		result = UtilDate.toLocalDate(utilDate);
		assertEquals(2015, result.getYear());
		assertEquals(Month.AUGUST, result.getMonth());
		assertEquals(1, result.getDayOfMonth());

		utilDate = new GregorianCalendar(2015, Calendar.AUGUST, 1, 0, 0, 1).getTime();
		result = UtilDate.toLocalDate(utilDate);
		assertEquals(2015, result.getYear());
		assertEquals(Month.AUGUST, result.getMonth());
		assertEquals(1, result.getDayOfMonth());

		utilDate = new GregorianCalendar(2015, Calendar.AUGUST, 1, 1, 0, 0).getTime();
		result = UtilDate.toLocalDate(utilDate);
		assertEquals(2015, result.getYear());
		assertEquals(Month.AUGUST, result.getMonth());
		assertEquals(1, result.getDayOfMonth());

		// 2-digit year
		{
			Date dateValue = UtilDate.of(10, Month.JANUARY, 1);
			assertThat(new SimpleDateFormat("yyyy-MM-dd").format(dateValue), equalTo("0010-01-01"));

			LocalDate localDate = UtilDate.toLocalDate(dateValue);
			assertThat(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate), equalTo("0010-01-01"));
			assertThat(localDate.getYear(), equalTo(10));
			assertThat(localDate.getMonth(), equalTo(Month.JANUARY));
			assertThat(localDate.getDayOfMonth(), equalTo(1));
		}

		// 3-digit year
		{
			Date dateValue = UtilDate.of(534, Month.MARCH, 29);
			assertThat(new SimpleDateFormat("yyyy-MM-dd").format(dateValue), equalTo("0534-03-29"));

			LocalDate localDate = UtilDate.toLocalDate(dateValue);
			assertThat(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate), equalTo("0534-03-29"));
			assertThat(localDate.getYear(), equalTo(534));
			assertThat(localDate.getMonth(), equalTo(Month.MARCH));
			assertThat(localDate.getDayOfMonth(), equalTo(29));
		}
	}

	@Test
	public void testToLocalDateTime() {

		Date utilDate;
		LocalDateTime result;

		// null as parameter is supported
		utilDate = null;
		assertNull(UtilDate.toLocalDate(utilDate));

		utilDate = new GregorianCalendar(2015, Calendar.JULY, 31).getTime();
		result = UtilDate.toLocalDateTime(utilDate);
		assertIsExpectedDateTime(result, 2015, Month.JULY, 31, 0, 0, 0);

		utilDate = new GregorianCalendar(2015, Calendar.JULY, 31, 23, 59, 59).getTime();
		result = UtilDate.toLocalDateTime(utilDate);
		assertIsExpectedDateTime(result, 2015, Month.JULY, 31, 23, 59, 59);

		utilDate = new GregorianCalendar(2015, Calendar.AUGUST, 1, 0, 0, 0).getTime();
		result = UtilDate.toLocalDateTime(utilDate);
		assertIsExpectedDateTime(result, 2015, Month.AUGUST, 1, 0, 0, 0);

		// test with different timezone
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone(ZoneOffset.UTC));
		calendar.set(Calendar.YEAR, 2015);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		result = UtilDate.toLocalDateTime(calendar.getTime(), ZoneOffset.UTC);
		assertIsExpectedDateTime(result, 2015, Month.AUGUST, 1, 0, 0, 0);

		// 2-digit year
		{
			utilDate = new GregorianCalendar(10, Calendar.AUGUST, 1, 3, 40, 0).getTime();
			result = UtilDate.toLocalDateTime(utilDate);
			assertIsExpectedDateTime(result, 10, Month.AUGUST, 1, 3, 40, 0);
		}

		// 3-digit year
		{
			utilDate = new GregorianCalendar(517, Calendar.AUGUST, 1, 7, 41, 0).getTime();
			result = UtilDate.toLocalDateTime(utilDate);
			assertIsExpectedDateTime(result, 517, Month.AUGUST, 1, 7, 41, 0);
		}

		// Precision up to milliseconds (before and after break in timeline)
		utilDate = new Date(new GregorianCalendar(2016, Calendar.OCTOBER, 12, 7, 11, 13).getTime().getTime() + 119);
		assertIsExpectedDateTime(UtilDate.toLocalDateTime(utilDate), 2016, Month.OCTOBER, 12, 7, 11, 13, 119);
		utilDate = new Date(new GregorianCalendar(1492, Calendar.OCTOBER, 12, 7, 11, 13).getTime().getTime() + 119);
		assertIsExpectedDateTime(UtilDate.toLocalDateTime(utilDate), 1492, Month.OCTOBER, 12, 7, 11, 13, 119);
	}

	private static void assertIsExpectedDateTime(
		LocalDateTime result,
		int expectedYear,
		Month expectedMonth,
		int expectedDayOfMonth,
		int expectedHour,
		int expectedMinute,
		int expectedSecond) {

		assertIsExpectedDateTime(result, expectedYear, expectedMonth, expectedDayOfMonth, expectedHour, expectedMinute, expectedSecond, 0);
	}

	private static void assertIsExpectedDateTime(
		LocalDateTime result,
		int expectedYear,
		Month expectedMonth,
		int expectedDayOfMonth,
		int expectedHour,
		int expectedMinute,
		int expectedSecond,
		int expectedMillisecond) {

		assertEquals(expectedYear, result.getYear());
		assertEquals(expectedMonth, result.getMonth());
		assertEquals(expectedDayOfMonth, result.getDayOfMonth());
		assertEquals(expectedHour, result.getHour());
		assertEquals(expectedMinute, result.getMinute());
		assertEquals(expectedSecond, result.getSecond());
		assertEquals(expectedMillisecond * UtilDate.NANOS_PER_MILLISECOND, result.getNano());
	}

	@Test
	public void testToLocalTime() {

		Date utilDate;

		// 0. null: null
		utilDate = null;
		assertNull(UtilDate.toLocalTime(utilDate));

		utilDate = new GregorianCalendar(1970, 1, 1, 10, 13).getTime();
		assertIsExpectedTime(UtilDate.toLocalTime(utilDate), 10, 13, 0, 0);

		utilDate = new GregorianCalendar(0, 0, 0, 10, 13, 37).getTime();
		assertIsExpectedTime(UtilDate.toLocalTime(utilDate), 10, 13, 37, 0);

		// 2-digit year: No problems with break in 19h century.
		utilDate = new GregorianCalendar(10, 0, 1, 10, 13, 37).getTime();
		assertIsExpectedTime(UtilDate.toLocalTime(utilDate), 10, 13, 37, 0);

		Calendar cal = Calendar.getInstance();
		cal.set(2016, 2, 31, 10, 13, 37);
		cal.set(Calendar.MILLISECOND, 12);
		utilDate = cal.getTime();
		assertIsExpectedTime(UtilDate.toLocalTime(utilDate), 10, 13, 37, 12_000_000);

		// Test different hours
		utilDate = new GregorianCalendar(0, 0, 0, 0, 13, 37).getTime();
		assertIsExpectedTime(UtilDate.toLocalTime(utilDate), 0, 13, 37, 0);

		utilDate = new GregorianCalendar(0, 0, 0, 12, 13, 37).getTime();
		assertIsExpectedTime(UtilDate.toLocalTime(utilDate), 12, 13, 37, 0);

		utilDate = new GregorianCalendar(0, 0, 0, 23, 13, 37).getTime();
		assertIsExpectedTime(UtilDate.toLocalTime(utilDate), 23, 13, 37, 0);
	}

	/**
	 * <ul>
	 * <li>For {@link java.sql.Date} the time is 00:00.</li>
	 * <li>For {@link java.sql.Time} the time is extracted.</li>
	 * <li>For {@link java.sql.Timestamp} the time is extracted with nanoseconds precision.</li>
	 * </ul>
	 */
	@Test
	@SuppressWarnings("deprecation")
	public void testToLocalTimeSqlTypes() {

		// 1. java.sql.Date is supported (with 00:00)
		java.sql.Date sqlDate = new java.sql.Date(1970, 1, 1);
		assertIsExpectedTime(UtilDate.toLocalTime(sqlDate), 0, 0, 0, 0);

		// 2. java.sql.Time is supported (hours to milliseconds)
		{
			java.sql.Time sqlTime = new java.sql.Time(10, 13, 37);
			assertIsExpectedTime(UtilDate.toLocalTime(sqlTime), 10, 13, 37, 0);

			int millies = 123;
			sqlTime = new java.sql.Time(sqlTime.getTime() + millies);
			assertIsExpectedTime(UtilDate.toLocalTime(sqlTime), 10, 13, 37, millies * UtilDate.NANOS_PER_MILLISECOND);
		}

		// 3. java.sql.Timestamp: nanoseconds are considered
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(1970, 1, 1, 10, 13, 37, 12345678);
		assertIsExpectedTime(UtilDate.toLocalTime(sqlTimestamp), 10, 13, 37, 12_345_678);
	}

	private static void assertIsExpectedTime(LocalTime result, int hour, int minute, int second, int nano) {

		assertEquals(hour, result.getHour());
		assertEquals(minute, result.getMinute());
		assertEquals(second, result.getSecond());
		assertEquals(nano, result.getNano());
	}

	@Test
	public void testToYearMonth() {

		Date utilDate;
		YearMonth result;

		// support null as parameter
		utilDate = null;
		assertNull(UtilDate.toYearMonth(utilDate));

		utilDate = new GregorianCalendar(2015, Calendar.JULY, 31).getTime();
		result = UtilDate.toYearMonth(utilDate);
		assertIsExpectedYearMonth(result, 2015, Month.JULY);

		utilDate = new GregorianCalendar(2015, Calendar.AUGUST, 1).getTime();
		result = UtilDate.toYearMonth(utilDate);
		assertIsExpectedYearMonth(result, 2015, Month.AUGUST);

		// other timezone
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone(ZoneOffset.UTC));
		calendar.set(Calendar.YEAR, 2015);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		result = UtilDate.toYearMonth(calendar.getTime(), ZoneOffset.UTC);
		assertIsExpectedYearMonth(result, 2015, Month.AUGUST);

		// 2-digit year
		{
			Date dateValue = UtilDate.of(10, Month.JANUARY, 1);
			assertThat(new SimpleDateFormat("yyyy-MM-dd").format(dateValue), equalTo("0010-01-01"));

			YearMonth localDate = UtilDate.toYearMonth(dateValue);
			assertThat(DateTimeFormatter.ofPattern("yyyy-MM").format(localDate), equalTo("0010-01"));
			assertThat(localDate.getYear(), equalTo(10));
			assertThat(localDate.getMonth(), equalTo(Month.JANUARY));
		}

		// 3-digit year
		{
			Date dateValue = UtilDate.of(534, Month.MARCH, 29);
			assertThat(new SimpleDateFormat("yyyy-MM-dd").format(dateValue), equalTo("0534-03-29"));

			YearMonth localDate = UtilDate.toYearMonth(dateValue);
			assertThat(DateTimeFormatter.ofPattern("yyyy-MM").format(localDate), equalTo("0534-03"));
			assertThat(localDate.getYear(), equalTo(534));
			assertThat(localDate.getMonth(), equalTo(Month.MARCH));
		}
	}

	private static void assertIsExpectedYearMonth(YearMonth result, int expectedYear, Month expectedMonth) {

		assertEquals(expectedYear, result.getYear());
		assertEquals(expectedMonth, result.getMonth());
	}
}
