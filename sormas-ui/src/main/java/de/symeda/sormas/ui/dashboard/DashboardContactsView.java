package de.symeda.sormas.ui.dashboard;

@SuppressWarnings("serial")
public class DashboardContactsView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/contacts";

	public static final String I18N_PREFIX = "Dashboard";

	public DashboardContactsView() {
		super(VIEW_NAME, DashboardType.CONTACTS);
		
		filterLayout.setInfoLabelText("All Dashboard elements that display general information about contacts use the follow-up period of the respective contact, starting with the contact report date.");

		// Add statistics
		statisticsComponent = new DashboardContactsStatisticsComponent(dashboardDataProvider);
		dashboardLayout.addComponent(statisticsComponent);
		dashboardLayout.setExpandRatio(statisticsComponent, 0);

		epiCurveComponent = new EpiCurveContactsComponent(dashboardDataProvider);
		mapComponent = new DashboardMapComponent(dashboardDataProvider);
		
		// Add epi curve and map
		epiCurveAndMapLayout = createEpiCurveAndMapLayout();
		dashboardLayout.addComponent(epiCurveAndMapLayout);
		dashboardLayout.setExpandRatio(epiCurveAndMapLayout, 1);
	}
	
}
