/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.user;

import com.vaadin.ui.UI;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignFormDataView;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.navigator.Navigator;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class UserRoleController {

	public void registerViews(Navigator navigator) {
		navigator.addView(UserRoleView.VIEW_NAME, UserRoleView.class);
		navigator.addView(UserRoleNotificationsView.VIEW_NAME, UserRoleNotificationsView.class);
	}

	public void create() {
		CommitDiscardWrapperComponent<UserRoleCreateForm> userCreateComponent = getUserRoleCreateComponent();
		VaadinUiUtil.showModalPopupWindow(userCreateComponent, I18nProperties.getString(Strings.headingCreateNewUserRole));
	}

	public CommitDiscardWrapperComponent<UserRoleCreateForm> getUserRoleCreateComponent() {

		UserRoleCreateForm createForm = new UserRoleCreateForm();
		createForm.setValue(UserRoleDto.build());
		final CommitDiscardWrapperComponent<UserRoleCreateForm> editView = new CommitDiscardWrapperComponent<>(
			createForm,
			// TODO - UserRight.USER_ROLE_CREATE
			UserProvider.getCurrent().hasUserRight(UserRight.USER_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				UserRoleDto dto = createForm.getValue();
				FacadeProvider.getUserRoleFacade().saveUserRole(dto);

				editData(dto.getUuid());
			}
		});

		return editView;
	}

	public void editData(String userRoleUuid) {
		String navigationState = UserRoleView.VIEW_NAME + "/" + userRoleUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public CommitDiscardWrapperComponent<UserRoleEditForm> getUserRoleEditComponent(UserRoleReferenceDto userRoleRef) {
		UserRoleEditForm form = new UserRoleEditForm();

		UserRoleDto userRole = FacadeProvider.getUserRoleFacade().getByUuid(userRoleRef.getUuid());
		form.setValue(userRole);

		final CommitDiscardWrapperComponent<UserRoleEditForm> editView =
			// TODO use UserRight.USER_ROLE_EDIT
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasUserRight(UserRight.USER_CREATE), form.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				UserRoleDto dto = form.getValue();
				FacadeProvider.getUserRoleFacade().saveUserRole(dto);

				Notification.show(I18nProperties.getString(Strings.messageUserRoleSaved), Notification.Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			}
		});

		editView.addDeleteListener(() -> {
			long userCountWithRole = FacadeProvider.getUserFacade().getUserCountHavingRole(userRoleRef);
			if (userCountWithRole > 0) {
				List<UserReferenceDto> usersWithOnlyRole = FacadeProvider.getUserFacade().getUsersHavingOnlyRole(userRoleRef);

				if (usersWithOnlyRole.isEmpty()) {
					FacadeProvider.getUserRoleFacade().deleteUserRole(userRoleRef);
					// TODO - navigate to user roles view
					UI.getCurrent().getNavigator().navigateTo(UsersView.VIEW_NAME);
				} else {
					VaadinUiUtil.showSimplePopupWindow(
						I18nProperties.getString(Strings.headingDeleteUserRoleNotPossible),
						String.format(
							I18nProperties.getString(Strings.errorDeleteUserRoleUsedAlone),
							usersWithOnlyRole.stream().map(r -> DataHelper.getShortUuid(r.getUuid())).collect(Collectors.joining(", "))),
						ContentMode.HTML);
				}
			}
		}, I18nProperties.getCaption(UserRoleDto.I18N_PREFIX), () -> {
			long userCountWithRole = FacadeProvider.getUserFacade().getUserCountHavingRole(userRoleRef);
			return userCountWithRole == 0 ? null : String.format(I18nProperties.getString(Strings.confirmationDeleteUserRole), userCountWithRole);
		});

		String enableDisableCaptionKey = userRole.isEnabled() ? Captions.actionDisable : Captions.actionEnable;
		Button enableDisableButton = ButtonHelper.createButton(enableDisableCaptionKey, I18nProperties.getCaption(enableDisableCaptionKey), e -> {
			boolean isCommitSuccessFul = true;
			if (editView.isModified()) {
				isCommitSuccessFul = editView.commitAndHandle();
			}

			if (isCommitSuccessFul) {
				UserRoleDto formData = editView.getWrappedComponent().getValue();
				formData.setEnabled(!userRole.isEnabled());
				FacadeProvider.getUserRoleFacade().saveUserRole(formData);

				SormasUI.refreshView();
			}
		}, ValoTheme.BUTTON_LINK);

		editView.getButtonsPanel().addComponentAsFirst(enableDisableButton);
		editView.getButtonsPanel().setComponentAlignment(enableDisableButton, Alignment.BOTTOM_LEFT);

		return editView;
	}

	public CommitDiscardWrapperComponent<UserRoleNotificationsForm> getUserRoleNotificationsEditComponent(UserRoleReferenceDto userRoleRef) {
		UserRoleNotificationsForm form = new UserRoleNotificationsForm();

		UserRoleDto userRole = FacadeProvider.getUserRoleFacade().getByUuid(userRoleRef.getUuid());
		form.setValue(userRole);

		final CommitDiscardWrapperComponent<UserRoleNotificationsForm> editView =
			// TODO - rights
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasUserRight(UserRight.USER_CREATE), form.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				UserRoleDto dto = form.getValue();
				FacadeProvider.getUserRoleFacade().saveUserRole(dto);

				Notification.show(I18nProperties.getString(Strings.messageUserRoleSaved), Notification.Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			}
		});

		return editView;
	}
}
