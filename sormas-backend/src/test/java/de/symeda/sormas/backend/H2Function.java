package de.symeda.sormas.backend;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.util.DateHelper8;

public class H2Function {

	public static float similarity(String a, String b) {
		return a.equalsIgnoreCase(b) ? 1 : 0;
	}

	public static int date_part(String part, Date date) {
		switch (part) {
		case "year":
			return DateHelper8.toLocalDate(date).getYear();
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
}
