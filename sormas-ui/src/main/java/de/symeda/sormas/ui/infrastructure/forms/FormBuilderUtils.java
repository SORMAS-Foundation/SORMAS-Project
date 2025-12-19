/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.infrastructure.forms;

import java.util.Comparator;
import java.util.List;

import de.symeda.sormas.api.infrastructure.fields.FormFieldReferenceDto;

public final class FormBuilderUtils {

	private FormBuilderUtils() {
		// Hide Utility Class Constructor
	}

	/**
	 * Sorts form fields by their display order.
	 * Fields with null displayOrder are placed at the end.
	 * 
	 * @param fields
	 *            List of form fields to sort
	 * @return Sorted list of form fields
	 */
	public static List<FormFieldReferenceDto> sortFieldsByDisplayOrder(List<FormFieldReferenceDto> fields) {
		if (fields == null) {
			return null;
		}
		fields.sort(Comparator.comparing(FormFieldReferenceDto::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())));
		return fields;
	}

	/**
	 * Checks if a field is a boolean/yes-no field based on its field name pattern.
	 * This is a simple heuristic - can be enhanced based on actual field naming conventions.
	 * 
	 * @param field
	 *            The form field to check
	 * @return true if the field appears to be a boolean field
	 */
	public static boolean isBooleanField(FormFieldReferenceDto field) {
		if (field == null || field.getCaption() == null) {
			return false;
		}
		String caption = field.getCaption().toLowerCase();
		// Common patterns for yes/no fields
		return caption.contains("yes") || caption.contains("no") || caption.startsWith("is ") || caption.startsWith("has ");
	}

	/**
	 * Checks if a field is a number field based on its field name pattern.
	 * 
	 * @param field
	 *            The form field to check
	 * @return true if the field appears to be a number field
	 */
	public static boolean isNumberField(FormFieldReferenceDto field) {
		if (field == null || field.getCaption() == null) {
			return false;
		}
		String caption = field.getCaption().toLowerCase();
		// Common patterns for number fields
		return caption.contains("number") || caption.contains("count") || caption.contains("age") || caption.contains("quantity")
			|| caption.contains("amount");
	}

	/**
	 * Checks if a field is a label/display-only field.
	 * 
	 * @param field
	 *            The form field to check
	 * @return true if the field appears to be a label field
	 */
	public static boolean isLabelField(FormFieldReferenceDto field) {
		if (field == null || field.getFieldName() == null) {
			return false;
		}
		String fieldName = field.getFieldName().toLowerCase();
		return fieldName.startsWith("label_") || fieldName.startsWith("section_") || fieldName.contains("header");
	}
}

