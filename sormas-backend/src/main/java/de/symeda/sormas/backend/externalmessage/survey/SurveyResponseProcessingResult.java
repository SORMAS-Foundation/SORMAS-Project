package de.symeda.sormas.backend.externalmessage.survey;

import java.util.Objects;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;

/**
 * Wrapper object once a SurveyResponse external message was processed.
 * Exceptions are caught to be able to continue with the next message from the list.
 */
public class SurveyResponseProcessingResult {

	private ExternalMessageDto externalMessage;
	private ProcessingResultStatus resultStatus;
	private RuntimeException runtimeException;

	public ExternalMessageDto getExternalMessage() {
		return externalMessage;
	}

	public SurveyResponseProcessingResult setExternalMessage(ExternalMessageDto externalMessage) {
		this.externalMessage = externalMessage;
		return this;
	}

	public ProcessingResultStatus getResultStatus() {
		return resultStatus;
	}

	public SurveyResponseProcessingResult setResultStatus(ProcessingResultStatus resultStatus) {
		this.resultStatus = resultStatus;
		return this;
	}

	public RuntimeException getRuntimeException() {
		return runtimeException;
	}

	public SurveyResponseProcessingResult setRuntimeException(RuntimeException runtimeException) {
		this.runtimeException = runtimeException;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		SurveyResponseProcessingResult that = (SurveyResponseProcessingResult) o;
		return Objects.equals(externalMessage, that.externalMessage)
			&& Objects.equals(resultStatus, that.resultStatus)
			&& Objects.equals(runtimeException, that.runtimeException);
	}

	@Override
	public int hashCode() {
		return Objects.hash(externalMessage, resultStatus, runtimeException);
	}

	@Override
	public String toString() {
		return "SurveyResponseProcessingResultWrapper{" + "externalMessage=" + externalMessage + ", result=" + resultStatus + ", runtimeException="
			+ runtimeException + '}';
	}
}
