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

package de.symeda.sormas.api.person;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@AuditedClass
public class PersonFollowUpEndDto implements Serializable {

	@AuditInclude
	private String personUuid;
	@AuditInclude
	private Date latestFollowUpEndDate;

	public PersonFollowUpEndDto(String personUuid, Date latestFollowUpEndDate) {
		this.personUuid = personUuid;
		this.latestFollowUpEndDate = latestFollowUpEndDate;
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}

	public Date getLatestFollowUpEndDate() {
		return latestFollowUpEndDate;
	}

	public void setLatestFollowUpEndDate(Date latestFollowUpEndDate) {
		this.latestFollowUpEndDate = latestFollowUpEndDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PersonFollowUpEndDto that = (PersonFollowUpEndDto) o;
		return Objects.equals(personUuid, that.personUuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(personUuid);
	}
}
