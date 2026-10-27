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

package de.symeda.sormas.ui.climate;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

/**
 * How the Climate data screen names places and values.
 */
public final class ClimateCaptions {

	private ClimateCaptions() {
	}

	/**
	 * @return the place a row belongs to: "Ikeja", "Well A (environment)", "Ikeja + 2 more" when the location is shared, or
	 *         "Not linked (6.60, 3.35)" for a location nothing uses any more
	 */
	public static String place(ClimateObservationIndexDto observation) {

		List<String> names = placeNames(observation);
		if (names.isEmpty()) {
			return observation.getLatitude() != null
				? String.format(I18nProperties.getString(Strings.climatePlaceNotLinked), observation.getLatitude(), observation.getLongitude())
				: I18nProperties.getString(Strings.climatePlaceNotLinkedNoPoint);
		}
		if (names.size() > 1) {
			return String.format(I18nProperties.getString(Strings.climatePlaceShared), names.get(0), names.size() - 1);
		}
		return names.get(0);
	}

	/**
	 * @return every district and environment using the row's location, environments marked as such
	 */
	public static List<String> placeNames(ClimateObservationIndexDto observation) {

		List<String> names = new ArrayList<>(observation.getDistrictNames());
		observation.getEnvironmentNames()
			.forEach(environment -> names.add(String.format(I18nProperties.getString(Strings.climatePlaceEnvironment), environment)));
		return names;
	}

	/**
	 * @return "84 (noon reading)", or empty without a value
	 */
	public static String humidity(ClimateObservationIndexDto observation) {

		if (observation.getRelativeHumidity() == null) {
			return "";
		}
		String value = number(observation.getRelativeHumidity());
		return observation.getHumidityMeasure() != null ? value + " (" + observation.getHumidityMeasure() + ")" : value;
	}

	/**
	 * @return the value without trailing zeros (26.4, 0), or empty for no value; a missing value is never shown as 0
	 */
	public static String number(Double value) {

		if (value == null) {
			return "";
		}
		return value == Math.rint(value) ? String.valueOf(value.longValue()) : String.valueOf(Math.round(value * 100) / 100.0);
	}
}
