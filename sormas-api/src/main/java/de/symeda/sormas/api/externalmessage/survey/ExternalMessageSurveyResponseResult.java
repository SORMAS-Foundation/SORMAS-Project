package de.symeda.sormas.api.externalmessage.survey;

import java.util.Objects;

import de.symeda.sormas.api.patch.DataPatchResponse;

public class ExternalMessageSurveyResponseResult {

	private String caseUuid;

	private DataPatchResponse patchResponse;

	public String getCaseUuid() {
		return caseUuid;
	}

	public ExternalMessageSurveyResponseResult setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
		return this;
	}

	public DataPatchResponse getPatchResponse() {
		return patchResponse;
	}

	public ExternalMessageSurveyResponseResult setPatchResponse(DataPatchResponse patchResponse) {
		this.patchResponse = patchResponse;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ExternalMessageSurveyResponseResult that = (ExternalMessageSurveyResponseResult) o;
		return Objects.equals(caseUuid, that.caseUuid) && Objects.equals(patchResponse, that.patchResponse);
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseUuid, patchResponse);
	}

	@Override
	public String toString() {
		return "ExternalMessageSurveyResponseResult{" + "caseUuid='" + caseUuid + '\'' + ", patchResponse=" + patchResponse + '}';
	}
}
