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

package de.symeda.sormas.backend.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class EventServiceTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private UserDto nationalUser;

	@Override
	public void init() {

		super.init();
		rdcf = creator.createRDCF("Region", "District", "Community", "Facility", "Point of entry");
		nationalUser = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.community.getUuid(),
			rdcf.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
	}

	@Test
	public void testHasRegionAndDistrict() {

		EventDto event1 = creator.createEvent(nationalUser.toReference());
		assertFalse(getEventService().hasRegionAndDistrict(event1.getUuid()));

		EventDto event2 = creator.createEvent(nationalUser.toReference(), Disease.EVD, e -> {
			e.getEventLocation().setRegion(rdcf.region);
			e.getEventLocation().setDistrict(rdcf.district);
		});
		assertTrue(getEventService().hasRegionAndDistrict(event2.getUuid()));
	}

	@Test
	public void testHasAnyEventParticipantWithoutJurisdiction() {

		EventDto event = creator.createEvent(nationalUser.toReference());
		PersonDto person1 = creator.createPerson();
		creator.createEventParticipant(event.toReference(), person1, "", nationalUser.toReference(), e -> {
			e.setRegion(rdcf.region);
			e.setDistrict(rdcf.district);
		}, null);
		assertFalse(getEventService().hasAnyEventParticipantWithoutJurisdiction(event.getUuid()));

		PersonDto person2 = creator.createPerson();
		creator.createEventParticipant(event.toReference(), person2, nationalUser.toReference());
		assertTrue(getEventService().hasAnyEventParticipantWithoutJurisdiction(event.getUuid()));
	}

	@Test
	public void testGetEventSummaryDetailsByContactsEventArchivingAndDeletion() {
		TestDataCreator.RDCFEntities rdcfEntities = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		TestDataCreator.RDCF rdcf = new TestDataCreator.RDCF(rdcfEntities);
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");

		ContactDto contact = creator.createContact(rdcf, user.toReference(), contactPerson.toReference());

		EventDto event = creator.createEvent(user.toReference(), contact.getDisease());
		creator.createEventParticipant(event.toReference(), contactPerson, user.toReference());

		EventService sut = getEventService();

		List<ContactEventSummaryDetails> result = sut.getEventSummaryDetailsByContacts(Arrays.asList(contact.getUuid()));
		assertEquals(1, result.size());
		assertEquals(event.getUuid(), result.get(0).getEventUuid());
		assertEquals(event.getEventTitle(), result.get(0).getEventTitle());

		// archiving should not have any effect on the export list
		getEventFacade().archive(Collections.singletonList(event.getUuid()));

		result = sut.getEventSummaryDetailsByContacts(Arrays.asList(contact.getUuid()));
		assertEquals(1, result.size());
		assertEquals(event.getUuid(), result.get(0).getEventUuid());
		assertEquals(event.getEventTitle(), result.get(0).getEventTitle());

		// deletion should have an effect on the export list
		getEventFacade().delete(event.getUuid(), new DeletionDetails());

		result = sut.getEventSummaryDetailsByContacts(Arrays.asList(contact.getUuid()));
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetEventSummaryDetailsByCasesEventArchivingAndDeletion() {
		TestDataCreator.RDCFEntities rdcfEntities = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		TestDataCreator.RDCF rdcf = new TestDataCreator.RDCF(rdcfEntities);
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto contactPerson = creator.createPerson("Case", "Person");

		CaseDataDto caze = creator.createCase(user.toReference(), contactPerson.toReference(), rdcf);

		EventDto event = creator.createEvent(user.toReference(), caze.getDisease());
		EventParticipantDto participant = creator.createEventParticipant(event.toReference(), contactPerson, user.toReference());
		participant.setResultingCase(caze.toReference());
		getEventParticipantFacade().save(participant);

		EventService sut = getEventService();

		Long cazeId = getCaseService().getIdByUuid(caze.getUuid());
		List<EventSummaryDetails> result = sut.getEventSummaryDetailsByCases(Arrays.asList(cazeId));
		assertEquals(1, result.size());
		assertEquals(event.getUuid(), result.get(0).getEventUuid());
		assertEquals(event.getEventTitle(), result.get(0).getEventTitle());

		// archiving should not have any effect on the export list
		getEventFacade().archive(Collections.singletonList(event.getUuid()));

		result = sut.getEventSummaryDetailsByCases(Arrays.asList(cazeId));
		assertEquals(1, result.size());
		assertEquals(event.getUuid(), result.get(0).getEventUuid());
		assertEquals(event.getEventTitle(), result.get(0).getEventTitle());

		// deletion should have an effect on the export list
		getEventFacade().delete(event.getUuid(), new DeletionDetails());

		result = sut.getEventSummaryDetailsByCases(Arrays.asList(cazeId));
		assertTrue(result.isEmpty());
	}
}
