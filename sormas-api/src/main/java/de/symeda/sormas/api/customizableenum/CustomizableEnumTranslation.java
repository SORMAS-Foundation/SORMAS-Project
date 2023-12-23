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

package de.symeda.sormas.api.customizableenum;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_SMALL;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;

public class CustomizableEnumTranslation implements Serializable {

	private static final long serialVersionUID = 1031723613185793585L;

	@Size(max = CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String languageCode;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String value;

	public CustomizableEnumTranslation() {

	}

	public CustomizableEnumTranslation(String languageCode, String value) {
		this.languageCode = languageCode;
		this.value = value;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CustomizableEnumTranslation that = (CustomizableEnumTranslation) o;
		return Objects.equals(languageCode, that.languageCode) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(languageCode, value);
	}
}
