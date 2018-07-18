package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */
public final class EventValidator {

    public static boolean validateEventData(NotificationContext activityContext, Event event, FragmentEventEditLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Type of place details
        if (event.getTypeOfPlace() != null) {
            if (event.getTypeOfPlace().equals(TypeOfPlace.OTHER)) {
                if (event.getTypeOfPlaceText() == null || event.getTypeOfPlaceText().trim().isEmpty()) {
                    binding.eventTypeOfPlaceText.enableErrorState(activityContext, R.string.validation_event_type_of_place_details);
                    success = false;
                }
            }
        }

        // Disease details
        if (event.getDisease() == Disease.OTHER) {
            binding.eventDiseaseDetails.enableErrorState(activityContext, R.string.validation_case_disease_details);
            success = false;
        }

        // Event description
        if (event.getEventDesc() == null || event.getEventDesc().trim().isEmpty()) {
            binding.eventEventDesc.enableErrorState(activityContext, R.string.validation_event_description);
            success = false;
        }

        // Event type
        if (event.getEventType() == null) {
            binding.eventEventType.enableErrorState(activityContext, R.string.validation_event_type);
            success = false;
        }

        return success;
    }

    public static void clearErrorsForEventData(FragmentEventEditLayoutBinding binding) {
        /*for (EditTeboPropertyField field : getEventDataFields(binding)) {
            field.clearError();
        }*/
    }
}
