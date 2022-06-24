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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.contact.ContactDataView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

import static de.symeda.sormas.ui.SormasUI.refreshView;

public class UserRoleController {

	public void create() {
		CommitDiscardWrapperComponent<UserRoleCreateForm> userCreateComponent = getUserRoleCreateComponent();
		VaadinUiUtil.showModalPopupWindow(userCreateComponent, I18nProperties.getString(Strings.headingCreateNewUserRole));
	}

	public CommitDiscardWrapperComponent<UserRoleCreateForm> getUserRoleCreateComponent() {

		UserRoleCreateForm createForm = new UserRoleCreateForm();
		createForm.setValue(UserRoleDto.build());
		final CommitDiscardWrapperComponent<UserRoleCreateForm> editView = new CommitDiscardWrapperComponent<UserRoleCreateForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.USER_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				UserRoleDto dto = createForm.getValue();
				FacadeProvider.getUserRoleFacade().saveUserRole(dto, createForm.getSelectedTemplateRole());

				editData(dto.getUuid());
			}
		});

		return editView;
	}

	public void editData(String userRoleUuid) {
		String navigationState = ContactDataView.VIEW_NAME + "/" + userRoleUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}
}
