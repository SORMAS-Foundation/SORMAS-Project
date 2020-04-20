package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.utils.DateHelper;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.util.Date;

public class DateFormatHelper {
	public static DateFormat getDateFormat() {
		Language userLanguage = FacadeProvider.getUserFacade().getCurrentUser().getLanguage();

		return DateHelper.getLocalDateFormat(userLanguage);
	}

	public static String formatDate(Date date) {
		Language userLanguage = FacadeProvider.getUserFacade().getCurrentUser().getLanguage();

		return DateHelper.formatLocalDate(date, userLanguage);
	}

	public static String getDateFormatPattern() {
		Language userLanguage = FacadeProvider.getUserFacade().getCurrentUser().getLanguage();

		return DateHelper.getLocalDatePattern(userLanguage);
	}

	public static String buildPeriodString(Date startDate, Date endDate) {
		Language userLanguage = FacadeProvider.getUserFacade().getCurrentUser().getLanguage();

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
}
