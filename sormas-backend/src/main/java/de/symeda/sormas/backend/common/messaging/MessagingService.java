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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexmo.client.NexmoClientException;

import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

/**
 * Service used to send email and SMS messages to SORMAS users.
 * 
 * @author Maté Strysewske
 */
@Stateless(name = "MessagingService")
@LocalBean
public class MessagingService {

	// Message contents (via properties file)
	public static final String CONTENT_CASE_CLASSIFICATION_CHANGED = "notificationCaseClassificationChanged";
	public static final String CONTENT_CASE_INVESTIGATION_DONE = "notificationCaseInvestigationDone";
	public static final String CONTENT_EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED = "notificationEventParticipantCaseClassificationConfirmed";
	public static final String CONTENT_LAB_RESULT_ARRIVED = "notificationLabResultArrived";
	public static final String CONTENT_LAB_RESULT_ARRIVED_CONTACT = "notificationLabResultArrivedContact";
	public static final String CONTENT_LAB_RESULT_ARRIVED_EVENT_PARTICIPANT = "notificationLabResultArrivedEventParticipant";
	public static final String CONTENT_LAB_RESULT_ARRIVED_EVENT_PARTICIPANT_NO_DISEASE = "notificationLabResultArrivedEventParticipantNoDisease";
	public static final String CONTENT_LAB_RESULT_SPECIFIED = "notificationLabResultSpecified";
	public static final String CONTENT_LAB_RESULT_SPECIFIED_CONTACT = "notificationLabResultSpecifiedContact";
	public static final String CONTENT_LAB_RESULT_SPECIFIED_EVENT_PARTICIPANT = "notificationLabResultSpecifiedEventParticipant";
	public static final String CONTENT_LAB_RESULT_SPECIFIED_EVENT_PARTICIPANT_NO_DISEASE = "notificationLabResultSpecifiedEventParticipantNoDisease";
	public static final String CONTENT_LAB_SAMPLE_SHIPPED = "notificationLabSampleShipped";
	public static final String CONTENT_LAB_SAMPLE_SHIPPED_SHORT = "notificationLabSampleShippedShort";
	public static final String CONTENT_LAB_SAMPLE_SHIPPED_SHORT_FOR_CONTACT = "notificationLabSampleShippedShortForContact";
	public static final String CONTENT_LAB_SAMPLE_SHIPPED_SHORT_FOR_EVENT_PARTICIPANT = "notificationLabSampleShippedShortForEventParticipant";
	public static final String CONTENT_CONTACT_SYMPTOMATIC = "notificationContactSymptomatic";
	public static final String CONTENT_CONTACT_WITHOUT_CASE_SYMPTOMATIC = "notificationContactWithoutCaseSymptomatic";
	public static final String CONTENT_TASK_START_GENERAL = "notificationTaskStartGeneral";
	public static final String CONTENT_TASK_START_SPECIFIC = "notificationTaskStartSpecific";
	public static final String CONTENT_TASK_DUE_GENERAL = "notificationTaskDueGeneral";
	public static final String CONTENT_TASK_DUE_SPECIFIC = "notificationTaskDueSpecific";
	public static final String CONTENT_VISIT_COMPLETED = "notificationVisitCompleted";
	public static final String CONTENT_DISEASE_CHANGED = "notificationDiseaseChanged";

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

	/**
	 * Sends the message specified by the messageContent via mail and/or SMS, according to the messageTypes, to the specified recipient's
	 * email address and/or phone number. Logs an error if the email address or phone number is not set.
	 */
	public void sendMessage(User recipient, MessageSubject subject, String messageContent, MessageType... messageTypes)
		throws NotificationDeliveryFailedException {

		// Don't send notifications if the feature is disabled for the current MessageSubject
		if ((MessageSubject.TASK_DUE.equals(subject) || MessageSubject.TASK_START.equals(subject))
			&& !featureConfigurationFacade.isFeatureEnabled(FeatureType.TASK_NOTIFICATIONS)
			|| !MessageSubject.TASK_DUE.equals(subject)
				&& !MessageSubject.TASK_START.equals(subject)
				&& !featureConfigurationFacade.isFeatureEnabled(FeatureType.OTHER_NOTIFICATIONS)) {
			return;
		}

		sendMessage(recipient, I18nProperties.getEnumCaption(subject), messageContent, messageTypes);
	}


	/**
	 * Sends the message specified by the messageContent via mail and/or SMS, according to the messageTypes, to the specified recipient's
	 * email address and/or phone number. Logs an error if the email address or phone number is not set.
	 */
	public void sendMessage(
			User recipient,
			MessageSubject subject,
			Object[] subjectParameters,
			String messageContent,
			MessageType... messageTypes)
			throws NotificationDeliveryFailedException {

		// Don't send notifications if the feature is disabled for the current MessageSubject
		if ((MessageSubject.TASK_DUE.equals(subject) || MessageSubject.TASK_START.equals(subject))
				&& !featureConfigurationFacade.isFeatureEnabled(FeatureType.TASK_NOTIFICATIONS)
				|| !MessageSubject.TASK_DUE.equals(subject)
				&& !MessageSubject.TASK_START.equals(subject)
				&& !featureConfigurationFacade.isFeatureEnabled(FeatureType.OTHER_NOTIFICATIONS)) {
			return;
		}

		sendMessage(
				recipient,
				String.format(I18nProperties.getEnumCaption(subject), subjectParameters),
				messageContent,
				messageTypes);
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
		for (MessageType messageType : messageTypes) {
			if (messageType == MessageType.EMAIL && DataHelper.isNullOrEmpty(emailAddress)) {
				logger.info(String.format("Tried to send an email to a " + recipientType + " without an email address (UUID: %s).", recipientUuid));
			} else if (messageType == MessageType.SMS && DataHelper.isNullOrEmpty(phoneNumber)) {
				logger.info(String.format("Tried to send an SMS to a " + recipientType + " without a phone number (UUID: %s).", recipientUuid));
			} else {
				try {
					if (messageType == MessageType.EMAIL) {
						emailService.sendEmail(emailAddress, subject, messageContent);
					} else if (messageType == MessageType.SMS) {
						smsService.sendSms(phoneNumber, messageContent);
					}
				} catch (MessagingException e) {
					throw new NotificationDeliveryFailedException("Email could not be sent due to an unexpected error.", MessageType.EMAIL, e);
				} catch (IOException | NexmoClientException e) {
					throw new NotificationDeliveryFailedException("SMS could not be sent due to an unexpected error.", MessageType.SMS, e);
				} catch (InvalidPhoneNumberException e) {
					throw new NotificationDeliveryFailedException("SMS could not be sent because of an invalid phone number.", MessageType.SMS, e);
				}
			}
		}
	}
}
