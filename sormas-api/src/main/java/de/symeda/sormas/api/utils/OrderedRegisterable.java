package de.symeda.sormas.api.utils;

import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * Can be used to define ordered mappers (same order by default) that supports specific type and that perform whatever action on this type.
 * Makes it simpler to register them into registries with: {@link Comparable} and the {@link #supports(Class)}.
 * 
 * @param <SELF>
 *            type of interface that extends this class.
 */
public interface OrderedRegisterable<SELF extends OrderedRegisterable<?>> extends Comparable<SELF> {

	int HIGH_PRECEDENCE = Integer.MIN_VALUE;

	int LOW_PRECEDENCE = Integer.MAX_VALUE;

	/**
	 * Can be used to add it to the default precedences values and a keep some "space between" the implementations ordering.
	 */
	int ORDER_CHUNK = 20;

	/**
	 * Meant to be implemented by classes implementing this {@link OrderedRegisterable} contract but to be used.
	 * For usages prefer {@link #supports(Class)}.
	 * 
	 * @return types that are supported by this class.
	 */
	@NotNull
	Set<Class<?>> getSupportedTypes();

	/**
	 * Specifies if the targetType is supported by this class.
	 *
	 * @param targetType
	 *            can be a child class.
	 * @return true if the class will be able to perform some action with this type.
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
	default int compareTo(SELF o) {
		return Integer.compare(this.getOrder(), o.getOrder());
	}
}
