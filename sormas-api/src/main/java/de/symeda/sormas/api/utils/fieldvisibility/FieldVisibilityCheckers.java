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
package de.symeda.sormas.api.utils.fieldvisibility;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.FeatureTypeFieldVisibilityChecker;

public class FieldVisibilityCheckers {

	private List<FieldNameBaseChecker> fieldNameBasedCheckers = new ArrayList<>();
	private List<FieldBasedChecker> fieldBasedCheckers = new ArrayList<>();

	public FieldVisibilityCheckers() {
	}

	public boolean isVisible(Class<?> parentType, String propertyId) {
		if (!isPropertyVisible(parentType, propertyId)) {
			return false;
		}

		Field declaredField = getDeclaredField(parentType, propertyId);
		if (declaredField == null) {
			return true;
		}

		return isFieldVisible(declaredField);
	}

	public FieldVisibilityCheckers add(Checker checker) {
		if (checker instanceof FieldNameBaseChecker) {
			add((FieldNameBaseChecker) checker);
		} else {
			add((FieldBasedChecker) checker);
		}

		return this;
	}

	public FieldVisibilityCheckers add(FieldNameBaseChecker checker) {
		this.fieldNameBasedCheckers.add(checker);

		return this;
	}

	public FieldVisibilityCheckers add(FieldBasedChecker checker) {
		this.fieldBasedCheckers.add(checker);

		return this;
	}

	private boolean isPropertyVisible(Class<?> parentType, String propertyId) {
		for (FieldNameBaseChecker checker : fieldNameBasedCheckers) {
			if (!checker.isVisible(parentType, propertyId)) {
				return false;
			}
		}

		return true;
	}

	private boolean isFieldVisible(Field field) {
		for (FieldBasedChecker checker : fieldBasedCheckers) {
			if (!checker.isVisible(field)) {
				return false;
			}
		}

		return true;
	}

	private Field getDeclaredField(Class<?> parentType, String propertyId) {
		try {
			return parentType.getDeclaredField(propertyId);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	public static FieldVisibilityCheckers withDisease(Disease disease) {
		return withCheckers(new DiseaseFieldVisibilityChecker(disease));
	}

	public static FieldVisibilityCheckers withCountry(String countryLocale) {
		return withCheckers(new CountryFieldVisibilityChecker(countryLocale));
	}

	public static FieldVisibilityCheckers withFeatureTypes(List<FeatureType> featureTypes) {
		return withCheckers(new FeatureTypeFieldVisibilityChecker(featureTypes));
	}

	public static FieldVisibilityCheckers withCheckers(Checker... checkers) {
		FieldVisibilityCheckers ret = new FieldVisibilityCheckers();

		for (Checker checker : checkers) {
			ret.add(checker);
		}

		return ret;
	}

	interface Checker {

	}

	public interface FieldNameBaseChecker extends Checker {

		boolean isVisible(Class<?> parentType, String propertyId);
	}

	public interface FieldBasedChecker extends Checker {

		boolean isVisible(Field field);
	}
}
