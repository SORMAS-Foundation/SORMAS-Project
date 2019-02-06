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
import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.SubNavigationActionMenu;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardSurveillanceDiseaseCarouselLayout extends VerticalLayout {

	private DashboardDataProvider dashboardDataProvider;
	
	private DiseaseStatisticsSubComponent statisticsComponent;
	private EpiCurveSurveillanceComponent epiCurveComponent;
	private DashboardMapComponent mapComponent;
	
	private SubNavigationActionMenu carouselMenu;
	private List<Disease> diseases;

	public DashboardSurveillanceDiseaseCarouselLayout(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
		
		statisticsComponent = new DiseaseStatisticsSubComponent(dashboardDataProvider);
		epiCurveComponent = new EpiCurveSurveillanceComponent(dashboardDataProvider);
		mapComponent = new DashboardMapComponent(dashboardDataProvider);
		
		//convert diseases array to a list. needed for indexOf in auto-slideshow
		Disease[] _diseases = Disease.values();
		diseases = new ArrayList<Disease>(_diseases.length);
		for (Disease disease : _diseases) {			
			diseases.add(disease);
		}

		this.initLayout();
	}

	private void initLayout() {
		addStyleName(DashboardCssStyles.CURVE_AND_MAP_LAYOUT);
		
		addComponent(createCarouselOptions());
		
		addComponent(statisticsComponent);
				
		addComponent(createEpiCurveAndMapLayout());
	}
	
	private HorizontalLayout createCarouselOptions() {
		HorizontalLayout layout = new HorizontalLayout();
		CssStyles.style(layout, CssStyles.VSPACE_TOP_4, CssStyles.HSPACE_LEFT_2);
		
		CheckBox autoSlide = this.setupSlideShow();
		layout.addComponent(autoSlide);
		layout.setComponentAlignment(autoSlide, Alignment.MIDDLE_LEFT);
		
		carouselMenu = new SubNavigationActionMenu();
		
		for (Disease disease : diseases) {
			carouselMenu.addView(disease.getName(), disease.toShortString(), (e) -> {
				this.changeSelectedDisease(disease);
			});
		}
		
		if (diseases.size() > 0) {
			this.setActiveDisease(diseases.get(0));
		}
		
		layout.addComponent(carouselMenu);
		
		return layout;
	}
	
	private CheckBox setupSlideShow() {
		//slideshow option
		CheckBox autoSlide = new CheckBox("slide show");
		autoSlide.addValueChangeListener(e -> {
			this.changeAutoSlideOption(autoSlide.getValue());
		});
		
		//set timer for slideshow
		SormasUI.getCurrent().addPollListener(e -> {
			Disease selectedDisease = dashboardDataProvider.getDisease();
			int nextDiseaseIndex = 0;
			
			if (selectedDisease != null) {
				nextDiseaseIndex = diseases.indexOf(selectedDisease) + 1;
				
				if (nextDiseaseIndex >= diseases.size()) {
					nextDiseaseIndex = 0;
				}
			}
			
			this.setActiveDisease(diseases.get(nextDiseaseIndex));	
		});
		
		return autoSlide;
	}
	
	private void setActiveDisease(Disease selectedDisease) {
		carouselMenu.setActiveView(selectedDisease.getName());
		this.changeSelectedDisease(selectedDisease);
	}
	
	private void changeSelectedDisease(Disease disease) {
		this.dashboardDataProvider.setDisease(disease);
		refresh();
	}
	
	private void changeAutoSlideOption(boolean isActivated) {
		if (isActivated) {			
			SormasUI.getCurrent().setPollInterval(5000);
		}
		else {
			SormasUI.getCurrent().setPollInterval(-1);
		}
	}

	protected HorizontalLayout createEpiCurveAndMapLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);

		layout.addComponent(epiCurveComponent);

		layout.addComponent(mapComponent);

		return layout;
	}

	public void refresh() {
		this.statisticsComponent.refresh();
		this.epiCurveComponent.clearAndFillEpiCurveChart();
		this.mapComponent.refreshMap();
	}
}
