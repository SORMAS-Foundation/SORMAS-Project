package de.symeda.sormas.backend;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.backend.vaccination.VaccinationService;

/**
 * When extending this class make sure to also extend {@link AbstractBeanTest#initH2Functions()} and {@link ExtendedH2Dialect}.
 */
public class H2Function {

	public static float similarity(String a, String b) {
		return a.equalsIgnoreCase(b) ? 1 : 0;
	}

	public static long date_part(String part, Date date) {
		switch (part) {
		case "year":
			return UtilDate.toLocalDate(date).getYear();
		case "epoch":
			return date.getTime() / 1000;
		default:
			throw new IllegalArgumentException(part);
		}
	}

	public static int epi_week(Date date) {
		return DateHelper.getEpiWeek(date).getWeek().intValue();
	}

	public static int epi_year(Date date) {
		return DateHelper.getEpiWeek(date).getYear().intValue();
	}

	public static boolean similarity_operator(String a, String b) {
		return a.equalsIgnoreCase(b) ? true : false;
	}

	public static double set_limit(Double limit) {
		return limit;
	}

	public static Date date(Date timestamp) {
		return DateHelper.getStartOfDay(timestamp);
	}

	public static Date timestamp_subtract_14_days(Date timestamp) {
		return DateHelper.subtractDays(timestamp, VaccinationService.REPORT_DATE_RELEVANT_DAYS);
	}

	public static Date at_end_of_day(Date timestamp) {
		return DateHelper.getEndOfDay(timestamp);
	}
}
