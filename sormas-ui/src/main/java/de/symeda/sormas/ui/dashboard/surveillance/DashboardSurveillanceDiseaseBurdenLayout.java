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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.DashboardSampleDto;
import de.symeda.sormas.api.sample.DashboardTestResultDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.task.DashboardTaskDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.statistics.AbstractDashboardStatisticsComponent;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsDiseaseElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsGrowthElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsPercentageElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.dashboard.statistics.SvgCircleElement;
import de.symeda.sormas.ui.dashboard.statistics.SvgCircleElement.SvgCircleElementPart;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardSurveillanceDiseaseBurdenLayout extends VerticalLayout {

	// private DashboardDataProvider dashboardDataProvider;

	protected DiseaseBurdenSurveillanceComponent diseaseBurdenComponent;
	protected DiseaseDifferenceSurveillanceComponent diseaseDifferenceComponent;
	private Button showMoreButton;
	private Button showLessButton;
	private Boolean isShowingAllDiseases;

	public DashboardSurveillanceDiseaseBurdenLayout(DashboardDataProvider dashboardDataProvider) {
		// this.dashboardDataProvider = dashboardDataProvider;

		diseaseBurdenComponent = new DiseaseBurdenSurveillanceComponent(dashboardDataProvider);
		diseaseDifferenceComponent = new DiseaseDifferenceSurveillanceComponent(dashboardDataProvider);

		this.initLayout();
	}

	private void initLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);

		layout.addComponent(diseaseBurdenComponent);
		layout.addComponent(diseaseDifferenceComponent);

		addComponent(layout);
		addShowMoreAndLessButtons();
	}

	private void addShowMoreAndLessButtons() {
		showMoreButton = new Button("Show All Diseases", FontAwesome.CHEVRON_DOWN);
		CssStyles.style(showMoreButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.VSPACE_3);
		showLessButton = new Button("Show First 6 Diseases", FontAwesome.CHEVRON_UP);
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

		addComponent(showMoreButton);
		addComponent(showLessButton);
		setComponentAlignment(showMoreButton, Alignment.MIDDLE_CENTER);
		setComponentAlignment(showLessButton, Alignment.MIDDLE_CENTER);
		
		isShowingAllDiseases = false;
		showLessButton.setVisible(false);
	}

	public void refresh() {
		int visibleDiseasesCount = isShowingAllDiseases ? Disease.values().length : 6;

		diseaseBurdenComponent.refresh(visibleDiseasesCount);
		diseaseDifferenceComponent.refresh(visibleDiseasesCount);
		
		diseaseBurdenComponent.setHeight(visibleDiseasesCount * 55, Unit.PIXELS);		
		diseaseDifferenceComponent.setHeight(visibleDiseasesCount * 55 + 50, Unit.PIXELS);			
	}
}
