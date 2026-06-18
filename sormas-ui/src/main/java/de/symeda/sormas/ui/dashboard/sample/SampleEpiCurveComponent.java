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

package de.symeda.sormas.ui.dashboard.sample;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.AbstractComponent;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveBuilder;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveSeriesElement;

public class SampleEpiCurveComponent extends AbstractEpiCurveComponent<SampleDashboardDataProvider> {

	private static final long serialVersionUID = 3759194459077992333L;

	public SampleEpiCurveComponent(SampleDashboardDataProvider dashboardDataProvider) {
		super(dashboardDataProvider, Descriptions.sampleDashboardHumans);
		epiCurveLabel.setValue(I18nProperties.getString(Strings.headingSampleDashboardEpiCurve));
	}

	@Override
	protected AbstractComponent createEpiCurveModeSelector() {
		return null;
	}

	@Override
	public void clearAndFillEpiCurveChart() {
		epiCurveChart.setHcjs(new SampleEpiCurveBuilder(epiCurveGrouping).buildFrom(buildListOfFilteredDates(), dashboardDataProvider));
	}

	private static class SampleEpiCurveBuilder extends AbstractEpiCurveBuilder<SampleDashboardCriteria, SampleDashboardDataProvider> {

		public SampleEpiCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
			super(Captions.dashboardNumberOfSamples, epiCurveGrouping);
		}

		@Override
		protected List<EpiCurveSeriesElement> getEpiCurveElements(List<Date> datesGroupedBy, SampleDashboardDataProvider dashboardDataProvider) {
			int[] pendingUpNumbers = new int[datesGroupedBy.size()];
			int[] positiveNumbers = new int[datesGroupedBy.size()];
			int[] negativeNumbers = new int[datesGroupedBy.size()];
			int[] indeterminateNumbers = new int[datesGroupedBy.size()];
			int[] notDoneNumbers = new int[datesGroupedBy.size()];
			if (dashboardDataProvider.getEnvironmentSampleMaterial() == null) {
				// EPI Curve is only for Human samples
				for (int i = 0; i < datesGroupedBy.size(); i++) {
					Date date = datesGroupedBy.get(i);

					SampleDashboardCriteria criteria = dashboardDataProvider.buildDashboardCriteria().sampleDateType(dashboardDataProvider.getDateType());
					if (epiCurveGrouping == EpiCurveGrouping.DAY) {
						criteria.dateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
					} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
						criteria.dateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
					} else {
						criteria.dateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
					}

					Map<PathogenTestResultType, Long> sampleCounts = FacadeProvider.getSampleDashboardFacade().getSampleCountsByResultType(criteria);

					Long positiveCount = sampleCounts.get(PathogenTestResultType.POSITIVE);
					Long negativeCount = sampleCounts.get(PathogenTestResultType.NEGATIVE);
					Long pendingCount = sampleCounts.get(PathogenTestResultType.PENDING);
					Long indeterminateCount = sampleCounts.get((PathogenTestResultType.INDETERMINATE));
					Long notDoneCount = sampleCounts.get(PathogenTestResultType.NOT_DONE);

					positiveNumbers[i] = positiveCount != null ? positiveCount.intValue() : 0;
					negativeNumbers[i] = negativeCount != null ? negativeCount.intValue() : 0;
					pendingUpNumbers[i] = pendingCount != null ? pendingCount.intValue() : 0;
					indeterminateNumbers[i] = indeterminateCount != null ? indeterminateCount.intValue() : 0;
					notDoneNumbers[i] = notDoneCount != null ? notDoneCount.intValue() : 0;
				}
			}

			return Arrays.asList(
				new EpiCurveSeriesElement(PathogenTestResultType.POSITIVE, "#c80000", positiveNumbers),
				new EpiCurveSeriesElement(PathogenTestResultType.NEGATIVE, "#32CD32", negativeNumbers),
				new EpiCurveSeriesElement(PathogenTestResultType.PENDING, "#be6900", pendingUpNumbers),
				new EpiCurveSeriesElement(PathogenTestResultType.INDETERMINATE, "#808080", indeterminateNumbers),
				new EpiCurveSeriesElement(PathogenTestResultType.NOT_DONE, "#00BFFF", notDoneNumbers));
		}
	}
}
