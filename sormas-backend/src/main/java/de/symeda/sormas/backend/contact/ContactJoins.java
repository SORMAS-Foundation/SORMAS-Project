/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.contact;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;
import de.symeda.sormas.backend.visit.Visit;

public class ContactJoins extends AbstractDomainObjectJoins<Contact, Contact> {

	private Join<Contact, Person> person;
//	private CaseJoins<Contact> caseJoins;
	private Join<Contact, Case> caze;
	private Join<Case, Person> casePerson;
	private Join<Case, User> caseReportingUser;
	private Join<Case, Region> caseRegion;
	private Join<Case, District> caseDistrict;
	private Join<Case, Community> caseCommunity;
	private Join<Case, Facility> caseHealthFacility;
	private Join<Case, PointOfEntry> caseasePointOfEntry;
	private Join<Contact, User> contactOfficer;
	private Join<Person, Location> address;
	private Join<Contact, Region> region;
	private Join<Contact, District> district;
	private Join<Contact, User> reportingUser;
	private Join<Location, Region> addressRegion;
	private Join<Location, Region> addressDistrict;
	private Join<Person, Facility> occupationFacility;

	private Join<Contact, Visit> visits;
	private Join<Visit, Symptoms> visitSymptoms;


	public ContactJoins(Root<Contact> contact) {
		super(contact);

//		this.caseJoins = new CaseJoins<>(contact.join(Contact.CAZE));
	}

	public Join<Contact, Person> getPerson() {
		return getOrCreate(person, Contact.PERSON, JoinType.LEFT, this::setPerson);
	}

	private void setPerson(Join<Contact, Person> person) {
		this.person = person;
	}

	public Join<Contact, Case> getCaze() {
		return getOrCreate(caze, Contact.CAZE, JoinType.LEFT, this::setCaze);
	}

	private void setCaze(Join<Contact, Case> caze) {
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

	public Join<Case, PointOfEntry> getCaseasePointOfEntry() {
		return getOrCreate(caseasePointOfEntry, Case.POINT_OF_ENTRY, JoinType.LEFT, getCaze(), this::setCaseasePointOfEntry);
	}

	private void setCaseasePointOfEntry(Join<Case, PointOfEntry> caseasePointOfEntry) {
		this.caseasePointOfEntry = caseasePointOfEntry;
	}

	public Join<Contact, User> getContactOfficer() {
		return getOrCreate(contactOfficer, Contact.CONTACT_OFFICER, JoinType.LEFT, this::setContactOfficer);
	}

	private void setContactOfficer(Join<Contact, User> contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	public Join<Person, Location> getAddress() {
		return getOrCreate(address, Person.ADDRESS, JoinType.LEFT, getPerson(), this::setAddress);
	}

	private void setAddress(Join<Person, Location> address) {
		this.address = address;
	}

	public Join<Contact, Region> getRegion() {
		return getOrCreate(region, Contact.REGION, JoinType.LEFT, this::setRegion);
	}

	private void setRegion(Join<Contact, Region> region) {
		this.region = region;
	}

	public Join<Contact, District> getDistrict() {
		return getOrCreate(district, Contact.DISTRICT, JoinType.LEFT, this::setDistrict);
	}

	private void setDistrict(Join<Contact, District> district) {
		this.district = district;
	}

	public Join<Contact, User> getReportingUser() {
		return getOrCreate(reportingUser, Contact.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<Contact, User> reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Join<Location, Region> getAddressRegion() {
		return getOrCreate(addressRegion, Location.REGION, JoinType.LEFT, getAddress(), this::setAddressRegion);
	}

	private void setAddressRegion(Join<Location, Region> addressRegion) {
		this.addressRegion = addressRegion;
	}

	public Join<Location, Region> getAddressDistrict() {
		return getOrCreate(addressDistrict, Location.DISTRICT, JoinType.LEFT, getAddress(), this::setAddressDistrict);
	}

	private void setAddressDistrict(Join<Location, Region> addressDistrict) {
		this.addressDistrict = addressDistrict;
	}

	public Join<Person, Facility> getOccupationFacility() {
		return getOrCreate(occupationFacility, Person.OCCUPATION_FACILITY, JoinType.LEFT, getPerson(), this::setOccupationFacility);
	}

	private void setOccupationFacility(Join<Person, Facility> occupationFacility) {
		this.occupationFacility = occupationFacility;
	}

	public Join<Contact, Visit> getVisits() {
		return getOrCreate(visits, Contact.VISITS, JoinType.LEFT, this::setVisits);
	}

	private void setVisits(Join<Contact, Visit> visits) {
		this.visits = visits;
	}

	public Join<Visit, Symptoms> getVisitSymptoms() {
		return getOrCreate(visitSymptoms, Visit.SYMPTOMS, JoinType.LEFT, getVisits(), this::setVisitSymptoms);
	}

	private void setVisitSymptoms(Join<Visit, Symptoms> visitSymptoms) {
		this.visitSymptoms = visitSymptoms;
	}
}
