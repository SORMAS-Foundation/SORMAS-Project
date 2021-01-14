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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import org.apache.commons.lang.mutable.MutableBoolean;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sample.SampleDto;
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
	private SormasToSormasOriginInfoFacadeEjbLocal oriInfoFacade;

	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSharedData(ProcessedCaseData caseData) throws SormasToSormasValidationException {
		persistProcessedData(caseData, null, (caze, contact) -> {
			contact.setSormasToSormasOriginInfo(caze.getSormasToSormasOriginInfo());
		}, (caze, sample) -> {
			sample.setSormasToSormasOriginInfo(caze.getSormasToSormasOriginInfo());
		}, true);
	}

	@Transactional(rollbackOn = {
		Exception.class })
	public void persistReturnedData(ProcessedCaseData caseData, SormasToSormasOriginInfoDto originInfo) throws SormasToSormasValidationException {
		final MutableBoolean originInfoSaved = new MutableBoolean();

		persistProcessedData(caseData, (caze) -> {
			SormasToSormasShareInfo shareInfo = shareInfoService.getByCaseAndOrganization(caze.getUuid(), originInfo.getOrganizationId());
			shareInfo.setOwnershipHandedOver(false);
			shareInfoService.persist(shareInfo);
		}, (caze, contact) -> {
			SormasToSormasShareInfo contactShareInfo =
				shareInfoService.getByContactAndOrganization(contact.getUuid(), originInfo.getOrganizationId());
			if (contactShareInfo == null) {
				if (!originInfoSaved.booleanValue()) {
					oriInfoFacade.saveOriginInfo(originInfo);
					originInfoSaved.setValue(true);
				}

				contact.setSormasToSormasOriginInfo(originInfo);
			} else {
				contactShareInfo.setOwnershipHandedOver(false);
				shareInfoService.persist(contactShareInfo);
			}
		}, (caze, sample) -> {
			SormasToSormasShareInfo sampleShareInfo = shareInfoService.getBySampleAndOrganization(sample.getUuid(), originInfo.getOrganizationId());
			if (sampleShareInfo == null) {
				if (!originInfoSaved.booleanValue()) {
					oriInfoFacade.saveOriginInfo(originInfo);
					originInfoSaved.setValue(true);
				}

				sample.setSormasToSormasOriginInfo(originInfo);
			} else {
				sampleShareInfo.setOwnershipHandedOver(false);
				shareInfoService.persist(sampleShareInfo);
			}
		}, false);
	}

	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSyncData(ProcessedCaseData caseData) throws SormasToSormasValidationException {
		SormasToSormasOriginInfoDto originInfo = caseData.getOriginInfo();

		persistProcessedData(caseData, (caze) -> {
			SormasToSormasOriginInfoDto caseOriginInfo = caze.getSormasToSormasOriginInfo();
			caseOriginInfo.setOwnershipHandedOver(originInfo.isOwnershipHandedOver());
			caseOriginInfo.setComment(originInfo.getComment());

			oriInfoFacade.saveOriginInfo(caseOriginInfo);
		}, (caze, contact) -> {
			if (contact.getSormasToSormasOriginInfo() == null) {
				contact.setSormasToSormasOriginInfo(caze.getSormasToSormasOriginInfo());
			}
		}, (caze, sample) -> {
			if (sample.getSormasToSormasOriginInfo() == null) {
				sample.setSormasToSormasOriginInfo(caze.getSormasToSormasOriginInfo());
			}
		}, false);
	}

	private void persistProcessedData(
		ProcessedCaseData caseData,
		Consumer<CaseDataDto> afterSaveCase,
		BiConsumer<CaseDataDto, ContactDto> beforeSaveContact,
		BiConsumer<CaseDataDto, SampleDto> beforeSaveSample,
		boolean isCreate)
		throws SormasToSormasValidationException {
		CaseDataDto caze = caseData.getCaze();

		final CaseDataDto savedCase;
		if (isCreate) {
			// save person first during creation
			handleValidationError(() -> personFacade.savePerson(caseData.getPerson(), false), Captions.Person, buildCaseValidationGroupName(caze));
			savedCase = handleValidationError(() -> caseFacade.saveCase(caze, true, false), Captions.CaseData, buildCaseValidationGroupName(caze));
		} else {
			//save case first during update
			savedCase = handleValidationError(() -> caseFacade.saveCase(caze, true, false), Captions.CaseData, buildCaseValidationGroupName(caze));
			handleValidationError(() -> personFacade.savePerson(caseData.getPerson(), false), Captions.Person, buildCaseValidationGroupName(caze));
		}

		if (afterSaveCase != null) {
			afterSaveCase.accept(savedCase);
		}

		if (caseData.getAssociatedContacts() != null) {
			for (SormasToSormasCaseDto.AssociatedContactDto associatedContact : caseData.getAssociatedContacts()) {
				ContactDto contact = associatedContact.getContact();

				if (beforeSaveContact != null) {
					beforeSaveContact.accept(savedCase, contact);
				}

				if (isCreate || !contactFacade.exists(contact.getUuid())) {
					// save person first during creation
					handleValidationError(
						() -> personFacade.savePerson(associatedContact.getPerson(), false),
						Captions.Person,
						buildContactValidationGroupName(contact));
					handleValidationError(
						() -> contactFacade.saveContact(contact, true, false),
						Captions.Contact,
						buildContactValidationGroupName(contact));
				} else {
					//save contact first during update
					handleValidationError(
						() -> contactFacade.saveContact(contact, true, false),
						Captions.Contact,
						buildContactValidationGroupName(contact));
					handleValidationError(
						() -> personFacade.savePerson(associatedContact.getPerson(), false),
						Captions.Person,
						buildContactValidationGroupName(contact));

				}
			}
		}

		if (caseData.getSamples() != null) {
			dataPersisterHelper.persistSamples(caseData.getSamples(), beforeSaveSample != null ? (s) -> beforeSaveSample.accept(savedCase, s) : null);
		}
	}
}
