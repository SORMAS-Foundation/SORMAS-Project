package de.symeda.sormas.ui.dashboard;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class DashboardContactsView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/contacts";

	public static final String I18N_PREFIX = "Dashboard";

	public DashboardContactsView() {
		super(VIEW_NAME, DashboardType.CONTACTS);
		
		filterLayout.setInfoLabelText("All Dashboard elements that display general information about contacts use the follow-up period of the respective contact, starting with the contact report date.");
	}
	
	@Override
	public void refreshDashboard() {
		dashboardDataProvider.refreshData();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}
	
}
