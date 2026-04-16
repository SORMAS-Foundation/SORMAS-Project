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

import java.util.Date;
import java.util.HashMap;
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

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueCriteria;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueFacade;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;

/**
 * Facade EJB implementation for customizable field values.
 */
@Stateless(name = "CustomizableFieldValueFacade")
public class CustomizableFieldValueFacadeEjb
	extends
	AbstractCoreFacadeEjb<CustomizableFieldValue, CustomizableFieldValueDto, CustomizableFieldValueDto, CustomizableFieldValueReferenceDto, CustomizableFieldValueService, CustomizableFieldValueCriteria>
	implements CustomizableFieldValueFacade {

	@EJB
	private CustomizableFieldMetadataService customizableFieldMetadataService;

	public CustomizableFieldValueFacadeEjb() {
	}

	@Inject
	public CustomizableFieldValueFacadeEjb(CustomizableFieldValueService service) {
		super(CustomizableFieldValue.class, CustomizableFieldValueDto.class, service);
	}

	@Override
	public Map<String, CustomizableFieldValueDto> getValuesForEntity(String entityUuid, CustomizableFieldContext contextClass) {
		Map<String, CustomizableFieldValue> entities = service.getValuesForEntity(entityUuid, contextClass);

		Map<String, CustomizableFieldValueDto> result = new HashMap<>();
		for (Map.Entry<String, CustomizableFieldValue> entry : entities.entrySet()) {
			result.put(entry.getKey(), toDto(entry.getValue()));
		}
		return result;
	}

	@Override
	public void saveEntityCustomFields(String entityUuid, Map<CustomizableFieldContext, Map<String, CustomizableFieldValueDto>> fieldsByContext) {
		fieldsByContext.forEach((context, fields) -> service.saveEntityValues(entityUuid, context, fields));
	}

	@Override
	public void deleteValuesForEntity(String entityUuid, CustomizableFieldContext contextClass) {
		service.deleteValuesForEntity(entityUuid, contextClass);
	}

	@Override
	public List<CustomizableFieldValueDto> getAll() {
		return service.getAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public CustomizableFieldValueDto save(@Valid @NotNull CustomizableFieldValueDto dto) {
		CustomizableFieldValue entity = service.getByUuid(dto.getUuid());
		entity = fillOrBuildEntity(dto, entity, true);
		service.ensurePersisted(entity);
		return toDto(entity);
	}

	@Override
	public void delete(String uuid, DeletionDetails deletionDetails) {
		CustomizableFieldValue entity = service.getByUuid(uuid);
		if (entity != null) {
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
	public long count(CustomizableFieldValueCriteria criteria) {
		throw new NotImplementedException();
	}

	@Override
	public List<CustomizableFieldValueDto> getIndexList(
		CustomizableFieldValueCriteria criteria,
		Integer first,
		Integer max,
		List<de.symeda.sormas.api.utils.SortProperty> sortProperties) {
		throw new NotImplementedException();
	}

	@Override
	public void validate(@Valid CustomizableFieldValueDto dto) throws ValidationRuntimeException {
		// no validation required for customizable field values
	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return DeletableEntityType.CUSTOMIZABLE_FIELD_VALUE;
	}

	@Override
	protected CustomizableFieldValueReferenceDto toRefDto(CustomizableFieldValue entity) {
		return new CustomizableFieldValueReferenceDto(entity.getUuid());
	}

	@Override
	protected void pseudonymizeDto(
		CustomizableFieldValue source,
		CustomizableFieldValueDto dto,
		Pseudonymizer<CustomizableFieldValueDto> pseudonymizer,
		boolean inJurisdiction) {
		// no sensitive fields to pseudonymize
	}

	@Override
	protected void restorePseudonymizedDto(
		CustomizableFieldValueDto dto,
		CustomizableFieldValueDto existingDto,
		CustomizableFieldValue entity,
		Pseudonymizer<CustomizableFieldValueDto> pseudonymizer) {
		// no sensitive fields to restore
	}

	@Override
	protected CustomizableFieldValue fillOrBuildEntity(
		@NotNull CustomizableFieldValueDto source,
		CustomizableFieldValue target,
		boolean checkChangeDate) {
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

	@Override
	protected CustomizableFieldValueDto toDto(CustomizableFieldValue source) {
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

		public CustomizableFieldValueFacadeEjbLocal() {
		}

		@Inject
		public CustomizableFieldValueFacadeEjbLocal(CustomizableFieldValueService service) {
			super(service);
		}
	}
}
