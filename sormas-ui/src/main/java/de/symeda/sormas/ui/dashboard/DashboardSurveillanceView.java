package de.symeda.sormas.ui.dashboard;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class DashboardSurveillanceView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/surveillance";

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
	private VerticalLayout mapLayout;
	private VerticalLayout epiCurveLayout;
	private HorizontalLayout epiCurveAndMapLayout;
	private MapComponent mapComponent;
	private EpiCurveComponent epiCurveComponent;
	private DashboardSurveillanceStatisticsComponent dashboardSurveillanceStatisticsComponent;

	public DashboardSurveillanceView() {
		super(VIEW_NAME, DashboardType.SURVEILLANCE);	

		filterLayout.setInfoLabelText("All Dashboard elements that display cases (the 'New Cases' statistics, the Epidemiological Curve and the Case Status Map) use the onset date of the first symptom for the date/epi week filter. If this date is not available, the reception date or date of report is used instead.");

		// Add statistics
		dashboardSurveillanceStatisticsComponent = new DashboardSurveillanceStatisticsComponent(dashboardDataProvider);
		dashboardLayout.addComponent(dashboardSurveillanceStatisticsComponent);

		// Add epi curve and map
		epiCurveAndMapLayout = createEpiCurveAndMapLayout();
		dashboardLayout.addComponent(epiCurveAndMapLayout);
		dashboardLayout.setExpandRatio(epiCurveAndMapLayout, 1);
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
		layout.setHeight(400, Unit.PIXELS);

		epiCurveComponent = new EpiCurveComponent(dashboardDataProvider);
		epiCurveComponent.setSizeFull();

		layout.addComponent(epiCurveComponent);
		layout.setExpandRatio(epiCurveComponent, 1);

		epiCurveComponent.setExpandListener(e -> {
			dashboardLayout.removeComponent(dashboardSurveillanceStatisticsComponent);
			epiCurveAndMapLayout.removeComponent(mapLayout);
			DashboardSurveillanceView.this.setHeight(100, Unit.PERCENTAGE);
			epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
			epiCurveLayout.setSizeFull();			
		});

		epiCurveComponent.setCollapseListener(e -> {
			dashboardLayout.addComponent(dashboardSurveillanceStatisticsComponent, 1);
			epiCurveAndMapLayout.addComponent(mapLayout, 1);
			epiCurveLayout.setHeight(400, Unit.PIXELS);
			DashboardSurveillanceView.this.setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
		});

		return layout;
	}

	private VerticalLayout createMapLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeight(400, Unit.PIXELS);

		mapComponent = new MapComponent(dashboardDataProvider);
		mapComponent.setSizeFull();

		layout.addComponent(mapComponent);
		layout.setExpandRatio(mapComponent, 1);

		mapComponent.setExpandListener(e -> {
			dashboardLayout.removeComponent(dashboardSurveillanceStatisticsComponent);
			epiCurveAndMapLayout.removeComponent(epiCurveLayout);
			DashboardSurveillanceView.this.setHeight(100, Unit.PERCENTAGE);
			epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
			mapLayout.setSizeFull();
		});

		mapComponent.setCollapseListener(e -> {
			dashboardLayout.addComponent(dashboardSurveillanceStatisticsComponent, 1);
			epiCurveAndMapLayout.addComponent(epiCurveLayout, 0);
			mapLayout.setHeight(400, Unit.PIXELS);
			DashboardSurveillanceView.this.setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
		});

		return layout;
	}

	@Override
	public void refreshDashboard() {		
		dashboardDataProvider.refreshData();

		// Updates statistics
		dashboardSurveillanceStatisticsComponent.updateStatistics(dashboardDataProvider.getDisease());

		// Update cases and contacts shown on the map
		mapComponent.refreshMap();

		// Epi curve chart has to be created again due to a canvas resizing issue when simply refreshing the component
		epiCurveComponent.clearAndFillEpiCurveChart();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		refreshDashboard();
	}

}
