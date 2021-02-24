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

import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.dashboard.surveillance.epicurve.SurveillanceEpiCurveBuilder;
import de.symeda.sormas.ui.dashboard.surveillance.epicurve.SurveillanceEpiCurveBuilders;
import de.symeda.sormas.ui.utils.CssStyles;

public class SurveillanceEpiCurveComponent extends AbstractEpiCurveComponent {

	private static final long serialVersionUID = 6582975657305031105L;

	private SurveillanceEpiCurveMode epiCurveSurveillanceMode;

	public SurveillanceEpiCurveComponent(DashboardDataProvider dashboardDataProvider) {
		super(dashboardDataProvider);
	}

	@Override
	protected OptionGroup createEpiCurveModeSelector() {
		if (epiCurveSurveillanceMode == null) {
			epiCurveSurveillanceMode = SurveillanceEpiCurveMode.CASE_STATUS;
		}

		OptionGroup epiCurveModeOptionGroup = new OptionGroup();
		CssStyles.style(epiCurveModeOptionGroup, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_SUBTLE);
		epiCurveModeOptionGroup.addItems((Object[]) SurveillanceEpiCurveMode.values());
		epiCurveModeOptionGroup.setValue(epiCurveSurveillanceMode);
		epiCurveModeOptionGroup.select(epiCurveSurveillanceMode);
		epiCurveModeOptionGroup.addValueChangeListener(e -> {
			epiCurveSurveillanceMode = (SurveillanceEpiCurveMode) e.getProperty().getValue();
			clearAndFillEpiCurveChart();
		});
		return epiCurveModeOptionGroup;
	}

	@Override
	public void clearAndFillEpiCurveChart() {
		SurveillanceEpiCurveBuilder surveillanceEpiCurveBuilder =
			SurveillanceEpiCurveBuilders.getEpiCurveBuilder(epiCurveSurveillanceMode, epiCurveGrouping);
		epiCurveChart
			.setHcjs(surveillanceEpiCurveBuilder.buildFrom(buildListOfFilteredDates(), NewCaseDateType.MOST_RELEVANT, dashboardDataProvider));
	}
}
