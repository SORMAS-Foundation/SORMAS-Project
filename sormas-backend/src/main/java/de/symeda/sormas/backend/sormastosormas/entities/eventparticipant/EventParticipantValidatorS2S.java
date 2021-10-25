package de.symeda.sormas.backend.sormastosormas.entities.eventparticipant;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.validation.Sormas2SormasEntityValidator;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasCommonDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;

import javax.ejb.EJB;

public class EventParticipantValidatorS2S implements Sormas2SormasEntityValidator<EventParticipantDto, SormasToSormasEventParticipantPreview> {

	@EJB
	private Sormas2SormasCommonDataValidator commonDataValidator;
	@EJB
	private InfrastructureValidator infraValidator;

	@Override
	public ValidationErrors validateInboundEntity(EventParticipantDto eventParticipant) {

		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = commonDataValidator.validatePerson(eventParticipant.getPerson());
		validationErrors.addAll(personValidationErrors);

		infraValidator.validateRegion(eventParticipant.getRegion(), Captions.EventParticipant, validationErrors, eventParticipant::setRegion);
		infraValidator.validateDistrit(eventParticipant.getDistrict(), Captions.EventParticipant, validationErrors, eventParticipant::setDistrict);
		return validationErrors;
	}

	@Override
	public ValidationErrors validateInboundPreviewEntity(SormasToSormasEventParticipantPreview preview) {
		return commonDataValidator.validatePersonPreview(preview.getPerson());
	}
}
