/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.util;

import java.util.function.Consumer;

import de.symeda.sormas.api.PseudonymizableDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.api.utils.pseudonymization.DtoPseudonymizer;
import de.symeda.sormas.backend.user.User;

public class Pseudonymizer extends DtoPseudonymizer {

	public static Pseudonymizer getDefault(RightCheck rightCheck) {
		return new Pseudonymizer(createDefaultFieldAccessCheckers(rightCheck), "");
	}

	public static Pseudonymizer getDefault(RightCheck rightCheck, String stringValuePlaceholder) {
		return new Pseudonymizer(createDefaultFieldAccessCheckers(rightCheck), stringValuePlaceholder);
	}

	public static Pseudonymizer empty() {
		return new Pseudonymizer(new FieldAccessCheckers(), "");
	}

	public Pseudonymizer(FieldAccessCheckers fieldAccessCheckers, String stringValuePlaceholder) {
		super(fieldAccessCheckers, stringValuePlaceholder);
	}

	private SensitiveDataFieldAccessChecker getSensitiveDataFieldAccessChecker() {
		return this.fieldAccessCheckers.getCheckerByType(SensitiveDataFieldAccessChecker.class);
	}

	public void pseudonymizeUser(User dtoUser, User currentUser, Consumer<UserReferenceDto> setPseudonymizedValue) {
		boolean isInJurisdiction = dtoUser == null || isUserInJurisdiction(dtoUser, currentUser);

		SensitiveDataFieldAccessChecker sensitiveDataFieldAccessChecker = getSensitiveDataFieldAccessChecker();
		if (sensitiveDataFieldAccessChecker != null && !sensitiveDataFieldAccessChecker.hasRight(isInJurisdiction)) {
			setPseudonymizedValue.accept(null);
		}
	}

	public <DTO extends PseudonymizableDto> void restoreUser(
		User originalDtoUser,
		User currentUser,
		DTO dto,
		Consumer<UserReferenceDto> setPseudonymizedValue) {

		boolean isInJurisdiction = originalDtoUser == null || isUserInJurisdiction(originalDtoUser, currentUser);

		SensitiveDataFieldAccessChecker sensitiveDataFieldAccessChecker = getSensitiveDataFieldAccessChecker();
		if (sensitiveDataFieldAccessChecker != null && !sensitiveDataFieldAccessChecker.hasRight(isInJurisdiction) || dto.isPseudonymized()) {
			setPseudonymizedValue.accept(originalDtoUser != null ? originalDtoUser.toReference() : null);
		}
	}

	private boolean isUserInJurisdiction(User user, User currentUser) {

		if (user.getJurisdictionLevel() == JurisdictionLevel.NATION || user.getJurisdictionLevel() == JurisdictionLevel.REGION) {
			return true;
		}

		if (currentUser.getPointOfEntry() != null) {
			return DataHelper.isSame(currentUser.getPointOfEntry(), user.getPointOfEntry());
		}

		if (currentUser.getHealthFacility() != null) {
			return DataHelper.isSame(currentUser.getHealthFacility(), user.getHealthFacility());
		}

		if (currentUser.getCommunity() != null) {
			return DataHelper.isSame(currentUser.getCommunity(), user.getCommunity());
		}

		if (currentUser.getDistrict() != null) {
			return DataHelper.isSame(currentUser.getDistrict(), user.getDistrict());
		}

		return true;
	}

	private static FieldAccessCheckers createDefaultFieldAccessCheckers(final RightCheck rightCheck) {
		return FieldAccessCheckers.withCheckers(PersonalDataFieldAccessChecker.create(new PersonalDataFieldAccessChecker.RightCheck() {

			@Override
			public boolean check(UserRight userRight) {
				return rightCheck.hasRight(userRight);
			}
		}), SensitiveDataFieldAccessChecker.create(new SensitiveDataFieldAccessChecker.RightCheck() {

			@Override
			public boolean check(UserRight userRight) {
				return rightCheck.hasRight(userRight);
			}
		}));
	}

}
