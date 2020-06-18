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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import de.symeda.sormas.api.PseudonymizableDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.backend.user.User;

public class Pseudonymizer {

	private final FieldAccessCheckers fieldAccessCheckers;
	private final SensitiveDataFieldAccessChecker sensitiveDataFieldAccessChecker;

	public Pseudonymizer(RightCheck rightCheck) {
		sensitiveDataFieldAccessChecker = SensitiveDataFieldAccessChecker.create(rightCheck::hasRight);

		this.fieldAccessCheckers = createFieldAccessCheckers(rightCheck).add(sensitiveDataFieldAccessChecker);
	}

	public <DTO> void pseudonymizeDtoCollection(
		Class<DTO> type,
		Collection<DTO> dtos,
		Function<DTO, Boolean> jurisdictionValidator,
		CustomPseudonymization<DTO> customPseudonymization) {

		List<Field> declaredFields = getDeclaredFields(type);

		dtos.forEach(dto -> {
			Boolean isInJurisdiction = jurisdictionValidator.apply(dto);
			pseudonymizeDto(
				dto,
				declaredFields,
				isInJurisdiction,
				customPseudonymization == null ? null : d -> customPseudonymization.pseudonymize(dto, isInJurisdiction));
		});
	}

	public <DTO> void pseudonymizeDto(Class<DTO> type, DTO dto, boolean isInJurisdiction, Consumer<DTO> customPseudonymization) {
		List<Field> declaredFields = getDeclaredFields(type);

		pseudonymizeDto(dto, declaredFields, isInJurisdiction, customPseudonymization);
	}

	public <DTO extends PseudonymizableDto> void restorePseudonymizedValues(Class<DTO> type, DTO dto, DTO originalDto, boolean isInJurisdiction) {
		List<Field> declaredFields = getDeclaredFields(type);

		declaredFields.forEach(field -> {
			if (fieldAccessCheckers.isConfiguredForCheck(field)) {
				if (!fieldAccessCheckers.isAccessible(field, isInJurisdiction) || dto.isPseudonymized()) {
					restoreOriginalValue(dto, field, originalDto);
				}
			}
		});
	}

	public <DTO> void pseudonymizeUser(User dtoUser, User currentUser, DTO dto, Consumer<UserReferenceDto> setPseudonymizedValue) {
		boolean isInJurisdiction = dtoUser == null || isUserInJurisdiction(dtoUser, currentUser);

		if (!sensitiveDataFieldAccessChecker.hasRight(isInJurisdiction)) {
			setPseudonymizedValue.accept(null);

			if (PseudonymizableDto.class.isAssignableFrom(dto.getClass())) {
				((PseudonymizableDto) dto).setPseudonymized(true);
			}
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

	private <DTO> void pseudonymizeDto(DTO dto, List<Field> declaredFields, boolean isInJurisdiction, Consumer<DTO> customPseudonymization) {

		AtomicBoolean didPseudonymization = new AtomicBoolean(false);
		declaredFields.forEach(field -> {
			if (!fieldAccessCheckers.isAccessible(field, isInJurisdiction)) {
				pseudonymizeField(dto, field);
				didPseudonymization.set(true);
			}
		});

		if (PseudonymizableDto.class.isAssignableFrom(dto.getClass())) {
			((PseudonymizableDto) dto).setPseudonymized(didPseudonymization.get());
		}

		if (customPseudonymization != null) {
			customPseudonymization.accept(dto);
		}
	}

	private FieldAccessCheckers createFieldAccessCheckers(RightCheck rightCheck) {
		return FieldAccessCheckers.withCheckers(PersonalDataFieldAccessChecker.create(rightCheck::hasRight));
	}

	private <DTO> void pseudonymizeField(DTO dto, Field field) {

		try {
			Object emptyValue = field.getType().equals(String.class) ? "" : null;
			field.setAccessible(true);
			field.set(dto, emptyValue);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} finally {
			field.setAccessible(false);
		}
	}

	private <DTO extends PseudonymizableDto> void restoreOriginalValue(DTO dto, Field field, DTO originalDto) {

		try {
			field.setAccessible(true);
			Object originalValue = field.get(originalDto);
			field.set(dto, originalValue);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} finally {
			field.setAccessible(false);
		}
	}

	private List<Field> getDeclaredFields(Class<?> type) {

		ArrayList<Field> declaredFields = new ArrayList<>(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null) {
			declaredFields.addAll(getDeclaredFields(type.getSuperclass()));
		}

		return declaredFields;
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

	public interface RightCheck {

		boolean hasRight(UserRight userRight);
	}

	public interface CustomPseudonymization<DTO> {

		void pseudonymize(DTO dto, boolean isInJurisdiction);
	}
}
