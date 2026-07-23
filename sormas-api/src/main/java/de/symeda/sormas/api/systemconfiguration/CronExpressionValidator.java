/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.systemconfiguration;

import java.util.regex.Pattern;

public final class CronExpressionValidator {

	public static final String FIELD_PATTERN = "(\\*|\\*/\\d{1,2}|\\d{1,2}(-\\d{1,2})?(/\\d{1,2})?(,\\d{1,2}(-\\d{1,2})?)*)";
	public static final String VALUE_PATTERN = "(|" + FIELD_PATTERN + "( " + FIELD_PATTERN + "){5})";

	public static final int SECOND = 0;
	public static final int MINUTE = 1;
	public static final int HOUR = 2;
	public static final int DAY_OF_MONTH = 3;
	public static final int MONTH = 4;
	public static final int DAY_OF_WEEK = 5;
	public static final int FIELD_COUNT = 6;

	static final int[] LOWER_BOUNDS = {
		0, 0, 0, 1, 1, 0 };
	static final int[] UPPER_BOUNDS = {
		59, 59, 23, 31, 12, 7 };

	private static final Pattern STRUCTURE = Pattern.compile(VALUE_PATTERN);

	private CronExpressionValidator() {
	}

	public static boolean isDisabled(String expression) {
		return expression == null || expression.trim().isEmpty();
	}

	public static boolean isValid(String expression) {

		if (isDisabled(expression)) {
			return true;
		}
		if (!STRUCTURE.matcher(expression).matches()) {
			return false;
		}

		String[] fields = expression.split(" ");
		for (int field = 0; field < fields.length; field++) {
			if (!isWithinBounds(field, fields[field])) {
				return false;
			}
		}
		return true;
	}

	private static boolean isWithinBounds(int fieldIndex, String field) {

		for (String listEntry : field.split(",")) {
			String[] baseAndIncrement = listEntry.split("/");
			if (baseAndIncrement.length > 1) {
				if (!allowsIncrement(fieldIndex)) {
					return false;
				}
				int increment = Integer.parseInt(baseAndIncrement[1]);
				if (increment < 1 || increment > UPPER_BOUNDS[fieldIndex]) {
					return false;
				}
			}
			String withoutIncrement = baseAndIncrement[0];
			if ("*".equals(withoutIncrement)) {
				continue;
			}
			for (String bound : withoutIncrement.split("-")) {
				int value = Integer.parseInt(bound);
				if (value < LOWER_BOUNDS[fieldIndex] || value > UPPER_BOUNDS[fieldIndex]) {
					return false;
				}
			}
		}
		return true;
	}

	static boolean allowsIncrement(int fieldIndex) {
		return fieldIndex == SECOND || fieldIndex == MINUTE || fieldIndex == HOUR;
	}
}
