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

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedContactDataPersister extends ProcessedDataPersister<ContactDto, SormasToSormasContactDto, Contact> {

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;

	@Override
	protected SormasToSormasShareInfoService getShareInfoService() {
		return shareInfoService;
	}

	@Override
	protected SormasToSormasOriginInfoFacadeEjb getOriginInfoFacade() {
		return originInfoFacade;
	}

	@Override
	public void persistSharedData(SormasToSormasContactDto processedData, Contact existingContact) throws SormasToSormasValidationException {
		persistProcessedData(processedData, existingContact == null);
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(ContactDto entity, String organizationId) {
		return shareInfoService.getByContactAndOrganization(entity.getUuid(), organizationId);
	}

	private void persistProcessedData(SormasToSormasContactDto processedData, boolean isCreate) throws SormasToSormasValidationException {
		ContactDto contact = processedData.getEntity();
		ValidationErrorGroup contactValidationGroupName = buildContactValidationGroupName(contact);

		final PersonDto person = processedData.getPerson();
		if (isCreate) {
			// save person first during creation
			handleValidationError(() -> personFacade.savePerson(person, false, false, false), Captions.Person, contactValidationGroupName, person);

			handleValidationError(() -> contactFacade.save(contact, true, true, false, false), Captions.Contact, contactValidationGroupName, contact);
		} else {
			//save contact first during update
			handleValidationError(() -> contactFacade.save(contact, true, true, false, false), Captions.Contact, contactValidationGroupName, contact);
			handleValidationError(() -> personFacade.savePerson(person, false, false, false), Captions.Person, contactValidationGroupName, contact);
		}
	}
}
