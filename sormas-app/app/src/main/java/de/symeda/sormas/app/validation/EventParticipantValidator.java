package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.TextField;
import de.symeda.sormas.app.databinding.EventParticipantFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.EventParticipantNewFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */
public final class EventParticipantValidator {

    public static boolean validateEventParticipantData(EventParticipant eventParticipant, EventParticipantFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Involvement description
        if (eventParticipant.getInvolvementDescription() == null || eventParticipant.getInvolvementDescription().trim().isEmpty()) {
            binding.eventParticipantInvolvementDescription.setError(resources.getString(R.string.validation_event_participant_involvement_desc));
            success = false;
        }

        return success;
    }

    public static boolean validateNewEvent(EventParticipant eventParticipant, EventParticipantNewFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Event participant's last name
        if (eventParticipant.getPerson().getLastName() == null || eventParticipant.getPerson().getLastName().trim().isEmpty()) {
            binding.eventParticipantLastName.setError(resources.getString(R.string.validation_person_last_name));
            success = false;
        }

        // Event participant's first name
        if (eventParticipant.getPerson().getFirstName() == null || eventParticipant.getPerson().getFirstName().trim().isEmpty()) {
            binding.eventParticipantFirstName.setError(resources.getString(R.string.validation_person_first_name));
            success = false;
        }

        // Involvement description
        if (eventParticipant.getInvolvementDescription() == null || eventParticipant.getInvolvementDescription().trim().isEmpty()) {
            binding.eventParticipantInvolvementDescription.setError(resources.getString(R.string.validation_event_participant_involvement_desc));
            success = false;
        }

        return success;
    }

    public static void clearErrorsForEventParticipantData(EventParticipantFragmentLayoutBinding binding) {
        for (PropertyField field : getEventParticipantDataFields(binding)) {
            field.clearError();
        }
    }

    public static void clearErrorsForNewEventParticipant(EventParticipantNewFragmentLayoutBinding binding) {
        for (PropertyField field : getNewEventParticipantFields(binding)) {
            field.clearError();
        }
    }

    public static void setRequiredHintsForEventParticipantData(EventParticipantFragmentLayoutBinding binding) {
        for (PropertyField field : getEventParticipantDataFields(binding)) {
            field.setRequiredHint(true);
        }
    }

    public static void setRequiredHintsForNewEventParticipant(EventParticipantNewFragmentLayoutBinding binding) {
        for (PropertyField field : getNewEventParticipantFields(binding)) {
            field.setRequiredHint(true);
        }
    }

    private static final List<TextField> getEventParticipantDataFields(EventParticipantFragmentLayoutBinding binding) {
        return Arrays.asList(binding.eventParticipantInvolvementDescription);
    }

    private static final List<TextField> getNewEventParticipantFields(EventParticipantNewFragmentLayoutBinding binding) {
        return Arrays.asList(binding.eventParticipantInvolvementDescription, binding.eventParticipantFirstName,
                binding.eventParticipantLastName);
    }

}
