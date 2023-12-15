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

package de.symeda.sormas.api.manualmessagelog;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class ManualMessageLogCriteria extends BaseCriteria {

    private MessageType messageType;
    private CaseReferenceDto caze;
    private ContactReferenceDto contact;
    private EventParticipantReferenceDto eventParticipant;
    private TravelEntryReferenceDto travelEntry;
    private Boolean withTemplate;

    public MessageType getMessageType() {
        return messageType;
    }

    public ManualMessageLogCriteria messageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    public CaseReferenceDto getCaze() {
        return caze;
    }

    public ManualMessageLogCriteria caze(CaseReferenceDto caze) {
        this.caze = caze;
        return this;
    }

    public ContactReferenceDto getContact() {
        return contact;
    }

    public ManualMessageLogCriteria contact(ContactReferenceDto contact) {
        this.contact = contact;
        return this;
    }

    public EventParticipantReferenceDto getEventParticipant() {
        return eventParticipant;
    }

    public ManualMessageLogCriteria eventParticipant(EventParticipantReferenceDto eventParticipant) {
        this.eventParticipant = eventParticipant;
        return this;
    }

    public TravelEntryReferenceDto getTravelEntry() {
        return travelEntry;
    }

    public ManualMessageLogCriteria travelEntry(TravelEntryReferenceDto travelEntry) {
        this.travelEntry = travelEntry;
        return this;
    }

    public Boolean getWithTemplate() {
        return withTemplate;
    }

    public ManualMessageLogCriteria withTemplate(Boolean withEmailTemplate) {
        this.withTemplate = withEmailTemplate;
        return this;
    }
}
