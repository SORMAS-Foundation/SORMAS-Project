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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CronExpressionValidatorTest {

	@Test
	public void nullAndBlankExpressionsAreDisabled() {
		assertTrue(CronExpressionValidator.isDisabled(null));
		assertTrue(CronExpressionValidator.isDisabled(""));
		assertTrue(CronExpressionValidator.isDisabled("   "));
		assertFalse(CronExpressionValidator.isDisabled("0 15 1 * * *"));
	}

	@Test
	public void disabledExpressionsCountAsValid() {
		assertTrue(CronExpressionValidator.isValid(null));
		assertTrue(CronExpressionValidator.isValid(""));
		assertTrue(CronExpressionValidator.isValid("   "));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"0 */10 * * * *",
		"0 */2 * * * *",
		"0 */30 * * * *",
		"0 */59 * * * *",
		"0 0 */23 * * *",
		"*/10 0 0 * * *",
		"0 0 1 * * *",
		"0 0 * * * *",
		"0 40 2 * * *",
		"0 0 1,13 * * *",
		"0 0 1-5 * * *",
		"0 0 1-5/2 * * *",
		"0 59 23 31 12 7" })
	public void acceptsSupportedSyntax(String expression) {
		assertTrue(CronExpressionValidator.isValid(expression), expression);
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
		"0 0 */24 * * *",
		"0 0 0 */2 * *",
		"0 0 0 * */2 *",
		"0 0 0 * * */2" })
	public void rejectsMalformedOrOutOfRangeExpressions(String expression) {
		assertFalse(CronExpressionValidator.isValid(expression), expression);
	}

	@Test
	public void incrementsAreOnlyValidOnTheFirstThreeFields() {

		assertTrue(CronExpressionValidator.isFieldValid(CronExpressionValidator.SECOND, "*/10"));
		assertTrue(CronExpressionValidator.isFieldValid(CronExpressionValidator.MINUTE, "*/10"));
		assertTrue(CronExpressionValidator.isFieldValid(CronExpressionValidator.HOUR, "*/2"));
		assertFalse(CronExpressionValidator.isFieldValid(CronExpressionValidator.DAY_OF_MONTH, "*/2"));
		assertFalse(CronExpressionValidator.isFieldValid(CronExpressionValidator.MONTH, "*/2"));
		assertFalse(CronExpressionValidator.isFieldValid(CronExpressionValidator.DAY_OF_WEEK, "*/2"));
	}

	@Test
	public void eachFieldEnforcesItsOwnRange() {

		assertTrue(CronExpressionValidator.isFieldValid(CronExpressionValidator.MINUTE, "59"));
		assertFalse(CronExpressionValidator.isFieldValid(CronExpressionValidator.MINUTE, "60"));
		assertTrue(CronExpressionValidator.isFieldValid(CronExpressionValidator.HOUR, "23"));
		assertFalse(CronExpressionValidator.isFieldValid(CronExpressionValidator.HOUR, "24"));
		assertTrue(CronExpressionValidator.isFieldValid(CronExpressionValidator.DAY_OF_MONTH, "31"));
		assertFalse(CronExpressionValidator.isFieldValid(CronExpressionValidator.DAY_OF_MONTH, "0"));
		assertTrue(CronExpressionValidator.isFieldValid(CronExpressionValidator.MONTH, "12"));
		assertFalse(CronExpressionValidator.isFieldValid(CronExpressionValidator.MONTH, "13"));
		assertTrue(CronExpressionValidator.isFieldValid(CronExpressionValidator.DAY_OF_WEEK, "7"));
		assertFalse(CronExpressionValidator.isFieldValid(CronExpressionValidator.DAY_OF_WEEK, "8"));
	}

	@Test
	public void anEmptyFieldIsNotValid() {
		assertFalse(CronExpressionValidator.isFieldValid(CronExpressionValidator.MINUTE, ""));
	}
}
