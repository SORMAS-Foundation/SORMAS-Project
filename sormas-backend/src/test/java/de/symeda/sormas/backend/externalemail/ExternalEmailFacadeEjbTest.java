/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.backend.externalemail;

import static de.symeda.sormas.backend.docgeneration.TemplateTestUtil.cleanLineSeparators;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

import javax.mail.MessagingException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.externalemail.ExternalEmailException;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.messaging.EmailService;
import de.symeda.sormas.backend.docgeneration.AbstractDocGenerationTest;

public class ExternalEmailFacadeEjbTest extends AbstractDocGenerationTest {

	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private TestDataCreator.RDCF rdcf;
	private UserDto userDto;

	private PersonDto personDto;

	@Mock
	private EmailService emailService;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility", "PointOfEntry");

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
	public void testSendEmailToCasePerson() throws DocumentTemplateException, ExternalEmailException, MessagingException {

		CaseDataDto caze = creator.createCase(userDto.toReference(), rdcf, (c) -> {
			c.setDisease(Disease.CORONAVIRUS);
			c.setPerson(personDto.toReference());
		});

		Mockito.doAnswer(invocation -> {
			assertThat(invocation.getArgument(0), is("test@mail.com"));
			assertThat(invocation.getArgument(1), is("Email subject in template"));

			StringWriter writer = new StringWriter();
			IOUtils.copy(getClass().getResourceAsStream("/docgeneration/emailTemplates/cases/CaseEmail.cmp"), writer, "UTF-8");
			String expectedContent = cleanLineSeparators(writer.toString());

			assertThat(invocation.getArgument(2), is(expectedContent));

			return null;
		}).when(emailService).sendEmail(any(), any(), any());

		ExternalEmailOptionsDto options = new ExternalEmailOptionsDto(DocumentWorkflow.CASE_EMAIL, RootEntityType.ROOT_CASE, caze.toReference());
		options.setTemplateName("CaseEmail.txt");
		options.setRecipientEmail("test@mail.com");
		getExternalEmailFacade().sendEmail(options);
	}

	@Test
	public void testSendEmailToContactPerson() throws DocumentTemplateException, ExternalEmailException, MessagingException {

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
		}).when(emailService).sendEmail(any(), any(), any());

		ExternalEmailOptionsDto options =
			new ExternalEmailOptionsDto(DocumentWorkflow.CONTACT_EMAIL, RootEntityType.ROOT_CONTACT, contact.toReference());
		options.setTemplateName("ContactEmail.txt");
		options.setRecipientEmail("test@mail.com");
		getExternalEmailFacade().sendEmail(options);
	}
}
