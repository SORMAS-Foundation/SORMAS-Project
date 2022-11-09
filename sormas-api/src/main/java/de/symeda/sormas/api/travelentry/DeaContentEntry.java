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

package de.symeda.sormas.api.travelentry;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.ObjectUtils;

import de.symeda.sormas.api.i18n.Validations;

public class DeaContentEntry implements Serializable, Comparable<DeaContentEntry> {

	private static final long serialVersionUID = -9182801671706246792L;

	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String caption;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String value;

	public DeaContentEntry(){ }

	public DeaContentEntry(String caption, String value) {
		this.caption = caption;
		this.value = value;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DeaContentEntry that = (DeaContentEntry) o;
		return Objects.equals(caption, that.caption) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(caption, value);
	}

	@Override
	public int compareTo(DeaContentEntry o) {
		int result = ObjectUtils.compare(caption != null ? caption.toLowerCase() : null, o.caption != null ? o.caption.toLowerCase() : null);
		if (result == 0) {
			return ObjectUtils.compare(value != null ? value.toLowerCase() : null, o.value != null ? o.value.toLowerCase() : null);
		}
		return result;
	}
}
