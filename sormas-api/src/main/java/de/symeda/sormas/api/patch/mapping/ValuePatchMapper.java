package de.symeda.sormas.api.patch.mapping;

import java.util.Set;

import javax.validation.constraints.NotNull;

// TODO: check if "in-value-type" must be checked: add self check ?

/**
 * Contract to specify how a type must be mapped into a value, NOT field specific.
 */
public interface ValuePatchMapper extends Comparable<ValuePatchMapper> {

	int HIGH_PRECEDENCE = Integer.MIN_VALUE;
	int LOW_PRECEDENCE = Integer.MAX_VALUE;

	/**
	 * Can be used to add it to the default precedences values and a keep some "space between" the implementations ordering.
	 */
	int ORDER_CHUNK = 20;

	/**
	 *
	 * @param value
	 *            raw value type, must either by String or the actual type.
	 * @param targetType
	 *            type that is expected.
	 * @return actual value
	 * @param <T>
	 *            target type
	 * @throws RuntimeException
	 *             in case of the value couldn't be mapped.
	 */
	@NotNull
	default <T> ValueMappingResult<T> map(Object value, @NotNull Class<T> targetType) {
		return this.map(new ValuePatchRequest<T>().setValue(value).setTargetType(targetType));
	}

	@NotNull
	<T> ValueMappingResult<T> map(ValuePatchRequest<T> request);

	@NotNull
	Set<Class<?>> getSupportedTypes();

	/**
	 * Specifies if the targetType is supported by this mapper.
	 * 
	 * @param targetType
	 * @return
	 */
	default boolean supports(@NotNull Class<?> targetType) {

		boolean directlySupportedType = getSupportedTypes().contains(targetType);

		if (directlySupportedType) {
			return true;
		}

		for (Class<?> supported : getSupportedTypes()) {
			if (supported.isAssignableFrom(targetType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Allows you to override default mappers.
	 * {@link #HIGH_PRECEDENCE} means this mapper will be used (among) first.
	 * {@link #LOW_PRECEDENCE} means this mapper will be used (among) last.
	 *
	 * @return defaults to LOW_PRECEDENCE
	 */
	default int getOrder() {
		return LOW_PRECEDENCE;
	}

	@Override
	default int compareTo(ValuePatchMapper o) {
		return Integer.compare(this.getOrder(), o.getOrder());
	}
}
