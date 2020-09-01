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
package de.symeda.sormas.backend.event;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.sample.Sample;

import javax.persistence.*;
import java.util.Set;

@Entity
@Audited
public class EventParticipant extends CoreAdo {

	private static final long serialVersionUID = -9006001699517297107L;

	public static final String TABLE_NAME = "eventparticipant";

	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final String RESULTING_CASE = "resultingCase";
	public static final String EVENT_PARTICIPANT_EMAIL="eventParticipantEmail";
	public static final String EVENT_PARTICIPANT_PHONE_NUMBER="eventParticipantPhoneNumber";

	private Event event;
	private Person person;
	private String involvementDescription;
	private Case resultingCase;
	private Set<Sample> samples;

	private String eventParticipantEmail;
	private String eventParticipantPhoneNumber;


	@ManyToOne(cascade = {})
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Column(length = 512, nullable = false)
	public String getInvolvementDescription() {
		return involvementDescription;
	}

	public void setInvolvementDescription(String involvementDescription) {
		this.involvementDescription = involvementDescription;
	}

	@Override
	public String toString() {
		return getPerson().toString();
	}

	@ManyToOne(cascade = {})
	@JoinColumn
	public Case getResultingCase() {
		return resultingCase;
	}

	public void setResultingCase(Case resultingCase) {
		this.resultingCase = resultingCase;
	}

	@OneToMany(mappedBy = Sample.ASSOCIATED_EVENT_PARTICIPANT, fetch = FetchType.LAZY)
	public Set<Sample> getSamples() {
		return samples;
	}

	public void setSamples(Set<Sample> samples) {
		this.samples = samples;
	}

	public String getEventParticipantEmail() { return eventParticipantEmail; }

	public void setEventParticipantEmail(String eventParticipantEmail) {
		this.eventParticipantEmail = eventParticipantEmail;
	}

	public String getEventParticipantPhoneNumber() { return eventParticipantPhoneNumber; }

	public void setEventParticipantPhoneNumber(String eventParticipantPhoneNumber) {
		this.eventParticipantPhoneNumber = eventParticipantPhoneNumber;
	}
}
