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
package de.symeda.sormas.backend.task;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.user.User;

public class TaskJoins extends QueryJoins<Task> {

	private Join<Task, Case> caze;
	private Join<Case, Person> casePerson;
	private Join<Task, Event> event;
	private Join<Event, User> eventReportingUser;
	private Join<Event, User> eventResponsibleUser;
	private Join<Event, Location> eventLocation;
	private Join<Location, Region> eventRegion;
	private Join<Location, District> eventDistrict;
	private Join<Location, Community> eventCommunity;
	private Join<Task, Contact> contact;
	private Join<Contact, Person> contactPerson;
	private Join<Contact, Case> contactCase;
	private Join<Case, Person> contactCasePerson;
	private Join<Task, User> creator;
	private Join<Task, User> assignee;
	private Join<Case, User> caseReportingUser;
	private Join<Case, Region> caseResponsibleRegion;
	private Join<Case, District> caseResponsibleDistrict;
	private Join<Case, Community> caseResponsibleCommunity;
	private Join<Case, Region> caseRegion;
	private Join<Case, District> caseDistrict;
	private Join<Case, Community> caseCommunity;
	private Join<Case, Facility> caseFacility;
	private Join<Case, PointOfEntry> casePointOfEntry;
	private Join<Contact, User> contactReportingUser;
	private Join<Contact, Region> contactRegion;
	private Join<Contact, District> contactDistrict;
	private Join<Contact, Community> contactCommunity;
	private Join<Case, User> contactCaseReportingUser;
	private Join<Case, Region> contactCaseResponsibleRegion;
	private Join<Case, District> contactCaseResponsibleDistrict;
	private Join<Case, Community> contactCaseResponsibleCommunity;
	private Join<Case, Region> contactCaseRegion;
	private Join<Case, District> contactCaseDistrict;
	private Join<Case, Community> contactCaseCommunity;
	private Join<Case, Facility> contactCaseHealthFacility;
	private Join<Case, PointOfEntry> contactCasePointOfEntry;
	private Join<Person, Location> casePersonAddress;
	private Join<Person, Location> contactPersonAddress;
	private final CaseJoins caseJoins;
	private final PersonJoins contactPersonJoins;
	private final PersonJoins casePersonJoins;
	private Join<Task, TravelEntry> travelEntry;
	private Join<TravelEntry, Region> travelEntryResponsibleRegion;
	private Join<TravelEntry, District> travelEntryResponsibleDistrict;
	private Join<TravelEntry, Community> travelEntryResponsibleCommunity;
	private Join<TravelEntry, Person> travelEntryPerson;

	public TaskJoins(From<?, Task> root) {
		super(root);

		caseJoins = new CaseJoins(getCaze());
		casePersonJoins = new PersonJoins(getCasePerson());
		contactPersonJoins = new PersonJoins(getContactPerson());
	}

	public CaseJoins getCaseJoins() {
		return caseJoins;
	}

	public Join<Task, Case> getCaze() {
		return getOrCreate(caze, Task.CAZE, JoinType.LEFT, this::setCaze);
	}

	private void setCaze(Join<Task, Case> caze) {
		this.caze = caze;
	}

	public Join<Case, Person> getCasePerson() {
		return getOrCreate(casePerson, Case.PERSON, JoinType.LEFT, getCaze(), this::setCasePerson);
	}

	private void setCasePerson(Join<Case, Person> casePerson) {
		this.casePerson = casePerson;
	}

	public Join<Task, Event> getEvent() {
		return getOrCreate(event, Task.EVENT, JoinType.LEFT, this::setEvent);
	}

	private void setEvent(Join<Task, Event> event) {
		this.event = event;
	}

	public Join<Task, TravelEntry> getTravelEntry() {
		return getOrCreate(travelEntry, Task.TRAVEL_ENTRY, JoinType.LEFT, this::setTravelEntry);
	}

	private void setTravelEntry(Join<Task, TravelEntry> travelEntry) {
		this.travelEntry = travelEntry;
	}

	public Join<TravelEntry, Region> getTravelEntryResponsibleRegion() {
		return getOrCreate(
			travelEntryResponsibleRegion,
			TravelEntry.RESPONSIBLE_REGION,
			JoinType.LEFT,
			getTravelEntry(),
			this::setTravelEntryResponsibleRegion);
	}

	public void setTravelEntryResponsibleRegion(Join<TravelEntry, Region> travelEntryResponsibleRegion) {
		this.travelEntryResponsibleRegion = travelEntryResponsibleRegion;
	}

	public Join<TravelEntry, District> getTravelEntryResponsibleDistrict() {
		return getOrCreate(
			travelEntryResponsibleDistrict,
			TravelEntry.RESPONSIBLE_DISTRICT,
			JoinType.LEFT,
			getTravelEntry(),
			this::setTravelEntryResponsibleDistrict);
	}

	public void setTravelEntryResponsibleDistrict(Join<TravelEntry, District> travelEntryResponsibleDistrict) {
		this.travelEntryResponsibleDistrict = travelEntryResponsibleDistrict;
	}

	public Join<TravelEntry, Community> getTravelEntryResponsibleCommunity() {
		return getOrCreate(
			travelEntryResponsibleCommunity,
			TravelEntry.RESPONSIBLE_COMMUNITY,
			JoinType.LEFT,
			getTravelEntry(),
			this::setTravelEntryResponsibleCommunity);
	}

	public void setTravelEntryResponsibleCommunity(Join<TravelEntry, Community> travelEntryResponsibleCommunity) {
		this.travelEntryResponsibleCommunity = travelEntryResponsibleCommunity;
	}

	public Join<TravelEntry, Person> getTravelEntryPerson() {
		return getOrCreate(travelEntryPerson, TravelEntry.PERSON, JoinType.LEFT, getTravelEntry(), this::setTravelEntryPerson);
	}

	public void setTravelEntryPerson(Join<TravelEntry, Person> travelEntryPerson) {
		this.travelEntryPerson = travelEntryPerson;
	}

	public Join<Event, User> getEventReportingUser() {
		return getOrCreate(eventReportingUser, Event.REPORTING_USER, JoinType.LEFT, getEvent(), this::setEventReportingUser);
	}

	private void setEventReportingUser(Join<Event, User> eventReportingUser) {
		this.eventReportingUser = eventReportingUser;
	}

	public Join<Event, User> getEventResponsibleUser() {
		return getOrCreate(eventResponsibleUser, Event.RESPONSIBLE_USER, JoinType.LEFT, getEvent(), this::setEventResponsibleUser);
	}

	private void setEventResponsibleUser(Join<Event, User> eventResponsibleUser) {
		this.eventResponsibleUser = eventResponsibleUser;
	}

	public Join<Event, Location> getEventLocation() {
		return getOrCreate(eventLocation, Event.EVENT_LOCATION, JoinType.LEFT, getEvent(), this::setEventLocation);
	}

	private void setEventLocation(Join<Event, Location> eventLocation) {
		this.eventLocation = eventLocation;
	}

	public Join<Location, Region> getEventRegion() {
		return getOrCreate(eventRegion, Location.REGION, JoinType.LEFT, getEventLocation(), this::setEventRegion);
	}

	private void setEventRegion(Join<Location, Region> eventRegion) {
		this.eventRegion = eventRegion;
	}

	public Join<Location, District> getEventDistrict() {
		return getOrCreate(eventDistrict, Location.DISTRICT, JoinType.LEFT, getEventLocation(), this::setEventDistrict);
	}

	private void setEventDistrict(Join<Location, District> eventDistrict) {
		this.eventDistrict = eventDistrict;
	}

	public Join<Location, Community> getEventCommunity() {
		return getOrCreate(eventCommunity, Location.COMMUNITY, JoinType.LEFT, getEventLocation(), this::setEventCommunity);
	}

	private void setEventCommunity(Join<Location, Community> eventCommunity) {
		this.eventCommunity = eventCommunity;
	}

	public Join<Task, Contact> getContact() {
		return getOrCreate(contact, Task.CONTACT, JoinType.LEFT, this::setContact);
	}

	private void setContact(Join<Task, Contact> contact) {
		this.contact = contact;
	}

	public Join<Contact, Person> getContactPerson() {
		return getOrCreate(contactPerson, Contact.PERSON, JoinType.LEFT, getContact(), this::setContactPerson);
	}

	private void setContactPerson(Join<Contact, Person> contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Join<Contact, Case> getContactCase() {
		return getOrCreate(contactCase, Contact.CAZE, JoinType.LEFT, getContact(), this::setContactCase);
	}

	private void setContactCase(Join<Contact, Case> contactCase) {
		this.contactCase = contactCase;
	}

	public Join<Case, Person> getContactCasePerson() {
		return getOrCreate(contactCasePerson, Case.PERSON, JoinType.LEFT, getContactCase(), this::setContactCasePerson);
	}

	private void setContactCasePerson(Join<Case, Person> contactCasePerson) {
		this.contactCasePerson = contactCasePerson;
	}

	public Join<Task, User> getCreator() {
		return getOrCreate(creator, Task.CREATOR_USER, JoinType.LEFT, this::setCreator);
	}

	private void setCreator(Join<Task, User> creator) {
		this.creator = creator;
	}

	public Join<Task, User> getAssignee() {
		return getOrCreate(assignee, Task.ASSIGNEE_USER, JoinType.LEFT, this::setAssignee);
	}

	private void setAssignee(Join<Task, User> assignee) {
		this.assignee = assignee;
	}

	public Join<Case, User> getCaseReportingUser() {
		return getOrCreate(caseReportingUser, Case.REPORTING_USER, JoinType.LEFT, getCaze(), this::setCaseReportingUser);
	}

	private void setCaseReportingUser(Join<Case, User> caseReportingUser) {
		this.caseReportingUser = caseReportingUser;
	}

	public Join<Case, Region> getCaseResponsibleRegion() {
		return getOrCreate(caseResponsibleRegion, Case.RESPONSIBLE_REGION, JoinType.LEFT, getCaze(), this::setCaseResponsibleRegion);
	}

	private void setCaseResponsibleRegion(Join<Case, Region> caseResponsibleRegion) {
		this.caseResponsibleRegion = caseResponsibleRegion;
	}

	public Join<Case, District> getCaseResponsibleDistrict() {
		return getOrCreate(caseResponsibleDistrict, Case.RESPONSIBLE_DISTRICT, JoinType.LEFT, getCaze(), this::setCaseResponsibleDistrict);
	}

	private void setCaseResponsibleDistrict(Join<Case, District> caseResponsibleDistrict) {
		this.caseResponsibleDistrict = caseResponsibleDistrict;
	}

	public Join<Case, Community> getCaseResponsibleCommunity() {
		return getOrCreate(caseResponsibleCommunity, Case.RESPONSIBLE_COMMUNITY, JoinType.LEFT, getCaze(), this::setCaseResponsibleCommunity);
	}

	private void setCaseResponsibleCommunity(Join<Case, Community> caseResponsibleCommunity) {
		this.caseResponsibleCommunity = caseResponsibleCommunity;
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

	public Join<Case, Facility> getCaseFacility() {
		return getOrCreate(caseFacility, Case.HEALTH_FACILITY, JoinType.LEFT, getCaze(), this::setCaseFacility);
	}

	private void setCaseFacility(Join<Case, Facility> caseFacility) {
		this.caseFacility = caseFacility;
	}

	public Join<Case, PointOfEntry> getCasePointOfEntry() {
		return getOrCreate(casePointOfEntry, Case.POINT_OF_ENTRY, JoinType.LEFT, getCaze(), this::setCasePointOfEntry);
	}

	private void setCasePointOfEntry(Join<Case, PointOfEntry> casePointOfEntry) {
		this.casePointOfEntry = casePointOfEntry;
	}

	public Join<Contact, User> getContactReportingUser() {
		return getOrCreate(contactReportingUser, Contact.REPORTING_USER, JoinType.LEFT, getContact(), this::setContactReportingUser);
	}

	private void setContactReportingUser(Join<Contact, User> contactReportingUser) {
		this.contactReportingUser = contactReportingUser;
	}

	public Join<Contact, Region> getContactRegion() {
		return getOrCreate(contactRegion, Contact.REGION, JoinType.LEFT, getContact(), this::setContactRegion);
	}

	private void setContactRegion(Join<Contact, Region> contactRegion) {
		this.contactRegion = contactRegion;
	}

	public Join<Contact, District> getContactDistrict() {
		return getOrCreate(contactDistrict, Contact.DISTRICT, JoinType.LEFT, getContact(), this::setContactDistrict);
	}

	private void setContactDistrict(Join<Contact, District> contactDistrict) {
		this.contactDistrict = contactDistrict;
	}

	public Join<Contact, Community> getContactCommunity() {
		return getOrCreate(contactCommunity, Contact.COMMUNITY, JoinType.LEFT, getContact(), this::setContactCommunity);
	}

	public void setContactCommunity(Join<Contact, Community> contactCommunity) {
		this.contactCommunity = contactCommunity;
	}

	public Join<Case, User> getContactCaseReportingUser() {
		return getOrCreate(contactCaseReportingUser, Case.REPORTING_USER, JoinType.LEFT, getContactCase(), this::setContactCaseReportingUser);
	}

	private void setContactCaseReportingUser(Join<Case, User> contactCaseReportingUser) {
		this.contactCaseReportingUser = contactCaseReportingUser;
	}

	public Join<Case, Region> getContactCaseResponsibleRegion() {
		return getOrCreate(
			contactCaseResponsibleRegion,
			Case.RESPONSIBLE_REGION,
			JoinType.LEFT,
			getContactCase(),
			this::setContactCaseResponsibleRegion);
	}

	private void setContactCaseResponsibleRegion(Join<Case, Region> contactCaseResponsibleRegion) {
		this.contactCaseResponsibleRegion = contactCaseResponsibleRegion;
	}

	public Join<Case, District> getContactCaseResponsibleDistrict() {
		return getOrCreate(
			contactCaseResponsibleDistrict,
			Case.RESPONSIBLE_DISTRICT,
			JoinType.LEFT,
			getContactCase(),
			this::setContactCaseResponsibleDistrict);
	}

	private void setContactCaseResponsibleDistrict(Join<Case, District> contactCaseResponsibleDistrict) {
		this.contactCaseResponsibleDistrict = contactCaseResponsibleDistrict;
	}

	public Join<Case, Community> getContactCaseResponsibleCommunity() {
		return getOrCreate(
			contactCaseResponsibleCommunity,
			Case.RESPONSIBLE_COMMUNITY,
			JoinType.LEFT,
			getContactCase(),
			this::setContactCaseResponsibleCommunity);
	}

	private void setContactCaseResponsibleCommunity(Join<Case, Community> contactCaseResponsibleCommunity) {
		this.contactCaseResponsibleCommunity = contactCaseResponsibleCommunity;
	}

	public Join<Case, Region> getContactCaseRegion() {
		return getOrCreate(contactCaseRegion, Case.REGION, JoinType.LEFT, getContactCase(), this::setContactCaseRegion);
	}

	private void setContactCaseRegion(Join<Case, Region> contactCaseRegion) {
		this.contactCaseRegion = contactCaseRegion;
	}

	public Join<Case, District> getContactCaseDistrict() {
		return getOrCreate(contactCaseDistrict, Case.DISTRICT, JoinType.LEFT, getContactCase(), this::setContactCaseDistrict);
	}

	private void setContactCaseDistrict(Join<Case, District> contactCaseDistrict) {
		this.contactCaseDistrict = contactCaseDistrict;
	}

	public Join<Case, Community> getContactCaseCommunity() {
		return getOrCreate(contactCaseCommunity, Case.COMMUNITY, JoinType.LEFT, getContactCase(), this::setContactCaseCommunity);
	}

	private void setContactCaseCommunity(Join<Case, Community> contactCaseCommunity) {
		this.contactCaseCommunity = contactCaseCommunity;
	}

	public Join<Case, Facility> getContactCaseHealthFacility() {
		return getOrCreate(contactCaseHealthFacility, Case.HEALTH_FACILITY, JoinType.LEFT, getContactCase(), this::setContactCaseHealthFacility);
	}

	private void setContactCaseHealthFacility(Join<Case, Facility> contactCaseHealthFacility) {
		this.contactCaseHealthFacility = contactCaseHealthFacility;
	}

	public Join<Case, PointOfEntry> getContactCasePointOfEntry() {
		return getOrCreate(contactCasePointOfEntry, Case.POINT_OF_ENTRY, JoinType.LEFT, getContactCase(), this::setContactCasePointOfEntry);
	}

	private void setContactCasePointOfEntry(Join<Case, PointOfEntry> contactCasePointOfEntry) {
		this.contactCasePointOfEntry = contactCasePointOfEntry;
	}

	public PersonJoins getCasePersonJoins() {
		return casePersonJoins;
	}

	public PersonJoins getContactPersonJoins() {
		return contactPersonJoins;
	}

	public Join<Person, Location> getCasePersonAddress() {
		return getOrCreate(casePersonAddress, Person.ADDRESS, JoinType.LEFT, getCasePerson(), this::setCasePersonAddress);
	}

	private void setCasePersonAddress(Join<Person, Location> casePersonAddress) {
		this.casePersonAddress = casePersonAddress;
	}

	public Join<Person, Location> getContactPersonAddress() {
		return getOrCreate(contactPersonAddress, Person.ADDRESS, JoinType.LEFT, getContactPerson(), this::setContactPersonAddress);
	}

	private void setContactPersonAddress(Join<Person, Location> contactPersonAddress) {
		this.contactPersonAddress = contactPersonAddress;
	}
}
