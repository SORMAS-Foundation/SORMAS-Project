package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardView extends AbstractView {

	public static final String VIEW_NAME = "dashboard";
	
	public static final String I18N_PREFIX = "Dashboard";
	public static final String FROM = "from";
	public static final String TO = "to";
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
	public static final String DISEASE = "disease";
	public static final String NOT_YET_CLASSIFIED = "notYetClassified";
	public static final String CONFIRMED = "confirmed";
	public static final String PROBABLE = "probable";
	public static final String SUSPECT = "suspect";
	public static final String EPI_CURVE = "epiCurve";
	public static final String SITUATION_REPORT = "situationReport";
	public static final String CASE_MAP = "caseMap";
	
	private final MapComponent mapComponent;
	private Table situationReportTable;
	private CssLayout chartWrapper;
	private HighChart epiCurveChart;

	private List<CaseDataDto> cases = new ArrayList<>();
	
	private Date fromDate;
	private Date toDate;
	private Disease disease;
	private boolean useDateFilterForMap;

	public DashboardView() {
		setSizeFull();
		addStyleName("crud-view");

        // Initialize case list with the pre-selected data
		cases = FacadeProvider.getCaseFacade().getAllCasesBetween(fromDate, toDate, disease, LoginHelper.getCurrentUser().getUuid());
		
		mapComponent = new MapComponent();
		
		VerticalLayout dashboardLayout = new VerticalLayout();
        dashboardLayout.setSpacing(false);
        dashboardLayout.setSizeFull();
        dashboardLayout.setStyleName("crud-main-layout");
        dashboardLayout.setMargin(true);
        
        dashboardLayout.addComponent(createTopBar());
        dashboardLayout.addComponent(createFilterBar());
		VerticalLayout contentLayout = createContents();
        dashboardLayout.addComponent(contentLayout);
        dashboardLayout.setExpandRatio(contentLayout, 1);
        
        addComponent(dashboardLayout);
	}
	
	private HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.addStyleName(CssStyles.VSPACE_NO_FILTERS);
		
		Label header = new Label("Dashboard");
    	header.setSizeUndefined();
    	CssStyles.style(header, CssStyles.H2, CssStyles.NO_MARGIN);
    	topLayout.addComponent(header);
		
		return topLayout;
	}
	
	private HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName(CssStyles.VSPACE3);
        
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		
		DateField dateFromFilter = new DateField();
		dateFromFilter.setDateFormat(DateHelper.getShortDateFormat().toLocalizedPattern());
        dateFromFilter.setWidth(200, Unit.PIXELS);
        dateFromFilter.setValue(DateHelper.subtractDays(c.getTime(), 28));
        dateFromFilter.setDateFormat(DateHelper.getShortDateFormat().toPattern());
        dateFromFilter.addValueChangeListener(e -> {
        	fromDate = dateFromFilter.getValue();
        	refreshDashboard();
        });
        filterLayout.addComponent(dateFromFilter);
        fromDate = dateFromFilter.getValue();
        
        DateField dateToFilter = new DateField();
		dateToFilter.setDateFormat(DateHelper.getShortDateFormat().toLocalizedPattern());
        dateToFilter.setWidth(200, Unit.PIXELS);
        dateToFilter.setValue(c.getTime());
        dateToFilter.setDateFormat(DateHelper.getShortDateFormat().toPattern());
        dateToFilter.addValueChangeListener(e -> {
        	toDate = dateToFilter.getValue();
        	refreshDashboard();
        });
        filterLayout.addComponent(dateToFilter);
        toDate = dateToFilter.getValue();
        
        CheckBox dateFilterForMap = new CheckBox();
        dateFilterForMap.setCaption(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DATE_FILTER_FOR_MAP));
        dateFilterForMap.addValueChangeListener(e -> {
        	useDateFilterForMap = dateFilterForMap.getValue();
        	refreshMap();
        });
        filterLayout.addComponent(dateFilterForMap);

		ComboBox diseaseFilter = new ComboBox();
        diseaseFilter.setWidth(200, Unit.PIXELS);
        diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DISEASE));
        diseaseFilter.addItems((Object[])Disease.values());
        diseaseFilter.addValueChangeListener(e -> {
        	disease = (Disease) diseaseFilter.getValue();
        	refreshDashboard();
        });
        filterLayout.addComponent(diseaseFilter);
        disease = (Disease) diseaseFilter.getValue();
        
        return filterLayout;
	}
	
	private VerticalLayout createContents() {
		VerticalLayout layout = new VerticalLayout();
		
		// Map header
		HorizontalLayout mapHeaderLayout = new HorizontalLayout();
		mapHeaderLayout.setWidth(100, Unit.PERCENTAGE);
		{
	        Label caseMapLabel = new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, CASE_MAP));
	        caseMapLabel.setSizeUndefined();
	        CssStyles.style(caseMapLabel, CssStyles.H3, CssStyles.NO_MARGIN);
			mapHeaderLayout.addComponent(caseMapLabel);
	
			HorizontalLayout keyLayout = new HorizontalLayout();
	        keyLayout.setSpacing(true);
	        
	        Image iconGrey = new Image(null, new ThemeResource("mapicons/grey-dot-small.png"));
	        Image iconYellow = new Image(null, new ThemeResource("mapicons/yellow-dot-small.png"));
	        Image iconOrange = new Image(null, new ThemeResource("mapicons/orange-dot-small.png"));
	        Image iconRed = new Image(null, new ThemeResource("mapicons/red-dot-small.png"));
	        iconGrey.setWidth(16.5f, Unit.PIXELS);
	        iconGrey.setHeight(22.5f, Unit.PIXELS);
	        iconYellow.setWidth(16.5f, Unit.PIXELS);
	        iconYellow.setHeight(22.5f, Unit.PIXELS);
	        iconOrange.setWidth(16.5f, Unit.PIXELS);
	        iconOrange.setHeight(22.5f, Unit.PIXELS);
	        iconRed.setWidth(16.5f, Unit.PIXELS);
	        iconRed.setHeight(22.5f, Unit.PIXELS);
	        
	        keyLayout.addComponent(iconGrey);
	        keyLayout.addComponent(new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, NOT_YET_CLASSIFIED)));
	        keyLayout.addComponent(iconYellow);
	        keyLayout.addComponent(new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, SUSPECT)));
	        keyLayout.addComponent(iconOrange);
	        keyLayout.addComponent(new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, PROBABLE)));
	        keyLayout.addComponent(iconRed);
	        keyLayout.addComponent(new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, CONFIRMED)));
	        
	        mapHeaderLayout.addComponent(keyLayout);
	        mapHeaderLayout.setComponentAlignment(keyLayout, Alignment.MIDDLE_RIGHT);
	        mapHeaderLayout.setExpandRatio(keyLayout, 1);
		}
		layout.addComponent(mapHeaderLayout);
        
		// Map
	    mapComponent.setHeight(700, Unit.PIXELS);
	    mapComponent.setWidth(100, Unit.PERCENTAGE);
	    mapComponent.addStyleName(CssStyles.VSPACE3);
	    mapComponent.addStyleName(CssStyles.VSPACETOP3);
        layout.addComponent(mapComponent);
        layout.setExpandRatio(mapComponent, 1);
        
        // Info graphics
        HorizontalLayout infoGraphicsLayout = new HorizontalLayout();
        infoGraphicsLayout.setId("infoGraphicsLayout");
        infoGraphicsLayout.setWidth(100, Unit.PERCENTAGE);
        infoGraphicsLayout.setHeightUndefined();
        infoGraphicsLayout.setSpacing(true);
        infoGraphicsLayout.setMargin(new MarginInfo(false, false, true, false));
        {
        	VerticalLayout reportTableLayout = new VerticalLayout();

        	Label reportTableLabel = new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, SITUATION_REPORT));
        	reportTableLabel.addStyleName(CssStyles.H3);
        	reportTableLayout.addComponent(reportTableLabel);
        
        	createSituationReportTable();
        	reportTableLayout.addComponent(situationReportTable);
        	reportTableLayout.setHeightUndefined();
        	reportTableLayout.setWidth(100, Unit.PERCENTAGE);
            infoGraphicsLayout.addComponent(reportTableLayout);
            
            VerticalLayout epiCurveLayout = new VerticalLayout();
            epiCurveLayout.setId("epiCurveLayout");
            epiCurveLayout.setWidth(100, Unit.PERCENTAGE);
        	epiCurveLayout.setHeight(100, Unit.PERCENTAGE);
	        
	        Label epiCurveLabel = new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, EPI_CURVE));
	        epiCurveLabel.addStyleName(CssStyles.H3);
	        epiCurveLayout.addComponent(epiCurveLabel);
	        
	        chartWrapper = new CssLayout();
	        chartWrapper.setId("chartWrapper");
	        {
		        createEpiCurveChart();
		        chartWrapper.setHeight(100, Unit.PERCENTAGE);
		        chartWrapper.setWidth(100, Unit.PERCENTAGE);
		        chartWrapper.addComponent(epiCurveChart);
		        epiCurveLayout.addComponent(chartWrapper);
		        epiCurveLayout.setExpandRatio(chartWrapper, 1);
	        }
	        
	        infoGraphicsLayout.addComponent(epiCurveLayout, 0);
        }
        layout.addComponent(infoGraphicsLayout);
        layout.setExpandRatio(infoGraphicsLayout, 1);
        
        return layout;
	}
	
	private void refreshDashboard() {
		// Update the cases list according to the filters
		String userUuid = LoginHelper.getCurrentUser().getUuid();
		cases = FacadeProvider.getCaseFacade().getAllCasesBetween(fromDate, toDate, disease, userUuid);
		
		// Update cases shown on the map
    	refreshMap();
		
    	// Update situation report and epi curve data
    	clearAndFillSituationReportTable();
    	// Epi curve chart has to be created again due to a canvas resizing issue when simply refreshing the component
    	chartWrapper.removeComponent(epiCurveChart);
    	createEpiCurveChart();
    	chartWrapper.addComponent(epiCurveChart);
	}
	
	private void refreshMap() {
		String userUuid = LoginHelper.getCurrentUser().getUuid();
		
		// If the "use date filter for map" check box is not checked, use a list of all cases irrespective of the dates instead
		if (useDateFilterForMap == true) {
        	mapComponent.showFacilities(cases);
    	} else {
    		List<CaseDataDto> casesForMap = FacadeProvider.getCaseFacade().getAllCasesByDiseaseAfter(null, disease, userUuid);
    		mapComponent.showFacilities(casesForMap);
    	}
	}
	
	private void createSituationReportTable() {
		situationReportTable = new Table();
		situationReportTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		situationReportTable.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
		situationReportTable.addContainerProperty(HEADING, Label.class, null);
		situationReportTable.addContainerProperty(SUB_HEADING, Label.class, null);
		situationReportTable.addContainerProperty(QUERY_PERIOD, HorizontalLayout.class, null);
		situationReportTable.addContainerProperty(PREVIOUS_PERIOD, HorizontalLayout.class, null);
		situationReportTable.setWidth(100, Unit.PERCENTAGE);
		situationReportTable.setColumnAlignment(QUERY_PERIOD, Align.CENTER);
		situationReportTable.setColumnAlignment(PREVIOUS_PERIOD, Align.CENTER);
		
		clearAndFillSituationReportTable();
	}
	
	private void clearAndFillSituationReportTable() {
		situationReportTable.removeAllItems();

		// Update header captions; this has to be done every time the data is changed to update the amount of days
		for (Object columnId : situationReportTable.getVisibleColumns()) {
			situationReportTable.setColumnHeader(columnId, String.format(
					I18nProperties.getPrefixFieldCaption(I18N_PREFIX, (String) columnId),
					DateHelper.getDaysBetween(fromDate, toDate)));
		}

		// Fetch data for chosen time period and disease
		String userUuid = LoginHelper.getCurrentUser().getUuid();
		
		// Cases
		List<CaseDataDto> previousCases = FacadeProvider.getCaseFacade().getAllCasesBetween(
				DateHelper.subtractDays(fromDate, DateHelper.getDaysBetween(fromDate, toDate)), 
				DateHelper.subtractDays(toDate, DateHelper.getDaysBetween(fromDate, toDate)), disease, userUuid);
						
		List<CaseDataDto> confirmedCases = cases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED)
				.collect(Collectors.toList());
		List<CaseDataDto> suspectedCases = cases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT)
				.collect(Collectors.toList());
		List<CaseDataDto> probableCases = cases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE)
				.collect(Collectors.toList());
		List<CaseDataDto> previousConfirmedCases = previousCases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED)
				.collect(Collectors.toList());
		List<CaseDataDto> previousSuspectedCases = previousCases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT)
				.collect(Collectors.toList());
		List<CaseDataDto> previousProbableCases = previousCases.stream()
				.filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE)
				.collect(Collectors.toList());
		
		int confirmedHcwsCount = (int) confirmedCases.stream()
				.filter(c -> FacadeProvider.getPersonFacade().getPersonByUuid(c.getPerson().getUuid()).getOccupationType() == OccupationType.HEALTHCARE_WORKER)
				.count();
		int suspectedHcwsCount = (int) suspectedCases.stream()
				.filter(c -> FacadeProvider.getPersonFacade().getPersonByUuid(c.getPerson().getUuid()).getOccupationType() == OccupationType.HEALTHCARE_WORKER)
				.count();
		int probableHcwsCount = (int) suspectedCases.stream()
				.filter(c -> FacadeProvider.getPersonFacade().getPersonByUuid(c.getPerson().getUuid()).getOccupationType() == OccupationType.HEALTHCARE_WORKER)
				.count();
		int previousConfirmedHcwsCount = (int) previousConfirmedCases.stream()
				.filter(c -> FacadeProvider.getPersonFacade().getPersonByUuid(c.getPerson().getUuid()).getOccupationType() == OccupationType.HEALTHCARE_WORKER)
				.count();
		int previousSuspectedHcwsCount = (int) previousSuspectedCases.stream()
				.filter(c -> FacadeProvider.getPersonFacade().getPersonByUuid(c.getPerson().getUuid()).getOccupationType() == OccupationType.HEALTHCARE_WORKER)
				.count();
		int previousProbableHcwsCount = (int) previousProbableCases.stream()
				.filter(c -> FacadeProvider.getPersonFacade().getPersonByUuid(c.getPerson().getUuid()).getOccupationType() == OccupationType.HEALTHCARE_WORKER)
				.count();
		
		int totalCases = confirmedCases.size() + suspectedCases.size() + probableCases.size();
		int previousTotalCases = previousConfirmedCases.size() + previousSuspectedCases.size() + previousProbableCases.size();
		int totalHcwsCount = confirmedHcwsCount + suspectedHcwsCount + probableHcwsCount;
		int previousTotalHcwsCount = previousConfirmedHcwsCount + previousSuspectedHcwsCount + previousProbableHcwsCount;
		
		// Deaths
		List<PersonDto> deadPersons = FacadeProvider.getPersonFacade().getDeathsBetween(fromDate, toDate, disease, userUuid);
		List<PersonDto> previousDeadPersons = FacadeProvider.getPersonFacade().getDeathsBetween(
				DateHelper.subtractDays(fromDate, DateHelper.getDaysBetween(fromDate, toDate)), 
				DateHelper.subtractDays(toDate, DateHelper.getDaysBetween(fromDate, toDate)), disease, userUuid);
		int deadHcws = (int) deadPersons.stream()
				.filter(p -> p.getOccupationType() == OccupationType.HEALTHCARE_WORKER)
				.count();
		int previousDeadHcws = (int) previousDeadPersons.stream()
				.filter(p -> p.getOccupationType() == OccupationType.HEALTHCARE_WORKER)
				.count();
		
		// Contacts
		List<ContactDto> contacts = FacadeProvider.getContactFacade().getFollowUpBetween(fromDate, toDate, disease, userUuid);
		List<ContactDto> previousContacts = FacadeProvider.getContactFacade().getFollowUpBetween(
				DateHelper.subtractDays(fromDate, DateHelper.getDaysBetween(fromDate, toDate)), 
				DateHelper.subtractDays(toDate, DateHelper.getDaysBetween(fromDate, toDate)), disease, userUuid);
		int onLastDayOfPeriod = (int) contacts.stream()
				.filter(c -> (c.getFollowUpUntil().after(toDate) || DateHelper.isSameDay(c.getFollowUpUntil(), toDate)) && 
						(c.getLastContactDate().before(toDate) || DateHelper.isSameDay(c.getLastContactDate(), toDate)))
				.count();
		int lostToFollowUpCount = (int) contacts.stream()
				.filter(c -> c.getFollowUpStatus() == FollowUpStatus.LOST)
				.count();
		int previousLostToFollowUpCount = (int) previousContacts.stream()
				.filter(c -> c.getFollowUpStatus() == FollowUpStatus.LOST)
				.count();
		
		// Events
		List<EventDto> events = FacadeProvider.getEventFacade().getAllEventsBetween(fromDate, toDate, disease, userUuid);
		List<EventDto> previousEvents = FacadeProvider.getEventFacade().getAllEventsBetween(
				DateHelper.subtractDays(fromDate, DateHelper.getDaysBetween(fromDate, toDate)), 
				DateHelper.subtractDays(toDate, DateHelper.getDaysBetween(fromDate, toDate)), disease, userUuid);
		int outbreaksCount = (int) events.stream()
				.filter(e -> e.getEventType() == EventType.OUTBREAK)
				.count();
		int previousOutbreaksCount = (int) previousEvents.stream()
				.filter(e -> e.getEventType() == EventType.OUTBREAK)
				.count();
		int rumorsCount = (int) events.stream()
				.filter(e -> e.getEventType() == EventType.RUMOR)
				.count();
		int previousRumorsCount = (int) previousEvents.stream()
				.filter(e -> e.getEventType() == EventType.RUMOR)
				.count();
		
		// Add data to the table
		addRowToTable(new Label("<b>" + I18nProperties.getPrefixFieldCaption(I18N_PREFIX, CASES) + "</b>", ContentMode.HTML), 
				new Label("<b>" + I18nProperties.getPrefixFieldCaption(I18N_PREFIX, CONFIRMED) + "</b>", ContentMode.HTML), 
				confirmedCases.size(), previousConfirmedCases.size(), 0, false);
		addRowToTable(null, createHcwLabel(), confirmedHcwsCount, previousConfirmedHcwsCount, 1, true);
		addRowToTable(null, new Label("<b>" + I18nProperties.getPrefixFieldCaption(I18N_PREFIX, PROBABLE) + "</b>", ContentMode.HTML), 
				probableCases.size(), previousProbableCases.size(), 4, false);
		addRowToTable(null, createHcwLabel(), probableHcwsCount, previousProbableHcwsCount, 5, true);
		addRowToTable(null, new Label("<b>" + I18nProperties.getPrefixFieldCaption(I18N_PREFIX, SUSPECT) + "</b>", ContentMode.HTML), 
				suspectedCases.size(), previousSuspectedCases.size(), 2, false);
		addRowToTable(null, createHcwLabel(), suspectedHcwsCount, previousSuspectedHcwsCount, 3, true);
		addRowToTable(null, new Label("<b>" + I18nProperties.getPrefixFieldCaption(I18N_PREFIX, TOTAL) + "</b>", ContentMode.HTML), 
				totalCases, previousTotalCases, 6, false);
		addRowToTable(null, createHcwLabel(), totalHcwsCount, previousTotalHcwsCount, 7, true);
		addRowToTable(new Label("<b>" + I18nProperties.getPrefixFieldCaption(I18N_PREFIX, DEATHS) + "</b>", ContentMode.HTML), 
				new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, HCWS)), deadHcws, previousDeadHcws, 8, false);
		addRowToTable(null, new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, TOTAL)), deadPersons.size(), previousDeadPersons.size(), 9, false);
		addRowToTable(new Label("<b>" + I18nProperties.getPrefixFieldCaption(I18N_PREFIX, CONTACTS) + "</b>", ContentMode.HTML), 
				new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, UNDER_FOLLOW_UP)), contacts.size(), previousContacts.size(), 10, false);
		Label dayLabel = new Label("<i>" + I18nProperties.getPrefixFieldCaption(I18N_PREFIX, ON) + " " + 
				DateHelper.formatShortDate(toDate) + "</i>", ContentMode.HTML);
		addRowToTable(null, dayLabel, onLastDayOfPeriod, null, 11, false);
		addRowToTable(null, new Label(I18nProperties.getEnumCaption(FollowUpStatus.LOST)), lostToFollowUpCount, previousLostToFollowUpCount, 12, false);
		addRowToTable(new Label("<b>" + I18nProperties.getPrefixFieldCaption(I18N_PREFIX, ALERTS) + "</b>", ContentMode.HTML), 
				new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, OUTBREAKS)), outbreaksCount, previousOutbreaksCount, 13, false);
		addRowToTable(null, new Label(I18nProperties.getPrefixFieldCaption(I18N_PREFIX, RUMORS)), rumorsCount, previousRumorsCount, 14, false);	
	}
	
	/**
	 * Add a row consisting of four columns to the situation report table
	 * 
	 * @param heading The heading, only used for the first row of a section (cases, deaths, contacts, alerts)
	 * @param subHeading The sub-heading that categorizes the data (e.g. confirmed, probable, suspect and total for cases)
	 * @param queryPeriod The amount of data entries of this row for the chosen time period
	 * @param previousPeriod The amount of data entries of this row for the period before the chosen time period
	 * @param position The position in the table
	 * @param smallRow Whether the fonts in this row should be displayed in a small size
	 */
	private void addRowToTable(Label heading, Label subHeading, Integer queryPeriod, Integer previousPeriod, int position, boolean smallRow) {
		// This layout is used to center the contents of the column
		HorizontalLayout queryPeriodLayout = new HorizontalLayout();
		Label queryPeriodLabel = new Label(String.valueOf(queryPeriod));
		queryPeriodLayout.addComponent(queryPeriodLabel);
		
		// Create the layout for the rightmost column that displays a number and a FontAwesome arrow
		HorizontalLayout previousPeriodLayout = new HorizontalLayout();
		previousPeriodLayout.setSpacing(true);
		if (previousPeriod != null) {
			Label number = new Label(String.valueOf(previousPeriod));
			Label arrow;
			if (previousPeriod > queryPeriod) {
				arrow = new Label(FontAwesome.ARROW_DOWN.getHtml(), ContentMode.HTML);
				arrow.addStyleName(CssStyles.COLOR_GREEN);
			} else if (previousPeriod < queryPeriod) {
				arrow = new Label(FontAwesome.ARROW_UP.getHtml(), ContentMode.HTML);
				arrow.addStyleName(CssStyles.COLOR_RED);
			} else {
				arrow = new Label(FontAwesome.ARROW_RIGHT.getHtml(), ContentMode.HTML);
				arrow.addStyleName(CssStyles.COLOR_GREY);
			}
			previousPeriodLayout.addComponent(number);
			previousPeriodLayout.addComponent(arrow);
		}
		
		// Disply the contents of the row in a small font size when the respective attribute is set
		if (smallRow) {
			previousPeriodLayout.addStyleName(CssStyles.FONT_SIZE_SMALL);
			queryPeriodLabel.addStyleName(CssStyles.FONT_SIZE_SMALL);
		}
		
		situationReportTable.addItem(new Object[]{heading, subHeading, queryPeriodLayout, previousPeriodLayout}, position);
	}
	
	/**
	 * Creates a "Healthcare worker" label with small font size and italic text
	 * @return
	 */
	private Label createHcwLabel() {
		Label hcwLabel = new Label("<i>" + I18nProperties.getPrefixFieldCaption(I18N_PREFIX, HCWS) + "</i>", ContentMode.HTML);
		hcwLabel.addStyleName(CssStyles.FONT_SIZE_SMALL);
		return hcwLabel;
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
		String chartLoadFunction = "MutationObserver = window.MutationObserver;"
				+ "var observer = new MutationObserver(function(mutations, observer) {"
				+ "console.log(mutations, observer);"
				+ "mutations.forEach(function(mutation) {"
				+ "console.log(mutation.target);"
				+ "});"
//				+ "var mutatedObject = mutations.target;"
//				+ "var parent = mutatedObject.parentElement;"
//				+ "var width = parent.offsetWidth;"
//				+ "var height = parent.offsetHeight;"
//				+ "mutatedObject.style.width = width;"
//				+ "mutatedObject.style.height = height;"
				+ "});"
				+ ""
				+ "event.target.redraw();"
				+ "var chartWrapper = document.getElementById(\"chartWrapper\");"
				+ "console.log(event.target.offsetWidth);"
				+ "console.log(event.target.offsetHeight);"
				+ "console.log(chartWrapper.offsetWidth);"
				+ "console.log(chartWrapper.offsetHeight);"
				+ "observer.observe(chartWrapper, {"
				+ "childList: false,"
				+ "subtree: false,"
				+ "attributes: true"
				+ "});";
//		String chartLoadFunction = "var infoGraphicsLayout = document.getElementById(\"infoGraphicsLayout\");"
//				+ "var chartDiv = document.getElementsByClassName(\"highcharts-container\")[0];"
//				+ "console.log(infoGraphicsLayout.clientWidth + \" \" + infoGraphicsLayout.clientHeight);"
//				+ "chartDiv.style.width = chartWrapper.clientWidth;"
//				+ "chartDiv.style.height = chartWrapper.clientHeight;";
		hcjs.append("var options = {"
				+ "chart: { type: 'column', backgroundColor: null, events: { addSeries: function(event) {" + chartLoadFunction + "} } },"
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
				
		hcjs.append("yAxis: { min: 0, title: { text: '' }, allowDecimals: false, softMax: 10, stackLabels: { enabled: true, style: {"
				+ "fontWeight: 'bold', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },"
				+ "legend: { verticalAlign: 'top', backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',"
				+ "borderColor: '#CCC', borderWidth: 1, shadow: false },"
				+ "tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'},"
				+ "plotOptions: { column: { stacking: 'normal', dataLabels: {"
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
		Date currentDate = new Date(fromDate.getTime());
		while (!currentDate.after(toDate)) {
			filteredDates.add(currentDate);
			currentDate = DateHelper.addDays(currentDate, 1);
		}
		
		return filteredDates;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		refreshDashboard();
	}
	
}
