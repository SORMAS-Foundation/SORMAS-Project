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

package de.symeda.sormas.ui.utils.components.customizablefield;

import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldType;

/**
 * Factory for creating {@link CustomizableFieldInput} instances based on
 * {@link CustomizableFieldMetadataDto#getFieldType()}.
 * <p>
 * To add support for a new type, add the corresponding {@link CustomizableFieldType} case
 * and return the appropriate {@link CustomizableFieldInput} subclass.
 */
public final class CustomizableFieldInputFactory {

	private CustomizableFieldInputFactory() {
		// utility class
	}

	/**
	 * Creates the appropriate {@link CustomizableFieldInput} for the given metadata.
	 *
	 * @param metadata
	 *            field metadata; must not be {@code null}
	 * @return a new input component wired to the given metadata
	 * @throws UnsupportedOperationException
	 *             when the {@link CustomizableFieldType} has no implementation yet
	 */
	public static CustomizableFieldInput<?> create(CustomizableFieldMetadataDto metadata) {
		CustomizableFieldType type = metadata.getFieldType();

		switch (type) {
		case TEXT:
			return new CustomizableFieldInputText(metadata);

		case TEXTAREA:
			return new CustomizableFieldInputTextArea(metadata);

		case NUMBER:
			return new CustomizableFieldInputNumber(metadata);

		case DECIMAL:
			return new CustomizableFieldInputDecimal(metadata);

		case DATE:
			return new CustomizableFieldInputDate(metadata);

		case DATE_TIME:
			return new CustomizableFieldInputDateTime(metadata);

		case COMBOBOX:
			return new CustomizableFieldInputCombobox(metadata);

		case CHECKBOX:
			return new CustomizableFieldInputCheckbox(metadata);

		case YES_NO_UNKNOWN:
			return new CustomizableFieldInputYesNoUnknown(metadata);

		case CHECKBOX_LIST:
			return new CustomizableFieldInputCheckboxList(metadata);

		case RADIO_BUTTON_LIST:
			return new CustomizableFieldInputRadioButtonList(metadata);

		default:
			throw new UnsupportedOperationException("No CustomizableFieldInput implementation for field type: " + type);
		}
	}
}
