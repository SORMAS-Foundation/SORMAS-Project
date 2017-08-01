package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.PersonEditFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 21.07.2017.
 */
public final class PersonValidator {

    public static void clearErrors(PersonEditFragmentLayoutBinding binding) {
        binding.personFirstName.setError(null);
        binding.personLastName.setError(null);
    }

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

}
