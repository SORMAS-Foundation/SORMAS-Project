package de.symeda.sormas.backend.patch;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import javax.validation.constraints.NotNull;

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
	private static final Tuple<Class<?>, PropertyAccessFailure> FIELD_DOES_NOT_EXIST = Tuple.of(null, PropertyAccessFailure.FIELD_DOES_NOT_EXIST);

	private static final Tuple<Class<?>, PropertyAccessFailure> UNSUPPORTED_FIELD =
		Tuple.of(null, PropertyAccessFailure.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);

	private static final Tuple<Class<?>, PropertyAccessFailure> INVALID_INPUT = Tuple.of(null, PropertyAccessFailure.INVALID_INPUT);

	private PropertyAccessor() {
	}

	public static Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> getNestedPropertyAndType(
		final Object bean,
		final String fieldName,
		FieldVisibilityCheckers fieldVisibilityCheckers) {
		if (bean == null || fieldName == null || fieldName.isEmpty()) {
			return new Tuple<>(null, PropertyAccessFailure.INVALID_INPUT);
		}

		boolean notNestedPath = fieldName.indexOf(PATH_SEPARATOR) == -1;

		if (notNestedPath) {
			return getPropertyTypeAndValue(bean, fieldName, fieldVisibilityCheckers);
		}

		String leafPath = fieldName.substring(fieldName.lastIndexOf(PATH_SEPARATOR) + 1);

		return getNestedProperty(bean, fieldName.substring(0, fieldName.lastIndexOf(PATH_SEPARATOR)))
			.map(leafParent -> getPropertyTypeAndValue(leafParent, leafPath, fieldVisibilityCheckers))
			.orElseGet(() -> new Tuple<>(null, PropertyAccessFailure.FIELD_DOES_NOT_EXIST));
	}

	public static Tuple<Class<?>, PropertyAccessFailure> getNestedPropertyType(
		final Object bean,
		final String fieldName,
		FieldVisibilityCheckers fieldVisibilityCheckers) {
		if (bean == null || fieldName == null || fieldName.isEmpty()) {
			return INVALID_INPUT;
		}

		boolean notNestedPath = fieldName.indexOf(PATH_SEPARATOR) == -1;

		if (notNestedPath) {
			return getPropertyType(bean, fieldName, fieldVisibilityCheckers);
		}

		String leafPath = fieldName.substring(fieldName.lastIndexOf(PATH_SEPARATOR) + 1);

		return getNestedProperty(bean, fieldName.substring(0, fieldName.lastIndexOf(PATH_SEPARATOR)))
			.map(leafParent -> getPropertyType(leafParent, leafPath, fieldVisibilityCheckers))
			.orElse(FIELD_DOES_NOT_EXIST);
	}

	@NotNull
	public static Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> getPropertyTypeAndValue(
		final Object bean,
		final String fieldName,
		FieldVisibilityCheckers fieldVisibilityCheckers) {
		try {
			return Optional.ofNullable(PropertyUtils.getPropertyType(bean, fieldName))
				.<Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure>> map(propertyType -> {
					boolean visible = fieldVisibilityCheckers.isVisible(bean.getClass(), fieldName);

					if (!visible) {
						return Tuple.of(null, PropertyAccessFailure.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);
					}

					return Tuple.of(Tuple.of(propertyType, getNestedProperty(bean, fieldName).orElse(null)), null);
				})
				.orElseGet(() -> Tuple.of(null, PropertyAccessFailure.FIELD_DOES_NOT_EXIST));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.info("Could not get property type for [{}], [{}]", fieldName, bean, e);
			return null;
		}
	}

	public static Tuple<Class<?>, PropertyAccessFailure> getPropertyType(
		final Object bean,
		final String fieldName,
		FieldVisibilityCheckers fieldVisibilityCheckers) {
		try {
			return Optional.ofNullable(PropertyUtils.getPropertyType(bean, fieldName)).<Tuple<Class<?>, PropertyAccessFailure>> map(propertyType -> {
				boolean visible = fieldVisibilityCheckers.isVisible(bean.getClass(), fieldName);

				if (!visible) {
					return UNSUPPORTED_FIELD;
				}

				return new Tuple<>(propertyType, null);
			}).orElse(FIELD_DOES_NOT_EXIST);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.info("Could not get property type for [{}], [{}]", fieldName, bean, e);
			return FIELD_DOES_NOT_EXIST;
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
