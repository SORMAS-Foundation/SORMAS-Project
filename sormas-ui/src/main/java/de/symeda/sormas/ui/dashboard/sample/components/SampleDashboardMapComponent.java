/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.dashboard.sample.components;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.map.BaseDashboardMapComponent;
import de.symeda.sormas.ui.dashboard.sample.SampleDashboardDataProvider;

public class SampleDashboardMapComponent extends BaseDashboardMapComponent<SampleDashboardCriteria, SampleDashboardDataProvider> {

	public SampleDashboardMapComponent(SampleDashboardDataProvider dashboardDataProvider) {
		super(Strings.headingSampleDashboardMap, dashboardDataProvider);
	}

	@Override
	protected Long getMarkerCount(Date fromDate, Date toDate, int maxCount) {
		return FacadeProvider.getSampleDashboardFacade().countSamplesForMap(dashboardDataProvider.buildDashboardCriteriaWithDates());
	}

	@Override
	protected void loadMapData(Date fromDate, Date toDate) {

	}

	@Override
	protected void addLayerOptions(VerticalLayout layersLayout) {

	}

	@Override
	protected List<Component> getLegendComponents() {
		return Collections.emptyList();
	}

	@Override
	protected void onMarkerClicked(String groupId, int markerIndex) {

	}
}
