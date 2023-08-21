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

package de.symeda.sormas.ui.environment;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class EnvironmentController {

	public void registerViews(Navigator navigator) {
		navigator.addView(EnvironmentsView.VIEW_NAME, EnvironmentsView.class);
		navigator.addView(EnvironmentDataView.VIEW_NAME, EnvironmentDataView.class);
	}

	public void create() {
		CommitDiscardWrapperComponent<EnvironmentCreateForm> environmentCreateComponent = getEnvironmentCreateComponent();
		if (environmentCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(environmentCreateComponent, I18nProperties.getString(Strings.headingCreateNewEnvironment));
		}

	}

	public CommitDiscardWrapperComponent<EnvironmentCreateForm> getEnvironmentCreateComponent() {
		UserProvider curentUser = UserProvider.getCurrent();

		if (curentUser != null) {
			EnvironmentCreateForm createForm;
			createForm = new EnvironmentCreateForm();
			final EnvironmentDto environment = EnvironmentDto.build(curentUser.getUser());
			createForm.setValue(environment);
			final CommitDiscardWrapperComponent<EnvironmentCreateForm> editView = new CommitDiscardWrapperComponent<>(
				createForm,
				UserProvider.getCurrent().hasUserRight(UserRight.ENVIRONMENT_CREATE),
				createForm.getFieldGroup());

			editView.addCommitListener(() -> {
				if (!createForm.getFieldGroup().isModified()) {
					EnvironmentDto dto = createForm.getValue();
					FacadeProvider.getEnvironmentFacade().save(dto);
					Notification.show(I18nProperties.getString(Strings.messageEnvironmentCreated), Notification.Type.WARNING_MESSAGE);

					navigateToEnvironment(dto.getUuid());
				}
			});
			return editView;
		}

		return null;

	}

	public void navigateToEnvironment(String uuid) {
		navigateToView(EnvironmentDataView.VIEW_NAME, uuid);
	}

	public void navigateToView(String viewName, String uuid) {
		final String navigationState = viewName + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	private static void saveEnvironment(EnvironmentDto environment) {
		FacadeProvider.getEnvironmentFacade().save(environment);

		Notification.show(I18nProperties.getString(Strings.messageEnvironmentSaved), Notification.Type.WARNING_MESSAGE);
		SormasUI.refreshView();
	}

	public CommitDiscardWrapperComponent<EnvironmentDataForm> getEnvironmentDataEditComponent(
		String environmentUuid,
		UserRight editUserRight,
		boolean isEditAllowed) {

		EnvironmentDto environmentDto = FacadeProvider.getEnvironmentFacade().getByUuid(environmentUuid);
		DeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getEnvironmentFacade().getAutomaticDeletionInfo(environmentUuid);
		DeletionInfoDto manuallyDeletionInfoDto = FacadeProvider.getEnvironmentFacade().getManuallyDeletionInfo(environmentUuid);

		EnvironmentDataForm environmentDataForm = new EnvironmentDataForm(
			environmentDto.isPseudonymized(),
			environmentDto.isInJurisdiction(),
			isEditAllowed && UserProvider.getCurrent().hasUserRight(editUserRight));
		environmentDataForm.setValue(environmentDto);

		CommitDiscardWrapperComponent<EnvironmentDataForm> editComponent =
			new CommitDiscardWrapperComponent<>(environmentDataForm, true, environmentDataForm.getFieldGroup());

		editComponent.addCommitListener(() -> {
			if (!environmentDataForm.getFieldGroup().isModified()) {
				EnvironmentDto dto = environmentDataForm.getValue();

				final UserDto user = UserProvider.getCurrent().getUser();
				final RegionReferenceDto userRegion = user.getRegion();
				final DistrictReferenceDto userDistrict = user.getDistrict();
				final RegionReferenceDto environmentRegion = dto.getLocation().getRegion();
				final DistrictReferenceDto environmentDistrict = dto.getLocation().getDistrict();
				final boolean outsideJurisdiction = (!DataHelper.isSame(dto.getReportingUser(), user)
					&& (userRegion != null && !DataHelper.isSame(userRegion, environmentRegion)
						|| userDistrict != null && !DataHelper.isSame(userDistrict, environmentDistrict)));

				if (outsideJurisdiction) {
					VaadinUiUtil.showConfirmationPopup(
						I18nProperties.getString(Strings.headingEnvironmentJurisdictionUpdated),
						new Label(I18nProperties.getString(Strings.messageEnvironmentJurisdictionUpdated)),
						I18nProperties.getString(Strings.yes),
						I18nProperties.getString(Strings.no),
						500,
						confirmed -> {
							if (Boolean.TRUE.equals(confirmed)) {
								saveEnvironment(dto);
							}
						});
				} else {
					saveEnvironment(dto);
				}

			}
		});

		editComponent.getButtonsPanel()
			.addComponentAsFirst(
				new DeletionLabel(automaticDeletionInfoDto, manuallyDeletionInfoDto, environmentDto.isDeleted(), EnvironmentDto.I18N_PREFIX));

		if (environmentDto.isDeleted()) {
			editComponent.getWrappedComponent().getField(EnvironmentDto.DELETION_REASON).setVisible(true);
			if (editComponent.getWrappedComponent().getField(EnvironmentDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				editComponent.getWrappedComponent().getField(EnvironmentDto.OTHER_DELETION_REASON).setVisible(true);
			}
		}

		// Initialize 'Delete' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.ENVIRONMENT_DELETE)) {
			editComponent.addDeleteWithReasonOrRestoreListener(
				EnvironmentsView.VIEW_NAME,
				null,
				I18nProperties.getString(Strings.entityEnvironment),
				environmentDto.getUuid(),
				FacadeProvider.getEnvironmentFacade());
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.ENVIRONMENT_ARCHIVE)) {
			ControllerProvider.getArchiveController()
				.addArchivingButton(
					environmentDto,
					ArchiveHandlers.forEnvironment(),
					editComponent,
					() -> navigateToEnvironment(environmentDto.getUuid()));
		}

		editComponent.restrictEditableComponentsOnEditView(
			UserRight.ENVIRONMENT_EDIT,
			null,
			UserRight.ENVIRONMENT_DELETE,
			UserRight.ENVIRONMENT_ARCHIVE,
			FacadeProvider.getEnvironmentFacade().getEditPermissionType(environmentDto.getUuid()),
			true);

		return editComponent;
	}

	public TitleLayout getEnvironmentViewTitleLayout(String uuid) {
		EnvironmentDto environmentDto = findEnvironment(uuid);

		TitleLayout titleLayout = new TitleLayout();

		String shortUuid = DataHelper.getShortUuid(environmentDto.getUuid());

		StringBuilder mainRowText = new StringBuilder(environmentDto.getEnvironmentName());
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);

		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	private EnvironmentDto findEnvironment(String uuid) {
		return FacadeProvider.getEnvironmentFacade().getByUuid(uuid);
	}

}
