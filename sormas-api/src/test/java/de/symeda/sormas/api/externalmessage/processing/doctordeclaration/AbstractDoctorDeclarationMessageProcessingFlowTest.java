package de.symeda.sormas.api.externalmessage.processing.doctordeclaration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEventResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateSampleResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.SampleAndPathogenTests;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;

class AbstractDoctorDeclarationMessageProcessingFlowTest {

	@Test
	void prepareSelectedCaseSyncsAndSavesUntouchedCaseData() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getCaseClassification()).thenReturn(CaseClassification.CONFIRMED);

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		when(processingFacade.saveCase(any(CaseDataDto.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertEquals(CaseClassification.CONFIRMED, result.getCaseClassification());
		verify(processingFacade).saveCase(caze);
	}

	@Test
	void prepareSelectedCaseDoesNotSyncTouchedCaseData() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getCaseClassification()).thenReturn(CaseClassification.CONFIRMED);

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);
		caze.setCaseClassification(CaseClassification.SUSPECT);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertEquals(CaseClassification.SUSPECT, result.getCaseClassification());
		verify(processingFacade, never()).saveCase(any(CaseDataDto.class));
	}

	@Test
	void prepareSelectedCaseSyncsAndSavesUntouchedTuberculosisHealthConditions() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getDisease()).thenReturn(Disease.TUBERCULOSIS);
		when(externalMessage.getTuberculosis()).thenReturn(YesNoUnknown.YES);

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		when(processingFacade.saveCase(any(CaseDataDto.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.TUBERCULOSIS);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertEquals(YesNoUnknown.YES, result.getHealthConditions().getTuberculosis());
		verify(processingFacade).saveCase(caze);
	}

	@Test
	void prepareSelectedCaseDoesNotSyncTouchedTuberculosisHealthConditions() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getDisease()).thenReturn(Disease.TUBERCULOSIS);
		when(externalMessage.getTuberculosis()).thenReturn(YesNoUnknown.YES);

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.TUBERCULOSIS);
		caze.getHealthConditions().setTuberculosis(YesNoUnknown.NO);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertEquals(YesNoUnknown.NO, result.getHealthConditions().getTuberculosis());
		verify(processingFacade, never()).saveCase(any(CaseDataDto.class));
	}

	@Test
	void prepareSelectedCaseSyncsAndSavesUntouchedSymptoms() {
		SymptomsDto externalSymptoms = SymptomsDto.build();
		externalSymptoms.setFever(SymptomState.YES);

		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getCaseSymptoms()).thenReturn(externalSymptoms);

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		when(processingFacade.saveCase(any(CaseDataDto.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);
		caze.getSymptoms().setSymptomatic(false);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertEquals(SymptomState.YES, result.getSymptoms().getFever());
		verify(processingFacade).saveCase(caze);
	}

	@Test
	void prepareSelectedCaseDoesNotSaveTouchedSymptoms() {
		SymptomsDto externalSymptoms = SymptomsDto.build();
		externalSymptoms.setFever(SymptomState.YES);

		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getCaseSymptoms()).thenReturn(externalSymptoms);

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);
		caze.getSymptoms().setFever(SymptomState.NO);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertEquals(SymptomState.NO, result.getSymptoms().getFever());
		verify(processingFacade, never()).saveCase(any(CaseDataDto.class));
	}

	@Test
	void prepareSelectedCaseSyncsAndSavesUntouchedHospitalizationActivitiesAndExposures() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getHospitalizationFacilityName()).thenReturn("General Hospital");
		when(externalMessage.getActivitiesAsCase()).thenReturn("[{}]");
		when(externalMessage.getExposures()).thenReturn("[{}]");

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		when(processingFacade.saveCase(any(CaseDataDto.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertTrue(flow.wasPostBuildHospitalizationCalled());
		assertTrue(flow.wasPostBuildActivitiesAsCaseCalled());
		assertTrue(flow.wasPostBuildExposureCalled());
		verify(processingFacade).saveCase(caze);
	}

	@Test
	void prepareSelectedCaseDoesNotSaveWhenHospitalizationActivitiesAndExposuresAreTouched() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getHospitalizationFacilityName()).thenReturn("General Hospital");
		when(externalMessage.getActivitiesAsCase()).thenReturn("[{}]");
		when(externalMessage.getExposures()).thenReturn("[{}]");

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.NO);
		caze.getEpiData().setActivityAsCaseDetailsKnown(YesNoUnknown.NO);
		caze.getEpiData().setExposureDetailsKnown(YesNoUnknown.NO);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertFalse(flow.wasPostBuildHospitalizationCalled());
		assertFalse(flow.wasPostBuildActivitiesAsCaseCalled());
		assertFalse(flow.wasPostBuildExposureCalled());
		verify(processingFacade, never()).saveCase(any(CaseDataDto.class));
	}

	@Test
	void prepareSelectedCaseSyncsExposuresWhenOnlyActivitiesFieldsAreTouched() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getExposures()).thenReturn("[{}]");

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		when(processingFacade.saveCase(any(CaseDataDto.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);
		caze.getEpiData().setActivityAsCaseDetailsKnown(YesNoUnknown.NO);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertTrue(flow.wasPostBuildExposureCalled());
		assertFalse(flow.wasPostBuildActivitiesAsCaseCalled());
		verify(processingFacade).saveCase(caze);
	}

	@Test
	void prepareSelectedCaseSyncsActivitiesWhenOnlyExposureFieldsAreTouched() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getActivitiesAsCase()).thenReturn("[{}]");

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		when(processingFacade.saveCase(any(CaseDataDto.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);
		caze.getEpiData().setExposureDetailsKnown(YesNoUnknown.NO);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertTrue(flow.wasPostBuildActivitiesAsCaseCalled());
		assertFalse(flow.wasPostBuildExposureCalled());
		verify(processingFacade).saveCase(caze);
	}

	@Test
	void prepareSelectedCaseEvaluatesExposureAndActivitySyncBeforeEpiDataMutation() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getActivitiesAsCase()).thenReturn("[{}]");
		when(externalMessage.getExposures()).thenReturn("[{}]");

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		when(processingFacade.saveCase(any(CaseDataDto.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		flow.enableActivityPostBuildMutatesExposureData();
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertTrue(flow.wasPostBuildActivitiesAsCaseCalled());
		assertTrue(flow.wasPostBuildExposureCalled());
		verify(processingFacade).saveCase(caze);
	}

	@Test
	void hasCaseSymptomsMismatchDependsOnUserDefinedCaseValues() {
		SymptomsDto externalSymptoms = SymptomsDto.build();
		externalSymptoms.setFever(SymptomState.YES);

		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getCaseSymptoms()).thenReturn(externalSymptoms);

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		assertFalse(flow.exposeHasCaseSymptomsMismatch(caze, externalMessage));

		caze.getSymptoms().setFever(SymptomState.YES);
		assertTrue(flow.exposeHasCaseSymptomsMismatch(caze, externalMessage));
	}

	@Test
	void hasCaseDataMismatchDependsOnUserDefinedCaseValues() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getCaseClassification()).thenReturn(CaseClassification.CONFIRMED);

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		assertFalse(flow.exposeHasCaseDataMismatch(caze, externalMessage));

		caze.setCaseClassification(CaseClassification.SUSPECT);
		assertTrue(flow.exposeHasCaseDataMismatch(caze, externalMessage));
	}

	@Test
	void hasCaseHealthConditionsMismatchDependsOnUserDefinedCaseValues() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getDisease()).thenReturn(Disease.TUBERCULOSIS);
		when(externalMessage.getTuberculosis()).thenReturn(YesNoUnknown.YES);

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.TUBERCULOSIS);

		assertFalse(flow.exposeHasCaseHealthConditionsMismatch(caze, externalMessage));

		caze.getHealthConditions().setTuberculosis(YesNoUnknown.NO);
		assertTrue(flow.exposeHasCaseHealthConditionsMismatch(caze, externalMessage));
	}

	@Test
	void hasCaseHospitalizationMismatchDependsOnUserDefinedCaseValues() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getHospitalizationAdmissionDate()).thenReturn(new Date());
		when(externalMessage.getHospitalizationFacilityName()).thenReturn("General Hospital");

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		assertFalse(flow.exposeHasCaseHospitalizationMismatch(caze, externalMessage));

		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.NO);
		assertTrue(flow.exposeHasCaseHospitalizationMismatch(caze, externalMessage));
	}

	@Test
	void hasCaseHospitalizationMismatchIsTrueWhenOnlyAdmittedFlagProvidedAndCaseTouched() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getAdmittedToHealthFacility()).thenReturn(YesNoUnknown.YES);

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.NO);

		assertTrue(flow.exposeHasCaseHospitalizationMismatch(caze, externalMessage));
	}

	@Test
	void hasCaseHospitalizationMismatchIsTrueWhenOnlyAdmissionDateProvidedAndCaseTouched() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getHospitalizationAdmissionDate()).thenReturn(new Date());

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.NO);

		assertTrue(flow.exposeHasCaseHospitalizationMismatch(caze, externalMessage));
	}

	@Test
	void hasCaseActivitiesAsCaseMismatchDependsOnUserDefinedCaseValues() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getActivitiesAsCase()).thenReturn("[{}]");

		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		assertFalse(flow.exposeHasCaseActivitiesAsCaseMismatch(caze, externalMessage));

		caze.getEpiData().setActivityAsCaseDetailsKnown(YesNoUnknown.NO);
		assertTrue(flow.exposeHasCaseActivitiesAsCaseMismatch(caze, externalMessage));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"",
		"[]",
		"{}",
		"null" })
	void hasExternalExposureDataIsFalseForBlankOrEmptyJson(String exposuresJson) {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getExposures()).thenReturn(exposuresJson);
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		assertFalse(flow.exposeHasExternalExposureData(externalMessage));
	}

	@Test
	void hasExternalExposureDataIsTrueForNonEmptyJsonArray() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getExposures()).thenReturn("[{}]");
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		assertTrue(flow.exposeHasExternalExposureData(externalMessage));
	}

	@Test
	void hasExternalExposureDataIsTrueWhenModeOfTransmissionSetEvenIfExposuresEmpty() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getExposures()).thenReturn("[]");
		when(externalMessage.getModeOfTransmission()).thenReturn(ModeOfTransmission.OTHER);
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		assertTrue(flow.exposeHasExternalExposureData(externalMessage));
	}

	@Test
	void hasExternalExposureDataIsTrueWhenModeOfTransmissionTypeSetEvenIfExposuresEmpty() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getExposures()).thenReturn("[]");
		when(externalMessage.getModeOfTransmissionType()).thenReturn("airborne");
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		assertTrue(flow.exposeHasExternalExposureData(externalMessage));
	}

	@Test
	void hasExternalActivitiesAsCaseDataIsFalseForEmptyJsonArray() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getActivitiesAsCase()).thenReturn("[]");
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		assertFalse(flow.exposeHasExternalActivitiesAsCaseData(externalMessage));
	}

	@Test
	void hasExternalActivitiesAsCaseDataIsTrueForNonEmptyJsonArray() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getActivitiesAsCase()).thenReturn("[{}]");
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, mock(ExternalMessageProcessingFacade.class));
		assertTrue(flow.exposeHasExternalActivitiesAsCaseData(externalMessage));
	}

	@Test
	void prepareSelectedCaseDoesNotSyncExposuresForEmptyJsonArray() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getExposures()).thenReturn("[]");
		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertFalse(flow.wasPostBuildExposureCalled());
		verify(processingFacade, never()).saveCase(any(CaseDataDto.class));
	}

	@Test
	void prepareSelectedCaseDoesNotSyncActivitiesForEmptyJsonArray() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getActivitiesAsCase()).thenReturn("[]");
		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		CaseDataDto result = flow.exposePrepareSelectedCase(caze, externalMessage);

		assertSame(caze, result);
		assertFalse(flow.wasPostBuildActivitiesAsCaseCalled());
		verify(processingFacade, never()).saveCase(any(CaseDataDto.class));
	}

	@Test
	void postBuildActivitiesAsCaseDeserializesActivitiesIntoCase() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getActivitiesAsCase()).thenReturn("[{}]");
		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		when(processingFacade.saveCase(any(CaseDataDto.class))).thenAnswer(invocation -> invocation.getArgument(0));
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		flow.exposePrepareSelectedCase(caze, externalMessage);

		List<ActivityAsCaseDto> activities = caze.getEpiData().getActivitiesAsCase();
		assertNotNull(activities);
		assertEquals(1, activities.size());
		assertEquals(ActivityAsCaseType.UNKNOWN, activities.get(0).getActivityAsCaseType());
	}

	@Test
	void prepareSelectedCaseThrowsWhenExposureJsonIsInvalid() {
		ExternalMessageDto externalMessage = mockExternalMessage();
		when(externalMessage.getExposures()).thenReturn("[");

		ExternalMessageProcessingFacade processingFacade = mock(ExternalMessageProcessingFacade.class);
		TestDoctorDeclarationFlow flow = new TestDoctorDeclarationFlow(externalMessage, processingFacade);
		CaseDataDto caze = CaseDataDto.build(mock(PersonReferenceDto.class), Disease.CORONAVIRUS);

		assertThrows(IllegalStateException.class, () -> flow.exposePrepareSelectedCase(caze, externalMessage));
	}

	private ExternalMessageDto mockExternalMessage() {
		ExternalMessageDto externalMessage = mock(ExternalMessageDto.class);
		when(externalMessage.getTreatmentStarted()).thenReturn(null);
		when(externalMessage.getTreatmentNotApplicable()).thenReturn(null);
		return externalMessage;
	}

	private static class TestDoctorDeclarationFlow extends AbstractDoctorDeclarationMessageProcessingFlow {

		private boolean postBuildHospitalizationCalled;
		private boolean postBuildActivitiesAsCaseCalled;
		private boolean postBuildExposureCalled;
		private boolean activityPostBuildMutatesExposureData;

		private TestDoctorDeclarationFlow(ExternalMessageDto externalMessage, ExternalMessageProcessingFacade processingFacade) {
			super(externalMessage, mock(UserDto.class), mock(ExternalMessageMapper.class), processingFacade);
		}

		private CaseDataDto exposePrepareSelectedCase(CaseDataDto caze, ExternalMessageDto externalMessage) {
			return prepareSelectedCase(caze, externalMessage);
		}

		private boolean exposeHasCaseSymptomsMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			return hasCaseSymptomsMismatch(caze, externalMessage);
		}

		private boolean exposeHasCaseDataMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			return hasCaseDataMismatch(caze, externalMessage);
		}

		private boolean exposeHasCaseHealthConditionsMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			return hasCaseHealthConditionsMismatch(caze, externalMessage);
		}

		private boolean exposeHasCaseHospitalizationMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			return hasCaseHospitalizationMismatch(caze, externalMessage);
		}

		private boolean exposeHasCaseActivitiesAsCaseMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
			return hasCaseActivitiesAsCaseMismatch(caze, externalMessage);
		}

		private boolean exposeHasExternalExposureData(ExternalMessageDto externalMessage) {
			return hasExternalExposureData(externalMessage);
		}

		private boolean exposeHasExternalActivitiesAsCaseData(ExternalMessageDto externalMessage) {
			return hasExternalActivitiesAsCaseData(externalMessage);
		}

		private boolean wasPostBuildHospitalizationCalled() {
			return postBuildHospitalizationCalled;
		}

		private boolean wasPostBuildActivitiesAsCaseCalled() {
			return postBuildActivitiesAsCaseCalled;
		}

		private boolean wasPostBuildExposureCalled() {
			return postBuildExposureCalled;
		}

		private void enableActivityPostBuildMutatesExposureData() {
			activityPostBuildMutatesExposureData = true;
		}

		@Override
		protected void postBuildHospitalization(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {
			postBuildHospitalizationCalled = true;
		}

		@Override
		protected void postBuildActivitiesAsCase(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {
			postBuildActivitiesAsCaseCalled = true;
			super.postBuildActivitiesAsCase(caseDto, externalMessageDto);

			if (activityPostBuildMutatesExposureData) {
				caseDto.getEpiData().setExposureDetailsKnown(YesNoUnknown.NO);
			}
		}

		@Override
		protected void postBuildExposure(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {
			postBuildExposureCalled = true;
			super.postBuildExposure(caseDto, externalMessageDto);
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
		protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void handlePickOrCreateEntry(
			List<CaseSelectionDto> similarCases,
			List<SimilarContactDto> similarContacts,
			List<SimilarEventParticipantDto> similarEventParticipants,
			ExternalMessageDto externalMessage,
			HandlerCallback<de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult> callback) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void handleCreateCase(
			CaseDataDto caze,
			PersonDto person,
			ExternalMessageDto externalMessage,
			HandlerCallback<CaseDataDto> callback) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void handleCreateContact(
			ContactDto contact,
			PersonDto person,
			ExternalMessageDto externalMessage,
			HandlerCallback<ContactDto> callback) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void handlePickOrCreateEvent(ExternalMessageDto externalMessage, HandlerCallback<PickOrCreateEventResult> callback) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void handleCreateEventParticipant(
			EventParticipantDto eventParticipant,
			EventDto event,
			ExternalMessageDto externalMessage,
			HandlerCallback<EventParticipantDto> callback) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void handlePickOrCreateSample(
			List<SampleDto> similarSamples,
			List<SampleDto> otherSamples,
			ExternalMessageDto labMessage,
			int sampleReportIndex,
			HandlerCallback<PickOrCreateSampleResult> callback) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void handleEditSample(
			SampleDto sample,
			List<PathogenTestDto> newPathogenTests,
			ExternalMessageDto labMessage,
			ExternalMessageMapper mapper,
			boolean lastSample,
			HandlerCallback<SampleAndPathogenTests> callback) {
			throw new UnsupportedOperationException();
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
			throw new UnsupportedOperationException();
		}

		@Override
		protected CompletionStage<Boolean> confirmPickExistingEventParticipant() {
			return CompletableFuture.completedFuture(true);
		}

		@Override
		protected CompletionStage<Void> notifyCorrectionsSaved() {
			return CompletableFuture.completedFuture(null);
		}

		@Override
		protected void markExternalMessageAsProcessed(
			ExternalMessageDto externalMessage,
			ProcessingResult<ExternalMessageProcessingResult> result,
			SurveillanceReportDto surveillanceReport) {
			throw new UnsupportedOperationException();
		}
	}
}
