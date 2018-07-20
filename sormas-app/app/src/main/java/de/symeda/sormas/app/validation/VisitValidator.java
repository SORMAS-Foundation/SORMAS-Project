package de.symeda.sormas.app.validation;

import android.content.Context;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.databinding.FragmentVisitEditLayoutBinding;

public final class VisitValidator {

    public static void validateNewVisit(Context context, FragmentVisitEditLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

    public static void validateVisit(Context context, FragmentVisitEditLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

}
