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

package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.ui.UserProvider;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class FieldVisibilityChecker {

	private List<Checker> checkers = new ArrayList<>();
	private List<FieldBasedChecker> fieldBasedCheckers = new ArrayList<>();

	public FieldVisibilityChecker() {
	}

	public boolean isVisible(Class<?> parentType, String propertyId) {
		boolean visible = checkers.stream().allMatch(c -> c.isVisible(parentType, propertyId));

		if (visible && fieldBasedCheckers.size() > 0) {
			Field declaredField = getDeclaredField(parentType, propertyId);
			visible = declaredField != null && fieldBasedCheckers.stream().allMatch(c -> c.isVisible(declaredField));
		}

		return visible;
	}

	public FieldVisibilityChecker addDisease(Disease disease) {
		this.checkers.add(new DiseaseFieldChecker(disease));

		return this;
	}

	public FieldVisibilityChecker addOutbreak(ViewMode viewMode) {
		this.checkers.add(new OutbreakFieldChecker(viewMode));

		return this;
	}

	public FieldVisibilityChecker addCurrentCountry() {
		this.fieldBasedCheckers.add(new CurrentCountryChecker());

		return this;
	}

	public FieldVisibilityChecker addPersonalData(boolean isInJurisdiction) {
		this.fieldBasedCheckers.add(new PersonalDataChecker(isInJurisdiction));

		return this;
	}

	private Field getDeclaredField(Class<?> parentType, String propertyId) {
		try {
			return parentType.getDeclaredField(propertyId);
		} catch (NoSuchFieldException e) {
			LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
		}

		return null;
	}

	private interface Checker {
		boolean isVisible(Class<?> parentType, String propertyId);
	}

	private static class DiseaseFieldChecker implements Checker {
		private final Disease disease;

		public DiseaseFieldChecker(Disease disease) {
			this.disease = disease;
		}

		@Override
		public boolean isVisible(Class<?> parentType, String propertyId) {
			return Diseases.DiseasesConfiguration.isDefinedOrMissing(parentType, propertyId, disease);
		}
	}

	private static class OutbreakFieldChecker implements Checker {
		private final ViewMode viewMode;

		public OutbreakFieldChecker(ViewMode viewMode) {
			this.viewMode = viewMode;
		}


		@Override
		public boolean isVisible(Class<?> parentType, String propertyId) {
			return viewMode != ViewMode.SIMPLE ||
					Outbreaks.OutbreaksConfiguration.isDefined(parentType, propertyId);
		}
	}

	private interface FieldBasedChecker {
		boolean isVisible(Field field);
	}

	private static class CurrentCountryChecker implements FieldBasedChecker {

		@Override
		public boolean isVisible(Field field) {
			final Predicate<String> currentCountryIsHiddenForField =
					country -> FacadeProvider.getConfigFacade().getCountryLocale().startsWith(country);

			return !field.isAnnotationPresent(HideForCountries.class) ||
					Arrays.stream(field.getAnnotation(HideForCountries.class).countries()).noneMatch(currentCountryIsHiddenForField);
		}
	}

	private static class PersonalDataChecker implements FieldBasedChecker {
		private final boolean isInJurisdiction;

		public PersonalDataChecker(boolean isInJurisdiction) {
			this.isInJurisdiction = isInJurisdiction;
		}

		@Override
		public boolean isVisible(Field field) {
			UserRight personalDataRight = isInJurisdiction
					? UserRight.SEE_PERSONAL_DATA_IN_JURISDICTION
					: UserRight.SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION;

			return !field.isAnnotationPresent(PersonalData.class) ||
					UserProvider.getCurrent().hasUserRight(personalDataRight);
		}
	}
}
