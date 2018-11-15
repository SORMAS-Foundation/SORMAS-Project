package de.symeda.sormas.ui.map;

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
