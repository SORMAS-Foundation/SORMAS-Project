package de.symeda.sormas.backend.patch.customizablefield;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Language;
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

	private boolean allowFallbackValues = true;

	@Nullable
	private List<Language> inputLanguages;

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

	public boolean isAllowFallbackValues() {
		return allowFallbackValues;
	}

	public CustomizableFieldValuePatchRequest setAllowFallbackValues(boolean allowFallbackValues) {
		this.allowFallbackValues = allowFallbackValues;
		return this;
	}

	@Nullable
	public List<Language> getInputLanguages() {
		return inputLanguages;
	}

	public CustomizableFieldValuePatchRequest setInputLanguages(@Nullable List<Language> inputLanguages) {
		this.inputLanguages = inputLanguages;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CustomizableFieldValuePatchRequest that = (CustomizableFieldValuePatchRequest) o;
		return allowFallbackValues == that.allowFallbackValues
			&& Objects.equals(value, that.value)
			&& targetType == that.targetType
			&& Objects.equals(customizableFieldValueDto, that.customizableFieldValueDto)
			&& Objects.equals(inputLanguages, that.inputLanguages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, targetType, customizableFieldValueDto, allowFallbackValues, inputLanguages);
	}

	@Override
	public String toString() {
		return "CustomizableFieldValuePatchRequest{" + "value=" + value + ", targetType=" + targetType + ", customizableFieldValueDto="
			+ customizableFieldValueDto + ", allowFallbackValues=" + allowFallbackValues + ", inputLanguages=" + inputLanguages + '}';
	}
}
