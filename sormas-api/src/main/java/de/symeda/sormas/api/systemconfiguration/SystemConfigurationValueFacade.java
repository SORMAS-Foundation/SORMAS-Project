/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.systemconfiguration;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.ejb.Remote;

import de.symeda.sormas.api.BaseFacade;

/**
 * Facade interface for managing system configuration settings.
 * This interface provides methods to retrieve, save, update, and delete configuration values.
 */
@Remote
public interface SystemConfigurationValueFacade
    extends
    BaseFacade<SystemConfigurationValueDto, SystemConfigurationValueIndexDto, SystemConfigurationValueReferenceDto, SystemConfigurationValueCriteria>,
    Serializable {

    /**
     * Retrieves system configuration values by their UUIDs.
     *
     * @param uuids the list of UUIDs
     * @return the list of matching system configuration value DTOs
     */
    List<SystemConfigurationValueDto> getByUuids(List<String> uuids);

    /**
     * Retrieves all UUIDs of system configuration values.
     *
     * @return the list of all UUIDs
     */
    List<String> getAllUuids();

    /**
     * Retrieves a configuration value associated with the given key.
     * The implementors should assure that proper caching is used.
     *
     * @param key The key of the configuration value to retrieve.
     * @return the value of the configuration.
     */
    String getValue(String key);

    /**
     * Checks if a configuration value exists for the given key.
     *
     * @param key The key to check.
     * @return true if the configuration value exists, false otherwise.
     */
    boolean exists(String key);

    /**
     * Clears the caches and reloads the system configuration values from the database.
     */
    void loadData();
}
