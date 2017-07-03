package de.symeda.sormas.ui.dashboard;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
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
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class SituationReportTable extends Table {

	public SituationReportTable() {
		addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
		addContainerProperty(DashboardView.HEADING, Label.class, null);
		addContainerProperty(DashboardView.SUB_HEADING, Label.class, null);
		addContainerProperty(DashboardView.QUERY_PERIOD, HorizontalLayout.class, null);
		addContainerProperty(DashboardView.PREVIOUS_PERIOD, HorizontalLayout.class, null);
		setWidth(100, Unit.PERCENTAGE);
		setColumnAlignment(DashboardView.QUERY_PERIOD, Align.CENTER);
		setColumnAlignment(DashboardView.PREVIOUS_PERIOD, Align.CENTER);
	}

	public void clearAndFill(Date fromDate, Date toDate, Disease disease, List<CaseDataDto> cases) {
		removeAllItems();

		// Update header captions; this has to be done every time the data is changed to update the amount of days
		for (Object columnId : getVisibleColumns()) {
			setColumnHeader(columnId, String.format(
					I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, (String) columnId),
					DateHelper.getDaysBetween(fromDate, toDate)));
		}

		this.setColumnExpandRatio(this.getVisibleColumns()[1], 1);
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
		addRowToTable(new Label("<b>" + I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.CASES) + "</b>", ContentMode.HTML), 
				new Label("<b>" + I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.CONFIRMED) + "</b>", ContentMode.HTML), 
				confirmedCases.size(), previousConfirmedCases.size(), 0, false);
		addRowToTable(null, createHcwLabel(), confirmedHcwsCount, previousConfirmedHcwsCount, 1, true);
		addRowToTable(null, new Label("<b>" + I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.PROBABLE) + "</b>", ContentMode.HTML), 
				probableCases.size(), previousProbableCases.size(), 4, false);
		addRowToTable(null, createHcwLabel(), probableHcwsCount, previousProbableHcwsCount, 5, true);
		addRowToTable(null, new Label("<b>" + I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.SUSPECT) + "</b>", ContentMode.HTML), 
				suspectedCases.size(), previousSuspectedCases.size(), 2, false);
		addRowToTable(null, createHcwLabel(), suspectedHcwsCount, previousSuspectedHcwsCount, 3, true);
		addRowToTable(null, new Label("<b>" + I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.TOTAL) + "</b>", ContentMode.HTML), 
				totalCases, previousTotalCases, 6, false);
		addRowToTable(null, createHcwLabel(), totalHcwsCount, previousTotalHcwsCount, 7, true);
		addRowToTable(new Label("<b>" + I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.DEATHS) + "</b>", ContentMode.HTML), 
				new Label(I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.TOTAL)), deadPersons.size(), previousDeadPersons.size(), 8, false);
		addRowToTable(null, createHcwLabel(), deadHcws, previousDeadHcws, 9, true);
		addRowToTable(new Label("<b>" + I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.CONTACTS) + "</b>", ContentMode.HTML), 
				new Label(I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.UNDER_FOLLOW_UP)), contacts.size(), previousContacts.size(), 10, false);
		Label dayLabel = new Label("<i>" + I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.ON) + " " + 
				DateHelper.formatShortDate(toDate) + "</i>", ContentMode.HTML);
		addRowToTable(null, dayLabel, onLastDayOfPeriod, null, 11, false);
		addRowToTable(null, new Label(I18nProperties.getEnumCaption(FollowUpStatus.LOST)), lostToFollowUpCount, previousLostToFollowUpCount, 12, false);
		addRowToTable(new Label("<b>" + I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.ALERTS) + "</b>", ContentMode.HTML), 
				new Label(I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.OUTBREAKS)), outbreaksCount, previousOutbreaksCount, 13, false);
		addRowToTable(null, new Label(I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.RUMORS)), rumorsCount, previousRumorsCount, 14, false);	
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

		// Display the contents of the row in a small font size when the respective attribute is set
		if (smallRow) {
			previousPeriodLayout.addStyleName(CssStyles.FONT_SIZE_SMALL);
			queryPeriodLabel.addStyleName(CssStyles.FONT_SIZE_SMALL);
		}

		addItem(new Object[]{heading, subHeading, queryPeriodLayout, previousPeriodLayout}, position);
	}

	/**
	 * Creates a "Healthcare worker" label with small font size and italic text
	 * @return
	 */
	private Label createHcwLabel() {
		Label hcwLabel = new Label("<i>" + I18nProperties.getPrefixFieldCaption(DashboardView.I18N_PREFIX, DashboardView.HCWS) + "</i>", ContentMode.HTML);
		hcwLabel.addStyleName(CssStyles.FONT_SIZE_SMALL);
		return hcwLabel;
	}

}
