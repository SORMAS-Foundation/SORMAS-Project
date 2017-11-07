package de.symeda.sormas.backend.event;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventDashboardDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sample.SampleTestFacadeEjb;
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
	public void testEventDashboardDtoListCreation() {
		EventFacade eventFacade = getBean(EventFacadeEjb.class);
		
		TestDataCreator creator = createTestDataCreator();
		
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		creator.createEvent(EventType.OUTBREAK, EventStatus.POSSIBLE, "Description", "First", "Name", "12345", TypeOfPlace.PUBLIC_PLACE, DateHelper.subtractDays(new Date(), 1), new Date(), user, user, Disease.EVD);
		
		List<EventDashboardDto> dashboardDtos = eventFacade.getNewEventsBetween(DateHelper.subtractDays(new Date(),  1), DateHelper.addDays(new Date(), 1), user.getUuid());
		
		// List should have one entry
		assertEquals(1, dashboardDtos.size());
	}
	
	private TestDataCreator createTestDataCreator() {
		return new TestDataCreator(getBean(UserFacadeEjbLocal.class), getBean(PersonFacadeEjb.class),
				getBean(CaseFacadeEjbLocal.class), getBean(ContactFacadeEjbLocal.class), getBean(TaskFacadeEjb.class),
				getBean(VisitFacadeEjb.class), getBean(WeeklyReportFacadeEjbLocal.class), getBean(EventFacadeEjb.class), 
				getBean(SampleFacadeEjb.class), getBean(SampleTestFacadeEjb.class), getBean(RegionFacadeEjb.class), 
				getBean(DistrictFacadeEjb.class), getBean(CommunityFacadeEjb.class), getBean(FacilityFacadeEjb.class), 
				getBean(RegionService.class), getBean(DistrictService.class), getBean(CommunityService.class), getBean(FacilityService.class));
	}
}
