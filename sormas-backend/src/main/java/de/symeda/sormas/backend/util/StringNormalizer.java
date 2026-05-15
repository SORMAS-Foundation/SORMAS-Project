package de.symeda.sormas.backend.util;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

public class StringNormalizer {

	private StringNormalizer() {
	}

	/**
	 * Meant to allow 'loose'-matching between strings, by unifying/removing:
	 * - case (to lower)
	 * - leading and trailing whitespaces
	 * - removing accents.
	 * 
	 * @param value
	 *            that must be normalized
	 * @return null if value is null otherwise normalized string.
	 */
	public static String normalize(@Nullable String value) {
		if (null == value) {
			return null;
		}
		return StringUtils.stripAccents(StringUtils.normalizeSpace(value.toLowerCase()));
	}
}
