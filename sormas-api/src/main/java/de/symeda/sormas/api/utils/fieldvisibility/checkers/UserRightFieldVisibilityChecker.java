/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils.fieldvisibility.checkers;

import java.lang.reflect.AccessibleObject;
import java.util.function.Function;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DependingOnUserRight;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class UserRightFieldVisibilityChecker implements FieldVisibilityCheckers.FieldBasedChecker {

	private static final long serialVersionUID = 8043968534521138969L;

	final Function<UserRight, Boolean> rightCheck;

	public UserRightFieldVisibilityChecker(Function<UserRight, Boolean> rightCheck) {
		this.rightCheck = rightCheck;
	}

	@Override
	public boolean isVisible(AccessibleObject field) {
		if (field.isAnnotationPresent(DependingOnUserRight.class)) {
			DependingOnUserRight annotation = field.getAnnotation(DependingOnUserRight.class);

			return rightCheck.apply(annotation.value());
		}

		return true;
	}
}
