package de.symeda.sormas.backend.externalmessage.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseRequest;
import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseResult;
import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseWrapper;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.DataPatchResponse;
import de.symeda.sormas.api.patch.DataPatcher;
import de.symeda.sormas.api.survey.SurveyReferenceDto;
import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;
import de.symeda.sormas.backend.externalmessage.labmessage.ExternalMessageProcessingFacadeEjbLocal;
import de.symeda.sormas.backend.survey.SurveyFacadeEjb;
import de.symeda.sormas.backend.survey.SurveyTokenFacadeEjb;
import de.symeda.sormas.backend.util.CollectorUtils;

@ApplicationScoped
public class AutomaticSurveyResponseProcessor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	private DataPatcher dataPatcher;

	@EJB
	private SurveyFacadeEjb.SurveyFacadeEjbLocal surveyFacade;

	@EJB
	private SurveyTokenFacadeEjb.SurveyTokenFacadeEjbLocal surveyTokenFacade;

	@EJB
	private ExternalMessageProcessingFacadeEjbLocal processingFacade;

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public List<SurveyResponseProcessingResult> processSurveyResponses(List<ExternalMessageDto> externalMessages)
		throws InterruptedException, ExecutionException {

		Map<String, String> tokenByExternalSurveyIdDictionary =
			externalMessages.stream().map(ExternalMessageDto::getSurveyResponseData).map(responseData -> {
				ExternalMessageSurveyResponseRequest request = responseData.getLatest().getRequest();
				return new Tuple<>(request.getExternalSurveyId(), request.getToken());
			}).collect(CollectorUtils.toOrderedNullSafeMap(Tuple::getFirst, Tuple::getSecond));

		List<String> externalSurveyIds = new ArrayList<>(tokenByExternalSurveyIdDictionary.keySet());

		List<Tuple<SurveyReferenceDto, String>> tokenBySurveyReferenceTuples = surveyFacade.getByExternalIds(externalSurveyIds)
			.stream()
			.map(survey -> new Tuple<>(survey.toReference(), tokenByExternalSurveyIdDictionary.get(survey.getExternalId())))
			.collect(Collectors.toList());

		List<SurveyTokenDto> surveyTokens = surveyTokenFacade.getBySurveyReferenceTokenTuples(tokenBySurveyReferenceTuples);

		return externalMessages.stream()
			.map((ExternalMessageDto externalMessage) -> tryProcessExternalMessage(externalMessage, surveyTokens))
			.collect(Collectors.toList());
	}

	private @NotNull SurveyResponseProcessingResult tryProcessExternalMessage(ExternalMessageDto externalMessage, List<SurveyTokenDto> surveyTokens) {
		SurveyResponseProcessingResult surveyResponseProcessingResult = new SurveyResponseProcessingResult().setExternalMessage(externalMessage);

		ExternalMessageSurveyResponseWrapper latestResponseWrapper = externalMessage.getSurveyResponseData().getLatest();
		ExternalMessageSurveyResponseRequest request = latestResponseWrapper.getRequest();

		// TODO: check this
		if (latestResponseWrapper.getResult() != null && request.isSkipIfAlreadyProcessed()) {
			logger.info(
				"Skipping survey response for external message [{}]: already processed and skipIfAlreadyProcessed=true",
				externalMessage.getUuid());
			return surveyResponseProcessingResult.setResultStatus(ProcessingResultStatus.CANCELED);
		}

		String requestToken = request.getToken();
		Optional<SurveyTokenDto> surveyToken =
			surveyTokens.stream().filter(tokenCandidate -> tokenCandidate.getToken().equals(requestToken)).findAny();

		if (surveyToken.isEmpty()) {
			logger.error("Token could not be found within available survey token DTOs: [{}]. Survey response processing is cancelled.", requestToken);
			return surveyResponseProcessingResult.setResultStatus(ProcessingResultStatus.CANCELED);
		}

		try {
			SurveyTokenDto surveyTokenDto = surveyToken.orElseThrow();
			surveyTokenDto.setResponseReceived(true);
			surveyTokenDto.setResponseReceivedDate(request.getResponseReceivedDate());
			surveyTokenDto.setExternalRespondentId(request.getExternalRespondentId());

			surveyTokenFacade.save(surveyTokenDto);

			CaseDataPatchRequest dataPatchRequest = from(request, surveyTokenDto);

			DataPatchResponse response = dataPatcher.patch(dataPatchRequest);
			logger.debug("Patch: request: [{}], response: [{}]", request, response);

			latestResponseWrapper
				.setResult(new ExternalMessageSurveyResponseResult().setPatchResponse(response).setCaseUuid(dataPatchRequest.getCaseUuid()));

			if (!response.isApplied()) {
				return surveyResponseProcessingResult.setResultStatus(ProcessingResultStatus.CANCELED);
			}

			externalMessage.setStatus(ExternalMessageStatus.PROCESSED);
			processingFacade.saveExternalMessage(externalMessage);

			return surveyResponseProcessingResult.setResultStatus(ProcessingResultStatus.DONE);

		} catch (RuntimeException e) {
			logger.error(
				"Exception while patching survey response for external message: [{}]. Processing will continue for other messages",
				externalMessage.getUuid(),
				e);
			surveyResponseProcessingResult.setRuntimeException(e);
		}

		return surveyResponseProcessingResult;
	}

	private static @NotNull CaseDataPatchRequest from(ExternalMessageSurveyResponseRequest request, SurveyTokenDto surveyTokenDto) {
		CaseDataPatchRequest caseDataPatchRequest = new CaseDataPatchRequest();

		caseDataPatchRequest.setEmptyValueBehavior(request.getEmptyValueBehavior());
		caseDataPatchRequest.setReplacementStrategy(request.getReplacementStrategy());
		caseDataPatchRequest.setPatchedInCaseOfFailures(request.isPatchedInCaseOfFailures());
		caseDataPatchRequest.setOrigin(request.getOrigin());
		caseDataPatchRequest.setInputLanguages(request.getInputLanguages());
		caseDataPatchRequest.setCaseUuid(surveyTokenDto.getCaseAssignedTo().getUuid());
		caseDataPatchRequest.setPatchDictionary(request.getPatchDictionary());
		caseDataPatchRequest.setAllowFallbackValues(request.isAllowFallbackValues());

		return caseDataPatchRequest;
	}
}
