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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.SormasToSormasI18nMessageError;

public class ValidationErrorGroup extends SormasToSormasI18nMessageError implements Serializable {

	private static final long serialVersionUID = 2075921523238507571L;

	private String i18nTag;
	private String validatedEntityShortUuid;

	private List<ValidationErrorMessage> messages = new ArrayList<>();

	public ValidationErrorGroup() {
		super();
		messages = new ArrayList<>();

	}

	public ValidationErrorGroup(String i18nTag) {
		this.i18nTag = i18nTag;
		this.validatedEntityShortUuid = null;
	}

	public ValidationErrorGroup(String i18nTag, String validatedEntityShortUuid) {
		this.i18nTag = i18nTag;
		this.validatedEntityShortUuid = validatedEntityShortUuid;
	}

	@Override
	public String getI18nTag() {
		return i18nTag;
	}

	public void setI18nTag(String i18nTag) {
		this.i18nTag = i18nTag;
	}

	public String getValidatedEntityShortUuid() {
		return validatedEntityShortUuid;
	}

	public void setValidatedEntityShortUuid(String validatedEntityShortUuid) {
		this.validatedEntityShortUuid = validatedEntityShortUuid;
	}

	@JsonIgnore
	@Override
	public Object[] getArgs() {
		return new Object[] {
				validatedEntityShortUuid};
	}

	public List<ValidationErrorMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ValidationErrorMessage> messages) {
		this.messages = messages;
	}

	@JsonIgnore
	@Override
	protected String getHumanMessageUnsafe() {
		if (StringUtils.isNotBlank(validatedEntityShortUuid)) {
			return String.format("%s %s", I18nProperties.getCaption(i18nTag), validatedEntityShortUuid);
		} else {
			return I18nProperties.getCaption(i18nTag);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ValidationErrorGroup that = (ValidationErrorGroup) o;
		return Objects.equals(i18nTag, that.i18nTag) && Objects.equals(validatedEntityShortUuid, that.validatedEntityShortUuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(i18nTag, validatedEntityShortUuid);
	}
}
