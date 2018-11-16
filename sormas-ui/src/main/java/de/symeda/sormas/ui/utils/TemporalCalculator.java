package de.symeda.sormas.ui.utils;

import java.util.Calendar;
import java.util.Date;

public class TemporalCalculator {
	
	/**
	 * If the century is positive and has only two digits, it is set to a fitting century relative to the current point in time.
	 * <p>
	 * Use case: Correcting a two-digit date entered by the user.
	 * 
	 * @param value The date to (possibly) correct
	 * @return The entered date, possibly with corrected century
	 */
	public static Date toCorrectCentury(Date value) {
		return toCorrectCentury(value, new Date());
	}
	
	/**
	 * If the century is positive and has only two digits, it is set to a fitting century relative to the current point in time.
	 * <p>
	 * Use case: Correcting a two-digit date entered by the user.
	 * 
	 * @param value The date to (possibly) correct
	 * @param reference The current date as reference
	 * @return The entered date, possibly with corrected century
	 */
	public static Date toCorrectCentury(Date value, Date reference) {
 
		if (value == null || reference == null) {
			return null;
		}
 
//		if (!IsoEra.CE.equals(value.getEra())) {
//			throw new IllegalArgumentException("Unexpected Era: " + value.getEra());
//		}
 
		Calendar c = Calendar.getInstance();
		c.setTime(value);
		int year = c.get(Calendar.YEAR);
		final Date correctedValue;
		if (year >= 0 && year < 100) {
			// Year is in first century
			Calendar refC = Calendar.getInstance();
			refC.setTime(reference);
			int currentYear = refC.get(Calendar.YEAR);
			int currentYY = currentYear % 100;
			int currentCC = currentYear / 100;
 
			// two-digit for up to 10 years in the future and 89 in the past
			if (year - 10 > currentYY) {
				// 19.. last century; ex: 30 - 10 > 15 (2015 % 100)
				year += 100 * (currentCC - 1);
			} else {
				// 20.. this century; ex: 16 - 10 <= 15 (2015 % 100)
				year += 100 * currentCC;
			}
			c.set(Calendar.YEAR, year);
			correctedValue = c.getTime();
		} else {
			correctedValue = value;
		}
 
		return correctedValue;
	}

}
