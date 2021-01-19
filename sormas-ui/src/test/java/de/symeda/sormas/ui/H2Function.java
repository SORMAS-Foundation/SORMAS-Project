package de.symeda.sormas.ui;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;

public class H2Function {

	public static float similarity(String a, String b) {
		return a.equalsIgnoreCase(b) ? 1 : 0;
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
}
