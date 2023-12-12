/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.common.messaging;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexmo.client.NexmoClientException;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

/**
 * Service used to send email and SMS messages to SORMAS users & persons.
 * 
 * @author Maté Strysewske
 */
@Stateless(name = "MessagingService")
@LocalBean
public class MessagingService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private UserService userService;
	@EJB
	private EmailService emailService;
	@EJB
	private SmsService smsService;
	@EJB
	private ManualMessageLogService manualMessageLogService;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	public void sendEmail(Map<User, String> userMessages, MessageSubject subject, Object[] subjectParameters)
		throws NotificationDeliveryFailedException {

		sendMessage(userMessages, subject, subjectParameters, User::getUserEmail, this::sendEmail);
	}

	public void sendSms(Map<User, String> userMessages, MessageSubject subject, Object[] subjectParameters)
		throws NotificationDeliveryFailedException {

		sendMessage(userMessages, subject, subjectParameters, User::getPhone, this::sendSms);
	}

	public void sendManualMessage(Person recipient, String subject, String messageContent, MessageType... messageTypes)
		throws NotificationDeliveryFailedException {
		final String emailAddress = recipient.getEmailAddress();
		final String phoneNumber = recipient.getPhone();
		final String recipientUuid = recipient.getUuid();
		final String recipientType = "person";

		for (MessageType messageType : messageTypes) {
			if (messageType == MessageType.EMAIL) {
				sendEmail(subject, messageContent, emailAddress, recipientUuid, recipientType);
			} else if (messageType == MessageType.SMS) {
				sendSms(subject, messageContent, phoneNumber, recipientUuid, recipientType);
			}

			final ManualMessageLog manualMessageLog = new ManualMessageLog();
			manualMessageLog.setMessageType(messageType);
			manualMessageLog.setRecipientPerson(recipient);
			manualMessageLog.setSendingUser(userService.getCurrentUser());
			manualMessageLog.setSentDate(new Date());
			manualMessageLogService.ensurePersisted(manualMessageLog);
		}
	}

	private void sendMessage(
		Map<User, String> userMessages,
		MessageSubject subject,
		Object[] subjectParameters,
		Function<User, String> contactInfoSupplier,
		Messenger messenger)
		throws NotificationDeliveryFailedException {

		for (Map.Entry<User, String> entry : userMessages.entrySet()) {
			final User recipient = entry.getKey();
			final String messageContent = entry.getValue();

			// Don't send notifications to users that initiated an action
			if (recipient.equals(userService.getCurrentUser()) || !recipient.isActive()) {
				return;
			}

			final String recipientUuid = recipient.getUuid();

			messenger.send(
				String.format(I18nProperties.getEnumCaption(subject), subjectParameters),
				messageContent,
				contactInfoSupplier.apply(recipient),
				recipientUuid,
				"user");
		}
	}

	private void sendEmail(String subject, String messageContent, String emailAddress, String recipientUuid, final String recipientType)
		throws NotificationDeliveryFailedException {

		if (DataHelper.isNullOrEmpty(emailAddress)) {
			logger.info(String.format("Tried to send an email to a %s without an email address (UUID: %s).", recipientType, recipientUuid));
		} else {
			try {
				emailService.sendEmailAsync(emailAddress, subject, messageContent);
			} catch (MessagingException e) {
				logError(recipientUuid, recipientType, MessageType.EMAIL);
				throw new NotificationDeliveryFailedException("Email could not be sent due to an unexpected error.", MessageType.EMAIL, e);
			}
		}
	}

	private void sendSms(String subject, String messageContent, String phoneNumber, String recipientUuid, final String recipientType)
		throws NotificationDeliveryFailedException {

		boolean isSmsServiceSetUp = configFacade.isSmsServiceSetUp();

		if (isSmsServiceSetUp && DataHelper.isNullOrEmpty(phoneNumber)) {
			logger.info(String.format("Tried to send an SMS to a %s without a phone number (UUID: %s).", recipientType, recipientUuid));
		} else {
			try {
				if (isSmsServiceSetUp) {
					smsService.sendSms(phoneNumber, messageContent);
				}
			} catch (IOException | NexmoClientException e) {
				logError(recipientUuid, recipientType, MessageType.SMS);
				throw new NotificationDeliveryFailedException("SMS could not be sent due to an unexpected error.", MessageType.SMS, e);
			} catch (InvalidPhoneNumberException e) {
				logError(recipientUuid, recipientType, MessageType.SMS);
				throw new NotificationDeliveryFailedException("SMS could not be sent because of an invalid phone number.", MessageType.SMS, e);
			}
		}
	}

	private void logError(String recipientUuid, String recipientType, MessageType messageType) {
		logger.error(String.format("Failed to send %s to %s with UUID %s.", messageType, recipientType, recipientUuid));
	}

	interface Messenger {

		void send(String subject, String messageContent, String contactInfo, String recipientUuid, String recipientType)
			throws NotificationDeliveryFailedException;
	}
}
