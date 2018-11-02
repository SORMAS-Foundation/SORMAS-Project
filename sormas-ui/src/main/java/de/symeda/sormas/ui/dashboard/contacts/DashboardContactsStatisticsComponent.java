package de.symeda.sormas.ui.dashboard.contacts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.DashboardContactDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.DashboardVisitDto;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.statistics.AbstractDashboardStatisticsComponent;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsDiseaseElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsGraphicalGrowthElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsPercentageElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardContactsStatisticsComponent extends AbstractDashboardStatisticsComponent {

	// "All Contacts" elements
	private CssLayout allContactsCountLayout;
	private DashboardStatisticsCountElement contactClassificationUnconfirmed;
	private DashboardStatisticsCountElement contactClassificationConfirmed;
	private DashboardStatisticsCountElement contactClassificationNotAContact;
	private DashboardStatisticsCountElement newContacts;
	private DashboardStatisticsCountElement symptomaticContacts;
	private DashboardStatisticsPercentageElement contactClassificationUnconfirmedLarge;
	private DashboardStatisticsPercentageElement contactClassificationConfirmedLarge;
	private DashboardStatisticsPercentageElement contactClassificationNotAContactLarge;
	private DashboardStatisticsPercentageElement newContactsLarge;
	private DashboardStatisticsPercentageElement symptomaticContactsLarge;

	// "Follow-up Status" elements
	private DashboardStatisticsPercentageElement underFollowUp;
	private DashboardStatisticsPercentageElement followUpCompleted;
	private DashboardStatisticsPercentageElement followUpCanceled;
	private DashboardStatisticsPercentageElement lostToFollowUp;
	private DashboardStatisticsPercentageElement contactStatusConverted;

	// "Follow-up Situation" elements
	private DashboardStatisticsCountElement cooperativeContacts;
	private DashboardStatisticsCountElement uncooperativeContacts;
	private DashboardStatisticsCountElement unavailableContacts;
	private DashboardStatisticsCountElement neverVisitedContacts;
	private DashboardStatisticsCountElement missedVisitsOneDay;
	private DashboardStatisticsCountElement missedVisitsTwoDays;
	private DashboardStatisticsCountElement missedVisitsThreeDays;
	private DashboardStatisticsCountElement missedVisitsGtThreeDays;

	// "Follow-Up Visits" elements
	private DashboardStatisticsGraphicalGrowthElement missedVisits;
	private DashboardStatisticsGraphicalGrowthElement unavailableVisits;
	private DashboardStatisticsGraphicalGrowthElement uncooperativeVisits;
	private DashboardStatisticsGraphicalGrowthElement cooperativeVisits;

	public DashboardContactsStatisticsComponent(DashboardDataProvider dashboardDataProvider) {
		super(dashboardDataProvider);

	}

	@Override
	protected void addFirstComponent() {
		firstComponent = new DashboardStatisticsSubComponent();

		// Header
		firstComponent.addHeader("All Contacts", null, true);

		// Content
		firstComponent.addMainContent();

		allContactsCountLayout = firstComponent.createCountLayout(true);
		contactClassificationUnconfirmed = new DashboardStatisticsCountElement("Unconfirmed", CountElementStyle.MINOR);
		contactClassificationConfirmed = new DashboardStatisticsCountElement("Confirmed", CountElementStyle.PRIMARY);
		contactClassificationNotAContact = new DashboardStatisticsCountElement("Not a contact", CountElementStyle.POSITIVE);
		newContacts = new DashboardStatisticsCountElement("New", CountElementStyle.NEUTRAL);
		symptomaticContacts = new DashboardStatisticsCountElement("Symptomatic", CountElementStyle.CRITICAL);
		firstComponent.addComponentToCountLayout(allContactsCountLayout, contactClassificationUnconfirmed);
		firstComponent.addComponentToCountLayout(allContactsCountLayout, contactClassificationConfirmed);
		firstComponent.addComponentToCountLayout(allContactsCountLayout, contactClassificationNotAContact);
		firstComponent.addComponentToCountLayout(allContactsCountLayout, newContacts);
		firstComponent.addComponentToCountLayout(allContactsCountLayout, symptomaticContacts);
		firstComponent.addComponentToContent(allContactsCountLayout);

		contactClassificationUnconfirmedLarge = new DashboardStatisticsPercentageElement("Unconfirmed", CssStyles.SVG_FILL_MINOR);
		contactClassificationConfirmedLarge = new DashboardStatisticsPercentageElement("Confirmed", CssStyles.SVG_FILL_CRITICAL);
		contactClassificationNotAContactLarge = new DashboardStatisticsPercentageElement("Not a contact", CssStyles.SVG_FILL_POSITIVE);
		newContactsLarge = new DashboardStatisticsPercentageElement("New", CssStyles.SVG_FILL_NEUTRAL);
		symptomaticContactsLarge = new DashboardStatisticsPercentageElement("Symptomatic", CssStyles.SVG_FILL_CRITICAL);

		subComponentsLayout.addComponent(firstComponent, FIRST_LOC);
	}

	@Override
	protected void updateFirstComponent(int visibleDiseasesCount) {
		List<DashboardContactDto> contacts = dashboardDataProvider.getContacts();
		List<DashboardContactDto> previousContacts = dashboardDataProvider.getPreviousContacts();

		int contactsCount = contacts.size();
		firstComponent.updateCountLabel(contactsCount);

		int newContactsCount = (int) contacts.stream()
				.filter(
						c -> c.getReportDate().after(dashboardDataProvider.getFromDate())
						|| c.getReportDate().equals(dashboardDataProvider.getFromDate()))
				.count();
		int newContactsPercentage = contactsCount == 0 ? 0 : (int) ((newContactsCount * 100.0f) / contactsCount);
		int symptomaticContactsCount = (int) contacts.stream().filter(c -> c.isSymptomatic()).count();
		int symptomaticContactsPercentage = contactsCount == 0 ? 0 : (int) ((symptomaticContactsCount * 100.0f) / contactsCount);
		int contactClassificationUnconfirmedCount = (int) contacts.stream().filter(c -> c.getContactClassification() == ContactClassification.UNCONFIRMED).count();
		int contactClassificationUnconfirmedPercentage = contactsCount == 0 ? 0 : (int) ((contactClassificationUnconfirmedCount * 100.0f) / contactsCount);
		int contactClassificationConfirmedCount = (int) contacts.stream().filter(c -> c.getContactClassification() == ContactClassification.CONFIRMED).count();
		int contactClassificationConfirmedPercentage = contactsCount == 0 ? 0 : (int) ((contactClassificationConfirmedCount * 100.0f) / contactsCount);
		int contactClassificationNotAContactCount = (int) contacts.stream().filter(c -> c.getContactClassification() == ContactClassification.NO_CONTACT).count();
		int contactClassificationNotAContactPercentage = contactsCount == 0 ? 0 : (int) ((contactClassificationNotAContactCount * 100.0f) / contactsCount);

		// Remove and re-create content layout if the disease filter has been applied or set to null
		if (currentDisease == null && previousDisease != null) {
			firstComponent.removeAllComponentsFromContent();
			firstComponent.addComponentToContent(allContactsCountLayout);
		} else if (currentDisease != null && previousDisease == null) {
			firstComponent.removeAllComponentsFromContent();
			firstComponent.addComponentToContent(contactClassificationUnconfirmedLarge);
			firstComponent.addComponentToContent(contactClassificationConfirmedLarge);
			firstComponent.addComponentToContent(contactClassificationNotAContactLarge);
			firstComponent.addComponentToContent(newContactsLarge);
			firstComponent.addComponentToContent(symptomaticContactsLarge);
		}

		if (currentDisease == null) {
			// Remove all children of the content layout
			firstComponent.removeAllComponentsFromContent();
			firstComponent.addComponentToContent(allContactsCountLayout);

			contactClassificationUnconfirmed.updateCountLabel(contactClassificationUnconfirmedCount + " (" + contactClassificationUnconfirmedPercentage + " %)");
			contactClassificationConfirmed.updateCountLabel(contactClassificationConfirmedCount + " (" + contactClassificationConfirmedPercentage + " %)");
			contactClassificationNotAContact.updateCountLabel(contactClassificationNotAContactCount + " (" + contactClassificationNotAContactPercentage + " %)");
			newContacts.updateCountLabel(newContactsCount);
			symptomaticContacts.updateCountLabel(symptomaticContactsCount + " (" + symptomaticContactsPercentage + " %)");

			// Create a map with all diseases as keys and their respective case counts as values
			Map<Disease, Integer> diseaseMap = new TreeMap<Disease, Integer>();
			for (Disease disease : DiseaseHelper.getAllDiseasesWithFollowUp()) {
				diseaseMap.put(disease, (int) contacts.stream().filter(c -> c.getDisease() == disease).count());
			}

			// Create a list from this map that sorts the entries by case counts
			List<Map.Entry<Disease, Integer>> sortedDiseaseList = createSortedDiseaseList(diseaseMap);

			// Create a new StatisticsDiseaseElement for every disease, automatically sorting them by case count
			for (int i = 0; i < visibleDiseasesCount; i++) {
				Map.Entry<Disease, Integer> mapEntry = sortedDiseaseList.get(i);
				int previousDiseaseCount = (int) previousContacts.stream().filter(c -> c.getDisease() == mapEntry.getKey()).count();
				DashboardStatisticsDiseaseElement diseaseElement = new DashboardStatisticsDiseaseElement(mapEntry.getKey().toString(), mapEntry.getValue(), previousDiseaseCount);
				firstComponent.addComponentToContent(diseaseElement);
			}
		} else {
			contactClassificationUnconfirmedLarge.updatePercentageValueWithCount(contactClassificationUnconfirmedCount, contactClassificationUnconfirmedPercentage);
			contactClassificationConfirmedLarge.updatePercentageValueWithCount(contactClassificationConfirmedCount, contactClassificationConfirmedPercentage);
			contactClassificationNotAContactLarge.updatePercentageValueWithCount(contactClassificationNotAContactCount, contactClassificationNotAContactPercentage);
			newContactsLarge.updatePercentageValueWithCount(newContactsCount, newContactsPercentage);
			symptomaticContactsLarge.updatePercentageValueWithCount(symptomaticContactsCount, symptomaticContactsPercentage);
		}
	}


	@Override
	protected void addSecondComponent() {
		secondComponent = new DashboardStatisticsSubComponent();

		// Header
		secondComponent.addHeader("Contact Follow-Up", null, true);

		// Content
		secondComponent.addMainContent();
		underFollowUp = new DashboardStatisticsPercentageElement("Under follow-up", CssStyles.SVG_FILL_PRIMARY);
		followUpCompleted = new DashboardStatisticsPercentageElement("Completed follow-up", CssStyles.SVG_FILL_POSITIVE);
		followUpCanceled = new DashboardStatisticsPercentageElement("Canceled follow-up", CssStyles.SVG_FILL_IMPORTANT);
		lostToFollowUp = new DashboardStatisticsPercentageElement("Lost to follow-up", CssStyles.SVG_FILL_CRITICAL);
		contactStatusConverted = new DashboardStatisticsPercentageElement("Converted to case", CssStyles.SVG_FILL_NEUTRAL);
		secondComponent.addComponentToContent(underFollowUp);
		secondComponent.addComponentToContent(followUpCompleted);
		secondComponent.addComponentToContent(followUpCanceled);
		secondComponent.addComponentToContent(lostToFollowUp);
		secondComponent.addComponentToContent(contactStatusConverted);

		subComponentsLayout.addComponent(secondComponent, SECOND_LOC);
	}

	@Override
	protected void updateSecondComponent(int visibleDiseasesCount) {
		List<DashboardContactDto> contacts = dashboardDataProvider.getContacts();
		contacts.removeIf(c -> c.getFollowUpStatus() == FollowUpStatus.NO_FOLLOW_UP);

		int contactsCount = contacts.size();
		secondComponent.updateCountLabel(contactsCount);

		int underFollowUpCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.FOLLOW_UP).count();
		int underFollowUpPercentage = contactsCount == 0 ? 0 : (int) ((underFollowUpCount * 100.0f) / contactsCount);
		int followUpCompletedCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.COMPLETED).count();
		int followUpCompletedPercentage = contactsCount == 0 ? 0 : (int) ((followUpCompletedCount * 100.0f) / contactsCount);
		int followUpCanceledCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.CANCELED).count();
		int followUpCanceledPercentage = contactsCount == 0 ? 0 : (int) ((followUpCanceledCount * 100.0f) / contactsCount);
		int lostToFollowUpCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.LOST).count();
		int lostToFollowUpPercentage = contactsCount == 0 ? 0 : (int) ((lostToFollowUpCount * 100.0f) / contactsCount);
		int contactStatusConvertedCount = (int) contacts.stream().filter(c -> c.getContactStatus() == ContactStatus.CONVERTED).count();
		int contactStatusConvertedPercentage = contactsCount == 0 ? 0 : (int) ((contactStatusConvertedCount * 100.0f) / contactsCount);

		underFollowUp.updatePercentageValueWithCount(underFollowUpCount, underFollowUpPercentage);
		followUpCompleted.updatePercentageValueWithCount(followUpCompletedCount, followUpCompletedPercentage);
		followUpCanceled.updatePercentageValueWithCount(followUpCanceledCount, followUpCanceledPercentage);
		lostToFollowUp.updatePercentageValueWithCount(lostToFollowUpCount, lostToFollowUpPercentage);
		contactStatusConverted.updatePercentageValueWithCount(contactStatusConvertedCount, contactStatusConvertedPercentage);
	}

	@Override
	protected void addThirdComponent() {
		thirdComponent = new DashboardStatisticsSubComponent();

		// Header
		thirdComponent.addHeader("Follow-Up Situation", null, false);

		// Visit status of last visit
		thirdComponent.addMainContent();
		CssLayout visitStatusCountLayout = thirdComponent.createCountLayout(true);
		cooperativeContacts = new DashboardStatisticsCountElement("Cooperative", CountElementStyle.POSITIVE);
		thirdComponent.addComponentToCountLayout(visitStatusCountLayout, cooperativeContacts);
		uncooperativeContacts = new DashboardStatisticsCountElement("Uncooperative", CountElementStyle.CRITICAL);
		thirdComponent.addComponentToCountLayout(visitStatusCountLayout, uncooperativeContacts);
		unavailableContacts = new DashboardStatisticsCountElement("Unavailable", CountElementStyle.RELEVANT);
		thirdComponent.addComponentToCountLayout(visitStatusCountLayout, unavailableContacts);
		neverVisitedContacts = new DashboardStatisticsCountElement("Never visited", CountElementStyle.MINOR);
		thirdComponent.addComponentToCountLayout(visitStatusCountLayout, neverVisitedContacts);

		Label infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription("Follow-up status is calculcated by taking the status of the last visit to the respective contact. \"Never Visited\" means that "
				+ "the contact has not yet been visited at all.");
		CssStyles.style(infoLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_SECONDARY, "follow-up-status-info-button");
		thirdComponent.addComponentToCountLayout(visitStatusCountLayout, infoLabel);

		thirdComponent.addComponentToContent(visitStatusCountLayout);

		// Number of missed visits
		Label missedVisitsLabel = new Label("Contacts not visited for...");
		CssStyles.style(missedVisitsLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT, CssStyles.LABEL_ROUNDED_CORNERS_SLIM);
		thirdComponent.addComponentToContent(missedVisitsLabel);

		CssLayout missedVisitsCountLayout = thirdComponent.createCountLayout(false);
		missedVisitsOneDay = new DashboardStatisticsCountElement("1 Day", CountElementStyle.PRIMARY);
		thirdComponent.addComponentToCountLayout(missedVisitsCountLayout, missedVisitsOneDay);
		missedVisitsTwoDays = new DashboardStatisticsCountElement("2 Days", CountElementStyle.PRIMARY);
		thirdComponent.addComponentToCountLayout(missedVisitsCountLayout, missedVisitsTwoDays);
		missedVisitsThreeDays = new DashboardStatisticsCountElement("3 Days", CountElementStyle.PRIMARY);
		thirdComponent.addComponentToCountLayout(missedVisitsCountLayout, missedVisitsThreeDays);
		missedVisitsGtThreeDays = new DashboardStatisticsCountElement("> 3 Days", CountElementStyle.PRIMARY);
		thirdComponent.addComponentToCountLayout(missedVisitsCountLayout, missedVisitsGtThreeDays);
		thirdComponent.addComponentToContent(missedVisitsCountLayout);

		subComponentsLayout.addComponent(thirdComponent, THIRD_LOC);
	}

	@Override
	protected void updateThirdComponent(int visibleDiseasesCount) {
		List<DashboardContactDto> contacts = dashboardDataProvider.getContacts();
		contacts.removeIf(c -> c.getFollowUpStatus() == FollowUpStatus.NO_FOLLOW_UP);

		int contactsCount = contacts.size();

		int cooperativeContactsCount = (int) contacts.stream().filter(c -> c.getLastVisitStatus() == VisitStatus.COOPERATIVE).count();
		int cooperativeContactsPercentage = contactsCount == 0 ? 0 : (int) ((cooperativeContactsCount * 100.0f) / contactsCount);
		cooperativeContacts.updateCountLabel(cooperativeContactsCount + " (" + cooperativeContactsPercentage + " %)");
		int uncooperativeContactsCount = (int) contacts.stream().filter(c -> c.getLastVisitStatus() == VisitStatus.UNCOOPERATIVE).count();
		int uncooperativeContactsPercentage = contactsCount == 0 ? 0 : (int) ((uncooperativeContactsCount * 100.0f) / contactsCount);
		uncooperativeContacts.updateCountLabel(uncooperativeContactsCount + " (" + uncooperativeContactsPercentage + " %)");
		int unavailableContactsCount = (int) contacts.stream().filter(c -> c.getLastVisitStatus() == VisitStatus.UNAVAILABLE).count();
		int unavailableContactsPercentage = contactsCount == 0 ? 0 : (int) ((unavailableContactsCount * 100.0f) / contactsCount);
		unavailableContacts.updateCountLabel(unavailableContactsCount + " (" + unavailableContactsPercentage + " %)");
		int notVisitedContactsCount = (int) contacts.stream().filter(c -> c.getLastVisitStatus() == null).count();
		int notVisitedContactsPercentage = contactsCount == 0 ? 0 : (int) ((notVisitedContactsCount * 100.0f) / contactsCount);
		neverVisitedContacts.updateCountLabel(notVisitedContactsCount + " (" + notVisitedContactsPercentage + " %)");

		int missedVisitsOneDayCount = 0;
		int missedVisitsTwoDaysCount = 0;
		int missedVisitsThreeDaysCount = 0;
		int missedVisitsGtThreeDaysCount = 0;

		for (DashboardContactDto contact : contacts) {
			Date lastVisitDateTime = contact.getLastVisitDateTime() != null ? contact.getLastVisitDateTime() : contact.getReportDate();

			Date referenceDate;
			Date now = new Date();
			// Current time should either be the end of the specified period or now if now is in the specified time interval
			if (DateHelper.isBetween(now, dashboardDataProvider.getFromDate(), dashboardDataProvider.getToDate())) {
				referenceDate = now;
			} else {
				referenceDate = dashboardDataProvider.getToDate();
			}

			int missedDays = DateHelper.getFullDaysBetween(lastVisitDateTime, referenceDate);
			if (missedDays == 1) {
				missedVisitsOneDayCount++;
			} else if (missedDays == 2) {
				missedVisitsTwoDaysCount++;
			} else if (missedDays == 3) {
				missedVisitsThreeDaysCount++;
			} else if (missedDays > 3) {
				missedVisitsGtThreeDaysCount++;
			}
		}

		missedVisitsOneDay.updateCountLabel(missedVisitsOneDayCount);
		missedVisitsTwoDays.updateCountLabel(missedVisitsTwoDaysCount);
		missedVisitsThreeDays.updateCountLabel(missedVisitsThreeDaysCount);
		missedVisitsGtThreeDays.updateCountLabel(missedVisitsGtThreeDaysCount);
	}

	@Override
	protected void addFourthComponent() {
		fourthComponent = new DashboardStatisticsSubComponent();

		// Header
		fourthComponent.addHeader("Visits", null, true);

		// Content
		fourthComponent.addMainContent();
		unavailableVisits = new DashboardStatisticsGraphicalGrowthElement("Unavailable", CssStyles.SVG_FILL_MINOR);
		fourthComponent.addComponentToContent(unavailableVisits);
		uncooperativeVisits = new DashboardStatisticsGraphicalGrowthElement("Uncooperative", CssStyles.SVG_FILL_IMPORTANT);
		fourthComponent.addComponentToContent(uncooperativeVisits);
		cooperativeVisits = new DashboardStatisticsGraphicalGrowthElement("Cooperative", CssStyles.SVG_FILL_POSITIVE);
		fourthComponent.addComponentToContent(cooperativeVisits);
		Label separator = new Label("<hr/>", ContentMode.HTML);
		CssStyles.style(separator, CssStyles.VSPACE_TOP_3, CssStyles.VSPACE_3);
		fourthComponent.addComponentToContent(separator);
		missedVisits = new DashboardStatisticsGraphicalGrowthElement("Missed", CssStyles.SVG_FILL_CRITICAL);
		fourthComponent.addComponentToContent(missedVisits);
		
		subComponentsLayout.addComponent(fourthComponent, FOURTH_LOC);
	}

	@Override
	protected void updateFourthComponent(int visibleDiseasesCount) {
		List<DashboardContactDto> contacts = dashboardDataProvider.getContacts();
		List<DashboardContactDto> previousContacts = dashboardDataProvider.getPreviousContacts();
		List<DashboardVisitDto> visits = new ArrayList<>();
		List<DashboardVisitDto> previousVisits = new ArrayList<>();
		int doneEssentialVisitsCount = 0;	// only visits that needed to be done, i.e. at most the amount of follow-up days
		int previousDoneEssentialVisitsCount = 0;

		VisitFacade visitFacade = FacadeProvider.getVisitFacade();
		Date now = new Date();
		int totalFollowUpDays = 0;
		int previousTotalFollowUpDays = 0;
		for (DashboardContactDto contact : contacts) {
			List<DashboardVisitDto> visitsForContact = visitFacade.getDashboardVisitsByContact(new ContactReferenceDto(contact.getUuid()), dashboardDataProvider.getFromDate(), dashboardDataProvider.getToDate());
			visits.addAll(visitsForContact);
			if (contact.getFollowUpUntil() != null) {
				int contactFollowUpDays = Math.min(DateHelper.getDaysBetween(contact.getReportDate(), now), DateHelper.getDaysBetween(contact.getReportDate(), contact.getFollowUpUntil()));
				totalFollowUpDays += contactFollowUpDays;
				doneEssentialVisitsCount += visitsForContact.size() > contactFollowUpDays ? contactFollowUpDays : visitsForContact.size();
			}
		}

		for (DashboardContactDto contact : previousContacts) {
			List<DashboardVisitDto> visitsForContact = visitFacade.getDashboardVisitsByContact(new ContactReferenceDto(contact.getUuid()), dashboardDataProvider.getPreviousFromDate(), dashboardDataProvider.getPreviousToDate());
			previousVisits.addAll(visitsForContact);
			if (contact.getFollowUpUntil() != null) {
				int contactFollowUpDays = Math.min(DateHelper.getDaysBetween(contact.getReportDate(), now), DateHelper.getDaysBetween(contact.getReportDate(), contact.getFollowUpUntil()));
				previousTotalFollowUpDays += contactFollowUpDays;
				previousDoneEssentialVisitsCount += visitsForContact.size() > contactFollowUpDays ? contactFollowUpDays : visitsForContact.size();
			}
		}

		int visitsCount = visits.size();
		fourthComponent.updateCountLabel(visitsCount);

		int missedVisitsCount = totalFollowUpDays - doneEssentialVisitsCount;
		int unavailableVisitsCount = (int) visits.stream().filter(v -> v.getVisitStatus() == VisitStatus.UNAVAILABLE).count();
		int uncooperativeVisitsCount = (int) visits.stream().filter(v -> v.getVisitStatus() == VisitStatus.UNCOOPERATIVE).count();
		int cooperativeVisitsCount = (int) visits.stream().filter(v -> v.getVisitStatus() == VisitStatus.COOPERATIVE).count();
		int previousMissedVisitsCount = previousTotalFollowUpDays - previousDoneEssentialVisitsCount;
		int previousUnavailableVisitsCount = (int) previousVisits.stream().filter(v -> v.getVisitStatus() == VisitStatus.UNAVAILABLE).count();
		int previousUncooperativeVisitsCount = (int) previousVisits.stream().filter(v -> v.getVisitStatus() == VisitStatus.UNCOOPERATIVE).count();
		int previousCooperativeVisitsCount = (int) previousVisits.stream().filter(v -> v.getVisitStatus() == VisitStatus.COOPERATIVE).count();
		int missedVisitsPercentage = totalFollowUpDays == 0 ? 0 : (int) ((missedVisitsCount * 100.0f) / totalFollowUpDays);
		int unavailableVisitsPercentage = visitsCount == 0 ? 0 : (int) ((unavailableVisitsCount * 100.0f) / visitsCount);
		int uncooperativeVisitsPercentage = visitsCount == 0 ? 0 : (int) ((uncooperativeVisitsCount * 100.0f) / visitsCount);
		int cooperativeVisitsPercentage = visitsCount == 0 ? 0 : (int) ((cooperativeVisitsCount * 100.0f) / visitsCount);

		int missedVisitsGrowth = calculateGrowth(missedVisitsCount, previousMissedVisitsCount);
		int unavailableVisitsGrowth = calculateGrowth(unavailableVisitsCount, previousUnavailableVisitsCount);
		int uncooperativeVisitsGrowth = calculateGrowth(uncooperativeVisitsCount, previousUncooperativeVisitsCount);
		int cooperativeVisitsGrowth = calculateGrowth(cooperativeVisitsCount, previousCooperativeVisitsCount);

		missedVisits.update(missedVisitsCount, missedVisitsPercentage, missedVisitsGrowth, false, false);
		unavailableVisits.update(unavailableVisitsCount, unavailableVisitsPercentage, unavailableVisitsGrowth, false, false);
		uncooperativeVisits.update(uncooperativeVisitsCount, uncooperativeVisitsPercentage, uncooperativeVisitsGrowth, false, false);
		cooperativeVisits.update(cooperativeVisitsCount, cooperativeVisitsPercentage, cooperativeVisitsGrowth, true, false);
	}

	@Override
	protected int getFullHeight() {
		return 430;
	}

	@Override
	protected int getNormalHeight() {
		return 320;
	}

	@Override
	protected int getFilteredHeight() {
		return 320;
	}

}
