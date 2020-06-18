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
package de.symeda.sormas.api.utils.fieldaccess.checkers;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.PersonalData;

public class PersonalDataFieldAccessChecker extends RightBasedFieldAccessChecker {

	private PersonalDataFieldAccessChecker(final boolean hasRightInJurisdiction, final boolean harRightOutsideJurisdiction) {
		super(PersonalData.class, new RightBasedFieldAccessChecker.RightCheck() {

			@Override
			public boolean check(boolean inJurisdiction) {
				return inJurisdiction ? hasRightInJurisdiction : harRightOutsideJurisdiction;
			}
		});
	}

	public static PersonalDataFieldAccessChecker create(RightCheck rightCheck) {
		return new PersonalDataFieldAccessChecker(
			rightCheck.check(UserRight.SEE_PERSONAL_DATA_IN_JURISDICTION),
			rightCheck.check(UserRight.SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION));
	}

	public interface RightCheck {

		boolean check(UserRight userRight);
	}
}
