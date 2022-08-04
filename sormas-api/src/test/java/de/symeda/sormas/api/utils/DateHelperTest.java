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
package de.symeda.sormas.api.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;

public class DateHelperTest {

	@Test
	public void testGetDaysInMonth() {

		assertDays(DateHelper.getDaysInMonth(1, 2010), 31);
		assertDays(DateHelper.getDaysInMonth(2, 2010), 28);
		assertDays(DateHelper.getDaysInMonth(3, 2010), 31);
		assertDays(DateHelper.getDaysInMonth(4, 2010), 30);
		assertDays(DateHelper.getDaysInMonth(5, 2010), 31);
		assertDays(DateHelper.getDaysInMonth(6, 2010), 30);
		assertDays(DateHelper.getDaysInMonth(7, 2010), 31);
		assertDays(DateHelper.getDaysInMonth(8, 2010), 31);
		assertDays(DateHelper.getDaysInMonth(9, 2010), 30);
		assertDays(DateHelper.getDaysInMonth(10, 2010), 31);
		assertDays(DateHelper.getDaysInMonth(11, 2010), 30);
		assertDays(DateHelper.getDaysInMonth(12, 2010), 31);
	}

	@Test
	public void testGetDaysInMonthLeapYear() {

		// No leap year
		assertDays(DateHelper.getDaysInMonth(2, 1900), 28);
		assertDays(DateHelper.getDaysInMonth(2, 2009), 28);
		assertDays(DateHelper.getDaysInMonth(2, 2010), 28);
		assertDays(DateHelper.getDaysInMonth(2, 2011), 28);

		// Leap year
		assertDays(DateHelper.getDaysInMonth(2, null), 29);
		assertDays(DateHelper.getDaysInMonth(2, 2000), 29);
		assertDays(DateHelper.getDaysInMonth(2, 2008), 29);
	}

	/**
	 * Asserts that all expected days are present in order from 1 to {@code maxDayInMonth}.
	 */
	private static void assertDays(List<Integer> days, int maxDayInMonth) {

		Integer[] expectedDays = new Integer[maxDayInMonth];
		for (int d = 0; d < maxDayInMonth; d++) {
			expectedDays[d] = d + 1;
		}
		assertThat(days, contains(expectedDays));
	}

	@Test
	public void testCalculateProperTimePeriodDifferences() {

		Date date = new Date();

		Date start = DateHelper.getStartOfWeek(date);
		Date end = DateHelper.getEndOfWeek(date);

		// This should be 7
		int period = DateHelper.getDaysBetween(start, end);

		Date previousStart = DateHelper.getStartOfDay(DateHelper.subtractDays(start, period));
		Date previousEnd = DateHelper.getEndOfDay(DateHelper.subtractDays(end, period));

		assertEquals(DateHelper.getStartOfWeek(DateHelper.subtractWeeks(date, 1)), previousStart);
		assertEquals(DateHelper.getEndOfWeek(DateHelper.subtractWeeks(date, 1)), previousEnd);
	}

	@Test
	public void testEpiWeekUsesCorrectYear() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.YEAR, 2018);

		EpiWeek epiWeek = DateHelper.getEpiWeek(calendar.getTime());

		assertEquals(new Integer(1), epiWeek.getWeek());
		assertEquals(new Integer(2019), epiWeek.getYear());
	}

	@Test
	public void testGetSameEpiWeek() {

		EpiWeek selectedEpiWeek = DateHelper.getEpiWeek(DateHelper.getDateZero(2022, Calendar.JANUARY, 17));

		// 0. Edge cases concerning null/empty
		{
			assertNull(DateHelper.getSameEpiWeek(null, null));
			assertNull(DateHelper.getSameEpiWeek(null, Collections.emptyList()));
			assertNull(DateHelper.getSameEpiWeek(selectedEpiWeek, null));
			assertNull(DateHelper.getSameEpiWeek(selectedEpiWeek, Collections.emptyList()));
		}

		int otherYear = 2021;

		// 1a. Get another year with begin of year
		{
			EpiWeek result = DateHelper.getSameEpiWeek(selectedEpiWeek, DateHelper.createEpiWeekList(otherYear));
			assertThat(selectedEpiWeek.getWeek(), equalTo(4));
			assertThat(result.getWeek(), equalTo(selectedEpiWeek.getWeek()));
			assertThat(result.getYear(), equalTo(otherYear));
		}

		selectedEpiWeek = DateHelper.getEpiWeek(DateHelper.getDateZero(2022, Calendar.DECEMBER, 24));

		// 1b. Get another year with end of year
		{
			EpiWeek result = DateHelper.getSameEpiWeek(selectedEpiWeek, DateHelper.createEpiWeekList(otherYear));
			assertThat(selectedEpiWeek.getWeek(), equalTo(52));
			assertThat(result.getWeek(), equalTo(selectedEpiWeek.getWeek()));
			assertThat(result.getYear(), equalTo(otherYear));
		}

		otherYear = 2023;

		// 3. Get another year with 53 epiWeeks
		{
			EpiWeek result = DateHelper.getSameEpiWeek(selectedEpiWeek, DateHelper.createEpiWeekList(otherYear));
			assertThat(selectedEpiWeek.getWeek(), equalTo(52));
			assertThat(result.getWeek(), equalTo(selectedEpiWeek.getWeek()));
			assertThat(result.getYear(), equalTo(otherYear));
		}

		otherYear = 2022;
		selectedEpiWeek = DateHelper.getEpiWeek(DateHelper.getDateZero(2023, Calendar.DECEMBER, 26));

		// 3. Get from year with 53 epiWeeks
		{
			EpiWeek result = DateHelper.getSameEpiWeek(selectedEpiWeek, DateHelper.createEpiWeekList(otherYear));
			assertThat(selectedEpiWeek.getWeek(), equalTo(53));
			assertNull(result);
		}
	}

	@Test
	public void testTwoDigitDateTransformationToCurrentCentury() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(2018, 1, 0);
		Date referenceDate = calendar.getTime();

		calendar.set(18, 1, 0);
		Date date = calendar.getTime();

		assertEquals(referenceDate, DateHelper.toCorrectCentury(date, referenceDate));

		calendar.set(87, 1, 0);
		date = calendar.getTime();

		Date correctCenturyDate = DateHelper.toCorrectCentury(date, referenceDate);
		calendar.setTime(correctCenturyDate);

		assertEquals(calendar.get(Calendar.YEAR), 1987);
	}

	@Test
	public void testToTimestampUpper() throws Exception {

		Date date = new Date(2323231232l);
		Timestamp timestamp = DateHelper.toTimestampUpper(date);
		assertEquals(date.getTime(), timestamp.getTime());
		assertTrue(timestamp.after(new Timestamp(date.getTime())));
		assertEquals(999999, timestamp.getNanos() % 1000000);
	}

	@Test
	public void testFormatBirthdateEN() throws Exception {
		assertEquals("", DateHelper.formatLocalDate(null, null, null, Language.EN));
		assertEquals("1990", DateHelper.formatLocalDate(null, null, 1990, Language.EN));
		assertEquals("7//1990", DateHelper.formatLocalDate(null, 7, 1990, Language.EN));
		assertEquals("7", DateHelper.formatLocalDate(null, 7, null, Language.EN));
		assertEquals("7/5", DateHelper.formatLocalDate(5, 7, null, Language.EN));
		assertEquals("5", DateHelper.formatLocalDate(5, null, null, Language.EN));
		assertEquals("5/1990", DateHelper.formatLocalDate(5, null, 1990, Language.EN));
		assertEquals("7/5/1990", DateHelper.formatLocalDate(5, 7, 1990, Language.EN));
	}

	@Test
	public void testFormatBirthdateDE() throws Exception {
		assertEquals("", DateHelper.formatLocalDate(null, null, null, Language.DE));
		assertEquals("1990", DateHelper.formatLocalDate(null, null, 1990, Language.DE));
		assertEquals("7.1990", DateHelper.formatLocalDate(null, 7, 1990, Language.DE));
		assertEquals("7", DateHelper.formatLocalDate(null, 7, null, Language.DE));
		assertEquals("5.7", DateHelper.formatLocalDate(5, 7, null, Language.DE));
		assertEquals("5", DateHelper.formatLocalDate(5, null, null, Language.DE));
		assertEquals("5..1990", DateHelper.formatLocalDate(5, null, 1990, Language.DE));
		assertEquals("5.7.1990", DateHelper.formatLocalDate(5, 7, 1990, Language.DE));
	}

	@Test
	public void testParseDateWithExceptionForEnFormat() throws ParseException {

		Date date = DateHelper.parseDate("4/21/2021", new SimpleDateFormat("M/dd/yyy"));

		Date parsed = DateHelper.parseDateWithException("4/21/2021", Language.EN.getDateFormat());
		assertEquals(date, parsed);

		parsed = DateHelper.parseDateWithException("4.21.2021", Language.EN.getDateFormat());
		assertEquals(date, parsed);

		parsed = DateHelper.parseDateWithException("4-21-2021", Language.EN.getDateFormat());
		assertEquals(date, parsed);

		parsed = DateHelper.parseDateWithException("4/21/21", Language.EN.getDateFormat());
		assertEquals(date, parsed);

		parsed = DateHelper.parseDateWithException("4.21.21", Language.EN.getDateFormat());
		assertEquals(date, parsed);

		parsed = DateHelper.parseDateWithException("4-21-21", Language.EN.getDateFormat());
		assertEquals(date, parsed);
	}

	@Test
	public void testParseDateWithExceptionForDeFormat() throws ParseException {

		Date date = DateHelper.parseDate("4/21/2021", new SimpleDateFormat("M/dd/yyy"));

		I18nProperties.setUserLanguage(Language.DE);
		try {
			Date parsed = DateHelper.parseDateWithException("21/04/2021", Language.DE.getDateFormat());
			assertEquals(date, parsed);

			parsed = DateHelper.parseDateWithException("21.04.2021", Language.DE.getDateFormat());
			assertEquals(date, parsed);

			parsed = DateHelper.parseDateWithException("21-04-2021", Language.DE.getDateFormat());
			assertEquals(date, parsed);

			parsed = DateHelper.parseDateWithException("21/04/21", Language.DE.getDateFormat());
			assertEquals(date, parsed);

			parsed = DateHelper.parseDateWithException("21.04.21", Language.DE.getDateFormat());
			assertEquals(date, parsed);

			parsed = DateHelper.parseDateWithException("21-04-21", Language.DE.getDateFormat());
			assertEquals(date, parsed);

			I18nProperties.setUserLanguage(null);
		} finally {
			I18nProperties.setUserLanguage(null);
		}
	}

	@Test
	public void testParseDateTimeWithExceptionForEnFormat() throws ParseException {

		Date date = DateHelper.parseDate("4/21/2021 13:30", new SimpleDateFormat("M/dd/yyy HH:mm"));

		Date parsed = DateHelper.parseDateTimeWithException("4/21/2021 13:30", Language.EN.getDateTimeFormat());
		assertEquals(date, parsed);

		parsed = DateHelper.parseDateTimeWithException("4.21.2021 1:30 pm", Language.EN.getDateTimeFormat());
		assertEquals(date, parsed);

		parsed = DateHelper.parseDateTimeWithException("4-21-2021 13.30", Language.EN.getDateTimeFormat());
		assertEquals(date, parsed);

		parsed = DateHelper.parseDateTimeWithException("4-21-2021 1.30 pm", Language.EN.getDateTimeFormat());
		assertEquals(date, parsed);

		Date parsedNoTime = DateHelper.parseDateTimeWithException("4/21/2021", Language.EN.getDateTimeFormat());
		assertEquals(parsedNoTime, DateHelper.parseDate("4/21/2021", new SimpleDateFormat("M/dd/yyy")));
	}

	@Test
	public void testParseDateTimeWithExceptionForDeFormat() throws ParseException {

		Date date = DateHelper.parseDate("4/21/2021 13:30", new SimpleDateFormat("M/dd/yyy HH:mm"));

		I18nProperties.setUserLanguage(Language.DE);
		try {
			Date parsed = DateHelper.parseDateTimeWithException("21/04/2021 13:30", Language.DE.getDateTimeFormat());
			assertEquals(date, parsed);

			// TODO: #6700: Does not work: Is this format legit with Locale = DE?
//			parsed = DateHelper.parseDateTimeWithException("21.04.2021 1:30 pm", Language.DE.getDateTimeFormat());
//			assertEquals(date, parsed);

			parsed = DateHelper.parseDateTimeWithException("21-04-2021 13.30", Language.DE.getDateTimeFormat());
			assertEquals(date, parsed);

			// TODO: #6700: Does not work: Is this format legit with Locale = DE?
//			parsed = DateHelper.parseDateTimeWithException("21-04-2021 1.30 pm", Language.DE.getDateTimeFormat());
//			assertEquals(date, parsed);

			Date parsedNoTime = DateHelper.parseDateTimeWithException("21/04/2021", Language.DE.getDateTimeFormat());
			assertEquals(UtilDate.of(2021, Month.APRIL, 21), parsedNoTime);
		} finally {
			I18nProperties.setUserLanguage(null);
		}
	}

	@Test
	public void testGetFullDaysBetween() {

		assertThat(DateHelper.getFullDaysBetween(UtilDate.of(2022, Month.JUNE, 25), UtilDate.of(2022, Month.JUNE, 25)), equalTo(0));
		assertThat(DateHelper.getFullDaysBetween(UtilDate.of(2022, Month.JUNE, 25), UtilDate.of(2022, Month.JULY, 5)), equalTo(10));
		assertThat(DateHelper.getFullDaysBetween(UtilDate.of(2022, Month.JULY, 1), UtilDate.of(2022, Month.JULY, 5)), equalTo(4));
	}

	@Test
	public void testGetDaysBetween() {

		assertThat(DateHelper.getDaysBetween(UtilDate.of(2022, Month.JUNE, 25), UtilDate.of(2022, Month.JUNE, 25)), equalTo(1));
		assertThat(DateHelper.getDaysBetween(UtilDate.of(2022, Month.JUNE, 25), UtilDate.of(2022, Month.JULY, 5)), equalTo(11));
		assertThat(DateHelper.getDaysBetween(UtilDate.of(2022, Month.JULY, 1), UtilDate.of(2022, Month.JULY, 5)), equalTo(5));
	}

	@Test
	public void testGetWeeksBetween() {

		assertThat(DateHelper.getWeeksBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2022, Month.MAY, 1)), equalTo(1));
		assertThat(DateHelper.getWeeksBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2022, Month.MAY, 5)), equalTo(1));
		assertThat(DateHelper.getWeeksBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2022, Month.MAY, 14)), equalTo(2));
		assertThat(DateHelper.getWeeksBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2022, Month.MAY, 15)), equalTo(3));
	}

	@Test
	public void testGetMonthsBetween() {

		assertThat(DateHelper.getMonthsBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2022, Month.MAY, 1)), equalTo(1));
		assertThat(DateHelper.getMonthsBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2022, Month.MAY, 31)), equalTo(1));
		assertThat(DateHelper.getMonthsBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2022, Month.JUNE, 1)), equalTo(2));
		assertThat(DateHelper.getMonthsBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2022, Month.JULY, 15)), equalTo(3));
	}

	@Test
	public void testGetYearsBetween() {

		assertThat(DateHelper.getYearsBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2022, Month.MAY, 1)), equalTo(0));
		assertThat(DateHelper.getYearsBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2023, Month.APRIL, 30)), equalTo(0));
		assertThat(DateHelper.getYearsBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2023, Month.MAY, 1)), equalTo(1));
		assertThat(DateHelper.getYearsBetween(UtilDate.of(2022, Month.MAY, 1), UtilDate.of(2024, Month.JULY, 15)), equalTo(2));
	}

	@Test
	public void testAddDays() {

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.addDays(null, 0), equalTo(UtilDate.today()));

		assertThat(DateHelper.addDays(UtilDate.now(), 0), equalTo(UtilDate.today()));
		assertThat(DateHelper.addDays(UtilDate.today(), 0), equalTo(UtilDate.today()));
		assertThat(DateHelper.addDays(UtilDate.now(), 1), equalTo(UtilDate.tomorrow()));
		assertThat(DateHelper.addDays(UtilDate.today(), 1), equalTo(UtilDate.tomorrow()));
	}

	@Test
	public void testSubtractDays() {

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.subtractDays(null, 0), equalTo(UtilDate.today()));

		assertThat(DateHelper.subtractDays(UtilDate.now(), 0), equalTo(UtilDate.today()));
		assertThat(DateHelper.subtractDays(UtilDate.today(), 0), equalTo(UtilDate.today()));
		assertThat(DateHelper.subtractDays(UtilDate.now(), 1), equalTo(UtilDate.yesterday()));
		assertThat(DateHelper.subtractDays(UtilDate.today(), 1), equalTo(UtilDate.yesterday()));
	}

	@Test
	public void testAddWeeks() {

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.addWeeks(null, 1), equalTo(UtilDate.from(LocalDate.now().plusWeeks(1))));

		Date date = UtilDate.of(2022, Month.MAY, 4);
		assertThat(DateHelper.addWeeks(date, 0), equalTo(date));
		assertThat(DateHelper.addWeeks(date, 1), equalTo(UtilDate.of(2022, Month.MAY, 11)));
		assertThat(DateHelper.addWeeks(date, 2), equalTo(UtilDate.of(2022, Month.MAY, 18)));
	}

	@Test
	public void testSubtractWeeks() {

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.subtractWeeks(null, 1), equalTo(UtilDate.from(LocalDate.now().minusWeeks(1))));

		Date date = UtilDate.of(2022, Month.MAY, 4);
		assertThat(DateHelper.subtractWeeks(date, 0), equalTo(date));
		assertThat(DateHelper.subtractWeeks(date, 1), equalTo(UtilDate.of(2022, Month.APRIL, 27)));
		assertThat(DateHelper.subtractWeeks(date, 2), equalTo(UtilDate.of(2022, Month.APRIL, 20)));
	}

	@Test
	public void testAddMonths() {

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.addMonths(null, 1), equalTo(UtilDate.from(LocalDate.now().plusMonths(1))));

		Date date = UtilDate.of(2022, Month.MAY, 4);
		assertThat(DateHelper.addMonths(date, 0), equalTo(date));
		assertThat(DateHelper.addMonths(date, 1), equalTo(UtilDate.of(2022, Month.JUNE, 4)));
		assertThat(DateHelper.addMonths(date, 2), equalTo(UtilDate.of(2022, Month.JULY, 4)));
	}

	@Test
	public void testSubtractMonths() {

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.subtractMonths(null, 1), equalTo(UtilDate.from(LocalDate.now().minusMonths(1))));

		Date date = UtilDate.of(2022, Month.MAY, 4);
		assertThat(DateHelper.subtractMonths(date, 0), equalTo(date));
		assertThat(DateHelper.subtractMonths(date, 1), equalTo(UtilDate.of(2022, Month.APRIL, 4)));
		assertThat(DateHelper.subtractMonths(date, 2), equalTo(UtilDate.of(2022, Month.MARCH, 4)));
	}

	@Test
	public void testAddYears() {

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.addYears(null, 1), equalTo(UtilDate.from(LocalDate.now().plusYears(1))));

		Date date = UtilDate.of(2022, Month.MAY, 4);
		assertThat(DateHelper.addYears(date, 0), equalTo(date));
		assertThat(DateHelper.addYears(date, 1), equalTo(UtilDate.of(2023, Month.MAY, 4)));
		assertThat(DateHelper.addYears(date, 2), equalTo(UtilDate.of(2024, Month.MAY, 4)));
	}

	@Test
	public void testSubtractYears() {

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.subtractYears(null, 1), equalTo(UtilDate.from(LocalDate.now().minusYears(1))));

		Date date = UtilDate.of(2022, Month.MAY, 4);
		assertThat(DateHelper.subtractYears(date, 0), equalTo(date));
		assertThat(DateHelper.subtractYears(date, 1), equalTo(UtilDate.of(2021, Month.MAY, 4)));
		assertThat(DateHelper.subtractYears(date, 2), equalTo(UtilDate.of(2020, Month.MAY, 4)));
	}

	@Test
	public void testGetStartOfDay() {

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.getStartOfDay(null), equalTo(UtilDate.today()));

		assertThat(DateHelper.getStartOfDay(UtilDate.now()), equalTo(UtilDate.today()));
	}

	@Test
	public void testGetEndOfDay() {

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.getEndOfDay(null), equalTo(UtilDate.from(LocalDate.now().plusDays(1).atStartOfDay().minus(1, ChronoUnit.MILLIS))));

		Date date = UtilDate.of(2022, Month.MAY, 4);
		Date expected = UtilDate.from(LocalDateTime.of(2022, Month.MAY, 5, 0, 0).minus(1, ChronoUnit.MILLIS));
		assertThat(DateHelper.getEndOfDay(date), equalTo(expected));
	}

	@Test
	public void testGetStartOfWeek() {

		LocalDate monday = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.getStartOfWeek(null), equalTo(UtilDate.from(monday)));

		assertThat(DateHelper.getStartOfWeek(UtilDate.now()), equalTo(UtilDate.from(monday)));
	}

	@Test
	public void testGetEndOfWeek() {

		LocalDate sunday = LocalDate.now().plusDays(7 - LocalDate.now().getDayOfWeek().getValue());

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.getEndOfWeek(null), equalTo(DateHelper.getEndOfDay(UtilDate.from(sunday))));

		assertThat(DateHelper.getEndOfWeek(UtilDate.now()), equalTo(DateHelper.getEndOfDay(UtilDate.from(sunday))));
	}

	@Test
	public void testGetStartOfMonth() {

		LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.getStartOfMonth(null), equalTo(UtilDate.from(firstOfMonth)));

		assertThat(DateHelper.getStartOfMonth(UtilDate.now()), equalTo(UtilDate.from(firstOfMonth)));
	}

	@Test
	public void testGetEndOfMonth() {

		LocalDate endOfMonth = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1);

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.getEndOfMonth(null), equalTo(DateHelper.getEndOfDay(UtilDate.from(endOfMonth))));

		assertThat(DateHelper.getEndOfMonth(UtilDate.now()), equalTo(DateHelper.getEndOfDay(UtilDate.from(endOfMonth))));
	}

	@Test
	public void testGetStartOfYear() {

		LocalDate firstOfYear = LocalDate.now().withDayOfMonth(1).withMonth(1);

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.getStartOfYear(null), equalTo(UtilDate.from(firstOfYear)));

		assertThat(DateHelper.getStartOfYear(UtilDate.now()), equalTo(UtilDate.from(firstOfYear)));
	}

	@Test
	public void testGetEndOfYear() {

		LocalDate endOfYear = LocalDate.now().withMonth(12).withDayOfMonth(31);

		// Mimic previous behaviour before switching to java.time.
		assertThat(DateHelper.getEndOfYear(null), equalTo(DateHelper.getEndOfDay(UtilDate.from(endOfYear))));

		assertThat(DateHelper.getEndOfYear(UtilDate.now()), equalTo(DateHelper.getEndOfDay(UtilDate.from(endOfYear))));
	}

	@Test
	public void testFindDateBounds() {

		// Invalid entries
		assertNull(DateHelper.findDateBounds(null));
		assertNull(DateHelper.findDateBounds(""));
		assertNull(DateHelper.findDateBounds(" "));
		assertNull(DateHelper.findDateBounds("yesterday"));

		// Year precision
		assertEqualDateRange(DateHelper.findDateBounds("90"), LocalDate.of(1990, Month.JANUARY, 1), LocalDate.of(1991, Month.JANUARY, 1));
		assertEqualDateRange(DateHelper.findDateBounds("08"), LocalDate.of(2008, Month.JANUARY, 1), LocalDate.of(2009, Month.JANUARY, 1));
		assertEqualDateRange(DateHelper.findDateBounds("1830"), LocalDate.of(1830, Month.JANUARY, 1), LocalDate.of(1831, Month.JANUARY, 1));

		int currentYear = LocalDate.now().getYear();

		// Month precision
		assertEqualDateRange(DateHelper.findDateBounds("3/01"), LocalDate.of(2001, Month.MARCH, 1), LocalDate.of(2001, Month.APRIL, 1));
		assertEqualDateRange(DateHelper.findDateBounds("3/"), LocalDate.of(currentYear, Month.MARCH, 1), LocalDate.of(currentYear, Month.APRIL, 1));

		// Day precision
		assertEqualDateRange(DateHelper.findDateBounds("3/4/2012"), LocalDate.of(2012, Month.APRIL, 3), LocalDate.of(2012, Month.APRIL, 4));
		assertEqualDateRange(DateHelper.findDateBounds("3/4/"), LocalDate.of(currentYear, Month.APRIL, 3), LocalDate.of(currentYear, Month.APRIL, 4));

		// Currently not working conversions
		assertNull(DateHelper.findDateBounds("3.01"));
		assertNull(DateHelper.findDateBounds("3.4.2012"));
	}

	private static void assertEqualDateRange(Date[] actualRange, LocalDate expectedBegin, LocalDate expectedEnd) {

		assertThat(actualRange[0], equalTo(UtilDate.from(expectedBegin)));
		assertThat(actualRange[1], equalTo(UtilDate.from(expectedEnd)));
	}
}
