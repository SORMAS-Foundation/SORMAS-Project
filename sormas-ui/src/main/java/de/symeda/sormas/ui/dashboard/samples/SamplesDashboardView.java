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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard.samples;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardFilterLayout;
import de.symeda.sormas.ui.dashboard.DashboardType;

@SuppressWarnings("serial")
public class SamplesDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/samples";

	protected CountsTileViewLayout countsTileViewLayout;

	public SamplesDashboardView() {
		super(VIEW_NAME, DashboardType.SAMPLES);

//		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.infoSampleDashboard));
		filterLayout = new DashboardFilterLayout(this, dashboardDataProvider);
		dashboardLayout.addComponent(filterLayout);
//		addComponent(dashboardLayout);

		//add samples
		countsTileViewLayout = new CountsTileViewLayout(dashboardDataProvider);
		dashboardLayout.addComponent(countsTileViewLayout);
		dashboardLayout.setExpandRatio(countsTileViewLayout, 1);
	}

	public void refreshDashboard() {
		super.refreshDashboard();

		// Update counts
		if (countsTileViewLayout != null)
			countsTileViewLayout.refresh();

	}

}
