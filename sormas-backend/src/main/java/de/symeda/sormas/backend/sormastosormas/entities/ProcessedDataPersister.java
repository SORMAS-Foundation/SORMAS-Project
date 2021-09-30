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

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
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
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private EventFacadeEjb.EventFacadeEjbLocal eventFacade;

	public void persistSharedData(SormasToSormasDto processedData, SormasToSormasOriginInfoDto originInfo) throws SormasToSormasValidationException {

		List<SormasToSormasCaseDto> cases = processedData.getCases();
		if (CollectionUtils.isNotEmpty(cases)) {
			for (SormasToSormasCaseDto c : cases) {
				caseDataPersister.persistSharedData(c, originInfo);
			}
		}

		List<SormasToSormasContactDto> contacts = processedData.getContacts();
		if (CollectionUtils.isNotEmpty(contacts)) {
			for (SormasToSormasContactDto c : contacts) {
				contactDataPersister.persistSharedData(c, originInfo);
			}
		}

		List<SormasToSormasEventDto> events = processedData.getEvents();
		if (CollectionUtils.isNotEmpty(events)) {
			for (SormasToSormasEventDto e : events) {
				eventDataPersister.persistSharedData(e, originInfo);
			}
		}

		List<SormasToSormasEventParticipantDto> eventParticipants = processedData.getEventParticipants();
		if (CollectionUtils.isNotEmpty(eventParticipants)) {
			for (SormasToSormasEventParticipantDto ep : eventParticipants) {
				eventParticipantDataPersister.persistSharedData(ep, originInfo);
			}
		}

		List<SormasToSormasSampleDto> samples = processedData.getSamples();
		if (CollectionUtils.isNotEmpty(samples)) {
			for (SormasToSormasSampleDto s : samples) {
				sampleDataPersister.persistSharedData(s, originInfo);
			}
		}
	}

	public void persistReturnedData(SormasToSormasDto processedData, SormasToSormasOriginInfoDto originInfoDto)
		throws SormasToSormasValidationException {
		List<SormasToSormasCaseDto> cases = processedData.getCases();
		if (CollectionUtils.isNotEmpty(cases)) {
			for (SormasToSormasCaseDto c : cases) {
				caseDataPersister.persistReturnedData(c, originInfoDto);
			}
		}

		List<SormasToSormasContactDto> contacts = processedData.getContacts();
		if (CollectionUtils.isNotEmpty(contacts)) {
			for (SormasToSormasContactDto c : contacts) {
				contactDataPersister.persistReturnedData(c, originInfoDto);
			}
		}

		List<SormasToSormasEventDto> events = processedData.getEvents();
		if (CollectionUtils.isNotEmpty(events)) {
			for (SormasToSormasEventDto e : events) {
				eventDataPersister.persistReturnedData(e, originInfoDto);
			}
		}

		List<SormasToSormasEventParticipantDto> eventParticipants = processedData.getEventParticipants();
		if (CollectionUtils.isNotEmpty(eventParticipants)) {
			for (SormasToSormasEventParticipantDto ep : eventParticipants) {
				eventParticipantDataPersister.persistReturnedData(ep, originInfoDto);
			}
		}

		List<SormasToSormasSampleDto> samples = processedData.getSamples();
		if (CollectionUtils.isNotEmpty(samples)) {
			for (SormasToSormasSampleDto s : samples) {
				sampleDataPersister.persistReturnedData(s, originInfoDto);
			}
		}
	}

	public void persistSyncData(SormasToSormasDto processedData, SormasToSormasOriginInfoDto originInfoDto, ShareTreeCriteria shareTreeCriteria)
		throws SormasToSormasValidationException {
		List<SormasToSormasCaseDto> cases = processedData.getCases();
		if (CollectionUtils.isNotEmpty(cases)) {
			for (SormasToSormasCaseDto c : cases) {
				caseDataPersister.persistSyncData(c, originInfoDto, shareTreeCriteria);
			}
		}

		List<SormasToSormasContactDto> contacts = processedData.getContacts();
		if (CollectionUtils.isNotEmpty(contacts)) {
			for (SormasToSormasContactDto c : contacts) {
				contactDataPersister.persistSyncData(c, originInfoDto, shareTreeCriteria);
			}
		}

		List<SormasToSormasEventDto> events = processedData.getEvents();
		if (CollectionUtils.isNotEmpty(events)) {
			for (SormasToSormasEventDto e : events) {
				eventDataPersister.persistSyncData(e, originInfoDto, shareTreeCriteria);
			}
		}

		List<SormasToSormasEventParticipantDto> eventParticipants = processedData.getEventParticipants();
		if (CollectionUtils.isNotEmpty(eventParticipants)) {
			for (SormasToSormasEventParticipantDto ep : eventParticipants) {
				eventParticipantDataPersister.persistSyncData(ep, originInfoDto, shareTreeCriteria);
			}
		}

		List<SormasToSormasSampleDto> samples = processedData.getSamples();
		if (CollectionUtils.isNotEmpty(samples)) {
			for (SormasToSormasSampleDto s : samples) {
				sampleDataPersister.persistSyncData(s, originInfoDto, shareTreeCriteria);
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
