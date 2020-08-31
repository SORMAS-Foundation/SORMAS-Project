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

public class FieldAccessCheckers {

	private List<FieldAccessChecker> checkers = new ArrayList<>();

	public FieldAccessCheckers() {
	}

	public boolean isAccessible(Class<?> parentType, String fieldName, boolean withMandatoryFields) {

		Field declaredField = getDeclaredField(parentType, fieldName);
		if (declaredField == null) {
			return true;
		}

		return isAccessible(declaredField, withMandatoryFields);
	}

	public boolean isAccessible(Field field, boolean withMandatoryFields) {

		for (FieldAccessChecker checker : checkers) {
			if (checker.isConfiguredForCheck(field, withMandatoryFields) && !checker.hasRight()) {
				return false;
			}
		}

		return true;
	}

	public boolean isConfiguredForCheck(Field field, boolean withMandatory) {

		for (FieldAccessChecker checker : checkers) {
			if (checker.isConfiguredForCheck(field, withMandatory)) {
				return true;
			}
		}

		return false;
	}

	public boolean isEmbedded(Class<?> parentType, String fieldName) {
		Field declaredField = getDeclaredField(parentType, fieldName);

		if (declaredField == null) {
			return false;
		}

		return isEmbedded(declaredField);
	}

	public boolean isEmbedded(Field field) {

		for (FieldAccessChecker checker : checkers) {
			if (checker.isEmbedded(field)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasRights() {

		for (FieldAccessChecker checker : checkers) {
			if (!checker.hasRight()) {
				return false;
			}
		}

		return true;
	}

	public FieldAccessCheckers add(FieldAccessChecker checker) {
		checkers.add(checker);
		return this;
	}

	private Field getDeclaredField(Class<?> parentType, String propertyId) {

		try {
			Field declaredField = parentType.getDeclaredField(propertyId);

			return declaredField;
		} catch (NoSuchFieldException e) {
			if (parentType.getSuperclass() != null) {
				return getDeclaredField(parentType.getSuperclass(), propertyId);
			}

			return null;
		}
	}

	public static FieldAccessCheckers withCheckers(FieldAccessChecker... checkers) {

		FieldAccessCheckers ret = new FieldAccessCheckers();
		for (FieldAccessChecker checker : checkers) {
			ret.add(checker);
		}

		return ret;
	}

	public <T extends FieldAccessChecker> T getCheckerByType(Class<T> checkerType) {
		for (FieldAccessChecker checker : checkers) {
			if (checkerType.isAssignableFrom(checker.getClass())) {
				return (T) checker;
			}
		}

		return null;
	}
}
