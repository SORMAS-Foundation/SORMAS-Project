package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.FragmentContactNewLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */
public final class ContactValidator {

    public static boolean validateNewContact(NotificationContext activityContext, Contact contact, FragmentContactNewLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Last name
        if (contact.getPerson().getLastName() == null || contact.getPerson().getLastName().trim().isEmpty()) {
            binding.personLastName.enableErrorState(activityContext, R.string.validation_person_last_name);
            success = false;
        }

        // First name
        if (contact.getPerson().getFirstName() == null || contact.getPerson().getFirstName().trim().isEmpty()) {
            binding.personFirstName.enableErrorState(activityContext, R.string.validation_person_first_name);
            success = false;
        }

        return success;
    }

    public static void clearErrorsForNewContact(FragmentContactNewLayoutBinding binding) {
        for (ControlPropertyEditField field : getNewContactFields(binding)) {
            //field.clearError();
        }
    }

    public static void setRequiredHintsForNewContact(FragmentContactNewLayoutBinding binding) {
        for (ControlPropertyEditField field : getNewContactFields(binding)) {
            field.setRequired(true);
        }
    }

    private static final List<? extends ControlPropertyEditField<?>> getNewContactFields(FragmentContactNewLayoutBinding binding) {
        return Arrays.asList(binding.personFirstName, binding.personLastName);
    }

}
