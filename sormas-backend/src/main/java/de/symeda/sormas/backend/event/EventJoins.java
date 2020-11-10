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

package de.symeda.sormas.backend.event;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;

public class EventJoins<T extends AbstractDomainObject> extends AbstractDomainObjectJoins<T, Event> {

	private Join<Event, EventParticipant> eventParticipant;
	private Join<EventParticipant, Person> person;
	private Join<Event, Location> location;
	private Join<Location, Region> region;
	private Join<Location, District> district;
	private Join<Location, Community> community;
	private Join<Event, User> reportingUser;

	public EventJoins(From<T, Event> event) {
		super(event);
	}

	public Join<Event, EventParticipant> getEventParticipant() {
		return getOrCreate(eventParticipant, Event.EVENT_PERSONS, JoinType.LEFT, this::setEventParticipant);
	}

	private void setEventParticipant(Join<Event, EventParticipant> eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	public Join<EventParticipant, Person> getPerson() {
		return getOrCreate(person, EventParticipant.PERSON, JoinType.LEFT, getEventParticipant(), this::setPerson);
	}

	private void setPerson(Join<EventParticipant, Person> person) {
		this.person = person;
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

	public Join<Event, User> getReportingUser() {
		return getOrCreate(reportingUser, Event.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<Event, User> reportingUser) {
		this.reportingUser = reportingUser;
	}
}
