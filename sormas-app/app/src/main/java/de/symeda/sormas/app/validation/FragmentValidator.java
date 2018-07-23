package de.symeda.sormas.app.validation;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.core.NotificationContext;

import static android.view.View.VISIBLE;

public class FragmentValidator {

    private FragmentValidator() { }

    public static void validate(Context context, ViewDataBinding contentBinding) throws ValidationException {
        validate(context, contentBinding, (NotificationContext) context);
    }

    public static void validate(Context context, ViewDataBinding contentBinding, NotificationContext notificationContext) throws ValidationException {
        ValidationErrorInfo errorInfo = FragmentValidator.validateFragmentRequirements(context, contentBinding, notificationContext);

        if (errorInfo.hasError()) {
            throw new ValidationException(errorInfo.toString());
        }
    }

    private static ValidationErrorInfo validateFragmentRequirements(Context context, ViewDataBinding fragmentBinding, NotificationContext notificationContext) {
        ValidationErrorInfo errorInfo = new ValidationErrorInfo(context);

        ViewGroup root = (ViewGroup) fragmentBinding.getRoot();
        validateRequiredPropertyEditFields(root, errorInfo, notificationContext);

        return errorInfo;
    }

    private static void validateRequiredPropertyEditFields(ViewGroup parent, ValidationErrorInfo errorInfo, NotificationContext notificationContext) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlPropertyEditField) {
                ControlPropertyEditField field = (ControlPropertyEditField) child;
                field.setErrorIfEmpty(notificationContext);

                if (!field.isHasError() && field.getValidationCallback() != null) {
                    field.getValidationCallback().call(notificationContext);
                }

                if (field.isHasError() && field.getVisibility() == VISIBLE) {
                    errorInfo.addFieldWithError(field);
                }
            } else if (child instanceof ViewGroup) {
                validateRequiredPropertyEditFields((ViewGroup) child, errorInfo, notificationContext);
            }
        }
    }

}
