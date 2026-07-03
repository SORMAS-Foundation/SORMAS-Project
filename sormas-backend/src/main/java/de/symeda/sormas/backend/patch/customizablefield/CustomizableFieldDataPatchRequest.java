package de.symeda.sormas.backend.patch.customizablefield;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

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

	@NotNull
	private Map<CustomizableContextIndexKey, Supplier<String>> entityUuidDictionary;

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

	public Map<CustomizableContextIndexKey, Supplier<String>> getEntityUuidDictionary() {
		return entityUuidDictionary;
	}

	public CustomizableFieldDataPatchRequest setEntityUuidDictionary(Map<CustomizableContextIndexKey, Supplier<String>> entityUuidDictionary) {
		this.entityUuidDictionary = entityUuidDictionary;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CustomizableFieldDataPatchRequest that = (CustomizableFieldDataPatchRequest) o;
		return Objects.equals(caseDataPatchRequest, that.caseDataPatchRequest)
			&& Objects.equals(patchingTuples, that.patchingTuples)
			&& Objects.equals(caseDataDto, that.caseDataDto)
			&& Objects.equals(entityUuidDictionary, that.entityUuidDictionary);
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseDataPatchRequest, patchingTuples, caseDataDto, entityUuidDictionary);
	}

	@Override
	public String toString() {
		return "CustomizableFieldDataPatchRequest{" + "caseDataPatchRequest=" + caseDataPatchRequest + ", patchingTuples=" + patchingTuples
			+ ", caseDataDto=" + caseDataDto + ", entityUuidDictionary=" + entityUuidDictionary + '}';
	}
}
