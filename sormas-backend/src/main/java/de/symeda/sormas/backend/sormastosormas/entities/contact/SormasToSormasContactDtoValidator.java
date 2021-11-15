package de.symeda.sormas.backend.sormastosormas.entities.contact;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
@LocalBean
public class SormasToSormasContactDtoValidator
	extends SormasToSormasDtoValidator<ContactDto, SormasToSormasContactDto, SormasToSormasContactPreview> {

	public SormasToSormasContactDtoValidator() {
	}

	@Inject
	protected SormasToSormasContactDtoValidator(InfrastructureValidator infraValidator) {
		super(infraValidator);
	}

	@Override
	public ValidationErrors validateIncoming(SormasToSormasContactDto sharedData) {
		ContactDto contact = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = validatePerson(sharedData.getPerson());
		validationErrors.addAll(personValidationErrors);

		String groupNameTag = Captions.Contact;
		infraValidator.validateRegion(contact.getRegion(), groupNameTag, validationErrors, contact::setRegion);
		infraValidator.validateDistrit(contact.getDistrict(), groupNameTag, validationErrors, contact::setDistrict);
		infraValidator.validateCommunity(contact.getCommunity(), groupNameTag, validationErrors, contact::setCommunity);

		validateEpiData(contact.getEpiData(), validationErrors);

		return validationErrors;
	}

	public ValidationErrors validateIncomingPreview(SormasToSormasContactPreview preview) {
		ValidationErrors validationErrors = new ValidationErrors();

		String groupNameTag = Captions.Contact;
		infraValidator.validateRegion(preview.getRegion(), groupNameTag, validationErrors, preview::setRegion);
		infraValidator.validateDistrit(preview.getDistrict(), groupNameTag, validationErrors, preview::setDistrict);
		infraValidator.validateCommunity(preview.getCommunity(), groupNameTag, validationErrors, preview::setCommunity);

		return validationErrors;
	}

	@Override
	public ValidationErrors validateOutgoing(SormasToSormasContactDto sharedData) {
		return null;
	}

	@Override
	public ValidationErrors validateOutgoingPreview(SormasToSormasContactPreview contactPreview) {
		return null;
	}
}
