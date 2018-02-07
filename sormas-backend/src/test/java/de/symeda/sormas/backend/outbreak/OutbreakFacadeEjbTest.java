package de.symeda.sormas.backend.outbreak;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.outbreak.OutbreakFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb.OutbreakFacadeEjbLocal;
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

public class OutbreakFacadeEjbTest extends BaseBeanTest {
	
	/**
	 * Resets mocks to their initial state so that mock configurations are not shared between tests.
	 */
	@Before
	public void resetMocks() {
		MockProducer.resetMocks();
	}
	
	@Test
	public void testOutbreakCreationAndDeletion() {
		OutbreakFacade outbreakFacade = getBean(OutbreakFacadeEjbLocal.class);
		
		TestDataCreator creator = createTestDataCreator();
		
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		OutbreakDto outbreak = creator.createOutbreak(rdcf, Disease.EVD, user.toReference());
		
		// Database should contain one outbreak
		assertEquals(1, outbreakFacade.getAllAfter(null).size());
		
		outbreakFacade.deleteOutbreak(outbreak);
		
		// Database should contain no outbreak
		assertEquals(0, outbreakFacade.getAllAfter(null).size());
	}
	
	private TestDataCreator createTestDataCreator() {
		return new TestDataCreator(getBean(UserFacadeEjbLocal.class), getBean(PersonFacadeEjbLocal.class),
				getBean(CaseFacadeEjbLocal.class), getBean(ContactFacadeEjbLocal.class), getBean(TaskFacadeEjb.class),
				getBean(VisitFacadeEjb.class), getBean(WeeklyReportFacadeEjbLocal.class), getBean(EventFacadeEjb.class), getBean(EventParticipantFacadeEjb.class),
				getBean(SampleFacadeEjb.class), getBean(SampleTestFacadeEjbLocal.class), getBean(RegionFacadeEjbLocal.class), 
				getBean(DistrictFacadeEjbLocal.class), getBean(CommunityFacadeEjb.class), getBean(FacilityFacadeEjbLocal.class), 
				getBean(RegionService.class), getBean(DistrictService.class), getBean(CommunityService.class), getBean(FacilityService.class),
				getBean(OutbreakFacadeEjbLocal.class));
	}

}
