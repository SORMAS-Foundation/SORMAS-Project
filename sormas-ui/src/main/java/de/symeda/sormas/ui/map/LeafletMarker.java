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
package de.symeda.sormas.ui.map;

import de.symeda.sormas.api.region.GeoLatLon;
import elemental.json.Json;
import elemental.json.JsonArray;

public class LeafletMarker {

	private double latitude;
	private double longitude;
	private MarkerIcon icon;
	private int markerCount = 1;

	/**
	 * lat & lon is less precise to save bandwidth
	 */
	public JsonArray toJson() {
		JsonArray marker = Json.createArray();
		marker.set(0, (int) (latitude * 10000.0) / 10000.0);
		marker.set(1, (int) (longitude * 10000.0) / 10000.0);
		marker.set(2, icon.ordinal());
		marker.set(3, markerCount);
		return marker;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatLon(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public void setLatLon(GeoLatLon coordinates) {
		this.latitude = coordinates.getLat();
		this.longitude = coordinates.getLon();
	}

	public MarkerIcon getIcon() {
		return icon;
	}

	public void setIcon(MarkerIcon icon) {
		this.icon = icon;
	}

	public int getMarkerCount() {
		return markerCount;
	}

	public void setMarkerCount(int markerCount) {
		this.markerCount = markerCount;
	}
}
