/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.pseudonymization.DtoPseudonymizer;
import de.symeda.sormas.backend.user.User;

public abstract class AssociableDtoPseudonymizer<T> {

	private final Pseudonymizer<T> rootPseudonymizer;
	private final Pseudonymizer<CaseReferenceDto> casePseudonymizer;
	private final Pseudonymizer<?> deafultAssociatedObjectPseudonymizer;

	public AssociableDtoPseudonymizer(
		Pseudonymizer<T> rootPseudonymizer,
		Pseudonymizer<CaseReferenceDto> casePseudonymizer,
		Pseudonymizer<?> deafultAssociatedObjectPseudonymizer) {
		this.rootPseudonymizer = rootPseudonymizer;
		this.casePseudonymizer = casePseudonymizer;
		this.deafultAssociatedObjectPseudonymizer = deafultAssociatedObjectPseudonymizer;
	}

	public void pseudonymizeDto(Class<T> type, T task, boolean inJurisdiction, DtoPseudonymizer.CustomPseudonymization<T> customPseudonymization) {
		rootPseudonymizer.pseudonymizeDto(type, task, inJurisdiction, customPseudonymization);
	}

	public void pseudonymizeDtoCollection(
		Class<T> type,
		Collection<T> dtos,
		DtoPseudonymizer.JurisdictionValidator<T> jurisdictionValidator,
		DtoPseudonymizer.CustomCollectionItemPseudonymization<T> customPseudonymization,
		boolean skipEmbeddedFields) {
		rootPseudonymizer.pseudonymizeDtoCollection(type, dtos, jurisdictionValidator, customPseudonymization, skipEmbeddedFields);
	}

	public void pseudonymizeCaseReference(CaseReferenceDto caze, Boolean caseInJurisdiction) {
		casePseudonymizer.pseudonymizeDto(CaseReferenceDto.class, caze, caseInJurisdiction, null);
	}

	public <X> void pseudonymizeAssociatedDto(Class<X> type, X dto, Boolean inJurisdiction) {
		//noinspection unchecked
		((Pseudonymizer<X>) deafultAssociatedObjectPseudonymizer).pseudonymizeDto(type, dto, inJurisdiction, null);
	}

	public void pseudonymizeUser(User user, User currentUser, Consumer<UserReferenceDto> setPseudonymizedValue, T sample) {
		rootPseudonymizer.pseudonymizeUser(user, currentUser, setPseudonymizedValue, sample);
	}

	public <X> void pseudonymizeEmbeddedDto(
		Class<X> type,
		X dto,
		boolean inJurisdiction,
		T rootDto,
		DtoPseudonymizer.CustomPseudonymization<X> customPseudonymization) {
		rootPseudonymizer.pseudonymizeEmbeddedDto(type, dto, inJurisdiction, rootDto, customPseudonymization);
	}

	public <X> void pseudonymizeEmbeddedDtoCollection(Class<X> type, List<X> dtos, boolean inJurisdiction, T rootDto) {
		rootPseudonymizer.pseudonymizeEmbeddedDtoCollection(type, dtos, inJurisdiction, rootDto);
	}

	public void restorePseudonymizedValues(Class<T> sampleDtoClass, T dto, T existingSampleDto, boolean inJurisdiction) {
		rootPseudonymizer.restorePseudonymizedValues(sampleDtoClass, dto, existingSampleDto, inJurisdiction);
	}

	public void restoreUser(User reportingUser, User currentUser, T dto, Consumer<UserReferenceDto> setPseudonymizedValue) {
		rootPseudonymizer.restoreUser(reportingUser, currentUser, dto, setPseudonymizedValue);
	}
}
