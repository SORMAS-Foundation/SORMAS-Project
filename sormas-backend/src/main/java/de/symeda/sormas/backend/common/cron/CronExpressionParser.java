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

package de.symeda.sormas.backend.common.cron;

import java.util.regex.Pattern;

import javax.ejb.ScheduleExpression;

public final class CronExpressionParser {

	public static final String FIELD_PATTERN = "(\\*|\\*/\\d{1,2}|\\d{1,2}(-\\d{1,2})?(/\\d{1,2})?(,\\d{1,2}(-\\d{1,2})?)*)";
	public static final String VALUE_PATTERN = "(|" + FIELD_PATTERN + "( " + FIELD_PATTERN + "){5})";

	private static final Pattern STRUCTURE = Pattern.compile(VALUE_PATTERN);

	private static final int SECOND = 0;
	private static final int MINUTE = 1;
	private static final int HOUR = 2;
	private static final int DAY_OF_MONTH = 3;
	private static final int MONTH = 4;
	private static final int DAY_OF_WEEK = 5;

	private static final int[] LOWER_BOUNDS = {
		0, 0, 0, 1, 1, 0 };
	private static final int[] UPPER_BOUNDS = {
		59, 59, 23, 31, 12, 7 };

	private CronExpressionParser() {
	}

	public static boolean isDisabled(String expression) {
		return expression == null || expression.trim().isEmpty();
	}

	public static boolean isValid(String expression) {
		return isDisabled(expression) || toScheduleExpression(expression) != null;
	}

	public static ScheduleExpression parse(String expression) {

		ScheduleExpression parsed = toScheduleExpression(expression);
		if (parsed == null) {
			throw new IllegalArgumentException("Not a valid cron expression: [" + expression + "]");
		}
		return parsed;
	}

	private static ScheduleExpression toScheduleExpression(String expression) {

		if (expression == null || !STRUCTURE.matcher(expression).matches() || expression.isEmpty()) {
			return null;
		}

		String[] fields = expression.split(" ");
		for (int field = 0; field < fields.length; field++) {
			if (!isWithinBounds(fields[field], LOWER_BOUNDS[field], UPPER_BOUNDS[field])) {
				return null;
			}
		}

		return new ScheduleExpression().second(fields[SECOND])
			.minute(fields[MINUTE])
			.hour(fields[HOUR])
			.dayOfMonth(fields[DAY_OF_MONTH])
			.month(fields[MONTH])
			.dayOfWeek(fields[DAY_OF_WEEK]);
	}

	private static boolean isWithinBounds(String field, int lowerBound, int upperBound) {

		for (String listEntry : field.split(",")) {
			String withoutIncrement = listEntry.split("/")[0];
			if ("*".equals(withoutIncrement)) {
				continue;
			}
			for (String bound : withoutIncrement.split("-")) {
				int value = Integer.parseInt(bound);
				if (value < lowerBound || value > upperBound) {
					return false;
				}
			}
		}
		return true;
	}
}
