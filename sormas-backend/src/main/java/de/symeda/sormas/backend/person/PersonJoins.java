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

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.immunization.Immunization;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;

public class PersonJoins<T> extends AbstractDomainObjectJoins<T, Person> {

	private Join<Person, Case> caze;
	private Join<Person, Contact> contact;
	private Join<Person, EventParticipant> eventParticipant;
	private Join<Person, Immunization> immunization;
	private Join<Person, TravelEntry> travelEntry;

	public PersonJoins(From<T, Person> root) {
		super(root);
	}

	public Join<Person, Case> getCaze() {
		return getOrCreate(caze, Person.CASES, JoinType.LEFT, this::setCaze);
	}

	private void setCaze(Join<Person, Case> caze) {
		this.caze = caze;
	}

	public Join<Person, Contact> getContact() {
		return getOrCreate(contact, Person.CONTACTS, JoinType.LEFT, this::setContact);
	}

	private void setContact(Join<Person, Contact> contact) {
		this.contact = contact;
	}

	public Join<Person, EventParticipant> getEventParticipant() {
		return getOrCreate(eventParticipant, Person.EVENT_PARTICIPANTS, JoinType.LEFT, this::setEventParticipant);
	}

	private void setEventParticipant(Join<Person, EventParticipant> eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	public Join<Person, Immunization> getImmunization() {
		return getOrCreate(immunization, Person.IMMUNIZATIONS, JoinType.LEFT, this::setImmunization);
	}

	public void setImmunization(Join<Person, Immunization> immunization) {
		this.immunization = immunization;
	}

	public Join<Person, TravelEntry> getTravelEntry() {
		return getOrCreate(travelEntry, Person.TRAVEL_ENTRIES, JoinType.LEFT, this::setTravelEntry);
	}

	public void setTravelEntry(Join<Person, TravelEntry> travelEntry) {
		this.travelEntry = travelEntry;
	}
}
