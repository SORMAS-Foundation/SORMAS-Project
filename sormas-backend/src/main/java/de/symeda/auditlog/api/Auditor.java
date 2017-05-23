package de.symeda.auditlog.api;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.persistence.Embedded;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import de.symeda.auditlog.api.value.DefaultValueContainer;
import de.symeda.auditlog.api.value.ValueContainer;
import de.symeda.auditlog.api.value.format.CollectionFormatter;
import de.symeda.auditlog.api.value.format.ValueFormatter;
import de.symeda.auditlog.api.value.format.override.DateFormatOverrideDetector;
import de.symeda.auditlog.api.value.format.override.OverrideDetector;
import de.symeda.auditlog.api.value.reflection.EntityInspector;

/**
 * Klasse zur Inspektion von Entity-Zuständen.
 * </p>
 * Der Auditor muss serialisierbar sein, weil er im Transaction Scope gehalten wird (Anforderung von CDI).
 * 
 * @author Oliver Milke
 * @since 13.01.2016
 */
public class Auditor implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Sollte der Auditor serialisiert werden, wird es für changes eine NPE geben.
	 * Dies ist aber sehr unwahrscheinlich.
	 */
	private final Map<EntityId, ValueContainer> changes = new HashMap<>();

	/**
	 * Registriert ein {@link HasUuid} zur Überwachung seiner Attribute. Spätere Vergleiche auf Attributsänderungen mit
	 * {@link #detectChanges(HasUuid)} erfolgen gegen den Zustand des Entities zu diesem Zeitpunkt.
	 * <p/>
	 * Sollte daher möglichst unmittelbar nach dem Laden einer Entity aufgerufen werden.
	 * 
	 * @param entity
	 *            Das zu inspizierende Entity.
	 */
	public void register(HasUuid entity) {

		if (isAudited(entity)) {
			final ValueContainer valueContainer = getValueContainerOf(entity);

			if (valueContainer != null) {
				this.changes.put(entity.getOid(), new DefaultValueContainer(valueContainer));
			}
		}
	}

	/**
	 * Überprüft, welche Attribute sich für dieses Entity geändert haben. Liefert eine leere Map, sofern keine Änderungen festgestellt
	 * wurden.
	 * 
	 * @param entity
	 *            Die zu inspizierende Entity.
	 * @return
	 *         <ol>
	 *         <li>Liefert eine Auflistung aller geänderten Attribute, wobei der <code>key</code> der Map der Name des geänderten Attributs
	 *         ist und der <code>value</code> der neue Wert des Attributs ist.</li>
	 *         <li>Gibt an, ob es sich um {@link ChangeType#CREATE} oder {@link ChangeType#UPDATE} handelt.</li>
	 *         </ol>
	 */
	public ChangeEvent detectChanges(HasUuid entity) {

		if (!isAudited(entity)) {
			return new ChangeEvent(Collections.emptySortedMap(), ChangeType.UPDATE);
		} else {

			final ChangeType changeType = detectChangeType(entity);
			final SortedMap<String, String> entityChanges;
			if (this.changes.containsKey(entity.getOid())) {
				//Attribute vergleichen, weil bereits vorhanden
				ValueContainer originalContainer = this.changes.get(entity.getOid());
				entityChanges = getValueContainerOf(entity).compare(originalContainer);
			} else {
				//Entity ist neu
				entityChanges = getValueContainerOf(entity).getChanges();
			}

			// Für nächsten Aufruf innerhalb derselben TX den aktuellen Zustand merken
			register(entity);
			return new ChangeEvent(entityChanges, changeType);
		}
	}

	/**
	 * Überprüft, ob ein Entity auditierbar ist.
	 * 
	 * @return Liefert <code>true</code>, wenn das Entity auditierbar ist (weil es von {@link AuditedEntity} ableitet oder mit
	 *         {@link Audited} annotiert ist). Liefert <code>false</code> anderenfalls.
	 */
	private boolean isAudited(HasUuid entity) {

		if (entity == null) {
			return false;
		} else {
			return entity instanceof AuditedEntity || isClassAudited(entity.getClass());
		}
	}

	/**
	 * Überprüft, ob ein Entity ganz neu ist, oder geändert wurde.
	 * 
	 * @param entity
	 *            Die zu inspizierende Entity.
	 * @return Liefert {@link ChangeType#UPDATE}, falls die Entity in dieser Transaktion verändert wurde, liefert {@link ChangeType#CREATE}
	 *         anderenfalls.
	 */
	private ChangeType detectChangeType(HasUuid entity) {

		if (this.changes.containsKey(entity.getOid())) {
			return ChangeType.UPDATE;
		} else {
			return ChangeType.CREATE;
		}
	}

	/**
	 * Liefert zu einem auditierbaren Entity den ValueContainer.
	 * 
	 * @throws IllegalStateException
	 *             Falls das Entity nicht auditierbar ist.
	 * @see #isAudited(HasUuid)
	 */
	private ValueContainer getValueContainerOf(HasUuid entity) {

		final ValueContainer container;

		Audited audited = entity.getClass().getDeclaredAnnotation(Audited.class);
		if (audited != null) {
			container = inspectEntity(entity);

			if (entity instanceof AuditedEntity) {
				LoggerFactory.getLogger(Auditor.class).warn(
					String.format(
						"Entity %s ist sowohl mit @Audited annotiert als auch von AuditedEntity abgeleitet. Nur die Annotationen werden betrachtet.",
						entity.getClass()));
			}

		} else if (entity instanceof AuditedEntity) {
			container = ((AuditedEntity) entity).inspectAttributes();
		} else {
			throw new IllegalStateException("ValueContainer kann nicht erstellt werden für Entity: " + entity);
		}

		return container;

	}

	/**
	 * Liefert anhand der Annotationen den {@link ValueContainer}.
	 * 
	 * @return Liefert den {@link ValueContainer} für dieses Entity. Der {@link ValueContainer} ist leer, wenn keine auditierten Attribute
	 *         gefunden werden können.
	 */
	ValueContainer inspectEntity(HasUuid entity) {

		DefaultValueContainer result = new DefaultValueContainer();

		EntityInspector inspector = new EntityInspector(entity);
		for (Method currentAttribute : inspector.getAuditedAttributes()) {

			try {
				AuditedAttribute auditedAttribute = currentAttribute.getAnnotation(AuditedAttribute.class);
				AuditedCollection auditedCollection = currentAttribute.getAnnotation(AuditedCollection.class);

				if (auditedAttribute != null) {
					logAttributeChange(result, inspector, currentAttribute, auditedAttribute);
				} else if (auditedCollection != null) {
					logCollectionChange(result, inspector, currentAttribute, auditedCollection);
				}

			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new AuditlogException(String.format("Änderung der Entity %s konnte nicht festgestellt werden.", entity.toString()), e);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private void logCollectionChange(DefaultValueContainer result, EntityInspector inspector, Method currentAttribute, AuditedCollection annotation)
		throws IllegalAccessException, InvocationTargetException {

		@SuppressWarnings("rawtypes")
		CollectionFormatter collectionFormatter = EntityInspector.getCollectionFormatter(annotation);

		final Collection<?> fieldValue = (Collection<?>) inspector.getAttributeValue(currentAttribute);
		final String fieldName = EntityInspector.buildFieldName(currentAttribute);

		result.put(fieldName, fieldValue, collectionFormatter);
	}

	private void logAttributeChange(DefaultValueContainer result, EntityInspector inspector, Method currentAttribute, AuditedAttribute annotation)
		throws IllegalAccessException, InvocationTargetException {

		if (isEmbeddedElement(currentAttribute)) {

			if (isClassAudited(currentAttribute.getReturnType())) {
				//die Attribute des Embeddables loggen
				Object embeddable = inspector.getAttributeValue(currentAttribute);
				EntityInspector embeddableInspector = new EntityInspector(embeddable);

				List<Method> auditedAttributes = embeddableInspector.getAuditedAttributes();

				for (Method embeddableAttribute : auditedAttributes) {
					logSingularAttributeChange(
						result,
						EntityInspector.buildFieldName(currentAttribute),
						embeddableInspector,
						embeddableAttribute,
						embeddableAttribute.getDeclaredAnnotation(AuditedAttribute.class));
				}

			}
		} else {
			//log simple value
			logSingularAttributeChange(result, inspector, currentAttribute, annotation);
		}

	}

	private boolean isClassAudited(final Class<?> clazz) {

		return clazz.getDeclaredAnnotation(Audited.class) != null;
	}

	private boolean isEmbeddedElement(Method currentAttribute) {

		return currentAttribute.getDeclaredAnnotation(Embedded.class) != null;
	}

	private void logSingularAttributeChange(
		DefaultValueContainer result,
		EntityInspector inspector,
		Method currentAttribute,
		AuditedAttribute annotation) throws IllegalAccessException, InvocationTargetException {

		this.logSingularAttributeChange(result, "", inspector, currentAttribute, annotation);

	}

	@SuppressWarnings("unchecked")
	private void logSingularAttributeChange(
		DefaultValueContainer result,
		String prefix,
		EntityInspector inspector,
		Method currentAttribute,
		AuditedAttribute annotation) throws IllegalAccessException, InvocationTargetException {

		@SuppressWarnings("rawtypes")
		ValueFormatter formatter = EntityInspector.getFormatter(annotation);

		formatter = overrideFormatter(formatter, currentAttribute);

		final String fieldName = EntityInspector.buildFieldName(currentAttribute);
		final Object fieldValue = inspector.getAttributeValue(currentAttribute);

		result.put(prefixFieldNameWith(prefix, fieldName), fieldValue, formatter);

		if (annotation.anonymous()) {
			result.configureAnonymizeValue(fieldName, annotation.anonymizingString());
		}
	}

	private String prefixFieldNameWith(String prefix, final String fieldName) {

		if (StringUtils.isBlank(prefix)) {
			return fieldName;
		} else {
			return prefix + "." + fieldName;
		}

	}

	/**
	 * Entscheidet, welcher {@link ValueFormatter} für das Entity-Property verwendet werden soll. Liefert den originalen
	 * {@link ValueFormatter}, falls dieser
	 * <ol>
	 * <li>bereits abweicht vom Default-ValueFormatter</li>
	 * <li>keine sinnvolle Ableitung anhand des Entity-Properties gefunden werden kann</li>
	 * </ol>
	 * 
	 * @param original
	 *            Der {@link ValueFormatter}, der mithilfe bei {@link AuditedAttribute} angegeben wurde.
	 * @param m
	 *            Das Entity-Property.
	 * @return Den zu verwendenden {@link ValueFormatter}. Niemals jedoch <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	ValueFormatter<?> overrideFormatter(ValueFormatter<?> original, Method m) {

		if (isDefaultFormatter(original.getClass())) {
			OverrideDetector<?> detector = findOverrideDector(m);

			if (detector != null) {
				//falls ein sinnvoller Default existiert, diesen verwenden.
				return ObjectUtils.firstNonNull(detector.override(m), original);
			} else {
				return original;
			}
		}

		return original;
	}

	/**
	 * Prüft anhand des Entity-Properties, welcher OverrideDetector infrage kommt.
	 * 
	 * @param m
	 *            Das Entity-Property.
	 * @return Liefert <code>null</code>, falls kein {@link OverrideDetector} für das Entity-Property gefunden werden kann.
	 */
	private OverrideDetector<?> findOverrideDector(Method m) {

		OverrideDetector<?> suitedDetector = null;
		if (Date.class.isAssignableFrom(m.getReturnType())) {
			suitedDetector = new DateFormatOverrideDetector();
		}

		return suitedDetector;
	}

	/**
	 * prüft, ob ein {@link ValueFormatter}, der Default-ValueFormatter ist.
	 * 
	 * @param specifiedFormatter
	 *            Der zu überprüfende {@link ValueFormatter}.
	 * @return Liefert <code>true</code>, falls der zu prüfende Formatter dem Default-ValueFormatter entspricht. Liefert <code>false</code>
	 *         anderenfalls.
	 */
	@SuppressWarnings("rawtypes")
	private static boolean isDefaultFormatter(Class<? extends ValueFormatter> specifiedFormatter) {
		return AuditedAttribute.DEFAULT_FORMATTER.equals(specifiedFormatter);
	}

}
