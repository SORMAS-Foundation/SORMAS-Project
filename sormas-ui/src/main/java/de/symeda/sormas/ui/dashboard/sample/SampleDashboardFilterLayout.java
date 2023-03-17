/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.dashboard.sample;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleDashboardFilterDateType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.ui.dashboard.components.DashboardFilterLayout;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.components.datetypeselector.DateTypeSelectorComponent;

public class SampleDashboardFilterLayout extends DashboardFilterLayout<SampleDashboardDataProvider> {

	public static final String DATE_TYPE_FILTER = "dateTypeFilter";
	public static final String SAMPLE_MATERIAL_FILTER = "sampleMaterialFilter";
	public static final String DISEASE_FILTER = "diseaseFilter";

	private final static String[] FILTERS = new String[] {
		DATE_TYPE_FILTER,
		REGION_FILTER,
		DISTRICT_FILTER,
		SAMPLE_MATERIAL_FILTER,
		DISEASE_FILTER };

	public SampleDashboardFilterLayout(SampleDashboardView dashboardView, SampleDashboardDataProvider dashboardDataProvider) {
		super(dashboardView, dashboardDataProvider, FILTERS);
	}

	@Override
	public void populateLayout() {
		super.populateLayout();

		createDateTypeSelector();
		createRegionFilter(I18nProperties.getDescription(Descriptions.sampleDashboardRegionFilter));
		createDistrictFilter(I18nProperties.getDescription(Descriptions.sampleDashboardDistrictFilter));
		createSampleMaterialFilter();
		createDiseaseFilter();
	}

	private void createDateTypeSelector() {
		@SuppressWarnings("unchecked")
		DateTypeSelectorComponent dateTypeSelectorComponent = new DateTypeSelectorComponent.Builder<>(SampleDashboardFilterDateType.class)
			.dateTypePrompt(I18nProperties.getString(Strings.promptSampleDashboardFilterDateType))
			.defaultDateType(dashboardDataProvider.getDateType())
			.build();

		dateTypeSelectorComponent.addValueChangeListener(e -> {
			dashboardDataProvider.setDateType((SampleDashboardFilterDateType) e.getProperty().getValue());
		});

		addCustomComponent(dateTypeSelectorComponent, DATE_TYPE_FILTER);
	}

	private void createDiseaseFilter() {
		ComboBox diseaseFilter = ComboBoxHelper.createComboBoxV7();
		diseaseFilter.setWidth(200, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getString(Strings.promptDisease));
		diseaseFilter.setDescription(I18nProperties.getDescription(Descriptions.sampleDashboardDiseaseFilter));
		List<?> availableDisease = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseasesWithFollowUp(true, true, true);

		diseaseFilter
			.addItems(Stream.concat(availableDisease.stream(), Stream.of(SampleDashboardCustomDiseaseFilter.values())).collect(Collectors.toList()));
		diseaseFilter.setValue(dashboardDataProvider.getDisease());

		diseaseFilter.addValueChangeListener(e -> {
			Object filterValue = diseaseFilter.getValue();
			if (filterValue instanceof Disease) {
				dashboardDataProvider.setDisease((Disease) filterValue);
				dashboardDataProvider.setWithNoDisease(null);
			} else if (filterValue == SampleDashboardCustomDiseaseFilter.NO_DISEASE) {
				dashboardDataProvider.setDisease(null);
				dashboardDataProvider.setWithNoDisease(true);
			} else if (filterValue == null) {
				dashboardDataProvider.setDisease(null);
				dashboardDataProvider.setWithNoDisease(null);
			} else {
				throw new RuntimeException("Disease filter [" + filterValue + "] not handled!");
			}
		});

		addCustomComponent(diseaseFilter, DISEASE_FILTER);
	}

	private void createSampleMaterialFilter() {
		ComboBox sampleMaterialFilter = ComboBoxHelper.createComboBoxV7();
		sampleMaterialFilter.setWidth(200, Unit.PIXELS);
		sampleMaterialFilter.setInputPrompt(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_MATERIAL));
		sampleMaterialFilter
			.addItems(Stream.of(SampleMaterial.values()).sorted(Comparator.comparing(SampleMaterial::toString)).collect(Collectors.toList()));
		sampleMaterialFilter.setValue(dashboardDataProvider.getSampleMaterial());

		sampleMaterialFilter.addValueChangeListener(e -> {
			dashboardDataProvider.setSampleMaterial((SampleMaterial) sampleMaterialFilter.getValue());
		});

		addCustomComponent(sampleMaterialFilter, SAMPLE_MATERIAL_FILTER);
	}

	enum SampleDashboardCustomDiseaseFilter {

		NO_DISEASE;

		@Override
		public String toString() {
			return I18nProperties.getEnumCaptionShort(this);
		}
	}
}
