/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.externalemail;

import static de.symeda.sormas.backend.docgeneration.TemplateTestUtil.updateLineSeparatorsBasedOnOS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.docgeneneration.AttachementReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsWithAttachmentsDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogCriteria;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogIndexDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.messaging.EmailService;
import de.symeda.sormas.backend.common.messaging.SmsService;
import de.symeda.sormas.backend.docgeneration.AbstractDocGenerationTest;
import de.symeda.sormas.backend.document.Document;
import de.symeda.sormas.backend.document.DocumentRelatedEntities;

public class ExternalBulkEmailFacadeEjbTest extends AbstractDocGenerationTest {

	private TestDataCreator.RDCF rdcf;
	private UserDto admin;
	private UserDto userDto;
	private PersonDto personDto;
	private LocationDto locationDto2;

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

		locationDto2 = LocationDto.build();
		locationDto2.setStreet("Nauwieserstraße");
		locationDto2.setHouseNumber("7");
		locationDto2.setCity("Saarbrücken");
		locationDto2.setPostalCode("66111");

		personDto = PersonDto.build();
		personDto.setFirstName("Guy");
		personDto.setLastName("Debord");
		personDto.setSex(Sex.UNKNOWN);
		personDto.setBirthdateYYYY(1931);
		personDto.setBirthdateMM(12);
		personDto.setBirthdateDD(28);
		personDto.setAddress(locationDto2);
		personDto.setPhone("+49 681 1234");
		personDto.setNationalHealthId("1985012512523");
		personDto.setEmailAddress("testEmail@email.com");

		getPersonFacade().save(personDto);
	}

	@BeforeEach
	public void setup() throws URISyntaxException {
		reset();
	}

	@Test
	public void testSendBulkEmailToCasePerson() throws MessagingException, IOException {

		CaseDataDto caze1 = creator.createCase(userDto.toReference(), personDto.toReference(), rdcf);

		loginWith(userDto);

		locationDto2 = LocationDto.build();
		locationDto2.setStreet("Nauwieserstraße");
		locationDto2.setHouseNumber("105");
		locationDto2.setCity("Saarbrücken");
		locationDto2.setPostalCode("66111");

		PersonDto personDto2 = PersonDto.build();
		personDto2.setFirstName("John");
		personDto2.setLastName("Doe");
		personDto2.setSex(Sex.UNKNOWN);
		personDto2.setBirthdateYYYY(1980);
		personDto2.setBirthdateMM(10);
		personDto2.setBirthdateDD(22);
		personDto2.setAddress(locationDto2);
		personDto2.setPhone("+49 123 4567");
		personDto2.setNationalHealthId("1987080412582");
		personDto2.setEmailAddress("testEmail2@email.com");
		getPersonFacade().save(personDto2);
		CaseDataDto caze2 = creator.createCase(userDto.toReference(), personDto2.toReference(), rdcf);

		Mockito.doAnswer(invocation -> {
			assertThat(invocation.getArgument(0), is("testEmail@email.com"));
			assertThat(invocation.getArgument(1), is("Email subject in template"));

			StringWriter writer = new StringWriter();
			IOUtils.copy(getClass().getResourceAsStream("/docgeneration/emailTemplates/cases/CaseEmail.cmp"), writer, "UTF-8");
			String expectedContent = updateLineSeparatorsBasedOnOS(writer.toString());

			assertThat(invocation.getArgument(2), is(expectedContent));
			assertThat(invocation.getArgument(3), aMapWithSize(1));

			return null;
		}).doAnswer(invocation -> {
			assertThat(invocation.getArgument(0), is("testEmail2@email.com"));
			assertThat(invocation.getArgument(1), is("Email subject in template"));

			StringWriter writer = new StringWriter();
			IOUtils.copy(getClass().getResourceAsStream("/docgeneration/emailTemplates/cases/CaseEmail2.cmp"), writer, "UTF-8");
			String expectedContent = updateLineSeparatorsBasedOnOS(writer.toString());

			assertThat(invocation.getArgument(2), is(expectedContent));
			assertThat(invocation.getArgument(3), aMapWithSize(1));

			return null;
		}

		).when(emailService).sendEmail(any(), any(), any(), any());

		List<ReferenceDto> selectedEntries = new ArrayList<>();
		selectedEntries.add(new CaseReferenceDto(caze1.getUuid()));
		selectedEntries.add(new CaseReferenceDto(caze2.getUuid()));

		ExternalEmailOptionsWithAttachmentsDto options =
			new ExternalEmailOptionsWithAttachmentsDto(DocumentWorkflow.CASE_EMAIL, RootEntityType.ROOT_CASE);
		options.setTemplateName("CaseEmail.txt");

		DocumentDto documentDto = DocumentDto.build();
		documentDto.setUploadingUser(admin.toReference());
		documentDto.setName("attached.docx");
		documentDto.setMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

		Set<AttachementReferenceDto> attachedDocuments = new HashSet<>();
		attachedDocuments.add(
			new AttachementReferenceDto(documentDto, getClass().getResourceAsStream("/docgeneration/testcasesDocx/BasicTest.docx").readAllBytes()));
		options.setAttachedDocuments(attachedDocuments);
		getExternalEmailFacade().sendBulkEmail(options, selectedEntries);

		Mockito.verify(emailService, Mockito.times(2)).sendEmail(any(), any(), any(), any());

		List<ManualMessageLogIndexDto> messageLogs =
			getManualMessageLogFacade().getIndexList(new ManualMessageLogCriteria().messageType(MessageType.EMAIL).caze(caze1.toReference()));
		assertThat(messageLogs, hasSize(1));
		assertThat(messageLogs.get(0).getUsedTemplate(), is("CaseEmail.txt"));
		assertThat(messageLogs.get(0).getEmailAddress(), is("testEmail@email.com"));
		assertThat(messageLogs.get(0).getSendingUser(), is(userDto.toReference()));
		assertThat(messageLogs.get(0).getAttachedDocuments(), hasSize(1));
		assertThat(messageLogs.get(0).getAttachedDocuments().get(0), is("attached.docx"));

		List<ManualMessageLogIndexDto> messageLogs2 =
			getManualMessageLogFacade().getIndexList(new ManualMessageLogCriteria().messageType(MessageType.EMAIL).caze(caze2.toReference()));
		assertThat(messageLogs2, hasSize(1));
		assertThat(messageLogs2.get(0).getUsedTemplate(), is("CaseEmail.txt"));
		assertThat(messageLogs2.get(0).getEmailAddress(), is("testEmail2@email.com"));
		assertThat(messageLogs2.get(0).getSendingUser(), is(userDto.toReference()));
		assertThat(messageLogs2.get(0).getAttachedDocuments(), hasSize(1));
		assertThat(messageLogs.get(0).getAttachedDocuments().get(0), is("attached.docx"));

		Document document = getDocumentService().getByUuid(documentDto.getUuid());
		List<String> relatedEntityUuids =
			document.getDocumentRelatedEntities().stream().map(DocumentRelatedEntities::getRelatedEntityUuid).collect(Collectors.toList());

		assertThat(relatedEntityUuids, hasItem(caze1.getUuid()));
		assertThat(relatedEntityUuids, hasItem(caze2.getUuid()));
	}

}
