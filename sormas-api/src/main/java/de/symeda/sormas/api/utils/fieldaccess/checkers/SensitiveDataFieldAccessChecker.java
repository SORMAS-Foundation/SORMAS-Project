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
import java.util.Arrays;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.SensitiveData;

public final class SensitiveDataFieldAccessChecker<T> extends AnnotationBasedFieldAccessChecker<T> {

	private final String serverCountry;

	private SensitiveDataFieldAccessChecker(final boolean hasRight, SpecialAccessCheck<T> specialAccessCheck, String serverCountry) {
		super(SensitiveData.class, EmbeddedSensitiveData.class, hasRight, specialAccessCheck);
		this.serverCountry = serverCountry;
	}

	public static <T> SensitiveDataFieldAccessChecker<T> inJurisdiction(
		RightCheck rightCheck,
		SpecialAccessCheck<T> specialAccessCheck,
		String serverCountry) {
		return new SensitiveDataFieldAccessChecker<>(
			rightCheck.check(UserRight.SEE_SENSITIVE_DATA_IN_JURISDICTION),
			specialAccessCheck,
			serverCountry);
	}

	public static <T> SensitiveDataFieldAccessChecker<T> outsideJurisdiction(
		RightCheck rightCheck,
		SpecialAccessCheck<T> specialAccessCheck,
		String serverCountry) {
		return new SensitiveDataFieldAccessChecker<>(
			rightCheck.check(UserRight.SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION),
			specialAccessCheck,
			serverCountry);
	}

	public static <T> SensitiveDataFieldAccessChecker<T> forcedNoAccess() {
		return new SensitiveDataFieldAccessChecker<>(false, t -> false, null);
	}

	@Override
	protected boolean isAnnotatedFieldMandatory(Field annotatedField) {
		return annotatedField.getAnnotation(SensitiveData.class).mandatoryField();
	}

	@Override
	public boolean isConfiguredForCheck(Field field, boolean withMandatory) {
		boolean annotationPresent = field.isAnnotationPresent(fieldAnnotation);

		if (annotationPresent) {
			String[] excludeForCountries = field.getAnnotation(SensitiveData.class).excludeForCountries();
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
