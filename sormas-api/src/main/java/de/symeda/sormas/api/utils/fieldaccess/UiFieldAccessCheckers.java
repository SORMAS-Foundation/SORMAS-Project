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

package de.symeda.sormas.api.utils.fieldaccess;

import de.symeda.sormas.api.user.PseudonymizableDataAccessLevel;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PseudonymizedFieldAccessChecker;

public final class UiFieldAccessCheckers<T> {

	private final FieldAccessCheckers<T> fieldAccessCheckers;

	private UiFieldAccessCheckers() {
		fieldAccessCheckers = new FieldAccessCheckers<>();
	}

	public boolean isAccessible(Class<T> parentType, String fieldName) {
		return fieldAccessCheckers.isAccessible(parentType, null, fieldName, true);
	}

	public boolean isEmbedded(Class<?> parentType, String fieldName) {
		return fieldAccessCheckers.isEmbedded(parentType, fieldName);
	}

	public boolean hasRight() {
		return fieldAccessCheckers.hasRights(null);
	}

	private UiFieldAccessCheckers<T> add(PseudonymizedFieldAccessChecker<T> accessChecker) {
		fieldAccessCheckers.add(accessChecker);

		return this;
	}

	public static <T> UiFieldAccessCheckers<T> getNoop() {
		return new UiFieldAccessCheckers<>();
	}

	public static <T> UiFieldAccessCheckers<T> getDefault(boolean isPseudonymized, String serverCountry) {
		UiFieldAccessCheckers<T> fieldAccessCheckers = new UiFieldAccessCheckers<>();

		fieldAccessCheckers.add(PseudonymizedFieldAccessChecker.forPersonalData(isPseudonymized, serverCountry))
			.add(PseudonymizedFieldAccessChecker.forSensitiveData(isPseudonymized, serverCountry));

		return fieldAccessCheckers;
	}

	public static <T> UiFieldAccessCheckers<T> forPersonalData(boolean isPseudonymized, String serverCountry) {
		UiFieldAccessCheckers<T> fieldAccessCheckers = new UiFieldAccessCheckers<>();

		fieldAccessCheckers.add(PseudonymizedFieldAccessChecker.forPersonalData(isPseudonymized, serverCountry));

		return fieldAccessCheckers;
	}

	public static <T> UiFieldAccessCheckers<T> forSensitiveData(boolean isPseudonymized, String serverCountry) {
		UiFieldAccessCheckers<T> fieldAccessCheckers = new UiFieldAccessCheckers<>();

		fieldAccessCheckers.add(PseudonymizedFieldAccessChecker.forSensitiveData(isPseudonymized, serverCountry));

		return fieldAccessCheckers;
	}

	public static <T> UiFieldAccessCheckers<T> forDataAccessLevel(
		PseudonymizableDataAccessLevel accessLevel,
		boolean isPseudonymized,
		String serverCountry) {

		switch (accessLevel) {
		case ALL:
		case NONE:
			return getDefault(isPseudonymized, serverCountry);
		case PERSONAL:
			return forSensitiveData(isPseudonymized, serverCountry);
		case SENSITIVE:
			return forPersonalData(isPseudonymized, serverCountry);
		default:
			throw new IllegalArgumentException(accessLevel.name());
		}
	}
}
