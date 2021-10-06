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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessorHelper;

@Stateless
@LocalBean
public class ReceivedContactProcessor implements ReceivedDataProcessor<ContactDto, SormasToSormasContactDto, SormasToSormasContactPreview, Contact> {

	@EJB
	private ReceivedDataProcessorHelper dataProcessorHelper;
	@EJB
	private ContactService contactService;
	@EJB
	private InfrastructureValidator infraValidator;

	@Override
	public ValidationErrors processReceivedData(SormasToSormasContactDto receivedContact, Contact existingContact) {

		ContactDto contact = receivedContact.getEntity();
		PersonDto person = receivedContact.getPerson();

		ValidationErrors uuidError = validateSharedUuid(contact.getUuid());
		if (uuidError.hasError()) {
			return uuidError;
		}

		return processContactData(contact, person, existingContact);
	}

	@Override
	public ValidationErrors processReceivedPreview(SormasToSormasContactPreview preview) {
		ValidationErrors uuidError = validateSharedUuid(preview.getUuid());
		if (uuidError.hasError()) {
			return uuidError;
		}

		return processContactPreview(preview);
	}

	private ValidationErrors processContactData(ContactDto contact, PersonDto person, Contact existingContact) {
		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = dataProcessorHelper.processPerson(person);
		validationErrors.addAll(personValidationErrors);

		contact.setPerson(person.toReference());
		dataProcessorHelper.updateReportingUser(contact, existingContact);

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(contact.getRegion(), contact.getDistrict(), contact.getCommunity());

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.Contact, validationErrors, (infrastructure -> {
			contact.setRegion(infrastructure.getRegion());
			contact.setDistrict(infrastructure.getDistrict());
			contact.setCommunity(infrastructure.getCommunity());
		}));

		dataProcessorHelper.processEpiData(contact.getEpiData(), validationErrors);

		return validationErrors;
	}

	private ValidationErrors validateSharedUuid(String uuid) {
		ValidationErrors errors = new ValidationErrors();

		if (contactService.exists(
			(cb, contactRoot) -> cb.and(
				cb.equal(contactRoot.get(Contact.UUID), uuid),
				cb.isNull(contactRoot.get(Contact.SORMAS_TO_SORMAS_ORIGIN_INFO)),
				cb.isEmpty(contactRoot.get(Contact.SORMAS_TO_SORMAS_SHARES))))) {

			errors.add(new ValidationErrorGroup(Captions.Contact), new ValidationErrorMessage(Validations.sormasToSormasContactExists));
		}

		return errors;
	}

	private ValidationErrors processContactPreview(SormasToSormasContactPreview contact) {
		ValidationErrors validationErrors = new ValidationErrors();

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(contact.getRegion(), contact.getDistrict(), contact.getCommunity());

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.Contact, validationErrors, (infrastructure -> {
			contact.setRegion(infrastructure.getRegion());
			contact.setDistrict(infrastructure.getDistrict());
			contact.setCommunity(infrastructure.getCommunity());
		}));

		return validationErrors;
	}
}
