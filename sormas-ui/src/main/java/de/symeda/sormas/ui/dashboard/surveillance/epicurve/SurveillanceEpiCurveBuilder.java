package de.symeda.sormas.ui.dashboard.surveillance.epicurve;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;
import de.symeda.sormas.ui.dashboard.epicurve.EpiCurveBuilder;

public abstract class SurveillanceEpiCurveBuilder extends EpiCurveBuilder {

	public SurveillanceEpiCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	public String buildFrom(List<Date> filteredDates, DashboardDataProvider dashboardDataProvider) {
		return super.buildFrom(filteredDates, I18nProperties.getCaption(Captions.dashboardNumberOfCases), dashboardDataProvider);
	}
}
