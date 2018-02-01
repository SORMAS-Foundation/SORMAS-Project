package de.symeda.sormas.backend.common;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.backend.user.User;

/**
 * Service used to send email or SMS messages to SORMAS users. SMS messages are currently not supported.
 * 
 * @author Maté Strysewske
 */
@Stateless(name = "MessagingService")
@LocalBean
public class MessagingService {

	// Message subjects
	public static final String SUBJECT_CASE_CLASSIFICATION_CHANGED = "caseClassificationChangedSubject";
	public static final String SUBJECT_LAB_RESULT_ARRIVED = "labResultArrivedSubject";
	public static final String SUBJECT_LAB_RESULT_SPECIFIED = "labResultSpecifiedSubject";
	public static final String SUBJECT_CONTACT_SYMPTOMATIC = "contactSymptomaticSubject";
	
	// Message contents (via properties file)
	public static final String CONTENT_CASE_CLASSIFICATION_CHANGED = "caseClassificationChanged";
	public static final String CONTENT_LAB_RESULT_ARRIVED = "labResultArrived";
	public static final String CONTENT_LAB_RESULT_SPECIFIED = "labResultSpecified";
	public static final String CONTENT_CONTACT_SYMPTOMATIC = "contactSymptomatic";
	
	private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);
	
	@EJB
	private EmailService emailService;
	
	/**
	 * Sends the message specified by the messageContent via mail or SMS, according to the messageType, to the specified recipient's
	 * email address or phone number. Logs an error if the email address or phone number is not set.
	 */
	public boolean sendMessage(User recipient, String subject, String messageContent, MessageType messageType) throws EmailDeliveryFailedException {

		String emailAddress = recipient.getUserEmail();
		String phoneNumber = recipient.getPhone();

		if (messageType == MessageType.EMAIL && emailAddress == null) {
			logger.info(String.format("Tried to send an email to a user without an email address (UUID: %s).", recipient.getUuid()));
			return false;
		} else if (messageType == MessageType.SMS && phoneNumber == null) {
			logger.info(String.format("Tried to send an SMS to a user without a phone number (UUID: %s).", recipient.getUuid()));
			return false;
		} else {
			try {
				if (messageType == MessageType.EMAIL) {
					emailService.sendEmail(emailAddress, subject, messageContent);
				} else {
					throw new UnsupportedOperationException("Sending SMS messages is currently not supported.");
				}
			} catch (MessagingException e) {
				throw new EmailDeliveryFailedException("Email could not be sent due to an unexpected error.", e);
			}

			return true;
		}
	}
	
}
