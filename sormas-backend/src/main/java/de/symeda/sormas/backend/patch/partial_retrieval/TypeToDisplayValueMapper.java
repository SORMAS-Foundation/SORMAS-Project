package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.Set;

import javax.validation.constraints.NotNull;

public interface TypeToDisplayValueMapper extends Comparable<TypeToDisplayValueMapper> {

	int HIGH_PRECEDENCE = Integer.MIN_VALUE;

	int LOW_PRECEDENCE = Integer.MAX_VALUE;

	String toDisplayValue(@NotNull Object value);

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
	default int compareTo(TypeToDisplayValueMapper o) {
		return Integer.compare(this.getOrder(), o.getOrder());
	}
}
