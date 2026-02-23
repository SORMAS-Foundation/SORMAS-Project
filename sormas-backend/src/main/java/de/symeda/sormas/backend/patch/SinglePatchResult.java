package de.symeda.sormas.backend.patch;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.patch.DataPatchFailure;

public class SinglePatchResult {

	@NotNull
	private String fieldName;

	@Nullable
	private Object value;

	@Nullable
	private DataPatchFailure failure;

	public String getFieldName() {
		return fieldName;
	}

	public SinglePatchResult setFieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	@Nullable
	public Object getValue() {
		return value;
	}

	public SinglePatchResult setValue(@Nullable Object value) {
		this.value = value;
		return this;
	}

	@Nullable
	public DataPatchFailure getFailure() {
		return failure;
	}

	public SinglePatchResult setFailure(@Nullable DataPatchFailure failure) {
		this.failure = failure;
		return this;
	}
}
