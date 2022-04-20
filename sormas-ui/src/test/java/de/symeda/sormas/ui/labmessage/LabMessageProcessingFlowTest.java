/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.labmessage;

import static de.symeda.sormas.ui.labmessage.LabMessageProcessingFlow.ResultStatus.CANCELED;
import static de.symeda.sormas.ui.labmessage.LabMessageProcessingFlow.ResultStatus.CANCELED_WITH_CORRECTIONS;
import static de.symeda.sormas.ui.labmessage.LabMessageProcessingFlow.ResultStatus.DONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.symeda.sormas.api.sample.SampleMaterial;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.labmessage.SimilarEntriesDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.labmessage.LabMessageProcessingFlow.ResultStatus;

public class LabMessageProcessingFlowTest extends AbstractBeanTest {

	private LabMessageProcessingFlow flow;

	private Supplier<CompletionStage<Boolean>> missingDiseaseHandler;
	private Supplier<CompletionStage<Boolean>> relatedForwardedMessagesHandler;
	private RelatedLabMessageHandler relatedLabMessageHandler;
	private Supplier<CompletionStage<PersonDto>> handlePickOrCreatePerson;
	private PickOrCreateEntryHandler handlePickOrCreateEntry;
	private BiFunction<CaseDataDto, PersonDto, CompletionStage<CaseDataDto>> handleCreateCase;
	private Function<SampleDto, CompletionStage<SampleDto>> handleCreateSample;
	private UserReferenceDto userRef;

	@Override
	public void init() {
		super.init();

		missingDiseaseHandler = Mockito.mock(Supplier.class);
		when(missingDiseaseHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		relatedForwardedMessagesHandler = Mockito.mock(Supplier.class);
		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		relatedLabMessageHandler = Mockito.mock(RelatedLabMessageHandler.class);
		when(relatedLabMessageHandler.handle(any()))
			.thenReturn(CompletableFuture.completedFuture(RelatedLabMessageHandler.HandlerResult.NOT_HANDLED));

		handlePickOrCreatePerson = Mockito.mock(Supplier.class);
		when(handlePickOrCreatePerson.get()).thenReturn(CompletableFuture.completedFuture(PersonDto.build()));

		handlePickOrCreateEntry = Mockito.mock(PickOrCreateEntryHandler.class);
		SimilarEntriesDto similarEntriesDto = new SimilarEntriesDto();
		similarEntriesDto.setNewCase(true);
		when(handlePickOrCreateEntry.handle(any(), any(), any())).thenReturn(CompletableFuture.completedFuture(similarEntriesDto));

		handleCreateCase = Mockito.mock(BiFunction.class);
		when(handleCreateCase.apply(any(), any())).then((invocation) -> CompletableFuture.completedFuture(invocation.getArgument(0)));

		handleCreateSample = Mockito.mock(Function.class);
		when(handleCreateSample.apply(any())).then((invocation) -> CompletableFuture.completedFuture(invocation.getArgument(0)));

		userRef = new UserReferenceDto(DataHelper.createUuid());
		flow = new LabMessageProcessingFlow(userRef) {

			@Override
			CompletionStage<Boolean> handleMissingDisease() {
				return missingDiseaseHandler.get();
			}

			@Override
			protected CompletionStage<Boolean> handleRelatedForwardedMessages() {
				return relatedForwardedMessagesHandler.get();
			}

			@Override
			protected CompletionStage<PersonDto> handlePickOrCreatePerson(LabMessageDto labMessage) {
				return handlePickOrCreatePerson.get();
			}

			@Override
			protected CompletionStage<SimilarEntriesDto> handlePickOrCreateEntry(
				LabMessageDto labMessageDto,
				List<CaseSelectionDto> similarCases,
				List<SimilarContactDto> similarContacts,
				List<SimilarEventParticipantDto> similarEventParticipants) {
				return handlePickOrCreateEntry.handle(similarCases, similarContacts, similarEventParticipants);
			}

			@Override
			protected CompletionStage<CaseDataDto> handleCreateCase(CaseDataDto caze, PersonDto person, LabMessageDto labMessage) {
				return handleCreateCase.apply(caze, person);
			}

			@Override
			protected CompletionStage<SampleDto> handleCreateSample(SampleDto sample, Disease disease, LabMessageDto labMessage) {
				return handleCreateSample.apply(sample);
			}
		};
	}

	@Test
	public void testRunFlow() throws ExecutionException, InterruptedException {
		ResultStatus result = runFlow(createLabMessage(null, "", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(DONE));
	}

	@Test
	public void testHandleMissingDisease() throws ExecutionException, InterruptedException {
		ResultStatus result = runFlow(createLabMessage(null, "", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(DONE));
		Mockito.verify(missingDiseaseHandler, Mockito.times(1)).get();
	}

	@Test
	public void testNoDiseaseHandler() throws ExecutionException, InterruptedException {
		runFlow(createLabMessage(Disease.CORONAVIRUS, "", LabMessageStatus.UNPROCESSED));

		Mockito.verify(missingDiseaseHandler, Mockito.times(0)).get();
	}

	@Test
	public void testCancelOnDiseaseCheck() throws ExecutionException, InterruptedException {
		when(missingDiseaseHandler.get()).thenReturn(CompletableFuture.completedFuture(false));

		ResultStatus result = runFlow(createLabMessage(null, "", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(CANCELED));
		verify(relatedForwardedMessagesHandler, times(0)).get();
		verify(relatedLabMessageHandler, times(0)).handle(any());
	}

	@Test
	public void testHandleRelatedForwardedMessages() throws ExecutionException, InterruptedException {
		FacadeProvider.getLabMessageFacade().save(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.FORWARDED));

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ResultStatus result = runFlow(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(DONE));
		verify(relatedForwardedMessagesHandler, times(1)).get();
	}

	@Test
	public void testNoHandleRelatedForwardedMessagesHandler() throws ExecutionException, InterruptedException {
		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ResultStatus result = runFlow(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(DONE));
		verify(relatedForwardedMessagesHandler, times(0)).get();
	}

	@Test
	public void testCancelOnHandleRelatedForwardedMessagesHandler() throws ExecutionException, InterruptedException {
		FacadeProvider.getLabMessageFacade().save(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.FORWARDED));

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(false));

		ResultStatus result = runFlow(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(CANCELED));
		verify(relatedLabMessageHandler, times(0)).handle(any());
	}

	@Test
	public void testHandleRelatedLabmessages() throws ExecutionException, InterruptedException {
		when(relatedLabMessageHandler.handle(any())).thenReturn(CompletableFuture.completedFuture(RelatedLabMessageHandler.HandlerResult.HANDLED));

		ResultStatus result = runFlow(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(DONE));
		verify(relatedLabMessageHandler, times(1)).handle(any());
		verify(handlePickOrCreatePerson, times(0)).get();
	}

	@Test
	public void testCancelOnRelatedLabmessages() throws ExecutionException, InterruptedException {
		when(relatedLabMessageHandler.handle(any())).thenReturn(CompletableFuture.completedFuture(RelatedLabMessageHandler.HandlerResult.CANCELED));

		ResultStatus result = runFlow(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(CANCELED));
		verify(handlePickOrCreatePerson, times(0)).get();
	}

	@Test
	public void testCancelWithChangesOnRelatedLabmessages() throws ExecutionException, InterruptedException {
		when(relatedLabMessageHandler.handle(any()))
			.thenReturn(CompletableFuture.completedFuture(RelatedLabMessageHandler.HandlerResult.CANCELED_WITH_UPDATES));

		ResultStatus result = runFlow(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(CANCELED_WITH_CORRECTIONS));
		verify(handlePickOrCreatePerson, times(0)).get();
	}

	@Test
	public void testContinueOnRelatedLabmessages() throws ExecutionException, InterruptedException {
		when(relatedLabMessageHandler.handle(any())).thenReturn(CompletableFuture.completedFuture(RelatedLabMessageHandler.HandlerResult.CONTINUE));

		ResultStatus result = runFlow(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(DONE));
		verify(handlePickOrCreatePerson, times(1)).get();
	}

	@Test
	public void testPickOrCreateEntry(){
		//TODO - check for similar case/contact/eventparticipant
	}

	@Test
	public void testCreateCase() throws ExecutionException, InterruptedException {
		PersonDto person = PersonDto.build();
		when(handlePickOrCreatePerson.get()).thenReturn(CompletableFuture.completedFuture(person));

		SimilarEntriesDto similarEntriesDto = new SimilarEntriesDto();
		similarEntriesDto.setNewCase(true);
		when(handlePickOrCreateEntry.handle(any(), any(), any())).thenReturn(CompletableFuture.completedFuture(similarEntriesDto));

		when(handleCreateCase.apply(any(), any())).then(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));

		ResultStatus result = runFlow(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(DONE));
		verify(handleCreateCase, times(1)).apply(any(), any());
		verify(handleCreateCase).apply(
			argThat(c -> c.getPerson().equals(person.toReference()) && c.getDisease() == Disease.CORONAVIRUS && c.getReportingUser().equals(userRef)),
			argThat(p -> p.equals(person)));
	}

	@Test
	public void testCancelOnCreateCase() throws ExecutionException, InterruptedException {
		PersonDto person = PersonDto.build();
		when(handlePickOrCreatePerson.get()).thenReturn(CompletableFuture.completedFuture(person));
		when(handleCreateCase.apply(any(), any())).then(invocation -> CompletableFuture.completedFuture(null));

		ResultStatus result = runFlow(createLabMessage(Disease.CORONAVIRUS, "teast-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result, is(CANCELED));
		verify(handleCreateSample, times(0)).apply(any());
	}

	@Test
	public void testCreateCaseAndSample() throws ExecutionException, InterruptedException {
		PersonDto person = PersonDto.build();
		when(handlePickOrCreatePerson.get()).thenReturn(CompletableFuture.completedFuture(person));
		SimilarEntriesDto similarEntriesDto = new SimilarEntriesDto();
		similarEntriesDto.setNewCase(true);
		when(handlePickOrCreateEntry.handle(any(), any(), any())).thenReturn(CompletableFuture.completedFuture(similarEntriesDto));
		when(handleCreateCase.apply(any(), any())).then(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));
		when(handleCreateSample.apply(any())).then(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));

		LabMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED);
		labMessage.setSampleDateTime(new Date());
		labMessage.setSampleMaterial(SampleMaterial.BLOOD);
		ResultStatus result = runFlow(labMessage);

		assertThat(result, is(DONE));
		verify(handleCreateSample).apply(argThat(s -> {
			assertThat(s.getSampleDateTime(), is(labMessage.getSampleDateTime()));
			assertThat(s.getSampleMaterial(), is(labMessage.getSampleMaterial()));
			
			return true;
		}));
	}

	private ResultStatus runFlow(LabMessageDto labMessage) throws ExecutionException, InterruptedException {
		return flow.run(labMessage, relatedLabMessageHandler);
	}

	private LabMessageDto createLabMessage(Disease disease, String reportId, LabMessageStatus status) {
		LabMessageDto labMessage = LabMessageDto.build();

		labMessage.setTestedDisease(disease);
		labMessage.setReportId(reportId);
		labMessage.setStatus(status);
		return labMessage;
	}

	private interface PickOrCreateEntryHandler {

		CompletionStage<SimilarEntriesDto> handle(
			List<CaseSelectionDto> similarCases,
			List<SimilarContactDto> similarContacts,
			List<SimilarEventParticipantDto> similarEventParticipants);
	}
}
