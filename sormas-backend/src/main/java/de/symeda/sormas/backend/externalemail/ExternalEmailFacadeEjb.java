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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflowType;
import de.symeda.sormas.api.docgeneneration.EmailAttachementDto;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderDocumentOptionsDto;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentReferenceDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.externalemail.AttachmentException;
import de.symeda.sormas.api.externalemail.ExternalEmailException;
import de.symeda.sormas.api.externalemail.ExternalEmailFacade;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsDto;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsWithAttachmentsDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.luxembourg.LuxembourgNationalHealthIdValidator;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.messaging.EmailService;
import de.symeda.sormas.backend.common.messaging.MessagingService;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.docgeneration.DocGenerationHelper;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateEntitiesBuilder;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.EmailTemplateTexts;
import de.symeda.sormas.backend.docgeneration.QuarantineOrderFacadeEjb;
import de.symeda.sormas.backend.docgeneration.RootEntities;
import de.symeda.sormas.backend.document.Document;
import de.symeda.sormas.backend.document.DocumentFacadeEjb.DocumentFacadeEjbLocal;
import de.symeda.sormas.backend.document.DocumentRelatedEntity;
import de.symeda.sormas.backend.document.DocumentRelatedEntityService;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.document.DocumentStorageService;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.manualmessagelog.ManualMessageLog;
import de.symeda.sormas.backend.manualmessagelog.ManualMessageLogService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

@Stateless(name = "ExternalEmailFacade")
public class ExternalEmailFacadeEjb implements ExternalEmailFacade {

	private static final Logger logger = LoggerFactory.getLogger(ExternalEmailFacadeEjb.class);
	public static final int ATTACHMENT_PASSWORD_LENGTH = 10;
	// @formatter:off
	private static final Map<DocumentWorkflow, DocumentRelatedEntityType> DOCUMENT_WORKFLOW_DOCUMENT_RELATION_MAPPING = Map.of(
		DocumentWorkflow.CASE_EMAIL, DocumentRelatedEntityType.CASE,
		DocumentWorkflow.CONTACT_EMAIL, DocumentRelatedEntityType.CONTACT,
		DocumentWorkflow.TRAVEL_ENTRY_EMAIL, DocumentRelatedEntityType.TRAVEL_ENTRY
	);
	// @formatter:on

	@EJB
	private DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal documentTemplateFacade;
	@EJB
	private QuarantineOrderFacadeEjb.QuarantineOrderFacadeEjbLocal quarantineOrderFacade;
	@EJB
	private DocumentTemplateEntitiesBuilder templateEntitiesBuilder;
	@EJB
	private EmailService emailService;
	@EJB
	private UserService userService;
	@EJB
	private DocumentFacadeEjbLocal documentFacade;
	@EJB
	private DocumentService documentService;
	@EJB
	private DocumentRelatedEntityService documentRelatedEntityService;
	@EJB
	private DocumentStorageService documentStorageService;
	@EJB
	private AttachmentService attachmentService;
	@EJB
	private PersonService personService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private MessagingService messagingService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private TravelEntryService travelEntryService;
	@EJB
	private ManualMessageLogService manualMessageLogService;
	@EJB
	private DocGenerationHelper docGenerationHelper;

	@Override
	public List<String> getTemplateNames(DocumentWorkflow documentWorkflow) {
		return documentTemplateFacade.getAvailableTemplates(documentWorkflow);
	}

	@Override
	public List<DocumentReferenceDto> getAttachableDocuments(DocumentWorkflow documentWorkflow, String relatedEntityUuid) {
		DocumentRelatedEntityType relatedEntityType = DOCUMENT_WORKFLOW_DOCUMENT_RELATION_MAPPING.get(documentWorkflow);
		if (relatedEntityType == null) {
			throw new IllegalArgumentException("Documents not supported for the given workflow");
		}

		return documentFacade.getReferencesRelatedToEntity(relatedEntityType, relatedEntityUuid, attachmentService.getAttachableFileExtensions());
	}

	@Override
	public void sendEmail(@Valid ExternalEmailOptionsDto options)
		throws AttachmentException, DocumentTemplateException, ValidationException, ExternalEmailException, IOException {
		sendEmail(options, true);
	}

	private void sendEmail(@Valid ExternalEmailOptionsDto options, boolean onlyLinkedDocumentsAsAttachments)
		throws DocumentTemplateException, ExternalEmailException, AttachmentException, ValidationException, IOException {
		validateOptions(options);

		User currentUser = userService.getCurrentUser();
		DocumentTemplateEntities documentEntities = templateEntitiesBuilder.resolveEntities(
			new RootEntities().addReference(options.getRootEntityType(), options.getRootEntityReference())
				.addEntity(RootEntityType.ROOT_USER, currentUser));

		PersonReferenceDto personRef = (PersonReferenceDto) documentEntities.getEntity(RootEntityType.ROOT_PERSON);
		Person person = personService.getByReferenceDto(personRef);

		if (StringUtils.isEmpty(options.getRecipientEmail())) {
			if (StringUtils.isEmpty(person.getEmailAddress())) {
				throw new ValidationException(I18nProperties.getString(Strings.errorExternalEmailMissingPersonEmailAddress));
			}
			options.setRecipientEmail(person.getEmailAddress());
		}

		File personalizedFile = null;
		String personalizedFileName = null;
		Map<File, String> filesToBeEncryped = new HashMap<>();

		if (options.getQuarantineOrderDocumentOptions() != null && options.getQuarantineOrderDocumentOptions().getTemplateFile() != null) {
			Map.Entry<File, String> fileFromTemplate = createFileFromTemplate(options);
			personalizedFile = fileFromTemplate.getKey();
			personalizedFileName = fileFromTemplate.getValue();
			filesToBeEncryped.put(personalizedFile, personalizedFileName);
		}

		Map<File, String> emailAttachments = Collections.emptyMap();
		Set<DocumentReferenceDto> attachedDocuments = options.getAttachedDocuments();
		List<Document> sormasDocuments;

		if (CollectionUtils.isNotEmpty(attachedDocuments)) {
			List<String> documentUuids = attachedDocuments.stream().map(DocumentReferenceDto::getUuid).collect(Collectors.toList());
			sormasDocuments = documentService.getByUuids(documentUuids);
			validateAttachedDocuments(sormasDocuments, options, onlyLinkedDocumentsAsAttachments);

			sormasDocuments.forEach(doc -> {
				File document = documentStorageService.getFile(doc.getStorageReference());
				String fileName = doc.getName();
				filesToBeEncryped.put(document, fileName);
			});
		}

		String password = null;
		PasswordType passwordType = null;
		if (!filesToBeEncryped.isEmpty()) {
			Pair<String, PasswordType> passwordAndType = getPassword(person);
			password = passwordAndType.getElement0();
			passwordType = passwordAndType.getElement1();
			emailAttachments = attachmentService.createEncryptedPdfs(filesToBeEncryped, password);
		}

		String generatedText =
			documentTemplateFacade.generateDocumentTxtFromEntities(options.getDocumentWorkflow(), options.getTemplateName(), documentEntities, null);
		EmailTemplateTexts emailTexts = splitTemplateContent(generatedText);

		try {
			emailService.sendEmail(options.getRecipientEmail(), emailTexts.getSubject(), emailTexts.getContent(), emailAttachments);

			if (personalizedFile != null) {
				if (options.getQuarantineOrderDocumentOptions().getShouldUploadGeneratedDoc()) {
					byte[] content;
					content = FileUtils.readFileToByteArray(personalizedFile);
					quarantineOrderFacade.uploadDocument(personalizedFileName, options.getRootEntityReference(), content);
				}
				//removes the file from temporary folder
				Files.delete(personalizedFile.toPath());
			}

			if (passwordType == PasswordType.RANDOM) {
				messagingService.sendManualMessage(
					person,
					null,
					String.format(I18nProperties.getString(Strings.messageExternalEmailAttachmentPassword), password),
					MessageType.SMS);
			}

		} catch (MessagingException | NotificationDeliveryFailedException | IOException e) {

			if (personalizedFile != null) {
				//removes the file from temporary folder
				Files.delete(personalizedFile.toPath());
			}

			for (File tempEncryptedFile : emailAttachments.keySet()) {
				Files.delete(tempEncryptedFile.toPath());
			}

			logger.error("Error sending email", e);
			throw new ExternalEmailException(I18nProperties.getString(Strings.errorSendingExternalEmail));
		}

		emailAttachments.keySet().forEach(File::delete);

		manualMessageLogService
			.ensurePersisted(createMessageLog(options, person.toReference(), currentUser, new ArrayList<>(filesToBeEncryped.values())));
	}

	private Map.Entry<File, String> createFileFromTemplate(ExternalEmailOptionsDto emailOptions) throws DocumentTemplateException, IOException {
		QuarantineOrderDocumentOptionsDto documentOptions = emailOptions.getQuarantineOrderDocumentOptions();
		byte[] generatedDocument = quarantineOrderFacade.getGeneratedDocument(
			documentOptions.getTemplateFile(),
			documentOptions.getDocumentWorkflow(),
			emailOptions.getRootEntityType(),
			emailOptions.getRootEntityReference(),
			null,
			null,
			null,
			documentOptions.getExtraProperties(),
			// document uploading will be handled in another place 
			false);
		String fileName = docGenerationHelper.getDocumentFileName(emailOptions.getRootEntityReference(), documentOptions.getTemplateFile());
		File tempTemplateFile = Paths.get(configFacade.getTempFilesPath()).resolve(fileName).toFile();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(tempTemplateFile.toPath()));
		bufferedOutputStream.write(generatedDocument);
		bufferedOutputStream.close();

		Map.Entry<File, String> templateFile = Map.entry(tempTemplateFile, fileName);
		return templateFile;
	}

	@Override
	@PermitAll
	public List<ProcessedEntity> sendBulkEmail(@Valid ExternalEmailOptionsWithAttachmentsDto options, List<ReferenceDto> rootReferences)
		throws IOException {

		List<DocumentDto> documentList = new ArrayList<>();
		boolean attachmentsSavedInPreviousBatches = false;
		if (options.getAttachedDocuments() != null) {
			for (EmailAttachementDto attachement : options.getAttachedDocuments()) {
				DocumentDto attachedDocument = attachement.getDocument();
				//since the bulk email sending process is done in batches and the attached documents are saved before first email sent
				//then the following situations could occur:
				// 1) Let's say we have this situation: in the first batch there are no eligible entities (no emails are sent). From the above we also know
				// that the attachments are saved before the starting of the batch. In this case the attachment must be removed from the file system since
				// there is no relatedEntities to be linked to them
				// 2) Point 1 is repeated for the second batch and further, until one eligible entity appears and an email is sent.
				// 3) Once at least one email has been sent for all the next batches the attachments will be retrieved from the file system. The process
				// of saving and removing the attachments is stopped.
				DocumentDto foundDocument = documentFacade.getDocumentByUuid(attachement.getDocument().getUuid());
				if (foundDocument != null) {
					attachmentsSavedInPreviousBatches = true;
				}

				if (foundDocument != null) {
					documentList.add(foundDocument);
				} else {
					DocumentDto newDocument = documentFacade.saveDocument(attachedDocument, attachement.getContent(), Collections.emptyList());
					documentList.add(newDocument);
				}
			}
		}

		Set<DocumentReferenceDto> attachedDocuments = options.getAttachedDocuments() != null
			? options.getAttachedDocuments().stream().map(EmailAttachementDto::getDocument).map(DocumentDto::toReference).collect(Collectors.toSet())
			: null;

		List<ProcessedEntity> processedEntities = rootReferences.stream().map(entityRef -> {
			try {
				ExternalEmailOptionsDto emailOptions =
					new ExternalEmailOptionsDto(options.getDocumentWorkflow(), options.getRootEntityType(), entityRef);
				emailOptions.setTemplateName(options.getTemplateName());

				emailOptions.setAttachedDocuments(attachedDocuments);
				emailOptions.setRootEntityReference(entityRef);

				emailOptions.setQuarantineOrderDocumentOptions(options.getQuarantineOrderDocumentOptionsDto());

				sendEmail(emailOptions, false);

				if (emailOptions.getAttachedDocuments() != null) {
					emailOptions.getAttachedDocuments().forEach(attachment -> {
						Document document = documentService.getByUuid(attachment.getUuid());
						DocumentRelatedEntity documentRelatedEntity = new DocumentRelatedEntity()
							.build(getDocRelatedEntityType(emailOptions.getRootEntityType()), emailOptions.getRootEntityReference().getUuid());
						documentRelatedEntity.setDocument(document);
						documentRelatedEntityService.persist(documentRelatedEntity);
					});
				}
			} catch (AttachmentException | ValidationException e) {
				logger.warn("Email could not be sent", e);
				return new ProcessedEntity(entityRef.getUuid(), ProcessedEntityStatus.NOT_ELIGIBLE);
			} catch (Exception e) {
				logger.error("Email could not be sent", e);
				return new ProcessedEntity(entityRef.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE);
			}
			return new ProcessedEntity(entityRef.getUuid(), ProcessedEntityStatus.SUCCESS);
		}).collect(Collectors.toList());

		if (processedEntities.stream().noneMatch(processedEntity -> ProcessedEntityStatus.SUCCESS.equals(processedEntity.getProcessedEntityStatus()))
			&& Boolean.FALSE.equals(attachmentsSavedInPreviousBatches)) {
			documentList.forEach(documentDto -> {
				Document toDeletedDocument = documentService.getByUuid(documentDto.getUuid());
				documentStorageService.delete(toDeletedDocument.getStorageReference());
				documentService.deletePermanent(toDeletedDocument);
			});
		}

		return processedEntities;
	}

	private static void validateAttachedDocuments(
		List<Document> sormasDocuments,
		ExternalEmailOptionsDto options,
		boolean onlyLinkedDocumentsAsAttachments) {
		DocumentRelatedEntityType documentRelatedEntityType = DOCUMENT_WORKFLOW_DOCUMENT_RELATION_MAPPING.get(options.getDocumentWorkflow());

		//all attached documents must be linked to the root entity
		if (onlyLinkedDocumentsAsAttachments) {
			if (sormasDocuments.stream()
				.anyMatch(
					d -> d.getRelatedEntities()
						.stream()
						.noneMatch(
							relatedEntity -> relatedEntity.getRelatedEntityType() == documentRelatedEntityType
								&& relatedEntity.getRelatedEntityUuid().equals(options.getRootEntityReference().getUuid())))) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.attachedDocumentNotRelatedToEntity));
			}
		}
	}

	@Override
	public boolean isAttachmentAvailable(PersonReferenceDto personReferenceDto) {
		Person person = personService.getByReferenceDto(personReferenceDto);

		return getApplicablePasswordType(person) != PasswordType.NONE;
	}

	@Override
	public Set<String> getAttachableFileExtensions() {
		return attachmentService.getAttachableFileExtensions();
	}

	private PasswordType getApplicablePasswordType(Person person) {
		String nationalHealthId = person.getNationalHealthId();
		boolean canSendSms = configFacade.isSmsServiceSetUp() && person.getPhone() != null;

		if (configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			if (isValidLuxembourgNationalHealthId(nationalHealthId, person)) {
				return PasswordType.HEALTH_ID;
			}

			return canSendSms ? PasswordType.RANDOM : PasswordType.NONE;
		} else {
			if (nationalHealthId != null) {
				return PasswordType.HEALTH_ID;
			}

			return canSendSms ? PasswordType.RANDOM : PasswordType.NONE;
		}
	}

	private Pair<String, PasswordType> getPassword(Person person) throws AttachmentException {
		PasswordType passwordType = getApplicablePasswordType(person);

		switch (passwordType) {
		case HEALTH_ID:
			return new Pair<>(person.getNationalHealthId(), passwordType);
		case RANDOM:
			return new Pair<>(generateRandomPassword(), passwordType);
		case NONE:
		default:
			throw new AttachmentException(I18nProperties.getString(Strings.errorExternalEmailAttachmentCannotEncrypt));
		}
	}

	private static String generateRandomPassword() {
		return new RandomStringGenerator.Builder().withinRange(
			new char[] {
				'a',
				'z' },
			new char[] {
				'A',
				'Z' },
			new char[] {
				'2',
				'9' })
			.filteredBy(codePoint -> !"lIO".contains(String.valueOf((char) codePoint)))
			.build()
			.generate(ATTACHMENT_PASSWORD_LENGTH);
	}

	private static void validateOptions(ExternalEmailOptionsDto options) {
		if (options.getDocumentWorkflow().getType() != DocumentWorkflowType.EMAIL) {
			// not a validation exception because this can only happen by a programming error
			throw new IllegalArgumentException("Document workflow type must be EMAIL");
		}
	}

	private ManualMessageLog createMessageLog(
		ExternalEmailOptionsDto options,
		PersonReferenceDto personRef,
		User currentUser,
		List<String> attachedDocumentsName) {
		ManualMessageLog log = new ManualMessageLog();

		log.setMessageType(MessageType.EMAIL);
		log.setSendingUser(currentUser);
		log.setRecipientPerson(personService.getByReferenceDto(personRef));
		log.setSentDate(new Date());
		log.setEmailAddress(options.getRecipientEmail());
		log.setUsedTemplate(options.getTemplateName());
		log.setAttachedDocuments(attachedDocumentsName);

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
			return referenceClass.cast(options.getRootEntityReference());
		}

		return null;
	}

	private static boolean isValidLuxembourgNationalHealthId(String nationalHealthId, Person person) {

		if (StringUtils.isEmpty(nationalHealthId)) {
			return false;
		}

		return LuxembourgNationalHealthIdValidator
			.isValid(nationalHealthId, person.getBirthdateYYYY(), person.getBirthdateMM(), person.getBirthdateDD());
	}

	private DocumentRelatedEntityType getDocRelatedEntityType(RootEntityType rootEntityType) {
		switch (rootEntityType) {
		case ROOT_CASE:
			return DocumentRelatedEntityType.CASE;
		case ROOT_CONTACT:
			return DocumentRelatedEntityType.CONTACT;
		case ROOT_TRAVEL_ENTRY:
			return DocumentRelatedEntityType.TRAVEL_ENTRY;
		default:
			throw new RuntimeException("Root entity type" + rootEntityType.name() + " does not support documents");
		}
	}

	@Stateless
	@LocalBean
	public static class ExternalEmailFacadeEjbLocal extends ExternalEmailFacadeEjb {
	}

	private enum PasswordType {
		HEALTH_ID,
		RANDOM,
		NONE
	}
}
