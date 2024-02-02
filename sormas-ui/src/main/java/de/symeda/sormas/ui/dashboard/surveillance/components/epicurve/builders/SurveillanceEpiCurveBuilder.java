package de.symeda.sormas.ui.dashboard.surveillance.components.epicurve.builders;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveBuilder;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveSeriesElement;

public abstract class SurveillanceEpiCurveBuilder extends AbstractEpiCurveBuilder<DashboardCriteria, DashboardDataProvider> {


	public SurveillanceEpiCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(Captions.dashboardNumberOfContacts, epiCurveGrouping);
	}

	@Override
	protected List<EpiCurveSeriesElement> getEpiCurveElements(List<Date> datesGroupedBy, DashboardDataProvider dashboardDataProvider) {
		DashboardCriteria dashboardCriteria = new DashboardCriteria().disease(dashboardDataProvider.getDisease())
			.region(dashboardDataProvider.getRegion())
			.district(dashboardDataProvider.getDistrict())
			.newCaseDateType(dashboardDataProvider.getNewCaseDateType());

		return buildEpiCurveSeriesElements(datesGroupedBy, dashboardCriteria);
	}

	protected DashboardCriteria setNewCaseDatesInCaseCriteria(Date date, DashboardCriteria dashboardCriteria) {
		if (epiCurveGrouping == EpiCurveGrouping.DAY) {
			dashboardCriteria.dateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
		} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
			dashboardCriteria.dateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
		} else {
			dashboardCriteria.dateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
		}
		return dashboardCriteria;
	}

	abstract List<EpiCurveSeriesElement> buildEpiCurveSeriesElements(List<Date> filteredDates, DashboardCriteria dashboardCriteria);
}
