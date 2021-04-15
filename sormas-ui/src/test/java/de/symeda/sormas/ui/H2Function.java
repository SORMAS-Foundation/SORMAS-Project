package de.symeda.sormas.ui;

import java.util.Arrays;
import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;

/**
 * When extending this class make sure to also extend {@link AbstractBeanTest#initH2Functions()} and {@link ExtendedH2Dialect}.
 */
public class H2Function {

	public static float similarity(String a, String b) {
		return a.equalsIgnoreCase(b) ? 1 : 0;
	}

	public static boolean similarity_operator(String a, String b) {
		return a.equalsIgnoreCase(b) ? true : false;
	}

	public static String array_to_string(String[] array, String delimiter) {
		return array != null ? String.join(delimiter, Arrays.asList(array)) : null;
	}

	public static double set_limit(Double limit) {
		return limit;
	}

	public static Date date(Date timestamp) {
		return DateHelper.getStartOfDay(timestamp);
	}
}
