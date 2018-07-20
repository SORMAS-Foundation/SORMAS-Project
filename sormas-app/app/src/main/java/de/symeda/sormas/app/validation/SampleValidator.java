package de.symeda.sormas.app.validation;

import android.content.Context;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.databinding.FragmentCaseNewLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentSampleNewLayoutBinding;

public final class SampleValidator {

    public static void validateNewSample(Context context, FragmentSampleNewLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

    public static void validateSample(Context context, FragmentSampleEditLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

}
