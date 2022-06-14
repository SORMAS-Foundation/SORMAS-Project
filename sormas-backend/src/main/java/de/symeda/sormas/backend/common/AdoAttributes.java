/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import java.util.Date;

import de.symeda.sormas.api.HasUuid;

public class AdoAttributes implements Serializable, HasUuid {

	private static final long serialVersionUID = 3704369066198799618L;

	public static final String ID = AbstractDomainObject.ID;
	public static final String UUID = AbstractDomainObject.UUID;
	public static final String CHANGE_DATE = AbstractDomainObject.CHANGE_DATE;

	private final Long id;
	private final String uuid;
	private final Date changeDate;

	public AdoAttributes(Long id, String uuid, Date changeDate) {

		this.id = id;
		this.uuid = uuid;
		this.changeDate = changeDate;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public Date getChangeDate() {
		return changeDate;
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
			HasUuid ado = (HasUuid) o;
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
