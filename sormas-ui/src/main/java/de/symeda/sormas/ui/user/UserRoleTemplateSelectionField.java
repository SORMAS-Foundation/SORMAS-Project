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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserRoleDto;

public class UserRoleTemplateSelectionField extends CustomField<UserRoleDto> {

	private VerticalLayout mainLayout;
	private ComboBox templateRoleCombo;
	private UserRoleEditForm userRoleEditForm;
	private DefaultUserRole defaultUserRole;
	private UserRoleNotificationsForm userRoleNotificationsForm;

	@Override
	protected void doSetValue(UserRoleDto userRoleDto) {
		super.setValue(userRoleDto);
	}

	@Override
	protected Component initContent() {
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(false);
		mainLayout.setSizeFull();
		mainLayout.setWidthFull();

		Label infoLabel =
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getDescription(Descriptions.userRoleTemplate), ContentMode.HTML);
		mainLayout.addComponent(infoLabel);

		templateRoleCombo = new ComboBox();
		templateRoleCombo.setWidth(300, Unit.PIXELS);
		UserRoleFormHelper.setTemplateRoleItems(templateRoleCombo);
		UserRoleFormHelper.setTemplateRoleValue(templateRoleCombo, defaultUserRole);
		templateRoleCombo.setCaption(I18nProperties.getCaption(Captions.userrole_applyUserRoleTemplate));
		mainLayout.addComponent(templateRoleCombo);

		return mainLayout;
	}

	@Override
	public UserRoleDto getValue() {
		return (UserRoleDto) templateRoleCombo.getValue();
	}

	public UserRoleEditForm getUserRoleEditForm() {
		return userRoleEditForm;
	}

	public void setUserRoleEditForm(UserRoleEditForm userRoleEditForm) {
		this.userRoleEditForm = userRoleEditForm;
	}

	public void setDefaultUserRole(DefaultUserRole defaultUserRole) {
		this.defaultUserRole = defaultUserRole;
		if (templateRoleCombo != null) {
			UserRoleFormHelper.setTemplateRoleValue(templateRoleCombo, defaultUserRole);
		}
	}

	public UserRoleNotificationsForm getUserRoleNotificationsForm() {
		return userRoleNotificationsForm;
	}

	public void setUserRoleNotificationsForm(UserRoleNotificationsForm userRoleNotificationsForm) {
		this.userRoleNotificationsForm = userRoleNotificationsForm;
	}
}
