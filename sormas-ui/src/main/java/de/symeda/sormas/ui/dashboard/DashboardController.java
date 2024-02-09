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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.NewDateFilterType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.campaigns.CampaignDashboardView;
import de.symeda.sormas.ui.dashboard.contacts.ContactsDashboardView;
import de.symeda.sormas.ui.dashboard.diseasedetails.DiseaseDetailsView;
import de.symeda.sormas.ui.dashboard.sample.SampleDashboardView;
import de.symeda.sormas.ui.dashboard.surveillance.SurveillanceDashboardView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class DashboardController {

	public DashboardController() {

	}

	public void registerViews(Navigator navigator) {
		if (permitted(FeatureType.CASE_SURVEILANCE, UserRight.DASHBOARD_SURVEILLANCE_VIEW)) {
			navigator.addView(SurveillanceDashboardView.VIEW_NAME, SurveillanceDashboardView.class);
		}
		if (permitted(FeatureType.CONTACT_TRACING, UserRight.DASHBOARD_CONTACT_VIEW)) {
			navigator.addView(ContactsDashboardView.VIEW_NAME, ContactsDashboardView.class);
		}
		if (permitted(FeatureType.CAMPAIGNS, UserRight.DASHBOARD_CAMPAIGNS_VIEW)) {
			navigator.addView(CampaignDashboardView.VIEW_NAME, CampaignDashboardView.class);
		}

		if (permitted(FeatureType.SAMPLES_LAB, UserRight.DASHBOARD_SAMPLES_VIEW)) {
			navigator.addView(SampleDashboardView.VIEW_NAME, SampleDashboardView.class);
		}

		//if (permitted(FeatureType.DISEASE_DETAILS,UserRight.DASHBOARD_DISEASE_DETAILS_ACCESS)) {
			navigator.addView(DiseaseDetailsView.VIEW_NAME, DiseaseDetailsView.class);
		//}
	}



	public void navigateToDisease(Disease disease,DashboardDataProvider dashboardDataProvider) {
		Date dateFrom = dashboardDataProvider.getFromDate();

		Date dateTo = dashboardDataProvider.getToDate();

		NewDateFilterType type = dashboardDataProvider.getDateFilterType();

		CaseClassification caseClassification= dashboardDataProvider.getCaseClassification();
//
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // Quoted "Z" to indicate UTC, no timezone offset
		df.setTimeZone(tz);
		String dateFromAsISO = df.format(dateFrom);
		String dateToAsISO = df.format(dateTo);

		NewCaseDateType newCaseDateType = dashboardDataProvider.getNewCaseDateType();

		RegionReferenceDto region = dashboardDataProvider.getRegion();
		String regionId = null;
		if(Objects.nonNull(region)&&region.getUuid()!=null){
			regionId= region.getUuid();
		}
		System.out.println(regionId);


//
		String paramData = dateFromAsISO+"/"+dateToAsISO+"/"+type+"/"+caseClassification+"/"+newCaseDateType+"/"+regionId;
//
		DiseaseDetailsView.setData(paramData);
		//DiseaseDetailsView.setProvider(dashboardDataProvider);

		String navigationState = DiseaseDetailsView.VIEW_NAME + "/" + disease.getName();

		SormasUI.get().getNavigator().navigateTo(navigationState);



	}
}
