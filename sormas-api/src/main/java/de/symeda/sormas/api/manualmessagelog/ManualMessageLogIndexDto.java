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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizable;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class ManualMessageLogIndexDto extends PseudonymizableIndexDto implements Serializable, Pseudonymizable {

	private static final long serialVersionUID = -6632086079342652486L;

	private final MessageType messageType;
	private final Date sentDate;
	private UserReferenceDto sendingUser;
	@PersonalData
	@SensitiveData
	private final String emailAddress;
	private final String usedTemplate;
	private final List<String> attachedDocuments;
	private final boolean senderInJurisdiction;

	public ManualMessageLogIndexDto(
		String uuid,
		MessageType messageType,
		Date sentDate,
		String sendingUserUuid,
		String sendingUserFirstName,
		String sendingUserLastName,
		String emailAddress,
		String usedTemplate,
		Object attachedDocuments,
		boolean inJurisdiction,
		boolean senderInJurisdiction) {
		super(uuid);
		this.messageType = messageType;
		this.sentDate = sentDate;
		this.sendingUser = new UserReferenceDto(sendingUserUuid, sendingUserFirstName, sendingUserLastName);
		this.emailAddress = emailAddress;
		this.usedTemplate = usedTemplate;
		this.attachedDocuments = (List<String>) attachedDocuments;
		this.inJurisdiction = inJurisdiction;
		this.senderInJurisdiction = senderInJurisdiction;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public UserReferenceDto getSendingUser() {
		return sendingUser;
	}

	public void setSendingUser(UserReferenceDto sendingUser) {
		this.sendingUser = sendingUser;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getUsedTemplate() {
		return usedTemplate;
	}

	public List<String> getAttachedDocuments() {
		return attachedDocuments;
	}

	public boolean isSenderInJurisdiction() {
		return senderInJurisdiction;
	}
}
