package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.EditTeboPropertyField;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.TextField;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.databinding.FragmentCaseEditPatientLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentContactEditPersonLayoutBinding;
import de.symeda.sormas.app.databinding.PersonEditFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 21.07.2017.
 */
public final class PersonValidator {

    public static boolean validatePersonData(INotificationContext activityContext, Person person, PersonEditFragmentLayoutBinding binding) {
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







    public static boolean validatePersonData(INotificationContext activityContext, Person person, FragmentContactEditPersonLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Last name
        if (person.getLastName() == null || person.getLastName().trim().isEmpty()) {
            binding.txtLastName.enableErrorState(activityContext, R.string.validation_person_last_name);
            success = false;
        }

        // First name
        if (person.getFirstName() == null || person.getFirstName().trim().isEmpty()) {
            binding.txtFirstName.enableErrorState(activityContext, R.string.validation_person_first_name);
            success = false;
        }

        return success;
    }

    public static void clearErrors(FragmentContactEditPersonLayoutBinding binding) {
        for (EditTeboPropertyField field : getPersonDataFields(binding)) {
            //field.clearError();
        }
    }

    public static void setRequiredHintsForPersonData(FragmentContactEditPersonLayoutBinding binding) {
        for (EditTeboPropertyField field : getPersonDataFields(binding)) {
            field.setRequired(true);
        }
    }

    private static final List<? extends EditTeboPropertyField<?>> getPersonDataFields(FragmentContactEditPersonLayoutBinding binding) {
        return Arrays.asList(binding.txtFirstName, binding.txtLastName);
    }








    public static boolean validatePersonData(INotificationContext activityContext, Person person, FragmentCaseEditPatientLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Last name
        if (person.getLastName() == null || person.getLastName().trim().isEmpty()) {
            binding.txtLastName.enableErrorState(activityContext, R.string.validation_person_last_name);
            success = false;
        }

        // First name
        if (person.getFirstName() == null || person.getFirstName().trim().isEmpty()) {
            binding.txtFirstName.enableErrorState(activityContext, R.string.validation_person_first_name);
            success = false;
        }

        return success;
    }

    public static void clearErrors(FragmentCaseEditPatientLayoutBinding binding) {
        for (EditTeboPropertyField field : getPersonDataFields(binding)) {
            //field.clearError();
        }
    }

    public static void setRequiredHintsForPersonData(FragmentCaseEditPatientLayoutBinding binding) {
        for (EditTeboPropertyField field : getPersonDataFields(binding)) {
            field.setRequired(true);
        }
    }

    private static final List<? extends EditTeboPropertyField<?>> getPersonDataFields(FragmentCaseEditPatientLayoutBinding binding) {
        return Arrays.asList(binding.txtFirstName, binding.txtLastName);
    }
}
