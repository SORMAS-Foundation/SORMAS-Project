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
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.dashboard.sample.SampleShipmentStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.components.DashboardHeadingComponent;
import de.symeda.sormas.ui.dashboard.sample.components.SampleCountTilesComponent;
import de.symeda.sormas.ui.dashboard.sample.components.SampleDashboardMapComponent;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.LaboratoryResultsStatisticsComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class SampleDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/samples";

	private static final int EPI_CURVE_AND_MAP_HEIGHT = 555;

	private static final String LAB_RESULTS = "labResults";
	private static final String SAMPLE_PURPOSE = "samplePurpose";
	private static final String TEST_RESULTS = "testResults";
	private static final String SPECIMEN_CONDITION = "specimenCondition";
	private static final String SHIPMENT_STATUS = "shipmentStatus";

	private final SampleDashboardDataProvider dataProvider;

	private final CustomLayout sampleCountsLayout;
	private final HorizontalLayout epiCurveAndMapLayout;
	private final VerticalLayout epiCurveLayout;
	private final VerticalLayout mapLayout;

	private final DashboardHeadingComponent heading;
	private final LaboratoryResultsStatisticsComponent sampleCountsByResultType;
	private final LaboratoryResultsStatisticsComponent testCountsByResultType;
	private final SampleCountTilesComponent<SamplePurpose> countsByPurpose;
	private final SampleCountTilesComponent<SpecimenCondition> countsBySpecimenCondition;
	private final SampleCountTilesComponent<SampleShipmentStatus> countsByShipmentStatus;
	private final SampleEpiCurveComponent epiCurveComponent;
	private final SampleDashboardMapComponent mapComponent;

	public SampleDashboardView() {
		super(VIEW_NAME, DashboardType.SAMPLES);

		dashboardSwitcher.setValue(DashboardType.SAMPLES);
		dashboardSwitcher.addValueChangeListener(this::navigateToDashboardView);

		dashboardLayout.setHeightUndefined();

		dataProvider = new SampleDashboardDataProvider();
		SampleDashboardFilterLayout filterLayout = new SampleDashboardFilterLayout(this, dataProvider);

		dashboardLayout.addComponent(filterLayout);
		dashboardLayout.setExpandRatio(filterLayout, 0);

		heading = new DashboardHeadingComponent(Captions.sampleDashboardAllSamples, null);
		heading.setMargin(new MarginInfo(true, true, false, true));
		dashboardLayout.addComponent(heading);

		sampleCountsLayout = new CustomLayout();
		sampleCountsLayout.setTemplateContents(
			LayoutUtil.fluidRow(
				LayoutUtil.fluidColumnLoc(4, 0, 5, 0, LAB_RESULTS),
				LayoutUtil.fluidColumnLoc(5, 0, 7, 0, SAMPLE_PURPOSE),
				LayoutUtil.fluidColumnLoc(3, 0, 6, 0, TEST_RESULTS))
				+ LayoutUtil.fluidRowCss(
					CssStyles.VSPACE_TOP_1,
					LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SHIPMENT_STATUS),
					LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SPECIMEN_CONDITION)));

		dashboardLayout.addComponent(sampleCountsLayout);

		sampleCountsByResultType =
			new LaboratoryResultsStatisticsComponent(Captions.sampleDashboardAllSamples, null, Captions.sampleDashboardFinalLabResults, true, false);
		sampleCountsByResultType.hideHeading();
		sampleCountsByResultType.setWithPercentage(true);
		sampleCountsLayout.addComponent(sampleCountsByResultType, LAB_RESULTS);

		countsByPurpose =
			new SampleCountTilesComponent<>(SamplePurpose.class, Captions.sampleDashboardSamplePurpose, this::getBackgroundStyleForPurpose, null);
		countsByPurpose.setTitleStyleNames(CssStyles.H3, CssStyles.VSPACE_TOP_5);
		countsByPurpose.setGroupLabelStyle(CssStyles.LABEL_LARGE + " " + CssStyles.LABEL_WHITE_SPACE_NORMAL);
		sampleCountsLayout.addComponent(countsByPurpose, SAMPLE_PURPOSE);

		countsByShipmentStatus = new SampleCountTilesComponent<>(
			SampleShipmentStatus.class,
			Captions.sampleDashboardShipmentStatus,
			this::getBackgroundStyleForShipmentStatus,
			Descriptions.sampleDashboardCountsByShipmentStatus);
		countsByShipmentStatus.setWithPercentage(true);
		countsByShipmentStatus.setGroupLabelStyle(CssStyles.LABEL_UPPERCASE);
		sampleCountsLayout.addComponent(countsByShipmentStatus, SHIPMENT_STATUS);

		countsBySpecimenCondition = new SampleCountTilesComponent<>(
			SpecimenCondition.class,
			Captions.sampleDashboardSpecimenCondition,
			this::getBackgroundStyleForSpecimenCondition,
			Descriptions.sampleDashboardCountsBySpecimenCondition);
		countsBySpecimenCondition.setWithPercentage(true);
		countsBySpecimenCondition.setGroupLabelStyle(CssStyles.LABEL_UPPERCASE);
		sampleCountsLayout.addComponent(countsBySpecimenCondition, SPECIMEN_CONDITION);

		testCountsByResultType = new LaboratoryResultsStatisticsComponent(Captions.sampleDashboardTestResults, null, null, false, false);
		testCountsByResultType.setWithPercentage(true);
		testCountsByResultType.setTitleStyleNamesOnTitleLabel(CssStyles.H3, CssStyles.VSPACE_TOP_5);
		testCountsByResultType.setTitleStyleNamesOnTotalLabel(
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_XXLARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_NONE,
			CssStyles.HSPACE_RIGHT_4,
			CssStyles.VSPACE_TOP_NONE);
		sampleCountsLayout.addComponent(testCountsByResultType, TEST_RESULTS);

		epiCurveComponent = new SampleEpiCurveComponent(dataProvider);
		epiCurveLayout = createEpiCurveLayout();

		mapComponent = new SampleDashboardMapComponent(dataProvider);
		mapLayout = createMapLayout(mapComponent);

		epiCurveAndMapLayout = createEpiCurveAndMapLayout(epiCurveLayout, mapLayout);
		epiCurveAndMapLayout.addStyleName(CssStyles.VSPACE_TOP_1);
		dashboardLayout.addComponent(epiCurveAndMapLayout);
		dashboardLayout.setExpandRatio(epiCurveAndMapLayout, 1);
	}

	@Override
	public void refreshDashboard() {
		dataProvider.refreshData();

		heading.updateTotalLabel(String.valueOf(dataProvider.getSampleCountsByResultType().values().stream().mapToLong(Long::longValue).sum()));

		sampleCountsByResultType.update(dataProvider.getSampleCountsByResultType());
		countsByPurpose.update(dataProvider.getSampleCountsByPurpose());
		countsBySpecimenCondition.update(dataProvider.getSampleCountsBySpecimenCondition());
		countsByShipmentStatus.update(dataProvider.getSampleCountsByShipmentStatus());
		testCountsByResultType.update(dataProvider.getTestResultCountsByResultType());
		epiCurveComponent.clearAndFillEpiCurveChart();
		mapComponent.refreshMap();
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

	protected HorizontalLayout createEpiCurveAndMapLayout(VerticalLayout epiCurveLayout, VerticalLayout mapLayout) {
		HorizontalLayout layout = new HorizontalLayout(epiCurveLayout, mapLayout);
		layout.addStyleName(DashboardCssStyles.CURVE_AND_MAP_LAYOUT);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);
		layout.setSpacing(false);

		return layout;
	}

	protected VerticalLayout createEpiCurveLayout() {
		if (epiCurveComponent == null) {
			throw new UnsupportedOperationException("EpiCurveComponent needs to be initialized before calling createEpiCurveLayout");
		}

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		layout.setHeight(EPI_CURVE_AND_MAP_HEIGHT, Unit.PIXELS);

		epiCurveComponent.setSizeFull();

		layout.addComponent(epiCurveComponent);
		layout.setExpandRatio(epiCurveComponent, 1);

		epiCurveComponent.setExpandListener(expanded -> {
			setExpanded(expanded, layout, mapLayout, 1);
		});

		return layout;
	}

	private VerticalLayout createMapLayout(SampleDashboardMapComponent mapComponent) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		layout.setHeight(EPI_CURVE_AND_MAP_HEIGHT, Unit.PIXELS);

		mapComponent.setSizeFull();

		layout.addComponent(mapComponent);
		layout.setExpandRatio(mapComponent, 1);

		mapComponent.setExpandListener(expanded -> {
			setExpanded(expanded, layout, epiCurveLayout, 0);
		});

		return layout;
	}

	private void setExpanded(Boolean expanded, Component componentToExpand, Component componentToRemove, int removedComponentIndex) {
		if (expanded) {
			dashboardLayout.removeComponent(heading);
			dashboardLayout.removeComponent(sampleCountsLayout);
			epiCurveAndMapLayout.removeComponent(componentToRemove);
			setHeight(100, Unit.PERCENTAGE);

			epiCurveAndMapLayout.setHeightFull();
			setHeightFull();
			componentToExpand.setSizeFull();
			dashboardLayout.setHeightFull();
		} else {
			dashboardLayout.addComponent(heading, 1);
			dashboardLayout.addComponent(sampleCountsLayout, 2);
			epiCurveAndMapLayout.addComponent(componentToRemove, removedComponentIndex);
			mapComponent.refreshMap();

			componentToExpand.setHeight(EPI_CURVE_AND_MAP_HEIGHT, Unit.PIXELS);
			setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
			dashboardLayout.setHeightUndefined();
		}
	}
}
