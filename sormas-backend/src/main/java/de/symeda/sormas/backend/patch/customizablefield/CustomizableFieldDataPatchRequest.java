package de.symeda.sormas.backend.patch.customizablefield;

import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.PlainSinglePatchResult;

public final class CustomizableFieldDataPatchRequest {

	@NotNull
	private CaseDataPatchRequest caseDataPatchRequest;

	@NotNull
	private List<PlainSinglePatchResult> patchingTuples;

	@NotNull
	private CaseDataDto caseDataDto;

	public CaseDataPatchRequest getCaseDataPatchRequest() {
		return caseDataPatchRequest;
	}

	public CustomizableFieldDataPatchRequest setCaseDataPatchRequest(CaseDataPatchRequest caseDataPatchRequest) {
		this.caseDataPatchRequest = caseDataPatchRequest;
		return this;
	}

	public List<PlainSinglePatchResult> getPatchingTuples() {
		return patchingTuples;
	}

	public CustomizableFieldDataPatchRequest setPatchingTuples(List<PlainSinglePatchResult> patchingTuples) {
		this.patchingTuples = patchingTuples;
		return this;
	}

	public CaseDataDto getCaseDataDto() {
		return caseDataDto;
	}

	public CustomizableFieldDataPatchRequest setCaseDataDto(CaseDataDto caseDataDto) {
		this.caseDataDto = caseDataDto;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CustomizableFieldDataPatchRequest request = (CustomizableFieldDataPatchRequest) o;
		return Objects.equals(caseDataPatchRequest, request.caseDataPatchRequest)
			&& Objects.equals(patchingTuples, request.patchingTuples)
			&& Objects.equals(caseDataDto, request.caseDataDto);
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseDataPatchRequest, patchingTuples, caseDataDto);
	}

	@Override
	public String toString() {
		return "Request{" + "caseDataPatchRequest=" + caseDataPatchRequest + ", patchingTuples=" + patchingTuples + ", caseDataDto=" + caseDataDto
			+ '}';
	}
}
