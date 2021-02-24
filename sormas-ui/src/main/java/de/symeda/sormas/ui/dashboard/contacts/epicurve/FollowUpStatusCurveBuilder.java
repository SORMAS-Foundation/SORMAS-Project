package de.symeda.sormas.ui.dashboard.contacts.epicurve;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;

public class FollowUpStatusCurveBuilder extends EpiCurveBuilder {

	public FollowUpStatusCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	public void buildEpiCurve(List<Date> datesGroupedBy, DashboardDataProvider dashboardDataProvider) {
		int[] underFollowUpNumbers = new int[datesGroupedBy.size()];
		int[] lostToFollowUpNumbers = new int[datesGroupedBy.size()];
		int[] completedFollowUpNumbers = new int[datesGroupedBy.size()];
		int[] canceledFollowUpNumbers = new int[datesGroupedBy.size()];
		int[] convertedNumbers = new int[datesGroupedBy.size()];

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
				hcjs.append(convertedNumbers[i] + "]}],");
			} else {
				hcjs.append(convertedNumbers[i] + ", ");
			}
		}
	}
}
