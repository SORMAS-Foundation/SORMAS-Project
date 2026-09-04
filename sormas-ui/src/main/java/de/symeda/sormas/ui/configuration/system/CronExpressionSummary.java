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

package de.symeda.sormas.ui.configuration.system;

import java.util.StringJoiner;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.systemconfiguration.CronExpressionValidator;

public final class CronExpressionSummary {

	private static final String[] FIELD_CAPTIONS = {
		Captions.cronFieldSecond,
		Captions.cronFieldMinute,
		Captions.cronFieldHour,
		Captions.cronFieldDayOfMonth,
		Captions.cronFieldMonth,
		Captions.cronFieldDayOfWeek };

	private CronExpressionSummary() {
	}

	public static String describe(String expression) {

		if (CronExpressionValidator.isDisabled(expression)) {
			return I18nProperties.getString(Strings.infoCronSummaryDisabled);
		}
		if (!CronExpressionValidator.isValid(expression)) {
			return structuralReadBack(expression);
		}

		String[] fields = expression.split(" ");
		String second = fields[CronExpressionValidator.SECOND];
		String minute = fields[CronExpressionValidator.MINUTE];
		String hour = fields[CronExpressionValidator.HOUR];
		boolean everyDay = "*".equals(fields[CronExpressionValidator.DAY_OF_MONTH])
			&& "*".equals(fields[CronExpressionValidator.MONTH])
			&& "*".equals(fields[CronExpressionValidator.DAY_OF_WEEK]);

		if (!everyDay || !"0".equals(second)) {
			return structuralReadBack(expression);
		}
		if (minute.startsWith("*/") && "*".equals(hour)) {
			return String.format(I18nProperties.getString(Strings.infoCronSummaryEveryNMinutes), minute.substring(2));
		}
		if ("0".equals(minute) && hour.startsWith("*/")) {
			return String.format(I18nProperties.getString(Strings.infoCronSummaryEveryNHours), hour.substring(2));
		}
		if (isNumber(minute) && "*".equals(hour)) {
			return String.format(I18nProperties.getString(Strings.infoCronSummaryHourlyAt), pad(minute));
		}
		if (isNumber(minute) && isNumber(hour)) {
			return String.format(I18nProperties.getString(Strings.infoCronSummaryDailyAt), pad(hour) + ":" + pad(minute));
		}
		return structuralReadBack(expression);
	}

	private static String structuralReadBack(String expression) {

		String[] fields = expression.trim().split(" ");
		StringJoiner readBack = new StringJoiner(" · ");
		for (int field = 0; field < FIELD_CAPTIONS.length; field++) {
			String value = field < fields.length ? fields[field] : "";
			readBack.add(I18nProperties.getCaption(FIELD_CAPTIONS[field]) + " " + value);
		}
		return readBack.toString();
	}

	private static boolean isNumber(String value) {
		return value.chars().allMatch(Character::isDigit) && !value.isEmpty();
	}

	private static String pad(String value) {
		return value.length() == 1 ? "0" + value : value;
	}
}
