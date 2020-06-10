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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class EpiWeekCalculationTest {

	@Test
	public void testCalculatePreviousEpiWeek() {

		EpiWeek epiWeek = new EpiWeek(2017, 1);
		EpiWeek previousEpiWeek = DateHelper.getPreviousEpiWeek(epiWeek);
		assertEquals(previousEpiWeek.getYear(), new Integer(2016));
		assertEquals(previousEpiWeek.getWeek(), new Integer(52));
	}

	@Test
	public void testCalculateNextEpiWeek() {

		EpiWeek epiWeek = new EpiWeek(2017, 53);
		EpiWeek nextEpiWeek = DateHelper.getNextEpiWeek(epiWeek);
		assertEquals(nextEpiWeek.getYear(), new Integer(2018));
		assertEquals(nextEpiWeek.getWeek(), new Integer(1));
	}

	/**
	 * TODO this test is not stable, because calculateEpiWeekReportStartAndEnd uses the current date for comparisons.
	 * Add a "now" param to calculateEpiWeekReportStartAndEnd
	 */
	@Test
	public void testCalculateEpiWeekReportStartAndEnd() {

		EpiWeek epiWeek = new EpiWeek(2017, 42);
		@SuppressWarnings("deprecation")
		Date now = new Date(2017, 9, 11);
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(2017, 10, 11);
		Date weeklyReportDate = calendar.getTime();
		calendar.clear();
		calendar.set(2017, 10, 10);
		Date previousWeeklyReportDate = calendar.getTime();

		Date[] startAndEnd = DateHelper.calculateEpiWeekReportStartAndEnd(now, epiWeek, weeklyReportDate, previousWeeklyReportDate, null);
		assertTrue(startAndEnd[0].equals(previousWeeklyReportDate));
		assertFalse(startAndEnd[1].after(weeklyReportDate));

		startAndEnd = DateHelper.calculateEpiWeekReportStartAndEnd(now, epiWeek, null, previousWeeklyReportDate, null);
		assertTrue(startAndEnd[0].equals(previousWeeklyReportDate));
		assertTrue(startAndEnd[1].equals(DateHelper.getEpiWeekEnd(epiWeek)));
	}
}
