package de.symeda.sormas.ui.map;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;

public class LeafletPolygon {

	private String caption;
	private double[][] latLons;
	private String options;


	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public double[][] getLatLons() {
		return latLons;
	}

	public void setLatLons(double[][] latLons) {
		this.latLons = latLons;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * lat & lon is less precise to save bandwidth
	 */
	public JsonObject toJson() {
		JsonObject polygon = Json.createObject();
		polygon.put("caption", caption);
		polygon.put("options", (JsonObject)JsonUtil.parse(options));
		JsonArray latLonsJson = Json.createArray();
		for (double[] latLon : latLons) {
			JsonArray latLonJson = Json.createArray();
			latLonJson.set(0, (int) (latLon[0] * 10000.0) / 10000.0);
			latLonJson.set(1, (int) (latLon[1] * 10000.0) / 10000.0);
			
			latLonsJson.set(latLonsJson.length(), latLonJson);
		}
		polygon.put("latLons", latLonsJson);
		return polygon;
	}

}
