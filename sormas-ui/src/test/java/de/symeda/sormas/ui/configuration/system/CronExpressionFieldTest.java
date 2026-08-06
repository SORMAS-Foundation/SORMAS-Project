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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

public class CronExpressionFieldTest {

	private boolean isJobEnabledChecked(CronExpressionField field) throws ReflectiveOperationException {

		Field enabledField = CronExpressionField.class.getDeclaredField("enabled");
		enabledField.setAccessible(true);
		return ((CheckBox) enabledField.get(field)).getValue();
	}

	private TextField[] fieldBoxesOf(CronExpressionField field) throws ReflectiveOperationException {

		Field fieldsField = CronExpressionField.class.getDeclaredField("fields");
		fieldsField.setAccessible(true);
		return (TextField[]) fieldsField.get(field);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"0 15 1 * * *",
		"0 */10 * * * *",
		"0 */2 * * * *",
		"0 0 * * * *",
		"0 40 2 * * *",
		"0 0 1,13 * * *" })
	public void splitsAndRejoinsWithoutChangingTheExpression(String expression) {

		CronExpressionField field = new CronExpressionField();
		field.setValue(expression);

		assertEquals(expression, field.getValue());
	}

	@Test
	public void clearingEnabledEmptiesTheValue() {

		CronExpressionField field = new CronExpressionField();
		field.setValue("0 15 1 * * *");

		field.setJobEnabled(false);

		assertEquals("", field.getValue());
	}

	@Test
	public void reEnablingRestoresTheDefaultShape() {

		CronExpressionField field = new CronExpressionField();
		field.setValue("");

		field.setJobEnabled(true);

		assertEquals("0 0 0 * * *", field.getValue());
	}

	@Test
	public void anOutOfRangeFieldIsReportedAsInvalid() {

		CronExpressionField field = new CronExpressionField();
		field.setValue("0 70 1 * * *");

		assertFalse(field.isExpressionValid());
	}

	@Test
	public void aValidExpressionIsReportedAsValid() {

		CronExpressionField field = new CronExpressionField();
		field.setValue("0 15 1 * * *");

		assertTrue(field.isExpressionValid());
	}

	@Test
	public void anOverLongExpressionDoesNotRoundTripToAValidExpression() {

		CronExpressionField field = new CronExpressionField();
		field.setValue("0 15 1 * * * extra");

		assertNotEquals("0 15 1 * * *", field.getValue());
		assertFalse(field.isExpressionValid());
	}

	@Test
	public void anUnderLengthExpressionIsReportedAsInvalid() {

		CronExpressionField field = new CronExpressionField();
		field.setValue("0 15 1 * *");

		assertFalse(field.isExpressionValid());
	}

	@Test
	public void anEmptyValueIsDisabled() throws ReflectiveOperationException {

		CronExpressionField field = new CronExpressionField();
		field.setValue("0 15 1 * * *");

		field.setValue("");

		assertEquals("", field.getValue());
		assertFalse(isJobEnabledChecked(field));
	}

	@Test
	public void aWhitespaceOnlyValueIsTreatedAsDisabled() throws ReflectiveOperationException {

		CronExpressionField field = new CronExpressionField();
		field.setValue("0 15 1 * * *");

		field.setValue("   ");

		assertEquals("", field.getValue());
		assertFalse(isJobEnabledChecked(field));
	}

	@Test
	public void blankFieldsWhileEnabledAreReportedAsInvalid() throws ReflectiveOperationException {

		CronExpressionField field = new CronExpressionField();
		field.setValue("0 15 1 * * *");

		for (TextField box : fieldBoxesOf(field)) {
			box.setValue("");
		}

		assertTrue(isJobEnabledChecked(field));
		assertFalse(field.isExpressionValid());
	}
}
