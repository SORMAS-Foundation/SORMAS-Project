package de.symeda.sormas.app.validation;

import android.content.Context;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.databinding.DialogMoveCaseLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditEpidLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditHospitalizationLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseNewLayoutBinding;

public final class CaseValidator {

    public static void validateNewCase(Context context, FragmentCaseNewLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

    public static void validateCase(Context context, FragmentCaseEditLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

    public static void validateHospitalization(Context context, FragmentCaseEditHospitalizationLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

    public static void validateEpiData(Context context, FragmentCaseEditEpidLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

    public static void validateMoveCase(Context context, DialogMoveCaseLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

}
