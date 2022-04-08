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
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.user.User;

public class TaskJoins extends QueryJoins<Task> {

	// TODO #8688: Totally remove cached JPA Joins beyond Event and TravelEntry, use EventJoins and TravelEntryJoins
	private Join<Task, Case> caze;
	private Join<Task, Event> event;
	private Join<Event, User> eventReportingUser;
	private Join<Event, User> eventResponsibleUser;
	private Join<Event, Location> eventLocation;
	private Join<Location, Region> eventRegion;
	private Join<Location, District> eventDistrict;
	private Join<Location, Community> eventCommunity;
	private Join<Task, Contact> contact;
	private Join<Task, User> creator;
	private Join<Task, User> assignee;
	private Join<Task, TravelEntry> travelEntry;
	private Join<TravelEntry, Region> travelEntryResponsibleRegion;
	private Join<TravelEntry, District> travelEntryResponsibleDistrict;
	private Join<TravelEntry, Community> travelEntryResponsibleCommunity;
	private Join<TravelEntry, Person> travelEntryPerson;

	private CaseJoins caseJoins;
	private ContactJoins contactJoins;

	public TaskJoins(From<?, Task> root) {
		super(root);
	}

	public Join<Task, Case> getCaze() {
		return getOrCreate(caze, Task.CAZE, JoinType.LEFT, this::setCaze);
	}

	private void setCaze(Join<Task, Case> caze) {
		this.caze = caze;
	}

	public Join<Case, Person> getCasePerson() {
		return getCaseJoins().getPerson();
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
		return getContactJoins().getPerson();
	}

	public Join<Contact, Case> getContactCase() {
		return getContactJoins().getCaze();
	}

	public Join<Case, Person> getContactCasePerson() {
		return getContactJoins().getCaseJoins().getPerson();
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

	public Join<Contact, Region> getContactRegion() {
		return getContactJoins().getRegion();
	}

	public Join<Contact, District> getContactDistrict() {
		return getContactJoins().getDistrict();
	}

	public Join<Contact, Community> getContactCommunity() {
		return getContactJoins().getCommunity();
	}

	public Join<Person, Location> getCasePersonAddress() {
		return getCaseJoins().getPersonJoins().getAddress();
	}

	public Join<Person, Location> getContactPersonAddress() {
		return getContactJoins().getPersonJoins().getAddress();
	}

	public CaseJoins getCaseJoins() {
		return getOrCreate(caseJoins, () -> new CaseJoins(getCaze()), this::setCaseJoins);
	}

	private void setCaseJoins(CaseJoins caseJoins) {
		this.caseJoins = caseJoins;
	}

	public ContactJoins getContactJoins() {
		return getOrCreate(contactJoins, () -> new ContactJoins(getContact()), this::setContactJoins);
	}

	private void setContactJoins(ContactJoins contactJoins) {
		this.contactJoins = contactJoins;
	}
}
