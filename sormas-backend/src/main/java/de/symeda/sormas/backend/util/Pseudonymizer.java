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
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.pseudonymization.DtoPseudonymizer;
import de.symeda.sormas.backend.user.User;

public class Pseudonymizer extends DtoPseudonymizer {

	public Pseudonymizer(RightCheck rightCheck) {
		super(rightCheck);
	}

	public void pseudonymizeUser(User dtoUser, User currentUser, Consumer<UserReferenceDto> setPseudonymizedValue) {
		boolean isInJurisdiction = dtoUser == null || isUserInJurisdiction(dtoUser, currentUser);

		if (!sensitiveDataFieldAccessChecker.hasRight(isInJurisdiction)) {
			setPseudonymizedValue.accept(null);
		}
	}

	public <DTO extends PseudonymizableDto> void restoreUser(
		User originalDtoUser,
		User currentUser,
		DTO dto,
		Consumer<UserReferenceDto> setPseudonymizedValue) {

		boolean isInJurisdiction = originalDtoUser == null || isUserInJurisdiction(originalDtoUser, currentUser);

		if (!sensitiveDataFieldAccessChecker.hasRight(isInJurisdiction) || dto.isPseudonymized()) {
			setPseudonymizedValue.accept(originalDtoUser != null ? originalDtoUser.toReference() : null);
		}
	}

	private boolean isUserInJurisdiction(User user, User currentUser) {

		if (currentUser.getDistrict() != null) {
			return DataHelper.isSame(currentUser.getDistrict(), user.getDistrict());
		}

		if (currentUser.getCommunity() != null) {
			return DataHelper.isSame(currentUser.getCommunity(), user.getCommunity());
		}

		if (currentUser.getHealthFacility() != null) {
			return DataHelper.isSame(currentUser.getHealthFacility(), user.getHealthFacility());
		}

		if (currentUser.getPointOfEntry() != null) {
			return DataHelper.isSame(currentUser.getPointOfEntry(), user.getPointOfEntry());
		}

		return true;
	}
}
