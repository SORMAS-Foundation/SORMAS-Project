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
package de.symeda.sormas.backend.util;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class PseudonymizationService {

	@EJB
	private UserService userService;

//	public <DTO> void pseudonymizeDtoCollection(
//		Class<DTO> type,
//		Collection<DTO> dtos,
//		Function<DTO, Boolean> jurisdictionValidator,
//		CustomPseudonymization<DTO> customPseudonymization) {
//
//		List<Field> declaredFields = getDeclaredFields(type);
//
//		dtos.forEach(dto -> {
//			Boolean isInJurisdiction = jurisdictionValidator.apply(dto);
//			pseudonymizeDto(
//				dto,
//				declaredFields,
//				isInJurisdiction,
//				customPseudonymization == null ? null : d -> customPseudonymization.pseudonymize(dto, isInJurisdiction));
//		});
//	}

//	public <DTO> void pseudonymizeDto(Class<DTO> type, DTO dto, boolean isInJurisdiction, Consumer<DTO> customPseudonymization) {
//		List<Field> declaredFields = getDeclaredFields(type);
//
//		pseudonymizeDto(dto, declaredFields, isInJurisdiction, customPseudonymization);
//	}

//	public <DTO extends PseudonymizableDto> void restorePseudonymizedValues(Class<DTO> type, DTO dto, DTO originalDto, boolean isInJurisdiction) {
//		FieldAccessCheckers accessCheckers = createFieldAccessCheckers(isInJurisdiction);
//
//		if (accessCheckers.hasRights() && !dto.isPseudonymized()) {
//			return;
//		}
//
//		List<Field> declaredFields = getDeclaredFields(type);
//
//		declaredFields.forEach(field -> {
//			if (accessCheckers.isConfiguredForCheck(field)) {
//				if (!accessCheckers.isAccessible(field) || dto.isPseudonymized()) {
//					restoreOriginalValue(dto, field, originalDto);
//				}
//			}
//		});
//	}

//	private <DTO> void pseudonymizeDto(DTO dto, List<Field> declaredFields, boolean isInJurisdiction, Consumer<DTO> customPseudonymization) {
//		FieldAccessCheckers accessCheckers = createFieldAccessCheckers(isInJurisdiction);
//
//		if (accessCheckers.hasRights()) {
//			return;
//		}
//
//		declaredFields.forEach(field -> {
//			if (!accessCheckers.isAccessible(field)) {
//				pseudonymizeField(dto, field);
//			}
//		});
//
//		if (PseudonymizableDto.class.isAssignableFrom(dto.getClass())) {
//			((PseudonymizableDto) dto).setPseudonymized(true);
//		}
//
//		if (customPseudonymization != null) {
//			customPseudonymization.accept(dto);
//		}
//	}

//	private FieldAccessCheckers createFieldAccessCheckers(boolean isInJurisdiction) {
//		return new FieldAccessCheckers().add(new PersonalDataFieldAccessChecker(r -> userService.hasRight(r), isInJurisdiction));
//	}

//	private <DTO> void pseudonymizeField(DTO dto, Field field) {
//
//		try {
//			Object emptyValue = field.getType().equals(String.class) ? "" : null;
//			field.setAccessible(true);
//			field.set(dto, emptyValue);
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException(e);
//		} finally {
//			field.setAccessible(false);
//		}
//	}
//
//	private <DTO extends PseudonymizableDto> void restoreOriginalValue(DTO dto, Field field, DTO originalDto) {
//
//		try {
//			field.setAccessible(true);
//			Object originalValue = field.get(originalDto);
//			field.set(dto, originalValue);
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException(e);
//		} finally {
//			field.setAccessible(false);
//		}
//	}
//
//	private List<Field> getDeclaredFields(Class<?> type) {
//
//		ArrayList<Field> declaredFields = new ArrayList<>(Arrays.asList(type.getDeclaredFields()));
//
//		if (type.getSuperclass() != null) {
//			declaredFields.addAll(getDeclaredFields(type.getSuperclass()));
//		}
//
//		return declaredFields;
//	}
//
//	public interface CustomPseudonymization<DTO> {
//
//		void pseudonymize(DTO dto, boolean isInJurisdiction);
//	}
}
