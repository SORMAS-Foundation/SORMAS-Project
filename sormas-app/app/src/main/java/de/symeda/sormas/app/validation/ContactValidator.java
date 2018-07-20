package de.symeda.sormas.app.validation;

import android.content.Context;
import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentContactNewLayoutBinding;

public final class ContactValidator {

    public static void validateNewContact(Context context, FragmentContactNewLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

    public static void validateContact(Context context, FragmentContactEditLayoutBinding contentBinding) throws ValidationException {
        FragmentValidator.performBasicValidation(context, contentBinding);
    }

}
