package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MapClickListener;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactMapDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.region.RegionDataDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class MapComponent extends VerticalLayout {

	final static Logger logger = LoggerFactory.getLogger(MapComponent.class);

	private static final int MARKER_NORMAL_SIZE = 2;
	private static final int MARKER_LARGE_SIZE = 3;
	private static final int MARKER_VERY_LARGE_SIZE = 4;
	
	private final GoogleMap map;
	
	private final HashMap<GoogleMapMarker, FacilityDto> markerCaseFacilities = new HashMap<GoogleMapMarker, FacilityDto>();
	private final HashMap<GoogleMapMarker, CaseDataDto> markerCases = new HashMap<GoogleMapMarker, CaseDataDto>();
	private final HashMap<GoogleMapMarker, ContactMapDto> markerContacts = new HashMap<GoogleMapMarker, ContactMapDto>();
	
	private final HashMap<RegionDataDto, GoogleMapPolygon> regionPolygons = new HashMap<RegionDataDto, GoogleMapPolygon>();
	
	private final HashMap<FacilityReferenceDto, List<CaseDataDto>> facilitiesCasesMaps = new HashMap<>();
	private final List<CaseDataDto> mapCases = new ArrayList<>();
	private final List<ContactMapDto> mapContacts = new ArrayList<>();
    
    public MapComponent() {    	
		map = new GoogleMap("AIzaSyAaJpN8a_NhEU-02-t5uVi02cAaZtKafkw", null, null);
		
		if (LoginHelper.isUserInRole(UserRole.NATIONAL_USER)) {
			map.setZoom(6);
			map.setCenter(new LatLon(9.101718, 7.396517));
		} else {
	    	UserDto user = LoginHelper.getCurrentUser();
	    	if (user.getRegion() != null) {
				GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfRegion(user.getRegion());
				map.setCenter(new LatLon(mapCenter.getLon(), mapCenter.getLat()));
	    	} else {
				map.setCenter(new LatLon(9.101718, 7.396517));
	    	}
	        map.setZoom(9);
		}
        
        map.setSizeFull();
        map.setMinZoom(4);
        map.setMaxZoom(16);
        
        addComponent(map);
        
        MapComponent mapComponent = this;
        map.addMarkerClickListener(new MarkerClickListener() {
            @Override
            public void markerClicked(GoogleMapMarker clickedMarker) {
            	FacilityDto facility = markerCaseFacilities.get(clickedMarker);
            	CaseDataDto caze = markerCases.get(clickedMarker);
            	ContactMapDto contact = markerContacts.get(clickedMarker);
            	
            	if (facility != null) {
                	VerticalLayout layout = new VerticalLayout();
                	Window window = VaadinUiUtil.showPopupWindow(layout);
                	CasePopupGrid caseGrid = new CasePopupGrid(window, facility, mapComponent);
                	caseGrid.setHeightMode(HeightMode.ROW);
                	layout.addComponent(caseGrid);
                	layout.setMargin(true);
                	window.setCaption("Cases in " + markerCaseFacilities.get(clickedMarker).getCaption());
            	} else if (caze != null) {
            		ControllerProvider.getCaseController().navigateToData(caze.getUuid());
            	} else if (contact != null) {
            		ControllerProvider.getContactController().navigateToData(contact.getUuid());
            	}
            }
        });
        
        map.addMapClickListener(new MapClickListener() {
			
			@Override
			public void mapClicked(LatLon position) {
				if (regionPolygons.isEmpty())
					return;
				
				RegionReferenceDto regionRef = FacadeProvider.getGeoShapeProvider().getRegionByCoord(new GeoLatLon(position.getLat(), position.getLon()));
				
				if (regionRef != null) {
					GoogleMapPolygon googleMapPolygon = regionPolygons.get(regionRef);
					googleMapPolygon.setStrokeWeight(3);
				}				
			}
		});
    }
    
    public void clearRegionShapes() {
    	
    	for (GoogleMapPolygon regionPolygon : regionPolygons.values()) {
			map.removePolygonOverlay(regionPolygon);
		}
    	regionPolygons.clear();
    	
    	map.removeStyleName("no-tiles");
    }
    
    public void showRegionsShapes(RegionMapVisualization regionMapVisualization, Date onsetFromDate, Date onsetToDate, Disease disease) {

    	clearRegionShapes();
    	
    	map.addStyleName("no-tiles");

    	List<RegionDataDto> regions = FacadeProvider.getRegionFacade().getAllData();
	    Map<RegionReferenceDto, Long> caseCountPerRegion = FacadeProvider.getCaseFacade().getCaseCountPerRegion(onsetFromDate, onsetToDate, disease);

	    for (RegionDataDto region : regions) {

	    	RegionReferenceDto regionRef = region.toReferenceDto();
		    GeoLatLon[][] regionShape = FacadeProvider.getGeoShapeProvider().getRegionShape(regionRef);
		    
		    for (GeoLatLon[] regionShapePart : regionShape) {
		    	
			    GoogleMapPolygon polygon = new GoogleMapPolygon(
			    		Arrays.stream(regionShapePart)
			    		.map(c -> new LatLon(c.getLat(), c.getLon()))
			    		.collect(Collectors.toList()));

			    polygon.setStrokeOpacity(0.5);
	    		
			    long caseCount = caseCountPerRegion.containsKey(regionRef) ? caseCountPerRegion.get(regionRef) : 0;
			    switch (regionMapVisualization) {
				case CASE_COUNT:
				    if (caseCount == 0) {
				    	polygon.setFillOpacity(0);
				    } else if (caseCount <= 5) {
					    polygon.setFillColor("#FFD800");
					    polygon.setFillOpacity(0.5);
				    } else if (caseCount <= 10) {
					    polygon.setFillColor("#FF6A00");
					    polygon.setFillOpacity(0.5);
				    } else {
					    polygon.setFillColor("#FF0000");
					    polygon.setFillOpacity(0.5);
				    }
					break;
				case CASE_INCIDENCE:
				    float incidence = (float)caseCount / (region.getPopulation() / 10000);
				    if (incidence == 0) {
				    	polygon.setFillOpacity(0);
				    } else if (incidence <= 0.5f) {
					    polygon.setFillColor("#FFD800");
					    polygon.setFillOpacity(0.5);
				    } else if (incidence <= 1) {
					    polygon.setFillColor("#FF6A00");
					    polygon.setFillOpacity(0.5);
				    } else {
					    polygon.setFillColor("#FF0000");
					    polygon.setFillOpacity(0.5);
				    }
					break;

				default:
					throw new IllegalArgumentException(regionMapVisualization.toString());
				}
			    
			    regionPolygons.put(region, polygon);
			    map.addPolygonOverlay(polygon);
		    }
		}
    }
    
    public void clearCaseMarkers() {

    	for (GoogleMapMarker facilityMarker : markerCaseFacilities.keySet()) {
			map.removeMarker(facilityMarker);
		}
    	for (GoogleMapMarker caseMarker : markerCases.keySet()) {
    		map.removeMarker(caseMarker);
    	}

    	markerCaseFacilities.clear();
    	markerCases.clear();
    	facilitiesCasesMaps.clear();
    	mapCases.clear();
    }


    public void showCaseMarkers(List<CaseDataDto> cases) {
    	
    	clearCaseMarkers();

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
    		
    		FacilityReferenceDto facility = caze.getHealthFacility();
    		if (facilitiesCasesMaps.get(facility) == null) {
    			facilitiesCasesMaps.put(facility, new ArrayList<CaseDataDto>());
    		}
    		facilitiesCasesMaps.get(facility).add(caze);
    	}
    	
    	// create markers for all facilities with cases
    	for (FacilityReferenceDto facilityReference : facilitiesCasesMaps.keySet()) {
    		
    		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(facilityReference.getUuid());
    		
    		if (facility.getLatitude() == null || facility.getLongitude() == null) {
    			continue;
    		}
    		
    		LatLon latLon = new LatLon(facility.getLatitude(), facility.getLongitude());
    		MapIcon icon;
    		
    		// colorize the icon by the "strongest" classification type (order as in enum) and set its size depending
    		// on the number of cases
    		int numberOfCases = facilitiesCasesMaps.get(facility).size();
    		Set<CaseClassification> classificationSet = new HashSet<>();
    		for (CaseDataDto caseDto : facilitiesCasesMaps.get(facility)) {
    			classificationSet.add(caseDto.getCaseClassification());
    		}
    		
    		if (classificationSet.contains(CaseClassification.CONFIRMED)) {
    			if (numberOfCases >= MARKER_VERY_LARGE_SIZE) icon = MapIcon.RED_HOUSE_VERY_LARGE;
    			else if (numberOfCases >= MARKER_LARGE_SIZE) icon = MapIcon.RED_HOUSE_LARGE;
    			else if (numberOfCases >= MARKER_NORMAL_SIZE) icon = MapIcon.RED_HOUSE;
    			else icon = MapIcon.RED_HOUSE_SMALL;
    		} else if (classificationSet.contains(CaseClassification.PROBABLE)) {
    			if (numberOfCases >= MARKER_VERY_LARGE_SIZE) icon = MapIcon.ORANGE_HOUSE_VERY_LARGE;
    			else if (numberOfCases >= MARKER_LARGE_SIZE) icon = MapIcon.ORANGE_HOUSE_LARGE;
    			else if (numberOfCases >= MARKER_NORMAL_SIZE) icon = MapIcon.ORANGE_HOUSE;
    			else icon = MapIcon.ORANGE_HOUSE_SMALL;
    		} else if (classificationSet.contains(CaseClassification.SUSPECT)) {
    			if (numberOfCases >= MARKER_VERY_LARGE_SIZE) icon = MapIcon.YELLOW_HOUSE_VERY_LARGE;
    			else if (numberOfCases >= MARKER_LARGE_SIZE) icon = MapIcon.YELLOW_HOUSE_LARGE;
    			else if (numberOfCases >= MARKER_NORMAL_SIZE) icon = MapIcon.YELLOW_HOUSE;
    			else icon = MapIcon.YELLOW_HOUSE_SMALL;
    		} else {
    			if (numberOfCases >= MARKER_VERY_LARGE_SIZE) icon = MapIcon.GREY_HOUSE_VERY_LARGE;
    			else if (numberOfCases >= MARKER_LARGE_SIZE) icon = MapIcon.GREY_HOUSE_LARGE;
    			else if (numberOfCases >= MARKER_NORMAL_SIZE) icon = MapIcon.GREY_HOUSE;
    			else icon = MapIcon.GREY_HOUSE_SMALL;
    		}
    		
    		// create and place the marker
    		GoogleMapMarker marker = new GoogleMapMarker(facility.toString() + " (" + facilitiesCasesMaps.get(facility).size() + " case(s))", latLon, false, icon.getUrl());
    		marker.setId(facility.getUuid().hashCode());
    		markerCaseFacilities.put(marker, facility);
    		map.addMarker(marker);
    	}
    	
    	for (CaseDataDto caze : mapCases) {
    		if (caze.getReportLat() == null || caze.getReportLon() == null) {
    			continue;
    		}
    		
    		LatLon latLon = new LatLon(caze.getReportLat(), caze.getReportLon());
    		MapIcon icon;
    		
    		if (caze.getCaseClassification() == CaseClassification.CONFIRMED) {
    			icon = MapIcon.RED_DOT_SMALL;
    		} else if (caze.getCaseClassification() == CaseClassification.PROBABLE) {
    			icon = MapIcon.ORANGE_DOT_SMALL;
    		} else if (caze.getCaseClassification() == CaseClassification.SUSPECT) {
    			icon = MapIcon.YELLOW_DOT_SMALL;
    		} else {
    			icon = MapIcon.GREY_DOT_SMALL;
    		}
    		
    		GoogleMapMarker marker = new GoogleMapMarker(caze.toString(), latLon, false, icon.getUrl());
    		marker.setId(caze.getUuid().hashCode());
    		markerCases.put(marker, caze);
    		map.addMarker(marker);
    	}
    }
    
    public void clearContactMarkers() {

    	for (GoogleMapMarker contactMarker : markerContacts.keySet()) {
    		map.removeMarker(contactMarker);
    	}

    	markerContacts.clear();
    	mapContacts.clear();
    }

    public void showContactMarkers(List<ContactMapDto> contacts, boolean showConfirmed, boolean showUnconfirmed) {

    	clearContactMarkers();
    	
    	for (ContactMapDto contact : contacts) {
    		VisitDto visit = null;
    		boolean noContactCoordinates = true;
    		boolean noVisitCoordinates = true;
    		
    		// Don't show a marker for contacts that don't have geo coordinates or geo coordinates for their last visit
    		noContactCoordinates = contact.getReportLat() == null || contact.getReportLon() == null;
    		if (contact.getLastVisit() != null) {
    			visit = FacadeProvider.getVisitFacade().getVisitByUuid(contact.getLastVisit().getUuid());
    			noVisitCoordinates = visit.getReportLat() == null || visit.getReportLon() == null;
    		}
    		if (noContactCoordinates && noVisitCoordinates) {
    			continue;
    		}
    		
    		// Don't show a marker for contacts that are filtered out
    		if (!showUnconfirmed && contact.getContactClassification() == ContactClassification.POSSIBLE) {
    			continue;
    		}
    		if (!showConfirmed && contact.getContactClassification() != ContactClassification.POSSIBLE) {
    			continue;
    		}
    		
    		
    		LatLon latLon;
    		if (!noVisitCoordinates) {
    			latLon = new LatLon(visit.getReportLat(), visit.getReportLon());
    		} else {
    			latLon = new LatLon(contact.getReportLat(), contact.getReportLon());
    		}
    		MapIcon icon;
    		
    		long currentTime = new Date().getTime();
    		if (visit != null) {
    			long visitTime = visit.getVisitDateTime() != null ? visit.getVisitDateTime().getTime() : 0;
	    		// 1000 ms = 1 second; 3600 seconds = 1 hour
	    		if (currentTime - visitTime >= 1000 * 3600 * 48) {				
	        		icon = MapIcon.RED_CONTACT;
	        	} else if (currentTime - visitTime >= 1000 * 3600 * 24) {
	        		icon = MapIcon.ORANGE_CONTACT;
	        	} else {
	        		icon = MapIcon.GREEN_CONTACT;
	        	}
    		} else {
    			icon = MapIcon.RED_CONTACT;
    		}
    		
    		GoogleMapMarker marker = new GoogleMapMarker(contact.toString(), latLon, false, icon.getUrl());
    		marker.setId(contact.getUuid().hashCode());
    		markerContacts.put(marker, contact);
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
    	RED_CONTACT("red-contact"),
    	YELLOW_DOT("yellow-dot"),
    	YELLOW_DOT_SMALL("yellow-dot-small"),
    	YELLOW_DOT_LARGE("yellow-dot-large"),
    	YELLOW_DOT_VERY_LARGE("yellow-dot-very-large"),
    	YELLOW_HOUSE("yellow-dot"),
    	YELLOW_HOUSE_SMALL("yellow-house-small"),
    	YELLOW_HOUSE_LARGE("yellow-house-large"),
    	YELLOW_HOUSE_VERY_LARGE("yellow-house-very-large"),
    	ORANGE_DOT("orange-dot"),
    	ORANGE_DOT_SMALL("orange-dot-small"),
    	ORANGE_DOT_LARGE("orange-dot-large"),
    	ORANGE_DOT_VERY_LARGE("orange-dot-very-large"),
    	ORANGE_HOUSE("orange-house"),
    	ORANGE_HOUSE_SMALL("orange-house-small"),
    	ORANGE_HOUSE_LARGE("orange-house-large"),
    	ORANGE_HOUSE_VERY_LARGE("orange-house-very-large"),
    	ORANGE_CONTACT("orange-contact"),
    	GREY_DOT("grey-dot"),
    	GREY_DOT_SMALL("grey-dot-small"),
    	GREY_DOT_LARGE("grey-dot-large"),
    	GREY_DOT_VERY_LARGE("grey-dot-very-large"),
    	GREY_HOUSE("grey-house"),
    	GREY_HOUSE_SMALL("grey-house-small"),
    	GREY_HOUSE_LARGE("grey-house-large"),
    	GREY_HOUSE_VERY_LARGE("grey-house-very-large"),
    	GREY_CONTACT("grey-contact"),
    	GREEN_CONTACT("green-contact")
    	;
    	
    	private final String imgName;

		private MapIcon(String imgName) {
			this.imgName = imgName;    		
    	}
		
		public String getUrl() {
			return "VAADIN/themes/sormas/mapicons/" + imgName + ".png";
		};
    }
    
    public List<CaseDataDto> getCasesForFacility(FacilityDto facility) {
    	return facilitiesCasesMaps.get(facility);
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
