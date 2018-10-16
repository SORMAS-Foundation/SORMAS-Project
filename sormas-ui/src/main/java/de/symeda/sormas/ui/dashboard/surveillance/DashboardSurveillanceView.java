package de.symeda.sormas.ui.dashboard.surveillance;

import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;

@SuppressWarnings("serial")
public class DashboardSurveillanceView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/surveillance";

	public DashboardSurveillanceView() {
		super(VIEW_NAME, DashboardType.SURVEILLANCE);

		filterLayout.setInfoLabelText("All Dashboard elements that display cases (the 'New Cases' statistics, the Epidemiological Curve and the Case Status Map) use the onset date of the first symptom for the date/epi week filter. If this date is not available, the reception date or date of report is used instead.");

		// Add statistics
		statisticsComponent = new DashboardSurveillanceStatisticsComponent(dashboardDataProvider);
		dashboardLayout.addComponent(statisticsComponent);

		epiCurveComponent = new EpiCurveSurveillanceComponent(dashboardDataProvider);
		mapComponent = new DashboardMapComponent(dashboardDataProvider);
		
		// Add epi curve and map
		epiCurveAndMapLayout = createEpiCurveAndMapLayout();
		dashboardLayout.addComponent(epiCurveAndMapLayout);
		dashboardLayout.setExpandRatio(epiCurveAndMapLayout, 1);
	}

}
