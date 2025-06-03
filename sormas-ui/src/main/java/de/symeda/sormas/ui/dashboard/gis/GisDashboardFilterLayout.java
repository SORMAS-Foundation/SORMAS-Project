/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.dashboard.gis;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.components.DashboardFilterLayout;
import de.symeda.sormas.ui.utils.ComboBoxHelper;

public class GisDashboardFilterLayout extends DashboardFilterLayout<GisDashboardDataProvider> {

	public static final String DISEASE_FILTER = "diseaseFilter";
	private final static String[] FILTERS = new String[] {
		REGION_FILTER,
		DISTRICT_FILTER,
		DISEASE_FILTER };

	public GisDashboardFilterLayout(GisDashboardView dashboardView, GisDashboardDataProvider dashboardDataProvider) {
		super(dashboardView, dashboardDataProvider, FILTERS);
	}

	@Override
	public void populateLayout() {
		super.populateLayout();

		createRegionFilter(I18nProperties.getDescription(Descriptions.aefiDashboardRegionFilter));
		createDistrictFilter(I18nProperties.getDescription(Descriptions.aefiDashboardDistrictFilter));
		createDiseaseFilter();
	}

	private void createDiseaseFilter() {
		ComboBox diseaseFilter = ComboBoxHelper.createComboBoxV7();
		diseaseFilter.setWidth(200, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getString(Strings.promptDisease));
		diseaseFilter.setDescription(I18nProperties.getDescription(Descriptions.aefiDashboardDiseaseFilter));
		List<?> availableDisease = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);

		diseaseFilter.addItems(
			Stream.concat(availableDisease.stream(), Stream.of(GisDashboardFilterLayout.GisDashboardCustomDiseaseFilter.values()))
				.collect(Collectors.toList()));
		diseaseFilter.setValue(dashboardDataProvider.getDisease());

		diseaseFilter.addValueChangeListener(e -> {
			Object filterValue = diseaseFilter.getValue();
			if (filterValue instanceof Disease) {
				dashboardDataProvider.setDisease((Disease) filterValue);
			} else if (filterValue == GisDashboardFilterLayout.GisDashboardCustomDiseaseFilter.NO_DISEASE) {
				dashboardDataProvider.setDisease(null);
			} else if (filterValue == null) {
				dashboardDataProvider.setDisease(null);
			} else {
				throw new RuntimeException("Disease filter [" + filterValue + "] not handled!");
			}
		});

		addCustomComponent(diseaseFilter, DISEASE_FILTER);
	}

	enum GisDashboardCustomDiseaseFilter {

		NO_DISEASE;

		@Override
		public String toString() {
			return I18nProperties.getEnumCaptionShort(this);
		}
	}
}
