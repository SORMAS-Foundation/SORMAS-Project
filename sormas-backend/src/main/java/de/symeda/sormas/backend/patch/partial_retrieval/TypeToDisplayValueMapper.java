package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.Set;

import javax.validation.constraints.NotNull;

public interface TypeToDisplayValueMapper extends Comparable<TypeToDisplayValueMapper> {

	int HIGH_PRECEDENCE = Integer.MIN_VALUE;

	int LOW_PRECEDENCE = Integer.MAX_VALUE;

	String toDisplayValue(@NotNull Object value);

	Set<Class<?>> supportedTypes();

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
