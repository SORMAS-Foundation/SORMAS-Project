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

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserRoleDto;

public class UserRoleNotificationsForm extends AbstractUserRoleForm {

	private static final String HTML_LAYOUT = fluidRowLocs(UserRoleDto.NOTIFICATION_TYPES);

	private UserRoleNotificationCheckboxSet notificationTypesCbSet;
	private DefaultUserRole defaultUserRole;

	protected UserRoleNotificationsForm() {
		super(UserRoleDto.class, UserRoleDto.I18N_PREFIX);
	}

	public UserRoleNotificationsForm(DefaultUserRole linkedDefaultUserRole) {
		this();
		this.defaultUserRole = linkedDefaultUserRole;
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		notificationTypesCbSet = addField(UserRoleDto.NOTIFICATION_TYPES, UserRoleNotificationCheckboxSet.class);
		notificationTypesCbSet.setCaption(null);
	}

	void applyTemplateData(UserRoleDto templateRole) {
		if (templateRole != null) {
			notificationTypesCbSet.setInternalValue(templateRole.getNotificationTypes());
		}
	}

	@Override
	DefaultUserRole getDefaultUserRole() {
		return defaultUserRole;
	}
}
