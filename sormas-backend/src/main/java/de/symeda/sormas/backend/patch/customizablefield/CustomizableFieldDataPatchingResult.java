package de.symeda.sormas.backend.patch.customizablefield;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.patch.DataPatchFailure;

public class CustomizableFieldDataPatchingResult {

	@NotNull
	private PatchField field;

	@Nullable
	private Object value;

	@Nullable
	private DataPatchFailure failure;

	@Nullable
	private CustomizableFieldValueDto valueMappingResult;

	public PatchField getField() {
		return field;
	}

	public CustomizableFieldDataPatchingResult setField(PatchField field) {
		this.field = field;
		return this;
	}

	@Nullable
	public Object getValue() {
		return value;
	}

	public CustomizableFieldDataPatchingResult setValue(@Nullable Object value) {
		this.value = value;
		return this;
	}

	@Nullable
	public DataPatchFailure getFailure() {
		return failure;
	}

	public CustomizableFieldDataPatchingResult setFailure(@Nullable DataPatchFailure failure) {
		this.failure = failure;
		return this;
	}

	@Nullable
	public CustomizableFieldValueDto getValueMappingResult() {
		return valueMappingResult;
	}

	public CustomizableFieldDataPatchingResult setValueMappingResult(@Nullable CustomizableFieldValueDto valueMappingResult) {
		this.valueMappingResult = valueMappingResult;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CustomizableFieldDataPatchingResult that = (CustomizableFieldDataPatchingResult) o;
		return Objects.equals(field, that.field)
			&& Objects.equals(value, that.value)
			&& Objects.equals(failure, that.failure)
			&& Objects.equals(valueMappingResult, that.valueMappingResult);
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, value, failure, valueMappingResult);
	}

	@Override
	public String toString() {
		return "CustomizableFieldDataPatchingResult{" + "field=" + field + ", value=" + value + ", failure=" + failure + ", valueMappingResult="
			+ valueMappingResult + '}';
	}
}
