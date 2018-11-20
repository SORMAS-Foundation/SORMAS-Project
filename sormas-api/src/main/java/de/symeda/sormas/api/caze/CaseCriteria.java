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

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;

public class CaseCriteria implements Serializable, Cloneable {

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
	private UserReferenceDto surveillanceOfficer;
	private Date newCaseDateFrom;
	private Date newCaseDateTo;
	private NewCaseDateType newCaseDateType;
	private PersonReferenceDto person;
	private Boolean mustHaveNoGeoCoordinates;
	private Boolean archived;

	@Override
	public CaseCriteria clone() {
		try {
			return (CaseCriteria)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public CaseCriteria reportingUserHasRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
		return this;
	}

	public CaseCriteria outcomeEquals(CaseOutcome outcome) {
		this.outcome = outcome;
		return this;
	}
	
	public CaseCriteria diseaseEquals(Disease disease) {
		this.disease = disease;
		return this;
	}
	
	public CaseCriteria regionEquals(RegionReferenceDto region) {
		this.region = region;
		return this;
	}
	
	public CaseCriteria districtEquals(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}
	
	public CaseCriteria newCaseDateBetween(Date newCaseDateFrom, Date newCaseDateTo, NewCaseDateType newCaseDateType) {
		this.newCaseDateFrom = newCaseDateFrom;
		this.newCaseDateTo = newCaseDateTo;
		this.newCaseDateType = newCaseDateType;
		return this;
	}
	
	public CaseCriteria personEquals(PersonReferenceDto person) {
		this.person = person;
		return this;
	}
	
	public CaseCriteria mustHaveNoGeoCoordinatesEquals(Boolean mustHaveNoGeoCoordinates) {
		this.mustHaveNoGeoCoordinates = mustHaveNoGeoCoordinates;
		return this;
	}
	
	public UserRole getReportingUserRole() {
		return reportingUserRole;
	}

	public CaseOutcome getOutcome( ){
		return outcome;
	}
	
	public Disease getDisease() {
		return disease;
	}
	
	public RegionReferenceDto getRegion() {
		return region;
	}
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public Date getNewCaseDateFrom() {
		return newCaseDateFrom;
	}

	public Date getNewCaseDateTo() {
		return newCaseDateTo;
	}
	
	public NewCaseDateType getNewCaseDateType() {
		return newCaseDateType;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public Boolean isMustHaveNoGeoCoordinates() {
		return mustHaveNoGeoCoordinates;
	}
	
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public CaseCriteria caseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
		return this;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public CaseCriteria investigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
		return this;
	}

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	public CaseCriteria presentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
		return this;
	}

	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public CaseCriteria healthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
		return this;
	}

	public UserReferenceDto getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public CaseCriteria surveillanceOfficer(UserReferenceDto surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
		return this;
	}

	public Boolean getArchived() {
		return archived;
	}

	public CaseCriteria archived(Boolean archived) {
		this.archived = archived;
		return this;
	}
	
}
