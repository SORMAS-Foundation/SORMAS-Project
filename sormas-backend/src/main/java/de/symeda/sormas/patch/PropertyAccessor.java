package de.symeda.sormas.patch;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyAccessor {
	// TODO: perform some caching of the fields

	private static final Logger logger = LoggerFactory.getLogger(PropertyAccessor.class);

	private PropertyAccessor() {
	}

	public static Optional<Class<?>> getNestedPropertyType(final Object bean, final String name) {
		// TODO: make nested
		try {
			return Optional.ofNullable(PropertyUtils.getPropertyType(bean, name));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.info("Could not get property type for [{}], [{}]", name, bean, e);
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
