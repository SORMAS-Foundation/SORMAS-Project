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

package de.symeda.sormas.api.sormastosormas.validation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.SormasToSormasI18nMessageError;

public class ValidationErrorMessage extends SormasToSormasI18nMessageError implements Serializable {

	private static final long serialVersionUID = 1693893611287329737L;

	private String i18nTag;

	private Object[] args;

	public ValidationErrorMessage() {

	}

	public ValidationErrorMessage(String i18nProperty, Object... args) {
		this.i18nTag = i18nProperty;
		this.args = args;
	}

	public void setI18nTag(String i18nTag) {
		this.i18nTag = i18nTag;
	}

	@Override
	public String getI18nTag() {
		return i18nTag;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	@Override
	public Object[] getArgs() {
		return args;
	}

	@JsonIgnore
	@Override
	protected String getHumanMessageUnsafe() {
		if (ArrayUtils.isNotEmpty(args)) {
			return I18nProperties.getValidationError(i18nTag, args);
		} else {
			return I18nProperties.getValidationError(i18nTag);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ValidationErrorMessage that = (ValidationErrorMessage) o;
		return Objects.equals(i18nTag, that.i18nTag) && Arrays.equals(args, that.args);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(i18nTag);
		result = 31 * result + Arrays.hashCode(args);
		return result;
	}
}
