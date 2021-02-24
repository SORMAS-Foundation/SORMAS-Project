package de.symeda.sormas.ui.dashboard.contacts.epicurve;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;
import de.symeda.sormas.ui.dashboard.epicurve.EpiCurveBuilder;

public abstract class ContactsEpiCurveBuilder extends EpiCurveBuilder {

	public ContactsEpiCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(epiCurveGrouping);
	}

	public String buildFrom(List<Date> datesGroupedBy, DashboardDataProvider dashboardDataProvider) {
		return super.buildFrom(datesGroupedBy, I18nProperties.getCaption(Captions.dashboardNumberOfContacts), dashboardDataProvider);
	}
}
