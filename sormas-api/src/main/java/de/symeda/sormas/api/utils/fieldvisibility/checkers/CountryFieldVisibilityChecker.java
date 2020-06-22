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
package de.symeda.sormas.api.utils.fieldvisibility.checkers;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class CountryFieldVisibilityChecker implements FieldVisibilityCheckers.FieldBasedChecker {

	private final String countryLocale;

	public CountryFieldVisibilityChecker(String countryLocale) {
		this.countryLocale = countryLocale;
	}

	@Override
	public boolean isVisible(AccessibleObject accessibleObject) {
		if (accessibleObject.isAnnotationPresent(HideForCountries.class)) {
			String[] countries = accessibleObject.getAnnotation(HideForCountries.class).countries();
			for (String country : countries) {
				if (countryLocale.startsWith(country)) {
					return false;
				}
			}

			return true;
		}

		if (accessibleObject.isAnnotationPresent(HideForCountriesExcept.class)) {
			String[] countries = accessibleObject.getAnnotation(HideForCountriesExcept.class).countries();
			for (String country : countries) {
				if (countryLocale.startsWith(country)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}
}
