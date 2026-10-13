package de.symeda.sormas.api.patch;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.externalmessage.survey.PatchField;

/**
 * In this context: plain means non-CustomizableField.
 */
public class PlainSinglePatchResult implements SinglePatchResult {

	@NotNull
	private PatchField field;

	@Nullable
	private DataPatchFailure failure;

	@Nullable
	private Object value;

	public PlainSinglePatchResult() {
	}

	public PlainSinglePatchResult(PatchField field, @Nullable DataPatchFailure failure, @Nullable Object value) {
		this.field = field;
		this.failure = failure;
		this.value = value;
	}

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
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		PlainSinglePatchResult that = (PlainSinglePatchResult) o;
		return Objects.equals(field, that.field) && Objects.equals(failure, that.failure) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, failure, value);
	}

	@Override
	public String toString() {
		return "SinglePatchResult{" + "field=" + field + ", value=" + value + ", failure=" + failure + '}';
	}
}
