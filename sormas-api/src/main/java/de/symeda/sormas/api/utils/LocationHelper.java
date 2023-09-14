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

package de.symeda.sormas.api.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;

public final class LocationHelper {

	private LocationHelper() {
		// Hide Utility Class Constructor
	}

	public static String buildGpsCoordinatesCaption(Double latitude, Double longitude, Float latLonAccuracy) {

		if (latitude == null && longitude == null) {
			return "";
		} else if (latitude == null || longitude == null) {
			return I18nProperties.getString(Strings.messageIncompleteGpsCoordinates);
		} else if (latLonAccuracy == null) {
			return latitude + ", " + longitude;
		} else {
			return latitude + ", " + longitude + " +-" + Math.round(latLonAccuracy) + "m";
		}
	}

	public static String buildLocationString(LocationDto location) {

		List<String> locationFields = new ArrayList<>();

		String region = DataHelper.toStringNullable(location.getRegion());
		if (!StringUtils.isBlank(region)) {
			locationFields.add(region);
		}

		String district = DataHelper.toStringNullable(location.getDistrict());
		if (!StringUtils.isBlank(district)) {
			locationFields.add(district);
		}

		String address = DataHelper.toStringNullable(
			LocationDto.buildAddressCaption(location.getStreet(), location.getHouseNumber(), location.getPostalCode(), location.getCity()));
		if (!StringUtils.isBlank(address)) {
			locationFields.add(address);
		}

		return StringUtils.join(locationFields, ", ");
	}

	public static boolean checkIsEmptyLocation(LocationDto location) {

		try {
			List<Method> methods = Arrays.stream(location.getClass().getDeclaredMethods())
				.filter(
					m -> !Modifier.isStatic(m.getModifiers())
						&& !Modifier.isPrivate(m.getModifiers())
						&& (m.getName().startsWith("get") || m.getName().startsWith("is")))
				.collect(Collectors.toList());

			for (Method m : methods) {
				if (m.getReturnType() == String.class) {
					if (StringUtils.isNotBlank((String) m.invoke(location))) {
						return false;
					}
				} else if (m.invoke(location) != null) {
					return false;
				}
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e.getMessage());
		}

		return true;
	}

	public static void resetContinentFieldsIfCountryRemoved(LocationDto newLocation, LocationDto oldLocation) {
		if (newLocation.getCountry() == null && oldLocation.getCountry() != null) {
			newLocation.setContinent(null);
			newLocation.setSubcontinent(null);
		}
	}
}
