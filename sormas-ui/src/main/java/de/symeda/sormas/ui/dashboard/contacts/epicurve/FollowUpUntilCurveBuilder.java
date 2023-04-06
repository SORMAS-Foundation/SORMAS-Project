package de.symeda.sormas.ui.dashboard.contacts.epicurve;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveSeriesElement;

public class FollowUpUntilCurveBuilder extends ContactsEpiCurveBuilder {

	public FollowUpUntilCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	protected List<EpiCurveSeriesElement> getEpiCurveElements(List<Date> datesGroupedBy, DashboardDataProvider dashboardDataProvider) {
		int[] followUpUntilNumbers = new int[datesGroupedBy.size()];

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

		return Collections.singletonList(new EpiCurveSeriesElement(Captions.dashboardFollowUpUntilShort, "#00BFFF", followUpUntilNumbers));
	}
}
