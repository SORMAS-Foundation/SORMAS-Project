package de.symeda.sormas.ui.map;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.Page;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.util.ReflectTools;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.ui.map.LeafletMap.MarkerClickEvent;
import de.symeda.sormas.ui.map.LeafletMap.MarkerClickListener;
import elemental.json.Json;
import elemental.json.JsonArray;


@JavaScript({
	"vaadin://map/leaflet.js",
	"vaadin://map/leaflet.fullscreen.js",
	"vaadin://map/leaflet-easy-print.js",
	"vaadin://map/leaflet.markercluster.js",
	"vaadin://map/leaflet-connector.js",
	"vaadin://map/realworld.388.js",
	"vaadin://map/grayscale.js",
	"vaadin://map/mapJS.js"})
@StyleSheet({
	"vaadin://map/leaflet.css",
	"vaadin://map/leaflet.fullscreen.css", 
	"vaadin://map/markerCluster.css",
	"vaadin://map/screen.css",
	"vaadin://map/mapCss.css"
	})
public class CampaignLeafletMap extends AbstractJavaScriptComponent {

	private final Logger logger = LoggerFactory.getLogger(getClass());

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
	public CampaignLeafletMap() {

		Page.getCurrent().getJavaScript().execute(""
				+ "var tiles = L.tileLayer.grayscale(\r\n"
				+ "          \"https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png\",\r\n"
				+ "          {\r\n"
				+ "            maxZoom: 18,\r\n"
				+ "            opacity: 0.75,\r\n"
				+ "            attribution:\r\n"
				+ "              '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors, Points &copy 2012 LINZ',\r\n"
				+ "          }\r\n"
				+ "        ),\r\n"
				+ "        latlng = L.latLng(-37.82, 175.24);\r\n"
				+ "\r\n"
				+ "      var map = L.map(\"map\", {\r\n"
				+ "        center: latlng,\r\n"
				+ "        zoom: 13,\r\n"
				+ "        layers: [tiles],\r\n"
				+ "      }).setView([35.126411, 33.429859], 9);\r\n"
				+ "      // .setMaxBounds(bounds);\r\n"
				+ "\r\n"
				+ "      var markers = L.markerClusterGroup();\r\n"
				+ "\r\n"
				+ "      for (var i = 0; i < addressPoints.length; i++) {\r\n"
				+ "        var a = addressPoints[i];\r\n"
				+ "        var title = a[2];\r\n"
				+ "        var marker = L.marker(new L.LatLng(a[0], a[1]), { title: title });\r\n"
				+ "        marker.bindPopup(title);\r\n"
				+ "        markers.addLayer(marker);\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "      map.addLayer(markers);");
	}
/*
 *  var tiles = L.tileLayer.grayscale(
          "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
          {
            maxZoom: 18,
            opacity: 0.75,
            attribution:
              '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Points &copy 2012 LINZ',
          }
        ),
        latlng = L.latLng(-37.82, 175.24);

      var map = L.map("map", {
        center: latlng,
        zoom: 13,
        layers: [tiles],
      }).setView([35.126411, 33.429859], 9);
      // .setMaxBounds(bounds);

      var markers = L.markerClusterGroup();

      for (var i = 0; i < addressPoints.length; i++) {
        var a = addressPoints[i];
        var title = a[2];
        var marker = L.marker(new L.LatLng(a[0], a[1]), { title: title });
        marker.bindPopup(title);
        markers.addLayer(marker);
      }

      map.addLayer(markers);

 */
}



