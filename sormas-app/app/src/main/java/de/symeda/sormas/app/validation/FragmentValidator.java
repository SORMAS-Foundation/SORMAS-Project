package de.symeda.sormas.app.validation;

import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;

import static android.view.View.VISIBLE;

public class FragmentValidator {

    private FragmentValidator() { }

    public static ValidationErrorInfo validateFragment(BaseEditFragment fragment) {
        ViewDataBinding contentBinding = fragment.getContentBinding();
        ValidationErrorInfo errorInfo = new ValidationErrorInfo(fragment.getContext());

        ViewGroup root = (ViewGroup) contentBinding.getRoot();
        validatePropertyEditFields(root, errorInfo);

        return errorInfo;
    }

    private static void validatePropertyEditFields(ViewGroup parent, ValidationErrorInfo errorInfo) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlPropertyEditField) {
                ((ControlPropertyEditField) child).setErrorIfEmpty();
                if (((ControlPropertyEditField) child).isHasError() && child.getVisibility() == VISIBLE) {
                    errorInfo.addFieldWithError((ControlPropertyEditField) child);
                }
            } else if (child instanceof ViewGroup) {
                validatePropertyEditFields((ViewGroup) child, errorInfo);
            }
        }
    }

}
