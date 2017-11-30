package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.EventDataFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */
public final class EventValidator {

    public static boolean validateEventData(Event event, EventDataFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Type of place details
        if (event.getTypeOfPlace() != null) {
            if (event.getTypeOfPlace().equals(TypeOfPlace.OTHER)) {
                if (event.getTypeOfPlaceText() == null || event.getTypeOfPlaceText().trim().isEmpty()) {
                    binding.eventTypeOfPlaceTxt.setError(resources.getString(R.string.validation_event_type_of_place_details));
                    success = false;
                }
            }
        }

        // Disease details
        if (event.getDisease() == Disease.OTHER) {
            binding.eventDiseaseDetails.setError(resources.getString(R.string.validation_case_disease_details));
            success = false;
        }

        // Event description
        if (event.getEventDesc() == null || event.getEventDesc().trim().isEmpty()) {
            binding.eventEventDesc.setError(resources.getString(R.string.validation_event_description));
            success = false;
        }

        // Event type
        if (event.getEventType() == null) {
            binding.eventEventType.setError(resources.getString(R.string.validation_event_type));
            success = false;
        }

        return success;
    }

    public static void clearErrorsForEventData(EventDataFragmentLayoutBinding binding) {
        for (PropertyField field : getEventDataFields(binding)) {
            field.clearError();
        }
    }

    public static void setRequiredHintsForEventData(EventDataFragmentLayoutBinding binding) {
        for (PropertyField field : getEventDataFields(binding)) {
            field.setRequiredHint(true);
        }
    }

    public static void setSoftRequiredHintsForEventData(EventDataFragmentLayoutBinding binding) {
        for (PropertyField field : getSoftRequiredEventDataFields(binding)) {
            field.makeFieldSoftRequired();
        }
    }

    private static final List<PropertyField<?>> getEventDataFields(EventDataFragmentLayoutBinding binding) {
        return Arrays.asList(binding.eventEventType, binding.eventEventDesc, binding.eventTypeOfPlaceTxt, binding.eventDiseaseDetails);
    }

    private static final List<PropertyField<?>> getSoftRequiredEventDataFields(EventDataFragmentLayoutBinding binding) {
        return Arrays.asList(binding.eventEventDate, binding.eventSrcFirstName, binding.eventSrcLastName, binding.eventSrcTelNo, binding.eventTypeOfPlace, binding.eventSurveillanceOfficer);
    }

}
