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

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.dashboard.sample.SampleShipmentStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.components.DashboardHeadingComponent;
import de.symeda.sormas.ui.dashboard.sample.components.SampleCountTilesComponent;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.FinalLaboratoryResultsStatisticsComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class SampleDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/samples";

	private static final String LAB_RESULTS = "labResults";
	private static final String SAMPLE_PURPOSE = "samplePurpose";
	private static final String TEST_RESULTS = "testResults";
	private static final String SPECIMEN_CONDITION = "specimenCondition";
	private static final String SHIPMENT_STATUS = "shipmentStatus";

	private final SampleDashboardDataProvider dataProvider;
	private final FinalLaboratoryResultsStatisticsComponent countsByResultType;
	private final SampleCountTilesComponent<SamplePurpose> countsByPurpose;
	private final SampleCountTilesComponent<SpecimenCondition> countsBySpecimenCondition;
	private final SampleCountTilesComponent<SampleShipmentStatus> countsByShipmentStatus;
	private final DashboardHeadingComponent heading;

	public SampleDashboardView() {
		super(VIEW_NAME);

		dashboardSwitcher.setValue(DashboardType.SAMPLES);
		dashboardSwitcher.addValueChangeListener(this::navigateToDashboardView);

		dashboardLayout.setHeightUndefined();

		dataProvider = new SampleDashboardDataProvider();
		SampleDashboardFilterLayout filterLayout = new SampleDashboardFilterLayout(this, dataProvider);

		dashboardLayout.addComponent(filterLayout);

		heading = new DashboardHeadingComponent(Captions.sampleDashboardAllSamples, null);
		heading.setMargin(new MarginInfo(true, true, false, true));
		dashboardLayout.addComponent(heading);

		CustomLayout sampleCountsLayout = new CustomLayout();
		sampleCountsLayout.setTemplateContents(
			LayoutUtil.fluidRowLocs(LAB_RESULTS, SAMPLE_PURPOSE, TEST_RESULTS)
				+ LayoutUtil.fluidRowCss(
					CssStyles.VSPACE_TOP_1,
					LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SHIPMENT_STATUS),
					LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SPECIMEN_CONDITION)));

		dashboardLayout.addComponent(sampleCountsLayout);

		countsByResultType =
			new FinalLaboratoryResultsStatisticsComponent(Captions.sampleDashboardAllSamples, null, Captions.sampleDashboardFinalLabResults, true);
		countsByResultType.hideHeading();
		sampleCountsLayout.addComponent(countsByResultType, LAB_RESULTS);

		countsByPurpose =
			new SampleCountTilesComponent<>(SamplePurpose.class, Captions.sampleDashboardSamplePurpose, this::getBackgroundStyleForPurpose, null);
		countsByPurpose.setTitleStyleNames(CssStyles.H3, CssStyles.VSPACE_TOP_NONE);
		countsByPurpose.setGroupLabelStyle(CssStyles.LABEL_LARGE);
		sampleCountsLayout.addComponent(countsByPurpose, SAMPLE_PURPOSE);

		countsByShipmentStatus = new SampleCountTilesComponent<>(
			SampleShipmentStatus.class,
			Captions.sampleDashboardShipmentStatus,
			this::getBackgroundStyleForShipmentStatus,
			Descriptions.sampleDashboardCountsByShipmentStatus);
		countsByShipmentStatus.setTitleStyleNames(CssStyles.H4);
		countsByShipmentStatus.setWithPercentage(true);
		countsByShipmentStatus.setGroupLabelStyle(CssStyles.LABEL_UPPERCASE);
		sampleCountsLayout.addComponent(countsByShipmentStatus, SHIPMENT_STATUS);

		countsBySpecimenCondition = new SampleCountTilesComponent<>(
			SpecimenCondition.class,
			Captions.sampleDashboardSpecimenCondition,
			this::getBackgroundStyleForSpecimenCondition,
			Descriptions.sampleDashboardCountsBySpecimenCondition);
		countsBySpecimenCondition.setTitleStyleNames(CssStyles.H4);
		countsBySpecimenCondition.setWithPercentage(true);
		countsBySpecimenCondition.setGroupLabelStyle(CssStyles.LABEL_UPPERCASE);
		sampleCountsLayout.addComponent(countsBySpecimenCondition, SPECIMEN_CONDITION);
	}

	@Override
	public void refreshDashboard() {
		dataProvider.refreshData();

		heading.updateTotalLabel(String.valueOf(dataProvider.getSampleCountsByResultType().values().stream().mapToLong(Long::longValue).sum()));

		countsByResultType.update(dataProvider.getSampleCountsByResultType());
		countsByPurpose.update(dataProvider.getSampleCountsByPurpose());
		countsBySpecimenCondition.update(dataProvider.getSampleCountsBySpecimenCondition());
		countsByShipmentStatus.update(dataProvider.getSampleCountsByShipmentStatus());
	}

	private String getBackgroundStyleForPurpose(SamplePurpose purpose) {
		return purpose == SamplePurpose.EXTERNAL ? "background-external-lab-samples" : "background-internal-lab-samples";
	}

	private String getBackgroundStyleForSpecimenCondition(SpecimenCondition specimenCondition) {
		if (specimenCondition == null) {
			return "background-specimen-condition-not-specified";
		}

		switch (specimenCondition) {
		case ADEQUATE:
			return "background-specimen-condition-adequate";
		case NOT_ADEQUATE:
			return "background-specimen-condition-inadequate";
		default:
			return "background-specimen-condition-not-specified";
		}
	}

	private String getBackgroundStyleForShipmentStatus(SampleShipmentStatus shipmentStatus) {
		switch (shipmentStatus) {
		case SHIPPED:
			return "background-shipment-status-shipped";
		case NOT_SHIPPED:
			return "background-shipment-status-not-shipped";
		default:
			return "background-shipment-status-received";
		}
	}
}
