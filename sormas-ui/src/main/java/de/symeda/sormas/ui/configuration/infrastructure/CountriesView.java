/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.Collections;
import java.util.Set;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.country.CountryCriteria;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryIndexDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.configuration.infrastructure.components.SearchField;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.ArchiveMessages;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.RowCount;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class CountriesView extends AbstractConfigurationView {

	private static final long serialVersionUID = -3900506227084862411L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/countries";

	private CountryCriteria criteria;
	private ViewConfiguration viewConfiguration;

	// Filter
	private SearchField searchField;
	private ComboBox subcontinentFilter;
	private ComboBox relevanceStatusFilter;
	private Button resetButton;

	private HorizontalLayout filterLayout;
	private VerticalLayout gridLayout;
	private CountriesGrid grid;
	private RowCount rowCount;
	protected Button createButton;
	protected Button importButton;
	protected Button importDefaultCountriesButton;
	private MenuBar bulkOperationsDropdown;

	public CountriesView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(CountriesView.class).get(ViewConfiguration.class);
		criteria = ViewModelProviders.of(CountriesView.class).get(CountryCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}
		grid = new CountriesGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		rowCount = new RowCount(Strings.labelNumberOfCountries, 0);
		grid.addDataSizeChangeListener(e -> rowCount.update(grid.getDataSize()));
		gridLayout.addComponent(rowCount);
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");

		boolean infrastructureDataEditable = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA);

		if (infrastructureDataEditable && UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_IMPORT)) {
			importButton = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new InfrastructureImportLayout(InfrastructureType.COUNTRY));
				window.setCaption(I18nProperties.getString(Strings.headingImportCountries));
				window.addCloseListener(c -> grid.reload());
			}, ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(importButton);

			importDefaultCountriesButton = ButtonHelper.createIconButton(Captions.actionImportAllCountries, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new ImportDefaultCountriesLayout());
				window.setCaption(I18nProperties.getString(Strings.headingImportAllCountries));
				window.addCloseListener(c -> grid.reload());
			}, ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(importDefaultCountriesButton);
		} else if (!infrastructureDataEditable) {
			Label infrastructureDataLocked = new Label();
			infrastructureDataLocked.setCaption(I18nProperties.getString(Strings.headingInfrastructureLocked));
			infrastructureDataLocked.setValue(I18nProperties.getString(Strings.messageInfrastructureLocked));
			infrastructureDataLocked.setIcon(VaadinIcons.WARNING);
			addHeaderComponent(infrastructureDataLocked);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EXPORT)) {
			Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
			exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
			addHeaderComponent(exportButton);

			StreamResource streamResource = GridExportStreamResource.createStreamResourceWithSelectedItems(
				grid,
				this::getSelectedRows,
				ExportEntityName.COUNTRIES,
				Collections.singletonList(CountriesGrid.ACTION_BTN_ID),
				Collections.singletonList(CountryIndexDto.DEFAULT_NAME));
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
		}

		if (infrastructureDataEditable && UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_CREATE)) {
			createButton = ButtonHelper.createIconButton(
				Captions.actionNewEntry,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getInfrastructureController().createCountry(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				viewConfiguration.setInEagerMode(true);
				bulkOperationsDropdown.setVisible(isBulkOperationsDropdownVisible());
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				searchField.setEnabled(false);
				grid.setInEagerMode(true);
				grid.reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				viewConfiguration.setInEagerMode(false);
				bulkOperationsDropdown.setVisible(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				searchField.setEnabled(true);
				navigateTo(criteria);
			});
		}

		addComponent(gridLayout);
	}

	private Set<CountryIndexDto> getSelectedRows() {
		CountriesGrid countriesGrid = this.grid;
		return this.viewConfiguration.isInEagerMode() ? countriesGrid.asMultiSelect().getSelectedItems() : Collections.emptySet();

	}

	private HorizontalLayout createFilterBar() {
		filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		searchField = new SearchField();
		searchField.addTextChangeListener(e -> {
			criteria.nameCodeLike(e.getText());
			grid.reload();
		});
		filterLayout.addComponent(searchField);

		subcontinentFilter = ComboBoxHelper.createComboBoxV7();
		subcontinentFilter.setId(CountryDto.SUBCONTINENT);
		subcontinentFilter.setWidth(140, Unit.PIXELS);
		subcontinentFilter.setCaption(I18nProperties.getPrefixCaption(CountryDto.I18N_PREFIX, CountryDto.SUBCONTINENT));
		subcontinentFilter.addItems(FacadeProvider.getSubcontinentFacade().getAllActiveAsReference());
		subcontinentFilter.addValueChangeListener(e -> {
			criteria.subcontinent((SubcontinentReferenceDto) e.getProperty().getValue());
			grid.reload();
		});
		filterLayout.addComponent(subcontinentFilter);

		resetButton = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(CountriesView.class).remove(CountryCriteria.class);
			navigateTo(null);
		}, CssStyles.FORCE_CAPTION);
		resetButton.setVisible(false);

		filterLayout.addComponent(resetButton);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_VIEW)) {
				relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
				relevanceStatusFilter.setId("relevanceStatus");
				relevanceStatusFilter.setWidth(220, Unit.PERCENTAGE);
				relevanceStatusFilter.setNullSelectionAllowed(false);
				relevanceStatusFilter.addItems(EntityRelevanceStatus.getAllExceptDeleted());
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.countryActiveCountries));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.countryArchivedCountries));
				relevanceStatusFilter
					.setItemCaption(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED, I18nProperties.getCaption(Captions.countryAllCountries));
				relevanceStatusFilter.addValueChangeListener(e -> {
					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(relevanceStatusFilter);

				// Bulk operation dropdown
				if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
					bulkOperationsDropdown = MenuBarHelper.createDropDown(
						Captions.bulkActions,
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.actionArchiveInfrastructure),
							VaadinIcons.ARCHIVE,
							selectedItem -> {
								ControllerProvider.getInfrastructureController()
									.archiveOrDearchiveAllSelectedItems(
										true,
										ArchiveHandlers.forInfrastructure(FacadeProvider.getCountryFacade(), ArchiveMessages.COUNTRY),
										grid,
										grid::reload,
										() -> navigateTo(criteria));
							},
							UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_ARCHIVE)
								&& EntityRelevanceStatus.ACTIVE.equals(criteria.getRelevanceStatus())),
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.actionDearchiveInfrastructure),
							VaadinIcons.ARCHIVE,
							selectedItem -> {
								ControllerProvider.getInfrastructureController()
									.archiveOrDearchiveAllSelectedItems(
										false,
										ArchiveHandlers.forInfrastructure(FacadeProvider.getCountryFacade(), ArchiveMessages.COUNTRY),
										grid,
										grid::reload,
										() -> navigateTo(criteria));
							},
							UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_ARCHIVE)
								&& EntityRelevanceStatus.ARCHIVED.equals(criteria.getRelevanceStatus())));

					bulkOperationsDropdown.setVisible(isBulkOperationsDropdownVisible());
					actionButtonsLayout.addComponent(bulkOperationsDropdown);
				}
			}
		}
		filterLayout.addComponent(actionButtonsLayout);
		filterLayout.setComponentAlignment(actionButtonsLayout, Alignment.BOTTOM_RIGHT);
		filterLayout.setExpandRatio(actionButtonsLayout, 1);

		return filterLayout;
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

		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		resetButton.setVisible(criteria.hasAnyFilterActive());

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}
		searchField.setValue(criteria.getNameCodeLike());
		subcontinentFilter.setValue(criteria.getSubcontinent());

		applyingCriteria = false;
	}

	private boolean isBulkOperationsDropdownVisible() {
		boolean infrastructureDataEditable = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA);

		return viewConfiguration.isInEagerMode()
			&& (EntityRelevanceStatus.ACTIVE.equals(criteria.getRelevanceStatus())
				|| (infrastructureDataEditable && EntityRelevanceStatus.ARCHIVED.equals(criteria.getRelevanceStatus())));
	}
}
