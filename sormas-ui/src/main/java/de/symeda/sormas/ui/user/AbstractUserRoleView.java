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

import com.vaadin.ui.Button;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public abstract class AbstractUserRoleView extends AbstractDetailView<UserRoleReferenceDto> {

	public static final String ROOT_VIEW_NAME = "userrole";
	protected UserRoleTemplateSelectionField userRoleTemplateSelectionField;
	private Button applyUserRoleTemplate;

	protected AbstractUserRoleView(String viewName) {
		super(viewName);

		if (UiUtil.permitted(UserRight.USER_ROLE_EDIT) || UiUtil.permitted(UserRight.USER_ROLE_VIEW)) {
			userRoleTemplateSelectionField = new UserRoleTemplateSelectionField();

			applyUserRoleTemplate = ButtonHelper.createButton(Captions.userrole_applyUserRoleTemplate, e -> {
				VaadinUiUtil.showConfirmationPopup(
					I18nProperties.getCaption(Captions.userrole_applyUserRoleTemplate),
					userRoleTemplateSelectionField,
					I18nProperties.getCaption(Captions.actionApply),
					I18nProperties.getCaption(Captions.actionCancel),
					720,
					confirmed -> {
						if (Boolean.TRUE.equals(confirmed)) {
							getForm().applyTemplateData(userRoleTemplateSelectionField.getValue());
						}
						return true;
					});
			});

			if (!UserProvider.getCurrent().getUserRights().contains(UserRight.USER_ROLE_EDIT)) {
				applyUserRoleTemplate.setEnabled(false);
			}

			addHeaderComponent(applyUserRoleTemplate);
		}
	}

	protected abstract AbstractUserRoleForm getForm();

	@Override
	protected UserRoleReferenceDto getReferenceByUuid(String uuid) {
		return FacadeProvider.getUserRoleFacade().getReferenceByUuid(uuid);
	}

	@Override
	protected String getRootViewName() {
		return ROOT_VIEW_NAME;
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}

		menu.removeAllViews();
		menu.addView(UserRolesView.VIEW_NAME, I18nProperties.getCaption(Captions.userRoleUserrolesView), params, true);
		menu.addView(UserRoleView.VIEW_NAME, I18nProperties.getCaption(UserRoleDto.I18N_PREFIX), params);
		menu.addView(UserRoleNotificationsView.VIEW_NAME, I18nProperties.getCaption(Captions.userRoleNotifications), params);

		TitleLayout mainHeader = new TitleLayout();
		mainHeader.addMainRow(getReference().getCaption());
		setMainHeaderComponent(mainHeader);
	}
}
