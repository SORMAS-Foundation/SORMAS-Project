window.de_symeda_sormas_ui_map_LeafletMap = function () {

	// make sure to manually reload this after making changes, because it is being cached  

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

	// doesn't correctly work - for some reason the height is set to 100px (probably base don 100%)
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
	
	var openStreetMapsLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
	    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
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
//			maxClusterRadius: 5,
//			spiderfyDistanceMultiplier: 0.5,
//		})
		var markerGroup = L.featureGroup()
			.addTo(map)
			.on("click", markerGroupClick);
		markerGroup.id = groupId;
		
		for (i=0; i<markers.length; i++) {
		
			var marker = L.marker([markers[i][0], markers[i][1]], {
				icon: mapIcons[markers[i][2]]
			}).addTo(markerGroup);
			marker.id = i;
		}
	}
	
	this.addPolygonGroup = function(groupId, polygons) {
		
		var polygonGroup = L.featureGroup()
			.addTo(map)
			.on("click", markerGroupClick);
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
 	
	function markerGroupClick(event) {
		connector.onClick(event.target.id, event.layer.id);
	}
	

	function icon(name) {
		return L.icon({
			iconUrl: 'VAADIN/map/icons/' + name + ".png",
		});
	}	
}