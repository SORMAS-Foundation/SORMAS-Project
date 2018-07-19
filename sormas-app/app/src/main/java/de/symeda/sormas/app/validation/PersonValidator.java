package de.symeda.sormas.app.validation;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.person.edit.PersonEditFragment;

public final class PersonValidator {

    public static void validatePerson(PersonEditFragment fragment) throws ValidationException {
        ValidationErrorInfo errorInfo = FragmentValidator.validateFragment(fragment);

        if (errorInfo.hasError()) {
            throw new ValidationException(errorInfo.toString());
        }
    }

}
