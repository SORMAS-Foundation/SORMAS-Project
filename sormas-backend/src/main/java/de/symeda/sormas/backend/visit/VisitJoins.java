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

package de.symeda.sormas.backend.visit;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;

public class VisitJoins extends QueryJoins<Visit> {

	private Join<Visit, Contact> contacts;
	private Join<Visit, Case> caze;
	private Join<Visit, Symptoms> symptoms;
	private Join<Visit, User> user;
	private Join<Visit, Person> person;

	private CaseJoins caseJoins;
	private ContactJoins contactJoins;

	private JoinType contactJoinType;

	public VisitJoins(From<?, Visit> root, JoinType contactJoinType) {
		super(root);

		this.contactJoinType = contactJoinType;
	}

	public Join<Visit, Contact> getContacts() {
		return getOrCreate(contacts, Visit.CONTACTS, contactJoinType, this::setContacts);
	}

	private void setContacts(Join<Visit, Contact> contacts) {
		this.contacts = contacts;
	}

	public Join<Visit, Symptoms> getSymptoms() {
		return getOrCreate(symptoms, Visit.SYMPTOMS, JoinType.LEFT, this::setSymptoms);
	}

	private void setSymptoms(Join<Visit, Symptoms> symptoms) {
		this.symptoms = symptoms;
	}

	public Join<Visit, User> getUser() {
		return getOrCreate(user, Visit.VISIT_USER, JoinType.LEFT, this::setUser);
	}

	private void setUser(Join<Visit, User> user) {
		this.user = user;
	}

	public Join<Visit, Person> getPerson() {
		return getOrCreate(person, Visit.PERSON, JoinType.LEFT, this::setPerson);
	}

	private void setPerson(Join<Visit, Person> person) {
		this.person = person;
	}

	public Join<Visit, Case> getCase() {
		return getOrCreate(caze, Visit.CAZE, JoinType.LEFT, this::setCase);
	}

	private void setCase(Join<Visit, Case> caze) {
		this.caze = caze;
	}

	public Join<Contact, User> getContactReportingUser() {
		return getContactJoins().getReportingUser();
	}

	public Join<Contact, Region> getContactRegion() {
		return getContactJoins().getRegion();
	}

	public Join<Contact, District> getContactDistrict() {
		return getContactJoins().getDistrict();
	}

	public Join<Contact, Community> getContactCommunity() {
		return getContactJoins().getCommunity();
	}

	public CaseJoins getCaseJoins() {
		return getOrCreate(caseJoins, () -> new CaseJoins(getCase()), this::setCaseJoins);
	}

	private void setCaseJoins(CaseJoins caseJoins) {
		this.caseJoins = caseJoins;
	}

	public ContactJoins getContactJoins() {
		return getOrCreate(contactJoins, () -> new ContactJoins(getContacts()), this::setContactJoins);
	}

	private void setContactJoins(ContactJoins contactJoins) {
		this.contactJoins = contactJoins;
	}

	public Join<Case, Person> getCasePerson() {
		return getCaseJoins().getPerson();
	}

	public Join<Case, User> getCaseReportingUser() {
		return getCaseJoins().getReportingUser();
	}

	public Join<Case, Region> getCaseResponsibleRegion() {
		return getCaseJoins().getResponsibleRegion();
	}

	public Join<Case, District> getCaseResponsibleDistrict() {
		return getCaseJoins().getResponsibleDistrict();
	}

	public Join<Case, Community> getCaseResponsibleCommunity() {
		return getCaseJoins().getResponsibleCommunity();
	}

	public Join<Case, Region> getCaseRegion() {
		return getCaseJoins().getRegion();
	}

	public Join<Case, District> getCaseDistrict() {
		return getCaseJoins().getDistrict();
	}

	public Join<Case, Community> getCaseCommunity() {
		return getCaseJoins().getCommunity();
	}

	public Join<Case, Facility> getCaseFacility() {
		return getCaseJoins().getFacility();
	}

	public Join<Case, PointOfEntry> getCasePointOfEntry() {
		return getCaseJoins().getPointOfEntry();
	}

	public Join<Contact, Case> getContactCase() {
		return getContactJoins().getCaze();
	}

	public Join<Case, User> getContactCaseReportingUser() {
		return getContactJoins().getCaseJoins().getReportingUser();
	}

	public Join<Case, Region> getContactCaseResponsibleRegion() {
		return getContactJoins().getCaseJoins().getResponsibleRegion();
	}

	public Join<Case, District> getContactCaseResponsibleDistrict() {
		return getContactJoins().getCaseJoins().getResponsibleDistrict();
	}

	public Join<Case, Community> getContactCaseResponsibleCommunity() {
		return getContactJoins().getCaseJoins().getResponsibleCommunity();
	}

	public Join<Case, Region> getContactCaseRegion() {
		return getContactJoins().getCaseJoins().getRegion();
	}

	public Join<Case, District> getContactCaseDistrict() {
		return getContactJoins().getCaseJoins().getDistrict();
	}

	public Join<Case, Community> getContactCaseCommunity() {
		return getContactJoins().getCaseJoins().getCommunity();
	}

	public Join<Case, Facility> getContactCaseHealthFacility() {
		return getContactJoins().getCaseJoins().getFacility();
	}

	public Join<Case, PointOfEntry> getContactCasePointOfEntry() {
		return getContactJoins().getCaseJoins().getPointOfEntry();
	}
}
