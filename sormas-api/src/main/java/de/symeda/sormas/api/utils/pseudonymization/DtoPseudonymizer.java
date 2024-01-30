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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotBlank;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.DefaultValuePseudonymizer;

public class DtoPseudonymizer<T> {

	protected FieldAccessCheckers<T> inJurisdictionCheckers;
	protected FieldAccessCheckers<T> outsideJurisdictionCheckers;

	private final String stringValuePlaceholder;

	protected final boolean pseudonymizeMandatoryFields;

	protected DtoPseudonymizer(
		FieldAccessCheckers<T> inJurisdictionCheckers,
		FieldAccessCheckers<T> outsideJurisdictionCheckers,
		String stringValuePlaceholder,
		boolean pseudonymizeMandatoryFields) {
		this.inJurisdictionCheckers = inJurisdictionCheckers;
		this.outsideJurisdictionCheckers = outsideJurisdictionCheckers;

		this.stringValuePlaceholder = stringValuePlaceholder;
		this.pseudonymizeMandatoryFields = pseudonymizeMandatoryFields;
	}

	public void addFieldAccessChecker(FieldAccessChecker<T> inJurisdictionChecker, FieldAccessChecker<T> outsideJurisdictionChecker) {
		this.inJurisdictionCheckers.add(inJurisdictionChecker);
		this.outsideJurisdictionCheckers.add(outsideJurisdictionChecker);
	}

	public void pseudonymizeDtoCollection(
		Class<T> type,
		Collection<T> dtos,
		JurisdictionValidator<T> jurisdictionValidator,
		final CustomCollectionItemPseudonymization<T> customPseudonymization) {

		pseudonymizeDtoCollection(type, dtos, jurisdictionValidator, customPseudonymization, false);
	}

	public void pseudonymizeDtoCollection(
		Class<T> type,
		Collection<T> dtos,
		JurisdictionValidator<T> jurisdictionValidator,
		final CustomCollectionItemPseudonymization<T> customPseudonymization,
		boolean skipEmbeddedFields) {

		List<Field> pseudonymizableFieldsInJurisdiction = getPseudonymizableFields(type, true);
		List<Field> pseudonymizableFieldsOutsideJurisdiction = getPseudonymizableFields(type, false);
		List<Field> embeddedFieldsInJurisdiction = getEmbeddedFields(type, true);
		List<Field> embeddedFieldsOutsideJurisdiction = getEmbeddedFields(type, false);

		for (final T dto : dtos) {
			final boolean isInJurisdiction = jurisdictionValidator.validate(dto);
			pseudonymizeDto(
				dto,
				isInJurisdiction ? pseudonymizableFieldsInJurisdiction : pseudonymizableFieldsOutsideJurisdiction,
				isInJurisdiction ? embeddedFieldsInJurisdiction : embeddedFieldsOutsideJurisdiction,
				isInJurisdiction,
				customPseudonymization == null ? null : d -> customPseudonymization.pseudonymize(dto, isInJurisdiction),
				skipEmbeddedFields);
		}
	}

	public <X> void pseudonymizeEmbeddedDtoCollection(Class<X> type, Collection<X> dtos, boolean inJurisdiction, T rootDto) {

		List<Field> pseudonymizableFieldsInJurisdiction = getPseudonymizableFields(type, true);
		List<Field> pseudonymizableFieldsOutsideJurisdiction = getPseudonymizableFields(type, false);
		List<Field> embeddedFieldsInJurisdiction = getEmbeddedFields(type, true);
		List<Field> embeddedFieldsOutsideJurisdiction = getEmbeddedFields(type, false);

		for (final X dto : dtos) {
			doPseudonymizeDto(
				dto,
				inJurisdiction ? pseudonymizableFieldsInJurisdiction : pseudonymizableFieldsOutsideJurisdiction,
				inJurisdiction ? embeddedFieldsInJurisdiction : embeddedFieldsOutsideJurisdiction,
				inJurisdiction,
				null,
				null,
				false,
				rootDto);
		}
	}

	public <X> void pseudonymizeEmbeddedDtoCollection(
		Class<X> type,
		Collection<X> dtos,
		boolean inJurisdiction,
		final CustomCollectionItemPseudonymization<X> customPseudonymization,
		boolean skipEmbeddedFields,
		T rootDto) {

		List<Field> pseudonymizableFieldsInJurisdiction = getPseudonymizableFields(type, true);
		List<Field> pseudonymizableFieldsOutsideJurisdiction = getPseudonymizableFields(type, false);
		List<Field> embeddedFieldsInJurisdiction = getEmbeddedFields(type, true);
		List<Field> embeddedFieldsOutsideJurisdiction = getEmbeddedFields(type, false);

		for (final X dto : dtos) {
			doPseudonymizeDto(
				dto,
				inJurisdiction ? pseudonymizableFieldsInJurisdiction : pseudonymizableFieldsOutsideJurisdiction,
				inJurisdiction ? embeddedFieldsInJurisdiction : embeddedFieldsOutsideJurisdiction,
				inJurisdiction,
				null,
				customPseudonymization == null ? null : d -> customPseudonymization.pseudonymize(dto, inJurisdiction),
				skipEmbeddedFields,
				rootDto);
		}
	}

	public void pseudonymizeDto(Class<T> type, T dto, boolean isInJurisdiction, CustomPseudonymization<T> customPseudonymization) {
		List<Field> declaredFields = getPseudonymizableFields(type, isInJurisdiction);
		List<Field> embeddedFields = getEmbeddedFields(type, isInJurisdiction);

		pseudonymizeDto(dto, declaredFields, embeddedFields, isInJurisdiction, customPseudonymization, false);
	}

	public boolean isAccessible(Class<T> type, String fieldName, T dto, boolean isInJurisdiction) {
		List<Field> pseudonymizableFields = getPseudonymizableFields(type, isInJurisdiction);
		for (Field field : pseudonymizableFields) {
			if (fieldName.equals(field.getName())) {
				return getFieldAccessCheckers(isInJurisdiction).isAccessible(field, dto, pseudonymizeMandatoryFields);
			}
		}
		throw new IllegalArgumentException("Could not find field: " + fieldName);
	}

	public void restorePseudonymizedValues(Class<T> type, T dto, T originalDto, boolean isInJurisdiction) {
		doRestorePseudonymizedValues(type, dto, originalDto, dto, isInJurisdiction);
	}

	public <X> void restoreEmbeddedPseudonymizedValues(Class<X> type, X dto, X originalDto, T rootDto, boolean isInJurisdiction) {
		doRestorePseudonymizedValues(type, dto, originalDto, rootDto, isInJurisdiction);
	}

	private <X> void doRestorePseudonymizedValues(Class<X> type, X dto, X originalDto, T rootDto, boolean isInJurisdiction) {
		if (dto == null || originalDto == null) {
			return;
		}

		List<Field> pseudonymizableFields = getPseudonymizableFields(type, isInJurisdiction);
		List<Field> embeddedFields = getEmbeddedFields(type, isInJurisdiction);

		for (Field pseudonymizedField : pseudonymizableFields) {
			try {
				if (!getFieldAccessCheckers(isInJurisdiction).isAccessible(pseudonymizedField, rootDto, pseudonymizeMandatoryFields)
					|| isPseudonymized(dto) && isFieldValuePseudonymized(pseudonymizedField, rootDto)) {
					restoreOriginalValue(dto, pseudonymizedField, originalDto);
				}
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		for (Field embeddedField : embeddedFields) {
			Class<?> fieldType = embeddedField.getType();

			if (Pseudonymizable.class.isAssignableFrom(fieldType)) {
				boolean isAccessible = embeddedField.isAccessible();
				try {
					embeddedField.setAccessible(true);

					doRestorePseudonymizedValues(
						(Class<Pseudonymizable>) fieldType,
						(Pseudonymizable) embeddedField.get(dto),
						(Pseudonymizable) embeddedField.get(originalDto),
						rootDto,
						isInJurisdiction);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Couldn't restore embedded field " + dto.getClass().getName() + "." + embeddedField.getName(), e);
				} finally {
					embeddedField.setAccessible(isAccessible);
				}
			}
		}
	}

	protected boolean isPseudonymized(Object dto) {
		return Pseudonymizable.class.isAssignableFrom(dto.getClass()) && ((Pseudonymizable) dto).isPseudonymized();
	}

	private boolean isFieldValuePseudonymized(Field pseudonymizedField, T dto)
		throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

		ValuePseudonymizer<Object> pseudonymizer = (ValuePseudonymizer<Object>) getPseudonymizer(pseudonymizedField, null);

		boolean accessible = pseudonymizedField.isAccessible();
		pseudonymizedField.setAccessible(true);

		Object fieldValue = pseudonymizedField.get(dto);

		pseudonymizedField.setAccessible(accessible);

		return pseudonymizer.isValuePseudonymized(fieldValue);
	}

	private void pseudonymizeDto(
		T dto,
		List<Field> pseudonymizableFields,
		List<Field> embeddedFields,
		boolean inJurisdiction,
		CustomPseudonymization<T> customPseudonymization,
		boolean skipEmbeddedFields) {
		doPseudonymizeDto(dto, pseudonymizableFields, embeddedFields, inJurisdiction, null, customPseudonymization, skipEmbeddedFields, dto);
	}

	private <X> boolean doPseudonymizeDto(
		X dto,
		List<Field> pseudonymizableFields,
		List<Field> embeddedFields,
		boolean inJurisdiction,
		Class<? extends ValuePseudonymizer<?>> defaultPseudonymizerClass,
		CustomPseudonymization<X> customPseudonymization,
		boolean skipEmbeddedFields,
		T rootDto) {
		if (dto == null) {
			return false;
		}

		boolean didPersonalOrSensitiveDataPseudonymization = false;

		for (Field field : pseudonymizableFields) {
			FieldAccessCheckers<T> fieldAccessCheckers = getFieldAccessCheckers(inJurisdiction);
			if (!fieldAccessCheckers.isAccessible(field, rootDto, pseudonymizeMandatoryFields)) {
				pseudonymizeField(dto, field, defaultPseudonymizerClass);
				// only personal and sensitive data pseudonymization needs special handling on the client side
				// other not accessible data is hidden on the client side, so just cleanup and don't mark the DTO as pseudonymized
				if (!didPersonalOrSensitiveDataPseudonymization) {
					didPersonalOrSensitiveDataPseudonymization = !fieldAccessCheckers.isAccessibleBy(
						field,
						rootDto,
						pseudonymizeMandatoryFields,
						PersonalDataFieldAccessChecker.class,
						SensitiveDataFieldAccessChecker.class);
				}
			}
		}

		if (!skipEmbeddedFields) {
			for (Field embeddedField : embeddedFields) {
				boolean accessible = embeddedField.isAccessible();
				try {

					embeddedField.setAccessible(true);
					Pseudonymizer pseudonymizerAnnotation = embeddedField.getAnnotation(Pseudonymizer.class);
					Class<? extends ValuePseudonymizer<?>> psudonomyzerClass = pseudonymizerAnnotation != null
						? (Class<? extends ValuePseudonymizer<?>>) pseudonymizerAnnotation.value()
						: defaultPseudonymizerClass;

					if (pseudonymizeEmbeddedDto(
						(Class<Object>) embeddedField.getType(),
						embeddedField.get(dto),
						inJurisdiction,
						psudonomyzerClass,
						skipEmbeddedFields,
						rootDto)) {
						didPersonalOrSensitiveDataPseudonymization = true;
					}
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
			((Pseudonymizable) dto).setPseudonymized(didPersonalOrSensitiveDataPseudonymization);
			((Pseudonymizable) dto).setInJurisdiction(inJurisdiction);
		}

		if (customPseudonymization != null) {
			customPseudonymization.pseudonymize(dto);
		}

		return didPersonalOrSensitiveDataPseudonymization;
	}

	public <X> void pseudonymizeEmbeddedDto(Class<X> type, X dto, boolean isInJurisdiction, T rootDto) {
		pseudonymizeEmbeddedDto(type, dto, isInJurisdiction, rootDto, null);
	}

	public <X> void pseudonymizeEmbeddedDto(Class<X> type, X dto, boolean isInJurisdiction, T rootDto, CustomPseudonymization<X> customPseudonymization) {
		List<Field> declaredFields = getPseudonymizableFields(type, isInJurisdiction);
		List<Field> embeddedFields = getEmbeddedFields(type, isInJurisdiction);

		doPseudonymizeDto(dto, declaredFields, embeddedFields, isInJurisdiction, null, customPseudonymization, false, rootDto);
	}

	private <X> boolean pseudonymizeEmbeddedDto(
		Class<X> type,
		X dto,
		boolean isInJurisdiction,
		Class<? extends ValuePseudonymizer<?>> defaultPseudonymizerClass,
		boolean skipEmbeddedFields,
		T rootDto) {
		List<Field> declaredFields = getPseudonymizableFields(type, isInJurisdiction);
		List<Field> embeddedFields = getEmbeddedFields(type, isInJurisdiction);

		return doPseudonymizeDto(dto, declaredFields, embeddedFields, isInJurisdiction, defaultPseudonymizerClass, null, skipEmbeddedFields, rootDto);
	}

	private <X> void pseudonymizeField(X dto, Field field, Class<? extends ValuePseudonymizer<?>> pseudonymizerClass) {

		try {
			field.setAccessible(true);

			if (field.getAnnotation(NotBlank.class) != null && String.class.isAssignableFrom(field.getType())) {
				field.set(dto, I18nProperties.getCaption(Captions.inaccessibleValue));
			} else {
				ValuePseudonymizer<?> pseudonymizer = getPseudonymizer(field, pseudonymizerClass);
				Object emptyValue = pseudonymizer.pseudonymize(field.get(dto));
				field.set(dto, emptyValue);
			}

		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		} finally {
			field.setAccessible(false);
		}
	}

	private ValuePseudonymizer<?> getPseudonymizer(Field field, Class<? extends ValuePseudonymizer<?>> defaultPseudonymizerClass)
		throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		Pseudonymizer pseudonymizerAnnotation = field.getAnnotation(Pseudonymizer.class);

		if (pseudonymizerAnnotation == null) {
			if (defaultPseudonymizerClass != null) {
				return defaultPseudonymizerClass.getDeclaredConstructor().newInstance();
			}

			return new DefaultValuePseudonymizer<>(stringValuePlaceholder);
		}

		return pseudonymizerAnnotation.value().getDeclaredConstructor().newInstance();
	}

	private <X> void restoreOriginalValue(X dto, Field field, X originalDto) {

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
		final FieldAccessCheckers<T> fieldAccessCheckers = getFieldAccessCheckers(inJurisdiction);

		return filterFields(type, field -> fieldAccessCheckers.isConfiguredForCheck(field, pseudonymizeMandatoryFields));
	}

	private List<Field> getEmbeddedFields(Class<?> type, boolean inJurisdiction) {
		final FieldAccessCheckers<T> fieldAccessCheckers = getFieldAccessCheckers(inJurisdiction);

		return filterFields(type, fieldAccessCheckers::isEmbedded);
	}

	private static List<Field> filterFields(Class<?> type, FieldFilter filter) {
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

	protected FieldAccessCheckers<T> getFieldAccessCheckers(boolean inJurisdiction) {
		return inJurisdiction ? inJurisdictionCheckers : outsideJurisdictionCheckers;
	}

	public interface RightCheck {

		boolean hasRight(UserRight userRight);
	}

	public interface JurisdictionValidator<X> {

		boolean validate(X dto);
	}

	public interface CustomCollectionItemPseudonymization<X> {

		void pseudonymize(X dto, boolean isInJurisdiction);
	}

	public interface CustomPseudonymization<X> {

		void pseudonymize(X dto);
	}

	private interface FieldFilter {

		boolean apply(Field field);
	}

}
