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

package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.utils.fieldaccess.FieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;

public class UiFieldAccessCheckers {

	private final boolean isInJurisdiction;
	private final FieldAccessCheckers fieldAccessCheckers;

	public UiFieldAccessCheckers(boolean isInJurisdiction) {
		this.isInJurisdiction = isInJurisdiction;
		fieldAccessCheckers = new FieldAccessCheckers();
	}

	public boolean isAccessible(Class<?> parentType, String fieldName) {
		return fieldAccessCheckers.isAccessible(parentType, fieldName, isInJurisdiction);
	}

	public UiFieldAccessCheckers add(FieldAccessChecker accessChecker) {
		fieldAccessCheckers.add(accessChecker);

		return this;
	}

	public static UiFieldAccessCheckers withCheckers(boolean isInJurisdiction, FieldAccessChecker... checkers) {
		UiFieldAccessCheckers fieldAccessCheckers = new UiFieldAccessCheckers(isInJurisdiction);

		for (FieldAccessChecker checker : checkers) {
			fieldAccessCheckers.add(checker);
		}

		return fieldAccessCheckers;
	}
}
