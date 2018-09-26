package de.symeda.sormas.ui.dashboard;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;

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
	private DashboardStatisticsComponent dashboardStatisticsComponent;

	// Filters
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	private DateFilterOption dateFilterOption;
	private Date fromDate = DateHelper.subtractDays(new Date(), 7);
	private Date toDate = new Date();
	private EpiWeek fromWeek;
	private EpiWeek toWeek;
	private Set<Button> dateFilterButtons;
	private PopupButton customButton;

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
		dateFilterButtons = new HashSet<>();
		dashboardLayout.addComponent(createFilterBar());

		// Add statistics
		dashboardStatisticsComponent = new DashboardStatisticsComponent(dashboardDataProvider);
		dashboardLayout.addComponent(dashboardStatisticsComponent);

		// Add epi curve and map
		epiCurveAndMapLayout = createEpiCurveAndMapLayout();
		dashboardLayout.addComponent(epiCurveAndMapLayout);
		dashboardLayout.setExpandRatio(epiCurveAndMapLayout, 1);

		addComponent(dashboardLayout);
	}

	private HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName(CssStyles.VSPACE_3);
		filterLayout.setMargin(new MarginInfo(true, true, false, true));

		ComboBox regionFilter = new ComboBox();
		ComboBox districtFilter = new ComboBox();
		ComboBox diseaseFilter = new ComboBox();

		// Region filter
		if (LoginHelper.getCurrentUser().getRegion() == null) {
			regionFilter.setWidth(200, Unit.PIXELS);
			regionFilter.setInputPrompt("State");
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
			regionFilter.addValueChangeListener(e -> {
				region = (RegionReferenceDto) regionFilter.getValue();
				dashboardDataProvider.setRegion(region);
				refreshDashboard();
			});
			regionFilter.setCaption("State");
			filterLayout.addComponent(regionFilter);
			region = (RegionReferenceDto) regionFilter.getValue();
			dashboardDataProvider.setRegion(region);
		}

		// District filter
		if (LoginHelper.getCurrentUser().getRegion() != null) {
			districtFilter.setWidth(200, Unit.PIXELS);
			districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DISTRICT));
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(LoginHelper.getCurrentUser().getRegion().getUuid()));
			districtFilter.addValueChangeListener(e -> {
				district = (DistrictReferenceDto) districtFilter.getValue();
				dashboardDataProvider.setDistrict(district);
				refreshDashboard();
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
			disease = (Disease) diseaseFilter.getValue();
			dashboardDataProvider.setDisease(disease);
			refreshDashboard();
		});
		diseaseFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DISEASE));
		filterLayout.addComponent(diseaseFilter);

		// Date filters
		HorizontalLayout dateFilterLayout = new HorizontalLayout();
		dateFilterLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
		dateFilterLayout.setSpacing(true);
		filterLayout.addComponent(dateFilterLayout);
		Date now = new Date();

		Button todayButton = new Button("Today");
		initializeDateFilterButton(todayButton);
		todayButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfDay(now), DateHelper.getEndOfDay(now));
			refreshDashboard();
		});
		Button yesterdayButton = new Button("Yesterday");
		initializeDateFilterButton(yesterdayButton);
		yesterdayButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfDay(DateHelper.subtractDays(now, 1)),
					DateHelper.getEndOfDay(DateHelper.subtractDays(now, 1)));
			refreshDashboard();
		});
		Button thisWeekButton = new Button("This week");
		initializeDateFilterButton(thisWeekButton);
		thisWeekButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfWeek(now), DateHelper.getEndOfWeek(now));
			refreshDashboard();
		});
		CssStyles.style(thisWeekButton, CssStyles.LINK_HIGHLIGHTED_DARK);
		CssStyles.removeStyles(thisWeekButton, CssStyles.LINK_HIGHLIGHTED_LIGHT);
		Button lastWeekButton = new Button("Last week");
		initializeDateFilterButton(lastWeekButton);
		lastWeekButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfWeek(DateHelper.subtractWeeks(now, 1)),
					DateHelper.getEndOfWeek(DateHelper.subtractWeeks(now, 1)));
			refreshDashboard();
		});
		Button thisYearButton = new Button("This year");
		initializeDateFilterButton(thisYearButton);
		thisYearButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfYear(now), DateHelper.getEndOfYear(now));
			refreshDashboard();
		});
		Button lastYearButton = new Button("Last year");
		initializeDateFilterButton(lastYearButton);
		lastYearButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfYear(DateHelper.subtractYears(now, 1)),
					DateHelper.getEndOfYear(DateHelper.subtractYears(now, 1)));
			refreshDashboard();
		});
		customButton = new PopupButton("Custom");
		initializeDateFilterButton(customButton);

		// Custom filter
		{
			HorizontalLayout customDateFilterLayout = new HorizontalLayout();
			customDateFilterLayout.setSpacing(true);
			customDateFilterLayout.setMargin(true);

			// 'Apply custom filter' button
			Button applyButton = new Button("Apply custom filter");
			CssStyles.style(applyButton, CssStyles.FORCE_CAPTION, ValoTheme.BUTTON_PRIMARY);

			// Date & Epi Week filter
			EpiWeekAndDateFilterComponent weekAndDateFilter = new EpiWeekAndDateFilterComponent(applyButton, true, true, false);
			customDateFilterLayout.addComponent(weekAndDateFilter);
			dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			dashboardDataProvider.setDateFilterOption(dateFilterOption);
			fromWeek = (EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue();
			dashboardDataProvider.setFromDate(DateHelper.getEpiWeekStart(fromWeek));
			toWeek = (EpiWeek) weekAndDateFilter.getWeekToFilter().getValue();
			dashboardDataProvider.setToDate(DateHelper.getEpiWeekEnd(toWeek));

			customDateFilterLayout.addComponent(applyButton);

			// Apply button listener
			applyButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
					dashboardDataProvider.setDateFilterOption(dateFilterOption);
					if (dateFilterOption == DateFilterOption.DATE) {
						fromDate = weekAndDateFilter.getDateFromFilter().getValue();
						dashboardDataProvider.setFromDate(fromDate);
						toDate = weekAndDateFilter.getDateToFilter().getValue();
						dashboardDataProvider.setToDate(toDate);
					} else {
						fromWeek = (EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue();
						dashboardDataProvider.setFromDate(DateHelper.getEpiWeekStart(fromWeek));
						toWeek = (EpiWeek) weekAndDateFilter.getWeekToFilter().getValue();
						dashboardDataProvider.setToDate(DateHelper.getEpiWeekEnd(toWeek));
					}

					if (fromDate != null && toDate != null) {
						changeDateFilterButtonsStyles(customButton);
						refreshDashboard();
						if (dateFilterOption == DateFilterOption.DATE) {
							customButton.setCaption(DateHelper.formatLocalShortDate(fromDate) + " - " + DateHelper.formatLocalShortDate(toDate));
						} else {
							customButton.setCaption(fromWeek.toShortString() + " - " + toWeek.toShortString());
						}
					} else {
						if (dateFilterOption == DateFilterOption.DATE) {
							new Notification("Missing date filter", "Please fill in both date filter fields", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
						} else {
							new Notification("Missing epi week filter", "Please fill in both epi week filter fields", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
						}
					}
				}
			});

			customButton.setContent(customDateFilterLayout);
		}

		dateFilterLayout.addComponents(todayButton, yesterdayButton, thisWeekButton, lastWeekButton,
				thisYearButton, lastYearButton, customButton);

		Label infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription("All Dashboard elements that display cases (the 'New Cases' statistics, the Epidemiological Curve and the Case Status Map) use the onset date of the first symptom for the date/epi week filter. If this date is not available, the reception date or date of report is used instead.");
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
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
		layout.setHeight(400, Unit.PIXELS);

		epiCurveComponent = new EpiCurveComponent(dashboardDataProvider);
		epiCurveComponent.setSizeFull();

		layout.addComponent(epiCurveComponent);
		layout.setExpandRatio(epiCurveComponent, 1);

		epiCurveComponent.setExpandListener(e -> {
			dashboardLayout.removeComponent(dashboardStatisticsComponent);
			epiCurveAndMapLayout.removeComponent(mapLayout);
			DashboardView.this.setHeight(100, Unit.PERCENTAGE);
			epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
			epiCurveLayout.setSizeFull();			
		});

		epiCurveComponent.setCollapseListener(e -> {
			dashboardLayout.addComponent(dashboardStatisticsComponent, 1);
			epiCurveAndMapLayout.addComponent(mapLayout, 1);
			epiCurveLayout.setHeight(400, Unit.PIXELS);
			DashboardView.this.setHeightUndefined();
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
			dashboardLayout.removeComponent(dashboardStatisticsComponent);
			epiCurveAndMapLayout.removeComponent(epiCurveLayout);
			DashboardView.this.setHeight(100, Unit.PERCENTAGE);
			epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
			mapLayout.setSizeFull();
		});

		mapComponent.setCollapseListener(e -> {
			dashboardLayout.addComponent(dashboardStatisticsComponent, 1);
			epiCurveAndMapLayout.addComponent(epiCurveLayout, 0);
			mapLayout.setHeight(400, Unit.PIXELS);
			DashboardView.this.setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
		});

		return layout;
	}

	private void refreshDashboard() {		
		dashboardDataProvider.refreshData();

		// Updates statistics
		dashboardStatisticsComponent.updateStatistics(disease);

		// Update cases and contacts shown on the map
		mapComponent.refreshMap();

		// Epi curve chart has to be created again due to a canvas resizing issue when simply refreshing the component
		epiCurveComponent.clearAndFillEpiCurveChart();
	}

	private void initializeDateFilterButton(Button button) {
		if (button != customButton) {
			button.addClickListener(e -> {
				changeDateFilterButtonsStyles(button);
			});
		}
		CssStyles.style(button, ValoTheme.BUTTON_LINK, CssStyles.LINK_HIGHLIGHTED, CssStyles.LINK_HIGHLIGHTED_LIGHT, CssStyles.FORCE_CAPTION);
		dateFilterButtons.add(button);
	}

	private void setDateFilter(Date from, Date to) {
		dateFilterOption = DateFilterOption.DATE;
		dashboardDataProvider.setDateFilterOption(DateFilterOption.DATE);
		fromDate = from;
		dashboardDataProvider.setFromDate(fromDate);
		toDate = to;
		dashboardDataProvider.setToDate(toDate);
	}

	private void changeDateFilterButtonsStyles(Button activeFilterButton) {
		CssStyles.style(activeFilterButton, CssStyles.LINK_HIGHLIGHTED_DARK);
		CssStyles.removeStyles(activeFilterButton, CssStyles.LINK_HIGHLIGHTED_LIGHT);
		if (customButton != activeFilterButton) {
			customButton.setCaption("Custom");
		}

		dateFilterButtons.forEach(b -> {
			if (b != activeFilterButton) {
				CssStyles.style(b, CssStyles.LINK_HIGHLIGHTED_LIGHT);
				CssStyles.removeStyles(b, CssStyles.LINK_HIGHLIGHTED_DARK);
			}
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		refreshDashboard();
	}

}
