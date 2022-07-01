/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.event;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.HideForCountriesExcept;

public enum TypeOfPlace {

	FACILITY,
	@HideForCountriesExcept(countries = {})
	FACILITY_23_IFSG,
	@HideForCountriesExcept(countries = {})
	COMMUNITY_FACILITY,
	@HideForCountriesExcept(countries = {})
	FACILITY_36_IFSG,
	FESTIVITIES,
	HOME,
	MEANS_OF_TRANSPORT,
	PUBLIC_PLACE,
	SCATTERED,
	UNKNOWN,
	OTHER;

	public static final List<TypeOfPlace> FOR_CASES = Arrays.asList(FACILITY, HOME);
	public static final List<TypeOfPlace> FOR_ACTIVITY_AS_CASE_GERMANY =
		Arrays.asList(FACILITY_23_IFSG, COMMUNITY_FACILITY, FACILITY_36_IFSG, UNKNOWN, OTHER);
	private static final List<TypeOfPlace> FACILITY_TYPES = Arrays.asList(FACILITY, FACILITY_23_IFSG, COMMUNITY_FACILITY, FACILITY_36_IFSG);

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static boolean isFacilityType(Object typeOfPlace) {
		return FACILITY_TYPES.contains(typeOfPlace);
	}
}
