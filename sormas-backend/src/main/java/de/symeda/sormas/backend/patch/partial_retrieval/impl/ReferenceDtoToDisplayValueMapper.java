package de.symeda.sormas.backend.patch.partial_retrieval.impl;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.backend.patch.partial_retrieval.TypeToDisplayValueMapper;

@ApplicationScoped
public class ReferenceDtoToDisplayValueMapper implements TypeToDisplayValueMapper {

	public static final Set<Class<?>> SUPPORTED_TYPES = Set.of(ReferenceDto.class);

	@Override
	public String toDisplayValue(Object value) {
		return ((ReferenceDto) value).getCaption();
	}

	@Override
	public Set<Class<?>> supportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	public int getOrder() {
		return HIGH_PRECEDENCE;
	}
}
