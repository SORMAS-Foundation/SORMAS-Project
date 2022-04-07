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

package de.symeda.sormas.backend.event;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObjectJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

public class EventJoins<T> extends AbstractDomainObjectJoins<T, Event> {

	private Join<Event, User> reportingUser;
	private Join<Event, User> responsibleUser;

	private Join<Event, Location> location;
	private Join<Location, Region> region;
	private Join<Location, District> district;
	private Join<Location, Community> community;
	private Join<Location, Facility> facility;

	private Join<Event, EventParticipant> eventParticipants;
	private Join<EventParticipant, Person> eventParticipantPersons;
	private Join<EventParticipant, Case> eventParticipantCases;

	private Join<Event, EventGroup> eventGroup;

	private Join<Event, Event> superordinateEvent;

	public EventJoins(From<T, Event> event) {
		super(event);
	}

	public Join<Event, User> getReportingUser() {
		return getOrCreate(reportingUser, Event.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<Event, User> reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Join<Event, User> getResponsibleUser() {
		return getOrCreate(responsibleUser, Event.RESPONSIBLE_USER, JoinType.LEFT, this::setResponsibleUser);
	}

	private void setResponsibleUser(Join<Event, User> responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public Join<Event, Location> getLocation() {
		return getOrCreate(location, Event.EVENT_LOCATION, JoinType.LEFT, this::setLocation);
	}

	private void setLocation(Join<Event, Location> location) {
		this.location = location;
	}

	public Join<Location, Region> getRegion() {
		return getOrCreate(region, Location.REGION, JoinType.LEFT, getLocation(), this::setRegion);
	}

	private void setRegion(Join<Location, Region> region) {
		this.region = region;
	}

	public Join<Location, District> getDistrict() {
		return getOrCreate(district, Location.DISTRICT, JoinType.LEFT, getLocation(), this::setDistrict);
	}

	private void setDistrict(Join<Location, District> district) {
		this.district = district;
	}

	public Join<Location, Community> getCommunity() {
		return getOrCreate(community, Location.COMMUNITY, JoinType.LEFT, getLocation(), this::setCommunity);
	}

	private void setCommunity(Join<Location, Community> community) {
		this.community = community;
	}

	public Join<Location, Facility> getFacility() {
		return getOrCreate(facility, Location.FACILITY, JoinType.LEFT, getLocation(), this::setFacility);
	}

	private void setFacility(Join<Location, Facility> facility) {
		this.facility = facility;
	}

	public Join<Event, EventParticipant> getEventParticipants() {
		return getOrCreate(eventParticipants, Event.EVENT_PERSONS, JoinType.LEFT, this::setEventParticipants);
	}

	private void setEventParticipants(Join<Event, EventParticipant> eventParticipants) {
		this.eventParticipants = eventParticipants;
	}

	public Join<EventParticipant, Person> getEventParticipantPersons() {
		return getOrCreate(eventParticipantPersons, EventParticipant.PERSON, JoinType.LEFT, getEventParticipants(), this::setEventParticipantPersons);
	}

	private void setEventParticipantPersons(Join<EventParticipant, Person> eventParticipantPersons) {
		this.eventParticipantPersons = eventParticipantPersons;
	}

	public Join<EventParticipant, Case> getEventParticipantCases() {
		return getOrCreate(
			eventParticipantCases,
			EventParticipant.RESULTING_CASE,
			JoinType.LEFT,
			getEventParticipants(),
			this::setEventParticipantCases);
	}

	private void setEventParticipantCases(Join<EventParticipant, Case> eventParticipantCases) {
		this.eventParticipantCases = eventParticipantCases;
	}

	public Join<Event, EventGroup> getEventGroup() {
		return getOrCreate(eventGroup, Event.EVENT_GROUPS, JoinType.LEFT, this::setEventGroup);
	}

	private void setEventGroup(Join<Event, EventGroup> eventGroup) {
		this.eventGroup = eventGroup;
	}

	public Join<Event, Event> getSuperordinateEvent() {
		return getOrCreate(superordinateEvent, Event.SUPERORDINATE_EVENT, JoinType.LEFT, this::setSuperordinateEvent);
	}

	private void setSuperordinateEvent(Join<Event, Event> superordinateEvent) {
		this.superordinateEvent = superordinateEvent;
	}
}
