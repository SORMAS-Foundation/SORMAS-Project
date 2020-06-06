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
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;

import java.lang.reflect.Field;

public class PersonalDataFieldAccessChecker implements FieldAccessCheckers.Checker {
	private final RightCheck rightCheck;
	private final boolean isInJurisdiction;

	public PersonalDataFieldAccessChecker(RightCheck rightCheck, boolean isInJurisdiction) {
		this.rightCheck = rightCheck;
		this.isInJurisdiction = isInJurisdiction;
	}

	@Override
	public boolean isConfiguredForCheck(Field field) {
		return field.isAnnotationPresent(PersonalData.class);
	}

	@Override
	public boolean hasRight() {
		UserRight personalDataRight = isInJurisdiction
				? UserRight.SEE_PERSONAL_DATA_IN_JURISDICTION
				: UserRight.SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION;

		return rightCheck.check(personalDataRight);
	}

	public interface RightCheck {
		boolean check(UserRight userRight);
	}
}
