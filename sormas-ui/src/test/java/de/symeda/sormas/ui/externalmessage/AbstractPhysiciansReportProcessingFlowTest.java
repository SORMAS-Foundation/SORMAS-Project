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

package de.symeda.sormas.ui.externalmessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.junit.jupiter.api.Test;
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
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.processing.AbstractProcessingFlow.HandlerCallback;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.externalmessage.physiciansreport.AbstractPhysiciansReportProcessingFlow;

public class AbstractPhysiciansReportProcessingFlowTest extends AbstractUiBeanTest {

	private AbstractPhysiciansReportProcessingFlow flow;

	private Supplier<CompletionStage<Boolean>> missingDiseaseHandler;

	/**
	 * Needed, because cdi-test InvocationTargetManager.onMockCreated doesn't allow multiple mocks for the same (generic) class
	 */
	private interface RelatedForwardedMessageHandler extends Supplier<CompletionStage<Boolean>> {
	}

	private RelatedForwardedMessageHandler relatedForwardedMessagesHandler;

	private interface PickOrCreatePersonHandler extends BiFunction<PersonDto, HandlerCallback<PersonDto>, Void> {
	}

	private PickOrCreatePersonHandler handlePickOrCreatePerson;
	private PickOrCreateEntryHandler handlePickOrCreateEntry;
	private CaseCreationHandler handleCreateCase;

	private interface UpdateCaseHandler extends BiFunction<CaseDataDto, HandlerCallback<CaseDataDto>, Void> {
	}

	private UpdateCaseHandler handleUpdateCase;

	private interface ConvertSamePersonDataHandler extends BiFunction<CaseDataDto, HandlerCallback<Void>, Void> {
	}

	private ConvertSamePersonDataHandler handleConvertSamePersonContactsAndEventParticipants;

	private TestDataCreator.RDCF rdcf;
	private UserDto user;

	@Override
	@SuppressWarnings("unchecked")
	public void init() {
		super.init();

		missingDiseaseHandler = Mockito.mock(Supplier.class);
		when(missingDiseaseHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		relatedForwardedMessagesHandler = Mockito.mock(RelatedForwardedMessageHandler.class);
		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		handlePickOrCreatePerson = Mockito.mock(PickOrCreatePersonHandler.class);
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

		handleUpdateCase = Mockito.mock(UpdateCaseHandler.class);
		doAnswer(invocation -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleUpdateCase).apply(any(), any());

		handleConvertSamePersonContactsAndEventParticipants = Mockito.mock(ConvertSamePersonDataHandler.class);
		doAnswer(invocation -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleConvertSamePersonContactsAndEventParticipants).apply(any(), any());

		rdcf = creator.createRDCF();
		user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));

		flow = new AbstractPhysiciansReportProcessingFlow(
			user,
			new ExternalMessageProcessingFacade(
				getExternalMessageFacade(),
				getFeatureConfigurationFacade(),
				getCaseFacade(),
				getContactFacade(),
				getEventFacade(),
				getEventParticipantFacade(),
				getSampleFacade(),
				getPathogenTestFacade(),
				getFacilityFacade()) {

				@Override
				public boolean hasAllUserRights(UserRight... userRights) {
					return true;
				}
			}) {

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
				ExternalMessageDto externalMessage,
				HandlerCallback<PickOrCreateEntryResult> callback) {
				handlePickOrCreateEntry.handle(similarCases, callback);
			}

			@Override
			protected void handleCreateCase(
				CaseDataDto caze,
				PersonDto person,
				ExternalMessageDto externalMessage,
				HandlerCallback<CaseDataDto> callback) {
				handleCreateCase.handle(caze, person, callback);
			}

			@Override
			protected void handleUpdateCase(CaseDataDto caze, ExternalMessageDto externalMessage, HandlerCallback<CaseDataDto> callback) {
				handleUpdateCase.apply(caze, callback);
			}

			@Override
			protected void handleConvertSamePersonContactsAndEventParticipants(CaseDataDto caze, HandlerCallback<Void> callback) {
				handleConvertSamePersonContactsAndEventParticipants.apply(caze, callback);
			}
		};
	}

	@Test
	public void testRunFlow() throws ExecutionException, InterruptedException {
		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.DONE));
	}

	@Test
	public void testHandleMissingDisease() throws ExecutionException, InterruptedException {

		ProcessingResult<CaseDataDto> result = runFlow(createExternalMessage(null, "", ExternalMessageStatus.UNPROCESSED));
		assertThat(result.getStatus(), is(ProcessingResultStatus.DONE));
		Mockito.verify(missingDiseaseHandler, Mockito.times(1)).get();
	}

	@Test
	public void testHandleMissingDiseaseNotNeeded() throws ExecutionException, InterruptedException {

		runFlow(createExternalMessage(Disease.CORONAVIRUS, "", ExternalMessageStatus.UNPROCESSED));
		Mockito.verify(missingDiseaseHandler, Mockito.times(0)).get();
	}

	@Test
	public void testHandleMissingDiseaseCancel() throws ExecutionException, InterruptedException {

		when(missingDiseaseHandler.get()).thenReturn(CompletableFuture.completedFuture(false));

		ProcessingResult<CaseDataDto> result = runFlow(createExternalMessage(null, "", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.CANCELED));
		verify(relatedForwardedMessagesHandler, times(0)).get();
		verify(handlePickOrCreatePerson, times(0)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedForwardedMessages() throws ExecutionException, InterruptedException {

		FacadeProvider.getExternalMessageFacade().save(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.FORWARDED));

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.DONE));
		verify(relatedForwardedMessagesHandler, times(1)).get();
		verify(handlePickOrCreatePerson, times(1)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedForwardedMessagesNotNeeded() throws ExecutionException, InterruptedException {

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.DONE));
		verify(relatedForwardedMessagesHandler, times(0)).get();
		verify(handlePickOrCreatePerson, times(1)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedForwardedMessagesCancel() throws ExecutionException, InterruptedException {

		FacadeProvider.getExternalMessageFacade().save(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.FORWARDED));

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(false));

		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.CANCELED));
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

		ExternalMessageDto externalMessage = createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		externalMessage.setPersonFirstName("Ftest");
		externalMessage.setPersonLastName("Ltest");
		externalMessage.setPersonSex(Sex.UNKNOWN);
		externalMessage.setPersonStreet("Test st.");

		ProcessingResult<CaseDataDto> result = runFlow(externalMessage);

		assertThat(result.getStatus(), is(ProcessingResultStatus.DONE));
		assertThat(personCaptor.getValue().getFirstName(), is(externalMessage.getPersonFirstName()));
		assertThat(personCaptor.getValue().getLastName(), is(externalMessage.getPersonLastName()));
		assertThat(personCaptor.getValue().getSex(), is(externalMessage.getPersonSex()));
		assertThat(personCaptor.getValue().getAddress().getStreet(), is(externalMessage.getPersonStreet()));

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

		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.DONE));

		// the selected person should be assigned to the case
		assertThat(caseCaptor.getValue().getPerson(), is(person.toReference()));
	}

	@Test
	public void testPickOrCreatePersonCancel() throws ExecutionException, InterruptedException {

		doAnswer(invocation -> {
			((HandlerCallback<?>) invocation.getArgument(1)).cancel();
			return null;
		}).when(handlePickOrCreatePerson).apply(any(), any());

		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.CANCELED));
		verify(handlePickOrCreateEntry, times(0)).handle(any(), any());
	}

	@Test
	public void testPickOrCreateEntryCancel() throws ExecutionException, InterruptedException {

		doAnswer(invocation -> {
			((HandlerCallback<?>) invocation.getArgument(1)).cancel();
			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any());

		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.CANCELED));
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

		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		verify(handleCreateCase).handle(argThat(c -> {
			assertThat(c.getPerson(), is(personCaptor.getValue().toReference()));
			assertThat(c.getDisease(), is(Disease.CORONAVIRUS));
			assertThat(c.getReportingUser(), is(user.toReference()));

			return true;
		}), argThat(p -> p.equals(personCaptor.getValue())), any());

		assertThat(result.getStatus(), is(ProcessingResultStatus.DONE));
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

		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.CANCELED));
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

		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		verify(handleUpdateCase).apply(argThat(c -> {
			assertThat(c.getUuid(), is(caze.getUuid()));
			assertThat(c.getDisease(), is(Disease.CORONAVIRUS));
			assertThat(c.getReportingUser(), is(user.toReference()));

			return true;
		}), any());

		assertThat(result.getStatus(), is(ProcessingResultStatus.DONE));
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

		ProcessingResult<CaseDataDto> result =
			runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(ProcessingResultStatus.CANCELED));
	}

	@Test
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

		assertThrows(
			ExecutionException.class,
			() -> runFlow(createExternalMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED)));
	}

	private ProcessingResult<CaseDataDto> runFlow(ExternalMessageDto externalMessage) throws ExecutionException, InterruptedException {

		return flow.run(externalMessage).toCompletableFuture().get();
	}

	private ExternalMessageDto createExternalMessage(Disease disease, String reportId, ExternalMessageStatus status) {
		return createExternalMessage(disease, reportId, status, null);
	}

	private ExternalMessageDto createExternalMessage(
		Disease disease,
		String reportId,
		ExternalMessageStatus status,
		Consumer<ExternalMessageDto> customConfig) {
		ExternalMessageDto externalMessage = ExternalMessageDto.build();

		externalMessage.setDisease(disease);
		externalMessage.setReportId(reportId);
		externalMessage.setStatus(status);

		if (customConfig != null) {
			customConfig.accept(externalMessage);
		}

		return externalMessage;
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
