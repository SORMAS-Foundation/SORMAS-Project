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
package de.symeda.sormas.backend.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class EventFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testDashboardEventListCreation() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		LocationDto eventLocation = new LocationDto();
		eventLocation.setDistrict(getDistrictFacade().getDistrictReferenceByUuid(rdcf.district.getUuid()));
		EventDto event = creator.createEvent(EventType.OUTBREAK, EventStatus.POSSIBLE, "Description", "First", "Name", "12345", TypeOfPlace.PUBLIC_PLACE, DateHelper.subtractDays(new Date(), 1), new Date(), user.toReference(), user.toReference(), Disease.EVD, eventLocation);

		List<DashboardEventDto> dashboardEventDtos = getEventFacade().getNewEventsForDashboard(new EventCriteria().region(eventLocation.getRegion()).district(eventLocation.getDistrict()).disease(event.getDisease()).reportedBetween(DateHelper.subtractDays(new Date(),  1), DateHelper.addDays(new Date(), 1)), user.getUuid());

		// List should have one entry
		assertEquals(1, dashboardEventDtos.size());
	}

	@Test
	public void testEventDeletion() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		LocationDto eventLocation = new LocationDto();
		eventLocation.setDistrict(getDistrictFacade().getDistrictReferenceByUuid(rdcf.district.getUuid()));
		EventDto event = creator.createEvent(EventType.OUTBREAK, EventStatus.POSSIBLE, "Description", "First", "Name", "12345", TypeOfPlace.PUBLIC_PLACE, DateHelper.subtractDays(new Date(), 1), new Date(), user.toReference(), user.toReference(), Disease.EVD, eventLocation);
		PersonDto eventPerson = creator.createPerson("Event", "Person");
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), eventPerson, "Description");

		// Database should contain the created event and event participant
		assertNotNull(getEventFacade().getEventByUuid(event.getUuid()));
		assertNotNull(getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()));

		getEventFacade().deleteEvent(event.toReference(), adminUuid);

		// Database should not contain the deleted event and event participant
		assertNull(getEventFacade().getEventByUuid(event.getUuid()));
		assertNull(getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()));
	}
	
	@Test
	public void testGetIndexList() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		LocationDto eventLocation = new LocationDto();
		eventLocation.setDistrict(getDistrictFacade().getDistrictReferenceByUuid(rdcf.district.getUuid()));
		creator.createEvent(EventType.OUTBREAK, EventStatus.POSSIBLE, "Description", "First", "Name", "12345", TypeOfPlace.PUBLIC_PLACE, DateHelper.subtractDays(new Date(), 1), new Date(), user.toReference(), user.toReference(), Disease.EVD, eventLocation);

		List<EventIndexDto> results = getEventFacade().getIndexList(user.getUuid(), null, 0, 100, null);

		// List should have one entry
		assertEquals(1, results.size());
	}

	@Test
	public void testArchiveOrDearchiveEvent() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		LocationDto eventLocation = new LocationDto();
		eventLocation.setDistrict(getDistrictFacade().getDistrictReferenceByUuid(rdcf.district.getUuid()));
		EventDto event = creator.createEvent(EventType.OUTBREAK, EventStatus.POSSIBLE, "Description", "First", "Name", "12345", TypeOfPlace.PUBLIC_PLACE, DateHelper.subtractDays(new Date(), 1), new Date(), user.toReference(), user.toReference(), Disease.EVD, eventLocation);
		PersonDto eventPerson = creator.createPerson("Event", "Person");
		creator.createEventParticipant(event.toReference(), eventPerson, "Description");
		Date testStartDate = new Date();
		
		// getAllActiveEvents/getAllActiveEventParticipants and getAllUuids should return length 1
		assertEquals(1, getEventFacade().getAllActiveEventsAfter(null, user.getUuid()).size());
		assertEquals(1, getEventFacade().getAllActiveUuids(user.getUuid()).size());
		assertEquals(1, getEventParticipantFacade().getAllActiveEventParticipantsAfter(null, user.getUuid()).size());
		assertEquals(1, getEventParticipantFacade().getAllActiveUuids(user.getUuid()).size());
		
		getEventFacade().archiveOrDearchiveEvent(event.getUuid(), true);
		
		// getAllActiveEvents/getAllActiveEventParticipants and getAllUuids should return length 0
		assertEquals(0, getEventFacade().getAllActiveEventsAfter(null, user.getUuid()).size());
		assertEquals(0, getEventFacade().getAllActiveUuids(user.getUuid()).size());
		assertEquals(0, getEventParticipantFacade().getAllActiveEventParticipantsAfter(null, user.getUuid()).size());
		assertEquals(0, getEventParticipantFacade().getAllActiveUuids(user.getUuid()).size());

		// getArchivedUuidsSince should return length 1
		assertEquals(1, getEventFacade().getArchivedUuidsSince(user.getUuid(), testStartDate).size());

		getEventFacade().archiveOrDearchiveEvent(event.getUuid(), false);

		// getAllActiveEvents/getAllActiveEventParticipants and getAllUuids should return length 1
		assertEquals(1, getEventFacade().getAllActiveEventsAfter(null, user.getUuid()).size());
		assertEquals(1, getEventFacade().getAllActiveUuids(user.getUuid()).size());
		assertEquals(1, getEventParticipantFacade().getAllActiveEventParticipantsAfter(null, user.getUuid()).size());
		assertEquals(1, getEventParticipantFacade().getAllActiveUuids(user.getUuid()).size());

		// getArchivedUuidsSince should return length 0
		assertEquals(0, getEventFacade().getArchivedUuidsSince(user.getUuid(), testStartDate).size());
	}

}
