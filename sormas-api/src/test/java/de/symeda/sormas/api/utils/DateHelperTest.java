package de.symeda.sormas.api.utils;

import static org.junit.Assert.assertEquals;

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
	
}
