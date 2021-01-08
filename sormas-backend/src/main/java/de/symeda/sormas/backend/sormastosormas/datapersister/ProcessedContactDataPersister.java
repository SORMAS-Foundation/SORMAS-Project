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
import javax.transaction.Transactional;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.ProcessedContactData;
import de.symeda.sormas.backend.sormastosormas.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedContactDataPersister implements ProcessedDataPersister<ProcessedContactData> {

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private ProcessedDataPersisterHelper dataPersisterHelper;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSharedData(ProcessedContactData contactData) throws SormasToSormasValidationException {
		handleValidationError(
			() -> personFacade.savePerson(contactData.getPerson()),
			Captions.Person,
			buildContactValidationGroupName(contactData.getContact()));
		ContactDto savedContact = handleValidationError(
			() -> contactFacade.saveContact(contactData.getContact()),
			Captions.Contact,
			buildContactValidationGroupName(contactData.getContact()));

		if (contactData.getSamples() != null) {
			dataPersisterHelper.persistSharedSamples(contactData.getSamples(), savedContact.getSormasToSormasOriginInfo());
		}
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void persistReturnedData(ProcessedContactData contactData, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasValidationException {
		ContactDto savedContact = handleValidationError(
			() -> contactFacade.saveContact(contactData.getContact()),
			Captions.Contact,
			buildContactValidationGroupName(contactData.getContact()));
		SormasToSormasShareInfo contactShareInfo =
			shareInfoService.getByContactAndOrganization(savedContact.getUuid(), originInfo.getOrganizationId());
		contactShareInfo.setOwnershipHandedOver(false);
		shareInfoService.persist(contactShareInfo);

		handleValidationError(
			() -> personFacade.savePerson(contactData.getPerson()),
			Captions.Person,
			buildContactValidationGroupName(contactData.getContact()));

		if (contactData.getSamples() != null) {
			dataPersisterHelper.persistReturnedSamples(contactData.getSamples(), originInfo);
		}
	}
}
