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

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;

@SuppressWarnings("serial")
public class DashboardSurveillanceView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/surveillance";

	protected DashboardSurveillanceDiseaseBurdenLayout diseaseBurdenAndDifferenceLayout;
	protected DashboardSurveillanceDiseaseCarouselLayout diseaseCarouselLayout;
	
	public DashboardSurveillanceView() {
		super(VIEW_NAME, DashboardType.SURVEILLANCE);

		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.infoSurveillanceDashboard));

		//add disease burden and cases
		diseaseBurdenAndDifferenceLayout = new DashboardSurveillanceDiseaseBurdenLayout(dashboardDataProvider);
		dashboardLayout.addComponent(diseaseBurdenAndDifferenceLayout);

		//add diseaseCarousel and map
		diseaseCarouselLayout = new DashboardSurveillanceDiseaseCarouselLayout(dashboardDataProvider);
		dashboardLayout.addComponent(diseaseCarouselLayout);
		dashboardLayout.setExpandRatio(diseaseCarouselLayout, 1);
	}
	
	public void refreshDashboard() {
		super.refreshDashboard();

		// Update disease burden
		if (diseaseBurdenAndDifferenceLayout != null)
			diseaseBurdenAndDifferenceLayout.refresh();

		//Update disease carousel
		if (diseaseCarouselLayout != null)
			diseaseCarouselLayout.refresh();
	}
}
