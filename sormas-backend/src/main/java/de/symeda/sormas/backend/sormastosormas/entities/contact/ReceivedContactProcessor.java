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

import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class ReceivedContactProcessor
	extends
	ReceivedDataProcessor<Contact, ContactDto, SormasToSormasContactDto, SormasToSormasContactPreview, Contact, ContactService, SormasToSormasContactDtoValidator> {

	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;


	public ReceivedContactProcessor() {
	}

	@Inject
	protected ReceivedContactProcessor(
		ContactService service,
		UserService userService,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade,
		SormasToSormasContactDtoValidator validator) {
		super(service, userService, configFacade, validator);
	}

	@Override
	public void handleReceivedData(SormasToSormasContactDto sharedData, Contact existingData) {
		handleIgnoredProperties(sharedData.getEntity(), contactFacade.toDto(existingData));
		handleIgnoredProperties(
			sharedData.getPerson(),
			Optional.ofNullable(existingData).map(c -> PersonFacadeEjb.toDto(c.getPerson())).orElse(null));

		ContactDto contact = sharedData.getEntity();
		PersonDto person = sharedData.getPerson();

		contact.setPerson(person.toReference());
		updateReportingUser(contact, existingData);
	}

	@Override
	public ValidationErrors existsNotShared(String uuid) {
		return existsNotShared(
			uuid,
			Contact.SORMAS_TO_SORMAS_ORIGIN_INFO,
			Contact.SORMAS_TO_SORMAS_SHARES,
			Captions.Contact,
			Validations.sormasToSormasContactExists);
	}
}
