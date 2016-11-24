package de.symeda.sormas.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.utils.DataHelper.Pair;

public final class DateHelper {

	private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat TIME_DATA_FORMAT = new SimpleDateFormat("HH:mm dd/MM/yyyy");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	public static final Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate, Date deathDate) {
		if (birthDate == null)
            return Pair.createPair(null, ApproximateAgeType.YEARS);

        DateTime toDate = deathDate==null?DateTime.now(): new DateTime(deathDate);
        DateTime startDate = new DateTime(birthDate);
        Years years = Years.yearsBetween(startDate, toDate);
        

        if(years.getYears()<1) {
            Months months = Months.monthsBetween(startDate, toDate);
            return Pair.createPair(months.getMonths(), ApproximateAgeType.MONTHS);
        }
        else {
            return Pair.createPair(years.getYears(), ApproximateAgeType.YEARS);
        }
        
// 		Same code for Java8		
//		if (birthDate == null)
//			return Pair.createPair(null, ApproximateAgeType.YEARS);
//		
//		LocalDate toDate = deathDate==null?LocalDate.now():Instant.ofEpochMilli(deathDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
//		LocalDate birthdate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		Period period = Period.between(birthdate, toDate);
//		
//		if(period.getYears()<1) {
//			return Pair.createPair(period.getMonths(), ApproximateAgeType.MONTHS);
//		}
//		else {
//			return Pair.createPair(period.getYears(), ApproximateAgeType.YEARS);
//		}
	}
	
	public static final Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate) {
		return getApproximateAge(birthDate, null);
	}
	
	public static String formatApproximateAge(Integer approximateAge, ApproximateAgeType approximateAgeType) {
		if (approximateAge == null) {
			return "";
		} else if (approximateAgeType == ApproximateAgeType.MONTHS) {
			return approximateAge + " " + approximateAgeType.toString();
		} else {
			return approximateAge.toString();
		}
	}
	
	/**
	 * Formats to "HH:mm"
	 * @return
	 */
	public static String formatHourMinute(Date date) {
		if (date != null) {
			return clone(TIME_FORMAT).format(date);
		} else {
			return "";
		}
	}

	/**
	 * Formats to "HH:mm"
	 * @return
	 */
	public static Date parseHourMinute(String date) {
		if (date != null) {
			try {
				return clone(TIME_FORMAT).parse(date);
			} catch (ParseException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Formats to "dd.MM.yy"
	 * @return
	 */
	public static String formatDMY(Date date) {
		if (date != null) {
			return clone(SHORT_DATE_FORMAT).format(date);
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
            return clone(DATE_FORMAT).format(date);
        } else {
            return "";
        }
    }
	
	/**
	 * Formats to "HH:mm dd/MM/yy"
	 * @return
	 */
	public static String formatHmDDMMYYYY(Date date) {
        if (date != null) {
            return clone(TIME_DATA_FORMAT).format(date);
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
    
    /**
     * Returns a list for days in month (1-31)
     * @return
     */
    public static List<Integer> getDaysInMonth() {
		List<Integer> x = new ArrayList<Integer>();
		for(int i=1; i<=31;i++) {
			x.add(i);
		}
		return x;
	}
	
    /**
     * Returns a list of months in years (1-12)
     * @return
     */
	public static List<Integer> getMonthsInYear() {
		List<Integer> x = new ArrayList<Integer>();
		for(int i=1; i<=12;i++) {
			x.add(i);
		}
		return x;
	}
	
	/**
	 * Returnsa list of years from 1900 to now.
	 * @return
	 */
	public static List<Integer> getYearsToNow() {
		List<Integer> x = new ArrayList<Integer>();
		Calendar now = new GregorianCalendar();
		for(int i=1900; i<=now.get(Calendar.YEAR);i++) {
			x.add(i);
		}
		return x;
	}
	
	public static SimpleDateFormat getShortDateFormat() {
		return clone(SHORT_DATE_FORMAT);
	}
}
