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
package de.symeda.sormas.ui.dashboard.surveillance;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;

@SuppressWarnings("serial")
public class DashboardSurveillanceView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/surveillance";

	public DashboardSurveillanceView() {
		super(VIEW_NAME, DashboardType.SURVEILLANCE);

		filterLayout.setInfoLabelText(
				"All Dashboard elements that display cases (the 'New Cases' statistics, the Epidemiological Curve and the Case Status Map) use the onset date of the first symptom for the date/epi week filter. If this date is not available, the reception date or date of report is used instead.");

		//add disease burden and cases
		diseaseBurdenAndDifferenceLayout = new DashboardSurveillanceDiseaseBurdenLayout(dashboardDataProvider);
		dashboardLayout.addComponent(diseaseBurdenAndDifferenceLayout);
		//dashboardLayout.setExpandRatio(diseaseBurdenAndDifferenceLayout, 1);

		//add diseaseCarousel and map
		diseaseCarouselLayout = new DashboardSurveillanceDiseaseCarouselLayout(dashboardDataProvider);
		dashboardLayout.addComponent(diseaseCarouselLayout);
		//dashboardLayout.setExpandRatio(diseaseCarouselLayout, 1);
	}
}
