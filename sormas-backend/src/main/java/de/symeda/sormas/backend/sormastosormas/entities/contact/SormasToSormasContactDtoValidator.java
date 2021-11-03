package de.symeda.sormas.backend.sormastosormas.entities.contact;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.sormastosormas.data.validation.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


@Stateless
@LocalBean
public class SormasToSormasContactDtoValidator extends SormasToSormasDtoValidator<ContactDto, SormasToSormasContactDto, SormasToSormasContactPreview, Contact> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;

	@Override
	public ValidationErrors validateIncoming(SormasToSormasContactDto sharedData) {
		return dataValidator.validateContactData(sharedData.getEntity(), sharedData.getPerson());
	}

	@Override
	public ValidationErrors validateIncomingPreview(SormasToSormasContactPreview preview) {
		return dataValidator.validateContactPreview(preview);
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
