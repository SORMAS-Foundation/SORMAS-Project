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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.surveillance.components.disease.DiseaseOverviewComponent;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SurveillanceOverviewLayout extends CustomLayout {

	private static final String BURDEN_LOC = "burden";
	private static final String DIFFERENCE_LOC = "difference";
	private static final String EXTEND_BUTTONS_LOC = "extendButtons";

	private final DashboardDataProvider dashboardDataProvider;

	private DiseaseOverviewComponent diseaseOverviewComponent;
	private CaseCountDifferenceComponent diseaseDifferenceComponent;
	private Button showMoreButton;
	private Button showLessButton;
	private CheckBox hideOverview;
	private Boolean isShowingAllDiseases;

	public SurveillanceOverviewLayout(DashboardDataProvider dashboardDataProvider) {

		setTemplateContents(
			LayoutUtil.fluidRow(LayoutUtil.fluidColumnLoc(6, 0, 12, 0, BURDEN_LOC), LayoutUtil.fluidColumnLoc(6, 0, 12, 0, DIFFERENCE_LOC))
				+ LayoutUtil.loc(EXTEND_BUTTONS_LOC));

		this.dashboardDataProvider = dashboardDataProvider;

		diseaseDifferenceComponent = new CaseCountDifferenceComponent(dashboardDataProvider);

		addDiseaseBurdenView();

		addComponent(diseaseDifferenceComponent, DIFFERENCE_LOC);

		addShowMoreAndLessButtons();
	}

	private void addDiseaseBurdenView() {
		diseaseOverviewComponent = new DiseaseOverviewComponent(dashboardDataProvider);

		addComponent(diseaseOverviewComponent, BURDEN_LOC);

		if (UserProvider.getCurrent().hasRegionJurisdictionLevel())
			diseaseOverviewComponent.getShowTableViewButton().click();
	}

	private void addShowMoreAndLessButtons() {

		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setHeightUndefined();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setMargin(new MarginInfo(false, true));

		showMoreButton = ButtonHelper.createIconButton(
			Captions.dashboardShowAllDiseases,
			VaadinIcons.CHEVRON_DOWN,
			null,
			ValoTheme.BUTTON_BORDERLESS,
			CssStyles.VSPACE_TOP_NONE,
			CssStyles.VSPACE_4);
		showLessButton = ButtonHelper.createIconButton(
			Captions.dashboardShowFirstDiseases,
			VaadinIcons.CHEVRON_UP,
			null,
			ValoTheme.BUTTON_BORDERLESS,
			CssStyles.VSPACE_TOP_NONE,
			CssStyles.VSPACE_4);
		hideOverview = new CheckBox(I18nProperties.getCaption(Captions.dashboardHideOverview));
		hideOverview.setId("hideOverview");
		CssStyles.style(hideOverview, CssStyles.VSPACE_3);

		showMoreButton.addClickListener(e -> {
			isShowingAllDiseases = true;
			refresh();

			showMoreButton.setVisible(false);
			showLessButton.setVisible(true);
		});

		showLessButton.addClickListener(e -> {
			isShowingAllDiseases = false;
			refresh();

			showLessButton.setVisible(false);
			showMoreButton.setVisible(true);
		});

		hideOverview.addValueChangeListener(e -> {
			if (hideOverview.getValue()) {
				diseaseOverviewComponent.setVisible(false);
				diseaseDifferenceComponent.setVisible(false);
				showLessButton.setVisible(false);
				showMoreButton.setVisible(false);
			} else {
				diseaseOverviewComponent.setVisible(true);
				diseaseDifferenceComponent.setVisible(true);
				showLessButton.setVisible(isShowingAllDiseases);
				showMoreButton.setVisible(!isShowingAllDiseases);
			}
		});

		buttonsLayout.addComponent(showMoreButton);
		buttonsLayout.addComponent(showLessButton);
		buttonsLayout.setComponentAlignment(showMoreButton, Alignment.BOTTOM_CENTER);
		buttonsLayout.setExpandRatio(showMoreButton, 1);
		buttonsLayout.setComponentAlignment(showLessButton, Alignment.BOTTOM_CENTER);
		buttonsLayout.setExpandRatio(showLessButton, 1);
		buttonsLayout.addComponent(hideOverview);
		buttonsLayout.setComponentAlignment(hideOverview, Alignment.BOTTOM_RIGHT);
		buttonsLayout.setExpandRatio(hideOverview, 0);

		addComponent(buttonsLayout, EXTEND_BUTTONS_LOC);

		isShowingAllDiseases = false;
		showLessButton.setVisible(false);
		buttonsLayout.setExpandRatio(showLessButton, 1);
	}

	public void refresh() {
		diseaseOverviewComponent.refresh(dashboardDataProvider.getDiseasesBurden(), isShowingAllDiseases);
		diseaseDifferenceComponent.refresh(isShowingAllDiseases ? 0 : 10);
	}

	public void updateDifferenceComponentSubHeader() {
		diseaseDifferenceComponent.updateSubHeader();
	}
}
