package de.symeda.sormas.api.patch;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.externalmessage.survey.PatchField;

public class PlainSinglePatchResult implements SinglePatchResult {

	@NotNull
	private PatchField field;

	@Nullable
	private Object value;

	@Nullable
	private DataPatchFailure failure;

	@Override
	public PatchField getField() {
		return field;
	}

	public PlainSinglePatchResult setField(PatchField field) {
		this.field = field;
		return this;
	}

	@Nullable
	@Override
	public Object getValue() {
		return value;
	}

	public PlainSinglePatchResult setValue(@Nullable Object value) {
		this.value = value;
		return this;
	}

	@Nullable
	@Override
	public DataPatchFailure getFailure() {
		return failure;
	}

	public PlainSinglePatchResult setFailure(@Nullable DataPatchFailure failure) {
		this.failure = failure;
		return this;
	}

	@Override
	public String toString() {
		return "SinglePatchResult{" + "field=" + field + ", value=" + value + ", failure=" + failure + '}';
	}
}
