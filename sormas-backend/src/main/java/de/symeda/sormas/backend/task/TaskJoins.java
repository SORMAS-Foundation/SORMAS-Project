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
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentJoins;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryJoins;
import de.symeda.sormas.backend.user.User;

public class TaskJoins extends QueryJoins<Task> {

	private Join<Task, Case> caze;
	private Join<Task, Event> event;
	private Join<Task, Contact> contact;
	private Join<Task, User> creator;
	private Join<Task, User> assignee;
	private Join<Task, User> assignedBy;
	private Join<Task, TravelEntry> travelEntry;
	private Join<Task, Environment> environment;
	private Join<Task, User> taskObservers;

	private CaseJoins caseJoins;
	private ContactJoins contactJoins;
	private EventJoins eventJoins;
	private TravelEntryJoins travelEntryJoins;
	private EnvironmentJoins environmentJoins;

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

	public EventJoins getEventJoins() {
		return getOrCreate(eventJoins, () -> new EventJoins(getEvent()), this::setEventJoins);
	}

	private void setEventJoins(EventJoins eventJoins) {
		this.eventJoins = eventJoins;
	}

	public Join<Task, TravelEntry> getTravelEntry() {
		return getOrCreate(travelEntry, Task.TRAVEL_ENTRY, JoinType.LEFT, this::setTravelEntry);
	}

	private void setTravelEntry(Join<Task, TravelEntry> travelEntry) {
		this.travelEntry = travelEntry;
	}

	public TravelEntryJoins getTravelEntryJoins() {
		return getOrCreate(travelEntryJoins, () -> new TravelEntryJoins(getTravelEntry()), this::setTravelEntryJoins);
	}

	private void setTravelEntryJoins(TravelEntryJoins travelEntryJoins) {
		this.travelEntryJoins = travelEntryJoins;
	}

	public Join<TravelEntry, Region> getTravelEntryResponsibleRegion() {
		return getTravelEntryJoins().getResponsibleRegion();
	}

	public Join<TravelEntry, District> getTravelEntryResponsibleDistrict() {
		return getTravelEntryJoins().getResponsibleDistrict();
	}

	public Join<TravelEntry, Community> getTravelEntryResponsibleCommunity() {
		return getTravelEntryJoins().getResponsibleCommunity();
	}

	public Join<TravelEntry, Person> getTravelEntryPerson() {
		return getTravelEntryJoins().getPerson();
	}

	public Join<Task, Environment> getEnvironment() {
		return getOrCreate(environment, Task.ENVIRONMENT, JoinType.LEFT, this::setEnvironment);
	}

	public void setEnvironment(Join<Task, Environment> environment) {
		this.environment = environment;
	}

	public EnvironmentJoins getEnvironmentJoins() {
		return getOrCreate(environmentJoins, () -> new EnvironmentJoins(getEnvironment()), this::setEnvironmentJoins);
	}

	public void setEnvironmentJoins(EnvironmentJoins environmentJoins) {
		this.environmentJoins = environmentJoins;
	}

	public Join<Environment, User> getEnvironmentReportingUser() {
		return getEnvironmentJoins().getReportingUser();
	}

	public Join<Environment, User> getEnvironmentResponsibleUser() {
		return getEnvironmentJoins().getResponsibleUser();
	}

	public Join<Environment, Location> getEnvironmentLocation() {
		return getEnvironmentJoins().getLocation();
	}

	public Join<Location, Region> getEnvironmentRegion() {
		return getEnvironmentJoins().getRegion();
	}

	public Join<Location, District> getEnvironmentDistrict() {
		return getEnvironmentJoins().getDistrict();
	}

	public Join<Location, Community> getEnvironmentCommunity() {
		return getEnvironmentJoins().getCommunity();
	}

	public Join<Event, User> getEventReportingUser() {
		return getEventJoins().getReportingUser();
	}

	public Join<Event, User> getEventResponsibleUser() {
		return getEventJoins().getResponsibleUser();
	}

	public Join<Event, Location> getEventLocation() {
		return getEventJoins().getLocation();
	}

	public Join<Location, Region> getEventRegion() {
		return getEventJoins().getRegion();
	}

	public Join<Location, District> getEventDistrict() {
		return getEventJoins().getDistrict();
	}

	public Join<Location, Community> getEventCommunity() {
		return getEventJoins().getCommunity();
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

	public Join<Task, User> getAssignedBy() {
		return getOrCreate(assignedBy, Task.ASSIGNED_BY_USER, JoinType.LEFT, this::setAssignedBy);
	}

	public void setAssignedBy(Join<Task, User> assignedBy) {
		this.assignedBy = assignedBy;
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

	public Join<Task, User> getTaskObservers() {
		return getOrCreate(taskObservers, Task.OBSERVER_USER, JoinType.LEFT, this::setTaskObservers);
	}

	public void setTaskObservers(Join<Task, User> taskObservers) {
		this.taskObservers = taskObservers;
	}
}
