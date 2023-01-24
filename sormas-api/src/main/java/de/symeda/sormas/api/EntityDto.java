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
import java.util.Date;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HasCaption;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.uuid.HasUuid;

/**
 * All inheriting classes of EntityDto must include a build() method that sets
 * the necessary default values. This method should then be used instead of the
 * constructor.
 * 
 * @JsonInclude We don't need to transfer properties with a null value. This
 *              will reduce data transferred to something between 20% and 50% -
 *              especially for fields that are not needed for all diseases
 */
@AuditedClass
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class EntityDto implements Serializable, Cloneable, HasUuid, HasCaption {

	private static final long serialVersionUID = -1L;

	public static final String CREATION_DATE = "creationDate";
	public static final String CHANGE_DATE = "changeDate";
	public static final String UUID = "uuid";
	public static final String NO_LAST_SYNCED_UUID = "NO_LAST_SYNCED_UUID";

	private Date creationDate;
	private Date changeDate;
	@Outbreaks
	@Pattern(regexp = UUID_REGEX, message = Validations.uuidPatternNotMatching)
	@Size(min = FieldConstraints.CHARACTER_LIMIT_UUID_MIN, max = FieldConstraints.CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	@AuditIncludeProperty
	private String uuid;

	protected EntityDto() {

	}

	protected EntityDto(Date creationDate, Date changeDate, String uuid) {

		this.creationDate = creationDate;
		this.changeDate = changeDate;
		this.uuid = uuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	public EntityDto clone() throws CloneNotSupportedException {

		if (getUuid() == null) {
			throw new CloneNotSupportedException("DataTransferObject must have uuid in order to be cloneable");
		}

		return (EntityDto) super.clone();
	}

	@JsonIgnore
	public String buildCaption() {
		return toString();
	}

	public String i18nPrefix() {
		return null;
	}

	@Override
	public String toString() {
		return (i18nPrefix() != null ? i18nPrefix() : getClass().getSimpleName()) + StringUtils.SPACE + this.getUuid();
	}
}
