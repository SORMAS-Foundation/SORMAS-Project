package de.symeda.sormas.backend.report;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.report.WeeklyReportSummaryDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb.OutbreakFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
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

public class WeeklyReportFacadeEjbTest extends BaseBeanTest {

	/**
	 * Resets mocks to their initial state so that mock configurations are not shared between tests.
	 */
	@Before
	public void resetMocks() {
		MockProducer.resetMocks();
	}
	
	@Test
	public void testShouldBuildDistrictSummaryDto() {
		RegionFacade regionFacade = getBean(RegionFacadeEjbLocal.class);
		WeeklyReportFacade reportFacade = getBean(WeeklyReportFacadeEjbLocal.class);
		
		TestDataCreator creator = createTestDataCreator();
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto informant1 = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Info", "One", UserRole.INFORMANT);
		creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Info", "Two", UserRole.INFORMANT);
		RDCF rdcf2 = creator.new RDCF(rdcf.region, rdcf.district, rdcf.community, creator.createFacility("Facility2", rdcf.region, rdcf.district, rdcf.community));
		UserDto informant3 = creator.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Info", "Three", UserRole.INFORMANT);
		District district2 = creator.createDistrict("District2", rdcf.region);
		Community community2 = creator.createCommunity("Community2", district2);
		Facility facility3 = creator.createFacility("Facility3", rdcf.region, district2, community2);
		RDCF rdcf3 = creator.new RDCF(rdcf.region, district2, community2, facility3);
		UserDto informant4 = creator.createUser(rdcf3.region.getUuid(), rdcf3.district.getUuid(), rdcf3.facility.getUuid(), "Info", "Four", UserRole.INFORMANT);
		
		EpiWeek previousEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		creator.createWeeklyReport(rdcf.facility.getUuid(), informant1.toReference(), new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 1);
		creator.createWeeklyReport(rdcf2.facility.getUuid(), informant3.toReference(), new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 1);
		creator.createWeeklyReport(rdcf3.facility.getUuid(), informant4.toReference(), new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 0);
		
		List<WeeklyReportSummaryDto> reportSummaries = reportFacade.getSummariesPerDistrict(regionFacade.getRegionReferenceByUuid(rdcf.region.getUuid()), previousEpiWeek);
		// Two districts have submitted reports, so there should be two summaries
		assertEquals(2, reportSummaries.size());
		// The first district has one submitted and one missing report
		assertEquals(1, reportSummaries.get(0).getReports());
		assertEquals(1, reportSummaries.get(0).getMissingReports());
		// The second district has one zero report
		assertEquals(1, reportSummaries.get(1).getZeroReports());
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
