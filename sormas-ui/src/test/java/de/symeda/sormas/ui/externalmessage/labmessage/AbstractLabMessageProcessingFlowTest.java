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

package de.symeda.sormas.ui.externalmessage.labmessage;

import static de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus.CANCELED;
import static de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus.CANCELED_WITH_CORRECTIONS;
import static de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus.DONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
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
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.AbstractProcessingFlow.HandlerCallback;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractLabMessageProcessingFlow;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractRelatedLabMessageHandler;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractRelatedLabMessageHandler.HandlerResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractRelatedLabMessageHandler.HandlerResultStatus;
import de.symeda.sormas.api.externalmessage.processing.labmessage.PickOrCreateEventResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.PickOrCreateSampleResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.RelatedSamplesReportsAndPathogenTests;
import de.symeda.sormas.api.externalmessage.processing.labmessage.SampleAndPathogenTests;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplingReason;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.ui.AbstractUiBeanTest;

public class AbstractLabMessageProcessingFlowTest extends AbstractUiBeanTest {

	private AbstractLabMessageProcessingFlow flow;

	private Supplier<CompletionStage<Boolean>> missingDiseaseHandler;

	/**
	 * Needed, because cdi-test InvocationTargetManager.onMockCreated doesn't allow multiple mocks for the same (generic) class
	 */
	private interface RelatedForwardedMessageHandler extends Supplier<CompletionStage<Boolean>> {
	}

	private RelatedForwardedMessageHandler relatedForwardedMessagesHandler;
	private AbstractRelatedLabMessageHandler relatedLabMessageHandler;
	private BiFunction<PersonDto, HandlerCallback<PersonDto>, Void> handlePickOrCreatePerson;
	private PickOrCreateEntryHandler handlePickOrCreateEntry;
	private CreateCaseHandler handleCreateCase;
	private CreateSampleAndPathogenTestHandler handleCreateSampleAndPathogenTests;
	private CreateContactHandler handleCreateContact;
	private Consumer<HandlerCallback<PickOrCreateEventResult>> handlePickOrCreateEvent;

	private interface CreateEventHandler extends BiConsumer<EventDto, HandlerCallback<EventDto>> {
	}

	private CreateEventHandler handleCreateEvent;

	private interface CreateEventParticipantHandler extends BiConsumer<EventParticipantDto, HandlerCallback<EventParticipantDto>> {
	}

	private CreateEventParticipantHandler handleCreateEventParticipant;

	private interface ConfirmPickExistingEventParticipantHandler extends Supplier<CompletionStage<Boolean>> {
	}

	private ConfirmPickExistingEventParticipantHandler confirmPickExistingEventParticipantHandler;

	private interface MultipleSamplesConfirmationHandler extends Supplier<CompletionStage<Boolean>> {
	}

	private MultipleSamplesConfirmationHandler multipleSamplesConfirmationHandler;
	private PickOrCreateSampleHandler handlePickOrCreateSample;
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

		relatedForwardedMessagesHandler = Mockito.mock(RelatedForwardedMessageHandler.class);
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

		handleCreateCase = Mockito.mock(CreateCaseHandler.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateCase).handle(any(), any(), any());

		handleCreateSampleAndPathogenTests = Mockito.mock(CreateSampleAndPathogenTestHandler.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(new SampleAndPathogenTests(invocation.getArgument(0), invocation.getArgument(1)));
			return null;
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), any(), any());

		handleCreateContact = Mockito.mock(CreateContactHandler.class);
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

		handleCreateEvent = Mockito.mock(CreateEventHandler.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateEvent).accept(any(), any());

		handleCreateEventParticipant = Mockito.mock(CreateEventParticipantHandler.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateEventParticipant).accept(any(), any());

		handlePickOrCreateSample = Mockito.mock(PickOrCreateSampleHandler.class);
		PickOrCreateSampleResult pickOrCreateSampleResult = new PickOrCreateSampleResult();
		pickOrCreateSampleResult.setNewSample(true);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateSampleResult);
			return null;
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		confirmPickExistingEventParticipantHandler = Mockito.mock(ConfirmPickExistingEventParticipantHandler.class);
		when(confirmPickExistingEventParticipantHandler.get()).thenReturn(CompletableFuture.completedFuture(true));
		multipleSamplesConfirmationHandler = Mockito.mock(MultipleSamplesConfirmationHandler.class);
		when(multipleSamplesConfirmationHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		handleEditSample = Mockito.mock(EditSampleHandler.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(new SampleAndPathogenTests(invocation.getArgument(0), invocation.getArgument(1)));
			return null;
		}).when(handleEditSample).handle(any(), any(), any(), any());

		rdcf = creator.createRDCF();
		user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		country = new CountryReferenceDto(DataHelper.createUuid(), "de-DE");
		flow = new AbstractLabMessageProcessingFlow(
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
			},
			country) {

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
				ExternalMessageDto externalMessageDto,
				HandlerCallback<PickOrCreateEntryResult> callback) {
				handlePickOrCreateEntry.handle(similarCases, similarContacts, similarEventParticipants, callback);
			}

			@Override
			protected void handleCreateCase(
				CaseDataDto caze,
				PersonDto person,
				ExternalMessageDto labMessage,
				HandlerCallback<CaseDataDto> callback) {
				handleCreateCase.handle(caze, person, callback);
			}

			@Override
			public CompletionStage<Boolean> handleMultipleSampleConfirmation() {
				return multipleSamplesConfirmationHandler.get();
			}

			@Override
			protected void handleCreateSampleAndPathogenTests(
				SampleDto sample,
				List<PathogenTestDto> pathogenTests,
				Disease disease,
				ExternalMessageDto labMessage,
				boolean entityCreated,
				boolean lastSample,
				HandlerCallback<SampleAndPathogenTests> callback) {
				handleCreateSampleAndPathogenTests.handle(sample, pathogenTests, entityCreated, lastSample, callback);
			}

			@Override
			protected void handleCreateContact(
				ContactDto contact,
				PersonDto person,
				ExternalMessageDto labMessage,
				HandlerCallback<ContactDto> callback) {
				handleCreateContact.handle(contact, person, callback);
			}

			@Override
			protected void handlePickOrCreateEvent(ExternalMessageDto labMessage, HandlerCallback<PickOrCreateEventResult> callback) {
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
				ExternalMessageDto labMessage,
				HandlerCallback<EventParticipantDto> callback) {
				handleCreateEventParticipant.accept(eventParticipant, callback);
			}

			@Override
			protected CompletionStage<Boolean> confirmPickExistingEventParticipant() {
				return confirmPickExistingEventParticipantHandler.get();
			}

			@Override
			protected void handlePickOrCreateSample(
				List<SampleDto> similarSamples,
				List<SampleDto> otherSamples,
				ExternalMessageDto externalMessageDto,
				int sampleReportIndex,
				HandlerCallback<PickOrCreateSampleResult> callback) {
				handlePickOrCreateSample.handle(similarSamples, otherSamples, callback);
			}

			@Override
			protected void handleEditSample(
				SampleDto sample,
				List<PathogenTestDto> newPathogenTests,
				ExternalMessageDto labMessage,
				boolean lastSample,
				HandlerCallback<SampleAndPathogenTests> callback) {
				handleEditSample.handle(sample, newPathogenTests, lastSample, callback);
			}
		};
	}

	@Test
	public void testCreateLabMessage() throws ExecutionException, InterruptedException {

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(createLabMessage(null, "", ExternalMessageStatus.UNPROCESSED));
		assertThat(result.getStatus(), is(DONE));
	}

	@Test
	public void testHandleMissingDisease() throws ExecutionException, InterruptedException {

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(createLabMessage(null, "", ExternalMessageStatus.UNPROCESSED));
		assertThat(result.getStatus(), is(DONE));
		Mockito.verify(missingDiseaseHandler, Mockito.times(1)).get();
	}

	@Test
	public void testHandleMissingDiseaseNotNeeded() throws ExecutionException, InterruptedException {

		runFlow(createLabMessage(Disease.CORONAVIRUS, "", ExternalMessageStatus.UNPROCESSED));
		Mockito.verify(missingDiseaseHandler, Mockito.times(0)).get();
	}

	@Test
	public void testHandleMissingDiseaseCancel() throws ExecutionException, InterruptedException {

		when(missingDiseaseHandler.get()).thenReturn(CompletableFuture.completedFuture(false));

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(createLabMessage(null, "", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(relatedForwardedMessagesHandler, times(0)).get();
		verify(relatedLabMessageHandler, times(0)).handle(any());
	}

	@Test
	public void testHandleRelatedForwardedMessages() throws ExecutionException, InterruptedException {

		FacadeProvider.getExternalMessageFacade().save(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.FORWARDED));

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		verify(relatedForwardedMessagesHandler, times(1)).get();
	}

	@Test
	public void testHandleRelatedForwardedMessagesNotNeeded() throws ExecutionException, InterruptedException {

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		verify(relatedForwardedMessagesHandler, times(0)).get();
	}

	@Test
	public void testHandleRelatedForwardedMessagesCancel() throws ExecutionException, InterruptedException {

		FacadeProvider.getExternalMessageFacade().save(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.FORWARDED));

		when(relatedForwardedMessagesHandler.get()).thenReturn(CompletableFuture.completedFuture(false));

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));
		assertThat(result.getData().getRelatedSampleReportsWithSamples().values(), hasItem(sample));
		verify(relatedLabMessageHandler, times(1)).handle(any());
		verify(handlePickOrCreatePerson, times(0)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedLabmessagesCancel() throws ExecutionException, InterruptedException {

		when(relatedLabMessageHandler.handle(any()))
			.thenReturn(CompletableFuture.completedFuture(new HandlerResult(HandlerResultStatus.CANCELED, null)));

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handlePickOrCreatePerson, times(0)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedLabmessagesCancelWithChanges() throws ExecutionException, InterruptedException {

		when(relatedLabMessageHandler.handle(any()))
			.thenReturn(CompletableFuture.completedFuture(new HandlerResult(HandlerResultStatus.CANCELED_WITH_UPDATES, null)));

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED_WITH_CORRECTIONS));
		verify(handlePickOrCreatePerson, times(0)).apply(any(), any());
	}

	@Test
	public void testHandleRelatedLabmessagesContinue() throws ExecutionException, InterruptedException {

		when(relatedLabMessageHandler.handle(any()))
			.thenReturn(CompletableFuture.completedFuture(new HandlerResult(HandlerResultStatus.CONTINUE, null)));

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

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

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		labMessage.setPersonFirstName("Ftest");
		labMessage.setPersonLastName("Ltest");
		labMessage.setPersonSex(Sex.UNKNOWN);
		labMessage.setPersonStreet("Test st.");
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateContact, times(0)).handle(any(), any(), any());
		verify(handleCreateSampleAndPathogenTests, times(0)).handle(any(), any(), any(), any(), any());
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
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), eq(true), any());

		SampleReportDto sampleReport = SampleReportDto.build();
		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		labMessage.addSampleReport(sampleReport);
		sampleReport.setSampleDateTime(new Date());
		sampleReport.setSampleMaterial(SampleMaterial.BLOOD);

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestType(PathogenTestType.CULTURE);
		testReport1.setTestResult(PathogenTestResultType.NEGATIVE);
		sampleReport.addTestReport(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.PENDING);
		sampleReport.addTestReport(testReport2);

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));
		verify(handleCreateSampleAndPathogenTests).handle(argThat(sample -> {
			assertThat(sample.getAssociatedCase(), is(caseCaptor.getValue().toReference()));
			assertThat(sample.getSampleDateTime(), is(labMessage.getSampleReports().get(0).getSampleDateTime()));
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
		}), argThat(lastSample -> {
			assertThat(lastSample, is(Boolean.TRUE));

			return true;
		}), any());

		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateCaseAndCreateTwoSamples() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewCase(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		ArgumentCaptor<CaseDataDto> caseCaptor = ArgumentCaptor.forClass(CaseDataDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateCase).handle(caseCaptor.capture(), any(), any());

		ArgumentCaptor<SampleDto> createdSampleArgs = ArgumentCaptor.forClass(SampleDto.class);
		ArgumentCaptor<List<PathogenTestDto>> createdPathogenTestsArgs = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Boolean> entityCreatedArgs = ArgumentCaptor.forClass(Boolean.class);
		ArgumentCaptor<Boolean> lastSampleArgs = ArgumentCaptor.forClass(Boolean.class);

		doAnswer((invocation) -> {
			SampleDto sample = invocation.getArgument(0);
			sample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> pathogenTests = invocation.getArgument(1);
			pathogenTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(sample, pathogenTests));
			return null;
		}).when(handleCreateSampleAndPathogenTests)
			.handle(createdSampleArgs.capture(), createdPathogenTestsArgs.capture(), entityCreatedArgs.capture(), lastSampleArgs.capture(), any());

		ExternalMessageDto labMessage = createStandardLabMessageWithTwoSampleReports();

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleCreateSampleAndPathogenTests, times(2)).handle(any(), any(), any(), any(), any());

		verifyStandardCreationOfTwoSamples(labMessage, createdSampleArgs, createdPathogenTestsArgs, entityCreatedArgs, lastSampleArgs, true);

		for (SampleDto argSample : createdSampleArgs.getAllValues()) {
			assertThat(argSample.getAssociatedCase(), is(caseCaptor.getValue().toReference()));
		}

		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(labMessage.getSampleReports().get(0)).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(labMessage.getSampleReports().get(1)).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(2).getTestResultText(), is("Dummy test result text"));
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
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), any(), any());

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		sampleReport.setSampleDateTime(new Date());
		sampleReport.setSampleMaterial(SampleMaterial.BLOOD);
		labMessage.addSampleReport(sampleReport);

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateCase, times(0)).handle(any(), any(), any());
		verify(handleCreateSampleAndPathogenTests, times(0)).handle(any(), any(), any(), any(), any());
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
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), eq(true), any());

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		sampleReport.setSampleDateTime(new Date());
		sampleReport.setSampleMaterial(SampleMaterial.BLOOD);
		labMessage.addSampleReport(sampleReport);

		TestReportDto testReport = TestReportDto.build();
		testReport.setTestType(PathogenTestType.CULTURE);
		testReport.setTestResult(PathogenTestResultType.NEGATIVE);
		labMessage.getSampleReports().get(0).addTestReport(testReport);

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));
		verify(handleCreateSampleAndPathogenTests).handle(argThat(sample -> {
			assertThat(sample.getAssociatedContact(), is(contactCaptor.getValue().toReference()));
			assertThat(sample.getSampleDateTime(), is(labMessage.getSampleReports().get(0).getSampleDateTime()));
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
		}), argThat(lastSample -> {
			assertThat(lastSample, is(true));

			return true;
		}), any());

		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	public void testCreateContactAndCreateTwoSamples() throws ExecutionException, InterruptedException {

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewContact(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		ArgumentCaptor<ContactDto> contactCaptor = ArgumentCaptor.forClass(ContactDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateContact).handle(contactCaptor.capture(), any(), any());

		ArgumentCaptor<SampleDto> createdSampleArgs = ArgumentCaptor.forClass(SampleDto.class);
		ArgumentCaptor<List<PathogenTestDto>> createdPathogenTestsArgs = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Boolean> entityCreatedArgs = ArgumentCaptor.forClass(Boolean.class);
		ArgumentCaptor<Boolean> lastSampleArgs = ArgumentCaptor.forClass(Boolean.class);

		doAnswer((invocation) -> {
			SampleDto sample = invocation.getArgument(0);
			sample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> pathogenTests = invocation.getArgument(1);
			pathogenTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(sample, pathogenTests));
			return null;
		}).when(handleCreateSampleAndPathogenTests)
			.handle(createdSampleArgs.capture(), createdPathogenTestsArgs.capture(), entityCreatedArgs.capture(), lastSampleArgs.capture(), any());

		ExternalMessageDto labMessage = createStandardLabMessageWithTwoSampleReports();

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verifyStandardCreationOfTwoSamples(labMessage, createdSampleArgs, createdPathogenTestsArgs, entityCreatedArgs, lastSampleArgs, true);

		for (SampleDto argSample : createdSampleArgs.getAllValues()) {
			assertThat(argSample.getAssociatedContact(), is(contactCaptor.getValue().toReference()));
		}

		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(labMessage.getSampleReports().get(0)).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(labMessage.getSampleReports().get(1)).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(2).getTestResultText(), is("Dummy test result text"));

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateEventParticipant, times(0)).accept(any(), any());
		verify(handleCreateSampleAndPathogenTests, times(0)).handle(any(), any(), any(), any(), any());
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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateEvent, times(0)).accept(any(), any());
		verify(handleCreateEventParticipant, times(0)).accept(any(), any());
		verify(handleCreateSampleAndPathogenTests, times(0)).handle(any(), any(), any(), any(), any());
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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

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

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
		verify(handleCreateSampleAndPathogenTests, times(0)).handle(any(), any(), any(), any(), any());
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
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), eq(true), any());

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		sampleReport.setSampleDateTime(new Date());
		sampleReport.setSampleMaterial(SampleMaterial.BLOOD);
		labMessage.addSampleReport(sampleReport);

		TestReportDto testReport = TestReportDto.build();
		testReport.setTestType(PathogenTestType.CULTURE);
		testReport.setTestResult(PathogenTestResultType.NEGATIVE);
		sampleReport.addTestReport(testReport);

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));
		verify(handleCreateSampleAndPathogenTests).handle(argThat(sample -> {
			assertThat(sample.getAssociatedEventParticipant(), is(eventParticipantCaptor.getValue().toReference()));
			assertThat(sample.getSampleDateTime(), is(labMessage.getSampleReports().get(0).getSampleDateTime()));
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
		}), argThat(lastSample -> {
			assertThat(lastSample, is(true));

			return true;
		}), any());

		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	public void testCreateEventAndCreateEventParticipantAndCreateTwoSamples() throws ExecutionException, InterruptedException {

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

		ArgumentCaptor<SampleDto> createdSampleArgs = ArgumentCaptor.forClass(SampleDto.class);
		ArgumentCaptor<List<PathogenTestDto>> createdPathogenTestsArgs = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Boolean> entityCreatedArgs = ArgumentCaptor.forClass(Boolean.class);
		ArgumentCaptor<Boolean> lastSampleArgs = ArgumentCaptor.forClass(Boolean.class);

		doAnswer(invocation -> {
			SampleDto sample = invocation.getArgument(0);
			sample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> pathogenTests = invocation.getArgument(1);
			pathogenTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(sample, pathogenTests));
			return null;
		}).when(handleCreateSampleAndPathogenTests)
			.handle(createdSampleArgs.capture(), createdPathogenTestsArgs.capture(), entityCreatedArgs.capture(), lastSampleArgs.capture(), any());

		ExternalMessageDto labMessage = createStandardLabMessageWithTwoSampleReports();

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verifyStandardCreationOfTwoSamples(labMessage, createdSampleArgs, createdPathogenTestsArgs, entityCreatedArgs, lastSampleArgs, true);

		for (SampleDto argSample : createdSampleArgs.getAllValues()) {
			assertThat(argSample.getAssociatedEventParticipant(), is(eventParticipantCaptor.getValue().toReference()));
		}
		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(labMessage.getSampleReports().get(0)).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(labMessage.getSampleReports().get(1)).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(2).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	public void testPickExistingEventAndCreateEventParticipant() throws ExecutionException, InterruptedException {

		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS, rdcf);
		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		EventIndexDto selectedEvent = new EventIndexDto(event.getUuid());
		pickOrCreateEventResult.setEvent(selectedEvent);
		pickOrCreateEventResult.setNewEvent(false);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		ArgumentCaptor<EventParticipantDto> eventParticipantCaptor = ArgumentCaptor.forClass(EventParticipantDto.class);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(invocation.getArgument(0));
			return null;
		}).when(handleCreateEventParticipant).accept(eventParticipantCaptor.capture(), any());

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(DONE));

		assertThat(eventParticipantCaptor.getValue().getEvent(), is(event.toReference()));
		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), any(), any(), any());
		verify(handleCreateSampleAndPathogenTests).handle(argThat(s -> {
			assertThat(s.getAssociatedEventParticipant(), is(eventParticipantCaptor.getValue().toReference()));

			return true;
		}), any(), argThat(entityCreated -> {
			assertThat(entityCreated, is(true));

			return true;
		}), argThat(lastSample -> {
			assertThat(lastSample, is(true));

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

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS, rdcf);
		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		EventIndexDto selectedEvent = new EventIndexDto(event.getUuid());
		pickOrCreateEventResult.setEvent(selectedEvent);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		when(confirmPickExistingEventParticipantHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		labMessage.addSampleReport(sampleReport);
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), argThat(entityCreated -> {
			assertThat(entityCreated, is(false));

			return true;
		}), argThat(lastSample -> {
			assertThat(lastSample, is(true));

			return true;
		}), any());
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getAssociatedEventParticipant(),
			is(eventParticipant.toReference()));
	}

	@Test
	public void testPickExistingEventWithExistingEventParticipantAndCancel() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS, rdcf);
		doAnswer((invocation) -> {
			// pick event
			PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
			EventIndexDto selectedEvent = new EventIndexDto(event.getUuid());
			pickOrCreateEventResult.setEvent(selectedEvent);

			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		when(confirmPickExistingEventParticipantHandler.get())
			// don't pick event participant
			.thenReturn(CompletableFuture.completedFuture(false))
			// pick event participant for the second time
			.thenReturn(CompletableFuture.completedFuture(true));

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		labMessage.addSampleReport(sampleReport);
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		// cancel on using event participant should return to event selection
		verify(handlePickOrCreateEvent, times(2)).accept(any());
		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), any(), any(), any());
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getAssociatedEventParticipant(),
			is(eventParticipant.toReference()));
	}

	@Test
	public void testPickExistingEventWithExistingEventParticipantAndCancelThenCreateEvent() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS, rdcf);
		doAnswer((invocation) -> {
			// pick event for the  first time
			PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
			EventIndexDto selectedEvent = new EventIndexDto(event.getUuid());
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
		when(confirmPickExistingEventParticipantHandler.get())
			// don't pick event participant
			.thenReturn(CompletableFuture.completedFuture(false));

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		labMessage.addSampleReport(sampleReport);
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		// cancel on using event participant should return to event selection
		verify(handlePickOrCreateEvent, times(2)).accept(any());
		// create event is picked at the end
		verify(handleCreateEvent, times(1)).accept(any(), any());
		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), any(), any(), any());
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getAssociatedEventParticipant(),
			is(not(eventParticipant.toReference())));
	}

	@Test
	public void testPickExistingEventAndPickExistingEventParticipantAndPickExistingSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS, rdcf);
		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		EventIndexDto selectedEvent = new EventIndexDto(event.getUuid());
		pickOrCreateEventResult.setEvent(selectedEvent);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		when(confirmPickExistingEventParticipantHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		SampleDto sample = creator
			.createSample(eventParticipant.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.CRUST, rdcf.facility, s -> {
				s.setLabSampleID("test-lab-sample-id");
				s.setSpecimenCondition(SpecimenCondition.ADEQUATE);
			});
		doAnswer((invocation) -> {
			PickOrCreateSampleResult result = new PickOrCreateSampleResult();
			List<SampleDto> samples = invocation.getArgument(0);
			result.setSample(samples.get(0));

			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

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
		}).when(handleEditSample).handle(editedSampleCaptor.capture(), editedTestsCaptor.capture(), any(), any());

		SampleReportDto sampleReport = SampleReportDto.build();
		sampleReport.setLabSampleId(sample.getLabSampleID());
		sampleReport.setSampleMaterial(SampleMaterial.RECTAL_SWAB);
		sampleReport.setSpecimenCondition(SpecimenCondition.NOT_ADEQUATE);

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED, l -> {
			l.addSampleReport(sampleReport);
		});

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestType(PathogenTestType.CULTURE);
		testReport1.setTestResult(PathogenTestResultType.NEGATIVE);
		sampleReport.addTestReport(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.PENDING);
		sampleReport.addTestReport(testReport2);

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleEditSample, times(1)).handle(any(), any(), eq(true), any());
		assertThat(result.getData().getRelatedSampleReportsWithSamples().get(sampleReport), is(sample));
		// sample not changed when calling edit handler
		assertThat(editedSampleCaptor.getValue().getSampleMaterial(), is(SampleMaterial.CRUST));
		assertThat(editedSampleCaptor.getValue().getSpecimenCondition(), is(SpecimenCondition.ADEQUATE));

		// test reports converted to pathogen test
		assertThat(editedTestsCaptor.getValue(), hasSize(2));
		assertThat(editedTestsCaptor.getValue().get(0).getTestType(), is(testReport1.getTestType()));
		assertThat(editedTestsCaptor.getValue().get(0).getTestResult(), is(testReport1.getTestResult()));
		assertThat(editedTestsCaptor.getValue().get(1).getTestResult(), is(testReport2.getTestResult()));

		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	public void testPickExistingEventAndPickExistingEventParticipantPickExistingSampleAndCreateSample()
		throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
		pickOrCreateEntryResult.setNewEventParticipant(true);
		doAnswer(answerPickOrCreateEntry(pickOrCreateEntryResult)).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS, rdcf);
		PickOrCreateEventResult pickOrCreateEventResult = new PickOrCreateEventResult();
		EventIndexDto selectedEvent = new EventIndexDto(event.getUuid());
		selectedEvent.setUuid(event.getUuid());
		pickOrCreateEventResult.setEvent(selectedEvent);
		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(pickOrCreateEventResult);
			return null;
		}).when(handlePickOrCreateEvent).accept(any());

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		when(confirmPickExistingEventParticipantHandler.get()).thenReturn(CompletableFuture.completedFuture(true));

		SampleDto sample = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSampleMaterial(SampleMaterial.CRUST);
			s.setLabSampleID("test-lab-sample-id");
			s.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		});

		AtomicBoolean firstInvocation = new AtomicBoolean(true);
		doAnswer((invocation) -> {
			PickOrCreateSampleResult result;
			if (firstInvocation.get()) {
				firstInvocation.set(false);
				List<SampleDto> samples = invocation.getArgument(0);
				result = new PickOrCreateSampleResult();
				result.setSample(samples.get(0));
			} else {
				result = new PickOrCreateSampleResult();
				result.setNewSample(true);
			}
			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		ArgumentCaptor<SampleDto> editedSampleCaptor = ArgumentCaptor.forClass(SampleDto.class);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<PathogenTestDto>> editedTestsCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Boolean> lastSampleArgsEdit = ArgumentCaptor.forClass(Boolean.class);

		doAnswer((invocation) -> {
			SampleDto editedSample = invocation.getArgument(0);
			editedSample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> editedTests = invocation.getArgument(1);
			editedTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(editedSample, editedTests));
			return null;
		}).when(handleEditSample).handle(editedSampleCaptor.capture(), editedTestsCaptor.capture(), lastSampleArgsEdit.capture(), any());

		ArgumentCaptor<SampleDto> createdSampleArgs = ArgumentCaptor.forClass(SampleDto.class);
		ArgumentCaptor<List<PathogenTestDto>> createdPathogenTestsArgs = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Boolean> entityCreatedArgs = ArgumentCaptor.forClass(Boolean.class);
		ArgumentCaptor<Boolean> lastSampleArgsCreate = ArgumentCaptor.forClass(Boolean.class);

		doAnswer((invocation) -> {
			SampleDto newSample = invocation.getArgument(0);
			newSample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> pathogenTests = invocation.getArgument(1);
			pathogenTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(newSample, pathogenTests));
			return null;
		}).when(handleCreateSampleAndPathogenTests)
			.handle(
				createdSampleArgs.capture(),
				createdPathogenTestsArgs.capture(),
				entityCreatedArgs.capture(),
				lastSampleArgsCreate.capture(),
				any());

		ExternalMessageDto standardLabMessage = createStandardLabMessageWithTwoSampleReports();

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(standardLabMessage);

		verifyStandardEditingAndCreationOfSample(
			standardLabMessage,
			editedSampleCaptor,
			editedTestsCaptor,
			lastSampleArgsEdit,
			createdSampleArgs,
			createdPathogenTestsArgs,
			entityCreatedArgs,
			lastSampleArgsCreate);

		assertThat(result.getStatus(), is(DONE));
		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(0)).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(1)).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(2).getTestResultText(), is("Dummy test result text"));
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
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		labMessage.addSampleReport(sampleReport);
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), argThat(entityCreated -> {
			assertThat(entityCreated, is(false));

			return true;
		}), argThat(lastSample -> {
			assertThat(lastSample, is(true));

			return true;
		}), any());
		assertThat(result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getAssociatedCase(), is(caze.toReference()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingCasePickExistingSimilarSampleAndCreateSample() throws ExecutionException, InterruptedException {

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

		AtomicBoolean firstInvocation = new AtomicBoolean(true);
		doAnswer((invocation) -> {
			PickOrCreateSampleResult result;
			if (firstInvocation.get()) {
				firstInvocation.set(false);
				List<SampleDto> samples = invocation.getArgument(0);
				result = new PickOrCreateSampleResult();
				result.setSample(samples.get(0));
			} else {
				result = new PickOrCreateSampleResult();
				result.setNewSample(true);
			}
			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		ArgumentCaptor<SampleDto> editedSampleCaptor = ArgumentCaptor.forClass(SampleDto.class);
		ArgumentCaptor<List<PathogenTestDto>> editedTestsCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Boolean> lastSampleArgsEdit = ArgumentCaptor.forClass(Boolean.class);

		doAnswer((invocation) -> {
			SampleDto editedSample = invocation.getArgument(0);
			editedSample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> editedTests = invocation.getArgument(1);
			editedTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(editedSample, editedTests));
			return null;
		}).when(handleEditSample).handle(editedSampleCaptor.capture(), editedTestsCaptor.capture(), lastSampleArgsEdit.capture(), any());

		ArgumentCaptor<SampleDto> createdSampleArgs = ArgumentCaptor.forClass(SampleDto.class);
		ArgumentCaptor<List<PathogenTestDto>> createdPathogenTestsArgs = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Boolean> entityCreatedArgs = ArgumentCaptor.forClass(Boolean.class);
		ArgumentCaptor<Boolean> lastSampleArgsCreate = ArgumentCaptor.forClass(Boolean.class);

		doAnswer((invocation) -> {
			SampleDto createdSample = invocation.getArgument(0);
			createdSample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> pathogenTests = invocation.getArgument(1);
			pathogenTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(sample, pathogenTests));
			return null;
		}).when(handleCreateSampleAndPathogenTests)
			.handle(
				createdSampleArgs.capture(),
				createdPathogenTestsArgs.capture(),
				entityCreatedArgs.capture(),
				lastSampleArgsCreate.capture(),
				any());

		ExternalMessageDto standardLabMessage = createStandardLabMessageWithTwoSampleReports();

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(standardLabMessage);

		assertThat(result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(0)), is(sample));
		verifyStandardEditingAndCreationOfSample(
			standardLabMessage,
			editedSampleCaptor,
			editedTestsCaptor,
			lastSampleArgsEdit,
			createdSampleArgs,
			createdPathogenTestsArgs,
			entityCreatedArgs,
			lastSampleArgsCreate);

		assertThat(result.getStatus(), is(DONE));
		assertThat(result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(0)), is(sample));

		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(0)).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(1)).getSampleMaterial(),
			is(SampleMaterial.CRUST));

		verify(multipleSamplesConfirmationHandler, times(1)).get();

		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(0)).getAssociatedCase(),
			is(caze.toReference()));
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(1)).getAssociatedCase(),
			is(caze.toReference()));

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingCaseAndPickExistingSimilarSample() throws ExecutionException, InterruptedException {

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
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		ArgumentCaptor<SampleDto> editedSampleCaptor = ArgumentCaptor.forClass(SampleDto.class);
		ArgumentCaptor<List<PathogenTestDto>> editedTestsCaptor = ArgumentCaptor.forClass(List.class);

		doAnswer((invocation) -> {
			SampleDto editedSample = invocation.getArgument(0);
			editedSample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> editedTests = invocation.getArgument(1);
			editedTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(editedSample, editedTests));
			return null;
		}).when(handleEditSample).handle(editedSampleCaptor.capture(), editedTestsCaptor.capture(), any(), any());

		SampleReportDto sampleReport = SampleReportDto.build();
		sampleReport.setLabSampleId(sample.getLabSampleID());
		sampleReport.setSampleMaterial(SampleMaterial.RECTAL_SWAB);
		sampleReport.setSpecimenCondition(SpecimenCondition.NOT_ADEQUATE);
		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED, l -> {
			l.addSampleReport(sampleReport);

		});

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestType(PathogenTestType.CULTURE);
		testReport1.setTestResult(PathogenTestResultType.NEGATIVE);
		sampleReport.addTestReport(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.PENDING);
		sampleReport.addTestReport(testReport2);

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleEditSample, times(1)).handle(any(), any(), eq(true), any());
		assertThat(result.getData().getRelatedSampleReportsWithSamples().get(sampleReport), is(sample));
		// sample not changed when calling edit handler
		assertThat(editedSampleCaptor.getValue().getSampleMaterial(), is(SampleMaterial.CRUST));
		assertThat(editedSampleCaptor.getValue().getSpecimenCondition(), is(SpecimenCondition.ADEQUATE));

		// test reports converted to pathogen test
		assertThat(editedTestsCaptor.getValue(), hasSize(2));
		assertThat(editedTestsCaptor.getValue().get(0).getTestType(), is(testReport1.getTestType()));
		assertThat(editedTestsCaptor.getValue().get(0).getTestResult(), is(testReport1.getTestResult()));
		assertThat(editedTestsCaptor.getValue().get(1).getTestResult(), is(testReport2.getTestResult()));

		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingCaseAndPickExistingOtherSample() throws ExecutionException, InterruptedException {

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

		SampleDto similarSample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSampleMaterial(SampleMaterial.CRUST);
			s.setLabSampleID("test-lab-sample-id");
			s.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		});

		SampleDto otherSample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSampleMaterial(SampleMaterial.CRUST);
		});

		doAnswer((invocation) -> {
			List<SampleDto> otherSamples = invocation.getArgument(1);
			PickOrCreateSampleResult result = new PickOrCreateSampleResult();
			result.setSample(otherSamples.get(0));

			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		doAnswer((invocation) -> {
			SampleDto editedSample = invocation.getArgument(0);
			List<PathogenTestDto> editedTests = invocation.getArgument(1);

			getCallbackParam(invocation).done(new SampleAndPathogenTests(editedSample, editedTests));
			return null;
		}).when(handleEditSample).handle(any(), any(), any(), any());

		SampleReportDto sampleReport = SampleReportDto.build();
		sampleReport.setLabSampleId(similarSample.getLabSampleID());
		ExternalMessageDto externalMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED, m -> {
			m.addSampleReport(sampleReport);
		});

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleEditSample, times(1)).handle(any(), any(), eq(true), any());
		assertThat(result.getData().getRelatedSampleReportsWithSamples().get(sampleReport), is(otherSample));
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
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		labMessage.addSampleReport(sampleReport);
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), argThat(entityCreated -> {
			assertThat(entityCreated, is(false));

			return true;
		}), argThat(lastSample -> {
			assertThat(lastSample, is(true));

			return true;
		}), any());
		assertThat(result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getAssociatedContact(), is(contact.toReference()));
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

		SampleDto sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);

		doAnswer((invocation) -> {
			List<SampleDto> samples = invocation.getArgument(0);
			PickOrCreateSampleResult result = new PickOrCreateSampleResult();
			result.setSample(samples.get(0));

			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(new SampleAndPathogenTests(invocation.getArgument(0), invocation.getArgument(1)));
			return null;
		}).when(handleEditSample).handle(any(), any(), any(), any());

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		labMessage.addSampleReport(sampleReport);
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleEditSample, times(1)).handle(any(), any(), eq(true), any());
		assertThat(result.getData().getRelatedSampleReportsWithSamples().get(sampleReport), is(sample));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingContactAndPickExistingSampleAndCreateSample() throws ExecutionException, InterruptedException {

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

		SampleDto sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setLabSampleID("test-lab-sample-id");
			s.setSampleMaterial(SampleMaterial.CRUST);
			s.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		});

		AtomicBoolean firstInvocation = new AtomicBoolean(true);
		doAnswer((invocation) -> {
			PickOrCreateSampleResult result;
			if (firstInvocation.get()) {
				firstInvocation.set(false);
				List<SampleDto> samples = invocation.getArgument(0);
				result = new PickOrCreateSampleResult();
				result.setSample(samples.get(0));
			} else {
				result = new PickOrCreateSampleResult();
				result.setNewSample(true);
			}
			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		ArgumentCaptor<SampleDto> editedSampleCaptor = ArgumentCaptor.forClass(SampleDto.class);
		ArgumentCaptor<List<PathogenTestDto>> editedTestsCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Boolean> lastSampleArgsEdit = ArgumentCaptor.forClass(Boolean.class);

		doAnswer((invocation) -> {
			SampleDto editedSample = invocation.getArgument(0);
			editedSample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> editedTests = invocation.getArgument(1);
			editedTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(editedSample, editedTests));
			return null;
		}).when(handleEditSample).handle(editedSampleCaptor.capture(), editedTestsCaptor.capture(), lastSampleArgsEdit.capture(), any());

		ArgumentCaptor<SampleDto> createdSampleArgs = ArgumentCaptor.forClass(SampleDto.class);
		ArgumentCaptor<List<PathogenTestDto>> createdPathogenTestsArgs = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Boolean> entityCreatedArgs = ArgumentCaptor.forClass(Boolean.class);
		ArgumentCaptor<Boolean> lastSampleArgsCreate = ArgumentCaptor.forClass(Boolean.class);

		doAnswer((invocation) -> {
			SampleDto createdSample = invocation.getArgument(0);
			createdSample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> pathogenTests = invocation.getArgument(1);
			pathogenTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(sample, pathogenTests));
			return null;
		}).when(handleCreateSampleAndPathogenTests)
			.handle(
				createdSampleArgs.capture(),
				createdPathogenTestsArgs.capture(),
				entityCreatedArgs.capture(),
				lastSampleArgsCreate.capture(),
				any());

		ExternalMessageDto standardLabMessage = createStandardLabMessageWithTwoSampleReports();
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(standardLabMessage);

		verifyStandardEditingAndCreationOfSample(
			standardLabMessage,
			editedSampleCaptor,
			editedTestsCaptor,
			lastSampleArgsEdit,
			createdSampleArgs,
			createdPathogenTestsArgs,
			entityCreatedArgs,
			lastSampleArgsCreate);

		assertThat(result.getStatus(), is(DONE));
		assertThat(result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(0)), is(sample));

		// test that changes in handler are kept
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(0)).getSamplingReason(),
			is(SamplingReason.PROFESSIONAL_REASON));
		assertThat(result.getData().getPathogenTests().get(0).getTestResultText(), is("Dummy test result text"));
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(1)).getSampleMaterial(),
			is(SampleMaterial.CRUST));

		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(0)).getAssociatedContact(),
			is(contact.toReference()));
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(standardLabMessage.getSampleReports().get(1)).getAssociatedContact(),
			is(contact.toReference()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingEventParticipantAndCreateSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS, rdcf);
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
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		labMessage.addSampleReport(sampleReport);
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), argThat(entityCreated -> {
			assertThat(entityCreated, is(false));

			return true;
		}), argThat(lastSample -> {
			assertThat(lastSample, is(true));

			return true;
		}), any());
		assertThat(
			result.getData().getRelatedSampleReportsWithSamples().get(sampleReport).getAssociatedEventParticipant(),
			is(eventParticipant.toReference()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPickExistingEventParticipantAndPickExistingSample() throws ExecutionException, InterruptedException {

		PersonDto person = creator.createPerson();
		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).apply(any(), any());

		EventDto event = creator.createEvent(user.toReference(), Disease.CORONAVIRUS, rdcf);
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		doAnswer(invocation -> {
			List<SimilarEventParticipantDto> eventParticipants = invocation.getArgument(2);

			PickOrCreateEntryResult pickOrCreateEntryResult = new PickOrCreateEntryResult();
			pickOrCreateEntryResult.setEventParticipant(eventParticipants.get(0));

			//noinspection unchecked
			((HandlerCallback<PickOrCreateEntryResult>) invocation.getArgument(3)).done(pickOrCreateEntryResult);

			return null;
		}).when(handlePickOrCreateEntry).handle(any(), any(), any(), any());

		SampleDto sample = creator
			.createSample(eventParticipant.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.CRUST, rdcf.facility, null);
		doAnswer((invocation) -> {
			List<SampleDto> samples = invocation.getArgument(0);
			PickOrCreateSampleResult result = new PickOrCreateSampleResult();
			result.setSample(samples.get(0));

			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).done(new SampleAndPathogenTests(invocation.getArgument(0), invocation.getArgument(1)));
			return null;
		}).when(handleEditSample).handle(any(), any(), any(), any());

		ExternalMessageDto labMessage = createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED);
		SampleReportDto sampleReport = SampleReportDto.build();
		labMessage.addSampleReport(sampleReport);
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(labMessage);

		assertThat(result.getStatus(), is(DONE));

		verify(handleEditSample, times(1)).handle(any(), any(), eq(true), any());
		assertThat(result.getData().getRelatedSampleReportsWithSamples().get(sampleReport), is(sample));
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
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

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
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handleEditSample).handle(any(), any(), any(), any());

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result =
			runFlow(createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED));

		assertThat(result.getStatus(), is(CANCELED));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEditSampleAndCreateSampleCancel() throws ExecutionException, InterruptedException {

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

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> s.setLabSampleID("test-lab-sample-id"));

		PickOrCreateSampleResult pickOrCreateSampleResult = new PickOrCreateSampleResult();
		pickOrCreateSampleResult.setSample(sample);

		AtomicBoolean firstInvocation = new AtomicBoolean(true);
		doAnswer((invocation) -> {
			PickOrCreateSampleResult result;
			if (firstInvocation.get()) {
				firstInvocation.set(false);
				List<SampleDto> samples = invocation.getArgument(0);
				result = new PickOrCreateSampleResult();
				result.setSample(samples.get(0));
			} else {
				result = new PickOrCreateSampleResult();
				result.setNewSample(true);
			}
			getCallbackParam(invocation).done(result);
			return null;
		}).when(handlePickOrCreateSample).handle(any(), any(), any());

		doAnswer((invocation) -> {
			SampleDto editedSample = invocation.getArgument(0);
			editedSample.setSamplingReason(SamplingReason.PROFESSIONAL_REASON);

			List<PathogenTestDto> editedTests = invocation.getArgument(1);
			editedTests.get(0).setTestResultText("Dummy test result text");

			getCallbackParam(invocation).done(new SampleAndPathogenTests(editedSample, editedTests));
			return null;
		}).when(handleEditSample).handle(any(), any(), any(), any());

		doAnswer((invocation) -> {
			getCallbackParam(invocation).cancel();
			return null;
		}).when(handleCreateSampleAndPathogenTests).handle(any(), any(), any(), any(), any());

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(createStandardLabMessageWithTwoSampleReports());

		assertThat(result.getStatus(), is(CANCELED));
	}

	private ProcessingResult<RelatedSamplesReportsAndPathogenTests> runFlow(ExternalMessageDto labMessage)
		throws ExecutionException, InterruptedException {

		return flow.run(labMessage, relatedLabMessageHandler).toCompletableFuture().get();
	}

	private ExternalMessageDto createLabMessage(Disease disease, String reportId, ExternalMessageStatus status) {
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

	private ExternalMessageDto createLabMessage(
		Disease disease,
		String reportId,
		ExternalMessageStatus status,
		Consumer<ExternalMessageDto> customConfig) {
		ExternalMessageDto labMessage = ExternalMessageDto.build();

		labMessage.setDisease(disease);
		labMessage.setReportId(reportId);
		labMessage.setStatus(status);

		if (customConfig != null) {
			customConfig.accept(labMessage);
		}

		return labMessage;
	}

	private ExternalMessageDto createStandardLabMessageWithTwoSampleReports() {

		SampleReportDto sampleReport1 = SampleReportDto.build();
		sampleReport1.setSampleDateTime(new Date(100000000L));
		sampleReport1.setSampleMaterial(SampleMaterial.BLOOD);
		sampleReport1.setLabSampleId("test-lab-sample-id");

		SampleReportDto sampleReport2 = SampleReportDto.build();
		sampleReport2.setSampleDateTime(new Date(200000000L));
		sampleReport2.setSampleMaterial(SampleMaterial.CRUST);

		TestReportDto testReport1a = TestReportDto.build();
		testReport1a.setTestType(PathogenTestType.CULTURE);
		testReport1a.setTestResult(PathogenTestResultType.NEGATIVE);
		sampleReport1.addTestReport(testReport1a);

		TestReportDto testReport1b = TestReportDto.build();
		testReport1b.setTestType(PathogenTestType.PCR_RT_PCR);
		testReport1b.setTestResult(PathogenTestResultType.PENDING);
		sampleReport1.addTestReport(testReport1b);

		TestReportDto testReport2 = TestReportDto.build();
		testReport1a.setTestType(PathogenTestType.SEQUENCING);
		testReport2.setTestResult(PathogenTestResultType.POSITIVE);
		sampleReport2.addTestReport(testReport2);

		return createLabMessage(Disease.CORONAVIRUS, "test-report-id", ExternalMessageStatus.UNPROCESSED, l -> {
			l.addSampleReport(sampleReport1);
			l.addSampleReport(sampleReport2);
		});
	}

	private void verifyStandardCreationOfTwoSamples(
		ExternalMessageDto standardLabMessage,
		ArgumentCaptor<SampleDto> sampleArgs,
		ArgumentCaptor<List<PathogenTestDto>> pathogenTestsArgs,
		ArgumentCaptor<Boolean> entityCreatedArgs,
		ArgumentCaptor<Boolean> lastSampleArgs,
		boolean entitiyCreated) {

		// verify first call to handleCreateSampleAndPathogenTests
		SampleDto argSample = sampleArgs.getAllValues().get(0);
		assertThat(argSample.getSampleDateTime(), is(standardLabMessage.getSampleReports().get(0).getSampleDateTime()));
		assertThat(argSample.getSampleMaterial(), is(SampleMaterial.BLOOD));
		assertThat(argSample.getReportingUser(), is(user.toReference()));

		List<PathogenTestDto> argPathogenTests = pathogenTestsArgs.getAllValues().get(0);
		assertThat(argPathogenTests, hasSize(2));
		assertThat(argPathogenTests.get(0).getTestType(), is(standardLabMessage.getSampleReports().get(0).getTestReports().get(0).getTestType()));
		assertThat(argPathogenTests.get(0).getTestResult(), is(standardLabMessage.getSampleReports().get(0).getTestReports().get(0).getTestResult()));
		assertThat(argPathogenTests.get(1).getTestType(), is(standardLabMessage.getSampleReports().get(0).getTestReports().get(1).getTestType()));
		assertThat(argPathogenTests.get(1).getTestResult(), is(standardLabMessage.getSampleReports().get(0).getTestReports().get(1).getTestResult()));

		assertThat(entityCreatedArgs.getAllValues().get(0), is(entitiyCreated));

		assertThat(lastSampleArgs.getAllValues().get(0), is(false));

		// verify second call to handleCreateSampleAndPathogenTests
		argSample = sampleArgs.getValue();
		assertThat(argSample.getSampleDateTime(), is(standardLabMessage.getSampleReports().get(1).getSampleDateTime()));
		assertThat(argSample.getSampleMaterial(), is(SampleMaterial.CRUST));
		assertThat(argSample.getReportingUser(), is(user.toReference()));

		argPathogenTests = pathogenTestsArgs.getValue();
		assertThat(argPathogenTests, hasSize(1));
		assertThat(argPathogenTests.get(0).getTestType(), is(standardLabMessage.getSampleReports().get(1).getTestReports().get(0).getTestType()));
		assertThat(argPathogenTests.get(0).getTestResult(), is(standardLabMessage.getSampleReports().get(1).getTestReports().get(0).getTestResult()));

		assertThat(entityCreatedArgs.getAllValues().get(1), is(entitiyCreated));

		assertThat(lastSampleArgs.getAllValues().get(1), is(true));
	}

	private void verifyStandardEditingAndCreationOfSample(
		ExternalMessageDto standardLabMessage,
		ArgumentCaptor<SampleDto> editedSampleCaptor,
		ArgumentCaptor<List<PathogenTestDto>> editedTestsCaptor,
		ArgumentCaptor<Boolean> lastSampleArgsEdit,
		ArgumentCaptor<SampleDto> createdSampleArgs,
		ArgumentCaptor<List<PathogenTestDto>> createdPathogenTestsArgs,
		ArgumentCaptor<Boolean> entityCreatedArgs,
		ArgumentCaptor<Boolean> lastSampleArgsCreate) {

		verify(multipleSamplesConfirmationHandler, times(1)).get();
		verify(handleEditSample, times(1)).handle(any(), any(), any(), any());
		verify(handleCreateSampleAndPathogenTests, times(1)).handle(any(), any(), any(), any(), any());

		//// verify sample edit
		// sample not changed when calling edit handler
		assertThat(editedSampleCaptor.getValue().getSampleMaterial(), is(SampleMaterial.CRUST));
		assertThat(editedSampleCaptor.getValue().getSpecimenCondition(), is(SpecimenCondition.ADEQUATE));

		// test reports converted to pathogen test
		assertThat(editedTestsCaptor.getValue(), hasSize(2));
		assertThat(
			editedTestsCaptor.getValue().get(0).getTestType(),
			is(standardLabMessage.getSampleReports().get(0).getTestReports().get(0).getTestType()));
		assertThat(
			editedTestsCaptor.getValue().get(0).getTestResult(),
			is(standardLabMessage.getSampleReports().get(0).getTestReports().get(0).getTestResult()));
		assertThat(
			editedTestsCaptor.getValue().get(1).getTestType(),
			is(standardLabMessage.getSampleReports().get(0).getTestReports().get(1).getTestType()));
		assertThat(
			editedTestsCaptor.getValue().get(1).getTestResult(),
			is(standardLabMessage.getSampleReports().get(0).getTestReports().get(1).getTestResult()));

		assertThat(lastSampleArgsEdit.getValue(), is(false));

		//// verify sample creation
		SampleDto argSample = createdSampleArgs.getValue();
		assertThat(argSample.getSampleDateTime(), is(standardLabMessage.getSampleReports().get(1).getSampleDateTime()));
		assertThat(argSample.getSampleMaterial(), is(SampleMaterial.CRUST));
		assertThat(argSample.getReportingUser(), is(user.toReference()));

		List<PathogenTestDto> argPathogenTests = createdPathogenTestsArgs.getValue();
		assertThat(argPathogenTests, hasSize(1));
		assertThat(argPathogenTests.get(0).getTestType(), is(standardLabMessage.getSampleReports().get(1).getTestReports().get(0).getTestType()));
		assertThat(argPathogenTests.get(0).getTestResult(), is(standardLabMessage.getSampleReports().get(1).getTestReports().get(0).getTestResult()));

		assertThat(entityCreatedArgs.getValue(), is(false));

		assertThat(lastSampleArgsCreate.getValue(), is(true));

	}

	private interface PickOrCreateEntryHandler {

		void handle(
			List<CaseSelectionDto> similarCases,
			List<SimilarContactDto> similarContacts,
			List<SimilarEventParticipantDto> similarEventParticipants,
			HandlerCallback<PickOrCreateEntryResult> callback);
	}

	private interface EditSampleHandler {

		void handle(SampleDto sample, List<PathogenTestDto> pathogenTests, Boolean lastSample, HandlerCallback<SampleAndPathogenTests> callback);
	}

	private interface PickOrCreateSampleHandler {

		void handle(List<SampleDto> similarSamples, List<SampleDto> otherSamples, HandlerCallback<PickOrCreateSampleResult> callback);
	}

	private interface CreateSampleAndPathogenTestHandler {

		CompletionStage<SampleAndPathogenTests> handle(
			SampleDto sample,
			List<PathogenTestDto> pathogenTests,
			Boolean entityCreated,
			Boolean lastSample,
			HandlerCallback<SampleAndPathogenTests> callback);
	}

	private interface CreateCaseHandler {

		Object handle(CaseDataDto caze, PersonDto person, HandlerCallback<?> callback);
	}

	private interface CreateContactHandler {

		Object handle(ContactDto contact, PersonDto person, HandlerCallback<?> callback);
	}
}
