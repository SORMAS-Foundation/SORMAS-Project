/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.externalmessage.processing;

import java.io.Serializable;

import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;

public class PickOrCreateEntryResult implements Serializable {

	private static final long serialVersionUID = 5902907041512754766L;

	private CaseSelectionDto caze;
	private SimilarContactDto contact;
	private SimilarEventParticipantDto eventParticipant;
	private boolean newCase;
	private boolean newContact;
	private boolean newEventParticipant;

	public CaseSelectionDto getCaze() {
		return caze;
	}

	public void setCaze(CaseSelectionDto caze) {
		this.caze = caze;
	}

	public SimilarContactDto getContact() {
		return contact;
	}

	public void setContact(SimilarContactDto contact) {
		this.contact = contact;
	}

	public SimilarEventParticipantDto getEventParticipant() {
		return eventParticipant;
	}

	public void setEventParticipant(SimilarEventParticipantDto eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	public boolean isNewCase() {
		return newCase;
	}

	public void setNewCase(boolean newCase) {
		this.newCase = newCase;
	}

	public boolean isNewContact() {
		return newContact;
	}

	public void setNewContact(boolean newContact) {
		this.newContact = newContact;
	}

	public boolean isNewEventParticipant() {
		return newEventParticipant;
	}

	public boolean isSelectedCase() {
		return caze != null;
	}

	public boolean isSelectedContact() {
		return contact != null;
	}

	public boolean isSelectedEventParticipant() {
		return eventParticipant != null;
	}

	public void setNewEventParticipant(boolean newEventParticipant) {
		this.newEventParticipant = newEventParticipant;
	}
}
