Window.sormas_ui_dashboard_map_CampaignDashboardMapComponent = function() { //Put correct package name

      this.myfunction = function(latitude, longitude) {     //Accept the parameters
         var map;

            //mapob is the id of your component
            map = new google.maps.Map(document.getElementById("mapob"), { 
            zoom: 8,
            center: {lat: latitude, lng: longitude}
              });
            google.maps.event.addDomListener(window, 'load', initialize);
    }
        };