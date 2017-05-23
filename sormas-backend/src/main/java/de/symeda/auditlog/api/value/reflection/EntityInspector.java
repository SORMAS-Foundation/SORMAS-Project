package de.symeda.auditlog.api.value.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedAttribute;
import de.symeda.auditlog.api.AuditedCollection;
import de.symeda.auditlog.api.AuditlogException;
import de.symeda.auditlog.api.value.format.CollectionFormatter;
import de.symeda.auditlog.api.value.format.DefaultCollectionFormatter;
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

				Method[] methods = clazz.getDeclaredMethods();
				for (Method method : methods) {
					if (isAudited(method)) {
						auditedMethods.add(method);
					}
				}
			}

			// Aktuelle Klasse und alle Oberklassen bis hin zu Object prüfen
			auditedMethods.addAll(getAuditedAttributes(clazz.getSuperclass()));
		}

		return auditedMethods;
	}

	private boolean isAudited(Method method) {

		return method.getAnnotation(AuditedAttribute.class) != null || method.getAnnotation(AuditedCollection.class) != null;
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

		Class<? extends ValueFormatter<?>> formatterClass = annotation.value();

		return buildFormatter(formatterClass);
	}

	/**
	 * Liefert eine Instanz des konfigurierten {@link CollectionFormatter} für diese Annotation.
	 */
	@SuppressWarnings({
			"rawtypes",
			"unchecked" })
	public static CollectionFormatter<?> getCollectionFormatter(AuditedCollection annotation) {

		Class<? extends ValueFormatter<?>> formatterClass = annotation.value();
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
