package de.symeda.sormas.api.patch.mapping;

import java.util.Set;

import javax.validation.constraints.NotNull;

// TODO: check if "in-value-type" must be checked: add self check ?
public interface ValueMapper extends Comparable<ValueMapper> {

	int HIGH_PRECEDENCE = Integer.MIN_VALUE;
	int LOW_PRECEDENCE = Integer.MAX_VALUE;

	/**
	 * Can be used to add it to the default precedences values and a keep some "space between" the implementations ordering.
	 */
	int ORDER_CHUNK = 20;

	// TODO: check if must return-type must be THE specific type.
	<T> T map(Object value, Class<T> targetType);

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
	default int compareTo(ValueMapper o) {
		return Integer.compare(this.getOrder(), o.getOrder());
	}
}
