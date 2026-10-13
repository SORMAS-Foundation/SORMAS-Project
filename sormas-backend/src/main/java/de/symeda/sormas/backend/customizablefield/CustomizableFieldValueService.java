/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.customizablefield;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldVisibilityContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldVisibilityRestrictions;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.DeletableAdo;

/**
 * Service class for customizable field values.
 */
@Stateless
@LocalBean
public class CustomizableFieldValueService extends AbstractCoreAdoService<CustomizableFieldValue, CustomizableFieldValueJoins> {

	@EJB
	private CustomizableFieldMetadataService customizableFieldMetadataService;

	public CustomizableFieldValueService() {
		super(CustomizableFieldValue.class, DeletableEntityType.CUSTOMIZABLE_FIELD_VALUE);
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, CustomizableFieldValue> from) {
		// No jurisdiction filtering for customizable field values
		return null;
	}

	@Override
	protected CustomizableFieldValueJoins toJoins(From<?, CustomizableFieldValue> adoPath) {
		return new CustomizableFieldValueJoins(adoPath);
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, CustomizableFieldValue> from) {
		return cb.conjunction();
	}

	public Map<CustomizableFieldMetadata, CustomizableFieldValue> getValuesForEntity(String entityUuid, CustomizableFieldContext contextClass) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomizableFieldValue> cq = cb.createQuery(CustomizableFieldValue.class);
		Root<CustomizableFieldValue> root = cq.from(CustomizableFieldValue.class);

		Predicate predicate = cb.and(
			cb.equal(root.get(CustomizableFieldValue.ENTITY_UUID), entityUuid),
			cb.equal(root.get(CustomizableFieldValue.CONTEXT_CLASS), contextClass),
			cb.isFalse(root.get(DeletableAdo.DELETED)));

		cq.where(predicate);

		List<CustomizableFieldValue> values = em.createQuery(cq).getResultList();

		Map<CustomizableFieldMetadata, CustomizableFieldValue> result = new HashMap<>();
		for (CustomizableFieldValue value : values) {
			result.put(value.getCustomizableFieldMetadata(), value);
		}
		return result;
	}

	public void saveEntityValues(
		String entityUuid,
		CustomizableFieldContext contextClass,
		Map<CustomizableFieldMetadata, CustomizableFieldValueDto> metadataToValue) {
		Map<CustomizableFieldMetadata, CustomizableFieldValue> existing = getValuesForEntity(entityUuid, contextClass);

		for (Map.Entry<CustomizableFieldMetadata, CustomizableFieldValueDto> entry : metadataToValue.entrySet()) {
			CustomizableFieldMetadata metadata = entry.getKey();
			CustomizableFieldValue value = existing.getOrDefault(metadata, createNewValue(metadata, entityUuid, contextClass));
			value.setValue(entry.getValue().getValue());
			ensurePersisted(value);
		}
	}

	public void ensureDefaultValuesForEntity(String entityUuid, CustomizableFieldContext contextClass) {
		ensureDefaultValuesForEntity(entityUuid, contextClass, null);
	}

	/**
	 * Ensures default values for customizable fields for the given entity.
	 * <p>
	 * Only fields that are visible for the given visibility context will have their default values
	 * persisted. Fields with specific visibility restrictions will be skipped
	 * if the visiblity context is not matched.
	 *
	 * @param entityUuid
	 *            the UUID of the entity
	 * @param contextClass
	 *            the context class for the customizable fields
	 * @param visibilityContext
	 *            the visibility context to check against field restrictions; may be null to skip
	 *            visibility checks
	 */
	public void ensureDefaultValuesForEntity(
		String entityUuid,
		CustomizableFieldContext contextClass,
		CustomizableFieldVisibilityContext visibilityContext) {
		if (StringUtils.isBlank(entityUuid) || contextClass == null) {
			return;
		}

		Map<CustomizableFieldMetadata, CustomizableFieldValue> existing = getValuesForEntity(entityUuid, contextClass);
		Set<String> existingMetadataUuids = new HashSet<>();
		for (CustomizableFieldMetadata metadata : existing.keySet()) {
			existingMetadataUuids.add(metadata.getUuid());
		}

		for (CustomizableFieldMetadata metadata : customizableFieldMetadataService.getActiveFieldsForContext(contextClass)) {
			if (existingMetadataUuids.contains(metadata.getUuid()) || StringUtils.isBlank(metadata.getDefaultValue())) {
				continue;
			}

			// Skip fields that are not visible for the given context
			if (visibilityContext != null && metadata.getVisibilityRestrictions() != null && !metadata.getVisibilityRestrictions().isBlank()) {
				CustomizableFieldVisibilityRestrictions metadataVisibilityRestrictions =
					CustomizableFieldMetadataFacadeEjb.parseVisibilityRestrictions(metadata.getVisibilityRestrictions());
				if (!metadataVisibilityRestrictions.matches(visibilityContext)) {
					continue;
				}
			}

			CustomizableFieldValue value = createNewValue(metadata, entityUuid, contextClass);
			value.setValue(metadata.getDefaultValue());
			ensurePersisted(value);
		}
	}

	public void deleteValuesForEntity(String entityUuid, CustomizableFieldContext contextClass) {
		Map<CustomizableFieldMetadata, CustomizableFieldValue> values = getValuesForEntity(entityUuid, contextClass);
		for (CustomizableFieldValue value : values.values()) {
			em.remove(value);
		}
	}

	public void softDeleteValuesForEntity(String entityUuid, CustomizableFieldContext contextClass, DeletionDetails deletionDetails) {
		Map<CustomizableFieldMetadata, CustomizableFieldValue> values = getValuesForEntity(entityUuid, contextClass);
		for (CustomizableFieldValue value : values.values()) {
			delete(value, deletionDetails);
		}
	}

	public List<CustomizableFieldValue> getValuesForMetadata(String metadataUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomizableFieldValue> cq = cb.createQuery(CustomizableFieldValue.class);
		Root<CustomizableFieldValue> root = cq.from(CustomizableFieldValue.class);

		cq.where(
			cb.and(
				cb.equal(root.get(CustomizableFieldValue.CUSTOMIZABLE_FIELD_METADATA).get(AbstractDomainObject.UUID), metadataUuid),
				cb.isFalse(root.get(DeletableAdo.DELETED))));

		return em.createQuery(cq).getResultList();
	}

	public void softDeleteValuesForMetadata(String metadataUuid, DeletionDetails deletionDetails) {
		List<CustomizableFieldValue> values = getValuesForMetadata(metadataUuid);
		for (CustomizableFieldValue value : values) {
			delete(value, deletionDetails);
		}
	}

	private CustomizableFieldValue createNewValue(CustomizableFieldMetadata metadata, String entityUuid, CustomizableFieldContext contextClass) {
		CustomizableFieldValue value = new CustomizableFieldValue();
		value.setCustomizableFieldMetadata(metadata);
		value.setEntityUuid(entityUuid);
		value.setContextClass(contextClass);
		return value;
	}
}
