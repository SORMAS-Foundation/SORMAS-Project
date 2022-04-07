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

package de.symeda.sormas.backend.person;

import java.util.Optional;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObjectJoins;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationJoins;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.travelentry.TravelEntry;

public class PersonJoins<T> extends AbstractDomainObjectJoins<T, Person> {

	private PersonAssociation personAssociation;
	private Join<Person, Case> caze;
	private Join<Person, Contact> contact;
	private Join<Person, EventParticipant> eventParticipant;
	private Join<Person, Immunization> immunization;
	private Join<Person, TravelEntry> travelEntry;
	private Join<Person, Location> address;
	private Join<Person, Country> birthCountry;
	private Join<Person, Country> citizenship;

	private final LocationJoins<Person> addressJoins;

	public PersonJoins(From<T, Person> root) {
		super(root);

		addressJoins = new LocationJoins<>(getAddress());
	}

	public void configure(PersonCriteria criteria) {

		this.personAssociation = Optional.ofNullable(criteria).map(e -> e.getPersonAssociation()).orElse(PersonCriteria.DEFAULT_ASSOCIATION);
	}

	/**
	 * @param personAssociation
	 *            The association from the {@link Person} to build a join for.
	 * @return Do INNER join on that association that is relevant, default to LEFT if not.<br />
	 *         Other joins have to be kept LEFT join for jurisdiction checks.
	 */
	private JoinType getJoinType(PersonAssociation personAssociation) {

		return this.personAssociation == personAssociation ? JoinType.INNER : JoinType.LEFT;
	}

	public Join<Person, Case> getCaze() {
		return getOrCreate(caze, Person.CASES, getJoinType(PersonAssociation.CASE), this::setCaze);
	}

	private void setCaze(Join<Person, Case> caze) {
		this.caze = caze;
	}

	public Join<Person, Contact> getContact() {
		return getOrCreate(contact, Person.CONTACTS, getJoinType(PersonAssociation.CONTACT), this::setContact);
	}

	private void setContact(Join<Person, Contact> contact) {
		this.contact = contact;
	}

	public Join<Person, EventParticipant> getEventParticipant() {
		return getOrCreate(eventParticipant, Person.EVENT_PARTICIPANTS, getJoinType(PersonAssociation.EVENT_PARTICIPANT), this::setEventParticipant);
	}

	private void setEventParticipant(Join<Person, EventParticipant> eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	public Join<Person, Immunization> getImmunization() {
		return getOrCreate(immunization, Person.IMMUNIZATIONS, getJoinType(PersonAssociation.IMMUNIZATION), this::setImmunization);
	}

	public void setImmunization(Join<Person, Immunization> immunization) {
		this.immunization = immunization;
	}

	public Join<Person, TravelEntry> getTravelEntry() {
		return getOrCreate(travelEntry, Person.TRAVEL_ENTRIES, getJoinType(PersonAssociation.TRAVEL_ENTRY), this::setTravelEntry);
	}

	public void setTravelEntry(Join<Person, TravelEntry> travelEntry) {
		this.travelEntry = travelEntry;
	}

	public LocationJoins<Person> getAddressJoins() {
		return addressJoins;
	}

	public Join<Person, Location> getAddress() {
		return getOrCreate(address, Person.ADDRESS, JoinType.LEFT, this::setAddress);
	}

	private void setAddress(Join<Person, Location> address) {
		this.address = address;
	}

	public Join<Person, Country> getBirthCountry() {
		return getOrCreate(birthCountry, Person.BIRTH_COUNTRY, JoinType.LEFT, this::setBirthCountry);
	}

	private void setBirthCountry(Join<Person, Country> birthCountry) {
		this.birthCountry = birthCountry;
	}

	public Join<Person, Country> getCitizenship() {
		return getOrCreate(citizenship, Person.CITIZENSHIP, JoinType.LEFT, this::setCitizenship);
	}

	private void setCitizenship(Join<Person, Country> citizenship) {
		this.citizenship = citizenship;
	}
}
