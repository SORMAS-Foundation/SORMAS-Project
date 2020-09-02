/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.auditlog.api.value.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedAttribute;
import de.symeda.auditlog.api.AuditedCollection;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.auditlog.api.AuditlogException;
import de.symeda.auditlog.api.value.format.CollectionFormatter;
import de.symeda.auditlog.api.value.format.DefaultCollectionFormatter;
import de.symeda.auditlog.api.value.format.DefaultValueFormatter;
import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * Inspects entities to track changes made to them.
 * 
 * @author Oliver Milke
 * @since 08.04.2016
 */
public class EntityInspector {

	private static final String[] METHOD_PREFIXES = {
		"get",
		"is" };

	private final Object entity;

	public EntityInspector(Object entity) {
		this.entity = entity;
	}

	/**
	 * Returns all attributes of an entity that are tracked.
	 */
	public List<Method> getAuditedAttributes() {

		List<Method> auditedMethods = new ArrayList<>();

		if (this.entity != null) {
			Class<?> clazz = this.entity.getClass();
			auditedMethods.addAll(getAuditedAttributes(clazz));
		}

		return auditedMethods;
	}

	/**
	 * @param clazz
	 *            This class and all super classes that are annotated with {@link Audited} are checked for methods to be audited.
	 * @return All methods to be audited of the given {@code clazz}.
	 */
	private List<Method> getAuditedAttributes(Class<?> clazz) {

		List<Method> auditedMethods = new ArrayList<>();

		if (clazz != null) {

			// Potentially not all classes in the class tree are annotated with @Audited
			if (clazz.getDeclaredAnnotation(Audited.class) != null) {

				// Only get/is methods are relevant - even if there are no setters (e.g. because they're protected)
				Method[] methods = clazz.getDeclaredMethods();
				for (Method method : methods) {
					if (!Modifier.isStatic(method.getModifiers())) {
						for (String prefix : METHOD_PREFIXES) {
							if (method.getName().startsWith(prefix) && isAudited(method)) {
								auditedMethods.add(method);
							}
						}
					}
				}
			}

			// Check all super classes that are audited
			auditedMethods.addAll(getAuditedAttributes(clazz.getSuperclass()));
		}

		return auditedMethods;
	}

	/**
	 * Returns whether the given method should be audited. This is the case when neither the {@link Transient} nor the
	 * {@link AuditedIgnore} annotations are set and the method does not have a {@link OneToMany} or {@link OneToOne}
	 * relation that is mapped by a different class.
	 * 
	 * @param method
	 *            The method to check.
	 * @return True if the method should be audited, false if not.
	 */
	private boolean isAudited(Method method) {
		if (method.getAnnotation(AuditedIgnore.class) != null) {
			return false;
		} else if (method.getAnnotation(AuditedAttribute.class) != null || method.getAnnotation(AuditedCollection.class) != null) {
			return true;
		} else {
			return method.getAnnotation(Transient.class) == null
				&& (method.getAnnotation(OneToMany.class) == null || method.getAnnotation(OneToMany.class).mappedBy().isEmpty())
				&& (method.getAnnotation(OneToOne.class) == null || method.getAnnotation(OneToOne.class).mappedBy().isEmpty());
		}
	}

	/**
	 * Returns the actual value of an attribute. It is assumed that this method is a getter.
	 * 
	 * @param method
	 *            The attribute to inspect.
	 * @throws IllegalAccessException
	 *             If an error occurs during the call of the getter (original exception).
	 * @throws IllegalArgumentException
	 *             If an error occurs during the call of the getter (original exception).
	 * @throws InvocationTargetException
	 *             If an error occurs during the call of the getter (original exception).
	 * @see Method#invoke(Object, Object...)
	 */
	public Object getAttributeValue(Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		return method.invoke(entity);
	}

	/**
	 * Returns an instance of the configured {@link ValueFormatter} for this annotation.
	 */
	public static ValueFormatter<?> getFormatter(AuditedAttribute annotation) {
		Class<? extends ValueFormatter<?>> formatterClass;
		if (annotation == null) {
			formatterClass = DefaultValueFormatter.class;
		} else {
			formatterClass = annotation.value();
		}

		return buildFormatter(formatterClass);
	}

	/**
	 * Returns an instance of the configured {@link CollectionFormatter} for this annotation.
	 */
	@SuppressWarnings({
		"rawtypes",
		"unchecked" })
	public static CollectionFormatter<?> getCollectionFormatter(AuditedCollection annotation) {
		Class<? extends ValueFormatter<?>> formatterClass;
		if (annotation == null) {
			formatterClass = DefaultCollectionFormatter.class;
		} else {
			formatterClass = annotation.value();
		}

		ValueFormatter declaredFormatter = buildFormatter(formatterClass);

		final CollectionFormatter resultingCollectionFormatter;
		if (declaredFormatter instanceof CollectionFormatter) {
			resultingCollectionFormatter = (CollectionFormatter) declaredFormatter;
		} else {
			resultingCollectionFormatter = new DefaultCollectionFormatter(declaredFormatter);
		}

		return resultingCollectionFormatter;
	}

	private static ValueFormatter<?> buildFormatter(Class<? extends ValueFormatter<?>> formatterClass) {

		try {
			return formatterClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new AuditlogException(String.format("ValueFormatter %s can not be instantiated!", formatterClass.getName()), e);
		}
	}

	/**
	 * Returns the name of the underlying field according to the JavaBean convention.
	 */
	public static String buildFieldName(Method method) {

		final String methodName = method.getName();
		return buildFieldName(methodName);
	}

	static String buildFieldName(final String methodName) {

		int skip = 0;
		for (String prefix : METHOD_PREFIXES) {
			if (methodName.startsWith(prefix)) {
				skip = prefix.length();
				break;
			}
		}

		final String withoutPrefix = methodName.substring(skip);

		final String firstLetterInLowerCase = withoutPrefix.substring(0, 1).toLowerCase();
		return firstLetterInLowerCase + withoutPrefix.substring(1);
	}
}
