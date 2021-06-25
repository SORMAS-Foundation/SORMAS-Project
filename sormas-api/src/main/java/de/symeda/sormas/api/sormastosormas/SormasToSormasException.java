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
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class SormasToSormasException extends Exception implements SormasToSormasErrorMessage {

	private static final long serialVersionUID = 952700907523341584L;

	private final String i18nProperty;

	private final Object[] args;

	private Map<String, ValidationErrors> errors;

	public SormasToSormasException(String message, String i18nProperty, Object... args) {
		super(message);
		this.i18nProperty = i18nProperty;
		this.args = args;
	}

	public SormasToSormasException(String message, String languageKey, Map<String, ValidationErrors> errors, Object... args) {
		super(message);
		this.i18nProperty = languageKey;
		this.args = args;
		this.errors = errors;
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
	public String toString() {
		if (StringUtils.isNotBlank(i18nProperty) && ArrayUtils.isNotEmpty(args)) {
			return String.format(I18nProperties.getString(i18nProperty), args);
		} else if (StringUtils.isNotBlank(i18nProperty)){
			return I18nProperties.getString(i18nProperty);
		} else {
			return getMessage();
		}
	}

	public Map<String, ValidationErrors> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, ValidationErrors> errors) {
		this.errors = errors;
	}

	public static SormasToSormasException fromStringProperty(String i18nProperty, Object... args) {
		return fromStringProperty(i18nProperty, null, args);
	}

	public static SormasToSormasException fromStringProperty(String i18nProperty, Map<String, ValidationErrors> errors, Object ... args) {

		String message;
		if (ArrayUtils.isNotEmpty(args)) {
			message = String.format(I18nProperties.getString(i18nProperty), args);
		} else {
			message = I18nProperties.getString(i18nProperty);
		}

		if (errors == null) {
			return new SormasToSormasException(message, i18nProperty, args);
		} else {
			return new SormasToSormasException(message, i18nProperty, errors, args);
		}
	}
}
