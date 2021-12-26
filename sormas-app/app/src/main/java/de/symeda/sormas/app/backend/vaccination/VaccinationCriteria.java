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

package de.symeda.sormas.app.backend.vaccination;

import java.io.Serializable;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.immunization.Immunization;

public class VaccinationCriteria implements Serializable {

	private Immunization immunization;
	private Case caze;
	private Contact contact;
	private EventParticipant eventParticipant;

	public VaccinationCriteria immunization(Immunization immunization) {
		this.immunization = immunization;
		return this;
	}

	public Immunization getImmunization() {
		return immunization;
	}

	public Case getCaze() {
		return caze;
	}

	public void caze(Case caze) {
		this.caze = caze;
	}

	public Contact getContact() {
		return contact;
	}

	public void contact(Contact contact) {
		this.contact = contact;
	}

	public EventParticipant getEventParticipant() {
		return eventParticipant;
	}

	public void eventParticipant(EventParticipant eventParticipant) {
		this.eventParticipant = eventParticipant;
	}
}
