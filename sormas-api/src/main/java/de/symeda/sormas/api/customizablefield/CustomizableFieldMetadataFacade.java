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

import javax.ejb.Remote;

import de.symeda.sormas.api.CoreFacade;

/**
 * Facade interface for managing customizable field metadata.
 */
@Remote
public interface CustomizableFieldMetadataFacade
    extends
    CoreFacade<CustomizableFieldMetadataDto, CustomizableFieldMetadataDto, CustomizableFieldMetadataReferenceDto, CustomizableFieldMetadataCriteria> {

    /**
     * Get all active custom fields for a specific context class
     */
    List<CustomizableFieldMetadataDto> getActiveFieldsForContext(CustomizableFieldContext contextClass);

    /**
     * Get custom fields grouped in a specific UI group
     */
    List<CustomizableFieldMetadataDto> getFieldsForUIGroup(CustomizableFieldGroup uiGroup);

    /**
     * Get custom fields ordered by UI line position
     */
    List<CustomizableFieldMetadataDto> getFieldsOrderedByUIPosition(CustomizableFieldGroup uiGroup);

    /**
     * Find field by name within a specific context
     */
    CustomizableFieldMetadataDto getByNameAndContext(String name, CustomizableFieldContext contextClass);

    /**
     * Clone an existing field with a new name
     */
    CustomizableFieldMetadataDto cloneField(String sourceUuid, String newName);

    /**
     * Set the active state of a field
     */
    void setFieldActive(String uuid, boolean active);

    /**
     * Get all field metadata
     */
    List<CustomizableFieldMetadataDto> getAll();
}
