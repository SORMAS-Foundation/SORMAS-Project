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

import java.lang.reflect.Field;
import java.util.function.Consumer;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.api.utils.pseudonymization.DtoPseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.backend.user.User;

public class Pseudonymizer extends DtoPseudonymizer {

	public static Pseudonymizer getDefault(RightCheck rightCheck) {
		return new Pseudonymizer(createDefaultFieldAccessCheckers(true, rightCheck), createDefaultFieldAccessCheckers(false, rightCheck), "", true);
	}

	public static Pseudonymizer getDefault(RightCheck rightCheck, String stringValuePlaceholder) {
		return new Pseudonymizer(
			createDefaultFieldAccessCheckers(true, rightCheck),
			createDefaultFieldAccessCheckers(false, rightCheck),
			stringValuePlaceholder,
			true);
	}

	public static Pseudonymizer getDefaultWithInaccessibleValuePlaceHolder(RightCheck rightCheck) {
		return getDefault(rightCheck, I18nProperties.getCaption(Captions.inaccessibleValue));
	}

	public static Pseudonymizer getDefaultNoCheckers(boolean pseudonymizeMandatoryFields) {
		return new Pseudonymizer(new FieldAccessCheckers(), new FieldAccessCheckers(), "", pseudonymizeMandatoryFields);
	}

	public Pseudonymizer(
		FieldAccessCheckers inJurisdictionCheckers,
		FieldAccessCheckers outsideJurisdictionCheckers,
		String stringValuePlaceholder,
		boolean pseudonymizeMandatoryFields) {
		super(inJurisdictionCheckers, outsideJurisdictionCheckers, stringValuePlaceholder, pseudonymizeMandatoryFields);
	}

	private SensitiveDataFieldAccessChecker getSensitiveDataFieldAccessChecker(boolean inJurisdiction) {
		return getFieldAccessCheckers(inJurisdiction).getCheckerByType(SensitiveDataFieldAccessChecker.class);
	}

    public boolean pseudonymizeUser(User dtoUser, User currentUser, Consumer<UserReferenceDto> setPseudonymizedValue) {
		boolean isInJurisdiction = dtoUser == null || isUserInJurisdiction(dtoUser, currentUser);

        return pseudonymizeUser(isInJurisdiction, setPseudonymizedValue);
    }

    public boolean pseudonymizeUser(boolean isUserInJurisdiction, Consumer<UserReferenceDto> setPseudonymizedValue) {

        SensitiveDataFieldAccessChecker sensitiveDataFieldAccessChecker = getSensitiveDataFieldAccessChecker(isUserInJurisdiction);
		boolean isConfiguredToCheck = sensitiveDataFieldAccessChecker != null && pseudonymizeMandatoryFields;

		if (isConfiguredToCheck && !sensitiveDataFieldAccessChecker.hasRight()) {
			setPseudonymizedValue.accept(null);

			return true;
		}

		return false;
	}

	private Field getDtoField(Class<?> dtoClass, String dtoFieldName) {
		try {
			return dtoClass.getDeclaredField(dtoFieldName);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public <DTO extends PseudonymizableDto> void restoreUser(
		User originalDtoUser,
		User currentUser,
		DTO dto,
		Consumer<UserReferenceDto> setPseudonymizedValue) {

		boolean isInJurisdiction = originalDtoUser == null || isUserInJurisdiction(originalDtoUser, currentUser);

		SensitiveDataFieldAccessChecker sensitiveDataFieldAccessChecker = getSensitiveDataFieldAccessChecker(isInJurisdiction);
		if (sensitiveDataFieldAccessChecker != null && !sensitiveDataFieldAccessChecker.hasRight() || dto.isPseudonymized()) {
			setPseudonymizedValue.accept(originalDtoUser != null ? originalDtoUser.toReference() : null);
		}
	}

	private boolean isUserInJurisdiction(User user, User currentUser) {

		JurisdictionLevel jurisdictionLevel = user.getJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.NATION || jurisdictionLevel == JurisdictionLevel.REGION) {
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

		if (currentUser.getLaboratory() != null) {
			return DataHelper.isSame(currentUser.getLaboratory(), user.getLaboratory());
		}

		return true;
	}

	private static FieldAccessCheckers createDefaultFieldAccessCheckers(boolean inJurisdiction, final RightCheck rightCheck) {
		PersonalDataFieldAccessChecker personalFieldAccessChecker = inJurisdiction
			? PersonalDataFieldAccessChecker.inJurisdiction(rightCheck::hasRight)
			: PersonalDataFieldAccessChecker.outsideJurisdiction(rightCheck::hasRight);
		SensitiveDataFieldAccessChecker sensitiveFieldAccessChecker = inJurisdiction
			? SensitiveDataFieldAccessChecker.inJurisdiction(rightCheck::hasRight)
			: SensitiveDataFieldAccessChecker.outsideJurisdiction(rightCheck::hasRight);

		return FieldAccessCheckers.withCheckers(personalFieldAccessChecker, sensitiveFieldAccessChecker);
	}
}
