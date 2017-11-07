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
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final SimpleDateFormat SHORT_DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat DATABASE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String formatTime(Date date) {
		if (date != null) {
			return clone(TIME_FORMAT).format(date);
		} else {
			return "";
		}
	}

	public static Date parseTime(String date) {
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
	
	public static Date parseDate(String date) {
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

	public static String formatShortDate(Date date) {
		if (date != null) {
			return clone(SHORT_DATE_FORMAT).format(date);
		} else {
			return "";
		}
	}
	
	public static String formatDate(Date date) {
        if (date != null) {
            return clone(DATE_FORMAT).format(date);
        } else {
            return "";
        }
    }
	
	public static String formatDateTime(Date date) {
		if (date != null) {
			return clone(DATE_TIME_FORMAT).format(date);
		} else {
			return "";
		}
	}
	
	public static String formatShortDateTime(Date date) {
		if (date != null) {
			return clone(SHORT_DATE_TIME_FORMAT).format(date);
		} else {
			return "";
		}
	}
	
	public static String formatDateForDatabase(Date date) {
		if (date != null) {
			return clone(DATABASE_DATE_FORMAT).format(date);
		} else {
			return "";
		}
	}
	
	private static SimpleDateFormat clone(SimpleDateFormat sdf) {
		return (SimpleDateFormat) sdf.clone();
	}
	
	public static boolean isSameDay(Date firstDate, Date secondDate) {
		Calendar firstCalendar = new GregorianCalendar();
		firstCalendar.setTime(firstDate);
		Calendar secondCalendar = new GregorianCalendar();
		secondCalendar.setTime(secondDate);
		
		return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR) &&
				firstCalendar.get(Calendar.MONTH) == secondCalendar.get(Calendar.MONTH) &&
				firstCalendar.get(Calendar.DAY_OF_MONTH) == secondCalendar.get(Calendar.DAY_OF_MONTH);
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
	
	public static SimpleDateFormat getDateFormat() {
		return clone(DATE_FORMAT);
	}
	
	public static SimpleDateFormat getShortDateFormat() {
		return clone(SHORT_DATE_FORMAT);
	}
	
	public static SimpleDateFormat getShortDateTimeFormat() {
		return clone(SHORT_DATE_TIME_FORMAT);
	}
	
	public static SimpleDateFormat getDateTimeFormat() {
		return clone(DATE_TIME_FORMAT);
	}
	
	/**
	 * Calculate days between the two given dates.
	 */
	public static int getDaysBetween(Date start, Date end) {
		return Days.daysBetween(
				new LocalDate(start.getTime()), 
                new LocalDate(end.getTime())).getDays();
	}
	
	public static Date addDays(Date date, int amountOfDays) {
		return new LocalDate(date).plusDays(amountOfDays).toDate();
	}
	
	public static Date subtractDays(Date date, int amountOfDays) {
		return new LocalDate(date).minusDays(amountOfDays).toDate();
	}
	
	public static Date addSeconds(Date date, int amountOfSeconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, amountOfSeconds);
		return calendar.getTime();
	}
	
	/**
	 * Builds and returns a Calendar object that is suited for date calculations with epi weeks.
	 * The Calendar's date still has to be set after retrieving the object.
	 * 
	 * @return
	 */
	public static Calendar getEpiCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(1);
		calendar.clear(); // this is necessary, because in some old java versions there a problems updating the fields based on the date
		return calendar;
	}
	
	/**
	 * Returns the epi week of the given date according to the Nigerian epi week system, i.e.
	 * the week that contains the 1st of January always is the first epi week of the year, even
	 * if it begins in December.
	 * 
	 * @param date The date to calculate the epi week for
	 * @return The epi week according to the Nigerian epi week system
	 */
	public static EpiWeek getEpiWeek(Date date) {
		Calendar calendar = getEpiCalendar();
		calendar.setTime(date);
		return new EpiWeek(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR));
	}
	
	/**
	 * Returns the epi week for the week before the given date according to the Nigerian epi week 
	 * system, i.e. the week that contains the 1st of January always is the first epi week of the 
	 * year, even if it begins in December.
	 * 
	 * @param date The date to calculate the previous epi week for
	 * @return The previous epi week according to the Nigerian epi week system
	 */
	public static EpiWeek getPreviousEpiWeek(Date date) {
		Calendar calendar = getEpiCalendar();
		calendar.setTime(subtractDays(date, 7));
		return new EpiWeek(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR));
	}
	
	public static EpiWeek getPreviousEpiWeek(EpiWeek epiWeek) {
		Calendar calendar = getEpiCalendar();
		calendar.set(Calendar.YEAR, epiWeek.getYear());
		calendar.set(Calendar.WEEK_OF_YEAR, epiWeek.getWeek());
		return getPreviousEpiWeek(calendar.getTime());
	}
	
	/**
	 * Returns the epi week for the week after the given date according to the Nigerian epi week 
	 * system, i.e. the week that contains the 1st of January always is the first epi week of the 
	 * year, even if it begins in December.
	 * 
	 * @param date The date to calculate the next epi week for
	 * @return The next epi week according to the Nigerian epi week system
	 */
	public static EpiWeek getNextEpiWeek(Date date) {
		Calendar calendar = getEpiCalendar();
		calendar.setTime(addDays(date, 7));
		return new EpiWeek(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR));
	}
	
	public static EpiWeek getNextEpiWeek(EpiWeek epiWeek) {
		Calendar calendar = getEpiCalendar();
		calendar.set(Calendar.YEAR, epiWeek.getYear());
		calendar.set(Calendar.WEEK_OF_YEAR, epiWeek.getWeek());
		return getNextEpiWeek(calendar.getTime());
	}
	
	/**
	 * Returns a Date object that is set to Monday of the given epi week.
	 * 
	 * @param year The year to get the first day of the epi week for
	 * @param week The epi week to get the first day for
	 * @return The first day of the epi week
	 */
	public static Date getEpiWeekStart(EpiWeek epiWeek) {
		Calendar calendar = getEpiCalendar();
		calendar.set(Calendar.YEAR, epiWeek.getYear());
		calendar.set(Calendar.WEEK_OF_YEAR, epiWeek.getWeek());
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 1);
		return calendar.getTime();
	}
	
	/**
	 * Returns a Date object that is set to Sunday of the given epi week.
	 * 
	 * @param year The year to get the last day of the epi week for
	 * @param week The epi week to get the last day for
	 * @return The last day of the epi week
	 */
	public static Date getEpiWeekEnd(EpiWeek epiWeek) {
		Calendar calendar = getEpiCalendar();
		calendar.set(Calendar.YEAR, epiWeek.getYear());
		calendar.set(Calendar.WEEK_OF_YEAR, epiWeek.getWeek());
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * Calculates whether the second epi week starts on a later date than the first one.
	 * 
	 * @param epiWeek The first epi week
	 * @param anotherEpiWeek The second epi week to check for a later beginning
	 * @return True if the second epi week is on a later date, false if not
	 */
	public static boolean isEpiWeekAfter(EpiWeek epiWeek, EpiWeek anotherEpiWeek) {
		Calendar calendar = getEpiCalendar();
		calendar.set(Calendar.YEAR, epiWeek.getYear());
		calendar.set(Calendar.WEEK_OF_YEAR, epiWeek.getWeek());
		
		Calendar secondCalendar = getEpiCalendar();
		secondCalendar.set(Calendar.YEAR, anotherEpiWeek.getYear());
		secondCalendar.set(Calendar.WEEK_OF_YEAR, anotherEpiWeek.getWeek());
		
		return secondCalendar.getTime().after(calendar.getTime());
	}
	
	public static List<Integer> createWeeksList(int year) {
        Calendar calendar = getEpiCalendar();
        calendar.set(year, 0, 1);
        List<Integer> weeksList = new ArrayList<>();
        for (int week = 1; week <= calendar.getActualMaximum(Calendar.WEEK_OF_YEAR); week++) {
            weeksList.add(week);
        }
        return weeksList;
    }
	
	/**
	 * Calculates the start and end dates of the report for the given epi week. 
	 * 
	 * @param now
	 * @param epiWeek The epi week to calculate the dates for
	 * @param weeklyReportDate The date of report for the given epi week, or null if none is available
	 * @param prevWeeklyReportDate The date of report for the week before the given epi week, or null if none is available
	 * @param nextWeeklyReportDate The date of report for the week after the given epi week, or null if none is available
	 * @return An array of size 2, containing the start date at index 0 and the end date at index 1
	 */
	public static Date[] calculateEpiWeekReportStartAndEnd(Date now, EpiWeek epiWeek, Date weeklyReportDate, Date prevWeeklyReportDate, Date nextWeeklyReportDate) {

		Date[] reportStartAndEnd = new Date[2];

		// start date:
		if (prevWeeklyReportDate != null) {
			// .. is the previous report date 
			reportStartAndEnd[0] = prevWeeklyReportDate;
		} else {
			// .. or the start of this week
			reportStartAndEnd[0] = getEpiWeekStart(epiWeek);
		}

		// end date:
		if (weeklyReportDate != null) {
			// .. is the report date
			reportStartAndEnd[1] = weeklyReportDate;
		} else {
			Date epiWeekEnd = getEpiWeekEnd(epiWeek);
			if (now.after(epiWeekEnd)) {
				if (nextWeeklyReportDate == null) {
					if (now.before(DateHelper.addDays(epiWeekEnd, 7))) {
						// we are in the following week -> all reports until now count
						reportStartAndEnd[1] = now;
					} else {
						// we are somewhere in the future - go with the unmodified epi week end
						reportStartAndEnd[1] = epiWeekEnd;
					}
				} else {
					// there is a next report - go with the unmodified epi week end
					reportStartAndEnd[1] = epiWeekEnd;
				}
			} else {
				// .. or the end of this week
				reportStartAndEnd[1] = epiWeekEnd;
			}
		}
		
		return reportStartAndEnd;
	}
	
}
