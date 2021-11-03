package de.symeda.sormas.backend.sormastosormas.entities.event;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.sormastosormas.data.validation.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


@Stateless
@LocalBean
public class SormasToSormasEventDtoValidator extends SormasToSormasDtoValidator<EventDto, SormasToSormasEventDto, SormasToSormasEventPreview, Event> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;

	@Override
	public ValidationErrors validateIncoming(SormasToSormasEventDto sharedData) {
		return dataValidator.validateEventData(sharedData.getEntity());
	}

	@Override
	public ValidationErrors validateIncomingPreview(SormasToSormasEventPreview preview) {
		ValidationErrors eventValidationErrors = new ValidationErrors();
		dataValidator.validateLocation(preview.getEventLocation(), Captions.Event, eventValidationErrors);
		return eventValidationErrors;
	}

	@Override
	public ValidationErrors validateOutgoing(SormasToSormasEventDto sharedData) {
		return null;
	}

	@Override
	public ValidationErrors validateOutgoingPreview(SormasToSormasEventPreview preview) {
		return null;
	}
}
