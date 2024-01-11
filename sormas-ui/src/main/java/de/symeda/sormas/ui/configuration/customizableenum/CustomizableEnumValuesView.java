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

package de.symeda.sormas.ui.configuration.customizableenum;

import java.util.Arrays;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumCriteria;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.configuration.infrastructure.components.SearchField;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CustomizableEnumValuesView extends AbstractConfigurationView {

	private static final long serialVersionUID = 6496373389997511056L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/customizableEnums";

	// Filters
	private SearchField searchField;
	private ComboBox<CustomizableEnumType> dataTypeFilter;
	private ComboBox<Disease> diseaseFilter;
	private ComboBox<Boolean> relevanceStatusFilter;

	private final CustomizableEnumCriteria criteria;
	private final CustomizableEnumValuesGrid grid;

	public CustomizableEnumValuesView() {

		super(VIEW_NAME);

		criteria = ViewModelProviders.of(CustomizableEnumValuesView.class).get(CustomizableEnumCriteria.class, new CustomizableEnumCriteria());
		grid = new CustomizableEnumValuesGrid(criteria);
		VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		setUpRelevanceStatusFilter();
		gridLayout.addComponent(relevanceStatusFilter);
		gridLayout.setComponentAlignment(relevanceStatusFilter, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");

		addHeaderComponent(ButtonHelper.createIconButton(null, VaadinIcons.INFO_CIRCLE, e -> {
			VaadinUiUtil.showSimplePopupWindow(
				I18nProperties.getString(Strings.headingCustomizableEnumConfigurationInfo),
				I18nProperties.getString(Strings.infoCustomizableEnumConfigurationInfo),
				ContentMode.HTML,
				640);
		}));

		addHeaderComponent(
			ButtonHelper.createIconButton(
				Captions.actionNewEntry,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getCustomizableEnumController().createCustomizableEnumValue(),
				ValoTheme.BUTTON_PRIMARY));

		addComponent(gridLayout);
	}

	private HorizontalLayout createFilterBar() {

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);

		searchField = new SearchField();
		searchField.setInputPrompt(I18nProperties.getString(Strings.promptCustomizableEnumSearchField));
		searchField.addTextChangeListener(e -> {
			criteria.freeTextFilter(e.getText());
			grid.reload();
		});
		filterLayout.addComponent(searchField);

		dataTypeFilter = new ComboBox<>(
			I18nProperties.getPrefixCaption(CustomizableEnumValueIndexDto.I18N_PREFIX, CustomizableEnumValueIndexDto.DATA_TYPE),
			Arrays.asList(CustomizableEnumType.values()));
		dataTypeFilter.addValueChangeListener(e -> {
			criteria.dataType(e.getValue());
			grid.reload();
		});
		filterLayout.addComponent(dataTypeFilter);

		diseaseFilter = new ComboBox<>(
			I18nProperties.getPrefixCaption(CustomizableEnumValueIndexDto.I18N_PREFIX, CustomizableEnumValueIndexDto.DISEASES),
			FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		diseaseFilter.addValueChangeListener(e -> {
			criteria.disease(e.getValue());
			grid.reload();
		});
		filterLayout.addComponent(diseaseFilter);

		filterLayout.addComponent(ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(CustomizableEnumValuesView.class).remove(CustomizableEnumCriteria.class);
			navigateTo(null);
		}, CssStyles.FORCE_CAPTION));

		return filterLayout;
	}

	private void setUpRelevanceStatusFilter() {

		relevanceStatusFilter = new ComboBox<>();
		relevanceStatusFilter.setId("relevanceStatus");
		relevanceStatusFilter.setWidth(210, Unit.PIXELS);
		relevanceStatusFilter.setEmptySelectionAllowed(false);
		relevanceStatusFilter.setItems(Boolean.TRUE, Boolean.FALSE);
		relevanceStatusFilter.setItemCaptionGenerator(
			item -> I18nProperties.getCaption(item ? Captions.customizableEnumValueActiveValues : Captions.customizableEnumValueInactiveValues));
		relevanceStatusFilter.addValueChangeListener(e -> {
			criteria.active(e.getValue());
			navigateTo(criteria);
		});
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
		grid.reload();
	}

	public void updateFilterComponents() {

		applyingCriteria = true;

		searchField.setValue(criteria.getFreeTextFilter());
		dataTypeFilter.setValue(criteria.getDataType());
		diseaseFilter.setValue(criteria.getDisease());

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getActive());
		}

		applyingCriteria = false;
	}
}
