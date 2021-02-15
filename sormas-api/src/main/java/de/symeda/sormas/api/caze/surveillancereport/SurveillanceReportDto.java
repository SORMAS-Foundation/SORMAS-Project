/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.caze.surveillancereport;

import java.util.Date;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

public class SurveillanceReportDto extends PseudonymizableDto {

	private static final long serialVersionUID = -8880285376883927386L;

	public static final String I18N_PREFIX = "SurveillanceReport";

	public static final String REPORTING_TYPE = "reportingType";
	public static final String CREATING_USER = "creatingUser";
	public static final String REPORT_DATE = "reportDate";
	public static final String DATE_OF_DIAGNOSIS = "dateOfDiagnosis";
	public static final String FACILITY_REGION = "facilityRegion";
	public static final String FACILITY_DISTRICT = "facilityDistrict";
	public static final String FACILITY_TYPE = "facilityType";
	public static final String FACILITY = "facility";
	public static final String FACILITY_DETAILS = "facilityDetails";
	public static final String NOTIFICATION_DETAILS = "notificationDetails";

	public static SurveillanceReportDto build(CaseReferenceDto caze, UserReferenceDto creatingUser) {
		SurveillanceReportDto surveillanceReport = new SurveillanceReportDto();

		surveillanceReport.setUuid(DataHelper.createUuid());
		surveillanceReport.setCaze(caze);
		surveillanceReport.setCreatingUser(creatingUser);

		return surveillanceReport;
	}

	private ReportingType reportingType;

	private UserReferenceDto creatingUser;

	private Date reportDate;

	private Date dateOfDiagnosis;

	private RegionReferenceDto facilityRegion;

	private DistrictReferenceDto facilityDistrict;

	private FacilityType facilityType;

	private FacilityReferenceDto facility;

	@SensitiveData
	private String facilityDetails;

	@SensitiveData
	private String notificationDetails;

	private CaseReferenceDto caze;

	public ReportingType getReportingType() {
		return reportingType;
	}

	public void setReportingType(ReportingType reportingType) {
		this.reportingType = reportingType;
	}

	public UserReferenceDto getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(UserReferenceDto creatingUser) {
		this.creatingUser = creatingUser;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Date getDateOfDiagnosis() {
		return dateOfDiagnosis;
	}

	public void setDateOfDiagnosis(Date dateOfDiagnosis) {
		this.dateOfDiagnosis = dateOfDiagnosis;
	}

	public RegionReferenceDto getFacilityRegion() {
		return facilityRegion;
	}

	public void setFacilityRegion(RegionReferenceDto facilityRegion) {
		this.facilityRegion = facilityRegion;
	}

	public DistrictReferenceDto getFacilityDistrict() {
		return facilityDistrict;
	}

	public void setFacilityDistrict(DistrictReferenceDto facilityDistrict) {
		this.facilityDistrict = facilityDistrict;
	}

	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
	}

	public FacilityReferenceDto getFacility() {
		return facility;
	}

	public void setFacility(FacilityReferenceDto facility) {
		this.facility = facility;
	}

	public String getFacilityDetails() {
		return facilityDetails;
	}

	public void setFacilityDetails(String facilityDetails) {
		this.facilityDetails = facilityDetails;
	}

	public String getNotificationDetails() {
		return notificationDetails;
	}

	public void setNotificationDetails(String notificationDetails) {
		this.notificationDetails = notificationDetails;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}
}
