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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldCustomProperties;
import de.symeda.sormas.api.customizablefield.CustomizableFieldGroup;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataCriteria;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataFacade;
import de.symeda.sormas.api.customizablefield.CustomizableFieldVisibilityRestrictions;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.util.DtoHelper;

/**
 * Facade EJB implementation for customizable field metadata.
 */
@Stateless(name = "CustomizableFieldMetadataFacade")
public class CustomizableFieldMetadataFacadeEjb implements CustomizableFieldMetadataFacade {

	private static final ObjectMapper mapper = new ObjectMapper();

	@EJB
	private CustomizableFieldMetadataService customizableFieldMetadataService;

	@Override
	public List<CustomizableFieldMetadataDto> getActiveFieldsForContext(CustomizableFieldContext contextClass) {
		return customizableFieldMetadataService.getActiveFieldsForContext(contextClass)
			.stream()
			.map(CustomizableFieldMetadataFacadeEjb::toDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<CustomizableFieldMetadataDto> getFieldsForUIGroup(CustomizableFieldGroup uiGroup) {
		return customizableFieldMetadataService.getFieldsForUIGroup(uiGroup)
			.stream()
			.map(CustomizableFieldMetadataFacadeEjb::toDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<CustomizableFieldMetadataDto> getFieldsOrderedByUIPosition(CustomizableFieldGroup uiGroup) {
		return getFieldsForUIGroup(uiGroup);	// Already ordered in service
	}

	@Override
	public CustomizableFieldMetadataDto getByNameAndContext(String name, CustomizableFieldContext contextClass) {
		CustomizableFieldMetadata entity = customizableFieldMetadataService.getByNameAndContext(name, contextClass);
		return entity != null ? toDto(entity) : null;
	}

	@Override
	public CustomizableFieldMetadataDto cloneField(String sourceUuid, String newName) {
		CustomizableFieldMetadata cloned = customizableFieldMetadataService.cloneField(sourceUuid, newName);
		return toDto(cloned);
	}

	@Override
	public void activateField(String uuid) {
		CustomizableFieldMetadata field = customizableFieldMetadataService.getByUuid(uuid);
		if (field != null) {
			field.setActive(true);
			customizableFieldMetadataService.ensurePersisted(field);
		}
	}

	@Override
	public void deactivateField(String uuid) {
		CustomizableFieldMetadata field = customizableFieldMetadataService.getByUuid(uuid);
		if (field != null) {
			field.setActive(false);
			customizableFieldMetadataService.ensurePersisted(field);
		}
	}

	@Override
	public CustomizableFieldMetadataDto getByUuid(String uuid) {
		CustomizableFieldMetadata entity = customizableFieldMetadataService.getByUuid(uuid);
		return entity != null ? toDto(entity) : null;
	}

	@Override
	public List<CustomizableFieldMetadataDto> getAll() {
		return customizableFieldMetadataService.getAll().stream().map(CustomizableFieldMetadataFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public List<CustomizableFieldMetadataDto> getIndexList(
		CustomizableFieldMetadataCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		return customizableFieldMetadataService.getIndexList(criteria, first, max, sortProperties)
			.stream()
			.map(CustomizableFieldMetadataFacadeEjb::toDto)
			.collect(Collectors.toList());
	}

	@Override
	public long count(CustomizableFieldMetadataCriteria criteria) {
		return customizableFieldMetadataService.count(criteria);
	}

	@Override
	public CustomizableFieldMetadataDto save(CustomizableFieldMetadataDto dto) {
		CustomizableFieldMetadata entity = customizableFieldMetadataService.getByUuid(dto.getUuid());
		entity = fillOrBuildEntity(dto, entity, true);
		customizableFieldMetadataService.ensurePersisted(entity);
		return toDto(entity);
	}

	@Override
	public void delete(String uuid, DeletionDetails deletionDetails) {
		CustomizableFieldMetadata entity = customizableFieldMetadataService.getByUuid(uuid);
		if (entity != null) {
			customizableFieldMetadataService.deletePermanent(entity);
		}
	}

	@Override
	public boolean exists(String uuid) {
		return customizableFieldMetadataService.exists(uuid);
	}

	public CustomizableFieldMetadata fillOrBuildEntity(
		CustomizableFieldMetadataDto source,
		CustomizableFieldMetadata target,
		boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, CustomizableFieldMetadata::new, checkChangeDate);

		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setFieldType(source.getFieldType());
		target.setContextClass(source.getContextClass());
		target.setUiGroup(source.getUiGroup());
		target.setUiLinePosition(source.getUiLinePosition());
		target.setUiLineWeight(source.getUiLineWeight());
		target.setActive(source.isActive());
		target.setMandatory(source.isMandatory());
		target.setReadOnly(source.isReadOnly());
		target.setDefaultValue(source.getDefaultValue());

		// Serialize complex properties to JSON
		if (source.getVisibilityRestrictions() != null) {
			try {
				target.setVisibilityRestrictions(mapper.writeValueAsString(source.getVisibilityRestrictions()));
			} catch (JsonProcessingException e) {
				throw new IllegalStateException("Failed to serialize visibilityRestrictions", e);
			}
		} else {
			target.setVisibilityRestrictions(null);
		}

		if (source.getCustomProperties() != null) {
			try {
				target.setCustomProperties(mapper.writeValueAsString(source.getCustomProperties()));
			} catch (JsonProcessingException e) {
				throw new IllegalStateException("Failed to serialize customProperties", e);
			}
		} else {
			target.setCustomProperties(null);
		}
		if (source.getTranslations() != null) {
			try {
				target.setTranslations(mapper.writeValueAsString(source.getTranslations()));
			} catch (JsonProcessingException e) {
				throw new IllegalStateException("Failed to serialize translations", e);
			}
		} else {
			target.setTranslations(null);
		}

		return target;
	}

	public static CustomizableFieldMetadataDto toDto(CustomizableFieldMetadata source) {
		if (source == null) {
			return null;
		}

		CustomizableFieldMetadataDto target = new CustomizableFieldMetadataDto();
		DtoHelper.fillDto(target, source);

		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setFieldType(source.getFieldType());
		target.setContextClass(source.getContextClass());
		target.setUiGroup(source.getUiGroup());
		target.setUiLinePosition(source.getUiLinePosition());
		target.setUiLineWeight(source.getUiLineWeight());
		target.setActive(source.isActive());
		target.setMandatory(source.isMandatory());
		target.setReadOnly(source.isReadOnly());
		target.setDefaultValue(source.getDefaultValue());

		// Deserialize JSON to maps
		target.setVisibilityRestrictions(parseVisibilityRestrictions(source.getVisibilityRestrictions()));
		target.setCustomProperties(parseCustomProperties(source.getCustomProperties()));
		target.setTranslations(parseTranslations(source.getTranslations()));

		return target;
	}

	private static CustomizableFieldVisibilityRestrictions parseVisibilityRestrictions(String json) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		try {
			return mapper.readValue(json, CustomizableFieldVisibilityRestrictions.class);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to parse visibilityRestrictions JSON", e);
		}
	}

	private static CustomizableFieldCustomProperties parseCustomProperties(String json) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		try {
			return mapper.readValue(json, CustomizableFieldCustomProperties.class);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to parse customProperties JSON", e);
		}
	}

	private static Map<String, Map<String, String>> parseTranslations(String json) {
		if (StringUtils.isBlank(json)) {
			// we could return a empty map but it might be interpreted as no translaton and postgres might create a '{}' jsonb entry
			return Collections.emptyMap();
		}
		try {
			return mapper.readValue(json, new TypeReference<Map<String, Map<String, String>>>() {
			});
		} catch (IOException e) {
			throw new IllegalStateException("Failed to parse translations JSON", e);
		}
	}

	@LocalBean
	@Stateless
	public static class CustomizableFieldMetadataFacadeEjbLocal extends CustomizableFieldMetadataFacadeEjb {
	}
}
