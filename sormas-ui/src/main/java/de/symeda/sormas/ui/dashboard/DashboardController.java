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
package de.symeda.sormas.ui.dashboard;

import static de.symeda.sormas.ui.UiUtil.permitted;

import com.vaadin.navigator.Navigator;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.dashboard.campaigns.CampaignDashboardView;
import de.symeda.sormas.ui.dashboard.contacts.ContactsDashboardView;
import de.symeda.sormas.ui.dashboard.samples.SamplesDashboardView;
import de.symeda.sormas.ui.dashboard.surveillance.SurveillanceDashboardView;

public class DashboardController {

	public DashboardController() {

	}

	public void registerViews(Navigator navigator) {
		if (permitted(FeatureType.CASE_SURVEILANCE, UserRight.DASHBOARD_SURVEILLANCE_ACCESS)) {
			navigator.addView(SurveillanceDashboardView.VIEW_NAME, SurveillanceDashboardView.class);
		}
		if (permitted(FeatureType.CONTACT_TRACING, UserRight.DASHBOARD_CONTACT_ACCESS)) {
			navigator.addView(ContactsDashboardView.VIEW_NAME, ContactsDashboardView.class);
		}
		if (permitted(FeatureType.CAMPAIGNS, UserRight.DASHBOARD_CAMPAIGNS_ACCESS)) {
			navigator.addView(CampaignDashboardView.VIEW_NAME, CampaignDashboardView.class);
		}
		if (permitted(FeatureType.SAMPLES_LAB, UserRight.DASHBOARD_SAMPLE_ACCESS)) {
			navigator.addView(SamplesDashboardView.VIEW_NAME, SamplesDashboardView.class);
		}
	}
}
