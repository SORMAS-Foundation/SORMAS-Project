/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization;

import java.io.Serializable;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class MapAefiDto implements Serializable {

	private static final long serialVersionUID = -7323648840592752250L;

	private Double latitude;
	private Double longitude;

	private AefiType aefiType;

	public MapAefiDto(Double longitude, Double latitude, Double associatedEntityLongitude, Double associatedEntityLatitude) {
		if (!setLatLonIfPresent(longitude, latitude, null))
			setLatLonIfPresent(associatedEntityLongitude, associatedEntityLatitude, null);
	}

	public MapAefiDto(
		Double aefiLon,
		Double aefiLat,
		Double immunizationFacilityLon,
		Double immunizationFacilityLat,
		Double immunizationPersonLon,
		Double immunizationPersonLat,
		YesNoUnknown serious) {

		aefiType = serious == YesNoUnknown.YES ? AefiType.SERIOUS : AefiType.NON_SERIOUS;
		if (!setLatLonIfPresent(aefiLon, aefiLat, aefiType))
			if (!setLatLonIfPresent(immunizationFacilityLon, immunizationFacilityLat, aefiType))
				setLatLonIfPresent(immunizationPersonLon, immunizationPersonLat, aefiType);
	}

	private boolean setLatLonIfPresent(Double longitude, Double latitude, AefiType aefiType) {
		if (longitude != null && latitude != null) {
			this.longitude = longitude;
			this.latitude = latitude;
			this.aefiType = aefiType;

			return true;
		}

		return false;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public AefiType getAefiType() {
		return aefiType;
	}
}
