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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class CaseCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = 5114202107622217837L;

	public static final String CREATION_DATE_FROM = "creationDateFrom";
	public static final String CREATION_DATE_TO = "creationDateTo";
	public static final String DISEASE = "disease";
	public static final String NAME_UUID_EPID_NUMBER_LIKE = "nameUuidEpidNumberLike";
	public static final String REPORTING_USER_LIKE = "reportingUserLike";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String NEW_CASE_DATE_TYPE = "newCaseDateType";
	public static final String NEW_CASE_DATE_FROM = "newCaseDateFrom";
	public static final String NEW_CASE_DATE_TO = "newCaseDateTo";

	private UserRole reportingUserRole;
	private Disease disease;
	private CaseOutcome outcome;
	private CaseClassification caseClassification;
	private InvestigationStatus investigationStatus;
	private PresentCondition presentCondition;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private FacilityReferenceDto healthFacility;
	private PointOfEntryReferenceDto pointOfEntry;
	private UserReferenceDto surveillanceOfficer;
	private Date newCaseDateFrom;
	private Date newCaseDateTo;
	private Date creationDateFrom;
	private Date creationDateTo;
	private NewCaseDateType newCaseDateType;
	private PersonReferenceDto person;
	private Boolean mustHaveNoGeoCoordinates;
	private Boolean mustBePortHealthCaseWithoutFacility;
	private Boolean mustHaveCaseManagementData;
	private Boolean withoutResponsibleOfficer;
	private Boolean deleted = Boolean.FALSE;
	private String nameUuidEpidNumberLike;
	private String reportingUserLike;
	private CaseOrigin caseOrigin;
	private EntityRelevanceStatus relevanceStatus;
	private String sourceCaseInfoLike;
	private Date quarantineTo;
	public Boolean excludeSharedCases;
	private CaseSurveillanceType surveillanceType;
	

	@Override
	public CaseCriteria clone() {
		try {
			return (CaseCriteria) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public CaseCriteria reportingUserRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
		return this;
	}

	public UserRole getReportingUserRole() {
		return reportingUserRole;
	}

	public CaseCriteria outcome(CaseOutcome outcome) {
		this.outcome = outcome;
		return this;
	}

	public CaseOutcome getOutcome() {
		return outcome;
	}

	public CaseCriteria caseOrigin(CaseOrigin caseOrigin) {
		this.caseOrigin = caseOrigin;
		return this;
	}

	public CaseOrigin getCaseOrigin() {
		return caseOrigin;
	}

	public CaseCriteria disease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public CaseCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public CaseCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	/**
	 * @param newCaseDateTo will automatically be set to the end of the day
	 */
	public CaseCriteria newCaseDateBetween(Date newCaseDateFrom, Date newCaseDateTo, NewCaseDateType newCaseDateType) {
		this.newCaseDateFrom = newCaseDateFrom;
		this.newCaseDateTo = newCaseDateTo;
		this.newCaseDateType = newCaseDateType;
		return this;
	}

	public CaseCriteria newCaseDateFrom(Date newCaseDateFrom) {
		this.newCaseDateFrom = newCaseDateFrom;
		return this;
	}

	public Date getNewCaseDateFrom() {
		return newCaseDateFrom;
	}

	public CaseCriteria newCaseDateTo(Date newCaseDateTo) {
		this.newCaseDateTo = newCaseDateTo;
		return this;
	}

	public Date getNewCaseDateTo() {
		return newCaseDateTo;
	}

	public CaseCriteria newCaseDateType(NewCaseDateType newCaseDateType) {
		this.newCaseDateType = newCaseDateType;
		return this;
	}

	public NewCaseDateType getNewCaseDateType() {
		return newCaseDateType;
	}

	public CaseCriteria person(PersonReferenceDto person) {
		this.person = person;
		return this;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public CaseCriteria mustHaveNoGeoCoordinates(Boolean mustHaveNoGeoCoordinates) {
		this.mustHaveNoGeoCoordinates = mustHaveNoGeoCoordinates;
		return this;
	}

	public Boolean isMustHaveNoGeoCoordinates() {
		return mustHaveNoGeoCoordinates;
	}

	public CaseCriteria mustBePortHealthCaseWithoutFacility(Boolean mustBePortHealthCaseWithoutFacility) {
		this.mustBePortHealthCaseWithoutFacility = mustBePortHealthCaseWithoutFacility;
		return this;
	}

	public Boolean isMustBePortHealthCaseWithoutFacility() {
		return mustBePortHealthCaseWithoutFacility;
	}

	public CaseCriteria mustHaveCaseManagementData(Boolean mustHaveCaseManagementData) {
		this.mustHaveCaseManagementData = mustHaveCaseManagementData;
		return this;
	}

	public Boolean isMustHaveCaseManagementData() {
		return mustHaveCaseManagementData;
	}

	public CaseCriteria withoutResponsibleOfficer(Boolean withoutResponsibleOfficer) {
		this.withoutResponsibleOfficer = withoutResponsibleOfficer;
		return this;
	}

	public Boolean isWithoutResponsibleOfficer() {
		return this.withoutResponsibleOfficer;
	}

	public CaseCriteria caseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
		return this;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public CaseCriteria investigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
		return this;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public CaseCriteria presentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
		return this;
	}

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	public CaseCriteria healthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
		return this;
	}

	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public CaseCriteria pointOfEntry(PointOfEntryReferenceDto pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
		return this;
	}

	public PointOfEntryReferenceDto getPointOfEntry() {
		return pointOfEntry;
	}

	public CaseCriteria surveillanceOfficer(UserReferenceDto surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
		return this;
	}

	public UserReferenceDto getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public CaseCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public CaseCriteria deleted(Boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	@IgnoreForUrl
	public Boolean getDeleted() {
		return deleted;
	}

	/**
	 * returns all entries that match ALL of the passed words
	 */
	public CaseCriteria nameUuidEpidNumberLike(String nameUuidEpidNumberLike) {
		this.nameUuidEpidNumberLike = nameUuidEpidNumberLike;
		return this;
	}

	@IgnoreForUrl
	public String getNameUuidEpidNumberLike() {
		return nameUuidEpidNumberLike;
	}

	@IgnoreForUrl
	public String getSourceCaseInfoLike() {
		return sourceCaseInfoLike;
	}

	public CaseCriteria setSourceCaseInfoLike(String sourceCaseInfoLike) {
		this.sourceCaseInfoLike = sourceCaseInfoLike;
		return this;
	}

	public CaseCriteria reportingUserLike(String reportingUserLike) {
		this.reportingUserLike = reportingUserLike;
		return this;
	}

	@IgnoreForUrl
	public String getReportingUserLike() {
		return reportingUserLike;
	}

	public Date getCreationDateFrom() {
		return creationDateFrom;
	}

	public CaseCriteria creationDateFrom(Date creationDateFrom) {
		this.creationDateFrom = creationDateFrom;
		return this;
	}

	public Date getCreationDateTo() {
		return creationDateTo;
	}

	public CaseCriteria creationDateTo(Date creationDateTo) {
		this.creationDateTo = creationDateTo;
		return this;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public void setCreationDateFrom(Date creationDateFrom) {
		this.creationDateFrom = creationDateFrom;
	}

	public void setCreationDateTo(Date creationDateTo) {
		this.creationDateTo = creationDateTo;
	}

	public void setNameUuidEpidNumberLike(String nameUuidEpidNumberLike) {
		this.nameUuidEpidNumberLike = nameUuidEpidNumberLike;
	}

	public void setReportingUserLike(String reportingUserLike) {
		this.reportingUserLike = reportingUserLike;
	}

	public void setNewCaseDateFrom(Date newCaseDateFrom) {
		this.newCaseDateFrom = newCaseDateFrom;
	}

	public void setNewCaseDateTo(Date newCaseDateTo) {
		this.newCaseDateTo = newCaseDateTo;
	}

	public void setNewCaseDateType(NewCaseDateType newCaseDateType) {
		this.newCaseDateType = newCaseDateType;
	}

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public CaseCriteria quarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
		return this;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	public Boolean getExcludeSharedCases() {
		return excludeSharedCases;
	}

	public CaseCriteria excludeSharedCases(Boolean excludeSharedCases) {
		this.excludeSharedCases = excludeSharedCases;
		return this;
	}

	public void setExcludeSharedCases(Boolean excludeSharedCases) {
		this.excludeSharedCases = excludeSharedCases;
	}
	
	public CaseCriteria surveillanceType(CaseSurveillanceType surveillanceType) {
		this.surveillanceType = surveillanceType;
		return this;
	}

	public CaseSurveillanceType getSurveillanceType() {
		return surveillanceType;
	}

	public void setSurveillanceType(CaseSurveillanceType surveillanceType) {
		this.surveillanceType = surveillanceType;
	}
	

}
