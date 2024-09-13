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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;

import javax.validation.constraints.NotNull;

public final class DateHelper {

	private DateHelper() {
		// Hide Utility Class Constructor
	}

	private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	private static final String DATE_FORMAT_DOTS = "dd.MM.yyyy";
	private static final String DATE_FORMAT_HYPHEN = "dd-MM-yyyy";
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat EXPORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_WITH_MONTH_ABBREVIATION_FORMAT = new SimpleDateFormat("MMM yyyy");

	private static final Set<String> DATE_FORMAT_SEPARATORS = Sets.newHashSet(".", "/", "-");
	private static final Pattern DATE_FORMAT_PATTERN = Pattern.compile("^(.*)([\\.\\-/])(.*)([\\.\\-/])(.*)$");
	public static final String TIME_SEPARATOR = " ";
	private static final List<String> ALLOWED_TIME_FORMATS = Arrays.asList("h:mm a", "HH:mm", "h.mm a", "HH.mm", "H:mm", "H.mm");

	public static SimpleDateFormat getLocalDateFormat(Language language) {
		Language formatLanguage = language != null ? language : I18nProperties.getUserLanguage();
		return new SimpleDateFormat(formatLanguage.getDateFormat(), formatLanguage.getLocale());
	}

	public static String getLocalDatePattern(Language language) {
		return getLocalDateFormat(language).toPattern();
	}

	public static SimpleDateFormat getLocalDateTimeFormat(Language language) {
		return new SimpleDateFormat(language.getDateTimeFormat(), language.getLocale());
	}

	// End of methods to create patterns/date formats that use the system's locale.

	// Date and time formatting

	public static String formatLocalDate(Date date, SimpleDateFormat dateFormat) {

		if (date != null && dateFormat != null) {
			return dateFormat.format(date);
		} else {
			return "";
		}
	}

	public static String formatShortDate(Date date) {

		if (date != null) {
			return clone(SHORT_DATE_FORMAT).format(date);
		} else {
			return "";
		}
	}

	public static String formatLocalDate(Date date, Language language) {

		if (date != null) {
			return getLocalDateFormat(language).format(date);
		} else {
			return "";
		}
	}

	public static String formatLocalDate(Integer dateDD, Integer dateMM, Integer dateYYYY, Language language) {

		if (dateDD == null && dateMM == null && dateYYYY == null) {
			return "";
		} else {
			String birthDate = DateHelper.getLocalDateFormat(language).toPattern();
			birthDate = birthDate.replaceAll("d+", dateDD != null ? dateDD.toString() : "");
			birthDate = birthDate.replaceAll("M+", dateMM != null ? dateMM.toString() : "");
			birthDate = birthDate.replaceAll("y+", dateYYYY != null ? dateYYYY.toString() : "");
			birthDate = birthDate.replaceAll("^[^\\d]*", "").replaceAll("[^\\d]*$", "");

			return birthDate;
		}
	}

	public static String formatLocalDateTime(Date date, Language language) {

		if (date != null) {
			return getLocalDateTimeFormat(language).format(date);
		} else {
			return "";
		}
	}

	public static String formatTime(Date date) {

		if (date != null) {
			return clone(TIME_FORMAT).format(date);
		} else {
			return "";
		}
	}

	// Date and time parsing

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

	public static Date parseDate(String date, SimpleDateFormat dateFormat) {

		if (date != null && dateFormat != null) {
			try {
				return dateFormat.parse(date);
			} catch (ParseException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public static Date parseDateWithException(String date, Language language) throws ParseException {

		return parseDateWithException(date, getAllowedDateFormats(language.getDateFormat()), language.getLocale());
	}

	public static Date parseDateTimeWithException(String date, Language language) throws ParseException {

		final Date result;
		if (!date.contains(TIME_SEPARATOR)) {
			// no separator means no time
			String dateFormat = language.getDateTimeFormat().split(TIME_SEPARATOR)[0];
			result = parseDateWithException(date, getAllowedDateFormats(dateFormat), language.getLocale());
		} else {
			result = parseDateWithException(date, getAllowedDateTimeFormats(language.getDateTimeFormat()), language.getLocale());
		}

		return result;
	}

	private static Date parseDateWithException(String date, List<String> dateFormats, Locale locale) throws ParseException {

		if (date == null) {
			return null;
		}

		Logger logger = LoggerFactory.getLogger(DateHelper.class);
		for (String format : dateFormats) {
			try {
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(format).toFormatter(locale);
				logger.trace("Format: {}, Locale: {}", format, formatter.getLocale());
				TemporalAccessor parsedTemporal = formatter.parse(date);

				final Date result;
				if (parsedTemporal.isSupported(ChronoField.MONTH_OF_YEAR)) {
					if (parsedTemporal.isSupported(ChronoField.DAY_OF_MONTH)) {
						if (parsedTemporal.isSupported(ChronoField.HOUR_OF_DAY)) {
							LocalDateTime temporal = LocalDateTime.from(parsedTemporal);
							result = UtilDate.from(temporal);
						} else {
							LocalDate temporal = LocalDate.from(parsedTemporal);
							result = UtilDate.from(temporal);
						}
					} else {
						YearMonth temporal = YearMonth.from(parsedTemporal);
						result = UtilDate.from(temporal);
					}
				} else {
					LocalTime temporal = LocalTime.from(parsedTemporal);
					result = UtilDate.from(temporal);
				}
				logger.trace("Parse successful. Result: {}", result);
				return result;
			} catch (DateTimeParseException e) {
				logger.trace("Parse failed: {}", e.getMessage());
				// Try next format
			}
		}

		throw new ParseException("Unable to parse date [" + date + "]", 0);
	}

	public static List<String> getAllowedDateFormats(String defaultFormat) {
		final List<String> dateFormats = new ArrayList<>();

		Matcher matcher = DATE_FORMAT_PATTERN.matcher(defaultFormat);
		if (matcher.find()) {
			final List<String> dateFieldsDefault = new ArrayList<>(Arrays.asList(matcher.group(1), matcher.group(3), matcher.group(5)));

			final List<String> dateFieldsYearFormat = new ArrayList<>(dateFieldsDefault.size());
			boolean isFourDigitYear = false;
			for (String dateField : dateFieldsDefault) {
				if (dateField.toLowerCase().startsWith("y")) {
					isFourDigitYear = dateField.length() == 4;
					dateFieldsYearFormat.add(isFourDigitYear ? dateField.substring(0, 2) : dateField + dateField);
				} else {
					dateFieldsYearFormat.add(dateField);
				}
			}

			final List<List<String>> dateFields = new ArrayList<>(dateFieldsDefault.size());
			// take 2 digit year formats first
			if (isFourDigitYear) {
				dateFields.add(dateFieldsYearFormat);
				dateFields.add(dateFieldsDefault);
			} else {
				dateFields.add(dateFieldsDefault);
				dateFields.add(dateFieldsYearFormat);
			}

			String defaultSeparator = matcher.group(2);
			for (List<String> fields : dateFields) {
				dateFormats.add(StringUtils.join(fields, defaultSeparator));

				for (String separator : DATE_FORMAT_SEPARATORS) {
					if (!separator.equals(defaultSeparator)) {
						dateFormats.add(StringUtils.join(fields, separator));
					}
				}
			}
		}

		return dateFormats;
	}

	public static List<String> getAllowedDateTimeFormats(String defaultFormat) {
		String[] dateAndTimeFormat = defaultFormat.split(TIME_SEPARATOR);
		final List<String> dateFormats = getAllowedDateFormats(dateAndTimeFormat[0]);

		List<String> dateTimeFormats = new ArrayList<>();

		for (String dateFormat : dateFormats) {
			dateTimeFormats.add(dateFormat + TIME_SEPARATOR + dateAndTimeFormat[1]);

			for (String timeFormat : ALLOWED_TIME_FORMATS) {
				if (!timeFormat.equals(dateAndTimeFormat[1])) {
					dateTimeFormats.add(dateFormat + TIME_SEPARATOR + timeFormat);
				}
			}
		}

		return dateTimeFormats;
	}

	public static List<String> getDateFields(String dateOrFOrmat) {
		Matcher matcher = DATE_FORMAT_PATTERN.matcher(dateOrFOrmat);

		if (!matcher.matches()) {
			return null;
		}

		return Arrays.asList(matcher.group(1), matcher.group(3), matcher.group(5));
	}

	public static String formatDateForExport(Date date) {

		if (date != null) {
			return clone(EXPORT_DATE_FORMAT).format(date);
		} else {
			return "";
		}
	}

	public static String formatDateWithoutYear(Date date, Language language) {

		if (date != null) {
			return new SimpleDateFormat(language.getDayMonthFormat(), language.getLocale()).format(date);
		} else {
			return "";
		}
	}

	public static String formatDateWithMonthAbbreviation(Date date) {

		if (date != null) {
			return clone(DATE_WITH_MONTH_ABBREVIATION_FORMAT).format(date);
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

		return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR)
			&& firstCalendar.get(Calendar.MONTH) == secondCalendar.get(Calendar.MONTH)
			&& firstCalendar.get(Calendar.DAY_OF_MONTH) == secondCalendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Returns a Date at 0 h, 0 m, 0 s
	 *
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 */
	public static Date getDateZero(int year, int month, int day) {

		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month, day, 0, 0, 0);
		return calendar.getTime();
	}

	/**
	 * Returns the time with a dummy date
	 *
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
	 * Returns a list for days in the specified month and year. The list is empty
	 * when the month is null and contains default values (i.e. 29 days in February)
	 * when the year is null, but a month is provided. Months should start from 1,
	 * i.e. January is 1 and December is 12.
	 */
	public static List<Integer> getDaysInMonth(Integer month, Integer year) {

		if (month == null) {
			return new ArrayList<>();
		}

		Calendar calendar = new GregorianCalendar();
		if (year == null) {
			// Be tolerant and assume that it might be a leap year
			calendar.set(Calendar.YEAR, 2020);
		} else {
			calendar.set(Calendar.YEAR, year);
		}
		// January is 0 in Calendar API
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		Integer daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		List<Integer> x = new ArrayList<Integer>();
		for (int i = 1; i <= daysInMonth; i++) {
			x.add(i);
		}
		return x;
	}

	/**
	 * Returns a list of months in years (1-12)
	 *
	 * @return
	 */
	public static List<Integer> getMonthsInYear() {

		List<Integer> x = new ArrayList<Integer>();
		for (int i = 1; i <= 12; i++) {
			x.add(i);
		}
		return x;
	}

	public static List<Integer> getYearsToNow(int startingYear) {

		List<Integer> x = new ArrayList<Integer>();
		Calendar now = new GregorianCalendar();
		for (int i = startingYear; i <= now.get(Calendar.YEAR); i++) {
			x.add(i);
		}
		return x;
	}

	/**
	 * Returns a list of years from 1900 to now.
	 *
	 * @return
	 */
	public static List<Integer> getYearsToNow() {
		return getYearsToNow(1900);
	}

	/**
	 * Calculate full days between the two given dates.
	 */
	public static int getFullDaysBetween(Date start, Date end) {
		return (int) ChronoUnit.DAYS.between(UtilDate.toLocalDate(start), UtilDate.toLocalDate(end));
	}

	/**
	 * Calculate days between the two given dates. This includes both the start and
	 * end dates, so a one-week period from Monday to Sunday will return 7.
	 */
	public static int getDaysBetween(Date start, Date end) {
		return (int) ChronoUnit.DAYS.between(UtilDate.toLocalDate(start), UtilDate.toLocalDate(end)) + 1;
	}

	/**
	 * List days between the two given dates. This includes both the start and
	 * end dates.
	 */
	public static List<Date> listDaysBetween(Date start, Date end) {

		int numOfDaysBetween = getDaysBetween(start, end);
		List<Date> dates = new ArrayList<Date>();
		for (int dayNum = 0; dayNum < numOfDaysBetween; dayNum++) {
			dates.add(addDays(start, dayNum));
		}

		return dates;
	}

	/**
	 * Calculate weeks between the two given dates. This includes both the start and
	 * end dates, so week 1 to week 4 of a year will return 4.
	 */
	public static int getWeeksBetween(Date start, Date end) {
		return (int) ChronoUnit.WEEKS.between(UtilDate.toLocalDate(start), UtilDate.toLocalDate(end)) + 1;
	}

	/**
	 * List weeks between the two given dates. This includes both the start and
	 * end dates.
	 */
	public static List<Date> listWeeksBetween(Date start, Date end) {

		int numOfWeeksBetween = getWeeksBetween(start, end);
		List<Date> dates = new ArrayList<Date>();
		for (int weekNum = 0; weekNum < numOfWeeksBetween; weekNum++) {
			dates.add(addWeeks(start, weekNum));
		}

		return dates;
	}

	/**
	 * Calculate months between the two given dates. This includes both the start
	 * and end dates, so a one-year period from January to December will return 12.
	 */
	public static int getMonthsBetween(Date start, Date end) {
		return (int) ChronoUnit.MONTHS.between(UtilDate.toLocalDate(start), UtilDate.toLocalDate(end)) + 1;
	}

	/**
	 * List months between the two given dates. This includes both the start and
	 * end dates.
	 */
	public static List<Date> listMonthsBetween(Date start, Date end) {

		int numOfMonthsBetween = getMonthsBetween(start, end);
		List<Date> dates = new ArrayList<Date>();
		for (int monthNum = 0; monthNum < numOfMonthsBetween; monthNum++) {
			dates.add(addMonths(start, monthNum));
		}

		return dates;
	}

	/**
	 * Calculate years between the two given dates.
	 */
	public static int getYearsBetween(Date start, Date end) {
		return (int) ChronoUnit.YEARS.between(UtilDate.toLocalDate(start), UtilDate.toLocalDate(end));
	}

	/**
	 * List years between the two given dates. This includes both the start and
	 * end dates.
	 */
	public static List<Date> listYearsBetween(Date start, Date end) {

		int numOfYearsBetween = getYearsBetween(start, end);
		List<Date> dates = new ArrayList<Date>();
		for (int yearNum = 0; yearNum <= numOfYearsBetween; yearNum++) {
			dates.add(addYears(start, yearNum));
		}

		return dates;
	}

	public static Date addDays(Date date, int amountOfDays) {
		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)).plusDays(amountOfDays));
	}

	public static Date subtractDays(Date date, int amountOfDays) {
		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)).minusDays(amountOfDays));
	}

	public static Date addWeeks(Date date, int amountOfWeeks) {
		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)).plusWeeks(amountOfWeeks));
	}

	public static Date subtractWeeks(Date date, int amountOfWeeks) {
		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)).minusWeeks(amountOfWeeks));
	}

	public static Date addMonths(Date date, int amountOfMonths) {
		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)).plusMonths(amountOfMonths));
	}

	public static Date subtractMonths(Date date, int amountOfMonths) {
		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)).minusMonths(amountOfMonths));
	}

	public static Date addYears(Date date, int amountOfYears) {
		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)).plusYears(amountOfYears));
	}

	public static Date subtractYears(Date date, int amountOfYears) {
		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)).minusYears(amountOfYears));
	}

	public static Date addSeconds(Date date, int amountOfSeconds) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, amountOfSeconds);
		return calendar.getTime();
	}

	/**
	 * Mimics previous behaviour before switching to java.time.
	 */
	private static Date handleNull(Date date) {

		return Optional.ofNullable(date).orElse(UtilDate.now());
	}

	public static Date getStartOfDay(Date date) {

		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)));
	}

	public static Date getEndOfDay(Date date) {

		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)).plusDays(1).atStartOfDay().minus(1, ChronoUnit.MILLIS));
	}

	public static Date getStartOfWeek(Date date) {

		LocalDate localDate = UtilDate.toLocalDate(handleNull(date));
		return UtilDate.from(localDate.minusDays(localDate.getDayOfWeek().getValue() - 1));
	}

	public static Date getEndOfWeek(Date date) {

		LocalDate localDate = UtilDate.toLocalDate(handleNull(date));
		return getEndOfDay(UtilDate.from(localDate.plusDays(DayOfWeek.SUNDAY.getValue() - localDate.getDayOfWeek().getValue())));
	}

	public static Date getStartOfMonth(Date date) {

		return UtilDate.from(UtilDate.toLocalDate(handleNull(date)).withDayOfMonth(1));
	}

	public static Date getEndOfMonth(Date date) {

		LocalDate localDate = UtilDate.toLocalDate(handleNull(date));
		return getEndOfDay(UtilDate.from(localDate.plusMonths(1).withDayOfMonth(1).minusDays(1)));
	}

	public static Date getStartOfYear(Date date) {

		return UtilDate.of(UtilDate.toLocalDate(handleNull(date)).getYear(), Month.JANUARY, 1);
	}

	public static Date getEndOfYear(Date date) {

		return getEndOfDay(UtilDate.of(UtilDate.toLocalDate(handleNull(date)).getYear(), Month.DECEMBER, 31));
	}

	public static boolean isBetween(Date date, Date start, Date end) {
		//sometimes date.equals(start) returns false but start.equals(date) returns true
		return (date.equals(start) || start.equals(date) || date.after(start)) && (date.equals(end) || end.equals(date) || date.before(end));
	}

	/**
	 * Checks on day-base - not time!
	 * @return false if one or both params are null
	 */
	public static boolean isDateAfter(Date thisDate, Date other) {
		if (thisDate == null || other == null) {
			return false;
		}
		return UtilDate.toLocalDate(thisDate).isAfter(UtilDate.toLocalDate(other));
	}

	/**
	 * Checks on day-base - not time!
	 * @return false if one or both params are null
	 */
	public static boolean isDateBefore(Date thisDate, Date other) {
		if (thisDate == null || other == null) {
			return false;
		}
		return UtilDate.toLocalDate(thisDate).isBefore(UtilDate.toLocalDate(other));
	}

	/**
	 * Builds and returns a Calendar object that is suited for date calculations
	 * with epi weeks. The Calendar's date still has to be set after retrieving the
	 * object.
	 *
	 * @return
	 */
	public static Calendar getEpiCalendar() {

		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		// Makes sure that the 1st of January is always in week 1
		calendar.setMinimalDaysInFirstWeek(1);
		// This is necessary because some old Java versions have problems
		// updating the fields based on the date
		calendar.clear();
		return calendar;
	}

	/**
	 * Returns the epi week of the given date according to the Nigerian epi week
	 * system, i.e. the week that contains the 1st of January always is the first
	 * epi week of the year, even if it begins in December.
	 *
	 * @param date
	 *            The date to calculate the epi week for
	 * @return The epi week according to the Nigerian epi week system
	 */
	public static EpiWeek getEpiWeek(Date date) {

		Calendar calendar = getEpiCalendar();
		calendar.setTime(date);
		return getEpiWeekWithCorrectYear(calendar);
	}

	public static EpiWeek getEpiWeekYearBefore(EpiWeek epiWeek) {

		Calendar calendar = getEpiCalendar();
		calendar.set(Calendar.YEAR, epiWeek.getYear() - 1);
		calendar.set(Calendar.WEEK_OF_YEAR, epiWeek.getWeek());
		return getEpiWeek(calendar.getTime());
	}

	/**
	 * Returns the epi week for the week before the given date according to the
	 * Nigerian epi week system, i.e. the week that contains the 1st of January
	 * always is the first epi week of the year, even if it begins in December.
	 *
	 * @param date
	 *            The date to calculate the previous epi week for
	 * @return The previous epi week according to the Nigerian epi week system
	 */
	public static EpiWeek getPreviousEpiWeek(Date date) {

		Calendar calendar = getEpiCalendar();
		calendar.setTime(subtractDays(date, 7));
		return getEpiWeekWithCorrectYear(calendar);
	}

	public static EpiWeek getPreviousEpiWeek(EpiWeek epiWeek) {

		Calendar calendar = getEpiCalendar();
		calendar.set(Calendar.YEAR, epiWeek.getYear());
		calendar.set(Calendar.WEEK_OF_YEAR, epiWeek.getWeek());
		return getPreviousEpiWeek(calendar.getTime());
	}

	/**
	 * Returns the epi week for the week after the given date according to the
	 * Nigerian epi week system, i.e. the week that contains the 1st of January
	 * always is the first epi week of the year, even if it begins in December.
	 *
	 * @param date
	 *            The date to calculate the next epi week for
	 * @return The next epi week according to the Nigerian epi week system
	 */
	public static EpiWeek getNextEpiWeek(Date date) {

		Calendar calendar = getEpiCalendar();
		calendar.setTime(addDays(date, 7));
		return getEpiWeekWithCorrectYear(calendar);
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
	 * @param sYear
	 *            The year to get the first day of the epi week for
	 * @param week
	 *            The epi week to get the first day for
	 * @return The first day of the epi week
	 */
	public static Date getEpiWeekStart(EpiWeek epiWeek) {

		if (epiWeek == null) {
			return null;
		}

		Calendar calendar = getEpiCalendar();
		calendar.set(Calendar.YEAR, epiWeek.getYear());
		calendar.set(Calendar.WEEK_OF_YEAR, epiWeek.getWeek());
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * Returns a Date object that is set to Sunday of the given epi week.
	 *
	 * @param sYear
	 *            The year to get the last day of the epi week for
	 * @param week
	 *            The epi week to get the last day for
	 * @return The last day of the epi week
	 */
	public static Date getEpiWeekEnd(EpiWeek epiWeek) {

		if (epiWeek == null) {
			return null;
		}

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
	 * Calculates whether the second epi week starts on a later date than the first
	 * one.
	 *
	 * @param epiWeek
	 *            The first epi week
	 * @param anotherEpiWeek
	 *            The second epi week to check for a later beginning
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

	/**
	 * @return The same {@link EpiWeek} within the the given {@code options} (first matching week number),
	 *         {@code null} if no option matches.
	 */
	public static EpiWeek getSameEpiWeek(EpiWeek epiWeek, List<EpiWeek> options) {

		EpiWeek result = null;
		if (epiWeek != null && CollectionUtils.isNotEmpty(options)) {
			for (EpiWeek option : options) {
				if (epiWeek.getWeek().equals(option.getWeek())) {
					result = option;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Returns the maximum possible number of EpiWeeks in a year
	 */
	public static int getMaximumEpiWeekNumber() {
		return 53;
	}

	/**
	 * Creates a list of EpiWeeks for the whole given year.
	 */
	public static List<EpiWeek> createEpiWeekList(int year) {

		Calendar calendar = getEpiCalendar();
		calendar.set(year, 0, 1);
		List<EpiWeek> epiWeekList = new ArrayList<>();
		for (int week = 1; week <= calendar.getActualMaximum(Calendar.WEEK_OF_YEAR); week++) {
			epiWeekList.add(new EpiWeek(year, week));
		}
		return epiWeekList;
	}

	/**
	 * Creates a list of EpiWeeks, starting with the given week in the given year,
	 * going back exactly one year.
	 */
	public static List<EpiWeek> createEpiWeekList(int year, int week) {

		Calendar calendar = getEpiCalendar();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.WEEK_OF_YEAR, week);

		Calendar lastYearCalendar = getEpiCalendar();
		lastYearCalendar.set(Calendar.YEAR, year - 1);
		lastYearCalendar.set(Calendar.WEEK_OF_YEAR, week);

		List<EpiWeek> epiWeekList = new ArrayList<>();
		for (int epiWeek = lastYearCalendar.get(Calendar.WEEK_OF_YEAR);
			epiWeek <= lastYearCalendar.getActualMaximum(Calendar.WEEK_OF_YEAR);
			epiWeek++) {
			epiWeekList.add(new EpiWeek(year - 1, epiWeek));
		}
		for (int epiWeek = 1; epiWeek <= calendar.get(Calendar.WEEK_OF_YEAR); epiWeek++) {
			epiWeekList.add(new EpiWeek(year, epiWeek));
		}

		return epiWeekList;
	}

	public static List<EpiWeek> createEpiWeekListFromInterval(EpiWeek startEpiweek, EpiWeek endEpiweek) {
		List<EpiWeek> epiWeekList = new ArrayList<>();
		int startYear = startEpiweek.getYear();
		int endYear = endEpiweek.getYear();

		if (endYear != startYear) {
			Calendar startYearCalendar = getEpiCalendar();
			startYearCalendar.set(Calendar.YEAR, startYear);
			startYearCalendar.set(Calendar.WEEK_OF_YEAR, startEpiweek.getWeek());

			Calendar endYearCalendar = getEpiCalendar();
			endYearCalendar.set(Calendar.YEAR, endYear);
			endYearCalendar.set(Calendar.WEEK_OF_YEAR, endEpiweek.getWeek());

			for (int epiWeek = startYearCalendar.get(Calendar.WEEK_OF_YEAR);
				epiWeek <= startYearCalendar.getActualMaximum(Calendar.WEEK_OF_YEAR);
				epiWeek++) {
				epiWeekList.add(new EpiWeek(startYear, epiWeek));
			}

			for (int year = startYear + 1; year < endYear; year++) {
				epiWeekList.addAll(createEpiWeekList(year));
			}

			for (int epiWeek = 1; epiWeek <= endYearCalendar.get(Calendar.WEEK_OF_YEAR); epiWeek++) {
				epiWeekList.add(new EpiWeek(endYear, epiWeek));
			}

		} else {
			for (int epiWeek = startEpiweek.getWeek(); epiWeek <= endEpiweek.getWeek(); epiWeek++) {
				epiWeekList.add(new EpiWeek(startYear, epiWeek));
			}
		}

		return epiWeekList;
	}

	private static EpiWeek getEpiWeekWithCorrectYear(Calendar calendar) {

		// Year has to be manually increased for week 1 of the next year because Calendar chooses the year
		// of the actual date; e.g., the 31st of December 2018 is technically in 2018, but already in epi week
		// 1 of 2019, which is why the year has to be manually increased.
		if (calendar.get(Calendar.WEEK_OF_YEAR) == 1 && calendar.get(Calendar.MONTH) == 11) {
			return new EpiWeek(calendar.get(Calendar.YEAR) + 1, calendar.get(Calendar.WEEK_OF_YEAR));
		} else {
			return new EpiWeek(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR));
		}
	}

	/**
	 * Calculates the start and end dates of the report for the given epi week.
	 *
	 * @param now
	 * @param epiWeek
	 *            The epi week to calculate the dates for
	 * @param weeklyReportDate
	 *            The date of report for the given epi week, or
	 *            null if none is available
	 * @param prevWeeklyReportDate
	 *            The date of report for the week before the given
	 *            epi week, or null if none is available
	 * @param nextWeeklyReportDate
	 *            The date of report for the week after the given
	 *            epi week, or null if none is available
	 * @return An array of size 2, containing the start date at index 0 and the end
	 *         date at index 1
	 */
	public static Date[] calculateEpiWeekReportStartAndEnd(
		Date now,
		EpiWeek epiWeek,
		Date weeklyReportDate,
		Date prevWeeklyReportDate,
		Date nextWeeklyReportDate) {

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

	/**
	 * If the century is positive and has only two digits, it is set to a fitting century relative to the current point in time.
	 * <p>
	 * Use case: Correcting a two-digit date entered by the user.
	 *
	 * @param value
	 *            The date to (possibly) correct
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
	 * @param value
	 *            The date to (possibly) correct
	 * @param reference
	 *            The current date as reference
	 * @return The entered date, possibly with corrected century
	 */
	public static Date toCorrectCentury(Date value, Date reference) {

		if (value == null || reference == null) {
			return null;
		}

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

	public static Timestamp toTimestampUpper(Date date) {
		Timestamp timestamp = new Timestamp(date.getTime());
		timestamp.setNanos(timestamp.getNanos() + 999999);
		return timestamp;
	}

	private static final int MILLISECONDS_PER_SECOND = 1000;

	/**
	 * @return Current time in milliseconds.
	 */
	public static long now() {

		return System.currentTimeMillis();
	}

	/**
	 * Redundant to {@link #now()} to propose {@code startTime} as variable name.
	 *
	 * @return Current time in milliseconds.
	 */
	public static long startTime() {

		return now();
	}

	/**
	 * @return Duration from {@code startTimeMillies} to now in milliseconds.
	 */
	public static long durationMillies(long startTimeMilliseconds) {
		return now() - startTimeMilliseconds;
	}

	/**
	 * @return Duration from {@code startTimeMillies} to now in seconds.
	 */
	public static long durationSeconds(long startTimeMilliseconds) {
		return toSeconds(durationMillies(startTimeMilliseconds));
	}

	/**
	 * @return Converts milliseconds to completed seconds.
	 */
	public static long toSeconds(long milliseconds) {
		return milliseconds / MILLISECONDS_PER_SECOND;
	}

	/**
	 * Find the latest date between 2 dates
	 * 
	 * @param date1
	 *            the first date
	 * @param date2
	 *            the second date
	 * @return the latest date between 2 dates. If any is null, the other is returned. If both are null, null is returned.
	 */
	public static Date getLatestDate(Date date1, Date date2) {
		if (ObjectUtils.allNotNull(date1, date2)) {
			if (date1.after(date2)) {
				return date1;
			} else {
				return date2;
			}
		} else {
			return ObjectUtils.firstNonNull(date1, date2);
		}
	}

	public static boolean isStartDateBeforeEndDate(Date startDate, Date endDate) {
		return startDate != null && endDate != null && endDate.before(startDate);
	}

	public static class ParsedDateFormat {

		private String day;
		private String month;
		private String year;
		private String separator;

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public String getSeparator() {
			return separator;
		}

		public void setSeparator(String separator) {
			this.separator = separator;
		}
	}
}
