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

import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

import java.lang.reflect.AccessibleObject;
import java.util.regex.Pattern;

public class CountryFieldVisibilityChecker implements FieldVisibilityCheckers.FieldBasedChecker {

	private final String countryLocale;

	private static final String FULL_COUNTRY_LOCALE_PATTERN = "[a-zA-Z]*-[a-zA-Z]*";

	public CountryFieldVisibilityChecker(String countryLocale) {
		this.countryLocale = countryLocale;
	}

	@Override
	public boolean isVisible(AccessibleObject accessibleObject) {
		if (accessibleObject.isAnnotationPresent(HideForCountries.class)) {
			String[] countries = accessibleObject.getAnnotation(HideForCountries.class).countries();
			for (String country : countries) {
				// If the country locale is complete (e.g. de-DE), check the last (country) part; 
				// otherwise check the first (language) part
				if (Pattern.matches(FULL_COUNTRY_LOCALE_PATTERN, countryLocale)) {
					if (countryLocale.toLowerCase().endsWith(country.toLowerCase())) {
						return false;
					}
				} else {
					if (countryLocale.toLowerCase().startsWith(country.toLowerCase())) {
						return false;
					}
				}
			}

			return true;
		}

		if (accessibleObject.isAnnotationPresent(HideForCountriesExcept.class)) {
			String[] countries = accessibleObject.getAnnotation(HideForCountriesExcept.class).countries();
			for (String country : countries) {
				// If the country locale is complete (e.g. de-DE), check the last (country) part; 
				// otherwise check the first (language) part
				if (Pattern.matches(FULL_COUNTRY_LOCALE_PATTERN, countryLocale)) {
					if (countryLocale.toLowerCase().endsWith(country.toLowerCase())) {
						return true;
					}
				} else {
					if (countryLocale.toLowerCase().startsWith(country.toLowerCase())) {
						return true;
					}
				}
			}

			return false;
		}

		return true;
	}
}
