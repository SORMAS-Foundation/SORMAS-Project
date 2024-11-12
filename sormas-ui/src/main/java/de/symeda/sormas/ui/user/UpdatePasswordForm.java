/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.user;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.v7.ui.PasswordField;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class UpdatePasswordForm extends AbstractEditForm<UserDto> {

	private static final long serialVersionUID = 1L;
	private static final String OLD_PASSWORD = "oldPassword";
	private static final String NEW_PASSWORD = "newPassword";
	private static final String CONFIRM_PASSWORD = "updateConfirmPassword";

	//@formater:off
	private static final String HTML_LAYOUT = loc(OLD_PASSWORD)
		+ fluidRowLocs(UserDto.PASSWORD)
		+ loc(NEW_PASSWORD)
		+ fluidRowLocs(UserDto.NEW_PASSWORD)
		+ loc(CONFIRM_PASSWORD)
		+ fluidRowLocs(UserDto.CONFIRM_PASSWORD);

	//@formatter:on
	PasswordField currentPassword;
	PasswordField newPassword;
	PasswordField confirmPassword;

	public UpdatePasswordForm() {
		super(UserDto.class, UserDto.I18N_PREFIX);

		setWidth(480, Unit.PIXELS);
	}

	@Override
	protected void addFields() {

		currentPassword = addField(UserDto.PASSWORD, PasswordField.class);
		newPassword = addField(UserDto.NEW_PASSWORD, PasswordField.class);
		confirmPassword = addField(UserDto.CONFIRM_PASSWORD, PasswordField.class);
		setRequired(true, UserDto.PASSWORD, UserDto.NEW_PASSWORD, UserDto.CONFIRM_PASSWORD);
	}

	@Override
	protected String createHtmlLayout() {

		return HTML_LAYOUT;
	}
}
