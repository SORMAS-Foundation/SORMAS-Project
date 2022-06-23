/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;

public class SormasToSormasException extends SormasToSormasI18nMessageError {

	private static final long serialVersionUID = 952700907523341584L;

	private final String i18nTag;

	private final Object[] args;

	private List<ValidationErrors> errors;

	boolean warning;

	public SormasToSormasException(String message, boolean warning, String i18nProperty, Object... args) {
		super(message);
		this.warning = warning;
		this.i18nTag = i18nProperty;
		this.args = args;
	}

	public SormasToSormasException(String message, boolean warning, List<ValidationErrors> errors, String i18nTag, Object... args) {
		super(message);
		this.warning = warning;
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
	protected String getHumanMessageUnsafe(){
		if (StringUtils.isNotBlank(i18nTag) && ArrayUtils.isNotEmpty(args)) {
			return String.format(I18nProperties.getString(i18nTag), args);
		} else if (StringUtils.isNotBlank(i18nTag)) {
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

	public static SormasToSormasException fromStringPropertyWithWarning(String i18nTag, Object... args) {
		return fromStringProperty(true, null, i18nTag, args);
	}

	public static SormasToSormasException fromStringProperty(String i18nTag, Object... args) {
		return fromStringProperty(false, null, i18nTag, args);
	}

	public static SormasToSormasException fromStringProperty(List<ValidationErrors> errors, String i18nTag, Object... args) {
		return fromStringProperty(false, errors, i18nTag, args);
	}

	private static SormasToSormasException fromStringProperty(boolean warning, List<ValidationErrors> errors, String i18nTag, Object... args) {

		String message;
		if (ArrayUtils.isNotEmpty(args)) {
			message = String.format(I18nProperties.getString(i18nTag), args);
		} else {
			message = I18nProperties.getString(i18nTag);
		}

		if (errors == null) {
			return new SormasToSormasException(message, warning, i18nTag, args);
		} else {
			return new SormasToSormasException(message, warning, errors, i18nTag, args);
		}
	}
}
