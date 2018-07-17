package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.FragmentEventParticipantNewLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */
public final class EventParticipantValidator {

    public static boolean validateEventParticipantData(NotificationContext activityContext, EventParticipant eventParticipant, FragmentEventParticipantNewLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        if (eventParticipant.getPerson() == null)
            return false;

        // Last name
        if (eventParticipant.getPerson().getLastName() == null || eventParticipant.getPerson().getLastName().trim().isEmpty()) {
            binding.eventParticipantLastName.enableErrorState(activityContext, R.string.validation_person_last_name);
            success = false;
        }

        // First name
        if (eventParticipant.getPerson().getFirstName() == null || eventParticipant.getPerson().getFirstName().trim().isEmpty()) {
            binding.eventParticipantFirstName.enableErrorState(activityContext, R.string.validation_person_first_name);
            success = false;
        }

        // Involvement description
        if (eventParticipant.getInvolvementDescription() == null || eventParticipant.getInvolvementDescription().trim().isEmpty()) {
            binding.eventParticipantInvolvementDescription.enableErrorState(activityContext, R.string.validation_event_participant_involvement_desc);
            success = false;
        }

        return success;
    }

    public static boolean validateNewEvent(NotificationContext activityContext, EventParticipant eventParticipant, FragmentEventParticipantNewLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Event participant's last name
        if (eventParticipant.getPerson().getLastName() == null || eventParticipant.getPerson().getLastName().trim().isEmpty()) {
            binding.eventParticipantLastName.enableErrorState(activityContext, R.string.validation_person_last_name);
            success = false;
        }

        // Event participant's first name
        if (eventParticipant.getPerson().getFirstName() == null || eventParticipant.getPerson().getFirstName().trim().isEmpty()) {
            binding.eventParticipantFirstName.enableErrorState(activityContext, R.string.validation_person_first_name);
            success = false;
        }

        // Involvement description
        if (eventParticipant.getInvolvementDescription() == null || eventParticipant.getInvolvementDescription().trim().isEmpty()) {
            binding.eventParticipantInvolvementDescription.enableErrorState(activityContext, R.string.validation_event_participant_involvement_desc);
            success = false;
        }

        return success;
    }

    public static void clearErrorsForNewEventParticipant(FragmentEventParticipantNewLayoutBinding binding) {
        /*for (EditTeboPropertyField field : getNewEventParticipantFields(binding)) {
            field.clearError();
        }*/
    }

    public static void setRequiredHintsForEventParticipantData(FragmentEventParticipantNewLayoutBinding binding) {
        for (ControlPropertyEditField field : getEventParticipantDataFields(binding)) {
            field.setRequired(true);
        }
    }

    public static void setRequiredHintsForNewEventParticipant(FragmentEventParticipantNewLayoutBinding binding) {
        for (ControlPropertyEditField field : getNewEventParticipantFields(binding)) {
            field.setRequired(true);
        }
    }

    private static final List<ControlTextEditField> getEventParticipantDataFields(FragmentEventParticipantNewLayoutBinding binding) {
        return Arrays.asList(binding.eventParticipantInvolvementDescription);
    }

    private static final List<ControlTextEditField> getNewEventParticipantFields(FragmentEventParticipantNewLayoutBinding binding) {
        return Arrays.asList(binding.eventParticipantInvolvementDescription, binding.eventParticipantFirstName,
                binding.eventParticipantLastName);
    }

}
