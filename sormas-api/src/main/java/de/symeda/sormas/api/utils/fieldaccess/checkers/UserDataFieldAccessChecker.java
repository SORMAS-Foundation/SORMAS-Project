/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.utils.fieldaccess.checkers;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.PersonnelData;

import java.lang.reflect.Field;

public class UserDataFieldAccessChecker extends RightBasedFieldAccessChecker {
	private UserJurisdictionCheck jurisdictionCheck;

	public UserDataFieldAccessChecker(RightCheck rightCheck, UserJurisdictionCheck jurisdictionCheck) {
		super(PersonnelData.class, rightCheck);

		this.jurisdictionCheck = jurisdictionCheck;
	}

	@Override
	public boolean hasRight(Field field) {
		UserRight userRight = jurisdictionCheck.isInJurisdiction(field)
				? UserRight.SEE_SENSITIVE_DATA_IN_JURISDICTION
				: UserRight.SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION;

		return rightCheck.check(userRight);
	}

	@Override
	protected UserRight getUserRight() {
		return null;
	}

	public interface UserJurisdictionCheck {
		boolean isInJurisdiction(Field field);
	}
}
