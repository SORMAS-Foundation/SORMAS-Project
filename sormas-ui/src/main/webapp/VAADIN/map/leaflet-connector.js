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
window.de_symeda_sormas_ui_map_LeafletMap = function () {

	// make sure to manually reload this after making changes, because it is being cached  
	// see https://leafletjs.com/reference-1.3.4.html

	// these mapIcons have to match the MarkerIcon java enum
	// clustering always falls back to the lowest icon index
	var mapIcons = [
		"case confirmed",
		"case suspect", 
		"case probable",
		"case unclassified",
		"facility confirmed",
		"facility suspect",
		"facility probable",
		"facility unclassified",
		"contact long-overdue",
		"contact overdue",
		"contact ok",
		"event outbreak",
		"event rumor",	
		"sample case",
		"sample contact",
		"sample event-participant",
		"sample environment",
		];
	
	var connector = this;

	var map = L.map(this.getElement(), { 
		center: [51.505, -0.09], 
		zoom: 13,
		trackResize: true,
		});


	// full-screen control
	map.addControl(new L.Control.Fullscreen({
		position: 'topright',
	}));

	// doesn't correctly work - for some reason the height is set to 100px (probably based on 100%)
//	// print control
//	var printControl = L.easyPrint({
//		position: 'bottomright',
//		sizeModes: ['Current'],
//		filename: 'SORMAS Map Export',
//		exportOnly: true,
//	}).addTo(map);
	
	// update the map whenever the vaadin element is resized
	this.addResizeListener(this.getElement(), function(o,b) {
		map.invalidateSize(true);
	});
	
//	var openStreetMapsLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
//	    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
//	});
	
	var openStreetMapsLayer = L.tileLayer("https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png", {
		attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors. Tiles courtesy of Humanitarian OpenStreetMap Team'
	});

	this.setTileLayer = function(layerUri, attribution){
		openStreetMapsLayer.remove();
		openStreetMapsLayer = L.tileLayer(layerUri, {
			attribution: attribution
		});

		this.onStateChange();
	}

	this.onStateChange = function () {

		map.setView([this.getState().centerLatitude, this.getState().centerLongitude], this.getState().zoom);
		
		if (this.getState().tileLayerVisible) {
			openStreetMapsLayer.addTo(map);
			openStreetMapsLayer.setOpacity(this.getState().tileLayerOpacity);
		} else {
			openStreetMapsLayer.remove();
		}
	};
	
	this.addMarkerGroup = function(groupId, markers) {

		// check prerequisites for clustering icon logic
		if (mapIcons[4].indexOf("facility") != 0
				|| mapIcons[7].indexOf("facility") != 0)
			throw "mapIcons indices 4 to 7 are supposed to be facilities";
		
		var markerGroup = L.markerClusterGroup({

			maxClusterRadius: 15,
			
			/** define how marker clusters are rendered **/
			iconCreateFunction: function(cluster) {
				children = cluster.getAllChildMarkers();
				count = 0;
				var minIconIndex = mapIcons.length;
				for (i=0; i<children.length; i++) {
					count += children[i].count;
					var iconIndex = children[i].iconIndex;
					
					// facilities are clustered as cases (see check above)
					if (iconIndex >= 4 && iconIndex <= 7)
						iconIndex -= 4;
					
					// use "most important" icon == smallest index
					if (iconIndex < minIconIndex)
						minIconIndex = iconIndex;
				}

				var size = 20 + 5 * Math.min(Math.ceil((count-1)/10), 4);

				var icon = new L.DivIcon({ 
					html: count > 1 ? '<div><span>' + count + '</span></div>' : '<div></div>', 
							className: 'marker cluster ' + mapIcons[minIconIndex], 
							iconSize: new L.Point(size,size) });
				icon.cluster = cluster;
				return icon;
			}			
		})
//		var markerGroup = L.featureGroup()
			.addTo(map)
			.on("click", featureGroupClick);
		markerGroup.id = groupId;
		
		for (iter=0; iter<markers.length; iter++) {
		
			var marker = markers[iter];
			var count = marker[3];
			var size = 20 + 5 * Math.min(Math.ceil((count-1)/10), 4);
			var leafletMarker = L.marker([marker[0], marker[1]], {
				icon: new L.DivIcon({ 
					html: count > 1 ? '<div><span>' + marker[3] + '</span></div>' : '&nbsp;', 
					className: 'marker ' + mapIcons[marker[2]], 
					iconSize: new L.Point(size,size) }),
			});
			leafletMarker.id = iter;
			leafletMarker.iconIndex = marker[2];
			leafletMarker.count = count;
			leafletMarker.addTo(markerGroup);
		}
	}
	
	this.addPolygonGroup = function(groupId, polygons) {
		
		var polygonGroup = L.featureGroup()
			.addTo(map)
			.on("click", featureGroupClick);
		polygonGroup.id = groupId;
		
		for (i=0; i<polygons.length; i++) {
		
			var polygon = L.polygon(polygons[i].latLons, polygons[i].options)
				.addTo(polygonGroup);
			if (polygons[i].caption != null) {
				polygon.bindTooltip(polygons[i].caption);
			}
			polygon.id = i;
		}
	}
	
	this.removeGroup = function(groupId) {
		
		map.eachLayer(function(layer){
		    if (layer.id == groupId) {
		    	map.removeLayer(layer);
		    }
		});
	}

	this.addShapefileAttribution = function(attribution){
		map.attributionControl.addAttribution(attribution);
	}
 	
	function featureGroupClick(event) {
		// call to server
		connector.onClick(event.target.id, event.layer.id);
	}
}
