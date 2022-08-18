/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.externalmessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class ExternalMessageFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetByReportIdWithCornerCaseInput() {
		String reportId = "123456789";
		creator.createLabMessage((lm) -> lm.setReportId(reportId));

		List<ExternalMessageDto> list = getExternalMessageFacade().getByReportId(null);

		assertNotNull(list);
		assertTrue(list.isEmpty());

		list = getExternalMessageFacade().getByReportId("");

		assertNotNull(list);
		assertTrue(list.isEmpty());
	}

	@Test
	public void testGetByReportIdWithOneMessage() {

		String reportId = "123456789";
		creator.createLabMessage((lm) -> lm.setReportId(reportId));

		// create noise
		creator.createLabMessage(null);
		creator.createLabMessage((lm) -> lm.setReportId("some-other-id"));

		List<ExternalMessageDto> list = getExternalMessageFacade().getByReportId(reportId);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		assertEquals(reportId, list.get(0).getReportId());
	}

	@Test
	public void testGetByUuid() {

		ExternalMessageDto labMessage = creator.createLabMessage(null);

		ExternalMessageDto result = getExternalMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(result, equalTo(labMessage));
	}

	@Test
	public void testExistsForwardedLabMessageWith() {

		String reportId = "1234";

		// create noise
		creator.createLabMessage((lm) -> lm.setStatus(ExternalMessageStatus.FORWARDED));

		assertFalse(getExternalMessageFacade().existsForwardedExternalMessageWith(reportId));
		assertFalse(getExternalMessageFacade().existsForwardedExternalMessageWith(null));

		creator.createLabMessage((lm) -> lm.setReportId(reportId));

		assertFalse(getExternalMessageFacade().existsForwardedExternalMessageWith(reportId));

		ExternalMessageDto forwardedMessage = creator.createLabMessage((lm) -> {
			lm.setReportId(reportId);
			lm.setStatus(ExternalMessageStatus.FORWARDED);
		});

		assertTrue(getExternalMessageFacade().existsForwardedExternalMessageWith(reportId));
	}

	@Test
	public void testExistsLabMessageForEntityCase() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));

		// create noise
		CaseDataDto noiseCaze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		creator.createSample(noiseCaze.toReference(), user.toReference(), rdcf.facility);

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));

		creator.createLabMessageWithTestReport(sample.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));

		// create additional matches
		creator.createLabMessageWithTestReport(sample.toReference());
		SampleDto sample2 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		creator.createLabMessageWithTestReport(sample2.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));
	}

	@Test
	public void testExistsLabMessageForEntityContact() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());

		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));

		// create noise
		ContactDto noiseContact = creator.createContact(user.toReference(), person.toReference());
		creator.createSample(noiseContact.toReference(), user.toReference(), rdcf.facility, null);

		SampleDto sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));

		creator.createLabMessageWithTestReport(sample.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));

		// create additional matches
		creator.createLabMessageWithTestReport(sample.toReference());
		SampleDto sample2 = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		creator.createLabMessageWithTestReport(sample2.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));
	}

	@Test
	public void testExistsLabMessageForEntityEventParticipant() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		EventDto event = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());

		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));

		// create noise
		EventParticipantDto noiseEventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		creator.createSample(noiseEventParticipant.toReference(), user.toReference(), rdcf.facility);

		SampleDto sample = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);
		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));

		creator.createLabMessageWithTestReport(sample.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));

		// create additional matches
		creator.createLabMessageWithTestReport(sample.toReference());
		SampleDto sample2 = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);
		creator.createLabMessageWithTestReport(sample2.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));
	}

//	This test currently does not work because the bean tests used don't support @TransactionAttribute tags.
//	This test should be enabled once there is a new test framework in use.
//	@Test
//	public void testSaveWithFallback() {
//
//		// valid message
//		ExternalMessageDto validMessage = ExternalMessageDto.build();
//		validMessage.setReportId("reportId");
//		validMessage.setStatus(ExternalMessageStatus.FORWARDED);
//		validMessage.setTestReports(Collections.singletonList(TestReportDto.build()));
//		validMessage.setPersonFirstName("Dude");
//		validMessage.setExternalMessageDetails("Details");
//		getLabMessageFacade().saveWithFallback(validMessage);
//		ExternalMessageDto savedMessage = getLabMessageFacade().getByUuid(validMessage.getUuid());
//		assertEquals(validMessage, savedMessage);
//
//		// Invalid message
//		ExternalMessageDto invalidMessage = ExternalMessageDto.build();
//		invalidMessage.setExternalMessageDetails("Details");
//		invalidMessage.setPersonFirstName(String.join("", Collections.nCopies(50, "MaliciousDude")));
//		getLabMessageFacade().saveWithFallback(invalidMessage);
//		savedMessage = getLabMessageFacade().getByUuid(invalidMessage.getUuid());
//		assertEquals(invalidMessage.getUuid(), savedMessage.getUuid());
//		assertEquals(invalidMessage.getStatus(), savedMessage.getStatus());
//		assertEquals(invalidMessage.getExternalMessageDetails(), savedMessage.getExternalMessageDetails());
//		assertNull(savedMessage.getPersonFirstName());
//
//		// make sure that valid message still exists
//		savedMessage = getLabMessageFacade().getByUuid(validMessage.getUuid());
//		assertEquals(validMessage, savedMessage);
//
//	}
}
