/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.externalmessage.processing.labmessage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.processing.AbstractProcessingFlow;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.api.externalmessage.processing.flow.FlowThen;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;

/**
 * Abstract class defining the flow of processing a lab message allowing to choose between multiple options like create or select a
 * case/contact/event participant and then create or update a sample with pathogen tests
 *
 * The flow is coded in the `run` method.
 */
public abstract class AbstractLabMessageProcessingFlow extends AbstractProcessingFlow {

	protected final CountryReferenceDto country;

	public AbstractLabMessageProcessingFlow(UserDto user, ExternalMessageProcessingFacade processingFacade, CountryReferenceDto country) {
		super(user, processingFacade);
		this.country = country;
	}

	public CompletionStage<ProcessingResult<RelatedSamplesReportsAndPathogenTests>> run(
		ExternalMessageDto externalMessage,
		AbstractRelatedLabMessageHandler relatedLabMessageHandler) {

		//@formatter:off
		return doInitialChecks(externalMessage)
			.then(ignored -> handleRelatedLabMessages(relatedLabMessageHandler, externalMessage))
			.then(ignored -> pickOrCreatePerson(externalMessage))
			.then(p -> pickOrCreateEntry(p.getData(), externalMessage))
				.thenSwitch()
				.when(PersonAndPickOrCreateEntryResult::isNewCase, (f, p) -> doCreateCaseFlow(f, externalMessage))
				.when(PersonAndPickOrCreateEntryResult::isNewContact, (f, p) -> doCreateContactFlow(f, externalMessage))
				.when(PersonAndPickOrCreateEntryResult::isNewEventParticipant, (f, p) -> doCreateEventParticipantFlow(f, externalMessage))
				.when(PersonAndPickOrCreateEntryResult::isSelectedCase, (f, p) -> doCaseSelectedFlow(p.getCaze(), f, externalMessage))
				.when(PersonAndPickOrCreateEntryResult::isSelectedContact, (f, p) -> doContactSelectedFlow(p.getContact(), f, externalMessage))
				.when(PersonAndPickOrCreateEntryResult::isEventParticipantSelected, (f, p) -> doEventParticipantSelectedFlow(p.getEventParticipant(), f, externalMessage))
			.then(sampleResult -> ProcessingResult.of(ProcessingResultStatus.DONE, sampleResult.getData()).asCompletedFuture())
			.getResult();
		//@formatter:on
	}

	private FlowThen<RelatedSamplesReportsAndPathogenTests> doCreateCaseFlow(
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		ExternalMessageDto labMessage) {

		//@formatter:off
		return flow.then(p -> createCase(p.getData().getPerson(), labMessage))
				.then(p -> {
					IntFunction<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleForCase =
						sampleReportIndex -> createOneSampleAndPathogenTests(p.getData(), labMessage, sampleReportIndex, true);
		
					return doPickOrCreateSamplesFlow(
						c -> c.sampleCriteria(new SampleCriteria().caze(p.getData().toReference())),
						createSampleForCase,
						flow,
						labMessage).getResult();
		});
		//@formatter:on
	}

	private FlowThen<RelatedSamplesReportsAndPathogenTests> doCreateContactFlow(
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		ExternalMessageDto labMessage) {

		//@formatter:off
		return flow.then(p -> createContact(p.getData().getPerson(), labMessage))
				.then(p -> {
					IntFunction<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleForContact =
							sampleReportIndex -> createOneSampleAndPathogenTests(p.getData(), labMessage, sampleReportIndex, true);

					return doPickOrCreateSamplesFlow(
							c -> c.sampleCriteria(new SampleCriteria().contact(p.getData().toReference())),
							createSampleForContact,
							flow,
							labMessage).getResult();
		});
		//@formatter:on
	}

	private FlowThen<RelatedSamplesReportsAndPathogenTests> doCreateEventParticipantFlow(
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		ExternalMessageDto labMessage) {

		return flow.then(p -> pickOrCreateEvent(p.getData().getPerson(), labMessage))
			.thenSwitch()
			.when(
				PersonAndPickOrCreateEventResult::isNewEvent,
				(f, p) -> f.then(ignored -> createEvent(labMessage))
					.then(ef -> doCreateEventParticipantAndSamplesFlow(ef.getData(), p.getPerson(), f, labMessage).getResult()))
			.when(
				PersonAndPickOrCreateEventResult::isEventSelected,
				(f, p) -> f.then(e -> validateSelectedEvent(e.getData().getPickOrCreateEventResult().getEvent(), p.getPerson()))
					.thenSwitch()
					.when(
						EventValidationResult::isEventSelected,
						(vf, v) -> doCreateEventParticipantAndSamplesFlow(v.getEvent(), p.getPerson(), vf, labMessage))
					.when(EventValidationResult::isEventParticipantSelected, (vf, v) -> {
						EventDto event = processingFacade.getEventByUuid(p.getEvent().getUuid());

						IntFunction<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleForEventParticipant =
							sampleReportIndex -> createOneSampleAndPathogenTests(v.eventParticipant, event, labMessage, sampleReportIndex, false);

						return vf.then(
							ignored -> doPickOrCreateSamplesFlow(
								c -> c.sampleCriteria(new SampleCriteria().eventParticipant(v.eventParticipant)),
								createSampleForEventParticipant,
								flow,
								labMessage).getResult());
					})
					.when(
						EventValidationResult::isEventSelectionCanceled,
						(vf, v) -> vf.then(ignored -> doCreateEventParticipantFlow(flow, labMessage).getResult()))
					.then(s -> ProcessingResult.continueWith(s.getData()).asCompletedFuture()))
			.then(s -> ProcessingResult.continueWith(s.getData()).asCompletedFuture());
	}

	private FlowThen<RelatedSamplesReportsAndPathogenTests> doCreateEventParticipantAndSamplesFlow(
		EventDto event,
		PersonDto person,
		FlowThen<?> flow,
		ExternalMessageDto labMessage) {

		//@formatter:off
		return flow.then(ignored -> createEventParticipant(event, person, labMessage))
				.then(p -> {
					IntFunction<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleForEventParticipant =
						sampleReportIndex -> createOneSampleAndPathogenTests(
							p.getData().eventParticipant.toReference(), 
							p.getData().getEvent(),
							labMessage,
							sampleReportIndex,
							true);

					return doPickOrCreateSamplesFlow(
						c -> c.sampleCriteria(new SampleCriteria().eventParticipant(p.getData().getEventParticipant().toReference())),
						createSampleForEventParticipant,
						flow,
						labMessage).getResult();
		});
		//@formatter:on
	}

	private FlowThen<RelatedSamplesReportsAndPathogenTests> doPickOrCreateSamplesFlow(
		Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
		IntFunction<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleAndPathogenTests,
		FlowThen<?> flow,
		ExternalMessageDto labMessage) {

		List<SampleReportDto> sampleReports = labMessage.getSampleReportsNullSafe();
		if (sampleReports.size() > 1) {
			flow = flow.then(
				result -> handleMultipleSampleConfirmation().thenCompose(
					next -> ProcessingResult
						.<Void> withStatus(Boolean.TRUE.equals(next) ? ProcessingResultStatus.CONTINUE : ProcessingResultStatus.CANCELED)
						.asCompletedFuture()));
		}

		RelatedSamplesReportsAndPathogenTests allSamplesAndPathogenTests = new RelatedSamplesReportsAndPathogenTests();
		int i = 0;
		do {
			int currentSampleReportIndex = i;
			flow = doSinglePickOrCreateSampleFlow(addSampleSearchCriteria, createSampleAndPathogenTests, flow, i, labMessage).then(r -> {
				allSamplesAndPathogenTests.add(sampleReports.get(currentSampleReportIndex), r.getData());
				return ProcessingResult.continueWith(r.getData()).asCompletedFuture();
			});
			i += 1;
		}
		while (i < sampleReports.size());

		return flow.then(ignored -> ProcessingResult.continueWith(allSamplesAndPathogenTests).asCompletedFuture());
	}

	private FlowThen<RelatedSamplesReportsAndPathogenTests> doCaseSelectedFlow(
		CaseSelectionDto caseSelection,
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		ExternalMessageDto labMessage) {

		CaseDataDto caze = processingFacade.getCaseDataByUuid(caseSelection.getUuid());

		IntFunction<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleForCase =
			sampleReportIndex -> createOneSampleAndPathogenTests(caze, labMessage, sampleReportIndex, false);

		return flow.then(
			ignored -> doPickOrCreateSamplesFlow(
				c -> c.sampleCriteria(new SampleCriteria().caze(caze.toReference())),
				createSampleForCase,
				flow,
				labMessage).getResult());
	}

	private FlowThen<RelatedSamplesReportsAndPathogenTests> doContactSelectedFlow(
		SimilarContactDto contactSelection,
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		ExternalMessageDto labMessage) {

		ContactDto contact = processingFacade.getContactByUuid(contactSelection.getUuid());

		IntFunction<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleForContact =
			sampleReportIndex -> createOneSampleAndPathogenTests(contact, labMessage, sampleReportIndex, false);

		return flow.then(
			ignored -> doPickOrCreateSamplesFlow(
				c -> c.sampleCriteria(new SampleCriteria().contact(contact.toReference())),
				createSampleForContact,
				flow,
				labMessage).getResult());
	}

	private FlowThen<RelatedSamplesReportsAndPathogenTests> doEventParticipantSelectedFlow(
		SimilarEventParticipantDto eventParticipantSelection,
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		ExternalMessageDto labMessage) {

		EventParticipantDto eventParticipant = processingFacade.getEventParticipantByUuid(eventParticipantSelection.getUuid());
		EventDto event = processingFacade.getEventByUuid(eventParticipant.getEvent().getUuid());

		IntFunction<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleForEventParticipant =
			sampleReportIndex -> createOneSampleAndPathogenTests(eventParticipant.toReference(), event, labMessage, sampleReportIndex, false);

		return flow.then(
			ignored -> doPickOrCreateSamplesFlow(
				c -> c.sampleCriteria(new SampleCriteria().eventParticipant(eventParticipant.toReference())),
				createSampleForEventParticipant,
				flow,
				labMessage).getResult());
	}

	private FlowThen<SampleAndPathogenTests> doSinglePickOrCreateSampleFlow(
		Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
		IntFunction<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleAndPathogenTests,
		FlowThen<?> flow,
		int sampleReportIndex,
		ExternalMessageDto labMessage) {

		//@formatter:off
		return flow.then(ignored -> pickOrCreateSample(addSampleSearchCriteria, sampleReportIndex, labMessage))
			.thenSwitch()
				.when(PickOrCreateSampleResult::isNewSample, (sf, p) -> sf.then(ignored -> createSampleAndPathogenTests.apply(sampleReportIndex)))
				.when(PickOrCreateSampleResult::isSelectedSample, (sf, p) -> sf.then(s -> editSample(s.getData().getSample(), sampleReportIndex, labMessage)))
			.then(s -> ProcessingResult.continueWith(s.getData()).asCompletedFuture());
		//@formatter:on

	}

	private CompletionStage<ProcessingResult<RelatedSamplesReportsAndPathogenTests>> handleRelatedLabMessages(
		AbstractRelatedLabMessageHandler relatedLabMessageHandler,
		ExternalMessageDto labMessage) {

		// TODO currently, related messages handling is just done if one sample report exists. That's why this works.
		SampleReportDto firstSampleReport = labMessage.getSampleReportsNullSafe().get(0);
		return relatedLabMessageHandler.handle(labMessage).thenCompose(result -> {
			AbstractRelatedLabMessageHandler.HandlerResultStatus status = result.getStatus();
			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CANCELED) {
				return ProcessingResult.<RelatedSamplesReportsAndPathogenTests> withStatus(ProcessingResultStatus.CANCELED).asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CANCELED_WITH_UPDATES) {
				return ProcessingResult.<RelatedSamplesReportsAndPathogenTests> withStatus(ProcessingResultStatus.CANCELED_WITH_CORRECTIONS)
					.asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.HANDLED) {
				SampleDto relatedSample = result.getSample();
				RelatedSamplesReportsAndPathogenTests handlerResult = new RelatedSamplesReportsAndPathogenTests();
				handlerResult.add(
					firstSampleReport,
					new SampleAndPathogenTests(relatedSample, processingFacade.getPathogenTestsBySample(relatedSample.toReference())));

				return ProcessingResult.of(ProcessingResultStatus.DONE, handlerResult).asCompletedFuture();
			}

			return ProcessingResult.<RelatedSamplesReportsAndPathogenTests> continueWith(null).asCompletedFuture();
		});
	}

	private CompletionStage<ProcessingResult<PersonAndPickOrCreateEntryResult>> pickOrCreateEntry(
		PersonDto person,
		ExternalMessageDto externalMessage) {

		PersonReferenceDto personRef = person.toReference();
		List<CaseSelectionDto> similarCases = getSimilarCases(personRef, externalMessage);
		List<SimilarContactDto> similarContacts = getSimilarContacts(personRef, externalMessage);
		List<SimilarEventParticipantDto> similarEventParticipants = getSimilarEventParticipants(personRef, externalMessage);

		HandlerCallback<PickOrCreateEntryResult> callback = new HandlerCallback<>();

		handlePickOrCreateEntry(similarCases, similarContacts, similarEventParticipants, externalMessage, callback);

		return callback.futureResult.thenCompose(p -> {
			if (p.getStatus().isCanceled()) {
				return ProcessingResult.<PersonAndPickOrCreateEntryResult> withStatus(p.getStatus()).asCompletedFuture();
			}

			return ProcessingResult.of(p.getStatus(), new PersonAndPickOrCreateEntryResult(person, p.getData())).asCompletedFuture();
		});
	}

	protected abstract void handlePickOrCreateEntry(
		List<CaseSelectionDto> similarCases,
		List<SimilarContactDto> similarContacts,
		List<SimilarEventParticipantDto> similarEventParticipants,
		ExternalMessageDto externalMessageDto,
		HandlerCallback<PickOrCreateEntryResult> callback);

	private List<SimilarContactDto> getSimilarContacts(PersonReferenceDto selectedPerson, ExternalMessageDto externalMessage) {

		if (processingFacade.isFeatureDisabled(FeatureType.CONTACT_TRACING)
			|| !processingFacade.hasAllUserRights(UserRight.CONTACT_CREATE, UserRight.CONTACT_EDIT)) {
			return Collections.emptyList();
		}
		ContactSimilarityCriteria contactSimilarityCriteria = new ContactSimilarityCriteria();
		contactSimilarityCriteria.setPerson(selectedPerson);
		contactSimilarityCriteria.setDisease(externalMessage.getDisease());

		return processingFacade.getMatchingContacts(contactSimilarityCriteria);
	}

	private List<SimilarEventParticipantDto> getSimilarEventParticipants(PersonReferenceDto selectedPerson, ExternalMessageDto labMessage) {

		if (processingFacade.isFeatureDisabled(FeatureType.EVENT_SURVEILLANCE)
			|| !processingFacade.hasAllUserRights(UserRight.EVENTPARTICIPANT_CREATE, UserRight.EVENTPARTICIPANT_EDIT)) {
			return Collections.emptyList();
		}

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setPerson(selectedPerson);
		eventParticipantCriteria.setDisease(labMessage.getDisease());

		return processingFacade.getMatchingEventParticipants(eventParticipantCriteria);
	}

	private CompletionStage<ProcessingResult<CaseDataDto>> createCase(PersonDto person, ExternalMessageDto labMessage) {

		CaseDataDto caze = buildCase(person, labMessage);

		HandlerCallback<CaseDataDto> callback = new HandlerCallback<>();
		handleCreateCase(caze, person, labMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handleCreateCase(
		CaseDataDto caze,
		PersonDto person,
		ExternalMessageDto labMessage,
		HandlerCallback<CaseDataDto> callback);

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createOneSampleAndPathogenTests(
		CaseDataDto caze,
		ExternalMessageDto labMessage,
		int sampleReportIndex,
		boolean entityCreated) {

		SampleDto sample = SampleDto.build(user.toReference(), caze.toReference());
		return createOneSampleAndPathogenTests(sample, caze.getDisease(), labMessage, sampleReportIndex, entityCreated);
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createOneSampleAndPathogenTests(
		ContactDto contact,
		ExternalMessageDto labMessage,
		int sampleReportIndex,
		boolean entityCreated) {

		SampleDto sample = SampleDto.build(user.toReference(), contact.toReference());
		return createOneSampleAndPathogenTests(sample, contact.getDisease(), labMessage, sampleReportIndex, entityCreated);
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createOneSampleAndPathogenTests(
		EventParticipantReferenceDto eventParticipant,
		EventDto event,
		ExternalMessageDto labMessage,
		int sampleReportIndex,
		boolean entityCreated) {

		SampleDto sample = SampleDto.build(user.toReference(), eventParticipant);
		return createOneSampleAndPathogenTests(sample, event.getDisease(), labMessage, sampleReportIndex, entityCreated);
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createOneSampleAndPathogenTests(
		SampleDto sample,
		Disease disease,
		ExternalMessageDto labMessage,
		int sampleReportIndex,
		boolean entityCreated) {

		ExternalMessageMapper.forLabMessage(labMessage).mapToSample(sample, labMessage.getSampleReportsNullSafe().get(sampleReportIndex));
		List<PathogenTestDto> pathogenTests = LabMessageProcessingHelper.buildPathogenTests(sample, sampleReportIndex, labMessage, user);
		HandlerCallback<SampleAndPathogenTests> callback = new HandlerCallback<>();
		handleCreateSampleAndPathogenTests(
			sample,
			pathogenTests,
			disease,
			labMessage,
			entityCreated,
			isLastSample(labMessage, sampleReportIndex),
			callback);

		return callback.futureResult;
	}

	public abstract CompletionStage<Boolean> handleMultipleSampleConfirmation();

	protected abstract void handleCreateSampleAndPathogenTests(
		SampleDto sample,
		List<PathogenTestDto> pathogenTests,
		Disease disease,
		ExternalMessageDto labMessage,
		boolean entityCreated,
		boolean lastSample,
		HandlerCallback<SampleAndPathogenTests> callback);

	private CompletionStage<ProcessingResult<ContactDto>> createContact(PersonDto person, ExternalMessageDto labMessage) {

		ContactDto contact = buildContact(labMessage, person);
		HandlerCallback<ContactDto> callback = new HandlerCallback<>();
		handleCreateContact(contact, person, labMessage, callback);
		return callback.futureResult;
	}

	protected abstract void handleCreateContact(
		ContactDto contact,
		PersonDto person,
		ExternalMessageDto labMessage,
		HandlerCallback<ContactDto> callback);

	private ContactDto buildContact(ExternalMessageDto externalMessageDto, PersonDto person) {

		ContactDto contactDto = ContactDto.build(null, externalMessageDto.getDisease(), null, null);
		contactDto.setReportingUser(user.toReference());
		contactDto.setReportDateTime(externalMessageDto.getMessageDateTime());
		contactDto.setPerson(person.toReference());
		return contactDto;
	}

	private CompletionStage<ProcessingResult<PersonAndPickOrCreateEventResult>> pickOrCreateEvent(
		PersonDto person,
		ExternalMessageDto externalMessageDto) {

		HandlerCallback<PickOrCreateEventResult> callback = new HandlerCallback<>();
		handlePickOrCreateEvent(externalMessageDto, callback);

		return callback.futureResult.thenCompose(p -> {
			if (p.getStatus().isCanceled()) {
				return ProcessingResult.<PersonAndPickOrCreateEventResult> withStatus(p.getStatus()).asCompletedFuture();
			}

			return ProcessingResult.of(p.getStatus(), new PersonAndPickOrCreateEventResult(person, p.getData())).asCompletedFuture();
		});
	}

	protected abstract void handlePickOrCreateEvent(ExternalMessageDto labMessage, HandlerCallback<PickOrCreateEventResult> callback);

	private CompletionStage<ProcessingResult<EventDto>> createEvent(ExternalMessageDto labMessage) {

		EventDto event = EventDto.build(country, user, labMessage.getDisease());
		event.setDiseaseVariant(labMessage.getDiseaseVariant());
		event.setDiseaseVariantDetails(labMessage.getDiseaseVariantDetails());

		HandlerCallback<EventDto> callback = new HandlerCallback<>();
		handleCreateEvent(event, callback);

		return callback.futureResult;
	}

	protected abstract void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback);

	private CompletionStage<ProcessingResult<EventAndParticipant>> createEventParticipant(
		EventDto event,
		PersonDto person,
		ExternalMessageDto labMessage) {

		EventParticipantDto eventParticipant = buildEventParticipant(event, person);
		HandlerCallback<EventParticipantDto> callback = new HandlerCallback<>();
		handleCreateEventParticipant(eventParticipant, event, labMessage, callback);

		return callback.futureResult.thenCompose(ep -> {
			if (ep.getStatus().isCanceled()) {
				return ProcessingResult.<EventAndParticipant> withStatus(ep.getStatus()).asCompletedFuture();
			}

			return ProcessingResult.of(ep.getStatus(), new EventAndParticipant(event, ep.getData())).asCompletedFuture();
		});
	}

	protected abstract void handleCreateEventParticipant(
		EventParticipantDto eventParticipant,
		EventDto event,
		ExternalMessageDto labMessage,
		HandlerCallback<EventParticipantDto> callback);

	private EventParticipantDto buildEventParticipant(EventDto eventDto, PersonDto person) {

		EventParticipantDto eventParticipant = EventParticipantDto.build(eventDto.toReference(), user.toReference());
		eventParticipant.setPerson(person);
		return eventParticipant;
	}

	private CompletionStage<ProcessingResult<EventValidationResult>> validateSelectedEvent(EventIndexDto event, PersonDto person) {

		CompletableFuture<ProcessingResult<EventValidationResult>> ret = new CompletableFuture<>();

		EventCriteria eventCriteria = new EventCriteria();
		eventCriteria.setPerson(person.toReference());
		eventCriteria.setUserFilterIncluded(false);
		List<EventIndexDto> personEvents = processingFacade.getEventsByCriteria(eventCriteria);

		EventValidationResult validationResult = new EventValidationResult();
		if (personEvents.contains(event)) {
			// event participant already exists
			return confirmPickExistingEventParticipant().thenCompose(useEventParticipant -> {
				if (Boolean.TRUE.equals(useEventParticipant)) {
					validationResult.setEventParticipant(processingFacade.getEventParticipantRefByEventAndPerson(event.getUuid(), person.getUuid()));
				} else {
					validationResult.setEventSelectionCanceled(true);
				}

				return ProcessingResult.continueWith(validationResult).asCompletedFuture();
			});
		} else {
			validationResult.setEvent(processingFacade.getEventByUuid(event.getUuid()));
			ret.complete(ProcessingResult.continueWith(validationResult));
		}

		return ret;
	}

	protected abstract CompletionStage<Boolean> confirmPickExistingEventParticipant();

	private CompletionStage<ProcessingResult<PickOrCreateSampleResult>> pickOrCreateSample(
		Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
		int sampleReportIndex,
		ExternalMessageDto labMessage) {

		SampleSimilarityCriteria sampleSimilarityCriteria = createSampleSimilarCriteria(labMessage.getSampleReportsNullSafe().get(sampleReportIndex));
		addSampleSearchCriteria.accept(sampleSimilarityCriteria);

		List<SampleDto> selectableSamples = processingFacade.getSamplesByCriteria(sampleSimilarityCriteria.getSampleCriteria());
		List<SampleDto> similarSamples = processingFacade.getSimilarSamples(sampleSimilarityCriteria);
		List<SampleDto> otherSamples = selectableSamples.stream().filter(s -> !similarSamples.contains(s)).collect(Collectors.toList());

		PickOrCreateSampleResult result = new PickOrCreateSampleResult();
		if (similarSamples.isEmpty() && otherSamples.isEmpty()) {
			result.setNewSample(true);
			return ProcessingResult.continueWith(result).asCompletedFuture();
		}

		HandlerCallback<PickOrCreateSampleResult> callback = new HandlerCallback<>();
		handlePickOrCreateSample(similarSamples, otherSamples, labMessage, sampleReportIndex, callback);

		return callback.futureResult;
	}

	protected abstract void handlePickOrCreateSample(
		List<SampleDto> similarSamples,
		List<SampleDto> otherSamples,
		ExternalMessageDto labMessage,
		int sampleReportIndex,
		HandlerCallback<PickOrCreateSampleResult> callback);

	private SampleSimilarityCriteria createSampleSimilarCriteria(SampleReportDto sampleReport) {

		SampleSimilarityCriteria sampleCriteria = new SampleSimilarityCriteria();
		sampleCriteria.setLabSampleId(sampleReport.getLabSampleId());
		sampleCriteria.setSampleDateTime(sampleReport.getSampleDateTime());
		sampleCriteria.setSampleMaterial(sampleReport.getSampleMaterial());

		return sampleCriteria;
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> editSample(
		SampleDto sample,
		int sampleReportIndex,
		ExternalMessageDto labMessage) {

		List<PathogenTestDto> newTests = LabMessageProcessingHelper.buildPathogenTests(sample, sampleReportIndex, labMessage, user);
		HandlerCallback<SampleAndPathogenTests> callback = new HandlerCallback<>();
		handleEditSample(sample, newTests, labMessage, isLastSample(labMessage, sampleReportIndex), callback);

		return callback.futureResult;
	}

	private boolean isLastSample(ExternalMessageDto labMessage, int sampleReportIndex) {
		if (sampleReportIndex >= labMessage.getSampleReportsNullSafe().size()) {
			throw new IllegalArgumentException("The sample report index is out of bounds.");
		}
		return labMessage.getSampleReportsNullSafe().size() == sampleReportIndex + 1;
	}

	protected abstract void handleEditSample(
		SampleDto sample,
		List<PathogenTestDto> newPathogenTests,
		ExternalMessageDto labMessage,
		boolean lastSample,
		HandlerCallback<SampleAndPathogenTests> callback);

	private static final class PersonAndPickOrCreateEventResult {

		private final PersonDto person;
		private final PickOrCreateEventResult pickOrCreateEventResult;

		public PersonAndPickOrCreateEventResult(PersonDto person, PickOrCreateEventResult pickOrCreateEventResult) {
			this.person = person;
			this.pickOrCreateEventResult = pickOrCreateEventResult;
		}

		public PersonDto getPerson() {
			return person;
		}

		public PickOrCreateEventResult getPickOrCreateEventResult() {
			return pickOrCreateEventResult;
		}

		public boolean isNewEvent() {
			return pickOrCreateEventResult.isNewEvent();
		}

		public boolean isEventSelected() {
			return pickOrCreateEventResult.getEvent() != null;
		}

		public EventIndexDto getEvent() {
			return pickOrCreateEventResult.getEvent();
		}
	}

	private static class EventValidationResult {

		private EventDto event;

		private EventParticipantReferenceDto eventParticipant;

		private boolean eventSelectionCanceled;

		public EventDto getEvent() {
			return event;
		}

		public void setEvent(EventDto event) {
			this.event = event;
		}

		public boolean isEventSelected() {
			return event != null;
		}

		public EventParticipantReferenceDto getEventParticipant() {
			return eventParticipant;
		}

		public void setEventParticipant(EventParticipantReferenceDto eventParticipant) {
			this.eventParticipant = eventParticipant;
		}

		public boolean isEventParticipantSelected() {
			return eventParticipant != null;
		}

		public boolean isEventSelectionCanceled() {
			return eventSelectionCanceled;
		}

		public void setEventSelectionCanceled(boolean eventSelectionCanceled) {
			this.eventSelectionCanceled = eventSelectionCanceled;
		}
	}

	private static class EventAndParticipant {

		private final EventDto event;
		private final EventParticipantDto eventParticipant;

		public EventAndParticipant(EventDto event, EventParticipantDto eventParticipant) {
			this.event = event;
			this.eventParticipant = eventParticipant;
		}

		public EventDto getEvent() {
			return event;
		}

		public EventParticipantDto getEventParticipant() {
			return eventParticipant;
		}
	}
}
