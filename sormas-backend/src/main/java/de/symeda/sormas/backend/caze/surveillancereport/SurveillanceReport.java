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

package de.symeda.sormas.backend.caze.surveillancereport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.user.User;

@Entity(name = "surveillancereports")
@Audited
public class SurveillanceReport extends AbstractDomainObject implements SormasToSormasShareable {

	private static final long serialVersionUID = -2599492274783441938L;

	public static final String TABLE_NAME = "surveillancereports";

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
	public static final String CAZE = "caze";

	private ReportingType reportingType;

	private String externalId;

	private User creatingUser;

	private Date reportDate;

	private Date dateOfDiagnosis;

	private Region facilityRegion;

	private District facilityDistrict;

	private FacilityType facilityType;

	private Facility facility;

	private String facilityDetails;

	private String notificationDetails;

	private Case caze;

	private SormasToSormasOriginInfo sormasToSormasOriginInfo;

	private List<SormasToSormasShareInfo> sormasToSormasShares = new ArrayList<>(0);

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ReportingType getReportingType() {
		return reportingType;
	}

	public void setReportingType(ReportingType reportingType) {
		this.reportingType = reportingType;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public User getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(User creatingUser) {
		this.creatingUser = creatingUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateOfDiagnosis() {
		return dateOfDiagnosis;
	}

	public void setDateOfDiagnosis(Date dateOfDiagnosis) {
		this.dateOfDiagnosis = dateOfDiagnosis;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Region getFacilityRegion() {
		return facilityRegion;
	}

	public void setFacilityRegion(Region facilityRegion) {
		this.facilityRegion = facilityRegion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public District getFacilityDistrict() {
		return facilityDistrict;
	}

	public void setFacilityDistrict(District facilityDistrict) {
		this.facilityDistrict = facilityDistrict;
	}

	@Enumerated(EnumType.STRING)
	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	@Column(columnDefinition = "text")
	public String getFacilityDetails() {
		return facilityDetails;
	}

	public void setFacilityDetails(String facilityDetails) {
		this.facilityDetails = facilityDetails;
	}

	@Column(columnDefinition = "text")
	public String getNotificationDetails() {
		return notificationDetails;
	}

	public void setNotificationDetails(String notificationDetails) {
		this.notificationDetails = notificationDetails;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}

	@Override
	@ManyToOne(cascade = {
		CascadeType.PERSIST,
		CascadeType.MERGE,
		CascadeType.DETACH,
		CascadeType.REFRESH })
	@AuditedIgnore
	public SormasToSormasOriginInfo getSormasToSormasOriginInfo() {
		return sormasToSormasOriginInfo;
	}

	@Override
	public void setSormasToSormasOriginInfo(SormasToSormasOriginInfo sormasToSormasOriginInfo) {
		this.sormasToSormasOriginInfo = sormasToSormasOriginInfo;
	}

	@OneToMany(mappedBy = SormasToSormasShareInfo.IMMUNIZATION, fetch = FetchType.LAZY)
	@AuditedIgnore
	public List<SormasToSormasShareInfo> getSormasToSormasShares() {
		return sormasToSormasShares;
	}

	public void setSormasToSormasShares(List<SormasToSormasShareInfo> sormasToSormasShares) {
		this.sormasToSormasShares = sormasToSormasShares;
	}

	/**
	 * Used only in S2S context
	 * 
	 * @return null
	 */
	@Override
	@Transient
	public User getReportingUser() {
		return null;
	}

}
