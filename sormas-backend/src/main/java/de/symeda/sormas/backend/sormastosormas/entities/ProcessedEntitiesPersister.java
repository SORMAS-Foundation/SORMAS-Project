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

package de.symeda.sormas.backend.sormastosormas.entities;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.DuplicateResult;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.entities.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.entities.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageDto;
import de.symeda.sormas.api.sormastosormas.entities.immunization.SormasToSormasImmunizationDto;
import de.symeda.sormas.api.sormastosormas.entities.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.entities.surveillancereport.SormasToSormasSurveillanceReportDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb.ExternalMessageFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.caze.ProcessedCaseDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.contact.ProcessedContactDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.event.ProcessedEventDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.eventparticipant.ProcessedEventParticipantDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.immunization.ProcessedImmunizationDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.sample.ProcessedSampleDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.surveillancereport.ProcessedSurveillanceReportDataPersister;

@Stateless
@LocalBean
public class ProcessedEntitiesPersister {

	@EJB
	private ProcessedCaseDataPersister caseDataPersister;
	@EJB
	private ProcessedContactDataPersister contactDataPersister;
	@EJB
	private ProcessedSampleDataPersister sampleDataPersister;
	@EJB
	private ProcessedEventDataPersister eventDataPersister;
	@EJB
	private ProcessedEventParticipantDataPersister eventParticipantDataPersister;
	@EJB
	private ProcessedImmunizationDataPersister immunizationDataPersister;
	@EJB
	private ProcessedSurveillanceReportDataPersister surveillanceReportDataPersister;

	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private EventFacadeEjb.EventFacadeEjbLocal eventFacade;
	@EJB
	private ExternalMessageFacadeEjbLocal externalMessageFacade;

	public void persistSharedData(SormasToSormasDto processedData, SormasToSormasOriginInfoDto originInfo, ShareDataExistingEntities existingEntities)
		throws SormasToSormasValidationException {

		List<SormasToSormasCaseDto> cases = processedData.getCases();
		if (CollectionUtils.isNotEmpty(cases)) {
			for (SormasToSormasCaseDto c : cases) {
				caseDataPersister.persistSharedData(c, originInfo, existingEntities.getCases().get(c.getEntity().getUuid()));
			}
		}

		List<SormasToSormasContactDto> contacts = processedData.getContacts();
		if (CollectionUtils.isNotEmpty(contacts)) {
			for (SormasToSormasContactDto c : contacts) {
				contactDataPersister.persistSharedData(c, originInfo, existingEntities.getContacts().get(c.getEntity().getUuid()));
			}
		}

		List<SormasToSormasEventDto> events = processedData.getEvents();
		if (CollectionUtils.isNotEmpty(events)) {
			for (SormasToSormasEventDto e : events) {
				eventDataPersister.persistSharedData(e, originInfo, existingEntities.getEvents().get(e.getEntity().getUuid()));
			}
		}

		List<SormasToSormasEventParticipantDto> eventParticipants = processedData.getEventParticipants();
		if (CollectionUtils.isNotEmpty(eventParticipants)) {
			for (SormasToSormasEventParticipantDto ep : eventParticipants) {
				eventParticipantDataPersister
					.persistSharedData(ep, originInfo, existingEntities.getEventParticipants().get(ep.getEntity().getUuid()));
			}
		}

		List<SormasToSormasSampleDto> samples = processedData.getSamples();
		List<SormasToSormasExternalMessageDto> externalMessages = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(samples)) {
			for (SormasToSormasSampleDto s : samples) {
				sampleDataPersister.persistSharedData(s, originInfo, existingEntities.getSamples().get(s.getEntity().getUuid()));
				if (!CollectionUtils.isEmpty(s.getExternalMessages())) {
					externalMessages.addAll(s.getExternalMessages());
				}
			}
		}

		List<SormasToSormasImmunizationDto> immunizations = processedData.getImmunizations();
		if (CollectionUtils.isNotEmpty(immunizations)) {
			for (SormasToSormasImmunizationDto s : immunizations) {
				immunizationDataPersister.persistSharedData(s, originInfo, existingEntities.getImmunizations().get(s.getEntity().getUuid()));
			}
		}

		List<SormasToSormasSurveillanceReportDto> reports = processedData.getSurveillanceReports();
		if (CollectionUtils.isNotEmpty(reports)) {
			for (SormasToSormasSurveillanceReportDto r : reports) {
				surveillanceReportDataPersister
					.persistSharedData(r, originInfo, existingEntities.getSurveillanceReports().get(r.getEntity().getUuid()));

				if (r.getExternalMessage() != null) {
					externalMessages.add(r.getExternalMessage());
				}
			}
		}

		if (!externalMessages.isEmpty()) {
			for (SormasToSormasExternalMessageDto s2sExternalMessage : externalMessages) {
				ExternalMessageDto externalMessage = s2sExternalMessage.getEntity();

				handleValidationError(
					() -> externalMessageFacade.save(externalMessage, false, false),
					Captions.ExternalMessage,
					buildValidationGroupName(Captions.ExternalMessage, externalMessage),
					externalMessage);
			}
		}

	}

	public DuplicateResult checkForSimilarEntities(SormasToSormasDto processedData, ShareDataExistingEntities existingEntities) {
		List<SormasToSormasCaseDto> cases = processedData.getCases();
		if (CollectionUtils.isNotEmpty(cases)) {
			List<SormasToSormasCaseDto> newCases =
				cases.stream().filter(c -> !existingEntities.getCases().containsKey(c.getEntity().getUuid())).collect(Collectors.toList());

			for (SormasToSormasCaseDto caze : newCases) {
				return caseDataPersister.checkForSimilarEntities(caze);
			}
		}

		List<SormasToSormasContactDto> contacts = processedData.getContacts();
		if (CollectionUtils.isNotEmpty(contacts)) {
			List<SormasToSormasContactDto> newContacts =
				contacts.stream().filter(c -> !existingEntities.getContacts().containsKey(c.getEntity().getUuid())).collect(Collectors.toList());

			for (SormasToSormasContactDto c : newContacts) {
				return contactDataPersister.checkForSimilarEntities(c);
			}
		}

		return DuplicateResult.none();
	}

	public void persistSyncData(
		SormasToSormasDto processedData,
		SormasToSormasOriginInfoDto originInfoDto,
		ShareTreeCriteria shareTreeCriteria,
		ShareDataExistingEntities existingEntities)
		throws SormasToSormasValidationException {
		List<SormasToSormasCaseDto> cases = processedData.getCases();
		if (CollectionUtils.isNotEmpty(cases)) {
			for (SormasToSormasCaseDto c : cases) {
				caseDataPersister.persistSyncData(c, originInfoDto, existingEntities.getCases().get(c.getEntity().getUuid()));
			}
		}

		List<SormasToSormasContactDto> contacts = processedData.getContacts();
		if (CollectionUtils.isNotEmpty(contacts)) {
			for (SormasToSormasContactDto c : contacts) {
				contactDataPersister.persistSyncData(c, originInfoDto, existingEntities.getContacts().get(c.getEntity().getUuid()));
			}
		}

		List<SormasToSormasEventDto> events = processedData.getEvents();
		if (CollectionUtils.isNotEmpty(events)) {
			for (SormasToSormasEventDto e : events) {
				eventDataPersister.persistSyncData(e, originInfoDto, existingEntities.getEvents().get(e.getEntity().getUuid()));
			}
		}

		List<SormasToSormasEventParticipantDto> eventParticipants = processedData.getEventParticipants();
		if (CollectionUtils.isNotEmpty(eventParticipants)) {
			for (SormasToSormasEventParticipantDto ep : eventParticipants) {
				eventParticipantDataPersister
					.persistSyncData(ep, originInfoDto, existingEntities.getEventParticipants().get(ep.getEntity().getUuid()));
			}
		}

		List<SormasToSormasSampleDto> samples = processedData.getSamples();
		if (CollectionUtils.isNotEmpty(samples)) {
			for (SormasToSormasSampleDto s : samples) {
				sampleDataPersister.persistSyncData(s, originInfoDto, existingEntities.getSamples().get(s.getEntity().getUuid()));
			}
		}

		List<SormasToSormasImmunizationDto> immunizations = processedData.getImmunizations();
		if (CollectionUtils.isNotEmpty(immunizations)) {
			for (SormasToSormasImmunizationDto i : immunizations) {
				immunizationDataPersister.persistSyncData(i, originInfoDto, existingEntities.getImmunizations().get(i.getEntity().getUuid()));
			}
		}

		List<SormasToSormasSurveillanceReportDto> reports = processedData.getSurveillanceReports();
		if (CollectionUtils.isNotEmpty(reports)) {
			for (SormasToSormasSurveillanceReportDto r : reports) {
				surveillanceReportDataPersister
					.persistSyncData(r, originInfoDto, existingEntities.getSurveillanceReports().get(r.getEntity().getUuid()));
			}
		}

		if (CollectionUtils.isNotEmpty(cases)) {
			caseFacade.syncSharesAsync(shareTreeCriteria);
		} else if (CollectionUtils.isNotEmpty(contacts)) {
			contactFacade.syncSharesAsync(shareTreeCriteria);
		}

		if (CollectionUtils.isNotEmpty(events)) {
			eventFacade.syncSharesAsync(shareTreeCriteria);
		}
	}

}
