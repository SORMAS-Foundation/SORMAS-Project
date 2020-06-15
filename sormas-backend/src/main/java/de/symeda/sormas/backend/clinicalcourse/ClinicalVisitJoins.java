/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.backend.clinicalcourse;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class ClinicalVisitJoins extends AbstractDomainObjectJoins<ClinicalVisit, ClinicalVisit> {
	private Join<ClinicalVisit, Symptoms> symptoms;
	private Join<ClinicalVisit, ClinicalCourse> clinicalCourse;
	private Join<ClinicalCourse, Case> caze;
	private Join<Case, Person> casePerson;
	private Join<Case, User> caseReportingUser;
	private Join<Case, Region> caseRegion;
	private Join<Case, District> caseDistrict;
	private Join<Case, Community> caseCommunity;
	private Join<Case, Facility> caseHealthFacility;
	private Join<Case, PointOfEntry> casePointOfEntry;


	public ClinicalVisitJoins(From<ClinicalVisit, ClinicalVisit> root) {
		super(root);
	}

	public Join<ClinicalVisit, Symptoms> getSymptoms() {
		return getOrCreate(symptoms, ClinicalVisit.SYMPTOMS, JoinType.LEFT, this::setSymptoms);
	}

	private void setSymptoms(Join<ClinicalVisit, Symptoms> symptoms) {
		this.symptoms = symptoms;
	}

	public Join<ClinicalVisit, ClinicalCourse> getClinicalCourse() {
		return getOrCreate(clinicalCourse, ClinicalVisit.CLINICAL_COURSE, JoinType.LEFT, this::setClinicalCourse);
	}

	private void setClinicalCourse(Join<ClinicalVisit, ClinicalCourse> clinicalCourse) {
		this.clinicalCourse = clinicalCourse;
	}

	public Join<ClinicalCourse, Case> getCaze() {
		return getOrCreate(caze, ClinicalCourse.CASE, JoinType.LEFT, getClinicalCourse(), this::setCaze);
	}

	private void setCaze(Join<ClinicalCourse, Case> caze) {
		this.caze = caze;
	}

	public Join<Case, Person> getCasePerson() {
		return getOrCreate(casePerson, Case.PERSON, JoinType.LEFT, getCaze(), this::setCasePerson);
	}

	private void setCasePerson(Join<Case, Person> casePerson) {
		this.casePerson = casePerson;
	}

	public Join<Case, User> getCaseReportingUser() {
		return getOrCreate(caseReportingUser, Case.REPORTING_USER, JoinType.LEFT, getCaze(), this::setCaseReportingUser);
	}

	private void setCaseReportingUser(Join<Case, User> caseReportingUser) {
		this.caseReportingUser = caseReportingUser;
	}

	public Join<Case, Region> getCaseRegion() {
		return getOrCreate(caseRegion, Case.REGION, JoinType.LEFT, getCaze(), this::setCaseRegion);
	}

	private void setCaseRegion(Join<Case, Region> caseRegion) {
		this.caseRegion = caseRegion;
	}

	public Join<Case, District> getCaseDistrict() {
		return getOrCreate(caseDistrict, Case.DISTRICT, JoinType.LEFT, getCaze(), this::setCaseDistrict);
	}

	private void setCaseDistrict(Join<Case, District> caseDistrict) {
		this.caseDistrict = caseDistrict;
	}

	public Join<Case, Community> getCaseCommunity() {
		return getOrCreate(caseCommunity, Case.COMMUNITY, JoinType.LEFT, getCaze(), this::setCaseCommunity);
	}

	private void setCaseCommunity(Join<Case, Community> caseCommunity) {
		this.caseCommunity = caseCommunity;
	}

	public Join<Case, Facility> getCaseHealthFacility() {
		return getOrCreate(caseHealthFacility, Case.HEALTH_FACILITY, JoinType.LEFT, getCaze(), this::setCaseHealthFacility);
	}

	private void setCaseHealthFacility(Join<Case, Facility> caseHealthFacility) {
		this.caseHealthFacility = caseHealthFacility;
	}

	public Join<Case, PointOfEntry> getCasePointOfEntry() {
		return getOrCreate(casePointOfEntry, Case.POINT_OF_ENTRY, JoinType.LEFT, getCaze(), this::setCasePointOfEntry);
	}

	private void setCasePointOfEntry(Join<Case, PointOfEntry> casePointOfEntry) {
		this.casePointOfEntry = casePointOfEntry;
	}
}
