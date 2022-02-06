package de.symeda.sormas.api.utils;

import java.text.DateFormat;
import java.util.Date;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;

public class DateFormatHelper {

	public static DateFormat getDateFormat() {
		return DateHelper.getLocalDateFormat(I18nProperties.getUserLanguage());
	}

	public static String formatDate(Date date) {
		return DateHelper.formatLocalDate(date, I18nProperties.getUserLanguage());
	}

	public static String formatDate(Integer dateDD, Integer dateMM, Integer dateYYYY) {
		return DateHelper.formatLocalDate(dateDD, dateMM, dateYYYY, I18nProperties.getUserLanguage());
	}

	public static String getDateFormatPattern() {
		return DateHelper.getLocalDatePattern(I18nProperties.getUserLanguage());
	}

	public static String buildPeriodString(Date startDate, Date endDate) {

		Language userLanguage = I18nProperties.getUserLanguage();

		String startDateString = startDate != null ? DateHelper.formatLocalDate(startDate, userLanguage) : "?";
		String endDateString = endDate != null ? DateHelper.formatLocalDate(endDate, userLanguage) : "?";
		if (startDate == null && endDate == null) {
			return "";
		} else if (startDate != null && endDate != null && DateHelper.isSameDay(startDate, endDate)) {
			return startDateString;
		} else {
			return startDateString + " - " + endDateString;
		}
	}

	public static String formatLocalDateTime(Date dateTime) {
		return DateHelper.formatLocalDateTime(dateTime, I18nProperties.getUserLanguage());
	}

	public static String formatLocalDate(Date dateTime) {
		return DateHelper.formatLocalDate(dateTime, I18nProperties.getUserLanguage());
	}

	public static String buildPeriodDateTimeString(Date startDate, Date endDate) {

		Language userLanguage = I18nProperties.getUserLanguage();

		String startDateString = startDate != null ? DateHelper.formatLocalDateTime(startDate, userLanguage) : "?";
		String endDateString = endDate != null ? DateHelper.formatLocalDateTime(endDate, userLanguage) : "?";
		if (startDate == null && endDate == null) {
			return "";
		} else {
			return startDateString + " - " + endDateString;
		}
	}
}
