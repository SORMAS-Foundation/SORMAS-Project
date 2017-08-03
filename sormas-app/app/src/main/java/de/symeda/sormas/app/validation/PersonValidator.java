package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.TextField;
import de.symeda.sormas.app.databinding.PersonEditFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 21.07.2017.
 */
public final class PersonValidator {

    public static boolean validatePersonData(Person person, PersonEditFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Last name
        if (person.getLastName() == null || person.getLastName().trim().isEmpty()) {
            binding.personLastName.setError(resources.getString(R.string.validation_person_last_name));
            success = false;
        }

        // First name
        if (person.getFirstName() == null || person.getFirstName().trim().isEmpty()) {
            binding.personFirstName.setError(resources.getString(R.string.validation_person_first_name));
            success = false;
        }

        return success;
    }

    public static void clearErrors(PersonEditFragmentLayoutBinding binding) {
        for (PropertyField field : getPersonDataFields(binding)) {
            field.clearError();
        }
    }

    public static void setRequiredHintsForPersonData(PersonEditFragmentLayoutBinding binding) {
        for (PropertyField field : getPersonDataFields(binding)) {
            field.setRequiredHint(true);
        }
    }

    private static final List<TextField> getPersonDataFields(PersonEditFragmentLayoutBinding binding) {
        return Arrays.asList(binding.personFirstName, binding.personLastName);
    }

}
