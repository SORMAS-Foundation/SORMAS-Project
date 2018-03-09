package de.symeda.sormas.ui.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class MapComponent extends VerticalLayout {

	final static Logger logger = LoggerFactory.getLogger(MapComponent.class);

	private static final int MARKER_NORMAL_SIZE = 2;
	private static final int MARKER_LARGE_SIZE = 3;
	private static final int MARKER_VERY_LARGE_SIZE = 4;

	// Layouts and components
	private final DashboardDataProvider dashboardDataProvider;
	private final GoogleMap map;
	private Button casesWithoutGPSButton;
	private Label mapDateLabel;
	private PopupButton mapKeyDropdown;

	// Layers
	private boolean showCases;
	private boolean showContacts;
	private boolean showConfirmedContacts;
	private boolean showUnconfirmedContacts;
	private boolean showEvents;
	private boolean showRegions;

	// Entities
	private final HashMap<FacilityReferenceDto, List<MapCaseDto>> casesByFacility = new HashMap<>();
	private List<MapCaseDto> mapCaseDtos = new ArrayList<>();
	private List<MapCaseDto> mapAndFacilityCases = new ArrayList<>();
	private List<MapContactDto> mapContactDtos = new ArrayList<>();

	// Markers
	private final HashMap<GoogleMapMarker, FacilityDto> markerCaseFacilities = new HashMap<GoogleMapMarker, FacilityDto>();
	private final HashMap<GoogleMapMarker, MapCaseDto> markerCases = new HashMap<GoogleMapMarker, MapCaseDto>();
	private final HashMap<GoogleMapMarker, MapContactDto> markerContacts = new HashMap<GoogleMapMarker, MapContactDto>();
	private final HashMap<GoogleMapMarker, DashboardEventDto> markerEvents = new HashMap<GoogleMapMarker, DashboardEventDto>();
	private final HashMap<RegionReferenceDto, GoogleMapPolygon[]> regionPolygonsMap = new HashMap<RegionReferenceDto, GoogleMapPolygon[]>();
	private final HashMap<DistrictReferenceDto, GoogleMapPolygon[]> districtPolygonsMap = new HashMap<DistrictReferenceDto, GoogleMapPolygon[]>();

	// Others
	private CaseMeasure caseMeasure = CaseMeasure.CASE_COUNT;
	private MapCaseDisplayMode mapCaseDisplayMode = MapCaseDisplayMode.CASES;
	private BigDecimal districtShapesLowerQuartile;
	private BigDecimal districtShapesMedian;
	private BigDecimal districtShapesUpperQuartile;
	private ClickListener externalExpandButtonListener;
	private ClickListener externalCollapseButtonListener;
	private boolean emptyPopulationDistrictPresent;

	public MapComponent(DashboardDataProvider dashboardDataProvider) {    	
		this.dashboardDataProvider = dashboardDataProvider;
		map = new GoogleMap("AIzaSyAaJpN8a_NhEU-02-t5uVi02cAaZtKafkw", null, null);

		if (LoginHelper.isUserInRole(UserRole.NATIONAL_USER)
				|| LoginHelper.isUserInRole(UserRole.NATIONAL_OBSERVER)) {
			showRegions = true;

			map.setZoom(6);
			GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfAllRegions();
			map.setCenter(new LatLon(mapCenter.getLon(), mapCenter.getLat()));
		} else {
			showCases = true;
			showContacts = true;
			showEvents = true;
			showConfirmedContacts = true;
			showUnconfirmedContacts = true;

			UserDto user = LoginHelper.getCurrentUser();
			if (user.getRegion() != null) {
				GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfRegion(user.getRegion());
				map.setCenter(new LatLon(mapCenter.getLon(), mapCenter.getLat()));
			} else {
				GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfAllRegions();
				map.setCenter(new LatLon(mapCenter.getLon(), mapCenter.getLat()));
			}
			map.setZoom(9);
		}

		map.setSizeFull();
		map.setMinZoom(4);
		map.setMaxZoom(16);
		this.setMargin(true);

		// Add components
		addComponent(createHeader());
		addComponent(map);
		addComponent(createFooter());
		setExpandRatio(map, 1);

		map.addMarkerClickListener(new MarkerClickListener() {
			@Override
			public void markerClicked(GoogleMapMarker clickedMarker) {
				FacilityDto facility = markerCaseFacilities.get(clickedMarker);
				MapCaseDto caze = markerCases.get(clickedMarker);
				MapContactDto contact = markerContacts.get(clickedMarker);
				DashboardEventDto event = markerEvents.get(clickedMarker);

				if (facility != null) {
					VerticalLayout layout = new VerticalLayout();
					Window window = VaadinUiUtil.showPopupWindow(layout);
					CasePopupGrid caseGrid = new CasePopupGrid(window, facility, MapComponent.this);
					caseGrid.setHeightMode(HeightMode.ROW);
					layout.addComponent(caseGrid);
					layout.setMargin(true);
					window.setCaption("Cases in " + markerCaseFacilities.get(clickedMarker).toString());
				} else if (caze != null) {
					ControllerProvider.getCaseController().navigateToCase(caze.getUuid());
				} else if (contact != null) {
					ControllerProvider.getContactController().navigateToData(contact.getUuid());
				} else if (event != null) {
					ControllerProvider.getEventController().navigateToData(event.getUuid());
				}
			}
		});
	}

	public void refreshMap() {
		clearRegionShapes();
		clearCaseMarkers();
		clearContactMarkers();
		clearEventMarkers();

		Date fromDate = dashboardDataProvider.getDateFilterOption() == DateFilterOption.DATE ? dashboardDataProvider.getFromDate() :
			DateHelper.getEpiWeekStart(dashboardDataProvider.getFromWeek());
		Date toDate = dashboardDataProvider.getDateFilterOption() == DateFilterOption.DATE ? dashboardDataProvider.getToDate() :
			DateHelper.getEpiWeekEnd(dashboardDataProvider.getToWeek());
		DistrictReferenceDto district = dashboardDataProvider.getDistrict();
		Disease disease = dashboardDataProvider.getDisease();

		if (showRegions) {
			showRegionsShapes(caseMeasure, fromDate, toDate, dashboardDataProvider.getDisease());
		}
		if (showCases) {
			showCaseMarkers(FacadeProvider.getCaseFacade().getCasesForMap(district, disease, fromDate, toDate, LoginHelper.getCurrentUser().getUuid()));
		}
		if (showContacts) {
			if (!showCases) {
				// Case lists need to be filled even when cases are hidden because they are needed to retrieve the contacts
				fillCaseLists(FacadeProvider.getCaseFacade().getCasesForMap(district, disease, fromDate, toDate, LoginHelper.getCurrentUser().getUuid()));
			}
			showContactMarkers(FacadeProvider.getContactFacade()
					.getContactsForMap(district, disease, fromDate, toDate, 
							LoginHelper.getCurrentUser().getUuid(), mapAndFacilityCases));
		}
		if (showEvents) {
			showEventMarkers(dashboardDataProvider.getEvents());
		}
		
		// Re-create the map key layout to only show the keys for the selected layers
		mapKeyDropdown.setContent(createMapKey());
	}

	public List<CaseDataDto> getCasesForFacility(FacilityDto facility) {
		List<CaseDataDto> casesForFacility = new ArrayList<>();
		CaseFacade caseFacade = FacadeProvider.getCaseFacade();
		for (MapCaseDto mapCaseDto : casesByFacility.get(facility)) {
			casesForFacility.add(caseFacade.getCaseDataByUuid(mapCaseDto.getUuid()));
		}
		return casesForFacility;
	}

	public List<CaseDataDto> getCasesWithoutGPSTag() {
		List<CaseDataDto> casesWithoutGPSTag = new ArrayList<>();

		CaseFacade caseFacade = FacadeProvider.getCaseFacade();
		for (MapCaseDto caze : mapCaseDtos) {
			if (caze.getReportLat() == null || caze.getReportLon() == null) {
				casesWithoutGPSTag.add(caseFacade.getCaseDataByUuid(caze.getUuid()));
			}
		}

		return casesWithoutGPSTag;
	}

	public void updateDateLabel() {
		if (dashboardDataProvider.getDateFilterOption() == DateFilterOption.EPI_WEEK) {
			EpiWeek fromWeek = dashboardDataProvider.getFromWeek();
			EpiWeek toWeek = dashboardDataProvider.getToWeek();
			if (fromWeek.getWeek() == toWeek.getWeek()) {
				mapDateLabel.setValue("ACTIVE CASES, CONTACTS AND EVENTS IN EPI WEEK " + fromWeek.getWeek());
			} else {
				mapDateLabel.setValue("ACTIVE CASES, CONTACTS AND EVENTS BETWEEN EPI WEEK " + fromWeek.getWeek() + " AND " + toWeek.getWeek());
			}
		} else {
			Date fromDate = dashboardDataProvider.getFromDate();
			Date toDate = dashboardDataProvider.getToDate();
			if (DateHelper.isSameDay(fromDate, toDate)) {
				mapDateLabel.setValue("ACTIVE CASES, CONTACTS AND EVENTS ON " + DateHelper.formatShortDate(fromDate));
			} else {
				mapDateLabel.setValue("ACTIVE CASES, CONTACTS AND EVENTS BETWEEN " + DateHelper.formatShortDate(fromDate) + 
						" AND " + DateHelper.formatShortDate(toDate));
			}
		}
	}

	public void setExpandListener(ClickListener listener) {
		externalExpandButtonListener = listener;
	}

	public void setCollapseListener(ClickListener listener) {
		externalCollapseButtonListener = listener;
	}

	private boolean hasCasesWithoutGPSTag() {
		for (MapCaseDto caze : mapCaseDtos) {
			if (caze.getReportLat() == null || caze.getReportLon() == null) {
				return true;
			}
		}

		return false;
	}

	private HorizontalLayout createHeader() {
		HorizontalLayout mapHeaderLayout = new HorizontalLayout();
		mapHeaderLayout.setWidth(100, Unit.PERCENTAGE);
		mapHeaderLayout.setSpacing(true);
		CssStyles.style(mapHeaderLayout, CssStyles.VSPACE_2);

		// Map and date labels
		VerticalLayout mapLabelLayout = new VerticalLayout();
		{
			mapLabelLayout.setSizeUndefined();
			Label caseMapLabel = new Label("Case Status Map");
			caseMapLabel.setSizeUndefined();
			CssStyles.style(caseMapLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			mapLabelLayout.addComponent(caseMapLabel);

			mapDateLabel = new Label();
			mapDateLabel.setSizeUndefined();
			CssStyles.style(mapDateLabel, CssStyles.H4, CssStyles.VSPACE_TOP_NONE);
			updateDateLabel();
			mapLabelLayout.addComponent(mapDateLabel);
		}
		mapHeaderLayout.addComponent(mapLabelLayout);
		mapHeaderLayout.setComponentAlignment(mapLabelLayout, Alignment.BOTTOM_LEFT);
		mapHeaderLayout.setExpandRatio(mapLabelLayout, 1);

		// "Cases without GPS Tag" button
		casesWithoutGPSButton = new Button("Cases without GPS tag");
		CssStyles.style(casesWithoutGPSButton, CssStyles.BUTTON_SUBTLE);
		casesWithoutGPSButton.addClickListener(e -> {
			VerticalLayout layout = new VerticalLayout();
			Window window = VaadinUiUtil.showPopupWindow(layout);
			if (!hasCasesWithoutGPSTag()) {
				Label noCasesLabel = new Label("There are no cases without a GPS tag.");
				layout.addComponent(noCasesLabel);
			} else {
				CasePopupGrid caseGrid = new CasePopupGrid(window, null, this);
				caseGrid.setHeightMode(HeightMode.ROW);
				layout.addComponent(caseGrid);
			}
			layout.setMargin(true);
			window.setCaption("Cases Without GPS Tag");
		});
		mapHeaderLayout.addComponent(casesWithoutGPSButton, 1);
		mapHeaderLayout.setComponentAlignment(casesWithoutGPSButton, Alignment.MIDDLE_RIGHT);
		toggleCasesWithoutGPSButtonVisibility();

		// "Expand" and "Collapse" buttons
		Button expandMapButton = new Button("", FontAwesome.EXPAND);
		CssStyles.style(expandMapButton, CssStyles.BUTTON_SUBTLE);
		expandMapButton.addStyleName(CssStyles.VSPACE_NONE);   
		Button collapseMapButton = new Button("", FontAwesome.COMPRESS);
		CssStyles.style(collapseMapButton, CssStyles.BUTTON_SUBTLE);
		collapseMapButton.addStyleName(CssStyles.VSPACE_NONE);

		expandMapButton.addClickListener(e -> {
			externalExpandButtonListener.buttonClick(e);
			mapHeaderLayout.removeComponent(expandMapButton);
			mapHeaderLayout.addComponent(collapseMapButton);
			mapHeaderLayout.setComponentAlignment(collapseMapButton, Alignment.MIDDLE_RIGHT);
		});
		collapseMapButton.addClickListener(e -> {
			externalCollapseButtonListener.buttonClick(e);
			mapHeaderLayout.removeComponent(collapseMapButton);
			mapHeaderLayout.addComponent(expandMapButton);
			mapHeaderLayout.setComponentAlignment(expandMapButton, Alignment.MIDDLE_RIGHT);
		});
		mapHeaderLayout.addComponent(expandMapButton);
		mapHeaderLayout.setComponentAlignment(expandMapButton, Alignment.MIDDLE_RIGHT);

		return mapHeaderLayout;
	}

	private HorizontalLayout createFooter() {
		HorizontalLayout mapFooterLayout = new HorizontalLayout();
		mapFooterLayout.setWidth(100, Unit.PERCENTAGE);
		mapFooterLayout.setSpacing(true);
		CssStyles.style(mapFooterLayout, CssStyles.VSPACE_TOP_2);

		// Map key dropdown button
		mapKeyDropdown = new PopupButton("Show Map Key");
		CssStyles.style(mapKeyDropdown, CssStyles.BUTTON_SUBTLE);
		mapKeyDropdown.setContent(createMapKey());
		mapFooterLayout.addComponent(mapKeyDropdown);
		mapFooterLayout.setComponentAlignment(mapKeyDropdown, Alignment.BOTTOM_LEFT);
		mapFooterLayout.setExpandRatio(mapKeyDropdown, 1);

		// Layers dropdown button
		PopupButton layersDropdown = new PopupButton("Layers");
		{
			CssStyles.style(layersDropdown, CssStyles.BUTTON_SUBTLE);

			VerticalLayout layersLayout = new VerticalLayout();
			layersLayout.setMargin(true);
			layersLayout.setSizeUndefined();
			layersDropdown.setContent(layersLayout);

			// Add check boxes and apply button
			{
				OptionGroup mapCaseDisplayModeSelect = new OptionGroup();
				mapCaseDisplayModeSelect.setWidth(100, Unit.PERCENTAGE);
				mapCaseDisplayModeSelect.addItems((Object[]) MapCaseDisplayMode.values());
				mapCaseDisplayModeSelect.setValue(mapCaseDisplayMode);
				mapCaseDisplayModeSelect.addValueChangeListener(event -> {
					mapCaseDisplayMode = (MapCaseDisplayMode) event.getProperty().getValue();
					applyLayerChanges();
				});

				HorizontalLayout showCasesLayout = new HorizontalLayout();
				{
					CheckBox showCasesCheckBox = new CheckBox();
					CssStyles.style(showCasesCheckBox, CssStyles.VSPACE_NONE);
					showCasesCheckBox.setCaption("Show cases");
					showCasesCheckBox.setValue(showCases);
					showCasesCheckBox.addValueChangeListener(e -> {
						showCases = (boolean) e.getProperty().getValue();
						mapCaseDisplayModeSelect.setEnabled(showCases);
						mapCaseDisplayModeSelect.setValue(mapCaseDisplayMode);
						applyLayerChanges();
					});
					showCasesLayout.addComponent(showCasesCheckBox);

					Label infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
					infoLabel.setDescription("If cases are shown by home address and there are no GPS coordinates available for it, the coordinates of the location where the case has been reported are used instead.");
					CssStyles.style(infoLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_SECONDARY, CssStyles.HSPACE_LEFT_3);
					infoLabel.setHeightUndefined();
					showCasesLayout.addComponent(infoLabel);
					showCasesLayout.setComponentAlignment(infoLabel, Alignment.TOP_CENTER);
				}
				layersLayout.addComponent(showCasesLayout);

				layersLayout.addComponent(mapCaseDisplayModeSelect);
				mapCaseDisplayModeSelect.setEnabled(showCases);

				CheckBox showConfirmedContactsCheckBox = new CheckBox();
				CheckBox showUnconfirmedContactsCheckBox = new CheckBox();

				CheckBox showContactsCheckBox = new CheckBox();
				CssStyles.style(showContactsCheckBox, CssStyles.VSPACE_NONE);
				showContactsCheckBox.setCaption("Show contacts");
				showContactsCheckBox.setValue(showContacts);
				showContactsCheckBox.addValueChangeListener(e -> {
					showContacts = (boolean) e.getProperty().getValue();
					showConfirmedContactsCheckBox.setEnabled(showContacts);
					showConfirmedContactsCheckBox.setValue(true);
					showUnconfirmedContactsCheckBox.setEnabled(showContacts);
					showUnconfirmedContactsCheckBox.setValue(true);
					applyLayerChanges();
				});
				layersLayout.addComponent(showContactsCheckBox);

				CssStyles.style(showConfirmedContactsCheckBox, CssStyles.VSPACE_NONE);
				showConfirmedContactsCheckBox.setCaption("Show confirmed contacts");
				showConfirmedContactsCheckBox.setValue(showConfirmedContacts);
				showConfirmedContactsCheckBox.addValueChangeListener(e -> {
					showConfirmedContacts = (boolean) e.getProperty().getValue();
					applyLayerChanges();
				});
				layersLayout.addComponent(showConfirmedContactsCheckBox);	 

				CssStyles.style(showUnconfirmedContactsCheckBox, CssStyles.VSPACE_NONE);
				showUnconfirmedContactsCheckBox.setCaption("Show unconfirmed contacts");
				showUnconfirmedContactsCheckBox.setValue(showUnconfirmedContacts);
				showUnconfirmedContactsCheckBox.addValueChangeListener(e -> {
					showUnconfirmedContacts = (boolean) e.getProperty().getValue();
					applyLayerChanges();
				});
				layersLayout.addComponent(showUnconfirmedContactsCheckBox);	

				showConfirmedContactsCheckBox.setEnabled(showContacts);
				showUnconfirmedContactsCheckBox.setEnabled(showContacts);

				CheckBox showEventsCheckBox = new CheckBox();
				CssStyles.style(showEventsCheckBox, CssStyles.VSPACE_NONE);
				showEventsCheckBox.setCaption("Show events");
				showEventsCheckBox.setValue(showEvents);
				showEventsCheckBox.addValueChangeListener(e -> {
					showEvents = (boolean) e.getProperty().getValue();
					applyLayerChanges();
				});
				layersLayout.addComponent(showEventsCheckBox);

				if (LoginHelper.isUserInRole(UserRole.NATIONAL_USER)
						|| LoginHelper.isUserInRole(UserRole.NATIONAL_OBSERVER)) {
					OptionGroup regionMapVisualizationSelect = new OptionGroup();
					regionMapVisualizationSelect.setWidth(100, Unit.PERCENTAGE);
					regionMapVisualizationSelect.addItems((Object[]) CaseMeasure.values());
					regionMapVisualizationSelect.setValue(caseMeasure);
					regionMapVisualizationSelect.addValueChangeListener(event -> {
						caseMeasure = (CaseMeasure) event.getProperty().getValue();
						applyLayerChanges();
					});

					CheckBox showRegionsCheckBox = new CheckBox();
					CssStyles.style(showRegionsCheckBox, CssStyles.VSPACE_NONE);
					showRegionsCheckBox.setCaption("Show regions");
					showRegionsCheckBox.setValue(showRegions);
					showRegionsCheckBox.addValueChangeListener(e -> {
						showRegions = (boolean) e.getProperty().getValue();
						regionMapVisualizationSelect.setEnabled(showRegions);
						regionMapVisualizationSelect.setValue(caseMeasure);
						applyLayerChanges();
					});
					layersLayout.addComponent(showRegionsCheckBox);
					layersLayout.addComponent(regionMapVisualizationSelect);
					regionMapVisualizationSelect.setEnabled(showRegions);
				}
			}
		}
		mapFooterLayout.addComponent(layersDropdown);
		mapFooterLayout.setComponentAlignment(layersDropdown, Alignment.BOTTOM_RIGHT);

		return mapFooterLayout;
	}

	private VerticalLayout createMapKey() {
		VerticalLayout mapKeyLayout = new VerticalLayout();
		mapKeyLayout.setMargin(true);
		mapKeyLayout.setSizeUndefined();

		// Disable map key dropdown if no layers have been selected
		if (showCases || showContacts || showRegions) {
			mapKeyDropdown.setEnabled(true);
		} else {
			mapKeyDropdown.setEnabled(false);
			return mapKeyLayout;
		}

		// Health facilities

		// Cases
		if (showCases) {
			if (mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITIES) {
				Label facilitiesKeyLabel = new Label("Health Facilities");
				CssStyles.style(facilitiesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
				mapKeyLayout.addComponent(facilitiesKeyLabel);

				HorizontalLayout facilitiesKeyLayout = new HorizontalLayout();
				{
					facilitiesKeyLayout.setSpacing(false);
					HorizontalLayout legendEntry = createMapKeyEntry("mapicons/grey-house-small.png", "Only Not Yet Classified Cases");
					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
					facilitiesKeyLayout.addComponent(legendEntry);
					legendEntry = createMapKeyEntry("mapicons/yellow-house-small.png", "> 1 Suspect Cases");
					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
					facilitiesKeyLayout.addComponent(legendEntry);
					legendEntry = createMapKeyEntry("mapicons/orange-house-small.png", "> 1 Probable Cases");
					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
					facilitiesKeyLayout.addComponent(legendEntry);
					legendEntry = createMapKeyEntry("mapicons/red-house-small.png", "> 1 Confirmed Cases");
					facilitiesKeyLayout.addComponent(legendEntry);
				}
				mapKeyLayout.addComponent(facilitiesKeyLayout);
			}

			Label casesKeyLabel = new Label("Cases");
			if (mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITIES) {
				CssStyles.style(casesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(casesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			mapKeyLayout.addComponent(casesKeyLabel);

			HorizontalLayout casesKeyLayout = new HorizontalLayout();
			{
				casesKeyLayout.setSpacing(false);
				HorizontalLayout legendEntry = createMapKeyEntry("mapicons/grey-dot-small.png", "Not Yet Classified");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = createMapKeyEntry("mapicons/yellow-dot-small.png", "Suspect");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = createMapKeyEntry("mapicons/orange-dot-small.png", "Probable");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = createMapKeyEntry("mapicons/red-dot-small.png", "Confirmed");
				casesKeyLayout.addComponent(legendEntry);
			}
			mapKeyLayout.addComponent(casesKeyLayout);
		}

		// Contacts
		if (showContacts) {
			Label contactsKeyLabel = new Label("Contacts");
			if (showCases) {
				CssStyles.style(contactsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(contactsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			mapKeyLayout.addComponent(contactsKeyLabel);

			HorizontalLayout contactsKeyLayout = new HorizontalLayout();
			{
				contactsKeyLayout.setSpacing(false);
				HorizontalLayout legendEntry = createMapKeyEntry("mapicons/green-contact.png", "Last Visit < 24h");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				contactsKeyLayout.addComponent(legendEntry);
				legendEntry = createMapKeyEntry("mapicons/orange-contact.png", "Last Visit < 48h");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				contactsKeyLayout.addComponent(legendEntry);
				legendEntry = createMapKeyEntry("mapicons/red-contact.png", "Last Visit > 48h or No Visit");
				contactsKeyLayout.addComponent(legendEntry);
			}
			mapKeyLayout.addComponent(contactsKeyLayout);
		}

		// Events
		if (showEvents) {
			Label eventsKeyLabel = new Label("Events");
			if (showCases || showContacts) {
				CssStyles.style(eventsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(eventsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			mapKeyLayout.addComponent(eventsKeyLabel);

			HorizontalLayout eventsKeyLayout = new HorizontalLayout();
			{
				eventsKeyLayout.setSpacing(false);
				HorizontalLayout legendEntry = createMapKeyEntry("mapicons/outbreak.png", "Outbreak");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				eventsKeyLayout.addComponent(legendEntry);
				legendEntry = createMapKeyEntry("mapicons/rumor.png", "Rumor");
				eventsKeyLayout.addComponent(legendEntry);
			}
			mapKeyLayout.addComponent(eventsKeyLayout);
		}

		// Districts
		if (showRegions && districtShapesLowerQuartile != null && districtShapesMedian != null && districtShapesUpperQuartile != null) {
			Label districtsKeyLabel = new Label("Districts");
			if (showCases || showContacts || showEvents) {
				CssStyles.style(districtsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(districtsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			mapKeyLayout.addComponent(districtsKeyLabel);
			mapKeyLayout.addComponent(createRegionKey());
		}

		return mapKeyLayout;
	}

	private HorizontalLayout createRegionKey() {
		HorizontalLayout regionKeyLayout = new HorizontalLayout();

		HorizontalLayout legendEntry;
		switch (caseMeasure) {
		case CASE_COUNT:
			legendEntry = createMapKeyEntry("mapicons/lowest-region-small.png", districtShapesLowerQuartile.compareTo(BigDecimal.ONE) > 0 ? "1 - " + districtShapesLowerQuartile + " cases" : "1 case");
			break;
		case CASE_INCIDENCE:
			legendEntry = createMapKeyEntry("mapicons/lowest-region-small.png", "<= " + DataHelper.getTruncatedBigDecimal(districtShapesLowerQuartile) + " cases / " + DistrictDto.CASE_INCIDENCE_DIVISOR);
			break;
		default: throw new IllegalArgumentException(caseMeasure.toString());
		}

		regionKeyLayout.addComponent(legendEntry);
		regionKeyLayout.setComponentAlignment(legendEntry, Alignment.MIDDLE_LEFT);
		regionKeyLayout.setExpandRatio(legendEntry, 0);

		Label spacer = new Label();
		spacer.setWidth(6, Unit.PIXELS);
		regionKeyLayout.addComponent(spacer);

		if (districtShapesLowerQuartile.compareTo(districtShapesMedian) < 0) {
			switch (caseMeasure) {
			case CASE_COUNT:
				legendEntry = createMapKeyEntry("mapicons/low-region-small.png", districtShapesMedian.compareTo(districtShapesLowerQuartile.add(BigDecimal.ONE)) > 0 ? districtShapesLowerQuartile.add(BigDecimal.ONE) + " - " + districtShapesMedian + " cases" : districtShapesMedian + " cases");
				break;
			case CASE_INCIDENCE:
				legendEntry = createMapKeyEntry("mapicons/low-region-small.png", DataHelper.getTruncatedBigDecimal(districtShapesLowerQuartile.add(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP)) + " - " + DataHelper.getTruncatedBigDecimal(districtShapesMedian) + " cases / " + DistrictDto.CASE_INCIDENCE_DIVISOR);
				break;
			default: throw new IllegalArgumentException(caseMeasure.toString());
			}
	
			regionKeyLayout.addComponent(legendEntry);
			regionKeyLayout.setComponentAlignment(legendEntry, Alignment.MIDDLE_LEFT);
			regionKeyLayout.setExpandRatio(legendEntry, 0);
	
			spacer = new Label();
			spacer.setWidth(6, Unit.PIXELS);
			regionKeyLayout.addComponent(spacer);
		}

		if (districtShapesMedian.compareTo(districtShapesUpperQuartile) < 0) {
			switch (caseMeasure) {
			case CASE_COUNT:
				legendEntry = createMapKeyEntry("mapicons/high-region-small.png", districtShapesUpperQuartile.compareTo(districtShapesMedian.add(BigDecimal.ONE)) > 0 ? districtShapesMedian.add(BigDecimal.ONE) + " - " + districtShapesUpperQuartile + " cases" : districtShapesUpperQuartile + " cases");
				break;
			case CASE_INCIDENCE:
				legendEntry = createMapKeyEntry("mapicons/high-region-small.png", DataHelper.getTruncatedBigDecimal(districtShapesMedian.add(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP)) + " - " + DataHelper.getTruncatedBigDecimal(districtShapesUpperQuartile) + " cases / " + DistrictDto.CASE_INCIDENCE_DIVISOR);
				break;
			default: throw new IllegalArgumentException(caseMeasure.toString());
			}	
			
			regionKeyLayout.addComponent(legendEntry);
			regionKeyLayout.setComponentAlignment(legendEntry, Alignment.MIDDLE_LEFT);
			regionKeyLayout.setExpandRatio(legendEntry, 0);
	
			spacer = new Label();
			spacer.setWidth(6, Unit.PIXELS);
			regionKeyLayout.addComponent(spacer);
		}
		
		switch (caseMeasure) {
		case CASE_COUNT:
			legendEntry = createMapKeyEntry("mapicons/highest-region-small.png", "> " + districtShapesUpperQuartile + " cases");
			break;
		case CASE_INCIDENCE:
			legendEntry = createMapKeyEntry("mapicons/red-region-small.png", "> " + DataHelper.getTruncatedBigDecimal(districtShapesUpperQuartile) + " cases / " + DistrictDto.CASE_INCIDENCE_DIVISOR);
			break;
		default: throw new IllegalArgumentException(caseMeasure.toString());
		}
		
		regionKeyLayout.addComponent(legendEntry);
		regionKeyLayout.setComponentAlignment(legendEntry, Alignment.MIDDLE_LEFT);
		regionKeyLayout.setExpandRatio(legendEntry, 1);

		if (caseMeasure == CaseMeasure.CASE_INCIDENCE && emptyPopulationDistrictPresent) {
			spacer = new Label();
			spacer.setWidth(6, Unit.PIXELS);
			regionKeyLayout.addComponent(spacer);
			
			legendEntry = createMapKeyEntry("mapicons/no-population-region-small.png", "No population data available");
			
			regionKeyLayout.addComponent(legendEntry);
			regionKeyLayout.setComponentAlignment(legendEntry, Alignment.MIDDLE_LEFT);
			regionKeyLayout.setExpandRatio(legendEntry, 1);
		}
		
		return regionKeyLayout;
	}

	private HorizontalLayout createMapKeyEntry(String iconThemeResource, String labelCaption) {
		HorizontalLayout entry = new HorizontalLayout();
		entry.setSizeUndefined();
		Image icon = new Image(null, new ThemeResource(iconThemeResource));
		CssStyles.style(icon, CssStyles.HSPACE_RIGHT_4);
		icon.setWidth(12.375f, Unit.PIXELS);
		icon.setHeight(16.875f, Unit.PIXELS);
		entry.addComponent(icon);
		Label label = new Label(labelCaption);
		label.setSizeUndefined();
		label.addStyleName(ValoTheme.LABEL_SMALL);
		entry.addComponent(label);
		return entry;
	}

	private void applyLayerChanges() {
		// Refresh the map according to the selected layers
		refreshMap();
		// Show or hide the button to show cases without a GPS tag depending on whether the cases layer has been selected
		toggleCasesWithoutGPSButtonVisibility();
	}

	private void toggleCasesWithoutGPSButtonVisibility() {
		casesWithoutGPSButton.setVisible(showCases);
	}

	private void clearRegionShapes() {

		for (GoogleMapPolygon[] regionPolygons : regionPolygonsMap.values()) {
			for (GoogleMapPolygon regionPolygon : regionPolygons) {
				map.removePolygonOverlay(regionPolygon);
			}
		}
		regionPolygonsMap.clear();

		for (GoogleMapPolygon[] districtPolygons : districtPolygonsMap.values()) {
			for (GoogleMapPolygon districtPolygon : districtPolygons) {
				map.removePolygonOverlay(districtPolygon);
			}
		}
		districtPolygonsMap.clear();
		
		emptyPopulationDistrictPresent = false;

		map.removeStyleName("no-tiles");
	}

	private void showRegionsShapes(CaseMeasure caseMeasure, Date fromDate, Date toDate, Disease disease) {

		clearRegionShapes();

		map.addStyleName("no-tiles");

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllAsReference();

		// draw outlines of all regions
		for (RegionReferenceDto region : regions) {

			GeoLatLon[][] regionShape = FacadeProvider.getGeoShapeProvider().getRegionShape(region);
			if (regionShape == null) {
				continue;
			}

			GoogleMapPolygon[] regionPolygons = new GoogleMapPolygon[regionShape.length];
			for (int part = 0; part<regionShape.length; part++) {
				GeoLatLon[] regionShapePart = regionShape[part];
				GoogleMapPolygon polygon = new GoogleMapPolygon(
						Arrays.stream(regionShapePart)
						.map(c -> new LatLon(c.getLat(), c.getLon()))
						.collect(Collectors.toList()));

				polygon.setStrokeOpacity(0.5);
				polygon.setFillOpacity(0);
				regionPolygons[part] = polygon;
				map.addPolygonOverlay(polygon);
			}
			regionPolygonsMap.put(region, regionPolygons);
		}

		List<Pair<DistrictDto, BigDecimal>> measurePerDistrict = FacadeProvider.getCaseFacade().getCaseMeasurePerDistrict(fromDate, toDate, disease, caseMeasure);
		if (caseMeasure == CaseMeasure.CASE_COUNT) {
			districtShapesLowerQuartile = measurePerDistrict.size() > 0 ? 
					measurePerDistrict.get((int) (measurePerDistrict.size()  * 0.25)).getElement1() : null;
			districtShapesMedian = measurePerDistrict.size() > 0 ? 
					measurePerDistrict.get((int) (measurePerDistrict.size() * 0.5)).getElement1() : null;
			districtShapesUpperQuartile = measurePerDistrict.size() > 0 ? 
					measurePerDistrict.get((int) (measurePerDistrict.size() * 0.75)).getElement1() : null;
		} else {
			// For case incidence, districts without or with a population <= 0 should not be used for the calculation of the quartiles because they will falsify the result
			List<Pair<DistrictDto, BigDecimal>> measurePerDistrictWithoutMissingPopulations = new ArrayList<>();
			measurePerDistrictWithoutMissingPopulations.addAll(measurePerDistrict);
			measurePerDistrictWithoutMissingPopulations.removeIf(d -> d.getElement0().getPopulation() == null || d.getElement0().getPopulation() <= 0);
			districtShapesLowerQuartile = measurePerDistrictWithoutMissingPopulations.size() > 0 ? 
					measurePerDistrictWithoutMissingPopulations.get((int) (measurePerDistrictWithoutMissingPopulations.size()  * 0.25)).getElement1() : null;
			districtShapesMedian = measurePerDistrictWithoutMissingPopulations.size() > 0 ? 
					measurePerDistrictWithoutMissingPopulations.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.5)).getElement1() : null;
			districtShapesUpperQuartile = measurePerDistrictWithoutMissingPopulations.size() > 0 ? 
					measurePerDistrictWithoutMissingPopulations.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.75)).getElement1() : null;
		}

		// Draw relevant district fills
		for (Pair<DistrictDto, BigDecimal> districtMeasure : measurePerDistrict) {

			DistrictDto district = districtMeasure.getElement0();
			DistrictReferenceDto districtRef = district.toReference();
			BigDecimal districtValue = districtMeasure.getElement1();
			GeoLatLon[][] districtShape = FacadeProvider.getGeoShapeProvider().getDistrictShape(districtRef);
			if (districtShape == null) {
				continue;
			}

			GoogleMapPolygon[] districtPolygons = new GoogleMapPolygon[districtShape.length];
			for (int part = 0; part < districtShape.length; part++) {
				GeoLatLon[] districtShapePart = districtShape[part];
				GoogleMapPolygon polygon = new GoogleMapPolygon(
						Arrays.stream(districtShapePart)
						.map(c -> new LatLon(c.getLat(), c.getLon()))
						.collect(Collectors.toList()));

				polygon.setStrokeOpacity(0);
				
				if (districtValue.compareTo(BigDecimal.ZERO) == 0) {
					polygon.setFillOpacity(0);
				} else if (districtValue.compareTo(districtShapesLowerQuartile) <= 0) {
					polygon.setFillColor("#FEDD6C");
					polygon.setFillOpacity(0.5);
				} else if (districtValue.compareTo(districtShapesMedian) <= 0) {
					polygon.setFillColor("#FDBF44");
					polygon.setFillOpacity(0.5);
				} else if (districtValue.compareTo(districtShapesUpperQuartile) <= 0) {
					polygon.setFillColor("#F47B20");
					polygon.setFillOpacity(0.5);							
				} else {
					polygon.setFillColor("#ED1B24");
					polygon.setFillOpacity(0.5);
				}
				
				if (caseMeasure == CaseMeasure.CASE_INCIDENCE) {
					if (district.getPopulation() == null || district.getPopulation() <= 0) {
						// grey when region has no population data
						emptyPopulationDistrictPresent = true;
						polygon.setFillColor("#999999");
						polygon.setFillOpacity(0.5);
					}
				}
				
				districtPolygons[part] = polygon;
				map.addPolygonOverlay(polygon);
			}
			districtPolygonsMap.put(districtRef, districtPolygons);
		}
	}

	private void clearCaseMarkers() {
		for (GoogleMapMarker facilityMarker : markerCaseFacilities.keySet()) {
			map.removeMarker(facilityMarker);
		}
		for (GoogleMapMarker caseMarker : markerCases.keySet()) {
			map.removeMarker(caseMarker);
		}

		markerCaseFacilities.clear();
		markerCases.clear();
		casesByFacility.clear();
		mapCaseDtos.clear();
		mapAndFacilityCases.clear();
	}

	private void showCaseMarkers(List<MapCaseDto> cases) {

		clearCaseMarkers();

		fillCaseLists(cases);

		for (FacilityReferenceDto facilityReference : casesByFacility.keySet()) {
			FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(facilityReference.getUuid());

			if (facility.getLatitude() == null || facility.getLongitude() == null) {
				continue;
			}

			LatLon latLon = new LatLon(facility.getLatitude(), facility.getLongitude());
			MapIcon icon;

			// colorize the icon by the "strongest" classification type (order as in enum) and set its size depending
			// on the number of cases
			int numberOfCases = casesByFacility.get(facility).size();
			Set<CaseClassification> classificationSet = new HashSet<>();
			for (MapCaseDto caze : casesByFacility.get(facility)) {
				classificationSet.add(caze.getCaseClassification());
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
			GoogleMapMarker marker = new GoogleMapMarker(facility.toString() + " (" + casesByFacility.get(facility).size() + " case(s))", latLon, false, icon.getUrl());
			marker.setId(facility.getUuid().hashCode());
			markerCaseFacilities.put(marker, facility);
			map.addMarker(marker);
		}

		for (MapCaseDto caze : mapCaseDtos) {
			if (caze.getAddressLat() == null || caze.getAddressLon() == null) {
				if (caze.getReportLat() == null || caze.getReportLon() == null) {
					continue;
				}
			}

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

			LatLon latLon;
			if (caze.getAddressLat() != null && caze.getAddressLon() != null) {
				latLon = new LatLon(caze.getAddressLat(), caze.getAddressLon());
			} else {
				latLon = new LatLon(caze.getReportLat(), caze.getReportLon());
			}
			GoogleMapMarker marker = new GoogleMapMarker(caze.toString(), latLon, false, icon.getUrl());
			marker.setId(caze.getUuid().hashCode());
			markerCases.put(marker, caze);
			map.addMarker(marker);
		}
	}

	private void fillCaseLists(List<MapCaseDto> cases) {
		for (MapCaseDto caze : cases) {
			CaseClassification classification = caze.getCaseClassification();
			if (classification == null || classification == CaseClassification.NO_CASE)
				continue;

			if (mapCaseDisplayMode == MapCaseDisplayMode.CASES) {
				mapCaseDtos.add(caze);
			} else {
				if (caze.getHealthFacilityUuid().equals(FacilityDto.NONE_FACILITY_UUID) ||
						caze.getHealthFacilityUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
					mapCaseDtos.add(caze);
				} else {
					FacilityReferenceDto facility = new FacilityReferenceDto();
					facility.setUuid(caze.getHealthFacilityUuid());
					if (casesByFacility.get(facility) == null) {
						casesByFacility.put(facility, new ArrayList<MapCaseDto>());
					}
					casesByFacility.get(facility).add(caze);
				}
			}
			mapAndFacilityCases.add(caze);
		}
	}

	private void clearContactMarkers() {
		for (GoogleMapMarker contactMarker : markerContacts.keySet()) {
			map.removeMarker(contactMarker);
		}

		markerContacts.clear();
		mapContactDtos.clear();
	}

	private void showContactMarkers(List<MapContactDto> contacts) {

		clearContactMarkers();

		for (MapContactDto contact : contacts) {

			// Don't show a marker for contacts that don't have geo coordinates
			if (contact.getAddressLat() == null || contact.getAddressLon() == null) {
				if (contact.getReportLat() == null || contact.getReportLon() == null) {
					continue;
				}
			}

			// Don't show a marker for contacts that are filtered out
			if (!showUnconfirmedContacts && contact.getContactClassification() == ContactClassification.UNCONFIRMED) {
				continue;
			}
			if (!showConfirmedContacts && contact.getContactClassification() != ContactClassification.UNCONFIRMED) {
				continue;
			}

			MapIcon icon;
			Date lastVisitDateTime = contact.getLastVisitDateTime();
			long currentTime = new Date().getTime();
			if (lastVisitDateTime != null) {
				// 1000 ms = 1 second; 3600 seconds = 1 hour
				if (currentTime - lastVisitDateTime.getTime() >= 1000 * 3600 * 48) {				
					icon = MapIcon.RED_CONTACT;
				} else if (currentTime - lastVisitDateTime.getTime() >= 1000 * 3600 * 24) {
					icon = MapIcon.ORANGE_CONTACT;
				} else {
					icon = MapIcon.GREEN_CONTACT;
				}
			} else {
				icon = MapIcon.RED_CONTACT;
			}

			LatLon latLon;
			if (contact.getAddressLat() != null && contact.getAddressLon() != null) {
				latLon = new LatLon(contact.getAddressLat(), contact.getAddressLon());
			} else {
				latLon = new LatLon(contact.getReportLat(), contact.getReportLon());
			}
			GoogleMapMarker marker = new GoogleMapMarker(contact.toString(), latLon, false, icon.getUrl());
			marker.setId(contact.getUuid().hashCode());
			markerContacts.put(marker, contact);
			map.addMarker(marker);
		}
	}

	private void clearEventMarkers() {
		for (GoogleMapMarker eventMarker : markerEvents.keySet()) {
			map.removeMarker(eventMarker);
		}

		markerEvents.clear();
	}

	private void showEventMarkers(List<DashboardEventDto> events) {

		clearEventMarkers();

		for (DashboardEventDto event : events) {
			MapIcon icon;
			if (event.getEventType() == EventType.OUTBREAK) {
				icon = MapIcon.OUTBREAK;
			} else {
				icon = MapIcon.RUMOR;
			}
			
			LatLon latLon = null;
			if (event.getReportLat() != null && event.getReportLon() != null) {
				latLon = new LatLon(event.getReportLat(), event.getReportLon());
			} else if (event.getDistrict() != null) {
				GeoLatLon districtCenter = FacadeProvider.getGeoShapeProvider().getCenterOfDistrict(event.getDistrict());
				latLon = new LatLon(districtCenter.getLon(), districtCenter.getLat());
			}
			
			if (latLon != null) {
				GoogleMapMarker marker = new GoogleMapMarker(event.toString(), latLon, false, icon.getUrl());
				marker.setId(event.getUuid().hashCode());
				markerEvents.put(marker, event);
				map.addMarker(marker);
			}
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
		GREEN_CONTACT("green-contact"),
		OUTBREAK("outbreak"),
		RUMOR("rumor")
		;

		private final String imgName;

		private MapIcon(String imgName) {
			this.imgName = imgName;    		
		}

		public String getUrl() {
			return "VAADIN/themes/sormas/mapicons/" + imgName + ".png";
		};
	}

	public Label getMapDateLabel() {
		return mapDateLabel;
	}

}
