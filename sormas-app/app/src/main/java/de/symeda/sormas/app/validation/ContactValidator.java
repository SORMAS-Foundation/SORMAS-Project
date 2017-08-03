package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.ContactDataFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.ContactNewFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */
public final class ContactValidator {

    public static boolean validateContactData(Contact contact, ContactDataFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Relation to case
        if (contact.getRelationToCase() == null) {
            binding.contactRelationToCase.setError(resources.getString(R.string.validation_contact_relation));
            success = false;
        }

        // Contact proximity
        if (contact.getContactProximity() == null) {
            binding.contactContactProximity.setError(resources.getString(R.string.validation_contact_proximity));
            success = false;
        }

        // Last contact date
        if (contact.getLastContactDate() == null || contact.getLastContactDate().getTime() > contact.getReportDateTime().getTime()) {
            binding.contactLastContactDate.setError(resources.getString(R.string.validation_contact_last_date));
            success = false;
        }

        return success;
    }

    public static boolean validateNewContact(Contact contact, ContactNewFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Relation to case
        if (contact.getRelationToCase() == null) {
            binding.contactRelationToCase.setError(resources.getString(R.string.validation_contact_relation));
            success = false;
        }

        // Contact proximity
        if (contact.getContactProximity() == null) {
            binding.contactContactProximity.setError(resources.getString(R.string.validation_contact_proximity));
            success = false;
        }

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

        // Last contact date
        if (contact.getLastContactDate() == null || contact.getLastContactDate().getTime() > contact.getReportDateTime().getTime()) {
            binding.contactLastContactDate.setError(resources.getString(R.string.validation_contact_last_date));
            success = false;
        }

        return success;
    }

    public static void clearErrorsForContactData(ContactDataFragmentLayoutBinding binding) {
        for (PropertyField field : getContactDataFields(binding)) {
            field.clearError();
        }
    }

    public static void clearErrorsForNewContact(ContactNewFragmentLayoutBinding binding) {
        for (PropertyField field : getNewContactFields(binding)) {
            field.clearError();
        }
    }

    public static void setRequiredHintsForContactData(ContactDataFragmentLayoutBinding binding) {
        for (PropertyField field : getContactDataFields(binding)) {
            field.setRequiredHint(true);
        }
    }

    public static void setRequiredHintsForNewContact(ContactNewFragmentLayoutBinding binding) {
        for (PropertyField field : getNewContactFields(binding)) {
            field.setRequiredHint(true);
        }
    }

    private static final List<PropertyField<?>> getContactDataFields(ContactDataFragmentLayoutBinding binding) {
        return Arrays.asList(binding.contactRelationToCase, binding.contactContactProximity,
                binding.contactLastContactDate);
    }

    private static final List<PropertyField<?>> getNewContactFields(ContactNewFragmentLayoutBinding binding) {
        return Arrays.asList(binding.contactRelationToCase, binding.contactContactProximity, binding.contactLastContactDate,
                binding.contactPersonFirstName, binding.contactPersonLastName);
    }

}
