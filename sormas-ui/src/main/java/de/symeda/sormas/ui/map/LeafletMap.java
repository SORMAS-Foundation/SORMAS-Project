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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.map;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.List;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.util.ReflectTools;

import de.symeda.sormas.api.region.GeoLatLon;
import elemental.json.Json;
import elemental.json.JsonArray;

/**
 * JS and CSS files are in the VAADIN folder, so we can also access required
 * images and other resources.
 * 
 * @author Martin Wahnschaffe
 */
@JavaScript({ "vaadin://map/leaflet.js", "vaadin://map/leaflet.fullscreen.js", "vaadin://map/leaflet-easy-print.js", "vaadin://map/leaflet.markercluster.js", "vaadin://map/leaflet-connector.js" })
@StyleSheet({ "vaadin://map/leaflet.css", "vaadin://map/leaflet.fullscreen.css", "vaadin://map/MarkerCluster.css" })
public class LeafletMap extends AbstractJavaScriptComponent {

	private static final long serialVersionUID = 1671451734103288729L;

	private static int currMapId = 0;

	public static int nextMapId() {
		return ++currMapId;
	}

	private int mapId = nextMapId();

	/**
	 * Creates the chart object.
	 */
	@SuppressWarnings("serial")
	public LeafletMap() {
		setId(getDomId());
		getState().setZoom(5);
		getState().setTileLayerVisible(true);
		getState().setTileLayerOpacity(1);

		addFunction("onClick", new JavaScriptFunction() {
			@Override
			public void call(JsonArray arguments) {
				String groupId = arguments.getString(0);
				int markerIndex = (int) arguments.getNumber(1);
				LeafletMap.this.fireEvent(new MarkerClickEvent(LeafletMap.this, groupId, markerIndex));
			}
		});
	}

	/**
	 * @return the state of the chart that is shared with the web browser
	 */
	@Override
	protected LeafletState getState() {
		return (LeafletState) super.getState();
	}

	/**
	 * @return the DOM ID of the map component
	 */
	public String getDomId() {
		return "leaflet_" + mapId;
	}

	/**
	 * Sets the Leaflet JavaScript code describing the map.
	 */
	public void setZoom(int zoom) {
		getState().setZoom(zoom);
	}

	public int getZoom() {
		return getState().getZoom();
	}

	public void setCenter(GeoLatLon coordinates) {
		getState().setCenterLatitude(coordinates.getLat());
		getState().setCenterLongitude(coordinates.getLon());
	}
	
	public void setTileLayerVisible(boolean tileLayerVisible) {
		getState().setTileLayerVisible(tileLayerVisible);
	}

	public void setTileLayerOpacity(float tileLayerOpacity) {
		getState().setTileLayerOpacity(tileLayerOpacity);
	}

	public void addMarkerGroup(String groupId, List<LeafletMarker> markers) {
		JsonArray markersJson = Json.createArray();
		for (LeafletMarker marker : markers) {
			markersJson.set(markersJson.length(), marker.toJson());
		}
		callFunction("addMarkerGroup", groupId, markersJson);
	}

	public void addPolygonGroup(String groupId, List<LeafletPolygon> polygons) {
		JsonArray polygonsJson = Json.createArray();
		for (LeafletPolygon polygon : polygons) {
			polygonsJson.set(polygonsJson.length(), polygon.toJson());
		}
		callFunction("addPolygonGroup", groupId, polygonsJson);
	}

	public void removeGroup(String groupId) {
		callFunction("removeGroup", groupId);
	}

	public void addMarkerClickListener(MarkerClickListener listener) {
		addListener(MarkerClickEvent.class, listener, MarkerClickListener.MARKER_CLICK_METHOD);
	}

	public interface MarkerClickListener extends Serializable {

		Method MARKER_CLICK_METHOD = ReflectTools.findMethod(MarkerClickListener.class, "markerClick", MarkerClickEvent.class);

		void markerClick(MarkerClickEvent event);
	}

	public static class MarkerClickEvent extends EventObject {
		private static final long serialVersionUID = -2607378360765308016L;
		private final String groupId;
		private final int markerIndex;

		public MarkerClickEvent(LeafletMap map, String groupId, int markerIndex) {
			super(map);
			this.groupId = groupId;
			this.markerIndex = markerIndex;
		}

		public String getGroupId() {
			return groupId;
		}

		public int getMarkerIndex() {
			return markerIndex;
		}
	}
}
