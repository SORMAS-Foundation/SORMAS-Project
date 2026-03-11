package de.symeda.sormas.backend.externalmessage.survey;

import java.util.Objects;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;

public class SurveyResponseProcessingResultWrapper {

	private ExternalMessageDto externalMessage;
	private ProcessingResult<ExternalMessageProcessingResult> result;
	private RuntimeException runtimeException;

	public ExternalMessageDto getExternalMessage() {
		return externalMessage;
	}

	public SurveyResponseProcessingResultWrapper setExternalMessage(ExternalMessageDto externalMessage) {
		this.externalMessage = externalMessage;
		return this;
	}

	public ProcessingResult<ExternalMessageProcessingResult> getResult() {
		return result;
	}

	public SurveyResponseProcessingResultWrapper setResult(ProcessingResult<ExternalMessageProcessingResult> result) {
		this.result = result;
		return this;
	}

	public RuntimeException getRuntimeException() {
		return runtimeException;
	}

	public SurveyResponseProcessingResultWrapper setRuntimeException(RuntimeException runtimeException) {
		this.runtimeException = runtimeException;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		SurveyResponseProcessingResultWrapper that = (SurveyResponseProcessingResultWrapper) o;
		return Objects.equals(externalMessage, that.externalMessage)
			&& Objects.equals(result, that.result)
			&& Objects.equals(runtimeException, that.runtimeException);
	}

	@Override
	public int hashCode() {
		return Objects.hash(externalMessage, result, runtimeException);
	}

	@Override
	public String toString() {
		return "SurveyResponseProcessingResultWrapper{" + "externalMessage=" + externalMessage + ", result=" + result + ", runtimeException="
			+ runtimeException + '}';
	}
}
