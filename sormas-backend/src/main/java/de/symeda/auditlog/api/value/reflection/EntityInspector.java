package de.symeda.auditlog.api.value.reflection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * Untersucht Entities, deren Änderungen verfolgt werden sollen.
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
	 * Liefert alle Attribute eines Entity, die der Änderungsverfolgung unterliegen sollen.
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
	 *            Diese Klasse und alle Oberklassen, die {@link Audited} annotiert sind, werden auf zu auditierende Methoden inspiziert.
	 * @return Alle zu auditierenden Methoden der übergebenen {@code clazz}.
	 */
	private List<Method> getAuditedAttributes(Class<?> clazz) {

		List<Method> auditedMethods = new ArrayList<>();

		if (clazz != null) {

			// Möglicherweise ist nicht jede Klasse im Vererbungsbaum mit @Audited annotiert
			if (clazz.getDeclaredAnnotation(Audited.class) != null) {

				try {
					// Ignore inherited methods because those will be processed later
					BeanInfo beanInfo = Introspector.getBeanInfo(clazz, clazz.getSuperclass());
					List<Method> methods = new ArrayList<>();
					
					// Only get/is methods are relevant - even if there are no setters (e.g. because they're protected)
					for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
						if (property.getReadMethod() != null) {
							methods.add(property.getReadMethod());
						}
					}
				
					for (Method method : methods) {
						if (isAudited(method)) {
							auditedMethods.add(method);
						}
					}

				} catch (IntrospectionException e) {
					throw new AuditlogException(String.format("Error while trying to retrieve the BeanInfo for %s!", clazz.getName()), e);
				}
			}

			// Aktuelle Klasse und alle Oberklassen bis hin zu Object prüfen
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
	 * 				The method to check.
	 * @return
	 * 				True if the method should be audited, false if not.
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
	 * Liefert zu einem Attribut den eigentlichen Wert. Es wird angenommen, dass es sich bei dieser Methode um einen Getter handelt.
	 * 
	 * @param method
	 *            Das zu inspizierende Attribut.
	 * @throws InvocationTargetException
	 *             Falls dem Aufruf des Getters ein Fehler auftritt (Originale Exception).
	 * @throws IllegalArgumentException
	 *             Falls dem Aufruf des Getters ein Fehler auftritt (Originale Exception).
	 * @throws IllegalAccessException
	 *             Falls dem Aufruf des Getters ein Fehler auftritt (Originale Exception).
	 * @see Method#invoke(Object, Object...)
	 */
	public Object getAttributeValue(Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		return method.invoke(entity);
	}

	/**
	 * Liefert eine Instanz des konfigurierten {@link ValueFormatter} für diese Annotation.
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
	 * Liefert eine Instanz des konfigurierten {@link CollectionFormatter} für diese Annotation.
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
			throw new AuditlogException(String.format("ValueFormatter %s kann nicht instanziert werden!", formatterClass.getName()), e);
		}
	}

	/**
	 * Liefert den Namen des zugrundeliegenden Fields nach JavaBean-Convention.
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
