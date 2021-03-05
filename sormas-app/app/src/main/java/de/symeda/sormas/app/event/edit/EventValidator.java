package de.symeda.sormas.app.event.edit;

import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.validation.ValidationHelper;
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;

class EventValidator {

	public static void initializeEventStartDateValidation(final Event event, final FragmentEventEditLayoutBinding contentBinding) {
		if (event != null) {
			ValidationHelper.initDateIntervalValidator(contentBinding.eventStartDate, contentBinding.eventEndDate);
		}
	}
}
