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

import de.symeda.auditlog.api.value.DefaultValueContainer;
import de.symeda.auditlog.api.value.ValueContainer;
import de.symeda.auditlog.api.value.format.CollectionFormatter;
import de.symeda.auditlog.api.value.format.ValueFormatter;
import de.symeda.auditlog.api.value.format.override.DateFormatOverrideDetector;
import de.symeda.auditlog.api.value.format.override.OverrideDetector;
import de.symeda.auditlog.api.value.reflection.EntityInspector;
import de.symeda.sormas.api.HasUuid;

/**
 * Class for the inspection of entity states.
 * </p>
 * The Auditor has to be serializable because it is kept in the Transaction Scope (requirement from CDI).
 * 
 * @author Oliver Milke
 * @since 13.01.2016
 */
public class Auditor implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Should the Auditor be serialized, there will be an NPE for changes.
	 * However, this is very unlikely.
	 */
	private final Map<EntityId, ValueContainer> changes = new HashMap<>();

	/**
	 * Registers a {@link HasUuid} to monitor its attributes. Later comparisons of attribute changes with
	 * {@link #detectChanges(HasUuid)} take place against the entity's state to this point in time.
	 * <p/>
	 * Should therefore be called immediately after an entity has been loaded.
	 * 
	 * @param entity
	 *            The entity to inspect.
	 */
	public void register(HasUuid entity) {

		if (isAudited(entity)) {
			final ValueContainer valueContainer = getValueContainerOf(entity);

			if (valueContainer != null) {
				this.changes.put(EntityId.getOidFromHasUuid(entity), new DefaultValueContainer(valueContainer));
			}
		}
	}

	/**
	 * Checks which attributes have changed for this entity. Returns an empty map when no changes have been detected.
	 * 
	 * @param entity
	 *            The entity to inspect.
	 * @return
	 *         <ol>
	 *         <li>Returns a list of all changed attributes, with the <code>key</code> of the map being the name of the changed attribute
	 *         and the <code>value</code> being the new value of the attribute.</li>
	 *         <li>Specifies whether it is a {@link ChangeType#CREATE} or {@link ChangeType#UPDATE}.</li>
	 */
	public ChangeEvent detectChanges(HasUuid entity) {

		if (!isAudited(entity)) {
			return new ChangeEvent(Collections.emptySortedMap(), ChangeType.UPDATE);
		} else {

			final ChangeType changeType = detectChangeType(entity);
			final SortedMap<String, String> entityChanges;
			if (this.changes.containsKey(EntityId.getOidFromHasUuid(entity))) {
				// Compare attributes because already existing
				ValueContainer originalContainer = this.changes.get(EntityId.getOidFromHasUuid(entity));
				entityChanges = getValueContainerOf(entity).compare(originalContainer);
			} else {
				// Entity is new
				entityChanges = getValueContainerOf(entity).getChanges();
			}

			// Save the current state for the next call within the same TX
			register(entity);
			return new ChangeEvent(entityChanges, changeType);
		}
	}

	/**
	 * Checks whether an entity is auditable.
	 * 
	 * @return Returns <code>true</code> if the entity is auditable (because it derives from {@link AuditedEntity} or is annotated
	 *         with {@link Audited}). Returns <code>false</code> otherwise.
	 */
	private boolean isAudited(HasUuid entity) {

		if (entity == null) {
			return false;
		} else {
			return isClassAudited(entity.getClass());
		}
	}

	/**
	 * Checks whether this entity is completely new or has been changed.
	 * 
	 * @param entity
	 *            The entity to inspect.
	 * @return Returns {@link ChangeType#UPDATE} if the entity has been changed in this transaction or {@link ChangeType#CREATE} otherwise.
	 */
	private ChangeType detectChangeType(HasUuid entity) {

		if (this.changes.containsKey(EntityId.getOidFromHasUuid(entity))) {
			return ChangeType.UPDATE;
		} else {
			return ChangeType.CREATE;
		}
	}

	/**
	 * Returns the ValueContainer to an auditable entity.
	 * 
	 * @throws IllegalStateException
	 *             If the entity is not auditable.
	 * @see #isAudited(HasUuid)
	 */
	private ValueContainer getValueContainerOf(HasUuid entity) {

		final ValueContainer container;

		Audited audited = entity.getClass().getDeclaredAnnotation(Audited.class);
		if (audited != null) {
			container = inspectEntity(entity);
		} else {
			throw new IllegalStateException("ValueContainer cannot be created for entity: " + entity);
		}

		return container;

	}

	/**
	 * Returns the {@link ValueContainer} based on the annotations.
	 * 
	 * @return Returns the {@link ValueContainer} for this entity. The {@link ValueContainer} is empty if no auditable attributes are found.
	 */
	ValueContainer inspectEntity(HasUuid entity) {
		DefaultValueContainer result = new DefaultValueContainer();

		EntityInspector inspector = new EntityInspector(entity);
		for (Method currentAttribute : inspector.getAuditedAttributes()) {

			try {
				AuditedAttribute auditedAttribute = currentAttribute.getAnnotation(AuditedAttribute.class);
				AuditedCollection auditedCollection = currentAttribute.getAnnotation(AuditedCollection.class);
				boolean isCollection = Collection.class.isAssignableFrom(currentAttribute.getReturnType());

				if (auditedAttribute != null || !isCollection) {
					logAttributeChange(result, inspector, currentAttribute, auditedAttribute);
				} else if (auditedCollection != null || isCollection) {
					logCollectionChange(result, inspector, currentAttribute, auditedCollection);
				}
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new AuditlogException(String.format("No changes for entity %s can be detected.", entity.toString()), e);
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
				// Log the attributes of the Embeddable
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
			// Log simple value
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
		AuditedAttribute annotation)
		throws IllegalAccessException, InvocationTargetException {

		this.logSingularAttributeChange(result, "", inspector, currentAttribute, annotation);

	}

	@SuppressWarnings("unchecked")
	private void logSingularAttributeChange(
		DefaultValueContainer result,
		String prefix,
		EntityInspector inspector,
		Method currentAttribute,
		AuditedAttribute annotation)
		throws IllegalAccessException, InvocationTargetException {

		@SuppressWarnings("rawtypes")
		ValueFormatter formatter = EntityInspector.getFormatter(annotation);

		formatter = overrideFormatter(formatter, currentAttribute);

		final String fieldName = EntityInspector.buildFieldName(currentAttribute);
		final Object fieldValue = inspector.getAttributeValue(currentAttribute);

		result.put(prefixFieldNameWith(prefix, fieldName), fieldValue, formatter);

		if (annotation != null) {
			if (annotation.anonymous()) {
				result.configureAnonymizeValue(fieldName, annotation.anonymizingString());
			}
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
	 * Decides which {@link ValueFormatter} should be used for this entity property. Returns the original {@link ValueFormatter} if
	 * <ol>
	 * <li>it already differs from the default ValueFormatter</li>
	 * <li>no reasonable derivation based on the entity property can be found</li>
	 * <ol>
	 * 
	 * @param original
	 *            The {@link ValueFormatter} given as a parameter to {@link AuditedAttribute}.
	 * @param m
	 *            The entity property.
	 * @return The {@link ValueFormatter} to be used. Must not return <code>null</code>.
	 */
	ValueFormatter<?> overrideFormatter(ValueFormatter<?> original, Method m) {

		if (isDefaultFormatter(original.getClass())) {
			OverrideDetector<?> detector = findOverrideDector(m);

			if (detector != null) {
				// If a reasonable default exists, use it.
				return ObjectUtils.firstNonNull(detector.override(m), original);
			} else {
				return original;
			}
		}

		return original;
	}

	/**
	 * Checks based on the entity property which OverrideDetector may be used.
	 * 
	 * @param m
	 *            The entity property.
	 * @return Returns <code>null</code> if no {@link OverrideDetector} for the entity property can be found.
	 */
	private OverrideDetector<?> findOverrideDector(Method m) {

		OverrideDetector<?> suitedDetector = null;
		if (Date.class.isAssignableFrom(m.getReturnType())) {
			suitedDetector = new DateFormatOverrideDetector();
		}

		return suitedDetector;
	}

	/**
	 * Checks whether a {@link ValueFormatter} is the default ValueFormatter.
	 * 
	 * @param specifiedFormatter
	 *            The {@link ValueFormatter} to check.
	 * @return Returns <code>true</code> if the Formatter to check matches the default ValueFormatter. Returns <code>false</code> otherwise.
	 */
	@SuppressWarnings("rawtypes")
	private static boolean isDefaultFormatter(Class<? extends ValueFormatter> specifiedFormatter) {
		return AuditedAttribute.DEFAULT_FORMATTER.equals(specifiedFormatter);
	}
}
