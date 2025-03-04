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
	BaseFacade<SystemConfigurationValueDto, SystemConfigurationValueIndexDto, SystemConfigurationValueReferenceDto, SystemConfigurationValueCriteria> {

	List<SystemConfigurationValueDto> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	/**
	 * Retrieves a configuration value associated with the given key.
	 * The implementors should assure that propper caching is used.
	 *
	 * @param key
	 *            The key of the configuration value to retrieve.
	 * @return An {@link Optional} containing the value if found, or an empty {@link Optional} if not found.
	 */
	public Optional<String> getValue(String key);

	/**
	 * Clears the caches and reloads the system configuration values from the database.
	 * s
	 */
	void loadData();
}
