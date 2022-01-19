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
import de.symeda.sormas.backend.sormastosormas.data.validation.ValidationDirection;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildEventValidationGroupName;

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
	public ValidationErrors validate(SormasToSormasEventDto sharedData, ValidationDirection direction) {
		EventDto event = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors(buildEventValidationGroupName(event));
		validateLocation(event.getEventLocation(), Captions.Event, validationErrors, direction);

		return validationErrors;
	}

	@Override
	public ValidationErrors validatePreview(SormasToSormasEventPreview preview, ValidationDirection direction) {
		ValidationErrors eventValidationErrors = new ValidationErrors(buildEventValidationGroupName(preview));
		validateLocation(preview.getEventLocation(), Captions.Event, eventValidationErrors, direction);
		return eventValidationErrors;
	}
}
