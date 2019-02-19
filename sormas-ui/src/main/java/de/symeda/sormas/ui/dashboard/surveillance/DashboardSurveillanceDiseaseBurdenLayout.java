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
package de.symeda.sormas.ui.dashboard.surveillance;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;

@SuppressWarnings("serial")
public class DashboardSurveillanceDiseaseBurdenLayout extends CustomLayout {

	protected DiseaseBurdenSurveillanceComponent diseaseBurdenComponent;
	protected DiseaseDifferenceSurveillanceComponent diseaseDifferenceComponent;
	private Button showMoreButton;
	private Button showLessButton;
	private Boolean isShowingAllDiseases;

	private static String BURDEN_LOC = "burden";
	private static String DIFFERENCE_LOC = "difference";
	private static String EXTEND_BUTTONS_LOC = "extendButtons";
	
	public DashboardSurveillanceDiseaseBurdenLayout(DashboardDataProvider dashboardDataProvider) {

		setTemplateContents(
				LayoutUtil.fluidRow(
						LayoutUtil.fluidColumnLoc(6, 0, 12, 0, BURDEN_LOC), 
						LayoutUtil.fluidColumnLoc(6, 0, 12, 0, DIFFERENCE_LOC))
				+ LayoutUtil.loc(EXTEND_BUTTONS_LOC));
		
		diseaseBurdenComponent = new DiseaseBurdenSurveillanceComponent(dashboardDataProvider);
		diseaseDifferenceComponent = new DiseaseDifferenceSurveillanceComponent(dashboardDataProvider);

		addComponent(diseaseBurdenComponent, BURDEN_LOC);
		addComponent(diseaseDifferenceComponent, DIFFERENCE_LOC);

		addShowMoreAndLessButtons();
	}

	private void addShowMoreAndLessButtons() {
		
		VerticalLayout buttonsLayout = new VerticalLayout();
		
		showMoreButton = new Button(I18nProperties.getCaption(Captions.dashboardShowAllDiseases), FontAwesome.CHEVRON_DOWN);
		CssStyles.style(showMoreButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.VSPACE_3);
		showLessButton = new Button(I18nProperties.getCaption(Captions.dashboardShowFirstDiseases), FontAwesome.CHEVRON_UP);
		CssStyles.style(showLessButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.VSPACE_3);

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

		buttonsLayout.addComponent(showMoreButton);
		buttonsLayout.addComponent(showLessButton);
		buttonsLayout.setComponentAlignment(showMoreButton, Alignment.MIDDLE_CENTER);
		buttonsLayout.setComponentAlignment(showLessButton, Alignment.MIDDLE_CENTER);
		
		addComponent(buttonsLayout, EXTEND_BUTTONS_LOC);

		isShowingAllDiseases = false;
		showLessButton.setVisible(false);
	}

	public void refresh() {
		int visibleDiseasesCount = isShowingAllDiseases ? Disease.values().length : 6;

		diseaseBurdenComponent.refresh(visibleDiseasesCount);
		diseaseDifferenceComponent.refresh(visibleDiseasesCount);
	}
}
