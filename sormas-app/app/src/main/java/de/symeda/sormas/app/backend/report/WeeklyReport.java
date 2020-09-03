/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.user.User;

@Entity(name = WeeklyReport.TABLE_NAME)
@DatabaseTable(tableName = WeeklyReport.TABLE_NAME)
public class WeeklyReport extends AbstractDomainObject {

	private static final long serialVersionUID = 2192478891179257201L;

	public static final String TABLE_NAME = "weeklyreport";
	public static final String I18N_PREFIX = "WeeklyReport";

	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String TOTAL_NUMBER_OF_CASES = "totalNumberOfCases";
	public static final String YEAR = "year";
	public static final String EPI_WEEK = "epiWeek";

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date reportDateTime;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private District district;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Community community;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility healthFacility;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User assignedOfficer;

	@Column(nullable = false)
	private Integer totalNumberOfCases;

	@Column(nullable = false)
	private Integer year;

	@Column(nullable = false)
	private Integer epiWeek;

	// just for reference, not persisted in DB
	private List<WeeklyReportEntry> reportEntries = new ArrayList<>();

	public Facility getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public Integer getTotalNumberOfCases() {
		return totalNumberOfCases;
	}

	public void setTotalNumberOfCases(Integer totalNumberOfCases) {
		this.totalNumberOfCases = totalNumberOfCases;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getEpiWeek() {
		return epiWeek;
	}

	public void setEpiWeek(Integer epiWeek) {
		this.epiWeek = epiWeek;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public User getAssignedOfficer() {
		return assignedOfficer;
	}

	public void setAssignedOfficer(User assignedOfficer) {
		this.assignedOfficer = assignedOfficer;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public List<WeeklyReportEntry> getReportEntries() {
		return reportEntries;
	}

	public void setReportEntries(List<WeeklyReportEntry> reportEntries) {
		this.reportEntries = reportEntries;
	}

	public WeeklyReportEntry getReportEntry(Disease disease) {
		for (WeeklyReportEntry reportEntry : reportEntries) {
			if (reportEntry.getDisease() == disease) {
				return reportEntry;
			}
		}
		return null;
	}
}
