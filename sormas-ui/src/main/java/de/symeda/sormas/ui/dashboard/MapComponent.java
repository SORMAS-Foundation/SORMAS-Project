package de.symeda.sormas.ui.dashboard;

import java.util.HashMap;
import java.util.List;

import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.ui.ControllerProvider;

@SuppressWarnings("serial")
public class MapComponent extends VerticalLayout {

	private final GoogleMap map;
	
	private final LatLon center = new LatLon(5.43089, 7.52472);
	
	private final HashMap<GoogleMapMarker, CaseReferenceDto> caseMarkers = new HashMap<GoogleMapMarker, CaseReferenceDto>();
    
    public MapComponent() {
    	
    	setSizeFull();
    	
		map = new GoogleMap("AIzaSyAaJpN8a_NhEU-02-t5uVi02cAaZtKafkw", null, null);
        map.setCenter(center);
        map.setZoom(10);
        map.setSizeFull();
        map.setMinZoom(4);
        map.setMaxZoom(16);
        
        addComponent(map);
        
        map.addMarkerClickListener(new MarkerClickListener() {
            @Override
            public void markerClicked(GoogleMapMarker clickedMarker) {
            	CaseReferenceDto caseReferenceDto = caseMarkers.get(clickedMarker);
            	if (caseReferenceDto != null) {
            		ControllerProvider.getCaseController().navigateToData(caseReferenceDto.getUuid());
            	}
            }
        });
    }
    
    public void showCases(List<CaseDataDto> cases) {
    	
    	// clear old markers
    	for (GoogleMapMarker caseMarker : caseMarkers.keySet()) {
			map.removeMarker(caseMarker);
		}
    	caseMarkers.clear();
    	
    	double latSum = 0, lonSum = 0;
    	int counter = 0;
    	
    	for (CaseDataDto caze : cases) {

    		CaseClassification classification = caze.getCaseClassification();
    		if (classification == null || classification == CaseClassification.NO_CASE)
    			continue;
    		if (caze.getHealthFacility() == null)
    			continue;

    		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(caze.getHealthFacility().getUuid());
    		LatLon latLon = new LatLon(facility.getLocation().getLatitude(), facility.getLocation().getLongitude());
    		latSum += latLon.getLat();
    		lonSum += latLon.getLon();
    		counter++;
    		
    		MapIcon icon;
    		switch (classification) {
			case CONFIRMED:
				icon = MapIcon.RED_DOT;
				break;
			case SUSPECT:
				icon = MapIcon.YELLOW_DOT;
				break;
			case POSSIBLE:
			case PROBABLE:
				icon = MapIcon.GREY_DOT;
				break;
			default:
				throw new IllegalArgumentException(classification.toString());
			}
    		
    		GoogleMapMarker marker = new GoogleMapMarker(caze.toString(), latLon, false, icon.getUrl());
    		marker.setId(caze.getUuid().hashCode());
    		caseMarkers.put(marker, caze);
    		map.addMarker(marker);
		}
    	
    	if (counter > 0) {
    		map.setCenter(new LatLon(latSum/counter, lonSum/counter));
    	}
    }
    
    public enum MapIcon {
    	RED_DOT("red-dot"),
    	GREEN_DOT("green-dot"),
    	BLUE_DOT("blue-dot"),
    	LIGHT_BLUE_DOT("ltblue-dot"),
    	YELLOW_DOT("yellow-dot"),
    	PINK_DOT("pink-dot"),
    	PURPLE_DOT("purple-dot"),
    	GREY_DOT("grey-dot"),
    	;
    	
    	private final String imgName;

		private MapIcon(String imgName) {
			this.imgName = imgName;    		
    	}
		
		public String getUrl() {
			return "VAADIN/themes/sormastheme/mapicons/" + imgName + ".png";
		};
    }
}
