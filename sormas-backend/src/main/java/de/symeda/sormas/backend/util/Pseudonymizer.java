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

import java.util.Arrays;
import java.util.function.Consumer;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldaccess.checkers.AnnotationBasedFieldAccessChecker.SpecialAccessCheck;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.api.utils.pseudonymization.DtoPseudonymizer;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

public final class Pseudonymizer<T> extends DtoPseudonymizer<T> {

	public static <T> Pseudonymizer<T> getDefault(UserService userService) {
		return getDefault(userService::hasRight, noopSpecialAccessCheck());
	}

	public static <T> Pseudonymizer<T> getDefault(UserService userService, SpecialAccessCheck<T> specialAccessCheck) {
		return getDefault(userService::hasRight, specialAccessCheck);
	}

	public static <T> Pseudonymizer<T> getDefault(UserService userService, SpecialAccessCheck<T> specialAccessCheck, String stringValuePlaceholder) {
		return getDefault(userService::hasRight, specialAccessCheck, stringValuePlaceholder);
	}

	public static <T> Pseudonymizer<T> getDefault(UserService userService, String stringValuePlaceholder) {
		return getDefault(userService::hasRight, noopSpecialAccessCheck(), stringValuePlaceholder);
	}

	private static <T> Pseudonymizer<T> getDefault(RightCheck rightCheck, SpecialAccessCheck<T> specialAccessCheck) {
		return new Pseudonymizer<>(
			createDefaultFieldAccessCheckers(true, rightCheck, specialAccessCheck),
			createDefaultFieldAccessCheckers(false, rightCheck, specialAccessCheck),
			"",
			true);
	}

	private static <T> Pseudonymizer<T> getDefault(RightCheck rightCheck, SpecialAccessCheck<T> specialAccessCheck, String stringValuePlaceholder) {
		return new Pseudonymizer<>(
			createDefaultFieldAccessCheckers(true, rightCheck, specialAccessCheck),
			createDefaultFieldAccessCheckers(false, rightCheck, specialAccessCheck),
			stringValuePlaceholder,
			true);
	}

	public static <T> Pseudonymizer<T> getDefaultWithPlaceHolder(UserService userService) {
		return getDefaultWithPlaceHolder(userService, noopSpecialAccessCheck());
	}

	public static <T> Pseudonymizer<T> getDefaultWithPlaceHolder(UserService userService, SpecialAccessCheck<T> specialAccessCheck) {
		return getDefaultWithPlaceHolder(userService::hasRight, specialAccessCheck);
	}

	private static <T> Pseudonymizer<T> getDefaultWithPlaceHolder(RightCheck rightCheck, SpecialAccessCheck<T> specialAccessCheck) {
		return getDefault(rightCheck, specialAccessCheck, I18nProperties.getCaption(Captions.inaccessibleValue));
	}

	public static <T> Pseudonymizer<T> getDefaultNoCheckers(boolean pseudonymizeMandatoryFields) {
		return new Pseudonymizer<>(new FieldAccessCheckers<>(), new FieldAccessCheckers<>(), "", pseudonymizeMandatoryFields);
	}

	private static <T> SpecialAccessCheck<T> noopSpecialAccessCheck() {
		return t -> false;
	}

	private Pseudonymizer(
		FieldAccessCheckers<T> inJurisdictionCheckers,
		FieldAccessCheckers<T> outsideJurisdictionCheckers,
		String stringValuePlaceholder,
		boolean pseudonymizeMandatoryFields) {
		super(inJurisdictionCheckers, outsideJurisdictionCheckers, stringValuePlaceholder, pseudonymizeMandatoryFields);
	}

	private SensitiveDataFieldAccessChecker<T> getSensitiveDataFieldAccessChecker(boolean inJurisdiction) {
		//noinspection unchecked
		return getFieldAccessCheckers(inJurisdiction).getCheckerByType(SensitiveDataFieldAccessChecker.class);
	}

	public boolean pseudonymizeUser(User dtoUser, User currentUser, Consumer<UserReferenceDto> setPseudonymizedValue, T dto) {
		boolean isInJurisdiction = dtoUser == null || isUserInJurisdiction(dtoUser, currentUser);

		return pseudonymizeUser(isInJurisdiction, setPseudonymizedValue, dto);
	}

	public boolean pseudonymizeUser(boolean isUserInJurisdiction, Consumer<UserReferenceDto> setPseudonymizedValue, T dto) {

		SensitiveDataFieldAccessChecker<T> sensitiveDataFieldAccessChecker = getSensitiveDataFieldAccessChecker(isUserInJurisdiction);
		boolean isConfiguredToCheck = sensitiveDataFieldAccessChecker != null && pseudonymizeMandatoryFields;

		if (isConfiguredToCheck && !sensitiveDataFieldAccessChecker.hasRight(dto)) {
			setPseudonymizedValue.accept(null);

			return true;
		}

		return false;
	}

	public void restoreUser(User originalDtoUser, User currentUser, T dto, Consumer<UserReferenceDto> setPseudonymizedValue) {

		boolean isInJurisdiction = originalDtoUser == null || isUserInJurisdiction(originalDtoUser, currentUser);

		SensitiveDataFieldAccessChecker<T> sensitiveDataFieldAccessChecker = getSensitiveDataFieldAccessChecker(isInJurisdiction);
		if (sensitiveDataFieldAccessChecker != null && !sensitiveDataFieldAccessChecker.hasRight(dto) || isPseudonymized(dto)) {
			setPseudonymizedValue.accept(originalDtoUser != null ? originalDtoUser.toReference() : null);
		}
	}

	private static boolean isUserInJurisdiction(User user, User currentUser) {

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

	private static <T> FieldAccessCheckers<T> createDefaultFieldAccessCheckers(
		boolean inJurisdiction,
		final RightCheck rightCheck,
		SpecialAccessCheck<T> specialAccessCheck) {
		PersonalDataFieldAccessChecker<T> personalFieldAccessChecker = inJurisdiction
			? PersonalDataFieldAccessChecker.inJurisdiction(rightCheck::hasRight, specialAccessCheck)
			: PersonalDataFieldAccessChecker.outsideJurisdiction(rightCheck::hasRight, specialAccessCheck);
		SensitiveDataFieldAccessChecker<T> sensitiveFieldAccessChecker = inJurisdiction
			? SensitiveDataFieldAccessChecker.inJurisdiction(rightCheck::hasRight, specialAccessCheck)
			: SensitiveDataFieldAccessChecker.outsideJurisdiction(rightCheck::hasRight, specialAccessCheck);

		return FieldAccessCheckers.withCheckers(Arrays.asList(personalFieldAccessChecker, sensitiveFieldAccessChecker));
	}
}
