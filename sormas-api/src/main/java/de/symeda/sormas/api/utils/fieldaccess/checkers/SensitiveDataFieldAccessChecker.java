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

package de.symeda.sormas.api.utils.fieldaccess.checkers;

import java.lang.reflect.Field;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.SensitiveData;

public final class SensitiveDataFieldAccessChecker<T> extends AnnotationBasedFieldAccessChecker<T> {

	private SensitiveDataFieldAccessChecker(final boolean hasRight) {
		super(SensitiveData.class, EmbeddedSensitiveData.class, hasRight);
	}

	public static <T>  SensitiveDataFieldAccessChecker<T> inJurisdiction(RightCheck rightCheck) {
		return new SensitiveDataFieldAccessChecker<>(rightCheck.check(UserRight.SEE_SENSITIVE_DATA_IN_JURISDICTION));
	}

	public static <T> SensitiveDataFieldAccessChecker<T> outsideJurisdiction(RightCheck rightCheck) {
		return new SensitiveDataFieldAccessChecker<>(rightCheck.check(UserRight.SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION));
	}

	public static <T> SensitiveDataFieldAccessChecker<T> forcedNoAccess() {
		return new SensitiveDataFieldAccessChecker<>(false);
	}

	@Override
	protected boolean isAnnotatedFieldMandatory(Field annotatedField) {
		return annotatedField.getAnnotation(SensitiveData.class).mandatoryField();
	}

	public interface RightCheck {

		boolean check(UserRight userRight);
	}
}
