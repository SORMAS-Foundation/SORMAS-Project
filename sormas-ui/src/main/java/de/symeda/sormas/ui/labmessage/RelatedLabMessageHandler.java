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

package de.symeda.sormas.ui.labmessage;

import static de.symeda.sormas.api.utils.DataHelper.Pair.createPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.utils.DataHelper;

public class RelatedLabMessageHandler {

	public interface CorrectedEntityHandler<T> {

		void handle(LabMessageDto labMessage, T original, T updated, List<String[]> changedFields, RelatedLabMessageHandlerChain chain);
	}
	public interface CratePathogenTestHandler {

		void handle(LabMessageDto labMessage, TestReportDto testReportDto, SampleDto sample, RelatedLabMessageHandlerChain chain);
	}

	public interface ShortcutHandler {

		void handle(LabMessageDto labMessage, SampleDto sample, RelatedLabMessageHandlerChain chain);
	}

	public enum HandlerResult {
		NOT_HANDLED,
		HANDLED,
		CANCELED,
		CANCELED_WITH_UPDATES
	}

	public interface RelatedLabMessageHandlerChain {

		void next(boolean savePerformed);

		void cancel();

		boolean done();
	}

	private final Supplier<CompletionStage<Boolean>> correctionFlowConfirmation;
	private final Function<Boolean, CompletionStage<Boolean>> shortcutFlowConfirmation;
	private final CorrectedEntityHandler<PersonDto> personHandler;
	private final CorrectedEntityHandler<SampleDto> sampleHandler;
	private final CorrectedEntityHandler<PathogenTestDto> pathogenTestHandler;
	private final CratePathogenTestHandler cratePathogenTestHandler;
	private final Function<LabMessageDto, CompletionStage<Boolean>> continueProcessingConfirmation;
	private final ShortcutHandler shortcutHandler;

	public RelatedLabMessageHandler(
		Supplier<CompletionStage<Boolean>> correctionFlowConfirmation,
		Function<Boolean, CompletionStage<Boolean>> shortcutFlowConfirmation,
		CorrectedEntityHandler<PersonDto> personHandler,
		CorrectedEntityHandler<SampleDto> sampleHandler,
		CorrectedEntityHandler<PathogenTestDto> pathogenTestHandler,
		CratePathogenTestHandler cratePathogenTestHandler,
		Function<LabMessageDto, CompletionStage<Boolean>> continueProcessingConfirmation,
		ShortcutHandler shortcutHandler) {
		this.correctionFlowConfirmation = correctionFlowConfirmation;
		this.shortcutFlowConfirmation = shortcutFlowConfirmation;
		this.continueProcessingConfirmation = continueProcessingConfirmation;
		this.personHandler = personHandler;
		this.sampleHandler = sampleHandler;
		this.pathogenTestHandler = pathogenTestHandler;
		this.cratePathogenTestHandler = cratePathogenTestHandler;
		this.shortcutHandler = shortcutHandler;
	}

	public CompletionStage<HandlerResult> handle(LabMessageDto labMessage) {
		RelatedEntities relatedEntities = getRelatedEntities(labMessage);

		if (relatedEntities == null) {
			return CompletableFuture.completedFuture(HandlerResult.NOT_HANDLED);
		}

		LabMessageMapper mapper = LabMessageMapper.forLabMessage(labMessage);

		ChainHandler chainHandler = new ChainHandler();
		final Mutable<CompletionStage<Boolean>> confirmedCorrectionFlow = new MutableObject<>();
		Supplier<CompletionStage<Boolean>> correctionFlowConfirmationSupplier = () -> {
			if (confirmedCorrectionFlow.getValue() == null) {
				confirmedCorrectionFlow.setValue(correctionFlowConfirmation.get());
			}

			return confirmedCorrectionFlow.getValue();
		};

		CompletionStage<HandlerResult> correctionFlow = CompletableFuture.completedFuture(HandlerResult.NOT_HANDLED);
		// do correction only if there are related lab messages already processed and no unmatched pathogen tests were found
		if (relatedEntities.relatedLabMessagesFound && relatedEntities.unmatchedPathogenTests.isEmpty()) {
			correctionFlow = correctionFlow
				.thenCompose(
					result -> handlePersonCorrection(
						labMessage,
						relatedEntities.person,
						mapper,
						correctionFlowConfirmationSupplier,
						result,
						chainHandler))
				.thenCompose(
					(personCorrectionResult) -> handleSampleCorrection(
						labMessage,
						relatedEntities.sample,
						mapper,
						correctionFlowConfirmationSupplier,
						personCorrectionResult,
						chainHandler));

			for (PathogenTestDto p : relatedEntities.pathogenTests) {
				Optional<TestReportDto> testReport = labMessage.getTestReports().stream().filter(t -> matchPathogenTest(t, p)).findFirst();
				if (testReport.isPresent()) {
					correctionFlow = correctionFlow.thenCompose(
						(testCorrectionResult) -> handlePathogenTestCorrection(
							labMessage,
							testReport.get(),
							p,
							mapper,
							correctionFlowConfirmationSupplier,
							testCorrectionResult,
							chainHandler));
				}
			}

			for (TestReportDto r : relatedEntities.unmatchedTestReports) {
				correctionFlow = correctionFlow.thenCompose(
					(result) -> handlePathogenTestCreation(labMessage, r, relatedEntities.sample, correctionFlowConfirmationSupplier, chainHandler));
			}
		}

		return correctionFlow.thenCompose((result) -> {
			if (result == HandlerResult.HANDLED) {
				// ask to continue post processing
				return continueProcessingConfirmation.apply(labMessage)
					.thenCompose((doPostProcess) -> CompletableFuture.completedFuture(CorrectionResult.of(result, doPostProcess)));
			}

			// if not corrections found, then continue
			return CompletableFuture.completedFuture(CorrectionResult.of(result, true));
		})
			// check for shortcut
			.thenCompose((correctionResult) -> {
				if (correctionResult.shouldContinue) {
					return shortcutFlowConfirmation.apply(relatedEntities.relatedLabMessagesFound).thenCompose((confirmed) -> {
						if (confirmed) {
							return chainHandler.run((chain) -> shortcutHandler.handle(labMessage, relatedEntities.sample, chain))
								.thenCompose((handled) -> CompletableFuture.completedFuture(HandlerResult.HANDLED));
						}

						return CompletableFuture.completedFuture(correctionResult.result);
					});
				}

				return CompletableFuture.completedFuture(correctionResult.result);
			})
			.exceptionally(e -> {
				if (e.getCause() instanceof CancellationException) {
					return chainHandler.savePerformed ? HandlerResult.CANCELED_WITH_UPDATES : HandlerResult.CANCELED;
				}

				throw (RuntimeException) e;
			});
	}

	// correction
	private CompletionStage<HandlerResult> handlePersonCorrection(
		LabMessageDto labMessage,
		PersonDto person,
		LabMessageMapper mapper,
		Supplier<CompletionStage<Boolean>> confirmationSupplier,
		HandlerResult defaultResult,
		ChainHandler chainHandler) {

		return handleCorrection(
			labMessage,
			person,
			(p) -> Stream.of(mapper.mapToPerson(p).stream(), mapper.mapToLocation(p.getAddress()).stream())
				.flatMap(s -> s)
				.collect(Collectors.toList()),
			confirmationSupplier,
			personHandler,
			defaultResult,
			chainHandler);
	}

	public CompletionStage<HandlerResult> handleSampleCorrection(
		LabMessageDto labMessage,
		SampleDto sample,
		LabMessageMapper mapper,
		Supplier<CompletionStage<Boolean>> confirmationSupplier,
		HandlerResult defaultResult,
		ChainHandler chainHandler) {

		return handleCorrection(labMessage, sample, mapper::mapToSample, confirmationSupplier, sampleHandler, defaultResult, chainHandler);
	}

	public CompletionStage<HandlerResult> handlePathogenTestCorrection(
		LabMessageDto labMessage,
		TestReportDto testReport,
		PathogenTestDto pathogenTest,
		LabMessageMapper mapper,
		Supplier<CompletionStage<Boolean>> confirmationSupplier,
		HandlerResult defaultResult,
		ChainHandler chainHandler) {

		return handleCorrection(
			labMessage,
			pathogenTest,
			(t) -> mapper.mapToPathogenTest(testReport, t),
			confirmationSupplier,
			pathogenTestHandler,
			defaultResult,
			chainHandler);
	}

	private <T extends EntityDto> CompletionStage<HandlerResult> handleCorrection(
		LabMessageDto labMessage,
		T entity,
		Function<T, List<String[]>> mapper,
		Supplier<CompletionStage<Boolean>> confirmationSupplier,
		CorrectedEntityHandler<T> correctionHandler,
		HandlerResult defaultResult,
		ChainHandler chainHandler) {
		T updatedEntity;
		try {
			updatedEntity = (T) entity.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		List<String[]> changedFields = mapper.apply(updatedEntity);

		if (changedFields.isEmpty()) {
			return CompletableFuture.completedFuture(defaultResult);
		}

		return confirmationSupplier.get().thenCompose((confirmed) -> {
			if (confirmed) {
				return chainHandler.run((chain) -> correctionHandler.handle(labMessage, entity, updatedEntity, changedFields, chain));
			}

			return CompletableFuture.completedFuture(defaultResult);
		});
	}

	private CompletionStage<HandlerResult> handlePathogenTestCreation(
		LabMessageDto labMessage,
		TestReportDto testReport,
		SampleDto sample,
		Supplier<CompletionStage<Boolean>> confirmationSupplier,
		ChainHandler chainHandler) {

		return confirmationSupplier.get().thenCompose((confirmed) -> {
			if (confirmed) {
				return chainHandler.run((chain) -> cratePathogenTestHandler.handle(labMessage, testReport, sample, chain));
			}

			return CompletableFuture.completedFuture(HandlerResult.NOT_HANDLED);
		});
	}

	// related entities
	public RelatedEntities getRelatedEntities(LabMessageDto labMessage) {
		String reportId = labMessage.getReportId();
		String labSampleId = labMessage.getLabSampleId();

		if (StringUtils.isBlank(reportId) || StringUtils.isBlank(labSampleId)) {
			return null;
		}

		List<SampleDto> relatedSamples = FacadeProvider.getSampleFacade().getByLabSampleId(labSampleId);
		if (relatedSamples.size() != 1) {
			return null;
		}

		SampleDto relatedSample = relatedSamples.get(0);
		List<LabMessageDto> relatedLabMessages = FacadeProvider.getLabMessageFacade()
			.getForSample(relatedSample.toReference())
			.stream()
			.filter(
				otherLabMessage -> reportId.equals(otherLabMessage.getReportId()) && LabMessageStatus.PROCESSED.equals(otherLabMessage.getStatus()))
			.collect(Collectors.toList());

		PersonDto relatedPerson;
		if (relatedSample.getAssociatedCase() != null) {
			relatedPerson = FacadeProvider.getPersonFacade().getByContext(PersonContext.CASE, relatedSample.getAssociatedCase().getUuid());
		} else if (relatedSample.getAssociatedContact() != null) {
			relatedPerson = FacadeProvider.getPersonFacade().getByContext(PersonContext.CONTACT, relatedSample.getAssociatedContact().getUuid());
		} else {
			relatedPerson = FacadeProvider.getPersonFacade()
				.getByContext(PersonContext.EVENT_PARTICIPANT, relatedSample.getAssociatedEventParticipant().getUuid());
		}

		List<PathogenTestDto> relatedPathogenTests = new ArrayList<>();
		List<TestReportDto> unmatchedTestReports = new ArrayList<>();
		List<PathogenTestDto> unmatchedPathogenTests = new ArrayList<>();

		List<TestReportDto> testReports = labMessage.getTestReports();
		List<PathogenTestDto> samplePathogenTests = FacadeProvider.getPathogenTestFacade().getAllBySample(relatedSample.toReference());

		List<DataHelper.Pair<TestReportDto, List<PathogenTestDto>>> testReportPathogenTestPairs = testReports.stream()
			.map(tr -> createPair(tr, samplePathogenTests.stream().filter(pt -> matchPathogenTest(tr, pt)).collect(Collectors.toList())))
			.collect(Collectors.toList());

		for (DataHelper.Pair<TestReportDto, List<PathogenTestDto>> p : testReportPathogenTestPairs) {
			TestReportDto testReport = p.getElement0();
			List<PathogenTestDto> pathogenTests = p.getElement1();

			if (pathogenTests.size() == 1) {
				relatedPathogenTests.add(pathogenTests.get(0));
			} else if (pathogenTests.isEmpty()) {
				unmatchedTestReports.add(testReport);
			} else {
				unmatchedPathogenTests.addAll(pathogenTests);
			}
		}

		return new RelatedEntities(
			relatedSample,
			relatedPerson,
			relatedPathogenTests,
			unmatchedTestReports,
			unmatchedPathogenTests,
			relatedLabMessages.size() > 0);
	}

	private boolean matchPathogenTest(TestReportDto tr, PathogenTestDto pt) {
		return DataHelper.equal(tr.getExternalId(), pt.getExternalId());
	}

	public static class RelatedEntities {

		private final SampleDto sample;

		private final PersonDto person;

		private final List<PathogenTestDto> pathogenTests;

		private final List<TestReportDto> unmatchedTestReports;

		private final boolean relatedLabMessagesFound;

		private final List<PathogenTestDto> unmatchedPathogenTests;

		public RelatedEntities(
			SampleDto sample,
			PersonDto person,
			List<PathogenTestDto> pathogenTests,
			List<TestReportDto> unmatchedTestReports,
			List<PathogenTestDto> unmatchedPathogenTests,
			boolean relatedLabMessagesFound) {
			this.sample = sample;
			this.person = person;
			this.pathogenTests = pathogenTests;
			this.unmatchedTestReports = unmatchedTestReports;
			this.unmatchedPathogenTests = unmatchedPathogenTests;
			this.relatedLabMessagesFound = relatedLabMessagesFound;
		}

		public SampleDto getSample() {
			return sample;
		}

		public PersonDto getPerson() {
			return person;
		}

		public List<PathogenTestDto> getPathogenTests() {
			return pathogenTests;
		}

		public List<TestReportDto> getUnmatchedTestReports() {
			return unmatchedTestReports;
		}

		public List<PathogenTestDto> getUnmatchedPathogenTests() {
			return unmatchedPathogenTests;
		}

		public boolean isRelatedLabMessagesFound() {
			return relatedLabMessagesFound;
		}
	}

	private static class ChainHandler {

		private boolean savePerformed = false;

		private CompletableFuture<HandlerResult> run(Consumer<RelatedLabMessageHandlerChain> action) {
			CompletableFuture<HandlerResult> future = new CompletableFuture<>();

			action.accept(new RelatedLabMessageHandlerChain() {

				@Override
				public void next(boolean saved) {
					savePerformed = saved;
					future.complete(HandlerResult.HANDLED);
				}

				@Override
				public void cancel() {
					future.cancel(true);
				}

				@Override
				public boolean done() {
					return future.isDone();
				}
			});

			return future;
		}
	}

	private static class CorrectionResult {

		private HandlerResult result;
		private boolean shouldContinue;

		static CorrectionResult of(HandlerResult result, boolean shouldContinue) {
			CorrectionResult correctionResult = new CorrectionResult();

			correctionResult.result = result;
			correctionResult.shouldContinue = shouldContinue;

			return correctionResult;
		}
	}
}
