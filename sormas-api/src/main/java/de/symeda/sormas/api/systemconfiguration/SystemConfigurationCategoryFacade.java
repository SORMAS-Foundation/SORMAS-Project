/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.systemconfiguration;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.BaseFacade;

/**
 * Facade interface for managing System Configuration Categories.
 * Provides methods to perform CRUD operations and retrieve specific categories.
 */
@Remote
public interface SystemConfigurationCategoryFacade
    extends
    BaseFacade<SystemConfigurationCategoryDto, SystemConfigurationCategoryIndexDto, SystemConfigurationCategoryReferenceDto, SystemConfigurationCategoryCriteria> {

    /**
     * Get system configuration categories by their UUIDs.
     *
     * @param uuids the list of UUIDs
     * @return the list of matching system configuration category DTOs
     */
    List<SystemConfigurationCategoryDto> getByUuids(List<String> uuids);

    /**
     * Get the default system configuration category DTO.
     *
     * @return the default system configuration category DTO
     */
    SystemConfigurationCategoryDto getDefaultCategoryDto();

    /**
     * Get the default system configuration category reference DTO.
     *
     * @return the default system configuration category reference DTO
     */
    SystemConfigurationCategoryReferenceDto getDefaultCategoryReferenceDto();

    /**
     * Get a category DTO by category name.
     *
     * @param name the category name
     * @return the system configuration category DTO
     */
    SystemConfigurationCategoryDto getCategoryDtoByName(String name);

    /**
     * Get a category reference DTO by category name.
     *
     * @param name the category name
     * @return the system configuration category reference DTO
     */
    SystemConfigurationCategoryReferenceDto getCategoryReferenceDtoByName(String name);

    /**
     * Get all UUIDs of system configuration categories.
     *
     * @return the list of all UUIDs
     */
    List<String> getAllUuids();
}
