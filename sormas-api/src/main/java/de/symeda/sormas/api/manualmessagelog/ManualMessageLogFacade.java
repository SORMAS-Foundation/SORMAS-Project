/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.manualmessagelog;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;

@Remote
public interface ManualMessageLogFacade {

    List<ManualMessageLogIndexDto> getEmailsByPersonAndCase(PersonReferenceDto person, CaseReferenceDto caze);

    List<ManualMessageLogIndexDto> getEmailsPersonAndContact(PersonReferenceDto person, ContactReferenceDto contact);

    List<ManualMessageLogIndexDto> getEmailsByPersonAndEventParticipant(PersonReferenceDto person, EventParticipantReferenceDto eventParticipant);

    List<ManualMessageLogIndexDto> getEmailsByPersonAndTravelEntry(PersonReferenceDto person, TravelEntryReferenceDto travelEntry);
}
