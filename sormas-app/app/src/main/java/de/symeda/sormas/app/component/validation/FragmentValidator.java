package de.symeda.sormas.app.component.validation;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.utils.ValidationException;
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

                if (!field.isHasError() && field.getValidationCallback() != null) {
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
