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

import static de.symeda.sormas.backend.docgeneration.TemplateTestUtil.cleanLineSeparators;
import static de.symeda.sormas.backend.externalemail.luxembourg.NationalHealthIdValidatorTest.VALID_LU_NATIONAL_HEALTH_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.Loader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentReferenceDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.externalemail.ExternalEmailException;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogCriteria;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogIndexDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.common.messaging.EmailService;
import de.symeda.sormas.backend.common.messaging.InvalidPhoneNumberException;
import de.symeda.sormas.backend.common.messaging.SmsService;
import de.symeda.sormas.backend.docgeneration.AbstractDocGenerationTest;

public class ExternalEmailFacadeEjbTest extends AbstractDocGenerationTest {

	private TestDataCreator.RDCF rdcf;
	private UserDto admin;
	private UserDto userDto;
	private PersonDto personDto;

	@Mock
	private EmailService emailService;
	@Mock
	private SmsService smsService;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility", "PointOfEntry");

		admin = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Admin",
			"Admin",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		userDto = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		loginWith(userDto);

		LocationDto locationDto = LocationDto.build();
		locationDto.setStreet("Nauwieserstraße");
		locationDto.setHouseNumber("7");
		locationDto.setCity("Saarbrücken");
		locationDto.setPostalCode("66111");

		personDto = PersonDto.build();
		personDto.setFirstName("Guy");
		personDto.setLastName("Debord");
		personDto.setSex(Sex.UNKNOWN);
		personDto.setBirthdateYYYY(1931);
		personDto.setBirthdateMM(12);
		personDto.setBirthdateDD(28);
		personDto.setAddress(locationDto);
		personDto.setPhone("+49 681 1234");

		getPersonFacade().save(personDto);
	}

	@BeforeEach
	public void setup() throws URISyntaxException {
		reset();
	}

	@Test
	public void testGetAvailableTemplates() throws DocumentTemplateException {
		loginWith(admin);

		getDocumentTemplateFacade()
			.writeDocumentTemplate(DocumentWorkflow.CASE_EMAIL, "CaseEmailMock.txt", "#Subject\nContent".getBytes(StandardCharsets.UTF_8));
		getDocumentTemplateFacade()
			.writeDocumentTemplate(DocumentWorkflow.CONTACT_EMAIL, "ContactEmailMock.txt", "#Subject\nContent".getBytes(StandardCharsets.UTF_8));

		loginWith(userDto);

		List<String> templateNames = getExternalEmailFacade().getTemplateNames(DocumentWorkflow.CASE_EMAIL);

		assertThat(templateNames.size(), is(2));
		// should return predefined test template "CaseEmail.txt" and the one just created by the test
		assertThat(templateNames, containsInAnyOrder("CaseEmail.txt", "CaseEmailMock.txt"));

		assertThat(
			getExternalEmailFacade().getTemplateNames(DocumentWorkflow.CONTACT_EMAIL),
			containsInAnyOrder("ContactEmail.txt", "ContactEmailMock.txt"));
	}

	@Test
	public void testGetAttachableDocuments() throws IOException {
		loginWith(admin);

		CaseDataDto caze = creator.createCase(userDto.toReference(), rdcf, null);
		ContactDto contact = creator.createContact(userDto.toReference(), creator.createPerson().toReference());

		createDocument("PDF Document.pdf", DocumentRelatedEntityType.CASE, caze.getUuid());
		createDocument("DOCX Document.docx", DocumentRelatedEntityType.CASE, caze.getUuid());
		createDocument("DOC Document.doc", DocumentRelatedEntityType.CASE, caze.getUuid());
		createDocument("TXT Document.txt", DocumentRelatedEntityType.CASE, caze.getUuid());
		createDocument("JPG Document.jpg", DocumentRelatedEntityType.CASE, caze.getUuid());
		createDocument("JPEG Document.jpeg", DocumentRelatedEntityType.CASE, caze.getUuid());
		createDocument("PNG Document.png", DocumentRelatedEntityType.CASE, caze.getUuid());
		createDocument("GIF Document.gif", DocumentRelatedEntityType.CASE, caze.getUuid());
		createDocument("PPT Document.ppt", DocumentRelatedEntityType.CASE, caze.getUuid());
		createDocument("Pdf Document Contact.pdf", DocumentRelatedEntityType.CONTACT, contact.getUuid());
		createDocument("HTML Document Contact.html", DocumentRelatedEntityType.CONTACT, contact.getUuid());
		createDocument("ODS Document Contact.ods", DocumentRelatedEntityType.CONTACT, contact.getUuid());

		loginWith(userDto);

		List<DocumentReferenceDto> attachableCaseDocuments =
			getExternalEmailFacade().getAttachableDocuments(DocumentWorkflow.CASE_EMAIL, caze.getUuid());
		assertThat(attachableCaseDocuments, hasSize(6));
		assertThat(
			attachableCaseDocuments.stream().map(DocumentReferenceDto::getCaption).collect(Collectors.toList()),
			containsInAnyOrder(
				"PDF Document.pdf",
				"DOCX Document.docx",
				"JPG Document.jpg",
				"JPEG Document.jpeg",
				"PNG Document.png",
				"GIF Document.gif"));
		assertThat(getExternalEmailFacade().getAttachableDocuments(DocumentWorkflow.CONTACT_EMAIL, contact.getUuid()), hasSize(1));

		assertThrows(
			IllegalArgumentException.class,
			() -> getExternalEmailFacade().getAttachableDocuments(DocumentWorkflow.EVENT_PARTICIPANT_EMAIL, "dummy-uuid"));
	}

	@Test
	public void testIsAttachmentAvailableWithNationalHealthId() {
		// not national health id and no phone number -> no attachment available
		PersonDto person = createPerson(null, null);
		assertThat(getExternalEmailFacade().isAttachmentAvailable(person.toReference()), is(false));

		//with national health id -> attachment available
		person = createPerson("1234567890", null);
		assertThat(getExternalEmailFacade().isAttachmentAvailable(person.toReference()), is(true));
	}

	@Test
	public void testIsAttachmentAvailableWithPhoneNumber() {
		PersonDto person = createPerson(null, "+49 681 1234");

		// without sms service setup -> no attachment available
		assertThat(getExternalEmailFacade().isAttachmentAvailable(person.toReference()), is(false));

		// setup mock sms service -> attachment available
		setupMockSmsService();

		assertThat(getExternalEmailFacade().isAttachmentAvailable(person.toReference()), is(true));
	}

	@Test
	public void testIsAttachmentAvailableForLuxembourg() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);

		// invalid national health id -> no attachment available
		PersonDto person = createPerson("1234567890", null);
		assertThat(getExternalEmailFacade().isAttachmentAvailable(person.toReference()), is(false));

		// valid national health id -> attachment available
		person = createPerson(VALID_LU_NATIONAL_HEALTH_ID, null);
		getPersonFacade().save(person);
		assertThat(getExternalEmailFacade().isAttachmentAvailable(person.toReference()), is(true));

		// no national health id but phone number -> attachment available
		person = createPerson(null, "+49 681 1234");

		// no sms service setup -> no attachment available
		assertThat(getExternalEmailFacade().isAttachmentAvailable(person.toReference()), is(false));

		// setup mock sms service -> attachment available
		setupMockSmsService();

		assertThat(getExternalEmailFacade().isAttachmentAvailable(person.toReference()), is(true));

		// invalid national health id but phone number -> attachment available
		person = createPerson(VALID_LU_NATIONAL_HEALTH_ID, "+49 681 1234");
		assertThat(getExternalEmailFacade().isAttachmentAvailable(person.toReference()), is(true));
	}

	@Test
	public void testSendEmailToCasePerson() throws DocumentTemplateException, ExternalEmailException, MessagingException, IOException {

		CaseDataDto caze = creator.createCase(userDto.toReference(), personDto.toReference(), rdcf);

		Mockito.doAnswer(invocation -> {
			assertThat(invocation.getArgument(0), is("test@mail.com"));
			assertThat(invocation.getArgument(1), is("Email subject in template"));

			StringWriter writer = new StringWriter();
			IOUtils.copy(getClass().getResourceAsStream("/docgeneration/emailTemplates/cases/CaseEmail.cmp"), writer, "UTF-8");
			String expectedContent = cleanLineSeparators(writer.toString());

			assertThat(invocation.getArgument(2), is(expectedContent));

			return null;
		}).when(emailService).sendEmail(any(), any(), any(), any());

		ExternalEmailOptionsDto options = new ExternalEmailOptionsDto(DocumentWorkflow.CASE_EMAIL, RootEntityType.ROOT_CASE, caze.toReference());
		options.setTemplateName("CaseEmail.txt");
		options.setRecipientEmail("test@mail.com");
		getExternalEmailFacade().sendEmail(options);

		Mockito.verify(emailService, Mockito.times(1)).sendEmail(any(), any(), any(), any());

		List<ManualMessageLogIndexDto> messageLogs =
			getManualMessageLogFacade().getIndexList(new ManualMessageLogCriteria().messageType(MessageType.EMAIL).caze(caze.toReference()));
		assertThat(messageLogs, hasSize(1));
		assertThat(messageLogs.get(0).getUsedTemplate(), is("CaseEmail.txt"));
		assertThat(messageLogs.get(0).getEmailAddress(), is("test@mail.com"));
		assertThat(messageLogs.get(0).getSendingUser(), is(userDto.toReference()));
		assertThat(messageLogs.get(0).getAttachedDocuments(), hasSize(0));
	}

	@Test
	public void testSendEmailToContactPerson() throws DocumentTemplateException, ExternalEmailException, MessagingException, IOException {

		ContactDto contact = creator.createContact(userDto.toReference(), personDto.toReference(), Disease.CORONAVIRUS, (c) -> {
			c.setContactStatus(ContactStatus.ACTIVE);
			c.setReturningTraveler(YesNoUnknown.YES);
		});

		Mockito.doAnswer(invocation -> {
			assertThat(invocation.getArgument(0), is("test@mail.com"));
			assertThat(invocation.getArgument(1), is("Email subject in template"));

			StringWriter writer = new StringWriter();
			IOUtils.copy(getClass().getResourceAsStream("/docgeneration/emailTemplates/contacts/ContactEmail.cmp"), writer, "UTF-8");
			String expectedContent = cleanLineSeparators(writer.toString());

			assertThat(invocation.getArgument(2), is(expectedContent));

			return null;
		}).when(emailService).sendEmail(any(), any(), any(), any());

		ExternalEmailOptionsDto options =
			new ExternalEmailOptionsDto(DocumentWorkflow.CONTACT_EMAIL, RootEntityType.ROOT_CONTACT, contact.toReference());
		options.setTemplateName("ContactEmail.txt");
		options.setRecipientEmail("test@mail.com");
		getExternalEmailFacade().sendEmail(options);

		Mockito.verify(emailService, Mockito.times(1)).sendEmail(any(), any(), any(), any());

		List<ManualMessageLogIndexDto> messageLogs =
			getManualMessageLogFacade().getIndexList(new ManualMessageLogCriteria().messageType(MessageType.EMAIL).contact(contact.toReference()));
		assertThat(messageLogs, hasSize(1));
		assertThat(messageLogs.get(0).getUsedTemplate(), is("ContactEmail.txt"));
		assertThat(messageLogs.get(0).getEmailAddress(), is("test@mail.com"));
		assertThat(messageLogs.get(0).getSendingUser(), is(userDto.toReference()));
		assertThat(messageLogs.get(0).getAttachedDocuments(), hasSize(0));
	}

	@Test
	public void testSendEmailWithAttachments() throws MessagingException, DocumentTemplateException, ExternalEmailException, IOException {
		MockProducer.mockProperty(ConfigFacadeEjb.DOCUMENT_FILES_PATH, MockProducer.TMP_PATH);
		String personNationalHealthId = "1234567890";
		PersonDto person = creator.createPerson("Person", "Name", Sex.UNKNOWN, p -> {
			p.setNationalHealthId(personNationalHealthId);
		});

		CaseDataDto caze = creator.createCase(userDto.toReference(), person.toReference(), rdcf);

		List<String> supportedFileTypes = Arrays.asList("pdf", "docx", "jpg", "png", "gif");
		List<DocumentDto> documents = new ArrayList<>(supportedFileTypes.size());
		for (String type : supportedFileTypes) {
			documents.add(
				createDocument(
					type.toUpperCase() + " Document." + type,
					DocumentRelatedEntityType.CASE,
					caze.getUuid(),
					IOUtils.toByteArray(getClass().getResourceAsStream("/externalemail/" + type.toUpperCase() + " Attachment." + type))));
		}

		Mockito.doAnswer(invocation -> {
			Map<File, String> filesAttached = invocation.getArgument(3);

			assertThat(filesAttached.size(), is(supportedFileTypes.size()));
			for (String type : supportedFileTypes) {
				assertThat(filesAttached, hasValue(type.toUpperCase() + " Document.pdf"));
			}

			for (Map.Entry<File, String> entry : filesAttached.entrySet()) {
				File file = entry.getKey();
				String attachmentName = entry.getValue();

				// pdf files should be readable
				try {
					Loader.loadPDF(file, personNationalHealthId);
				} catch (IOException e) {
					fail("Failed to open file " + attachmentName + "(" + file.getName() + "): " + e.getMessage());
				}
			}

			return null;
		}).when(emailService).sendEmail(any(), any(), any(), any());

		ExternalEmailOptionsDto options = new ExternalEmailOptionsDto(DocumentWorkflow.CASE_EMAIL, RootEntityType.ROOT_CASE, caze.toReference());
		options.setTemplateName("CaseEmail.txt");
		options.setRecipientEmail("test@mail.com");
		options.setAttachedDocuments(documents.stream().map(DocumentDto::toReference).collect(Collectors.toSet()));

		getExternalEmailFacade().sendEmail(options);
		Mockito.verify(emailService, Mockito.times(1)).sendEmail(any(), any(), any(), any());

		List<ManualMessageLogIndexDto> messageLogs =
			getManualMessageLogFacade().getIndexList(new ManualMessageLogCriteria().messageType(MessageType.EMAIL).caze(caze.toReference()));
		assertThat(messageLogs, hasSize(1));
		assertThat(messageLogs.get(0).getUsedTemplate(), is("CaseEmail.txt"));
		assertThat(messageLogs.get(0).getEmailAddress(), is("test@mail.com"));
		assertThat(messageLogs.get(0).getSendingUser(), is(userDto.toReference()));
		assertThat(messageLogs.get(0).getAttachedDocuments(), hasSize(supportedFileTypes.size()));
		documents.forEach(d -> assertThat(messageLogs.get(0).getAttachedDocuments(), hasItem(d.getName())));
	}

	@Test
	public void testEncryptAttachmentsWithRandomPassword()
		throws MessagingException, DocumentTemplateException, ExternalEmailException, IOException, InvalidPhoneNumberException {
		setupMockSmsService();

		MockProducer.mockProperty(ConfigFacadeEjb.DOCUMENT_FILES_PATH, MockProducer.TMP_PATH);
		PersonDto person = creator.createPerson("Person", "Name", Sex.UNKNOWN, p -> {
			p.setPhone("+49 681 1234");
		});

		CaseDataDto caze = creator.createCase(userDto.toReference(), person.toReference(), rdcf);

		List<String> supportedFileTypes = Arrays.asList("pdf", "docx", "jpg", "png", "gif");
		List<DocumentDto> documents = new ArrayList<>(supportedFileTypes.size());
		for (String type : supportedFileTypes) {
			documents.add(
				createDocument(
					type.toUpperCase() + " Document." + type,
					DocumentRelatedEntityType.CASE,
					caze.getUuid(),
					IOUtils.toByteArray(getClass().getResourceAsStream("/externalemail/" + type.toUpperCase() + " Attachment." + type))));
		}

		ArgumentCaptor<Map<File, String>> attachmentsCaptor = ArgumentCaptor.forClass(Map.class);
		Mockito.doAnswer(invocation -> {
			Map<File, String> filesAttached = invocation.getArgument(3);

			assertThat(filesAttached.size(), is(supportedFileTypes.size()));
			for (String type : supportedFileTypes) {
				assertThat(filesAttached, hasValue(type.toUpperCase() + " Document.pdf"));
			}

			return null;
		}).when(emailService).sendEmail(any(), any(), any(), attachmentsCaptor.capture());

		Mockito.doAnswer(invocation -> {
			assertThat(invocation.getArgument(0), is(person.getPhone()));
			// get the random password out of the message
			Matcher smsMessageMatcher =
				Pattern.compile(String.format(I18nProperties.getString(Strings.messageExternalEmailAttachmentPassword), "([a-zA-Z0-9]{10})"))
					.matcher(invocation.getArgument(1));
			assertThat("Sms message is not the expected one", smsMessageMatcher.matches(), is(true));

			String password = smsMessageMatcher.group(1);

			for (Map.Entry<File, String> entry : attachmentsCaptor.getValue().entrySet()) {
				File file = entry.getKey();
				String attachmentName = entry.getValue();

				// pdf files should be readable
				try {
					Loader.loadPDF(file, password);
				} catch (IOException e) {
					fail("Failed to open file " + attachmentName + "(" + file.getName() + "): " + e.getMessage());
				}
			}

			return null;
		}).when(smsService).sendSms(any(), any());

		ExternalEmailOptionsDto options = new ExternalEmailOptionsDto(DocumentWorkflow.CASE_EMAIL, RootEntityType.ROOT_CASE, caze.toReference());
		options.setTemplateName("CaseEmail.txt");
		options.setRecipientEmail("test@mail.com");
		options.setAttachedDocuments(documents.stream().map(DocumentDto::toReference).collect(Collectors.toSet()));

		getExternalEmailFacade().sendEmail(options);

		Mockito.verify(emailService, Mockito.times(1)).sendEmail(any(), any(), any(), any());
		Mockito.verify(smsService, Mockito.times(1)).sendSms(any(), any());
	}

	@Test
	public void testSendEmailWithUnsupportedAttachment() throws MessagingException, IOException, InvalidPhoneNumberException {
		MockProducer.mockProperty(ConfigFacadeEjb.DOCUMENT_FILES_PATH, MockProducer.TMP_PATH);
		PersonDto person = creator.createPerson("Person", "Name", Sex.UNKNOWN, p -> p.setNationalHealthId("1234567890"));

		CaseDataDto caze = creator.createCase(userDto.toReference(), person.toReference(), rdcf);

		DocumentDto document =
			createDocument("SomeDocument.txt", DocumentRelatedEntityType.CASE, caze.getUuid(), "Some content".getBytes(StandardCharsets.UTF_8));

		ExternalEmailOptionsDto options = new ExternalEmailOptionsDto(DocumentWorkflow.CASE_EMAIL, RootEntityType.ROOT_CASE, caze.toReference());
		options.setTemplateName("CaseEmail.txt");
		options.setRecipientEmail("test@mail.com");
		options.setAttachedDocuments(Collections.singleton(document.toReference()));

		assertThrows(IllegalArgumentException.class, () -> getExternalEmailFacade().sendEmail(options));

		Mockito.verify(emailService, Mockito.times(0)).sendEmail(any(), any(), any(), any());
		Mockito.verify(smsService, Mockito.times(0)).sendSms(any(), any());
	}

	@Test
	public void testSendAttachmentWithUnavailablePassword() throws MessagingException, IOException, InvalidPhoneNumberException {
		MockProducer.mockProperty(ConfigFacadeEjb.DOCUMENT_FILES_PATH, MockProducer.TMP_PATH);
		PersonDto person = creator.createPerson("Person", "Name", Sex.UNKNOWN);

		CaseDataDto caze = creator.createCase(userDto.toReference(), person.toReference(), rdcf);

		DocumentDto document =
			createDocument("SomeDocument.txt", DocumentRelatedEntityType.CASE, caze.getUuid(), "Some content".getBytes(StandardCharsets.UTF_8));

		ExternalEmailOptionsDto options = new ExternalEmailOptionsDto(DocumentWorkflow.CASE_EMAIL, RootEntityType.ROOT_CASE, caze.toReference());
		options.setTemplateName("CaseEmail.txt");
		options.setRecipientEmail("test@mail.com");
		options.setAttachedDocuments(Collections.singleton(document.toReference()));

		assertThrows(ExternalEmailException.class, () -> getExternalEmailFacade().sendEmail(options));

		Mockito.verify(emailService, Mockito.times(0)).sendEmail(any(), any(), any(), any());
		Mockito.verify(smsService, Mockito.times(0)).sendSms(any(), any());
	}

	@Test
	public void testSendAttachmentNotRelatedToEntity() throws MessagingException, IOException, InvalidPhoneNumberException {
		MockProducer.mockProperty(ConfigFacadeEjb.DOCUMENT_FILES_PATH, MockProducer.TMP_PATH);
		PersonDto person = creator.createPerson("Person", "Name", Sex.UNKNOWN, p -> {
			p.setNationalHealthId("1234567890");
		});

		CaseDataDto caze = creator.createCase(userDto.toReference(), person.toReference(), rdcf);

		DocumentDto document =
			createDocument("SomeDocument.txt", DocumentRelatedEntityType.CONTACT, "mock-uuid", "Some content".getBytes(StandardCharsets.UTF_8));

		ExternalEmailOptionsDto options = new ExternalEmailOptionsDto(DocumentWorkflow.CASE_EMAIL, RootEntityType.ROOT_CASE, caze.toReference());
		options.setTemplateName("CaseEmail.txt");
		options.setRecipientEmail("test@mail.com");
		options.setAttachedDocuments(Collections.singleton(document.toReference()));

		assertThrows(ValidationRuntimeException.class, () -> getExternalEmailFacade().sendEmail(options));

		Mockito.verify(emailService, Mockito.times(0)).sendEmail(any(), any(), any(), any());
		Mockito.verify(smsService, Mockito.times(0)).sendSms(any(), any());
	}

	private void createDocument(String fileName, DocumentRelatedEntityType relatedEntityType, String relatedEntityUuid) throws IOException {
		createDocument(fileName, relatedEntityType, relatedEntityUuid, new byte[0]);
	}

	private DocumentDto createDocument(String fileName, DocumentRelatedEntityType relatedEntityType, String relatedEntityUuid, byte[] content)
		throws IOException {
		DocumentDto document = DocumentDto.build();
		document.setName(fileName);
		document.setUploadingUser(userDto.toReference());
		document.setRelatedEntityType(relatedEntityType);
		document.setRelatedEntityUuid(relatedEntityUuid);
		document.setMimeType("application/octet-stream");

		return getDocumentFacade().saveDocument(document, content);
	}

	private PersonDto createPerson(String nationalHealthId, String phone) {
		PersonDto person = creator.createPerson("Person", "Name", Sex.UNKNOWN, p -> {
			p.setNationalHealthId(nationalHealthId);
			p.setPhone(phone);
		});
		creator.createCase(userDto.toReference(), person.toReference(), rdcf);

		return person;
	}

	private static void setupMockSmsService() {
		MockProducer.mockProperty(ConfigFacadeEjb.SMS_AUTH_KEY, "test");
		MockProducer.mockProperty(ConfigFacadeEjb.SMS_AUTH_SECRET, "test");
		MockProducer.mockProperty(ConfigFacadeEjb.SMS_SENDER_NAME, "test");
	}
}
