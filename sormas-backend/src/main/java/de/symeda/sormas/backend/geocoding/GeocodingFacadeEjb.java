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
package de.symeda.sormas.backend.geocoding;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.geocoding.GeocodingFacade;

@Stateless(name = "GeocodingFacade")
public class GeocodingFacadeEjb implements GeocodingFacade {

	@EJB
	private GeocodingService geocodingService;

	@Override
	public boolean isEnabled() {
		return geocodingService.isEnabled();
	}

	@Override
	public GeoLatLon getLatLon(String street, String houseNumber, String postalCode, String city) {

		if (StringUtils.isNotBlank(street) && (StringUtils.isNotBlank(city) || StringUtils.isNotBlank(postalCode))) {
			return geocodingService.getLatLon(new LocationQuery(houseNumber, street, postalCode, city));
		}

		return null;
	}
}
