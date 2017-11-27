package de.symeda.sormas.backend.event;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.DashboardEvent;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sample.SampleTestFacadeEjb.SampleTestFacadeEjbLocal;
import de.symeda.sormas.backend.task.TaskFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;
import info.novatec.beantest.api.BaseBeanTest;

public class EventFacadeEjbTest extends BaseBeanTest {

	/**
	 * Resets mocks to their initial state so that mock configurations are not shared between tests.
	 */
	@Before
	public void resetMocks() {
		MockProducer.resetMocks();
	}

	@Test
	public void testDashboardEventListCreation() {
		EventFacade eventFacade = getBean(EventFacadeEjb.class);
		DistrictFacade districtFacade = getBean(DistrictFacadeEjbLocal.class);

		TestDataCreator creator = createTestDataCreator();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		LocationDto eventLocation = new LocationDto();
		eventLocation.setDistrict(districtFacade.getDistrictReferenceByUuid(rdcf.district.getUuid()));
		EventDto event = creator.createEvent(EventType.OUTBREAK, EventStatus.POSSIBLE, "Description", "First", "Name", "12345", TypeOfPlace.PUBLIC_PLACE, DateHelper.subtractDays(new Date(), 1), new Date(), user, user, Disease.EVD, eventLocation);

		List<DashboardEvent> dashboardEvents = eventFacade.getNewEventsForDashboard(eventLocation.getDistrict(), event.getDisease(), DateHelper.subtractDays(new Date(),  1), DateHelper.addDays(new Date(), 1), user.getUuid());

		// List should have one entry
		assertEquals(1, dashboardEvents.size());
	}

	@Test
	public void testEventDeletion() {
		EventFacade eventFacade = getBean(EventFacadeEjb.class);
		EventParticipantFacade eventParticipantFacade = getBean(EventParticipantFacadeEjb.class);
		DistrictFacade districtFacade = getBean(DistrictFacadeEjbLocal.class);

		TestDataCreator creator = createTestDataCreator();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		String userUuid = user.getUuid();
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		LocationDto eventLocation = new LocationDto();
		eventLocation.setDistrict(districtFacade.getDistrictReferenceByUuid(rdcf.district.getUuid()));
		EventDto event = creator.createEvent(EventType.OUTBREAK, EventStatus.POSSIBLE, "Description", "First", "Name", "12345", TypeOfPlace.PUBLIC_PLACE, DateHelper.subtractDays(new Date(), 1), new Date(), user, user, Disease.EVD, eventLocation);
		PersonDto eventPerson = creator.createPerson("Event", "Person");
		creator.createEventParticipant(event, eventPerson, "Description");

		// Database should contain one event and event participant
		assertEquals(1, eventFacade.getAllEventsAfter(null, userUuid).size());
		assertEquals(1, eventParticipantFacade.getAllEventParticipantsAfter(null, userUuid).size());

		eventFacade.deleteEvent(event, adminUuid);

		// Database should contain no event or associated event participant
		assertEquals(0, eventFacade.getAllEventsAfter(null, userUuid).size());
		assertEquals(0, eventParticipantFacade.getAllEventParticipantsAfter(null, userUuid).size());
	}

	private TestDataCreator createTestDataCreator() {
		return new TestDataCreator(getBean(UserFacadeEjbLocal.class), getBean(PersonFacadeEjbLocal.class),
				getBean(CaseFacadeEjbLocal.class), getBean(ContactFacadeEjbLocal.class), getBean(TaskFacadeEjb.class),
				getBean(VisitFacadeEjb.class), getBean(WeeklyReportFacadeEjbLocal.class), getBean(EventFacadeEjb.class), getBean(EventParticipantFacadeEjb.class),
				getBean(SampleFacadeEjb.class), getBean(SampleTestFacadeEjbLocal.class), getBean(RegionFacadeEjbLocal.class), 
				getBean(DistrictFacadeEjbLocal.class), getBean(CommunityFacadeEjb.class), getBean(FacilityFacadeEjbLocal.class), 
				getBean(RegionService.class), getBean(DistrictService.class), getBean(CommunityService.class), getBean(FacilityService.class));
	}
}
