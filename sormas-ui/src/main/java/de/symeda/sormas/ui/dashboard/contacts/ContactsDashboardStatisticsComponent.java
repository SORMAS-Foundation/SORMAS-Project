/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard.contacts;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.DashboardContactDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
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
public class ContactsDashboardStatisticsComponent extends AbstractDashboardStatisticsComponent {

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
	private DashboardStatisticsCountElement cooperativeContacts;
	private DashboardStatisticsCountElement uncooperativeContacts;
	private DashboardStatisticsCountElement unavailableContacts;
	private DashboardStatisticsCountElement neverVisitedContacts;
	private DashboardStatisticsCountElement missedVisitsOneDay;
	private DashboardStatisticsCountElement missedVisitsTwoDays;
	private DashboardStatisticsCountElement missedVisitsThreeDays;
	private DashboardStatisticsCountElement missedVisitsGtThreeDays;

	// "Stopped Follow-up" elements
	private DashboardStatisticsPercentageElement followUpCompleted;
	private DashboardStatisticsPercentageElement followUpCanceled;
	private DashboardStatisticsPercentageElement lostToFollowUp;
	private DashboardStatisticsPercentageElement contactStatusConverted;

	// "Follow-Up Visits" elements
	private DashboardStatisticsGraphicalGrowthElement missedVisits;
	private DashboardStatisticsGraphicalGrowthElement unavailableVisits;
	private DashboardStatisticsGraphicalGrowthElement uncooperativeVisits;
	private DashboardStatisticsGraphicalGrowthElement cooperativeVisits;

	public ContactsDashboardStatisticsComponent(DashboardDataProvider dashboardDataProvider) {
		super(dashboardDataProvider);
	}

	@Override
	protected void addFirstComponent() {
		firstComponent = new DashboardStatisticsSubComponent();

		// Header
		firstComponent.addHeader(I18nProperties.getString(Strings.headingAllContacts), null, true);

		// Content
		firstComponent.addMainContent();

		allContactsCountLayout = firstComponent.createCountLayout(true);
		contactClassificationUnconfirmed =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardUnconfirmed), CountElementStyle.MINOR);
		contactClassificationConfirmed =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardConfirmed), CountElementStyle.PRIMARY);
		contactClassificationNotAContact =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotAContact), CountElementStyle.POSITIVE);
		newContacts = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNew), CountElementStyle.NEUTRAL);
		symptomaticContacts =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardSymptomatic), CountElementStyle.CRITICAL);
		firstComponent.addComponentToCountLayout(allContactsCountLayout, contactClassificationUnconfirmed);
		firstComponent.addComponentToCountLayout(allContactsCountLayout, contactClassificationConfirmed);
		firstComponent.addComponentToCountLayout(allContactsCountLayout, contactClassificationNotAContact);
		firstComponent.addComponentToCountLayout(allContactsCountLayout, newContacts);
		firstComponent.addComponentToCountLayout(allContactsCountLayout, symptomaticContacts);
		firstComponent.addComponentToContent(allContactsCountLayout);

		contactClassificationUnconfirmedLarge =
			new DashboardStatisticsPercentageElement(I18nProperties.getCaption(Captions.dashboardUnconfirmed), CssStyles.SVG_FILL_MINOR);
		contactClassificationConfirmedLarge =
			new DashboardStatisticsPercentageElement(I18nProperties.getCaption(Captions.dashboardConfirmed), CssStyles.SVG_FILL_CRITICAL);
		contactClassificationNotAContactLarge =
			new DashboardStatisticsPercentageElement(I18nProperties.getCaption(Captions.dashboardNotAContact), CssStyles.SVG_FILL_POSITIVE);
		newContactsLarge = new DashboardStatisticsPercentageElement(I18nProperties.getCaption(Captions.dashboardNew), CssStyles.SVG_FILL_NEUTRAL);
		symptomaticContactsLarge =
			new DashboardStatisticsPercentageElement(I18nProperties.getCaption(Captions.dashboardSymptomatic), CssStyles.SVG_FILL_CRITICAL);

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
				c -> c.getReportDate().after(dashboardDataProvider.getFromDate()) || c.getReportDate().equals(dashboardDataProvider.getFromDate()))
			.count();
		int newContactsPercentage = contactsCount == 0 ? 0 : (int) ((newContactsCount * 100.0f) / contactsCount);
		int symptomaticContactsCount = (int) contacts.stream().filter(c -> Boolean.TRUE.equals(c.getSymptomatic())).count();
		int symptomaticContactsPercentage = contactsCount == 0 ? 0 : (int) ((symptomaticContactsCount * 100.0f) / contactsCount);
		int contactClassificationUnconfirmedCount =
			(int) contacts.stream().filter(c -> c.getContactClassification() == ContactClassification.UNCONFIRMED).count();
		int contactClassificationUnconfirmedPercentage =
			contactsCount == 0 ? 0 : (int) ((contactClassificationUnconfirmedCount * 100.0f) / contactsCount);
		int contactClassificationConfirmedCount =
			(int) contacts.stream().filter(c -> c.getContactClassification() == ContactClassification.CONFIRMED).count();
		int contactClassificationConfirmedPercentage =
			contactsCount == 0 ? 0 : (int) ((contactClassificationConfirmedCount * 100.0f) / contactsCount);
		int contactClassificationNotAContactCount =
			(int) contacts.stream().filter(c -> c.getContactClassification() == ContactClassification.NO_CONTACT).count();
		int contactClassificationNotAContactPercentage =
			contactsCount == 0 ? 0 : (int) ((contactClassificationNotAContactCount * 100.0f) / contactsCount);

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

			contactClassificationUnconfirmed
				.updateCountLabel(contactClassificationUnconfirmedCount + " (" + contactClassificationUnconfirmedPercentage + " %)");
			contactClassificationConfirmed
				.updateCountLabel(contactClassificationConfirmedCount + " (" + contactClassificationConfirmedPercentage + " %)");
			contactClassificationNotAContact
				.updateCountLabel(contactClassificationNotAContactCount + " (" + contactClassificationNotAContactPercentage + " %)");
			newContacts.updateCountLabel(newContactsCount);
			symptomaticContacts.updateCountLabel(symptomaticContactsCount + " (" + symptomaticContactsPercentage + " %)");

			// Create a map with all diseases as keys and their respective case counts as values
			Map<Disease, Integer> diseaseMap = new TreeMap<Disease, Integer>();
			for (Disease disease : FacadeProvider.getDiseaseConfigurationFacade().getAllDiseasesWithFollowUp()) {
				diseaseMap.put(disease, (int) contacts.stream().filter(c -> c.getDisease() == disease).count());
			}

			// Create a list from this map that sorts the entries by case counts
			List<Map.Entry<Disease, Integer>> sortedDiseaseList = createSortedDiseaseList(diseaseMap);

			// Create a new StatisticsDiseaseElement for every disease, automatically sorting them by case count
			for (int i = 0; i < (visibleDiseasesCount <= sortedDiseaseList.size() ? visibleDiseasesCount : sortedDiseaseList.size()); i++) {

				Map.Entry<Disease, Integer> mapEntry = sortedDiseaseList.get(i);
				int previousDiseaseCount = (int) previousContacts.stream().filter(c -> c.getDisease() == mapEntry.getKey()).count();
				DashboardStatisticsDiseaseElement diseaseElement =
					new DashboardStatisticsDiseaseElement(mapEntry.getKey().toString(), mapEntry.getValue(), previousDiseaseCount);
				firstComponent.addComponentToContent(diseaseElement);
			}
		} else {
			contactClassificationUnconfirmedLarge
				.updatePercentageValueWithCount(contactClassificationUnconfirmedCount, contactClassificationUnconfirmedPercentage);
			contactClassificationConfirmedLarge
				.updatePercentageValueWithCount(contactClassificationConfirmedCount, contactClassificationConfirmedPercentage);
			contactClassificationNotAContactLarge
				.updatePercentageValueWithCount(contactClassificationNotAContactCount, contactClassificationNotAContactPercentage);
			newContactsLarge.updatePercentageValueWithCount(newContactsCount, newContactsPercentage);
			symptomaticContactsLarge.updatePercentageValueWithCount(symptomaticContactsCount, symptomaticContactsPercentage);
		}
	}

	@Override
	protected void addSecondComponent() {
		secondComponent = new DashboardStatisticsSubComponent();

		// Header
		secondComponent.addHeader(I18nProperties.getString(Strings.headingUnderFollowUp), null, true);

		// Content
		secondComponent.addMainContent();

		CssLayout visitStatusCountLayout = secondComponent.createCountLayout(true);
		cooperativeContacts =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardCooperative), CountElementStyle.POSITIVE);
		secondComponent.addComponentToCountLayout(visitStatusCountLayout, cooperativeContacts);
		uncooperativeContacts =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardUncooperative), CountElementStyle.CRITICAL);
		secondComponent.addComponentToCountLayout(visitStatusCountLayout, uncooperativeContacts);
		unavailableContacts =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardUnavailable), CountElementStyle.RELEVANT);
		secondComponent.addComponentToCountLayout(visitStatusCountLayout, unavailableContacts);
		neverVisitedContacts =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNeverVisited), CountElementStyle.MINOR);
		secondComponent.addComponentToCountLayout(visitStatusCountLayout, neverVisitedContacts);

		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription(I18nProperties.getDescription(Descriptions.descDashboardFollowUpInfo));
		CssStyles.style(infoLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_SECONDARY, "follow-up-status-info-button");
		secondComponent.addComponentToCountLayout(visitStatusCountLayout, infoLabel);

		secondComponent.addComponentToContent(visitStatusCountLayout);

		// Number of missed visits
		Label missedVisitsLabel = new Label(I18nProperties.getCaption(Captions.dashboardNotVisitedFor));
		CssStyles.style(
			missedVisitsLabel,
			CssStyles.LABEL_BOLD,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_UPPERCASE,
			CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT,
			CssStyles.LABEL_ROUNDED_CORNERS_SLIM);
		secondComponent.addComponentToContent(missedVisitsLabel);

		CssLayout missedVisitsCountLayout = secondComponent.createCountLayout(false);
		missedVisitsOneDay = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardOneDay), CountElementStyle.PRIMARY);
		secondComponent.addComponentToCountLayout(missedVisitsCountLayout, missedVisitsOneDay);
		missedVisitsTwoDays = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardTwoDays), CountElementStyle.PRIMARY);
		secondComponent.addComponentToCountLayout(missedVisitsCountLayout, missedVisitsTwoDays);
		missedVisitsThreeDays =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardThreeDays), CountElementStyle.PRIMARY);
		secondComponent.addComponentToCountLayout(missedVisitsCountLayout, missedVisitsThreeDays);
		missedVisitsGtThreeDays =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardGtThreeDays), CountElementStyle.PRIMARY);
		secondComponent.addComponentToCountLayout(missedVisitsCountLayout, missedVisitsGtThreeDays);
		secondComponent.addComponentToContent(missedVisitsCountLayout);

		subComponentsLayout.addComponent(secondComponent, SECOND_LOC);
	}

	@Override
	protected void updateSecondComponent(int visibleDiseasesCount) {
		List<DashboardContactDto> contacts = new ArrayList<>();
		contacts.addAll(dashboardDataProvider.getContacts());
		contacts.removeIf(c -> c.getFollowUpStatus() != FollowUpStatus.FOLLOW_UP);

		int contactsCount = contacts.size();
		secondComponent.updateCountLabel(contactsCount);

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
	protected void addThirdComponent() {
		thirdComponent = new DashboardStatisticsSubComponent();

		// Header
		thirdComponent.addHeader(I18nProperties.getString(Strings.headingStoppedFollowUp), null, true);

		// Visit status of last visit
		thirdComponent.addMainContent();

		followUpCompleted =
			new DashboardStatisticsPercentageElement(I18nProperties.getCaption(Captions.dashboardCompletedFollowUp), CssStyles.SVG_FILL_POSITIVE);
		followUpCompleted.setDescription(FollowUpStatus.COMPLETED.getDescription());
		followUpCanceled =
			new DashboardStatisticsPercentageElement(I18nProperties.getCaption(Captions.dashboardCanceledFollowUp), CssStyles.SVG_FILL_IMPORTANT);
		followUpCanceled.setDescription(FollowUpStatus.CANCELED.getDescription());
		lostToFollowUp =
			new DashboardStatisticsPercentageElement(I18nProperties.getCaption(Captions.dashboardLostToFollowUp), CssStyles.SVG_FILL_CRITICAL);
		lostToFollowUp.setDescription(FollowUpStatus.LOST.getDescription());
		contactStatusConverted =
			new DashboardStatisticsPercentageElement(I18nProperties.getCaption(Captions.dashboardConvertedToCase), CssStyles.SVG_FILL_NEUTRAL);
		contactStatusConverted.setDescription(I18nProperties.getDescription(Descriptions.descDashboardConvertedToCase));
		thirdComponent.addComponentToContent(followUpCompleted);
		thirdComponent.addComponentToContent(followUpCanceled);
		thirdComponent.addComponentToContent(lostToFollowUp);
		thirdComponent.addComponentToContent(contactStatusConverted);

		subComponentsLayout.addComponent(thirdComponent, THIRD_LOC);
	}

	@Override
	protected void updateThirdComponent(int visibleDiseasesCount) {
		List<DashboardContactDto> contacts = new ArrayList<>();
		contacts.addAll(dashboardDataProvider.getContacts());
		contacts.removeIf(c -> c.getFollowUpStatus() == FollowUpStatus.NO_FOLLOW_UP || c.getFollowUpStatus() == FollowUpStatus.FOLLOW_UP);

		int contactsCount = contacts.size();
		thirdComponent.updateCountLabel(contactsCount);

		int followUpCompletedCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.COMPLETED).count();
		int followUpCompletedPercentage = contactsCount == 0 ? 0 : (int) ((followUpCompletedCount * 100.0f) / contactsCount);
		int followUpCanceledCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.CANCELED).count();
		int followUpCanceledPercentage = contactsCount == 0 ? 0 : (int) ((followUpCanceledCount * 100.0f) / contactsCount);
		int lostToFollowUpCount = (int) contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.LOST).count();
		int lostToFollowUpPercentage = contactsCount == 0 ? 0 : (int) ((lostToFollowUpCount * 100.0f) / contactsCount);
		int contactStatusConvertedCount = (int) contacts.stream().filter(c -> c.getContactStatus() == ContactStatus.CONVERTED).count();
		int contactStatusConvertedPercentage = contactsCount == 0 ? 0 : (int) ((contactStatusConvertedCount * 100.0f) / contactsCount);

		followUpCompleted.updatePercentageValueWithCount(followUpCompletedCount, followUpCompletedPercentage);
		followUpCanceled.updatePercentageValueWithCount(followUpCanceledCount, followUpCanceledPercentage);
		lostToFollowUp.updatePercentageValueWithCount(lostToFollowUpCount, lostToFollowUpPercentage);
		contactStatusConverted.updatePercentageValueWithCount(contactStatusConvertedCount, contactStatusConvertedPercentage);
	}

	@Override
	protected void addFourthComponent() {
		fourthComponent = new DashboardStatisticsSubComponent();

		// Header
		fourthComponent.addHeader(I18nProperties.getString(Strings.headingVisits), null, true);

		// Content
		fourthComponent.addMainContent();
		unavailableVisits =
			new DashboardStatisticsGraphicalGrowthElement(I18nProperties.getCaption(Captions.dashboardUnavailable), CssStyles.SVG_FILL_MINOR);
		fourthComponent.addComponentToContent(unavailableVisits);
		uncooperativeVisits =
			new DashboardStatisticsGraphicalGrowthElement(I18nProperties.getCaption(Captions.dashboardUncooperative), CssStyles.SVG_FILL_IMPORTANT);
		fourthComponent.addComponentToContent(uncooperativeVisits);
		cooperativeVisits =
			new DashboardStatisticsGraphicalGrowthElement(I18nProperties.getCaption(Captions.dashboardCooperative), CssStyles.SVG_FILL_POSITIVE);
		fourthComponent.addComponentToContent(cooperativeVisits);
		Label separator = new Label("<hr/>", ContentMode.HTML);
		CssStyles.style(separator, CssStyles.VSPACE_TOP_3, CssStyles.VSPACE_3);
		fourthComponent.addComponentToContent(separator);
		missedVisits =
			new DashboardStatisticsGraphicalGrowthElement(I18nProperties.getCaption(Captions.dashboardMissed), CssStyles.SVG_FILL_CRITICAL);
		fourthComponent.addComponentToContent(missedVisits);

		subComponentsLayout.addComponent(fourthComponent, FOURTH_LOC);
	}

	@Override
	protected void updateFourthComponent(int visibleDiseasesCount) {
		List<DashboardContactDto> contacts = dashboardDataProvider.getContacts();
		List<DashboardContactDto> previousContacts = dashboardDataProvider.getPreviousContacts();
		Map<VisitStatus, Long> visitStatusMap = new HashMap<>();
		Map<VisitStatus, Long> previousVisitStatusMap = new HashMap<>();
		int doneEssentialVisitsCount = 0;	// only visits that needed to be done, i.e. at most the amount of follow-up days
		int previousDoneEssentialVisitsCount = 0;

		Date now = new Date();
		int totalFollowUpDays = 0;
		int previousTotalFollowUpDays = 0;
		for (DashboardContactDto contact : contacts) {
			for (VisitStatus visitStatus : contact.getVisitStatusMap().keySet()) {
				Long value = 0L;
				if (visitStatusMap.containsKey(visitStatus)) {
					value = visitStatusMap.get(visitStatus);
				}
				visitStatusMap.put(visitStatus, value + contact.getVisitStatusMap().get(visitStatus));
			}
			if (contact.getFollowUpUntil() != null) {
				int contactFollowUpDays = Math.min(
					DateHelper.getDaysBetween(contact.getReportDate(), now),
					DateHelper.getDaysBetween(contact.getReportDate(), contact.getFollowUpUntil()));
				totalFollowUpDays += contactFollowUpDays;
				Long visitCount = contact.getVisitStatusMap().values().stream().reduce(0L, Long::sum);
				doneEssentialVisitsCount += (visitCount > contactFollowUpDays ? contactFollowUpDays : visitCount);
			}
		}

		for (DashboardContactDto contact : previousContacts) {
			for (VisitStatus visitStatus : contact.getVisitStatusMap().keySet()) {
				Long value = 0L;
				if (previousVisitStatusMap.containsKey(visitStatus)) {
					value = previousVisitStatusMap.get(visitStatus);
				}
				previousVisitStatusMap.put(visitStatus, value + contact.getVisitStatusMap().get(visitStatus));
			}
			if (contact.getFollowUpUntil() != null) {
				int contactFollowUpDays = Math.min(
					DateHelper.getDaysBetween(contact.getReportDate(), now),
					DateHelper.getDaysBetween(contact.getReportDate(), contact.getFollowUpUntil()));
				previousTotalFollowUpDays += contactFollowUpDays;
				Long visitCount = contact.getVisitStatusMap().values().stream().reduce(0L, Long::sum);
				previousDoneEssentialVisitsCount += (visitCount > contactFollowUpDays ? contactFollowUpDays : visitCount);
			}
		}

		Long visitsCount = visitStatusMap.values().stream().reduce(0L, Long::sum);
		fourthComponent.updateCountLabel(visitsCount.intValue());

		int missedVisitsCount = totalFollowUpDays - doneEssentialVisitsCount;
		int unavailableVisitsCount = Optional.ofNullable(visitStatusMap.get(VisitStatus.UNAVAILABLE)).orElse(0L).intValue();
		int uncooperativeVisitsCount = Optional.ofNullable(visitStatusMap.get(VisitStatus.UNCOOPERATIVE)).orElse(0L).intValue();
		int cooperativeVisitsCount = Optional.ofNullable(visitStatusMap.get(VisitStatus.COOPERATIVE)).orElse(0L).intValue();
		int previousMissedVisitsCount = previousTotalFollowUpDays - previousDoneEssentialVisitsCount;
		int previousUnavailableVisitsCount = Optional.ofNullable(previousVisitStatusMap.get(VisitStatus.UNAVAILABLE)).orElse(0L).intValue();
		int previousUncooperativeVisitsCount = Optional.ofNullable(previousVisitStatusMap.get(VisitStatus.UNCOOPERATIVE)).orElse(0L).intValue();
		int previousCooperativeVisitsCount = Optional.ofNullable(previousVisitStatusMap.get(VisitStatus.COOPERATIVE)).orElse(0L).intValue();
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
