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

import javax.ejb.ScheduleExpression;

import de.symeda.sormas.api.systemconfiguration.CronExpressionValidator;

public final class CronExpressionParser {

	private CronExpressionParser() {
	}

	public static ScheduleExpression parse(String expression) {

		if (CronExpressionValidator.isDisabled(expression) || !CronExpressionValidator.isValid(expression)) {
			throw new IllegalArgumentException("Not a valid cron expression: [" + expression + "]");
		}

		String[] fields = expression.split(" ");
		return new ScheduleExpression().second(fields[CronExpressionValidator.SECOND])
			.minute(fields[CronExpressionValidator.MINUTE])
			.hour(fields[CronExpressionValidator.HOUR])
			.dayOfMonth(fields[CronExpressionValidator.DAY_OF_MONTH])
			.month(fields[CronExpressionValidator.MONTH])
			.dayOfWeek(fields[CronExpressionValidator.DAY_OF_WEEK]);
	}
}
