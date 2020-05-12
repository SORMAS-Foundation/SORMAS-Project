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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.common;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexmo.client.NexmoClientException;

import de.symeda.sormas.api.utils.DataHelper;
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

	// Message subjects
	public static final String SUBJECT_CASE_CLASSIFICATION_CHANGED = "notificationCaseClassificationChangedSubject";
	public static final String SUBJECT_CASE_INVESTIGATION_DONE = "notificationCaseInvestigationDoneSubject";
	public static final String SUBJECT_LAB_RESULT_ARRIVED = "notificationLabResultArrivedSubject";
	public static final String SUBJECT_LAB_RESULT_SPECIFIED = "notificationLabResultSpecifiedSubject";
	public static final String SUBJECT_LAB_SAMPLE_SHIPPED = "notificationLabSampleShippedSubject";
	public static final String SUBJECT_CONTACT_SYMPTOMATIC = "notificationContactSymptomaticSubject";
	public static final String SUBJECT_TASK_START = "notificationTaskStartSubject";
	public static final String SUBJECT_TASK_DUE = "notificationTtaskDueSubject";
	public static final String SUBJECT_VISIT_COMPLETED = "notificationVisitCompletedSubject";
	public static final String SUBJECT_DISEASE_CHANGED = "notificationDiseaseChangedSubject";
	
	// Message contents (via properties file)
	public static final String CONTENT_CASE_CLASSIFICATION_CHANGED = "notificationCaseClassificationChanged";
	public static final String CONTENT_CASE_INVESTIGATION_DONE = "notificationCaseInvestigationDone";
	public static final String CONTENT_LAB_RESULT_ARRIVED = "notificationLabResultArrived";
	public static final String CONTENT_LAB_RESULT_SPECIFIED = "notificationLabResultSpecified";
	public static final String CONTENT_LAB_SAMPLE_SHIPPED = "notificationLabSampleShipped";
	public static final String CONTENT_LAB_SAMPLE_SHIPPED_SHORT = "notificationLabSampleShippedShort";
	public static final String CONTENT_LAB_SAMPLE_SHIPPED_SHORT_FOT_CONTACT = "notificationLabSampleShippedShortForContact";
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
	
	/**
	 * Sends the message specified by the messageContent via mail and/or SMS, according to the messageTypes, to the specified recipient's
	 * email address and/or phone number. Logs an error if the email address or phone number is not set.
	 */
	public void sendMessage(User recipient, String subject, String messageContent, MessageType... messageTypes) throws NotificationDeliveryFailedException {
		// Don't send notifications to users that initiated an action
		if (recipient.equals(userService.getCurrentUser())) {
			return;
		}
		
		String emailAddress = recipient.getUserEmail();
		String phoneNumber = recipient.getPhone();
		
		for (MessageType messageType : messageTypes) {
			if (messageType == MessageType.EMAIL && DataHelper.isNullOrEmpty(emailAddress)) {
				logger.info(String.format("Tried to send an email to a user without an email address (UUID: %s).", recipient.getUuid()));
			} else if (messageType == MessageType.SMS && DataHelper.isNullOrEmpty(phoneNumber)) {
				logger.info(String.format("Tried to send an SMS to a user without a phone number (UUID: %s).", recipient.getUuid()));
			} else {
				try {
					if (messageType == MessageType.EMAIL) {
						emailService.sendEmail(emailAddress, subject, messageContent);
					} else if (messageType == MessageType.SMS) {
						smsService.sendSms(phoneNumber, subject, messageContent);
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
