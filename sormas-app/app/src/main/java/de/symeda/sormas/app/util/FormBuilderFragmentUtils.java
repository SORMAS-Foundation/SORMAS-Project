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

package de.symeda.sormas.app.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.view.View;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.formfield.FormField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;

public class FormBuilderFragmentUtils {

	private FormBuilderFragmentUtils() {
	}

	/**
	 * Handle visibility dependencies for a form field based on another field's value
	 * 
	 * @param fieldMap
	 *            Map of field names to ControlPropertyField instances
	 * @param formField
	 *            FormField entity containing dependency information
	 * @param dynamicField
	 *            The field whose visibility depends on another field
	 */
	public static void handleDependingOn(
		Map<String, ControlPropertyField> fieldMap,
		FormField formField,
		ControlPropertyField dynamicField) {

		String dependingOn = formField.getDependingOn();
		String[] dependingOnValues = formField.getDependingOnValuesArray();

		if (dependingOn != null && dependingOnValues != null && dependingOnValues.length > 0) {
			ControlPropertyField parentField = fieldMap.get(dependingOn);
			if (parentField != null) {
				// Set initial visibility
				setVisibilityDependency(dynamicField, dependingOnValues, parentField.getValue());

				// Listen for value changes
				final ControlPropertyField finalDynamicField = dynamicField;
				parentField.addValueChangedListener(field -> setVisibilityDependency(finalDynamicField, dependingOnValues, field.getValue()));
			}
		}
	}

	/**
	 * Set visibility of a field based on dependency values
	 * 
	 * @param field
	 *            The field whose visibility to set
	 * @param dependingOnValues
	 *            Array of values that make the field visible
	 * @param dependingOnFieldValue
	 *            Current value of the parent field
	 */
	public static void setVisibilityDependency(ControlPropertyField field, String[] dependingOnValues, Object dependingOnFieldValue) {
		String parsedValue = dependingOnFieldValue == null
			? ""
			: dependingOnFieldValue instanceof Boolean
				? YesNoUnknown.valueOf(((Boolean) dependingOnFieldValue).booleanValue()).name()
				: dependingOnFieldValue.toString();

		// Check if value matches any of the dependingOnValues
		if (!containsIgnoreCase(Arrays.asList(dependingOnValues), parsedValue)) {
			field.setVisibility(View.INVISIBLE);
		} else {
			field.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Check if a list contains a string (case-insensitive)
	 */
	private static boolean containsIgnoreCase(List<String> list, String soughtFor) {
		for (String current : list) {
			if (current != null && current.equalsIgnoreCase(soughtFor)) {
				return true;
			}
		}
		return false;
	}
}




