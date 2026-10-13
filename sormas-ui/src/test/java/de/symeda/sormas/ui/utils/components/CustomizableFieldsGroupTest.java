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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
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
