package de.symeda.sormas.patch;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyAccessor {

	private static final Logger logger = LoggerFactory.getLogger(PropertyAccessor.class);

	private PropertyAccessor() {
	}

	public Optional<Class<?>> getNestedPropertyType(final Object bean, final String name) {
		try {
			return Optional.ofNullable(PropertyUtils.getPropertyType(bean, name));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.debug("Could not get property type for [{}], [{}]", name, bean, e);
			return Optional.empty();
		}
	}

	public static Optional<Exception> setNestedProperty(final Object bean, final String name, final Object value) {
		try {
			PropertyUtils.setNestedProperty(bean, name, value);
			return Optional.empty();
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return Optional.of(e);
		}
	}
}
