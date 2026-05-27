package de.symeda.sormas.backend.patch.customizablefield;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.SinglePatchResult;

public class CustomizableFieldSinglePatchingResult implements SinglePatchResult {

	@NotNull
	private PatchField field;

	@Nullable
	private Object value;

	@Nullable
	private DataPatchFailure failure;

	@Nullable
	private CustomizableFieldValueDto customizableFieldValue;

	public PatchField getField() {
		return field;
	}

	public CustomizableFieldSinglePatchingResult setField(PatchField field) {
		this.field = field;
		return this;
	}

	@Nullable
	public Object getValue() {
		return value;
	}

	public CustomizableFieldSinglePatchingResult setValue(@Nullable Object value) {
		this.value = value;
		return this;
	}

	@Nullable
	public DataPatchFailure getFailure() {
		return failure;
	}

	public CustomizableFieldSinglePatchingResult setFailure(@Nullable DataPatchFailure failure) {
		this.failure = failure;
		return this;
	}

	@Nullable
	public CustomizableFieldValueDto getCustomizableFieldValue() {
		return customizableFieldValue;
	}

	public CustomizableFieldSinglePatchingResult setCustomizableFieldValue(@Nullable CustomizableFieldValueDto customizableFieldValue) {
		this.customizableFieldValue = customizableFieldValue;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CustomizableFieldSinglePatchingResult that = (CustomizableFieldSinglePatchingResult) o;
		return Objects.equals(field, that.field)
			&& Objects.equals(value, that.value)
			&& Objects.equals(failure, that.failure)
			&& Objects.equals(customizableFieldValue, that.customizableFieldValue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, value, failure, customizableFieldValue);
	}

	@Override
	public String toString() {
		return "CustomizableFieldDataPatchingResult{" + "field=" + field + ", value=" + value + ", failure=" + failure + ", valueMappingResult="
			+ customizableFieldValue + '}';
	}
}
