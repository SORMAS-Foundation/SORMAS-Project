window.de_symeda_sormas_ui_map_LeafletMap = function () {

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

	var map = L.map(this.getElement()).setView([51.505, -0.09], 13);
   
	L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
	    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
	}).addTo(map);

	this.onStateChange = function () {

		// make sure to manually reload this after making changes, because it is being cached  

		map.setView([this.getState().center.lat, this.getState().center.lon], this.getState().zoom);
	};
	
	this.addMarkerGroup = function(groupId, markers) {

//		var markerGroup = L.markerClusterGroup({
//			maxClusterRadius: 5,
//			spiderfyDistanceMultiplier: 0.5,
//		})
		var markerGroup = L.featureGroup()
		.addTo(map).on("click", markerGroupClick);
		markerGroup.id = groupId;
		
		for (i=0; i<markers.length; i++) {
		
			var marker = L.marker([markers[i][0], markers[i][1]], {
				icon: mapIcons[markers[i][2]]
			}).addTo(markerGroup);
			marker.id = i;
		}
	}
	
	this.removeMarkerGroup = function(groupId) {
		
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