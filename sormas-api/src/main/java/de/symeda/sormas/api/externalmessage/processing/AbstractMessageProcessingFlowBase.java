package de.symeda.sormas.api.externalmessage.processing;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;
import de.symeda.sormas.api.utils.dataprocessing.flow.FlowThen;

public abstract class AbstractMessageProcessingFlowBase extends AbstractProcessingFlow {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageProcessingFlowBase.class);

    private final ExternalMessageDto externalMessage;

    protected AbstractMessageProcessingFlowBase(
        UserDto user,
        ExternalMessageDto externalMessage,
        ExternalMessageMapper mapper,
        ExternalMessageProcessingFacade processingFacade) {
        super(user, mapper, processingFacade);
        this.externalMessage = externalMessage;
    }

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

    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> doInitialSetup(
        ProcessingResult<ExternalMessageProcessingResult> previousResult) {
        return previousResult.asCompletedFuture();
    }

    protected FlowThen<ExternalMessageProcessingResult> doCreateCaseFlow(FlowThen<ExternalMessageProcessingResult> flow) {
        return flow.then(p -> createCase(p.getData()));
    }

    protected FlowThen<ExternalMessageProcessingResult> doCreateContactFlow(FlowThen<ExternalMessageProcessingResult> flow) {
        return flow.then(p -> createContact(p.getData().getPerson(), p.getData()));
    }

    protected abstract FlowThen<ExternalMessageProcessingResult> doCreateEventParticipantFlow(FlowThen<ExternalMessageProcessingResult> flow);

    protected abstract FlowThen<ExternalMessageProcessingResult> doCaseSelectedFlow(
        CaseSelectionDto caseSelection,
        FlowThen<ExternalMessageProcessingResult> flow);

    protected abstract FlowThen<ExternalMessageProcessingResult> doContactSelectedFlow(
        SimilarContactDto contactSelection,
        FlowThen<ExternalMessageProcessingResult> flow);

    protected abstract FlowThen<ExternalMessageProcessingResult> doEventParticipantSelectedFlow(
        SimilarEventParticipantDto eventParticipantSelection,
        FlowThen<ExternalMessageProcessingResult> flow);

    protected List<SimilarEventParticipantDto> getSimilarEventParticipants(PersonReferenceDto selectedPerson, ExternalMessageDto labMessage) {

        if (getExternalMessageProcessingFacade().isFeatureDisabled(FeatureType.EVENT_SURVEILLANCE)
            || !getExternalMessageProcessingFacade().hasAllUserRights(UserRight.EVENTPARTICIPANT_CREATE, UserRight.EVENTPARTICIPANT_EDIT)) {
            return Collections.emptyList();
        }

        EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
        eventParticipantCriteria.setPerson(selectedPerson);
        eventParticipantCriteria.setDisease(labMessage.getDisease());

        return getExternalMessageProcessingFacade().getMatchingEventParticipants(eventParticipantCriteria);
    }

    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createCase(ExternalMessageProcessingResult previousResult) {

        PersonDto person = previousResult.getPerson();
        CaseDataDto caze = buildCase(person, externalMessage);

        HandlerCallback<CaseDataDto> callback = new HandlerCallback<>();
        handleCreateCase(caze, person, externalMessage, callback);

        return mapHandlerResult(callback, previousResult, previousResult::withCreatedCase);
    }

    protected CompletionStage<ProcessingResult<PickOrCreateEntryResult>> pickOrCreateEntry(
        ExternalMessageProcessingResult previousResult,
        ExternalMessageDto externalMessage) {

        PersonReferenceDto personRef = previousResult.getPerson().toReference();
        List<CaseSelectionDto> similarCases = getExternalMessageProcessingFacade().getSimilarCases(personRef, externalMessage.getDisease());
        List<SimilarContactDto> similarContacts = getExternalMessageProcessingFacade().getSimilarContacts(personRef, externalMessage.getDisease());
        List<SimilarEventParticipantDto> similarEventParticipants = getSimilarEventParticipants(personRef, externalMessage);

        HandlerCallback<PickOrCreateEntryResult> callback = new HandlerCallback<>();

        handlePickOrCreateEntry(similarCases, similarContacts, similarEventParticipants, externalMessage, callback);

        return callback.futureResult;
    }

    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createContact(
        PersonDto person,
        ExternalMessageProcessingResult previousResult) {

        ContactDto contact = buildContact(getExternalMessage(), person);
        HandlerCallback<ContactDto> callback = new HandlerCallback<>();
        handleCreateContact(contact, person, getExternalMessage(), callback);

        return mapHandlerResult(callback, previousResult, previousResult::withCreatedContact);
    }

    protected EventParticipantDto buildEventParticipant(EventDto eventDto, PersonDto person) {

        EventParticipantDto eventParticipant = EventParticipantDto.build(eventDto.toReference(), getUser().toReference());
        eventParticipant.setPerson(person);
        return eventParticipant;
    }

    protected CompletionStage<ProcessingResult<PickOrCreateEventResult>> pickOrCreateEvent() {

        HandlerCallback<PickOrCreateEventResult> callback = new HandlerCallback<>();
        handlePickOrCreateEvent(getExternalMessage(), callback);

        return callback.futureResult;
    }

    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createEvent(ExternalMessageProcessingResult previousResult) {

        EventDto event = EventDto.build(getExternalMessageProcessingFacade().getServerCountry(), getUser(), externalMessage.getDisease());
        event.setDiseaseVariant(externalMessage.getDiseaseVariant());
        event.setDiseaseVariantDetails(externalMessage.getDiseaseVariantDetails());

        HandlerCallback<EventDto> callback = new HandlerCallback<>();
        handleCreateEvent(event, callback);

        return mapHandlerResult(callback, previousResult, previousResult::withCreatedEvent);
    }

    protected ContactDto buildContact(ExternalMessageDto externalMessageDto, PersonDto person) {

        ContactDto contactDto = ContactDto.build(null, externalMessageDto.getDisease(), null, null);
        contactDto.setReportingUser(getUser().toReference());
        contactDto.setReportDateTime(externalMessageDto.getMessageDateTime());
        contactDto.setPerson(person.toReference());
        return contactDto;
    }

    protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createEventParticipant(

        EventDto event,
        PersonDto person,
        ExternalMessageProcessingResult previousResult) {

        EventParticipantDto eventParticipant = buildEventParticipant(event, person);
        HandlerCallback<EventParticipantDto> callback = new HandlerCallback<>();
        handleCreateEventParticipant(eventParticipant, event, externalMessage, callback);

        return mapHandlerResult(callback, previousResult, previousResult::withCreatedEventParticipant);
    }

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

    protected SurveillanceReportDto createSurveillanceReport(ExternalMessageDto externalMessage, CaseDataDto caze) {

        SurveillanceReportDto surveillanceReport = SurveillanceReportDto.build(caze.toReference(), getUser().toReference());
        setSurvReportFacility(surveillanceReport, externalMessage, caze);
        surveillanceReport.setReportDate(externalMessage.getMessageDateTime());
        surveillanceReport.setExternalId(externalMessage.getReportMessageId());
        setSurvReportingType(surveillanceReport, externalMessage);
        return surveillanceReport;
    }

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

    protected abstract CompletionStage<Boolean> confirmPickExistingEventParticipant();

    protected abstract CompletionStage<Void> notifyCorrectionsSaved();

    protected abstract void handlePickOrCreateEntry(
        List<CaseSelectionDto> similarCases,
        List<SimilarContactDto> similarContacts,
        List<SimilarEventParticipantDto> similarEventParticipants,
        ExternalMessageDto externalMessageDto,
        HandlerCallback<PickOrCreateEntryResult> callback);

    protected abstract void handleCreateCase(
        CaseDataDto caze,
        PersonDto person,
        ExternalMessageDto labMessage,
        HandlerCallback<CaseDataDto> callback);

    protected abstract void handleCreateContact(
        ContactDto contact,
        PersonDto person,
        ExternalMessageDto externalMessage,
        HandlerCallback<ContactDto> callback);

    protected abstract void handlePickOrCreateEvent(ExternalMessageDto externalMessage, HandlerCallback<PickOrCreateEventResult> callback);

    protected abstract void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback);

    protected abstract void handleCreateEventParticipant(
        EventParticipantDto eventParticipant,
        EventDto event,
        ExternalMessageDto externalMessage,
        HandlerCallback<EventParticipantDto> callback);

    protected abstract void markExternalMessageAsProcessed(
        ExternalMessageDto externalMessage,
        ProcessingResult<ExternalMessageProcessingResult> result,
        SurveillanceReportDto surveillanceReport);

    public ExternalMessageDto getExternalMessage() {
        return externalMessage;
    }

}
