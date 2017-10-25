package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactMapDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * TODO THIS NEEDS A REFACTORING!
 */
@SuppressWarnings("serial")
public class DashboardView extends AbstractView {

	public static final String VIEW_NAME = "dashboard";

	public static final String I18N_PREFIX = "Dashboard";
	public static final String FROM_DATE = "fromDate";
	public static final String TO_DATE = "toDate";
	public static final String FROM_WEEK = "fromWeek";
	public static final String TO_WEEK = "toWeek";
	public static final String HEADING = "heading";
	public static final String SUB_HEADING = "subHeading";
	public static final String QUERY_PERIOD = "queryPeriod";
	public static final String PREVIOUS_PERIOD = "previousPeriod";
	public static final String DATE_FILTER_FOR_MAP = "dateFilterForMap";
	public static final String HCWS = "hcws";
	public static final String TOTAL = "total";
	public static final String UNDER_FOLLOW_UP = "underFollowUp";
	public static final String ON = "on";
	public static final String OUTBREAKS = "outbreaks";
	public static final String RUMORS = "rumors";
	public static final String CASES = "cases";
	public static final String DEATHS = "deaths";
	public static final String CONTACTS = "contacts";
	public static final String ALERTS = "alerts";
	public static final String NEW_CASES = "newCases";
	public static final String ALL = "all";
	public static final String DISTRICT = "district";
	public static final String DISEASE = "disease";
	public static final String NOT_YET_CLASSIFIED = "notYetClassified";
	public static final String CONFIRMED = "confirmed";
	public static final String PROBABLE = "probable";
	public static final String SUSPECT = "suspect";
	public static final String EPI_CURVE = "epiCurve";
	public static final String SITUATION_REPORT = "situationReport";
	public static final String CASE_MAP = "caseMap";
	public static final String EXPAND = "expand";
	public static final String COLLAPSE = "collapse";
	public static final String APPLY = "apply";
	public static final String SHOW_CASES = "showCases";
	public static final String SHOW_CONTACTS = "showContacts";
	public static final String SHOW_CONFIRMED_CONTACTS = "showConfirmedContacts";
	public static final String SHOW_UNCONFIRMED_CONTACTS = "showUnconfirmedContacts";
	public static final String NO_FOLLOW_UP = "noFollowUp";
	public static final String VISIT_24_HOURS = "visit24Hours";
	public static final String VISIT_48_HOURS = "visit48Hours";
	public static final String VISIT_GT_48_HOURS = "visitGT48Hours";

	private VerticalLayout dashboardLayout;
	private VerticalLayout mapLayout;
	private MapComponent mapComponent;
	private SituationReportTable situationReportTableLeft;
	private SituationReportTable situationReportTableRight;
	
	private VerticalLayout situationReportLayout;
	private HighChart epiCurveChart;
	private PopupButton mapKeyDropdown;
	private Button noGPSButton;
	
	private Label epiCurveDateLabel;
	private Label mapDateLabel;

	private List<CaseDataDto> cases = new ArrayList<>();
	private List<ContactMapDto> contacts = new ArrayList<>();

	private DistrictReferenceDto district;
	private Disease disease;
	private DateFilterOptions dateFilterOption;
	private Date fromDate = DateHelper.subtractDays(new Date(), 7);
	private Date toDate = new Date();
	private int fromWeek;
	private int toWeek;

	private boolean useDistrictFilterForMap;
	private boolean useDiseaseFilterForMap;
	private boolean useDateFilterForMap;
	private boolean showCases;
	private boolean showContacts;
	private boolean showConfirmedContacts;
	private boolean showUnconfirmedContacts;
	private boolean showRegions;
	private RegionMapVisualization regionMapVisualization = RegionMapVisualization.CASE_COUNT;

	public DashboardView() {
		super(VIEW_NAME);		
		addStyleName("dashboard-screen");		

		if (LoginHelper.isUserInRole(UserRole.NATIONAL_USER)) {
			showRegions = true;
		} else {
			showCases = true;
			showContacts = true;
			showConfirmedContacts = true;
			showUnconfirmedContacts = true;
		}

		dashboardLayout = new VerticalLayout();
		dashboardLayout.setSpacing(false);
		dashboardLayout.setSizeFull();
		dashboardLayout.setStyleName("crud-main-layout");

		dashboardLayout.addComponent(createFilterBar());
		
		// Temporary, will be replaced by statistics layout
		situationReportLayout = new VerticalLayout();
		{
			situationReportLayout.setMargin(new MarginInfo(false, true, true, true));
			situationReportLayout.setWidth(100, Unit.PERCENTAGE);
			situationReportLayout.setHeightUndefined();
			
			Label reportTableLabel = new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, SITUATION_REPORT));
			reportTableLabel.addStyleName(CssStyles.H4);
			situationReportLayout.addComponent(reportTableLabel);
			
			HorizontalLayout situationReportSubLayout = new HorizontalLayout();
			{
				situationReportSubLayout.setSpacing(true);
				situationReportSubLayout.setWidth(100, Unit.PERCENTAGE);
				situationReportSubLayout.setHeightUndefined();
				
				situationReportTableLeft = new SituationReportTable(true, false, false, false);
				situationReportTableRight = new SituationReportTable(false, true, true, true);

				if (dateFilterOption == DateFilterOptions.DATE) {
					situationReportTableLeft.clearAndFill(fromDate, toDate, district, disease, cases);
					situationReportTableRight.clearAndFill(fromDate, toDate, district, disease, cases);
				} else {
					int year = Calendar.getInstance().get(Calendar.YEAR);
					situationReportTableLeft.clearAndFill(DateHelper.getEpiWeekStart(new EpiWeek(year, fromWeek)), DateHelper.getEpiWeekEnd(new EpiWeek(year, toWeek)), district, disease, cases);
					situationReportTableRight.clearAndFill(DateHelper.getEpiWeekStart(new EpiWeek(year, fromWeek)), DateHelper.getEpiWeekEnd(new EpiWeek(year, toWeek)), district, disease, cases);
				}
				situationReportSubLayout.addComponent(situationReportTableLeft);
				situationReportSubLayout.addComponent(situationReportTableRight);
			}
			situationReportLayout.addComponent(situationReportSubLayout);
		}
		dashboardLayout.addComponent(situationReportLayout);
		
		HorizontalLayout contentLayout = createCurveAndMapLayout();
		dashboardLayout.addComponent(contentLayout);
		dashboardLayout.setExpandRatio(contentLayout, 1);

		addComponent(dashboardLayout);
	}

	private HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName(CssStyles.VSPACE_3);
		filterLayout.setMargin(new MarginInfo(true, true, false, true));

		ComboBox districtFilter = new ComboBox();
		ComboBox diseaseFilter = new ComboBox();
		ComboBox dateFilterOptionsFilter = new ComboBox();
		DateField dateFromFilter = new DateField();
		DateField dateToFilter = new DateField();
		ComboBox weekFromFilter = new ComboBox();
		ComboBox weekToFilter = new ComboBox();

		// 'Apply Filter' button
		Button applyButton = new Button(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, APPLY));
		applyButton.addStyleName(CssStyles.FORCE_CAPTION);
		applyButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				district = (DistrictReferenceDto) districtFilter.getValue();
				disease = (Disease) diseaseFilter.getValue();
				dateFilterOption = (DateFilterOptions) dateFilterOptionsFilter.getValue();
				if (dateFilterOption == DateFilterOptions.DATE) {
					fromDate = dateFromFilter.getValue();
					toDate = dateToFilter.getValue();
				} else {
					fromWeek = (int) weekFromFilter.getValue();
					toWeek = (int) weekToFilter.getValue();
				}
				applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
				refreshDashboard();
			}
		});

		// District filter
		if (LoginHelper.getCurrentUser().getRegion() != null) {
			districtFilter.setWidth(200, Unit.PIXELS);
			districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DISTRICT));
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(LoginHelper.getCurrentUser().getRegion().getUuid()));
			districtFilter.addValueChangeListener(e -> {
				applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			});
			districtFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DISTRICT));
			filterLayout.addComponent(districtFilter);
			district = (DistrictReferenceDto) districtFilter.getValue();
		}

		// Disease filter
		diseaseFilter.setWidth(200, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DISEASE));
		diseaseFilter.addItems((Object[])Disease.values());
		diseaseFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});
		diseaseFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DISEASE));
		filterLayout.addComponent(diseaseFilter);
		disease = (Disease) diseaseFilter.getValue();

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());

		// Date filter options
		dateFilterOptionsFilter.setWidth(200, Unit.PIXELS);
		dateFilterOptionsFilter.addStyleName(CssStyles.FORCE_CAPTION);
		dateFilterOptionsFilter.addItems((Object[])DateFilterOptions.values());
		dateFilterOptionsFilter.setNullSelectionAllowed(false);
		dateFilterOptionsFilter.select(DateFilterOptions.EPI_WEEK);
		dateFilterOptionsFilter.addValueChangeListener(e -> {
			if (e.getProperty().getValue() == DateFilterOptions.DATE) {
				filterLayout.removeComponent(weekFromFilter);
				filterLayout.removeComponent(weekToFilter);
				filterLayout.addComponent(dateFromFilter, filterLayout.getComponentIndex(dateFilterOptionsFilter) + 1);
				dateFromFilter.setValue(DateHelper.subtractDays(c.getTime(), 7));
				filterLayout.addComponent(dateToFilter, filterLayout.getComponentIndex(dateFromFilter) + 1);
				dateToFilter.setValue(c.getTime());
			} else {
				filterLayout.removeComponent(dateFromFilter);
				filterLayout.removeComponent(dateToFilter);
				filterLayout.addComponent(weekFromFilter, filterLayout.getComponentIndex(dateFilterOptionsFilter) + 1);
				weekFromFilter.setValue(DateHelper.getEpiWeek(c.getTime()).getWeek());
				filterLayout.addComponent(weekToFilter, filterLayout.getComponentIndex(weekFromFilter) + 1);
				weekToFilter.setValue(DateHelper.getEpiWeek(c.getTime()).getWeek());
			}
		});
		filterLayout.addComponent(dateFilterOptionsFilter);
		dateFilterOption = (DateFilterOptions) dateFilterOptionsFilter.getValue();

		// Epi week filter
		weekFromFilter.setWidth(200, Unit.PIXELS);
		weekFromFilter.addItems(DateHelper.createWeeksList(c.get(Calendar.YEAR)));
		weekFromFilter.setNullSelectionAllowed(false);
		weekFromFilter.setValue(DateHelper.getEpiWeek(c.getTime()).getWeek());
		weekFromFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, FROM_WEEK));
		weekFromFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});
		filterLayout.addComponent(weekFromFilter);
		fromWeek = (int) weekFromFilter.getValue();

		weekToFilter.setWidth(200, Unit.PIXELS);
		weekToFilter.addItems(DateHelper.createWeeksList(c.get(Calendar.YEAR)));
		weekToFilter.setNullSelectionAllowed(false);
		weekToFilter.setValue(DateHelper.getEpiWeek(c.getTime()).getWeek());
		weekToFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, TO_WEEK));
		weekToFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});
		filterLayout.addComponent(weekToFilter);
		toWeek = (int) weekToFilter.getValue();

		// Date filter
		dateFromFilter.setDateFormat(DateHelper.getShortDateFormat().toPattern());
		dateFromFilter.setWidth(200, Unit.PIXELS);
		dateFromFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, FROM_DATE));
		dateFromFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});

		dateToFilter.setDateFormat(DateHelper.getShortDateFormat().toPattern());
		dateToFilter.setWidth(200, Unit.PIXELS);
		dateToFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, TO_DATE));
		dateToFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});

		filterLayout.addComponent(applyButton);

		return filterLayout;
	}

//	private HorizontalLayout createStatisticsLayout() {
//		return null;
//	}

	private HorizontalLayout createCurveAndMapLayout() {
		HorizontalLayout wrapperLayout = new HorizontalLayout();
		wrapperLayout.addStyleName("curve-and-map-layout");
		wrapperLayout.setWidth(100, Unit.PERCENTAGE);
		wrapperLayout.setMargin(false);

		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setSpacing(true);
		layout.setMargin(true);

		// Epi curve layout
		VerticalLayout epiCurveLayout = createEpiCurveLayout();
		layout.addComponent(epiCurveLayout);

		// Map layout
		VerticalLayout epiMapLayout = new VerticalLayout();
		{
			mapLayout = createMapLayout(
					new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							dashboardLayout.removeComponent(situationReportLayout);
							layout.removeComponent(epiCurveLayout);
							layout.removeComponent(epiMapLayout);
							layout.addComponent(mapLayout);
							DashboardView.this.setHeight(100, Unit.PERCENTAGE);
							wrapperLayout.setHeight(100, Unit.PERCENTAGE);
							layout.setHeight(100, Unit.PERCENTAGE);
							mapLayout.setSizeFull();
						}
					},
					new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							layout.removeComponent(mapLayout);
							dashboardLayout.addComponent(situationReportLayout, 1);
							layout.addComponent(epiCurveLayout);
							layout.addComponent(epiMapLayout);
							epiMapLayout.addComponent(mapLayout);
							mapLayout.setHeight(380, Unit.PIXELS);
							DashboardView.this.setHeightUndefined();
							wrapperLayout.setHeightUndefined();
							layout.setHeightUndefined();
						}
					});
		}
		epiMapLayout.addComponent(mapLayout);
		layout.addComponent(epiMapLayout);

		wrapperLayout.addComponent(layout);

		return wrapperLayout;
	}
	
	private VerticalLayout createEpiCurveLayout() {
		VerticalLayout epiCurveLayout = new VerticalLayout();
		epiCurveLayout.setWidth(100, Unit.PERCENTAGE);
		epiCurveLayout.setHeight(380, Unit.PIXELS);

		// Epi curve header
		epiCurveLayout.addComponent(createEpiCurveHeaderLayout());

		// Epi curve chart
		createEpiCurveChart();
		epiCurveLayout.addComponent(epiCurveChart);
		epiCurveLayout.setExpandRatio(epiCurveChart, 1);

		return epiCurveLayout;
	}
	
	private HorizontalLayout createEpiCurveHeaderLayout() {
		HorizontalLayout epiCurveHeaderLayout = new HorizontalLayout();
		{
			epiCurveHeaderLayout.setWidth(100, Unit.PERCENTAGE);
			epiCurveHeaderLayout.setSpacing(true);
			CssStyles.style(epiCurveHeaderLayout, CssStyles.VSPACE_4);
			
			VerticalLayout epiCurveLabelLayout = new VerticalLayout();
			{
				epiCurveLabelLayout.setSizeUndefined();
				Label caseMapLabel = new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, EPI_CURVE));
				CssStyles.style(caseMapLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
				epiCurveLabelLayout.addComponent(caseMapLabel);

				epiCurveDateLabel = new Label();
				CssStyles.style(epiCurveDateLabel, CssStyles.H4, CssStyles.VSPACE_TOP_NONE);
				updateHeaderDateLabel(epiCurveDateLabel);
				epiCurveLabelLayout.addComponent(epiCurveDateLabel);
			}
			epiCurveHeaderLayout.addComponent(epiCurveLabelLayout);
			epiCurveHeaderLayout.setComponentAlignment(epiCurveLabelLayout, Alignment.BOTTOM_LEFT);
			epiCurveHeaderLayout.setExpandRatio(epiCurveLabelLayout, 1);
		}
		
		return epiCurveHeaderLayout;
	}

	private VerticalLayout createMapLayout(ClickListener expandListener, ClickListener collapseListener) {
		VerticalLayout mapLayout = new VerticalLayout();
		mapLayout.setWidth(100, Unit.PERCENTAGE);
		mapLayout.setHeight(360, Unit.PIXELS);

		mapComponent = new MapComponent();
		mapComponent.setSizeFull();

		// Map header
		mapLayout.addComponent(createMapHeaderLayout(expandListener, collapseListener));

		// Map
		mapLayout.addComponent(mapComponent);
		mapLayout.setExpandRatio(mapComponent, 1);

		// Map footer
		mapLayout.addComponent(createMapFooterLayout());

		return mapLayout;
	}

	private HorizontalLayout createMapHeaderLayout(ClickListener expandListener, ClickListener collapseListener) {
		HorizontalLayout mapHeaderLayout = new HorizontalLayout();
		{
			mapHeaderLayout.setWidth(100, Unit.PERCENTAGE);
			mapHeaderLayout.setSpacing(true);
			CssStyles.style(mapHeaderLayout, CssStyles.VSPACE_2);

			VerticalLayout mapLabelLayout = new VerticalLayout();
			{
				mapLabelLayout.setSizeUndefined();
				Label caseMapLabel = new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, CASE_MAP));
				CssStyles.style(caseMapLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
				mapLabelLayout.addComponent(caseMapLabel);

				mapDateLabel = new Label();
				CssStyles.style(mapDateLabel, CssStyles.H4, CssStyles.VSPACE_TOP_NONE);
				updateHeaderDateLabel(mapDateLabel);
				mapLabelLayout.addComponent(mapDateLabel);
			}
			mapHeaderLayout.addComponent(mapLabelLayout);
			mapHeaderLayout.setComponentAlignment(mapLabelLayout, Alignment.BOTTOM_LEFT);
			mapHeaderLayout.setExpandRatio(mapLabelLayout, 1);

			// "Cases without GPS Tag" button
			noGPSButton = new Button("Cases without GPS tag");
			CssStyles.style(noGPSButton, CssStyles.BUTTON_SUBTLE);
			noGPSButton.addClickListener(e -> {
				VerticalLayout layout = new VerticalLayout();
				Window window = VaadinUiUtil.showPopupWindow(layout);
				List<CaseDataDto> casesWithoutGPSTag = mapComponent.getCasesWithoutGPSTag();
				if (casesWithoutGPSTag == null || casesWithoutGPSTag.isEmpty()) {
					Label noCasesLabel = new Label("There are no cases without a GPS tag.");
					layout.addComponent(noCasesLabel);
				} else {
					CasePopupGrid caseGrid = new CasePopupGrid(window, null, mapComponent);
					caseGrid.setHeightMode(HeightMode.ROW);
					layout.addComponent(caseGrid);
				}
				layout.setMargin(true);
				window.setCaption("Cases Without GPS Tag");
			});
			mapHeaderLayout.addComponent(noGPSButton, 1);
			mapHeaderLayout.setComponentAlignment(noGPSButton, Alignment.MIDDLE_RIGHT);
			toggleNoGPSButtonVisibility();

			Button expandMapButton = new Button(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, EXPAND), FontAwesome.EXPAND);
			CssStyles.style(expandMapButton, CssStyles.BUTTON_SUBTLE);
			expandMapButton.addStyleName(CssStyles.VSPACE_NONE);   
			Button collapseMapButton = new Button(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, COLLAPSE), FontAwesome.COMPRESS);
			CssStyles.style(collapseMapButton, CssStyles.BUTTON_SUBTLE);
			collapseMapButton.addStyleName(CssStyles.VSPACE_NONE);

			expandMapButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					expandListener.buttonClick(event);
					mapHeaderLayout.removeComponent(expandMapButton);
					mapHeaderLayout.addComponent(collapseMapButton);
					mapHeaderLayout.setComponentAlignment(collapseMapButton, Alignment.MIDDLE_RIGHT);
				}
			});
			collapseMapButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					collapseListener.buttonClick(event);
					mapHeaderLayout.removeComponent(collapseMapButton);
					mapHeaderLayout.addComponent(expandMapButton);
					mapHeaderLayout.setComponentAlignment(expandMapButton, Alignment.MIDDLE_RIGHT);
				}
			});

			mapHeaderLayout.addComponent(expandMapButton);
			mapHeaderLayout.setComponentAlignment(expandMapButton, Alignment.MIDDLE_RIGHT);
		}

		return mapHeaderLayout;
	}

	private HorizontalLayout createMapFooterLayout() {
		HorizontalLayout mapFooterLayout = new HorizontalLayout();
		mapFooterLayout.setWidth(100, Unit.PERCENTAGE);
		mapFooterLayout.setSpacing(true);
		CssStyles.style(mapFooterLayout, CssStyles.VSPACE_TOP_2);

		// Map key dropdown button
		mapKeyDropdown = new PopupButton("Show Map Key");
		{
			CssStyles.style(mapKeyDropdown, CssStyles.BUTTON_SUBTLE);
			mapKeyDropdown.setContent(createMapKeyLayout());
		}
		mapFooterLayout.addComponent(mapKeyDropdown);
		mapFooterLayout.setComponentAlignment(mapKeyDropdown, Alignment.BOTTOM_LEFT);
		mapFooterLayout.setExpandRatio(mapKeyDropdown, 1);

		// Filters dropdown button
		PopupButton filtersDropdown = new PopupButton("Filters");
		{
			CssStyles.style(filtersDropdown, CssStyles.BUTTON_SUBTLE);

			VerticalLayout filtersLayout = new VerticalLayout();
			filtersLayout.setMargin(true);
			filtersLayout.setSizeUndefined();
			filtersDropdown.setContent(filtersLayout);

			// Add check boxes and apply button
			{
				if (LoginHelper.getCurrentUser().getRegion() != null) {
					CheckBox districtFilterForMap = new CheckBox();
					districtFilterForMap.addStyleName(CssStyles.VSPACE_NONE);
					districtFilterForMap.setCaption("Apply district filter");
					districtFilterForMap.addValueChangeListener(e -> {
						useDistrictFilterForMap = districtFilterForMap.getValue();
						refreshMap();
					});
					filtersLayout.addComponent(districtFilterForMap);
				}
				
				CheckBox diseaseFilterForMap = new CheckBox();
				diseaseFilterForMap.addStyleName(CssStyles.VSPACE_NONE);
				diseaseFilterForMap.setCaption("Apply disease filter");
				diseaseFilterForMap.addValueChangeListener(e -> {
					useDiseaseFilterForMap = diseaseFilterForMap.getValue();
					refreshMap();
				});
				filtersLayout.addComponent(diseaseFilterForMap);
				
				CheckBox dateFilterForMap = new CheckBox();
				dateFilterForMap.addStyleName(CssStyles.VSPACE_NONE);
				dateFilterForMap.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DATE_FILTER_FOR_MAP));
				dateFilterForMap.addValueChangeListener(e -> {
					useDateFilterForMap = dateFilterForMap.getValue();
					refreshMap();
				});
				filtersLayout.addComponent(dateFilterForMap);
			}
		}
		mapFooterLayout.addComponent(filtersDropdown);
		mapFooterLayout.setComponentAlignment(filtersDropdown, Alignment.BOTTOM_RIGHT);

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
				CheckBox showCasesCheckBox = new CheckBox();
				showCasesCheckBox.addStyleName(CssStyles.VSPACE_NONE);
				showCasesCheckBox.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, SHOW_CASES));
				showCasesCheckBox.setValue(showCases);
				showCasesCheckBox.addValueChangeListener(e -> {
					showCases = (boolean) e.getProperty().getValue();
					applyLayerChanges();
				});
				layersLayout.addComponent(showCasesCheckBox);

				CheckBox showConfirmedContactsCheckBox = new CheckBox();
				CheckBox showUnconfirmedContactsCheckBox = new CheckBox();

				CheckBox showContactsCheckBox = new CheckBox();
				showContactsCheckBox.addStyleName(CssStyles.VSPACE_NONE);
				showContactsCheckBox.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, SHOW_CONTACTS));
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

				showConfirmedContactsCheckBox.addStyleName(CssStyles.VSPACE_NONE);
				showConfirmedContactsCheckBox.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, SHOW_CONFIRMED_CONTACTS));
				showConfirmedContactsCheckBox.setValue(showConfirmedContacts);
				showConfirmedContactsCheckBox.addValueChangeListener(e -> {
					showConfirmedContacts = (boolean) e.getProperty().getValue();
					applyLayerChanges();
				});
				layersLayout.addComponent(showConfirmedContactsCheckBox);	 

				showUnconfirmedContactsCheckBox.addStyleName(CssStyles.VSPACE_NONE);
				showUnconfirmedContactsCheckBox.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, SHOW_UNCONFIRMED_CONTACTS));
				showUnconfirmedContactsCheckBox.setValue(showUnconfirmedContacts);
				showUnconfirmedContactsCheckBox.addValueChangeListener(e -> {
					showUnconfirmedContacts = (boolean) e.getProperty().getValue();
					applyLayerChanges();
				});
				layersLayout.addComponent(showUnconfirmedContactsCheckBox);	

				showConfirmedContactsCheckBox.setEnabled(showContacts);
				showUnconfirmedContactsCheckBox.setEnabled(showContacts);
				
				if (LoginHelper.isUserInRole(UserRole.NATIONAL_USER)) {
					OptionGroup regionMapVisualizationSelect = new OptionGroup();
					regionMapVisualizationSelect.setWidth(100, Unit.PERCENTAGE);
					regionMapVisualizationSelect.addItems((Object[]) RegionMapVisualization.values());
					regionMapVisualizationSelect.setValue(regionMapVisualization);
					regionMapVisualizationSelect.addValueChangeListener(event -> {
						regionMapVisualization = (RegionMapVisualization) event.getProperty().getValue();
						applyLayerChanges();
					});

					CheckBox showRegionsCheckBox = new CheckBox();
					showRegionsCheckBox.addStyleName(CssStyles.VSPACE_NONE);
					showRegionsCheckBox.setCaption("Show regions");
					showRegionsCheckBox.setValue(showRegions);
					showRegionsCheckBox.addValueChangeListener(e -> {
						showRegions = (boolean) e.getProperty().getValue();
						regionMapVisualizationSelect.setEnabled(showRegions);
						regionMapVisualizationSelect.setValue(regionMapVisualization);
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

	private VerticalLayout createMapKeyLayout() {
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

		// Health facilities & cases
		if (showCases) {
			Label facilitiesKeyLabel = new Label("Health Facilities");
			CssStyles.style(facilitiesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			mapKeyLayout.addComponent(facilitiesKeyLabel);

			HorizontalLayout facilitiesKeyLayout = new HorizontalLayout();
			{
				facilitiesKeyLayout.setSpacing(false);
				HorizontalLayout legendEntry = createLegendEntry("mapicons/grey-house-small.png", "Only Not Yet Classified Cases");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				facilitiesKeyLayout.addComponent(legendEntry);
				legendEntry = createLegendEntry("mapicons/yellow-house-small.png", "> 1 Suspect Cases");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				facilitiesKeyLayout.addComponent(legendEntry);
				legendEntry = createLegendEntry("mapicons/orange-house-small.png", "> 1 Probable Cases");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				facilitiesKeyLayout.addComponent(legendEntry);
				legendEntry = createLegendEntry("mapicons/red-house-small.png", "> 1 Confirmed Cases");
				facilitiesKeyLayout.addComponent(legendEntry);
			}
			mapKeyLayout.addComponent(facilitiesKeyLayout);

			Label casesKeyLabel = new Label("Cases");
			CssStyles.style(casesKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			mapKeyLayout.addComponent(casesKeyLabel);

			HorizontalLayout casesKeyLayout = new HorizontalLayout();
			{
				casesKeyLayout.setSpacing(false);
				HorizontalLayout legendEntry = createLegendEntry("mapicons/grey-dot-small.png", "Not Yet Classified");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = createLegendEntry("mapicons/yellow-dot-small.png", "Suspect");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = createLegendEntry("mapicons/orange-dot-small.png", "Probable");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				casesKeyLayout.addComponent(legendEntry);
				legendEntry = createLegendEntry("mapicons/red-dot-small.png", "Confirmed");
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
				HorizontalLayout legendEntry = createLegendEntry("mapicons/green-contact.png", "Last Visit < 24h");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				contactsKeyLayout.addComponent(legendEntry);
				legendEntry = createLegendEntry("mapicons/orange-contact.png", "Last Visit < 48h");
				CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
				contactsKeyLayout.addComponent(legendEntry);
				legendEntry = createLegendEntry("mapicons/red-contact.png", "Last Visit > 48h or No Visit");
				contactsKeyLayout.addComponent(legendEntry);
			}
			mapKeyLayout.addComponent(contactsKeyLayout);
		}

		// Regions
		if (showRegions) {
			Label regionsKeyLabel = new Label("Regions");
			if (showCases || showContacts) {
				CssStyles.style(regionsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
			} else {
				CssStyles.style(regionsKeyLabel, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			}
			mapKeyLayout.addComponent(regionsKeyLabel);
			mapKeyLayout.addComponent(createRegionKeyLayout());
		}

		return mapKeyLayout;
	}

	private void toggleNoGPSButtonVisibility() {
		noGPSButton.setVisible(showCases);
	}
	
	private void applyLayerChanges() {
		// Refresh the map according to the selected layers
		refreshMap();
		// Re-create the map key layout to only show the keys for the selected layers
		mapKeyDropdown.setContent(createMapKeyLayout());
		// Show or hide the button to show cases without a GPS tag depending on whether the cases layer has been selected
		toggleNoGPSButtonVisibility();
	}

	private HorizontalLayout createRegionKeyLayout() {
		HorizontalLayout regionKeyLayout = new HorizontalLayout();
		
		HorizontalLayout legendEntry;
		switch (regionMapVisualization) {
			case CASE_COUNT:
				legendEntry = createLegendEntry("mapicons/yellow-region-small.png", "1 - 5 cases");
				break;
			case CASE_INCIDENCE:
				legendEntry = createLegendEntry("mapicons/yellow-region-small.png", "<= 0.5 cases / 10.000");
				break;
			default: throw new IllegalArgumentException(regionMapVisualization.toString());
		}
		
		regionKeyLayout.addComponent(legendEntry);
		regionKeyLayout.setComponentAlignment(legendEntry, Alignment.MIDDLE_LEFT);
		regionKeyLayout.setExpandRatio(legendEntry, 0);

		Label spacer = new Label();
		spacer.setWidth(6, Unit.PIXELS);
		regionKeyLayout.addComponent(spacer);

		switch (regionMapVisualization) {
			case CASE_COUNT:
				legendEntry = createLegendEntry("mapicons/orange-region-small.png", "6 - 10 cases");
				break;
			case CASE_INCIDENCE:
				legendEntry = createLegendEntry("mapicons/orange-region-small.png", "0.6 - 1 cases / 10.000");
				break;
			default: throw new IllegalArgumentException(regionMapVisualization.toString());
		}
		
		regionKeyLayout.addComponent(legendEntry);
		regionKeyLayout.setComponentAlignment(legendEntry, Alignment.MIDDLE_LEFT);
		regionKeyLayout.setExpandRatio(legendEntry, 0);

		spacer = new Label();
		spacer.setWidth(6, Unit.PIXELS);
		regionKeyLayout.addComponent(spacer);

		switch (regionMapVisualization) {
			case CASE_COUNT:
				legendEntry = createLegendEntry("mapicons/red-region-small.png", "> 10 cases");
				break;
			case CASE_INCIDENCE:
				legendEntry = createLegendEntry("mapicons/red-region-small.png", "> 1 cases / 10.000");
				break;
			default: throw new IllegalArgumentException(regionMapVisualization.toString());
		}	
		
		regionKeyLayout.addComponent(legendEntry);
		regionKeyLayout.setComponentAlignment(legendEntry, Alignment.MIDDLE_LEFT);
		regionKeyLayout.setExpandRatio(legendEntry, 1);
		
		return regionKeyLayout;
	}

	private HorizontalLayout createLegendEntry(String iconThemeResource, String labelCaption) {
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

	private void refreshDashboard() {
		// Update the cases and contacts lists according to the filters
		String userUuid = LoginHelper.getCurrentUser().getUuid();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if (dateFilterOption == DateFilterOptions.DATE) {
			cases = FacadeProvider.getCaseFacade().getAllCasesBetween(fromDate, toDate, district, disease, userUuid);
			contacts = FacadeProvider.getContactFacade().getMapContacts(fromDate, toDate, district, disease, userUuid);
		} else {
			cases = FacadeProvider.getCaseFacade().getAllCasesBetween(DateHelper.getEpiWeekStart(new EpiWeek(year, fromWeek)), 
					DateHelper.getEpiWeekEnd(new EpiWeek(year, toWeek)), district, disease, userUuid);
			contacts = FacadeProvider.getContactFacade().getMapContacts(DateHelper.getEpiWeekStart(new EpiWeek(year, fromWeek)), 
					DateHelper.getEpiWeekEnd(new EpiWeek(year, toWeek)), district, disease, userUuid);
		}

		// Update cases and contacts shown on the map
		refreshMap();
		
		// Update situation report and epi curve data
		if (dateFilterOption == DateFilterOptions.DATE) {
			situationReportTableLeft.clearAndFill(fromDate, toDate, district, disease, cases);
			situationReportTableRight.clearAndFill(fromDate, toDate, district, disease, cases);
		} else {
			situationReportTableLeft.clearAndFill(DateHelper.getEpiWeekStart(new EpiWeek(year, fromWeek)), DateHelper.getEpiWeekEnd(new EpiWeek(year, toWeek)), district, disease, cases);
			situationReportTableRight.clearAndFill(DateHelper.getEpiWeekStart(new EpiWeek(year, fromWeek)), DateHelper.getEpiWeekEnd(new EpiWeek(year, toWeek)), district, disease, cases);
		}
		
		// Update epi curve and map date labels
		updateHeaderDateLabel(epiCurveDateLabel);
		updateHeaderDateLabel(mapDateLabel);
		
		// Epi curve chart has to be created again due to a canvas resizing issue when simply refreshing the component
		//chartWrapper.removeComponent(epiCurveChart);
		clearAndFillEpiCurveChart();
		//chartWrapper.addComponent(epiCurveChart);
	}

	private void refreshMap() {

		mapComponent.clearRegionShapes();
		mapComponent.clearCaseMarkers();
		mapComponent.clearContactMarkers();

		if (showRegions) {
			mapComponent.showRegionsShapes(regionMapVisualization, 
					useDateFilterForMap ? fromDate : null, useDateFilterForMap ? toDate : null, disease);
		}

		if (showCases || showContacts) {
			List<CaseDataDto> casesForMap;
			List<ContactMapDto> contactsForMap;
			
			// Re-create the lists of cases and contacts depending on the filters set for the map
			if (useDistrictFilterForMap && useDiseaseFilterForMap && useDateFilterForMap) {
				casesForMap = cases;
				contactsForMap = contacts;
			} else {
				String userUuid = LoginHelper.getCurrentUser().getUuid();
				if (dateFilterOption == DateFilterOptions.DATE) {
					if (showCases) {
						casesForMap = FacadeProvider.getCaseFacade().getAllCasesBetween(useDateFilterForMap ? fromDate : null, useDateFilterForMap ? toDate : null,
								useDistrictFilterForMap ? district : null, useDiseaseFilterForMap ? disease : null, userUuid);
					} else {
						casesForMap = null;
					}
					if (showContacts) {
						contactsForMap = FacadeProvider.getContactFacade().getMapContacts(useDateFilterForMap ? fromDate : null, useDateFilterForMap ? toDate : null,
								useDistrictFilterForMap ? district : null, useDiseaseFilterForMap ? disease : null, userUuid);
					} else {
						contactsForMap = null;
					}
				} else {
					int year = Calendar.getInstance().get(Calendar.YEAR);
					if (showCases) {
						casesForMap = FacadeProvider.getCaseFacade().getAllCasesBetween(useDateFilterForMap ? DateHelper.getEpiWeekStart(new EpiWeek(year, fromWeek)) : null, 
								useDateFilterForMap ? DateHelper.getEpiWeekEnd(new EpiWeek(year, toWeek)) : null,
								useDistrictFilterForMap ? district : null, useDiseaseFilterForMap ? disease : null, userUuid);
					} else {
						casesForMap = null;
					}
					if (showContacts) {
						contactsForMap = FacadeProvider.getContactFacade().getMapContacts(useDateFilterForMap ? DateHelper.getEpiWeekStart(new EpiWeek(year, fromWeek)) : null, 
								useDateFilterForMap ? DateHelper.getEpiWeekEnd(new EpiWeek(year, toWeek)) : null,
								useDistrictFilterForMap ? district : null, useDiseaseFilterForMap ? disease : null, userUuid);
					} else {
						contactsForMap = null;
					}
				}
			}

			if (showCases) {
				mapComponent.showCaseMarkers(casesForMap);
			}
			if (showContacts) {
				mapComponent.showContactMarkers(contactsForMap, showConfirmedContacts, showUnconfirmedContacts);
			}
		}
	}

	/**
	 * Creates the epi curve chart using the Chart.js Vaadin addon
	 */
	private void createEpiCurveChart() {
		epiCurveChart = new HighChart();
		epiCurveChart.setSizeFull();
		clearAndFillEpiCurveChart();
	}

	private void clearAndFillEpiCurveChart() {
		StringBuilder hcjs = new StringBuilder();
		hcjs.append("var options = {"
				+ "chart: { type: 'column', backgroundColor: '#CDD8EC' },"//, events: { addSeries: function(event) {" + chartLoadFunction + "} } },"
				+ "credits: { enabled: false },"
				+ "title: { text: '' },");

		// Creates and sets the labels for each day on the x-axis
		List<Date> filteredDates = buildListOfFilteredDates();
		List<String> newLabels = new ArrayList<>();
		for (Date date : filteredDates) {
			String label = DateHelper.formatShortDate(date);
			newLabels.add(label);
		}

		hcjs.append("xAxis: { categories: [");
		for (String s : newLabels) {
			if (newLabels.indexOf(s) == newLabels.size() - 1) {
				hcjs.append("'" + s + "']},");
			} else {
				hcjs.append("'" + s + "', ");
			}
		}

		hcjs.append("yAxis: { min: 0, title: { text: 'Number of Cases' }, allowDecimals: false, softMax: 10, "
				+ "stackLabels: { enabled: true, "
				+ "style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },"
				+ "legend: { verticalAlign: 'top', backgroundColor: 'transparent', align: 'left', "
				+ "borderWidth: 0, shadow: false, margin: 30, padding: 0 },"
				+ "tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'},"
				+ "plotOptions: { column: { borderWidth: 0, stacking: 'normal', dataLabels: {"
				+ "enabled: true, formatter: function() { if (this.y > 0) return this.y; },"
				+ "color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white' } } },");

		// Adds the number of confirmed, probable and suspect cases for each day as data
		List<CaseDataDto> confirmedCases = cases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED)
				.collect(Collectors.toList());
		List<CaseDataDto> suspectedCases = cases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT)
				.collect(Collectors.toList());
		List<CaseDataDto> probableCases = cases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE)
				.collect(Collectors.toList());

		int[] confirmedNumbers = new int[newLabels.size()];
		int[] probableNumbers = new int[newLabels.size()];
		int[] suspectNumbers = new int[newLabels.size()];

		for (int i = 0; i < filteredDates.size(); i++) {
			Date date = filteredDates.get(i);
			int confirmedCasesAtDate = (int) confirmedCases.stream()
					.filter(c -> DateHelper.isSameDay(c.getSymptoms().getOnsetDate(), date))
					.count();
			confirmedNumbers[i] = confirmedCasesAtDate;
			int probableCasesAtDate = (int) probableCases.stream()
					.filter(c -> DateHelper.isSameDay(c.getSymptoms().getOnsetDate(), date))
					.count();
			probableNumbers[i] = probableCasesAtDate;
			int suspectCasesAtDate = (int) suspectedCases.stream()
					.filter(c -> DateHelper.isSameDay(c.getSymptoms().getOnsetDate(), date))
					.count();
			suspectNumbers[i] = suspectCasesAtDate;
		}

		hcjs.append("series: [");
		hcjs.append("{ name: 'Confirmed', color: '#B22222', dataLabels: { allowOverlap: false }, data: [");
		for (int i = 0; i < confirmedNumbers.length; i++) {
			if (i == confirmedNumbers.length - 1) {
				hcjs.append(confirmedNumbers[i] + "]},");
			} else {
				hcjs.append(confirmedNumbers[i] + ", ");
			}
		}
		hcjs.append("{ name: 'Probable', color: '#FF4500', dataLabels: { allowOverlap: false },  data: [");
		for (int i = 0; i < probableNumbers.length; i++) {
			if (i == probableNumbers.length - 1) {
				hcjs.append(probableNumbers[i] + "]},");
			} else {
				hcjs.append(probableNumbers[i] + ", ");
			}
		}
		hcjs.append("{ name: 'Suspect', color: '#FFD700', dataLabels: { allowOverlap: false },  data: [");
		for (int i = 0; i < suspectNumbers.length; i++) {
			if (i == suspectNumbers.length - 1) {
				hcjs.append(suspectNumbers[i] + "]}]};");
			} else {
				hcjs.append(suspectNumbers[i] + ", ");
			}
		}

		epiCurveChart.setHcjs(hcjs.toString());	
	}

	/**
	 * Builds a list that contains an object for each day between the from and to dates
	 * @return
	 */
	private List<Date> buildListOfFilteredDates() {
		List<Date> filteredDates = new ArrayList<>();
		if (dateFilterOption == DateFilterOptions.DATE) {
			Date currentDate = new Date(fromDate.getTime());
			while (!currentDate.after(toDate)) {
				filteredDates.add(currentDate);
				currentDate = DateHelper.addDays(currentDate, 1);
			}
		} else {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			Date currentDate = DateHelper.getEpiWeekStart(new EpiWeek(year, fromWeek));
			Date targetDate = DateHelper.getEpiWeekEnd(new EpiWeek(year, toWeek));
			while (!currentDate.after(targetDate)) {
				filteredDates.add(currentDate);
				currentDate = DateHelper.addDays(currentDate, 1);
			}
		}
		
		return filteredDates;
	}
	
	private void updateHeaderDateLabel(Label headerLabel) {
		if (dateFilterOption == DateFilterOptions.EPI_WEEK) {
			headerLabel.setValue("FOR WEEK " + fromWeek + (toWeek != fromWeek ? " TO WEEK " + toWeek : ""));
		} else {
			headerLabel.setValue("FROM " + DateHelper.formatShortDate(fromDate) + 
					(DateHelper.isSameDay(fromDate, toDate) ? " TO " + DateHelper.formatShortDate(toDate) : ""));
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		refreshDashboard();
	}

}
