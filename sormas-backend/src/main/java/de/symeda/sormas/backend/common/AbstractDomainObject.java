/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.common;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.utils.DataHelper;

@MappedSuperclass
public abstract class AbstractDomainObject implements Serializable, Cloneable, HasUuid {

	private static final long serialVersionUID = 3957437214306161226L;

	private static final String SEQ_GEN_NAME = "ENTITY_SEQ_GEN";
	private static final String SEQ_SQL_NAME = "ENTITY_SEQ";

	public static final String HISTORY_TABLE_SUFFIX = "_history";

	public static final String ID = "id";
	public static final String UUID = "uuid";
	public static final String CREATION_DATE = "creationDate";
	public static final String CHANGE_DATE = "changeDate";

	private Long id;
	private String uuid;
	private Timestamp creationDate;
	private Timestamp changeDate;

	@Override
	public AbstractDomainObject clone() {
		try {
			return (AbstractDomainObject) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Id
	@SequenceGenerator(name = SEQ_GEN_NAME, allocationSize = 1, sequenceName = SEQ_SQL_NAME)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_GEN_NAME)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic(optional = false)
	@Size(min = 20, max = 36)
	@Column(nullable = false, unique = true, length = 36)
	public String getUuid() {

		if (uuid == null) {
			/**
			 * New objects should automatically get a UUID.
			 * This should be returned already before saving via getUuid().
			 * The generation of UUIDs is relatively time-consuming. Most objects are loaded from the database.
			 * Therefore, no UUIDs should be created for these objects.
			 * This is not compatible with lazy instance loading:
			 * The UUIDs will not be overwritten later.
			 * Solution: getUuid () may create a UUID & the ADO Interceptor calls getUuid() before saving.
			 * Then this can be done immediately with getDate().
			 */
			uuid = DataHelper.createUuid();
		}
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Column(nullable = false)
	public Timestamp getCreationDate() {
		if (creationDate == null) {
			creationDate = Timestamp.from(Instant.now());
		}
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	@Version
	@Column(nullable = false)
	public Timestamp getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Timestamp changeDate) {
		this.changeDate = changeDate;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}

		if (o.getClass() == this.getClass()) {
			// this works, because we are using UUIDs
			AbstractDomainObject ado = (AbstractDomainObject) o;
			return getUuid().equals(ado.getUuid());

		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getUuid().hashCode();
	}
}
