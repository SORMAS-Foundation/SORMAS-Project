package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.EditTeboPropertyField;
import de.symeda.sormas.app.component.TeboTextInputEditText;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.databinding.FragmentEventNewPersonFullLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentEventNewPersonShortLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */
public final class EventParticipantValidator {

    public static boolean validateEventParticipantData(INotificationContext activityContext, EventParticipant eventParticipant, FragmentEventNewPersonFullLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        if (eventParticipant.getPerson() == null)
            return false;

        // Last name
        if (eventParticipant.getPerson().getLastName() == null || eventParticipant.getPerson().getLastName().trim().isEmpty()) {
            binding.txtLastName.enableErrorState(activityContext, R.string.validation_person_last_name);
            success = false;
        }

        // First name
        if (eventParticipant.getPerson().getFirstName() == null || eventParticipant.getPerson().getFirstName().trim().isEmpty()) {
            binding.txtFirstName.enableErrorState(activityContext, R.string.validation_person_first_name);
            success = false;
        }

        // Involvement description
        if (eventParticipant.getInvolvementDescription() == null || eventParticipant.getInvolvementDescription().trim().isEmpty()) {
            binding.txtInvolvementDesc.enableErrorState(activityContext, R.string.validation_event_participant_involvement_desc);
            success = false;
        }

        return success;
    }

    public static boolean validateNewEvent(INotificationContext activityContext, EventParticipant eventParticipant, FragmentEventNewPersonShortLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Event participant's last name
        if (eventParticipant.getPerson().getLastName() == null || eventParticipant.getPerson().getLastName().trim().isEmpty()) {
            binding.txtLastName.enableErrorState(activityContext, R.string.validation_person_last_name);
            success = false;
        }

        // Event participant's first name
        if (eventParticipant.getPerson().getFirstName() == null || eventParticipant.getPerson().getFirstName().trim().isEmpty()) {
            binding.txtFirstName.enableErrorState(activityContext, R.string.validation_person_first_name);
            success = false;
        }

        // Involvement description
        if (eventParticipant.getInvolvementDescription() == null || eventParticipant.getInvolvementDescription().trim().isEmpty()) {
            binding.txtInvolvementDesc.enableErrorState(activityContext, R.string.validation_event_participant_involvement_desc);
            success = false;
        }

        return success;
    }

    public static void clearErrorsForEventParticipantData(FragmentEventNewPersonShortLayoutBinding binding) {
        /*for (EditTeboPropertyField field : getEventParticipantDataFields(binding)) {
            field.clearError();
        }*/
    }

    public static void clearErrorsForEventParticipantData(FragmentEventNewPersonFullLayoutBinding binding) {
        /*for (EditTeboPropertyField field : getEventParticipantDataFields(binding)) {
            field.clearError();
        }*/
    }

    public static void clearErrorsForNewEventParticipant(FragmentEventNewPersonShortLayoutBinding binding) {
        /*for (EditTeboPropertyField field : getNewEventParticipantFields(binding)) {
            field.clearError();
        }*/
    }

    public static void setRequiredHintsForEventParticipantData(FragmentEventNewPersonShortLayoutBinding binding) {
        for (EditTeboPropertyField field : getEventParticipantDataFields(binding)) {
            field.setRequired(true);
        }
    }

    public static void setRequiredHintsForNewEventParticipant(FragmentEventNewPersonShortLayoutBinding binding) {
        for (EditTeboPropertyField field : getNewEventParticipantFields(binding)) {
            field.setRequired(true);
        }
    }

    private static final List<TeboTextInputEditText> getEventParticipantDataFields(FragmentEventNewPersonShortLayoutBinding binding) {
        return Arrays.asList(binding.txtInvolvementDesc);
    }

    private static final List<TeboTextInputEditText> getNewEventParticipantFields(FragmentEventNewPersonShortLayoutBinding binding) {
        return Arrays.asList(binding.txtInvolvementDesc, binding.txtFirstName,
                binding.txtLastName);
    }

}
