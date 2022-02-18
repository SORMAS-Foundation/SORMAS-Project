package de.symeda.sormas.backend.sormastosormas.entities.contact;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.ValidationDirection;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;

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
	public ValidationErrors validate(SormasToSormasContactDto sharedData, ValidationDirection direction) {
		ContactDto contact = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors(buildContactValidationGroupName(contact));

		ValidationErrors personValidationErrors = validatePerson(sharedData.getPerson(), direction);
		validationErrors.addAll(personValidationErrors);

		String groupNameTag = Captions.Contact;
		infraValidator.validateRegion(contact.getRegion(), groupNameTag, validationErrors, contact::setRegion, direction);
		infraValidator.validateDistrict(contact.getDistrict(), groupNameTag, validationErrors, contact::setDistrict, direction);
		infraValidator.validateCommunity(contact.getCommunity(), groupNameTag, validationErrors, contact::setCommunity, direction);
		infraValidator.validateDistrict(contact.getReportingDistrict(), groupNameTag, validationErrors, contact::setReportingDistrict, direction);

		validateEpiData(contact.getEpiData(), validationErrors, direction);

		return validationErrors;
	}

	public ValidationErrors validatePreview(SormasToSormasContactPreview preview, ValidationDirection direction) {
		ValidationErrors validationErrors = new ValidationErrors(buildContactValidationGroupName(preview));

		ValidationErrors personValidationErrors = validatePersonPreview(preview.getPerson(), direction);
		validationErrors.addAll(personValidationErrors);

		String groupNameTag = Captions.Contact;

		infraValidator.validateRegion(preview.getRegion(), groupNameTag, validationErrors, preview::setRegion, direction);
		infraValidator.validateDistrict(preview.getDistrict(), groupNameTag, validationErrors, preview::setDistrict, direction);
		infraValidator.validateCommunity(preview.getCommunity(), groupNameTag, validationErrors, preview::setCommunity, direction);

		return validationErrors;
	}
}
