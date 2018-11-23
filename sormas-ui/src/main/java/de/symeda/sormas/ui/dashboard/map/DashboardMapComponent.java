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
package de.symeda.sormas.ui.dashboard.map;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractOrderedLayout;
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
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.map.LeafletMap;
import de.symeda.sormas.ui.map.LeafletMap.MarkerClickEvent;
import de.symeda.sormas.ui.map.LeafletMap.MarkerClickListener;
import de.symeda.sormas.ui.map.LeafletMarker;
import de.symeda.sormas.ui.map.LeafletPolygon;
import de.symeda.sormas.ui.map.MarkerIcon;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class DashboardMapComponent extends VerticalLayout {

	final static Logger logger = LoggerFactory.getLogger(DashboardMapComponent.class);

//	private static final String CASE_FACILITIES_GROUP_ID = "facilities";
	private static final String CASES_GROUP_ID = "cases";
	private static final String CONTACTS_GROUP_ID = "contacts";
	private static final String EVENTS_GROUP_ID = "events";
	private static final String REGIONS_GROUP_ID = "regions";
	private static final String DISTRICTS_GROUP_ID = "districts";

	// Layouts and components
	private final DashboardDataProvider dashboardDataProvider;
	private final LeafletMap map;
	private PopupButton legendDropdown;

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
	private final List<FacilityReferenceDto> markerCaseFacilities = new ArrayList<FacilityReferenceDto>();
	private final List<MapContactDto> markerContacts = new ArrayList<MapContactDto>();
	private final List<DashboardEventDto> markerEvents = new ArrayList<DashboardEventDto>();
	private final List<RegionReferenceDto> polygonRegions = new ArrayList<RegionReferenceDto>();
	private final List<DistrictReferenceDto> polygonDistricts = new ArrayList<DistrictReferenceDto>();

	// Others
	private CaseMeasure caseMeasure = CaseMeasure.CASE_COUNT;
	private MapCaseDisplayMode mapCaseDisplayMode = MapCaseDisplayMode.CASES;
	private BigDecimal districtValuesLowerQuartile;
	private BigDecimal districtValuesMedian;
	private BigDecimal districtValuesUpperQuartile;
	private ClickListener externalExpandButtonListener;
	private ClickListener externalCollapseButtonListener;
	private boolean emptyPopulationDistrictPresent;

	public DashboardMapComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		map = new LeafletMap();
		map.setSizeFull();
		map.addMarkerClickListener(new MarkerClickListener() {
			@Override
			public void markerClick(MarkerClickEvent event) {
				onMarkerClicked(event.getGroupId(), event.getMarkerIndex());
			}
		});

		if (LoginHelper.isUserInRole(UserRole.NATIONAL_USER) || LoginHelper.isUserInRole(UserRole.NATIONAL_OBSERVER)) {
			showRegions = true;

			map.setZoom(6);
			GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfAllRegions();
			map.setCenter(mapCenter.getLon(), mapCenter.getLat());
		} else {
			if (dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE) {
				showCases = true;
				showContacts = true;
				showEvents = true;
				showConfirmedContacts = true;
				showUnconfirmedContacts = true;
			} else if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
				showCases = false;
				showContacts = true;
				showEvents = false;
				showConfirmedContacts = true;
				showUnconfirmedContacts = true;
			}

			UserDto user = LoginHelper.getCurrentUser();
			if (user.getRegion() != null) {
				GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfRegion(user.getRegion());
				map.setCenter(mapCenter.getLon(), mapCenter.getLat());
			} else {
				GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfAllRegions();
				map.setCenter(mapCenter.getLon(), mapCenter.getLat());
			}
			map.setZoom(6);
		}

		this.setMargin(true);

		// Add components
		addComponent(createHeader());
		addComponent(map);
		setExpandRatio(map, 1);
	}

	public void refreshMap() {
		clearRegionShapes();
		clearCaseMarkers();
		clearContactMarkers();
		clearEventMarkers();

		Date fromDate = dashboardDataProvider.getFromDate();
		Date toDate = dashboardDataProvider.getToDate();
		RegionReferenceDto region = dashboardDataProvider.getRegion();
		DistrictReferenceDto district = dashboardDataProvider.getDistrict();
		Disease disease = dashboardDataProvider.getDisease();

		if (showRegions) {
			showRegionsShapes(caseMeasure, fromDate, toDate, dashboardDataProvider.getDisease());
		}
		if (showCases) {
			showCaseMarkers(FacadeProvider.getCaseFacade().getCasesForMap(region, district, disease, fromDate, toDate,
					LoginHelper.getCurrentUser().getUuid()));
		}
		if (showContacts) {
			if (!showCases) {
				// Case lists need to be filled even when cases are hidden because they are
				// needed to retrieve the contacts
				fillCaseLists(FacadeProvider.getCaseFacade().getCasesForMap(region, district, disease, fromDate, toDate,
						LoginHelper.getCurrentUser().getUuid()));
			}
			showContactMarkers(FacadeProvider.getContactFacade().getContactsForMap(region, district, disease, fromDate,
					toDate, LoginHelper.getCurrentUser().getUuid(), mapAndFacilityCases));
		}
		if (showEvents) {
			showEventMarkers(dashboardDataProvider.getEvents());
		}

		// Re-create the map key layout to only show the keys for the selected layers
		legendDropdown.setContent(createLegend());
	}

	public List<CaseDataDto> getCasesForFacility(FacilityReferenceDto facility) {
		List<CaseDataDto> casesForFacility = new ArrayList<>();
		CaseFacade caseFacade = FacadeProvider.getCaseFacade();
		for (MapCaseDto mapCaseDto : casesByFacility.get(facility)) {
			casesForFacility.add(caseFacade.getCaseDataByUuid(mapCaseDto.getUuid()));
		}
		return casesForFacility;
	}

	public void setExpandListener(ClickListener listener) {
		externalExpandButtonListener = listener;
	}

	public void setCollapseListener(ClickListener listener) {
		externalCollapseButtonListener = listener;
	}

	private HorizontalLayout createHeader() {
		HorizontalLayout mapHeaderLayout = new HorizontalLayout();
		mapHeaderLayout.setWidth(100, Unit.PERCENTAGE);
		mapHeaderLayout.setSpacing(true);
		CssStyles.style(mapHeaderLayout, CssStyles.VSPACE_4);

		Label mapLabel = new Label();
		if (dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE) {
			mapLabel.setValue("Case Status Map");
		} else {
			mapLabel.setValue("Contact Map");
		}
		mapLabel.setSizeUndefined();
		CssStyles.style(mapLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

		mapHeaderLayout.addComponent(mapLabel);
		mapHeaderLayout.setComponentAlignment(mapLabel, Alignment.BOTTOM_LEFT);
		mapHeaderLayout.setExpandRatio(mapLabel, 1);

//		Button testButton = new NativeButton("Test");
//		testButton.addClickListener(new ClickListener() {
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//
//				// add some random markers
//				Random random = new Random();
//				GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfAllRegions();
//				List<LeafletMarker> markers = new ArrayList<LeafletMarker>();
//				for (int i = 0; i < 2000; i++) {
//					LeafletMarker marker = new LeafletMarker();
//					marker.setLatLon(mapCenter.getLat() + 0.2 * random.nextDouble() - 0.1,
//							mapCenter.getLon() + 0.2 * random.nextDouble() - 0.1);
//					marker.setIcon(MarkerIcon.RED_DOT);
//					markers.add(marker);
//				}
//				map.addMarkerGroup("test", markers);
//			}
//		});
//		mapHeaderLayout.addComponent(testButton);

		// Map key dropdown button
		legendDropdown = new PopupButton("Map Key");
		CssStyles.style(legendDropdown, CssStyles.BUTTON_SUBTLE);
		legendDropdown.setContent(createLegend());
		mapHeaderLayout.addComponent(legendDropdown);
		mapHeaderLayout.setComponentAlignment(legendDropdown, Alignment.MIDDLE_RIGHT);
		mapHeaderLayout.setExpandRatio(legendDropdown, 1);

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
					refreshMap();
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
						refreshMap();
					});
					showCasesLayout.addComponent(showCasesCheckBox);

					Label infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
					infoLabel.setDescription(
							"If cases are shown by home address and there are no GPS coordinates available for it, the coordinates of the location where the case has been reported are used instead.");
					CssStyles.style(infoLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_SECONDARY,
							CssStyles.HSPACE_LEFT_3);
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
					refreshMap();
				});
				layersLayout.addComponent(showContactsCheckBox);

				CssStyles.style(showConfirmedContactsCheckBox, CssStyles.VSPACE_NONE);
				showConfirmedContactsCheckBox.setCaption("Show confirmed contacts");
				showConfirmedContactsCheckBox.setValue(showConfirmedContacts);
				showConfirmedContactsCheckBox.addValueChangeListener(e -> {
					showConfirmedContacts = (boolean) e.getProperty().getValue();
					refreshMap();
				});
				layersLayout.addComponent(showConfirmedContactsCheckBox);

				CssStyles.style(showUnconfirmedContactsCheckBox, CssStyles.VSPACE_NONE);
				showUnconfirmedContactsCheckBox.setCaption("Show unconfirmed contacts");
				showUnconfirmedContactsCheckBox.setValue(showUnconfirmedContacts);
				showUnconfirmedContactsCheckBox.addValueChangeListener(e -> {
					showUnconfirmedContacts = (boolean) e.getProperty().getValue();
					refreshMap();
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
					refreshMap();
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
						refreshMap();
					});

					HorizontalLayout showRegionsLayout = new HorizontalLayout();
					{
						CheckBox showRegionsCheckBox = new CheckBox();
						CssStyles.style(showRegionsCheckBox, CssStyles.VSPACE_NONE);
						showRegionsCheckBox.setCaption("Show regions");
						showRegionsCheckBox.setValue(showRegions);
						showRegionsCheckBox.addValueChangeListener(e -> {
							showRegions = (boolean) e.getProperty().getValue();
							regionMapVisualizationSelect.setEnabled(showRegions);
							regionMapVisualizationSelect.setValue(caseMeasure);
							refreshMap();
						});
						showRegionsLayout.addComponent(showRegionsCheckBox);

						Label infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
						infoLabel.setDescription(
								"\"Case incidence ratio\" means the number of cases per 100,000 inhabitants. You can check the map key to see the thresholds that define "
										+ "how the districts are colorized.");
						CssStyles.style(infoLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_SECONDARY,
								CssStyles.HSPACE_LEFT_3);
						infoLabel.setHeightUndefined();
						showRegionsLayout.addComponent(infoLabel);
						showRegionsLayout.setComponentAlignment(infoLabel, Alignment.TOP_CENTER);
					}
					layersLayout.addComponent(showRegionsLayout);
					layersLayout.addComponent(regionMapVisualizationSelect);
					regionMapVisualizationSelect.setEnabled(showRegions);
				}
			}
		}
		mapHeaderLayout.addComponent(layersDropdown);
		mapHeaderLayout.setComponentAlignment(layersDropdown, Alignment.MIDDLE_RIGHT);

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

	private VerticalLayout createLegend() {
		VerticalLayout legendLayout = new VerticalLayout();
		legendLayout.setMargin(true);
		legendLayout.setSizeUndefined();

		// Disable map key dropdown if no layers have been selected
		if (showCases || showContacts || showRegions) {
			legendDropdown.setEnabled(true);
		} else {
			legendDropdown.setEnabled(false);
			return legendLayout;
		}

		// Health facilities

		// Cases
		if (showCases) {
			if (mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITIES
					|| mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITIES_OR_ADDRESS) {
				Label facilitiesKeyLabel = new Label("Health Facilities");
				CssStyles.style(facilitiesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
				legendLayout.addComponent(facilitiesKeyLabel);

				HorizontalLayout facilitiesKeyLayout = new HorizontalLayout();
				{
//					facilitiesKeyLayout.setSpacing(false);
//					HorizontalLayout legendEntry = buildMarkerLegendEntry(MarkerIcon.GREY_HOUSE_SMALL,
//							"Only Not Yet Classified Cases");
//					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
//					facilitiesKeyLayout.addComponent(legendEntry);
//					legendEntry = buildMarkerLegendEntry(MarkerIcon.YELLOW_HOUSE_SMALL, "> 1 Suspect Cases");
//					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
//					facilitiesKeyLayout.addComponent(legendEntry);
//					legendEntry = buildMarkerLegendEntry(MarkerIcon.ORANGE_HOUSE_SMALL, "> 1 Probable Cases");
//					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
//					facilitiesKeyLayout.addComponent(legendEntry);
//					legendEntry = buildMarkerLegendEntry(MarkerIcon.RED_HOUSE_SMALL, "> 1 Confirmed Cases");
//					facilitiesKeyLayout.addComponent(legendEntry);
				}
				legendLayout.addComponent(facilitiesKeyLayout);
			}

			Label casesKeyLabel = new Label("Cases");
			if (mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITIES
					|| mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITIES_OR_ADDRESS) {
				CssStyles.style(casesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(casesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendLayout.addComponent(casesKeyLabel);

			HorizontalLayout casesKeyLayout = new HorizontalLayout();
			{
//				casesKeyLayout.setSpacing(false);
//				HorizontalLayout legendEntry = buildMarkerLegendEntry(MarkerIcon.GREY_DOT_SMALL, "Not Yet Classified");
//				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
//				casesKeyLayout.addComponent(legendEntry);
//				legendEntry = buildMarkerLegendEntry(MarkerIcon.YELLOW_DOT_SMALL, "Suspect");
//				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
//				casesKeyLayout.addComponent(legendEntry);
//				legendEntry = buildMarkerLegendEntry(MarkerIcon.ORANGE_DOT_SMALL, "Probable");
//				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
//				casesKeyLayout.addComponent(legendEntry);
//				legendEntry = buildMarkerLegendEntry(MarkerIcon.RED_DOT_SMALL, "Confirmed");
//				casesKeyLayout.addComponent(legendEntry);
			}
			legendLayout.addComponent(casesKeyLayout);
		}

		// Contacts
		if (showContacts) {
			Label contactsKeyLabel = new Label("Contacts");
			if (showCases) {
				CssStyles.style(contactsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(contactsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendLayout.addComponent(contactsKeyLabel);

			HorizontalLayout contactsKeyLayout = new HorizontalLayout();
			{
//				contactsKeyLayout.setSpacing(false);
//				HorizontalLayout legendEntry = buildMarkerLegendEntry(MarkerIcon.GREEN_CONTACT, "Last Visit < 24h");
//				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
//				contactsKeyLayout.addComponent(legendEntry);
//				legendEntry = buildMarkerLegendEntry(MarkerIcon.ORANGE_CONTACT, "Last Visit < 48h");
//				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
//				contactsKeyLayout.addComponent(legendEntry);
//				legendEntry = buildMarkerLegendEntry(MarkerIcon.RED_CONTACT, "Last Visit > 48h or No Visit");
//				contactsKeyLayout.addComponent(legendEntry);
			}
			legendLayout.addComponent(contactsKeyLayout);
		}

		// Events
		if (showEvents) {
			Label eventsKeyLabel = new Label("Events");
			if (showCases || showContacts) {
				CssStyles.style(eventsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(eventsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendLayout.addComponent(eventsKeyLabel);

			HorizontalLayout eventsKeyLayout = new HorizontalLayout();
			{
//				eventsKeyLayout.setSpacing(false);
//				HorizontalLayout legendEntry = buildMarkerLegendEntry(MarkerIcon.OUTBREAK, "Outbreak");
//				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
//				eventsKeyLayout.addComponent(legendEntry);
//				legendEntry = buildMarkerLegendEntry(MarkerIcon.RUMOR, "Rumor");
//				eventsKeyLayout.addComponent(legendEntry);
			}
			legendLayout.addComponent(eventsKeyLayout);
		}

		// Districts
		if (showRegions && districtValuesLowerQuartile != null && districtValuesMedian != null
				&& districtValuesUpperQuartile != null) {
			Label districtsKeyLabel = new Label("Districts");
			if (showCases || showContacts || showEvents) {
				CssStyles.style(districtsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(districtsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendLayout.addComponent(districtsKeyLabel);
			legendLayout.addComponent(buildRegionLegend(false, caseMeasure, emptyPopulationDistrictPresent,
					districtValuesLowerQuartile, districtValuesMedian, districtValuesUpperQuartile));
		}

		return legendLayout;
	}

//	public static HorizontalLayout buildMarkerLegendEntry(MarkerIcon icon, String labelCaption) {
//		return buildLegendEntry(icon.getExternalUrl(), labelCaption);
//	}

	public static HorizontalLayout buildMapIconLegendEntry(String iconName, String labelCaption) {
		return buildLegendEntry("VAADIN/map/icons/" + iconName + ".png", labelCaption);
	}

	private static HorizontalLayout buildLegendEntry(String externalUrl, String labelCaption) {
		HorizontalLayout entry = new HorizontalLayout();
		entry.setSizeUndefined();
		Image iconImage = new Image(null, new ExternalResource(externalUrl));
		CssStyles.style(iconImage, CssStyles.HSPACE_RIGHT_4);
		iconImage.setWidth(12.375f, Unit.PIXELS);
		iconImage.setHeight(16.875f, Unit.PIXELS);
		entry.addComponent(iconImage);
		Label label = new Label(labelCaption);
		label.setSizeUndefined();
		label.addStyleName(ValoTheme.LABEL_SMALL);
		entry.addComponent(label);
		return entry;
	}

	public static AbstractOrderedLayout buildRegionLegend(boolean vertical, CaseMeasure caseMeasure,
			boolean emptyPopulationDistrictPresent, BigDecimal districtShapesLowerQuartile,
			BigDecimal districtShapesMedian, BigDecimal districtShapesUpperQuartile) {
		AbstractOrderedLayout regionLegendLayout = vertical ? new VerticalLayout() : new HorizontalLayout();
		regionLegendLayout.setSpacing(true);
		CssStyles.style(regionLegendLayout, CssStyles.LAYOUT_MINIMAL);
		regionLegendLayout.setSizeUndefined();

		HorizontalLayout legendEntry;
		switch (caseMeasure) {
		case CASE_COUNT:
			legendEntry = buildMapIconLegendEntry("lowest-region-small",
					districtShapesLowerQuartile.compareTo(BigDecimal.ONE) > 0
							? "1 - " + districtShapesLowerQuartile + " cases"
							: "1 case");
			break;
		case CASE_INCIDENCE:
			legendEntry = buildMapIconLegendEntry("lowest-region-small",
					"<= " + DataHelper.getTruncatedBigDecimal(districtShapesLowerQuartile) + " cases / "
							+ DistrictDto.CASE_INCIDENCE_DIVISOR);
			break;
		default:
			throw new IllegalArgumentException(caseMeasure.toString());
		}
		regionLegendLayout.addComponent(legendEntry);

		if (districtShapesLowerQuartile.compareTo(districtShapesMedian) < 0) {
			switch (caseMeasure) {
			case CASE_COUNT:
				legendEntry = buildMapIconLegendEntry("low-region-small",
						districtShapesMedian.compareTo(districtShapesLowerQuartile.add(BigDecimal.ONE)) > 0
								? districtShapesLowerQuartile.add(BigDecimal.ONE) + " - " + districtShapesMedian
										+ " cases"
								: districtShapesMedian + " cases");
				break;
			case CASE_INCIDENCE:
				legendEntry = buildMapIconLegendEntry("low-region-small",
						DataHelper.getTruncatedBigDecimal(
								districtShapesLowerQuartile.add(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP))
								+ " - " + DataHelper.getTruncatedBigDecimal(districtShapesMedian) + " cases / "
								+ DistrictDto.CASE_INCIDENCE_DIVISOR);
				break;
			default:
				throw new IllegalArgumentException(caseMeasure.toString());
			}

			regionLegendLayout.addComponent(legendEntry);
		}

		if (districtShapesMedian.compareTo(districtShapesUpperQuartile) < 0) {
			switch (caseMeasure) {
			case CASE_COUNT:
				legendEntry = buildMapIconLegendEntry("high-region-small",
						districtShapesUpperQuartile.compareTo(districtShapesMedian.add(BigDecimal.ONE)) > 0
								? districtShapesMedian.add(BigDecimal.ONE) + " - " + districtShapesUpperQuartile
										+ " cases"
								: districtShapesUpperQuartile + " cases");
				break;
			case CASE_INCIDENCE:
				legendEntry = buildMapIconLegendEntry("high-region-small",
						DataHelper.getTruncatedBigDecimal(
								districtShapesMedian.add(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP)) + " - "
								+ DataHelper.getTruncatedBigDecimal(districtShapesUpperQuartile) + " cases / "
								+ DistrictDto.CASE_INCIDENCE_DIVISOR);
				break;
			default:
				throw new IllegalArgumentException(caseMeasure.toString());
			}

			regionLegendLayout.addComponent(legendEntry);
		}

		switch (caseMeasure) {
		case CASE_COUNT:
			legendEntry = buildMapIconLegendEntry("highest-region-small",
					"> " + districtShapesUpperQuartile + " cases");
			break;
		case CASE_INCIDENCE:
			legendEntry = buildMapIconLegendEntry("red-region-small",
					"> " + DataHelper.getTruncatedBigDecimal(districtShapesUpperQuartile) + " cases / "
							+ DistrictDto.CASE_INCIDENCE_DIVISOR);
			break;
		default:
			throw new IllegalArgumentException(caseMeasure.toString());
		}
		regionLegendLayout.addComponent(legendEntry);

		if (caseMeasure == CaseMeasure.CASE_INCIDENCE && emptyPopulationDistrictPresent) {
			legendEntry = buildMapIconLegendEntry("no-population-region-small", "No population data available");
			regionLegendLayout.addComponent(legendEntry);
		}

		return regionLegendLayout;
	}

	private void clearRegionShapes() {

		map.removeGroup(REGIONS_GROUP_ID);
		map.removeGroup(DISTRICTS_GROUP_ID);
		polygonRegions.clear();
		polygonDistricts.clear();

		emptyPopulationDistrictPresent = false;
		map.setTileLayerOpacity(1);
	}

	private void showRegionsShapes(CaseMeasure caseMeasure, Date fromDate, Date toDate, Disease disease) {

		clearRegionShapes();
		map.setTileLayerOpacity(0.5f);

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllAsReference();
		List<LeafletPolygon> regionPolygons = new ArrayList<LeafletPolygon>();

		// draw outlines of all regions
		for (RegionReferenceDto region : regions) {

			GeoLatLon[][] regionShape = FacadeProvider.getGeoShapeProvider().getRegionShape(region);
			if (regionShape == null) {
				continue;
			}

			for (GeoLatLon[] regionShapePart : regionShape) {
				LeafletPolygon polygon = new LeafletPolygon();
				polygon.setCaption(region.getCaption());
				// fillOpacity is used, so we can still hover the region
				polygon.setOptions("{\"weight\": 1, \"color\": '#444', \"fillOpacity\": 0.02}");
				polygon.setLatLons(regionShapePart);
				regionPolygons.add(polygon);
				polygonRegions.add(region);
			}
		}

		map.addPolygonGroup(REGIONS_GROUP_ID, regionPolygons);

		List<Pair<DistrictDto, BigDecimal>> measurePerDistrict = FacadeProvider.getCaseFacade()
				.getCaseMeasurePerDistrict(fromDate, toDate, disease, caseMeasure);
		if (caseMeasure == CaseMeasure.CASE_COUNT) {
			districtValuesLowerQuartile = measurePerDistrict.size() > 0
					? measurePerDistrict.get((int) (measurePerDistrict.size() * 0.25)).getElement1()
					: null;
			districtValuesMedian = measurePerDistrict.size() > 0
					? measurePerDistrict.get((int) (measurePerDistrict.size() * 0.5)).getElement1()
					: null;
			districtValuesUpperQuartile = measurePerDistrict.size() > 0
					? measurePerDistrict.get((int) (measurePerDistrict.size() * 0.75)).getElement1()
					: null;
		} else {
			// For case incidence, districts without or with a population <= 0 should not be
			// used for the calculation of the quartiles because they will falsify the
			// result
			List<Pair<DistrictDto, BigDecimal>> measurePerDistrictWithoutMissingPopulations = new ArrayList<>();
			measurePerDistrictWithoutMissingPopulations.addAll(measurePerDistrict);
			measurePerDistrictWithoutMissingPopulations
					.removeIf(d -> d.getElement0().getPopulation() == null || d.getElement0().getPopulation() <= 0);
			districtValuesLowerQuartile = measurePerDistrictWithoutMissingPopulations.size() > 0
					? measurePerDistrictWithoutMissingPopulations
							.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.25)).getElement1()
					: null;
			districtValuesMedian = measurePerDistrictWithoutMissingPopulations.size() > 0
					? measurePerDistrictWithoutMissingPopulations
							.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.5)).getElement1()
					: null;
			districtValuesUpperQuartile = measurePerDistrictWithoutMissingPopulations.size() > 0
					? measurePerDistrictWithoutMissingPopulations
							.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.75)).getElement1()
					: null;
		}

		List<LeafletPolygon> districtPolygons = new ArrayList<LeafletPolygon>();

		// Draw relevant district fills
		for (Pair<DistrictDto, BigDecimal> districtMeasure : measurePerDistrict) {

			DistrictDto district = districtMeasure.getElement0();
			DistrictReferenceDto districtRef = district.toReference();
			BigDecimal districtValue = districtMeasure.getElement1();
			GeoLatLon[][] districtShape = FacadeProvider.getGeoShapeProvider().getDistrictShape(districtRef);
			if (districtShape == null) {
				continue;
			}

			String fillColor;
			if (districtValue.compareTo(BigDecimal.ZERO) == 0) {
				fillColor = "#000";
			} else if (districtValue.compareTo(districtValuesLowerQuartile) < 0) {
				fillColor = "#FEDD6C";
			} else if (districtValue.compareTo(districtValuesMedian) < 0) {
				fillColor = "#FDBF44";
			} else if (districtValue.compareTo(districtValuesUpperQuartile) < 0) {
				fillColor = "#F47B20";
			} else {
				fillColor = "#ED1B24";
			}

			if (caseMeasure == CaseMeasure.CASE_INCIDENCE) {
				if (district.getPopulation() == null || district.getPopulation() <= 0) {
					// grey when region has no population data
					emptyPopulationDistrictPresent = true;
					fillColor = "#999";
				}
			}

			for (GeoLatLon[] districtShapePart : districtShape) {
				LeafletPolygon polygon = new LeafletPolygon();
				polygon.setCaption(district.getName() + "<br>" + districtValue);
				polygon.setOptions("{\"stroke\": false, \"color\": '" + fillColor + "', \"fillOpacity\": 0.8}");
				polygon.setLatLons(districtShapePart);
				districtPolygons.add(polygon);
				polygonDistricts.add(districtRef);
			}
		}

		map.addPolygonGroup(DISTRICTS_GROUP_ID, districtPolygons);
	}

	private void clearCaseMarkers() {

		map.removeGroup(CASES_GROUP_ID);
//		map.removeGroup(CASE_FACILITIES_GROUP_ID);

		markerCaseFacilities.clear();
		casesByFacility.clear();
		mapCaseDtos.clear();
		mapAndFacilityCases.clear();
	}

	private void showCaseMarkers(List<MapCaseDto> cases) {

		clearCaseMarkers();

		fillCaseLists(cases);

		List<LeafletMarker> caseMarkers = new ArrayList<LeafletMarker>();

		for (FacilityReferenceDto facilityReference : casesByFacility.keySet()) {

			List<MapCaseDto> casesList = casesByFacility.get(facilityReference);
			// colorize the icon by the "strongest" classification type (order as in enum)
			// and set its size depending
			// on the number of cases
			int numberOfCases = casesList.size();
			Set<CaseClassification> classificationSet = new HashSet<>();
			for (MapCaseDto caze : casesList) {
				classificationSet.add(caze.getCaseClassification());
			}

			MarkerIcon icon;
			if (classificationSet.contains(CaseClassification.CONFIRMED)) {
				icon = MarkerIcon.FACILITY_CONFIRMED;
			} else if (classificationSet.contains(CaseClassification.PROBABLE)) {
				icon = MarkerIcon.FACILITY_PROBABLE;
			} else if (classificationSet.contains(CaseClassification.SUSPECT)) {
				icon = MarkerIcon.FACILITY_SUSPECT;
			} else {
				icon = MarkerIcon.FACILITY_UNCLASSIFIED;
			}

			// create and place the marker
			markerCaseFacilities.add(facilityReference);

			MapCaseDto firstCase = casesList.get(0);
			LeafletMarker leafletMarker = new LeafletMarker();
			leafletMarker.setLatLon(firstCase.getHealthFacilityLat(), firstCase.getHealthFacilityLon());
			leafletMarker.setIcon(icon);
			leafletMarker.setMarkerCount(numberOfCases);
			caseMarkers.add(leafletMarker);
		}

//		map.addMarkerGroup(CASE_FACILITIES_GROUP_ID, caseMarkers);
//		List<LeafletMarker> caseMarkers = new ArrayList<LeafletMarker>();

		for (MapCaseDto caze : mapCaseDtos) {
			LeafletMarker marker = new LeafletMarker();
			if (caze.getCaseClassification() == CaseClassification.CONFIRMED) {
				marker.setIcon(MarkerIcon.CASE_CONFIRMED);
			} else if (caze.getCaseClassification() == CaseClassification.PROBABLE) {
				marker.setIcon(MarkerIcon.CASE_PROBABLE);
			} else if (caze.getCaseClassification() == CaseClassification.SUSPECT) {
				marker.setIcon(MarkerIcon.CASE_SUSPECT);
			} else {
				marker.setIcon(MarkerIcon.CASE_UNCLASSIFIED);
			}

			if (caze.getAddressLat() != null && caze.getAddressLon() != null) {
				marker.setLatLon(caze.getAddressLat(), caze.getAddressLon());
			} else {
				marker.setLatLon(caze.getReportLat(), caze.getReportLon());
			}

			caseMarkers.add(marker);
		}

		map.addMarkerGroup("cases", caseMarkers);
	}

	private void fillCaseLists(List<MapCaseDto> cases) {
		for (MapCaseDto caze : cases) {
			CaseClassification classification = caze.getCaseClassification();
			if (classification == null || classification == CaseClassification.NO_CASE)
				continue;
			boolean hasCaseGps = (caze.getAddressLat() != null && caze.getAddressLon() != null) 
					 || (caze.getReportLat() != null || caze.getReportLon() != null);
			boolean hasFacilityGps = caze.getHealthFacilityLat() != null && caze.getHealthFacilityLon() != null;
			if (!hasCaseGps && !hasFacilityGps) {
				continue; // no gps at all
			}			

			if (mapCaseDisplayMode == MapCaseDisplayMode.CASES) {
				if (!hasCaseGps) {
					continue; 
				}
				mapCaseDtos.add(caze);
			} else {
				if (caze.getHealthFacilityUuid().equals(FacilityDto.NONE_FACILITY_UUID)
						|| caze.getHealthFacilityUuid().equals(FacilityDto.OTHER_FACILITY_UUID)
						|| !hasFacilityGps) {
					if (mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITIES_OR_ADDRESS) {
						if (!hasCaseGps) {
							continue;
						}
						mapCaseDtos.add(caze);
					} else {
						continue;
					}
				} else {
					if (!hasFacilityGps) {
						continue;
					}
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
		map.removeGroup(CONTACTS_GROUP_ID);
		markerContacts.clear();
		mapContactDtos.clear();
	}

	private void showContactMarkers(List<MapContactDto> contacts) {

		clearContactMarkers();

		List<LeafletMarker> contactMarkers = new ArrayList<LeafletMarker>();

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

			MarkerIcon icon;
			Date lastVisitDateTime = contact.getLastVisitDateTime();
			long currentTime = new Date().getTime();
			if (lastVisitDateTime != null) {
				// 1000 ms = 1 second; 3600 seconds = 1 hour
				if (currentTime - lastVisitDateTime.getTime() >= 1000 * 3600 * 48) {
					icon = MarkerIcon.CONTACT_LONG_OVERDUE;
				} else if (currentTime - lastVisitDateTime.getTime() >= 1000 * 3600 * 24) {
					icon = MarkerIcon.CONTACT_OVERDUE;
				} else {
					icon = MarkerIcon.CONTACT_OK;
				}
			} else {
				icon = MarkerIcon.CONTACT_LONG_OVERDUE;
			}

			LeafletMarker marker = new LeafletMarker();
			marker.setIcon(icon);
			if (contact.getAddressLat() != null && contact.getAddressLon() != null) {
				marker.setLatLon(contact.getAddressLat(), contact.getAddressLon());
			} else {
				marker.setLatLon(contact.getReportLat(), contact.getReportLon());
			}
			markerContacts.add(contact);
			contactMarkers.add(marker);
		}
		map.addMarkerGroup(CONTACTS_GROUP_ID, contactMarkers);
	}

	private void clearEventMarkers() {
		map.removeGroup(EVENTS_GROUP_ID);
		markerEvents.clear();
	}

	private void showEventMarkers(List<DashboardEventDto> events) {

		clearEventMarkers();

		List<LeafletMarker> eventMarkers = new ArrayList<LeafletMarker>();

		for (DashboardEventDto event : events) {
			MarkerIcon icon;
			if (event.getEventType() == EventType.OUTBREAK) {
				icon = MarkerIcon.EVENT_OUTBREAK;
			} else {
				icon = MarkerIcon.EVENT_RUMOR;
			}

			LeafletMarker marker = new LeafletMarker();
			if (event.getReportLat() != null && event.getReportLon() != null) {
				marker.setLatLon(event.getReportLat(), event.getReportLon());
			} else if (event.getDistrict() != null) {
				GeoLatLon districtCenter = FacadeProvider.getGeoShapeProvider()
						.getCenterOfDistrict(event.getDistrict());
				marker.setLatLon(districtCenter.getLat(), districtCenter.getLon());
			} else {
				continue;
			}

			marker.setIcon(icon);
			markerEvents.add(event);
			eventMarkers.add(marker);
		}

		map.addMarkerGroup(EVENTS_GROUP_ID, eventMarkers);
	}

	private void onMarkerClicked(String groupId, int markerIndex) {

		switch (groupId) {
		case CASES_GROUP_ID:// CASE_FACILITIES_GROUP_ID:

			if (markerIndex < markerCaseFacilities.size()) {
				FacilityReferenceDto facility = markerCaseFacilities.get(markerIndex);
				VerticalLayout layout = new VerticalLayout();
				Window window = VaadinUiUtil.showPopupWindow(layout);
				CasePopupGrid caseGrid = new CasePopupGrid(window, facility, DashboardMapComponent.this);
				caseGrid.setHeightMode(HeightMode.ROW);
				layout.addComponent(caseGrid);
				layout.setMargin(true);
				FacilityDto facilityDto = FacadeProvider.getFacilityFacade().getByUuid(facility.getUuid());
				window.setCaption("Cases in " + facilityDto.toString());
			} else {
//			break;
//		case CASES_GROUP_ID: {
				markerIndex -= markerCaseFacilities.size();
				MapCaseDto caze = mapCaseDtos.get(markerIndex);
				ControllerProvider.getCaseController().navigateToCase(caze.getUuid());
			}
			break;
		case CONTACTS_GROUP_ID: {
			MapContactDto contact = markerContacts.get(markerIndex);
			ControllerProvider.getContactController().navigateToData(contact.getUuid());
		}
			break;
		case EVENTS_GROUP_ID: {
			DashboardEventDto event = markerEvents.get(markerIndex);
			ControllerProvider.getEventController().navigateToData(event.getUuid());
		}
			break;
		}
	}
}
