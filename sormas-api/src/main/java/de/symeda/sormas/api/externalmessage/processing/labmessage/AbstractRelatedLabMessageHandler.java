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

package de.symeda.sormas.api.externalmessage.processing.labmessage;

import java.util.ArrayList;
import java.util.Collections;
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

/**
 * Abstract class for having a clear flow for handling lab messages the contain only corrections to prevoius ones
 * e.g. Changing some person sample or pathogen test details
 *
 * The flow is coded in the `handle` method.
 *
 */
public abstract class AbstractRelatedLabMessageHandler {

	public static class HandlerResult {

		private final HandlerResultStatus status;
		private final SampleDto sample;

		public HandlerResult(HandlerResultStatus status, SampleDto sample) {
			this.status = status;
			this.sample = sample;
		}

		public HandlerResultStatus getStatus() {
			return status;
		}

		public SampleDto getSample() {
			return sample;
		}
	}

	public enum HandlerResultStatus {
		NOT_HANDLED,
		HANDLED,
		CANCELED,
		CANCELED_WITH_UPDATES,
		CONTINUE
	}

	public AbstractRelatedLabMessageHandler() {
	}

	public CompletionStage<HandlerResult> handle(ExternalMessageDto labMessage) {
		RelatedEntities relatedEntities = getRelatedEntities(labMessage);

		if (relatedEntities == null) {
			return CompletableFuture.completedFuture(new HandlerResult(HandlerResultStatus.NOT_HANDLED, null));
		}

		ExternalMessageMapper mapper = ExternalMessageMapper.forLabMessage(labMessage);

		ChainHandler chainHandler = new ChainHandler();
		Supplier<CompletionStage<Boolean>> correctionFlowConfirmationSupplier = createCachedCorrectionFlowConfirmationSupplier();

		CompletionStage<HandlerResultStatus> correctionFlow = CompletableFuture.completedFuture(HandlerResultStatus.NOT_HANDLED);

		if (relatedEntities.relatedLabMessagesFound && !relatedEntities.pathogenTestMisMatch) {
			correctionFlow = correctionFlow.thenCompose(
				result -> doPersonCorrection(labMessage, relatedEntities.person, mapper, correctionFlowConfirmationSupplier, result, chainHandler))
				.thenCompose(
					personCorrectionResult -> doSampleCorrection(
						labMessage,
						relatedEntities.sample,
						mapper,
						correctionFlowConfirmationSupplier,
						personCorrectionResult,
						chainHandler));

			for (PathogenTestDto p : relatedEntities.pathogenTests) {
				Optional<TestReportDto> testReport =
					labMessage.getSampleReportsNullSafe().get(0).getTestReports().stream().filter(t -> matchPathogenTest(t, p)).findFirst();
				if (testReport.isPresent()) {
					correctionFlow = correctionFlow.thenCompose(
						testCorrectionResult -> doPathogenTestCorrection(
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
				correctionFlow = correctionFlow.thenCompose(result -> {
					if (result == HandlerResultStatus.HANDLED) {
						// do not handle pathogen test creation if there were no corrections in the lab message
						return createPathogenTest(labMessage, r, relatedEntities.sample, correctionFlowConfirmationSupplier, chainHandler);
					}

					return CompletableFuture.completedFuture(result);
				});
			}
		}

		return correctionFlow.thenCompose(result -> {
			if (result == HandlerResultStatus.HANDLED) {
				// ask to continue post processing
				return confirmContinueProcessing(labMessage, relatedEntities.sample.toReference())
					.thenCompose(doPostProcess -> CompletableFuture.completedFuture(CorrectionResult.of(result, doPostProcess)));
			}

			// if no corrections found, then continue
			return CompletableFuture.completedFuture(CorrectionResult.of(result, true));
		})
			// check for shortcut
			.thenCompose(correctionResult -> {
				if (correctionResult.shouldContinue) {
					return confirmShortcut(relatedEntities.relatedLabMessagesFound).thenCompose(confirmed -> {
						if (Boolean.TRUE.equals(confirmed)) {
							return chainHandler.run(chain -> handleShortcut(labMessage, relatedEntities.sample, chain))
								.thenCompose(
									handled -> CompletableFuture
										.completedFuture(new HandlerResult(HandlerResultStatus.HANDLED, relatedEntities.sample)));
						}

						return CompletableFuture.completedFuture(new HandlerResult(HandlerResultStatus.CONTINUE, relatedEntities.sample));
					});
				}

				return CompletableFuture.completedFuture(new HandlerResult(correctionResult.result, relatedEntities.sample));
			})
			.exceptionally(e -> {
				if (e.getCause() instanceof CancellationException) {
					return new HandlerResult(
						chainHandler.savePerformed ? HandlerResultStatus.CANCELED_WITH_UPDATES : HandlerResultStatus.CANCELED,
						null);
				}

				throw (RuntimeException) e;
			});
	}

	protected abstract CompletionStage<Boolean> confirmShortcut(boolean hasRelatedLabMessages);

	protected abstract CompletionStage<Boolean> confirmContinueProcessing(ExternalMessageDto labMessage, SampleReferenceDto sample);

	protected abstract void handleShortcut(ExternalMessageDto labMessage, SampleDto sample, RelatedLabMessageHandlerChain chain);

	/**
	 * Used for confirming the correction flow on the first changed entity and reuse the confirmation status on the other entities
	 * 
	 * @return Supplier that calls `correctionFlowConfirmation` only once, subsequent calls will return the result of confirmation
	 */
	private Supplier<CompletionStage<Boolean>> createCachedCorrectionFlowConfirmationSupplier() {
		final Mutable<CompletionStage<Boolean>> confirmedCorrectionFlow = new MutableObject<>();

		return () -> {
			if (confirmedCorrectionFlow.getValue() == null) {
				confirmedCorrectionFlow.setValue(confirmCorrectionFlow());
			}

			return confirmedCorrectionFlow.getValue();
		};
	}

	protected abstract CompletionStage<Boolean> confirmCorrectionFlow();

	// correction
	private CompletionStage<HandlerResultStatus> doPersonCorrection(
		ExternalMessageDto labMessage,
		PersonDto person,
		ExternalMessageMapper mapper,
		Supplier<CompletionStage<Boolean>> confirmationSupplier,
		HandlerResultStatus defaultResult,
		ChainHandler chainHandler) {

		return handleCorrection(
			labMessage,
			person,
			p -> Stream.of(mapper.mapToPerson(p).stream(), mapper.mapToLocation(p.getAddress()).stream())
				.flatMap(s -> s)
				.collect(Collectors.toList()),
			confirmationSupplier,
			this::handlePersonCorrection,
			defaultResult,
			chainHandler);
	}

	protected abstract void handlePersonCorrection(
		ExternalMessageDto labMessage,
		PersonDto person,
		PersonDto updatedPerson,
		List<String[]> changedFields,
		RelatedLabMessageHandlerChain chain);

	public CompletionStage<HandlerResultStatus> doSampleCorrection(
		ExternalMessageDto labMessage,
		SampleDto sample,
		ExternalMessageMapper mapper,
		Supplier<CompletionStage<Boolean>> confirmationSupplier,
		HandlerResultStatus defaultResult,
		ChainHandler chainHandler) {

		// TODO Generify this. It may be possible to handle the correction of multiple samples, but this is not yet thought through
		return handleCorrection(
			labMessage,
			sample,
			mapper::mapFirstSampleReportToSample,
			confirmationSupplier,
			this::handleSampleCorrection,
			defaultResult,
			chainHandler);
	}

	protected abstract void handleSampleCorrection(
		ExternalMessageDto labMessage,
		SampleDto sample,
		SampleDto updatedSample,
		List<String[]> changedFields,
		RelatedLabMessageHandlerChain chain);

	public CompletionStage<HandlerResultStatus> doPathogenTestCorrection(
		ExternalMessageDto labMessage,
		TestReportDto testReport,
		PathogenTestDto pathogenTest,
		ExternalMessageMapper mapper,
		Supplier<CompletionStage<Boolean>> confirmationSupplier,
		HandlerResultStatus defaultResult,
		ChainHandler chainHandler) {

		return handleCorrection(
			labMessage,
			pathogenTest,
			t -> mapper.mapToPathogenTest(testReport, t),
			confirmationSupplier,
			this::handlePathogenTestCorrection,
			defaultResult,
			chainHandler);
	}

	protected abstract void handlePathogenTestCorrection(
		ExternalMessageDto labMessage,
		PathogenTestDto pathogenTest,
		PathogenTestDto updatedPathogenTest,
		List<String[]> changedFields,
		RelatedLabMessageHandlerChain chain);

	private <T extends EntityDto> CompletionStage<HandlerResultStatus> handleCorrection(
		ExternalMessageDto labMessage,
		T entity,
		Function<T, List<String[]>> mapper,
		Supplier<CompletionStage<Boolean>> confirmationSupplier,
		CorrectedEntityHandler<T> correctionHandler,
		HandlerResultStatus defaultResult,
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

		return confirmationSupplier.get().thenCompose(confirmed -> {
			if (Boolean.TRUE.equals(confirmed)) {
				return chainHandler.run(chain -> correctionHandler.handle(labMessage, entity, updatedEntity, changedFields, chain));
			}

			return CompletableFuture.completedFuture(defaultResult);
		});
	}

	private CompletionStage<HandlerResultStatus> createPathogenTest(
		ExternalMessageDto labMessage,
		TestReportDto testReport,
		SampleDto sample,
		Supplier<CompletionStage<Boolean>> confirmationSupplier,
		ChainHandler chainHandler) {

		return confirmationSupplier.get().thenCompose(confirmed -> {
			if (Boolean.TRUE.equals(confirmed)) {
				return chainHandler.run(chain -> handlePathogenTestCreation(labMessage, testReport, sample, chain));
			}

			return CompletableFuture.completedFuture(HandlerResultStatus.NOT_HANDLED);
		});
	}

	protected abstract void handlePathogenTestCreation(
		ExternalMessageDto labMessage,
		TestReportDto testReport,
		SampleDto sample,
		RelatedLabMessageHandlerChain chain);

	// related entities
	public RelatedEntities getRelatedEntities(ExternalMessageDto labMessage) {
		// TODO It may be possible to use related entities for multiple sample reports, but this is not yet thought through
		if (labMessage.getSampleReportsNullSafe().size() > 1) {
			return null;
		}

		String reportId = labMessage.getReportId();
		String labSampleId = labMessage.getSampleReportsNullSafe().get(0).getLabSampleId();

		if (StringUtils.isBlank(reportId) || StringUtils.isBlank(labSampleId))

		{
			return null;
		}

		List<SampleDto> relatedSamples = FacadeProvider.getSampleFacade().getByLabSampleId(labSampleId);
		if (relatedSamples.size() != 1) {
			return null;
		}

		SampleDto relatedSample = relatedSamples.get(0);
		List<ExternalMessageDto> relatedLabMessages = FacadeProvider.getExternalMessageFacade()
			.getForSample(relatedSample.toReference())
			.stream()
			.filter(
				otherLabMessage -> reportId.equals(otherLabMessage.getReportId())
					&& ExternalMessageStatus.PROCESSED.equals(otherLabMessage.getStatus()))
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
		boolean pathogenTestMisMatch = false;

		List<TestReportDto> testReports = labMessage.getSampleReportsNullSafe().get(0).getTestReports();
		List<PathogenTestDto> samplePathogenTests = FacadeProvider.getPathogenTestFacade().getAllBySample(relatedSample.toReference());

		for (TestReportDto testReport : testReports) {
			List<PathogenTestDto> matchedPathogenTests = StringUtils.isBlank(testReport.getExternalId())
				? Collections.emptyList()
				: samplePathogenTests.stream().filter(pt -> matchPathogenTest(testReport, pt)).collect(Collectors.toList());

			if (matchedPathogenTests.isEmpty()) {
				unmatchedTestReports.add(testReport);
			} else if (matchedPathogenTests.size() == 1) {
				relatedPathogenTests.add(matchedPathogenTests.get(0));
			} else {
				unmatchedTestReports.add(testReport);
				pathogenTestMisMatch = true;
			}
		}

		return new RelatedEntities(
			relatedSample,
			relatedPerson,
			relatedPathogenTests,
			unmatchedTestReports,
			pathogenTestMisMatch,
			CollectionUtils.isNotEmpty(relatedLabMessages));
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

		private final boolean pathogenTestMisMatch;

		public RelatedEntities(
			SampleDto sample,
			PersonDto person,
			List<PathogenTestDto> pathogenTests,
			List<TestReportDto> unmatchedTestReports,
			boolean pathogenTestMisMatch,
			boolean relatedLabMessagesFound) {
			this.sample = sample;
			this.person = person;
			this.pathogenTests = pathogenTests;
			this.unmatchedTestReports = unmatchedTestReports;
			this.pathogenTestMisMatch = pathogenTestMisMatch;
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

		public boolean isPathogenTestMisMatch() {
			return pathogenTestMisMatch;
		}

		public boolean isRelatedLabMessagesFound() {
			return relatedLabMessagesFound;
		}
	}

	public interface CorrectedEntityHandler<T> {

		void handle(ExternalMessageDto labMessage, T original, T updated, List<String[]> changedFields, RelatedLabMessageHandlerChain chain);
	}

	public interface RelatedLabMessageHandlerChain {

		void next(boolean savePerformed);

		void cancel();

		boolean done();
	}

	private static class ChainHandler {

		private boolean savePerformed = false;

		private CompletableFuture<HandlerResultStatus> run(Consumer<RelatedLabMessageHandlerChain> action) {
			CompletableFuture<HandlerResultStatus> future = new CompletableFuture<>();

			action.accept(new RelatedLabMessageHandlerChain() {

				@Override
				public void next(boolean saved) {
					savePerformed = saved;
					future.complete(HandlerResultStatus.HANDLED);
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

		private HandlerResultStatus result;
		private boolean shouldContinue;

		static CorrectionResult of(HandlerResultStatus result, boolean shouldContinue) {
			CorrectionResult correctionResult = new CorrectionResult();

			correctionResult.result = result;
			correctionResult.shouldContinue = shouldContinue;

			return correctionResult;
		}
	}
}
