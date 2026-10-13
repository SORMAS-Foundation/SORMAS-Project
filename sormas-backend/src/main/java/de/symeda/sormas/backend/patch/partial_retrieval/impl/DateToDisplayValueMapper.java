package de.symeda.sormas.backend.patch.partial_retrieval.impl;

import java.util.Date;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.backend.patch.partial_retrieval.TypeToDisplayValueMapper;

@ApplicationScoped
public class DateToDisplayValueMapper implements TypeToDisplayValueMapper {

	public static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Date.class);

	@Override
	public String toDisplayValue(Object value) {
		return DateFormatHelper.formatDate((Date) value);
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
