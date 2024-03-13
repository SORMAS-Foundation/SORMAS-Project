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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class UserRoleController {

	public void create() {
		CommitDiscardWrapperComponent<UserRoleCreateForm> userCreateComponent = getUserRoleCreateComponent();
		VaadinUiUtil.showModalPopupWindow(userCreateComponent, I18nProperties.getString(Strings.headingCreateNewUserRole));
	}

	public CommitDiscardWrapperComponent<UserRoleCreateForm> getUserRoleCreateComponent() {

		UserRoleCreateForm createForm = new UserRoleCreateForm();
		createForm.setValue(UserRoleDto.build());
		final CommitDiscardWrapperComponent<UserRoleCreateForm> editView =
			new CommitDiscardWrapperComponent<>(createForm, UiUtil.permitted(UserRight.USER_ROLE_EDIT), createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				UserRoleDto dto = createForm.getValue();
				FacadeProvider.getUserRoleFacade().saveUserRole(dto);
				if (dto.getUserRights().size() == 0) {
					Notification.show(
						I18nProperties.getString(Strings.messageUserRoleSaved),
						I18nProperties.getString(Strings.messageUserRoleHasNoRights),
						Notification.Type.WARNING_MESSAGE);
				}
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

		final CommitDiscardWrapperComponent<UserRoleEditForm> editView = new CommitDiscardWrapperComponent<>(form, true, form.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				UserRoleDto dto = form.getValue();

				UserDto currentUser = UiUtil.getUser();
				if (currentUser.getUserRoles().stream().anyMatch(r -> DataHelper.isSame(r, dto))) {
					Collection<UserRoleDto> currentUserRoles = FacadeProvider.getUserRoleFacade().getByReferences(currentUser.getUserRoles());
					Set<UserRight> currentUserRights = UserRoleDto.getUserRights(currentUserRoles);
					Set<UserRight> newUserRights = UserRoleDto
						// replace old user role with the one being edited
						.getUserRights(currentUserRoles.stream().map(r -> DataHelper.isSame(r, dto) ? dto : r).collect(Collectors.toList()));

					if (currentUserRights.contains(UserRight.USER_ROLE_EDIT) && !newUserRights.contains(UserRight.USER_ROLE_EDIT)) {
						new Notification(
							I18nProperties.getString(Strings.messageCheckInputData),
							I18nProperties.getValidationError(Validations.removeUserRightEditRightFromOwnUser),
							Notification.Type.ERROR_MESSAGE,
							true).show(Page.getCurrent());

						return;
					} else if (currentUserRights.contains(UserRight.USER_EDIT) && !newUserRights.contains(UserRight.USER_EDIT)) {
						new Notification(
							I18nProperties.getString(Strings.messageCheckInputData),
							I18nProperties.getValidationError(Validations.removeUserEditRightFromOwnUser),
							Notification.Type.ERROR_MESSAGE,
							true).show(Page.getCurrent());

						return;
					}
				}
				FacadeProvider.getUserRoleFacade().saveUserRole(dto);

				if (!(dto.getUserRights().contains(UserRight.SORMAS_UI) && dto.getUserRights().contains(UserRight.SORMAS_REST))) {
					Notification.show(
						I18nProperties.getString(Strings.messageUserRoleSaved),
						I18nProperties.getString(Strings.messageUserRoleUnusableForLogin),
						Notification.Type.WARNING_MESSAGE);

				} else {
					Notification.show(I18nProperties.getString(Strings.messageUserRoleSaved), Notification.Type.WARNING_MESSAGE);
				}
				SormasUI.refreshView();
			}
		});

		if (UiUtil.permitted(UserRight.USER_ROLE_DELETE)) {
			editView.addDeleteListener(() -> {
				long userCountWithRole = FacadeProvider.getUserFacade().getUserCountHavingRole(userRoleRef);
				if (userCountWithRole > 0) {
					List<UserReferenceDto> usersWithOnlyRole = FacadeProvider.getUserFacade().getUsersHavingOnlyRole(userRoleRef);

					if (usersWithOnlyRole.isEmpty()) {
						FacadeProvider.getUserRoleFacade().deleteUserRole(userRoleRef);
						UI.getCurrent().getNavigator().navigateTo(UserRolesView.VIEW_NAME);
					} else {
						VaadinUiUtil.showSimplePopupWindow(
							I18nProperties.getString(Strings.headingDeleteUserRoleNotPossible),
							String.format(
								I18nProperties.getString(Strings.errorDeleteUserRoleUsedAlone),
								usersWithOnlyRole.stream().map(r -> DataHelper.getShortUuid(r.getUuid())).collect(Collectors.joining(", "))),
							ContentMode.HTML);
					}
				} else {
					FacadeProvider.getUserRoleFacade().deleteUserRole(userRoleRef);
					UI.getCurrent().getNavigator().navigateTo(UserRolesView.VIEW_NAME);
				}
			}, I18nProperties.getCaption(UserRoleDto.I18N_PREFIX), () -> {
				long userCountWithRole = FacadeProvider.getUserFacade().getUserCountHavingRole(userRoleRef);
				return userCountWithRole == 0 ? null : String.format(I18nProperties.getString(Strings.confirmationDeleteUserRole), userCountWithRole);
			});
		}

		String enableDisableCaptionKey = userRole.isEnabled() ? Captions.actionDisable : Captions.actionEnable;
		Button enableDisableButton = ButtonHelper.createButton(enableDisableCaptionKey, I18nProperties.getCaption(enableDisableCaptionKey), e -> {
			UserRoleDto refreshedUserRole = editView.getWrappedComponent().getValue();

			boolean isCommitSuccessFul = true;
			if (editView.isDirty()) {
				isCommitSuccessFul = editView.commitAndHandle();
				refreshedUserRole = FacadeProvider.getUserRoleFacade().getByUuid(refreshedUserRole.getUuid());
			}

			if (isCommitSuccessFul) {
				refreshedUserRole.setEnabled(!refreshedUserRole.isEnabled());
				FacadeProvider.getUserRoleFacade().saveUserRole(refreshedUserRole);

				SormasUI.refreshView();
			}
		}, ValoTheme.BUTTON_LINK);

		editView.getButtonsPanel().addComponentAsFirst(enableDisableButton);
		editView.getButtonsPanel().setComponentAlignment(enableDisableButton, Alignment.BOTTOM_LEFT);

		editView.restrictEditableComponentsOnEditView(UserRight.USER_ROLE_EDIT, null, UserRight.USER_ROLE_DELETE, null, true);

		return editView;
	}

	public CommitDiscardWrapperComponent<UserRoleNotificationsForm> getUserRoleNotificationsEditComponent(UserRoleReferenceDto userRoleRef) {
		UserRoleDto userRole = FacadeProvider.getUserRoleFacade().getByUuid(userRoleRef.getUuid());
		UserRoleNotificationsForm form = new UserRoleNotificationsForm(userRole.getLinkedDefaultUserRole());

		form.setValue(userRole);

		final CommitDiscardWrapperComponent<UserRoleNotificationsForm> editView =
			new CommitDiscardWrapperComponent<>(form, UiUtil.permitted(UserRight.USER_ROLE_EDIT), form.getFieldGroup());

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
