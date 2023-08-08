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
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityExportDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityIndexDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
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
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.RowCount;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class FacilitiesView extends AbstractConfigurationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/facilities";
	private static final long serialVersionUID = -2015225571046243640L;
	protected FacilitiesGrid grid;
	protected Button importButton;
	protected Button createButton;
	protected PopupButton exportPopupButton;
	private FacilityCriteria criteria;
	private ViewConfiguration viewConfiguration;
	// Filter
	private SearchField searchField;
	private ComboBox typeGroupFilter;
	private ComboBox typeFilter;
	private ComboBox countryFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox communityFilter;
	private ComboBox relevanceStatusFilter;
	private Button resetButton;
	//	private HorizontalLayout headerLayout;
	private HorizontalLayout filterLayout;
	private VerticalLayout gridLayout;
	private MenuBar bulkOperationsDropdown;
	private RowCount rowCount;

	public FacilitiesView() {

		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		criteria = ViewModelProviders.of(getClass())
			.get(FacilityCriteria.class, new FacilityCriteria().country(FacadeProvider.getCountryFacade().getServerCountry()));
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		grid = new FacilitiesGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		rowCount = new RowCount(Strings.labelNumberOfFacilities, grid.getDataSize());
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
				Window window = VaadinUiUtil.showPopupWindow(new InfrastructureImportLayout(InfrastructureType.FACILITY));
				window.setCaption(I18nProperties.getString(Strings.headingImportFacilities));
				window.addCloseListener(c -> {
					grid.reload();
					rowCount.update(grid.getDataSize());
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

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EXPORT)) {
			VerticalLayout exportLayout = new VerticalLayout();
			exportLayout.setSpacing(true);
			exportLayout.setMargin(true);
			exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			exportLayout.setWidth(250, Unit.PIXELS);

			exportPopupButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD, exportLayout);
			addHeaderComponent(exportPopupButton);

			StreamResource basicExportStreamResource = GridExportStreamResource
				.createStreamResourceWithSelectedItems(grid, this::getSelectedRows, ExportEntityName.FACILITIES, FacilitiesGrid.ACTION_BTN_ID);

			addExportButton(
				basicExportStreamResource,
				exportPopupButton,
				exportLayout,
				VaadinIcons.TABLE,
				Captions.exportBasic,
				Strings.infoBasicExport);

			// Detailed export

			StreamResource detailedExportStreamResource = DownloadUtil.createCsvExportStreamResource(
				FacilityExportDto.class,
				null,
				(Integer start, Integer max) -> FacadeProvider.getFacilityFacade()
					.getExportList(
						grid.getCriteria(),
						getSelectedRows().stream().map(FacilityIndexDto::getUuid).collect(Collectors.toList()),
						start,
						max),
				(propertyId, type) -> {
					String caption = I18nProperties.findPrefixCaption(propertyId, FacilityExportDto.I18N_PREFIX, FacilityDto.I18N_PREFIX);
					if (Date.class.isAssignableFrom(type)) {
						caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
					}
					return caption;
				},
				ExportEntityName.FACILITIES,
				null);

			addExportButton(
				detailedExportStreamResource,
				exportPopupButton,
				exportLayout,
				VaadinIcons.FILE_TEXT,
				Captions.exportDetailed,
				Strings.infoDetailedExport);
		}

		if (infrastructureDataEditable && UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_CREATE)) {
			createButton = ButtonHelper.createIconButtonWithCaption(
				"create",
				I18nProperties.getCaption(Captions.actionNewEntry),
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getInfrastructureController().createFacility(),
				ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(createButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
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
				rowCount.update(grid.getDataSize());
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

	//	TODO additional filter bar (active, archived and other)
	//	private HorizontalLayout createHeaderBar() {
	//		headerLayout = new HorizontalLayout();
	//		headerLayout.setSpacing(true);
	//		headerLayout.setWidth(100, Unit.PERCENTAGE);
	//
	//		return headerLayout;
	//	}

	private Set<FacilityIndexDto> getSelectedRows() {
		FacilitiesGrid facilitiesGrid = this.grid;
		return this.viewConfiguration.isInEagerMode() ? facilitiesGrid.asMultiSelect().getSelectedItems() : Collections.emptySet();
	}

	private HorizontalLayout createFilterBar() {
		filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		searchField = new SearchField();
		searchField.addTextChangeListener(e -> {
			criteria.nameAddressLike(e.getText());
			grid.reload();
			rowCount.update(grid.getDataSize());
		});
		filterLayout.addComponent(searchField);

		typeGroupFilter = ComboBoxHelper.createComboBoxV7();
		typeGroupFilter.setId("typeGroup");
		typeGroupFilter.setWidth(220, Unit.PIXELS);
		typeGroupFilter.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		typeGroupFilter.addItems(FacilityTypeGroup.values());
		typeGroupFilter.addValueChangeListener(e -> {
			criteria.typeGroup((FacilityTypeGroup) e.getProperty().getValue());
			FieldHelper.updateItems(typeFilter, FacilityType.getTypes((FacilityTypeGroup) typeGroupFilter.getValue()));
		});
		filterLayout.addComponent(typeGroupFilter);

		typeFilter = ComboBoxHelper.createComboBoxV7();
		typeFilter.setId(FacilityDto.TYPE);
		typeFilter.setWidth(220, Unit.PIXELS);
		typeFilter.setCaption(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.TYPE));
		typeFilter.addValueChangeListener(e -> {
			criteria.type((FacilityType) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(typeFilter);

		countryFilter = addCountryFilter(filterLayout, country -> {
			criteria.country(country);
			grid.reload();
			rowCount.update(grid.getDataSize());
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
		regionFilter.setId(FacilityDto.REGION);
		regionFilter.setWidth(140, Unit.PIXELS);
		regionFilter.setCaption(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.REGION));
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
		districtFilter.setId(FacilityDto.DISTRICT);
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setCaption(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.DISTRICT));
		districtFilter.addValueChangeListener(e -> {
			DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
			criteria.district(district);
			navigateTo(criteria);
			FieldHelper.updateItems(
				communityFilter,
				district != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()) : null);
		});
		filterLayout.addComponent(districtFilter);

		communityFilter = ComboBoxHelper.createComboBoxV7();
		communityFilter.setId(FacilityDto.COMMUNITY);
		communityFilter.setWidth(140, Unit.PIXELS);
		communityFilter.setCaption(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.COMMUNITY));
		communityFilter.addValueChangeListener(e -> {
			criteria.community((CommunityReferenceDto) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(communityFilter);

		resetButton = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(FacilitiesView.class).remove(FacilityCriteria.class);
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
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.facilityActiveFacilities));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.facilityArchivedFacilities));

				relevanceStatusFilter
					.setItemCaption(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED, I18nProperties.getCaption(Captions.facilityAllFacilities));
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
										ArchiveHandlers.forInfrastructure(FacadeProvider.getFacilityFacade(), ArchiveMessages.FACILITY),
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
										ArchiveHandlers.forInfrastructure(FacadeProvider.getFacilityFacade(), ArchiveMessages.FACILITY),
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
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
		grid.reload();
		rowCount.update(grid.getDataSize());
	}

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		resetButton.setVisible(criteria.hasAnyFilterActive());

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}
		searchField.setValue(criteria.getNameAddressLike());
		typeGroupFilter.setValue(criteria.getTypeGroup());
		typeFilter.setValue(criteria.getType());
		countryFilter.setValue(criteria.getCountry());
		regionFilter.setValue(criteria.getRegion());
		districtFilter.setValue(criteria.getDistrict());
		communityFilter.setValue(criteria.getCommunity());

		applyingCriteria = false;
	}

	private boolean isBulkOperationsDropdownVisible() {
		boolean infrastructureDataEditable = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA);

		return viewConfiguration.isInEagerMode()
			&& (EntityRelevanceStatus.ACTIVE.equals(criteria.getRelevanceStatus())
				|| (infrastructureDataEditable && EntityRelevanceStatus.ARCHIVED.equals(criteria.getRelevanceStatus())));
	}

}
