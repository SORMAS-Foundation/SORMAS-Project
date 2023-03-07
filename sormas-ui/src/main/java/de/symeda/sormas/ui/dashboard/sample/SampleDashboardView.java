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

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.FinalLaboratoryResultsStatisticsComponent;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class SampleDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/samples";

	private static final String LAB_RESULTS = "labResults";
	private static final String SAMPLE_PURPOSE = "samplePurpose";
	private static final String TEST_RESULTS = "testResults";
	private static final String SPECIMEN_CONDITION = "specimenCondition";
	private static final String SHIPMENT_STATUS = "shipmentStatus";

	private final SampleDashboardDataProvider dataProvider;
	private final FinalLaboratoryResultsStatisticsComponent labResultsStatisticsComponent;

	public SampleDashboardView() {
		super(VIEW_NAME);

		dashboardSwitcher.setValue(DashboardType.SAMPLES);
		dashboardSwitcher.addValueChangeListener(this::navigateToDashboardView);

		dashboardLayout.setHeightUndefined();

		dataProvider = new SampleDashboardDataProvider();
		SampleDashboardFilterLayout filterLayout = new SampleDashboardFilterLayout(this, dataProvider);

		dashboardLayout.addComponent(filterLayout);

		CustomLayout sampleCountsLayout = new CustomLayout();
		sampleCountsLayout.setTemplateContents(
			LayoutUtil.fluidRowLocs(LAB_RESULTS, SAMPLE_PURPOSE, TEST_RESULTS)
				+ LayoutUtil
					.fluidRow(LayoutUtil.fluidColumnLoc(2, 0, 4, 0, SPECIMEN_CONDITION), LayoutUtil.fluidColumnLoc(5, 0, 8, 0, SHIPMENT_STATUS)));

		dashboardLayout.addComponent(sampleCountsLayout);

		labResultsStatisticsComponent =
			new FinalLaboratoryResultsStatisticsComponent(Captions.sampleDashboardAllSamples, null, Captions.sampleDashboardFinalLabResults, true);
		sampleCountsLayout.addComponent(labResultsStatisticsComponent, LAB_RESULTS);

	}

	@Override
	public void refreshDashboard() {
		dataProvider.refreshData();

		labResultsStatisticsComponent.update(dataProvider.getNewCasesFinalLabResultCountsByResultType());
	}
}
