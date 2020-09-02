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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class WeeklyReportDto extends EntityDto {

	private static final long serialVersionUID = -2884998571593631851L;

	public static final String I18N_PREFIX = "WeeklyReport";

	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String ASSIGNED_OFFICER = "assignedOfficer";
	public static final String TOTAL_NUMBER_OF_CASES = "totalNumberOfCases";
	public static final String YEAR = "year";
	public static final String EPI_WEEK = "epiWeek";
	public static final String REPORT_ENTRIES = "reportEntries";

	private UserReferenceDto reportingUser;
	private Date reportDateTime;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private FacilityReferenceDto healthFacility;
	private UserReferenceDto assignedOfficer;
	private Integer totalNumberOfCases;
	private Integer year;
	private Integer epiWeek;
	private List<WeeklyReportEntryDto> reportEntries = new ArrayList<>();

	public static WeeklyReportDto build(UserReferenceDto reportingUser) {

		WeeklyReportDto dto = new WeeklyReportDto();
		dto.setUuid(DataHelper.createUuid());
		dto.setReportingUser(reportingUser);
		return dto;
	}

	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	/**
	 * For informants the number of cases reported by the user. For officers the
	 * number of cases reported by the user and all related informants.
	 */
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

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public UserReferenceDto getAssignedOfficer() {
		return assignedOfficer;
	}

	public void setAssignedOfficer(UserReferenceDto assignedOfficer) {
		this.assignedOfficer = assignedOfficer;
	}

	public WeeklyReportReferenceDto toReference() {
		return new WeeklyReportReferenceDto(getUuid());
	}

	public List<WeeklyReportEntryDto> getReportEntries() {
		return reportEntries;
	}

	public void setReportEntries(List<WeeklyReportEntryDto> reportEntries) {
		this.reportEntries = reportEntries;
	}
}
