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

package de.symeda.sormas.api.campaign.data;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.JsonDataEntry;

public class CampaignFormDataEntry implements Serializable, JsonDataEntry {

	private static final long serialVersionUID = -3096020120349257398L;

	public static final String ID = "id";
	public static final String VALUE = "value";

	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String id;
	private Object value;

	public CampaignFormDataEntry() {

	}

	public CampaignFormDataEntry(String id, Object value) {
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		if (value == null) {
			return "";
		}

		if (value instanceof Boolean) {
			return value.equals(Boolean.TRUE) ? I18nProperties.getString(Strings.yes) : I18nProperties.getString(Strings.no);
		}

		return value.toString();
	}

	/**
	 * Needed. Otherwise hibernate will persist whenever loading,
	 * because hibernate types creates new instances that aren't equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CampaignFormDataEntry that = (CampaignFormDataEntry) o;
		return Objects.equals(id, that.id) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, value);
	}
}
