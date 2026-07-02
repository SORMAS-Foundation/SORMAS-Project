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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldCustomProperties;
import de.symeda.sormas.api.customizablefield.CustomizableFieldGroup;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataCriteria;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataFacade;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataReferenceDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldVisibilityRestrictions;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;

/**
 * Facade EJB implementation for customizable field metadata.
 */
@Stateless(name = "CustomizableFieldMetadataFacade")
public class CustomizableFieldMetadataFacadeEjb
	extends
	AbstractCoreFacadeEjb<CustomizableFieldMetadata, CustomizableFieldMetadataDto, CustomizableFieldMetadataDto, CustomizableFieldMetadataReferenceDto, CustomizableFieldMetadataService, CustomizableFieldMetadataCriteria>
	implements CustomizableFieldMetadataFacade {

	private static final ObjectMapper mapper = new ObjectMapper();

	@EJB
	private CustomizableFieldValueService customizableFieldValueService;

	public CustomizableFieldMetadataFacadeEjb() {
	}

	@Inject
	public CustomizableFieldMetadataFacadeEjb(CustomizableFieldMetadataService service) {
		super(CustomizableFieldMetadata.class, CustomizableFieldMetadataDto.class, service);
	}

	@Override
	public List<CustomizableFieldMetadataDto> getActiveFieldsForContext(CustomizableFieldContext contextClass) {
		return service.getActiveFieldsForContext(contextClass).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<CustomizableFieldMetadataDto> getFieldsForUIGroup(CustomizableFieldGroup uiGroup) {
		return service.getFieldsForUIGroup(uiGroup).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<CustomizableFieldMetadataDto> getFieldsOrderedByUIPosition(CustomizableFieldGroup uiGroup) {
		return getFieldsForUIGroup(uiGroup); // Already ordered in service
	}

	@Override
	public CustomizableFieldMetadataDto getByNameAndContext(String name, CustomizableFieldContext contextClass) {
		CustomizableFieldMetadata entity = service.getByNameAndContext(name, contextClass);
		return entity != null ? toDto(entity) : null;
	}

	@Override
	public CustomizableFieldMetadataDto cloneField(String sourceUuid, String newName) {
		CustomizableFieldMetadata cloned = service.cloneField(sourceUuid, newName);
		return toDto(cloned);
	}

	@Override
	public void setFieldActive(String uuid, boolean active) {
		CustomizableFieldMetadata field = service.getByUuid(uuid);
		if (field != null) {
			field.setActive(active);
			service.ensurePersisted(field);
		}
	}

	@Override
	public List<CustomizableFieldMetadataDto> getAll() {
		return service.getAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<CustomizableFieldMetadataDto> getIndexList(
		CustomizableFieldMetadataCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		return service.getIndexList(criteria, first, max, sortProperties).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public long count(CustomizableFieldMetadataCriteria criteria) {
		return service.count(criteria);
	}

	@Override
	public CustomizableFieldMetadataDto save(@Valid @NotNull CustomizableFieldMetadataDto dto) {
		CustomizableFieldMetadata entity = service.getByUuid(dto.getUuid());
		entity = fillOrBuildEntity(dto, entity, true);
		service.ensurePersisted(entity);
		return toDto(entity);
	}

	@Override
	public void delete(String uuid, DeletionDetails deletionDetails) {
		CustomizableFieldMetadata entity = service.getByUuid(uuid);
		if (entity != null) {
			customizableFieldValueService.softDeleteValuesForMetadata(uuid, deletionDetails);
			service.delete(entity, deletionDetails);
		}
	}

	@Override
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		throw new NotImplementedException();
	}

	@Override
	public void restore(String uuid) {
		super.restore(uuid);
	}

	@Override
	public List<ProcessedEntity> restore(List<String> uuids) {
		throw new NotImplementedException();
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		throw new NotImplementedException();
	}

	@Override
	public void validate(@Valid CustomizableFieldMetadataDto dto) throws ValidationRuntimeException {
		// no validation required for customizable field metadata
	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return DeletableEntityType.CUSTOMIZABLE_FIELD_METADATA;
	}

	@Override
	protected CustomizableFieldMetadataReferenceDto toRefDto(CustomizableFieldMetadata entity) {
		return new CustomizableFieldMetadataReferenceDto(entity.getUuid());
	}

	@Override
	protected void pseudonymizeDto(
		CustomizableFieldMetadata source,
		CustomizableFieldMetadataDto dto,
		Pseudonymizer<CustomizableFieldMetadataDto> pseudonymizer,
		boolean inJurisdiction) {
		// no sensitive fields to pseudonymize
	}

	@Override
	protected void restorePseudonymizedDto(
		CustomizableFieldMetadataDto dto,
		CustomizableFieldMetadataDto existingDto,
		CustomizableFieldMetadata entity,
		Pseudonymizer<CustomizableFieldMetadataDto> pseudonymizer) {
		// no sensitive fields to restore
	}

	@Override
	protected CustomizableFieldMetadata fillOrBuildEntity(
		@NotNull CustomizableFieldMetadataDto source,
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

	@Override
	protected CustomizableFieldMetadataDto toDto(CustomizableFieldMetadata source) {
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

		// Deserialize JSON to objects
		target.setVisibilityRestrictions(parseVisibilityRestrictions(source.getVisibilityRestrictions()));
		target.setCustomProperties(parseCustomProperties(source.getCustomProperties()));
		target.setTranslations(parseTranslations(source.getTranslations()));

		return target;
	}

	public static CustomizableFieldVisibilityRestrictions parseVisibilityRestrictions(String json) {
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
			// we could return an empty map but it might be interpreted as no translation and postgres might create a '{}' jsonb entry
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

		public CustomizableFieldMetadataFacadeEjbLocal() {
		}

		@Inject
		public CustomizableFieldMetadataFacadeEjbLocal(CustomizableFieldMetadataService service) {
			super(service);
		}
	}
}
