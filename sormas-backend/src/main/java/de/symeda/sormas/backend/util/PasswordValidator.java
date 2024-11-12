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
package de.symeda.sormas.backend.util;

public class PasswordValidator {

	private PasswordValidator() {

		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static String checkPasswordStrength(String password) {

		boolean strongPassword = isStrongPassword(password);

		if (strongPassword) {
			return "Password is Strong";
		} else {
			return "Password is Weak";
		}
	}

	public static boolean isStrongPassword(String password) {

		if (password == null) {
			throw new IllegalArgumentException("Password cannot be null");
		}

		boolean isLengthValid = password.length() >= 8;
		boolean hasDigit = hasDigits(password);
		boolean hasCapitalLetter = hasCapitalLetter(password);
		boolean hasLowercaseLetter = hasLowercaseLetter(password);

		return isLengthValid && hasDigit && hasCapitalLetter && hasLowercaseLetter;
	}

	private static boolean hasDigits(String password) {

		return password.chars().anyMatch(Character::isDigit);
	}

	private static boolean hasCapitalLetter(String password) {

		return password.chars().anyMatch(Character::isUpperCase);
	}

	private static boolean hasLowercaseLetter(String password) {

		return password.chars().anyMatch(Character::isLowerCase);
	}
}
