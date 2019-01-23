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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.task.DashboardTaskDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DiseaseBurdenGrid;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseBurdenSurveillanceComponent extends VerticalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private DashboardDataProvider dashboardDataProvider;
	private DiseaseBurdenGrid grid;
	
	public DiseaseBurdenSurveillanceComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
		
		Label title = new Label("Disease Burden Information");
		CssStyles.style(title, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

		grid = new DiseaseBurdenGrid();
		grid.setHeightMode(HeightMode.UNDEFINED);
		
		//layout
		setWidth(100, Unit.PERCENTAGE);
		setHeight(400, Unit.PIXELS);
		
		addComponent(title);
		addComponent(grid);
		setMargin(true);
		setSpacing(false);
		setSizeFull();
		setExpandRatio(grid, 1);
	}

	public void refresh() {
		List<DashboardCaseDto> cases = dashboardDataProvider.getCases();
		List<DashboardCaseDto> previousCases = dashboardDataProvider.getPreviousCases();
		List<DashboardEventDto> events = dashboardDataProvider.getEvents();
		
		List<DiseaseBurdenDto> diseasesBurden = new ArrayList<>();
		
		//build diseases burden
		for (Disease disease : Disease.values()) {
			DiseaseBurdenDto diseaseBurden = new DiseaseBurdenDto(disease);
			
			List<DashboardCaseDto> _cases = cases.stream().filter(c -> c.getDisease() == diseaseBurden.getDisease()).collect(Collectors.toList());
			diseaseBurden.setCaseCount(Long.valueOf(_cases.size()));
			diseaseBurden.setOutbreakDistrictCount(_cases.stream().map((c) -> c.getDistrict().getUuid()).distinct().count());
			diseaseBurden.setCaseDeathCount(_cases.stream().filter(c -> c.getCauseOfDeathDisease() != null).count());
			
			_cases = previousCases.stream().filter(c -> c.getDisease() == diseaseBurden.getDisease()).collect(Collectors.toList());
			diseaseBurden.setPreviousCaseCount(Long.valueOf(_cases.size()));
			
			List<DashboardEventDto> _events = events.stream().filter(e -> e.getDisease() == diseaseBurden.getDisease()).collect(Collectors.toList());
			diseaseBurden.setEventCount(Long.valueOf(_events.size()));
			
			diseasesBurden.add(diseaseBurden);
		}
		
		grid.reload(diseasesBurden);
	}
}
