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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FieldAccessCheckers<T> {

	private final List<FieldAccessChecker<T>> checkers = new ArrayList<>();

	public boolean isAccessible(Class<T> parentType, T dto, String fieldName, boolean withMandatoryFields) {

		Field declaredField = getDeclaredField(parentType, fieldName);
		if (declaredField == null) {
			return true;
		}

		return isAccessible(declaredField, dto, withMandatoryFields);
	}

	public boolean isAccessible(Field field, T object, boolean withMandatoryFields) {

		for (FieldAccessChecker<T> checker : checkers) {
			if (checker.isConfiguredForCheck(field, withMandatoryFields) && !checker.hasRight(object)) {
				return false;
			}
		}

		return true;
	}

	@SafeVarargs
	public final boolean isAccessibleBy(
		Field field,
		T object,
		boolean withMandatoryFields,
		@SuppressWarnings("rawtypes") Class<? extends FieldAccessChecker>... checkerTypes) {

		List<FieldAccessChecker<T>> filteredCheckers =
			checkers.stream().filter(c -> Arrays.stream(checkerTypes).anyMatch(t -> c.getClass().isAssignableFrom(t))).collect(Collectors.toList());
		for (FieldAccessChecker<T> checker : filteredCheckers) {
			if (checker.isConfiguredForCheck(field, withMandatoryFields) && !checker.hasRight(object)) {
				return false;
			}
		}

		return true;
	}

	public boolean isConfiguredForCheck(Field field, boolean withMandatory) {

		for (FieldAccessChecker<T> checker : checkers) {
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

		for (FieldAccessChecker<T> checker : checkers) {
			if (checker.isEmbedded(field)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasRights(T object) {

		for (FieldAccessChecker<T> checker : checkers) {
			if (!checker.hasRight(object)) {
				return false;
			}
		}

		return true;
	}

	public FieldAccessCheckers<T> add(FieldAccessChecker<T> checker) {
		checkers.add(checker);
		return this;
	}

	private static Field getDeclaredField(Class<?> parentType, String propertyId) {

		try {
			return parentType.getDeclaredField(propertyId);
		} catch (NoSuchFieldException e) {
			if (parentType.getSuperclass() != null) {
				return getDeclaredField(parentType.getSuperclass(), propertyId);
			}

			return null;
		}
	}

	public static <T> FieldAccessCheckers<T> withCheckers(Collection<FieldAccessChecker<T>> checkers) {

		FieldAccessCheckers<T> ret = new FieldAccessCheckers<>();
		for (FieldAccessChecker<T> checker : checkers) {
			ret.add(checker);
		}

		return ret;
	}

	public <F extends FieldAccessChecker<T>> F getCheckerByType(Class<F> checkerType) {
		for (FieldAccessChecker<T> checker : checkers) {
			if (checkerType.isAssignableFrom(checker.getClass())) {
				//noinspection unchecked
				return (F) checker;
			}
		}

		return null;
	}
}
