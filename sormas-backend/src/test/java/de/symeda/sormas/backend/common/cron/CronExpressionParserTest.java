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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ejb.ScheduleExpression;

import org.junit.jupiter.api.Test;

public class CronExpressionParserTest {

	@Test
	public void parsesEveryFieldInOrder() {

		ScheduleExpression parsed = CronExpressionParser.parse("1 2 3 4 5 6");
		assertAll(
			() -> assertEquals("1", parsed.getSecond()),
			() -> assertEquals("2", parsed.getMinute()),
			() -> assertEquals("3", parsed.getHour()),
			() -> assertEquals("4", parsed.getDayOfMonth()),
			() -> assertEquals("5", parsed.getMonth()),
			() -> assertEquals("6", parsed.getDayOfWeek()));
	}

	@Test
	public void parseThrowsOnInvalidExpression() {
		assertThrows(IllegalArgumentException.class, () -> CronExpressionParser.parse("15 1 * * *"));
	}

	@Test
	public void parseThrowsOnDisabledExpression() {
		assertThrows(IllegalArgumentException.class, () -> CronExpressionParser.parse(""));
	}
}
