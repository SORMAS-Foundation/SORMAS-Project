/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api;

import java.util.regex.Pattern;

import de.symeda.sormas.api.i18n.I18nProperties;

public final class CountryHelper {

	public static final String COUNTRY_CODE_GERMANY = "de";
	public static final String COUNTRY_CODE_FRANCE = "fr";
	public static final String COUNTRY_CODE_SWITZERLAND = "ch";

	public static boolean isCountry(String countryLocale, String country) {
		// If the country locale is complete (e.g. de-DE), check the last (country) part; 
		// otherwise check the first (language) part
		return Pattern.matches(I18nProperties.FULL_COUNTRY_LOCALE_PATTERN, countryLocale)
			? countryLocale.toLowerCase().endsWith(country.toLowerCase())
			: countryLocale.toLowerCase().startsWith(country.toLowerCase());
	}

	public static boolean isInCountries(String countryLocale, String... countries) {
		for (String country : countries) {
			if (isCountry(countryLocale, country)) {
				return true;
			}
		}
		return false;
	}
}
