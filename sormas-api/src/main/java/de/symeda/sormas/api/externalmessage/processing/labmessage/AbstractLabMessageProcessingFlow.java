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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
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
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.processing.AbstractProcessingFlow;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.api.externalmessage.processing.flow.FlowThen;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
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
 * The flow is coded in the `run` method.
 */
public abstract class AbstractLabMessageProcessingFlow extends AbstractProcessingFlow {

	private final ExternalMessageDto externalMessage;
	private final AbstractRelatedLabMessageHandler relatedLabMessageHandler;
	private final Boolean forceSampleCreation;

	public AbstractLabMessageProcessingFlow(
		ExternalMessageDto externalMessage,
		UserDto user,
		ExternalMessageMapper mapper,
		ExternalMessageProcessingFacade processingFacade,
		AbstractRelatedLabMessageHandler relatedLabMessageHandler,
		Boolean forceSampleCreation) {
		super(user, mapper, processingFacade);
		this.externalMessage = externalMessage;
		this.relatedLabMessageHandler = relatedLabMessageHandler;
		this.forceSampleCreation = forceSampleCreation;
	}

	public AbstractLabMessageProcessingFlow(
		ExternalMessageDto externalMessage,
		UserDto user,
		ExternalMessageMapper mapper,
		ExternalMessageProcessingFacade processingFacade,
		AbstractRelatedLabMessageHandler relatedLabMessageHandler) {
		this(externalMessage, user, mapper, processingFacade, relatedLabMessageHandler, false);
	}

	private static CompletableFuture<ProcessingResult<PickOrCreateSampleResult>> continueWithCreateSample() {
		PickOrCreateSampleResult result = new PickOrCreateSampleResult();
		result.setNewSample(true);
		return ProcessingResult.continueWith(result).asCompletedFuture();
	}

	private static SampleSimilarityCriteria createSampleSimilarCriteria(SampleReportDto sampleReport) {

		SampleSimilarityCriteria sampleCriteria = new SampleSimilarityCriteria();
		sampleCriteria.setLabSampleId(sampleReport.getLabSampleId());
		sampleCriteria.setSampleDateTime(sampleReport.getSampleDateTime());
		sampleCriteria.setSampleMaterial(sampleReport.getSampleMaterial());

		return sampleCriteria;
	}

	private static boolean isLastSample(ExternalMessageDto labMessage, int sampleReportIndex) {
		if (sampleReportIndex >= labMessage.getSampleReportsNullSafe().size()) {
			throw new IndexOutOfBoundsException("The sample report index is out of bounds.");
		}
		return labMessage.getSampleReportsNullSafe().size() == sampleReportIndex + 1;
	}

	public CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> run() {
		//@formatter:off
		return doInitialChecks(externalMessage, new ExternalMessageProcessingResult())
			.then(initialCheckResult -> handleRelatedLabMessages(relatedLabMessageHandler, initialCheckResult))
			// if no handling happened, or opted to continue regular processing, ignore results
			.then(ignored -> pickOrCreatePerson(new ExternalMessageProcessingResult()))
			.thenSwitch(p -> pickOrCreateEntry(p.getData(), externalMessage))
				.when(PickOrCreateEntryResult::isNewCase, (f, p, r) -> doCreateCaseFlow(f))
				.when(PickOrCreateEntryResult::isNewContact, (f, p, r) -> doCreateContactFlow(f))
				.when(PickOrCreateEntryResult::isNewEventParticipant, (f, p, r) -> doCreateEventParticipantFlow(f))
				.when(PickOrCreateEntryResult::isSelectedCase, (f, p, r) -> doCaseSelectedFlow(p.getCaze(), f))
				.when(PickOrCreateEntryResult::isSelectedContact, (f, p, r) -> doContactSelectedFlow(p.getContact(), f))
				.when(PickOrCreateEntryResult::isSelectedEventParticipant, (f, p, r) -> doEventParticipantSelectedFlow(p.getEventParticipant(), f))
			.then(f -> ProcessingResult.of(ProcessingResultStatus.DONE, f.getData()).asCompletedFuture())
			.getResult().thenCompose(this::handleProcessingDone);
		//@formatter:on
	}

	private FlowThen<ExternalMessageProcessingResult> doCreateCaseFlow(FlowThen<ExternalMessageProcessingResult> flow) {

		FlowThen<ExternalMessageProcessingResult> caseFlow = flow.then(p -> createCase(p.getData()));

		return caseFlow.then(p -> {
			ExternalMessageProcessingResult previousResult = p.getData();
			CaseDataDto caze = previousResult.getCase();
			BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForCase =
				(sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(caze, sampleReportIndex, true, previousSampleResult);

			return doPickOrCreateSamplesFlow(c -> c.sampleCriteria(new SampleCriteria().caze(caze.toReference())), createSampleForCase, caseFlow)
				.getResult();
		});
	}

	private FlowThen<ExternalMessageProcessingResult> doCreateContactFlow(FlowThen<ExternalMessageProcessingResult> flow) {

		FlowThen<ExternalMessageProcessingResult> contactFlow = flow.then(p -> createContact(p.getData().getPerson(), p.getData()));
		return contactFlow.then(p -> {
			ContactDto contact = p.getData().getContact();
			BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForContact =
				(sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(contact, sampleReportIndex, true, previousSampleResult);

			return doPickOrCreateSamplesFlow(
				c -> c.sampleCriteria(new SampleCriteria().contact(contact.toReference())),
				createSampleForContact,
				contactFlow).getResult();
		});
	}

	private FlowThen<ExternalMessageProcessingResult> doCreateEventParticipantFlow(FlowThen<ExternalMessageProcessingResult> flow) {

		//@formatter:off
		return flow.thenSwitch(p -> pickOrCreateEvent())
				.when(PickOrCreateEventResult::isNewEvent, (f, p, r) -> {
					FlowThen<ExternalMessageProcessingResult> eventFlow = f.then(ignored -> createEvent(r));
					return eventFlow.then(ef -> doCreateEventParticipantAndSamplesFlow(r.getPerson(), eventFlow).getResult());
				})
				.when(PickOrCreateEventResult::isEventSelected, (f, p, r) -> f
					.thenSwitch(e -> validateSelectedEvent(p.getEvent(), e.getData().getPerson()))
						.when(EventValidationResult::isEventSelected, (vf, v, vr) -> {
							FlowThen<ExternalMessageProcessingResult> eventFlow = vf.then(e -> ProcessingResult.continueWith(e.getData().withSelectedEvent(v.getEvent())).asCompletedFuture());
							return doCreateEventParticipantAndSamplesFlow(vr.getPerson(), eventFlow);
						})
						.when(EventValidationResult::isEventParticipantSelected, (vf, v, vr) -> {
							EventDto event = processingFacade.getEventByUuid(p.getEvent().getUuid());
							EventParticipantDto eventParticipant = processingFacade.getEventParticipantByUuid(v.eventParticipant.getUuid());

							FlowThen<ExternalMessageProcessingResult> eventParticipantFlow = vf.then(ignored -> ProcessingResult
								.continueWith(vr.withSelectedEvent(event).withSelectedEventParticipant(eventParticipant))
								.asCompletedFuture());

							BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForEventParticipant =
								(sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(
									SampleDto.build(user.toReference(), eventParticipant.toReference()),
									event.getDisease(),
									sampleReportIndex,
									false,
									previousSampleResult);

							return eventParticipantFlow.then(
								sf -> doPickOrCreateSamplesFlow(
									c -> c.sampleCriteria(new SampleCriteria().eventParticipant(eventParticipant.toReference())),
									createSampleForEventParticipant,
										eventParticipantFlow).getResult());
						})
						.when(EventValidationResult::isEventSelectionCanceled, (vf, v, vr) -> vf.then(ignored -> doCreateEventParticipantFlow(vf).getResult()))
					.then(ProcessingResult::asCompletedFuture))
			.then(ProcessingResult::asCompletedFuture);
		//@formatter:on
	}

	private FlowThen<ExternalMessageProcessingResult> doCreateEventParticipantAndSamplesFlow(
		PersonDto person,
		FlowThen<ExternalMessageProcessingResult> flow) {

		FlowThen<ExternalMessageProcessingResult> eventParticipantFlow =
			flow.then(p -> createEventParticipant(p.getData().getEvent(), person, p.getData()));
		return eventParticipantFlow.then(p -> {
			EventParticipantDto eventParticipant = p.getData().getEventParticipant();

			BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForEventParticipant =
				(sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(
					eventParticipant.toReference(),
					previousSampleResult.getEvent(),
					sampleReportIndex,
					true,
					previousSampleResult);

			return doPickOrCreateSamplesFlow(
				c -> c.sampleCriteria(new SampleCriteria().eventParticipant(eventParticipant.toReference())),
				createSampleForEventParticipant,
				eventParticipantFlow).getResult();
		});
	}

	private FlowThen<ExternalMessageProcessingResult> doPickOrCreateSamplesFlow(
		Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
		BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleAndPathogenTests,
		FlowThen<ExternalMessageProcessingResult> flow) {

		List<SampleReportDto> sampleReports = externalMessage.getSampleReportsNullSafe();
		if (sampleReports.size() > 1) {
			flow = flow.then(
				result -> handleMultipleSampleConfirmation().thenCompose(
					next -> ProcessingResult
						.withStatus(Boolean.TRUE.equals(next) ? ProcessingResultStatus.CONTINUE : ProcessingResultStatus.CANCELED, result.getData())
						.asCompletedFuture()));
		}

		int i = 0;
		do {
			flow = doSinglePickOrCreateSampleFlow(addSampleSearchCriteria, createSampleAndPathogenTests, flow, i);
			i += 1;
		}
		while (i < sampleReports.size());

		return flow;
	}

	private FlowThen<ExternalMessageProcessingResult> doCaseSelectedFlow(
		CaseSelectionDto caseSelection,
		FlowThen<ExternalMessageProcessingResult> flow) {

		CaseDataDto caze = processingFacade.getCaseDataByUuid(caseSelection.getUuid());

		BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForCase =
			(sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(caze, sampleReportIndex, false, previousSampleResult);

		FlowThen<ExternalMessageProcessingResult> caseFlow =
			flow.then(previousResult -> ProcessingResult.continueWith(previousResult.getData().withSelectedCase(caze)).asCompletedFuture());
		return caseFlow.then(
			previousResult -> doPickOrCreateSamplesFlow(
				c -> c.sampleCriteria(new SampleCriteria().caze(caze.toReference())),
				createSampleForCase,
				caseFlow).getResult());
	}

	private FlowThen<ExternalMessageProcessingResult> doContactSelectedFlow(
		SimilarContactDto contactSelection,
		FlowThen<ExternalMessageProcessingResult> flow) {

		ContactDto contact = processingFacade.getContactByUuid(contactSelection.getUuid());

		BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForContact =
			(sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(contact, sampleReportIndex, false, previousSampleResult);

		FlowThen<ExternalMessageProcessingResult> contactFlow =
			flow.then(previousResult -> ProcessingResult.continueWith(previousResult.getData().withSelectedContact(contact)).asCompletedFuture());

		return contactFlow.then(
			p -> doPickOrCreateSamplesFlow(
				c -> c.sampleCriteria(new SampleCriteria().contact(contact.toReference())),
				createSampleForContact,
				contactFlow).getResult());
	}

	private FlowThen<ExternalMessageProcessingResult> doEventParticipantSelectedFlow(
		SimilarEventParticipantDto eventParticipantSelection,
		FlowThen<ExternalMessageProcessingResult> flow) {

		EventParticipantDto eventParticipant = processingFacade.getEventParticipantByUuid(eventParticipantSelection.getUuid());
		EventDto event = processingFacade.getEventByUuid(eventParticipant.getEvent().getUuid());

		BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForEventParticipant =
			(sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(
				eventParticipant.toReference(),
				event,
				sampleReportIndex,
				false,
				previousSampleResult);

		FlowThen<ExternalMessageProcessingResult> eventParticipantFlow = flow.then(
			previousResult -> ProcessingResult
				.continueWith(previousResult.getData().withSelectedEvent(event).withSelectedEventParticipant(eventParticipant))
				.asCompletedFuture());

		return eventParticipantFlow.then(
			p -> doPickOrCreateSamplesFlow(
				c -> c.sampleCriteria(new SampleCriteria().eventParticipant(eventParticipant.toReference())),
				createSampleForEventParticipant,
				eventParticipantFlow).getResult());
	}

	private FlowThen<ExternalMessageProcessingResult> doSinglePickOrCreateSampleFlow(
		Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
		BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleAndPathogenTests,
		FlowThen<ExternalMessageProcessingResult> flow,
		int sampleReportIndex) {

		//@formatter:off
		return flow.thenSwitch(p -> pickOrCreateSample(addSampleSearchCriteria, sampleReportIndex))
				.when(PickOrCreateSampleResult::isNewSample, (sf, p, r) -> sf.then(ignored -> createSampleAndPathogenTests.apply(sampleReportIndex, r)))
				.when(PickOrCreateSampleResult::isSelectedSample, (sf, p, r) -> sf.then(s -> editSample(p.getSample(), sampleReportIndex, r)))
			.then(ProcessingResult::asCompletedFuture);
		//@formatter:on

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

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> handleRelatedLabMessages(
		AbstractRelatedLabMessageHandler relatedLabMessageHandler,
		ProcessingResult<ExternalMessageProcessingResult> previousResult) {

		if (relatedLabMessageHandler == null) {
			return previousResult.asCompletedFuture();
		}

		// TODO currently, related messages handling is just done if one sample report exists. That's why this works.
		SampleReportDto firstSampleReport = externalMessage.getSampleReportsNullSafe().get(0);
		return relatedLabMessageHandler.handle(externalMessage).thenCompose(result -> {
			AbstractRelatedLabMessageHandler.HandlerResultStatus status = result.getStatus();
			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CANCELED) {
				return ProcessingResult.withStatus(ProcessingResultStatus.CANCELED, previousResult.getData()).asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CANCELED_WITH_UPDATES) {
				return ProcessingResult.withStatus(ProcessingResultStatus.CANCELED_WITH_CORRECTIONS, previousResult.getData()).asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.HANDLED) {
				SampleDto relatedSample = result.getSample();

				return ProcessingResult
					.of(
						ProcessingResultStatus.DONE,
						setPersonAssociationsOnResult(
							relatedSample,
							previousResult.getData()
								.withPerson(result.getPerson(), false)
								.andWithSampleAndPathogenTests(
									result.getSample(),
									processingFacade.getPathogenTestsBySample(relatedSample.toReference()),
									firstSampleReport,
									false)))
					.asCompletedFuture();
			}

			return ProcessingResult.continueWith(previousResult.getData()).asCompletedFuture();
		});
	}

	protected abstract void handleCreateCase(
		CaseDataDto caze,
		PersonDto person,
		ExternalMessageDto labMessage,
		HandlerCallback<CaseDataDto> callback);

	private ExternalMessageProcessingResult setPersonAssociationsOnResult(SampleDto sample, ExternalMessageProcessingResult result) {
		if (sample.getAssociatedCase() != null) {
			return result.withSelectedCase(processingFacade.getCaseDataByUuid(sample.getAssociatedCase().getUuid()));
		} else if (sample.getAssociatedContact() != null) {
			return result.withSelectedContact(processingFacade.getContactByUuid(sample.getAssociatedContact().getUuid()));
		} else if (sample.getAssociatedEventParticipant() != null) {
			return result.withSelectedEventParticipant(processingFacade.getEventParticipantByUuid(sample.getAssociatedEventParticipant().getUuid()));
		}

		return result;
	}

	private CompletionStage<ProcessingResult<PickOrCreateEntryResult>> pickOrCreateEntry(
		ExternalMessageProcessingResult previousResult,
		ExternalMessageDto externalMessage) {

		PersonReferenceDto personRef = previousResult.getPerson().toReference();
		List<CaseSelectionDto> similarCases = getSimilarCases(personRef, externalMessage);
		List<SimilarContactDto> similarContacts = getSimilarContacts(personRef, externalMessage);
		List<SimilarEventParticipantDto> similarEventParticipants = getSimilarEventParticipants(personRef, externalMessage);

		HandlerCallback<PickOrCreateEntryResult> callback = new HandlerCallback<>();

		handlePickOrCreateEntry(similarCases, similarContacts, similarEventParticipants, externalMessage, callback);

		return callback.futureResult;
	}

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createCase(ExternalMessageProcessingResult previousResult) {

		PersonDto person = previousResult.getPerson();
		CaseDataDto caze = buildCase(person, externalMessage);

		HandlerCallback<CaseDataDto> callback = new HandlerCallback<>();
		handleCreateCase(caze, person, externalMessage, callback);

		return mapHandlerResult(callback, previousResult, previousResult::withCreatedCase);
	}

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createOneSampleAndPathogenTests(
		CaseDataDto caze,
		int sampleReportIndex,
		boolean entityCreated,
		ExternalMessageProcessingResult previousResult) {

		SampleDto sample = SampleDto.build(user.toReference(), caze.toReference());
		return createOneSampleAndPathogenTests(sample, caze.getDisease(), sampleReportIndex, entityCreated, previousResult);
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

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createOneSampleAndPathogenTests(
		ContactDto contact,
		int sampleReportIndex,
		boolean entityCreated,
		ExternalMessageProcessingResult previousResult) {

		SampleDto sample = SampleDto.build(user.toReference(), contact.toReference());
		return createOneSampleAndPathogenTests(sample, contact.getDisease(), sampleReportIndex, entityCreated, previousResult);
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

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createOneSampleAndPathogenTests(
		EventParticipantReferenceDto eventParticipant,
		EventDto event,
		int sampleReportIndex,
		boolean entityCreated,
		ExternalMessageProcessingResult previousResult) {

		SampleDto sample = SampleDto.build(user.toReference(), eventParticipant);
		return createOneSampleAndPathogenTests(sample, event.getDisease(), sampleReportIndex, entityCreated, previousResult);
	}

	protected abstract void handlePickOrCreateEvent(ExternalMessageDto labMessage, HandlerCallback<PickOrCreateEventResult> callback);

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createOneSampleAndPathogenTests(
		SampleDto sample,
		Disease disease,
		int sampleReportIndex,
		boolean entityCreated,
		ExternalMessageProcessingResult previousResult) {

		SampleReportDto sampleReport = externalMessage.getSampleReportsNullSafe().get(sampleReportIndex);
		mapper.mapToSample(sample, sampleReport);
		List<PathogenTestDto> pathogenTests = LabMessageProcessingHelper.buildPathogenTests(sample, sampleReportIndex, externalMessage, mapper, user);
		HandlerCallback<SampleAndPathogenTests> callback = new HandlerCallback<>();
		handleCreateSampleAndPathogenTests(
			sample,
			pathogenTests,
			disease,
			externalMessage,
			entityCreated,
			isLastSample(externalMessage, sampleReportIndex),
			callback);

		return mapHandlerResult(
			callback,
			previousResult,
			s -> previousResult.andWithSampleAndPathogenTests(s.getSample(), s.getPathogenTests(), sampleReport, true));
	}

	protected abstract void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback);

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createContact(
		PersonDto person,
		ExternalMessageProcessingResult previousResult) {

		ContactDto contact = buildContact(externalMessage, person);
		HandlerCallback<ContactDto> callback = new HandlerCallback<>();
		handleCreateContact(contact, person, externalMessage, callback);

		return mapHandlerResult(callback, previousResult, previousResult::withCreatedContact);
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

	private CompletionStage<ProcessingResult<PickOrCreateEventResult>> pickOrCreateEvent() {

		HandlerCallback<PickOrCreateEventResult> callback = new HandlerCallback<>();
		handlePickOrCreateEvent(externalMessage, callback);

		return callback.futureResult;
	}

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createEvent(ExternalMessageProcessingResult previousResult) {

		EventDto event = EventDto.build(processingFacade.getServerCountry(), user, externalMessage.getDisease());
		event.setDiseaseVariant(externalMessage.getDiseaseVariant());
		event.setDiseaseVariantDetails(externalMessage.getDiseaseVariantDetails());

		HandlerCallback<EventDto> callback = new HandlerCallback<>();
		handleCreateEvent(event, callback);

		return mapHandlerResult(callback, previousResult, previousResult::withCreatedEvent);
	}

	protected abstract void handlePickOrCreateSample(
		List<SampleDto> similarSamples,
		List<SampleDto> otherSamples,
		ExternalMessageDto labMessage,
		int sampleReportIndex,
		HandlerCallback<PickOrCreateSampleResult> callback);

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createEventParticipant(
		EventDto event,
		PersonDto person,
		ExternalMessageProcessingResult previousResult) {

		EventParticipantDto eventParticipant = buildEventParticipant(event, person);
		HandlerCallback<EventParticipantDto> callback = new HandlerCallback<>();
		handleCreateEventParticipant(eventParticipant, event, externalMessage, callback);

		return mapHandlerResult(callback, previousResult, previousResult::withCreatedEventParticipant);
	}

	private CompletionStage<ProcessingResult<PickOrCreateSampleResult>> pickOrCreateSample(
		Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
		int sampleReportIndex) {

		if (Boolean.TRUE.equals(forceSampleCreation)) {
			return continueWithCreateSample();
		}

		SampleSimilarityCriteria sampleSimilarityCriteria =
			createSampleSimilarCriteria(externalMessage.getSampleReportsNullSafe().get(sampleReportIndex));
		addSampleSearchCriteria.accept(sampleSimilarityCriteria);

		List<SampleDto> selectableSamples = processingFacade.getSamplesByCriteria(sampleSimilarityCriteria.getSampleCriteria());
		List<SampleDto> similarSamples = processingFacade.getSimilarSamples(sampleSimilarityCriteria);
		List<SampleDto> otherSamples = selectableSamples.stream().filter(s -> !similarSamples.contains(s)).collect(Collectors.toList());

		if (similarSamples.isEmpty() && otherSamples.isEmpty()) {
			return continueWithCreateSample();
		}

		HandlerCallback<PickOrCreateSampleResult> callback = new HandlerCallback<>();
		handlePickOrCreateSample(similarSamples, otherSamples, externalMessage, sampleReportIndex, callback);

		return callback.futureResult;
	}

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> editSample(
		SampleDto sample,
		int sampleReportIndex,
		ExternalMessageProcessingResult previousResult) {

		List<PathogenTestDto> newTests = LabMessageProcessingHelper.buildPathogenTests(sample, sampleReportIndex, externalMessage, mapper, user);
		HandlerCallback<SampleAndPathogenTests> callback = new HandlerCallback<>();
		handleEditSample(sample, newTests, externalMessage, mapper, isLastSample(externalMessage, sampleReportIndex), callback);

		return mapHandlerResult(
			callback,
			previousResult,
			r -> previousResult.andWithSampleAndPathogenTests(
				r.getSample(),
				r.getPathogenTests(),
				externalMessage.getSampleReportsNullSafe().get(sampleReportIndex),
				false));
	}

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> handleProcessingDone(
		ProcessingResult<ExternalMessageProcessingResult> result) {
		ProcessingResultStatus status = result.getStatus();

		if (status == ProcessingResultStatus.CANCELED_WITH_CORRECTIONS) {
			return notifyCorrectionsSaved().thenCompose(ignored -> result.asCompletedFuture());
		} else if (status.isDone()) {
			SurveillanceReportDto surveillanceReport = null;
			CaseDataDto caze = result.getData().getCase();
			if (caze != null) {
				surveillanceReport = createSurveillanceReport(externalMessage, caze);
				processingFacade.saveSurveillanceReport(surveillanceReport);
			}
			markExternalMessageAsProcessed(externalMessage, result.getData().getSamples(), surveillanceReport);

		}

		return result.asCompletedFuture();
	}

	protected SurveillanceReportDto createSurveillanceReport(ExternalMessageDto externalMessage, CaseDataDto caze) {
		SurveillanceReportDto surveillanceReport = SurveillanceReportDto.build(caze.toReference(), user.toReference());
		setSurvReportFacility(surveillanceReport, externalMessage, caze);
		surveillanceReport.setReportDate(externalMessage.getMessageDateTime());
		surveillanceReport.setExternalId(externalMessage.getReportMessageId());
		setSurvReportingType(surveillanceReport, externalMessage);
		return surveillanceReport;
	}

	private void setSurvReportFacility(SurveillanceReportDto surveillanceReport, ExternalMessageDto externalMessage, CaseDataDto caze) {
		FacilityReferenceDto reporterReference = processingFacade.getFacilityReference(externalMessage.getReporterExternalIds());
		FacilityDto reporter;
		if (reporterReference != null) {
			reporter = processingFacade.getFacilityByUuid(reporterReference.getUuid());
			surveillanceReport.setFacility(reporterReference);
			if (FacilityDto.OTHER_FACILITY_UUID.equals(reporter.getUuid())) {
				surveillanceReport.setFacilityDetails(I18nProperties.getCaption(Captions.unknown));
			}
			surveillanceReport.setFacilityDistrict(reporter.getDistrict());
			surveillanceReport.setFacilityRegion(reporter.getRegion());
			surveillanceReport.setFacilityType(reporter.getType());
		} else {
			reporter = processingFacade.getFacilityByUuid(FacilityDto.OTHER_FACILITY_UUID);
			surveillanceReport.setFacility(reporter != null ? reporter.toReference() : null);
			String reporterName = externalMessage.getReporterName();
			if (StringUtils.isNotBlank(reporterName)) {
				surveillanceReport.setFacilityDetails(reporterName);
			} else {
				surveillanceReport.setFacilityDetails(I18nProperties.getCaption(Captions.unknown));
			}
			surveillanceReport.setFacilityRegion(caze.getRegion());
			surveillanceReport.setFacilityDistrict(caze.getDistrict());
			if (ExternalMessageType.LAB_MESSAGE.equals(externalMessage.getType())) {
				surveillanceReport.setFacilityType(FacilityType.LABORATORY);
			} else if (ExternalMessageType.PHYSICIANS_REPORT.equals(externalMessage.getType())) {
				surveillanceReport.setFacilityType(FacilityType.HOSPITAL);
			}
		}
	}

	protected void setSurvReportingType(SurveillanceReportDto surveillanceReport, ExternalMessageDto externalMessage) {
		if (ExternalMessageType.LAB_MESSAGE.equals(externalMessage.getType())) {
			surveillanceReport.setReportingType(ReportingType.LABORATORY);
		} else if (ExternalMessageType.PHYSICIANS_REPORT.equals(externalMessage.getType())) {
			surveillanceReport.setReportingType(ReportingType.DOCTOR);
		} else {
			throw new UnsupportedOperationException(
				String.format("There is no reporting type defined for this type of external message: %s", externalMessage.getType()));
		}
	}

	protected abstract CompletionStage<Void> notifyCorrectionsSaved();

	private void markExternalMessageAsProcessed(
		ExternalMessageDto externalMessage,
		List<ExternalMessageProcessingResult.SampleSelection> relatedSampleReports,
		SurveillanceReportDto surveillanceReport) {

		for (ExternalMessageProcessingResult.SampleSelection entry : relatedSampleReports) {
			entry.getSampleReport().setSample(entry.getEntity().toReference());
		}
		if (surveillanceReport != null) {
			externalMessage.setSurveillanceReport(surveillanceReport.toReference());
		}
		externalMessage.setStatus(ExternalMessageStatus.PROCESSED);
		externalMessage.setChangeDate(new Date());
		processingFacade.saveExternalMessage(externalMessage);
	}

	protected abstract void handleEditSample(
		SampleDto sample,
		List<PathogenTestDto> newPathogenTests,
		ExternalMessageDto labMessage,
		ExternalMessageMapper mapper,
		boolean lastSample,
		HandlerCallback<SampleAndPathogenTests> callback);

	public ExternalMessageMapper getMapper() {
		return mapper;
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
}
