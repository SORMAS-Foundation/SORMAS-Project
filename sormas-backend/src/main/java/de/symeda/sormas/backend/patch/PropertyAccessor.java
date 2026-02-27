package de.symeda.sormas.backend.patch;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

/**
 * SORMAS-opinionated reflection accessing of fields.
 * Type retrieval caching was not implement due to {@link FieldVisibilityCheckers}.
 */
public class PropertyAccessor {

	private static final Logger logger = LoggerFactory.getLogger(PropertyAccessor.class);

	public static final char PATH_SEPARATOR = '.';

	private PropertyAccessor() {
	}

	public static Optional<Tuple<Class<?>, Boolean>> getNestedPropertyType(
		final Object bean,
		final String fieldName,
		FieldVisibilityCheckers fieldVisibilityCheckers) {
		if (bean == null || fieldName == null || fieldName.isEmpty()) {
			return Optional.empty();
		}

		boolean notNestedPath = fieldName.indexOf(PATH_SEPARATOR) == fieldName.lastIndexOf(PATH_SEPARATOR);

		if (notNestedPath) {
			return getPropertyType(bean, fieldName, fieldVisibilityCheckers);
		}

		String leafPath = fieldName.substring(fieldName.lastIndexOf(PATH_SEPARATOR) + 1);

		return Optional.ofNullable(getNestedProperty(bean, fieldName))
			.flatMap(leafParent -> getPropertyType(leafParent, leafPath, fieldVisibilityCheckers));
	}

	public static Optional<Tuple<Class<?>, Boolean>> getPropertyType(
		final Object bean,
		final String fieldName,
		FieldVisibilityCheckers fieldVisibilityCheckers) {
		try {
			return Optional.ofNullable(PropertyUtils.getPropertyType(bean, fieldName)).map(propertyType -> {
				boolean visible = fieldVisibilityCheckers.isVisible(bean.getClass(), fieldName);
				return new Tuple<>(propertyType, visible);
			});
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.info("Could not get property type for [{}], [{}]", fieldName, bean.getClass().getSimpleName(), e);
			return Optional.empty();
		}
	}

	public static Optional<Object> getNestedProperty(final Object bean, final String name) {
		try {
			return Optional.ofNullable(PropertyUtils.getNestedProperty(bean, name));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.info("Could not get property value for [{}], [{}]", name, bean, e);
			return Optional.empty();
		}
	}

	public static Optional<Exception> setNestedProperty(final Object bean, final String name, final Object value) {
		try {
			PropertyUtils.setNestedProperty(bean, name, value);
			return Optional.empty();
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.info("Could not set property for bean [{}], name [{}], value [{}]", bean, name, value, e);
			return Optional.of(e);
		}
	}
}
