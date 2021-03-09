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
package de.symeda.sormas.ui.dashboard.visualisation;

import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class DashboardNetworkComponent extends VerticalLayout {

	public static final int MAX_CONTACTS_SUPPORTED = 5000;
	// Layouts and components
	private final DashboardDataProvider dashboardDataProvider;
	private HorizontalLayout mapHeaderLayout;
	private Button expandMapButton;
	private Button collapseMapButton;
	private final NetworkDiagram networkDiagram;

	private Consumer<Boolean> externalExpandListener;

	private String getNetworkDiagramJson() {
		Set<Disease> diseases =
			Optional.of(dashboardDataProvider.getDisease()).map(Collections::singleton).orElseGet(() -> EnumSet.allOf(Disease.class));

		Date fromDate = dashboardDataProvider.getFromDate();
		Date toDate = dashboardDataProvider.getToDate();
		RegionReferenceDto region = dashboardDataProvider.getRegion();
		DistrictReferenceDto district = dashboardDataProvider.getDistrict();

		return FacadeProvider.getVisualizationFacade()
			.buildTransmissionChainJson(fromDate, toDate, region, district, diseases, ((SormasUI) getUI()).getUserProvider().getUser().getLanguage());
	}

	public DashboardNetworkComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		setMargin(false);
		setSpacing(false);
		setSizeFull();

		networkDiagram = new NetworkDiagram();
		networkDiagram.setSizeFull();

		this.setMargin(true);

		// Add components
		addComponent(createHeader());
		addComponent(networkDiagram);
//		addComponent(createFooter());
		setExpandRatio(networkDiagram, 1);
		networkDiagram.setVisible(false);
	}

	boolean dirty = true;

	public void refreshDiagram() {
		dirty = true;
		updateDiagram();

	}

	private void updateDiagram() {
		if (dirty && networkDiagram.isVisible()) {

			Long contactCount = FacadeProvider.getVisualizationFacade()
				.getContactCount(
					dashboardDataProvider.getFromDate(),
					dashboardDataProvider.getToDate(),
					dashboardDataProvider.getRegion(),
					dashboardDataProvider.getDistrict(),
					Optional.of(dashboardDataProvider.getDisease()).map(Collections::singleton).orElseGet(() -> EnumSet.allOf(Disease.class)));

			if (contactCount <= MAX_CONTACTS_SUPPORTED) {
				updateNetworkDiagram();
			} else {
				VaadinUiUtil.showConfirmationPopup(
					I18nProperties.getString(Strings.headingNetworkDiagramTooManyContacts),
					new Label(
						String.format(
							"%s<br/><br/>%s",
							String.format(I18nProperties.getString(Strings.warningNetworkDiagramTooManyContacts), contactCount),
							I18nProperties.getString(Strings.confirmNetworkDiagramTooManyContacts)),
						ContentMode.HTML),
					I18nProperties.getString(Strings.yes),
					I18nProperties.getString(Strings.no),
					640,
					confirmed -> {
						if (confirmed) {
							updateNetworkDiagram();
						}
					});
			}
		}
	}

	private void updateNetworkDiagram() {
		networkDiagram.updateDiagram(getNetworkDiagramJson());
		dirty = false;
	}

	public void setExpandListener(Consumer<Boolean> listener) {
		externalExpandListener = listener;
	}

	private HorizontalLayout createHeader() {
		mapHeaderLayout = new HorizontalLayout();
		mapHeaderLayout.setWidth(100, Unit.PERCENTAGE);
		mapHeaderLayout.setSpacing(true);
		CssStyles.style(mapHeaderLayout, CssStyles.VSPACE_4);

//		{
//			Label diagramLabel = new Label();
//			diagramLabel.setValue(I18nProperties.getString(Strings.headingContactMap));
//			diagramLabel.setSizeUndefined();
//			CssStyles.style(diagramLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
//	
//			mapHeaderLayout.addComponent(diagramLabel);
//			mapHeaderLayout.setComponentAlignment(diagramLabel, Alignment.BOTTOM_LEFT);
//			mapHeaderLayout.setExpandRatio(diagramLabel, 1);
//		}
		expandMapButton = ButtonHelper.createIconButtonWithCaption(
			Strings.infoDisplayNetworkDiagram,
			I18nProperties.getString(Strings.infoDisplayNetworkDiagram),
			VaadinIcons.EXPAND,
			e -> expandMap(true),
			CssStyles.BUTTON_SUBTLE,
			CssStyles.VSPACE_NONE);
		collapseMapButton = ButtonHelper
			.createIconButtonWithCaption("", "", VaadinIcons.COMPRESS, e -> expandMap(false), CssStyles.BUTTON_SUBTLE, CssStyles.VSPACE_NONE);

		mapHeaderLayout.addComponent(expandMapButton);
		mapHeaderLayout.setComponentAlignment(expandMapButton, Alignment.MIDDLE_RIGHT);

		return mapHeaderLayout;
	}

	private void expandMap(boolean expand) {
		externalExpandListener.accept(expand);
		if (expand) {
			mapHeaderLayout.removeComponent(expandMapButton);
			mapHeaderLayout.addComponent(collapseMapButton);
			mapHeaderLayout.setComponentAlignment(collapseMapButton, Alignment.MIDDLE_RIGHT);
			networkDiagram.setVisible(true);
			updateDiagram();
		} else {
			mapHeaderLayout.removeComponent(collapseMapButton);
			mapHeaderLayout.addComponent(expandMapButton);
			mapHeaderLayout.setComponentAlignment(expandMapButton, Alignment.MIDDLE_RIGHT);
			networkDiagram.setVisible(false);
		}
	}

//	private HorizontalLayout createFooter() {
//		HorizontalLayout mapFooterLayout = new HorizontalLayout();
//		mapFooterLayout.setWidth(100, Unit.PERCENTAGE);
//		mapFooterLayout.setSpacing(true);
//		CssStyles.style(mapFooterLayout, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_3);
//
//		return mapFooterLayout;
//	}
}
