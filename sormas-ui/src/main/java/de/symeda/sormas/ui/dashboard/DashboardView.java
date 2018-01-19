package de.symeda.sormas.ui.dashboard;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

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
	public static final String EVENTS = "events";
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

	// Layouts and Components
	private VerticalLayout dashboardLayout;
	private VerticalLayout mapLayout;
	private VerticalLayout epiCurveLayout;
	private HorizontalLayout epiCurveAndMapLayout;
	private MapComponent mapComponent;
	private EpiCurveComponent epiCurveComponent;
	private StatisticsComponent statisticsComponent;

	// Filters
	private DistrictReferenceDto district;
	private Disease disease;
	private DateFilterOption dateFilterOption;
	private Date fromDate = DateHelper.subtractDays(new Date(), 7);
	private Date toDate = new Date();
	private EpiWeek fromWeek;
	private EpiWeek toWeek;

	// Others
	private DashboardDataProvider dashboardDataProvider;

	public DashboardView() {
		super(VIEW_NAME);		
		addStyleName(DashboardCssStyles.DASHBOARD_SCREEN);		

		dashboardDataProvider = new DashboardDataProvider();

		dashboardLayout = new VerticalLayout();
		dashboardLayout.setSpacing(false);
		dashboardLayout.setSizeFull();
		dashboardLayout.setStyleName("crud-main-layout");

		// Add filter bar
		dashboardLayout.addComponent(createFilterBar());

		// Add statistics
		statisticsComponent = new StatisticsComponent(dashboardDataProvider);
		dashboardLayout.addComponent(statisticsComponent);

		// Add epi curve and map
		epiCurveAndMapLayout = createEpiCurveAndMapLayout();
		dashboardLayout.addComponent(epiCurveAndMapLayout);
		dashboardLayout.setExpandRatio(epiCurveAndMapLayout, 1);

		addComponent(dashboardLayout);
	}
	
	public void updateDateLabel(Label dateLabel) {
		if (dateFilterOption == DateFilterOption.EPI_WEEK) {
			if (fromWeek.getWeek() == toWeek.getWeek()) {
				dateLabel.setValue("EPI WEEK " + fromWeek.getWeek());
			} else {
				dateLabel.setValue("EPI WEEK " + fromWeek.getWeek() + " TO " + toWeek.getWeek());
			}
		} else {
			if (DateHelper.isSameDay(fromDate, toDate)) {
				dateLabel.setValue("ON " + DateHelper.formatShortDate(fromDate));
			} else {
				dateLabel.setValue("FROM " + DateHelper.formatShortDate(fromDate) + 
						" TO " + DateHelper.formatShortDate(toDate));
			}
		}
	}

	private HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName(CssStyles.VSPACE_3);
		filterLayout.setMargin(new MarginInfo(true, true, false, true));

		ComboBox districtFilter = new ComboBox();
		ComboBox diseaseFilter = new ComboBox();
		ComboBox dateFilterOptionFilter = new ComboBox();
		DateField dateFromFilter = new DateField();
		DateField dateToFilter = new DateField();
		ComboBox weekFromFilter = new ComboBox();
		ComboBox weekToFilter = new ComboBox();

		// 'Apply Filter' button
		Button applyButton = new Button(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, APPLY));
		CssStyles.style(applyButton, CssStyles.FORCE_CAPTION);
		applyButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				district = (DistrictReferenceDto) districtFilter.getValue();
				dashboardDataProvider.setDistrict(district);
				disease = (Disease) diseaseFilter.getValue();
				dashboardDataProvider.setDisease(disease);
				dateFilterOption = (DateFilterOption) dateFilterOptionFilter.getValue();
				dashboardDataProvider.setDateFilterOption(dateFilterOption);
				if (dateFilterOption == DateFilterOption.DATE) {
					fromDate = dateFromFilter.getValue();
					dashboardDataProvider.setFromDate(fromDate);
					toDate = dateToFilter.getValue();
					dashboardDataProvider.setToDate(toDate);
				} else {
					fromWeek = (EpiWeek) weekFromFilter.getValue();
					dashboardDataProvider.setFromWeek(fromWeek);
					toWeek = (EpiWeek) weekToFilter.getValue();
					dashboardDataProvider.setToWeek(toWeek);
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
			dashboardDataProvider.setDistrict(district);
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

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());

		// Date filter options
		dateFilterOptionFilter.setWidth(200, Unit.PIXELS);
		CssStyles.style(dateFilterOptionFilter, CssStyles.FORCE_CAPTION);
		dateFilterOptionFilter.addItems((Object[])DateFilterOption.values());
		dateFilterOptionFilter.setNullSelectionAllowed(false);
		dateFilterOptionFilter.select(DateFilterOption.EPI_WEEK);
		dateFilterOptionFilter.addValueChangeListener(e -> {
			if (e.getProperty().getValue() == DateFilterOption.DATE) {
				filterLayout.removeComponent(weekFromFilter);
				filterLayout.removeComponent(weekToFilter);
				filterLayout.addComponent(dateFromFilter, filterLayout.getComponentIndex(dateFilterOptionFilter) + 1);
				dateFromFilter.setValue(DateHelper.subtractDays(c.getTime(), 7));
				filterLayout.addComponent(dateToFilter, filterLayout.getComponentIndex(dateFromFilter) + 1);
				dateToFilter.setValue(c.getTime());
			} else {
				filterLayout.removeComponent(dateFromFilter);
				filterLayout.removeComponent(dateToFilter);
				filterLayout.addComponent(weekFromFilter, filterLayout.getComponentIndex(dateFilterOptionFilter) + 1);
				weekFromFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
				filterLayout.addComponent(weekToFilter, filterLayout.getComponentIndex(weekFromFilter) + 1);
				weekToFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
			}
		});
		filterLayout.addComponent(dateFilterOptionFilter);
		dateFilterOption = (DateFilterOption) dateFilterOptionFilter.getValue();
		dashboardDataProvider.setDateFilterOption(dateFilterOption);

		// Epi week filter
		List<EpiWeek> epiWeekList = DateHelper.createEpiWeekList(c.get(Calendar.YEAR), c.get(Calendar.WEEK_OF_YEAR));
		
		weekFromFilter.setWidth(200, Unit.PIXELS);
		for (EpiWeek week : epiWeekList) {
			weekFromFilter.addItem(week);
		}
		weekFromFilter.setNullSelectionAllowed(false);
		weekFromFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
		weekFromFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, FROM_WEEK));
		weekFromFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});
		filterLayout.addComponent(weekFromFilter);
		dashboardDataProvider.setFromWeek((EpiWeek) weekFromFilter.getValue());
		
		weekToFilter.setWidth(200, Unit.PIXELS);
		for (EpiWeek week : epiWeekList) {
			weekToFilter.addItem(week);
		}
		weekToFilter.setNullSelectionAllowed(false);
		weekToFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
		weekToFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, TO_WEEK));
		weekToFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});
		filterLayout.addComponent(weekToFilter);
		dashboardDataProvider.setToWeek((EpiWeek) weekToFilter.getValue());
		
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
		
		Label infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription("All Dashboard elements that display cases (the 'New Cases' statistics, the Epidemiological Curve and the Case Status Map) use the onset date of the first symptom for the date/epi week filter. If this date is not available, the date of report is used instead.");
		CssStyles.style(infoLabel, CssStyles.SIZE_XLARGE, CssStyles.COLOR_SECONDARY);
		filterLayout.addComponent(infoLabel);
		filterLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_RIGHT);

		return filterLayout;
	}

	private HorizontalLayout createEpiCurveAndMapLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName(DashboardCssStyles.CURVE_AND_MAP_LAYOUT);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);

		// Epi curve layout
		epiCurveLayout = createEpiCurveLayout();
		layout.addComponent(epiCurveLayout);

		// Map layout	
		mapLayout = createMapLayout();
		layout.addComponent(mapLayout);

		return layout;
	}

	private VerticalLayout createEpiCurveLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeight(380, Unit.PIXELS);

		epiCurveComponent = new EpiCurveComponent(dashboardDataProvider);
		epiCurveComponent.setSizeFull();
		
		layout.addComponent(epiCurveComponent);
		layout.setExpandRatio(epiCurveComponent, 1);
		
		epiCurveComponent.setExpandListener(e -> {
			dashboardLayout.removeComponent(statisticsComponent);
			epiCurveAndMapLayout.removeComponent(mapLayout);
			DashboardView.this.setHeight(100, Unit.PERCENTAGE);
			epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
			epiCurveLayout.setSizeFull();			
		});
		
		epiCurveComponent.setCollapseListener(e -> {
			dashboardLayout.addComponent(statisticsComponent, 1);
			epiCurveAndMapLayout.addComponent(mapLayout, 1);
			epiCurveLayout.setHeight(380, Unit.PIXELS);
			DashboardView.this.setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
		});

		return layout;
	}

	private VerticalLayout createMapLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeight(380, Unit.PIXELS);

		mapComponent = new MapComponent(dashboardDataProvider);
		mapComponent.setSizeFull();
		
		layout.addComponent(mapComponent);
		layout.setExpandRatio(mapComponent, 1);

		mapComponent.setExpandListener(e -> {
			dashboardLayout.removeComponent(statisticsComponent);
			epiCurveAndMapLayout.removeComponent(epiCurveLayout);
			DashboardView.this.setHeight(100, Unit.PERCENTAGE);
			epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
			mapLayout.setSizeFull();
		});

		mapComponent.setCollapseListener(e -> {
			dashboardLayout.addComponent(statisticsComponent, 1);
			epiCurveAndMapLayout.addComponent(epiCurveLayout, 0);
			mapLayout.setHeight(380, Unit.PIXELS);
			DashboardView.this.setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
		});

		return layout;
	}

	private void refreshDashboard() {		
		dashboardDataProvider.refreshData();
		
		// Updates statistics
		statisticsComponent.updateStatistics(disease);
		
		// Update cases and contacts shown on the map
		mapComponent.refreshMap();

		// Epi curve chart has to be created again due to a canvas resizing issue when simply refreshing the component
		epiCurveComponent.clearAndFillEpiCurveChart();
		
		// Update epi curve and map date labels
		epiCurveComponent.updateDateLabel();
		mapComponent.updateDateLabel();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		refreshDashboard();
	}

}
