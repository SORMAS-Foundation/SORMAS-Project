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
import javax.transaction.Transactional;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.ProcessedCaseData;
import de.symeda.sormas.backend.sormastosormas.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfoService;

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
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;

	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSharedData(ProcessedCaseData caseData) throws SormasToSormasValidationException {
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
			dataPersisterHelper.persistSharedSamples(caseData.getSamples(), savedCase.getSormasToSormasOriginInfo());
		}
	}

	@Transactional(rollbackOn = {
		Exception.class })
	public void persistReturnedData(ProcessedCaseData caseData, SormasToSormasOriginInfoDto originInfo) throws SormasToSormasValidationException {
		CaseDataDto caze = caseData.getCaze();

		CaseDataDto savedCase = handleValidationError(() -> caseFacade.saveCase(caze), Captions.CaseData, buildCaseValidationGroupName(caze));
		SormasToSormasShareInfo shareInfo = shareInfoService.getByCaseAndOrganization(savedCase.getUuid(), originInfo.getOrganizationId());
		shareInfo.setOwnershipHandedOver(false);
		shareInfoService.persist(shareInfo);

		handleValidationError(() -> personFacade.savePerson(caseData.getPerson()), Captions.Person, buildCaseValidationGroupName(caze));

		SormasToSormasOriginInfoDto savedOriginInfo = null;

		if (caseData.getAssociatedContacts() != null) {
			for (SormasToSormasCaseDto.AssociatedContactDto associatedContact : caseData.getAssociatedContacts()) {
				ContactDto contact = associatedContact.getContact();

				handleValidationError(
					() -> personFacade.savePerson(associatedContact.getPerson()),
					Captions.Person,
					buildContactValidationGroupName(contact));

				SormasToSormasShareInfo contactShareInfo =
					shareInfoService.getByContactAndOrganization(contact.getUuid(), originInfo.getOrganizationId());
				if (contactShareInfo == null) {
					if (savedOriginInfo == null) {
						savedOriginInfo = originInfoFacade.saveOriginInfo(originInfo);
					}

					contact.setSormasToSormasOriginInfo(savedOriginInfo);
				} else {
					contactShareInfo.setOwnershipHandedOver(false);
					shareInfoService.persist(contactShareInfo);
				}

				handleValidationError(() -> contactFacade.saveContact(contact), Captions.Contact, buildContactValidationGroupName(contact));
			}
		}

		if (caseData.getSamples() != null) {
			dataPersisterHelper.persistReturnedSamples(caseData.getSamples(), savedOriginInfo == null ? originInfo : savedOriginInfo);
		}
	}
}
