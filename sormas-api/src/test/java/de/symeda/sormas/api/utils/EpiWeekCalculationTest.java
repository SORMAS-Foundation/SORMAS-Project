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
