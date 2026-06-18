/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.dashboard.gis;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.ui.Alignment;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class GisDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/gis";

	private final GisDashboardDataProvider dataProvider;
	private final GisDashboardMapComponent mapComponent;

	public GisDashboardView() {
		super(VIEW_NAME);

		dashboardSwitcher.setValue(DashboardType.GIS);
		dashboardSwitcher.addValueChangeListener(this::navigateToDashboardView);

		dataProvider = new GisDashboardDataProvider();
		GisDashboardFilterLayout filterLayout = new GisDashboardFilterLayout(this, dataProvider);

		mapComponent = new GisDashboardMapComponent(dataProvider);
		mapComponent.setSizeFull();

		PopupButton filterDropdown = ButtonHelper.createPopupButton(Captions.dashboardGisMapFilter, filterLayout, CssStyles.BUTTON_SUBTLE);
		mapComponent.getMapHeaderLayout().addComponent(filterDropdown);
		mapComponent.getMapHeaderLayout().setComponentAlignment(filterDropdown, Alignment.MIDDLE_LEFT);

		dashboardLayout.addComponent(mapComponent);
		dashboardLayout.setExpandRatio(mapComponent, 1);
	}

	@Override
	public void refreshDashboard() {
		dataProvider.refreshData();

		mapComponent.updateEntityLoadingStatus();
		mapComponent.refreshMap();
	}
}
