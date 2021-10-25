package de.symeda.sormas.backend.sormastosormas.entities.contact;

import de.symeda.sormas.api.contact.ContactDto;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.validation.Sormas2SormasEntityValidator;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasCommonDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;

import javax.ejb.EJB;

public class ContactValidatorS2S implements Sormas2SormasEntityValidator<ContactDto, SormasToSormasContactPreview> {

	@EJB
	private InfrastructureValidator infraValidator;
	@EJB
	private Sormas2SormasCommonDataValidator commonDataValidator;

	@Override
	public ValidationErrors validateInboundEntity(ContactDto contact) {
		ValidationErrors validationErrors = new ValidationErrors();

		infraValidator.validateRegion(contact.getRegion(), Captions.Contact, validationErrors, contact::setRegion);
		infraValidator.validateDistrit(contact.getDistrict(), Captions.Contact, validationErrors, contact::setDistrict);
		infraValidator.validateCommunity(contact.getCommunity(), Captions.Contact, validationErrors, contact::setCommunity);

		commonDataValidator.validateEpiData(contact.getEpiData(), validationErrors);

		return validationErrors;
	}

	@Override
	public ValidationErrors validateInboundPreviewEntity(SormasToSormasContactPreview preview) {
		ValidationErrors validationErrors = new ValidationErrors();

		infraValidator.validateRegion(preview.getRegion(), Captions.Contact, validationErrors, preview::setRegion);
		infraValidator.validateDistrit(preview.getDistrict(), Captions.Contact, validationErrors, preview::setDistrict);
		infraValidator.validateCommunity(preview.getCommunity(), Captions.Contact, validationErrors, preview::setCommunity);

		return validationErrors;
	}
}
