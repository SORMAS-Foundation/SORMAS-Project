package de.symeda.sormas.app.component.validation;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;

public class ValidationErrorInfo {

    private Context context;
    private List<ControlPropertyEditField> fieldsWithError;

    public ValidationErrorInfo(Context context) {
        this.context = context;
        fieldsWithError = new ArrayList<>();
    }

    public void addFieldWithError(ControlPropertyEditField field) {
        fieldsWithError.add(field);
    }

    public boolean hasError() {
        return !fieldsWithError.isEmpty();
    }

    @Override
    public String toString() {
        if (fieldsWithError.isEmpty()) {
            return null;
        }

        StringBuilder errorStringBuilder = new StringBuilder();
        Resources resources = context.getResources();

        errorStringBuilder.append(resources.getString(R.string.validation_error_info_pre_text)).append(" ");

        for (ControlPropertyEditField field : fieldsWithError) {
            errorStringBuilder.append(field.getCaption());
            if (fieldsWithError.indexOf(field) == fieldsWithError.size() - 2) {
                errorStringBuilder.append(" ").append(resources.getString(R.string.and_not_capitalized)).append(" ");
            } else if (fieldsWithError.indexOf(field) != fieldsWithError.size() - 1) {
                errorStringBuilder.append(", ");
            } else {
                errorStringBuilder.append(".");
            }
        }

        errorStringBuilder.append("\n\n").append(resources.getString(R.string.validation_error_info_post_text));

        return errorStringBuilder.toString();
    }

}
