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
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class CaseCriteria extends BaseCriteria implements Cloneable  {

	private static final long serialVersionUID = 5114202107622217837L;

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
	private NewCaseDateType newCaseDateType;
	private PersonReferenceDto person;
	private Boolean mustHaveNoGeoCoordinates;
	private Boolean mustBePortHealthCaseWithoutFacility;
	private Boolean archived;
	private String nameUuidEpidNumberLike;
	private String reportingUserLike;
	private CaseOrigin caseOrigin;
	
	@Override
	public CaseCriteria clone() {
		try {
			return (CaseCriteria)super.clone();
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

	public CaseCriteria archived(Boolean archived) {
		this.archived = archived;
		return this;
	}
	
	public Boolean getArchived() {
		return archived;
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
	
	public CaseCriteria reportingUserLike(String reportingUserLike) {
		this.reportingUserLike = reportingUserLike;
		return this;
	}
	
	@IgnoreForUrl
	public String getReportingUserLike() {
		return reportingUserLike;
	}
}
