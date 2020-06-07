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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class DateHelperTest {

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
}
