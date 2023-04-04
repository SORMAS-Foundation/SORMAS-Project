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

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.CaseStatisticsComponent;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.EventStatisticsComponent;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.LaboratoryResultsStatisticsComponent;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.summary.DiseaseSummaryComponent;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class DiseaseStatisticsComponent extends CustomLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private final DashboardDataProvider dashboardDataProvider;

	private final CaseStatisticsComponent caseStatisticsComponent;
	private final DiseaseSummaryComponent diseaseSummaryComponent;
	private final EventStatisticsComponent eventStatisticsComponent;
	private final LaboratoryResultsStatisticsComponent labResultsStatisticsComponent;

	private static final String CASE_LOC = "case";
	private static final String OUTBREAK_LOC = "outbreak";
	private static final String EVENT_LOC = "event";
	private static final String SAMPLE_LOC = "sample";

	public DiseaseStatisticsComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		setWidth(100, Unit.PERCENTAGE);

		setTemplateContents(
			LayoutUtil.fluidRow(
				LayoutUtil.fluidColumn(3, 0, 12, 0, LayoutUtil.fluidRowLocs(CASE_LOC)),
				LayoutUtil.fluidColumn(4, 0, 12, 0, LayoutUtil.fluidRowLocs(OUTBREAK_LOC)),
				LayoutUtil.fluidColumn(5, 0, 12, 0, LayoutUtil.fluidRowLocs(EVENT_LOC, SAMPLE_LOC))));

		caseStatisticsComponent = new CaseStatisticsComponent();
		diseaseSummaryComponent = new DiseaseSummaryComponent();
		eventStatisticsComponent = new EventStatisticsComponent();
		labResultsStatisticsComponent = new LaboratoryResultsStatisticsComponent(
			Captions.dashboardNewFinalLaboratoryResults,
			Descriptions.descDashboardNewFinalLaboratoryResults,
			null,
			false,
			true);
		labResultsStatisticsComponent.setWithPercentage(false);

		addComponent(caseStatisticsComponent, CASE_LOC);
		addComponent(diseaseSummaryComponent, OUTBREAK_LOC);
		addComponent(eventStatisticsComponent, EVENT_LOC);
		addComponent(labResultsStatisticsComponent, SAMPLE_LOC);
	}

	public void refresh() {
		caseStatisticsComponent.update(dashboardDataProvider.getCasesCountByClassification());
		diseaseSummaryComponent.update(dashboardDataProvider);
		eventStatisticsComponent.update(dashboardDataProvider.getEventCountByStatus());
		labResultsStatisticsComponent.update(dashboardDataProvider.getNewCasesFinalLabResultCountByResultType());
	}
}
