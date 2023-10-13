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

package de.symeda.sormas.api.externalmessage.processing.labmessage;

import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.api.person.PersonDto;

public class PersonAndPickOrCreateEntryResult {

	private final PersonDto person;
	private final PickOrCreateEntryResult pickOrCreateEntryResult;

	public PersonAndPickOrCreateEntryResult(PersonDto person, PickOrCreateEntryResult pickOrCreateEntryResult) {
		this.person = person;
		this.pickOrCreateEntryResult = pickOrCreateEntryResult;
	}

	public PersonDto getPerson() {
		return person;
	}

	public boolean isNewCase() {
		return pickOrCreateEntryResult.isNewCase();
	}

	public boolean isNewContact() {
		return pickOrCreateEntryResult.isNewContact();
	}

	public boolean isNewEventParticipant() {
		return pickOrCreateEntryResult.isNewEventParticipant();
	}

	public boolean isSelectedCase() {
		return pickOrCreateEntryResult.getCaze() != null;
	}

	public CaseSelectionDto getCaze() {
		return pickOrCreateEntryResult.getCaze();
	}

	public boolean isSelectedContact() {
		return pickOrCreateEntryResult.getContact() != null;
	}

	public SimilarContactDto getContact() {
		return pickOrCreateEntryResult.getContact();
	}

	public boolean isEventParticipantSelected() {
		return pickOrCreateEntryResult.getEventParticipant() != null;
	}

	public SimilarEventParticipantDto getEventParticipant() {
		return pickOrCreateEntryResult.getEventParticipant();
	}
}
