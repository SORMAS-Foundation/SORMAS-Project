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
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class SormasToSormasException extends Exception implements SormasToSormasI18nMessage {

	private static final long serialVersionUID = 952700907523341584L;

	private final String i18nTag;

	private final Object[] args;

	private List<ValidationErrors> errors;

	public SormasToSormasException(String message, String i18nProperty, Object... args) {
		super(message);
		this.i18nTag = i18nProperty;
		this.args = args;
	}

	public SormasToSormasException(String message, String i18nTag, List<ValidationErrors> errors, Object... args) {
		super(message);
		this.i18nTag = i18nTag;
		this.args = args;
		this.errors = errors;
	}

	@Override
	public String getI18nTag() {
		return i18nTag;
	}

	@Override
	public Object[] getArgs() {
		return args;
	}

	@Override
	public String getHumanMessage() {
		if (StringUtils.isNotBlank(i18nTag) && ArrayUtils.isNotEmpty(args)) {
			return String.format(I18nProperties.getString(i18nTag), args);
		} else if (StringUtils.isNotBlank(i18nTag)){
			return I18nProperties.getString(i18nTag);
		} else {
			return getMessage();
		}
	}

	public List<ValidationErrors> getErrors() {
		return errors;
	}

	public void setErrors(List<ValidationErrors> errors) {
		this.errors = errors;
	}

	public boolean isWarning() {
		return warning;
	}

	public static SormasToSormasException fromStringProperty(String i18nProperty, Object... args) {
		return fromStringProperty(i18nProperty, null, args);
	}

	public static SormasToSormasException fromStringProperty(String i18nTag, Map<String, ValidationErrors> errors, Object ... args) {

		String message;
		if (ArrayUtils.isNotEmpty(args)) {
			message = String.format(I18nProperties.getString(i18nTag), args);
		} else {
			message = I18nProperties.getString(i18nTag);
		}

		if (errors == null) {
			return new SormasToSormasException(message, i18nTag, args);
		} else {
			return new SormasToSormasException(message, i18nTag, errors, args);
		}
	}
}
