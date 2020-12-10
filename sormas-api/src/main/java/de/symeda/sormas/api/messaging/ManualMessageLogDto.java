package de.symeda.sormas.api.messaging;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class ManualMessageLogDto implements Serializable {

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
