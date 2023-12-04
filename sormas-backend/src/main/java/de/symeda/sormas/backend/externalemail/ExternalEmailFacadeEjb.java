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

package de.symeda.sormas.backend.externalemail;

import static de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.splitTemplateContent;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.validation.Valid;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflowType;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.externalemail.ExternalEmailException;
import de.symeda.sormas.api.externalemail.ExternalEmailFacade;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.manualmessagelog.MessageType;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.messaging.EmailService;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateEntitiesBuilder;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.EmailTemplateTexts;
import de.symeda.sormas.backend.docgeneration.RootEntities;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.manualmessagelog.ManualMessageLog;
import de.symeda.sormas.backend.manualmessagelog.ManualMessageLogService;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.UserService;

@Stateless(name = "ExternalEmailFacade")
public class ExternalEmailFacadeEjb implements ExternalEmailFacade {

	private static final Logger logger = LoggerFactory.getLogger(ExternalEmailFacadeEjb.class);
	@EJB
	private DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal documentTemplateFacade;
	@EJB
	private DocumentTemplateEntitiesBuilder templateEntitiesBuilder;
	@EJB
	private EmailService emailService;
	@EJB
	private UserService userService;
	@EJB
	private ManualMessageLogService manualMessageLogService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private TravelEntryService travelEntryService;

	@Override
	public List<String> getTemplateNames(DocumentWorkflow documentWorkflow) {
		return documentTemplateFacade.getAvailableTemplates(documentWorkflow);
	}

	@Override
	public void sendEmail(@Valid ExternalEmailOptionsDto options) throws DocumentTemplateException, ExternalEmailException {
		validateOptions(options);
		String generatedText = documentTemplateFacade.generateDocumentTxtFromEntities(
			options.getDocumentWorkflow(),
			options.getTemplateName(),
			templateEntitiesBuilder.resolveEntities(
				new RootEntities().addReference(options.getRootEntityType(), options.getRootEntityReference())
					.addEntity(RootEntityType.ROOT_USER, userService.getCurrentUser())),
			null);
		EmailTemplateTexts emailTexts = splitTemplateContent(generatedText);

		try {
			emailService.sendEmail(options.getRecipientEmail(), emailTexts.getSubject(), emailTexts.getContent());
		} catch (MessagingException e) {
			logger.error("Error sending email", e);
			throw new ExternalEmailException(I18nProperties.getString(Strings.errorSendingExternalEmail));
		}

		manualMessageLogService.ensurePersisted(createMessageLog(options));
	}

	@NotNull
	private ManualMessageLog createMessageLog(ExternalEmailOptionsDto options) {
		ManualMessageLog log = new ManualMessageLog();

		log.setMessageType(MessageType.EMAIL);
		log.setSendingUser(log.getSendingUser());
		log.setSentDate(new Date());
		log.setEmailAddress(options.getRecipientEmail());
		log.setUsedTemplate(options.getTemplateName());

		// `*Service::getByReferenceDto` does a null check, so we don't need to do it here
		log.setCaze(caseService.getByReferenceDto(getRootEntityReference(options, RootEntityType.ROOT_CASE, CaseReferenceDto.class)));
		log.setContact(contactService.getByReferenceDto(getRootEntityReference(options, RootEntityType.ROOT_CONTACT, ContactReferenceDto.class)));
		log.setEventParticipant(
				eventParticipantService
						.getByReferenceDto(getRootEntityReference(options, RootEntityType.ROOT_EVENT_PARTICIPANT, EventParticipantReferenceDto.class)));
		log.setTravelEntry(
				travelEntryService.getByReferenceDto(getRootEntityReference(options, RootEntityType.ROOT_TRAVEL_ENTRY, TravelEntryReferenceDto.class)));

		return log;
	}

	private static <T> T getRootEntityReference(ExternalEmailOptionsDto options, RootEntityType rootEntityType, Class<T> referenceClass) {
		if (options.getRootEntityType() == rootEntityType) {
			referenceClass.cast(options.getRootEntityReference());
		}

		return null;
	}

	private static void validateOptions(ExternalEmailOptionsDto options) {
		if (options.getDocumentWorkflow().getType() != DocumentWorkflowType.EMAIL) {
			// not a validation exception because this can only happen by a programming error
			throw new IllegalArgumentException("Document workflow type must be EMAIL");
		}
	}

	@Stateless
	@LocalBean
	public static class ExternalEmailFacadeEjbLocal extends ExternalEmailFacadeEjb {
	}
}
