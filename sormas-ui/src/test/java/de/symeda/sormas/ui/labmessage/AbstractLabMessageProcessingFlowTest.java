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
import static de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResultStatus.CANCELED_WITH_CORRECTIONS;
import static de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResultStatus.DONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
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
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplingReason;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.labmessage.processing.AbstractLabMessageProcessingFlow;
import de.symeda.sormas.ui.labmessage.processing.AbstractLabMessageProcessingFlow.HandlerCallback;
import de.symeda.sormas.ui.labmessage.processing.AbstractRelatedLabMessageHandler;
import de.symeda.sormas.ui.labmessage.processing.AbstractRelatedLabMessageHandler.HandlerResult;
import de.symeda.sormas.ui.labmessage.processing.AbstractRelatedLabMessageHandler.HandlerResultStatus;
import de.symeda.sormas.ui.labmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.labmessage.processing.PickOrCreateEventResult;
import de.symeda.sormas.ui.labmessage.processing.PickOrCreateSampleResult;
import de.symeda.sormas.ui.labmessage.processing.SampleAndPathogenTests;
import de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResult;

public class AbstractLabMessageProcessingFlowTest extends AbstractBeanTest {

	private AbstractLabMessageProcessingFlow flow;

	private Supplier<CompletionStage<Boolean>> missingDiseaseHandler;
	private Supplier<CompletionStage<Boolean>> relatedForwardedMessagesHandler;
	private AbstractRelatedLabMessageHandler relatedLabMessageHandler;
	private BiFunction<PersonDto, HandlerCallback<PersonDto>, Void> handlePickOrCreatePerson;
	private PickOrCreateEntryHandler handlePickOrCreateEntry;
	private EntityCreationHandler<CaseDataDto> handleCreateCase;
	private CreateSampleAndPathogenTestHandler handleCreateSampleAndPathogenTests;
	private EntityCreationHandler<ContactDto> handleCreateContact;
	private Consumer<HandlerCallback<PickOrCreateEventResult>> handlePickOrCreateEvent;
	private BiConsumer<EventDto, HandlerCallback<EventDto>> handleCreateEvent;
	private BiConsumer<EventParticipantDto, HandlerCallback<EventParticipantDto>> handleCreateEventParticipant;
	private Supplier<CompletionStage<Boolean>> confirmPickExistingEventParticipant;
	private BiConsumer<List<SampleDto>, HandlerCallback<PickOrCreateSampleResult>> handlePickOrCreateSample;
	private EditSampleHandler handleEditSample;
	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private CountryReferenceDto country;

	@Override
	@SuppressWarnings("unchecked")
	public void init() {
		super.init();

		missingDiseaseHandler = Mockito.mock(Supplier.class);
		when(missingDiseaseHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		relatedForwardedMessagesHandler = Mockito.mock(Supplier.class);
		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		relatedLabMessageHandler = Mockito.mock(AbstractRelatedLabMessageHandler.class);
		when(relatedLabMessageHandler.handle(any()))
			.thenReturn(CompletableFuture.completedFuture(new HandlerResult(HandlerResultStatus.NOT_HANDLED, null)));

		handlePickOrCreatePerson = Mockito.mock(BiFunction.class);
		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).apply(any(), any());

		handlePickOrCreateEntry = Mockito.mock(PickOrCreateEntryHandler.class);
		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewCase(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		handleCreateCase = Mockito.mock(EntityCreationHandler.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateCase).handle(any(), any(), any());

		handleCreateSampleAndPathogenTests = Mockito.mock(CreateSampleAndPathogenTestHandler.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(new SampleAndPathogenTests(invocation.getArgument(0), invocation.getArgument(1)));
			return null;
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), any());

		handleCreateContact = Mockito.mock(EntityCreationHandler.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateContact).handle(any(), any(), any());

		handlePickOrCreateEvent = Mockito.mock(Consumer.class);
		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		pickOrCreateEventResult.setNewEvent(true);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		handleCreateEvent = Mockito.mock(BiConsumer.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateEvent).accept(any(), any());

		handleCreateEventParticipant = Mockito.mock(BiConsumer.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateEventParticipant).accept(any(), any());

		confirmPickExistingEventParticipant = Mockito.mock(Supplier.class);
		when(confirmPickExistingEventParticipant.get()).thenReturn(CompletableFuture.completedFuture(true));

		handlePickOrCreateSample = Mockito.mock(BiConsumer.class);
		PickOrCreateSampleResult pickOrCreateSampleResult = new PickOrCreateSampleResult();
		pickOrCreateSampleResult.setNewSample(true);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateSampleResult);
			return null;
		}).when(handlePickOrCreateSample).accept(any(), any());

		handleEditSample = Mockito.mock(EditSampleHandler.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(new SampleAndPathogenTests(invocation.getArgument(0), invocation.getArgument(1)));
			return null;
		}).when(handleEditSample).handle(any(), any(), any());

		rdcf = creator.createRDCF();
		user = creator.createUser(rdcf, UserRole.NATIONAL_USER);
		country = new CountryReferenceDto(DataHelper.createUuid(), "de-DE");
		flow = new AbstractLabMessageProcessingFlow(user, country) {

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
				List<SimilarContactDto> similarContacts,
				List<SimilarEventParticipantDto> similarEventParticipants,
				LabMessageDto labMessageDto,
				HandlerCallback<PickOrCreateEntryResult> callback) {
				handlePickOrCreateEntry.handle(similarCases, similarContacts, similarEventParticipants, callback);
			}

			@Override
			protected void handleCreateCase(CaseDataDto caze, PersonDto person, LabMessageDto labMessage, HandlerCallback<CaseDataDto> callback) {
				handleCreateCase.handle(caze, person, callback);
			}

			@Override
			protected void handleCreateSampleAndPathogenTests(
				SampleDto sample,
				List<PathogenTestDto> pathogenTests,
				Disease disease,
				LabMessageDto labMessage,
				boolean entityCreated,
				HandlerCallback<SampleAndPathogenTests> callback) {
				handleCreateSampleAndPathogenTests.handle(sample, pathogenTests, entityCreated, callback);
			}

			@Override
			protected void handleCreateContact(ContactDto contact, PersonDto person, LabMessageDto labMessage, HandlerCallback<ContactDto> callback) {
				handleCreateContact.handle(contact, person, callback);
			}

			@Override
			protected void handlePickOrCreateEvent(LabMessageDto labMessage, HandlerCallback<PickOrCreateEventResult> callback) {
				handlePickOrCreateEvent.accept(callback);
			}

			@Override
			protected void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback) {
				handleCreateEvent.accept(event, callback);
			}

			@Override
			protected void handleCreateEventParticipant(
				EventParticipantDto eventParticipant,
				EventDto event,
				LabMessageDto labMessage,
				HandlerCallback<EventParticipantDto> callback) {
				handleCreateEventParticipant.accept(eventParticipant, callback);
			}

			@Override
			protected CompletionStage<Boolean> confirmPickExistingEventParticipant() {
				return confirmPickExistingEventParticipant.get();
			}

			@Override
			protected void handlePickOrCreateSample(
				List<SampleDto> samples,
				LabMessageDto labMessageDto,
				HandlerCallback<PickOrCreateSampleResult> callback) {
				handlePickOrCreateSample.accept(samples, callback);
			}

			@Override
			protected void handleEditSample(
				SampleDto sample,
				List<PathogenTestDto> newPathogenTests,
				LabMessageDto labMessage,
				HandlerCallback<SampleAndPathogenTests> callback) {
				handleEditSample.handle(sample, newPathogenTests, callback);
			}
		};
	}

	@Test
	public void testCreateLabMessage() throws ExecutionException, InterruptedException {

		ProcessingResult<SampleAndPathogenTests> result = runFlow(createLabMessage(null, "", LabMessageStatus.UNPROCESSED));
		assertThat(result.getStatus(), is(DONE));
	}

	@Test
	public void testHandleMissingDisease() throws ExecutionException, InterruptedException {

		ProcessingResult<SampleAndPathogenTests> result = runFlow(createLabMessage(null, "", LabMessageStatus.UNPROCESSED));
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

		ProcessingResult<SampleAndPathogenTests> result = runFlow(createLabMessage(null, "", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(relatedForwardedMessagesHandler, times(0)).get();
		verify(relatedLabMessageHandler, times(0)).handle(any());
	}

	@Test
	public void testHandleRelatedForwardedMessages() throws ExecutionException, InterruptedException {

		FacadeProvider.getLabMessageFacade().save(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.FORWARDED));

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		verify(relatedForwardedMessagesHandler, times(1)).get();
	}

	@Test
	public void testHandleRelatedForwardedMessagesNotNeeded() throws ExecutionException, InterruptedException {

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		verify(relatedForwardedMessagesHandler, times(0)).get();
	}

	@Test
	public void testHandleRelatedForwardedMessagesCancel() throws ExecutionException, InterruptedException {

		FacadeProvider.getLabMessageFacade().save(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.FORWARDED));

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(false));

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(relatedLabMessageHandler, times(0)).handle(any());
	}

	@Test
	public void testHandleRelatedLabmessages() throws ExecutionException, InterruptedException {

		SampleDto sample = creator.createSample(
			creator
				.createCase(
					user.toReference(),
					creator.createPerson().toReference(),
					Disease.CORONAVIRUS,
					CaseClassification.SUSPECT,
					InvestigationStatus.PENDING,
					new Date(),
					rdcf)
				.toReference(),
			user.toReference(),
			rdcf.facility);

		when(relatedLabMessageHandler.handle(any()))
			.thenReturn(CompletableFuture.completedFuture(new HandlerResult(HandlerResultStatus.HANDLED, sample)));

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		assertThat(result.getData().getSample(), is(sample));
		verify(relatedLabMessageHandler, times(1)).handle(any());
		verify(handlePickOrCreatePerson, times(0)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedLabmessagesCancel() throws ExecutionException, InterruptedException {

		when(relatedLabMessageHandler.handle(any()))
			.thenReturn(CompletableFuture.completedFuture(new HandlerResult(HandlerResultStatus.CANCELED, null)));

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handlePickOrCreatePerson, times(0)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedLabmessagesCancelWithChanges() throws ExecutionException, InterruptedException {

		when(relatedLabMessageHandler.handle(any()))
			.thenReturn(CompletableFuture.completedFuture(new HandlerResult(HandlerResultStatus.CANCELED_WITH_UPDATES, null)));

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED_WITH_CORRECTIONS));
		verify(handlePickOrCreatePerson, times(0)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedLabmessagesContinue() throws ExecutionException, InterruptedException {

		when(relatedLabMessageHandler.handle(any()))
			.thenReturn(CompletableFuture.completedFuture(new HandlerResult(HandlerResultStatus.CONTINUE, null)));

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		verify(handlePickOrCreatePerson, times(1)).apply(any(), any());
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
		ProcessingResult<SampleAndPathogenTests> result = runFlow(labMessage);

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
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		ArgumentCaptor<CaseDataDto> caseCaptor = ArgumentCaptor.forClass(CaseDataDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateCase).handle(caseCaptor.capture(), any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

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

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handlePickOrCreateEntry, times(0)).handle(any(), any(), any(), any());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickOrCreateEntryCancel() throws ExecutionException, InterruptedException {

		doAnswer(invocation -> {
			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).cancel();
			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateCase, times(0)).handle(any(), any(), any());
		verify(handleCreateContact, times(0)).handle(any(), any(), any());
		verify(handlePickOrCreateEvent, times(0)).accept(any());
	}

	@Test
	public void testCreateCase() throws ExecutionException, InterruptedException {

		ArgumentCaptor<PersonDto> personCaptor = ArgumentCaptor.forClass(PersonDto.class);
		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).apply(personCaptor.capture(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewCase(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateCase).handle(any(), any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		verify(handleCreateCase, times(1)).handle(any(), any(), any());
		verify(handleCreateCase).handle(argThat(c -> {
			assertThat(c.getPerson(), is(personCaptor.getValue().toReference()));
			assertThat(c.getDisease(), is(Disease.CORONAVIRUS));
			assertThat(c.getReportingUser(), is(user.toReference()));

			return true;
		}), argThat(p -> p.equals(personCaptor.getValue())), any());

		verify(handleCreateContact, times(0)).handle(any(), any(), any());
	}

	@Test
	public void testCreateCaseCancel() throws ExecutionException, InterruptedException {

		doAnswer((invocation) -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handleCreateCase).handle(any(), any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateContact, times(0)).handle(any(), any(), any());
		verify(handleCreateSampleAndPathogenTests, times(0)).handle(any(), any(), any(), any());
	}

	@Test
	public void testCreateCaseAndCreateSample() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewCase(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		ArgumentCaptor<CaseDataDto> caseCaptor = ArgumentCaptor.forClass(CaseDataDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateCase).handle(caseCaptor.capture(), any(), any());

		doAnswer((invocation) -> {
			SampleDto sample = invocation.getArgument(0);
			sample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> pathogenTests = invocation.getArgument(1);
			pathogenTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(sample, pathogenTests));
			return null;
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), any());

		LabMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED);
		labMessage.setSampleDateTime(new Date());
		labMessage.setSampleMaterial(SampleMaterial.BLOOD);

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestType(PathogenTestType.CULTURE);
		testReport1.setTestResult(PathogenTestResultType.NEGATIVE);
		labMessage.getTestReports().add(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.PENDING);
		labMessage.getTestReports().add(testReport2);

		ProcessingResult<SampleAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));
		verify(handleCreateSampleAndPathogenTests).handle(argThat(sample -> {
			assertThat(sample.getAssociatedCase(), is(caseCaptor.getValue().toReference()));
			assertThat(sample.getSampleDateTime(), is(labMessage.getSampleDateTime()));
			assertThat(sample.getSampleMaterial(), is(SampleMaterial.BLOOD));
			assertThat(sample.getReportingUser(), is(user.toReference()));

			return true;
		}), argThat(pathogenTests -> {
			assertThat(pathogenTests, hasSize(2));

			assertThat(pathogenTests.get(0).getTestType(), is(testReport1.getTestType()));
			assertThat(pathogenTests.get(0).getTestResult(), is(testReport1.getTestResult()));

			assertThat(pathogenTests.get(1).getTestResult(), is(testReport2.getTestResult()));

			return true;
		}), argThat(entityCreated -> {
			assertThat(entityCreated, is(true));

			return true;
		}), any());

		// test that changes in handler are kept
		assertThat(result.getData().getSample().getSamplingReason(), is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	public void testCreateSampleCancel() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewCase(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateCase).handle(any(), any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), any());

		LabMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED);
		labMessage.setSampleDateTime(new Date());
		labMessage.setSampleMaterial(SampleMaterial.BLOOD);

		ProcessingResult<SampleAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(CANCELED));
		assertThat(result.getData(), is(nullValue()));
	}

	@Test
	public void testCreateContact() throws ExecutionException, InterruptedException {

		ArgumentCaptor<PersonDto> personCaptor = ArgumentCaptor.forClass(PersonDto.class);
		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).apply(personCaptor.capture(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewContact(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateContact).handle(any(), any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		verify(handleCreateContact, times(1)).handle(any(), any(), any());
		verify(handleCreateContact).handle(argThat(c -> {
			assertThat(c.getPerson(), is(personCaptor.getValue().toReference()));
			assertThat(c.getDisease(), is(Disease.CORONAVIRUS));
			assertThat(c.getReportingUser(), is(user.toReference()));

			return true;
		}), argThat(p -> p.equals(personCaptor.getValue())), any());

		verify(handleCreateCase, times(0)).handle(any(), any(), any());
	}

	@Test
	public void testCreateContactCancel() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewContact(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handleCreateContact).handle(any(), any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateCase, times(0)).handle(any(), any(), any());
		verify(handleCreateSampleAndPathogenTests, times(0)).handle(any(), any(), any(), any());
	}

	@Test
	public void testCreateContactAndCreateSample() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewContact(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		ArgumentCaptor<ContactDto> contactCaptor = ArgumentCaptor.forClass(ContactDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateContact).handle(contactCaptor.capture(), any(), any());

		doAnswer((invocation) -> {
			SampleDto sample = invocation.getArgument(0);
			sample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> pathogenTests = invocation.getArgument(1);
			pathogenTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(sample, pathogenTests));
			return null;
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), any());

		LabMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED);
		labMessage.setSampleDateTime(new Date());
		labMessage.setSampleMaterial(SampleMaterial.BLOOD);

		TestReportDto testReport = TestReportDto.build();
		testReport.setTestType(PathogenTestType.CULTURE);
		testReport.setTestResult(PathogenTestResultType.NEGATIVE);
		labMessage.getTestReports().add(testReport);

		ProcessingResult<SampleAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));
		verify(handleCreateSampleAndPathogenTests).handle(argThat(sample -> {
			assertThat(sample.getAssociatedContact(), is(contactCaptor.getValue().toReference()));
			assertThat(sample.getSampleDateTime(), is(labMessage.getSampleDateTime()));
			assertThat(sample.getSampleMaterial(), is(SampleMaterial.BLOOD));
			assertThat(sample.getReportingUser(), is(user.toReference()));

			return true;
		}), argThat(pathogenTests -> {
			assertThat(pathogenTests, hasSize(1));

			assertThat(pathogenTests.get(0).getTestType(), is(testReport.getTestType()));
			assertThat(pathogenTests.get(0).getTestResult(), is(testReport.getTestResult()));

			return true;
		}), argThat(entityCreated -> {
			assertThat(entityCreated, is(true));

			return true;
		}), any());

		// test that changes in handler are kept
		assertThat(result.getData().getSample().getSamplingReason(), is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	public void testCreateEvent() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		pickOrCreateEventResult.setNewEvent(true);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		ArgumentCaptor<EventDto> eventCaptor = ArgumentCaptor.forClass(EventDto.class);
		doAnswer((invocation) -> {
			EventDto event = invocation.getArgument(0);
			event.setEventTitle("Test event");

			getCallbackParam(invocation).done(event);
			return null;
		}).when(handleCreateEvent).accept(eventCaptor.capture(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		verify(handleCreateEvent, times(1)).accept(any(), any());
		verify(handleCreateEvent).accept(argThat(e -> {
			assertThat(e, is(eventCaptor.getValue()));
			assertThat(e.getEventTitle(), is("Test event"));

			return true;
		}), any());

		verify(handleCreateCase, times(0)).handle(any(), any(), any());
		verify(handleCreateContact, times(0)).handle(any(), any(), any());
	}

	@Test
	public void testCreateEventCancel() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		pickOrCreateEventResult.setNewEvent(true);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handleCreateEvent).accept(any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateEventParticipant, times(0)).accept(any(), any());
		verify(handleCreateSampleAndPathogenTests, times(0)).handle(any(), any(), any(), any());
	}

	@Test
	public void testPickOrCreateEventCancel() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());
		doAnswer((invocation) -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateEvent, times(0)).accept(any(), any());
		verify(handleCreateEventParticipant, times(0)).accept(any(), any());
		verify(handleCreateSampleAndPathogenTests, times(0)).handle(any(), any(), any(), any());
	}

	@Test
	public void testCreateEventAndCreateEventParticipant() throws ExecutionException, InterruptedException {

		ArgumentCaptor<PersonDto> personCaptor = ArgumentCaptor.forClass(PersonDto.class);
		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).apply(personCaptor.capture(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		pickOrCreateEventResult.setNewEvent(true);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		ArgumentCaptor<EventDto> eventCaptor = ArgumentCaptor.forClass(EventDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateEvent).accept(eventCaptor.capture(), any());

		ArgumentCaptor<EventParticipantDto> eventParticipantCaptor = ArgumentCaptor.forClass(EventParticipantDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateEventParticipant).accept(eventParticipantCaptor.capture(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		assertThat(eventParticipantCaptor.getValue().getEvent(), is(eventCaptor.getValue().toReference()));
		assertThat(eventParticipantCaptor.getValue().getPerson(), is(personCaptor.getValue()));
	}

	@Test
	public void testCreateEventParticipantCancel() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handleCreateEventParticipant).accept(any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateSampleAndPathogenTests, times(0)).handle(any(), any(), any(), any());
	}

	@Test
	public void testCreateEventAndCreateEventParticipantAndCreateSample() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		pickOrCreateEventResult.setNewEvent(true);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		ArgumentCaptor<EventParticipantDto> eventParticipantCaptor = ArgumentCaptor.forClass(EventParticipantDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateEventParticipant).accept(eventParticipantCaptor.capture(), any());

		doAnswer(invocation -> {
			SampleDto sample = invocation.getArgument(0);
			sample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> pathogenTests = invocation.getArgument(1);
			pathogenTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(sample, pathogenTests));
			return null;
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), any());

		LabMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED);
		labMessage.setSampleDateTime(new Date());
		labMessage.setSampleMaterial(SampleMaterial.BLOOD);

		TestReportDto testReport = TestReportDto.build();
		testReport.setTestType(PathogenTestType.CULTURE);
		testReport.setTestResult(PathogenTestResultType.NEGATIVE);
		labMessage.getTestReports().add(testReport);

		ProcessingResult<SampleAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));
		verify(handleCreateSampleAndPathogenTests).handle(argThat(sample -> {
			assertThat(sample.getAssociatedEventParticipant(), is(eventParticipantCaptor.getValue().toReference()));
			assertThat(sample.getSampleDateTime(), is(labMessage.getSampleDateTime()));
			assertThat(sample.getSampleMaterial(), is(SampleMaterial.BLOOD));
			assertThat(sample.getReportingUser(), is(user.toReference()));

			return true;
		}), argThat(pathogenTests -> {
			assertThat(pathogenTests, hasSize(1));

			assertThat(pathogenTests.get(0).getTestType(), is(testReport.getTestType()));
			assertThat(pathogenTests.get(0).getTestResult(), is(testReport.getTestResult()));

			return true;
		}), argThat(entityCreated -> {
			assertThat(entityCreated, is(true));

			return true;
		}), any());

		// test that changes in handler are kept
		assertThat(result.getData().getSample().getSamplingReason(), is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	public void testPickExistingEventAndCreateEventParticipant() throws ExecutionException, InterruptedException {

		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		EventIndexDto selectedEvent = new EventIndexDto();
		selectedEvent.setUuid(event.getUuid());
		pickOrCreateEventResult.setEvent(selectedEvent);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		ArgumentCaptor<EventParticipantDto> eventParticipantCaptor = ArgumentCaptor.forClass(EventParticipantDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateEventParticipant).accept(eventParticipantCaptor.capture(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		assertThat(eventParticipantCaptor.getValue().getEvent(), is(event.toReference()));
		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), any(), any());
		verify(handleCreateSampleAndPathogenTests).handle(argThat(s -> {
			assertThat(s.getAssociatedEventParticipant(), is(eventParticipantCaptor.getValue().toReference()));

			return true;
		}), any(), argThat(entityCreated -> {
			assertThat(entityCreated, is(true));

			return true;
		}), any());
	}

	@Test
	public void testPickExistingEventWithExistingEventParticipantAndCreateSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		EventIndexDto selectedEvent = new EventIndexDto();
		selectedEvent.setUuid(event.getUuid());
		pickOrCreateEventResult.setEvent(selectedEvent);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		when(confirmPickExistingEventParticipant.get()).thenReturn(CompletableFuture.completedFuture(true));

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), argThat(entityCreated -> {
			assertThat(entityCreated, is(false));

			return true;
		}), any());
		assertThat(result.getData().getSample().getAssociatedEventParticipant(), is(eventParticipant.toReference()));
	}

	@Test
	public void testPickExistingEventWithExistingEventParticipantAndCancel() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		doAnswer((invocation) -> {
			// pick event
			PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
			EventIndexDto selectedEvent = new EventIndexDto();
			selectedEvent.setUuid(event.getUuid());
			pickOrCreateEventResult.setEvent(selectedEvent);

			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		when(confirmPickExistingEventParticipant.get())
			// don't pick event participant
			.thenReturn(CompletableFuture.completedFuture(false))
			// pick event participant for the second time
			.thenReturn(CompletableFuture.completedFuture(true));

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		// cancel on using event participant should return to event selection
		verify(handlePickOrCreateEvent, times(2)).accept(any());
		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), any(), any());
		assertThat(result.getData().getSample().getAssociatedEventParticipant(), is(eventParticipant.toReference()));
	}

	@Test
	public void testPickExistingEventWithExistingEventParticipantAndCancelThenCreateEvent() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		doAnswer((invocation) -> {
			// pick event for the  first time
			PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
			EventIndexDto selectedEvent = new EventIndexDto();
			selectedEvent.setUuid(event.getUuid());
			pickOrCreateEventResult.setEvent(selectedEvent);

			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).doAnswer((invocation) -> {
			//create event for the second time
			PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
			pickOrCreateEventResult.setNewEvent(true);

			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		when(confirmPickExistingEventParticipant.get())
			// don't pick event participant
			.thenReturn(CompletableFuture.completedFuture(false));

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		// cancel on using event participant should return to event selection
		verify(handlePickOrCreateEvent, times(2)).accept(any());
		// create event is picked at the end
		verify(handleCreateEvent, times(1)).accept(any(), any());
		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), any(), any());
		assertThat(result.getData().getSample().getAssociatedEventParticipant(), is(not(eventParticipant.toReference())));
	}

	@Test
	public void testPickExistingEventAndPickExistingEventParticipantAndPickExistingSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		EventIndexDto selectedEvent = new EventIndexDto();
		selectedEvent.setUuid(event.getUuid());
		pickOrCreateEventResult.setEvent(selectedEvent);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		when(confirmPickExistingEventParticipant.get()).thenReturn(CompletableFuture.completedFuture(true));

		SampleDto sample = creator.createSample(
			eventParticipant.toReference(),
			new Date(),
			new Date(),
			user.toReference(),
			SampleMaterial.CRUST,
			rdcf.facility.toReference(),
			s -> {
				s.setLabSampleID("test-lab-sample-id");
				s.setSpecimenCondition(SpecimenCondition.ADEQUATE);
			});
		doAnswer((invocation) -> {
			PickOrCreateSampleResult result = new PickOrCreateSampleResult();
			List<SampleDto> samples = invocation.getArgument(0);
			result.setSample(samples.get(0));

			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).accept(any(), any());

		ArgumentCaptor<SampleDto> editedSampleCaptor = ArgumentCaptor.forClass(SampleDto.class);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<PathogenTestDto>> editedTestsCaptor = ArgumentCaptor.forClass(List.class);
		doAnswer((invocation) -> {
			SampleDto editedSample = invocation.getArgument(0);
			editedSample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> editedTests = invocation.getArgument(1);
			editedTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(editedSample, editedTests));
			return null;
		}).when(handleEditSample).handle(editedSampleCaptor.capture(), editedTestsCaptor.capture(), any());

		LabMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED, l -> {
			l.setLabSampleId(sample.getLabSampleID());
			l.setSampleMaterial(SampleMaterial.RECTAL_SWAB);
			l.setSpecimenCondition(SpecimenCondition.NOT_ADEQUATE);
		});

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestType(PathogenTestType.CULTURE);
		testReport1.setTestResult(PathogenTestResultType.NEGATIVE);
		labMessage.getTestReports().add(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.PENDING);
		labMessage.getTestReports().add(testReport2);

		ProcessingResult<SampleAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleEditSample, times(1)).handle(any(), any(), any());
		assertThat(result.getData().getSample(), is(sample));
		// sample not changed when calling edit handler
		assertThat(editedSampleCaptor.getValue().getSampleMaterial(), is(SampleMaterial.CRUST));
		assertThat(editedSampleCaptor.getValue().getSpecimenCondition(), is(SpecimenCondition.ADEQUATE));

		// test reports converted to pathogen test
		assertThat(editedTestsCaptor.getValue(), hasSize(2));
		assertThat(editedTestsCaptor.getValue().get(0).getTestType(), is(testReport1.getTestType()));
		assertThat(editedTestsCaptor.getValue().get(0).getTestResult(), is(testReport1.getTestResult()));
		assertThat(editedTestsCaptor.getValue().get(1).getTestResult(), is(testReport2.getTestResult()));

		// test that changes in handler are kept
		assertThat(result.getData().getSample().getSamplingReason(), is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingCaseAndCreateSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		doAnswer(invocation -> {
			List<CaseSelectionDto> cases = invocation.getArgument(0);

			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setCaze(cases.get(0));

			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		PickOrCreateSampleResult pickOrCreateSampleResult = new PickOrCreateSampleResult();
		pickOrCreateSampleResult.setNewSample(true);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateSampleResult);
			return null;
		}).when(handlePickOrCreateSample).accept(any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), argThat(entityCreated -> {
			assertThat(entityCreated, is(false));

			return true;
		}), any());
		assertThat(result.getData().getSample().getAssociatedCase(), is(caze.toReference()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingCaseAndPickExistingSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		doAnswer(invocation -> {
			List<CaseSelectionDto> cases = invocation.getArgument(0);

			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setCaze(cases.get(0));

			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSampleMaterial(SampleMaterial.CRUST);
			s.setLabSampleID("test-lab-sample-id");
			s.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		});

		doAnswer((invocation) -> {
			List<SampleDto> samples = invocation.getArgument(0);
			PickOrCreateSampleResult result = new PickOrCreateSampleResult();
			result.setSample(samples.get(0));

			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).accept(any(), any());

		ArgumentCaptor<SampleDto> editedSampleCaptor = ArgumentCaptor.forClass(SampleDto.class);
		ArgumentCaptor<List<PathogenTestDto>> editedTestsCaptor = ArgumentCaptor.forClass(List.class);
		doAnswer((invocation) -> {
			SampleDto editedSample = invocation.getArgument(0);
			editedSample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> editedTests = invocation.getArgument(1);
			editedTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(editedSample, editedTests));
			return null;
		}).when(handleEditSample).handle(editedSampleCaptor.capture(), editedTestsCaptor.capture(), any());

		LabMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED, l -> {
			l.setLabSampleId(sample.getLabSampleID());
			l.setSampleMaterial(SampleMaterial.RECTAL_SWAB);
			l.setSpecimenCondition(SpecimenCondition.NOT_ADEQUATE);
		});

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestType(PathogenTestType.CULTURE);
		testReport1.setTestResult(PathogenTestResultType.NEGATIVE);
		labMessage.getTestReports().add(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.PENDING);
		labMessage.getTestReports().add(testReport2);

		ProcessingResult<SampleAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleEditSample, times(1)).handle(any(), any(), any());
		assertThat(result.getData().getSample(), is(sample));
		// sample not changed when calling edit handler
		assertThat(editedSampleCaptor.getValue().getSampleMaterial(), is(SampleMaterial.CRUST));
		assertThat(editedSampleCaptor.getValue().getSpecimenCondition(), is(SpecimenCondition.ADEQUATE));

		// test reports converted to pathogen test
		assertThat(editedTestsCaptor.getValue(), hasSize(2));
		assertThat(editedTestsCaptor.getValue().get(0).getTestType(), is(testReport1.getTestType()));
		assertThat(editedTestsCaptor.getValue().get(0).getTestResult(), is(testReport1.getTestResult()));
		assertThat(editedTestsCaptor.getValue().get(1).getTestResult(), is(testReport2.getTestResult()));

		// test that changes in handler are kept
		assertThat(result.getData().getSample().getSamplingReason(), is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingContactAndCreateSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		ContactDto contact = creator.createContact(user.toReference(), person.toReference(), Disease.CORONAVIRUS, rdcf);
		doAnswer(invocation -> {
			List<SimilarContactDto> contacts = invocation.getArgument(1);

			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setContact(contacts.get(0));

			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		PickOrCreateSampleResult pickOrCreateSampleResult = new PickOrCreateSampleResult();
		pickOrCreateSampleResult.setNewSample(true);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateSampleResult);
			return null;
		}).when(handlePickOrCreateSample).accept(any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), argThat(entityCreated -> {
			assertThat(entityCreated, is(false));

			return true;
		}), any());
		assertThat(result.getData().getSample().getAssociatedContact(), is(contact.toReference()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingContactAndPickExistingSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		ContactDto contact = creator.createContact(user.toReference(), person.toReference(), Disease.CORONAVIRUS, rdcf);
		doAnswer(invocation -> {
			List<SimilarContactDto> contacts = invocation.getArgument(1);

			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setContact(contacts.get(0));

			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		SampleDto sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility.toReference(), null);

		doAnswer((invocation) -> {
			List<SampleDto> samples = invocation.getArgument(0);
			PickOrCreateSampleResult result = new PickOrCreateSampleResult();
			result.setSample(samples.get(0));

			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).accept(any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(new SampleAndPathogenTests(invocation.getArgument(0), invocation.getArgument(1)));
			return null;
		}).when(handleEditSample).handle(any(), any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		verify(handleEditSample, times(1)).handle(any(), any(), any());
		assertThat(result.getData().getSample(), is(sample));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingEventParticipantAndCreateSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		doAnswer(invocation -> {
			List<SimilarEventParticipantDto> eventParticipants = invocation.getArgument(2);

			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setEventParticipant(eventParticipants.get(0));

			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		PickOrCreateSampleResult pickOrCreateSampleResult = new PickOrCreateSampleResult();
		pickOrCreateSampleResult.setNewSample(true);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateSampleResult);
			return null;
		}).when(handlePickOrCreateSample).accept(any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), argThat(entityCreated -> {
			assertThat(entityCreated, is(false));

			return true;
		}), any());
		assertThat(result.getData().getSample().getAssociatedEventParticipant(), is(eventParticipant.toReference()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingEventParticipantAndPickExistingSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		doAnswer(invocation -> {
			List<SimilarEventParticipantDto> eventParticipants = invocation.getArgument(2);

			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setEventParticipant(eventParticipants.get(0));

			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		SampleDto sample = creator.createSample(
			eventParticipant.toReference(),
			new Date(),
			new Date(),
			user.toReference(),
			SampleMaterial.CRUST,
			rdcf.facility.toReference(),
			null);
		doAnswer((invocation) -> {
			List<SampleDto> samples = invocation.getArgument(0);
			PickOrCreateSampleResult result = new PickOrCreateSampleResult();
			result.setSample(samples.get(0));

			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).accept(any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(new SampleAndPathogenTests(invocation.getArgument(0), invocation.getArgument(1)));
			return null;
		}).when(handleEditSample).handle(any(), any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		verify(handleEditSample, times(1)).handle(any(), any(), any());
		assertThat(result.getData().getSample(), is(sample));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickOrCreateSampleCancel() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		doAnswer(invocation -> {
			List<CaseSelectionDto> cases = invocation.getArgument(0);

			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setCaze(cases.get(0));

			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, null);

		doAnswer((invocation) -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handlePickOrCreateSample).accept(any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEditSampleCancel() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		doAnswer(invocation -> {
			List<CaseSelectionDto> cases = invocation.getArgument(0);

			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setCaze(cases.get(0));

			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, null);

		PickOrCreateSampleResult pickOrCreateSampleResult = new PickOrCreateSampleResult();
		pickOrCreateSampleResult.setSample(sample);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateSampleResult);
			return null;
		}).when(handlePickOrCreateSample).accept(any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handleEditSample).handle(any(), any(), any());

		ProcessingResult<SampleAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", LabMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
	}

	private ProcessingResult<SampleAndPathogenTests> runFlow(LabMessageDto labMessage) throws ExecutionException, InterruptedException {

		return flow.run(labMessage, relatedLabMessageHandler).toCompletableFuture().get();
	}

	private LabMessageDto createLabMessage(Disease disease, String reportId, LabMessageStatus status) {
		return createLabMessage(disease, reportId, status, null);
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

	@SuppressWarnings("unchecked")
	@NotNull
	private Answer<?> answerPickOrCreateEntry(PickOrCreateEntryResult pickOrCreateEntryResult) {
		return invocation -> {
			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).done(pickOrCreateEntryResult);
			return null;
		};
	}

	@SuppressWarnings("unchecked")
	private <T> HandlerCallback<T> getCallbackParam(InvocationOnMock invocation) {
		Object[] arguments = invocation.getArguments();
		return (HandlerCallback<T>) arguments[arguments.length - 1];
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

	private interface PickOrCreateEntryHandler {

		void handle(
			List<CaseSelectionDto> similarCases,
			List<SimilarContactDto> similarContacts,
			List<SimilarEventParticipantDto> similarEventParticipants,
			HandlerCallback<PickOrCreateEntryResult> callback);
	}

	private interface EditSampleHandler {

		void handle(SampleDto sample, List<PathogenTestDto> pathogenTests, HandlerCallback<SampleAndPathogenTests> callback);
	}

	private interface CreateSampleAndPathogenTestHandler {

		CompletionStage<SampleAndPathogenTests> handle(
			SampleDto sample,
			List<PathogenTestDto> pathogenTests,
			Boolean entityCreated,
			HandlerCallback<SampleAndPathogenTests> callback);
	}

	private interface EntityCreationHandler<T> {

		Object handle(T entity, PersonDto person, HandlerCallback<?> callback);
	}
}
