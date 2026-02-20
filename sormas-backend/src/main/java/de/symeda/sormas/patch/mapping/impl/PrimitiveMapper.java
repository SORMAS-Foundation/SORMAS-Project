package de.symeda.sormas.patch.mapping.impl;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.patch.mapping.FieldPatchRequest.ValueMapper;

@ApplicationScoped
public class PrimitiveMapper implements ValueMapper {

	private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(String.class, Integer.class, Double.class, Float.class, Boolean.class, boolean.class);

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T map(Object value, Class<T> targetType) {
		String str = value.toString();

		if (targetType == String.class)
			return (T) str;
		if (targetType == Integer.class)
			return (T) Integer.valueOf(str);
		if (targetType == Double.class)
			return (T) Double.valueOf(str);
		if (targetType == Float.class)
			return (T) Float.valueOf(str);
		if (targetType == Boolean.class || targetType == boolean.class)
			return (T) Boolean.valueOf(str);

		throw new IllegalArgumentException("PrimitiveWrapperMapper: unsupported type " + targetType.getName());
	}
}
