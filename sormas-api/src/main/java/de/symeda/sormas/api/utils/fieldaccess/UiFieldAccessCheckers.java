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

import de.symeda.sormas.api.utils.fieldaccess.checkers.PseudonymizedFieldAccessChecker;

public class UiFieldAccessCheckers {

	private final FieldAccessCheckers fieldAccessCheckers;

	private UiFieldAccessCheckers() {
		fieldAccessCheckers = new FieldAccessCheckers();
	}

	public boolean isAccessible(Class<?> parentType, String fieldName) {
		return fieldAccessCheckers.isAccessible(parentType, fieldName, true);
	}

	public boolean isEmbedded(Class<?> parentType, String fieldName) {
		return fieldAccessCheckers.isEmbedded(parentType, fieldName);
	}

	public boolean hasRight() {
		return fieldAccessCheckers.hasRights();
	}

	public UiFieldAccessCheckers add(FieldAccessChecker accessChecker) {
		fieldAccessCheckers.add(accessChecker);

		return this;
	}

	public static UiFieldAccessCheckers getNoop() {
		return new UiFieldAccessCheckers();
	}

	public static UiFieldAccessCheckers getDefault(boolean isPseudonymized) {
		UiFieldAccessCheckers fieldAccessCheckers = new UiFieldAccessCheckers();

		fieldAccessCheckers.add(PseudonymizedFieldAccessChecker.forPersonalData(isPseudonymized));
		fieldAccessCheckers.add(PseudonymizedFieldAccessChecker.forSensitiveData(isPseudonymized));

		return fieldAccessCheckers;
	}

	public static UiFieldAccessCheckers forSensitiveData(boolean isPseudonymized) {
		UiFieldAccessCheckers fieldAccessCheckers = new UiFieldAccessCheckers();

		fieldAccessCheckers.add(PseudonymizedFieldAccessChecker.forSensitiveData(isPseudonymized));

		return fieldAccessCheckers;
	}
}
