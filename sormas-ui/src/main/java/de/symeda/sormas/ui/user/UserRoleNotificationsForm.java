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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class UserRoleNotificationsForm extends AbstractEditForm<UserRoleDto> {

	private static final String INFO_LABEL_LOC = "infoLabbelLoc";

	private static final String HTML_LAYOUT = fluidRowLocs(INFO_LABEL_LOC) + fluidRowLocs(UserRoleDto.NOTIFICATION_TYPES);

	private UserRoleNotificationCheckboxSet notificationTypesCbSet;

	protected UserRoleNotificationsForm() {
		super(UserRoleDto.class, UserRoleDto.I18N_PREFIX);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		Label infoLabel =
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getDescription(Descriptions.userRoleTemplate), ContentMode.HTML);
		getContent().addComponent(infoLabel, INFO_LABEL_LOC);

		notificationTypesCbSet = addField(UserRoleDto.NOTIFICATION_TYPES, UserRoleNotificationCheckboxSet.class);
		notificationTypesCbSet.setCaption(null);
	}

	void applyTemplateData(UserRoleDto templateRole) {
		if (templateRole != null) {
			notificationTypesCbSet.setInternalValue(templateRole.getNotificationTypes());
		}
	}
}
