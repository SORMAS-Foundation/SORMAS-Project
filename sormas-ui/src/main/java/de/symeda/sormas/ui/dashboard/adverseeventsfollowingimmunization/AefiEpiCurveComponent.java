/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.AbstractComponent;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.dashboard.AefiDashboardCriteria;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveBuilder;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveSeriesElement;

public class AefiEpiCurveComponent extends AbstractEpiCurveComponent<AefiDashboardDataProvider> {

	private static final long serialVersionUID = 6767165953640006853L;

	public AefiEpiCurveComponent(AefiDashboardDataProvider dashboardDataProvider) {
		super(dashboardDataProvider);
		epiCurveLabel.setValue(I18nProperties.getString(Strings.headingAefiDashboardEpiCurve));
	}

	@Override
	protected AbstractComponent createEpiCurveModeSelector() {
		return null;
	}

	@Override
	public void clearAndFillEpiCurveChart() {
		epiCurveChart.setHcjs(new AefiEpiCurveBuilder(epiCurveGrouping).buildFrom(buildListOfFilteredDates(), dashboardDataProvider));
	}

	private static class AefiEpiCurveBuilder extends AbstractEpiCurveBuilder<AefiDashboardCriteria, AefiDashboardDataProvider> {

		public AefiEpiCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
			super(Captions.dashboardNumberOfAdverseEvents, epiCurveGrouping);
		}

		@Override
		protected List<EpiCurveSeriesElement> getEpiCurveElements(List<Date> datesGroupedBy, AefiDashboardDataProvider dashboardDataProvider) {
			int[] seriousNumbers = new int[datesGroupedBy.size()];
			int[] nonSeriousNumbers = new int[datesGroupedBy.size()];

			for (int i = 0; i < datesGroupedBy.size(); i++) {
				Date date = datesGroupedBy.get(i);

				AefiDashboardCriteria criteria =
					dashboardDataProvider.buildDashboardCriteria().aefiDashboardDateType(dashboardDataProvider.getDateType());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					criteria.dateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					criteria.dateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
				} else {
					criteria.dateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
				}

				Map<AefiType, Long> aefiCounts = FacadeProvider.getAefiDashboardFacade().getAefiCountsByType(criteria);

				Long seriousCount = aefiCounts.get(AefiType.SERIOUS);
				Long nonSeriousCount = aefiCounts.get(AefiType.NON_SERIOUS);

				seriousNumbers[i] = seriousCount != null ? seriousCount.intValue() : 0;
				nonSeriousNumbers[i] = nonSeriousCount != null ? nonSeriousCount.intValue() : 0;
			}

			return Arrays.asList(
				new EpiCurveSeriesElement(AefiType.SERIOUS, "#c80000", seriousNumbers),
				new EpiCurveSeriesElement(AefiType.NON_SERIOUS, "#32CD32", nonSeriousNumbers));
		}
	}
}
