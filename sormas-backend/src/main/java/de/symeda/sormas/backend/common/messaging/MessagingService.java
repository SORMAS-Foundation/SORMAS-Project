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
import java.util.function.Supplier;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexmo.client.NexmoClientException;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
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
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	public void sendMessages(Supplier<Map<User, String>> userMessagesSupplier, MessageSubject subject, MessageType... messageTypes)
		throws NotificationDeliveryFailedException {

		if (relatedFeatureEnabled(subject.getRelatedFeatureType())) {
			for (Map.Entry<User, String> entry : userMessagesSupplier.get().entrySet()) {
				final User user = entry.getKey();
				final String messageContent = entry.getValue();
				sendMessage(user, I18nProperties.getEnumCaption(subject), messageContent, messageTypes);
			}
		}
	}

	public void sendMessages(
		Supplier<Map<User, String>> userMessagesSupplier,
		MessageSubject subject,
		Object[] subjectParameters,
		MessageType... messageTypes)
		throws NotificationDeliveryFailedException {

		if (relatedFeatureEnabled(subject.getRelatedFeatureType())) {
			for (Map.Entry<User, String> entry : userMessagesSupplier.get().entrySet()) {
				final User user = entry.getKey();
				final String messageContent = entry.getValue();
				sendMessage(user, String.format(I18nProperties.getEnumCaption(subject), subjectParameters), messageContent, messageTypes);
			}
		}
	}

	private boolean relatedFeatureEnabled(FeatureType relatedFeatureType) {
		return featureConfigurationFacade.isFeatureEnabled(relatedFeatureType);
	}

	private void sendMessage(User recipient, String subject, String messageContent, MessageType... messageTypes)
		throws NotificationDeliveryFailedException {
		// Don't send notifications to users that initiated an action
		if (recipient.equals(userService.getCurrentUser()) || !recipient.isActive()) {
			return;
		}

		final String emailAddress = recipient.getUserEmail();
		final String phoneNumber = recipient.getPhone();
		final String recipientUuid = recipient.getUuid();
		sendMessage(subject, messageContent, emailAddress, phoneNumber, recipientUuid, "user", messageTypes);
	}

	public void sendMessage(Person recipient, String subject, String messageContent, MessageType... messageTypes)
		throws NotificationDeliveryFailedException {
		final String emailAddress = recipient.getEmailAddress();
		final String phoneNumber = recipient.getPhone();
		final String recipientUuid = recipient.getUuid();
		for (MessageType messageType : messageTypes) {
			sendMessage(subject, messageContent, emailAddress, phoneNumber, recipientUuid, "person", messageType);
			final ManualMessageLog manualMessageLog = new ManualMessageLog();
			manualMessageLog.setMessageType(messageType);
			manualMessageLog.setRecipientPerson(recipient);
			manualMessageLog.setSendingUser(userService.getCurrentUser());
			manualMessageLog.setSentDate(new Date());
			manualMessageLogService.ensurePersisted(manualMessageLog);
		}
	}

	private void sendMessage(
		String subject,
		String messageContent,
		String emailAddress,
		String phoneNumber,
		String recipientUuid,
		final String recipientType,
		MessageType... messageTypes)
		throws NotificationDeliveryFailedException {

		boolean isSmsServiceSetUp = configFacade.isSmsServiceSetUp();
		for (MessageType messageType : messageTypes) {
			if (messageType == MessageType.EMAIL && DataHelper.isNullOrEmpty(emailAddress)) {
				logger.info(String.format("Tried to send an email to a %s without an email address (UUID: %s).", recipientType, recipientUuid));
			} else if (isSmsServiceSetUp && messageType == MessageType.SMS && DataHelper.isNullOrEmpty(phoneNumber)) {
				logger.info(String.format("Tried to send an SMS to a %s without a phone number (UUID: %s).", recipientType, recipientUuid));
			} else {
				try {
					if (messageType == MessageType.EMAIL) {
						emailService.sendEmail(emailAddress, subject, messageContent);
					} else if (isSmsServiceSetUp && messageType == MessageType.SMS) {
						smsService.sendSms(phoneNumber, messageContent);
					}
				} catch (MessagingException e) {
					logError(recipientUuid, recipientType, messageType);
					throw new NotificationDeliveryFailedException("Email could not be sent due to an unexpected error.", MessageType.EMAIL, e);
				} catch (IOException | NexmoClientException e) {
					logError(recipientUuid, recipientType, messageType);
					throw new NotificationDeliveryFailedException("SMS could not be sent due to an unexpected error.", MessageType.SMS, e);
				} catch (InvalidPhoneNumberException e) {
					logError(recipientUuid, recipientType, messageType);
					throw new NotificationDeliveryFailedException("SMS could not be sent because of an invalid phone number.", MessageType.SMS, e);
				}
			}
		}
	}

	private void logError(String recipientUuid, String recipientType, MessageType messageType) {
		logger.error(String.format("Failed to send %s to %s with UUID %s.", messageType, recipientType, recipientUuid));
	}
}
