/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.utils.fieldaccess.checkers;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class RightBasedFieldAccessChecker implements FieldAccessCheckers.Checker {
	private Class<? extends Annotation> fieldAnnotation;
	protected final RightCheck rightCheck;

	public RightBasedFieldAccessChecker(Class<? extends Annotation> fieldAnnotation, RightCheck rightCheck) {
		this.fieldAnnotation = fieldAnnotation;
		this.rightCheck = rightCheck;
	}

	@Override
	public boolean isConfiguredForCheck(Field field) {
		return field.isAnnotationPresent(fieldAnnotation);
	}

	@Override
	public boolean hasRight(Field field) {
		return rightCheck.check(getUserRight());
	}

	protected abstract UserRight getUserRight();

	public interface RightCheck {
		boolean check(UserRight userRight);
	}
}
