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
package de.symeda.auditlog.api.sample;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.HasUuid;

@Audited
public class OverridingFormatterEntity implements HasUuid {

	public static final String THE_DATE = "theDate";
	public static final String THE_DATE_WITHOUT_TEMPORAL = "theDateWithoutTemporal";

	private String uuid;
	private Date theDate;
	private Date theDateWithoutTemporal;

	public OverridingFormatterEntity(String uuid, Date theDate, Date theDateWithoutTemporal) {

		this.uuid = uuid;
		this.theDate = theDate;
		this.theDateWithoutTemporal = theDateWithoutTemporal;
	}

	@AuditedIgnore
	public String getUuid() {
		return uuid;
	}

	@Temporal(TemporalType.TIME)
	public Date getTheDate() {
		return theDate;
	}

	public Date getTheDateWithoutTemporal() {
		return theDateWithoutTemporal;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setTheDate(Date theDate) {
		this.theDate = theDate;
	}
}
