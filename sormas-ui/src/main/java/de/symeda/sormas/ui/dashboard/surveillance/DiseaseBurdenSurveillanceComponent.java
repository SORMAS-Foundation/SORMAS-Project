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
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
//import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DiseaseBurdenGrid;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseBurdenSurveillanceComponent extends VerticalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private DashboardDataProvider dashboardDataProvider;
	private DiseaseBurdenGrid grid;

	public DiseaseBurdenSurveillanceComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		Label title = new Label(I18nProperties.getCaption(Captions.dashboardDiseaseBurdenInfo));
		CssStyles.style(title, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

		grid = new DiseaseBurdenGrid();
		grid.setHeightMode(HeightMode.ROW);
		grid.setWidth(100, Unit.PERCENTAGE);

		// layout
		setWidth(100, Unit.PERCENTAGE);

		addComponent(title);
		addComponent(grid);
		setMargin(true);
		setSpacing(false);
		setExpandRatio(grid, 1);
	}

	public void refresh(int visibleDiseasesCount) {
		List<DiseaseBurdenDto> diseasesBurden = dashboardDataProvider.getDiseasesBurden();

		// data mockup: manipulate the data
//		diseasesBurden = mockDataUp(diseasesBurden);
		
		// sort, limit and filter
		diseasesBurden = diseasesBurden.stream()
									   .sorted((dto1, dto2) -> (int) (dto2.getCaseCount() - dto1.getCaseCount()))
									   .limit(visibleDiseasesCount)
									   //.filter((dto) -> dto.hasCount())
									   .collect(Collectors.toList());

		grid.reload(diseasesBurden);
		grid.setHeightByRows(visibleDiseasesCount);
	}
	
	@SuppressWarnings("unused")
	private List<DiseaseBurdenDto> mockDataUp(List<DiseaseBurdenDto> data) {
		List<DiseaseBurdenDto> newData = new ArrayList<DiseaseBurdenDto>();

		Long diff = 6L;
		for (DiseaseBurdenDto diseaseBurden : data) {
			Long caseCount = 0L;
			Long previousCaseCount = 0L;

			if (diff >= 0)
				caseCount = diff;
			else
				previousCaseCount = Math.abs(diff);

			newData.add(new DiseaseBurdenDto(diseaseBurden.getDisease(), caseCount, previousCaseCount,
					diseaseBurden.getEventCount(), diseaseBurden.getOutbreakDistrictCount(),
					diseaseBurden.getCaseDeathCount()));

			diff -= 2;
		}

		return newData;
	}
}
