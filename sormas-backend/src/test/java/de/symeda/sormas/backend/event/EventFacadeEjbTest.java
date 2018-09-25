package de.symeda.sormas.backend.event;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
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

		List<DashboardEventDto> dashboardEventDtos = getEventFacade().getNewEventsForDashboard(eventLocation.getRegion(), eventLocation.getDistrict(), event.getDisease(), DateHelper.subtractDays(new Date(),  1), DateHelper.addDays(new Date(), 1), user.getUuid());

		// List should have one entry
		assertEquals(1, dashboardEventDtos.size());
	}

	@Test
	public void testEventDeletion() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		String userUuid = user.getUuid();
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		LocationDto eventLocation = new LocationDto();
		eventLocation.setDistrict(getDistrictFacade().getDistrictReferenceByUuid(rdcf.district.getUuid()));
		EventDto event = creator.createEvent(EventType.OUTBREAK, EventStatus.POSSIBLE, "Description", "First", "Name", "12345", TypeOfPlace.PUBLIC_PLACE, DateHelper.subtractDays(new Date(), 1), new Date(), user.toReference(), user.toReference(), Disease.EVD, eventLocation);
		PersonDto eventPerson = creator.createPerson("Event", "Person");
		creator.createEventParticipant(event.toReference(), eventPerson, "Description");

		// Database should contain one event and event participant
		assertEquals(1, getEventFacade().getAllEventsAfter(null, userUuid).size());
		assertEquals(1, getEventParticipantFacade().getAllEventParticipantsAfter(null, userUuid).size());

		getEventFacade().deleteEvent(event.toReference(), adminUuid);

		// Database should contain no event or associated event participant
		assertEquals(0, getEventFacade().getAllEventsAfter(null, userUuid).size());
		assertEquals(0, getEventParticipantFacade().getAllEventParticipantsAfter(null, userUuid).size());
	}
	
	@Test
	public void testGetIndexList() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		LocationDto eventLocation = new LocationDto();
		eventLocation.setDistrict(getDistrictFacade().getDistrictReferenceByUuid(rdcf.district.getUuid()));
		creator.createEvent(EventType.OUTBREAK, EventStatus.POSSIBLE, "Description", "First", "Name", "12345", TypeOfPlace.PUBLIC_PLACE, DateHelper.subtractDays(new Date(), 1), new Date(), user.toReference(), user.toReference(), Disease.EVD, eventLocation);

		List<EventIndexDto> results = getEventFacade().getIndexList(user.getUuid(), null);

		// List should have one entry
		assertEquals(1, results.size());
	}
	
}
