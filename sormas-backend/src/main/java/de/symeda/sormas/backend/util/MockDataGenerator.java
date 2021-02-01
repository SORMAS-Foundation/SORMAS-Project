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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.backend.user.User;

public final class MockDataGenerator {

	private MockDataGenerator() {
		// Hide Utility Class Constructor
	}

	public static User createUser(UserRole userRole, String firstName, String lastName, String password) {
		Set<UserRole> userRoles = userRole != null ? Collections.singleton(userRole) : null;
		return createUser(userRoles, firstName, lastName, password);
	}

	public static User createUser(Set<UserRole> userRoles, String firstName, String lastName, String password) {

		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		if (CollectionUtils.isNotEmpty(userRoles)) {
			user.setUserRoles(new HashSet<>(userRoles));
		}
		user.setUserName(UserHelper.getSuggestedUsername(user.getFirstName(), user.getLastName()));
		user.setSeed(PasswordHelper.createPass(16));
		user.setPassword(PasswordHelper.encodePassword(password, user.getSeed()));
		return user;
	}
}
