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

package de.symeda.sormas.api.utils.pseudonymization;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.symeda.sormas.api.PseudonymizableDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.DefaultValuePseudonymizer;

public class DtoPseudonymizer {

	private final FieldAccessCheckers fieldAccessCheckers;
	protected final SensitiveDataFieldAccessChecker sensitiveDataFieldAccessChecker;

	public DtoPseudonymizer(final RightCheck rightCheck) {
		sensitiveDataFieldAccessChecker = SensitiveDataFieldAccessChecker.create(new SensitiveDataFieldAccessChecker.RightCheck() {

			@Override
			public boolean check(UserRight userRight) {
				return rightCheck.hasRight(userRight);
			}
		});

		this.fieldAccessCheckers = createFieldAccessCheckers(rightCheck).add(sensitiveDataFieldAccessChecker);
	}

	public <DTO> void pseudonymizeDtoCollection(
		Class<DTO> type,
		Collection<DTO> dtos,
		JurisdictionValidator<DTO> jurisdictionValidator,
		final CustomCollectionItemPseudonymization<DTO> customPseudonymization) {

		List<Field> declaredFields = getPseudonymizableFields(type);

		for (final DTO dto : dtos) {
			final boolean isInJurisdiction = jurisdictionValidator.validate(dto);
			pseudonymizeDto(dto, declaredFields, isInJurisdiction, customPseudonymization == null ? null : new CustomPseudonymization<DTO>() {

				@Override
				public void pseudonymize(DTO d) {
					customPseudonymization.pseudonymize(dto, isInJurisdiction);
				}
			});
		}
	}

	public <DTO> void pseudonymizeDto(Class<DTO> type, DTO dto, boolean isInJurisdiction, CustomPseudonymization<DTO> customPseudonymization) {
		List<Field> declaredFields = getPseudonymizableFields(type);

		pseudonymizeDto(dto, declaredFields, isInJurisdiction, customPseudonymization);
	}

	public <DTO extends PseudonymizableDto> void restorePseudonymizedValues(Class<DTO> type, DTO dto, DTO originalDto, boolean isInJurisdiction) {
		List<Field> pseudonymizableFields = getPseudonymizableFields(type);

		for (Field field : pseudonymizableFields) {
			if (fieldAccessCheckers.isConfiguredForCheck(field)) {
				if (!fieldAccessCheckers.isAccessible(field, isInJurisdiction) || dto.isPseudonymized()) {
					restoreOriginalValue(dto, field, originalDto);
				}
			}
		}
	}

	private <DTO> void pseudonymizeDto(DTO dto, List<Field> fields, boolean isInJurisdiction, CustomPseudonymization<DTO> customPseudonymization) {

		boolean didPseudonymization = false;

		for (Field field : fields) {
			if (!fieldAccessCheckers.isAccessible(field, isInJurisdiction)) {
				pseudonymizeField(dto, field);
				didPseudonymization = true;
			}
		}

		if (PseudonymizableDto.class.isAssignableFrom(dto.getClass())) {
			((PseudonymizableDto) dto).setPseudonymized(didPseudonymization);
		}

		if (customPseudonymization != null) {
			customPseudonymization.pseudonymize(dto);
		}
	}

	private FieldAccessCheckers createFieldAccessCheckers(final RightCheck rightCheck) {
		return FieldAccessCheckers.withCheckers(PersonalDataFieldAccessChecker.create(new PersonalDataFieldAccessChecker.RightCheck() {

			@Override
			public boolean check(UserRight userRight) {
				return rightCheck.hasRight(userRight);
			}
		}));
	}

	private <DTO> void pseudonymizeField(DTO dto, Field field) {

		try {
			field.setAccessible(true);

			ValuePseudonymizer<?> pseudonymizer = getPseudonymizer(field);
			Object emptyValue = pseudonymizer.pseudonymize(field.get(dto));
			field.set(dto, emptyValue);
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		} finally {
			field.setAccessible(false);
		}
	}

	private ValuePseudonymizer<?> getPseudonymizer(Field field) throws IllegalAccessException, InstantiationException {
		Pseudonymizer pseudonymizerAnnotation = field.getAnnotation(Pseudonymizer.class);

		if (pseudonymizerAnnotation == null) {
			return new DefaultValuePseudonymizer<>();
		}

		return pseudonymizerAnnotation.value().newInstance();
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

	private List<Field> getPseudonymizableFields(Class<?> type) {

		List<Field> declaredFields = new ArrayList<>();

		for (Field field : type.getDeclaredFields()) {
			if (fieldAccessCheckers.isConfiguredForCheck(field)) {
				declaredFields.add(field);
			}
		}

		if (type.getSuperclass() != null) {
			declaredFields.addAll(getPseudonymizableFields(type.getSuperclass()));
		}

		return declaredFields;
	}

	public interface RightCheck {

		boolean hasRight(UserRight userRight);
	}

	public interface JurisdictionValidator<DTO> {

		boolean validate(DTO dto);
	}

	public interface CustomCollectionItemPseudonymization<DTO> {

		void pseudonymize(DTO dto, boolean isInJurisdiction);
	}

	public interface CustomPseudonymization<DTO> {

		void pseudonymize(DTO dto);
	}
}
