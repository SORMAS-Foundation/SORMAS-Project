package de.symeda.sormas.api.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.utils.DataHelper.Pair;

public final class DateHelper {

	private static final SimpleDateFormat dMy_FORMAT = new SimpleDateFormat("dd.MM.yy");
	private static final SimpleDateFormat DDMMYY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static final Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate, Date deathDate) {
		if (birthDate == null)
			return Pair.createPair(null, ApproximateAgeType.YEARS);
		
		LocalDate toDate = deathDate==null?LocalDate.now():Instant.ofEpochMilli(deathDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate birthdate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Period period = Period.between(birthdate, toDate);
		
		if(period.getYears()<1) {
			return Pair.createPair(period.getMonths(), ApproximateAgeType.MONTHS);
		}
		else {
			return Pair.createPair(period.getYears(), ApproximateAgeType.YEARS);
		}
	}
	
	public static final Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate) {
		return getApproximateAge(birthDate, null);
	}
	
	
	/**
	 * Formats to "dd.MM.yy"
	 * @return
	 */
	public static String formatDMY(Date date) {
		if (date != null) {
			return clone(dMy_FORMAT).format(date);
		} else {
			return "";
		}
	}
	
	/**
	 * Formats to "dd/MM/yy"
	 * @return
	 */
	public static String formatDDMMYYYY(Date date) {
        if (date != null) {
            return clone(DDMMYY_FORMAT).format(date);
        } else {
            return "";
        }
    }
	
	private static SimpleDateFormat clone(SimpleDateFormat sdf) {
		return (SimpleDateFormat) sdf.clone();
	}
	
	
	
	/**
     * Returns a Date at 0 h, 0 m, 0 s
     * @param day
     * @param month
     * @param year
     * @return
     */
    public static Date getDateZero(int year, int month,int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year,month,day,0,0,0);
        return calendar.getTime();
    }
}
