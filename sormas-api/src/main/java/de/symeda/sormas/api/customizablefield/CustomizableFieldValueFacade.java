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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.common.DeletionDetails;

/**
 * Facade interface for managing customizable field values.
 */
@Remote
public interface CustomizableFieldValueFacade {

    /**
     * Load all custom field values for a specific entity
     * Returns a map of field metadata UUID -> field value DTO
     */
    Map<String, CustomizableFieldValueDto> getValuesForEntity(String entityUuid, CustomizableFieldContext contextClass);

    /**
     * Save all custom field values for an entity in a single context.
     * fieldValues: map of field metadata UUID -> value DTO (the raw string is read from {@link CustomizableFieldValueDto#getValue()})
     */
    default void saveEntityCustomFields(
        String entityUuid,
        CustomizableFieldContext contextClass,
        Map<String, CustomizableFieldValueDto> fieldValues) {
        saveEntityCustomFields(entityUuid, Map.of(contextClass, fieldValues));
    }

    /**
     * Save all custom field values for an entity across multiple contexts in one call.
     * fieldsByContext: map of context -> (field metadata UUID -> value DTO)
     */
    void saveEntityCustomFields(String entityUuid, Map<CustomizableFieldContext, Map<String, CustomizableFieldValueDto>> fieldsByContext);

    /**
     * Delete all custom field values for an entity
     */
    void deleteValuesForEntity(String entityUuid, CustomizableFieldContext contextClass);

    /**
     * Get a field value by UUID
     */
    CustomizableFieldValueDto getByUuid(String uuid);

    /**
     * Get all field values
     */
    List<CustomizableFieldValueDto> getAll();

    /**
     * Save a single customizable field value
     */
    CustomizableFieldValueDto save(@Valid @NotNull CustomizableFieldValueDto dto);

    /**
     * Delete a customizable field value
     */
    void delete(String uuid, DeletionDetails deletionDetails);
}
