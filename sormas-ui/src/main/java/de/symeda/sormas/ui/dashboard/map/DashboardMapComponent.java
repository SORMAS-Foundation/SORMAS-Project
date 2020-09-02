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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
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
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.region.GeoShapeProvider;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.map.LeafletMap;
import de.symeda.sormas.ui.map.LeafletMapUtil;
import de.symeda.sormas.ui.map.LeafletMarker;
import de.symeda.sormas.ui.map.LeafletPolygon;
import de.symeda.sormas.ui.map.MarkerIcon;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class DashboardMapComponent extends VerticalLayout {

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
	private MapCaseClassificationOption caseClassificationOption;
	private boolean showContacts;
	private boolean showConfirmedContacts;
	private boolean showUnconfirmedContacts;
	private boolean showEvents;
	private boolean showRegions;
	private boolean hideOtherCountries;
	private Date dateFrom = null;
	private Date dateTo = null;

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
	private MapCaseDisplayMode mapCaseDisplayMode = MapCaseDisplayMode.HEALTH_FACILITY_OR_CASE_ADDRESS;
	private BigDecimal districtValuesLowerQuartile;
	private BigDecimal districtValuesMedian;
	private BigDecimal districtValuesUpperQuartile;
	private Consumer<Boolean> externalExpandListener;
	private boolean emptyPopulationDistrictPresent;

	private ComboBox cmbPeriodType;
	private ComboBox cmbPeriodFilter;

	public DashboardMapComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		setMargin(false);
		setSpacing(false);
		setSizeFull();

		map = new LeafletMap();
		map.setSizeFull();
		map.addMarkerClickListener(event -> onMarkerClicked(event.getGroupId(), event.getMarkerIndex()));

		{

			GeoShapeProvider geoShapeProvider = FacadeProvider.getGeoShapeProvider();

			final GeoLatLon mapCenter;
			if (UserProvider.getCurrent().hasAnyUserRole(UserRole.NATIONAL_USER, UserRole.NATIONAL_CLINICIAN, UserRole.NATIONAL_OBSERVER)) {
				mapCenter = geoShapeProvider.getCenterOfAllRegions();

			} else {
				UserDto user = UserProvider.getCurrent().getUser();
				if (user.getRegion() != null) {
					mapCenter = geoShapeProvider.getCenterOfRegion(user.getRegion());
				} else {
					mapCenter = geoShapeProvider.getCenterOfAllRegions();
				}
			}

			GeoLatLon center = Optional.ofNullable(mapCenter).orElseGet(FacadeProvider.getConfigFacade()::getCountryCenter);

			map.setCenter(center);
		}

		map.setZoom(FacadeProvider.getConfigFacade().getMapZoom());

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

		this.setMargin(true);

		// Add components
		addComponent(createHeader());
		addComponent(map);
		addComponent(createFooter());
		setExpandRatio(map, 1);
	}

	public void refreshMap() {
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
		RegionReferenceDto region = dashboardDataProvider.getRegion();
		DistrictReferenceDto district = dashboardDataProvider.getDistrict();
		Disease disease = dashboardDataProvider.getDisease();

		if (showRegions) {
			showRegionsShapes(caseMeasure, fromDate, toDate, dashboardDataProvider.getDisease());
		}
		if (showCases) {
			showCaseMarkers(FacadeProvider.getCaseFacade().getCasesForMap(region, district, disease, fromDate, toDate));
		}
		if (showContacts) {
			if (!showCases) {
				// Case lists need to be filled even when cases are hidden because they are
				// needed to retrieve the contacts
				fillCaseLists(FacadeProvider.getCaseFacade().getCasesForMap(region, district, disease, fromDate, toDate));
			}
			showContactMarkers(FacadeProvider.getContactFacade().getContactsForMap(region, district, disease, fromDate, toDate, mapAndFacilityCases));
		}
		if (showEvents) {
			showEventMarkers(dashboardDataProvider.getEvents());
		}

		// Re-create the map key layout to only show the keys for the selected layers
		legendDropdown.setContent(createLegend());

		updatePeriodFilters();
	}

	public List<CaseDataDto> getCasesForFacility(FacilityReferenceDto facility) {
		List<CaseDataDto> casesForFacility = new ArrayList<>();
		CaseFacade caseFacade = FacadeProvider.getCaseFacade();
		for (MapCaseDto mapCaseDto : casesByFacility.get(facility)) {
			casesForFacility.add(caseFacade.getCaseDataByUuid(mapCaseDto.getUuid()));
		}
		return casesForFacility;
	}

	public void setExpandListener(Consumer<Boolean> listener) {
		externalExpandListener = listener;
	}

	private HorizontalLayout createHeader() {
		HorizontalLayout mapHeaderLayout = new HorizontalLayout();
		mapHeaderLayout.setWidth(100, Unit.PERCENTAGE);
		mapHeaderLayout.setSpacing(true);
		CssStyles.style(mapHeaderLayout, CssStyles.VSPACE_4);

		Label mapLabel = new Label();
		if (dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE) {
			mapLabel.setValue(I18nProperties.getString(Strings.headingCaseStatusMap));
		} else {
			mapLabel.setValue(I18nProperties.getString(Strings.headingContactMap));
		}
		mapLabel.setSizeUndefined();
		CssStyles.style(mapLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

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
		collapseMapButton.addClickListener(e -> {
			externalExpandListener.accept(false);
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
					refreshMap();
				});
				layersLayout.addComponent(caseClassificationOptions);

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
						refreshMap();
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
					refreshMap();
				});
				layersLayout.addComponent(showContactsCheckBox);

				showConfirmedContactsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowConfirmedContacts));
				showConfirmedContactsCheckBox.setValue(showConfirmedContacts);
				showConfirmedContactsCheckBox.addValueChangeListener(e -> {
					showConfirmedContacts = (boolean) e.getProperty().getValue();
					refreshMap();
				});
				layersLayout.addComponent(showConfirmedContactsCheckBox);

				CssStyles.style(showUnconfirmedContactsCheckBox, CssStyles.VSPACE_3);
				showUnconfirmedContactsCheckBox.setCaption(I18nProperties.getCaption(Captions.dashboardShowUnconfirmedContacts));
				showUnconfirmedContactsCheckBox.setValue(showUnconfirmedContacts);
				showUnconfirmedContactsCheckBox.addValueChangeListener(e -> {
					showUnconfirmedContacts = (boolean) e.getProperty().getValue();
					refreshMap();
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
					refreshMap();
				});
				layersLayout.addComponent(showEventsCheckBox);

				if (UserProvider.getCurrent().hasUserRole(UserRole.NATIONAL_USER)
					|| UserProvider.getCurrent().hasUserRole(UserRole.NATIONAL_CLINICIAN)
					|| UserProvider.getCurrent().hasUserRole(UserRole.NATIONAL_OBSERVER)) {
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
							refreshMap();
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
					refreshMap();
				});
				layersLayout.addComponent(hideOtherCountriesCheckBox);

				createPeriodFilters(layersLayout);
			}
		}

		PopupButton layersDropdown = ButtonHelper.createPopupButton(Captions.dashboardMapLayers, layersLayout, CssStyles.BUTTON_SUBTLE);

		mapFooterLayout.addComponent(layersDropdown);
		mapFooterLayout.setComponentAlignment(layersDropdown, Alignment.MIDDLE_RIGHT);

		return mapFooterLayout;
	}

	private enum PeriodFilterReloadFlag {
		RELOAD_AND_KEEP_VALUE,
		RELOAD_AND_CLEAR_VALUE,
		DONT_RELOAD
	}

	private PeriodFilterReloadFlag reloadPeriodFiltersFlag = PeriodFilterReloadFlag.RELOAD_AND_KEEP_VALUE;

	private void createPeriodFilters(VerticalLayout layersLayout) {
		cmbPeriodType = new ComboBox();
		cmbPeriodType.setId("periodType");
		cmbPeriodFilter = new ComboBox();
		cmbPeriodFilter.setId("periodFilter");

		Button btnBack = ButtonHelper.createIconButtonWithCaption("periodBack", null, VaadinIcons.CHEVRON_LEFT, e -> {
			Date curDate = (Date) cmbPeriodFilter.getValue();
			int curDateIndex = ((List<?>) cmbPeriodFilter.getItemIds()).indexOf(curDate);

			if (curDateIndex <= 0)
				return;

			int prevDateIndex = curDateIndex - 1;
			Date prevDate = (Date) ((List<?>) cmbPeriodFilter.getItemIds()).get(prevDateIndex);

			cmbPeriodFilter.setValue(prevDate);
		}, ValoTheme.BUTTON_BORDERLESS);
		btnBack.setEnabled(false);

		Button btnForward = ButtonHelper.createIconButtonWithCaption("periodForward", null, VaadinIcons.CHEVRON_RIGHT, e -> {
			Date curDate = (Date) cmbPeriodFilter.getValue();
			int curDateIndex = ((List<?>) cmbPeriodFilter.getItemIds()).indexOf(curDate);

			if (curDateIndex >= cmbPeriodFilter.size() - 1)
				return;

			int nextDateIndex = curDateIndex + 1;
			Date nextDate = (Date) ((List<?>) cmbPeriodFilter.getItemIds()).get(nextDateIndex);

			cmbPeriodFilter.setValue(nextDate);
		}, ValoTheme.BUTTON_BORDERLESS);
		btnForward.setEnabled(false);

		cmbPeriodType.addItems(MapPeriodType.values());
		cmbPeriodType.setInputPrompt(I18nProperties.getString(Strings.promptFilterByPeriod));
		cmbPeriodType.setWidth(132, Unit.PIXELS);
		cmbPeriodType.addValueChangeListener(e -> {
			reloadPeriodFiltersFlag = PeriodFilterReloadFlag.RELOAD_AND_CLEAR_VALUE;
			updatePeriodFilters();
		});

		cmbPeriodFilter.setInputPrompt(I18nProperties.getString(Strings.promptSelectPeriod));
		cmbPeriodFilter.setWidth(120, Unit.PIXELS);
		cmbPeriodFilter.setNullSelectionAllowed(false);
		cmbPeriodFilter.setEnabled(false);
		cmbPeriodFilter.addValueChangeListener(e -> {
			Date date = (Date) e.getProperty().getValue();

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
			int curDateIndex = ((List<?>) cmbPeriodFilter.getItemIds()).indexOf(date);
			Boolean hasNextDate = cmbPeriodFilter.size() > 0 && curDateIndex < cmbPeriodFilter.size() - 1;
			Boolean hasPrevDate = cmbPeriodFilter.size() > 0 && curDateIndex > 0;
			btnBack.setEnabled(hasPrevDate);
			btnForward.setEnabled(hasNextDate);

			reloadPeriodFiltersFlag = PeriodFilterReloadFlag.DONT_RELOAD;

			refreshMap();
		});
		cmbPeriodFilter.addItemSetChangeListener(e -> {
			cmbPeriodFilter.setEnabled(cmbPeriodFilter.size() > 0);
			btnForward.setEnabled(cmbPeriodFilter.size() > 0);
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
	}

	private void updatePeriodFilters() {
		MapPeriodType periodType = (MapPeriodType) cmbPeriodType.getValue();

		//store current flag and reset it
		PeriodFilterReloadFlag reloadFlag = reloadPeriodFiltersFlag;
		reloadPeriodFiltersFlag = PeriodFilterReloadFlag.RELOAD_AND_KEEP_VALUE;

		String cachedDateValue = cmbPeriodFilter.getCaption();

		if (reloadFlag != PeriodFilterReloadFlag.DONT_RELOAD)
			cmbPeriodFilter.removeAllItems();

		if (periodType == null) {
			dateFrom = null;
			dateTo = null;

			if (reloadFlag != PeriodFilterReloadFlag.RELOAD_AND_KEEP_VALUE)
				refreshMap();

			return;
		}

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

		cmbPeriodFilter.addItems(dates);
		for (Date date : dates) {
			String caption = DateHelper.formatLocalDate(date, dateFormat);
			cmbPeriodFilter.setItemCaption(date, caption);
			if (reloadFlag != PeriodFilterReloadFlag.RELOAD_AND_CLEAR_VALUE && caption.equals(cachedDateValue))
				cmbPeriodFilter.setValue(date);
		}

		if (reloadFlag == PeriodFilterReloadFlag.RELOAD_AND_CLEAR_VALUE)
			cmbPeriodFilter.setValue(cmbPeriodFilter.getItemIds().iterator().next());
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
			if (mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITY
				|| mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITY_OR_CASE_ADDRESS) {
				Label facilitiesKeyLabel = new Label(I18nProperties.getCaption(Captions.dashboardHealthFacilities));
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
			if (mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITY
				|| mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITY_OR_CASE_ADDRESS) {
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
				HorizontalLayout legendEntry = buildMarkerLegendEntry(MarkerIcon.EVENT_RUMOR, EventStatus.POSSIBLE.toString());
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				eventsKeyLayout.addComponent(legendEntry);
				legendEntry = buildMarkerLegendEntry(MarkerIcon.EVENT_OUTBREAK, EventStatus.CONFIRMED.toString());
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

	public static HorizontalLayout buildMarkerLegendEntry(MarkerIcon icon, String labelCaption) {
		return buildLegendEntry(new Label(icon.getHtmlElement("16px"), ContentMode.HTML), labelCaption);
	}

	public static HorizontalLayout buildMapIconLegendEntry(String iconName, String labelCaption) {
		Image icon = new Image(null, new ExternalResource("VAADIN/map/marker/" + iconName + ".png"));
		icon.setWidth(12.375f, Unit.PIXELS);
		icon.setHeight(16.875f, Unit.PIXELS);
		return buildLegendEntry(icon, labelCaption);
	}

	private static HorizontalLayout buildLegendEntry(AbstractComponent icon, String labelCaption) {
		HorizontalLayout entry = new HorizontalLayout();
		entry.setSpacing(false);
		entry.setSizeUndefined();
		CssStyles.style(icon, CssStyles.HSPACE_RIGHT_4);
		entry.addComponent(icon);
		Label label = new Label(labelCaption);
		label.setSizeUndefined();
		label.addStyleName(ValoTheme.LABEL_SMALL);
		entry.addComponent(label);
		return entry;
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

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveAsReference();
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
			if (caseClassificationOption == MapCaseClassificationOption.CONFIRMED_CASES_ONLY && classification != CaseClassification.CONFIRMED)
				continue;
			if (dateTo != null && !(caze.getReportDate() == dateTo || caze.getReportDate().before(dateTo) || dateTo.after(caze.getReportDate())))
				continue;
			boolean hasCaseGps =
				(caze.getAddressLat() != null && caze.getAddressLon() != null) || (caze.getReportLat() != null && caze.getReportLon() != null);
			boolean hasFacilityGps = caze.getHealthFacilityLat() != null && caze.getHealthFacilityLon() != null;
			if (!hasCaseGps && !hasFacilityGps) {
				continue; // no gps at all
			}

			if (mapCaseDisplayMode == MapCaseDisplayMode.CASE_ADDRESS) {
				if (!hasCaseGps) {
					continue;
				}
				mapCaseDtos.add(caze);
			} else {
				if (FacilityDto.NONE_FACILITY_UUID.equals(caze.getHealthFacilityUuid())
					|| FacilityDto.OTHER_FACILITY_UUID.equals(caze.getHealthFacilityUuid())
					|| !hasFacilityGps) {
					if (mapCaseDisplayMode == MapCaseDisplayMode.HEALTH_FACILITY_OR_CASE_ADDRESS) {
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
			Date referenceDate = contact.getCaseReportDate() != null ? contact.getCaseReportDate() : contact.getContactReportDate();
			if (dateTo != null && !(referenceDate == dateTo || referenceDate.before(dateTo) || dateTo.after(referenceDate))) {
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
			case CONFIRMED:
				icon = MarkerIcon.EVENT_OUTBREAK;
				break;
			case POSSIBLE:
				icon = MarkerIcon.EVENT_RUMOR;
				break;
			default:
				continue;
			}

			if (dateTo != null
				&& event.getEventDate() != null
				&& !(event.getEventDate() == dateTo || event.getEventDate().before(dateTo) || dateTo.after(event.getEventDate()))) {
				continue;
			}

			LeafletMarker marker = new LeafletMarker();
			if (event.getReportLat() != null && event.getReportLon() != null) {
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

	private void onMarkerClicked(String groupId, int markerIndex) {

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
				window.setCaption(I18nProperties.getCaption(Captions.dashboardCasesIn) + " " + facilityDto.toString());
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
}
