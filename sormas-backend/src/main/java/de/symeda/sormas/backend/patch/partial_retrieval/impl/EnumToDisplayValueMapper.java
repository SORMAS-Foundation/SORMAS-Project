package de.symeda.sormas.backend.patch.partial_retrieval.impl;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.backend.patch.partial_retrieval.TypeToDisplayValueMapper;

@ApplicationScoped
public class EnumToDisplayValueMapper implements TypeToDisplayValueMapper {

	public static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Enum.class);

	@Override
	public String toDisplayValue(Object value) {
		return I18nProperties.getEnumCaption((Enum<?>) value);
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	public int getOrder() {
		return HIGH_PRECEDENCE;
	}
}
