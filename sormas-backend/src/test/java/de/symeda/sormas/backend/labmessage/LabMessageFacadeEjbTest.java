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

package de.symeda.sormas.backend.labmessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.TestDataCreator;
import org.junit.Test;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.backend.AbstractBeanTest;

public class LabMessageFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetByReportIdWithCornerCaseInput() {
		String reportId = "123456789";
		creator.createLabMessage((lm) -> lm.setReportId(reportId));

		List<LabMessageDto> list = getLabMessageFacade().getByReportId(null);

		assertNotNull(list);
		assertTrue(list.isEmpty());

		list = getLabMessageFacade().getByReportId("");

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

		List<LabMessageDto> list = getLabMessageFacade().getByReportId(reportId);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		assertEquals(reportId, list.get(0).getReportId());
	}

	@Test
	public void testGetByUuid() {

		LabMessageDto labMessage = creator.createLabMessage(null);

		LabMessageDto result = getLabMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(result, equalTo(labMessage));

		getLabMessageFacade().deleteLabMessage(labMessage.getUuid());

		// deleted lab messages shall still be returned
		result = getLabMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(result, equalTo(labMessage));

	}

	@Test
	public void testExistsForwardedLabMessageWith() {

		String reportId = "1234";

		// create noise
		creator.createLabMessage((lm) -> lm.setStatus(LabMessageStatus.FORWARDED));

		assertFalse(getLabMessageFacade().existsForwardedLabMessageWith(reportId));
		assertFalse(getLabMessageFacade().existsForwardedLabMessageWith(null));

		creator.createLabMessage((lm) -> lm.setReportId(reportId));

		assertFalse(getLabMessageFacade().existsForwardedLabMessageWith(reportId));

		LabMessageDto forwardedMessage = creator.createLabMessage((lm) -> {
			lm.setReportId(reportId);
			lm.setStatus(LabMessageStatus.FORWARDED);
		});

		assertTrue(getLabMessageFacade().existsForwardedLabMessageWith(reportId));

		getLabMessageFacade().deleteLabMessage(forwardedMessage.getUuid());

		assertTrue(getLabMessageFacade().existsForwardedLabMessageWith(reportId));
	}

	@Test
	public void testExistsLabMessageForEntityCase() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		assertFalse(getLabMessageFacade().existsLabMessageForEntity(caze.toReference()));

		// create noise
		CaseDataDto noiseCaze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		creator.createSample(noiseCaze.toReference(), user.toReference(), rdcf.facility);

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		assertFalse(getLabMessageFacade().existsLabMessageForEntity(caze.toReference()));

		creator.createLabMessage(lm -> lm.setSample(sample.toReference()));
		assertTrue(getLabMessageFacade().existsLabMessageForEntity(caze.toReference()));

		// create additional matches
		creator.createLabMessage(lm -> lm.setSample(sample.toReference()));
		SampleDto sample2 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		creator.createLabMessage(lm -> lm.setSample(sample2.toReference()));
		assertTrue(getLabMessageFacade().existsLabMessageForEntity(caze.toReference()));
	}

	@Test
	public void testExistsLabMessageForEntityContact() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());

		assertFalse(getLabMessageFacade().existsLabMessageForEntity(contact.toReference()));

		// create noise
		ContactDto noiseContact = creator.createContact(user.toReference(), person.toReference());
		creator.createSample(noiseContact.toReference(), user.toReference(), rdcf.facility, null);

		SampleDto sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		assertFalse(getLabMessageFacade().existsLabMessageForEntity(contact.toReference()));

		creator.createLabMessage(lm -> lm.setSample(sample.toReference()));
		assertTrue(getLabMessageFacade().existsLabMessageForEntity(contact.toReference()));

		// create additional matches
		creator.createLabMessage(lm -> lm.setSample(sample.toReference()));
		SampleDto sample2 = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		creator.createLabMessage(lm -> lm.setSample(sample2.toReference()));
		assertTrue(getLabMessageFacade().existsLabMessageForEntity(contact.toReference()));
	}

	@Test
	public void testExistsLabMessageForEntityEventParticipant() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		EventDto event = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());

		assertFalse(getLabMessageFacade().existsLabMessageForEntity(eventParticipant.toReference()));

		// create noise
		EventParticipantDto noiseEventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		creator.createSample(noiseEventParticipant.toReference(), user.toReference(), rdcf.facility);

		SampleDto sample = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);
		assertFalse(getLabMessageFacade().existsLabMessageForEntity(eventParticipant.toReference()));

		creator.createLabMessage(lm -> lm.setSample(sample.toReference()));
		assertTrue(getLabMessageFacade().existsLabMessageForEntity(eventParticipant.toReference()));

		// create additional matches
		creator.createLabMessage(lm -> lm.setSample(sample.toReference()));
		SampleDto sample2 = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);
		creator.createLabMessage(lm -> lm.setSample(sample2.toReference()));
		assertTrue(getLabMessageFacade().existsLabMessageForEntity(eventParticipant.toReference()));
	}
}
