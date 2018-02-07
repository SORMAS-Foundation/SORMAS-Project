package de.symeda.sormas.backend.caze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
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
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;
import info.novatec.beantest.api.BaseBeanTest;

public class CaseFacadeEjbTest extends BaseBeanTest {

	/**
	 * Resets mocks to their initial state so that mock configurations are not shared between tests.
	 */
	@Before
	public void resetMocks() {
		MockProducer.resetMocks();
	}

	@Test
	public void testDiseaseChangeUpdatesContacts() {
		CaseFacade caseFacade = getBean(CaseFacadeEjbLocal.class);
		ContactFacade contactFacade = getBean(ContactFacadeEjbLocal.class);

		TestDataCreator creator = createTestDataCreator();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());

		// Follow-up status and duration should be set to the requirements for EVD
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));

		caze.setDisease(Disease.MEASLES);
		caze = caseFacade.saveCase(caze);

		// Follow-up status and duration should be set to no follow-up and null respectively because
		// Measles does not require a follow-up
		contact = contactFacade.getContactByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.NO_FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(null, contact.getFollowUpUntil());
	}

	@Test
	public void testMovingCaseUpdatesTaskAssigneeAndCreatesPreviousHospitalization() {
		CaseFacade caseFacade = getBean(CaseFacadeEjbLocal.class);
		TaskFacade taskFacade = getBean(TaskFacadeEjb.class);
		CommunityFacade communityFacade = getBean(CommunityFacadeEjb.class);
		FacilityFacade facilityFacade = getBean(FacilityFacadeEjbLocal.class);

		TestDataCreator creator = createTestDataCreator();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		UserDto caseOfficer = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Case", "Officer", UserRole.CASE_OFFICER);
		TaskDto pendingTask = creator.createTask(TaskContext.CASE, TaskType.CASE_INVESTIGATION, TaskStatus.PENDING, caze.toReference(), null, new Date(), user.toReference());
		TaskDto doneTask = creator.createTask(TaskContext.CASE, TaskType.CASE_INVESTIGATION, TaskStatus.DONE, caze.toReference(), null, new Date(), user.toReference());

		RDCF newRDCF = creator.createRDCF("New Region", "New District", "New Community", "New Facility");
		caseFacade.moveCase(caze.toReference(), communityFacade.getCommunityReferenceByUuid(newRDCF.community.getUuid()), facilityFacade.getFacilityReferenceByUuid(newRDCF.facility.getUuid()), caze.getHealthFacilityDetails(), caseOfficer.toReference());

		caze = caseFacade.getCaseDataByUuid(caze.getUuid());
		pendingTask = taskFacade.getByUuid(pendingTask.getUuid());
		doneTask = taskFacade.getByUuid(doneTask.getUuid());

		// Case should have the new region, district, community and facility set
		assertEquals(caze.getRegion().getUuid(), newRDCF.region.getUuid());
		assertEquals(caze.getDistrict().getUuid(), newRDCF.district.getUuid());
		assertEquals(caze.getCommunity().getUuid(), newRDCF.community.getUuid());
		assertEquals(caze.getHealthFacility().getUuid(), newRDCF.facility.getUuid());

		// Pending task should've been reassigned to the case officer, done task should still be assigned to the surveillance supervisor
		assertEquals(pendingTask.getAssigneeUser().getUuid(), caseOfficer.getUuid());
		assertEquals(doneTask.getAssigneeUser().getUuid(), user.getUuid());

		// A previous hospitalization with the former facility should have been created
		List<PreviousHospitalizationDto> previousHospitalizations = caze.getHospitalization().getPreviousHospitalizations();
		assertEquals(previousHospitalizations.size(), 1);
	}

	@Test
	public void testDashboardCaseListCreation() {
		CaseFacade caseFacade = getBean(CaseFacadeEjbLocal.class);

		TestDataCreator creator = createTestDataCreator();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);

		List<DashboardCaseDto> dashboardCaseDtos = caseFacade.getNewCasesForDashboard(caze.getDistrict(), caze.getDisease(), DateHelper.subtractDays(new Date(),  1), DateHelper.addDays(new Date(), 1), user.getUuid());

		// List should have one entry
		assertEquals(1, dashboardCaseDtos.size());
	}

	@Test
	public void testMapCaseListCreation() {
		CaseFacade caseFacade = getBean(CaseFacadeEjbLocal.class);

		TestDataCreator creator = createTestDataCreator();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);

		List<MapCaseDto> mapCaseDtos = caseFacade.getCasesForMap(caze.getDistrict(), caze.getDisease(), DateHelper.subtractDays(new Date(),  1), DateHelper.addDays(new Date(), 1), user.getUuid());

		// List should have one entry
		assertEquals(1, mapCaseDtos.size());
	}
	
	@Test
	public void testGetIndexList() {
		CaseFacade caseFacade = getBean(CaseFacadeEjbLocal.class);

		TestDataCreator creator = createTestDataCreator();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);

		List<CaseIndexDto> results = caseFacade.getIndexList(user.getUuid(), null);

		// List should have one entry
		assertEquals(1, results.size());
	}

	@Test
	public void testCaseDeletion() {
		CaseFacade caseFacade = getBean(CaseFacadeEjbLocal.class);
		ContactFacade contactFacade = getBean(ContactFacadeEjbLocal.class);
		TaskFacade taskFacade = getBean(TaskFacadeEjb.class);
		SampleFacade sampleFacade = getBean(SampleFacadeEjb.class);

		TestDataCreator creator = createTestDataCreator();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		String userUuid = user.getUuid();
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());
		TaskDto task = creator.createTask(TaskContext.CASE, TaskType.CASE_INVESTIGATION, TaskStatus.PENDING, caze.toReference(), null, new Date(), user.toReference());
		creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		
		// Database should contain one case, associated contact, task and sample
		assertEquals(1, caseFacade.getAllCasesAfter(null, userUuid).size());
		assertEquals(1, contactFacade.getAllContactsAfter(null, userUuid).size());
		assertNotNull(taskFacade.getByUuid(task.getUuid()));
		assertEquals(1, sampleFacade.getAllAfter(null, userUuid).size());
		
		caseFacade.deleteCase(caze.toReference(), adminUuid);
		
		// Database should contain no case and associated contact, task or sample
		assertEquals(0, caseFacade.getAllCasesAfter(null, userUuid).size());
		assertEquals(0, contactFacade.getAllContactsAfter(null, userUuid).size());
		assertNull(taskFacade.getByUuid(task.getUuid()));
		assertEquals(0, sampleFacade.getAllAfter(null, userUuid).size());
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
