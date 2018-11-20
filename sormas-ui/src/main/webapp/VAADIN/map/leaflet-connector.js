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

	var mapIcons = [
		icon("red-dot"),
		icon("red-dot-small"), 
		icon("red-dot-large"),
		icon("red-dot-very-large"),
		icon("red-house"),
		icon("red-house-small"),
		icon("red-house-large"),
		icon("red-house-very-large"),
		icon("red-contact"),
		icon("yellow-dot"), 
		icon("yellow-dot-small"), 
		icon("yellow-dot-large"),
		icon("yellow-dot-very-large"),
		icon("yellow-house"),
		icon("yellow-house-small"),
		icon("yellow-house-large"),
		icon("yellow-house-very-large"), 
		icon("orange-dot"),
		icon("orange-dot-small"), 
		icon("orange-dot-large"),
		icon("orange-dot-very-large"),
		icon("orange-house"),
		icon("orange-house-small"), 
		icon("orange-house-large"),
		icon("orange-house-very-large"),
		icon("orange-contact"), 
		icon("grey-dot"),
		icon("grey-dot-small"), 
		icon("grey-dot-large"), 
		icon("grey-dot-very-large"),
		icon("grey-house"), 
		icon("grey-house-small"), 
		icon("grey-house-large"),
		icon("grey-house-very-large"), 
		icon("grey-contact"), 
		icon("green-contact"),
		icon("outbreak"), 
		icon("rumor")
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
	
	var openStreetMapsLayer = L.tileLayer("http://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png", {
		attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors. Tiles courtesy of Humanitarian OpenStreetMap Team'
	});

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

//		var markerGroup = L.markerClusterGroup({
//			//maxClusterRadius: 20,
//			
//			iconCreateFunction: function(cluster) {
//				children = cluster.getAllChildMarkers();
//				count = 0;
//				anyContact = false;
//				anyEvent = false;
//				anyRed = false;
//				anyOrange = false;
//				anyYellow = false;
//				for (i=0; i<children.length; i++) {
//					count += children[i].number;
//					if (!anyContact && children[i].__parent._group.id == "contacts")
//						anyContact = true;
//					if (!anyEvent && children[i].__parent._group.id == "events")
//						anyEvent = true;
//					if (!anyRed && children[i].options.icon.options.iconUrl.includes("red"))
//						anyRed = true;
//					if (!anyRed && !anyOrange && children[i].options.icon.options.iconUrl.includes("orange"))
//						anyOrange = true;
//					if (!anyRed && !anyOrange && !anyYellow && children[i].options.icon.options.iconUrl.includes("yellow"))
//						anyYellow = true;
//				}
//				
//				var c = ' marker-cluster-';
//				if (anyRed) {
//					c += 'red';
//				} else if (anyYellow) {
//					c += 'yellow';
//				} else if (anyOrange) {
//					c += 'orange';
//				} else {
//					c += 'grey';
//				}
//
//				classNameVal = anyEvent ? 'marker-cluster light-box' : (anyContact ? 'marker-cluster' : 'marker-cluster box');
//				return new L.DivIcon({ html: '<div><span>' + count + '</span></div>', className: classNameVal + c, iconSize: new L.Point(40, 40) });				
//			}			
//		})
		var markerGroup = L.featureGroup()
			.addTo(map)
			.on("click", featureGroupClick);
		markerGroup.id = groupId;
		
		for (i=0; i<markers.length; i++) {
		
			var marker = L.marker([markers[i][0], markers[i][1]], {
				icon: mapIcons[markers[i][2]]
			});
			marker.id = i;
			marker.number = markers[i][3];
			marker.addTo(markerGroup);
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
			polygon.bindTooltip(polygons[i].caption);
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
 	
	function featureGroupClick(event) {
		// call to server
		connector.onClick(event.target.id, event.layer.id);
	}
	

	function icon(name) {
		return L.icon({
			iconUrl: 'VAADIN/map/icons/' + name + ".png",
		});
	}	
}