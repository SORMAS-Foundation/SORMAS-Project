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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class CorrectionLabMessageHandler {

	public static class CorrectionHandlerChain {

		private final Runnable onNext;
		private final Runnable onCancel;

		public CorrectionHandlerChain(Runnable onNext, Runnable onCancel) {
			this.onNext = onNext;
			this.onCancel = onCancel;
		}

		public void next() {
			onNext.run();
		}

		public void cancel() {
			onCancel.run();
		}
	}
	public interface CorrectedEntityHandler<T> {

		void handle(T original, T updated, List<String[]> changedFields, CorrectionHandlerChain chain);
	}

	public enum CorrectionResult {
		NO_RELATIONS_FOUND,
		NO_CORRECTIONS,
		HANDLED
	}

	public CompletionStage<CorrectionResult> handle(
		LabMessageDto labMessage,
		LabMessageMapper mapper,
		CorrectedEntityHandler<PersonDto> personHandler,
		CorrectedEntityHandler<SampleDto> sampleHandler,
		CorrectedEntityHandler<PathogenTestDto> pathogenTestHandler) {
		RelatedEntities relatedEntities = getRelatedEntities(labMessage);

		if (relatedEntities == null) {
			return CompletableFuture.completedFuture(CorrectionResult.NO_RELATIONS_FOUND);
		}

		CompletionStage<CorrectionResult> chain =
			handlePersonCorrection(relatedEntities.person, mapper, personHandler, CorrectionResult.NO_CORRECTIONS)
				.thenCompose((personCorrection) -> handleSampleCorrection(relatedEntities.sample, mapper, sampleHandler, personCorrection));

		if (relatedEntities.pathogenTests.size() > 0) {
			for (PathogenTestDto p : relatedEntities.pathogenTests) {
				chain =
					chain.thenCompose((testCorrectionResult) -> handlePathogenTestCorrection(p, mapper, pathogenTestHandler, testCorrectionResult));
			}
		}

		return chain;
	}

	// correction
	private CompletionStage<CorrectionResult> handlePersonCorrection(
		PersonDto person,
		LabMessageMapper mapper,
		CorrectedEntityHandler<PersonDto> personHandler,
		CorrectionResult defaultResult) {

		return handleCorrection(
			person,
			(p) -> Stream.of(mapper.mapToPerson(p).stream(), mapper.mapToLocation(p.getAddress()).stream())
				.flatMap(s -> s)
				.collect(Collectors.toList()),
			personHandler,
			defaultResult);
	}

	public CompletionStage<CorrectionResult> handleSampleCorrection(
		SampleDto sample,
		LabMessageMapper mapper,
		CorrectedEntityHandler<SampleDto> sampleHandler,
		CorrectionResult defaultResult) {

		return handleCorrection(sample, mapper::mapToSample, sampleHandler, defaultResult);
	}

	public CompletionStage<CorrectionResult> handlePathogenTestCorrection(
		PathogenTestDto pathogenTest,
		LabMessageMapper mapper,
		CorrectedEntityHandler<PathogenTestDto> pathogenTestHandler,
		CorrectionResult defaultResult) {

		return handleCorrection(pathogenTest, mapper::mapToPathogenTest, pathogenTestHandler, defaultResult);
	}

	private <T extends EntityDto> CompletionStage<CorrectionResult> handleCorrection(
		T entity,
		Function<T, List<String[]>> mapper,
		CorrectedEntityHandler<T> correctionHandler,
		CorrectionResult defaultResult) {
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

		CompletableFuture<CorrectionResult> future = new CompletableFuture<>();
		correctionHandler.handle(entity, updatedEntity, changedFields, new CorrectionHandlerChain(() -> {
			future.complete(CorrectionResult.HANDLED);
		}, () -> future.cancel(true)));

		return future;
	}

	// related entities
	public RelatedEntities getRelatedEntities(LabMessageDto labMessage) {
		String reportId = labMessage.getReportId();
		String labSampleId = labMessage.getLabSampleId();

		if (StringUtils.isBlank(reportId) || StringUtils.isBlank(labSampleId)) {
			return null;
		}

		List<LabMessageDto> relatedLabMessages = FacadeProvider.getLabMessageFacade()
			.getByReportId(reportId)
			.stream()
			.filter(lm -> !DataHelper.isSame(lm, labMessage) && LabMessageStatus.PROCESSED.equals(lm.getStatus()))
			.collect(Collectors.toList());
		List<String> sampleUuids = relatedLabMessages.stream()
			.map(LabMessageDto::getSample)
			.filter(Objects::nonNull)
			.map(SampleReferenceDto::getUuid)
			.collect(Collectors.toList());
		List<SampleDto> relatedSamples = sampleUuids.isEmpty()
			? Collections.emptyList()
			: FacadeProvider.getSampleFacade()
				.getByUuids(sampleUuids)
				.stream()
				.filter(s -> labSampleId.equals(s.getLabSampleID()))
				.collect(Collectors.toList());

		if (relatedSamples.size() != 1) {
			return null;
		}

		SampleDto relatedSample = relatedSamples.get(0);

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

		List<TestReportDto> testReports = labMessage.getTestReports();
		List<PathogenTestDto> samplePathogenTests = FacadeProvider.getPathogenTestFacade().getAllBySample(relatedSample.toReference());

		List<DataHelper.Pair<TestReportDto, List<PathogenTestDto>>> testReportPathogenTestPairs = testReports.stream()
			.map(
				tr -> DataHelper.Pair.createPair(
					tr,
					samplePathogenTests.stream().filter(pt -> DataHelper.equal(tr.getExternalId(), pt.getExternalId())).collect(Collectors.toList())))
			.collect(Collectors.toList());

		for (DataHelper.Pair<TestReportDto, List<PathogenTestDto>> p : testReportPathogenTestPairs) {
			TestReportDto testReport = p.getElement0();
			List<PathogenTestDto> pathogenTests = p.getElement1();

			if (pathogenTests.size() == 1) {
				relatedPathogenTests.add(pathogenTests.get(0));
			} else if (pathogenTests.isEmpty()) {
				unmatchedTestReports.add(testReport);
			} else {
				// test report with multiple pathogen test related could not be considered as a correction one
				return null;
			}
		}

		return new RelatedEntities(relatedSample, relatedPerson, relatedPathogenTests, unmatchedTestReports);
	}

	public static class RelatedEntities {

		private final SampleDto sample;

		private final PersonDto person;

		private final List<PathogenTestDto> pathogenTests;

		private final List<TestReportDto> unmatchedTestReports;

		public RelatedEntities(SampleDto sample, PersonDto person, List<PathogenTestDto> pathogenTests, List<TestReportDto> unmatchedTestReports) {
			this.sample = sample;
			this.person = person;
			this.pathogenTests = pathogenTests;
			this.unmatchedTestReports = unmatchedTestReports;
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
	}
}
