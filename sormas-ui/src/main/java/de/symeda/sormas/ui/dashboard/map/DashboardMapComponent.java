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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard.map;

import static de.symeda.sormas.api.i18n.Captions.UserRole;
import static java.util.Objects.nonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.geo.GeoShapeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.map.*;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import org.vaadin.hene.popupbutton.PopupButton;

@SuppressWarnings("serial")
public class DashboardMapComponent extends BaseDashboardMapComponent<DashboardCriteria, DashboardDataProvider> {

	private static final String CASES_GROUP_ID = "cases";
	private static final String CONTACTS_GROUP_ID = "contacts";
	private static final String EVENTS_GROUP_ID = "events";
	private static final String REGIONS_GROUP_ID = "regions";
	private static final String DISTRICTS_GROUP_ID = "districts";

	// Layers
	private boolean showCases;
	private MapCaseClassificationOption caseClassificationOption;
	private boolean showContacts;
	private boolean showConfirmedContacts;
	private boolean showUnconfirmedContacts;
	private boolean showEvents;
	private boolean showRegions;
	private boolean hideOtherCountries;
	private boolean showCurrentEpiSituation;



	// Entities
	private final HashMap<FacilityReferenceDto, List<MapCaseDto>> casesByFacility = new HashMap<>();
	private List<MapCaseDto> mapCaseDtos = new ArrayList<>();
	private List<MapCaseDto> mapAndFacilityCases = new ArrayList<>();
	private List<MapContactDto> mapContactDtos = new ArrayList<>();

	// Map data
	private final List<FacilityReferenceDto> markerCaseFacilities = new ArrayList<FacilityReferenceDto>();
	private final List<MapContactDto> markerContacts = new ArrayList<MapContactDto>();
	private final List<DashboardEventDto> markerEvents = new ArrayList<DashboardEventDto>();
	private final List<RegionReferenceDto> polygonRegions = new ArrayList<RegionReferenceDto>();
	private final List<DistrictReferenceDto> polygonDistricts = new ArrayList<DistrictReferenceDto>();

	// Others
	private CaseMeasure caseMeasure = CaseMeasure.CASE_COUNT;
	private MapCaseDisplayMode mapCaseDisplayMode = MapCaseDisplayMode.FACILITY_OR_CASE_ADDRESS;
	private  PeriodFilterReloadFlag reloadPeriodFiltersFlag = PeriodFilterReloadFlag.RELOAD_AND_KEEP_VALUE;

	private BigDecimal districtValuesLowerQuartile;
	private BigDecimal districtValuesMedian;
	private BigDecimal districtValuesUpperQuartile;
	private Consumer<Boolean> externalExpandListener;
	private boolean emptyPopulationDistrictPresent;
	//private  LeafletMap map;

	private MapCasePeriodOption mapCasePeriodOption;
	private  Label overlayMessageLabel;

	ComboBox cmbPeriodFilter;
	//private PopupButton legendDropdown;
	ComboBox cmbPeriodType;

	private Date dateFrom = null;
	private Date dateTo = null;
	private enum PeriodFilterReloadFlag {
		RELOAD_AND_KEEP_VALUE,
		RELOAD_AND_CLEAR_VALUE,
		DONT_RELOAD
	}



//	public DashboardMapComponent(DashboardDataProvider dashboardDataProvider) {
//		super(
//			dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE ? Strings.headingCaseStatusMap : Strings.headingContactMap,
//			dashboardDataProvider,
//			null);
//
//		//this.dashboardDataProvider = dashboardDataProvider;
//
//		setMargin(false);
//		setSpacing(false);
//		setSizeFull();
//
//		map = new LeafletMap();
//
//		map.setSizeFull();
//		map.addMarkerClickListener(event -> onMarkerClicked(event.getGroupId(), event.getMarkerIndex()));
//
//		{
//
//			GeoShapeProvider geoShapeProvider = FacadeProvider.getGeoShapeProvider();
//
//			final GeoLatLon mapCenter;
//			//if (UserProvider.getCurrent().hasAnyUserRole(DefaultUserRole.NATIONAL_USER, DefaultUserRole.NATIONAL_CLINICIAN, DefaultUserRole.NATIONAL_OBSERVER)) {
//				mapCenter = geoShapeProvider.getCenterOfAllRegions();
//
//			//} else {
//				UserDto user = UserProvider.getCurrent().getUser();
//
////				if (user.getRegion() != null) {
////					mapCenter = geoShapeProvider.getCenterOfRegion(user.getRegion());
////				} else {
////					mapCenter = geoShapeProvider.getCenterOfAllRegions();
////				}
//		//	}
//
//			GeoLatLon center = Optional.ofNullable(mapCenter).orElseGet(FacadeProvider.getConfigFacade()::getCountryCenter);
//
//			if (center == null || (center.getLat() == 0.0 && center.getLon() == 0)) {
//				center = new GeoLatLon(8.134, 1.423);
//			}
//			map.setCenter(center);
//		}
//
//		int zoomVal=FacadeProvider.getConfigFacade().getMapZoom();
//        if(zoomVal!=0) {
//			map.setZoom(zoomVal);
//		}
//
//	}

	public DashboardMapComponent(DashboardDataProvider dashboardDataProvider) {
		//super(dashboardDataProvider);
		super(
			dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE ? Strings.headingCaseStatusMap : Strings.headingContactMap,
			dashboardDataProvider,
			null);
//
		//this.dashboardDataProvider = dashboardDataProvider;
		if(dashboardDataProvider.getDashboardType().equals(DashboardType.DISEASE)) {

			setMargin(false);
			setSpacing(false);
			setSizeFull();

			map = new LeafletMap();

			map.setSizeFull();
			map.addMarkerClickListener(event -> onMarkerClicked(event.getGroupId(), event.getMarkerIndex()));

			{

				GeoShapeProvider geoShapeProvider = FacadeProvider.getGeoShapeProvider();

				final GeoLatLon mapCenter;
				// If map.usecountrycenter=true, use config coordinates. Else try to calculate the center of the user region/country
				if (FacadeProvider.getConfigFacade().isMapUseCountryCenter()) {
					mapCenter = FacadeProvider.getConfigFacade().getCountryCenter();
					map.setCenter(mapCenter);
				} else {
					UserDto user = UserProvider.getCurrent().getUser();
					if (user.getRegion() != null) {
						mapCenter = geoShapeProvider.getCenterOfRegion(user.getRegion());
					} else {
						mapCenter = geoShapeProvider.getCenterOfAllRegions();
					}

					GeoLatLon center = Optional.ofNullable(mapCenter).orElseGet(FacadeProvider.getConfigFacade()::getCountryCenter);
					map.setCenter(center);
				}

				GeoLatLon center = Optional.ofNullable(mapCenter).orElseGet(FacadeProvider.getConfigFacade()::getCountryCenter);

				if (center == null || (center.getLat() == 0.0 && center.getLon() == 0)) {
					center = new GeoLatLon(8.134, 1.423);
				}
				map.setCenter(center);
			}

			map.setZoom(FacadeProvider.getConfigFacade().getMapZoom());

			if (dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE) {
				showCases = true;
				caseClassificationOption = MapCaseClassificationOption.ALL_CASES;
				mapCasePeriodOption = MapCasePeriodOption.CASES_INCIDENCE;
				showContacts = false;
				showEvents = false;
				showConfirmedContacts = true;
				showUnconfirmedContacts = true;
			} else if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
				showCases = false;
				caseClassificationOption = MapCaseClassificationOption.ALL_CASES;
				mapCasePeriodOption = MapCasePeriodOption.CASES_INCIDENCE;
				showContacts = true;
				showEvents = false;
				showConfirmedContacts = true;
				showUnconfirmedContacts = true;
			} else if (dashboardDataProvider.getDashboardType() == DashboardType.DISEASE) {
				map.setZoom(6);
				showCases = true;
				caseClassificationOption = MapCaseClassificationOption.ALL_CASES;
				showContacts = false;
				showEvents = false;
				showConfirmedContacts = true;
				showUnconfirmedContacts = true;
			}
			hideOtherCountries = false;
			showCurrentEpiSituation = false;

			this.setMargin(true);

			// Add components
			addComponent(createHeader());

			CssLayout mapLayout = new CssLayout();
			mapLayout.setSizeFull();
			mapLayout.setStyleName(DashboardCssStyles.MAP_CONTAINER);

			map.addStyleName(DashboardCssStyles.MAP_COMPONENT);
			mapLayout.addComponent(map);

			overlayBackground = new CssLayout();
			overlayBackground.setStyleName(DashboardCssStyles.MAP_OVERLAY_BACKGROUND);
			overlayBackground.setVisible(false);
			mapLayout.addComponent(overlayBackground);

			overlayMessageLabel = new Label();
			overlayMessageLabel.addStyleNames(CssStyles.ALIGN_CENTER, CssStyles.LABEL_WHITE, CssStyles.LABEL_WHITE_SPACE_NORMAL);

			Button button = ButtonHelper.createButton(Captions.showPlacesOnMap, (e) -> {
				refreshMapDashboard(true);
			});

			overlayLayout = new VerticalLayout(overlayMessageLabel, button);
			overlayLayout.setStyleName(DashboardCssStyles.MAP_OVERLAY);
			overlayLayout.setHeightFull();
			overlayLayout.setComponentAlignment(overlayMessageLabel, Alignment.MIDDLE_CENTER);
			overlayLayout.setExpandRatio(overlayMessageLabel, 0);
			overlayLayout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
			overlayLayout.setExpandRatio(button, 0);
			overlayLayout.setVisible(false);
			mapLayout.addComponent(overlayLayout);

			addComponent(mapLayout);
			setExpandRatio(mapLayout, 1);

			addComponent(createFooter());
		}
	}

	@Override
	protected void addComponents() {
		if (dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE) {
			showCases = true;
			caseClassificationOption = MapCaseClassificationOption.ALL_CASES;
			showContacts = false;
			showEvents = false;
			showConfirmedContacts = true;
			showUnconfirmedContacts = true;
		} else if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
			showCases = false;
			caseClassificationOption = MapCaseClassificationOption.ALL_CASES;
			showContacts = true;
			showEvents = false;
			showConfirmedContacts = true;
			showUnconfirmedContacts = true;
		}

		hideOtherCountries = false;
		showCurrentEpiSituation = false;

		super.addComponents();
	}


	private HorizontalLayout createFooter() {
		HorizontalLayout mapFooterLayout = new HorizontalLayout();
		mapFooterLayout.setWidth(100, Unit.PERCENTAGE);
		mapFooterLayout.setSpacing(true);
		CssStyles.style(mapFooterLayout, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);

		// Map key dropdown button
		legendDropdown = ButtonHelper.createPopupButton(Captions.dashboardMapKey, null, CssStyles.BUTTON_SUBTLE);
		legendDropdown.setContent(createLegend());

		mapFooterLayout.addComponent(legendDropdown);
		mapFooterLayout.setComponentAlignment(legendDropdown, Alignment.MIDDLE_RIGHT);
		mapFooterLayout.setExpandRatio(legendDropdown, 1);

		// Layers dropdown button
		VerticalLayout layersLayout = new VerticalLayout();
		{
			layersLayout.setMargin(true);
			layersLayout.setSpacing(false);
			layersLayout.setSizeUndefined();

			// Add check boxes and apply button
			{
				//case classifications
				OptionGroup caseClassificationOptions = new OptionGroup();
				caseClassificationOptions.addItems((Object[]) MapCaseClassificationOption.values());
				caseClassificationOptions.setValue(caseClassificationOption);
				caseClassificationOptions.addValueChangeListener(event -> {
					caseClassificationOption = (MapCaseClassificationOption) event.getProperty().getValue();
					refreshMapDashboard(true);
				});

				// Optiongroup to select what property the coordinates should be based on
				OptionGroup mapCaseDisplayModeSelect = new OptionGroup();
				mapCaseDisplayModeSelect.setWidth(100, Unit.PERCENTAGE);
				mapCaseDisplayModeSelect.addItems((Object[]) MapCaseDisplayMode.values());
				mapCaseDisplayModeSelect.setValue(mapCaseDisplayMode);
				mapCaseDisplayModeSelect.addValueChangeListener(event -> {
					mapCaseDisplayMode = (MapCaseDisplayMode) event.getProperty().getValue();
					refreshMapDashboard(true);
				});

				HorizontalLayout showCasesLayout = new HorizontalLayout();
				{
					showCasesLayout.setMargin(false);
					showCasesLayout.setSpacing(false);
					CheckBox showCasesCheckBox = new CheckBox();
					showCasesCheckBox.setId(Captions.dashboardShowCases);
					showCasesCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowCases));
					showCasesCheckBox.setValue(showCases);
					showCasesCheckBox.addValueChangeListener(e -> {
						showCases = (boolean) e.getProperty().getValue();
						mapCaseDisplayModeSelect.setEnabled(showCases);
						caseClassificationOptions.setEnabled(showCases);
						mapCaseDisplayModeSelect.setValue(mapCaseDisplayMode);
						caseClassificationOptions.setEnabled(showCases);
						refreshMapDashboard(true);
					});
					showCasesLayout.addComponent(showCasesCheckBox);

					Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
					infoLabel.setDescription(I18nProperties.getString(Strings.infoCaseMap));
					CssStyles.style(infoLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_SECONDARY, CssStyles.HSPACE_LEFT_3);
					infoLabel.setHeightUndefined();
					showCasesLayout.addComponent(infoLabel);
					showCasesLayout.setComponentAlignment(infoLabel, Alignment.TOP_CENTER);
				}
				layersLayout.addComponent(showCasesLayout);

				layersLayout.addComponent(caseClassificationOptions);
				caseClassificationOptions.setEnabled(showCases);

				layersLayout.addComponent(mapCaseDisplayModeSelect);
				mapCaseDisplayModeSelect.setEnabled(showCases);

				layersLayout.addComponent(caseClassificationOptions);
				caseClassificationOptions.setEnabled(showCases);

				CheckBox showConfirmedContactsCheckBox = new CheckBox();
				showConfirmedContactsCheckBox.setId(Captions.dashboardShowConfirmedContacts);
				CheckBox showUnconfirmedContactsCheckBox = new CheckBox();
				showUnconfirmedContactsCheckBox.setId(Captions.dashboardShowUnconfirmedContacts);

				CheckBox showContactsCheckBox = new CheckBox();
				showContactsCheckBox.setId(Captions.dashboardShowContacts);
				showContactsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowContacts));
				showContactsCheckBox.setValue(showContacts);
				showContactsCheckBox.addValueChangeListener(e -> {
					showContacts = (boolean) e.getProperty().getValue();
					showConfirmedContactsCheckBox.setEnabled(showContacts);
					showConfirmedContactsCheckBox.setValue(true);
					showUnconfirmedContactsCheckBox.setEnabled(showContacts);
					showUnconfirmedContactsCheckBox.setValue(true);
					refreshMapDashboard(true);
				});
				layersLayout.addComponent(showContactsCheckBox);

				showConfirmedContactsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowConfirmedContacts));
				showConfirmedContactsCheckBox.setValue(showConfirmedContacts);
				showConfirmedContactsCheckBox.addValueChangeListener(e -> {
					showConfirmedContacts = (boolean) e.getProperty().getValue();
					refreshMapDashboard(true);
				});
				layersLayout.addComponent(showConfirmedContactsCheckBox);

				CssStyles.style(showUnconfirmedContactsCheckBox, CssStyles.VSPACE_3);
				showUnconfirmedContactsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowUnconfirmedContacts));
				showUnconfirmedContactsCheckBox.setValue(showUnconfirmedContacts);
				showUnconfirmedContactsCheckBox.addValueChangeListener(e -> {
					showUnconfirmedContacts = (boolean) e.getProperty().getValue();
					refreshMapDashboard(true);
				});
				layersLayout.addComponent(showUnconfirmedContactsCheckBox);

				showConfirmedContactsCheckBox.setEnabled(showContacts);
				showUnconfirmedContactsCheckBox.setEnabled(showContacts);

				CheckBox showEventsCheckBox = new CheckBox();
				showEventsCheckBox.setId(Captions.dashboardShowEvents);
				CssStyles.style(showEventsCheckBox, CssStyles.VSPACE_3);
				showEventsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowEvents));
				showEventsCheckBox.setValue(showEvents);
				showEventsCheckBox.addValueChangeListener(e -> {
					showEvents = (boolean) e.getProperty().getValue();
					refreshMapDashboard(true);
				});
				layersLayout.addComponent(showEventsCheckBox);
				if (nonNull(UserProvider.getCurrent()) && UserProvider.getCurrent().hasNationJurisdictionLevel()) {
					OptionGroup regionMapVisualizationSelect = new OptionGroup();
					regionMapVisualizationSelect.setWidth(100, Unit.PERCENTAGE);
					regionMapVisualizationSelect.addItems((Object[]) CaseMeasure.values());
					regionMapVisualizationSelect.setValue(caseMeasure);
					regionMapVisualizationSelect.addValueChangeListener(event -> {
						caseMeasure = (CaseMeasure) event.getProperty().getValue();
						refreshMapDashboard(true);
					});

					HorizontalLayout showRegionsLayout = new HorizontalLayout();
					{
						showRegionsLayout.setMargin(false);
						showRegionsLayout.setSpacing(false);
						CheckBox showRegionsCheckBox = new CheckBox();
						showRegionsCheckBox.setId(Captions.dashboardShowRegions);
						showRegionsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowRegions));
						showRegionsCheckBox.setValue(showRegions);
						showRegionsCheckBox.addValueChangeListener(e -> {
							showRegions = (boolean) e.getProperty().getValue();
							regionMapVisualizationSelect.setEnabled(showRegions);
							regionMapVisualizationSelect.setValue(caseMeasure);
							refreshMapDashboard(true);
						});
						showRegionsLayout.addComponent(showRegionsCheckBox);

						Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
						infoLabel.setDescription(I18nProperties.getString(Strings.infoCaseIncidence));
						CssStyles.style(infoLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_SECONDARY, CssStyles.HSPACE_LEFT_3);
						infoLabel.setHeightUndefined();
						showRegionsLayout.addComponent(infoLabel);
						showRegionsLayout.setComponentAlignment(infoLabel, Alignment.TOP_CENTER);
					}
					layersLayout.addComponent(showRegionsLayout);
					layersLayout.addComponent(regionMapVisualizationSelect);
					regionMapVisualizationSelect.setEnabled(showRegions);
				}

				CheckBox hideOtherCountriesCheckBox = new CheckBox();
				hideOtherCountriesCheckBox.setId(Captions.dashboardHideOtherCountries);
				hideOtherCountriesCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardHideOtherCountries));
				hideOtherCountriesCheckBox.setValue(hideOtherCountries);
				hideOtherCountriesCheckBox.addValueChangeListener(e -> {
					hideOtherCountries = (boolean) e.getProperty().getValue();
					refreshMapDashboard(true);
				});
				CssStyles.style(hideOtherCountriesCheckBox, CssStyles.VSPACE_3);
				layersLayout.addComponent(hideOtherCountriesCheckBox);

				CheckBox showCurrentEpiSituationCB = new CheckBox();
				showCurrentEpiSituationCB.setId(Captions.dashboardMapShowEpiSituation);
				showCurrentEpiSituationCB.setCaption(I18nProperties.getCaption(Captions.dashboardMapShowEpiSituation));
				showCurrentEpiSituationCB.setValue(false);
				showCurrentEpiSituationCB.addValueChangeListener(e -> {
					showCurrentEpiSituation = (boolean) e.getProperty().getValue();
					refreshMapDashboard(true);
				});
				layersLayout.addComponent(showCurrentEpiSituationCB);

				createPeriodFilters(layersLayout);
			}
		}

		PopupButton layersDropdown = ButtonHelper.createPopupButton(Captions.dashboardMapLayers, layersLayout, CssStyles.BUTTON_SUBTLE);

		mapFooterLayout.addComponent(layersDropdown);
		mapFooterLayout.setComponentAlignment(layersDropdown, Alignment.MIDDLE_RIGHT);

		return mapFooterLayout;
	}

	private VerticalLayout createLegend() {
		VerticalLayout legendLayout = new VerticalLayout();
		legendLayout.setSpacing(false);
		legendLayout.setMargin(true);
		legendLayout.setSizeUndefined();

		// Disable map key dropdown if no layers have been selected
		if (showCases || showContacts || showEvents || showRegions) {
			legendDropdown.setEnabled(true);
		} else {
			legendDropdown.setEnabled(false);
			return legendLayout;
		}

		// Health facilities

		// Cases
		if (showCases) {
			if (mapCaseDisplayMode == MapCaseDisplayMode.FACILITY || mapCaseDisplayMode == MapCaseDisplayMode.FACILITY_OR_CASE_ADDRESS) {
				Label facilitiesKeyLabel = new Label(I18nProperties.getCaption(Captions.dashboardFacilities));
				CssStyles.style(facilitiesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
				legendLayout.addComponent(facilitiesKeyLabel);

				HorizontalLayout facilitiesKeyLayout = new HorizontalLayout();
				{
					facilitiesKeyLayout.setSpacing(false);
					facilitiesKeyLayout.setMargin(false);
					HorizontalLayout legendEntry =
							buildMarkerLegendEntry(MarkerIcon.FACILITY_UNCLASSIFIED, I18nProperties.getCaption(Captions.dashboardNotYetClassifiedOnly));
					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
					facilitiesKeyLayout.addComponent(legendEntry);
					legendEntry = buildMarkerLegendEntry(MarkerIcon.FACILITY_SUSPECT, I18nProperties.getCaption(Captions.dashboardGt1SuspectCases));
					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
					facilitiesKeyLayout.addComponent(legendEntry);
					legendEntry = buildMarkerLegendEntry(MarkerIcon.FACILITY_PROBABLE, I18nProperties.getCaption(Captions.dashboardGt1ProbableCases));
					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
					facilitiesKeyLayout.addComponent(legendEntry);
					legendEntry =
							buildMarkerLegendEntry(MarkerIcon.FACILITY_CONFIRMED, I18nProperties.getCaption(Captions.dashboardGt1ConfirmedCases));
					facilitiesKeyLayout.addComponent(legendEntry);
				}
				legendLayout.addComponent(facilitiesKeyLayout);
			}

			Label casesKeyLabel = new Label(I18nProperties.getString(Strings.entityCases));
			if (mapCaseDisplayMode == MapCaseDisplayMode.FACILITY || mapCaseDisplayMode == MapCaseDisplayMode.FACILITY_OR_CASE_ADDRESS) {
				CssStyles.style(casesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(casesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendLayout.addComponent(casesKeyLabel);

			HorizontalLayout casesKeyLayout = new HorizontalLayout();
			{
				casesKeyLayout.setSpacing(false);
				casesKeyLayout.setMargin(false);
				HorizontalLayout legendEntry =
						buildMarkerLegendEntry(MarkerIcon.CASE_UNCLASSIFIED, I18nProperties.getCaption(Captions.dashboardNotYetClassified));
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.CASE_SUSPECT, I18nProperties.getCaption(Captions.dashboardSuspect));
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.CASE_PROBABLE, I18nProperties.getCaption(Captions.dashboardProbable));
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.CASE_CONFIRMED, I18nProperties.getCaption(Captions.dashboardConfirmed));
				casesKeyLayout.addComponent(legendEntry);
			}
			legendLayout.addComponent(casesKeyLayout);
		}

		// Contacts
		if (showContacts) {
			Label contactsKeyLabel = new Label(I18nProperties.getString(Strings.entityContacts));
			if (showCases) {
				CssStyles.style(contactsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(contactsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendLayout.addComponent(contactsKeyLabel);

			HorizontalLayout contactsKeyLayout = new HorizontalLayout();
			{
				contactsKeyLayout.setSpacing(false);
				contactsKeyLayout.setMargin(false);
				HorizontalLayout legendEntry =
						buildMarkerLegendEntry(MarkerIcon.CONTACT_OK, I18nProperties.getCaption(Captions.dashboardNotAContact));
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				contactsKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.CONTACT_OVERDUE, I18nProperties.getCaption(Captions.dashboardUnconfirmedContact));
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				contactsKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.CONTACT_LONG_OVERDUE, I18nProperties.getCaption(Captions.dashboardConfirmedContact));
				contactsKeyLayout.addComponent(legendEntry);
			}
			legendLayout.addComponent(contactsKeyLayout);
		}

		// Events
		if (showEvents) {
			Label eventsKeyLabel = new Label(I18nProperties.getString(Strings.entityEvents));
			if (showCases || showContacts) {
				CssStyles.style(eventsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(eventsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendLayout.addComponent(eventsKeyLabel);

			HorizontalLayout eventsKeyLayout = new HorizontalLayout();
			{
				eventsKeyLayout.setSpacing(false);
				eventsKeyLayout.setMargin(false);
				HorizontalLayout legendEntry = buildMarkerLegendEntry(MarkerIcon.EVENT_RUMOR, EventStatus.SIGNAL.toString());
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				eventsKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.EVENT_OUTBREAK, EventStatus.EVENT.toString());
				eventsKeyLayout.addComponent(legendEntry);
			}
			legendLayout.addComponent(eventsKeyLayout);
		}

		// Districts
		if (showRegions && districtValuesLowerQuartile != null && districtValuesMedian != null && districtValuesUpperQuartile != null) {
			Label districtsKeyLabel = new Label(I18nProperties.getString(Strings.entityDistricts));
			if (showCases || showContacts || showEvents) {
				CssStyles.style(districtsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(districtsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendLayout.addComponent(districtsKeyLabel);
			legendLayout.addComponent(
					buildRegionLegend(
							false,
							caseMeasure,
							emptyPopulationDistrictPresent,
							districtValuesLowerQuartile,
							districtValuesMedian,
							districtValuesUpperQuartile,
							InfrastructureHelper.CASE_INCIDENCE_DIVISOR));

			Label descLabel = new Label(I18nProperties.getString(Strings.infoDashboardIncidence));
			CssStyles.style(descLabel, CssStyles.LABEL_SMALL);
			legendLayout.addComponent(descLabel);
		}

		return legendLayout;
	}
//	@Override
//	protected void addComponents() {
//		if (dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE) {
//			showCases = true;
//			caseClassificationOption = MapCaseClassificationOption.ALL_CASES;
//			showContacts = false;
//			showEvents = false;
//			showConfirmedContacts = true;
//			showUnconfirmedContacts = true;
//		}
//		else if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
//			showCases = false;
//			caseClassificationOption = MapCaseClassificationOption.ALL_CASES;
//			showContacts = true;
//			showEvents = false;
//			showConfirmedContacts = true;
//			showUnconfirmedContacts = true;
//		}
//
//		else if (dashboardDataProvider.getDashboardType() == DashboardType.DISEASE) {
//			map.setZoom(6);
//			showCases = true;
//			caseClassificationOption = MapCaseClassificationOption.ALL_CASES;
//			showContacts = false;
//			showEvents = false;
//			showConfirmedContacts = true;
//			showUnconfirmedContacts = true;
//		}
//		hideOtherCountries = false;
//		showCurrentEpiSituation = false;
//
//		super.addComponents();
//	}

	private void createPeriodFilters(VerticalLayout layersLayout) {
		cmbPeriodType = new ComboBox();
		cmbPeriodFilter = new ComboBox();

		Button btnBack = new Button(VaadinIcons.CHEVRON_LEFT);
		Button btnForward = new Button(VaadinIcons.CHEVRON_RIGHT);

		cmbPeriodType.setItems(MapPeriodType.values());
		cmbPeriodType.setPlaceholder(I18nProperties.getString(Strings.promptFilterByPeriod));
		cmbPeriodType.setWidth(132, Unit.PIXELS);
		cmbPeriodType.addValueChangeListener(e -> {
			reloadPeriodFiltersFlag = PeriodFilterReloadFlag.RELOAD_AND_CLEAR_VALUE;

			updatePeriodFilters();
		});

		//case period display
		OptionGroup casePeriodDisplayOptions = new OptionGroup();
		casePeriodDisplayOptions.addItems((Object[]) MapCasePeriodOption.values());
		casePeriodDisplayOptions.setValue(mapCasePeriodOption);
		casePeriodDisplayOptions.setEnabled(false);
		casePeriodDisplayOptions.addValueChangeListener(event -> {
			mapCasePeriodOption = (MapCasePeriodOption) event.getProperty().getValue();
			Date date = (Date) cmbPeriodFilter.getValue();
			if (date != null) {
				if (mapCasePeriodOption.equals(MapCasePeriodOption.CASES_INCIDENCE)) {
					dateFrom = DateHelper.getStartOfYear(date);

				} else {
					dateFrom = DateHelper.getStartOfMonth(date);

				}
				dateTo = DateHelper.getEndOfMonth(date);
				reloadPeriodFiltersFlag = PeriodFilterReloadFlag.DONT_RELOAD;
				dashboardDataProvider.setFromDate(dateFrom);
				dashboardDataProvider.setToDate(dateTo);
				refreshMapDashboard();
			} else {
				refreshMapDashboard(false);
			}
		});

		cmbPeriodFilter.setPlaceholder(I18nProperties.getString(Strings.promptSelectPeriod));
		cmbPeriodFilter.setWidth(120, Unit.PIXELS);
		cmbPeriodFilter.setEmptySelectionAllowed(false);
		cmbPeriodFilter.setEnabled(false);
		cmbPeriodFilter.addValueChangeListener(e -> {
			Date date = (Date) e.getValue();

			if (date != null) {
				MapPeriodType periodType = (MapPeriodType) cmbPeriodType.getValue();

				switch (periodType) {
					case DAILY:
						dateFrom = DateHelper.getStartOfDay(date);
						dateTo = DateHelper.getEndOfDay(date);
						break;
					case WEEKLY:
						dateFrom = DateHelper.getStartOfWeek(date);
						dateTo = DateHelper.getEndOfWeek(date);
						break;
					case MONTHLY:
						dateFrom = DateHelper.getStartOfMonth(date);
						dateTo = DateHelper.getEndOfMonth(date);
						casePeriodDisplayOptions.setEnabled(true);
						break;
					case YEARLY:
						dateFrom = DateHelper.getStartOfYear(date);
						dateTo = DateHelper.getEndOfYear(date);
						break;
					default:
						dateFrom = null;
						dateTo = null;
				}
			} else {
				dateFrom = null;
				dateTo = null;
			}

			//disable arrow buttons if date is first or last item in the dropdown
			int curDateIndex = ((List<?>) cmbPeriodFilter.getValue()).indexOf(date);
			Boolean hasNextDate = ((List<?>) cmbPeriodFilter.getValue()).size() > 0 && curDateIndex < ((List<?>)cmbPeriodFilter.getValue()).size() - 1;
			Boolean hasPrevDate = ((List<?>)cmbPeriodFilter.getValue()).size() > 0 && curDateIndex > 0;
			btnBack.setEnabled(hasPrevDate);
			btnForward.setEnabled(hasNextDate);

			reloadPeriodFiltersFlag = PeriodFilterReloadFlag.DONT_RELOAD;

			refreshMapDashboard();
		});
		cmbPeriodFilter.addValueChangeListener(e -> {
			cmbPeriodFilter.setEnabled(((List<?>)cmbPeriodFilter.getValue()).size() > 0);
			btnForward.setEnabled(((List<?>)cmbPeriodFilter.getValue()).size() > 0);
		});

		CssStyles.style(btnBack, ValoTheme.BUTTON_BORDERLESS);
		btnBack.setEnabled(false);
		btnBack.addClickListener(e -> {
			Date curDate = (Date) cmbPeriodFilter.getValue();
			int curDateIndex = ((List<?>) cmbPeriodFilter.getValue()).indexOf(curDate);

			if (curDateIndex <= 0)
				return;

			int prevDateIndex = curDateIndex - 1;
			Date prevDate = (Date) ((List<?>) cmbPeriodFilter.getValue()).get(prevDateIndex);

			cmbPeriodFilter.setValue(prevDate);
		});

		CssStyles.style(btnForward, ValoTheme.BUTTON_BORDERLESS);
		btnForward.setEnabled(false);
		btnForward.addClickListener(e -> {
			Date curDate = (Date) cmbPeriodFilter.getValue();
			int curDateIndex = ((List<?>) cmbPeriodFilter.getValue()).indexOf(curDate);

			if (curDateIndex >= ((List<?>) cmbPeriodFilter.getValue()).size() - 1)
				return;

			int nextDateIndex = curDateIndex + 1;
			Date nextDate = (Date) ((List<?>) cmbPeriodFilter.getValue()).get(nextDateIndex);

			cmbPeriodFilter.setValue(nextDate);
		});

		HorizontalLayout periodSelectionLayout = new HorizontalLayout();
		periodSelectionLayout.setSpacing(false);

		periodSelectionLayout.addComponent(btnBack);
		periodSelectionLayout.addComponent(cmbPeriodFilter);
		periodSelectionLayout.addComponent(btnForward);

		HorizontalLayout periodFilterLayout = new HorizontalLayout();
		periodFilterLayout.setStyleName(CssStyles.VSPACE_TOP_2);
		periodFilterLayout.addComponent(cmbPeriodType);
		periodFilterLayout.addComponent(periodSelectionLayout);
		layersLayout.addComponent(periodFilterLayout);
		layersLayout.addComponent(casePeriodDisplayOptions);
	}

	private void updatePeriodFilters() {
		MapPeriodType periodType = (MapPeriodType) cmbPeriodType.getValue();

		//store current flag and reset it
		PeriodFilterReloadFlag reloadFlag = reloadPeriodFiltersFlag;
		reloadPeriodFiltersFlag = PeriodFilterReloadFlag.RELOAD_AND_KEEP_VALUE;

		String cachedDateValue = cmbPeriodFilter.getCaption();

		if (reloadFlag != PeriodFilterReloadFlag.DONT_RELOAD)
			cmbPeriodFilter.setItems();

		//cmbPeriodFilter.removeAllItems();

		if (periodType == null) {
			cmbPeriodFilter.setEnabled(false);
			dateFrom = null;
			dateTo = null;

			if (reloadFlag != PeriodFilterReloadFlag.RELOAD_AND_KEEP_VALUE)
				refreshMapDashboard();

			return;
		}

		cmbPeriodFilter.setEnabled(true);
//		checks if map has a case before rendering period dropdown
		if (mapAndFacilityCases.size() == 0)
			return;

		List<Date> reportedDates = mapAndFacilityCases.stream().map(c -> c.getReportDate()).collect(Collectors.toList());
		Date minDate = reportedDates.stream().min(Date::compareTo).get();
		Date maxDate = reportedDates.stream().max(Date::compareTo).get();

		List<Date> dates;
		String strDateFormat = "";
		switch (periodType) {
			case DAILY:
				dates = DateHelper.listDaysBetween(minDate, maxDate);
				strDateFormat = "MMM dd, yyyy";
				break;
			case WEEKLY:
				dates = DateHelper.listWeeksBetween(minDate, maxDate);
				strDateFormat = "'" + I18nProperties.getString(Strings.weekShort) + "' w, yyyy";
				break;
			case MONTHLY:
				dates = DateHelper.listMonthsBetween(minDate, maxDate);
				strDateFormat = "MMM yyyy";
				break;
			case YEARLY:
				dates = DateHelper.listYearsBetween(minDate, maxDate);
				strDateFormat = "yyyy";
				break;
			default:
				dates = Collections.emptyList();
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat);

		cmbPeriodFilter.setItems(dates);
		for (Date date : dates) {
			String caption = DateHelper.formatLocalDate(date, dateFormat);
			cmbPeriodFilter.setCaption(caption);
			if (reloadFlag != PeriodFilterReloadFlag.RELOAD_AND_CLEAR_VALUE && caption.equals(cachedDateValue))
				cmbPeriodFilter.setValue(date);
		}

		if (reloadFlag == PeriodFilterReloadFlag.RELOAD_AND_CLEAR_VALUE)
			cmbPeriodFilter.setValue(cmbPeriodFilter.getData());
	}

//	protected void refreshMapDashboard(boolean forced) {
//		clearRegionShapes();
//		clearCaseMarkers();
//		clearContactMarkers();
//		clearEventMarkers();
//		LeafletMapUtil.clearOtherCountriesOverlay(map);
//
//		if (hideOtherCountries) {
//			LeafletMapUtil.addOtherCountriesOverlay(map);
//		}
//
//		Date fromDate = dashboardDataProvider.getFromDate();
//		Date toDate = dashboardDataProvider.getToDate();
//
//		if (showRegions) {
//			showRegionsShapes(caseMeasure, fromDate, toDate, dashboardDataProvider.getDisease());
//		}
//
//		super.refreshMapDashboard(forced);
//	}

	@Override
	protected Long getMarkerCount(Date fromDate, Date toDate, int maxCount) {
		RegionReferenceDto region = dashboardDataProvider.getRegion();
		DistrictReferenceDto district = dashboardDataProvider.getDistrict();
		Disease disease = dashboardDataProvider.getDisease();

		Long count = 0L;

		if (showCases) {
			count += FacadeProvider.getCaseFacade()
				.countCasesForMap(
					region,
					district,
					disease,
					fromDate,
					toDate,
					showCurrentEpiSituation ? null : dashboardDataProvider.getNewCaseDateType());
		}

		if (count < maxCount && showContacts) {
			count += FacadeProvider.getContactFacade().countContactsForMap(region, district, disease, fromDate, toDate);
		}

		if (count < maxCount && showEvents) {
			count += dashboardDataProvider.getEvents().size();
		}

		return count;
	}

	@Override
	protected void loadMapData(Date fromDate, Date toDate) {
		RegionReferenceDto region = dashboardDataProvider.getRegion();
		DistrictReferenceDto district = dashboardDataProvider.getDistrict();
		Disease disease = dashboardDataProvider.getDisease();

		if (showCases) {
			showCaseMarkers(
				FacadeProvider.getCaseFacade()
					.getCasesForMap(
						region,
						district,
						disease,
						fromDate,
						toDate,
						showCurrentEpiSituation ? null : dashboardDataProvider.getNewCaseDateType()));
		}
		if (showContacts) {
			showContactMarkers(FacadeProvider.getContactFacade().getContactsForMap(region, district, disease, fromDate, toDate));
		}
		if (showEvents) {
			showEventMarkers(dashboardDataProvider.getEvents());
		}
	}

	public List<CaseDataDto> getCasesForFacility(FacilityReferenceDto facility) {
		List<CaseDataDto> casesForFacility = new ArrayList<>();
		CaseFacade caseFacade = FacadeProvider.getCaseFacade();
		for (MapCaseDto mapCaseDto : casesByFacility.get(facility)) {
			casesForFacility.add(caseFacade.getCaseDataByUuid(mapCaseDto.getUuid()));
		}
		return casesForFacility;
	}

	@Override
	protected void addLayerOptions(VerticalLayout layersLayout) {

		//case classifications
		OptionGroup caseClassificationOptions = new OptionGroup();
		caseClassificationOptions.addItems((Object[]) MapCaseClassificationOption.values());
		caseClassificationOptions.setValue(caseClassificationOption);
		caseClassificationOptions.addValueChangeListener(event -> {
			caseClassificationOption = (MapCaseClassificationOption) event.getProperty().getValue();
			refreshMapDashboard(true);
		});

		// Optiongroup to select what property the coordinates should be based on
		OptionGroup mapCaseDisplayModeSelect = new OptionGroup();
		mapCaseDisplayModeSelect.setWidth(100, Unit.PERCENTAGE);
		mapCaseDisplayModeSelect.addItems((Object[]) MapCaseDisplayMode.values());
		mapCaseDisplayModeSelect.setValue(mapCaseDisplayMode);
		mapCaseDisplayModeSelect.addValueChangeListener(event -> {
			mapCaseDisplayMode = (MapCaseDisplayMode) event.getProperty().getValue();
			refreshMapDashboard(true);
		});

		HorizontalLayout showCasesLayout = new HorizontalLayout();
		{
			showCasesLayout.setMargin(false);
			showCasesLayout.setSpacing(false);
			CheckBox showCasesCheckBox = new CheckBox();
			showCasesCheckBox.setId(Captions.dashboardShowCases);
			showCasesCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowCases));
			showCasesCheckBox.setValue(showCases);
			showCasesCheckBox.addValueChangeListener(e -> {
				showCases = (boolean) e.getProperty().getValue();
				mapCaseDisplayModeSelect.setEnabled(showCases);
				mapCaseDisplayModeSelect.setValue(mapCaseDisplayMode);
				caseClassificationOptions.setEnabled(showCases);
				refreshMapDashboard(true);
			});
			showCasesLayout.addComponent(showCasesCheckBox);

			Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
			infoLabel.setDescription(I18nProperties.getString(Strings.infoCaseMap));
			CssStyles.style(infoLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_SECONDARY, CssStyles.HSPACE_LEFT_3);
			infoLabel.setHeightUndefined();
			showCasesLayout.addComponent(infoLabel);
			showCasesLayout.setComponentAlignment(infoLabel, Alignment.TOP_CENTER);
		}
		layersLayout.addComponent(showCasesLayout);

		layersLayout.addComponent(mapCaseDisplayModeSelect);
		mapCaseDisplayModeSelect.setEnabled(showCases);

		layersLayout.addComponent(caseClassificationOptions);
		caseClassificationOptions.setEnabled(showCases);

		CheckBox showConfirmedContactsCheckBox = new CheckBox();
		showConfirmedContactsCheckBox.setId(Captions.dashboardShowConfirmedContacts);
		CheckBox showUnconfirmedContactsCheckBox = new CheckBox();
		showUnconfirmedContactsCheckBox.setId(Captions.dashboardShowUnconfirmedContacts);

		CheckBox showContactsCheckBox = new CheckBox();
		showContactsCheckBox.setId(Captions.dashboardShowContacts);
		showContactsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowContacts));
		showContactsCheckBox.setValue(showContacts);
		showContactsCheckBox.addValueChangeListener(e -> {
			showContacts = (boolean) e.getProperty().getValue();
			showConfirmedContactsCheckBox.setEnabled(showContacts);
			showConfirmedContactsCheckBox.setValue(true);
			showUnconfirmedContactsCheckBox.setEnabled(showContacts);
			showUnconfirmedContactsCheckBox.setValue(true);
			refreshMapDashboard(true);
		});
		layersLayout.addComponent(showContactsCheckBox);

		showConfirmedContactsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowConfirmedContacts));
		showConfirmedContactsCheckBox.setValue(showConfirmedContacts);
		showConfirmedContactsCheckBox.addValueChangeListener(e -> {
			showConfirmedContacts = (boolean) e.getProperty().getValue();
			refreshMapDashboard(true);
		});
		layersLayout.addComponent(showConfirmedContactsCheckBox);

		CssStyles.style(showUnconfirmedContactsCheckBox, CssStyles.VSPACE_3);
		showUnconfirmedContactsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowUnconfirmedContacts));
		showUnconfirmedContactsCheckBox.setValue(showUnconfirmedContacts);
		showUnconfirmedContactsCheckBox.addValueChangeListener(e -> {
			showUnconfirmedContacts = (boolean) e.getProperty().getValue();
			refreshMapDashboard(true);
		});
		layersLayout.addComponent(showUnconfirmedContactsCheckBox);

		showConfirmedContactsCheckBox.setEnabled(showContacts);
		showUnconfirmedContactsCheckBox.setEnabled(showContacts);

		CheckBox showEventsCheckBox = new CheckBox();
		showEventsCheckBox.setId(Captions.dashboardShowEvents);
		CssStyles.style(showEventsCheckBox, CssStyles.VSPACE_3);
		showEventsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowEvents));
		showEventsCheckBox.setValue(showEvents);
		showEventsCheckBox.addValueChangeListener(e -> {
			showEvents = (boolean) e.getProperty().getValue();
			refreshMapDashboard(true);
		});
		layersLayout.addComponent(showEventsCheckBox);
		if (nonNull(UserProvider.getCurrent()) && UserProvider.getCurrent().hasNationJurisdictionLevel()) {
			OptionGroup regionMapVisualizationSelect = new OptionGroup();
			regionMapVisualizationSelect.setWidth(100, Unit.PERCENTAGE);
			regionMapVisualizationSelect.addItems((Object[]) CaseMeasure.values());
			regionMapVisualizationSelect.setValue(caseMeasure);
			regionMapVisualizationSelect.addValueChangeListener(event -> {
				caseMeasure = (CaseMeasure) event.getProperty().getValue();
				refreshMapDashboard(true);
			});

			HorizontalLayout showRegionsLayout = new HorizontalLayout();
			{
				showRegionsLayout.setMargin(false);
				showRegionsLayout.setSpacing(false);
				CheckBox showRegionsCheckBox = new CheckBox();
				showRegionsCheckBox.setId(Captions.dashboardShowRegions);
				showRegionsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowRegions));
				showRegionsCheckBox.setValue(showRegions);
				showRegionsCheckBox.addValueChangeListener(e -> {
					showRegions = (boolean) e.getProperty().getValue();
					regionMapVisualizationSelect.setEnabled(showRegions);
					regionMapVisualizationSelect.setValue(caseMeasure);
					refreshMapDashboard(true);
				});
				showRegionsLayout.addComponent(showRegionsCheckBox);

				Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
				infoLabel.setDescription(I18nProperties.getString(Strings.infoCaseIncidence));
				CssStyles.style(infoLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_SECONDARY, CssStyles.HSPACE_LEFT_3);
				infoLabel.setHeightUndefined();
				showRegionsLayout.addComponent(infoLabel);
				showRegionsLayout.setComponentAlignment(infoLabel, Alignment.TOP_CENTER);
			}
			layersLayout.addComponent(showRegionsLayout);
			layersLayout.addComponent(regionMapVisualizationSelect);
			regionMapVisualizationSelect.setEnabled(showRegions);
		}

		CheckBox hideOtherCountriesCheckBox = new CheckBox();
		hideOtherCountriesCheckBox.setId(Captions.dashboardHideOtherCountries);
		hideOtherCountriesCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardHideOtherCountries));
		hideOtherCountriesCheckBox.setValue(hideOtherCountries);
		hideOtherCountriesCheckBox.addValueChangeListener(e -> {
			hideOtherCountries = (boolean) e.getProperty().getValue();
			refreshMapDashboard(true);
		});
		CssStyles.style(hideOtherCountriesCheckBox, CssStyles.VSPACE_3);
		layersLayout.addComponent(hideOtherCountriesCheckBox);

		CheckBox showCurrentEpiSituationCB = new CheckBox();
		showCurrentEpiSituationCB.setId(Captions.dashboardMapShowEpiSituation);
		showCurrentEpiSituationCB.setCaption(I18nProperties.getCaption(Captions.dashboardMapShowEpiSituation));
		showCurrentEpiSituationCB.setValue(false);
		showCurrentEpiSituationCB.addValueChangeListener(e -> {
			showCurrentEpiSituation = (boolean) e.getProperty().getValue();
			refreshMapDashboard(true);
		});
		layersLayout.addComponent(showCurrentEpiSituationCB);
	}

	private HorizontalLayout createHeader() {
		HorizontalLayout mapHeaderLayout = new HorizontalLayout();
		mapHeaderLayout.setWidth(100, Unit.PERCENTAGE);
		mapHeaderLayout.setSpacing(true);
		CssStyles.style(mapHeaderLayout, CssStyles.VSPACE_4);

		Label mapLabel = new Label();
		if (dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE) {
			mapLabel.setValue(I18nProperties.getString(Strings.headingCaseStatusMap));
			CssStyles.style(mapLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		}
		else if (dashboardDataProvider.getDashboardType() == DashboardType.DISEASE) {
			mapLabel.setValue(I18nProperties.getCaption(Captions.diseaseDetailMap));
			CssStyles.style(mapLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_NONE);
		}
		else {
			mapLabel.setValue(I18nProperties.getString(Strings.headingContactMap));
			CssStyles.style(mapLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		}
		mapLabel.setSizeUndefined();

		mapHeaderLayout.addComponent(mapLabel);
		mapHeaderLayout.setComponentAlignment(mapLabel, Alignment.BOTTOM_LEFT);
		mapHeaderLayout.setExpandRatio(mapLabel, 1);

		// "Expand" and "Collapse" buttons
		Button expandMapButton =
			ButtonHelper.createIconButtonWithCaption("expandMap", "", VaadinIcons.EXPAND, null, CssStyles.BUTTON_SUBTLE, CssStyles.VSPACE_NONE);
		Button collapseMapButton =
			ButtonHelper.createIconButtonWithCaption("collapseMap", "", VaadinIcons.COMPRESS, null, CssStyles.BUTTON_SUBTLE, CssStyles.VSPACE_NONE);

		expandMapButton.addClickListener(e -> {
			externalExpandListener.accept(true);
			mapHeaderLayout.removeComponent(expandMapButton);
			mapHeaderLayout.addComponent(collapseMapButton);
			mapHeaderLayout.setComponentAlignment(collapseMapButton, Alignment.MIDDLE_RIGHT);
		});


        return mapHeaderLayout;
    }

	@Override
	protected List<Component> getLegendComponents() {
		List<Component> legendComponents = new ArrayList<>();
		// Cases
		if (showCases) {
			if (mapCaseDisplayMode == MapCaseDisplayMode.FACILITY || mapCaseDisplayMode == MapCaseDisplayMode.FACILITY_OR_CASE_ADDRESS) {
				Label facilitiesKeyLabel = new Label(I18nProperties.getCaption(Captions.dashboardFacilities));
				CssStyles.style(facilitiesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
				legendComponents.add(facilitiesKeyLabel);

				HorizontalLayout facilitiesKeyLayout = new HorizontalLayout();
				{
					facilitiesKeyLayout.setSpacing(false);
					facilitiesKeyLayout.setMargin(false);
					HorizontalLayout legendEntry =
						buildMarkerLegendEntry(MarkerIcon.FACILITY_UNCLASSIFIED, I18nProperties.getCaption(Captions.dashboardNotYetClassifiedOnly));
					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
					facilitiesKeyLayout.addComponent(legendEntry);
					legendEntry = buildMarkerLegendEntry(MarkerIcon.FACILITY_SUSPECT, I18nProperties.getCaption(Captions.dashboardGt1SuspectCases));
					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
					facilitiesKeyLayout.addComponent(legendEntry);
					legendEntry = buildMarkerLegendEntry(MarkerIcon.FACILITY_PROBABLE, I18nProperties.getCaption(Captions.dashboardGt1ProbableCases));
					CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
					facilitiesKeyLayout.addComponent(legendEntry);
					legendEntry =
						buildMarkerLegendEntry(MarkerIcon.FACILITY_CONFIRMED, I18nProperties.getCaption(Captions.dashboardGt1ConfirmedCases));
					facilitiesKeyLayout.addComponent(legendEntry);
				}
				legendComponents.add(facilitiesKeyLayout);
			}

			Label casesKeyLabel = new Label(I18nProperties.getString(Strings.entityCases));
			if (mapCaseDisplayMode == MapCaseDisplayMode.FACILITY || mapCaseDisplayMode == MapCaseDisplayMode.FACILITY_OR_CASE_ADDRESS) {
				CssStyles.style(casesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(casesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendComponents.add(casesKeyLabel);

			HorizontalLayout casesKeyLayout = new HorizontalLayout();
			{
				casesKeyLayout.setSpacing(false);
				casesKeyLayout.setMargin(false);
				HorizontalLayout legendEntry =
					buildMarkerLegendEntry(MarkerIcon.CASE_UNCLASSIFIED, I18nProperties.getCaption(Captions.dashboardNotYetClassified));
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.CASE_SUSPECT, I18nProperties.getCaption(Captions.dashboardSuspect));
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.CASE_PROBABLE, I18nProperties.getCaption(Captions.dashboardProbable));
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.CASE_CONFIRMED, I18nProperties.getCaption(Captions.dashboardConfirmed));
				casesKeyLayout.addComponent(legendEntry);
			}
			legendComponents.add(casesKeyLayout);
		}

		// Contacts
		if (showContacts) {
			Label contactsKeyLabel = new Label(I18nProperties.getString(Strings.entityContacts));
			if (showCases) {
				CssStyles.style(contactsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(contactsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendComponents.add(contactsKeyLabel);

			HorizontalLayout contactsKeyLayout = new HorizontalLayout();
			{
				contactsKeyLayout.setSpacing(false);
				contactsKeyLayout.setMargin(false);
				HorizontalLayout legendEntry =
					buildMarkerLegendEntry(MarkerIcon.CONTACT_OK, I18nProperties.getCaption(Captions.dashboardNotAContact));
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				contactsKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.CONTACT_OVERDUE, I18nProperties.getCaption(Captions.dashboardUnconfirmedContact));
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				contactsKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.CONTACT_LONG_OVERDUE, I18nProperties.getCaption(Captions.dashboardConfirmedContact));
				contactsKeyLayout.addComponent(legendEntry);
			}
			legendComponents.add(contactsKeyLayout);
		}

		// Events
		if (showEvents) {
			Label eventsKeyLabel = new Label(I18nProperties.getString(Strings.entityEvents));
			if (showCases || showContacts) {
				CssStyles.style(eventsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(eventsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendComponents.add(eventsKeyLabel);

			HorizontalLayout eventsKeyLayout = new HorizontalLayout();
			{
				eventsKeyLayout.setSpacing(false);
				eventsKeyLayout.setMargin(false);
				HorizontalLayout legendEntry = buildMarkerLegendEntry(MarkerIcon.EVENT_RUMOR, EventStatus.SIGNAL.toString());
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				eventsKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.EVENT_OUTBREAK, EventStatus.EVENT.toString());
				eventsKeyLayout.addComponent(legendEntry);
			}
			legendComponents.add(eventsKeyLayout);
		}

		// Districts
		if (showRegions && districtValuesLowerQuartile != null && districtValuesMedian != null && districtValuesUpperQuartile != null) {
			Label districtsKeyLabel = new Label(I18nProperties.getString(Strings.entityDistricts));
			if (showCases || showContacts || showEvents) {
				CssStyles.style(districtsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(districtsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			legendComponents.add(districtsKeyLabel);
			legendComponents.add(
				buildRegionLegend(
					false,
					caseMeasure,
					emptyPopulationDistrictPresent,
					districtValuesLowerQuartile,
					districtValuesMedian,
					districtValuesUpperQuartile,
					InfrastructureHelper.CASE_INCIDENCE_DIVISOR));

			Label descLabel = new Label(I18nProperties.getString(Strings.infoDashboardIncidence));
			CssStyles.style(descLabel, CssStyles.LABEL_SMALL);
			legendComponents.add(descLabel);
		}

		return legendComponents;
	}

	public static HorizontalLayout buildMapIconLegendEntry(String iconName, String labelCaption) {
		Image icon = new Image(null, new ExternalResource("VAADIN/map/marker/" + iconName + ".png"));
		icon.setWidth(12.375f, Unit.PIXELS);
		icon.setHeight(16.875f, Unit.PIXELS);
		return buildLegendEntry(icon, labelCaption);
	}

	public static AbstractOrderedLayout buildRegionLegend(
		boolean vertical,
		CaseMeasure caseMeasure,
		boolean emptyPopulationDistrictPresent,
		BigDecimal districtShapesLowerQuartile,
		BigDecimal districtShapesMedian,
		BigDecimal districtShapesUpperQuartile,
		int caseIncidenceDivisor) {
		AbstractOrderedLayout regionLegendLayout = vertical ? new VerticalLayout() : new HorizontalLayout();
		regionLegendLayout.setSpacing(true);
		CssStyles.style(regionLegendLayout, CssStyles.LAYOUT_MINIMAL);
		regionLegendLayout.setSizeUndefined();

		HorizontalLayout legendEntry;
		switch (caseMeasure) {
		case CASE_COUNT:
			legendEntry = buildMapIconLegendEntry(
				"lowest-region-small",
				districtShapesLowerQuartile.compareTo(BigDecimal.ONE) > 0
					? "1 - " + districtShapesLowerQuartile + " " + I18nProperties.getString(Strings.entityCases)
					: "1 " + I18nProperties.getString(Strings.entityCase));
			break;
		case CASE_INCIDENCE:
			legendEntry = buildMapIconLegendEntry(
				"lowest-region-small",
				"<= " + DataHelper.getTruncatedBigDecimal(districtShapesLowerQuartile) + " " + I18nProperties.getString(Strings.entityCases) + " / "
					+ caseIncidenceDivisor);
			break;
		default:
			throw new IllegalArgumentException(caseMeasure.toString());
		}
		regionLegendLayout.addComponent(legendEntry);

		if (districtShapesLowerQuartile.compareTo(districtShapesMedian) < 0) {
			switch (caseMeasure) {
			case CASE_COUNT:
				legendEntry = buildMapIconLegendEntry(
					"low-region-small",
					districtShapesMedian.compareTo(districtShapesLowerQuartile.add(BigDecimal.ONE)) > 0
						? districtShapesLowerQuartile.add(BigDecimal.ONE) + " - " + districtShapesMedian + " "
							+ I18nProperties.getString(Strings.entityCases)
						: districtShapesMedian + " " + I18nProperties.getString(Strings.entityCases));
				break;
			case CASE_INCIDENCE:
				legendEntry = buildMapIconLegendEntry(
					"low-region-small",
					DataHelper.getTruncatedBigDecimal(districtShapesLowerQuartile.add(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP)) + " - "
						+ DataHelper.getTruncatedBigDecimal(districtShapesMedian) + " " + I18nProperties.getString(Strings.entityCases) + " / "
						+ caseIncidenceDivisor);
				break;
			default:
				throw new IllegalArgumentException(caseMeasure.toString());
			}

			regionLegendLayout.addComponent(legendEntry);
		}

		if (districtShapesMedian.compareTo(districtShapesUpperQuartile) < 0) {
			switch (caseMeasure) {
			case CASE_COUNT:
				legendEntry = buildMapIconLegendEntry(
					"high-region-small",
					districtShapesUpperQuartile.compareTo(districtShapesMedian.add(BigDecimal.ONE)) > 0
						? districtShapesMedian.add(BigDecimal.ONE) + " - " + districtShapesUpperQuartile + " "
							+ I18nProperties.getString(Strings.entityCases)
						: districtShapesUpperQuartile + " " + I18nProperties.getString(Strings.entityCases));
				break;
			case CASE_INCIDENCE:
				legendEntry = buildMapIconLegendEntry(
					"high-region-small",
					DataHelper.getTruncatedBigDecimal(districtShapesMedian.add(new BigDecimal(0.1)).setScale(1, RoundingMode.HALF_UP)) + " - "
						+ DataHelper.getTruncatedBigDecimal(districtShapesUpperQuartile) + " " + I18nProperties.getString(Strings.entityCases) + " / "
						+ caseIncidenceDivisor);
				break;
			default:
				throw new IllegalArgumentException(caseMeasure.toString());
			}

			regionLegendLayout.addComponent(legendEntry);
		}

		switch (caseMeasure) {
		case CASE_COUNT:
			legendEntry = buildMapIconLegendEntry(
				"highest-region-small",
				"> " + districtShapesUpperQuartile + " " + I18nProperties.getString(Strings.entityCases));
			break;
		case CASE_INCIDENCE:
			legendEntry = buildMapIconLegendEntry(
				"highest-region-small",
				"> " + DataHelper.getTruncatedBigDecimal(districtShapesUpperQuartile) + " " + I18nProperties.getString(Strings.entityCases) + " / "
					+ caseIncidenceDivisor);
			break;
		default:
			throw new IllegalArgumentException(caseMeasure.toString());
		}
		regionLegendLayout.addComponent(legendEntry);

		if (caseMeasure == CaseMeasure.CASE_INCIDENCE && emptyPopulationDistrictPresent) {
			legendEntry = buildMapIconLegendEntry("no-population-region-small", I18nProperties.getCaption(Captions.dashboardNoPopulationData));
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

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveByServerCountry();
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

		List<Pair<DistrictDto, BigDecimal>> measurePerDistrict =
			FacadeProvider.getCaseFacade().getCaseMeasurePerDistrict(fromDate, toDate, disease, caseMeasure);
		if (caseMeasure == CaseMeasure.CASE_COUNT) {
			districtValuesLowerQuartile =
				measurePerDistrict.size() > 0 ? measurePerDistrict.get((int) (measurePerDistrict.size() * 0.25)).getElement1() : null;
			districtValuesMedian =
				measurePerDistrict.size() > 0 ? measurePerDistrict.get((int) (measurePerDistrict.size() * 0.5)).getElement1() : null;
			districtValuesUpperQuartile =
				measurePerDistrict.size() > 0 ? measurePerDistrict.get((int) (measurePerDistrict.size() * 0.75)).getElement1() : null;
		} else {
			// For case incidence, districts without or with a population <= 0 should not be
			// used for the calculation of the quartiles because they will falsify the
			// result
			List<Pair<DistrictDto, BigDecimal>> measurePerDistrictWithoutMissingPopulations = new ArrayList<>();
			measurePerDistrictWithoutMissingPopulations.addAll(measurePerDistrict);
			measurePerDistrictWithoutMissingPopulations.removeIf(d -> d.getElement1() == null || d.getElement1().intValue() <= 0);
			districtValuesLowerQuartile = measurePerDistrictWithoutMissingPopulations.size() > 0
				? measurePerDistrictWithoutMissingPopulations.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.25)).getElement1()
				: null;
			districtValuesMedian = measurePerDistrictWithoutMissingPopulations.size() > 0
				? measurePerDistrictWithoutMissingPopulations.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.5)).getElement1()
				: null;
			districtValuesUpperQuartile = measurePerDistrictWithoutMissingPopulations.size() > 0
				? measurePerDistrictWithoutMissingPopulations.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.75)).getElement1()
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
				if (districtValue == null || districtValue.intValue() <= 0) {
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

		for (MapCaseDto caze : mapCaseDtos) {
			LeafletMarker marker = new LeafletMarker();
			CaseClassification caseClassification = caze.getCaseClassification();
			if (caseClassification == CaseClassification.CONFIRMED
				|| caseClassification == CaseClassification.CONFIRMED_NO_SYMPTOMS
				|| caseClassification == CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS) {
				marker.setIcon(MarkerIcon.CASE_CONFIRMED);
			} else if (caseClassification == CaseClassification.PROBABLE) {
				marker.setIcon(MarkerIcon.CASE_PROBABLE);
			} else if (caseClassification == CaseClassification.SUSPECT) {
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
			// these filters need to be used for the count too
			CaseClassification classification = caze.getCaseClassification();
			if (caseClassificationOption == MapCaseClassificationOption.CONFIRMED_CASES_ONLY && classification != CaseClassification.CONFIRMED)
				continue;
			boolean hasCaseGps =
				(caze.getAddressLat() != null && caze.getAddressLon() != null) || (caze.getReportLat() != null && caze.getReportLon() != null);
			boolean hasFacilityGps = caze.getHealthFacilityLat() != null && caze.getHealthFacilityLon() != null;

			if (mapCaseDisplayMode == MapCaseDisplayMode.CASE_ADDRESS) {
				if (!hasCaseGps) {
					continue;
				}
				mapCaseDtos.add(caze);
			} else {
				if (FacilityDto.NONE_FACILITY_UUID.equals(caze.getHealthFacilityUuid())
					|| FacilityDto.OTHER_FACILITY_UUID.equals(caze.getHealthFacilityUuid())
					|| !hasFacilityGps) {
					if (mapCaseDisplayMode == MapCaseDisplayMode.FACILITY_OR_CASE_ADDRESS) {
						if (!hasCaseGps) {
							continue;
						}
						mapCaseDtos.add(caze);
					} else {
						continue;
					}
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
			// #1274 Temporarily disabled because it severely impacts the performance of the Dashboard
			//			Date lastVisitDateTime = contact.getLastVisitDateTime();
			//			long currentTime = new Date().getTime();
			//			if (lastVisitDateTime != null) {
			//				// 1000 ms = 1 second; 3600 seconds = 1 hour
			//				if (currentTime - lastVisitDateTime.getTime() >= 1000 * 3600 * 48) {
			//					icon = MarkerIcon.CONTACT_LONG_OVERDUE;
			//				} else if (currentTime - lastVisitDateTime.getTime() >= 1000 * 3600 * 24) {
			//					icon = MarkerIcon.CONTACT_OVERDUE;
			//				} else {
			//					icon = MarkerIcon.CONTACT_OK;
			//				}
			//			} else {
			//				icon = MarkerIcon.CONTACT_LONG_OVERDUE;
			//			}
			switch (contact.getContactClassification()) {
			case CONFIRMED:
				icon = MarkerIcon.CONTACT_LONG_OVERDUE;
				break;
			case UNCONFIRMED:
				icon = MarkerIcon.CONTACT_OVERDUE;
				break;
			case NO_CONTACT:
				icon = MarkerIcon.CONTACT_OK;
				break;
			default:
				icon = MarkerIcon.CONTACT_OK;
				break;
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
			switch (event.getEventStatus()) {
			case EVENT:
				icon = MarkerIcon.EVENT_OUTBREAK;
				break;
			case SIGNAL:
				icon = MarkerIcon.EVENT_RUMOR;
				break;
			default:
				continue;
			}

			// Because events are pulled from the dashboardDataProvider, we do not need to add additional filters for event dates here

			LeafletMarker marker = new LeafletMarker();
			if (event.getLocationLat() != null && event.getLocationLon() != null) {
				marker.setLatLon(event.getLocationLat(), event.getLocationLon());
			} else if (event.getReportLat() != null && event.getReportLon() != null) {
				marker.setLatLon(event.getReportLat(), event.getReportLon());
			} else if (event.getDistrict() != null) {
				GeoLatLon districtCenter = FacadeProvider.getGeoShapeProvider().getCenterOfDistrict(event.getDistrict());
				if (districtCenter != null) {
					marker.setLatLon(districtCenter.getLat(), districtCenter.getLon());
				} else {
					GeoLatLon countryCenter = FacadeProvider.getConfigFacade().getCountryCenter();
					marker.setLatLon(countryCenter.getLat(), countryCenter.getLon());
				}
			} else {
				continue;
			}

			marker.setIcon(icon);
			markerEvents.add(event);
			eventMarkers.add(marker);
		}

		map.addMarkerGroup(EVENTS_GROUP_ID, eventMarkers);
	}

	@Override
	protected void onMarkerClicked(String groupId, int markerIndex) {

		switch (groupId) {
		case CASES_GROUP_ID:
			if (markerIndex < markerCaseFacilities.size()) {
				FacilityReferenceDto facility = markerCaseFacilities.get(markerIndex);
				VerticalLayout layout = new VerticalLayout();
				Window window = VaadinUiUtil.showPopupWindow(layout);
				CasePopupGrid caseGrid = new CasePopupGrid(window, facility, DashboardMapComponent.this);
				caseGrid.setHeightMode(HeightMode.ROW);
				layout.addComponent(caseGrid);
				layout.setMargin(true);
				FacilityDto facilityDto = FacadeProvider.getFacilityFacade().getByUuid(facility.getUuid());
				window.setCaption(I18nProperties.getCaption(Captions.dashboardCasesIn) + " " + facilityDto.buildCaption());
			} else {
				markerIndex -= markerCaseFacilities.size();
				MapCaseDto caze = mapCaseDtos.get(markerIndex);
				ControllerProvider.getCaseController().navigateToCase(caze.getUuid(), true);
			}
			break;
		case CONTACTS_GROUP_ID:
			MapContactDto contact = markerContacts.get(markerIndex);
			ControllerProvider.getContactController().navigateToData(contact.getUuid(), true);
			break;
		case EVENTS_GROUP_ID:
			DashboardEventDto event = markerEvents.get(markerIndex);
			ControllerProvider.getEventController().navigateToData(event.getUuid(), true);
			break;
		}
	}


	private void refreshMapDashboard(boolean forced) {
		clearRegionShapes();
		clearCaseMarkers();
		clearContactMarkers();
		clearEventMarkers();
		LeafletMapUtil.clearOtherCountriesOverlay(map);

		if (hideOtherCountries) {
			LeafletMapUtil.addOtherCountriesOverlay(map);
		}

		Date fromDate = dashboardDataProvider.getFromDate();
		Date toDate = dashboardDataProvider.getToDate();

		if (showRegions) {
			showRegionsShapes(caseMeasure, fromDate, toDate, dashboardDataProvider.getDisease());
		}

		int maxDisplayCount = FacadeProvider.getConfigFacade().getDashboardMapMarkerLimit();

		Long count = 0L;
		if (!forced && maxDisplayCount >= 0) {
			count = getMarkerCount(fromDate, toDate, maxDisplayCount);
		}
if(dashboardDataProvider.getDashboardType().equals(DashboardType.DISEASE)) {
	if (!forced && maxDisplayCount >= 0 && count > maxDisplayCount) {
		showMapOverlay(maxDisplayCount);
	} else {
		hideMapOverlay();

		loadMapData(fromDate, toDate);
	}
}
	}

	public void refreshMapDashboard() {
		refreshMapDashboard(false);
	}

	private void showMapOverlay(int maxCount) {
		overlayBackground.setVisible(true);
		overlayLayout.setVisible(true);
		overlayMessageLabel.setValue(String.format(I18nProperties.getString(Strings.warningDashboardMapTooManyMarkers), maxCount));
	}

	private void hideMapOverlay() {
		overlayBackground.setVisible(false);
		overlayLayout.setVisible(false);
	}
}
