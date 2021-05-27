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

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Base class for customizable enums. Supposed to be extended for every enum that is made customizable to ensure type safety.
 */
public abstract class CustomizableEnum implements Serializable {

	private static final long serialVersionUID = 8698428745095686559L;

	/**
	 * The enum value, identical {@link CustomizableEnumValueDto#getValue()}.
	 */
	private String value;
	/**
	 * The enum caption, internationalized according to the user language if that language is present in
	 * {@link CustomizableEnumValueDto#getTranslations()}, otherwise identical to {@link CustomizableEnumValueDto#getCaption()}.
	 */
	private String caption;

	/**
	 * Sets the properties of the extending class if it has any. Otherwise, the implementation may simply contain an empty body.
	 * 
	 * @param properties
	 *            A map with the property names as key and values as value as stored in the database
	 */
	public abstract void setProperties(Map<String, Object> properties);

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public String toString() {
		return caption;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CustomizableEnum that = (CustomizableEnum) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
