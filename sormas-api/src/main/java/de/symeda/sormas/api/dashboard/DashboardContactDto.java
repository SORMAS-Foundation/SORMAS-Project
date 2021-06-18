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
package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.visit.VisitStatus;

public class DashboardContactDto implements Serializable {

	private static final long serialVersionUID = -8118109313009645462L;

	public static final String I18N_PREFIX = "Contact";

	private long id;
	private Date reportDate;
	private Boolean symptomatic;
	private VisitStatus lastVisitStatus;
	private Date lastVisitDateTime;
	private ContactStatus contactStatus;
	private ContactClassification contactClassification;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private Disease disease;
	private Map<VisitStatus, Long> visitStatusMap = new HashMap<>();

	private DashboardQuarantineDataDto dashboardQuarantineDataDto;

	public DashboardContactDto(
		long id,
		Date reportDate,
		ContactStatus contactStatus,
		ContactClassification contactClassification,
		FollowUpStatus followUpStatus,
		Date followUpUntil,
		Disease disease,
		Date quarantineFrom,
		Date quarantineTo) {

		this.id = id;
		this.reportDate = reportDate;
		this.contactStatus = contactStatus;
		this.contactClassification = contactClassification;
		this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.disease = disease;
		this.dashboardQuarantineDataDto = new DashboardQuarantineDataDto(id, quarantineFrom, quarantineTo);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Boolean getSymptomatic() {
		return symptomatic;
	}

	public void setSymptomatic(Boolean symptomatic) {
		this.symptomatic = symptomatic;
	}

	public VisitStatus getLastVisitStatus() {
		return lastVisitStatus;
	}

	public void setLastVisitStatus(VisitStatus lastVisitStatus) {
		this.lastVisitStatus = lastVisitStatus;
	}

	public Date getLastVisitDateTime() {
		return lastVisitDateTime;
	}

	public void setLastVisitDateTime(Date lastVisitDateTime) {
		this.lastVisitDateTime = lastVisitDateTime;
	}

	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Map<VisitStatus, Long> getVisitStatusMap() {
		return visitStatusMap;
	}

	public void setVisitStatusMap(Map<VisitStatus, Long> visitStatusMap) {
		this.visitStatusMap = visitStatusMap;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DashboardContactDto other = (DashboardContactDto) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public DashboardQuarantineDataDto getDashboardQuarantineDataDto() {
		return dashboardQuarantineDataDto;
	}
}
