package de.symeda.sormas.ui.dashboard.contacts.epicurve;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;

public class FollowUpUntilCurveBuilder extends EpiCurveBuilder {

	public FollowUpUntilCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	@Override
	public void buildEpiCurve(List<Date> datesGroupedBy, DashboardDataProvider dashboardDataProvider) {
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

		hcjs.append("series: [");
		hcjs.append(
			"{ name: '" + I18nProperties.getCaption(Captions.dashboardFollowUpUntilShort)
				+ "', color: '#00BFFF', dataLabels: { allowOverlap: false },  data: [");
		for (int i = 0; i < followUpUntilNumbers.length; i++) {
			if (i == followUpUntilNumbers.length - 1) {
				hcjs.append(followUpUntilNumbers[i] + "]}],");
			} else {
				hcjs.append(followUpUntilNumbers[i] + ", ");
			}
		}
	}
}
