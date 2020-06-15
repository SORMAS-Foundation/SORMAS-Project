/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.component.validation;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlDateTimeField;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;

/**
 * Custom validator that iterates over all ControlPropertyEditFields in the given Fragment and
 * builds information about encountered validation errors. These include:
 * - Required fields without a value
 * - ControlDateFields or ControlDateTimeFields that have a value outside the allowed range
 * - Any custom validation that is done on the specific field with a ValidationCallback
 */
public class FragmentValidator {

	private FragmentValidator() {
	}

	/**
	 * Validates all ControlPropertyEditFields within the given ViewDataBinding. If there are
	 * any fields with an error, throws a ValidationException containing a description of all
	 * errors.
	 */
	public static void validate(Context context, ViewDataBinding contentBinding) throws ValidationException {
		ValidationErrorInfo errorInfo = FragmentValidator.validateFragment(context, contentBinding);

		if (errorInfo.hasError()) {
			throw new ValidationException(errorInfo.toString());
		}
	}

	private static ValidationErrorInfo validateFragment(Context context, ViewDataBinding fragmentBinding) {
		ValidationErrorInfo errorInfo = new ValidationErrorInfo(context);

		ViewGroup root = (ViewGroup) fragmentBinding.getRoot();
		validatePropertyEditFields(root, errorInfo);

		return errorInfo;
	}

	private static void validatePropertyEditFields(ViewGroup parent, ValidationErrorInfo errorInfo) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			View child = parent.getChildAt(i);
			if (child instanceof ControlPropertyEditField) {
				ControlPropertyEditField field = (ControlPropertyEditField) child;
				boolean fieldHasError = field.setErrorIfEmpty();

				if (field instanceof ControlDateField) {
					fieldHasError |= ((ControlDateField) field).setErrorIfOutOfDateRange();
				}

				if (field instanceof ControlDateTimeField) {
					fieldHasError |= ((ControlDateTimeField) field).setErrorIfOutOfDateRange();
				}

				if (field.getValidationCallback() != null) {
					fieldHasError |= (Boolean) field.getValidationCallback().call();
				}

				// Disable error state on the field if all check returned without errors
				if (!fieldHasError) {
					field.disableErrorState();
				}

				if (field.isHasError() && field.getVisibility() == VISIBLE && field.isEnabled()) {
					errorInfo.addFieldWithError(field);
				}
			} else if (child instanceof ViewGroup) {
				validatePropertyEditFields((ViewGroup) child, errorInfo);
			}
		}
	}
}
