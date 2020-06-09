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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;

public class FieldAccessCheckers {

	private List<Checker> checkers = new ArrayList<>();

	public boolean isAccessible(Class<?> parentType, String fieldName) {

		Field declaredField = getDeclaredField(parentType, fieldName);
		if (declaredField == null) {
			return true;
		}

		return isAccessible(declaredField);
	}

	public boolean isAccessible(Field field) {

		for (Checker checker : checkers) {
			if (checker.isConfiguredForCheck(field) && !checker.hasRight(field)) {
				return false;
			}
		}

		return true;
	}

	public boolean isConfiguredForCheck(Field field) {

		for (Checker checker : checkers) {
			if (checker.isConfiguredForCheck(field)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasRights(List<Field> fields) {

		for (Checker checker : checkers) {
			for (Field field : fields) {
				if (!checker.hasRight(field)) {
					return false;
				}
			}
		}

		return true;
	}

	public FieldAccessCheckers add(Checker checker) {
		checkers.add(checker);
		return this;
	}

	private Field getDeclaredField(Class<?> parentType, String propertyId) {

		try {
			return parentType.getDeclaredField(propertyId);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	public static FieldAccessCheckers withPersonalData(PersonalDataFieldAccessChecker.RightCheck rightCheck, boolean isInJurisdiction) {
		return withCheckers(new PersonalDataFieldAccessChecker(rightCheck, isInJurisdiction));
	}

	public static FieldAccessCheckers withCheckers(Checker... checkers) {

		FieldAccessCheckers ret = new FieldAccessCheckers();
		for (Checker checker : checkers) {
			ret.add(checker);
		}

		return ret;
	}

	public interface Checker {

		boolean isConfiguredForCheck(Field field);

		boolean hasRight(Field field);
	}
}
