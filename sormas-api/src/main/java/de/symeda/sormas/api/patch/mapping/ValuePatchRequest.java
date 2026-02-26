package de.symeda.sormas.api.patch.mapping;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Language;

public class ValuePatchRequest {

	@NotNull
	private Object value;

	@NotNull
	private Class<?> targetType;

	/**
	 * Languages translations will be fetched in that order, so can have minor performance impact.
	 */
	@Nullable
	private List<Language> inputLanguages;

	/**
	 * If true, for enumeration-like targetTypes the default value will be used.
	 * Mostly "OTHER".
	 */
	private boolean allowDefaultValues = true;

	public Object getValue() {
		return value;
	}

	public ValuePatchRequest setValue(Object value) {
		this.value = value;
		return this;
	}

	public Class<?> getTargetType() {
		return targetType;
	}

	public ValuePatchRequest setTargetType(Class<?> targetType) {
		this.targetType = targetType;
		return this;
	}

	public List<Language> getInputLanguages() {
		return inputLanguages;
	}

	public ValuePatchRequest setInputLanguages(List<Language> inputLanguages) {
		this.inputLanguages = inputLanguages;
		return this;
	}

	public boolean isAllowDefaultValues() {
		return allowDefaultValues;
	}

	public ValuePatchRequest setAllowDefaultValues(boolean allowDefaultValues) {
		this.allowDefaultValues = allowDefaultValues;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ValuePatchRequest that = (ValuePatchRequest) o;
		return allowDefaultValues == that.allowDefaultValues
			&& Objects.equals(value, that.value)
			&& Objects.equals(targetType, that.targetType)
			&& Objects.equals(inputLanguages, that.inputLanguages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, targetType, inputLanguages, allowDefaultValues);
	}

	@Override
	public String toString() {
		return "ValuePatchRequest{" + "value=" + value + ", targetType=" + targetType + ", inputLanguages=" + inputLanguages + ", allowDefaultValues="
			+ allowDefaultValues + '}';
	}
}
