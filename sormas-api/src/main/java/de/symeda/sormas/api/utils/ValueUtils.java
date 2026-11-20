package de.symeda.sormas.api.utils;

public class ValueUtils {

	private ValueUtils() {
	}

	public static boolean anyNonNullOrStringNotEmpty(Object... values) {
		if (values != null) {
			for (final Object val : values) {
				if (val != null && (!(val instanceof String) || !((String) val).isBlank())) {
					return true;
				}
			}
		}
		return false;
	}
}
