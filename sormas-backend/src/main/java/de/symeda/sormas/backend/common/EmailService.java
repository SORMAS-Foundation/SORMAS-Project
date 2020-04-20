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

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;

@Stateless(name = "EmailService")
@LocalBean
public class EmailService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource(name = "mail/MailSession")
	private Session mailSession;
	
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@Asynchronous
	public void sendEmail(String recipient, String subject, String content) throws AddressException, MessagingException {
		 
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
 
		MimeMessage message = new MimeMessage(mailSession);
 
		String senderAddress = configFacade.getEmailSenderAddress();
		String senderName = configFacade.getEmailSenderName();
 
		try {
			InternetAddress fromAddress = new InternetAddress(senderAddress, senderName);
			message.setFrom(fromAddress);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
 
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
 
		message.setSubject(subject, "UTF-8");
		message.setContent(content, "text/plain; charset=utf-8");
 
		Transport.send(message);
		logger.info("Mail sent to {}.", recipient);
	}
	
}
