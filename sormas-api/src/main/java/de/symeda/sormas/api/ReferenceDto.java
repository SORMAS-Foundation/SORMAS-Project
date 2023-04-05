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

package de.symeda.sormas.api;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.HasCaption;
import de.symeda.sormas.api.uuid.HasUuid;

@AuditedClass
public abstract class ReferenceDto implements Serializable, HasUuid, HasCaption, Comparable<ReferenceDto> {

	public static final String CAPTION = "caption";
	public static final String NO_REFERENCE_UUID = "SORMAS-CONSTID-NO-REFERENCE";

	@NotNull(message = Validations.requiredField)
	@AuditIncludeProperty
	@Pattern(regexp = UUID_REGEX, message = Validations.uuidPatternNotMatching)
	private String uuid;
	private String caption;

	protected ReferenceDto() {

	}

	protected ReferenceDto(String uuid) {
		this.uuid = uuid;
	}

	protected ReferenceDto(String uuid, String caption) {
		this.uuid = uuid;
		this.caption = caption;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public String buildCaption() {
		return getCaption();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + StringUtils.SPACE + this.getUuid();
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}

		if (getUuid() != null && o instanceof HasUuid && ((HasUuid) o).getUuid() != null) {
			// this works, because we are using UUIDs
			HasUuid ado = (HasUuid) o;
			return getUuid().equals(ado.getUuid());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {

		if (getUuid() != null) {
			return getUuid().hashCode();
		}
		return 0;
	}

	@Override
	public int compareTo(ReferenceDto o) {
		return ObjectUtils.compare(getCaption(), o.getCaption());
	}
}
