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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class SurveillanceDiseaseCarouselLayout extends VerticalLayout {

	private DashboardDataProvider dashboardDataProvider;

	private DiseaseStatisticsComponent statisticsComponent;
	private SurveillanceEpiCurveComponent epiCurveComponent;
	private DashboardMapComponent mapComponent;

	private Consumer<Boolean> externalExpandListener;
	private SubMenu carouselMenu;
	private List<Disease> diseases;
	private Registration pollRegistration;

	public SurveillanceDiseaseCarouselLayout(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		statisticsComponent = new DiseaseStatisticsComponent(dashboardDataProvider);
		epiCurveComponent = new SurveillanceEpiCurveComponent(dashboardDataProvider);
		mapComponent = new DashboardMapComponent(dashboardDataProvider);
		diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		//remove 'other' and 'undefined' if present
		diseases.removeAll(Arrays.asList(Disease.OTHER, Disease.UNDEFINED));

		this.initLayout();
	}

	public void setExpandListener(Consumer<Boolean> listener) {
		externalExpandListener = listener;
	}

	private void initLayout() {
		addStyleName(DashboardCssStyles.CURVE_AND_MAP_LAYOUT);
		setWidth(100, Unit.PERCENTAGE);
		setHeightUndefined();
		setMargin(false);
		setSpacing(false);

		HorizontalLayout carouselMenuLayout = createCarouselMenuLayout();
		addComponent(carouselMenuLayout);
		setExpandRatio(carouselMenuLayout, 0);

		addComponent(statisticsComponent);
		statisticsComponent.addStyleName(DashboardCssStyles.HIGHLIGHTED_STATISTICS_COMPONENT);
		setExpandRatio(statisticsComponent, 0);

		HorizontalLayout epiCurveAndMapLayout = createEpiCurveAndMapLayout();
		addComponent(epiCurveAndMapLayout);
		setExpandRatio(epiCurveAndMapLayout, 1);
	}

	private HorizontalLayout createCarouselMenuLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(false);
		CssStyles.style(layout, CssStyles.HSPACE_LEFT_2);

		CheckBox autoSlide = this.setupSlideShow();
		layout.addComponent(autoSlide);
		layout.setComponentAlignment(autoSlide, Alignment.MIDDLE_LEFT);

		carouselMenu = new SubMenu();

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

	private HorizontalLayout createEpiCurveAndMapLayout() {
		HorizontalLayout epiCurveAndMapLayout = new HorizontalLayout();
		epiCurveAndMapLayout.setWidth(100, Unit.PERCENTAGE);
		final int BASE_HEIGHT = 480;
		epiCurveAndMapLayout.setHeight(BASE_HEIGHT, Unit.PIXELS);

		epiCurveAndMapLayout.addComponent(epiCurveComponent);
		epiCurveAndMapLayout.addComponent(mapComponent);

		epiCurveComponent.setExpandListener(expanded -> {
			if (expanded) {
				removeComponent(statisticsComponent);
				epiCurveAndMapLayout.removeComponent(mapComponent);
				epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
				setHeight(100, Unit.PERCENTAGE);
			} else {
				addComponent(statisticsComponent, 1);
				epiCurveAndMapLayout.addComponent(mapComponent, 1);
				epiCurveAndMapLayout.setHeight(BASE_HEIGHT, Unit.PIXELS);
				setHeightUndefined();
			}
			if (externalExpandListener != null) {
				externalExpandListener.accept(expanded);
			}
		});

		mapComponent.setExpandListener(expanded -> {
			if (expanded) {
				removeComponent(statisticsComponent);
				epiCurveAndMapLayout.removeComponent(epiCurveComponent);
				epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
				setHeight(100, Unit.PERCENTAGE);
			} else {
				addComponent(statisticsComponent, 1);
				epiCurveAndMapLayout.addComponent(epiCurveComponent, 0);
				epiCurveAndMapLayout.setHeight(BASE_HEIGHT, Unit.PIXELS);
				setHeightUndefined();
			}
			if (externalExpandListener != null) {
				externalExpandListener.accept(expanded);
			}
		});

		return epiCurveAndMapLayout;
	}

	private CheckBox setupSlideShow() {
		// slideshow option
		CheckBox autoSlide = new CheckBox(I18nProperties.getCaption(Captions.dashboardDiseaseCarouselSlideShow));
		autoSlide.addValueChangeListener(e -> {
			this.changeAutoSlideOption(autoSlide.getValue());
		});

		// enabled by default
		autoSlide.setValue(true);

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
	
	@Override
	public void detach() {
		super.detach();
		
		// deactivate polling
		changeAutoSlideOption(false);
	}

	private void changeAutoSlideOption(boolean isActivated) {
		
		if (isActivated) {
			SormasUI.getCurrent().setPollInterval(1000 * 90);

			// set timer for slideshow
			if (pollRegistration == null) {
				pollRegistration = SormasUI.getCurrent().addPollListener(e -> {
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
			}
		} else {
			SormasUI.getCurrent().setPollInterval(-1);
			
			if (pollRegistration != null) {
				pollRegistration.remove();
				pollRegistration = null;
			}
		}
	}

	public void refresh() {
		this.statisticsComponent.refresh();
		this.epiCurveComponent.clearAndFillEpiCurveChart();
		this.mapComponent.refreshMap();
	}
}
