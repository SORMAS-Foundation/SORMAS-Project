/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.sormastosormas;

import de.symeda.sormas.api.i18n.I18nProperties;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ValidationErrors implements Serializable {

	private static final long serialVersionUID = 1635651082132555214L;

	private final Map<String, List<ValidationError>> errors;

	public ValidationErrors() {
		errors = new HashMap<>();
	}

	public void add(String group, ValidationError validationError) {
		List<ValidationError> groupErrors;

		if (errors.containsKey(group)) {
			groupErrors = errors.get(group);
		} else {
			groupErrors = new ArrayList<>();
			errors.put(group, groupErrors);
		}

		groupErrors.add(validationError);
	}

	public void add(String group, String i18nProperty, Object... args) {
		add(group, new ValidationError(i18nProperty, args));
	}

	public void addAll(ValidationErrors errors) {
		for (Map.Entry<String, List<ValidationError>> error : errors.errors.entrySet()) {
			for (ValidationError message : error.getValue()) {
				add(error.getKey(), message);
			}
		}
	}

	public Map<String, List<ValidationError>> getErrors() {
		return errors;
	}

	public boolean hasError() {
		return errors.size() > 0;
	}

	public static ValidationErrors create(String group, String i18nProperty, Object... args) {
		ValidationErrors errors = new ValidationErrors();

		errors.add(group, new ValidationError(i18nProperty, args));

		return errors;
	}

	public static class ValidationError implements SormasToSormasErrorMessage {

		private final String i18nProperty;

		private final Object[] args;

		public ValidationError(String i18nProperty, Object... args) {
			this.i18nProperty = i18nProperty;
			this.args = args;
		}

		@Override
		public String getI18nProperty() {
			return i18nProperty;
		}

		@Override
		public Object[] getArgs() {
			return args;
		}

		@Override
		public String getHumanErrorMessage() {
			if (ArrayUtils.isNotEmpty(args)) {
				return String.format(I18nProperties.getValidationError(i18nProperty), args);
			} else {
				return I18nProperties.getValidationError(i18nProperty);
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ValidationError that = (ValidationError) o;
			return Objects.equals(i18nProperty, that.i18nProperty) && Arrays.equals(args, that.args);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(i18nProperty);
			result = 31 * result + Arrays.hashCode(args);
			return result;
		}

	}
}
