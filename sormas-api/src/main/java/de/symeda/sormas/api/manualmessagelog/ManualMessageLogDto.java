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

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class ManualMessageLogDto extends EntityDto {

	private MessageType messageType;
	private Date sentDate;
	private UserReferenceDto sendingUser;
	private PersonReferenceDto recipientPerson;

	public ManualMessageLogDto() {
	}

	public ManualMessageLogDto(MessageType messageType, Date sentDate, UserReferenceDto sendingUser, PersonReferenceDto recipientPerson) {
		this.messageType = messageType;
		this.sentDate = sentDate;
		this.sendingUser = sendingUser;
		this.recipientPerson = recipientPerson;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public UserReferenceDto getSendingUser() {
		return sendingUser;
	}

	public void setSendingUser(UserReferenceDto sendingUser) {
		this.sendingUser = sendingUser;
	}

	public PersonReferenceDto getRecipientPerson() {
		return recipientPerson;
	}

	public void setRecipientPerson(PersonReferenceDto recipientPerson) {
		this.recipientPerson = recipientPerson;
	}
}
