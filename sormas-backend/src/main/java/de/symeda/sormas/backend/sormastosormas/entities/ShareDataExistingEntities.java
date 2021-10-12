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

package de.symeda.sormas.backend.sormastosormas.entities;

import java.util.Map;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sample.Sample;

public class ShareDataExistingEntities {

	private final Map<String, Case> cases;
	private final Map<String, Contact> contacts;
	private final Map<String, Event> events;
	private final Map<String, EventParticipant> eventParticipants;
	private final Map<String, Sample> samples;
	private final Map<String, Immunization> immunizations;

	public ShareDataExistingEntities(
		Map<String, Case> cases,
		Map<String, Contact> contacts,
		Map<String, Event> events,
		Map<String, EventParticipant> eventParticipants,
		Map<String, Sample> samples,
		Map<String, Immunization> immunizations) {
		this.cases = cases;
		this.contacts = contacts;
		this.events = events;
		this.eventParticipants = eventParticipants;
		this.samples = samples;
		this.immunizations = immunizations;
	}

	public Map<String, Case> getCases() {
		return cases;
	}

	public Map<String, Contact> getContacts() {
		return contacts;
	}

	public Map<String, Event> getEvents() {
		return events;
	}

	public Map<String, EventParticipant> getEventParticipants() {
		return eventParticipants;
	}

	public Map<String, Sample> getSamples() {
		return samples;
	}

	public Map<String, Immunization> getImmunizations() {
		return immunizations;
	}
}
