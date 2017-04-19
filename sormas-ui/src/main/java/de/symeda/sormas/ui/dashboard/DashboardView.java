package de.symeda.sormas.ui.dashboard;

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
import com.vaadin.ui.ComboBox;
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
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardView extends AbstractView {

	public static final String I18N_PREFIX = "Dashboard";
	public static final String VIEW_NAME = "dashboard";
	
	private final ComboBox diseaseFilter;
	private final DateField dateFromFilter;
	private final DateField dateToFilter;
	private final MapComponent mapComponent;
	private Table situationReportTable;

	public DashboardView() {
		setSizeFull();
		setSpacing(false);

		diseaseFilter = new ComboBox();
		dateFromFilter = new DateField();
		dateFromFilter.setDateFormat(DateHelper.getShortDateFormat().toLocalizedPattern());
		dateToFilter = new DateField();
		dateToFilter.setDateFormat(DateHelper.getShortDateFormat().toLocalizedPattern());
        addComponent(createTopBar());
        
		mapComponent = new MapComponent();
		VerticalLayout layout = createContents();
		layout.setHeightUndefined();
        addComponent(layout);
        setExpandRatio(layout, 1);
	}
	
	private HorizontalLayout createTopBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
    	filterLayout.setWidth(100, Unit.PERCENTAGE);
		filterLayout.setSpacing(true);
		filterLayout.setMargin(true);
        
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        dateFromFilter.setWidth(200, Unit.PIXELS);
        dateFromFilter.setCaption("From");
        dateFromFilter.setValue(DateHelper.subtractDays(c.getTime(), 28));
        dateFromFilter.setDateFormat(DateHelper.getShortDateFormat().toPattern());
        dateFromFilter.addValueChangeListener(e -> refreshDashboard());
        filterLayout.addComponent(dateFromFilter);
        dateToFilter.setWidth(200, Unit.PIXELS);
        dateToFilter.setCaption("To");
        dateToFilter.setValue(c.getTime());
        dateToFilter.setDateFormat(DateHelper.getShortDateFormat().toPattern());
        dateToFilter.addValueChangeListener(e -> refreshDashboard());
        filterLayout.addComponent(dateToFilter);

        diseaseFilter.setWidth(200, Unit.PIXELS);
        diseaseFilter.setInputPrompt("All");
        diseaseFilter.addItems((Object[])Disease.values());
        diseaseFilter.setCaption("Disease");
        diseaseFilter.addValueChangeListener(e -> refreshDashboard());
        filterLayout.addComponent(diseaseFilter);
        
        HorizontalLayout keyLayout = new HorizontalLayout();
        keyLayout.setSpacing(true);
        keyLayout.setMargin(false);

        Image iconGrey = new Image(null, new ThemeResource("mapicons/grey-dot-small.png"));
        Image iconYellow = new Image(null, new ThemeResource("mapicons/yellow-dot-small.png"));
        Image iconOrange = new Image(null, new ThemeResource("mapicons/orange-dot-small.png"));
        Image iconRed = new Image(null, new ThemeResource("mapicons/red-dot-small.png"));
        
        keyLayout.addComponent(iconGrey);
        keyLayout.addComponent(new Label("Possible"));
        keyLayout.addComponent(iconYellow);
        keyLayout.addComponent(new Label("Suspect"));
        keyLayout.addComponent(iconOrange);
        keyLayout.addComponent(new Label("Probable"));
        keyLayout.addComponent(iconRed);
        keyLayout.addComponent(new Label("Confirmed"));
//        CssStyles.stylePrimary(keyLayout, CssStyles.DASHBOARD_KEY);
        
        filterLayout.addComponent(keyLayout);
        filterLayout.setComponentAlignment(keyLayout, Alignment.MIDDLE_RIGHT);
        filterLayout.setExpandRatio(keyLayout, 1);
        
		return filterLayout;
	}
	
	private VerticalLayout createContents() {
		VerticalLayout layout = new VerticalLayout();
        
        mapComponent.setHeight(700, Unit.PIXELS);
        mapComponent.setWidth(100, Unit.PERCENTAGE);
        mapComponent.setMargin(new MarginInfo(false, true));
        layout.addComponent(mapComponent);
        layout.setExpandRatio(mapComponent, 1);
        
        HorizontalLayout infoGraphicsLayout = new HorizontalLayout();
        Image previewImg1 = new Image(null, new ThemeResource("img/dashboard-preview1.png"));
        infoGraphicsLayout.addComponent(previewImg1);
        createSituationReportTable();
        infoGraphicsLayout.addComponent(situationReportTable);
        infoGraphicsLayout.setWidth(100, Unit.PERCENTAGE);
        infoGraphicsLayout.setSpacing(true);
        infoGraphicsLayout.setMargin(true);
        infoGraphicsLayout.setSizeFull();
        layout.addComponent(infoGraphicsLayout);
        layout.setExpandRatio(infoGraphicsLayout, 1);
        
        return layout;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		refreshDashboard();
	}
	
	private void refreshDashboard() {
    	List<CaseDataDto> cases = ControllerProvider.getCaseController().getCaseIndexList();

    	// TODO move into service layer
		Disease disease = (Disease) diseaseFilter.getValue(); 
		if (disease != null) {
			cases = cases.stream()
    		.filter(entry -> disease.equals(entry.getDisease()))
    		.collect(Collectors.toList());
		}
		
    	mapComponent.showFacilities(cases);
    	clearAndFillSituationReportTable();
	}
	
	private void createSituationReportTable() {
		situationReportTable = new Table();
		situationReportTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		situationReportTable.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
		situationReportTable.setSizeFull();
		situationReportTable.addContainerProperty("heading", Label.class, null);
		situationReportTable.addContainerProperty("subHeading", Label.class, null);
		situationReportTable.addContainerProperty("queryPeriod", HorizontalLayout.class, null);
		situationReportTable.addContainerProperty("previousPeriod", HorizontalLayout.class, null);
		
		situationReportTable.setColumnAlignment("queryPeriod", Align.CENTER);
		situationReportTable.setColumnAlignment("previousPeriod", Align.CENTER);
		
		clearAndFillSituationReportTable();
	}
	
	private void clearAndFillSituationReportTable() {
		situationReportTable.removeAllItems();
		
		Date fromDate = dateFromFilter.getValue();
		Date toDate = dateToFilter.getValue();
		Disease disease = (Disease) diseaseFilter.getValue();
		String userUuid = LoginHelper.getCurrentUser().getUuid();
				
		// Update header captions
		for (Object columnId : situationReportTable.getVisibleColumns()) {
			situationReportTable.setColumnHeader(columnId, String.format(
					I18nProperties.getPrefixFieldCaption(I18N_PREFIX, (String) columnId),
					DateHelper.getDaysBetween(fromDate, toDate)));
		}

		// Fetch data for chosen time period and disease
		
		// Cases
		List<CaseDataDto> cases = FacadeProvider.getCaseFacade().getAllCasesBetween(fromDate, toDate, disease, userUuid);
		List<CaseDataDto> previousCases = FacadeProvider.getCaseFacade().getAllCasesBetween(DateHelper.subtractDays(fromDate, DateHelper.getDaysBetween(fromDate, toDate)), DateHelper.subtractDays(toDate, DateHelper.getDaysBetween(fromDate, toDate)), disease, userUuid);
						
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
		List<PersonDto> previousDeadPersons = FacadeProvider.getPersonFacade().getDeathsBetween(DateHelper.subtractDays(fromDate, DateHelper.getDaysBetween(fromDate, toDate)), DateHelper.subtractDays(toDate, DateHelper.getDaysBetween(fromDate, toDate)), disease, userUuid);
		int deadHcws = (int) deadPersons.stream()
				.filter(p -> p.getOccupationType() == OccupationType.HEALTHCARE_WORKER)
				.count();
		int previousDeadHcws = (int) previousDeadPersons.stream()
				.filter(p -> p.getOccupationType() == OccupationType.HEALTHCARE_WORKER)
				.count();
		
		// Contacts
		List<ContactDto> contacts = FacadeProvider.getContactFacade().getFollowUpBetween(fromDate, toDate, disease, userUuid);
		List<ContactDto> previousContacts = FacadeProvider.getContactFacade().getFollowUpBetween(DateHelper.subtractDays(fromDate, DateHelper.getDaysBetween(fromDate, toDate)), DateHelper.subtractDays(toDate, DateHelper.getDaysBetween(fromDate, toDate)), disease, userUuid);
		int onLastDayOfPeriod = (int) contacts.stream()
				.filter(c -> (c.getFollowUpUntil().after(toDate) || c.getFollowUpUntil().equals(toDate)) && 
						(c.getLastContactDate().before(toDate) || c.getLastContactDate().equals(toDate)))
				.count();
		int lostToFollowUpCount = (int) contacts.stream()
				.filter(c -> c.getFollowUpStatus() == FollowUpStatus.LOST)
				.count();
		int previousLostToFollowUpCount = (int) previousContacts.stream()
				.filter(c -> c.getFollowUpStatus() == FollowUpStatus.LOST)
				.count();
		
		// Events
		List<EventDto> events = FacadeProvider.getEventFacade().getAllEventsBetween(fromDate, toDate, disease, userUuid);
		List<EventDto> previousEvents = FacadeProvider.getEventFacade().getAllEventsBetween(DateHelper.subtractDays(fromDate, DateHelper.getDaysBetween(fromDate, toDate)), DateHelper.subtractDays(toDate, DateHelper.getDaysBetween(fromDate, toDate)), disease, userUuid);
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
		addRowToTable(new Label("<b>Cases</b>", ContentMode.HTML), new Label("<b>Confirmed</b>", ContentMode.HTML), confirmedCases.size(), previousConfirmedCases.size(), 0, false);
		addRowToTable(null, createHcwLabel(), confirmedHcwsCount, previousConfirmedHcwsCount, 1, true);
		addRowToTable(null, new Label("<b>Suspected</b>", ContentMode.HTML), suspectedCases.size(), previousSuspectedCases.size(), 2, false);
		addRowToTable(null, createHcwLabel(), suspectedHcwsCount, previousSuspectedHcwsCount, 3, true);
		addRowToTable(null, new Label("<b>Probable</b>", ContentMode.HTML), probableCases.size(), previousProbableCases.size(), 4, false);
		addRowToTable(null, createHcwLabel(), probableHcwsCount, previousProbableHcwsCount, 5, true);
		addRowToTable(null, new Label("<b>Total</b>", ContentMode.HTML), totalCases, previousTotalCases, 6, false);
		addRowToTable(null, createHcwLabel(), totalHcwsCount, previousTotalHcwsCount, 7, true);
		addRowToTable(new Label("<b>Deaths</b>", ContentMode.HTML), new Label("Healthcare workers"), deadHcws, previousDeadHcws, 8, false);
		addRowToTable(null, new Label("Total"), deadPersons.size(), previousDeadPersons.size(), 9, false);
		addRowToTable(new Label("<b>Contacts</b>", ContentMode.HTML), new Label("Under follow-up"), contacts.size(), previousContacts.size(), 10, false);
		Label dayLabel = new Label("<i>on " + DateHelper.formatShortDate(toDate) + "</i>", ContentMode.HTML);
		dayLabel.setSizeFull();
		addRowToTable(null, dayLabel, onLastDayOfPeriod, null, 11, false);
		addRowToTable(null, new Label("Lost to follow-up"), lostToFollowUpCount, previousLostToFollowUpCount, 12, false);
		addRowToTable(new Label("<b>Alerts</b>", ContentMode.HTML), new Label("Outbreaks"), outbreaksCount, previousOutbreaksCount, 13, false);
		addRowToTable(null, new Label("Rumors"), rumorsCount, previousRumorsCount, 14, false);	
	}
	
	private void addRowToTable(Label heading, Label subHeading, Integer queryPeriod, Integer previousPeriod, int position, boolean smallRow) {
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
		
		// This layout is used to center the contents of the column
		HorizontalLayout queryPeriodLayout = new HorizontalLayout();
		Label queryPeriodLabel = new Label(String.valueOf(queryPeriod));
		queryPeriodLayout.addComponent(queryPeriodLabel);
		
		if (smallRow) {
			previousPeriodLayout.addStyleName(CssStyles.FONT_SIZE_SMALL);
			queryPeriodLabel.addStyleName(CssStyles.FONT_SIZE_SMALL);
		}
		
		situationReportTable.addItem(new Object[]{heading, subHeading, queryPeriodLayout, previousPeriodLayout}, position);
	}
	
	private Label createHcwLabel() {
		Label hcwLabel = new Label("<i>Healthcare workers</i>", ContentMode.HTML);
		hcwLabel.setSizeFull();
		hcwLabel.addStyleName(CssStyles.FONT_SIZE_SMALL);
		return hcwLabel;
	}
	
}
