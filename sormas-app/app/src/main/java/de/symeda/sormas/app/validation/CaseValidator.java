package de.symeda.sormas.app.validation;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.caze.edit.CaseEditFragment;
import de.symeda.sormas.app.caze.edit.CaseNewFragment;

public final class CaseValidator {

    public static void validateNewCase(CaseNewFragment fragment) throws ValidationException {
        ValidationErrorInfo errorInfo = FragmentValidator.validateFragment(fragment);

        if (errorInfo.hasError()) {
            throw new ValidationException(errorInfo.toString());
        }
    }

    public static void validateCase(CaseEditFragment fragment) throws ValidationException {
        ValidationErrorInfo errorInfo = FragmentValidator.validateFragment(fragment);

        if (errorInfo.hasError()) {
            throw new ValidationException(errorInfo.toString());
        }
    }

}
