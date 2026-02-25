package de.symeda.sormas.backend.patch;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.Tuple;

public class PropertyAccessor {
	// TODO: perform some caching of the fields

	private static final Logger logger = LoggerFactory.getLogger(PropertyAccessor.class);

	public static final char PATH_SEPARATOR = '.';

	private PropertyAccessor() {
	}

	public static Optional<Tuple<Class<?>, Set<Disease>>> getNestedPropertyType(final Object bean, final String fieldName) {
		if (bean == null || fieldName == null || fieldName.isEmpty()) {
			return Optional.empty();
		}

		boolean notNestedPath = fieldName.indexOf(PATH_SEPARATOR) == fieldName.lastIndexOf(PATH_SEPARATOR);

		if (notNestedPath) {
			return getPropertyType(bean, fieldName);
		}

		String leafPath = fieldName.substring(fieldName.lastIndexOf(PATH_SEPARATOR) + 1);

		return Optional.ofNullable(getNestedProperty(bean, fieldName)).flatMap(leafParent -> getPropertyType(leafParent, leafPath));
	}

	public static Optional<Tuple<Class<?>, Set<Disease>>> getPropertyType(final Object bean, final String fieldName) {
		try {
			Field declaredField = bean.getClass().getDeclaredField(fieldName);
			Set<Disease> supportedDiseases = Optional.ofNullable(declaredField.getAnnotation(Diseases.class)).map(a -> {
				boolean invert = a.hide();

				Set<Disease> annotatedDiseases = Arrays.stream(a.value()).collect(Collectors.toSet());
				if (invert) {
					return Disease.ALL_DISEASES.stream().filter(disease -> !annotatedDiseases.contains(disease)).collect(Collectors.toSet());
				}

				return annotatedDiseases;
			}).orElse(Disease.ALL_DISEASES);
			return Optional.of(new Tuple<>(PropertyUtils.getPropertyType(bean, fieldName), supportedDiseases));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
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
