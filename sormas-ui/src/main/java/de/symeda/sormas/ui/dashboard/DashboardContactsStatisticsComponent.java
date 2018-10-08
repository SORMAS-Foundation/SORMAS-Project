package de.symeda.sormas.ui.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
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
import de.symeda.sormas.ui.dashboard.SvgCircleElement.SvgCircleElementPart;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardContactsStatisticsComponent extends AbstractDashboardStatisticsComponent {

	// "All Contacts" elements
	private DashboardStatisticsCountElement newContacts;
	private DashboardStatisticsCountElement symptomaticContacts;
	private DashboardStatisticsCountElement contactStatusActive;
	private DashboardStatisticsCountElement contactStatusConverted;
	private DashboardStatisticsCountElement contactStatusDropped;
	private DashboardStatisticsCountElement contactClassificationUnconfirmed;
	private DashboardStatisticsCountElement contactClassificationConfirmed;
	private DashboardStatisticsCountElement contactClassificationNotAContact;

	// "Follow-up Status" elements
	private CssLayout followUpStatusCountLayout;
	private DashboardStatisticsCountGrowthElement underFollowUp;
	private DashboardStatisticsCountGrowthElement lostToFollowUp;
	private DashboardStatisticsCountGrowthElement followUpCompleted;
	private DashboardStatisticsCountGrowthElement followUpCanceled;
	private SvgCircleElement followUpStatusCircleGraph;
	private DashboardStatisticsGraphicalGrowthElement underFollowUpGrowthElement;
	private DashboardStatisticsGraphicalGrowthElement lostToFollowUpGrowthElement;
	private DashboardStatisticsGraphicalGrowthElement followUpCompletedGrowthElement;
	private DashboardStatisticsGraphicalGrowthElement followUpCanceledGrowthElement;

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
	private DashboardStatisticsComparisonElement completedAndMissedVisits;
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

		// Count layout
		CssLayout countLayout = firstComponent.createCountLayout(true);
		newContacts = new DashboardStatisticsCountElement("New", CountElementStyle.NEUTRAL);
		firstComponent.addComponentToCountLayout(countLayout, newContacts);
		symptomaticContacts = new DashboardStatisticsCountElement("Symptomatic", CountElementStyle.CRITICAL);
		firstComponent.addComponentToCountLayout(countLayout, symptomaticContacts);
		firstComponent.addComponent(countLayout);

		// Content
		firstComponent.addMainContent();

		Label contactStatusLabel = new Label("Contact Status");
		CssStyles.style(contactStatusLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_5, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT, CssStyles.LABEL_ROUNDED_CORNERS_SLIM);
		firstComponent.addComponentToContent(contactStatusLabel);

		CssLayout contactStatusCountLayout = firstComponent.createCountLayout(true);
		contactStatusActive = new DashboardStatisticsCountElement("Active", CountElementStyle.PRIMARY);
		firstComponent.addComponentToCountLayout(contactStatusCountLayout, contactStatusActive);
		contactStatusConverted = new DashboardStatisticsCountElement("Converted to case", CountElementStyle.PRIMARY);
		firstComponent.addComponentToCountLayout(contactStatusCountLayout, contactStatusConverted);
		contactStatusDropped = new DashboardStatisticsCountElement("Dropped", CountElementStyle.PRIMARY);
		firstComponent.addComponentToCountLayout(contactStatusCountLayout, contactStatusDropped);
		firstComponent.addComponentToContent(contactStatusCountLayout);

		Label contactClassificationLabel = new Label("Contact Classification");
		CssStyles.style(contactClassificationLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_5, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT, CssStyles.LABEL_ROUNDED_CORNERS_SLIM);
		firstComponent.addComponentToContent(contactClassificationLabel);

		CssLayout contactClassificationCountLayout = firstComponent.createCountLayout(true);
		contactClassificationUnconfirmed = new DashboardStatisticsCountElement("Unconfirmed", CountElementStyle.MINOR);
		firstComponent.addComponentToCountLayout(contactClassificationCountLayout, contactClassificationUnconfirmed);
		contactClassificationConfirmed = new DashboardStatisticsCountElement("Confirmed", CountElementStyle.CRITICAL);
		firstComponent.addComponentToCountLayout(contactClassificationCountLayout, contactClassificationConfirmed);
		contactClassificationNotAContact = new DashboardStatisticsCountElement("Not a contact", CountElementStyle.POSITIVE);
		firstComponent.addComponentToCountLayout(contactClassificationCountLayout, contactClassificationNotAContact);
		firstComponent.addComponentToContent(contactClassificationCountLayout);

		subComponentsLayout.addComponent(firstComponent, FIRST_LOC);
	}

	@Override
	protected void updateFirstComponent(int visibleDiseasesCount) {
		List<DashboardContactDto> dashboardContactDtos = dashboardDataProvider.getContacts();

		int contactsCount = dashboardContactDtos.size();
		firstComponent.updateCountLabel(contactsCount);

		int newContactsCount = (int) dashboardContactDtos.stream()
				.filter(
						c -> c.getReportDate().after(dashboardDataProvider.getFromDate())
						|| c.getReportDate().equals(dashboardDataProvider.getFromDate()))
				.count();
		newContacts.updateCountLabel(newContactsCount);
		int symptomaticContactsCount = (int) dashboardContactDtos.stream().filter(c -> c.isSymptomatic()).count();
		int symptomaticContactsPercentage = contactsCount == 0 ? 0 : (int) ((symptomaticContactsCount * 100.0f) / contactsCount);
		symptomaticContacts.updateCountLabel(symptomaticContactsCount + " (" + symptomaticContactsPercentage + " %)");

		int contactStatusActiveCount = (int) dashboardContactDtos.stream().filter(c -> c.getContactStatus() == ContactStatus.ACTIVE).count();
		int contactStatusActivePercentage = contactsCount == 0 ? 0 : (int) ((contactStatusActiveCount * 100.0f) / contactsCount);
		contactStatusActive.updateCountLabel(contactStatusActiveCount + " (" + contactStatusActivePercentage + " %)");
		int contactStatusConvertedCount = (int) dashboardContactDtos.stream().filter(c -> c.getContactStatus() == ContactStatus.CONVERTED).count();
		int contactStatusConvertedPercentage = contactsCount == 0 ? 0 : (int) ((contactStatusConvertedCount * 100.0f) / contactsCount);
		contactStatusConverted.updateCountLabel(contactStatusConvertedCount + " (" + contactStatusConvertedPercentage + " %)");
		int contactStatusDroppedCount = (int) dashboardContactDtos.stream().filter(c -> c.getContactStatus() == ContactStatus.DROPPED).count();
		int contactStatusDroppedPercentage = contactsCount == 0 ? 0 : (int) ((contactStatusDroppedCount * 100.0f) / contactsCount);
		contactStatusDropped.updateCountLabel(contactStatusDroppedCount + " (" + contactStatusDroppedPercentage + " %)");

		int contactClassificationUnconfirmedCount = (int) dashboardContactDtos.stream().filter(c -> c.getContactClassification() == ContactClassification.UNCONFIRMED).count();
		int contactClassificationUnconfirmedPercentage = contactsCount == 0 ? 0 : (int) ((contactClassificationUnconfirmedCount * 100.0f) / contactsCount);
		contactClassificationUnconfirmed.updateCountLabel(contactClassificationUnconfirmedCount + " (" + contactClassificationUnconfirmedPercentage + " %)");
		int contactClassificationConfirmedCount = (int) dashboardContactDtos.stream().filter(c -> c.getContactClassification() == ContactClassification.CONFIRMED).count();
		int contactClassificationConfirmedPercentage = contactsCount == 0 ? 0 : (int) ((contactClassificationConfirmedCount * 100.0f) / contactsCount);
		contactClassificationConfirmed.updateCountLabel(contactClassificationConfirmedCount + " (" + contactClassificationConfirmedPercentage + " %)");
		int contactClassificationNotAContactCount = (int) dashboardContactDtos.stream().filter(c -> c.getContactClassification() == ContactClassification.NO_CONTACT).count();
		int contactClassificationNotAContactPercentage = contactsCount == 0 ? 0 : (int) ((contactClassificationNotAContactCount * 100.0f) / contactsCount);
		contactClassificationNotAContact.updateCountLabel(contactClassificationNotAContactCount + " (" + contactClassificationNotAContactPercentage + " %)");
	}


	@Override
	protected void addSecondComponent() {
		secondComponent = new DashboardStatisticsSubComponent();

		// Header
		secondComponent.addHeader("Follow-up Status", null, true);

		// Content
		secondComponent.addMainContent();
		followUpStatusCountLayout = secondComponent.createCountLayout(false);
		underFollowUp = new DashboardStatisticsCountGrowthElement("Under F/U", CountElementStyle.PRIMARY);
		lostToFollowUp = new DashboardStatisticsCountGrowthElement("Lost To F/U", CountElementStyle.CRITICAL);
		followUpCompleted = new DashboardStatisticsCountGrowthElement("Completed F/U", CountElementStyle.POSITIVE);
		followUpCanceled = new DashboardStatisticsCountGrowthElement("Canceled F/U", CountElementStyle.IMPORTANT);
		secondComponent.addComponentToCountLayout(followUpStatusCountLayout, underFollowUp);
		secondComponent.addComponentToCountLayout(followUpStatusCountLayout, lostToFollowUp);
		secondComponent.addComponentToCountLayout(followUpStatusCountLayout, followUpCompleted);
		secondComponent.addComponentToCountLayout(followUpStatusCountLayout, followUpCanceled);
		secondComponent.addComponentToContent(followUpStatusCountLayout);

		followUpStatusCircleGraph = new SvgCircleElement(false);
		underFollowUpGrowthElement = new DashboardStatisticsGraphicalGrowthElement("Under F/U", CssStyles.SVG_FILL_PRIMARY);
		lostToFollowUpGrowthElement = new DashboardStatisticsGraphicalGrowthElement("Lost To F/U", CssStyles.SVG_FILL_CRITICAL);
		followUpCompletedGrowthElement = new DashboardStatisticsGraphicalGrowthElement("Completed F/U", CssStyles.SVG_FILL_POSITIVE);
		followUpCanceledGrowthElement = new DashboardStatisticsGraphicalGrowthElement("Canceled F/U", CssStyles.SVG_FILL_IMPORTANT);

		subComponentsLayout.addComponent(secondComponent, SECOND_LOC);
	}

	@Override
	protected void updateSecondComponent(int visibleDiseasesCount) {
		List<DashboardContactDto> contacts = dashboardDataProvider.getContacts();
		List<DashboardContactDto> previousContacts = dashboardDataProvider.getPreviousContacts();

		int contactsCount = contacts.size();
		secondComponent.updateCountLabel(contactsCount);

		// Remove and re-create content layout if the disease filter has been applied or set to null
		if ((currentDisease == null && previousDisease != null) || (previousDisease == null && currentDisease != null)) {
			secondComponent.removeContent();
			if (currentDisease != null) {
				secondComponent.addTwoColumnsMainContent(true, 60);
				secondComponent.addComponentToRightContentColumn(followUpStatusCircleGraph);
				secondComponent.addComponentToLeftContentColumn(underFollowUpGrowthElement);
				secondComponent.addComponentToLeftContentColumn(lostToFollowUpGrowthElement);
				secondComponent.addComponentToLeftContentColumn(followUpCompletedGrowthElement);
				secondComponent.addComponentToLeftContentColumn(followUpCanceledGrowthElement);
			} else {
				secondComponent.addMainContent();
				secondComponent.addComponentToContent(followUpStatusCountLayout);
			}
		}

		int underFollowUpCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.FOLLOW_UP).count();
		int previousUnderFollowUpCount = (int) previousContacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.FOLLOW_UP).count();
		int lostToFollowUpCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.LOST).count();
		int previousLostToFollowUpCount = (int) previousContacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.LOST).count();
		int followUpCompletedCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.COMPLETED).count();
		int previousFollowUpCompletedCount = (int) previousContacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.COMPLETED).count();
		int followUpCanceledCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.CANCELED).count();
		int previousFollowUpCanceledCount = (int) previousContacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.CANCELED).count();

		float underFollowUpGrowth = underFollowUpCount == 0 ? previousUnderFollowUpCount > 0 ? -100 : 0 : previousUnderFollowUpCount == 0 ? underFollowUpCount > 0 ? Float.MIN_VALUE : 0 : new BigDecimal(underFollowUpCount).subtract(new BigDecimal(previousUnderFollowUpCount)).divide(new BigDecimal(underFollowUpCount), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();
		float lostToFollowUpGrowth = lostToFollowUpCount == 0 ? previousLostToFollowUpCount > 0 ? -100 : 0 : previousLostToFollowUpCount == 0 ? lostToFollowUpCount > 0 ? Float.MIN_VALUE : 0 : new BigDecimal(lostToFollowUpCount).subtract(new BigDecimal(previousLostToFollowUpCount)).divide(new BigDecimal(lostToFollowUpCount), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();
		float followUpCompletedGrowth = followUpCompletedCount == 0 ? previousFollowUpCompletedCount > 0 ? -100 : 0 : previousFollowUpCompletedCount == 0 ? followUpCompletedCount > 0 ? Float.MIN_VALUE : 0 : new BigDecimal(followUpCompletedCount).subtract(new BigDecimal(previousFollowUpCompletedCount)).divide(new BigDecimal(followUpCompletedCount), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();
		float followUpCanceledGrowth = followUpCanceledCount == 0 ? previousFollowUpCanceledCount > 0 ? -100 : 0 : previousFollowUpCanceledCount == 0 ? followUpCanceledCount > 0 ? Float.MIN_VALUE : 0 : new BigDecimal(followUpCanceledCount).subtract(new BigDecimal(previousFollowUpCanceledCount)).divide(new BigDecimal(followUpCanceledCount), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();

		if (currentDisease == null) {
			// Remove all children of the content layout
			secondComponent.removeAllComponentsFromContent();
			secondComponent.addComponentToContent(followUpStatusCountLayout);

			underFollowUp.update(underFollowUpCount, underFollowUpGrowth, true);
			lostToFollowUp.update(lostToFollowUpCount, lostToFollowUpGrowth, false);
			followUpCompleted.update(followUpCompletedCount, followUpCompletedGrowth, true);
			followUpCanceled.update(followUpCanceledCount, followUpCanceledGrowth, false);

			// Create a map with all diseases as keys and their respective case counts as values
			Map<Disease, Integer> diseaseMap = new TreeMap<Disease, Integer>();
			for (Disease disease : Disease.values()) {
				diseaseMap.put(disease, (int) contacts.stream().filter(c -> c.getDisease() == disease).count());
			}

			// Create a list from this map that sorts the entries by case counts
			List<Map.Entry<Disease, Integer>> sortedDiseaseList = createSortedDiseaseList(diseaseMap);

			// Create a new StatisticsDiseaseElement for every disease, automatically sorting them by case count
			for (int i = 0; i < visibleDiseasesCount; i++) {
				Map.Entry<Disease, Integer> mapEntry = sortedDiseaseList.get(i);
				int previousDiseaseCount = (int) previousContacts.stream().filter(c -> c.getDisease() == mapEntry.getKey()).count();
				DashboardStatisticsDiseaseElement diseaseElement = new DashboardStatisticsDiseaseElement(mapEntry.getKey().toString(), mapEntry.getValue(), previousDiseaseCount);
				secondComponent.addComponentToContent(diseaseElement);
			}
		} else {
			int underFollowUpPercentage = contactsCount == 0 ? 0 : (int) ((underFollowUpCount * 100.0f) / contactsCount);
			int lostToFollowUpPercentage = contactsCount == 0 ? 0 : (int) ((lostToFollowUpCount * 100.0f) / contactsCount);
			int followUpCompletedPercentage = contactsCount == 0 ? 0 : (int) ((followUpCompletedCount * 100.0f) / contactsCount);
			int followUpCanceledPercentage = contactsCount == 0 ? 0 : (int) ((followUpCanceledCount * 100.0f) / contactsCount);
			SvgCircleElementPart underFollowUpPart = followUpStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_PRIMARY, underFollowUpPercentage);
			SvgCircleElementPart lostToFollowUpPart = followUpStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_CRITICAL, lostToFollowUpPercentage);
			SvgCircleElementPart followUpCompletedPart = followUpStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_POSITIVE, followUpCompletedPercentage);
			SvgCircleElementPart followUpCanceledPart = followUpStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_IMPORTANT, followUpCanceledPercentage);
			followUpStatusCircleGraph.updateSvg(underFollowUpPart, lostToFollowUpPart, followUpCompletedPart, followUpCanceledPart);
			underFollowUpGrowthElement.update(underFollowUpCount, underFollowUpPercentage, underFollowUpGrowth, true);
			lostToFollowUpGrowthElement.update(lostToFollowUpCount, lostToFollowUpPercentage, lostToFollowUpGrowth, false);
			followUpCompletedGrowthElement.update(followUpCompletedCount, followUpCompletedPercentage, followUpCompletedGrowth, true);
			followUpCanceledGrowthElement.update(followUpCanceledCount, followUpCanceledPercentage, followUpCanceledGrowth, false);
		}
	}

	@Override
	protected void addThirdComponent() {
		thirdComponent = new DashboardStatisticsSubComponent();

		// Header
		thirdComponent.addHeader("Follow-up Situation", null, true);

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

		int contactsCount = contacts.size();
		thirdComponent.updateCountLabel(contactsCount);

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
		fourthComponent.addHeader("Follow-up Visits", null, false);

		// Content
		fourthComponent.addMainContent();
		completedAndMissedVisits = new DashboardStatisticsComparisonElement("Visits", "Missed");
		CssStyles.style(completedAndMissedVisits, CssStyles.VSPACE_2);
		fourthComponent.addComponentToContent(completedAndMissedVisits, Alignment.MIDDLE_RIGHT);
		unavailableVisits = new DashboardStatisticsGraphicalGrowthElement("Unavailable", CssStyles.SVG_FILL_MINOR);
		fourthComponent.addComponentToContent(unavailableVisits);
		uncooperativeVisits = new DashboardStatisticsGraphicalGrowthElement("Uncooperative", CssStyles.SVG_FILL_CRITICAL);
		fourthComponent.addComponentToContent(uncooperativeVisits);
		cooperativeVisits = new DashboardStatisticsGraphicalGrowthElement("Cooperative", CssStyles.SVG_FILL_POSITIVE);
		fourthComponent.addComponentToContent(cooperativeVisits);

		subComponentsLayout.addComponent(fourthComponent, FOURTH_LOC);
	}


	@Override
	protected void updateFourthComponent(int visibleDiseasesCount) {
		List<DashboardContactDto> contacts = dashboardDataProvider.getContacts();
		List<DashboardContactDto> previousContacts = dashboardDataProvider.getPreviousContacts();
		List<DashboardVisitDto> visits = new ArrayList<>();
		List<DashboardVisitDto> previousVisits = new ArrayList<>();

		VisitFacade visitFacade = FacadeProvider.getVisitFacade();
		Date now = new Date();
		int totalFollowUpDays = 0;
		for (DashboardContactDto contact : contacts) {
			visits.addAll(visitFacade.getDashboardVisitsByContact(new ContactReferenceDto(contact.getUuid())));
			if (contact.getFollowUpUntil() != null) {
				totalFollowUpDays += Math.min(DateHelper.getDaysBetween(contact.getReportDate(), now), DateHelper.getDaysBetween(contact.getReportDate(), contact.getFollowUpUntil()));
			}
		}
		for (DashboardContactDto contact : previousContacts) {
			previousVisits.addAll(visitFacade.getDashboardVisitsByContact(new ContactReferenceDto(contact.getUuid())));
		}
		int visitsCount = visits.size();

		int missedVisitsCount = totalFollowUpDays - visitsCount;
		int missedVisitsPercentage = totalFollowUpDays == 0 ? 0 : (int) ((missedVisitsCount * 100.0f) / totalFollowUpDays);

		completedAndMissedVisits.update(visitsCount, missedVisitsCount, null, missedVisitsPercentage + "%");

		int unavailableVisitsCount = (int) visits.stream().filter(v -> v.getVisitStatus() == VisitStatus.UNAVAILABLE).count();
		int uncooperativeVisitsCount = (int) visits.stream().filter(v -> v.getVisitStatus() == VisitStatus.UNCOOPERATIVE).count();
		int cooperativeVisitsCount = (int) visits.stream().filter(v -> v.getVisitStatus() == VisitStatus.COOPERATIVE).count();
		int previousUnavailableVisitsCount = (int) previousVisits.stream().filter(v -> v.getVisitStatus() == VisitStatus.UNAVAILABLE).count();
		int previousUncooperativeVisitsCount = (int) previousVisits.stream().filter(v -> v.getVisitStatus() == VisitStatus.UNCOOPERATIVE).count();
		int previousCooperativeVisitsCount = (int) previousVisits.stream().filter(v -> v.getVisitStatus() == VisitStatus.COOPERATIVE).count();
		int unavailableVisitsPercentage = visitsCount == 0 ? 0 : (int) ((unavailableVisitsCount * 100.0f) / visitsCount);
		int uncooperativeVisitsPercentage = visitsCount == 0 ? 0 : (int) ((uncooperativeVisitsCount * 100.0f) / visitsCount);
		int cooperativeVisitsPercentage = visitsCount == 0 ? 0 : (int) ((cooperativeVisitsCount * 100.0f) / visitsCount);

		float unavailableVisitsGrowth = unavailableVisitsCount == 0 ? previousUnavailableVisitsCount > 0 ? -100 : 0 : previousUnavailableVisitsCount == 0 ? unavailableVisitsCount > 0 ? Float.MIN_VALUE : 0 : new BigDecimal(unavailableVisitsCount).subtract(new BigDecimal(previousUnavailableVisitsCount)).divide(new BigDecimal(unavailableVisitsCount), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();
		float uncooperativeVisitsGrowth = uncooperativeVisitsCount == 0 ? previousUncooperativeVisitsCount > 0 ? -100 : 0 : previousUncooperativeVisitsCount == 0 ? uncooperativeVisitsCount > 0 ? Float.MIN_VALUE : 0 : new BigDecimal(uncooperativeVisitsCount).subtract(new BigDecimal(previousUnavailableVisitsCount)).divide(new BigDecimal(uncooperativeVisitsCount), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();
		float cooperativeVisitsGrowth = cooperativeVisitsCount == 0 ? previousCooperativeVisitsCount > 0 ? -100 : 0 : previousCooperativeVisitsCount == 0 ? cooperativeVisitsCount > 0 ? Float.MIN_VALUE : 0 : new BigDecimal(cooperativeVisitsCount).subtract(new BigDecimal(previousCooperativeVisitsCount)).divide(new BigDecimal(cooperativeVisitsCount), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();

		unavailableVisits.update(unavailableVisitsCount, unavailableVisitsPercentage, unavailableVisitsGrowth, false);
		uncooperativeVisits.update(uncooperativeVisitsCount, uncooperativeVisitsPercentage, uncooperativeVisitsGrowth, false);
		cooperativeVisits.update(cooperativeVisitsCount, cooperativeVisitsPercentage, cooperativeVisitsGrowth, true);
	}

}
