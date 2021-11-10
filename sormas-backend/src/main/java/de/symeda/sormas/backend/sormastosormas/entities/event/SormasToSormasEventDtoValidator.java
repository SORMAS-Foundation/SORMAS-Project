package de.symeda.sormas.backend.sormastosormas.entities.event;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

@Stateless
@LocalBean
public class SormasToSormasEventDtoValidator extends SormasToSormasDtoValidator<EventDto, SormasToSormasEventDto, SormasToSormasEventPreview> {

	public SormasToSormasEventDtoValidator() {
	}

	@Inject
	protected SormasToSormasEventDtoValidator(InfrastructureValidator infraValidator) {
		super(infraValidator);
	}

	@Override
	public ValidationErrors validateIncoming(SormasToSormasEventDto sharedData) {
		EventDto event = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors();
		validateLocation(event.getEventLocation(), Captions.Event, validationErrors);

		return validationErrors;
	}

	@Override
	public ValidationErrors validateIncomingPreview(SormasToSormasEventPreview preview) {
		ValidationErrors eventValidationErrors = new ValidationErrors();
		validateLocation(preview.getEventLocation(), Captions.Event, eventValidationErrors);
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
