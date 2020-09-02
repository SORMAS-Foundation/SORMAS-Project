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
package de.symeda.sormas.api.report;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.EpiWeek;

public class WeeklyReportCriteria extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = 5114202107622217837L;

	private EpiWeek epiWeek;
	private UserReferenceDto reportingUser;
	private RegionReferenceDto reportingUserRegion;
	private UserReferenceDto assignedOfficer;
	private Boolean officerReport;
	private Boolean zeroReport;

	@Override
	public WeeklyReportCriteria clone() {
		try {
			return (WeeklyReportCriteria) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public EpiWeek getEpiWeek() {
		return epiWeek;
	}

	public WeeklyReportCriteria epiWeek(EpiWeek epiWeek) {
		this.epiWeek = epiWeek;
		return this;
	}

	public RegionReferenceDto getReportingUserRegion() {
		return reportingUserRegion;
	}

	public WeeklyReportCriteria reportingUserRegion(RegionReferenceDto reportingUserRegion) {
		this.reportingUserRegion = reportingUserRegion;
		return this;
	}

	public UserReferenceDto getAssignedOfficer() {
		return assignedOfficer;
	}

	public WeeklyReportCriteria assignedOfficer(UserReferenceDto assignedOfficer) {
		this.assignedOfficer = assignedOfficer;
		return this;
	}

	public Boolean getOfficerReport() {
		return officerReport;
	}

	public WeeklyReportCriteria officerReport(Boolean officerReport) {
		this.officerReport = officerReport;
		return this;
	}

	public Boolean getZeroReport() {
		return zeroReport;
	}

	public WeeklyReportCriteria zeroReport(Boolean zeroReport) {
		this.zeroReport = zeroReport;
		return this;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public WeeklyReportCriteria reportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
		return this;
	}
}
