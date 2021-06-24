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

package de.symeda.sormas.api.sormastosormas;

import de.symeda.sormas.api.i18n.I18nProperties;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;

public class SormasToSormasException extends Exception {

	private static final long serialVersionUID = 952700907523341584L;

	private final String property;

	private Map<String, ValidationErrors> errors;

	public SormasToSormasException(String message, String languageKey) {
		super(message);
		this.property = languageKey;
	}

	public SormasToSormasException(String message, String languageKey, Map<String, ValidationErrors> errors) {
		super(message);
		this.property = languageKey;
		this.errors = errors;
	}

	public Map<String, ValidationErrors> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, ValidationErrors> errors) {
		this.errors = errors;
	}

	public String getProperty() {
		return property;
	}

	public static SormasToSormasException fromStringProperty(String property, Object... args) {
		return fromStringProperty(property, null, args);
	}

	public static SormasToSormasException fromStringProperty(String property, Map<String, ValidationErrors> errors, Object ... args) {

		String message;
		if (ArrayUtils.isNotEmpty(args)) {
			message = String.format(I18nProperties.getString(property), args);
		} else {
			message = I18nProperties.getString(property);
		}

		if (errors == null) {
			return new SormasToSormasException(message, property);
		} else {
			return new SormasToSormasException(message, property, errors);
		}
	}

}
