package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.databinding.EventDataFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */
public final class EventValidator {

    public static void clearErrorsForEventData(EventDataFragmentLayoutBinding binding) {
        binding.eventEventType.setError(null);
        binding.eventEventDesc.setError(null);
        binding.eventEventDate.setError(null);
        binding.eventTypeOfPlace.setError(null);
        binding.eventTypeOfPlaceTxt.setError(null);
        binding.eventSrcFirstName.setError(null);
        binding.eventSrcLastName.setError(null);
        binding.eventSrcTelNo.setError(null);
    }

    public static boolean validateEventData(Event event, EventDataFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Event source's telephone number
        if (event.getSrcTelNo() == null || event.getSrcTelNo().trim().isEmpty()) {
            binding.eventSrcTelNo.setError(resources.getString(R.string.validation_event_src_tel_no));
            success = false;
        }

        // Event source's last name
        if (event.getSrcLastName() == null || event.getSrcLastName().trim().isEmpty()) {
            binding.eventSrcLastName.setError(resources.getString(R.string.validation_event_src_last_name));
            success = false;
        }

        // Event source's first name
        if (event.getSrcFirstName() == null || event.getSrcFirstName().trim().isEmpty()) {
            binding.eventSrcFirstName.setError(resources.getString(R.string.validation_event_src_first_name));
            success = false;
        }

        // Type of place & details
        if (event.getTypeOfPlace() == null) {
            binding.eventTypeOfPlace.setError(resources.getString(R.string.validation_event_type_of_place));
            success = false;
        } else {
            if (event.getTypeOfPlace().equals(TypeOfPlace.OTHER)) {
                if (event.getTypeOfPlaceText() == null || event.getTypeOfPlaceText().trim().isEmpty()) {
                    binding.eventTypeOfPlaceTxt.setError(resources.getString(R.string.validation_event_type_of_place_details));
                    success = false;
                }
            }
        }

        // Event date
        if (event.getEventDate() == null) {
            binding.eventEventDate.setError(resources.getString(R.string.validation_event_date));
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

}
