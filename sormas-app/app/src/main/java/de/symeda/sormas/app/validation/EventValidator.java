package de.symeda.sormas.app.validation;

import android.content.Context;
import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;

public final class EventValidator {

    public static void validateNewEvent(Context context, FragmentEventEditLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

    public static void validateEvent(Context context, FragmentEventEditLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

}
