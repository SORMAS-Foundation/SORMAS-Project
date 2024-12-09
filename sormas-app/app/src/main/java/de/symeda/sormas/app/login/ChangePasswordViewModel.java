/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.login;

import androidx.databinding.BaseObservable;

public class ChangePasswordViewModel extends BaseObservable {

	private String currentPassword;
	private String newPassword;
	private String confirmPassword;

	public String getCurrentPassword() {

		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {

		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {

		return newPassword;
	}

	public void setNewPassword(String newPassword) {

		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {

		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {

		this.confirmPassword = confirmPassword;
	}
}
