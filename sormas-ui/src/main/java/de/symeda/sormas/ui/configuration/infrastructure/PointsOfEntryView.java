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
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.configuration.infrastructure.components.SearchField;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.ArchiveMessages;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.RowCount;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class PointsOfEntryView extends AbstractConfigurationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/pointsofentry";
	private static final long serialVersionUID = 169878675780777319L;
	protected PointsOfEntryGrid grid;
	protected Button createButton;
	protected Button exportButton;
	private PointOfEntryCriteria criteria;
	private ViewConfiguration viewConfiguration;
	// Filters
	private SearchField searchField;
	private ComboBox countryFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox typeFilter;
	private ComboBox activeFilter;
	private ComboBox relevanceStatusFilter;
	private Button resetButton;
	private HorizontalLayout filterLayout;
	private VerticalLayout gridLayout;
	private MenuBar bulkOperationsDropdown;
	private RowCount rowCount;

	public PointsOfEntryView() {

		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(PointsOfEntryView.class).get(ViewConfiguration.class);
		criteria = ViewModelProviders.of(PointsOfEntryView.class)
			.get(PointOfEntryCriteria.class, new PointOfEntryCriteria().country(FacadeProvider.getCountryFacade().getServerCountry()));
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		grid = new PointsOfEntryGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		rowCount = new RowCount(Strings.labelNumberOfPointofEntry, grid.getDataSize());
		grid.addDataSizeChangeListener(e -> rowCount.update(grid.getDataSize()));
		gridLayout.addComponent(rowCount);
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");

		boolean infrastructureDataEditable = UiUtil.enabled(FeatureType.EDIT_INFRASTRUCTURE_DATA);

		if (UiUtil.permitted(infrastructureDataEditable, UserRight.INFRASTRUCTURE_IMPORT)) {
			Button importButton = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new InfrastructureImportLayout(InfrastructureType.POINT_OF_ENTRY));
				window.setCaption(I18nProperties.getString(Strings.headingImportPointsOfEntry));
				window.addCloseListener(c -> {
					grid.reload();
				});
			}, ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(importButton);
		} else if (!infrastructureDataEditable) {
			Label infrastructureDataLocked = new Label();
			infrastructureDataLocked.setCaption(I18nProperties.getString(Strings.headingInfrastructureLocked));
			infrastructureDataLocked.setValue(I18nProperties.getString(Strings.messageInfrastructureLocked));
			infrastructureDataLocked.setIcon(VaadinIcons.WARNING);
			addHeaderComponent(infrastructureDataLocked);
		}

		if (UiUtil.permitted(UserRight.INFRASTRUCTURE_EXPORT)) {
			Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
			exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
			addHeaderComponent(exportButton);

			StreamResource streamResource = GridExportStreamResource.createStreamResourceWithSelectedItems(
				grid,
				this::getSelectedRows,
				ExportEntityName.POINTS_OF_ENTRY,
				PointsOfEntryGrid.ACTION_BTN_ID);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
		}

		if (UiUtil.permitted(infrastructureDataEditable, UserRight.INFRASTRUCTURE_CREATE)) {
			createButton = ButtonHelper.createIconButton(
				Captions.actionNewEntry,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getInfrastructureController().createPointOfEntry(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}

		if (UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode = ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
			btnLeaveBulkEditMode.setStyleName(ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				viewConfiguration.setInEagerMode(true);
				bulkOperationsDropdown.setVisible(isBulkOperationsDropdownVisible());
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				searchField.setEnabled(false);
				grid.setEagerDataProvider();
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

	private Set<PointOfEntryDto> getSelectedRows() {
		PointsOfEntryGrid facilitiesGrid = this.grid;
		return this.viewConfiguration.isInEagerMode() ? facilitiesGrid.asMultiSelect().getSelectedItems() : Collections.emptySet();
	}

	private HorizontalLayout createFilterBar() {

		filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		searchField = new SearchField();
		searchField.addTextChangeListener(e -> {
			criteria.nameLike(e.getText());
			grid.reload();
		});
		CssStyles.style(searchField, CssStyles.FORCE_CAPTION);
		filterLayout.addComponent(searchField);

		countryFilter = addCountryFilter(filterLayout, country -> {
			criteria.country(country);
			grid.reload();
		}, regionFilter);
		countryFilter.addValueChangeListener(country -> {
			CountryReferenceDto countryReferenceDto = (CountryReferenceDto) country.getProperty().getValue();
			criteria.country(countryReferenceDto);
			navigateTo(criteria);
			FieldHelper.updateItems(
				regionFilter,
				countryReferenceDto != null ? FacadeProvider.getRegionFacade().getAllActiveByCountry(countryReferenceDto.getUuid()) : null);

		});

		regionFilter = ComboBoxHelper.createComboBoxV7();
		regionFilter.setId(PointOfEntryDto.REGION);
		regionFilter.setWidth(140, Unit.PIXELS);
		regionFilter.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.REGION));
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		regionFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
			criteria.region(region);
			navigateTo(criteria);
			FieldHelper
				.updateItems(districtFilter, region != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()) : null);
		});
		filterLayout.addComponent(regionFilter);

		districtFilter = ComboBoxHelper.createComboBoxV7();
		districtFilter.setId(PointOfEntryDto.DISTRICT);
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.DISTRICT));
		districtFilter.addValueChangeListener(e -> {
			criteria.district((DistrictReferenceDto) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(districtFilter);

		typeFilter = ComboBoxHelper.createComboBoxV7();
		typeFilter.setId(PointOfEntryDto.POINT_OF_ENTRY_TYPE);
		typeFilter.setWidth(140, Unit.PIXELS);
		typeFilter.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.POINT_OF_ENTRY_TYPE));
		typeFilter.addItems(PointOfEntryType.values());
		typeFilter.addValueChangeListener(e -> {
			criteria.type((PointOfEntryType) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(typeFilter);

		activeFilter = ComboBoxHelper.createComboBoxV7();
		activeFilter.setId(PointOfEntryDto.ACTIVE);
		activeFilter.setWidth(140, Unit.PIXELS);
		activeFilter.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.ACTIVE));
		activeFilter.addItem(Boolean.TRUE);
		activeFilter.addItem(Boolean.FALSE);
		activeFilter.setItemCaption(Boolean.TRUE, DataHelper.stringifyBoolean(Boolean.TRUE));
		activeFilter.setItemCaption(Boolean.FALSE, DataHelper.stringifyBoolean(Boolean.FALSE));
		activeFilter.addValueChangeListener(e -> {
			criteria.active((Boolean) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(activeFilter);

		resetButton = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(PointsOfEntryView.class).remove(PointOfEntryCriteria.class);
			navigateTo(null);
		}, CssStyles.FORCE_CAPTION);
		resetButton.setVisible(false);

		filterLayout.addComponent(resetButton);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (UiUtil.permitted(UserRight.INFRASTRUCTURE_VIEW)) {
				relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
				relevanceStatusFilter.setId("relevanceStatus");
				relevanceStatusFilter.setWidth(220, Unit.PERCENTAGE);
				relevanceStatusFilter.setNullSelectionAllowed(false);
				relevanceStatusFilter.addItems(EntityRelevanceStatus.getAllExceptDeleted());
				relevanceStatusFilter
					.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.pointOfEntryActivePointsOfEntry));

				if (UiUtil.permitted(UserRight.INFRASTRUCTURE_VIEW_ARCHIVED)) {
					relevanceStatusFilter
						.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.pointOfEntryArchivedPointsOfEntry));
					relevanceStatusFilter
						.setItemCaption(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED, I18nProperties.getCaption(Captions.pointOfEntryAllPointsOfEntry));
				} else {
					relevanceStatusFilter.removeItem(EntityRelevanceStatus.ARCHIVED);
					relevanceStatusFilter.removeItem(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED);
				}

				relevanceStatusFilter.addValueChangeListener(e -> {
					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(relevanceStatusFilter);

				// Bulk operation dropdown
				if (UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
					bulkOperationsDropdown = MenuBarHelper.createDropDown(
						Captions.bulkActions,
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.actionArchiveInfrastructure),
							VaadinIcons.ARCHIVE,
							selectedItem -> ControllerProvider.getInfrastructureController()
								.archiveOrDearchiveAllSelectedItems(
									true,
									ArchiveHandlers.forInfrastructure(FacadeProvider.getPointOfEntryFacade(), ArchiveMessages.POINT_OF_ENTRY),
									grid,
									grid::reload,
									() -> navigateTo(criteria)),
							UiUtil.permitted(UserRight.INFRASTRUCTURE_ARCHIVE) && EntityRelevanceStatus.ACTIVE.equals(criteria.getRelevanceStatus())),
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.actionDearchiveInfrastructure),
							VaadinIcons.ARCHIVE,
							selectedItem -> ControllerProvider.getInfrastructureController()
								.archiveOrDearchiveAllSelectedItems(
									false,
									ArchiveHandlers.forInfrastructure(FacadeProvider.getPointOfEntryFacade(), ArchiveMessages.POINT_OF_ENTRY),
									grid,
									grid::reload,
									() -> navigateTo(criteria)),
							UiUtil.permitted(UserRight.INFRASTRUCTURE_ARCHIVE, UserRight.INFRASTRUCTURE_VIEW_ARCHIVED)
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
	public void enter(ViewChangeEvent event) {

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
		searchField.setValue(criteria.getNameLike());
		countryFilter.setValue(criteria.getCountry());
		regionFilter.setValue(criteria.getRegion());
		districtFilter.setValue(criteria.getDistrict());
		typeFilter.setValue(criteria.getType());
		activeFilter.setValue(criteria.getActive());

		applyingCriteria = false;
	}

	private boolean isBulkOperationsDropdownVisible() {
		boolean infrastructureDataEditable = UiUtil.enabled(FeatureType.EDIT_INFRASTRUCTURE_DATA);

		return viewConfiguration.isInEagerMode()
			&& (EntityRelevanceStatus.ACTIVE.equals(criteria.getRelevanceStatus())
				|| (infrastructureDataEditable && EntityRelevanceStatus.ARCHIVED.equals(criteria.getRelevanceStatus())));
	}
}
