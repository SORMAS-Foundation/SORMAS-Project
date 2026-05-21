package de.symeda.sormas.api.patch;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.externalmessage.survey.PatchField;

public class SinglePatchResult {

	@NotNull
	private PatchField field;

	@Nullable
	private Object value;

	@Nullable
	private DataPatchFailure failure;

	public PatchField getField() {
		return field;
	}

	public SinglePatchResult setField(PatchField field) {
		this.field = field;
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
