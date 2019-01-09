/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.component.validation;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlDateTimeField;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;

import static android.view.View.VISIBLE;

public class FragmentValidator {

    private FragmentValidator() { }

    public static void validate(Context context, ViewDataBinding contentBinding) throws ValidationException {
        ValidationErrorInfo errorInfo = FragmentValidator.validateFragmentRequirements(context, contentBinding);

        if (errorInfo.hasError()) {
            throw new ValidationException(errorInfo.toString());
        }
    }

    private static ValidationErrorInfo validateFragmentRequirements(Context context, ViewDataBinding fragmentBinding) {
        ValidationErrorInfo errorInfo = new ValidationErrorInfo(context);

        ViewGroup root = (ViewGroup) fragmentBinding.getRoot();
        validateRequiredPropertyEditFields(root, errorInfo);

        return errorInfo;
    }

    private static void validateRequiredPropertyEditFields(ViewGroup parent, ValidationErrorInfo errorInfo) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlPropertyEditField) {
                ControlPropertyEditField field = (ControlPropertyEditField) child;
                field.setErrorIfEmpty();

                if (field instanceof ControlDateField) {
                    ((ControlDateField) field).setErrorIfOutOfDateRange();
                }

                if (field instanceof ControlDateTimeField) {
                    ((ControlDateTimeField) field).setErrorIfOutOfDateRange();
                }

                if (field.getValidationCallback() != null) {
                    field.getValidationCallback().call();
                }

                if (field.isHasError() && field.getVisibility() == VISIBLE) {
                    errorInfo.addFieldWithError(field);
                }
            } else if (child instanceof ViewGroup) {
                validateRequiredPropertyEditFields((ViewGroup) child, errorInfo);
            }
        }
    }

}
