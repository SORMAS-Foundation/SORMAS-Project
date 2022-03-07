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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.RowCount;
import de.symeda.sormas.ui.utils.ViewConfiguration;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link UserController} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class UsersView extends AbstractView {

	private static final long serialVersionUID = -3533557348144005469L;

	public static final String VIEW_NAME = "users";
	public static final String ACTIVE_FILTER = I18nProperties.getString(Strings.active);
	public static final String INACTIVE_FILTER = I18nProperties.getString(Strings.inactive);

	private UserCriteria criteria;

	private UserGrid grid;
	private Button createButton;
	private Button syncButton;

	private VerticalLayout gridLayout;

	// Filters
	private ComboBox activeFilter;
	private ComboBox userRolesFilter;
	private ComboBox areaFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private TextField searchField;

	private RowCount rowsCount;

	// Bulk operations
	private MenuBar bulkOperationsDropdown;

	public UsersView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(UsersView.class).get(UserCriteria.class);

		grid = new UserGrid();
		grid.setCriteria(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createActionsBar());

		rowsCount = new RowCount(Strings.labelNumberOfUsers, grid.getItemCount());
		gridLayout.addComponent(rowsCount);

		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		if (UserProvider.getCurrent().hasUserRight(UserRight.USER_CREATE)) {
			createButton = ButtonHelper.createIconButton(
				Captions.userNewUser,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getUserController().create(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);

			Button exportUserRightsButton =
				ButtonHelper.createIconButton(Captions.exportUserRoles, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);

			new FileDownloader(new StreamResource(() -> new DownloadUtil.DelayedInputStream((out) -> {
				try {
					String documentPath = FacadeProvider.getUserRightsFacade().generateUserRightsDocument(true);
					IOUtils.copy(Files.newInputStream(new File(documentPath).toPath()), out);
				} catch (IOException e) {
					LoggerFactory.getLogger(DownloadUtil.class).error(e.getMessage(), e);
					new Notification(
						I18nProperties.getString(Strings.headingExportUserRightsFailed),
						I18nProperties.getString(Strings.messageUserRightsExportFailed),
						Notification.Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}, (e) -> {
			}), createFileNameWithCurrentDate(ExportEntityName.USER_ROLES, ".xlsx"))).extend(exportUserRightsButton);

			addHeaderComponent(exportUserRightsButton);
		}

		if (AuthProvider.getProvider(FacadeProvider.getConfigFacade()).isUserSyncSupported()) {
			syncButton = ButtonHelper.createIconButton(Captions.syncUsers, VaadinIcons.REFRESH, e -> ControllerProvider.getUserController().sync());

			addHeaderComponent(syncButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!ViewModelProviders.of(UsersView.class).get(ViewConfiguration.class).isInEagerMode());

			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(ViewModelProviders.of(UsersView.class).get(ViewConfiguration.class).isInEagerMode());

			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(true);
				ViewModelProviders.of(UsersView.class).get(ViewConfiguration.class).setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				grid.reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(false);
				ViewModelProviders.of(UsersView.class).get(ViewConfiguration.class).setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				navigateTo(criteria);
			});
		}
	}

	public HorizontalLayout createFilterBar() {

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		activeFilter = ComboBoxHelper.createComboBoxV7();
		activeFilter.setId(UserDto.ACTIVE);
		activeFilter.setWidth(200, Unit.PIXELS);
		activeFilter.setInputPrompt(I18nProperties.getPrefixCaption(UserDto.I18N_PREFIX, UserDto.ACTIVE));
		activeFilter.addItems(ACTIVE_FILTER, INACTIVE_FILTER);
		activeFilter.addValueChangeListener(e -> {
			criteria.active(
				ACTIVE_FILTER.equals(e.getProperty().getValue())
					? Boolean.TRUE
					: INACTIVE_FILTER.equals(e.getProperty().getValue()) ? Boolean.FALSE : null);
			navigateTo(criteria);
		});
		filterLayout.addComponent(activeFilter);

		userRolesFilter = ComboBoxHelper.createComboBoxV7();
		userRolesFilter.setId(UserDto.USER_ROLES);
		userRolesFilter.setWidth(200, Unit.PIXELS);
		userRolesFilter.setInputPrompt(I18nProperties.getPrefixCaption(UserDto.I18N_PREFIX, UserDto.USER_ROLES));
		userRolesFilter.addItems(UserUiHelper.getAssignableRoles(Collections.emptySet()));
		userRolesFilter.addValueChangeListener(e -> {
			criteria.userRole((UserRole) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(userRolesFilter);

		UserDto user = UserProvider.getCurrent().getUser();
		
		areaFilter = ComboBoxHelper.createComboBoxV7();
		areaFilter.setId(CaseDataDto.AREA);

		if (user.getArea() == null) {
			areaFilter.setWidth(140, Unit.PIXELS);
			areaFilter.setInputPrompt(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.AREA));
			areaFilter.addItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
			areaFilter.addValueChangeListener(e -> {
				AreaReferenceDto area = (AreaReferenceDto) e.getProperty().getValue();

				if (!DataHelper.equal(area, criteria.getArea())) {
					criteria.region(null);
				}

				criteria.area(area);
				navigateTo(criteria);
			});
			filterLayout.addComponent(areaFilter);
		}


		regionFilter = ComboBoxHelper.createComboBoxV7();
		regionFilter.setId(CaseDataDto.REGION);

		if (user.getRegion() == null) {
			regionFilter.setWidth(140, Unit.PIXELS);
			regionFilter.setInputPrompt("Province");
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByArea(areaFilter.getId()));
			regionFilter.addValueChangeListener(e -> {
				RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();

				if (!DataHelper.equal(region, criteria.getRegion())) {
					criteria.district(null);
				}

				criteria.region(region);
				navigateTo(criteria);
			});
			filterLayout.addComponent(regionFilter);
		}
		
		
		
		districtFilter = ComboBoxHelper.createComboBoxV7();
		districtFilter.setId(CaseDataDto.DISTRICT);
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setInputPrompt(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
		districtFilter.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
		districtFilter.addValueChangeListener(e -> {
			DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
			criteria.district(district);
			navigateTo(criteria);
		});
		filterLayout.addComponent(districtFilter);

		searchField = new TextField();
		searchField.setId("search");
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setNullRepresentation("");
		searchField.setInputPrompt(I18nProperties.getString(Strings.promptUserSearch));
		searchField.setImmediate(true);
		searchField.addTextChangeListener(e -> {
			criteria.freeText(e.getText());
			grid.reload();
			rowsCount.update(grid.getItemCount());
		});
		filterLayout.addComponent(searchField);

		return filterLayout;
	}

	public HorizontalLayout createActionsBar() {

		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Bulk operation dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				bulkOperationsDropdown = MenuBarHelper.createDropDown(
					Captions.bulkActions,
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.actionEnable), VaadinIcons.CHECK_SQUARE_O, selectedItem -> {
						ControllerProvider.getUserController()
							.enableAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
					}, true),
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.actionDisable), VaadinIcons.THIN_SQUARE, selectedItem -> {
						ControllerProvider.getUserController()
							.disableAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
					}, true));

				bulkOperationsDropdown.setVisible(ViewModelProviders.of(UsersView.class).get(ViewConfiguration.class).isInEagerMode());
				actionButtonsLayout.addComponent(bulkOperationsDropdown);
			}
		}
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {

		if (event != null) {
			String params = event.getParameters().trim();
			if (params.startsWith("?")) {
				params = params.substring(1);
				criteria.fromUrlParams(params);
			}
			updateFilterComponents();
		}
		grid.reload();
	}

	public void updateFilterComponents() {

		applyingCriteria = true;
		UserDto user = UserProvider.getCurrent().getUser();

		activeFilter.setValue(criteria.getActive() == null ? null : criteria.getActive() ? ACTIVE_FILTER : INACTIVE_FILTER);
		userRolesFilter.setValue(criteria.getUserRole());
		areaFilter.setValue(criteria.getArea());
//work here TODO TONIGHT
		if (user.getArea() != null && user.getRegion() == null){
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByArea(user.getArea().getUuid()));
			regionFilter.setEnabled(true);
		} else if (criteria.getArea() != null) {
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByArea(criteria.getArea().getUuid()));
			regionFilter.setEnabled(true);
		} else if (criteria.getRegion() != null) {
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(criteria.getRegion().getUuid()));
			districtFilter.setEnabled(true);
		} else {
			regionFilter.setEnabled(false);
			districtFilter.setEnabled(false);
		}
		regionFilter.setValue(criteria.getRegion());
		districtFilter.setValue(criteria.getDistrict());
		searchField.setValue(criteria.getFreeText());

		applyingCriteria = false;
	}
}
