package de.symeda.sormas.backend.patch.partial_retrieval.impl;

import java.math.BigDecimal;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.patch.partial_retrieval.TypeToDisplayValueMapper;

@ApplicationScoped
public class PrimitivesToDisplayValueMapper implements TypeToDisplayValueMapper {

	private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
		String.class,
		int.class,
		Integer.class,
		long.class,
		Long.class,
		BigDecimal.class,
		double.class,
		Double.class,
		float.class,
		Float.class,
		Boolean.class,
		boolean.class);

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	public String toDisplayValue(Object value) {

		if (value instanceof Number) {
			return numToString((Number) value);
		}

		if (value instanceof Boolean) {
			YesNoUnknown yes = YesNoUnknown.YES;
			return (Boolean) value ? I18nProperties.getEnumCaption(yes) : I18nProperties.getEnumCaption(YesNoUnknown.NO);
		}

		return value.toString();
	}

	private static String numToString(Number num) {
		if (num instanceof BigDecimal) {
			return ((BigDecimal) num).toPlainString();
		}
		if (num instanceof Double || num instanceof Float) {
			return num.toString();
		}
		return String.valueOf(num.longValue());
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
