package de.symeda.sormas.app.validation;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;

import static android.view.View.VISIBLE;

public class FragmentValidator {

    private FragmentValidator() { }

    protected static void performBasicValidation(Context context, ViewDataBinding contentBinding) throws ValidationException {
        ValidationErrorInfo errorInfo = FragmentValidator.validateFragmentRequirements(context, contentBinding);

        if (errorInfo.hasError()) {
            throw new ValidationException(errorInfo.toString());
        }
    }

    protected static ValidationErrorInfo validateFragmentRequirements(Context context, ViewDataBinding fragmentBinding) {
        ValidationErrorInfo errorInfo = new ValidationErrorInfo(context);

        ViewGroup root = (ViewGroup) fragmentBinding.getRoot();
        validateRequiredPropertyEditFields(root, errorInfo);

        return errorInfo;
    }

    private static void validateRequiredPropertyEditFields(ViewGroup parent, ValidationErrorInfo errorInfo) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlPropertyEditField) {
                ((ControlPropertyEditField) child).setErrorIfEmpty();
                if (((ControlPropertyEditField) child).isHasError() && child.getVisibility() == VISIBLE) {
                    errorInfo.addFieldWithError((ControlPropertyEditField) child);
                }
            } else if (child instanceof ViewGroup) {
                validateRequiredPropertyEditFields((ViewGroup) child, errorInfo);
            }
        }
    }

}
