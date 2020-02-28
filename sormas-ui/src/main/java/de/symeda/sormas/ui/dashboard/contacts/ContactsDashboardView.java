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
package de.symeda.sormas.ui.dashboard.contacts;

import java.util.Optional;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;
import de.symeda.sormas.ui.dashboard.statistics.AbstractDashboardStatisticsComponent;
import de.symeda.sormas.ui.dashboard.visualisation.DashboardNetworkComponent;

@SuppressWarnings("serial")
public class ContactsDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/contacts";

	protected AbstractDashboardStatisticsComponent statisticsComponent;
	protected AbstractEpiCurveComponent epiCurveComponent;
	protected DashboardMapComponent mapComponent;
	protected Optional<DashboardNetworkComponent> networkDiagramComponent;
	protected HorizontalLayout epiCurveAndMapLayout;
	protected HorizontalLayout networkDiagramRowLayout;
	private VerticalLayout epiCurveLayout;
	private Optional<VerticalLayout> mapLayout;
	private Optional<VerticalLayout> networkDiagramLayout;

	public ContactsDashboardView() {
		super(VIEW_NAME, DashboardType.CONTACTS);

		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.infoContactDashboard));

		// Add statistics
		statisticsComponent = new ContactsDashboardStatisticsComponent(dashboardDataProvider);
		dashboardLayout.addComponent(statisticsComponent);
		dashboardLayout.setExpandRatio(statisticsComponent, 0);

		epiCurveComponent = new ContactsEpiCurveComponent(dashboardDataProvider);
		mapComponent = new DashboardMapComponent(dashboardDataProvider);

		// Add epi curve and map
		epiCurveAndMapLayout = createEpiCurveAndMapLayout();
		dashboardLayout.addComponent(epiCurveAndMapLayout);
		dashboardLayout.setExpandRatio(epiCurveAndMapLayout, 1);
		
		// add network diagram
		networkDiagramComponent = Optional.of(FacadeProvider.getConfigFacade())
		.map(ConfigFacade::getRScriptExecutable)
		.map(x -> new DashboardNetworkComponent(dashboardDataProvider));
		
		networkDiagramRowLayout = createNetworkDiagramRowLayout();
		dashboardLayout.addComponent(networkDiagramRowLayout);
		dashboardLayout.setExpandRatio(networkDiagramRowLayout, 1);
	}

	protected HorizontalLayout createEpiCurveAndMapLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName(DashboardCssStyles.CURVE_AND_MAP_LAYOUT);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);
		layout.setSpacing(false);

		// Epi curve layout
		epiCurveLayout = createEpiCurveLayout();
		layout.addComponent(epiCurveLayout);

		// Map layout
		mapLayout = createMapLayout();
		mapLayout.ifPresent(layout::addComponent);
		
		return layout;
	}

	protected HorizontalLayout createNetworkDiagramRowLayout() {
		HorizontalLayout layout = new HorizontalLayout();
//		layout.addStyleName(DashboardCssStyles.CURVE_AND_MAP_LAYOUT);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);
		layout.setSpacing(false);

		// network diagram layout 
		networkDiagramLayout = createNetworkDiagramLayout();
		networkDiagramLayout.ifPresent(layout::addComponent);

		return layout;
	}

	protected VerticalLayout createEpiCurveLayout() {
		if (epiCurveComponent == null) {
			throw new UnsupportedOperationException(
					"EpiCurveComponent needs to be initialized before calling createEpiCurveLayout");
		}

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeight(400, Unit.PIXELS);

		epiCurveComponent.setSizeFull();

		layout.addComponent(epiCurveComponent);
		layout.setExpandRatio(epiCurveComponent, 1);

		epiCurveComponent.setExpandListener(expanded -> {
			if (expanded) {
				dashboardLayout.removeComponent(statisticsComponent);
				mapLayout.ifPresent(epiCurveAndMapLayout::removeComponent);
				ContactsDashboardView.this.setHeight(100, Unit.PERCENTAGE);
				epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
				epiCurveLayout.setSizeFull();
			} else {
				dashboardLayout.addComponent(statisticsComponent, 1);
				mapLayout.ifPresent(l -> epiCurveAndMapLayout.addComponent(l, 1));
				epiCurveLayout.setHeight(400, Unit.PIXELS);
				ContactsDashboardView.this.setHeightUndefined();
				epiCurveAndMapLayout.setHeightUndefined();
			}
			networkDiagramRowLayout.setVisible(!expanded);
		});

		return layout;
	}
	
	protected Optional<VerticalLayout> createMapLayout() {
		if (mapComponent == null) {
			throw new UnsupportedOperationException(
					"MapComponent needs to be initialized before calling createMapLayout");
		}
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeight(555, Unit.PIXELS);

		mapComponent.setSizeFull();

		layout.addComponent(mapComponent);
		layout.setExpandRatio(mapComponent, 1);

		mapComponent.setExpandListener(expanded -> {
			
			if (expanded) {
				dashboardLayout.removeComponent(statisticsComponent);
				epiCurveAndMapLayout.removeComponent(epiCurveLayout);
				ContactsDashboardView.this.setHeight(100, Unit.PERCENTAGE);
				epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
				layout.setSizeFull();
			} else {
				dashboardLayout.addComponent(statisticsComponent, 1);
				epiCurveAndMapLayout.addComponent(epiCurveLayout, 0);
				layout.setHeight(400, Unit.PIXELS);
				ContactsDashboardView.this.setHeightUndefined();
				epiCurveAndMapLayout.setHeightUndefined();
			}
			networkDiagramRowLayout.setVisible(!expanded);
		});

		return Optional.of(layout);
	}

	protected Optional<VerticalLayout> createNetworkDiagramLayout() {
		
		return networkDiagramComponent.map(ndc -> {

			VerticalLayout layout = new VerticalLayout();
			layout.setMargin(false);
			layout.setSpacing(false);
			layout.setWidth(100, Unit.PERCENTAGE);
			layout.setHeight(555, Unit.PIXELS);
		
			ndc.setSizeFull();
		
			layout.addComponent(ndc);
			layout.setExpandRatio(ndc, 1);
	
			ndc.setExpandListener(expanded -> {
				if (expanded) {
					dashboardLayout.removeComponent(statisticsComponent);
					ContactsDashboardView.this.setHeight(100, Unit.PERCENTAGE);
					layout.setSizeFull();
					networkDiagramRowLayout.setHeight(100, Unit.PERCENTAGE);
				} else {
					dashboardLayout.addComponent(statisticsComponent, 1);
					ContactsDashboardView.this.setHeightUndefined();
					layout.setHeight(400, Unit.PIXELS);
					networkDiagramRowLayout.setHeightUndefined();
				}
				epiCurveAndMapLayout.setVisible(!expanded);
			});
			return layout;
		});
	}

	public void refreshDashboard() {
		super.refreshDashboard();

		// Updates statistics
		statisticsComponent.updateStatistics(dashboardDataProvider.getDisease());

		// Update cases and contacts shown on the map
		if (mapComponent != null)
			mapComponent.refreshMap();

		// Update cases and contacts shown on the map
		networkDiagramComponent.ifPresent(DashboardNetworkComponent::refreshDiagram);

		// Epi curve chart has to be created again due to a canvas resizing issue when
		// simply refreshing the component
		if (epiCurveComponent != null)
			epiCurveComponent.clearAndFillEpiCurveChart();
	}

}
