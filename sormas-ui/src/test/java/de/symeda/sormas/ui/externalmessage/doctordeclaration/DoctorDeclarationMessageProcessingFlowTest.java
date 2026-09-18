/*
 * SORMAS(R) - Surveillance Outbreak Response Management & Analysis System
 * Copyright (C) 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.externalmessage.doctordeclaration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEventResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateSampleResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.SampleAndPathogenTests;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.UiUtil;

class DoctorDeclarationMessageProcessingFlowTest extends AbstractUiBeanTest {

	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private ExternalMessageProcessingFacade processingFacade;

	@Override
	public void init() {
		super.init();
		rdcf = creator.createRDCF();
		user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
		loginWith(user);
		processingFacade = getExternalMessageProcessingFacade();

		executeInTransaction(em -> {
			em.createNativeQuery(
				"CREATE TABLE IF NOT EXISTS notifier_history (" + "id BIGINT," + "uuid VARCHAR(255)," + "changedate TIMESTAMP,"
					+ "registrationnumber VARCHAR(255)," + "firstname VARCHAR(255)," + "lastname VARCHAR(255)," + "address VARCHAR(2048),"
					+ "email VARCHAR(255)," + "phone VARCHAR(255)," + "agentfirstname VARCHAR(255)," + "agentlastname VARCHAR(255))")
				.executeUpdate();
		});
	}

	@Test
	void handleProcessingDoneDoneAppliesDeferredPersonAndNotifierUpdates() throws Exception {
		PersonDto person = creator.createPerson("Given", "Family", p -> p.getAddress().setStreet("Old Street"));
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(m -> {
			m.setPersonStreet("New Street");
			m.setPersonPhone("+352111111");
			m.setPersonEmail("doctor.declaration@sormas.test");
			m.setNotifierRegistrationNumber("REG-123");
			m.setNotifierFirstName("Notif");
			m.setNotifierLastName("Ier");
			m.setNotifierAddress("Notifier Address");
			m.setNotifierPhone("12345");
			m.setNotifierEmail("notifier@test.local");
			m.setReporterName("Alice-Bob-Charlie");
		});

		TestDoctorDeclarationFlow flow = createFlow(externalMessage);

		ExternalMessageProcessingResult resultData = new ExternalMessageProcessingResult().withPerson(person, false).withSelectedCase(caze);
		ProcessingResult<ExternalMessageProcessingResult> doneResult = ProcessingResult.of(ProcessingResultStatus.DONE, resultData);

		flow.exposeHandleProcessingDone(doneResult).toCompletableFuture().get();

		PersonDto updatedPerson = FacadeProvider.getPersonFacade().getByUuid(person.getUuid());
		assertEquals("New Street", updatedPerson.getAddress().getStreet());
		assertTrue(hasPrimaryDetail(updatedPerson, PersonContactDetailType.PHONE, "+352111111"));
		assertTrue(hasPrimaryDetail(updatedPerson, PersonContactDetailType.EMAIL, "doctor.declaration@sormas.test"));

		CaseDataDto updatedCase = FacadeProvider.getCaseFacade().getByUuid(caze.getUuid());
		assertNotNull(updatedCase.getNotifier());
		assertEquals("Notif", updatedCase.getNotifier().getFirstName());
		assertEquals("Ier", updatedCase.getNotifier().getLastName());

		ExternalMessageDto updatedMessage = FacadeProvider.getExternalMessageFacade().getByUuid(externalMessage.getUuid());
		assertEquals(ExternalMessageStatus.PROCESSED, updatedMessage.getStatus());
	}

	@Test
	void handleProcessingDoneCanceledSkipsDeferredUpdates() throws Exception {
		PersonDto person = creator.createPerson("Given", "Family", p -> p.getAddress().setStreet("Old Street"));
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(m -> {
			m.setPersonStreet("New Street");
			m.setNotifierRegistrationNumber("REG-CANCEL");
		});

		TestDoctorDeclarationFlow flow = createFlow(externalMessage);
		ExternalMessageProcessingResult resultData = new ExternalMessageProcessingResult().withPerson(person, false).withSelectedCase(caze);
		ProcessingResult<ExternalMessageProcessingResult> canceledResult = ProcessingResult.of(ProcessingResultStatus.CANCELED, resultData);

		flow.exposeHandleProcessingDone(canceledResult).toCompletableFuture().get();

		PersonDto unchangedPerson = FacadeProvider.getPersonFacade().getByUuid(person.getUuid());
		assertEquals("Old Street", unchangedPerson.getAddress().getStreet());

		CaseDataDto unchangedCase = FacadeProvider.getCaseFacade().getByUuid(caze.getUuid());
		assertNull(unchangedCase.getNotifier());

		ExternalMessageDto unchangedMessage = FacadeProvider.getExternalMessageFacade().getByUuid(externalMessage.getUuid());
		assertEquals(ExternalMessageStatus.UNPROCESSED, unchangedMessage.getStatus());
	}

	@Test
	void handleProcessingDoneDoneWithoutNotifierRegistrationDoesNotSetNotifier() throws Exception {
		PersonDto person = creator.createPerson("Given", "Family");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(m -> {
			m.setNotifierFirstName("Name");
			m.setNotifierLastName("WithoutReg");
			m.setReporterName("John-Doe");
		});

		TestDoctorDeclarationFlow flow = createFlow(externalMessage);
		ExternalMessageProcessingResult resultData = new ExternalMessageProcessingResult().withPerson(person, false).withSelectedCase(caze);
		ProcessingResult<ExternalMessageProcessingResult> doneResult = ProcessingResult.of(ProcessingResultStatus.DONE, resultData);

		flow.exposeHandleProcessingDone(doneResult).toCompletableFuture().get();

		CaseDataDto updatedCase = FacadeProvider.getCaseFacade().getByUuid(caze.getUuid());
		assertNull(updatedCase.getNotifier());
	}

	@Test
	void handleProcessingDoneDoneWithMissingPersonDoesNotFail() {
		PersonDto existingPerson = creator.createPerson("Given", "Family", p -> p.getAddress().setStreet("Original"));
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			existingPerson.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(null);
		TestDoctorDeclarationFlow flow = createFlow(externalMessage);

		PersonDto unknownPerson = PersonDto.build();
		unknownPerson.setUuid(DataHelper.createUuid());

		ExternalMessageProcessingResult resultData = new ExternalMessageProcessingResult().withPerson(unknownPerson, false).withSelectedCase(caze);
		ProcessingResult<ExternalMessageProcessingResult> doneResult = ProcessingResult.of(ProcessingResultStatus.DONE, resultData);

		assertDoesNotThrow(() -> flow.exposeHandleProcessingDone(doneResult).toCompletableFuture().get());
	}

	@Test
	void prepareSelectedCaseSyncsUntouchedCaseDataWhenReusingCase() {
		PersonDto person = creator.createPerson("Given", "Family");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.NOT_CLASSIFIED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(m -> m.setCaseClassification(CaseClassification.CONFIRMED));
		TestDoctorDeclarationFlow flow = createFlow(externalMessage);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertEquals(CaseClassification.CONFIRMED, result.getCaseClassification());

		CaseDataDto persistedCase = FacadeProvider.getCaseFacade().getByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, persistedCase.getCaseClassification());
	}

	@Test
	void prepareSelectedCaseDoesNotOverwriteTouchedSymptomsWhenReusingCase() {
		PersonDto person = creator.createPerson("Given", "Family");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		caze.getSymptoms().setFever(SymptomState.NO);
		caze = FacadeProvider.getCaseFacade().save(caze);

		SymptomsDto externalSymptoms = SymptomsDto.build();
		externalSymptoms.setFever(SymptomState.YES);
		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(m -> m.setCaseSymptoms(externalSymptoms));

		TestDoctorDeclarationFlow flow = createFlow(externalMessage);
		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertEquals(SymptomState.NO, result.getSymptoms().getFever());
	}

	@Test
	void prepareSelectedCaseSyncsExposuresWhenOnlyActivitiesAreTouched() {
		PersonDto person = creator.createPerson("Given", "Family");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		caze.getEpiData().setActivityAsCaseDetailsKnown(YesNoUnknown.NO);
		caze = FacadeProvider.getCaseFacade().save(caze);

		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(m -> m.setExposures("[{\"exposureType\":\"UNKNOWN\"}]"));
		TestDoctorDeclarationFlow flow = createFlow(externalMessage);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertEquals(YesNoUnknown.YES, result.getEpiData().getExposureDetailsKnown());
		assertEquals(1, result.getEpiData().getExposures().size());
		assertEquals(ExposureType.UNKNOWN, result.getEpiData().getExposures().get(0).getExposureType());
	}

	@Test
	void prepareSelectedCaseSyncsActivitiesWhenOnlyExposuresAreTouched() {
		PersonDto person = creator.createPerson("Given", "Family");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		caze.getEpiData().setExposureDetailsKnown(YesNoUnknown.NO);
		caze = FacadeProvider.getCaseFacade().save(caze);

		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(m -> m.setActivitiesAsCase("[{\"activityAsCaseType\":\"UNKNOWN\"}]"));
		TestDoctorDeclarationFlow flow = createFlow(externalMessage);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertEquals(YesNoUnknown.YES, result.getEpiData().getActivityAsCaseDetailsKnown());
		assertEquals(1, result.getEpiData().getActivitiesAsCase().size());
	}

	@Test
	void runFlowCancelsOnSymptomsMismatchAndSkipsDeferredUpdates() throws Exception {
		PersonDto person = creator.createPerson("Given", "Family", p -> p.getAddress().setStreet("Stable Street"));
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		caze.getSymptoms().setFever(SymptomState.NO);
		caze = FacadeProvider.getCaseFacade().save(caze);

		SymptomsDto externalSymptoms = SymptomsDto.build();
		externalSymptoms.setFever(SymptomState.YES);

		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(m -> {
			m.setCaseSymptoms(externalSymptoms);
			m.setPersonStreet("ShouldNotBeApplied");
			m.setNotifierRegistrationNumber("REG-SHOULD-NOT-BE-SET");
		});

		TestDoctorDeclarationFlow flow = createFlow(externalMessage);
		flow.selectedPerson = new EntitySelection<>(person, false);
		flow.pickOrCreateEntryResult = selectCaseResult(caze);
		flow.symptomsMismatchConfirmation = false;

		ProcessingResult<ExternalMessageProcessingResult> result = flow.run().toCompletableFuture().get();

		assertEquals(ProcessingResultStatus.CANCELED, result.getStatus());
		assertEquals(1, flow.symptomsMismatchCalls);

		PersonDto unchangedPerson = FacadeProvider.getPersonFacade().getByUuid(person.getUuid());
		assertEquals("Stable Street", unchangedPerson.getAddress().getStreet());

		CaseDataDto unchangedCase = FacadeProvider.getCaseFacade().getByUuid(caze.getUuid());
		assertNull(unchangedCase.getNotifier());
	}

	@Test
	void runFlowInvokesMismatchConfirmationsInDefinedOrder() throws Exception {
		PersonDto person = creator.createPerson("Given", "Family");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.TUBERCULOSIS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		caze.getSymptoms().setFever(SymptomState.NO);
		caze.getHealthConditions().setTuberculosis(YesNoUnknown.NO);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.NO);
		caze.getEpiData().setExposureDetailsKnown(YesNoUnknown.NO);
		caze.getEpiData().setActivityAsCaseDetailsKnown(YesNoUnknown.NO);
		caze = FacadeProvider.getCaseFacade().save(caze);

		SymptomsDto externalSymptoms = SymptomsDto.build();
		externalSymptoms.setFever(SymptomState.YES);

		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(m -> {
			m.setDisease(Disease.TUBERCULOSIS);
			m.setCaseClassification(CaseClassification.CONFIRMED);
			m.setTuberculosis(YesNoUnknown.YES);
			m.setCaseSymptoms(externalSymptoms);
			m.setAdmittedToHealthFacility(YesNoUnknown.YES);
			m.setHospitalizationFacilityName("Unknown Hospital");
			m.setExposures("[{\"exposureType\":\"UNKNOWN\"}]");
			m.setActivitiesAsCase("[{\"activityAsCaseType\":\"UNKNOWN\"}]");
		});

		TestDoctorDeclarationFlow flow = createFlow(externalMessage);
		flow.selectedPerson = new EntitySelection<>(person, false);
		flow.pickOrCreateEntryResult = selectCaseResult(caze);
		flow.activitiesMismatchConfirmation = false;

		ProcessingResult<ExternalMessageProcessingResult> result = flow.run().toCompletableFuture().get();

		assertEquals(ProcessingResultStatus.CANCELED, result.getStatus());
		assertEquals(
			List.of("CASE_DATA", "HEALTH_CONDITIONS", "SYMPTOMS", "HOSPITALIZATION", "EXPOSURES", "ACTIVITIES_AS_CASE"),
			flow.confirmationOrder);
	}

	@Test
	void prepareSelectedCaseThrowsWhenExposureJsonIsInvalid() {
		PersonDto person = creator.createPerson("Given", "Family");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.NOT_CLASSIFIED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		ExternalMessageDto externalMessage = createDoctorDeclarationMessage(m -> m.setExposures("["));
		TestDoctorDeclarationFlow flow = createFlow(externalMessage);

		assertThrows(IllegalStateException.class, () -> flow.exposePrepareSelectedCase(caze, externalMessage));
	}

	private TestDoctorDeclarationFlow createFlow(ExternalMessageDto externalMessage) {
		ExternalMessageMapper mapper = new ExternalMessageMapper(externalMessage, processingFacade);
		try (MockedStatic<UiUtil> uiUtilMock = Mockito.mockStatic(UiUtil.class)) {
			uiUtilMock.when(UiUtil::getUser).thenReturn(user);
			return new TestDoctorDeclarationFlow(externalMessage, mapper, processingFacade);
		}
	}

	private ExternalMessageDto createDoctorDeclarationMessage(Consumer<ExternalMessageDto> customConfig) {
		return creator.createExternalMessage(m -> {
			m.setType(ExternalMessageType.PHYSICIANS_REPORT);
			m.setDisease(Disease.CORONAVIRUS);
			m.setStatus(ExternalMessageStatus.UNPROCESSED);
			m.setReportId(DataHelper.createUuid());
			m.setReportMessageId(DataHelper.createUuid());
			m.setMessageDateTime(new Date());
			if (customConfig != null) {
				customConfig.accept(m);
			}
		});
	}

	private static PickOrCreateEntryResult selectCaseResult(CaseDataDto caze) {
		PickOrCreateEntryResult result = new PickOrCreateEntryResult();
		result
			.setCaze(new CaseSelectionDto(caze.getUuid(), null, null, caze.getDisease(), null, null, null, null, null, null, null, null, null, true));
		return result;
	}

	private static boolean hasPrimaryDetail(PersonDto person, PersonContactDetailType type, String value) {
		for (PersonContactDetailDto detail : person.getPersonContactDetails()) {
			if (detail.getPersonContactDetailType() == type
				&& value.equals(detail.getContactInformation())
				&& detail.isPrimaryContact()
				&& !detail.isThirdParty()) {
				return true;
			}
		}

		return false;
	}

	private static class TestDoctorDeclarationFlow extends DoctorDeclarationMessageProcessingFlow {

		private EntitySelection<PersonDto> selectedPerson;
		private PickOrCreateEntryResult pickOrCreateEntryResult;
		private boolean caseDataMismatchConfirmation = true;
		private boolean caseHealthMismatchConfirmation = true;
		private boolean symptomsMismatchConfirmation = true;
		private boolean hospitalizationMismatchConfirmation = true;
		private boolean exposuresMismatchConfirmation = true;
		private boolean activitiesMismatchConfirmation = true;
		private int symptomsMismatchCalls;
		private final List<String> confirmationOrder = new ArrayList<>();

		private TestDoctorDeclarationFlow(
			ExternalMessageDto externalMessage,
			ExternalMessageMapper mapper,
			ExternalMessageProcessingFacade processingFacade) {
			super(externalMessage, mapper, processingFacade);
		}

		private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> exposeHandleProcessingDone(
			ProcessingResult<ExternalMessageProcessingResult> result) {
			return super.handleProcessingDone(result);
		}

		private CaseDataDto exposePrepareSelectedCase(CaseDataDto caze, ExternalMessageDto externalMessage) {
			return super.prepareSelectedCase(caze, externalMessage);
		}

		@Override
		protected CompletionStage<Boolean> handleMissingDisease() {
			return CompletableFuture.completedFuture(true);
		}

		@Override
		protected CompletionStage<Boolean> handleRelatedForwardedMessages() {
			return CompletableFuture.completedFuture(true);
		}

		@Override
		protected CompletionStage<Boolean> handleInfraDataChecks() {
			return CompletableFuture.completedFuture(true);
		}

		@Override
		protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback) {
			if (selectedPerson != null) {
				callback.done(selectedPerson);
				return;
			}

			callback.done(new EntitySelection<>(person, true));
		}

		@Override
		protected void handlePickOrCreateEntry(
			List<CaseSelectionDto> similarCases,
			List<SimilarContactDto> similarContacts,
			List<SimilarEventParticipantDto> similarEventParticipants,
			ExternalMessageDto externalMessage,
			HandlerCallback<PickOrCreateEntryResult> callback) {

			if (pickOrCreateEntryResult != null) {
				callback.done(pickOrCreateEntryResult);
				return;
			}

			if (!similarCases.isEmpty()) {
				PickOrCreateEntryResult result = new PickOrCreateEntryResult();
				result.setCaze(similarCases.get(0));
				callback.done(result);
				return;
			}

			PickOrCreateEntryResult result = new PickOrCreateEntryResult();
			result.setNewCase(true);
			callback.done(result);
		}

		@Override
		protected CompletionStage<Boolean> confirmCaseDataMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			confirmationOrder.add("CASE_DATA");
			return CompletableFuture.completedFuture(caseDataMismatchConfirmation);
		}

		@Override
		protected CompletionStage<Boolean> confirmCaseHealthConditionsMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			confirmationOrder.add("HEALTH_CONDITIONS");
			return CompletableFuture.completedFuture(caseHealthMismatchConfirmation);
		}

		@Override
		protected CompletionStage<Boolean> confirmCaseSymptomsMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			symptomsMismatchCalls += 1;
			confirmationOrder.add("SYMPTOMS");
			return CompletableFuture.completedFuture(symptomsMismatchConfirmation);
		}

		@Override
		protected CompletionStage<Boolean> confirmCaseHospitalizationMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			confirmationOrder.add("HOSPITALIZATION");
			return CompletableFuture.completedFuture(hospitalizationMismatchConfirmation);
		}

		@Override
		protected CompletionStage<Boolean> confirmCaseExposuresMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			confirmationOrder.add("EXPOSURES");
			return CompletableFuture.completedFuture(exposuresMismatchConfirmation);
		}

		@Override
		protected CompletionStage<Boolean> confirmCaseActivitiesAsCaseMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			confirmationOrder.add("ACTIVITIES_AS_CASE");
			return CompletableFuture.completedFuture(activitiesMismatchConfirmation);
		}

		@Override
		protected void handleCreateCase(
			CaseDataDto caze,
			PersonDto person,
			ExternalMessageDto externalMessage,
			HandlerCallback<CaseDataDto> callback) {
			callback.done(caze);
		}

		@Override
		protected void handleCreateContact(
			ContactDto contact,
			PersonDto person,
			ExternalMessageDto externalMessage,
			HandlerCallback<ContactDto> callback) {
			callback.done(contact);
		}

		@Override
		protected void handlePickOrCreateEvent(ExternalMessageDto externalMessage, HandlerCallback<PickOrCreateEventResult> callback) {
			callback.cancel();
		}

		@Override
		protected void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback) {
			callback.done(event);
		}

		@Override
		protected void handleCreateEventParticipant(
			EventParticipantDto eventParticipant,
			EventDto event,
			ExternalMessageDto externalMessage,
			HandlerCallback<EventParticipantDto> callback) {
			callback.done(eventParticipant);
		}

		@Override
		protected void handlePickOrCreateSample(
			List<SampleDto> similarSamples,
			List<SampleDto> otherSamples,
			ExternalMessageDto labMessage,
			int sampleReportIndex,
			HandlerCallback<PickOrCreateSampleResult> callback) {
			callback.cancel();
		}

		@Override
		protected void handleEditSample(
			SampleDto sample,
			List<PathogenTestDto> newPathogenTests,
			ExternalMessageDto labMessage,
			ExternalMessageMapper mapper,
			boolean lastSample,
			HandlerCallback<SampleAndPathogenTests> callback) {
			callback.cancel();
		}

		@Override
		public CompletionStage<Boolean> handleMultipleSampleConfirmation() {
			return CompletableFuture.completedFuture(true);
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
			callback.done(new SampleAndPathogenTests(sample, pathogenTests));
		}

		@Override
		protected CompletionStage<Boolean> confirmPickExistingEventParticipant() {
			return CompletableFuture.completedFuture(true);
		}

		@Override
		protected CompletionStage<Void> notifyCorrectionsSaved() {
			return CompletableFuture.completedFuture(null);
		}
	}
}
