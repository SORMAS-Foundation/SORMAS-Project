package de.symeda.sormas.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;

public class DateFormatHelper {
    public static String formatLocalDate(Date date) {
        return DateHelper.formatLocalDate(date, ConfigProvider.getUser().getLanguage());
    }

    public static SimpleDateFormat getLocalDateFormat() {
        return DateHelper.getLocalDateFormat(ConfigProvider.getUser().getLanguage());
    }

    public static String formatLocalDateTime(Date date) {
        return DateHelper.formatLocalDateTime(date, ConfigProvider.getUser().getLanguage());
    }

    public static String formatBirthdate(Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY) {
        return PersonHelper.formatBirthdate(birthdateDD, birthdateMM, birthdateYYYY, ConfigProvider.getUser().getLanguage());
    }

    public static String getAgeAndBirthdateString(Integer age, ApproximateAgeType ageType, Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY) {
        return PersonHelper.getAgeAndBirthdateString(age, ageType, birthdateDD, birthdateMM, birthdateYYYY, ConfigProvider.getUser().getLanguage());
    }
}
