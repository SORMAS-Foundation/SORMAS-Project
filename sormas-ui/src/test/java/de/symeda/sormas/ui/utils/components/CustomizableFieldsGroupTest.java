/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.utils.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.vaadin.data.HasValue;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HasComponents;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldCustomProperties;
import de.symeda.sormas.api.customizablefield.CustomizableFieldGroup;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.ui.utils.components.customizablefield.CustomizableFieldInput;

class CustomizableFieldsGroupTest {

	@Test
	void testUsesDefaultValueWhenNoStoredValueExists() {
		CustomizableFieldMetadataDto metadata = buildMetadata("field-default", "default-value");

		CustomizableFieldsGroup group = new CustomizableFieldsGroup(CustomizableFieldGroup.CASE_DATA_GENERAL);
		group.setFieldsMetadata(List.of(metadata));
		group.setFieldsValues(Collections.emptyMap());
		group.updateFieldsDisplay();

		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values = group.getFieldsValues();
		assertTrue(values.containsKey(metadata));
		assertEquals("default-value", values.get(metadata).getValue());
	}

	@Test
	void testKeepsExplicitClearOfDefaultValue() {
		CustomizableFieldMetadataDto metadata = buildMetadata("field-clear", "default-value");

		CustomizableFieldsGroup group = new CustomizableFieldsGroup(CustomizableFieldGroup.CASE_DATA_GENERAL);
		group.setFieldsMetadata(List.of(metadata));
		group.setFieldsValues(Collections.emptyMap());
		group.updateFieldsDisplay();

		CustomizableFieldInput<?> field = group.getFieldByMetadataUuid(metadata.getUuid());
		assertNotNull(field);
		field.clear();

		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values = group.getFieldsValues();
		assertTrue(values.containsKey(metadata));
		assertNull(values.get(metadata).getValue());
	}

	@Test
	void testDoesNotCreateValueForEmptyFieldWithoutDefault() {
		CustomizableFieldMetadataDto metadata = buildMetadata("field-empty", null);

		CustomizableFieldsGroup group = new CustomizableFieldsGroup(CustomizableFieldGroup.CASE_DATA_GENERAL);
		group.setFieldsMetadata(List.of(metadata));
		group.setFieldsValues(Collections.emptyMap());
		group.updateFieldsDisplay();

		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values = group.getFieldsValues();
		assertFalse(values.containsKey(metadata));
	}

	@Test
	void testMandatoryTextUntouchedFailsValidation() {
		CustomizableFieldsGroup group = buildGroup(buildMandatoryMetadata("text-untouched", CustomizableFieldType.TEXT));

		assertEquals(1, group.validateAndCollectErrors().size());
	}

	@Test
	void testMandatoryTextClearedToEmptyStringFailsValidation() {
		CustomizableFieldMetadataDto metadata = buildMandatoryMetadata("text-cleared", CustomizableFieldType.TEXT);
		CustomizableFieldsGroup group = buildGroup(metadata);
		CustomizableFieldInput<String> field = getField(group, metadata);

		field.setValue("typed");
		field.setValue("");

		assertEquals(1, group.validateAndCollectErrors().size());
	}

	@Test
	void testMandatoryTextBlankFailsValidation() {
		CustomizableFieldMetadataDto metadata = buildMandatoryMetadata("text-blank", CustomizableFieldType.TEXT);
		CustomizableFieldsGroup group = buildGroup(metadata);
		this.<String> getField(group, metadata).setValue("   ");

		assertEquals(1, group.validateAndCollectErrors().size());
	}

	@Test
	void testMandatoryTextFilledPassesValidation() {
		CustomizableFieldMetadataDto metadata = buildMandatoryMetadata("text-filled", CustomizableFieldType.TEXT);
		CustomizableFieldsGroup group = buildGroup(metadata);
		this.<String> getField(group, metadata).setValue("value");

		assertTrue(group.validateAndCollectErrors().isEmpty());
	}

	@Test
	void testMandatoryErrorHasMessageAfterCaption() {
		CustomizableFieldsGroup group = buildGroup(buildMandatoryMetadata("text-message", CustomizableFieldType.TEXT));

		String error = group.validateAndCollectErrors().get(0);
		assertTrue(error.startsWith("text-message: "));
		assertFalse(error.substring("text-message: ".length()).isBlank());
	}

	@Test
	void testOptionalTextEmptyPassesValidation() {
		CustomizableFieldMetadataDto metadata = buildMetadata("text-optional", null);
		CustomizableFieldsGroup group = buildGroup(metadata);
		this.<String> getField(group, metadata).setValue("");

		assertTrue(group.validateAndCollectErrors().isEmpty());
	}

	@Test
	void testMandatoryCheckboxMustBeTicked() {
		CustomizableFieldMetadataDto metadata = buildMandatoryMetadata("checkbox", CustomizableFieldType.CHECKBOX);
		CustomizableFieldsGroup group = buildGroup(metadata);
		CustomizableFieldInput<Boolean> field = getField(group, metadata);

		assertEquals(1, group.validateAndCollectErrors().size(), "untouched");

		field.setValue(Boolean.TRUE);
		assertTrue(group.validateAndCollectErrors().isEmpty(), "ticked");

		field.setValue(Boolean.FALSE);
		assertEquals(1, group.validateAndCollectErrors().size(), "unticked again");
	}

	@Test
	void testMandatoryCheckboxListEmptiedFailsValidation() {
		CustomizableFieldMetadataDto metadata = buildMandatoryMetadata("checkbox-list", CustomizableFieldType.CHECKBOX_LIST);
		CustomizableFieldsGroup group = buildGroup(metadata);
		CustomizableFieldInput<Set<String>> field = getField(group, metadata);

		field.setValue(Set.of("A"));
		assertTrue(group.validateAndCollectErrors().isEmpty(), "one option selected");

		field.setValue(Collections.emptySet());
		assertEquals(1, group.validateAndCollectErrors().size(), "all options deselected");
	}

	@Test
	void testNumberRejectsValuesWithoutDigits() {
		assertValidity(CustomizableFieldType.NUMBER, false, "-", "--1", "1-", "1.5", "a");
		assertValidity(CustomizableFieldType.NUMBER, true, "0", "12", "-3");
	}

	@Test
	void testDecimalRejectsValuesWithoutDigits() {
		assertValidity(CustomizableFieldType.DECIMAL, false, "-", ".", ",", "-.", "-,", "1.2.3", "1..2", "a");
		assertValidity(CustomizableFieldType.DECIMAL, true, "0", "12", "-3", "1.5", "1,5", "-1.5", ".5", "1.");
	}

	@Test
	void testReadOnlyIsAppliedToInnerWidgetForAllTypes() {
		for (CustomizableFieldType type : CustomizableFieldType.values()) {
			CustomizableFieldInput<?> field = buildSingleField(type, true);
			Component inner = getInnerComponent(field);

			if (inner instanceof HasValue) {
				assertTrue(((HasValue<?>) inner).isReadOnly(), type + " inner widget must be read-only");
			} else {
				List<Button> buttons = getButtons(inner);
				assertFalse(buttons.isEmpty(), type + " has neither a HasValue nor buttons as inner component");
				buttons.forEach(button -> assertFalse(button.isEnabled(), type + " buttons must be disabled"));
			}
		}
	}

	@Test
	void testEditableFieldKeepsInnerWidgetEditableForAllTypes() {
		for (CustomizableFieldType type : CustomizableFieldType.values()) {
			CustomizableFieldInput<?> field = buildSingleField(type, false);
			Component inner = getInnerComponent(field);

			if (inner instanceof HasValue) {
				assertFalse(((HasValue<?>) inner).isReadOnly(), type + " inner widget must be editable");
			} else {
				getButtons(inner).forEach(button -> assertTrue(button.isEnabled(), type + " buttons must be enabled"));
			}
		}
	}

	@Test
	void testReadOnlyFromMetadataCannotBeLiftedByCaller() {
		CustomizableFieldInput<?> field = buildSingleField(CustomizableFieldType.TEXT, true);
		getInnerComponent(field);

		field.setReadOnly(false);

		assertTrue(field.isReadOnly());
		assertTrue(((HasValue<?>) getInnerComponent(field)).isReadOnly());
	}

	@Test
	void testReadOnlyYesNoUnknownIgnoresButtonClicks() {
		CustomizableFieldInput<?> field = buildSingleField(CustomizableFieldType.YES_NO_UNKNOWN, true);

		getButtons(getInnerComponent(field)).forEach(Button::click);

		assertNull(field.getValue());
	}

	@Test
	void testCheckboxListDefaultValueIsTicked() {
		CustomizableFieldMetadataDto metadata = buildMetadata("checkbox-list-default", "Option B");
		metadata.setFieldType(CustomizableFieldType.CHECKBOX_LIST);
		CustomizableFieldCustomProperties props = new CustomizableFieldCustomProperties();
		props.setOptions(List.of("Option A", "Option B", "Option C"));
		metadata.setCustomProperties(props);

		CustomizableFieldInput<Set<String>> field = getField(buildGroup(metadata), metadata);

		assertEquals(Set.of("Option B"), field.getValue());
		@SuppressWarnings("unchecked")
		CheckBoxGroup<String> inner = (CheckBoxGroup<String>) getInnerComponent(field);
		assertEquals(Set.of("Option B"), inner.getValue());
	}

	private CustomizableFieldInput<?> buildSingleField(CustomizableFieldType type, boolean readOnly) {
		CustomizableFieldMetadataDto metadata = buildMetadata("field-" + type + "-" + readOnly, null);
		metadata.setFieldType(type);
		metadata.setReadOnly(readOnly);
		return getField(buildGroup(metadata), metadata);
	}

	/**
	 * Builds (if needed) and returns the inner widget. {@link CustomField#getContent()} is protected and is normally
	 * triggered by attaching the field to a UI, which is not available in this test.
	 */
	private Component getInnerComponent(CustomizableFieldInput<?> field) {
		try {
			Method getContent = CustomField.class.getDeclaredMethod("getContent");
			getContent.setAccessible(true);
			return (Component) getContent.invoke(field);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	private List<Button> getButtons(Component container) {
		List<Button> buttons = new ArrayList<>();
		if (container instanceof HasComponents) {
			((HasComponents) container).forEach(child -> {
				if (child instanceof Button) {
					buttons.add((Button) child);
				}
			});
		}
		return buttons;
	}

	private void assertValidity(CustomizableFieldType type, boolean expectedValid, String... values) {
		CustomizableFieldMetadataDto metadata = buildMetadata("numeric-" + type, null);
		metadata.setFieldType(type);
		CustomizableFieldsGroup group = buildGroup(metadata);
		CustomizableFieldInput<String> field = getField(group, metadata);

		for (String value : values) {
			field.setValue(value);
			assertEquals(expectedValid, group.validateAndCollectErrors().isEmpty(), type + " value '" + value + "'");
		}
	}

	private CustomizableFieldsGroup buildGroup(CustomizableFieldMetadataDto metadata) {
		CustomizableFieldsGroup group = new CustomizableFieldsGroup(CustomizableFieldGroup.CASE_DATA_GENERAL);
		group.setFieldsMetadata(List.of(metadata));
		group.setFieldsValues(Collections.emptyMap());
		group.updateFieldsDisplay();
		return group;
	}

	@SuppressWarnings("unchecked")
	private <T> CustomizableFieldInput<T> getField(CustomizableFieldsGroup group, CustomizableFieldMetadataDto metadata) {
		CustomizableFieldInput<?> field = group.getFieldByMetadataUuid(metadata.getUuid());
		assertNotNull(field);
		return (CustomizableFieldInput<T>) field;
	}

	private CustomizableFieldMetadataDto buildMandatoryMetadata(String uuid, CustomizableFieldType type) {
		CustomizableFieldMetadataDto metadata = buildMetadata(uuid, null);
		metadata.setFieldType(type);
		metadata.setMandatory(true);
		return metadata;
	}

	private CustomizableFieldMetadataDto buildMetadata(String uuid, String defaultValue) {
		CustomizableFieldMetadataDto metadata = new CustomizableFieldMetadataDto();
		metadata.setUuid(uuid);
		metadata.setName(uuid);
		metadata.setFieldType(CustomizableFieldType.TEXT);
		metadata.setContextClass(CustomizableFieldContext.CASE);
		metadata.setUiGroup(CustomizableFieldGroup.CASE_DATA_GENERAL);
		metadata.setUiLinePosition(1);
		metadata.setDefaultValue(defaultValue);
		metadata.setActive(true);
		return metadata;
	}
}
