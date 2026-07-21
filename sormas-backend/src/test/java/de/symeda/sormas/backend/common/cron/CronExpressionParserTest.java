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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.ejb.ScheduleExpression;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CronExpressionParserTest {

	@Test
	public void nullAndBlankExpressionsAreDisabled() {
		assertTrue(CronExpressionParser.isDisabled(null));
		assertTrue(CronExpressionParser.isDisabled(""));
		assertTrue(CronExpressionParser.isDisabled("   "));
		assertFalse(CronExpressionParser.isDisabled("0 15 1 * * *"));
	}

	@Test
	public void disabledExpressionsCountAsValid() {
		assertTrue(CronExpressionParser.isValid(null));
		assertTrue(CronExpressionParser.isValid(""));
	}

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

	@ParameterizedTest
	@ValueSource(strings = {
		"0 */10 * * * *",
		"0 */2 * * * *",
		"0 */30 * * * *",
		"0 */59 * * * *",
		"0 0 */23 * * *",
		"0 0 1 * * *",
		"0 0 * * * *",
		"0 40 2 * * *",
		"0 0 1,13 * * *",
		"0 0 1-5 * * *",
		"0 0 1-5/2 * * *" })
	public void acceptsSupportedSyntax(String expression) {
		assertTrue(CronExpressionParser.isValid(expression), expression);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"15 1 * * *",
		"0 15 1 * * * *",
		"0 15 1 * *",
		"nightly",
		"0 15 1 * * * extra",
		"0  15 1 * * *",
		"0 60 1 * * *",
		"0 0 24 * * *",
		"0 0 1 32 * *",
		"0 0 1 * 13 *",
		"0 0 1 * * 8",
		"0 0 1 0 * *",
		"0 */70 * * * *",
		"0 5/99 * * * *",
		"0 */0 * * * *",
		"0 0 */24 * * *" })
	public void rejectsMalformedOrOutOfRangeExpressions(String expression) {
		assertFalse(CronExpressionParser.isValid(expression), expression);
	}

	@Test
	public void parseThrowsOnInvalidExpression() {
		assertThrows(IllegalArgumentException.class, () -> CronExpressionParser.parse("15 1 * * *"));
	}
}
