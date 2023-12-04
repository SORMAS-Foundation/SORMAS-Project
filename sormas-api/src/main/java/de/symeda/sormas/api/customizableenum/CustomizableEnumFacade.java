/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.customizableenum;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.Disease;

@Remote
public interface CustomizableEnumFacade
	extends BaseFacade<CustomizableEnumValueDto, CustomizableEnumValueIndexDto, CustomizableEnumValueReferenceDto, CustomizableEnumCriteria> {

	List<CustomizableEnumValueDto> getAllAfter(Date date);

	List<CustomizableEnumValueDto> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	/**
	 * Retrieves the cached content of a specific enum value instance. The result is already internationalized based
	 * on the user's language, or the server language as a fallback. If this specific value has not been requested yet,
	 * the cache is extended with it on demand.
	 * 
	 * @param type
	 *            The type for which to retrieve the enum value
	 * @param value
	 *            The value used as the identifier in the database, identical to {@link CustomizableEnumValueDto#getValue()}
	 * @param <T>
	 *            The specific extension of {@link CustomizableEnum} for type safety
	 * @return The enum instance containing its value, internationalized caption, and optional properties
	 */
	<T extends CustomizableEnum> T getEnumValue(CustomizableEnumType type, String value);

	/**
	 * Works similar to the {@link CustomizableEnumFacade#getEnumValues(CustomizableEnumType, Disease)}, but looks up a specific value.
	 * Unlike the {@link CustomizableEnumFacade#getEnumValue(CustomizableEnumType, String)}, this method does not throw a RuntimeException
	 * when an enum can not be found.
	 * 
	 * @param type
	 *            The type for which to retrieve the enum value
	 * @param value
	 *            The value used as the identifier in the database, identical to {@link CustomizableEnumValueDto#getValue()}
	 * @param disease
	 *            The disease for which to retrieve the enum values. If null, all enum values that are disease-independent are retrieved
	 * @param <T>
	 *            The specific extension of {@link CustomizableEnum} for type safety
	 * @return The enum instance containing its value, internationalized caption, and optional properties
	 */
	<T extends CustomizableEnum> T getEnumValue(CustomizableEnumType type, String value, Disease disease) throws CustomEnumNotFoundException;

	/**
	 * Checks if the specified enum value exists for the specified type and disease.
	 * 
	 * @param type
	 *            The type for which to retrieve the enum value
	 * @param value
	 *            The values to search for
	 * @param disease
	 *            The disease for which to retrieve the enum values. If null, all enum values that are disease-independent are retrieved
	 * @return true if the enum value exists
	 */
	boolean existsEnumValue(CustomizableEnumType type, String value, Disease disease);

	/**
	 * Retrieves the cached contents of all enum value instances of the specified type. The results are already
	 * internationalized based on the user's language, or the server language as a fallback. If the enum values for the
	 * specified type and disease have not been requested yet, the cache is extended with them on demand.
	 * 
	 * @param type
	 *            The type for which to retrieve the enum values
	 * @param disease
	 *            The disease for which to retrieve the enum values. If null, all enum values that are disease-independent are retrieved
	 * @param <T>
	 *            The specific extension of {@link CustomizableEnum} for type safety
	 * @return A list of all enum instances containing their values, internationalized captions, and optional properties
	 */
	<T extends CustomizableEnum> List<T> getEnumValues(CustomizableEnumType type, Disease disease);

	/**
	 * Indicates if the specified type has any entry in the database for a specific disease.
	 *
	 * @param type
	 *            The type for which to retrieve the enum values
	 * @param disease
	 *            The disease for which to retrieve the enum values. If null, all enum values that are disease-independent are retrieved
	 * @return true if the type has any entry in the database
	 */
	boolean hasEnumValues(CustomizableEnumType type, Disease disease);

	/**
	 * Clears the caches and reloads the customizable enum values from the database. Does not load enum values by language
	 * or disease as those are retrieved on demand by using {@link #getEnumValue(CustomizableEnumType, String)} and
	 * {@link #getEnumValues(CustomizableEnumType, Disease)}. Exposed to this facade to allow reloading the caches without
	 * having to restart the server.
	 */
	void loadData();

}
