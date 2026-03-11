package de.symeda.sormas.api.externalmessage.survey;

import java.util.Objects;

public class ExternalMessageSurveyResponseWrapper {

	private ExternalMessageSurveyResponseRequest request;
	private ExternalMessageSurveyResponseResult result;

	public ExternalMessageSurveyResponseRequest getRequest() {
		return request;
	}

	public ExternalMessageSurveyResponseWrapper setRequest(ExternalMessageSurveyResponseRequest request) {
		this.request = request;
		return this;
	}

	public ExternalMessageSurveyResponseResult getResult() {
		return result;
	}

	public ExternalMessageSurveyResponseWrapper setResult(ExternalMessageSurveyResponseResult result) {
		this.result = result;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ExternalMessageSurveyResponseWrapper that = (ExternalMessageSurveyResponseWrapper) o;
		return Objects.equals(request, that.request) && Objects.equals(result, that.result);
	}

	@Override
	public int hashCode() {
		return Objects.hash(request, result);
	}

	@Override
	public String toString() {
		return "ExternalMessageSurveyResponseWrapper{" + "request=" + request + ", result=" + result + '}';
	}
}
