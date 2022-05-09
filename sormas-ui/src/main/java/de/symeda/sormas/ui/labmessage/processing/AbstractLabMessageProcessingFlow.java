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

package de.symeda.sormas.ui.labmessage.processing;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
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
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.labmessage.LabMessageMapper;
import de.symeda.sormas.ui.labmessage.processing.AbstractRelatedLabMessageHandler.HandlerResultStatus;
import de.symeda.sormas.ui.labmessage.processing.flow.FlowThen;
import de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResultStatus;

/**
 * Abstract class defining the flow of processing a lab message allowing to choose between multiple options like create or select a
 * case/contact/event participant and then create or update a sample with pathogen tests
 *
 * The flow is coded in the `run` method.
 */
public abstract class AbstractLabMessageProcessingFlow {

	protected final UserDto user;
	protected final CountryReferenceDto country;

	public AbstractLabMessageProcessingFlow(UserDto user, CountryReferenceDto country) {
		this.user = user;
		this.country = country;
	}

	public CompletionStage<ProcessingResult<SampleAndPathogenTests>> run(
		LabMessageDto labMessage,
		AbstractRelatedLabMessageHandler relatedLabMessageHandler) {

		return new FlowThen<Void>().then((ignored) -> checkDisease(labMessage))
			.then((ignored) -> checkRelatedForwardedMessages(labMessage))
			.then((ignored) -> handleRelatedLabMessages(relatedLabMessageHandler, labMessage))
			.then((ignored) -> pickOrCreatePerson(labMessage))
			.then((p) -> pickOrCreateEntry(p.getData(), labMessage))
			//@formatter:off
				.thenSwitch()
				.when(PersonAndPickOrCreateEntryResult::isNewCase, (f, p) -> doCreateCaseFlow(f, labMessage))
				.when(PersonAndPickOrCreateEntryResult::isNewContact, (f, p) -> doCreateContactFlow(f, labMessage))
				.when(PersonAndPickOrCreateEntryResult::isNewEventParticipant, (f, p) -> doCreateEventParticipantFlow(f, labMessage))
				.when(PersonAndPickOrCreateEntryResult::isSelectedCase, (f, p) -> doCaseSelectedFlow(p.getCaze(), f, labMessage))
				.when(PersonAndPickOrCreateEntryResult::isSelectedContact, (f, p) -> doContactSelectedFlow(p.getContact(), f, labMessage))
				.when(PersonAndPickOrCreateEntryResult::isEventParticipantSelected, (f, p) -> doEventParticipantSelectedFlow(p.getEventParticipant(), f, labMessage))
			//@formatter:on
			.then((sampleResult) -> ProcessingResult.completed(ProcessingResultStatus.DONE, sampleResult.getData()))
			.getResult();
	}

	private FlowThen<SampleAndPathogenTests> doCreateCaseFlow(FlowThen<PersonAndPickOrCreateEntryResult> flow, LabMessageDto labMessage) {
		return flow.then((p) -> createCase(p.getData().getPerson(), labMessage))
			.then((c) -> createSampleAndPathogenTests(c.getData(), labMessage, true));
	}

	private FlowThen<SampleAndPathogenTests> doCreateContactFlow(FlowThen<PersonAndPickOrCreateEntryResult> flow, LabMessageDto labMessage) {
		return flow.then((p) -> createContact(p.getData().getPerson(), labMessage))
			.then((c) -> createSampleAndPathogenTests(c.getData(), labMessage, true));
	}

	private FlowThen<SampleAndPathogenTests> doCreateEventParticipantFlow(FlowThen<PersonAndPickOrCreateEntryResult> flow, LabMessageDto labMessage) {
		return flow.then((p) -> pickOrCreateEvent(p.getData().getPerson(), labMessage))
			.thenSwitch()
			.when(
				PersonAndPickOrCreateEventResult::isNewEvent,
				(f, p) -> f.then((ignored) -> createEvent(labMessage))
					.then((ef) -> doCreateEventParticipantAndSampleFlow(ef.getData(), p.getPerson(), f, labMessage).getResult()))
			.when(
				PersonAndPickOrCreateEventResult::isEventSelected,
				(f, p) -> f.then((e) -> validateSelectedEvent(e.getData().getPickOrCreateEventResult().getEvent(), p.getPerson()))
					.thenSwitch()
					.when(
						EventValidationResult::isEventSelected,
						(vf, v) -> doCreateEventParticipantAndSampleFlow(v.getEvent(), p.getPerson(), vf, labMessage))
					.when(
						EventValidationResult::isEventParticipantSelected,
						(vf, v) -> doPickOrCreateSampleFlow(c -> c.eventParticipant(v.getEventParticipant()), () -> {
							EventDto event = FacadeProvider.getEventFacade().getEventByUuid(p.getEvent().getUuid(), false);

							return createSampleAndPathogenTests(v.getEventParticipant(), event, labMessage, false);
						}, vf, labMessage))
					.when(
						EventValidationResult::isEventSelectionCanceled,
						(vf, v) -> vf.then((ignored) -> doCreateEventParticipantFlow(flow, labMessage).getResult()))
					.then(s -> ProcessingResult.completedContinue(s.getData())))
			.then(s -> ProcessingResult.completedContinue(s.getData()));
	}

	private FlowThen<SampleAndPathogenTests> doCreateEventParticipantAndSampleFlow(
		EventDto event,
		PersonDto person,
		FlowThen<?> flow,
		LabMessageDto labMessage) {
		return flow.then((ignored) -> createEventParticipant(event, person, labMessage))
			.then((ep) -> createSampleAndPathogenTests(ep.getData().getEventParticipant().toReference(), ep.getData().getEvent(), labMessage, true));
	}

	private FlowThen<SampleAndPathogenTests> doCaseSelectedFlow(
		CaseSelectionDto caseSelection,
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		LabMessageDto labMessage) {
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseSelection.getUuid());

		return doPickOrCreateSampleFlow(
			c -> c.caze(caze.toReference()),
			() -> createSampleAndPathogenTests(caze, labMessage, false),
			flow,
			labMessage);
	}

	private FlowThen<SampleAndPathogenTests> doPickOrCreateSampleFlow(
		Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
		Supplier<CompletionStage<ProcessingResult<SampleAndPathogenTests>>> createSampleAndPathogenTests,
		FlowThen<?> flow,
		LabMessageDto labMessage) {

		return flow.then(ignored -> pickOrCreateSample(addSampleSearchCriteria, labMessage))
			.thenSwitch()
			.when(PickOrCreateSampleResult::isNewSample, (sf, p) -> sf.then(ignored -> createSampleAndPathogenTests.get()))
			.when(PickOrCreateSampleResult::isSelectedSample, (sf, p) -> sf.then(s -> editSample(s.getData().getSample(), labMessage)))
			.then(s -> ProcessingResult.completedContinue(s.getData()));
	}

	private FlowThen<SampleAndPathogenTests> doContactSelectedFlow(
		SimilarContactDto contactSelection,
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		LabMessageDto labMessage) {
		ContactDto contact = FacadeProvider.getContactFacade().getByUuid(contactSelection.getUuid());

		return doPickOrCreateSampleFlow(
			c -> c.contact(contact.toReference()),
			() -> createSampleAndPathogenTests(contact, labMessage, false),
			flow,
			labMessage);
	}

	private FlowThen<SampleAndPathogenTests> doEventParticipantSelectedFlow(
		SimilarEventParticipantDto eventParticipantSelection,
		FlowThen<PersonAndPickOrCreateEntryResult> flow,
		LabMessageDto labMessage) {
		EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getByUuid(eventParticipantSelection.getUuid());
		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);

		return doPickOrCreateSampleFlow(
			c -> c.eventParticipant(eventParticipant.toReference()),
			() -> createSampleAndPathogenTests(eventParticipant.toReference(), event, labMessage, false),
			flow,
			labMessage);
	}

	private CompletionStage<ProcessingResult<Void>> checkDisease(LabMessageDto labMessage) {
		if (labMessage.getTestedDisease() == null) {
			return handleMissingDisease()
				.thenCompose(next -> ProcessingResult.completedStatus(next ? ProcessingResultStatus.CONTINUE : ProcessingResultStatus.CANCELED));
		} else {
			return ProcessingResult.completedContinue();
		}
	}

	protected abstract CompletionStage<Boolean> handleMissingDisease();

	private CompletionStage<ProcessingResult<Void>> checkRelatedForwardedMessages(LabMessageDto labMessage) {
		if (FacadeProvider.getLabMessageFacade().existsForwardedLabMessageWith(labMessage.getReportId())) {
			return handleRelatedForwardedMessages()
				.thenCompose(next -> ProcessingResult.completedStatus(next ? ProcessingResultStatus.CONTINUE : ProcessingResultStatus.CANCELED));
		} else {
			return ProcessingResult.completedStatus(ProcessingResultStatus.CONTINUE);
		}
	}

	protected abstract CompletionStage<Boolean> handleRelatedForwardedMessages();

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> handleRelatedLabMessages(
		AbstractRelatedLabMessageHandler relatedLabMessageHandler,
		LabMessageDto labMessage) {
		return relatedLabMessageHandler.handle(labMessage).thenCompose((result) -> {
			HandlerResultStatus status = result.getStatus();
			if (status == HandlerResultStatus.CANCELED) {
				return ProcessingResult.completedStatus(ProcessingResultStatus.CANCELED);
			}

			if (status == HandlerResultStatus.CANCELED_WITH_UPDATES) {
				return ProcessingResult.completedStatus(ProcessingResultStatus.CANCELED_WITH_CORRECTIONS);
			}

			if (status == HandlerResultStatus.HANDLED) {
				SampleDto relatedSample = result.getSample();
				return ProcessingResult.completed(
					ProcessingResultStatus.DONE,
					new SampleAndPathogenTests(relatedSample, FacadeProvider.getPathogenTestFacade().getAllBySample(relatedSample.toReference())));
			}

			return ProcessingResult.completedContinue(null);
		});
	}

	private CompletionStage<ProcessingResult<PersonDto>> pickOrCreatePerson(LabMessageDto labMessage) {
		final PersonDto person = buildPerson(LabMessageMapper.forLabMessage(labMessage));

		HandlerCallback<PersonDto> callback = new HandlerCallback<>();
		handlePickOrCreatePerson(person, callback);

		return callback.futureResult;
	}

	protected abstract void handlePickOrCreatePerson(PersonDto person, HandlerCallback<PersonDto> callback);

	private PersonDto buildPerson(LabMessageMapper mapper) {
		final PersonDto personDto = PersonDto.build();

		mapper.mapToPerson(personDto);
		mapper.mapToLocation(personDto.getAddress());

		return personDto;
	}

	private CompletionStage<ProcessingResult<PersonAndPickOrCreateEntryResult>> pickOrCreateEntry(PersonDto person, LabMessageDto labMessage) {
		PersonReferenceDto personRef = person.toReference();
		List<CaseSelectionDto> similarCases = getSimilarCases(personRef, labMessage);
		List<SimilarContactDto> similarContacts = getSimilarContacts(personRef, labMessage);
		List<SimilarEventParticipantDto> similarEventParticipants = getSimilarEventParticipants(labMessage, personRef);

		HandlerCallback<PickOrCreateEntryResult> callback = new HandlerCallback<>();

		handlePickOrCreateEntry(similarCases, similarContacts, similarEventParticipants, labMessage, callback);

		return callback.futureResult.thenCompose(p -> {
			if (p.getStatus().isCanceled()) {
				return ProcessingResult.completedStatus(p.getStatus());
			}

			return ProcessingResult.completed(p.getStatus(), new PersonAndPickOrCreateEntryResult(person, p.getData()));
		});
	}

	protected abstract void handlePickOrCreateEntry(
		List<CaseSelectionDto> similarCases,
		List<SimilarContactDto> similarContacts,
		List<SimilarEventParticipantDto> similarEventParticipants,
		LabMessageDto labMessageDto,
		HandlerCallback<PickOrCreateEntryResult> callback);

	private List<CaseSelectionDto> getSimilarCases(PersonReferenceDto selectedPerson, LabMessageDto labMessage) {
		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.person(selectedPerson);
		caseCriteria.disease(labMessage.getTestedDisease());
		CaseSimilarityCriteria caseSimilarityCriteria = new CaseSimilarityCriteria();
		caseSimilarityCriteria.caseCriteria(caseCriteria);
		caseSimilarityCriteria.personUuid(selectedPerson.getUuid());

		return FacadeProvider.getCaseFacade().getSimilarCases(caseSimilarityCriteria);
	}

	private List<SimilarContactDto> getSimilarContacts(PersonReferenceDto selectedPerson, LabMessageDto labMessage) {
		ContactSimilarityCriteria contactSimilarityCriteria = new ContactSimilarityCriteria();
		contactSimilarityCriteria.setPerson(selectedPerson);
		contactSimilarityCriteria.setDisease(labMessage.getTestedDisease());

		return FacadeProvider.getContactFacade().getMatchingContacts(contactSimilarityCriteria);
	}

	private List<SimilarEventParticipantDto> getSimilarEventParticipants(LabMessageDto labMessage, PersonReferenceDto selectedPerson) {
		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setPerson(selectedPerson);
		eventParticipantCriteria.setDisease(labMessage.getTestedDisease());

		return FacadeProvider.getEventParticipantFacade().getMatchingEventParticipants(eventParticipantCriteria);
	}

	private CompletionStage<ProcessingResult<CaseDataDto>> createCase(PersonDto person, LabMessageDto labMessage) {
		CaseDataDto caze = buildCase(person, labMessage);

		HandlerCallback<CaseDataDto> callback = new HandlerCallback<>();
		handleCreateCase(caze, person, labMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handleCreateCase(CaseDataDto caze, PersonDto person, LabMessageDto labMessage, HandlerCallback<CaseDataDto> callback);

	private CaseDataDto buildCase(PersonDto person, LabMessageDto labMessage) {
		CaseDataDto caseDto = CaseDataDto.build(person.toReference(), labMessage.getTestedDisease());
		caseDto.setReportingUser(user.toReference());
		return caseDto;
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createSampleAndPathogenTests(
		CaseDataDto caze,
		LabMessageDto labMessage,
		boolean entityCreated) {
		SampleDto sample = SampleDto.build(user.toReference(), caze.toReference());

		return createSampleAndPathogenTests(sample, caze.getDisease(), labMessage, entityCreated);
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createSampleAndPathogenTests(
		EventParticipantReferenceDto eventParticipant,
		EventDto event,
		LabMessageDto labMessage,
		boolean entityCreated) {
		SampleDto sample = SampleDto.build(user.toReference(), eventParticipant);

		return createSampleAndPathogenTests(sample, event.getDisease(), labMessage, entityCreated);
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createSampleAndPathogenTests(
		SampleDto sample,
		Disease disease,
		LabMessageDto labMessage,
		boolean entityCreated) {
		LabMessageMapper.forLabMessage(labMessage).mapToSample(sample);

		List<PathogenTestDto> pathogenTests = LabMessageProcessingHelper.buildPathogenTests(sample, labMessage, user);

		HandlerCallback<SampleAndPathogenTests> callback = new HandlerCallback<>();
		handleCreateSampleAndPathogenTests(sample, pathogenTests, disease, labMessage, entityCreated, callback);

		return callback.futureResult;
	}

	protected abstract void handleCreateSampleAndPathogenTests(
		SampleDto sample,
		List<PathogenTestDto> pathogenTests,
		Disease disease,
		LabMessageDto labMessage,
		boolean entityCreated,
		HandlerCallback<SampleAndPathogenTests> callback);

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> createSampleAndPathogenTests(
		ContactDto contact,
		LabMessageDto labMessage,
		boolean entityCreated) {
		SampleDto sample = SampleDto.build(user.toReference(), contact.toReference());

		return createSampleAndPathogenTests(sample, contact.getDisease(), labMessage, entityCreated);
	}

	private CompletionStage<ProcessingResult<ContactDto>> createContact(PersonDto person, LabMessageDto labMessage) {
		ContactDto contact = buildContact(labMessage, person);

		HandlerCallback<ContactDto> callback = new HandlerCallback<>();
		handleCreateContact(contact, person, labMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handleCreateContact(ContactDto contact, PersonDto person, LabMessageDto labMessage, HandlerCallback<ContactDto> callback);

	private ContactDto buildContact(LabMessageDto labMessageDto, PersonDto person) {
		ContactDto contactDto = ContactDto.build(null, labMessageDto.getTestedDisease(), null, null);
		contactDto.setReportingUser(user.toReference());
		contactDto.setPerson(person.toReference());

		return contactDto;
	}

	private CompletionStage<ProcessingResult<PersonAndPickOrCreateEventResult>> pickOrCreateEvent(PersonDto person, LabMessageDto labMessageDto) {
		HandlerCallback<PickOrCreateEventResult> callback = new HandlerCallback<>();
		handlePickOrCreateEvent(labMessageDto, callback);

		return callback.futureResult.thenCompose(p -> {
			if (p.getStatus().isCanceled()) {
				return ProcessingResult.completedStatus(p.getStatus());
			}

			return ProcessingResult.completed(p.getStatus(), new PersonAndPickOrCreateEventResult(person, p.getData()));
		});
	}

	protected abstract void handlePickOrCreateEvent(LabMessageDto labMessage, HandlerCallback<PickOrCreateEventResult> callback);

	private CompletionStage<ProcessingResult<EventDto>> createEvent(LabMessageDto labMessage) {
		EventDto event = EventDto.build(country, user, labMessage.getTestedDisease());

		HandlerCallback<EventDto> callback = new HandlerCallback<>();
		handleCreateEvent(event, callback);

		return callback.futureResult;
	}

	protected abstract void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback);

	private CompletionStage<ProcessingResult<EventAndParticipant>> createEventParticipant(
		EventDto event,
		PersonDto person,
		LabMessageDto labMessage) {
		EventParticipantDto eventParticipant = buildEventParticipant(event, person);

		HandlerCallback<EventParticipantDto> callback = new HandlerCallback<>();
		handleCreateEventParticipant(eventParticipant, event, labMessage, callback);

		return callback.futureResult.thenCompose((ep) -> {
			if (ep.getStatus().isCanceled()) {
				return ProcessingResult.completedStatus(ep.getStatus());
			}

			return ProcessingResult.completed(ep.getStatus(), new EventAndParticipant(event, ep.getData()));
		});
	}

	protected abstract void handleCreateEventParticipant(
		EventParticipantDto eventParticipant,
		EventDto event,
		LabMessageDto labMessage,
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
				if (useEventParticipant) {
					validationResult.setEventParticipant(
						FacadeProvider.getEventParticipantFacade().getReferenceByEventAndPerson(event.getUuid(), person.getUuid()));
				} else {
					validationResult.setEventSelectionCanceled(true);
				}

				return ProcessingResult.completedContinue(validationResult);
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
		LabMessageDto labMessage) {

		SampleSimilarityCriteria sampleCriteria = createSampleCriteria(labMessage);
		addSampleSearchCriteria.accept(sampleCriteria);

		List<SampleDto> samples = FacadeProvider.getSampleFacade().getSimilarSamples(sampleCriteria);

		PickOrCreateSampleResult result = new PickOrCreateSampleResult();
		if (samples.isEmpty()) {
			result.setNewSample(true);
			return ProcessingResult.completedContinue(result);
		}

		HandlerCallback<PickOrCreateSampleResult> callback = new HandlerCallback<>();
		handlePickOrCreateSample(samples, labMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handlePickOrCreateSample(
		List<SampleDto> samples,
		LabMessageDto labMessage,
		HandlerCallback<PickOrCreateSampleResult> callback);

	private SampleSimilarityCriteria createSampleCriteria(LabMessageDto labMessage) {
		SampleSimilarityCriteria sampleCriteria = new SampleSimilarityCriteria();
		sampleCriteria.setLabSampleId(labMessage.getLabSampleId());
		sampleCriteria.setSampleDateTime(labMessage.getSampleDateTime());
		sampleCriteria.setSampleMaterial(labMessage.getSampleMaterial());

		return sampleCriteria;
	}

	private CompletionStage<ProcessingResult<SampleAndPathogenTests>> editSample(SampleDto sample, LabMessageDto labMessage) {
		List<PathogenTestDto> newTests = LabMessageProcessingHelper.buildPathogenTests(sample, labMessage, user);

		HandlerCallback<SampleAndPathogenTests> callback = new HandlerCallback<>();
		handleEditSample(sample, newTests, labMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handleEditSample(
		SampleDto sample,
		List<PathogenTestDto> newPathogenTests,
		LabMessageDto labMessage,
		HandlerCallback<SampleAndPathogenTests> callback);

	public static class HandlerCallback<T> {

		private final CompletableFuture<ProcessingResult<T>> futureResult;

		private HandlerCallback() {
			this.futureResult = new CompletableFuture<>();
		}

		public void done(T result) {
			futureResult.complete(ProcessingResult.continueWith(result));
		}

		public void cancel() {
			futureResult.complete(ProcessingResult.withStatus(ProcessingResultStatus.CANCELED));
		}
	}

	private static final class PersonAndPickOrCreateEntryResult {

		private final PersonDto person;
		private final PickOrCreateEntryResult pickOrCreateEntryResult;

		public PersonAndPickOrCreateEntryResult(PersonDto person, PickOrCreateEntryResult pickOrCreateEntryResult) {
			this.person = person;
			this.pickOrCreateEntryResult = pickOrCreateEntryResult;
		}

		public PersonDto getPerson() {
			return person;
		}

		public boolean isNewCase() {
			return pickOrCreateEntryResult.isNewCase();
		}

		public boolean isNewContact() {
			return pickOrCreateEntryResult.isNewContact();
		}

		public boolean isNewEventParticipant() {
			return pickOrCreateEntryResult.isNewEventParticipant();
		}

		public boolean isSelectedCase() {
			return pickOrCreateEntryResult.getCaze() != null;
		}

		public CaseSelectionDto getCaze() {
			return pickOrCreateEntryResult.getCaze();
		}

		public boolean isSelectedContact() {
			return pickOrCreateEntryResult.getContact() != null;
		}

		public SimilarContactDto getContact() {
			return pickOrCreateEntryResult.getContact();
		}

		public boolean isEventParticipantSelected() {
			return pickOrCreateEntryResult.getEventParticipant() != null;
		}

		public SimilarEventParticipantDto getEventParticipant() {
			return pickOrCreateEntryResult.getEventParticipant();
		}
	}

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
