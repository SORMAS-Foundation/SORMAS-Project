package de.symeda.sormas.api.patch.mapping;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.patch.SinglePatchResult;

public class GroupedFieldsResponse<T extends EntityDto> {

	/**
	 * In case of errors this might be null, otherwise should always be present to be stored at the very end of the patching processing.
	 */
	@Nullable
	private List<T> entityDto;

	/**
	 * Actual results from the original {@link GroupedFieldsRequest}.
	 */
	private List<SinglePatchResult> patchingResults;

	@Nullable
	public List<T> getEntityDto() {
		return entityDto;
	}

	public GroupedFieldsResponse<T> setEntityDto(@Nullable List<T> entityDto) {
		this.entityDto = entityDto;
		return this;
	}

	public List<SinglePatchResult> getPatchingResults() {
		return patchingResults;
	}

	public GroupedFieldsResponse<T> setPatchingResults(List<SinglePatchResult> patchingResults) {
		this.patchingResults = patchingResults;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		GroupedFieldsResponse<T> that = (GroupedFieldsResponse<T>) o;
		return Objects.equals(entityDto, that.entityDto) && Objects.equals(patchingResults, that.patchingResults);
	}

	@Override
	public int hashCode() {
		return Objects.hash(entityDto, patchingResults);
	}

	@Override
	public String toString() {
		return "GroupedFieldsResponse{" + "entityDto=" + entityDto + ", patchingResults=" + patchingResults + '}';
	}
}
