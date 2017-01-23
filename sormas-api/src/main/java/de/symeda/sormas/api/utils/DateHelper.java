package de.symeda.sormas.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public final class DateHelper {

	private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat TIME_DATE_FORMAT = new SimpleDateFormat("HH:mm dd/MM/yyyy");
	private static final SimpleDateFormat SHORT_TIME_DATE_FORMAT = new SimpleDateFormat("HH:mm dd/MM/yy");
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	
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
	
	public static Date parseDDMMYYYY(String date) {
		if (date != null) {
			try {
				return clone(DATE_FORMAT).parse(date);
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
            return clone(TIME_DATE_FORMAT).format(date);
        } else {
            return "";
        }
    }
	
	/**
	 * Formats to "dd/MM/yyyy HH:mm "
	 * @return
	 */
	public static String formatDDMMYYYYHm(Date date) {
		if (date != null) {
			return clone(DATE_TIME_FORMAT).format(date);
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
     * Returns the time with a dummy date
     * @param hour
     * @param minute
     * @return
     */
    public static Date getTime(int hour, int minute) {
    	Calendar calendar = new GregorianCalendar();
    	calendar.set(1970, 01, 01, hour, minute);
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
	 * Returns a list of years from 1900 to now.
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
	
	public static SimpleDateFormat getShortTimeDateFormat() {
		return clone(SHORT_TIME_DATE_FORMAT);
	}
	
	public static SimpleDateFormat getTimeDateFormat() {
		return clone(TIME_DATE_FORMAT);
	}
	
	/**
	 * Calculate days between the two given dates.
	 */
	public static int getDaysBetween(Date start, Date end) {
		return Days.daysBetween(
				new LocalDate(start.getTime()), 
                new LocalDate(end.getTime())).getDays();
	}
}
