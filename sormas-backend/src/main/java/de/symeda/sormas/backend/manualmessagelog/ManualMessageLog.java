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

package de.symeda.sormas.backend.manualmessagelog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.manualmessagelog.MessageType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.user.User;

@Entity
public class ManualMessageLog extends AbstractDomainObject {

    public static final String TABLE_NAME = "manualmessagelog";

    public static final String MESSAGE_TYPE = "messageType";
    public static final String SENT_DATE = "sentDate";
    public static final String SENDING_USER = "sendingUser";
    public static final String RECIPIENT_PERSON = "recipientPerson";
    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String USED_TEMPLATE = "usedTemplate";

    public static final String CASE = "caze";
    public static final String CONTACT = "contact";
    public static final String EVENT_PARTICIPANT = "eventParticipant";
    public static final String TRAVEL_ENTRY = "travelEntry";

    private MessageType messageType;
    private Date sentDate;
    private User sendingUser;
    private Person recipientPerson;
    private String emailAddress;
    private String usedTemplate;

    private Case caze;
    private Contact contact;
    private EventParticipant eventParticipant;
    private TravelEntry travelEntry;

    @Enumerated(EnumType.STRING)
    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    public User getSendingUser() {
        return sendingUser;
    }

    public void setSendingUser(User sendingUser) {
        this.sendingUser = sendingUser;
    }

    @ManyToOne(cascade = {})
    @JoinColumn(nullable = false)
    public Person getRecipientPerson() {
        return recipientPerson;
    }

    public void setRecipientPerson(Person recipientPerson) {
        this.recipientPerson = recipientPerson;
    }

    @Column(length = FieldConstraints.CHARACTER_LIMIT_DEFAULT)
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Column(length = FieldConstraints.CHARACTER_LIMIT_BIG)
    public String getUsedTemplate() {
        return usedTemplate;
    }

    public void setUsedTemplate(String usedTemplate) {
        this.usedTemplate = usedTemplate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Case getCaze() {
        return caze;
    }

    public void setCaze(Case caze) {
        this.caze = caze;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public EventParticipant getEventParticipant() {
        return eventParticipant;
    }

    public void setEventParticipant(EventParticipant eventParticipant) {
        this.eventParticipant = eventParticipant;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public TravelEntry getTravelEntry() {
        return travelEntry;
    }

    public void setTravelEntry(TravelEntry travelEntry) {
        this.travelEntry = travelEntry;
    }
}
