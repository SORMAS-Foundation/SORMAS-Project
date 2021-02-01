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

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.DefaultValuePseudonymizer;

public class DtoPseudonymizer {

	protected FieldAccessCheckers inJurisdictionCheckers;
	protected FieldAccessCheckers outsideJurisdictionCheckers;

	private final String stringValuePlaceholder;

	private final boolean pseudonymizeMandatoryFields;

	protected DtoPseudonymizer(
		FieldAccessCheckers inJurisdictionCheckers,
		FieldAccessCheckers outsideJurisdictionCheckers,
		String stringValuePlaceholder,
		boolean pseudonymizeMandatoryFields) {
		this.inJurisdictionCheckers = inJurisdictionCheckers;
		this.outsideJurisdictionCheckers = outsideJurisdictionCheckers;

		this.stringValuePlaceholder = stringValuePlaceholder;
		this.pseudonymizeMandatoryFields = pseudonymizeMandatoryFields;
	}

	public void addFieldAccessChecker(FieldAccessChecker inJurisdictionChecker, FieldAccessChecker outsideJurisdictionChecker) {
		this.inJurisdictionCheckers.add(inJurisdictionChecker);
		this.outsideJurisdictionCheckers.add(outsideJurisdictionChecker);
	}

	public <DTO> void pseudonymizeDtoCollection(
		Class<DTO> type,
		Collection<DTO> dtos,
		JurisdictionValidator<DTO> jurisdictionValidator,
		final CustomCollectionItemPseudonymization<DTO> customPseudonymization) {

		pseudonymizeDtoCollection(type, dtos, jurisdictionValidator, customPseudonymization, false);
	}

	public <DTO> void pseudonymizeDtoCollection(
		Class<DTO> type,
		Collection<DTO> dtos,
		JurisdictionValidator<DTO> jurisdictionValidator,
		final CustomCollectionItemPseudonymization<DTO> customPseudonymization,
		boolean skipEmbeddedFields) {

		List<Field> pseudonymizableFieldsInJurisdiction = getPseudonymizableFields(type, true);
		List<Field> pseudonymizableFieldsOutsideJurisdiction = getPseudonymizableFields(type, false);
		List<Field> embeddedFieldsInJurisdiction = getEmbeddedFields(type, true);
		List<Field> embeddedFieldsOutsideJurisdiction = getEmbeddedFields(type, false);

		for (final DTO dto : dtos) {
			final boolean isInJurisdiction = jurisdictionValidator.validate(dto);
			pseudonymizeDto(
				dto,
				isInJurisdiction ? pseudonymizableFieldsInJurisdiction : pseudonymizableFieldsOutsideJurisdiction,
				isInJurisdiction ? embeddedFieldsInJurisdiction : embeddedFieldsOutsideJurisdiction,
				isInJurisdiction,
				null,
				customPseudonymization == null ? null : new CustomPseudonymization<DTO>() {

					@Override
					public void pseudonymize(DTO d) {
						customPseudonymization.pseudonymize(dto, isInJurisdiction);
					}
				},
				skipEmbeddedFields);
		}
	}

	public <DTO> void pseudonymizeDto(Class<DTO> type, DTO dto, boolean isInJurisdiction, CustomPseudonymization<DTO> customPseudonymization) {
		List<Field> declaredFields = getPseudonymizableFields(type, isInJurisdiction);
		List<Field> embeddedFields = getEmbeddedFields(type, isInJurisdiction);

		pseudonymizeDto(dto, declaredFields, embeddedFields, isInJurisdiction, null, customPseudonymization, false);
	}

	public <DTO extends Pseudonymizable> void restorePseudonymizedValues(Class<DTO> type, DTO dto, DTO originalDto, boolean isInJurisdiction) {
		if (originalDto == null) {
			return;
		}

		List<Field> pseudonymizableFields = getPseudonymizableFields(type, isInJurisdiction);
		List<Field> embeddedFields = getEmbeddedFields(type, isInJurisdiction);

		for (Field pseudonymizedField : pseudonymizableFields) {
			if (!getFieldAccessCheckers(isInJurisdiction).isAccessible(pseudonymizedField, pseudonymizeMandatoryFields) || dto.isPseudonymized()) {
				restoreOriginalValue(dto, pseudonymizedField, originalDto);
			}
		}
		for (Field embeddedField : embeddedFields) {
			Class<?> fieldType = embeddedField.getType();

			if (Pseudonymizable.class.isAssignableFrom(fieldType)) {
				boolean isAccessible = embeddedField.isAccessible();
				try {
					embeddedField.setAccessible(true);

					restorePseudonymizedValues(
						(Class<Pseudonymizable>) fieldType,
						(Pseudonymizable) embeddedField.get(dto),
						(Pseudonymizable) embeddedField.get(originalDto),
						isInJurisdiction);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Couldn't restore embedded field " + dto.getClass().getName() + "." + embeddedField.getName(), e);
				} finally {
					embeddedField.setAccessible(isAccessible);
				}
			}
		}
	}

	private <DTO> boolean pseudonymizeDto(
		Class<DTO> type,
		DTO dto,
		boolean isInJurisdiction,
		Class<? extends ValuePseudonymizer> defaultPseudonymizerClass,
		CustomPseudonymization<DTO> customPseudonymization,
		boolean skipEmbeddedFields) {
		List<Field> declaredFields = getPseudonymizableFields(type, isInJurisdiction);
		List<Field> embeddedFields = getEmbeddedFields(type, isInJurisdiction);

		return pseudonymizeDto(
			dto,
			declaredFields,
			embeddedFields,
			isInJurisdiction,
			defaultPseudonymizerClass,
			customPseudonymization,
			skipEmbeddedFields);
	}

	private <DTO> boolean pseudonymizeDto(
		DTO dto,
		List<Field> pseudonymizableFields,
		List<Field> embeddedFields,
		boolean inJurisdiction,
		Class<? extends ValuePseudonymizer> defaultPseudonymizerClass,
		CustomPseudonymization<DTO> customPseudonymization,
		boolean skipEmbeddedFields) {
		if (dto == null) {
			return false;
		}

		boolean didPseudonymization = false;

		for (Field field : pseudonymizableFields) {
			if (!getFieldAccessCheckers(inJurisdiction).isAccessible(field, pseudonymizeMandatoryFields)) {
				pseudonymizeField(dto, field, defaultPseudonymizerClass);
				didPseudonymization = true;
			}
		}

		if (!skipEmbeddedFields) {
			for (Field embeddedField : embeddedFields) {
				boolean accessible = embeddedField.isAccessible();
				try {

					embeddedField.setAccessible(true);
					Pseudonymizer pseudonymizerAnnotation = embeddedField.getAnnotation(Pseudonymizer.class);
					Class<? extends ValuePseudonymizer> psudonomyzerClass =
						pseudonymizerAnnotation != null ? pseudonymizerAnnotation.value() : defaultPseudonymizerClass;

					didPseudonymization = pseudonymizeDto(
						(Class<Object>) embeddedField.getType(),
						embeddedField.get(dto),
						inJurisdiction,
						psudonomyzerClass,
						null,
						skipEmbeddedFields);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(
						"Failed to pseudonymize embedded field " + dto.getClass().getName() + "." + embeddedField.getName(),
						e);
				} finally {
					embeddedField.setAccessible(accessible);
				}
			}
		}

		if (Pseudonymizable.class.isAssignableFrom(dto.getClass())) {
			((Pseudonymizable) dto).setPseudonymized(didPseudonymization);
		}

		if (customPseudonymization != null) {
			customPseudonymization.pseudonymize(dto);
		}

		return didPseudonymization;
	}

	private <DTO> void pseudonymizeField(DTO dto, Field field, Class<? extends ValuePseudonymizer> pseudonymizerClass) {

		try {
			field.setAccessible(true);

			ValuePseudonymizer<?> pseudonymizer = getPseudonymizer(field, pseudonymizerClass);
			Object emptyValue = pseudonymizer.pseudonymize(field.get(dto));
			field.set(dto, emptyValue);
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		} finally {
			field.setAccessible(false);
		}
	}

	private ValuePseudonymizer<?> getPseudonymizer(Field field, Class<? extends ValuePseudonymizer> defaultPseudonymizerClass)
		throws IllegalAccessException, InstantiationException {
		Pseudonymizer pseudonymizerAnnotation = field.getAnnotation(Pseudonymizer.class);

		if (pseudonymizerAnnotation == null) {
			if (defaultPseudonymizerClass != null) {
				return defaultPseudonymizerClass.newInstance();
			}

			return new DefaultValuePseudonymizer<>(stringValuePlaceholder);
		}

		return pseudonymizerAnnotation.value().newInstance();
	}

	private <DTO extends Pseudonymizable> void restoreOriginalValue(DTO dto, Field field, DTO originalDto) {

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

	private List<Field> getPseudonymizableFields(Class<?> type, boolean inJurisdiction) {
		final FieldAccessCheckers fieldAccessCheckers = getFieldAccessCheckers(inJurisdiction);

		return filterFields(type, new FieldFilter() {

			@Override
			public boolean apply(Field field) {
				return fieldAccessCheckers.isConfiguredForCheck(field, pseudonymizeMandatoryFields);
			}
		});
	}

	private List<Field> getEmbeddedFields(Class<?> type, boolean inJurisdiction) {
		final FieldAccessCheckers fieldAccessCheckers = getFieldAccessCheckers(inJurisdiction);

		return filterFields(type, new FieldFilter() {

			@Override
			public boolean apply(Field field) {
				return fieldAccessCheckers.isEmbedded(field);
			}
		});
	}

	private List<Field> filterFields(Class<?> type, FieldFilter filter) {
		List<Field> declaredFields = new ArrayList<>();

		for (Field field : type.getDeclaredFields()) {
			if (filter.apply(field)) {
				declaredFields.add(field);
			}
		}

		if (type.getSuperclass() != null) {
			declaredFields.addAll(filterFields(type.getSuperclass(), filter));
		}

		return declaredFields;
	}

	protected FieldAccessCheckers getFieldAccessCheckers(boolean inJurisdiction) {
		return inJurisdiction ? inJurisdictionCheckers : outsideJurisdictionCheckers;
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

	private interface FieldFilter {

		boolean apply(Field field);
	}
}
