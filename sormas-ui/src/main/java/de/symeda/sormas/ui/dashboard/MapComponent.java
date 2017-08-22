package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class MapComponent extends VerticalLayout {

	private static final int MARKER_NORMAL_SIZE = 2;
	private static final int MARKER_LARGE_SIZE = 3;
	private static final int MARKER_VERY_LARGE_SIZE = 4;
	
	private final GoogleMap map;
	
	private final LatLon centerKano = new LatLon(11.5636503, 8.4596675);
	private final LatLon centerOyo = new LatLon(8.110803, 3.625342);
	
	private final HashMap<GoogleMapMarker, FacilityDto> facilityMarkers = new HashMap<GoogleMapMarker, FacilityDto>();
	private final HashMap<GoogleMapMarker, CaseDataDto> caseMarkers = new HashMap<GoogleMapMarker, CaseDataDto>();
	private final HashMap<FacilityDto, List<CaseDataDto>> facilities = new HashMap<>();
	private final List<CaseDataDto> mapCases = new ArrayList<>();
    
    public MapComponent() {    	
		map = new GoogleMap("AIzaSyAaJpN8a_NhEU-02-t5uVi02cAaZtKafkw", null, null);
		
    	UserDto user = LoginHelper.getCurrentUser();
    	if (user.getRegion() != null) {
    		map.setCenter("Oyo".equals(user.getRegion().getCaption()) ? centerOyo : centerKano);
    	}
        
        map.setZoom(9);
        map.setSizeFull();
        map.setMinZoom(4);
        map.setMaxZoom(16);
        
        addComponent(map);
        
        MapComponent mapComponent = this;
        map.addMarkerClickListener(new MarkerClickListener() {
            @Override
            public void markerClicked(GoogleMapMarker clickedMarker) {
            	FacilityDto facility = facilityMarkers.get(clickedMarker);
            	CaseDataDto caze = caseMarkers.get(clickedMarker);
            	
            	if (facility != null) {
                	VerticalLayout layout = new VerticalLayout();
                	Window window = VaadinUiUtil.showPopupWindow(layout);
                	CasePopupGrid caseGrid = new CasePopupGrid(window, facility, mapComponent);
                	caseGrid.setHeightMode(HeightMode.ROW);
                	layout.addComponent(caseGrid);
                	layout.setMargin(true);
                	window.setCaption("Cases in " + facilityMarkers.get(clickedMarker).getCaption());
            	} else if (caze != null) {
            		ControllerProvider.getCaseController().navigateToData(caze.getUuid());
            	}
            }
        });
    }
    
    public void showMarkers(List<CaseDataDto> cases) {
    	// clear old markers
    	for (GoogleMapMarker facilityMarker : facilityMarkers.keySet()) {
			map.removeMarker(facilityMarker);
		}
    	for (GoogleMapMarker caseMarker : caseMarkers.keySet()) {
    		map.removeMarker(caseMarker);
    	}
    	facilityMarkers.clear();
    	caseMarkers.clear();
    	facilities.clear();
    	mapCases.clear();
    	
    	// collect cases for health facilities
    	for (CaseDataDto caze : cases) {
    		CaseClassification classification = caze.getCaseClassification();
    		if (classification == null || classification == CaseClassification.NO_CASE)
    			continue;
    		if (caze.getHealthFacility() == null)
    			continue;
    		if (caze.getHealthFacility().getUuid().equals(FacilityDto.NONE_FACILITY_UUID) ||
    				caze.getHealthFacility().getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
    			mapCases.add(caze);
    			continue;
    		}
    		
    		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(caze.getHealthFacility().getUuid());
    		if (facilities.get(facility) == null) {
    			facilities.put(facility, new ArrayList<CaseDataDto>());
    		}
    		facilities.get(facility).add(caze);
    	}
    	
    	// create markers for all facilities with cases
    	for (FacilityDto facility : facilities.keySet()) {
    		
    		if (facility.getLatitude() == null || facility.getLongitude() == null) {
    			continue;
    		}
    		
    		LatLon latLon = new LatLon(facility.getLatitude(), facility.getLongitude());
    		MapIcon icon;
    		
    		// colorize the icon by the "strongest" classification type (order as in enum) and set its size depending
    		// on the number of cases
    		int numberOfCases = facilities.get(facility).size();
    		Set<CaseClassification> classificationSet = new HashSet<>();
    		for (CaseDataDto caseDto : facilities.get(facility)) {
    			classificationSet.add(caseDto.getCaseClassification());
    		}
    		
    		if (classificationSet.contains(CaseClassification.CONFIRMED)) {
    			if (numberOfCases >= MARKER_VERY_LARGE_SIZE) icon = MapIcon.RED_HOUSE_VERY_LARGE;
    			else if (numberOfCases >= MARKER_LARGE_SIZE) icon = MapIcon.RED_HOUSE_LARGE;
    			else if (numberOfCases >= MARKER_NORMAL_SIZE) icon = MapIcon.RED_HOUSE;
    			else icon = MapIcon.RED_HOUSE_SMALL;
    		} else if (classificationSet.contains(CaseClassification.PROBABLE)) {
    			if (numberOfCases >= MARKER_VERY_LARGE_SIZE) icon = MapIcon.ORANGE_DOT_VERY_LARGE;
    			else if (numberOfCases >= MARKER_LARGE_SIZE) icon = MapIcon.ORANGE_DOT_LARGE;
    			else if (numberOfCases >= MARKER_NORMAL_SIZE) icon = MapIcon.ORANGE_DOT;
    			else icon = MapIcon.ORANGE_DOT_SMALL;
    		} else if (classificationSet.contains(CaseClassification.SUSPECT)) {
    			if (numberOfCases >= MARKER_VERY_LARGE_SIZE) icon = MapIcon.YELLOW_DOT_VERY_LARGE;
    			else if (numberOfCases >= MARKER_LARGE_SIZE) icon = MapIcon.YELLOW_DOT_LARGE;
    			else if (numberOfCases >= MARKER_NORMAL_SIZE) icon = MapIcon.YELLOW_DOT;
    			else icon = MapIcon.YELLOW_DOT_SMALL;
    		} else {
    			if (numberOfCases >= MARKER_VERY_LARGE_SIZE) icon = MapIcon.GREY_DOT_VERY_LARGE;
    			else if (numberOfCases >= MARKER_LARGE_SIZE) icon = MapIcon.GREY_DOT_LARGE;
    			else if (numberOfCases >= MARKER_NORMAL_SIZE) icon = MapIcon.GREY_DOT;
    			else icon = MapIcon.GREY_DOT_SMALL;
    		}
    		
    		// create and place the marker
    		GoogleMapMarker marker = new GoogleMapMarker(facility.toString() + " (" + facilities.get(facility).size() + " case(s))", latLon, false, icon.getUrl());
    		marker.setId(facility.getUuid().hashCode());
    		facilityMarkers.put(marker, facility);
    		map.addMarker(marker);
    	}
    	
    	for (CaseDataDto caze : mapCases) {
    		if (caze.getReportLat() == null || caze.getReportLon() == null) {
    			continue;
    		}
    		
    		LatLon latLon = new LatLon(caze.getReportLat(), caze.getReportLon());
    		MapIcon icon;
    		
    		if (caze.getCaseClassification() == CaseClassification.CONFIRMED) {
    			icon = MapIcon.RED_DOT;
    		} else if (caze.getCaseClassification() == CaseClassification.PROBABLE) {
    			icon = MapIcon.ORANGE_CIRCLE;
    		} else if (caze.getCaseClassification() == CaseClassification.SUSPECT) {
    			icon = MapIcon.YELLOW_CIRCLE;
    		} else {
    			icon = MapIcon.GREY_CIRCLE;
    		}
    		
    		GoogleMapMarker marker = new GoogleMapMarker(caze.toString(), latLon, false, icon.getUrl());
    		marker.setId(caze.getUuid().hashCode());
    		caseMarkers.put(marker, caze);
    		map.addMarker(marker);
    	}
    }
    
    public enum MapIcon {
    	RED_DOT("red-dot"),
    	RED_DOT_SMALL("red-dot-small"),
    	RED_DOT_LARGE("red-dot-large"),
    	RED_DOT_VERY_LARGE("red-dot-very-large"),
    	RED_HOUSE("red-house"),
    	RED_HOUSE_SMALL("red-house-small"),
    	RED_HOUSE_LARGE("red-house-large"),
    	RED_HOUSE_VERY_LARGE("red-house-very-large"),
    	GREEN_DOT("green-dot"),
    	BLUE_DOT("blue-dot"),
    	LIGHT_BLUE_DOT("ltblue-dot"),
    	YELLOW_DOT("yellow-dot"),
    	YELLOW_DOT_SMALL("yellow-dot-small"),
    	YELLOW_DOT_LARGE("yellow-dot-large"),
    	YELLOW_DOT_VERY_LARGE("yellow-dot-very-large"),
    	ORANGE_DOT("orange-dot"),
    	ORANGE_DOT_SMALL("orange-dot-small"),
    	ORANGE_DOT_LARGE("orange-dot-large"),
    	ORANGE_DOT_VERY_LARGE("orange-dot-very-large"),
    	PINK_DOT("pink-dot"),
    	PURPLE_DOT("purple-dot"),
    	GREY_DOT("grey-dot"),
    	GREY_DOT_SMALL("grey-dot-small"),
    	GREY_DOT_LARGE("grey-dot-large"),
    	GREY_DOT_VERY_LARGE("grey-dot-very-large"),
    	RED_CIRCLE("red-circle"),
    	ORANGE_CIRCLE("orange-circle"),
    	YELLOW_CIRCLE("yellow-circle"),
    	GREY_CIRCLE("grey-circle")
    	;
    	
    	private final String imgName;

		private MapIcon(String imgName) {
			this.imgName = imgName;    		
    	}
		
		public String getUrl() {
			return "VAADIN/themes/sormastheme/mapicons/" + imgName + ".png";
		};
    }
    
    public List<CaseDataDto> getCasesForFacility(FacilityDto facility) {
    	return facilities.get(facility);
    }
    
    public List<CaseDataDto> getCasesWithoutGPSTag() {
    	List<CaseDataDto> casesWithoutGPSTag = new ArrayList<>();
    	
    	for (CaseDataDto caze : mapCases) {
    		if (caze.getReportLat() == null || caze.getReportLon() == null) {
    			casesWithoutGPSTag.add(caze);
    		}
    	}
    	
    	return casesWithoutGPSTag;
    }
}
