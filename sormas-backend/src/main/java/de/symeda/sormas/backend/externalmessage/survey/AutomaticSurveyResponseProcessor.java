package de.symeda.sormas.backend.externalmessage.survey;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;

@ApplicationScoped
public class AutomaticSurveyResponseProcessor {

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public List<SurveyResponseProcessingResultWrapper> processSurveyResponses(List<ExternalMessageDto> externalMessage)
		throws InterruptedException, ExecutionException {

		return List.of();
	}
}
