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

package de.symeda.sormas.api.utils.fieldaccess.checkers;

import java.lang.reflect.Field;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DependingOnUserRight;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessChecker;

public class UserRightFieldAccessChecker<T> implements FieldAccessChecker<T> {

	private final UserRight userRight;
	private final boolean hasRight;

	public UserRightFieldAccessChecker(UserRight userRight, boolean hasRight) {
		this.userRight = userRight;
		this.hasRight = hasRight;
	}

	@Override
	public boolean isConfiguredForCheck(Field field, boolean withMandatory) {
		return field.isAnnotationPresent(DependingOnUserRight.class) && userRight.equals(field.getAnnotation(DependingOnUserRight.class).value());
	}

	@Override
	public boolean isEmbedded(Field field) {
		return false;
	}

	@Override
	public boolean hasRight(T object) {
		return hasRight;
	}
}
