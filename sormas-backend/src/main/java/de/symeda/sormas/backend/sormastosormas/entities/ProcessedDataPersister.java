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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.sormastosormas.immunization.SormasToSormasImmunizationDto;
import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.entities.caze.ProcessedCaseDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.contact.ProcessedContactDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.event.ProcessedEventDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.eventparticipant.ProcessedEventParticipantDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.immunization.ProcessedImmunizationDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.sample.ProcessedSampleDataPersister;

@Stateless
@LocalBean
public class ProcessedDataPersister {

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
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private EventFacadeEjb.EventFacadeEjbLocal eventFacade;

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
		if (CollectionUtils.isNotEmpty(samples)) {
			for (SormasToSormasSampleDto s : samples) {
				sampleDataPersister.persistSharedData(s, originInfo, existingEntities.getSamples().get(s.getEntity().getUuid()));
			}
		}

		List<SormasToSormasImmunizationDto> immunizations = processedData.getImmunizations();
		if (CollectionUtils.isNotEmpty(immunizations)) {
			for (SormasToSormasImmunizationDto s : immunizations) {
				immunizationDataPersister.persistSharedData(s, originInfo, existingEntities.getImmunizations().get(s.getEntity().getUuid()));
			}
		}
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
