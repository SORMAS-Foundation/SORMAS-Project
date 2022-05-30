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

package de.symeda.sormas.ui.labmessage;

import static de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResultStatus.CANCELED;
import static de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResultStatus.DONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.labmessage.processing.AbstractPhysicianReportProcessingFlow;
import de.symeda.sormas.ui.labmessage.processing.AbstractProcessingFlow.HandlerCallback;
import de.symeda.sormas.ui.labmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResultStatus;

public class AbstractPhysicianReportProcessingFlowTest extends AbstractBeanTest {

	private AbstractPhysicianReportProcessingFlow flow;

	private Supplier<CompletionStage<Boolean>> missingDiseaseHandler;
	private Supplier<CompletionStage<Boolean>> relatedForwardedMessagesHandler;
	private BiFunction<PersonDto, HandlerCallback<PersonDto>, Void> handlePickOrCreatePerson;
	private PickOrCreateEntryHandler handlePickOrCreateEntry;
	private CaseCreationHandler handleCreateCase;
	private BiFunction<CaseDataDto, HandlerCallback<CaseDataDto>, Void> handleUpdateCase;

	private TestDataCreator.RDCF rdcf;
	private UserDto user;

	@Override
	@SuppressWarnings("unchecked")
	public void init() {
		super.init();

		missingDiseaseHandler = Mockito.mock(Supplier.class);
		when(missingDiseaseHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		relatedForwardedMessagesHandler = Mockito.mock(Supplier.class);
		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		handlePickOrCreatePerson = Mockito.mock(BiFunction.class);
		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).apply(any(), any());

		handlePickOrCreateEntry = Mockito.mock(PickOrCreateEntryHandler.class);
		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewCase(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any());

		handleCreateCase = Mockito.mock(CaseCreationHandler.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateCase).handle(any(), any(), any());

		handleUpdateCase = Mockito.mock(BiFunction.class);
		doAnswer(invocation -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleUpdateCase).apply(any(), any());

		rdcf = creator.createRDCF();
		user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));

		flow = new AbstractPhysicianReportProcessingFlow(user) {

			@Override
			protected CompletionStage<Boolean> handleMissingDisease() {
				return missingDiseaseHandler.get();
			}

			@Override
			protected CompletionStage<Boolean> handleRelatedForwardedMessages() {
				return relatedForwardedMessagesHandler.get();
			}

			@Override
			protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<PersonDto> callback) {
				handlePickOrCreatePerson.apply(person, callback);
			}

			@Override
			protected void handlePickOrCreateEntry(
				List<CaseSelectionDto> similarCases,
				LabMessageDto labMessageDto,
				HandlerCallback<PickOrCreateEntryResult> callback) {
				handlePickOrCreateEntry.handle(similarCases, callback);
			}

			@Override
			protected void handleCreateCase(CaseDataDto caze, PersonDto person, LabMessageDto labMessage, HandlerCallback<CaseDataDto> callback) {
				handleCreateCase.handle(caze, person, callback);
			}

			@Override
			protected void handleUpdateCase(CaseDataDto caze, LabMessageDto labMessage, HandlerCallback<CaseDataDto> callback) {
				handleUpdateCase.apply(caze, callback);
			}
		};
	}

	@Test
	public void testRunFlow() throws ExecutionException, InterruptedException {
		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.DONE));
	}

	@Test
	public void testHandleMissingDisease() throws ExecutionException, InterruptedException {

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(null, "", LabMessageStatus.UNPROCESSED));
		assertThat(result.getStatus(), is(DONE));
		Mockito.verify(missingDiseaseHandler, Mockito.times(1)).get();
	}

	@Test
	public void testHandleMissingDiseaseNotNeeded() throws ExecutionException, InterruptedException {

		runFlow(createLabMessage(Disease.CORONAVIRUS, "", LabMessageStatus.UNPROCESSED));
		Mockito.verify(missingDiseaseHandler, Mockito.times(0)).get();
	}

	@Test
	public void testHandleMissingDiseaseCancel() throws ExecutionException, InterruptedException {

		when(missingDiseaseHandler.get()).thenReturn(CompletableFuture.completedFuture(false));

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(null, "", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(relatedForwardedMessagesHandler, times(0)).get();
		verify(handlePickOrCreatePerson, times(0)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedForwardedMessages() throws ExecutionException, InterruptedException {

		FacadeProvider.getLabMessageFacade().save(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.FORWARDED));

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		verify(relatedForwardedMessagesHandler, times(1)).get();
		verify(handlePickOrCreatePerson, times(1)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedForwardedMessagesNotNeeded() throws ExecutionException, InterruptedException {

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		verify(relatedForwardedMessagesHandler, times(0)).get();
		verify(handlePickOrCreatePerson, times(1)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedForwardedMessagesCancel() throws ExecutionException, InterruptedException {

		FacadeProvider.getLabMessageFacade().save(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.FORWARDED));

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(false));

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handlePickOrCreatePerson, times(0)).apply(any(), any());
	}

	@Test
	public void testCreatePerson() throws ExecutionException, InterruptedException {

		ArgumentCaptor<PersonDto> personCaptor = ArgumentCaptor.forClass(PersonDto.class);
		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).apply(personCaptor.capture(), any());

		ArgumentCaptor<CaseDataDto> caseCaptor = ArgumentCaptor.forClass(CaseDataDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateCase).handle(caseCaptor.capture(), any(), any());

		LabMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED);
		labMessage.setPersonFirstName("Ftest");
		labMessage.setPersonLastName("Ltest");
		labMessage.setPersonSex(Sex.UNKNOWN);
		labMessage.setPersonStreet("Test st.");

		ProcessingResult<CaseDataDto> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));
		assertThat(personCaptor.getValue().getFirstName(), is(labMessage.getPersonFirstName()));
		assertThat(personCaptor.getValue().getLastName(), is(labMessage.getPersonLastName()));
		assertThat(personCaptor.getValue().getSex(), is(labMessage.getPersonSex()));
		assertThat(personCaptor.getValue().getAddress().getStreet(), is(labMessage.getPersonStreet()));

		// the created person should be assigned to the case
		assertThat(caseCaptor.getValue().getPerson(), is(personCaptor.getValue().toReference()));
	}

	@Test
	public void testPickPerson() throws ExecutionException, InterruptedException {

		PersonDto person = PersonDto.build();
		person.setFirstName("Ftest");

		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());
		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewCase(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any());

		ArgumentCaptor<CaseDataDto> caseCaptor = ArgumentCaptor.forClass(CaseDataDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateCase).handle(caseCaptor.capture(), any(), any());

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		// the selected person should be assigned to the case
		assertThat(caseCaptor.getValue().getPerson(), is(person.toReference()));
	}

	@Test
	public void testPickOrCreatePersonCancel() throws ExecutionException, InterruptedException {

		doAnswer(invocation -> {
			((HandlerCallback<?>) invocation.getArgument(1)).cancel();
			return null;
		}).when(handlePickOrCreatePerson).apply(any(), any());

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handlePickOrCreateEntry, times(0)).handle(any(), any());
	}

	@Test
	public void testPickOrCreateEntryCancel() throws ExecutionException, InterruptedException {

		doAnswer(invocation -> {
			((HandlerCallback<?>) invocation.getArgument(1)).cancel();
			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any());

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateCase, times(0)).handle(any(), any(), any());
		verify(handleUpdateCase, times(0)).apply(any(), any());
	}

	@Test
	public void testCreateCase() throws ExecutionException, InterruptedException {

		ArgumentCaptor<PersonDto> personCaptor = ArgumentCaptor.forClass(PersonDto.class);
		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).apply(personCaptor.capture(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewCase(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any());

		ArgumentCaptor<CaseDataDto> caseCaptor = ArgumentCaptor.forClass(CaseDataDto.class);
		doAnswer(invocation -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));

			return null;
		}).when(handleCreateCase).handle(caseCaptor.capture(), any(), any());

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		verify(handleCreateCase).handle(argThat(c -> {
			assertThat(c.getPerson(), is(personCaptor.getValue().toReference()));
			assertThat(c.getDisease(), is(Disease.CORONAVIRUS));
			assertThat(c.getReportingUser(), is(user.toReference()));

			return true;
		}), argThat(p -> p.equals(personCaptor.getValue())), any());

		assertThat(result.getStatus(), is(DONE));
		assertThat(caseCaptor.getValue(), is(result.getData()));
		verify(handleUpdateCase, times(1)).apply(argThat(c -> {
			assertThat(c, is(caseCaptor.getValue()));
			return true;
		}), any());
	}

	@Test
	public void testCreateCaseCancel() throws ExecutionException, InterruptedException {

		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewCase(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any());

		doAnswer(invocation -> {
			getCallbackParam(invocation).cancel();

			return null;
		}).when(handleCreateCase).handle(any(), any(), any());

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleUpdateCase, times(0)).apply(any(), any());
	}

	@Test
	public void testUpdateCase() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson("Ftest", "Ltest");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		doAnswer(invocation -> {
			List<CaseSelectionDto> cases = invocation.getArgument(0);
			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setCaze(cases.get(0));

			getCallbackParam(invocation).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any());

		ArgumentCaptor<CaseDataDto> caseCaptor = ArgumentCaptor.forClass(CaseDataDto.class);
		doAnswer(invocation -> {
			CaseDataDto updatedCase = invocation.getArgument(0);

			updatedCase.setCaseClassification(CaseClassification.NO_CASE);
			updatedCase.setOutcome(CaseOutcome.RECOVERED);

			getCallbackParam(invocation).done(updatedCase);

			return null;
		}).when(handleUpdateCase).apply(caseCaptor.capture(), any());

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		verify(handleUpdateCase).apply(argThat(c -> {
			assertThat(c.getUuid(), is(caze.getUuid()));
			assertThat(c.getDisease(), is(Disease.CORONAVIRUS));
			assertThat(c.getReportingUser(), is(user.toReference()));

			return true;
		}), any());

		assertThat(result.getStatus(), is(DONE));
		assertThat(caze, is(result.getData()));
		assertThat(result.getData().getCaseClassification(), is(CaseClassification.NO_CASE));
		assertThat(result.getData().getOutcome(), is(CaseOutcome.RECOVERED));
	}

	@Test
	public void testUpdateCaseCancel() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson("Ftest", "Ltest");
		creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		doAnswer(invocation -> {
			List<CaseSelectionDto> cases = invocation.getArgument(0);
			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setCaze(cases.get(0));

			getCallbackParam(invocation).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any());

		doAnswer(invocation -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handleUpdateCase).apply(any(), any());

		ProcessingResult<CaseDataDto> result = runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
	}

	@Test(expected = ExecutionException.class)
	public void testExceptionInFlow() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson("Ftest", "Ltest");
		creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		doThrow(new RuntimeException("Error")).when(handlePickOrCreateEntry).handle(any(), any());

		runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));
	}

	private ProcessingResult<CaseDataDto> runFlow(LabMessageDto labMessage) throws ExecutionException, InterruptedException {

		return flow.run(labMessage).toCompletableFuture().get();
	}

	private LabMessageDto createLabMessage(Disease disease, String reportId, LabMessageStatus status) {
		return createLabMessage(disease, reportId, status, null);
	}

	private LabMessageDto createLabMessage(Disease disease, String reportId, LabMessageStatus status, Consumer<LabMessageDto> customConfig) {
		LabMessageDto labMessage = LabMessageDto.build();

		labMessage.setTestedDisease(disease);
		labMessage.setReportId(reportId);
		labMessage.setStatus(status);

		if (customConfig != null) {
			customConfig.accept(labMessage);
		}

		return labMessage;
	}

	@NotNull
	private Answer<Object> answerPickOrCreatePerson(@Nullable PersonDto existingPerson) {
		return invocation -> {
			HandlerCallback<PersonDto> callback = invocation.getArgument(1);
			PersonDto person = existingPerson != null ? existingPerson : invocation.getArgument(0);
			callback.done(person);

			return null;
		};
	}

	@NotNull
	private Answer<?> answerPickOrCreateEntry(PickOrCreateEntryResult pickOrCreateEntryResult) {
		return invocation -> {
			getCallbackParam(invocation).done(pickOrCreateEntryResult);
			return null;
		};
	}

	@SuppressWarnings("unchecked")
	private <T> HandlerCallback<T> getCallbackParam(InvocationOnMock invocation) {
		Object[] arguments = invocation.getArguments();
		return (HandlerCallback<T>) arguments[arguments.length - 1];
	}

	private interface PickOrCreateEntryHandler {

		void handle(List<CaseSelectionDto> similarCases, HandlerCallback<PickOrCreateEntryResult> callback);
	}

	private interface CaseCreationHandler {

		Object handle(CaseDataDto entity, PersonDto person, HandlerCallback<?> callback);
	}

}
