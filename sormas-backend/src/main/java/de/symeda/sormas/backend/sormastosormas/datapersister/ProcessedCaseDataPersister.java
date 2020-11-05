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

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildCaseValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.ProcessedCaseData;
import de.symeda.sormas.backend.sormastosormas.ProcessedDataPersister;

@Stateless
@LocalBean
public class ProcessedCaseDataPersister implements ProcessedDataPersister<ProcessedCaseData> {

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private ProcessedDataPersisterHelper dataPersisterHelper;

	public void persistProcessedData(ProcessedCaseData caseData) throws SormasToSormasValidationException {
		CaseDataDto caze = caseData.getCaze();

		handleValidationError(() -> personFacade.savePerson(caseData.getPerson()), Captions.Person, buildCaseValidationGroupName(caze));
		CaseDataDto savedCase = handleValidationError(() -> caseFacade.saveCase(caze), Captions.CaseData, buildCaseValidationGroupName(caze));

		if (caseData.getAssociatedContacts() != null) {
			for (SormasToSormasCaseDto.AssociatedContactDto associatedContact : caseData.getAssociatedContacts()) {
				ContactDto contact = associatedContact.getContact();

				handleValidationError(
					() -> personFacade.savePerson(associatedContact.getPerson()),
					Captions.Person,
					buildContactValidationGroupName(contact));

				// set the persisted origin info to avoid outdated entity issue
				contact.setSormasToSormasOriginInfo(savedCase.getSormasToSormasOriginInfo());

				handleValidationError(() -> contactFacade.saveContact(contact), Captions.Contact, buildContactValidationGroupName(contact));
			}
		}

		if (caseData.getSamples() != null) {
			dataPersisterHelper.saveSamples(caseData.getSamples(), savedCase.getSormasToSormasOriginInfo());
		}
	}
}
