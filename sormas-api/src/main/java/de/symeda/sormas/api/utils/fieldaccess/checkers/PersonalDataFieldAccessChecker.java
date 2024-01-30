/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.utils.fieldaccess.checkers;

import java.lang.reflect.Field;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.PersonalData;

public final class PersonalDataFieldAccessChecker<T> extends AnnotationBasedFieldAccessChecker<T> {

	private PersonalDataFieldAccessChecker(final boolean hasRight) {
		super(PersonalData.class, EmbeddedPersonalData.class, hasRight);
	}

	public static <T> PersonalDataFieldAccessChecker<T> inJurisdiction(RightCheck rightCheck) {
		return new PersonalDataFieldAccessChecker<>(rightCheck.check(UserRight.SEE_PERSONAL_DATA_IN_JURISDICTION));
	}

	public static <T> PersonalDataFieldAccessChecker<T> outsideJurisdiction(RightCheck rightCheck) {
		return new PersonalDataFieldAccessChecker<>(rightCheck.check(UserRight.SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION));
	}

	public static <T> PersonalDataFieldAccessChecker<T> forcedNoAccess() {
		return new PersonalDataFieldAccessChecker<>(false);
	}

	@Override
	protected boolean isAnnotatedFieldMandatory(Field annotatedField) {
		return annotatedField.getAnnotation(PersonalData.class).mandatoryField();
	}

	public interface RightCheck {

		boolean check(UserRight userRight);
	}
}
