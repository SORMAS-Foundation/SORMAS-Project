package de.symeda.sormas.backend.sormastosormas.entities.eventparticipant;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.ValidationDirection;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildEventParticipantValidationGroupName;

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
	public ValidationErrors validate(SormasToSormasEventParticipantDto sharedData, ValidationDirection direction) {
		EventParticipantDto ep = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors(buildEventParticipantValidationGroupName(ep));

		ValidationErrors personValidationErrors = validatePerson(ep.getPerson(), direction);
		validationErrors.addAll(personValidationErrors);

		final String groupNameTag = Captions.EventParticipant;
		infraValidator.validateRegion(ep.getRegion(), groupNameTag, validationErrors, ep::setRegion, direction);
		infraValidator.validateDistrict(ep.getDistrict(), groupNameTag, validationErrors, ep::setDistrict, direction);

		return validationErrors;
	}

	@Override
	public ValidationErrors validatePreview(SormasToSormasEventParticipantPreview preview, ValidationDirection direction) {
		ValidationErrors validationErrors = new ValidationErrors(buildEventParticipantValidationGroupName(preview));
		validationErrors.addAll(validatePersonPreview(preview.getPerson(), direction));
		return validationErrors;
	}
}
