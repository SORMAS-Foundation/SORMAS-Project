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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.dashboard.contacts.DashboardContactsView;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;
import de.symeda.sormas.ui.dashboard.statistics.AbstractDashboardStatisticsComponent;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.dashboard.surveillance.DashboardSurveillanceView;
import de.symeda.sormas.ui.dashboard.surveillance.DiseaseBurdenSurveillanceComponent;
import de.symeda.sormas.ui.dashboard.surveillance.DiseaseDifferenceSurveillanceComponent;
import de.symeda.sormas.ui.reports.WeeklyReportOfficersGrid;
import de.symeda.sormas.ui.reports.WeeklyReportRegionsGrid;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public abstract class AbstractDashboardView extends AbstractView {

	public static final String I18N_PREFIX = "Dashboard";

	public static final String ROOT_VIEW_NAME = "dashboard";

	protected DashboardDataProvider dashboardDataProvider;

	protected VerticalLayout dashboardLayout;
	protected DashboardFilterLayout filterLayout;
	protected AbstractDashboardStatisticsComponent statisticsComponent;
	protected AbstractEpiCurveComponent epiCurveComponent;
	protected DashboardMapComponent mapComponent;
	protected HorizontalLayout epiCurveAndMapLayout;
	private VerticalLayout epiCurveLayout;
	private VerticalLayout mapLayout;
//	protected DiseaseBurdenGrid diseaseBurdenGrid;
	protected DiseaseBurdenSurveillanceComponent diseaseBurdenComponent;
	protected DiseaseDifferenceSurveillanceComponent diseaseDifferenceComponent;
	protected HorizontalLayout diseaseBurdenAndCasesLayout;
	private VerticalLayout diseaseBurdenLayout;
	private VerticalLayout diseaseDifferenceLayout;

	protected AbstractDashboardView(String viewName, DashboardType dashboardType) {
		super(viewName);	

		addStyleName(DashboardCssStyles.DASHBOARD_SCREEN);

		dashboardDataProvider = new DashboardDataProvider();
		if (dashboardDataProvider.getDashboardType() == null) {
			dashboardDataProvider.setDashboardType(dashboardType);
		}

		OptionGroup dashboardSwitcher = new OptionGroup();
		CssStyles.style(dashboardSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		if (CurrentUser.getCurrent().hasUserRight(UserRight.DASHBOARD_SURVEILLANCE_ACCESS)) {
			dashboardSwitcher.addItem(DashboardType.SURVEILLANCE);
			dashboardSwitcher.setItemCaption(DashboardType.SURVEILLANCE, I18nProperties.getEnumCaption(DashboardType.SURVEILLANCE));
		}
		if (CurrentUser.getCurrent().hasUserRight(UserRight.DASHBOARD_CONTACT_ACCESS)) {
			dashboardSwitcher.addItem(DashboardType.CONTACTS);
			dashboardSwitcher.setItemCaption(DashboardType.CONTACTS, I18nProperties.getEnumCaption(DashboardType.CONTACTS));
		}
		dashboardSwitcher.setValue(dashboardType);
		dashboardSwitcher.addValueChangeListener(e -> {
			dashboardDataProvider.setDashboardType((DashboardType) e.getProperty().getValue());
			if (e.getProperty().getValue().equals(DashboardType.SURVEILLANCE)) {
				SormasUI.get().getNavigator().navigateTo(DashboardSurveillanceView.VIEW_NAME);
			} else {
				SormasUI.get().getNavigator().navigateTo(DashboardContactsView.VIEW_NAME);
			}
		});
		addHeaderComponent(dashboardSwitcher);
		
		// Hide the dashboard switcher if only one dashboard is accessible to the user
		if (dashboardSwitcher.size() <= 1) {
			dashboardSwitcher.setVisible(false);
		}

		// Dashboard layout
		dashboardLayout = new VerticalLayout();
		dashboardLayout.setSpacing(false);
		dashboardLayout.setSizeFull();
		dashboardLayout.setStyleName("crud-main-layout");

		// Filter bar
		filterLayout = new DashboardFilterLayout(this, dashboardDataProvider);
		dashboardLayout.addComponent(filterLayout);

		addComponent(dashboardLayout);
	}

	protected HorizontalLayout createEpiCurveAndMapLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName(DashboardCssStyles.CURVE_AND_MAP_LAYOUT);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);

		// Epi curve layout
		epiCurveLayout = createEpiCurveLayout();
		layout.addComponent(epiCurveLayout);

		// Map layout	
		mapLayout = createMapLayout();
		layout.addComponent(mapLayout);

		return layout;
	}

	protected VerticalLayout createEpiCurveLayout() {
		if (epiCurveComponent == null) {
			throw new UnsupportedOperationException("EpiCurveComponent needs to be initialized before calling createEpiCurveLayout");
		}

		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeight(400, Unit.PIXELS);

		epiCurveComponent.setSizeFull();

		layout.addComponent(epiCurveComponent);
		layout.setExpandRatio(epiCurveComponent, 1);

		epiCurveComponent.setExpandListener(e -> {
			dashboardLayout.removeComponent(statisticsComponent);
			epiCurveAndMapLayout.removeComponent(mapLayout);
			AbstractDashboardView.this.setHeight(100, Unit.PERCENTAGE);
			epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
			epiCurveLayout.setSizeFull();			
		});

		epiCurveComponent.setCollapseListener(e -> {
			dashboardLayout.addComponent(statisticsComponent, 1);
			epiCurveAndMapLayout.addComponent(mapLayout, 1);
			epiCurveLayout.setHeight(400, Unit.PIXELS);
			AbstractDashboardView.this.setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
		});

		return layout;
	}

	protected VerticalLayout createMapLayout() {
		if (mapComponent == null) {
			throw new UnsupportedOperationException("MapComponent needs to be initialized before calling createMapLayout");
		}
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeight(400, Unit.PIXELS);

		mapComponent.setSizeFull();

		layout.addComponent(mapComponent);
		layout.setExpandRatio(mapComponent, 1);

		mapComponent.setExpandListener(e -> {
			dashboardLayout.removeComponent(statisticsComponent);
			epiCurveAndMapLayout.removeComponent(epiCurveLayout);
			AbstractDashboardView.this.setHeight(100, Unit.PERCENTAGE);
			epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
			mapLayout.setSizeFull();
		});

		mapComponent.setCollapseListener(e -> {
			dashboardLayout.addComponent(statisticsComponent, 1);
			epiCurveAndMapLayout.addComponent(epiCurveLayout, 0);
			mapLayout.setHeight(400, Unit.PIXELS);
			AbstractDashboardView.this.setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
		});

		return layout;
	}
	

	protected HorizontalLayout createDiseaseBurdenAndCasesLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);

		diseaseBurdenLayout = createDiseaseBurdenLayout();
		layout.addComponent(diseaseBurdenLayout);

		diseaseDifferenceLayout = createDiseaseDifferenceLayout();
		layout.addComponent(diseaseDifferenceLayout);

		return layout;
	}
	
	protected VerticalLayout createDiseaseBurdenLayout() {
		if (diseaseBurdenComponent == null) {
			throw new UnsupportedOperationException("DiseaseBurdenComponent needs to be initialized before calling createDiseaseBurdenLayout");
		}
		
		DiseaseBurdenSurveillanceComponent layout = diseaseBurdenComponent;

		return layout;
	}

	protected VerticalLayout createDiseaseDifferenceLayout() {
		if (diseaseDifferenceComponent == null) {
			throw new UnsupportedOperationException("DiseaseDifferenceComponent needs to be initialized before calling createDiseaseDifferenceLayout");
		}
		
		DiseaseDifferenceSurveillanceComponent layout = diseaseDifferenceComponent;
		
		return layout;
	}

	public void refreshDashboard() {		
		dashboardDataProvider.refreshData();

		// Updates statistics
		statisticsComponent.updateStatistics(dashboardDataProvider.getDisease());

		// Update cases and contacts shown on the map
		mapComponent.refreshMap();

		// Epi curve chart has to be created again due to a canvas resizing issue when simply refreshing the component
		epiCurveComponent.clearAndFillEpiCurveChart();
		
		diseaseBurdenComponent.refresh();
		diseaseDifferenceComponent.refresh();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		refreshDashboard();
	}


}
