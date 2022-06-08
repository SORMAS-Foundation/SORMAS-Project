/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.externalmessage.labmessage.processing;

import de.symeda.sormas.ui.externalmessage.processing.AbstractProcessingFlow;
import de.symeda.sormas.ui.externalmessage.processing.PersonAndPickOrCreateEntryResult;
import de.symeda.sormas.ui.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.externalmessage.processing.flow.FlowThen;
import de.symeda.sormas.ui.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.ui.externalmessage.processing.flow.ProcessingResultStatus;
import de.symeda.sormas.api.sample.SampleCriteria;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
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
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.externalmessage.ExternalMessageMapper;
import java.util.stream.Collectors;

/**
 * Abstract class defining the flow of processing a lab message allowing to choose between multiple options like create or select a
 * case/contact/event participant and then create or update a sample with pathogen tests
 *
 * The flow is coded in the `run` method.
 */
public abstract class AbstractLabMessageProcessingFlow extends AbstractProcessingFlow {

	protected final CountryReferenceDto country;

	public AbstractLabMessageProcessingFlow(UserDto user, CountryReferenceDto country) {
		super(user);
		this.country = country;
	}

	public CompletionStage<ProcessingResult<SampleAndPathogenTests>> run(
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

	private FlowThen<SampleAndPathogenTests> doCreateCaseFlow(FlowThen<PersonAndPickOrCreateEntryResult> flow, ExternalMessageDto labMessage) {

		return flow.then(p -> createCase(p.getData().getPerson(), labMessage)).then(c -> createSampleAndPathogenTests(c.getData(), labMessage, true));
	}

	private FlowThen<SampleAndPathogenTests> doCreateContactFlow(FlowThen<PersonAndPickOrCreateEntryResult> flow, ExternalMessageDto labMessage) {

		return flow.then(p -> createContact(p.getData().getPerson(), labMessage))
			.then(c -> createSampleAndPathogenTests(c.getData(), labMessage, true));
	}

	private FlowThen<SampleAndPathogenTests> doCreateEventParticipantFlow(
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		ExternalMessageDto labMessage) {

		return flow.then(p -> pickOrCreateEvent(p.getData().getPerson(), labMessage))
			.thenSwitch()
			.when(
				PersonAndPickOrCreateEventResult::isNewEvent,
				(f, p) -> f.then(ignored -> createEvent(labMessage))
					.then(ef -> doCreateEventParticipantAndSampleFlow(ef.getData(), p.getPerson(), f, labMessage).getResult()))
			.when(
				PersonAndPickOrCreateEventResult::isEventSelected,
				(f, p) -> f.then(e -> validateSelectedEvent(e.getData().getPickOrCreateEventResult().getEvent(), p.getPerson()))
					.thenSwitch()
					.when(
						EventValidationResult::isEventSelected,
						(vf, v) -> doCreateEventParticipantAndSampleFlow(v.getEvent(), p.getPerson(), vf, labMessage))
					.when(
						EventValidationResult::isEventParticipantSelected,
						(vf, v) -> doPickOrCreateSampleFlow(
							c -> c.sampleCriteria(new SampleCriteria().eventParticipant(v.getEventParticipant())),
							() -> {
							EventDto event = FacadeProvider.getEventFacade().getEventByUuid(p.getEvent().getUuid(), false);

							return createSampleAndPathogenTests(v.getEventParticipant(), event, labMessage, false);
						}, vf, labMessage))
					.when(
						EventValidationResult::isEventSelectionCanceled,
						(vf, v) -> vf.then(ignored -> doCreateEventParticipantFlow(flow, labMessage).getResult()))
					.then(s -> ProcessingResult.continueWith(s.getData()).asCompletedFuture()))
			.then(s -> ProcessingResult.continueWith(s.getData()).asCompletedFuture());
	}

	private FlowThen<SampleAndPathogenTests> doCreateEventParticipantAndSampleFlow(
		EventDto event,
		PersonDto person,
		FlowThen<?> flow,
		ExternalMessageDto labMessage) {

		return flow.then(ignored -> createEventParticipant(event, person, labMessage))
			.then(ep -> createSampleAndPathogenTests(ep.getData().getEventParticipant().toReference(), ep.getData().getEvent(), labMessage, true));
	}

	private FlowThen<SampleAndPathogenTests> doCaseSelectedFlow(
		CaseSelectionDto caseSelection,
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		ExternalMessageDto labMessage) {

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseSelection.getUuid());

		return doPickOrCreateSampleFlow(
			c -> c.sampleCriteria(new SampleCriteria().caze(caze.toReference())),
			() -> createSampleAndPathogenTests(caze, labMessage, false),
			flow,
			labMessage);
	}

	private FlowThen<SampleAndPathogenTests> doPickOrCreateSampleFlow(
		Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
		Supplier<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleAndPathogenTests,
		FlowThen<?> flow,
		ExternalMessageDto labMessage) {

		return flow.then(ignored -> pickOrCreateSample(addSampleSearchCriteria, labMessage))
			.thenSwitch()
			.when(PickOrCreateSampleResult::isNewSample, (sf, p) -> sf.then(ignored -> createSampleAndPathogenTests.get()))
			.when(PickOrCreateSampleResult::isSelectedSample, (sf, p) -> sf.then(s -> editSample(s.getData().getSample(), labMessage)))
			.then(s -> ProcessingResult.continueWith(s.getData()).asCompletedFuture());
	}

	private FlowThen<SampleAndPathogenTests> doContactSelectedFlow(
		SimilarContactDto contactSelection,
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		ExternalMessageDto labMessage) {

		ContactDto contact = FacadeProvider.getContactFacade().getByUuid(contactSelection.getUuid());
		return doPickOrCreateSampleFlow(
			c -> c.sampleCriteria(new SampleCriteria().contact(contact.toReference())),
			() -> createSampleAndPathogenTests(contact, labMessage, false),
			flow,
			labMessage);
	}

	private FlowThen<SampleAndPathogenTests> doEventParticipantSelectedFlow(
		SimilarEventParticipantDto eventParticipantSelection,
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		ExternalMessageDto labMessage) {

		EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getByUuid(eventParticipantSelection.getUuid());
		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);

		return doPickOrCreateSampleFlow(
			c -> c.sampleCriteria(new SampleCriteria().eventParticipant(eventParticipant.toReference())),
			() -> createSampleAndPathogenTests(eventParticipant.toReference(), event, labMessage, false),
			flow,
			labMessage);
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> handleRelatedLabMessages(
		AbstractRelatedLabMessageHandler relatedLabMessageHandler,
		ExternalMessageDto labMessage) {

		return relatedLabMessageHandler.handle(labMessage).thenCompose(result -> {
			AbstractRelatedLabMessageHandler.HandlerResultStatus status = result.getStatus();
			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CANCELED) {
				return ProcessingResult.<SampleAndPathogenTests> withStatus(ProcessingResultStatus.CANCELED).asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CANCELED_WITH_UPDATES) {
				return ProcessingResult.<SampleAndPathogenTests> withStatus(ProcessingResultStatus.CANCELED_WITH_CORRECTIONS).asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.HANDLED) {
				SampleDto relatedSample = result.getSample();
				return ProcessingResult
					.of(
						ProcessingResultStatus.DONE,
						new SampleAndPathogenTests(relatedSample, FacadeProvider.getPathogenTestFacade().getAllBySample(relatedSample.toReference())))
					.asCompletedFuture();
			}

			return ProcessingResult.<SampleAndPathogenTests> continueWith(null).asCompletedFuture();
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

		ContactSimilarityCriteria contactSimilarityCriteria = new ContactSimilarityCriteria();
		contactSimilarityCriteria.setPerson(selectedPerson);
		contactSimilarityCriteria.setDisease(externalMessage.getTestedDisease());

		return FacadeProvider.getContactFacade().getMatchingContacts(contactSimilarityCriteria);
	}

	private List<SimilarEventParticipantDto> getSimilarEventParticipants(PersonReferenceDto selectedPerson, ExternalMessageDto labMessage) {

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setPerson(selectedPerson);
		eventParticipantCriteria.setDisease(labMessage.getTestedDisease());

		return FacadeProvider.getEventParticipantFacade().getMatchingEventParticipants(eventParticipantCriteria);
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

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createSampleAndPathogenTests(
		CaseDataDto caze,
		ExternalMessageDto labMessage,
		boolean entityCreated) {

		SampleDto sample = SampleDto.build(user.toReference(), caze.toReference());
		return createSampleAndPathogenTests(sample, caze.getDisease(), labMessage, entityCreated);
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createSampleAndPathogenTests(
		EventParticipantReferenceDto eventParticipant,
		EventDto event,
		ExternalMessageDto labMessage,
		boolean entityCreated) {

		SampleDto sample = SampleDto.build(user.toReference(), eventParticipant);
		return createSampleAndPathogenTests(sample, event.getDisease(), labMessage, entityCreated);
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createSampleAndPathogenTests(
		SampleDto sample,
		Disease disease,
		ExternalMessageDto labMessage,
		boolean entityCreated) {

		ExternalMessageMapper.forLabMessage(labMessage).mapToSample(sample);
		List<PathogenTestDto> pathogenTests = LabMessageProcessingHelper.buildPathogenTests(sample, labMessage, user);
		HandlerCallback<SampleAndPathogenTests> callback = new HandlerCallback<>();
		handleCreateSampleAndPathogenTests(sample, pathogenTests, disease, labMessage, entityCreated, callback);

		return callback.futureResult;
	}

	protected abstract void handleCreateSampleAndPathogenTests(
		SampleDto sample,
		List<PathogenTestDto> pathogenTests,
		Disease disease,
		ExternalMessageDto labMessage,
		boolean entityCreated,
		HandlerCallback<SampleAndPathogenTests> callback);

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createSampleAndPathogenTests(
		ContactDto contact,
		ExternalMessageDto labMessage,
		boolean entityCreated) {

		SampleDto sample = SampleDto.build(user.toReference(), contact.toReference());
		return createSampleAndPathogenTests(sample, contact.getDisease(), labMessage, entityCreated);
	}

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

		ContactDto contactDto = ContactDto.build(null, externalMessageDto.getTestedDisease(), null, null);
		contactDto.setReportingUser(user.toReference());
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

		EventDto event = EventDto.build(country, user, labMessage.getTestedDisease());

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
		List<EventIndexDto> personEvents = FacadeProvider.getEventFacade().getIndexList(eventCriteria, null, null, null);

		EventValidationResult validationResult = new EventValidationResult();
		if (personEvents.contains(event)) {
			// event participant already exists
			return confirmPickExistingEventParticipant().thenCompose(useEventParticipant -> {
				if (Boolean.TRUE.equals(useEventParticipant)) {
					validationResult.setEventParticipant(
						FacadeProvider.getEventParticipantFacade().getReferenceByEventAndPerson(event.getUuid(), person.getUuid()));
				} else {
					validationResult.setEventSelectionCanceled(true);
				}

				return ProcessingResult.continueWith(validationResult).asCompletedFuture();
			});
		} else {
			validationResult.setEvent(FacadeProvider.getEventFacade().getEventByUuid(event.getUuid(), false));
			ret.complete(ProcessingResult.continueWith(validationResult));
		}

		return ret;
	}

	protected abstract CompletionStage<Boolean> confirmPickExistingEventParticipant();

	private CompletionStage<ProcessingResult<PickOrCreateSampleResult>> pickOrCreateSample(
		Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
		ExternalMessageDto labMessage) {

		SampleSimilarityCriteria sampleCriteria = createSampleCriteria(labMessage);
		addSampleSearchCriteria.accept(sampleCriteria);

		List<SampleDto> selectableSamples = FacadeProvider.getSampleFacade().getSamplesByCriteria(sampleCriteria.getSampleCriteria());
		List<SampleDto> similarSamples = FacadeProvider.getSampleFacade().getSimilarSamples(sampleCriteria);
		List<SampleDto> otherSamples = selectableSamples.stream().filter(s -> !similarSamples.contains(s)).collect(Collectors.toList());

		PickOrCreateSampleResult result = new PickOrCreateSampleResult();
		if (similarSamples.isEmpty() && otherSamples.isEmpty()) {
			result.setNewSample(true);
			return ProcessingResult.continueWith(result).asCompletedFuture();
		}

		HandlerCallback<PickOrCreateSampleResult> callback = new HandlerCallback<>();
		handlePickOrCreateSample(similarSamples, otherSamples, labMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handlePickOrCreateSample(
		List<SampleDto> similarSamples,
		List<SampleDto> otherSamples,
		ExternalMessageDto labMessage,
		HandlerCallback<PickOrCreateSampleResult> callback);

	private SampleSimilarityCriteria createSampleCriteria(ExternalMessageDto labMessage) {

		SampleSimilarityCriteria sampleCriteria = new SampleSimilarityCriteria();
		sampleCriteria.setLabSampleId(labMessage.getLabSampleId());
		sampleCriteria.setSampleDateTime(labMessage.getSampleDateTime());
		sampleCriteria.setSampleMaterial(labMessage.getSampleMaterial());

		return sampleCriteria;
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> editSample(SampleDto sample, ExternalMessageDto labMessage) {

		List<PathogenTestDto> newTests = LabMessageProcessingHelper.buildPathogenTests(sample, labMessage, user);
		HandlerCallback<SampleAndPathogenTests> callback = new HandlerCallback<>();
		handleEditSample(sample, newTests, labMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handleEditSample(
		SampleDto sample,
		List<PathogenTestDto> newPathogenTests,
		ExternalMessageDto labMessage,
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
