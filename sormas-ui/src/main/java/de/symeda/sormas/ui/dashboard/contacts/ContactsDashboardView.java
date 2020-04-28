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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;
import de.symeda.sormas.ui.dashboard.statistics.AbstractDashboardStatisticsComponent;
import de.symeda.sormas.ui.dashboard.visualisation.DashboardNetworkComponent;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class ContactsDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/contacts";

	private static final int ROW_HEIGHT = 555;

	protected AbstractDashboardStatisticsComponent statisticsComponent;
	protected AbstractEpiCurveComponent epiCurveComponent;
	protected DashboardMapComponent mapComponent;
	protected Optional<DashboardNetworkComponent> networkDiagramComponent;
	protected HorizontalLayout epiCurveAndMapLayout;
	protected HorizontalLayout networkDiagramRowLayout;
	protected HorizontalLayout caseStatisticsLayout;
	private VerticalLayout epiCurveLayout;
	private Optional<VerticalLayout> mapLayout;
	private Optional<VerticalLayout> networkDiagramLayout;
	private HorizontalLayout noNetworkDiagramLayout;

	private VerticalLayout rowsLayout;

	// Case counts
	private Label minLabel = new Label();
	private Label maxLabel = new Label();
	private Label avgLabel = new Label();
	private Label sourceCasesLabel = new Label();

	public ContactsDashboardView() {
		super(VIEW_NAME, DashboardType.CONTACTS);

		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.infoContactDashboard));

		rowsLayout = new VerticalLayout();
		rowsLayout.setMargin(false);
		rowsLayout.setSpacing(false);
		dashboardLayout.addComponent(rowsLayout);
		dashboardLayout.setExpandRatio(rowsLayout, 1);

		// Add statistics
		statisticsComponent = new ContactsDashboardStatisticsComponent(dashboardDataProvider);
		rowsLayout.addComponent(statisticsComponent);
		rowsLayout.setExpandRatio(statisticsComponent, 0);

		caseStatisticsLayout = createCaseStatisticsLayout();
		rowsLayout.addComponent(caseStatisticsLayout);
		rowsLayout.setExpandRatio(caseStatisticsLayout, 0);

		epiCurveComponent = new ContactsEpiCurveComponent(dashboardDataProvider);
		mapComponent = new DashboardMapComponent(dashboardDataProvider);

		// Add epi curve and map
		epiCurveAndMapLayout = createEpiCurveAndMapLayout();
		rowsLayout.addComponent(epiCurveAndMapLayout);

		// add network diagram
		if (UserProvider.getCurrent().hasUserRight(UserRight.DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS)) {
			networkDiagramComponent = Optional.of(FacadeProvider.getConfigFacade())
					.map(ConfigFacade::getRScriptExecutable)
					.map(x -> new DashboardNetworkComponent(dashboardDataProvider));

			networkDiagramRowLayout = createNetworkDiagramRowLayout();
			rowsLayout.addComponent(networkDiagramRowLayout);

			networkDiagramLayout.ifPresent(l -> {
				filterLayout.setDiseaseFilterChangeCallback((diseaseSelected) -> {
					networkDiagramLayout.get().setVisible(diseaseSelected);
					noNetworkDiagramLayout.setVisible(!diseaseSelected);
				});
			});
		}
	}

	private HorizontalLayout createCaseStatisticsLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName(DashboardCssStyles.HIGHLIGHTED_STATISTICS_COMPONENT);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);
		layout.setSpacing(false);

		HorizontalLayout contactsPerCaseLayout = createContactsPerCaseLayout();
		HorizontalLayout sourceCasesLayout = createSourceCasesLayout();
		layout.addComponent(contactsPerCaseLayout);
		layout.addComponent(sourceCasesLayout);
		layout.setExpandRatio(contactsPerCaseLayout, 1);
		layout.setComponentAlignment(sourceCasesLayout, Alignment.MIDDLE_RIGHT);

		return layout;
	}

	private HorizontalLayout createContactsPerCaseLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(new MarginInfo(false, true, false, true));
		layout.setSpacing(false);

		Label caption = new Label(I18nProperties.getString(Strings.headingContactsPerCase));
		CssStyles.style(caption, CssStyles.H3, CssStyles.HSPACE_RIGHT_1, CssStyles.VSPACE_TOP_NONE);
		layout.addComponent(caption);

		CssStyles.style(minLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_LARGE_ALT, CssStyles.LABEL_BOLD, CssStyles.VSPACE_5, CssStyles.HSPACE_RIGHT_3);
		layout.addComponent(minLabel);
		CssStyles.style(maxLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_LARGE_ALT, CssStyles.LABEL_BOLD, CssStyles.VSPACE_5, CssStyles.HSPACE_RIGHT_3);
		layout.addComponent(maxLabel);
		CssStyles.style(avgLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_LARGE_ALT, CssStyles.LABEL_BOLD, CssStyles.VSPACE_5);
		layout.addComponent(avgLabel);

		return layout;
	}

	private HorizontalLayout createSourceCasesLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(new MarginInfo(false, true, false, true));
		layout.setSpacing(false);

		Label caption = new Label(I18nProperties.getString(Strings.headingNewSourceCases));
		CssStyles.style(caption, CssStyles.H3, CssStyles.HSPACE_RIGHT_1, CssStyles.VSPACE_TOP_NONE);
		layout.addComponent(caption);

		CssStyles.style(sourceCasesLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_LARGE_ALT, CssStyles.LABEL_BOLD, CssStyles.VSPACE_5);
		layout.addComponent(sourceCasesLabel);

		return layout;
	}

	private void updateCaseCountsAndSourceCasesLabels() {
		List<String> contactUuids = dashboardDataProvider.getContacts().stream().map(dto -> dto.getUuid()).collect(Collectors.toList());
		int[] counts = null;
		if (!contactUuids.isEmpty()) {
			counts = FacadeProvider.getContactFacade().getContactCountsByCasesForDashboard(contactUuids);
		} else {
			counts = new int[3];
		}

		int minContactCount = counts[0];
		int maxContactCount = counts[1];
		int avgContactCount = counts[2];

		minLabel.setValue(I18nProperties.getString(Strings.min) + ": " + minContactCount);
		maxLabel.setValue(I18nProperties.getString(Strings.max) + ": " + maxContactCount);
		avgLabel.setValue(I18nProperties.getString(Strings.average) + ": " + avgContactCount);

		List<String> caseUuids = dashboardDataProvider.getCases().stream().map(dto -> dto.getUuid()).collect(Collectors.toList());
		int nonSourceCases = 0;
		if (!caseUuids.isEmpty()) {
			nonSourceCases = FacadeProvider.getContactFacade().getNonSourceCaseCountForDashboard(caseUuids);
		}

		int newSourceCases = caseUuids.size() - nonSourceCases;
		int newSourceCasesPercentage = newSourceCases == 0 ? 0 : (int) ((newSourceCases * 100.0f) / caseUuids.size());

		sourceCasesLabel.setValue(newSourceCases + " (" + newSourceCasesPercentage + " %)");
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

		noNetworkDiagramLayout = new HorizontalLayout();
		noNetworkDiagramLayout.setMargin(true);
		Label noDiagramLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoNoNetworkDiagram), ContentMode.HTML);
		noNetworkDiagramLayout.addComponent(noDiagramLabel);
		layout.addComponent(noNetworkDiagramLayout);
		layout.setComponentAlignment(noNetworkDiagramLayout, Alignment.MIDDLE_CENTER);
		noNetworkDiagramLayout.setVisible(false);

		networkDiagramLayout.ifPresent(l -> {
			l.setVisible(filterLayout.hasDiseaseSelected());
			noNetworkDiagramLayout.setVisible(!filterLayout.hasDiseaseSelected());
		});

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
		layout.setHeight(ROW_HEIGHT, Unit.PIXELS);

		epiCurveComponent.setSizeFull();

		layout.addComponent(epiCurveComponent);
		layout.setExpandRatio(epiCurveComponent, 1);

		epiCurveComponent.setExpandListener(expanded -> {
			if (expanded) {
				rowsLayout.removeComponent(statisticsComponent);
				mapLayout.ifPresent(epiCurveAndMapLayout::removeComponent);
				ContactsDashboardView.this.setHeight(100, Unit.PERCENTAGE);
				epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
				epiCurveLayout.setSizeFull();
				rowsLayout.setSizeFull();
			} else {
				rowsLayout.addComponent(statisticsComponent, 0);
				mapLayout.ifPresent(l -> epiCurveAndMapLayout.addComponent(l, 1));
				epiCurveLayout.setHeight(ROW_HEIGHT, Unit.PIXELS);
				ContactsDashboardView.this.setHeightUndefined();
				epiCurveAndMapLayout.setHeightUndefined();
				rowsLayout.setHeightUndefined();
			}
			caseStatisticsLayout.setVisible(!expanded);
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
		layout.setHeight(ROW_HEIGHT, Unit.PIXELS);

		mapComponent.setSizeFull();

		layout.addComponent(mapComponent);
		layout.setExpandRatio(mapComponent, 1);

		mapComponent.setExpandListener(expanded -> {

			if (expanded) {
				rowsLayout.removeComponent(statisticsComponent);
				epiCurveAndMapLayout.removeComponent(epiCurveLayout);
				ContactsDashboardView.this.setHeight(100, Unit.PERCENTAGE);
				epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
				layout.setSizeFull();
				rowsLayout.setSizeFull();
			} else {
				rowsLayout.addComponent(statisticsComponent, 0);
				epiCurveAndMapLayout.addComponent(epiCurveLayout, 0);
				layout.setHeight(ROW_HEIGHT, Unit.PIXELS);
				ContactsDashboardView.this.setHeightUndefined();
				epiCurveAndMapLayout.setHeightUndefined();
				rowsLayout.setHeightUndefined();
			}
			caseStatisticsLayout.setVisible(!expanded);
			networkDiagramRowLayout.setVisible(!expanded);
		});

		return Optional.of(layout);
	}

	protected Optional<VerticalLayout> createNetworkDiagramLayout() {

		return networkDiagramComponent.map(ndc -> {

			VerticalLayout layout = new VerticalLayout();
			layout.setMargin(false);
			layout.setSpacing(false);
			layout.setHeight(ROW_HEIGHT, Unit.PIXELS);

			ndc.setSizeFull();

			layout.addComponent(ndc);
			layout.setExpandRatio(ndc, 1);

			ndc.setExpandListener(expanded -> {
				if (expanded) {
					rowsLayout.removeComponent(statisticsComponent);
					ContactsDashboardView.this.setHeight(100, Unit.PERCENTAGE);
					layout.setSizeFull();
					networkDiagramRowLayout.setHeight(100, Unit.PERCENTAGE);
					rowsLayout.setSizeFull();
				} else {
					rowsLayout.addComponent(statisticsComponent, 0);
					ContactsDashboardView.this.setHeightUndefined();
					layout.setHeight(ROW_HEIGHT, Unit.PIXELS);
					networkDiagramRowLayout.setHeightUndefined();
					rowsLayout.setHeightUndefined();
				}
				caseStatisticsLayout.setVisible(!expanded);
				epiCurveAndMapLayout.setVisible(!expanded);
			});
			return layout;
		});
	}

	public void refreshDashboard() {
		super.refreshDashboard();

		// Updates statistics
		statisticsComponent.updateStatistics(dashboardDataProvider.getDisease());
		updateCaseCountsAndSourceCasesLabels();

		// Update cases and contacts shown on the map
		if (mapComponent != null)
			mapComponent.refreshMap();

		// Update cases and contacts shown on the map
		if (UserProvider.getCurrent().hasUserRight(UserRight.DASHBOARD_CONTACT_VIEW_TRANSMISSION_CHAINS)) {
			networkDiagramComponent
			.filter(c -> c.getParent().isVisible())
			.ifPresent(DashboardNetworkComponent::refreshDiagram);
		}

		// Epi curve chart has to be created again due to a canvas resizing issue when
		// simply refreshing the component
		if (epiCurveComponent != null)
			epiCurveComponent.clearAndFillEpiCurveChart();
	}

}
