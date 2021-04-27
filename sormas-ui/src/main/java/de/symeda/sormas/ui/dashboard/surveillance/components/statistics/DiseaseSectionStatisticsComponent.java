package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import com.vaadin.ui.CssLayout;

import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;

public class DiseaseSectionStatisticsComponent extends DashboardStatisticsSubComponent {

	public void buildCountLayout(DashboardStatisticsCountElement... dashboardStatisticsCountElements) {
		CssLayout countLayout = createCountLayout(true);
		for (DashboardStatisticsCountElement dashboardStatisticsCountElement : dashboardStatisticsCountElements) {
			addComponentToCountLayout(countLayout, dashboardStatisticsCountElement);
		}
		addComponent(countLayout);
	}
}
