package de.symeda.sormas.backend.sormastosormas.entities.event;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventPreview;
import de.symeda.sormas.api.sormastosormas.validation.Sormas2SormasEntityValidator;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasCommonDataValidator;

import javax.ejb.EJB;

public class EventValidatorS2S implements Sormas2SormasEntityValidator<EventDto, SormasToSormasEventPreview> {

	@EJB
	private Sormas2SormasCommonDataValidator commonDataValidator;

	@Override
	public ValidationErrors validateInboundEntity(EventDto event) {
		ValidationErrors validationErrors = new ValidationErrors();
		LocationDto eventLocation = event.getEventLocation();
		commonDataValidator.validateLocation(eventLocation, Captions.Event, validationErrors);
		return validationErrors;
	}

	@Override
	public ValidationErrors validateInboundPreviewEntity(SormasToSormasEventPreview sormasToSormasEventPreview) {
		return null;
	}
}
