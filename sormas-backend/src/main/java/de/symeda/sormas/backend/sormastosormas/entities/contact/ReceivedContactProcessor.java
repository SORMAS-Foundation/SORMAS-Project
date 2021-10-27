/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.sormastosormas.entities.contact;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;

@Stateless
@LocalBean
public class ReceivedContactProcessor extends ReceivedDataProcessor<ContactDto, SormasToSormasContactDto, SormasToSormasContactPreview, Contact> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;
	@EJB
	private ContactService contactService;

	@Override
	public void handleReceivedData(SormasToSormasContactDto sharedData, Contact existingData) {
		dataValidator.handleIgnoredProperties(sharedData.getEntity(), ContactFacadeEjb.toDto(existingData));
		dataValidator.handleIgnoredProperties(sharedData.getPerson(), dataValidator.getExistingPerson(existingData));
	}

	@Override
	public ValidationErrors exists(String uuid) {
		ValidationErrors errors = new ValidationErrors();
		if (contactService.exists(
			(cb, contactRoot, cq) -> cb.and(
				cb.equal(contactRoot.get(AbstractDomainObject.UUID), uuid),
				cb.isNull(contactRoot.get(Contact.SORMAS_TO_SORMAS_ORIGIN_INFO)),
				cb.isEmpty(contactRoot.get(Contact.SORMAS_TO_SORMAS_SHARES))))) {

			errors.add(new ValidationErrorGroup(Captions.Contact), new ValidationErrorMessage(Validations.sormasToSormasContactExists));
		}

		return errors;
	}

	@Override
	public ValidationErrors validation(SormasToSormasContactDto sharedData, Contact existingData) {
		return dataValidator.validateContactData(sharedData.getEntity(), sharedData.getPerson(), existingData);
	}

	@Override
	public ValidationErrors validatePreview(SormasToSormasContactPreview preview) {
		return dataValidator.validateContactPreview(preview);
	}
}
