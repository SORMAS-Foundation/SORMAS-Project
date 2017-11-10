package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.TextField;
import de.symeda.sormas.app.databinding.ContactDataFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.ContactNewFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */
public final class ContactValidator {

    public static boolean validateNewContact(Contact contact, ContactNewFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Last name
        if (contact.getPerson().getLastName() == null || contact.getPerson().getLastName().trim().isEmpty()) {
            binding.contactPersonLastName.setError(resources.getString(R.string.validation_person_last_name));
            success = false;
        }

        // First name
        if (contact.getPerson().getFirstName() == null || contact.getPerson().getFirstName().trim().isEmpty()) {
            binding.contactPersonFirstName.setError(resources.getString(R.string.validation_person_first_name));
            success = false;
        }

        return success;
    }

    public static void clearErrorsForNewContact(ContactNewFragmentLayoutBinding binding) {
        for (PropertyField field : getNewContactFields(binding)) {
            field.clearError();
        }
    }

    public static void setRequiredHintsForNewContact(ContactNewFragmentLayoutBinding binding) {
        for (PropertyField field : getNewContactFields(binding)) {
            field.setRequiredHint(true);
        }
    }

    private static final List<TextField> getNewContactFields(ContactNewFragmentLayoutBinding binding) {
        return Arrays.asList(binding.contactPersonFirstName, binding.contactPersonLastName);
    }

}
