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
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;

/**
 * Service class for customizable field values.
 */
@Stateless
@LocalBean
public class CustomizableFieldValueService extends AdoServiceWithUserFilterAndJurisdiction<CustomizableFieldValue> {

	@EJB
	private CustomizableFieldMetadataService customizableFieldMetadataService;

	public CustomizableFieldValueService() {
		super(CustomizableFieldValue.class);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CustomizableFieldValue> from) {
		// No jurisdiction filtering for customizable field values
		return null;
	}

	public Map<String, CustomizableFieldValue> getValuesForEntity(String entityUuid, CustomizableFieldContext contextClass) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomizableFieldValue> cq = cb.createQuery(CustomizableFieldValue.class);
		Root<CustomizableFieldValue> root = cq.from(CustomizableFieldValue.class);

		Predicate predicate = cb.and(
			cb.equal(root.get(CustomizableFieldValue.ENTITY_UUID), entityUuid),
			cb.equal(root.get(CustomizableFieldValue.CONTEXT_CLASS), contextClass));

		cq.where(predicate);

		List<CustomizableFieldValue> values = em.createQuery(cq).getResultList();

		// Convert to map for easier access by field metadata UUID
		Map<String, CustomizableFieldValue> result = new HashMap<>();
		for (CustomizableFieldValue value : values) {
			result.put(value.getCustomizableFieldMetadata().getUuid(), value);
		}
		return result;
	}

	public void saveEntityValues(String entityUuid, CustomizableFieldContext contextClass, Map<String, CustomizableFieldValueDto> fieldUuidToValue) {
		// Get existing values
		Map<String, CustomizableFieldValue> existing = getValuesForEntity(entityUuid, contextClass);

		// Update or create values
		for (Map.Entry<String, CustomizableFieldValueDto> entry : fieldUuidToValue.entrySet()) {
			String fieldMetadataUuid = entry.getKey();
			CustomizableFieldMetadata metadata = customizableFieldMetadataService.getByUuid(fieldMetadataUuid);

			if (metadata == null) {
				throw new IllegalArgumentException("Field metadata not found: " + fieldMetadataUuid);
			}

			CustomizableFieldValue value = existing.getOrDefault(fieldMetadataUuid, createNewValue(metadata, entityUuid, contextClass));
			value.setValue(entry.getValue().getValue());
			ensurePersisted(value);
		}
	}

	public void deleteValuesForEntity(String entityUuid, CustomizableFieldContext contextClass) {
		Map<String, CustomizableFieldValue> values = getValuesForEntity(entityUuid, contextClass);
		for (CustomizableFieldValue value : values.values()) {
			em.remove(value);
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
