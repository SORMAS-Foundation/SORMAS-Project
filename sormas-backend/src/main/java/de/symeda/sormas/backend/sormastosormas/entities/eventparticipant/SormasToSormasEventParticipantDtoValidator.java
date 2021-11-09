package de.symeda.sormas.backend.sormastosormas.entities.eventparticipant;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
@LocalBean
public class SormasToSormasEventParticipantDtoValidator
	extends SormasToSormasDtoValidator<EventParticipantDto, SormasToSormasEventParticipantDto, SormasToSormasEventParticipantPreview> {

	public SormasToSormasEventParticipantDtoValidator() {
	}

	@Inject
	protected SormasToSormasEventParticipantDtoValidator(InfrastructureValidator infraValidator) {
		super(infraValidator);
	}

	@Override
	public ValidationErrors validateIncoming(SormasToSormasEventParticipantDto sharedData) {
		EventParticipantDto ep = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = validatePerson(ep.getPerson());
		validationErrors.addAll(personValidationErrors);

		final String groupNameTag = Captions.EventParticipant;
		infraValidator.validateRegion(ep.getRegion(), groupNameTag, validationErrors, ep::setRegion);
		infraValidator.validateDistrit(ep.getDistrict(), groupNameTag, validationErrors, ep::setDistrict);

		return validationErrors;
	}

	@Override
	public ValidationErrors validateIncomingPreview(SormasToSormasEventParticipantPreview preview) {
		return validatePersonPreview(preview.getPerson());
	}

	@Override
	public ValidationErrors validateOutgoing(SormasToSormasEventParticipantDto sharedData) {
		return null;
	}

	@Override
	public ValidationErrors validateOutgoingPreview(SormasToSormasEventParticipantPreview preview) {
		return null;
	}
}
