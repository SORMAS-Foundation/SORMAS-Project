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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public class ContactsEpiCurveComponent extends AbstractEpiCurveComponent {

	private static final long serialVersionUID = 6582975657305031105L;

	private ContactsEpiCurveMode epiCurveContactsMode;

	public ContactsEpiCurveComponent(DashboardDataProvider dashboardDataProvider) {
		super(dashboardDataProvider);
	}

	@Override
	protected PopupButton createEpiCurveModeSelector() {
		if (epiCurveContactsMode == null) {
			epiCurveContactsMode = ContactsEpiCurveMode.FOLLOW_UP_STATUS;
			epiCurveLabel.setValue(I18nProperties.getCaption(Captions.dashboardFollowUpStatusChart));
		}

		VerticalLayout groupingLayout = new VerticalLayout();
		groupingLayout.setMargin(true);
		groupingLayout.setSizeUndefined();

		PopupButton dataDropdown = ButtonHelper.createPopupButton(Captions.dashboardData, groupingLayout, CssStyles.BUTTON_SUBTLE);

		OptionGroup dataSelect = new OptionGroup();
		dataSelect.setWidth(100, Unit.PERCENTAGE);
		dataSelect.addItems((Object[]) ContactsEpiCurveMode.values());
		dataSelect.setValue(epiCurveContactsMode);
		dataSelect.select(epiCurveContactsMode);
		dataSelect.addValueChangeListener(e -> {
			epiCurveContactsMode = (ContactsEpiCurveMode) e.getProperty().getValue();
			switch (epiCurveContactsMode) {
			case FOLLOW_UP_STATUS:
				epiCurveLabel.setValue(I18nProperties.getCaption(Captions.dashboardFollowUpStatusChart));
				break;
			case CONTACT_CLASSIFICATION:
				epiCurveLabel.setValue(I18nProperties.getCaption(Captions.dashboardContactClassificationChart));
				break;
			case FOLLOW_UP_UNTIL:
				epiCurveLabel.setValue(I18nProperties.getCaption(Captions.dashboardFollowUpUntilChart));
				break;
			}
			clearAndFillEpiCurveChart();
		});
		groupingLayout.addComponent(dataSelect);

		return dataDropdown;
	}

	@Override
	public void clearAndFillEpiCurveChart() {

		//@formatter:off
		StringBuilder hcjs = new StringBuilder();
		hcjs.append(
				"var options = {"
						+ "chart:{ "
						+ " type: 'column', "
						+ " backgroundColor: 'transparent' "
						+ "},"
						+ "credits:{ enabled: false },"
						+ "exporting:{ "
						+ " enabled: true,"
						+ " buttons:{ contextButton:{ theme:{ fill: 'transparent' } } }"
						+ "},"
						+ "title:{ text: '' },"
				);
		//@formatter:on

		// Creates and sets the labels for each day on the x-axis
		List<Date> datesGroupedBy = buildListOfFilteredDates(); // When grouped by week/month, these mark the start of the week/month
		List<String> newLabels = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		for (Date date : datesGroupedBy) {
			if (epiCurveGrouping == EpiCurveGrouping.DAY) {
				String label = DateFormatHelper.formatDate(date);
				newLabels.add(label);
			} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
				calendar.setTime(date);
				String label = DateHelper.getEpiWeek(date).toShortString();
				newLabels.add(label);
			} else {
				String label = DateHelper.formatDateWithMonthAbbreviation(date);
				newLabels.add(label);
			}
		}

		hcjs.append("xAxis: { categories: [");
		for (String s : newLabels) {
			if (newLabels.indexOf(s) == newLabels.size() - 1) {
				hcjs.append("'" + s + "']},");
			} else {
				hcjs.append("'" + s + "', ");
			}
		}

		//@formatter:off
		hcjs.append("yAxis: { min: 0, title: { text: '" + I18nProperties.getCaption(Captions.dashboardNumberOfContacts) + "' }, allowDecimals: false, softMax: 10, "
				+ "stackLabels: { enabled: true, "
				+ "style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },"
				+ "legend: { verticalAlign: 'top', backgroundColor: 'transparent', align: 'left', "
				+ "borderWidth: 0, shadow: false, margin: 30, padding: 0 },"
				+ "tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}<br/>" + I18nProperties.getCaption(Captions.dashboardTotal) + ": {point.stackTotal}'},"
				+ "plotOptions: { column: { borderWidth: 0, stacking: 'normal', groupPadding: 0, pointPadding: 0, dataLabels: {"
				+ "enabled: true, formatter: function() { if (this.y > 0) return this.y; },"
				+ "color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white' } } },");
		//@formatter:on

		if (epiCurveContactsMode == ContactsEpiCurveMode.CONTACT_CLASSIFICATION) {
			int[] unconfirmedNumbers = new int[newLabels.size()];
			int[] confirmedNumbers = new int[newLabels.size()];

			for (int i = 0; i < datesGroupedBy.size(); i++) {
				Date date = datesGroupedBy.get(i);

				ContactCriteria contactCriteria = new ContactCriteria().disease(dashboardDataProvider.getDisease())
					.region(dashboardDataProvider.getRegion())
					.district(dashboardDataProvider.getDistrict());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					contactCriteria.reportDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					contactCriteria.reportDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
				} else {
					contactCriteria.reportDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
				}

				Map<ContactClassification, Long> contactCounts =
					FacadeProvider.getContactFacade().getNewContactCountPerClassification(contactCriteria);

				Long unconfirmedCount = contactCounts.get(ContactClassification.UNCONFIRMED);
				Long confirmedCount = contactCounts.get(ContactClassification.CONFIRMED);
				unconfirmedNumbers[i] = unconfirmedCount != null ? unconfirmedCount.intValue() : 0;
				confirmedNumbers[i] = confirmedCount != null ? confirmedCount.intValue() : 0;
			}

			hcjs.append("series: [");
			hcjs.append(
				"{ name: '" + I18nProperties.getCaption(Captions.dashboardUnconfirmed)
					+ "', color: '#808080', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < unconfirmedNumbers.length; i++) {
				if (i == unconfirmedNumbers.length - 1) {
					hcjs.append(unconfirmedNumbers[i] + "]},");
				} else {
					hcjs.append(unconfirmedNumbers[i] + ", ");
				}
			}
			hcjs.append(
				"{ name: '" + I18nProperties.getCaption(Captions.dashboardConfirmed)
					+ "', color: '#005A9C', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < confirmedNumbers.length; i++) {
				if (i == confirmedNumbers.length - 1) {
					hcjs.append(confirmedNumbers[i] + "]}]};");
				} else {
					hcjs.append(confirmedNumbers[i] + ", ");
				}
			}
		} else if (epiCurveContactsMode == ContactsEpiCurveMode.FOLLOW_UP_STATUS) {
			int[] underFollowUpNumbers = new int[newLabels.size()];
			int[] lostToFollowUpNumbers = new int[newLabels.size()];
			int[] completedFollowUpNumbers = new int[newLabels.size()];
			int[] canceledFollowUpNumbers = new int[newLabels.size()];
			int[] convertedNumbers = new int[newLabels.size()];

			for (int i = 0; i < datesGroupedBy.size(); i++) {
				Date date = datesGroupedBy.get(i);

				ContactCriteria contactCriteria = new ContactCriteria().disease(dashboardDataProvider.getDisease())
					.region(dashboardDataProvider.getRegion())
					.district(dashboardDataProvider.getDistrict());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					contactCriteria.reportDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					contactCriteria.reportDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
				} else {
					contactCriteria.reportDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
				}

				Map<FollowUpStatus, Long> contactCounts = FacadeProvider.getContactFacade().getNewContactCountPerFollowUpStatus(contactCriteria);
				Map<ContactStatus, Long> contactStatusCounts = FacadeProvider.getContactFacade().getNewContactCountPerStatus(contactCriteria);

				Long underFollowUpCount = contactCounts.get(FollowUpStatus.FOLLOW_UP);
				Long lostToFollowUpCount = contactCounts.get(FollowUpStatus.LOST);
				Long completedFollowUpCount = contactCounts.get(FollowUpStatus.COMPLETED);
				Long canceledFollowUpCount = contactCounts.get(FollowUpStatus.CANCELED);
				Long convertedCount = contactStatusCounts.get(ContactStatus.CONVERTED);
				underFollowUpNumbers[i] = underFollowUpCount != null ? underFollowUpCount.intValue() : 0;
				lostToFollowUpNumbers[i] = lostToFollowUpCount != null ? lostToFollowUpCount.intValue() : 0;
				completedFollowUpNumbers[i] = completedFollowUpCount != null ? completedFollowUpCount.intValue() : 0;
				canceledFollowUpNumbers[i] = canceledFollowUpCount != null ? canceledFollowUpCount.intValue() : 0;
				convertedNumbers[i] = convertedCount != null ? convertedCount.intValue() : 0;
			}

			hcjs.append("series: [");
			hcjs.append(
				"{ name: '" + I18nProperties.getCaption(Captions.dashboardUnderFollowUpShort)
					+ "', color: '#005A9C', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < underFollowUpNumbers.length; i++) {
				if (i == underFollowUpNumbers.length - 1) {
					hcjs.append(underFollowUpNumbers[i] + "]},");
				} else {
					hcjs.append(underFollowUpNumbers[i] + ", ");
				}
			}
			hcjs.append(
				"{ name: '" + I18nProperties.getCaption(Captions.dashboardLostToFollowUpShort)
					+ "', color: '#FF0000', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < lostToFollowUpNumbers.length; i++) {
				if (i == lostToFollowUpNumbers.length - 1) {
					hcjs.append(lostToFollowUpNumbers[i] + "]},");
				} else {
					hcjs.append(lostToFollowUpNumbers[i] + ", ");
				}
			}
			hcjs.append(
				"{ name: '" + I18nProperties.getCaption(Captions.dashboardCompletedFollowUpShort)
					+ "', color: '#32CD32', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < completedFollowUpNumbers.length; i++) {
				if (i == completedFollowUpNumbers.length - 1) {
					hcjs.append(completedFollowUpNumbers[i] + "]},");
				} else {
					hcjs.append(completedFollowUpNumbers[i] + ", ");
				}
			}
			hcjs.append(
				"{ name: '" + I18nProperties.getCaption(Captions.dashboardCanceledFollowUpShort)
					+ "', color: '#FF8C00', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < canceledFollowUpNumbers.length; i++) {
				if (i == canceledFollowUpNumbers.length - 1) {
					hcjs.append(canceledFollowUpNumbers[i] + "]},");
				} else {
					hcjs.append(canceledFollowUpNumbers[i] + ", ");
				}
			}
			hcjs.append(
				"{ name: '" + I18nProperties.getCaption(Captions.dashboardConvertedToCase)
					+ "', color: '#00BFFF', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < convertedNumbers.length; i++) {
				if (i == convertedNumbers.length - 1) {
					hcjs.append(convertedNumbers[i] + "]}]};");
				} else {
					hcjs.append(convertedNumbers[i] + ", ");
				}
			}
		} else if (epiCurveContactsMode == ContactsEpiCurveMode.FOLLOW_UP_UNTIL) {
			int[] followUpUntilNumbers = new int[newLabels.size()];

			for (int i = 0; i < datesGroupedBy.size(); i++) {
				Date date = datesGroupedBy.get(i);

				ContactCriteria contactCriteria = new ContactCriteria().disease(dashboardDataProvider.getDisease())
					.region(dashboardDataProvider.getRegion())
					.district(dashboardDataProvider.getDistrict());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					contactCriteria.followUpUntilBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					contactCriteria.followUpUntilBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
				} else {
					contactCriteria.followUpUntilBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
				}

				followUpUntilNumbers[i] = FacadeProvider.getContactFacade().getFollowUpUntilCount(contactCriteria);
			}

			hcjs.append("series: [");
			hcjs.append(
				"{ name: '" + I18nProperties.getCaption(Captions.dashboardFollowUpUntilShort)
					+ "', color: '#00BFFF', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < followUpUntilNumbers.length; i++) {
				if (i == followUpUntilNumbers.length - 1) {
					hcjs.append(followUpUntilNumbers[i] + "]}]};");
				} else {
					hcjs.append(followUpUntilNumbers[i] + ", ");
				}
			}
		}

		epiCurveChart.setHcjs(hcjs.toString());
	}
}
