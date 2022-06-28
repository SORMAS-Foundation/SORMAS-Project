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

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class UserRoleController {

	public void registerViews(Navigator navigator) {
		navigator.addView(UserRoleView.VIEW_NAME, UserRoleView.class);
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

	public CommitDiscardWrapperComponent<UserRoleDataForm> getUserRoleEditComponent(UserRoleReferenceDto userRoleRef) {
		UserRoleDataForm form = new UserRoleDataForm();
		form.setValue(UserRoleDto.build());
		final CommitDiscardWrapperComponent<UserRoleDataForm> editView =
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasUserRight(UserRight.USER_CREATE), form.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				UserRoleDto dto = form.getValue();
				FacadeProvider.getUserRoleFacade().saveUserRole(dto);

				Notification.show(I18nProperties.getString(Strings.messageUserRoleSaved), Notification.Type.WARNING_MESSAGE);
				SormasUI.refreshView();

			}
		});

		UserRoleDto userRole = FacadeProvider.getUserRoleFacade().getByUuid(userRoleRef.getUuid());
		form.setValue(userRole);

		return editView;
	}
}
