package de.symeda.sormas.backend.patch;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.expression.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyAccessor {
	// TODO: perform some caching of the fields

	private static final Logger logger = LoggerFactory.getLogger(PropertyAccessor.class);
	private static final PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();
	public static final char PATH_SEPARATOR = '.';

	private PropertyAccessor() {
	}

	public static Optional<Class<?>> getNestedPropertyType(final Object bean, final String fieldName) {
		if (bean == null || fieldName == null || fieldName.isEmpty()) {
			return Optional.empty();
		}

		try {
			boolean notNestedPath = fieldName.indexOf(PATH_SEPARATOR) == fieldName.lastIndexOf(PATH_SEPARATOR);

			if (notNestedPath) {
				return Optional.ofNullable(PropertyUtils.getPropertyType(bean, fieldName));
			}

			String leafPath = fieldName.substring(fieldName.lastIndexOf('.') + 1);

			return Optional.ofNullable(getNestedProperty(bean, fieldName)).flatMap(leafParent -> getPropertyType(leafParent, leafPath));

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.info("Could not get property type for [{}], [{}]", fieldName, bean.getClass().getSimpleName(), e);
			return Optional.empty();
		}
	}

	public static Optional<Class<?>> getPropertyType(final Object bean, final String fieldName) {
		try {
			return Optional.ofNullable(PropertyUtils.getPropertyType(bean, fieldName));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.info("Could not get property type for [{}], [{}]", fieldName, bean.getClass().getSimpleName(), e);
			return Optional.empty();
		}
	}

	/**
	 * Resolves the type of a nested property using BeanUtils' internal resolver.
	 * Supports: `user.address.city`, `items[0].name`, `map[key].value`
	 */
	private static Class<?> getPropertyTypeRecursive(Class<?> beanClass, String propertyName)
		throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		if (!propertyName.contains(".") && !propertyName.contains("[") && !propertyName.contains("(")) {
			return PropertyUtils.getPropertyType(beanClass, propertyName);
		}

		Resolver resolver = propertyUtils.getResolver();
		if (!resolver.hasNested(propertyName)) {
			return PropertyUtils.getPropertyType(beanClass, propertyName);
		}

		String next = resolver.next(propertyName);
		String property = resolver.getProperty(next);
		Class<?> currentType = PropertyUtils.getPropertyType(beanClass, property);

		if (currentType == null) {
			throw new IllegalArgumentException(String.format("No such property: [%s] on type: [%s]", property, beanClass));
		}

		if (resolver.isIndexed(next)) {
			currentType = getIndexedPropertyType(currentType);
		} else if (resolver.isMapped(next)) {
			throw new UnsupportedOperationException("Maps are not supported yet.");
		}

		String remaining = resolver.remove(propertyName);
		if (remaining.isEmpty()) {
			return currentType;
		}
		return getPropertyTypeRecursive(currentType, remaining);
	}

	private static Class<?> getIndexedPropertyType(Class<?> collectionType) {
		if (Iterable.class.isAssignableFrom(collectionType) || collectionType.isArray()) {
			if (collectionType.isArray()) {
				return collectionType.getComponentType();
			}
			return Object.class;
		}
		return collectionType;
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
