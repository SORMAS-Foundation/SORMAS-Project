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

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.ui.SubNavigationMenu2;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardSurveillanceDiseaseCarouselLayout extends VerticalLayout {

	private DiseaseStatisticsSubComponent statisticsComponent;
	private EpiCurveSurveillanceComponent epiCurveComponent;
	private DashboardMapComponent mapComponent;

	public DashboardSurveillanceDiseaseCarouselLayout(DashboardDataProvider dashboardDataProvider) {
		statisticsComponent = new DiseaseStatisticsSubComponent(dashboardDataProvider);
		epiCurveComponent = new EpiCurveSurveillanceComponent(dashboardDataProvider);
		mapComponent = new DashboardMapComponent(dashboardDataProvider);

		this.initLayout();
	}

	private void initLayout() {
		addStyleName(DashboardCssStyles.CURVE_AND_MAP_LAYOUT);
		
		HorizontalLayout carouselMenu = createCarouselMenu();
		addComponent(carouselMenu);
		
				
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout statisticsAndEpiCurveLayout = createStatisticsAndEpiCurveLayout();
		layout.addComponent(statisticsAndEpiCurveLayout);
		
		layout.addComponent(mapComponent);
		mapComponent.setHeight(100, Unit.PERCENTAGE);
		
		addComponent(layout);
	}
	
	private HorizontalLayout createCarouselMenu() {
		HorizontalLayout layout = new HorizontalLayout();
		CssStyles.style(layout, CssStyles.VSPACE_TOP_4);
		
		SubNavigationMenu2 menu = new SubNavigationMenu2();
		
		for (Disease disease : Disease.values()) {
			menu.addView(disease.getName(), disease.toShortString(), (e) -> {
				this.onDiseaseSelected(disease);
			});
		}
		
		layout.addComponent(menu);
		
		return layout;
	}
	
	private void onDiseaseSelected(Disease disease) {
		
	}

	protected HorizontalLayout createStatisticsAndEpiCurveLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);

		layout.addComponent(statisticsComponent);

		layout.addComponent(epiCurveComponent);

		return layout;
	}

	public void refresh() {
		this.statisticsComponent.refresh();
		this.epiCurveComponent.clearAndFillEpiCurveChart();
		this.mapComponent.refreshMap();
	}
}
