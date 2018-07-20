package de.symeda.sormas.app.validation;

import android.content.Context;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.databinding.FragmentPersonEditLayoutBinding;

public final class PersonValidator {

    public static void validatePerson(Context context, FragmentPersonEditLayoutBinding contentBinding) throws ValidationException {
        ValidationErrorInfo errorInfo = FragmentValidator.validateFragmentRequirements(context, contentBinding);

        if (errorInfo.hasError()) {
            throw new ValidationException(errorInfo.toString());
        }
    }

}
