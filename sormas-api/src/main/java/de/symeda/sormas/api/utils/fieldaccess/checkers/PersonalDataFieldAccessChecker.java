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
import java.util.Arrays;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.PersonalData;

public final class PersonalDataFieldAccessChecker<T> extends AnnotationBasedFieldAccessChecker<T> {

	private final String serverCountry;

	private PersonalDataFieldAccessChecker(final boolean hasRight, SpecialAccessCheck<T> specialAccessCheck, String serverCountry) {
		super(PersonalData.class, EmbeddedPersonalData.class, hasRight, specialAccessCheck);
		this.serverCountry = serverCountry;
	}

	public static <T> PersonalDataFieldAccessChecker<T> inJurisdiction(
		RightCheck rightCheck,
		SpecialAccessCheck<T> specialAccessCheck,
		String serverCountry) {
		return new PersonalDataFieldAccessChecker<>(rightCheck.check(UserRight.SEE_PERSONAL_DATA_IN_JURISDICTION), specialAccessCheck, serverCountry);
	}

	public static <T> PersonalDataFieldAccessChecker<T> outsideJurisdiction(
		RightCheck rightCheck,
		SpecialAccessCheck<T> specialAccessCheck,
		String serverCountry) {
		return new PersonalDataFieldAccessChecker<>(
			rightCheck.check(UserRight.SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION),
			specialAccessCheck,
			serverCountry);
	}

	public static <T> PersonalDataFieldAccessChecker<T> forcedNoAccess() {
		return new PersonalDataFieldAccessChecker<>(false, t -> false, null);
	}

	@Override
	protected boolean isAnnotatedFieldMandatory(Field annotatedField) {
		return annotatedField.getAnnotation(PersonalData.class).mandatoryField();
	}

	@Override
	public boolean isConfiguredForCheck(Field field, boolean withMandatory) {
		boolean annotationPresent = field.isAnnotationPresent(fieldAnnotation);

		if (annotationPresent) {
			String[] excludeForCountries = field.getAnnotation(PersonalData.class).excludeForCountries();
			if (Arrays.asList(excludeForCountries).contains(serverCountry)) {
				return false;
			}
		}

		if (!annotationPresent || withMandatory) {
			return annotationPresent;
		}
		return !isAnnotatedFieldMandatory(field);
	}

	public interface RightCheck {

		boolean check(UserRight userRight);
	}
}
