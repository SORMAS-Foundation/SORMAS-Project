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
package de.symeda.sormas.ui.dashboard.surveillance.components.disease.tile;

import java.util.List;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;


public class DiseaseTileViewLayout extends CssLayout {

	private DashboardDataProvider dashboardDataProvider;

	private static final long serialVersionUID = 6582975657305031105L;

	public DiseaseTileViewLayout(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
	}

	@Override
	protected String getCss(Component c) {
		return "margin-left: 18px; margin-bottom: 18px;";
	}

	public void refresh(List<DiseaseBurdenDto> diseasesBurden) {
		this.removeAllComponents();

		for (DiseaseBurdenDto diseaseBurden : diseasesBurden) {

			DiseaseTileComponent tile = new DiseaseTileComponent(diseaseBurden,this.dashboardDataProvider);
			tile.setWidth(230, Unit.PIXELS);
			addComponent(tile);
		}
	}
}