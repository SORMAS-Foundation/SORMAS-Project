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

package de.symeda.sormas.api.customizablefield;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.CoreFacade;

/**
 * Facade interface for managing customizable field values.
 */
@Remote
public interface CustomizableFieldValueFacade
    extends CoreFacade<CustomizableFieldValueDto, CustomizableFieldValueDto, CustomizableFieldValueReferenceDto, CustomizableFieldValueCriteria> {

    /**
     * Load all custom field values for a specific entity.
     * Returns a map of field metadata DTO -> field value DTO.
     */
    Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> getValuesForEntity(String entityUuid, CustomizableFieldContext contextClass);

    /**
     * Save all custom field values for an entity in a single context.
     * fieldValues: map of field metadata DTO -> value DTO (the raw string is read from {@link CustomizableFieldValueDto#getValue()})
     */
    void saveEntityCustomFields(
        String entityUuid,
        CustomizableFieldContext contextClass,
        Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> fieldValues);

    /**
     * Save all custom field values for an entity across multiple contexts in one call.
     * fieldsByContext: map of context -> (field metadata DTO -> value DTO)
     */
    void saveEntityCustomFields(String entityUuid, Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto>> fieldsByContext);

    /**
     * Delete all custom field values for an entity
     */
    void deleteValuesForEntity(String entityUuid, CustomizableFieldContext contextClass);

    /**
     * Get all field values
     */
    List<CustomizableFieldValueDto> getAll();
}
