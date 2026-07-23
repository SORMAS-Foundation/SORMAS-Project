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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class CronExpressionValidator {

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

	public static final String VALUE_PATTERN = buildValuePattern();

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

	public static boolean isFieldValid(int fieldIndex, String value) {

		if (fieldIndex < 0 || fieldIndex >= FIELD_COUNT || value == null) {
			return false;
		}
		if ("*".equals(value.trim())) {
			return true;
		}
		return Pattern.compile(buildFieldPattern(fieldIndex)).matcher(value.trim()).matches();
	}

	static boolean isWithinBounds(int fieldIndex, String field) {

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

	private static String buildValuePattern() {

		StringBuilder pattern = new StringBuilder("(\\s*|");
		for (int field = 0; field < FIELD_COUNT; field++) {
			if (field > 0) {
				pattern.append(' ');
			}
			pattern.append(buildFieldPattern(field));
		}
		return pattern.append(')').toString();
	}

	private static String buildFieldPattern(int fieldIndex) {

		String value = numberRange(LOWER_BOUNDS[fieldIndex], UPPER_BOUNDS[fieldIndex]);
		String listEntry = value + "(-" + value + ")?";
		String list = "(," + listEntry + ")*";

		if (!allowsIncrement(fieldIndex)) {
			return "(\\*|" + listEntry + list + ")";
		}

		String increment = "(/" + numberRange(1, UPPER_BOUNDS[fieldIndex]) + ")?";
		return "(\\*" + increment + "|" + listEntry + increment + list + ")";
	}

	private static String numberRange(int lowerBound, int upperBound) {

		List<String> alternatives = new ArrayList<>();

		int singleDigitHigh = Math.min(upperBound, 9);
		if (lowerBound <= singleDigitHigh) {
			alternatives.add(
				lowerBound == singleDigitHigh ? String.valueOf(lowerBound) : "[" + lowerBound + "-" + singleDigitHigh + "]");
		}

		List<Integer> fullTens = new ArrayList<>();
		for (int tens = 1; tens <= upperBound / 10; tens++) {
			int unitsLow = Math.max(lowerBound, tens * 10) - tens * 10;
			int unitsHigh = Math.min(upperBound, tens * 10 + 9) - tens * 10;
			if (unitsLow > unitsHigh) {
				continue;
			}
			if (unitsLow == 0 && unitsHigh == 9) {
				fullTens.add(tens);
				continue;
			}
			appendFullTens(fullTens, alternatives);
			alternatives.add(unitsLow == unitsHigh ? "" + tens + unitsLow : "" + tens + "[" + unitsLow + "-" + unitsHigh + "]");
		}
		appendFullTens(fullTens, alternatives);

		return alternatives.size() == 1 ? alternatives.get(0) : "(" + String.join("|", alternatives) + ")";
	}

	private static void appendFullTens(List<Integer> fullTens, List<String> alternatives) {

		if (fullTens.isEmpty()) {
			return;
		}
		int first = fullTens.get(0);
		int last = fullTens.get(fullTens.size() - 1);
		alternatives.add((first == last ? String.valueOf(first) : "[" + first + "-" + last + "]") + "[0-9]");
		fullTens.clear();
	}
}
