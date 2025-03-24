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

import java.util.Date;
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
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.processing.AbstractMessageProcessingFlowBase;
import de.symeda.sormas.api.externalmessage.processing.EventValidationResult;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEventResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateSampleResult;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;
import de.symeda.sormas.api.utils.dataprocessing.flow.FlowThen;

/**
 * Abstract class defining the flow of processing a lab message allowing to choose between multiple options like create or select a
 * case/contact/event participant and then create or update a sample with pathogen tests
 * The flow is coded in the `run` method.
 */
public abstract class AbstractLabMessageProcessingFlow extends AbstractMessageProcessingFlowBase {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final AbstractRelatedLabMessageHandler relatedLabMessageHandler;
	private final Boolean forceSampleCreation;

	public AbstractLabMessageProcessingFlow(
		ExternalMessageDto externalMessage,
		UserDto user,
		ExternalMessageMapper mapper,
		ExternalMessageProcessingFacade processingFacade,
		AbstractRelatedLabMessageHandler relatedLabMessageHandler,
		Boolean forceSampleCreation) {
		super(user, externalMessage, mapper, processingFacade);
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

	protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> doInitialSetup(
		ProcessingResult<ExternalMessageProcessingResult> previousResult) {
		if (relatedLabMessageHandler == null) {
			return super.doInitialSetup(previousResult);
		}
		return handleRelatedLabMessages(relatedLabMessageHandler, previousResult);
	}

	protected FlowThen<ExternalMessageProcessingResult> doCreateCaseFlow(FlowThen<ExternalMessageProcessingResult> flow) {

		FlowThen<ExternalMessageProcessingResult> caseFlow = super.doCreateCaseFlow(flow);

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

		FlowThen<ExternalMessageProcessingResult> contactFlow = super.doCreateContactFlow(flow);
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

	private FlowThen<ExternalMessageProcessingResult> doPickOrCreateSamplesFlow(
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

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> handleRelatedLabMessages(
		AbstractRelatedLabMessageHandler relatedLabMessageHandler,
		ProcessingResult<ExternalMessageProcessingResult> previousResult) {

		if (relatedLabMessageHandler == null) {
			return previousResult.asCompletedFuture();
		}

		// TODO currently, related messages handling is just done if one sample report exists. That's why this works.
		SampleReportDto firstSampleReport = getExternalMessage().getSampleReportsNullSafe().get(0);
		return relatedLabMessageHandler.handle(getExternalMessage()).thenCompose(result -> {
			AbstractRelatedLabMessageHandler.HandlerResultStatus status = result.getStatus();
			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CANCELED) {
				logger.debug("[MESSAGE PROCESSING] Canceled while handling as a related message.");
				return ProcessingResult.withStatus(ProcessingResultStatus.CANCELED, previousResult.getData()).asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CANCELED_WITH_UPDATES) {
				logger.debug("[MESSAGE PROCESSING] Canceled while handling as a related message. But some updates were made.");
				return ProcessingResult.withStatus(ProcessingResultStatus.CANCELED_WITH_CORRECTIONS, previousResult.getData()).asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.HANDLED) {
				logger.debug("[MESSAGE PROCESSING] Processing done as a related message to another one.");
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
									getExternalMessageProcessingFacade().getPathogenTestsBySample(relatedSample.toReference()),
									firstSampleReport,
									false)))
					.asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CONTINUE) {
				logger.debug("[MESSAGE PROCESSING] Processing done as a related message to another one and continue with the normal flow.");
				return ProcessingResult.continueWith(previousResult.getData()).asCompletedFuture();
			}

			return ProcessingResult.continueWith(previousResult.getData()).asCompletedFuture();
		});
	}

	private ExternalMessageProcessingResult setPersonAssociationsOnResult(SampleDto sample, ExternalMessageProcessingResult result) {
		if (sample.getAssociatedCase() != null) {
			return result.withSelectedCase(getExternalMessageProcessingFacade().getCaseDataByUuid(sample.getAssociatedCase().getUuid()));
		} else if (sample.getAssociatedContact() != null) {
			return result.withSelectedContact(getExternalMessageProcessingFacade().getContactByUuid(sample.getAssociatedContact().getUuid()));
		} else if (sample.getAssociatedEventParticipant() != null) {
			return result.withSelectedEventParticipant(
				getExternalMessageProcessingFacade().getEventParticipantByUuid(sample.getAssociatedEventParticipant().getUuid()));
		}

		return result;
	}

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> createOneSampleAndPathogenTests(
		CaseDataDto caze,
		int sampleReportIndex,
		boolean entityCreated,
		ExternalMessageProcessingResult previousResult) {

		SampleDto sample = SampleDto.build(getUser().toReference(), caze.toReference());
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

	protected abstract void handlePickOrCreateSample(
		List<SampleDto> similarSamples,
		List<SampleDto> otherSamples,
		ExternalMessageDto labMessage,
		int sampleReportIndex,
		HandlerCallback<PickOrCreateSampleResult> callback);

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

	protected abstract void handleEditSample(
		SampleDto sample,
		List<PathogenTestDto> newPathogenTests,
		ExternalMessageDto labMessage,
		ExternalMessageMapper mapper,
		boolean lastSample,
		HandlerCallback<SampleAndPathogenTests> callback);

	protected void markExternalMessageAsProcessed(
		ExternalMessageDto externalMessage,
		ProcessingResult<ExternalMessageProcessingResult> result,
		SurveillanceReportDto surveillanceReport) {

		List<ExternalMessageProcessingResult.SampleSelection> relatedSampleReports = result.getData().getSamples();
		if (relatedSampleReports != null && !relatedSampleReports.isEmpty()) {
			relatedSampleReports.forEach(e -> e.getSampleReport().setSample(e.getEntity().toReference()));
		}
		if (surveillanceReport != null) {
			externalMessage.setSurveillanceReport(surveillanceReport.toReference());
		}
		externalMessage.setStatus(ExternalMessageStatus.PROCESSED);
		externalMessage.setChangeDate(new Date());
		getExternalMessageProcessingFacade().saveExternalMessage(externalMessage);
	}

}
