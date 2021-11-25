package de.symeda.sormas.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.DateHelper;

public class DateFormatHelper {

	public static String formatLocalDate(Date date) {
		return DateHelper.formatLocalDate(date, I18nProperties.getUserLanguage());
	}

	public static SimpleDateFormat getLocalDateFormat() {
		return DateHelper.getLocalDateFormat(I18nProperties.getUserLanguage());
	}

	public static String formatLocalDateTime(Date date) {
		return DateHelper.formatLocalDateTime(date, I18nProperties.getUserLanguage());
	}

	public static String formatBirthdate(Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY) {
		return DateHelper.formatLocalDate(birthdateDD, birthdateMM, birthdateYYYY, I18nProperties.getUserLanguage());
	}

	public static String getAgeAndBirthdateString(
		Integer age,
		ApproximateAgeType ageType,
		Integer birthdateDD,
		Integer birthdateMM,
		Integer birthdateYYYY) {
		return PersonHelper.getAgeAndBirthdateString(age, ageType, birthdateDD, birthdateMM, birthdateYYYY);
	}

	public static String formatDateInterval(Date startDate, Date endDate) {
		String intervalDate = null;

		String formatedStartDate = DateHelper.formatLocalDate(startDate, I18nProperties.getUserLanguage());
		if (endDate == null) {
			intervalDate = formatedStartDate;
		} else {
			String formatedEndDate = DateHelper.formatLocalDate(endDate, I18nProperties.getUserLanguage());
			intervalDate = String.format("%s - %s", formatedStartDate, formatedEndDate);
		}
		return intervalDate;
	}
}
