package de.symeda.sormas.backend.externalmessage.survey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.survey.*;
import de.symeda.sormas.api.patch.*;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.survey.SurveyFacadeEjb;
import de.symeda.sormas.backend.survey.SurveyTokenFacadeEjb;

class AutomaticSurveyResponseProcessorTest extends AbstractUnitTest {

	@Mock
	private DataPatcher dataPatcher;

	@Mock
	private SurveyFacadeEjb.SurveyFacadeEjbLocal surveyFacade;

	@Mock
	private SurveyTokenFacadeEjb.SurveyTokenFacadeEjbLocal surveyTokenFacade;

	@InjectMocks
	private AutomaticSurveyResponseProcessor victim;

	@Test
	void processSurveyResponses_singleMessage_patchApplied_returnsDone() throws Exception {
		// PREPARE
		String caseUuid = DataHelper.createUuid();
		String token = "TOKEN-1";
		String externalSurveyId = "SURVEY-EXT-1";

		ExternalMessageDto message = buildMessage(externalSurveyId, token, null);
		SurveyTokenDto surveyToken = buildSurveyToken(token, caseUuid);
		SurveyDto survey = buildSurvey(externalSurveyId);

		when(surveyFacade.getByExternalIds(List.of(externalSurveyId))).thenReturn(List.of(survey));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(surveyToken));
		when(dataPatcher.patch(any())).thenReturn(new DataPatchResponse().setApplied(true));

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(message));

		// CHECK
		assertEquals(1, results.size());
		assertEquals(ProcessingResultStatus.DONE, results.get(0).getResultStatus());
		assertEquals(ExternalMessageStatus.PROCESSED, message.getStatus());
		assertNotNull(message.getSurveyResponseData().getLatest().getResult());
	}

	@Test
	void processSurveyResponses_emptyMessageList_returnsEmptyResult() throws Exception {
		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of());

		// CHECK
		assertEquals(0, results.size());
		verify(surveyFacade, never()).getByExternalIds(any());
	}

	@Test
	void processSurveyResponses_multipleMessages_allSucceed_allReturnDone() throws Exception {
		// PREPARE
		String caseUuid1 = DataHelper.createUuid();
		String caseUuid2 = DataHelper.createUuid();
		SurveyTokenDto token1 = buildSurveyToken("T1", caseUuid1);
		SurveyTokenDto token2 = buildSurveyToken("T2", caseUuid2);

		ExternalMessageDto msg1 = buildMessage("S1", "T1", null);
		ExternalMessageDto msg2 = buildMessage("S2", "T2", null);
		SurveyDto survey1 = buildSurvey("S1");
		SurveyDto survey2 = buildSurvey("S2");

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey1, survey2));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(token1, token2));
		when(dataPatcher.patch(any())).thenReturn(new DataPatchResponse().setApplied(true));

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(msg1, msg2));

		// CHECK
		assertEquals(2, results.size());
		assertEquals(ProcessingResultStatus.DONE, results.get(0).getResultStatus());
		assertEquals(ProcessingResultStatus.DONE, results.get(1).getResultStatus());
		verify(dataPatcher, times(2)).patch(any());
	}

	@Test
	void processSurveyResponses_skipIfAlreadyProcessed_resultAlreadySet_returnsCanceled() throws Exception {
		// PREPARE - message already has a result
		ExternalMessageSurveyResponseResult existingResult = new ExternalMessageSurveyResponseResult().setCaseUuid(DataHelper.createUuid());
		ExternalMessageDto message = buildMessage("S1", "T1", existingResult);
		message.getSurveyResponseData().getLatest().getRequest().setSkipIfAlreadyProcessed(true);

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of());
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of());

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(message));

		// CHECK
		assertEquals(ProcessingResultStatus.CANCELED, results.get(0).getResultStatus());
		verify(dataPatcher, never()).patch(any());
	}

	@Test
	void processSurveyResponses_skipIfAlreadyProcessed_false_resultAlreadySet_reprocesses() throws Exception {
		// PREPARE - message already has a result but skipIfAlreadyProcessed=false
		String caseUuid = DataHelper.createUuid();
		ExternalMessageSurveyResponseResult existingResult = new ExternalMessageSurveyResponseResult().setCaseUuid(caseUuid);
		ExternalMessageDto message = buildMessage("S1", "T1", existingResult);
		message.getSurveyResponseData().getLatest().getRequest().setSkipIfAlreadyProcessed(false);

		SurveyTokenDto surveyToken = buildSurveyToken("T1", caseUuid);
		SurveyDto survey = buildSurvey("S1");

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(surveyToken));
		when(dataPatcher.patch(any())).thenReturn(new DataPatchResponse().setApplied(true));

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(message));

		// CHECK
		assertEquals(ProcessingResultStatus.DONE, results.get(0).getResultStatus());
		verify(dataPatcher).patch(any());
	}

	@Test
	void processSurveyResponses_skipIfAlreadyProcessed_resultNull_processes() throws Exception {
		// PREPARE - no result yet, skipIfAlreadyProcessed=true should not block first processing
		String caseUuid = DataHelper.createUuid();
		ExternalMessageDto message = buildMessage("S1", "T1", null);
		message.getSurveyResponseData().getLatest().getRequest().setSkipIfAlreadyProcessed(true);

		SurveyTokenDto surveyToken = buildSurveyToken("T1", caseUuid);
		SurveyDto survey = buildSurvey("S1");

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(surveyToken));
		when(dataPatcher.patch(any())).thenReturn(new DataPatchResponse().setApplied(true));

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(message));

		// CHECK
		assertEquals(ProcessingResultStatus.DONE, results.get(0).getResultStatus());
	}

	@Test
	void processSurveyResponses_tokenNotInAvailableTokens_returnsCanceled() throws Exception {
		// PREPARE - token in message doesn't match any fetched survey token
		ExternalMessageDto message = buildMessage("S1", "TOKEN-UNKNOWN", null);
		SurveyDto survey = buildSurvey("S1");
		SurveyTokenDto unrelatedToken = buildSurveyToken("DIFFERENT-TOKEN", DataHelper.createUuid());

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(unrelatedToken));

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(message));

		// CHECK
		assertEquals(ProcessingResultStatus.CANCELED, results.get(0).getResultStatus());
		verify(dataPatcher, never()).patch(any());
	}

	@Test
	void processSurveyResponses_surveyNotFound_tokenListEmpty_returnsCanceled() throws Exception {
		// PREPARE - survey not found → no tokens fetched
		ExternalMessageDto message = buildMessage("S1", "T1", null);

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of());
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of());

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(message));

		// CHECK
		assertEquals(ProcessingResultStatus.CANCELED, results.get(0).getResultStatus());
	}

	@Test
	void processSurveyResponses_patchNotApplied_resultSetOnWrapper_statusCanceled() throws Exception {
		// PREPARE
		String caseUuid = DataHelper.createUuid();
		ExternalMessageDto message = buildMessage("S1", "T1", null);
		SurveyTokenDto surveyToken = buildSurveyToken("T1", caseUuid);
		SurveyDto survey = buildSurvey("S1");

		DataPatchResponse failedResponse = new DataPatchResponse().setApplied(false);
		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(surveyToken));
		when(dataPatcher.patch(any())).thenReturn(failedResponse);

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(message));

		// CHECK
		assertEquals(ProcessingResultStatus.CANCELED, results.get(0).getResultStatus());
		// Result IS set even when patch fails — records the attempt
		assertNotNull(message.getSurveyResponseData().getLatest().getResult());
		assertEquals(failedResponse, message.getSurveyResponseData().getLatest().getResult().getPatchResponse());
		// Message is NOT marked as processed when patch fails
		assertEquals(ExternalMessageStatus.UNPROCESSED, message.getStatus());
	}

	@Test
	void processSurveyResponses_runtimeException_exceptionCaptured_resultStatusIsNull() throws Exception {
		// PREPARE
		String caseUuid = DataHelper.createUuid();
		ExternalMessageDto message = buildMessage("S1", "T1", null);
		SurveyTokenDto surveyToken = buildSurveyToken("T1", caseUuid);
		SurveyDto survey = buildSurvey("S1");

		RuntimeException thrown = new RuntimeException("patching failed");
		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(surveyToken));
		when(dataPatcher.patch(any())).thenThrow(thrown);

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(message));

		// CHECK
		SurveyResponseProcessingResult result = results.get(0);
		assertEquals(thrown, result.getRuntimeException());
		// BUG: resultStatus is never set in the catch block — it remains null
		assertNull(result.getResultStatus());
	}

	@Test
	void processSurveyResponses_runtimeException_doesNotAbortOtherMessages() throws Exception {
		// PREPARE - first message throws, second should still be processed
		String caseUuid2 = DataHelper.createUuid();
		ExternalMessageDto msg1 = buildMessage("S1", "T1", null);
		ExternalMessageDto msg2 = buildMessage("S2", "T2", null);
		SurveyTokenDto token1 = buildSurveyToken("T1", DataHelper.createUuid());
		SurveyTokenDto token2 = buildSurveyToken("T2", caseUuid2);
		SurveyDto survey1 = buildSurvey("S1");
		SurveyDto survey2 = buildSurvey("S2");

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey1, survey2));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(token1, token2));
		when(dataPatcher.patch(any())).thenThrow(new RuntimeException("fail")).thenReturn(new DataPatchResponse().setApplied(true));

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(msg1, msg2));

		// CHECK
		assertEquals(2, results.size());
		assertNotNull(results.get(0).getRuntimeException());
		assertEquals(ProcessingResultStatus.DONE, results.get(1).getResultStatus());
	}

	@Test
	void processSurveyResponses_surveyTokenUpdatedWithResponseData() throws Exception {
		// PREPARE
		String caseUuid = DataHelper.createUuid();
		Date responseDate = new Date();
		String respondentId = "RESPONDENT-123";
		String token = "TOKEN-XYZ";

		ExternalMessageDto message = buildMessage("S1", token, null);
		message.getSurveyResponseData().getLatest().getRequest().setResponseReceivedDate(responseDate).setExternalRespondentId(respondentId);

		SurveyTokenDto surveyToken = buildSurveyToken(token, caseUuid);
		SurveyDto survey = buildSurvey("S1");

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(surveyToken));
		when(dataPatcher.patch(any())).thenReturn(new DataPatchResponse().setApplied(true));

		// EXECUTE
		victim.processSurveyResponses(List.of(message));

		// CHECK - token was updated and saved before patch
		ArgumentCaptor<SurveyTokenDto> tokenCaptor = forClass(SurveyTokenDto.class);
		verify(surveyTokenFacade).save(tokenCaptor.capture());
		SurveyTokenDto saved = tokenCaptor.getValue();
		assertEquals(true, saved.isResponseReceived());
		assertEquals(responseDate, saved.getResponseReceivedDate());
		assertEquals(respondentId, saved.getExternalRespondentId());
	}

	@Test
	void processSurveyResponses_surveyTokenSavedEvenWhenPatchFails() throws Exception {
		// PREPARE
		ExternalMessageDto message = buildMessage("S1", "T1", null);
		SurveyTokenDto surveyToken = buildSurveyToken("T1", DataHelper.createUuid());
		SurveyDto survey = buildSurvey("S1");

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(surveyToken));
		when(dataPatcher.patch(any())).thenReturn(new DataPatchResponse().setApplied(false));

		// EXECUTE
		victim.processSurveyResponses(List.of(message));

		// CHECK
		verify(surveyTokenFacade).save(any());
	}

	@Test
	void processSurveyResponses_patchRequestBuiltCorrectlyFromSurveyRequest() throws Exception {
		// PREPARE
		String caseUuid = DataHelper.createUuid();
		String origin = "NGSurvey";
		PatchDictionary patchDict = new PatchDictionary();
		patchDict.put("person.firstName", "Alice");

		ExternalMessageDto message = buildMessage("S1", "T1", null);
		message.getSurveyResponseData()
			.getLatest()
			.getRequest()
			.setOrigin(origin)
			.setPatchDictionary(patchDict)
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setEmptyValueBehavior(EmptyValueBehavior.REPLACE)
			.setPatchedInCaseOfFailures(true)
			.setAllowFallbackValues(false)
			.setInputLanguages(List.of(Language.FR));

		SurveyTokenDto surveyToken = buildSurveyToken("T1", caseUuid);
		SurveyDto survey = buildSurvey("S1");

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(surveyToken));
		when(dataPatcher.patch(any())).thenReturn(new DataPatchResponse().setApplied(true));

		// EXECUTE
		victim.processSurveyResponses(List.of(message));

		// CHECK
		ArgumentCaptor<CaseDataPatchRequest> patchCaptor = forClass(CaseDataPatchRequest.class);
		verify(dataPatcher).patch(patchCaptor.capture());
		CaseDataPatchRequest builtRequest = patchCaptor.getValue();

		assertEquals(caseUuid, builtRequest.getCaseUuid());
		assertEquals(origin, builtRequest.getOrigin());
		assertEquals(patchDict, builtRequest.getPatchDictionary());
		assertEquals(DataReplacementStrategy.ALWAYS, builtRequest.getReplacementStrategy());
		assertEquals(EmptyValueBehavior.REPLACE, builtRequest.getEmptyValueBehavior());
		assertEquals(true, builtRequest.isPatchedInCaseOfFailures());
		assertEquals(false, builtRequest.isAllowFallbackValues());
		assertEquals(List.of(Language.FR), builtRequest.getInputLanguages());
	}

	@Test
	void processSurveyResponses_patchApplied_resultContainsCaseUuidAndResponse() throws Exception {
		// PREPARE
		String caseUuid = DataHelper.createUuid();
		ExternalMessageDto message = buildMessage("S1", "T1", null);
		SurveyTokenDto surveyToken = buildSurveyToken("T1", caseUuid);
		SurveyDto survey = buildSurvey("S1");
		DataPatchResponse patchResponse = new DataPatchResponse().setApplied(true);

		when(surveyFacade.getByExternalIds(any())).thenReturn(List.of(survey));
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(surveyToken));
		when(dataPatcher.patch(any())).thenReturn(patchResponse);

		// EXECUTE
		victim.processSurveyResponses(List.of(message));

		// CHECK
		ExternalMessageSurveyResponseResult result = message.getSurveyResponseData().getLatest().getResult();
		assertNotNull(result);
		assertEquals(caseUuid, result.getCaseUuid());
		assertEquals(patchResponse, result.getPatchResponse());
	}

	@Test
	void processSurveyResponses_duplicateExternalSurveyId_differentTokens_firstMessageCanceled() throws Exception {
		// PREPARE
		String caseUuid1 = DataHelper.createUuid();
		String caseUuid2 = DataHelper.createUuid();
		ExternalMessageDto msg1 = buildMessage("SAME-SURVEY", "TOKEN-A", null);
		ExternalMessageDto msg2 = buildMessage("SAME-SURVEY", "TOKEN-B", null);

		SurveyTokenDto tokenB = buildSurveyToken("TOKEN-B", caseUuid2);
		SurveyDto survey = buildSurvey("SAME-SURVEY");

		when(surveyFacade.getByExternalIds(List.of("SAME-SURVEY"))).thenReturn(List.of(survey));
		// The bug: only one token is passed to getBySurveyReferenceTokenTuples because the map
		// stores only one token per externalSurveyId (last one wins)
		when(surveyTokenFacade.getBySurveyReferenceTokenTuples(any())).thenReturn(List.of(tokenB));
		when(dataPatcher.patch(any())).thenReturn(new DataPatchResponse().setApplied(true));

		// EXECUTE
		List<SurveyResponseProcessingResult> results = victim.processSurveyResponses(List.of(msg1, msg2));

		// CHECK - BUG: msg1 (TOKEN-A) is canceled because TOKEN-A was lost from the map
		assertEquals(ProcessingResultStatus.CANCELED, results.get(0).getResultStatus());
		assertEquals(ProcessingResultStatus.DONE, results.get(1).getResultStatus());
	}

	private static ExternalMessageDto buildMessage(String externalSurveyId, String token, ExternalMessageSurveyResponseResult result) {
		ExternalMessageSurveyResponseRequest request = new ExternalMessageSurveyResponseRequest().setExternalSurveyId(externalSurveyId)
			.setToken(token)
			.setPatchDictionary(new PatchDictionary())
			.setExcludedPatchDictionary(new PatchDictionary());

		ExternalMessageSurveyResponseWrapper wrapper = new ExternalMessageSurveyResponseWrapper().setRequest(request).setResult(result);

		ExternalSurveyResponseData responseData = new ExternalSurveyResponseData().setOriginal(wrapper);

		ExternalMessageDto message = ExternalMessageDto.build();
		message.setSurveyResponseData(responseData);

		return message;
	}

	private static SurveyTokenDto buildSurveyToken(String token, String caseUuid) {
		SurveyTokenDto dto = new SurveyTokenDto();
		dto.setUuid(DataHelper.createUuid());
		dto.setToken(token);
		dto.setCaseAssignedTo(new CaseReferenceDto(caseUuid));
		return dto;
	}

	private static SurveyDto buildSurvey(String externalId) {
		SurveyDto survey = SurveyDto.build();
		survey.setExternalId(externalId);
		return survey;
	}

}
