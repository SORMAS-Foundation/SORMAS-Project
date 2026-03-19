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
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueFacade;
import de.symeda.sormas.backend.util.DtoHelper;

/**
 * Facade EJB implementation for customizable field values.
 */
@Stateless(name = "CustomizableFieldValueFacade")
public class CustomizableFieldValueFacadeEjb implements CustomizableFieldValueFacade {

	@EJB
	private CustomizableFieldValueService customizableFieldValueService;

	@EJB
	private CustomizableFieldMetadataService customizableFieldMetadataService;

	@Override
	public Map<String, CustomizableFieldValueDto> getValuesForEntity(String entityUuid, CustomizableFieldContext contextClass) {
		Map<String, CustomizableFieldValue> entities = customizableFieldValueService.getValuesForEntity(entityUuid, contextClass);

		Map<String, CustomizableFieldValueDto> result = new HashMap<>();
		for (Map.Entry<String, CustomizableFieldValue> entry : entities.entrySet()) {
			result.put(entry.getKey(), toDto(entry.getValue()));
		}
		return result;
	}

	@Override
	public void saveEntityCustomFields(String entityUuid, Map<CustomizableFieldContext, Map<String, CustomizableFieldValueDto>> fieldsByContext) {
		fieldsByContext.forEach((context, fields) -> customizableFieldValueService.saveEntityValues(entityUuid, context, fields));
	}

	@Override
	public void deleteValuesForEntity(String entityUuid, CustomizableFieldContext contextClass) {
		customizableFieldValueService.deleteValuesForEntity(entityUuid, contextClass);
	}

	@Override
	public CustomizableFieldValueDto getByUuid(String uuid) {
		CustomizableFieldValue entity = customizableFieldValueService.getByUuid(uuid);
		return entity != null ? toDto(entity) : null;
	}

	@Override
	public List<CustomizableFieldValueDto> getAll() {
		return customizableFieldValueService.getAll().stream().map(CustomizableFieldValueFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public CustomizableFieldValueDto save(CustomizableFieldValueDto dto) {
		CustomizableFieldValue entity = customizableFieldValueService.getByUuid(dto.getUuid());
		entity = fillOrBuildEntity(dto, entity, true);
		customizableFieldValueService.ensurePersisted(entity);
		return toDto(entity);
	}

	@Override
	public void delete(String uuid, DeletionDetails deletionDetails) {
		CustomizableFieldValue entity = customizableFieldValueService.getByUuid(uuid);
		if (entity != null) {
			customizableFieldValueService.deletePermanent(entity);
		}
	}

	public CustomizableFieldValue fillOrBuildEntity(CustomizableFieldValueDto source, CustomizableFieldValue target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, CustomizableFieldValue::new, checkChangeDate);

		// Load the metadata reference
		CustomizableFieldMetadata metadata = customizableFieldMetadataService.getByUuid(source.getCustomizableFieldMetadataUuid());
		if (metadata == null) {
			throw new IllegalArgumentException("Field metadata not found: " + source.getCustomizableFieldMetadataUuid());
		}

		target.setCustomizableFieldMetadata(metadata);
		target.setEntityUuid(source.getEntityUuid());
		target.setContextClass(source.getContextClass());
		target.setValue(source.getValue());

		return target;
	}

	public static CustomizableFieldValueDto toDto(CustomizableFieldValue source) {
		if (source == null) {
			return null;
		}

		CustomizableFieldValueDto target = new CustomizableFieldValueDto();
		DtoHelper.fillDto(target, source);

		target.setCustomizableFieldMetadataUuid(source.getCustomizableFieldMetadata().getUuid());
		target.setEntityUuid(source.getEntityUuid());
		target.setContextClass(source.getContextClass());
		target.setValue(source.getValue());

		return target;
	}

	@LocalBean
	@Stateless
	public static class CustomizableFieldValueFacadeEjbLocal extends CustomizableFieldValueFacadeEjb {
	}
}
