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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.ValidationHelper;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessorHelper;
import de.symeda.sormas.backend.sormastosormas.entities.caze.ReceivedCaseProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.contact.ReceivedContactProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.event.ReceivedEventProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.eventparticipant.ReceivedEventParticipantProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.sample.ReceivedSampleProcessor;
import de.symeda.sormas.backend.sormastosormas.share.ShareRequestData;

@Stateless
@LocalBean
public class ReceivedDataProcessor {

	@EJB
	private ReceivedDataProcessorHelper dataProcessorHelper;
	@EJB
	private ReceivedCaseProcessor caseProcessor;
	@EJB
	private ReceivedContactProcessor contactProcessor;
	@EJB
	private ReceivedSampleProcessor sampleProcessor;
	@EJB
	private ReceivedEventProcessor eventProcessor;
	@EJB
	private ReceivedEventParticipantProcessor eventParticipantProcessor;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private EventFacadeEjbLocal eventFacade;
	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;

	public List<ValidationErrors> processReceivedData(SormasToSormasDto receivedData) {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		SormasToSormasOriginInfoDto originInfo = receivedData.getOriginInfo();
		ValidationErrors originInfoErrors = dataProcessorHelper.processOriginInfo(originInfo, Captions.sormasToSormasOriginInfo);
		if (originInfoErrors.hasError()) {
			validationErrors.add(originInfoErrors);
		}

		List<SormasToSormasCaseDto> cases = receivedData.getCases();
		if (CollectionUtils.isNotEmpty(cases)) {
			Map<String, CaseDataDto> existingCases =
				caseFacade.getByUuids(cases.stream().map(SormasToSormasCaseDto::getEntity).map(CaseDataDto::getUuid).collect(Collectors.toList()))
					.stream()
					.collect(Collectors.toMap(CaseDataDto::getUuid, Function.identity()));
			cases.forEach(c -> {
				ValidationErrors caseErrors = caseProcessor.processReceivedData(c, existingCases.get(c.getEntity().getUuid()));

				if (caseErrors.hasError()) {
					validationErrors.add(new ValidationErrors(ValidationHelper.buildCaseValidationGroupName(c.getEntity()), caseErrors));
				}
			});
		}

		List<SormasToSormasContactDto> contacts = receivedData.getContacts();
		if (CollectionUtils.isNotEmpty(contacts)) {
			Map<String, ContactDto> existingContacts = contactFacade
				.getByUuids(contacts.stream().map(SormasToSormasContactDto::getEntity).map(ContactDto::getUuid).collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(ContactDto::getUuid, Function.identity()));
			contacts.forEach(c -> {
				ValidationErrors contactErrors = contactProcessor.processReceivedData(c, existingContacts.get(c.getEntity().getUuid()));

				if (contactErrors.hasError()) {
					validationErrors.add(new ValidationErrors(ValidationHelper.buildContactValidationGroupName(c.getEntity()), contactErrors));
				}
			});
		}

		List<SormasToSormasEventDto> events = receivedData.getEvents();
		if (CollectionUtils.isNotEmpty(events)) {
			Map<String, EventDto> existingEvents =
				eventFacade.getByUuids(events.stream().map(SormasToSormasEventDto::getEntity).map(EventDto::getUuid).collect(Collectors.toList()))
					.stream()
					.collect(Collectors.toMap(EventDto::getUuid, Function.identity()));
			events.forEach(e -> {
				ValidationErrors eventErrors = eventProcessor.processReceivedData(e, existingEvents.get(e.getEntity().getUuid()));

				if (eventErrors.hasError()) {
					validationErrors.add(new ValidationErrors(ValidationHelper.buildEventValidationGroupName(e.getEntity()), eventErrors));
				}
			});
		}

		List<SormasToSormasEventParticipantDto> eventParticipants = receivedData.getEventParticipants();
		if (CollectionUtils.isNotEmpty(eventParticipants)) {
			Map<String, EventParticipantDto> existingEventParticipants =
				eventParticipantFacade
					.getByUuids(
						eventParticipants.stream()
							.map(SormasToSormasEventParticipantDto::getEntity)
							.map(EventParticipantDto::getUuid)
							.collect(Collectors.toList()))
					.stream()
					.collect(Collectors.toMap(EventParticipantDto::getUuid, Function.identity()));
			eventParticipants.forEach(ep -> {
				ValidationErrors eventParticipantErrors =
					eventParticipantProcessor.processReceivedData(ep, existingEventParticipants.get(ep.getEntity().getUuid()));

				if (eventParticipantErrors.hasError()) {
					validationErrors
						.add(new ValidationErrors(ValidationHelper.buildEventParticipantValidationGroupName(ep.getEntity()), eventParticipantErrors));
				}
			});
		}

		List<SormasToSormasSampleDto> samples = receivedData.getSamples();
		if (CollectionUtils.isNotEmpty(samples)) {
			Map<String, SampleDto> existingSamples =
				sampleFacade.getByUuids(samples.stream().map(SormasToSormasSampleDto::getEntity).map(SampleDto::getUuid).collect(Collectors.toList()))
					.stream()
					.collect(Collectors.toMap(SampleDto::getUuid, Function.identity()));
			samples.forEach(s -> {
				ValidationErrors contactErrors = sampleProcessor.processReceivedData(s, existingSamples.get(s.getEntity().getUuid()));

				if (contactErrors.hasError()) {
					validationErrors.add(new ValidationErrors(ValidationHelper.buildSampleValidationGroupName(s.getEntity()), contactErrors));
				}
			});
		}

		return validationErrors;
	}

	public List<ValidationErrors> processReceivedRequest(ShareRequestData shareData) {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		SormasToSormasOriginInfoDto originInfo = shareData.getOriginInfo();
		ValidationErrors originInfoErrors = dataProcessorHelper.processOriginInfo(originInfo, Captions.sormasToSormasOriginInfo);
		if (originInfoErrors.hasError()) {
			validationErrors.add(new ValidationErrors(new ValidationErrorGroup(Captions.sormasToSormasOriginInfo), originInfoErrors));
		}

		shareData.getPreviews().getCases().forEach(c -> {
			ValidationErrors caseErrors = caseProcessor.processReceivedPreview(c);

			if (caseErrors.hasError()) {
				validationErrors.add(new ValidationErrors(ValidationHelper.buildCaseValidationGroupName(c), caseErrors));
			}
		});
		shareData.getPreviews().getContacts().forEach(c -> {
			ValidationErrors contactErrors = contactProcessor.processReceivedPreview(c);

			if (contactErrors.hasError()) {
				validationErrors.add(new ValidationErrors(ValidationHelper.buildContactValidationGroupName(c), contactErrors));
			}
		});
		shareData.getPreviews().getEvents().forEach(e -> {
			ValidationErrors eventErrors = eventProcessor.processReceivedPreview(e);

			if (eventErrors.hasError()) {
				validationErrors.add(new ValidationErrors(ValidationHelper.buildEventValidationGroupName(e), eventErrors));
			}
		});
		shareData.getPreviews().getEventParticipants().forEach(ep -> {
			ValidationErrors eventParticipantErrors = eventParticipantProcessor.processReceivedPreview(ep);

			if (eventParticipantErrors.hasError()) {
				validationErrors.add(new ValidationErrors(ValidationHelper.buildEventParticipantValidationGroupName(ep), eventParticipantErrors));
			}
		});

		return validationErrors;
	}
}
