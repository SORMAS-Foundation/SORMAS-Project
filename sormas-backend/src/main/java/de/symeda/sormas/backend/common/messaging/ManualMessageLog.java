package de.symeda.sormas.backend.common.messaging;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

@Entity
@Audited
public class ManualMessageLog extends AbstractDomainObject {

	public static final String TABLE_NAME = "manualmessagelog";

	public static final String MESSAGE_TYPE = "messageType";
	public static final String SENT_DATE = "sentDate";
	public static final String SENDING_USER = "sendingUser";
	public static final String RECIPIENT_PERSON = "recipientPerson";

	private MessageType messageType;
	private Date sentDate;
	private User sendingUser;
	private Person recipientPerson;

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

	@ManyToOne(cascade = {})
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
}
