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

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import de.symeda.sormas.api.geo.GeoLatLon;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;

public class LeafletPolygon {

	private String caption;
	private double[][] latLons;
	private double[] minLatLon;
	private double[] maxLatLon;
	private double[][][] holeLatLons;
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
		this.minLatLon = null;
		this.maxLatLon = null;
	}

	public void setLatLons(GeoLatLon[] geoLatLons) {
		double[][] latLons = Arrays.stream(geoLatLons)
			.map(
				latLon -> new double[] {
					latLon.getLat(),
					latLon.getLon() })
			.toArray(size -> new double[size][]);
		setLatLons(latLons);
	}

	// return (and if necessary calculate) minimum latitude and longitude
	public double[] getMinLatLon() {
		if (minLatLon == null) {
			minLatLon = new double[2];
			minLatLon[0] = Collections.min(Arrays.asList(latLons).stream().map(x -> x[0]).collect(Collectors.toList()));
			minLatLon[1] = Collections.min(Arrays.asList(latLons).stream().map(x -> x[1]).collect(Collectors.toList()));
		}
		return minLatLon;
	}

	// return (and if necessary calculate) minimum latitude and longitude
	public double[] getMaxLatLon() {
		if (maxLatLon == null) {
			maxLatLon = new double[2];
			maxLatLon[0] = Collections.max(Arrays.asList(latLons).stream().map(x -> x[0]).collect(Collectors.toList()));
			maxLatLon[1] = Collections.max(Arrays.asList(latLons).stream().map(x -> x[1]).collect(Collectors.toList()));
		}
		return maxLatLon;
	}

	public double[][][] getHoleLatLons() {
		return holeLatLons;
	}

	public void setHoleLatLons(double[][][] holeLatLons) {
		this.holeLatLons = holeLatLons;
	}

	public void setHoleLatLons(GeoLatLon[][] geoHoleLatLons) {
		double[][][] holeLatLons = Arrays.stream(geoHoleLatLons).map(latLons -> {
			return Arrays.stream(latLons)
				.map(
					latLon -> new double[] {
						latLon.getLat(),
						latLon.getLon() })
				.toArray(size -> new double[size][]);
		}).toArray(size -> new double[size][][]);
		setHoleLatLons(holeLatLons);
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
		if (caption != null) {
			polygon.put("caption", caption);
		}
		if (options != null) {
			polygon.put("options", (JsonObject) JsonUtil.parse(options));
		}
		if (latLons != null) {
			JsonArray latLonsJson = Json.createArray();
			for (double[] latLon : latLons) {
				JsonArray latLonJson = Json.createArray();
				latLonJson.set(0, (int) (latLon[0] * 10000.0) / 10000.0);
				latLonJson.set(1, (int) (latLon[1] * 10000.0) / 10000.0);

				latLonsJson.set(latLonsJson.length(), latLonJson);
			}

			if (holeLatLons != null) {
				JsonArray latLonsOuterJson = Json.createArray();
				// first is outer
				latLonsOuterJson.set(latLonsOuterJson.length(), latLonsJson);

				// additional are holes
				for (double[][] latLons : holeLatLons) {
					latLonsJson = Json.createArray();
					for (double[] latLon : latLons) {
						JsonArray latLonJson = Json.createArray();
						latLonJson.set(0, (int) (latLon[0] * 10000.0) / 10000.0);
						latLonJson.set(1, (int) (latLon[1] * 10000.0) / 10000.0);

						latLonsJson.set(latLonsJson.length(), latLonJson);
					}
					latLonsOuterJson.set(latLonsOuterJson.length(), latLonsJson);
				}

				polygon.put("latLons", latLonsOuterJson);
			} else {

				polygon.put("latLons", latLonsJson);
			}

		}
		return polygon;
	}
}
