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
import java.util.stream.Stream;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
//import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DiseaseBurdenGrid;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseTileViewLayout extends HorizontalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private DashboardDataProvider dashboardDataProvider;

	public DiseaseTileViewLayout(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		// layout
		setWidth(100, Unit.PERCENTAGE);

//		addComponent(grid);
		setMargin(new MarginInfo(true, true, false, true));
		setSpacing(false);
//		setExpandRatio(grid, 1);
	}

	public void refresh(int limitDiseasesCount) {
		List<DiseaseBurdenDto> diseasesBurden = dashboardDataProvider.getDiseasesBurden();
		// data mockup: manipulate the data
//		diseasesBurden = mockDataUp(diseasesBurden);
		
		// sort, limit and filter
		Stream<DiseaseBurdenDto> diseasesBurdenStream = diseasesBurden.stream()
									   .sorted((dto1, dto2) -> (int) (dto2.getCaseCount() - dto1.getCaseCount()));
		if (limitDiseasesCount > 0) {
			diseasesBurdenStream = diseasesBurdenStream.limit(limitDiseasesCount);
		}
		diseasesBurden = diseasesBurdenStream.collect(Collectors.toList()).subList(0, 3);
		
		for (DiseaseBurdenDto disease : diseasesBurden) {
			DiseaseTileComponent tile = new DiseaseTileComponent(disease);
			addComponent(tile);
			this.setExpandRatio(tile, .33f);
		}
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
