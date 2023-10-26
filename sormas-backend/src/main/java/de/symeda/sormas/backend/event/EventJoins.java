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

import java.util.Objects;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.action.Action;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationJoins;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

public class EventJoins extends QueryJoins<Event> {

	private Join<Event, User> reportingUser;
	private Join<Event, User> responsibleUser;
	private Join<Event, Location> location;
	private From<?, EventParticipant> eventParticipants;
	private Join<Event, EventGroup> eventGroup;
	private Join<Event, Action> eventActions;
	private Join<Event, Event> superordinateEvent;

	private LocationJoins locationJoins;
	private EventParticipantJoins eventParticipantJoins;

	public EventJoins(From<?, Event> event) {
		super(event);
	}

	/**
	 * For use cases where the EventParticipant is the origin for the query
	 */
	public EventJoins(From<?, EventParticipant> eventParticipants, From<?, Event> event) {
		this(event);
		Objects.requireNonNull(eventParticipants);
		this.eventParticipants = eventParticipants;
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
		return getLocationJoins().getRegion();
	}

	public Join<Location, District> getDistrict() {
		return getLocationJoins().getDistrict();
	}

	public Join<Location, Community> getCommunity() {
		return getLocationJoins().getCommunity();
	}

	public Join<Location, Facility> getFacility() {
		return getLocationJoins().getFacility();
	}

	public From<?, EventParticipant> getEventParticipants() {

		if (eventParticipants == null) {
			setEventParticipants(getRoot().join(Event.EVENT_PARTICIPANTS, JoinType.LEFT));
		}
		return eventParticipants;
	}

	private void setEventParticipants(From<?, EventParticipant> eventParticipants) {
		this.eventParticipants = eventParticipants;
	}

	public Join<EventParticipant, Person> getEventParticipantPersons() {
		return getEventParticipantJoins().getPerson();
	}

	public Join<EventParticipant, Case> getEventParticipantCases() {
		return getEventParticipantJoins().getResultingCase();
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

	public Join<Event, Action> getEventActions() {
		return getOrCreate(eventActions, Event.ACTIONS, JoinType.LEFT, this::setEventActions);
	}

	private void setEventActions(Join<Event, Action> eventActions) {
		this.eventActions = eventActions;
	}

	public LocationJoins getLocationJoins() {
		return getOrCreate(locationJoins, () -> new LocationJoins(getLocation()), this::setLocationJoins);
	}

	private void setLocationJoins(LocationJoins locationJoins) {
		this.locationJoins = locationJoins;
	}

	public EventParticipantJoins getEventParticipantJoins() {
		return getOrCreate(eventParticipantJoins, () -> new EventParticipantJoins(getEventParticipants()), this::setEventParticipantJoins);
	}

	private void setEventParticipantJoins(EventParticipantJoins eventParticipantJoins) {
		this.eventParticipantJoins = eventParticipantJoins;
	}
}
