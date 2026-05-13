package de.symeda.sormas.backend.patch.partial_retrieval.impl;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.backend.patch.partial_retrieval.TypeToDisplayValueMapper;

@ApplicationScoped
public class ObjectToDisplayValueMapper implements TypeToDisplayValueMapper {

	public static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Object.class);

	@Override
	public String toDisplayValue(Object value) {
		return value.toString();
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}
}
