package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.FragmentContactEditPersonLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentPersonEditLayoutBinding;

/**
 * Created by Mate Strysewske on 21.07.2017.
 */
public final class PersonValidator {


    public static boolean validatePersonData(NotificationContext activityContext, Person person, FragmentContactEditPersonLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Last name
        if (person.getLastName() == null || person.getLastName().trim().isEmpty()) {
            binding.personLastName.enableErrorState(activityContext, R.string.validation_person_last_name);
            success = false;
        }

        // First name
        if (person.getFirstName() == null || person.getFirstName().trim().isEmpty()) {
            binding.personFirstName.enableErrorState(activityContext, R.string.validation_person_first_name);
            success = false;
        }

        return success;
    }

    public static void clearErrors(FragmentContactEditPersonLayoutBinding binding) {
        for (ControlPropertyEditField field : getPersonDataFields(binding)) {
            //field.clearError();
        }
    }

    public static void setRequiredHintsForPersonData(FragmentContactEditPersonLayoutBinding binding) {
        for (ControlPropertyEditField field : getPersonDataFields(binding)) {
            field.setRequired(true);
        }
    }

    private static final List<? extends ControlPropertyEditField<?>> getPersonDataFields(FragmentContactEditPersonLayoutBinding binding) {
        return Arrays.asList(binding.personFirstName, binding.personLastName);
    }

    public static boolean validatePersonData(NotificationContext activityContext, Person person, FragmentPersonEditLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Last name
        if (person.getLastName() == null || person.getLastName().trim().isEmpty()) {
            binding.personLastName.enableErrorState(activityContext, R.string.validation_person_last_name);
            success = false;
        }

        // First name
        if (person.getFirstName() == null || person.getFirstName().trim().isEmpty()) {
            binding.personFirstName.enableErrorState(activityContext, R.string.validation_person_first_name);
            success = false;
        }

        return success;
    }

    public static void clearErrors(FragmentPersonEditLayoutBinding binding) {
        for (ControlPropertyEditField field : getPersonDataFields(binding)) {
            //field.clearError();
        }
    }

    public static void setRequiredHintsForPersonData(FragmentPersonEditLayoutBinding binding) {
        for (ControlPropertyEditField field : getPersonDataFields(binding)) {
            field.setRequired(true);
        }
    }

    private static final List<? extends ControlPropertyEditField<?>> getPersonDataFields(FragmentPersonEditLayoutBinding binding) {
        return Arrays.asList(binding.personFirstName, binding.personLastName);
    }
}
