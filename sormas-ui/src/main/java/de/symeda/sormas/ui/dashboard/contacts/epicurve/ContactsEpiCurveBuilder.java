package de.symeda.sormas.ui.dashboard.contacts.epicurve;

import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveBuilder;

public abstract class ContactsEpiCurveBuilder extends AbstractEpiCurveBuilder<DashboardCriteria, DashboardDataProvider> {

	public ContactsEpiCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		super(Captions.dashboardNumberOfContacts, epiCurveGrouping);
	}
}
