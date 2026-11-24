package de.symeda.sormas.api.utils;

public class ValueUtils {

	private ValueUtils() {
	}

	public static boolean anyNonNullOrStringNotEmpty(Object... values) {
		if (values != null) {
			for (final Object val : values) {
				if (isNonNullOrStringNotEmpty(val)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isNonNullOrStringNotEmpty(Object value) {
		return value != null && (!(value instanceof String) || !((String) value).isBlank());
	}

	public static boolean isYes(YesNoUnknown candidate) {
		return candidate == YesNoUnknown.YES;
	}
}
