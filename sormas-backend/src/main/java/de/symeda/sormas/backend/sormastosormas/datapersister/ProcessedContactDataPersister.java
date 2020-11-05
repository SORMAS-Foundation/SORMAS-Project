/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.datapersister;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.ProcessedContactData;
import de.symeda.sormas.backend.sormastosormas.ProcessedDataPersister;

@Stateless
@LocalBean
public class ProcessedContactDataPersister implements ProcessedDataPersister<ProcessedContactData> {

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private ProcessedDataPersisterHelper dataPersisterHelper;

	public void persistProcessedData(ProcessedContactData contactData) throws SormasToSormasValidationException {
		handleValidationError(
			() -> personFacade.savePerson(contactData.getPerson()),
			Captions.Person,
			buildContactValidationGroupName(contactData.getContact()));
		ContactDto savedContact = handleValidationError(
			() -> contactFacade.saveContact(contactData.getContact()),
			Captions.Contact,
			buildContactValidationGroupName(contactData.getContact()));

		if (contactData.getSamples() != null) {
			dataPersisterHelper.saveSamples(contactData.getSamples(), savedContact.getSormasToSormasOriginInfo());
		}
	}
}
