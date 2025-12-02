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

package de.symeda.sormas.api.externalmessage.processing;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.processing.labmessage.LabMessageProcessingHelper;
import de.symeda.sormas.api.externalmessage.processing.labmessage.SampleAndPathogenTests;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;
import de.symeda.sormas.api.utils.dataprocessing.flow.FlowThen;

/**
 * Abstract base class for processing external messages. This class defines the flow for handling
 * external messages and provides utility methods for creating or selecting cases, contacts, and event participants.
 */
public abstract class AbstractMessageProcessingFlowBase extends AbstractProcessingFlow {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageProcessingFlowBase.class);

    private final Boolean forceSampleCreation;
    private final ExternalMessageDto externalMessage;

    protected AbstractMessageProcessingFlowBase(
        UserDto user,
        ExternalMessageDto externalMessage,
        ExternalMessageMapper mapper,
        ExternalMessageProcessingFacade processingFacade) {
        this(user, externalMessage, mapper, processingFacade, false);
    }

    protected AbstractMessageProcessingFlowBase(
        UserDto user,
        ExternalMessageDto externalMessage,
        ExternalMessageMapper mapper,
        ExternalMessageProcessingFacade processingFacade,
        Boolean forceSampleCreation) {
        super(user, mapper, processingFacade);
        this.externalMessage = externalMessage;
        this.forceSampleCreation = forceSampleCreation;
    }

    /**
     * Executes the processing flow for the external message.
     *
     * @return A {@link CompletionStage} containing the result of the processing.
     */
    public CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> run() {

        logger.debug("[MESSAGE PROCESSING] Start processing lab message: {}", externalMessage);

        //@formatter:off
        return doInitialChecks(externalMessage, new ExternalMessageProcessingResult())
            .then(initialCheckResult -> doInitialSetup(initialCheckResult))
            // if no handling happened, or opted to continue regular processing, ignore results
            .then(ignored -> pickOrCreatePerson(new ExternalMessageProcessingResult()))
            .thenSwitch(p -> pickOrCreateEntry(p.getData(), externalMessage))
                .when(PickOrCreateEntryResult::isNewCase, (f, p, r) -> doCreateCaseFlow(f))
                .when(PickOrCreateEntryResult::isNewContact, (f, p, r) -> doCreateContactFlow(f))
                .when(PickOrCreateEntryResult::isNewEventParticipant, (f, p, r) -> doCreateEventParticipantFlow(f))
                .when(PickOrCreateEntryResult::isSelectedCase, (f, p, r) -> doCaseSelectedFlow(p.getCaze(), f))
                .when(PickOrCreateEntryResult::isSelectedContact, (f, p, r) -> doContactSelectedFlow(p.getContact(), f))
                .when(PickOrCreateEntryResult::isSelectedEventParticipant, (f, p, r) -> doEventParticipantSelectedFlow(p.getEventParticipant(), f))
            .then(f -> {
                logger.debug("[MESSAGE PROCESSING] Processing done: {}", f.getData());
                return ProcessingResult.of(ProcessingResultStatus.DONE, f.getData()).asCompletedFuture();
            })
            .getResult().thenCompose(this::handleProcessingDone);
        //@formatter:on
    }

    /**
     * Performs the initial setup for the processing flow.
     *
     * @param previousResult
     *            The result of the previous processing step.
     * @return A {@link CompletionStage} containing the result of the setup.
     */
    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> doInitialSetup(
        ProcessingResult<ExternalMessageProcessingResult> previousResult) {
        return previousResult.asCompletedFuture();
    }

    protected FlowThen<ExternalMessageProcessingResult> doCreateCaseFlow(FlowThen<ExternalMessageProcessingResult> flow) {

        FlowThen<ExternalMessageProcessingResult> caseFlow = flow.then(p -> createCase(p.getData()));

        return caseFlow.then(p -> {
            ExternalMessageProcessingResult previousResult = p.getData();
            CaseDataDto caze = previousResult.getCase();

            logger.debug("[MESSAGE PROCESSING] Continue processing with case: {}", previousResult);

            BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForCase =
                (sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(caze, sampleReportIndex, true, previousSampleResult);

            return doPickOrCreateSamplesFlow(c -> c.sampleCriteria(new SampleCriteria().caze(caze.toReference())), createSampleForCase, caseFlow)
                .getResult();
        });
    }

    protected FlowThen<ExternalMessageProcessingResult> doCreateContactFlow(FlowThen<ExternalMessageProcessingResult> flow) {

        FlowThen<ExternalMessageProcessingResult> contactFlow = flow.then(p -> createContact(p.getData().getPerson(), p.getData()));
        return contactFlow.then(p -> {
            logger.debug("[MESSAGE PROCESSING] Continue processing with contact: {}", p.getData());

            ContactDto contact = p.getData().getContact();
            BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForContact =
                (sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(contact, sampleReportIndex, true, previousSampleResult);

            return doPickOrCreateSamplesFlow(
                c -> c.sampleCriteria(new SampleCriteria().contact(contact.toReference())),
                createSampleForContact,
                contactFlow).getResult();
        });
    }

    protected FlowThen<ExternalMessageProcessingResult> doCreateEventParticipantFlow(FlowThen<ExternalMessageProcessingResult> flow) {

        //@formatter:off
		return flow.thenSwitch(p -> pickOrCreateEvent())
				.when(PickOrCreateEventResult::isNewEvent, (f, p, r) -> {
					FlowThen<ExternalMessageProcessingResult> eventFlow = f.then(ignored -> createEvent(r));
					return eventFlow.then(ef -> {
						logger.debug("[MESSAGE PROCESSING] Continue processing with event: {}", ef.getData());

						return doCreateEventParticipantAndSamplesFlow(r.getPerson(), eventFlow).getResult();
					});
				})
				.when(PickOrCreateEventResult::isEventSelected, (f, p, r) -> f
					.thenSwitch(e -> validateSelectedEvent(p.getEvent(), e.getData().getPerson()))
						.when(EventValidationResult::isEventSelected, (vf, v, vr) -> {
							FlowThen<ExternalMessageProcessingResult> eventFlow = vf.then(e -> {
								ExternalMessageProcessingResult withEvent = e.getData().withSelectedEvent(v.getEvent());

								logger.debug("[MESSAGE PROCESSING] Continue processing with event: {}", withEvent);
								
								return ProcessingResult.continueWith(withEvent).asCompletedFuture();
							});
							return doCreateEventParticipantAndSamplesFlow(vr.getPerson(), eventFlow);
						})
						.when(EventValidationResult::isEventParticipantSelected, (vf, v, vr) -> {
							EventDto event = getExternalMessageProcessingFacade().getEventByUuid(p.getEvent().getUuid());
							EventParticipantDto eventParticipant = getExternalMessageProcessingFacade().getEventParticipantByUuid(v.getEventParticipant().getUuid());

							FlowThen<ExternalMessageProcessingResult> eventParticipantFlow = vf.then(ignored -> {
								ExternalMessageProcessingResult withEventParticipant = vr.withSelectedEvent(event).withSelectedEventParticipant(eventParticipant);
								logger.debug("[MESSAGE PROCESSING] Continue processing with event participant: {}", withEventParticipant);
								
								return ProcessingResult
										.continueWith(withEventParticipant)
										.asCompletedFuture();
							});

							BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForEventParticipant =
								(sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(
									SampleDto.build(getUser().toReference(), eventParticipant.toReference()),
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
						.when(EventValidationResult::isEventSelectionCanceled, (vf, v, vr) -> {
							logger.debug("[MESSAGE PROCESSING] Event selection discarded");
							return vf.then(ignored -> doCreateEventParticipantFlow(vf).getResult());
						})
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
            logger.debug("[MESSAGE PROCESSING] Continue processing with event participant: {}", p.getData());

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

    protected FlowThen<ExternalMessageProcessingResult> doPickOrCreateSamplesFlow(
        Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
        BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleAndPathogenTests,
        FlowThen<ExternalMessageProcessingResult> flow) {

        logger.debug("[MESSAGE PROCESSING] Processing sample report(s)");

        List<SampleReportDto> sampleReports = getExternalMessage().getSampleReportsNullSafe();
        if (sampleReports.size() > 1) {
            flow = flow.then(result -> handleMultipleSampleConfirmation().thenCompose(next -> {
                boolean confirmed = Boolean.TRUE.equals(next);
                if (!confirmed) {
                    logger.debug("[MESSAGE PROCESSING] Canceled processing of multiple sample reports.");
                    return ProcessingResult.withStatus(ProcessingResultStatus.CANCELED, result.getData()).asCompletedFuture();
                }

                return ProcessingResult.withStatus(ProcessingResultStatus.CONTINUE, result.getData()).asCompletedFuture();
            }));
        }

        int i = 0;
        do {
            flow = doSinglePickOrCreateSampleFlow(addSampleSearchCriteria, createSampleAndPathogenTests, flow, i);
            i += 1;
        }
        while (i < sampleReports.size());

        return flow;
    }

    protected FlowThen<ExternalMessageProcessingResult> doCaseSelectedFlow(
        CaseSelectionDto caseSelection,
        FlowThen<ExternalMessageProcessingResult> flow) {

        CaseDataDto caze = getExternalMessageProcessingFacade().getCaseDataByUuid(caseSelection.getUuid());

        BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForCase =
            (sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(caze, sampleReportIndex, false, previousSampleResult);

        FlowThen<ExternalMessageProcessingResult> caseFlow = flow.then(previousResult -> {
            ExternalMessageProcessingResult withCase = previousResult.getData().withSelectedCase(caze);

            logger.debug("[MESSAGE PROCESSING] Continue processing with case: {}", withCase);

            return ProcessingResult.continueWith(withCase).asCompletedFuture();
        });
        return caseFlow.then(
            previousResult -> doPickOrCreateSamplesFlow(
                c -> c.sampleCriteria(new SampleCriteria().caze(caze.toReference())),
                createSampleForCase,
                caseFlow).getResult());
    }

    protected FlowThen<ExternalMessageProcessingResult> doContactSelectedFlow(
        SimilarContactDto contactSelection,
        FlowThen<ExternalMessageProcessingResult> flow) {

        ContactDto contact = getExternalMessageProcessingFacade().getContactByUuid(contactSelection.getUuid());

        BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForContact =
            (sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(contact, sampleReportIndex, false, previousSampleResult);

        FlowThen<ExternalMessageProcessingResult> contactFlow = flow.then(previousResult -> {
            ExternalMessageProcessingResult withContact = previousResult.getData().withSelectedContact(contact);

            logger.debug("[MESSAGE PROCESSING] Continue processing with contact: {}", withContact);

            return ProcessingResult.continueWith(withContact).asCompletedFuture();
        });

        return contactFlow.then(
            p -> doPickOrCreateSamplesFlow(
                c -> c.sampleCriteria(new SampleCriteria().contact(contact.toReference())),
                createSampleForContact,
                contactFlow).getResult());
    }

    protected FlowThen<ExternalMessageProcessingResult> doEventParticipantSelectedFlow(
        SimilarEventParticipantDto eventParticipantSelection,
        FlowThen<ExternalMessageProcessingResult> flow) {

        EventParticipantDto eventParticipant = getExternalMessageProcessingFacade().getEventParticipantByUuid(eventParticipantSelection.getUuid());
        EventDto event = getExternalMessageProcessingFacade().getEventByUuid(eventParticipant.getEvent().getUuid());

        BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleForEventParticipant =
            (sampleReportIndex, previousSampleResult) -> createOneSampleAndPathogenTests(
                eventParticipant.toReference(),
                event,
                sampleReportIndex,
                false,
                previousSampleResult);

        FlowThen<ExternalMessageProcessingResult> eventParticipantFlow = flow.then(previousResult -> {
            ExternalMessageProcessingResult withEventParticipant =
                previousResult.getData().withSelectedEvent(event).withSelectedEventParticipant(eventParticipant);

            logger.debug("[MESSAGE PROCESSING] Continue processing with event participant: {}", withEventParticipant);

            return ProcessingResult.continueWith(withEventParticipant).asCompletedFuture();
        });

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

    /**
     * Retrieves similar cases based on the selected person and lab message.
     * This method will call getSimilarCases(PersonReferenceDto selectedPerson, Disease disease) with the disease of the external message by
     * default.
     * 
     * @param selectedPerson
     * @param externalMessage
     * @return
     */
    protected List<CaseSelectionDto> getSimilarCases(PersonReferenceDto selectedPerson, ExternalMessageDto externalMessage) {
        return getSimilarCases(selectedPerson, externalMessage.getDisease());
    }

    protected List<CaseSelectionDto> getSimilarCases(PersonReferenceDto selectedPerson, Disease disease) {
        return getExternalMessageProcessingFacade().getSimilarCases(selectedPerson, disease);
    }

    /**
     * Retrieves similar contacts based on the selected person and lab message.
     * This method will call getSimilarContacts(PersonReferenceDto selectedPerson, Disease disease) with the disease of the external message
     * by default.
     * 
     * @param selectedPerson
     * @param externalMessage
     * @return
     */
    protected List<SimilarContactDto> getSimilarContacts(PersonReferenceDto selectedPerson, ExternalMessageDto externalMessage) {
        return getSimilarContacts(selectedPerson, externalMessage.getDisease());
    }

    protected List<SimilarContactDto> getSimilarContacts(PersonReferenceDto selectedPerson, Disease disease) {
        return getExternalMessageProcessingFacade().getSimilarContacts(selectedPerson, disease);
    }

    /**
     * Retrieves similar event participants based on the selected person and lab message.
     * This method will call getSimilarEventParticipants(PersonReferenceDto selectedPerson, Disease disease) with the disease of the
     * external message by default.
     * 
     * @param selectedPerson
     * @param externalMessage
     * @return
     */
    protected List<SimilarEventParticipantDto> getSimilarEventParticipants(PersonReferenceDto selectedPerson, ExternalMessageDto externalMessage) {
        return getSimilarEventParticipants(selectedPerson, externalMessage.getDisease());
    }

    /**
     * Retrieves similar event participants based on the selected person and lab message.
     *
     * @param selectedPerson
     *            The selected person reference.
     * @param externalMessage
     *            The external lab message.
     * @return A list of similar event participants.
     */
    protected List<SimilarEventParticipantDto> getSimilarEventParticipants(PersonReferenceDto selectedPerson, Disease disease) {

        if (getExternalMessageProcessingFacade().isFeatureDisabled(FeatureType.EVENT_SURVEILLANCE)
            || !getExternalMessageProcessingFacade().hasAllUserRights(UserRight.EVENTPARTICIPANT_CREATE, UserRight.EVENTPARTICIPANT_EDIT)) {
            return Collections.emptyList();
        }

        EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
        eventParticipantCriteria.setPerson(selectedPerson);
        eventParticipantCriteria.setDisease(disease);

        return getExternalMessageProcessingFacade().getMatchingEventParticipants(eventParticipantCriteria);
    }

    /**
     * Creates a new case based on the external message and previous result.
     *
     * @param previousResult
     *            The result of the previous processing step.
     * @return A {@link CompletionStage} containing the result of the case creation.
     */
    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createCase(ExternalMessageProcessingResult previousResult) {

        PersonDto person = previousResult.getPerson();
        CaseDataDto caze = buildCase(person, externalMessage);

        HandlerCallback<CaseDataDto> callback = new HandlerCallback<>();
        handleCreateCase(caze, person, externalMessage, callback);

        return mapHandlerResult(callback, previousResult, previousResult::withCreatedCase);
    }

    /**
     * Picks or creates an entry based on the external message and previous result.
     *
     * @param previousResult
     *            The result of the previous processing step.
     * @param externalMessage
     *            The external message to be processed.
     * @return A {@link CompletionStage} containing the result of the entry selection or creation.
     */
    protected CompletionStage<ProcessingResult<PickOrCreateEntryResult>> pickOrCreateEntry(
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

    /**
     * Creates a new contact based on the external message and previous result.
     *
     * @param person
     *            The person associated with the contact.
     * @param previousResult
     *            The result of the previous processing step.
     * @return A {@link CompletionStage} containing the result of the contact creation.
     */
    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createContact(
        PersonDto person,
        ExternalMessageProcessingResult previousResult) {

        ContactDto contact = buildContact(getExternalMessage(), person);
        HandlerCallback<ContactDto> callback = new HandlerCallback<>();
        handleCreateContact(contact, person, getExternalMessage(), callback);

        return mapHandlerResult(callback, previousResult, previousResult::withCreatedContact);
    }

    /**
     * Builds an event participant based on the event and person.
     *
     * @param eventDto
     *            The event associated with the participant.
     * @param person
     *            The person associated with the participant.
     * @return The constructed event participant.
     */
    protected EventParticipantDto buildEventParticipant(EventDto eventDto, PersonDto person) {

        EventParticipantDto eventParticipant = EventParticipantDto.build(eventDto.toReference(), getUser().toReference());
        eventParticipant.setPerson(person);
        return eventParticipant;
    }

    /**
     * Picks or creates an event based on the external message.
     *
     * @return A {@link CompletionStage} containing the result of the event selection or creation.
     */
    protected CompletionStage<ProcessingResult<PickOrCreateEventResult>> pickOrCreateEvent() {

        HandlerCallback<PickOrCreateEventResult> callback = new HandlerCallback<>();
        handlePickOrCreateEvent(getExternalMessage(), callback);

        return callback.futureResult;
    }

    /**
     * Creates a new event based on the external message and previous result.
     *
     * @param previousResult
     *            The result of the previous processing step.
     * @return A {@link CompletionStage} containing the result of the event creation.
     */
    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createEvent(ExternalMessageProcessingResult previousResult) {

        EventDto event = EventDto.build(getExternalMessageProcessingFacade().getServerCountry(), getUser(), externalMessage.getDisease());
        event.setDiseaseVariant(externalMessage.getDiseaseVariant());
        event.setDiseaseVariantDetails(externalMessage.getDiseaseVariantDetails());

        HandlerCallback<EventDto> callback = new HandlerCallback<>();
        handleCreateEvent(event, callback);

        return mapHandlerResult(callback, previousResult, previousResult::withCreatedEvent);
    }

    /**
     * Builds a contact based on the external message and person.
     *
     * @param externalMessageDto
     *            The external message associated with the contact.
     * @param person
     *            The person associated with the contact.
     * @return The constructed contact.
     */
    protected ContactDto buildContact(ExternalMessageDto externalMessageDto, PersonDto person) {

        ContactDto contactDto = ContactDto.build(null, externalMessageDto.getDisease(), null, null);
        contactDto.setReportingUser(getUser().toReference());
        contactDto.setReportDateTime(externalMessageDto.getMessageDateTime());
        contactDto.setPerson(person.toReference());
        return contactDto;
    }

    /**
     * Creates a new event participant based on the event, person, and previous result.
     *
     * @param event
     *            The event associated with the participant.
     * @param person
     *            The person associated with the participant.
     * @param previousResult
     *            The result of the previous processing step.
     * @return A {@link CompletionStage} containing the result of the event participant creation.
     */
    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createEventParticipant(

        EventDto event,
        PersonDto person,
        ExternalMessageProcessingResult previousResult) {

        EventParticipantDto eventParticipant = buildEventParticipant(event, person);
        HandlerCallback<EventParticipantDto> callback = new HandlerCallback<>();
        handleCreateEventParticipant(eventParticipant, event, externalMessage, callback);

        return mapHandlerResult(callback, previousResult, previousResult::withCreatedEventParticipant);
    }

    /**
     * Validates the selected event based on the person.
     *
     * @param event
     *            The selected event.
     * @param person
     *            The person associated with the event.
     * @return A {@link CompletionStage} containing the result of the event validation.
     */
    protected CompletionStage<ProcessingResult<EventValidationResult>> validateSelectedEvent(EventIndexDto event, PersonDto person) {

        CompletableFuture<ProcessingResult<EventValidationResult>> ret = new CompletableFuture<>();

        EventCriteria eventCriteria = new EventCriteria();
        eventCriteria.setPerson(person.toReference());
        eventCriteria.setUserFilterIncluded(false);
        List<EventIndexDto> personEvents = getExternalMessageProcessingFacade().getEventsByCriteria(eventCriteria);

        EventValidationResult validationResult = new EventValidationResult();
        if (personEvents.contains(event)) {
            // event participant already exists
            return confirmPickExistingEventParticipant().thenCompose(useEventParticipant -> {
                if (Boolean.TRUE.equals(useEventParticipant)) {
                    validationResult.setEventParticipant(
                        getExternalMessageProcessingFacade().getEventParticipantRefByEventAndPerson(event.getUuid(), person.getUuid()));
                } else {
                    validationResult.setEventSelectionCanceled(true);
                }

                return ProcessingResult.continueWith(validationResult).asCompletedFuture();
            });
        } else {
            validationResult.setEvent(getExternalMessageProcessingFacade().getEventByUuid(event.getUuid()));
            ret.complete(ProcessingResult.continueWith(validationResult));
        }

        return ret;
    }

    /**
     * Handles the completion of the processing flow.
     *
     * @param result
     *            The result of the processing.
     * @return A {@link CompletionStage} containing the final result of the processing.
     */
    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> handleProcessingDone(
        ProcessingResult<ExternalMessageProcessingResult> result) {
        ProcessingResultStatus status = result.getStatus();

        if (status == ProcessingResultStatus.CANCELED_WITH_CORRECTIONS) {
            return notifyCorrectionsSaved().thenCompose(ignored -> result.asCompletedFuture());
        } else if (status.isDone()) {
            SurveillanceReportDto surveillanceReport = null;
            CaseDataDto caze = result.getData().getCase();
            if (caze != null) {
                surveillanceReport = createSurveillanceReport(externalMessage, caze);
                getExternalMessageProcessingFacade().saveSurveillanceReport(surveillanceReport);
            }
            markExternalMessageAsProcessed(externalMessage, result, surveillanceReport);

        }

        return result.asCompletedFuture();
    }

    /**
     * Creates a surveillance report based on the external message and case.
     *
     * @param externalMessage
     *            The external message associated with the report.
     * @param caze
     *            The case associated with the report.
     * @return The constructed surveillance report.
     */
    protected SurveillanceReportDto createSurveillanceReport(ExternalMessageDto externalMessage, CaseDataDto caze) {

        SurveillanceReportDto surveillanceReport = SurveillanceReportDto.build(caze.toReference(), getUser().toReference());
        setSurvReportFacility(surveillanceReport, externalMessage, caze);
        surveillanceReport.setReportDate(externalMessage.getMessageDateTime());
        surveillanceReport.setExternalId(externalMessage.getReportMessageId());
        setSurvReportingType(surveillanceReport, externalMessage);
        return surveillanceReport;
    }

    /**
     * Sets the facility information for the surveillance report.
     *
     * @param surveillanceReport
     *            The surveillance report to update.
     * @param externalMessage
     *            The external message associated with the report.
     * @param caze
     *            The case associated with the report.
     */
    protected void setSurvReportFacility(SurveillanceReportDto surveillanceReport, ExternalMessageDto externalMessage, CaseDataDto caze) {
        FacilityReferenceDto reporterReference = getExternalMessageProcessingFacade().getFacilityReference(externalMessage.getReporterExternalIds());

        if (reporterReference != null) {
            FacilityDto reporter = getExternalMessageProcessingFacade().getFacilityByUuid(reporterReference.getUuid());

            surveillanceReport.setFacility(reporterReference);

            if (FacilityDto.OTHER_FACILITY_UUID.equals(reporter.getUuid())) {
                surveillanceReport.setFacilityDetails(externalMessage.getReporterName());

                if (ExternalMessageType.LAB_MESSAGE.equals(externalMessage.getType())) {
                    surveillanceReport.setFacilityType(FacilityType.LABORATORY);
                } else if (ExternalMessageType.PHYSICIANS_REPORT.equals(externalMessage.getType())) {
                    surveillanceReport.setFacilityType(FacilityType.HOSPITAL);
                }

                surveillanceReport.setFacilityRegion(caze.getResponsibleRegion());
                surveillanceReport.setFacilityDistrict(caze.getResponsibleDistrict());
            } else {
                surveillanceReport.setFacilityType(reporter.getType());
                surveillanceReport.setFacilityDistrict(reporter.getDistrict());
                surveillanceReport.setFacilityRegion(reporter.getRegion());
            }
        }
    }

    /**
     * Sets the reporting type for the surveillance report.
     *
     * @param surveillanceReport
     *            The surveillance report to update.
     * @param externalMessage
     *            The external message associated with the report.
     */
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

    private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createOneSampleAndPathogenTests(
        CaseDataDto caze,
        int sampleReportIndex,
        boolean entityCreated,
        ExternalMessageProcessingResult previousResult) {

        SampleDto sample = SampleDto.build(getUser().toReference(), caze.toReference());
        return createOneSampleAndPathogenTests(sample, caze.getDisease(), sampleReportIndex, entityCreated, previousResult);
    }

    private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createOneSampleAndPathogenTests(
        ContactDto contact,
        int sampleReportIndex,
        boolean entityCreated,
        ExternalMessageProcessingResult previousResult) {

        SampleDto sample = SampleDto.build(getUser().toReference(), contact.toReference());
        return createOneSampleAndPathogenTests(sample, contact.getDisease(), sampleReportIndex, entityCreated, previousResult);
    }

    private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createOneSampleAndPathogenTests(
        EventParticipantReferenceDto eventParticipant,
        EventDto event,
        int sampleReportIndex,
        boolean entityCreated,
        ExternalMessageProcessingResult previousResult) {

        SampleDto sample = SampleDto.build(getUser().toReference(), eventParticipant);
        return createOneSampleAndPathogenTests(sample, event.getDisease(), sampleReportIndex, entityCreated, previousResult);
    }

    private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createOneSampleAndPathogenTests(
        SampleDto sample,
        Disease disease,
        int sampleReportIndex,
        boolean entityCreated,
        ExternalMessageProcessingResult previousResult) {

        SampleReportDto sampleReport = getExternalMessage().getSampleReportsNullSafe().get(sampleReportIndex);
        getMapper().mapToSample(sample, sampleReport);
        List<PathogenTestDto> pathogenTests =
            LabMessageProcessingHelper.buildPathogenTests(sample, sampleReportIndex, getExternalMessage(), getMapper(), getUser());
        HandlerCallback<SampleAndPathogenTests> callback = new HandlerCallback<>();
        handleCreateSampleAndPathogenTests(
            sample,
            pathogenTests,
            disease,
            getExternalMessage(),
            entityCreated,
            isLastSample(getExternalMessage(), sampleReportIndex),
            callback);

        return mapHandlerResult(callback, previousResult, s -> {
            ExternalMessageProcessingResult withSampleAndPathogenTests =
                previousResult.andWithSampleAndPathogenTests(s.getSample(), s.getPathogenTests(), sampleReport, true);

            logger.debug("[MESSAGE PROCESSING] Continue processing with sample and pathogen tests: {}", withSampleAndPathogenTests);

            return withSampleAndPathogenTests;
        });
    }

    private CompletionStage<ProcessingResult<PickOrCreateSampleResult>> pickOrCreateSample(
        Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
        int sampleReportIndex) {

        if (Boolean.TRUE.equals(forceSampleCreation)) {
            return continueWithCreateSample();
        }

        SampleSimilarityCriteria sampleSimilarityCriteria =
            createSampleSimilarCriteria(getExternalMessage().getSampleReportsNullSafe().get(sampleReportIndex));
        addSampleSearchCriteria.accept(sampleSimilarityCriteria);

        List<SampleDto> selectableSamples = getExternalMessageProcessingFacade().getSamplesByCriteria(sampleSimilarityCriteria.getSampleCriteria());
        List<SampleDto> similarSamples = getExternalMessageProcessingFacade().getSimilarSamples(sampleSimilarityCriteria);
        List<SampleDto> otherSamples = selectableSamples.stream().filter(s -> !similarSamples.contains(s)).collect(Collectors.toList());

        if (similarSamples.isEmpty() && otherSamples.isEmpty()) {
            return continueWithCreateSample();
        }

        HandlerCallback<PickOrCreateSampleResult> callback = new HandlerCallback<>();
        handlePickOrCreateSample(similarSamples, otherSamples, getExternalMessage(), sampleReportIndex, callback);

        return callback.futureResult;
    }

    private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> editSample(
        SampleDto sample,
        int sampleReportIndex,
        ExternalMessageProcessingResult previousResult) {

        List<PathogenTestDto> newTests =
            LabMessageProcessingHelper.buildPathogenTests(sample, sampleReportIndex, getExternalMessage(), getMapper(), getUser());
        HandlerCallback<SampleAndPathogenTests> callback = new HandlerCallback<>();
        handleEditSample(sample, newTests, getExternalMessage(), getMapper(), isLastSample(getExternalMessage(), sampleReportIndex), callback);

        return mapHandlerResult(callback, previousResult, r -> {
            ExternalMessageProcessingResult withSampleAndPathogenTests = previousResult.andWithSampleAndPathogenTests(
                r.getSample(),
                r.getPathogenTests(),
                getExternalMessage().getSampleReportsNullSafe().get(sampleReportIndex),
                false);

            logger.debug("[MESSAGE PROCESSING] Continue processing with sample and pathogen tests: {}", withSampleAndPathogenTests);

            return withSampleAndPathogenTests;
        });
    }

    protected abstract void handlePickOrCreateSample(
        List<SampleDto> similarSamples,
        List<SampleDto> otherSamples,
        ExternalMessageDto labMessage,
        int sampleReportIndex,
        HandlerCallback<PickOrCreateSampleResult> callback);

    protected abstract void handleEditSample(
        SampleDto sample,
        List<PathogenTestDto> newPathogenTests,
        ExternalMessageDto labMessage,
        ExternalMessageMapper mapper,
        boolean lastSample,
        HandlerCallback<SampleAndPathogenTests> callback);

    public abstract CompletionStage<Boolean> handleMultipleSampleConfirmation();

    protected abstract void handleCreateSampleAndPathogenTests(
        SampleDto sample,
        List<PathogenTestDto> pathogenTests,
        Disease disease,
        ExternalMessageDto labMessage,
        boolean entityCreated,
        boolean lastSample,
        HandlerCallback<SampleAndPathogenTests> callback);

    /**
     * Confirms whether to pick an existing event participant.
     *
     * @return A {@link CompletionStage} containing the confirmation result.
     */
    protected abstract CompletionStage<Boolean> confirmPickExistingEventParticipant();

    /**
     * Notifies that corrections have been saved.
     *
     * @return A {@link CompletionStage} representing the completion of the notification.
     */
    protected abstract CompletionStage<Void> notifyCorrectionsSaved();

    /**
     * Handles the selection or creation of an entry.
     *
     * @param similarCases
     *            A list of similar cases.
     * @param similarContacts
     *            A list of similar contacts.
     * @param similarEventParticipants
     *            A list of similar event participants.
     * @param externalMessageDto
     *            The external message associated with the entry.
     * @param callback
     *            The callback for handling the result.
     */
    protected abstract void handlePickOrCreateEntry(
        List<CaseSelectionDto> similarCases,
        List<SimilarContactDto> similarContacts,
        List<SimilarEventParticipantDto> similarEventParticipants,
        ExternalMessageDto externalMessageDto,
        HandlerCallback<PickOrCreateEntryResult> callback);

    /**
     * Handles the creation of a case.
     *
     * @param caze
     *            The case to be created.
     * @param person
     *            The person associated with the case.
     * @param labMessage
     *            The external lab message associated with the case.
     * @param callback
     *            The callback for handling the result.
     */
    protected abstract void handleCreateCase(
        CaseDataDto caze,
        PersonDto person,
        ExternalMessageDto labMessage,
        HandlerCallback<CaseDataDto> callback);

    /**
     * Handles the creation of a contact.
     *
     * @param contact
     *            The contact to be created.
     * @param person
     *            The person associated with the contact.
     * @param externalMessage
     *            The external message associated with the contact.
     * @param callback
     *            The callback for handling the result.
     */
    protected abstract void handleCreateContact(
        ContactDto contact,
        PersonDto person,
        ExternalMessageDto externalMessage,
        HandlerCallback<ContactDto> callback);

    /**
     * Handles the selection or creation of an event.
     *
     * @param externalMessage
     *            The external message associated with the event.
     * @param callback
     *            The callback for handling the result.
     */
    protected abstract void handlePickOrCreateEvent(ExternalMessageDto externalMessage, HandlerCallback<PickOrCreateEventResult> callback);

    /**
     * Handles the creation of an event.
     *
     * @param event
     *            The event to be created.
     * @param callback
     *            The callback for handling the result.
     */
    protected abstract void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback);

    /**
     * Handles the creation of an event participant.
     *
     * @param eventParticipant
     *            The event participant to be created.
     * @param event
     *            The event associated with the participant.
     * @param externalMessage
     *            The external message associated with the participant.
     * @param callback
     *            The callback for handling the result.
     */
    protected abstract void handleCreateEventParticipant(
        EventParticipantDto eventParticipant,
        EventDto event,
        ExternalMessageDto externalMessage,
        HandlerCallback<EventParticipantDto> callback);

    /**
     * Marks the external message as processed.
     *
     * @param externalMessage
     *            The external message to be marked.
     * @param result
     *            The result of the processing.
     * @param surveillanceReport
     *            The surveillance report associated with the message.
     */
    protected abstract void markExternalMessageAsProcessed(
        ExternalMessageDto externalMessage,
        ProcessingResult<ExternalMessageProcessingResult> result,
        SurveillanceReportDto surveillanceReport);

    protected void doPersonUpdates(EntitySelection<PersonDto> personSelection) {
        // requested for #13589
        // TODO: we need to find a better way to handle this

        if (personSelection.isNew()) {
            // no updates for new persons
            return;
        }

        final PersonDto person = personSelection.getEntity();
        if (person == null) {
            return;
        }

        final LocationDto personAddress = person.getAddress();

        if (personAddress != null) {
            final String houseNumber = getExternalMessage().getPersonHouseNumber();
            if (houseNumber != null) {
                personAddress.setHouseNumber(houseNumber);
            }
            final String street = getExternalMessage().getPersonStreet();
            if (street != null) {
                personAddress.setStreet(street);
            }
            final String city = getExternalMessage().getPersonCity();
            if (city != null) {
                personAddress.setCity(city);
            }
            final String postalCode = getExternalMessage().getPersonPostalCode();
            if (postalCode != null) {
                personAddress.setPostalCode(postalCode);
            }
            final CountryReferenceDto country = getExternalMessage().getPersonCountry();
            if (country != null) {
                personAddress.setCountry(country);
            }

            getExternalMessageProcessingFacade().updatePerson(person);
        }
    }

    /**
     * Retrieves the external message associated with the processing flow.
     *
     * @return The external message.
     */
    public ExternalMessageDto getExternalMessage() {
        return externalMessage;
    }

}
