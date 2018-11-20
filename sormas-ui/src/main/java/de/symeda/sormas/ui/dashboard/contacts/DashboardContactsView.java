/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard.contacts;

import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;

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
