package de.symeda.sormas.ui.dashboard.contacts.epicurve;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveSeriesElement;

public class FollowUpStatusCurveBuilder extends ContactsEpiCurveBuilder {

	public FollowUpStatusCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	protected List<EpiCurveSeriesElement> getEpiCurveElements(List<Date> datesGroupedBy, DashboardDataProvider dashboardDataProvider) {
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

		return Arrays.asList(
			new EpiCurveSeriesElement(Captions.dashboardUnderFollowUpShort, "#005A9C", underFollowUpNumbers),
			new EpiCurveSeriesElement(Captions.dashboardLostToFollowUpShort, "#FF0000", lostToFollowUpNumbers),
			new EpiCurveSeriesElement(Captions.dashboardCompletedFollowUpShort, "#32CD32", completedFollowUpNumbers),
			new EpiCurveSeriesElement(Captions.dashboardCanceledFollowUpShort, "#FF8C00", canceledFollowUpNumbers),
			new EpiCurveSeriesElement(Captions.dashboardConvertedToCase, "#00BFFF", convertedNumbers));
	}
}
