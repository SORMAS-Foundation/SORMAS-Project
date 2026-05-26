package de.symeda.sormas.backend.patch.customizablefield;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;

public class CustomizableFieldValuePatchRequest {

	@NotNull
	private Object value;

	@NotNull
	private CustomizableFieldType targetType;

	/**
	 * WARNING: will be mutated with {@link #getValue()}
	 */
	@NotNull
	private CustomizableFieldValueDto customizableFieldValueDto;

	public Object getValue() {
		return value;
	}

	public CustomizableFieldValuePatchRequest setValue(Object value) {
		this.value = value;
		return this;
	}

	public CustomizableFieldType getTargetType() {
		return targetType;
	}

	public CustomizableFieldValuePatchRequest setTargetType(CustomizableFieldType targetType) {
		this.targetType = targetType;
		return this;
	}

	public CustomizableFieldValueDto getCustomizableFieldValueDto() {
		return customizableFieldValueDto;
	}

	public CustomizableFieldValuePatchRequest setCustomizableFieldValueDto(CustomizableFieldValueDto customizableFieldValueDto) {
		this.customizableFieldValueDto = customizableFieldValueDto;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CustomizableFieldValuePatchRequest that = (CustomizableFieldValuePatchRequest) o;
		return Objects.equals(value, that.value)
			&& targetType == that.targetType
			&& Objects.equals(customizableFieldValueDto, that.customizableFieldValueDto);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, targetType, customizableFieldValueDto);
	}

	@Override
	public String toString() {
		return "CustomizableFieldValuePatchRequest{" + "value=" + value + ", targetType=" + targetType + ", customizableFieldValueDto="
			+ customizableFieldValueDto + '}';
	}
}
