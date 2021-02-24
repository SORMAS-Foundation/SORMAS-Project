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
package de.symeda.sormas.ui.dashboard.surveillance;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardFilterLayout;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.surveillance.layout.DateTypeSelectorLayout;

@SuppressWarnings("serial")
public class SurveillanceDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/surveillance";

	protected DashboardDataProvider dashboardDataProvider;
	protected DashboardFilterLayout filterLayout;

	protected SurveillanceOverviewLayout surveillanceOverviewLayout;
	protected SurveillanceDiseaseCarouselLayout diseaseCarouselLayout;

	public SurveillanceDashboardView() {
		super(VIEW_NAME);

		dashboardDataProvider = new DashboardDataProvider();
		if (dashboardDataProvider.getDashboardType() == null) {
			dashboardDataProvider.setDashboardType(DashboardType.SURVEILLANCE);
		}
		if (DashboardType.CONTACTS.equals(dashboardDataProvider.getDashboardType())) {
			dashboardDataProvider.setDisease(FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease());
		}
		filterLayout = new DashboardFilterLayout(this, dashboardDataProvider);
		dashboardLayout.addComponent(filterLayout);

		DateTypeSelectorLayout dateTypeSelectorLayout = new DateTypeSelectorLayout();
		dateTypeSelectorLayout.setValue(NewCaseDateType.MOST_RELEVANT);
		dateTypeSelectorLayout.addValueChangeListener(e -> {
			dashboardDataProvider.setNewCaseDateType((NewCaseDateType) e.getProperty().getValue());
			dashboardDataProvider.refreshData();
			refreshDashboard();
			surveillanceOverviewLayout.refresh();
		});
		dashboardLayout.addComponent(dateTypeSelectorLayout);

		dashboardSwitcher.setValue(DashboardType.SURVEILLANCE);
		dashboardSwitcher.addValueChangeListener(e -> {
			dashboardDataProvider.setDashboardType((DashboardType) e.getProperty().getValue());
			navigateToDashboardView(e);
		});

		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.infoSurveillanceDashboard));
		dashboardLayout.setSpacing(false);

		//add disease burden and cases
		surveillanceOverviewLayout = new SurveillanceOverviewLayout(dashboardDataProvider);
		dashboardLayout.addComponent(surveillanceOverviewLayout);
		filterLayout.setDateFilterChangeCallback(() -> {
			surveillanceOverviewLayout.updateDifferenceComponentSubHeader();
		});

		//add diseaseCarousel and map
		diseaseCarouselLayout = new SurveillanceDiseaseCarouselLayout(dashboardDataProvider);
		dashboardLayout.addComponent(diseaseCarouselLayout);
		dashboardLayout.setExpandRatio(diseaseCarouselLayout, 1);

		diseaseCarouselLayout.setExpandListener(expanded -> {
			if (expanded) {
				dashboardLayout.removeComponent(surveillanceOverviewLayout);
			} else {
				dashboardLayout.addComponent(surveillanceOverviewLayout, 1);
			}
		});
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		refreshDashboard();
	}

	public void refreshDashboard() {
		dashboardDataProvider.refreshData();

		// Update disease burden
		if (surveillanceOverviewLayout != null)
			surveillanceOverviewLayout.refresh();

		//Update disease carousel
		if (diseaseCarouselLayout != null)
			diseaseCarouselLayout.refresh();
	}
}
