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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization.components.AefiByVaccineDoseChart;
import de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization.components.AefiCountTilesComponent;
import de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization.components.AefiDashboardMapComponent;
import de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization.components.AefiReactionsByGenderChart;
import de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization.components.AefiTypeStatisticsGroupComponent;
import de.symeda.sormas.ui.dashboard.components.DashboardHeadingComponent;
import de.symeda.sormas.ui.utils.CssStyles;

public class AefiDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/adverseevents";

	private static final int EPI_CURVE_AND_MAP_HEIGHT = 555;

	private static final String HEADING_LOC = "headingLoc";
	private static final String AEFI_TYPE_LOC = "aefiTypeLoc";
	private static final String VACCINES_LOC = "vaccinesLoc";
	private static final String VACCINE_DOSE_LOC = "vaccineDoseLoc";
	private static final String REACTIONS_LOC = "reactionsLoc";

	private final AefiDashboardDataProvider dataProvider;

	private final CustomLayout aefiCountsLayout;
	private final HorizontalLayout epiCurveAndMapLayout;
	private final VerticalLayout epiCurveLayout;
	private final VerticalLayout mapLayout;

	private final DashboardHeadingComponent heading;
	private final AefiCountTilesComponent<AefiType> aefiCountsByType;
	private final AefiTypeStatisticsGroupComponent aefiCountsByVaccine;
	private final AefiByVaccineDoseChart vaccineDoseChart;
	private final AefiReactionsByGenderChart reactionsByGenderChart;
	private final AefiEpiCurveComponent epiCurveComponent;
	private final AefiDashboardMapComponent mapComponent;

	public AefiDashboardView() {
		super(VIEW_NAME);

		CssStyles.style(getViewTitleLabel(), CssStyles.PAGE_TITLE);

		dashboardSwitcher.setValue(DashboardType.ADVERSE_EVENTS);
		dashboardSwitcher.addValueChangeListener(this::navigateToDashboardView);

		dashboardLayout.setHeightUndefined();

		dataProvider = new AefiDashboardDataProvider();
		AefiDashboardFilterLayout filterLayout = new AefiDashboardFilterLayout(this, dataProvider);

		dashboardLayout.addComponent(filterLayout);
		dashboardLayout.setExpandRatio(filterLayout, 0);

		aefiCountsLayout = new CustomLayout();
		//@formatter:off
		aefiCountsLayout.setTemplateContents(
			fluidRowCss(
					CssStyles.PADDING_X_20 + " " + CssStyles.VSPACE_TOP_2,
				fluidColumn(6, 0, locCss("", HEADING_LOC)),
				fluidColumn(4, 0, locCss("", AEFI_TYPE_LOC))
			)
			+ fluidRowCss(
				CssStyles.VSPACE_TOP_2,
				fluidColumnLoc(12, 0, 12, 0, VACCINES_LOC)
			)
			+ fluidRowCss(
		CssStyles.PADDING_X_20 + " " + CssStyles.VSPACE_TOP_2,
				fluidColumn(6, 0, locCss("", VACCINE_DOSE_LOC)),
				fluidColumn(6, 0, locCss("", REACTIONS_LOC))
			)
		);
		//@formatter:on
		dashboardLayout.addComponent(aefiCountsLayout);

		heading = new DashboardHeadingComponent(Captions.aefiDashboardAllAefi, null);
		heading.setMargin(false);
		aefiCountsLayout.addComponent(heading, HEADING_LOC);

		aefiCountsByType = new AefiCountTilesComponent<>(AefiType.class, "", this::getBackgroundStyleForAefiCountByType, null);
		aefiCountsByType.setTitleStyleNames(CssStyles.H3, CssStyles.VSPACE_TOP_5);
		aefiCountsByType.setGroupLabelStyle(CssStyles.LABEL_LARGE + " " + CssStyles.LABEL_WHITE_SPACE_NORMAL);
		aefiCountsLayout.addComponent(aefiCountsByType, AEFI_TYPE_LOC);

		aefiCountsByVaccine = new AefiTypeStatisticsGroupComponent();
		aefiCountsByVaccine.addStyleNames(CssStyles.PADDING_X_20);
		aefiCountsLayout.addComponent(aefiCountsByVaccine, VACCINES_LOC);

		vaccineDoseChart = new AefiByVaccineDoseChart();
		aefiCountsLayout.addComponent(vaccineDoseChart, VACCINE_DOSE_LOC);

		reactionsByGenderChart = new AefiReactionsByGenderChart();
		aefiCountsLayout.addComponent(reactionsByGenderChart, REACTIONS_LOC);

		epiCurveComponent = new AefiEpiCurveComponent(dataProvider);
		epiCurveLayout = createEpiCurveLayout();

		mapComponent = new AefiDashboardMapComponent(dataProvider);
		mapLayout = createMapLayout(mapComponent);

		epiCurveAndMapLayout = createEpiCurveAndMapLayout(epiCurveLayout, mapLayout);
		epiCurveAndMapLayout.addStyleName(CssStyles.VSPACE_TOP_1);
		dashboardLayout.addComponent(epiCurveAndMapLayout);
		dashboardLayout.setExpandRatio(epiCurveAndMapLayout, 1);
	}

	@Override
	public void refreshDashboard() {
		dataProvider.refreshData();

		heading.updateTotalLabel(String.valueOf(dataProvider.getAefiCountsByType().values().stream().mapToLong(Long::longValue).sum()));

		aefiCountsByType.update(dataProvider.getAefiCountsByType());
		aefiCountsByVaccine.update(dataProvider.getAefiCountsByVaccine());
		vaccineDoseChart.update(dataProvider.getAefiByVaccineDoseChartData());
		reactionsByGenderChart.update(dataProvider.getAefiEventsByGenderChartData());
		epiCurveComponent.clearAndFillEpiCurveChart();
		mapComponent.refreshMap();
	}

	private String getBackgroundStyleForAefiCountByType(AefiType aefiType) {
		return aefiType == AefiType.SERIOUS ? "background-shipment-status-not-shipped" : "background-internal-lab-samples";
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

	private VerticalLayout createMapLayout(AefiDashboardMapComponent mapComponent) {
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
			dashboardLayout.removeComponent(aefiCountsLayout);
			epiCurveAndMapLayout.removeComponent(componentToRemove);
			setHeight(100, Unit.PERCENTAGE);

			epiCurveAndMapLayout.setHeightFull();
			setHeightFull();
			componentToExpand.setSizeFull();
			dashboardLayout.setHeightFull();
		} else {
			dashboardLayout.addComponent(heading, 1);
			dashboardLayout.addComponent(aefiCountsLayout, 2);
			epiCurveAndMapLayout.addComponent(componentToRemove, removedComponentIndex);
			mapComponent.refreshMap();

			componentToExpand.setHeight(EPI_CURVE_AND_MAP_HEIGHT, Unit.PIXELS);
			setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
			dashboardLayout.setHeightUndefined();
		}
	}
}
