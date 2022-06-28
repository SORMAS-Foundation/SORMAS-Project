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
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractDetailView;

public abstract class AbstractUserRoleView extends AbstractDetailView<UserRoleReferenceDto> {

	public static final String ROOT_VIEW_NAME = UsersView.VIEW_NAME;

	protected AbstractUserRoleView(String viewName) {
		super(viewName);
	}

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
		menu.addView(UserRoleView.VIEW_NAME, I18nProperties.getCaption(UserRoleDto.I18N_PREFIX), params);
	}
}
