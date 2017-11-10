package de.symeda.sormas.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
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
	
	public static List<EpiWeek> createEpiWeekList(int year) {
        Calendar calendar = getEpiCalendar();
        calendar.set(year, 0, 1);
        List<EpiWeek> epiWeekList = new ArrayList<>();
        for (int week = 1; week <= calendar.getActualMaximum(Calendar.WEEK_OF_YEAR); week++) {
            epiWeekList.add(new EpiWeek(year, week));
        }
        return epiWeekList;
    }
	
	public static List<Integer> createIntegerEpiWeeksList(int year) {
		Calendar calendar = getEpiCalendar();
        calendar.set(year, 0, 1);
        List<Integer> epiWeekList = new ArrayList<>();
        for (int week = 1; week <= calendar.getActualMaximum(Calendar.WEEK_OF_YEAR); week++) {
            epiWeekList.add(week);
        }
        return epiWeekList;
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
	
	private static Pattern COMPLETE_DATE_PATTERN = Pattern.compile("(([012]?\\d)|30|31)\\/((0?\\d)|10|11|12)\\/((18|19|20|21)?\\d\\d)");
	private static Pattern DAY_MONTH_DATE_PATTERN = Pattern.compile("(([012]?\\d)|30|31)\\/((0?\\d)|10|11|12)\\/");
	private static Pattern MONTH_YEAR_DATE_PATTERN = Pattern.compile("((0?\\d)|10|11|12)\\/((18|19|20|21)?\\d\\d)");
	private static Pattern MONTH_DATE_PATTERN = Pattern.compile("((0?\\d)|10|11|12)\\/");
	private static Pattern DAY_DATE_PATTERN = Pattern.compile("(([012]?\\d)|30|31)\\/");
	private static Pattern YEAR_DATE_PATTERN = Pattern.compile("((18|19|20|21)?\\d\\d)");

	private static Pattern DAY_MONTH_PREFIX_DATE_PATTERN = Pattern.compile("(([012]?\\d)|30|31)\\/((0?\\d)|10|11|12)\\/?");
	private static Pattern MONTH_PREFIX_DATE_PATTERN = Pattern.compile("((0?\\d)|10|11|12)\\/?");
	private static Pattern DAY_PREFIX_DATE_PATTERN = Pattern.compile("(([012]?\\d)|30|31)\\/?");
	
	/**
	 *requries joda-time
	 * 
	 * <table>
	 * <tr><td>90      </td><td>-&gt;</td><td>[1/1/1990, 1/1/1991)</td></tr>
	 * <tr><td>08      </td><td>-&gt;</td><td>[1/1/2008, 1/1/2009)</td></tr>
	 * <tr><td>1830    </td><td>-&gt;</td><td>[1/1/1830, 1/1/1831)</td></tr>
	 * <tr><td>3.01    </td><td>-&gt;</td><td>[1/3/2001, 1/4/2001)</td></tr>
	 * <tr><td>3.      </td><td>-&gt;</td><td>[1/3/THIS_YEAR, 1/4/THIS_YEAR)</td></tr>
	 * <tr><td>3.4.2012</td><td>-&gt;</td><td>[3/4/2012, 4/4/2012)</td></tr>
	 * <tr><td>3.4.    </td><td>-&gt;</td><td>[3/4/THIS_YEAR, 4/4/THIS_YEAR)</td></tr>
	 * </table>
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public static Date[] findDateBounds(String value){
		
		if (value==null  || value.length() < 2)
			return null;

		int day = -1;
		int month = -1;
		int year = -1;
		
		Matcher matcher = COMPLETE_DATE_PATTERN.matcher(value);
		if (matcher.matches()) {
			day = Integer.parseInt(matcher.group(1));
			month = Integer.parseInt(matcher.group(3));
			year = Integer.parseInt(matcher.group(5));
		} 
		else {
			matcher = DAY_MONTH_DATE_PATTERN.matcher(value);
			if (matcher.matches()) {
				day = Integer.parseInt(matcher.group(1));
				month = Integer.parseInt(matcher.group(3));
			} 
			else {
				matcher = MONTH_YEAR_DATE_PATTERN.matcher(value);
				if (matcher.matches()) {
					month = Integer.parseInt(matcher.group(1));
					year = Integer.parseInt(matcher.group(3));
				} 
				else {
					matcher = MONTH_DATE_PATTERN.matcher(value);
					if (matcher.matches()) {
						month = Integer.parseInt(matcher.group(1));
					} 
					else {
						matcher = DAY_DATE_PATTERN.matcher(value);
						if (matcher.matches()) {
							day = Integer.parseInt(matcher.group(1));
						} 
						else {
							matcher = YEAR_DATE_PATTERN.matcher(value);
							if (matcher.matches()) {
								year = Integer.parseInt(matcher.group(1));
							} 
							else
								return null;
						}
					}
				}
			}
		}

		int thisYear = DateTime.now().year().get();
		if (year == -1) {
			year = thisYear;
		} 
		else if (year < 100) {
			int thisYearDigits = thisYear % 100;
			int thisCentury = thisYear - thisYearDigits;
			if (year < thisYearDigits + 20)
				year += thisCentury;
			else
				year += thisCentury - 100;
		}

		LocalDate start = new LocalDate(year, 1, 1);
		LocalDate end = new LocalDate(year, 1, 1);

		if (month == -1)
			end = end.plusMonths(11);
		else {
			start = start.plusMonths(month-1);
			end = end.plusMonths(month-1);
		}
		if (day == -1) {
			end = end.plusMonths(1);
		} else {
			start = start.plusDays(day-1);
			end = end.plusDays(day);
		}

		return new Date[] { start.toDate(), end.toDate() };
	}
	
	/**
	 * Ergänzt findDateBounds um die Möglichkeit nach einem Datum unabhängig vom Jahr zu suchen
	 * @param value
	 * @return { day, month } - eins von beiden kann null sein
	 */
	public static Integer[] findDatePrefix(String value){

		Integer day = null;
		Integer month = null;

		Matcher matcher = DAY_MONTH_PREFIX_DATE_PATTERN.matcher(value);
		if (matcher.matches()) {
			day = Integer.parseInt(matcher.group(1));
			month = Integer.parseInt(matcher.group(3));
		}
		else {
			matcher = MONTH_PREFIX_DATE_PATTERN.matcher(value);
			if (matcher.matches()) {
				month = Integer.parseInt(matcher.group(1));
			}
			else {
				matcher = DAY_PREFIX_DATE_PATTERN.matcher(value);
				if (matcher.matches()) {
					day = Integer.parseInt(matcher.group(1));
				}
				else
					return null;
			}
		}

		return new Integer[]{ day, month };
	}
}
