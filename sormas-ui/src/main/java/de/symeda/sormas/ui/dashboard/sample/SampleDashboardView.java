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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.FinalLaboratoryResultsStatisticsComponent;
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

	private final FinalLaboratoryResultsStatisticsComponent labResultsStatisticsComponent;
	private final HorizontalLayout epiCurveAndMapLayout;

	private final VerticalLayout epiCurveLayout;
	private final VerticalLayout mapLayout;

	private final SampleEpiCurveComponent epiCurveComponent;

	public SampleDashboardView() {
		super(VIEW_NAME);

		dashboardSwitcher.setValue(DashboardType.SAMPLES);
		dashboardSwitcher.addValueChangeListener(this::navigateToDashboardView);

		dashboardLayout.setHeightUndefined();

		dataProvider = new SampleDashboardDataProvider();
		SampleDashboardFilterLayout filterLayout = new SampleDashboardFilterLayout(this, dataProvider);

		dashboardLayout.addComponent(filterLayout);

		sampleCountsLayout = new CustomLayout();
		sampleCountsLayout.setTemplateContents(
			LayoutUtil.fluidRowLocs(LAB_RESULTS, SAMPLE_PURPOSE, TEST_RESULTS)
				+ LayoutUtil
					.fluidRow(LayoutUtil.fluidColumnLoc(2, 0, 4, 0, SPECIMEN_CONDITION), LayoutUtil.fluidColumnLoc(5, 0, 8, 0, SHIPMENT_STATUS)));

		dashboardLayout.addComponent(sampleCountsLayout);

		Label warningMessage = new Label(I18nProperties.getString(Strings.sampleDashboardWarning));
		warningMessage.addStyleNames(CssStyles.HSPACE_LEFT_2, CssStyles.VSPACE_TOP_2, CssStyles.LABEL_WARNING);
		labResultsStatisticsComponent =
			new FinalLaboratoryResultsStatisticsComponent(Captions.sampleDashboardAllSamples, null, Captions.sampleDashboardFinalLabResults, true);
		VerticalLayout labResultStatisticsLayout = new VerticalLayout(warningMessage, labResultsStatisticsComponent);
		labResultStatisticsLayout.setMargin(false);
		labResultStatisticsLayout.setSpacing(false);
		sampleCountsLayout.addComponent(labResultStatisticsLayout, LAB_RESULTS);

		epiCurveComponent = new SampleEpiCurveComponent(dataProvider);

		epiCurveLayout = createEpiCurveLayout();
		mapLayout = createMapLayout();
		epiCurveAndMapLayout = createEpiCurveAndMapLayout(epiCurveLayout, mapLayout);
		dashboardLayout.addComponent(epiCurveAndMapLayout);
	}

	@Override
	public void refreshDashboard() {
		dataProvider.refreshData();

		labResultsStatisticsComponent.update(dataProvider.getNewCasesFinalLabResultCountsByResultType());
		epiCurveComponent.clearAndFillEpiCurveChart();
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
			if (expanded) {
				dashboardLayout.removeComponent(sampleCountsLayout);
				epiCurveAndMapLayout.removeComponent(mapLayout);
				setHeight(100, Unit.PERCENTAGE);
				epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
				epiCurveLayout.setSizeFull();
			} else {
				dashboardLayout.addComponent(sampleCountsLayout, 1);
				epiCurveAndMapLayout.addComponent(mapLayout, 1);
				// TODO Should be uncommented when the map is added on the dashboard
				//mapComponent.refreshMap();

				epiCurveLayout.setHeight(EPI_CURVE_AND_MAP_HEIGHT, Unit.PIXELS);
				this.setHeightUndefined();
				epiCurveAndMapLayout.setHeightUndefined();
			}
		});

		return layout;
	}

	private VerticalLayout createMapLayout() {
		return new VerticalLayout();
	}
}
