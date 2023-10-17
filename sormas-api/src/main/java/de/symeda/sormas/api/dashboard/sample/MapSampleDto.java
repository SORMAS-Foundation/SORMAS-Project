/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.dashboard.sample;

import java.io.Serializable;

import de.symeda.sormas.api.sample.SampleAssociationType;

public class MapSampleDto implements Serializable {

	private static final long serialVersionUID = -9000671285025223947L;

	private Double latitude;
	private Double longitude;

	private SampleAssociationType associationType;

	public MapSampleDto(Double longitude, Double latitude, Double associatedEntityLongitude, Double associatedEntityLatitude) {
		if (!setLatLonIfPresent(longitude, latitude, null))
			setLatLonIfPresent(associatedEntityLongitude, associatedEntityLatitude, null);
	}

	public MapSampleDto(
		Double casePersonLon,
		Double casePersonLat,
		Double caseLon,
		Double caseLat,
		Double contactPersonLon,
		Double contactPersonLat,
		Double contactLon,
		Double contactLat,
		Double eventParticipantPersonLon,
		Double eventParticipantPersonLat,
		Double eventLon,
		Double eventLat) {
		if (!setLatLonIfPresent(casePersonLon, casePersonLat, SampleAssociationType.CASE))
			if (!setLatLonIfPresent(caseLon, caseLat, SampleAssociationType.CASE))
				if (!setLatLonIfPresent(contactPersonLon, contactPersonLat, SampleAssociationType.CONTACT))
					if (!setLatLonIfPresent(contactLon, contactLat, SampleAssociationType.CONTACT))
						if (!setLatLonIfPresent(eventParticipantPersonLon, eventParticipantPersonLat, SampleAssociationType.EVENT_PARTICIPANT))
							setLatLonIfPresent(eventLon, eventLat, SampleAssociationType.EVENT_PARTICIPANT);
	}

	private boolean setLatLonIfPresent(Double longitude, Double latitude, SampleAssociationType associationType) {
		if (longitude != null && latitude != null) {
			this.longitude = longitude;
			this.latitude = latitude;
			this.associationType = associationType;

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

	public SampleAssociationType getAssociationType() {
		return associationType;
	}
}
