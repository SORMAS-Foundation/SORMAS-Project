package de.symeda.sormas.api.patch.mapping;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Language;

/**
 * Specifies how to patch a single value into a targetType.
 */
public class ValuePatchRequest<T> {

	@NotNull
	private Object value;

	@NotNull
	private Class<T> targetType;

	/**
	 * To be able to support I18n inputs the input languages can be passed, system locale by default.
	 */
	@Nullable
	private List<Language> inputLanguages;

	/**
	 * If true, for enumeration-like targetTypes the default value will be used.
	 * Mostly "OTHER".
	 */
	private boolean allowFallbackValues = true;

	public Object getValue() {
		return value;
	}

	public ValuePatchRequest<T> setValue(Object value) {
		this.value = value;
		return this;
	}

	public Class<?> getTargetType() {
		return targetType;
	}

	public ValuePatchRequest<T> setTargetType(Class<T> targetType) {
		this.targetType = targetType;
		return this;
	}

	public List<Language> getInputLanguages() {
		return inputLanguages;
	}

	public ValuePatchRequest<T> setInputLanguages(List<Language> inputLanguages) {
		this.inputLanguages = inputLanguages;
		return this;
	}

	public boolean isAllowFallbackValues() {
		return allowFallbackValues;
	}

	public ValuePatchRequest<T> setAllowFallbackValues(boolean allowDefaultValues) {
		this.allowFallbackValues = allowDefaultValues;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ValuePatchRequest<?> that = (ValuePatchRequest<?>) o;
		return allowFallbackValues == that.allowFallbackValues
			&& Objects.equals(value, that.value)
			&& Objects.equals(targetType, that.targetType)
			&& Objects.equals(inputLanguages, that.inputLanguages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, targetType, inputLanguages, allowFallbackValues);
	}

	@Override
	public String toString() {
		return "ValuePatchRequest{" + "value=" + value + ", targetType=" + targetType + ", inputLanguages=" + inputLanguages + ", allowDefaultValues="
			+ allowFallbackValues + '}';
	}
}
