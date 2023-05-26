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

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class UserRoleView extends AbstractUserRoleView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/main";
	private static final long serialVersionUID = -9114105065249694196L;

	public UserRoleView() {
		super(VIEW_NAME);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected void initView(String params) {

		CommitDiscardWrapperComponent<UserRoleEditForm> editComponent =
			ControllerProvider.getUserRoleController().getUserRoleEditComponent(getReference());

		if (userRoleTemplateSelectionField != null) {
			UserRoleEditForm wrappedComponent = editComponent.getWrappedComponent();
			userRoleTemplateSelectionField.setUserRoleEditForm(wrappedComponent);
		}

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		container.addComponent(editComponent);
	}

	@Override
	protected AbstractUserRoleForm getForm() {
		return userRoleTemplateSelectionField.getUserRoleEditForm();
	}
}
