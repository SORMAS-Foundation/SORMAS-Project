package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
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
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactMapDto;
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

	// Layouts and Components
	private VerticalLayout mapLayout;
	private VerticalLayout epiCurveLayout;
	private HorizontalLayout epiCurveAndMapLayout;
	private MapComponent mapComponent;
	private EpiCurveComponent epiCurveComponent;
	private StatisticsComponent statisticsComponent;

	// Entities
	private List<CaseDataDto> cases = new ArrayList<>();
	private List<ContactMapDto> contacts = new ArrayList<>();

	// Filters
	private DistrictReferenceDto district;
	private Disease disease;
	private DateFilterOption dateFilterOption;
	private Date fromDate = DateHelper.subtractDays(new Date(), 7);
	private Date toDate = new Date();
	private EpiWeek fromWeek;
	private EpiWeek toWeek;

	// Others
	private int thisYear;

	public DashboardView() {
		super(VIEW_NAME);		
		addStyleName("dashboard-screen");		

		thisYear = Calendar.getInstance().get(Calendar.YEAR);

		VerticalLayout dashboardLayout = new VerticalLayout();
		dashboardLayout.setSpacing(false);
		dashboardLayout.setSizeFull();
		dashboardLayout.setStyleName("crud-main-layout");

		// Add filter bar
		dashboardLayout.addComponent(createFilterBar());

		// Add statistics
		statisticsComponent = new StatisticsComponent(this);
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
				dateLabel.setValue("IN EPI WEEK " + fromWeek.getWeek());
			} else {
				dateLabel.setValue("FROM EPI WEEK " + fromWeek.getWeek() + " TO " + toWeek.getWeek());
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

	public void expandMap() {
		epiCurveAndMapLayout.removeComponent(epiCurveLayout);
		this.setHeight(100, Unit.PERCENTAGE);
		epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
		mapLayout.setSizeFull();
	}

	public void collapseMap() {
		epiCurveAndMapLayout.addComponent(epiCurveLayout, 0);
		mapLayout.setHeight(380, Unit.PIXELS);
		this.setHeightUndefined();
		epiCurveAndMapLayout.setHeightUndefined();
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
				disease = (Disease) diseaseFilter.getValue();
				dateFilterOption = (DateFilterOption) dateFilterOptionFilter.getValue();
				if (dateFilterOption == DateFilterOption.DATE) {
					fromDate = dateFromFilter.getValue();
					toDate = dateToFilter.getValue();
				} else {
					fromWeek = new EpiWeek(thisYear, (int) weekFromFilter.getValue());
					toWeek = new EpiWeek(thisYear, (int) weekToFilter.getValue());
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
				weekFromFilter.setValue(DateHelper.getEpiWeek(c.getTime()).getWeek());
				filterLayout.addComponent(weekToFilter, filterLayout.getComponentIndex(weekFromFilter) + 1);
				weekToFilter.setValue(DateHelper.getEpiWeek(c.getTime()).getWeek());
			}
		});
		filterLayout.addComponent(dateFilterOptionFilter);
		dateFilterOption = (DateFilterOption) dateFilterOptionFilter.getValue();

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
		setFromWeek((int) weekFromFilter.getValue());

		weekToFilter.setWidth(200, Unit.PIXELS);
		weekToFilter.addItems(DateHelper.createWeeksList(c.get(Calendar.YEAR)));
		weekToFilter.setNullSelectionAllowed(false);
		weekToFilter.setValue(DateHelper.getEpiWeek(c.getTime()).getWeek());
		weekToFilter.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, TO_WEEK));
		weekToFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});
		filterLayout.addComponent(weekToFilter);
		setToWeek((int) weekToFilter.getValue());

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

	private HorizontalLayout createEpiCurveAndMapLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName("curve-and-map-layout");
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

		epiCurveComponent = new EpiCurveComponent(this);
		epiCurveComponent.setSizeFull();
		
		layout.addComponent(epiCurveComponent);
		layout.setExpandRatio(epiCurveComponent, 1);

		return layout;
	}

	private VerticalLayout createMapLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeight(360, Unit.PIXELS);

		mapComponent = new MapComponent(this);
		mapComponent.setSizeFull();
		
		layout.addComponent(mapComponent);
		layout.setExpandRatio(mapComponent, 1);

		return layout;
	}

	private void refreshDashboard() {
		// Update the cases and contacts lists according to the filters
		String userUuid = LoginHelper.getCurrentUser().getUuid();
		if (dateFilterOption == DateFilterOption.DATE) {
			cases = FacadeProvider.getCaseFacade().getAllCasesBetween(fromDate, toDate, district, disease, userUuid);
			contacts = FacadeProvider.getContactFacade().getMapContacts(fromDate, toDate, district, disease, userUuid);
		} else {
			cases = FacadeProvider.getCaseFacade().getAllCasesBetween(DateHelper.getEpiWeekStart(fromWeek), 
					DateHelper.getEpiWeekEnd(toWeek), district, disease, userUuid);
			contacts = FacadeProvider.getContactFacade().getMapContacts(DateHelper.getEpiWeekStart(fromWeek), 
					DateHelper.getEpiWeekEnd(toWeek), district, disease, userUuid);
		}

		// Updates statistics
		statisticsComponent.updateStatistics();
		
		// Update cases and contacts shown on the map
		mapComponent.refreshMap();

		// Update epi curve and map date labels
		updateDateLabel(epiCurveComponent.getEpiCurveDateLabel());
		updateDateLabel(mapComponent.getMapDateLabel());

		// Epi curve chart has to be created again due to a canvas resizing issue when simply refreshing the component
		epiCurveComponent.clearAndFillEpiCurveChart();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		refreshDashboard();
	}

	public List<CaseDataDto> getCases() {
		return cases;
	}
	
	public List<ContactMapDto> getContacts() {
		return contacts;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public EpiWeek getFromWeek() {
		return fromWeek;
	}

	public EpiWeek getToWeek() {
		return toWeek;
	}
	
	public Disease getDisease() {
		return disease;
	}
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}

	private void setFromWeek(int week) {
		fromWeek = new EpiWeek(thisYear, week);
	}

	private void setToWeek(int week) {
		toWeek = new EpiWeek(thisYear, week);
	}

}
