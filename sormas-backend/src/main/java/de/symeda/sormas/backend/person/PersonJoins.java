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

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantJoins;
import de.symeda.sormas.backend.immunization.ImmunizationJoins;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationJoins;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryJoins;

public class PersonJoins extends QueryJoins<Person> {

	private PersonAssociation personAssociation;

	private Join<Person, Case> caze;
	private Join<Person, Contact> contact;
	private Join<Person, EventParticipant> eventParticipant;
	private Join<Person, Immunization> immunization;
	private Join<Person, TravelEntry> travelEntry;
	private Join<Person, Location> address;
	private Join<Person, Country> birthCountry;
	private Join<Person, Country> citizenship;
	private Join<Person, List<Location>> addresses;

	private LocationJoins addressJoins;
	private CaseJoins caseJoins;
	private ContactJoins contactJoins;
	private EventParticipantJoins eventParticipantJoins;
	private ImmunizationJoins immunizationJoins;
	private TravelEntryJoins travelEntryJoins;

	public PersonJoins(From<?, Person> root) {
		super(root);
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

	public CaseJoins getCaseJoins() {
		return getOrCreate(caseJoins, () -> new CaseJoins(getCaze()), this::setCaseJoins);
	}

	private void setCaseJoins(CaseJoins caseJoins) {
		this.caseJoins = caseJoins;
	}

	public Join<Person, Contact> getContact() {
		return getOrCreate(contact, Person.CONTACTS, getJoinType(PersonAssociation.CONTACT), this::setContact);
	}

	private void setContact(Join<Person, Contact> contact) {
		this.contact = contact;
	}

	public ContactJoins getContactJoins() {
		return getOrCreate(contactJoins, () -> new ContactJoins(getContact()), this::setContactJoins);
	}

	private void setContactJoins(ContactJoins contactJoins) {
		this.contactJoins = contactJoins;
	}

	public Join<Person, EventParticipant> getEventParticipant() {
		return getOrCreate(eventParticipant, Person.EVENT_PARTICIPANTS, getJoinType(PersonAssociation.EVENT_PARTICIPANT), this::setEventParticipant);
	}

	private void setEventParticipant(Join<Person, EventParticipant> eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	public EventParticipantJoins getEventParticipantJoins() {
		return getOrCreate(eventParticipantJoins, () -> new EventParticipantJoins(getEventParticipant()), this::setEventParticipantJoins);
	}

	private void setEventParticipantJoins(EventParticipantJoins eventParticipantJoins) {
		this.eventParticipantJoins = eventParticipantJoins;
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

	public Join<Person, List<Location>> getAddresses() {
		return getOrCreate(addresses, Person.ADDRESSES, JoinType.LEFT, this::setAddresses);
	}

	private void setAddresses(Join<Person, List<Location>> personAddresses) {
		this.addresses = personAddresses;
	}

	public LocationJoins getAddressJoins() {
		return getOrCreate(addressJoins, () -> new LocationJoins(getAddress()), this::setAddressJoins);
	}

	private void setAddressJoins(LocationJoins addressJoins) {
		this.addressJoins = addressJoins;
	}

	public ImmunizationJoins getImmunizationJoins() {
		return getOrCreate(immunizationJoins, () -> new ImmunizationJoins(getImmunization()), this::setImmunizationJoins);
	}

	private void setImmunizationJoins(ImmunizationJoins immunizationJoins) {
		this.immunizationJoins = immunizationJoins;
	}

	public TravelEntryJoins getTravelEntryJoins() {
		return getOrCreate(travelEntryJoins, () -> new TravelEntryJoins(getTravelEntry()), this::setTravelEntryJoins);
	}

	private void setTravelEntryJoins(TravelEntryJoins travelEntryJoins) {
		this.travelEntryJoins = travelEntryJoins;
	}
}
