package de.symeda.sormas.backend.caze;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.hospitalization.HospitalizationFacade;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.task.TaskFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.util.DateHelper8;
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

		// TODO create provider for facades (probably best to add a SormasBeanTest class)
		UserFacade userFacade = getBean(UserFacadeEjbLocal.class);
		PersonFacade personFacade = getBean(PersonFacadeEjb.class);
		CaseFacade caseFacade = getBean(CaseFacadeEjbLocal.class);
		ContactFacade contactFacade = getBean(ContactFacadeEjbLocal.class);

		RDCF rdcf = createRDCF("Region", "District", "Community", "Facility");
		UserDto user = createSurveillanceSupervisor(userFacade, rdcf.region.getUuid(), rdcf.district.getUuid());
		PersonDto cazePerson = createCasePerson(personFacade);
		CaseDataDto caze = createCase(caseFacade, user, cazePerson, rdcf);

		PersonDto contactPerson = new PersonDto();
		cazePerson.setUuid(DataHelper.createUuid());
		contactPerson.setFirstName("Steff");
		contactPerson.setLastName("Hansen");
		contactPerson = personFacade.savePerson(contactPerson);

		ContactDto contact = new ContactDto();
		contact.setUuid(DataHelper.createUuid());
		contact.setReportDateTime(new Date());
		contact.setReportingUser(user);
		contact.setContactOfficer(user);
		contact.setPerson(contactPerson);
		contact.setCaze(caze);
		contact.setLastContactDate(new Date());
		contact = contactFacade.saveContact(contact);

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
		
		// TODO create provider for facades (probably best to add a SormasBeanTest class)
		UserFacade userFacade = getBean(UserFacadeEjbLocal.class);
		CaseFacade caseFacade = getBean(CaseFacadeEjbLocal.class);
		PersonFacade personFacade = getBean(PersonFacadeEjb.class);
		TaskFacade taskFacade = getBean(TaskFacadeEjb.class);
		CommunityFacade communityFacade = getBean(CommunityFacadeEjb.class);
		FacilityFacade facilityFacade = getBean(FacilityFacadeEjb.class);

		RDCF rdcf = createRDCF("Region", "District", "Community", "Facility");
		UserDto survSupervisor = createSurveillanceSupervisor(userFacade, rdcf.region.getUuid(), rdcf.district.getUuid());
		PersonDto cazePerson = createCasePerson(personFacade);
		CaseDataDto caze = createCase(caseFacade, survSupervisor, cazePerson, rdcf);

		UserDto caseOfficer = new UserDto();
		caseOfficer.setUuid(DataHelper.createUuid());
		caseOfficer.setFirstName("Case");
		caseOfficer.setLastName("Officer");
		caseOfficer.setUserName("CaseOfficer");
		caseOfficer.setUserRoles(new HashSet<UserRole>(Arrays.asList(UserRole.CASE_OFFICER)));
		caseOfficer = userFacade.saveUser(caseOfficer);
		
		TaskDto pendingTask = new TaskDto();
		pendingTask.setUuid(DataHelper.createUuid());
		pendingTask.setTaskContext(TaskContext.CASE);
		pendingTask.setTaskType(TaskType.CASE_INVESTIGATION);
		pendingTask.setTaskStatus(TaskStatus.PENDING);
		pendingTask.setCaze(caze);
		pendingTask.setAssigneeUser(survSupervisor);
		pendingTask = taskFacade.saveTask(pendingTask);
		
		TaskDto doneTask = new TaskDto();
		doneTask.setUuid(DataHelper.createUuid());
		doneTask.setTaskContext(TaskContext.CASE);
		doneTask.setTaskType(TaskType.CASE_INVESTIGATION);
		doneTask.setTaskStatus(TaskStatus.DONE);
		doneTask.setCaze(caze);
		doneTask.setAssigneeUser(survSupervisor);
		doneTask = taskFacade.saveTask(doneTask);
		
		RDCF newRDCF = createRDCF("New Region", "New District", "New Community", "New Facility");
		caseFacade.moveCase(caze, communityFacade.getByUuid(newRDCF.community.getUuid()), facilityFacade.getByUuid(newRDCF.facility.getUuid()), caze.getHealthFacilityDetails(), caseOfficer);
		
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
		assertEquals(doneTask.getAssigneeUser().getUuid(), survSupervisor.getUuid());
		
		// A previous hospitalization with the former facility should have been created
		List<PreviousHospitalizationDto> previousHospitalizations = caze.getHospitalization().getPreviousHospitalizations();
		assertEquals(previousHospitalizations.size(), 1);
	}
	
	private UserDto createSurveillanceSupervisor(UserFacade userFacade, String regionUuid, String districtUuid) {
		RegionFacade regionFacade = getBean(RegionFacadeEjb.class);
		DistrictFacade districtFacade = getBean(DistrictFacadeEjb.class);
		
		// TODO handle user creation at a central place
		UserDto user = new UserDto();
		user.setUuid(DataHelper.createUuid());
		user.setFirstName("Admin");
		user.setLastName("Symeda");
		user.setUserName("AdminSymeda");
		user.setUserRoles(new HashSet<UserRole>(Arrays.asList(UserRole.SURVEILLANCE_SUPERVISOR)));
		user.setRegion(regionFacade.getRegionByUuid(regionUuid));
		user.setDistrict(districtFacade.getDistrictByUuid(districtUuid));
		user = userFacade.saveUser(user);
		
		return user;
	}
	
	private PersonDto createCasePerson(PersonFacade personFacade) {
		// TODO add create method to PersonFacade
		PersonDto cazePerson = new PersonDto();
		cazePerson.setUuid(DataHelper.createUuid());
		cazePerson.setFirstName("Tim");
		cazePerson.setLastName("Kunsen");
		cazePerson = personFacade.savePerson(cazePerson);
		
		return cazePerson;
	}
	
	private CaseDataDto createCase(CaseFacade caseFacade, UserDto user, PersonDto cazePerson, RDCF rdcf) {
		RegionFacade regionFacade = getBean(RegionFacadeEjb.class);
		DistrictFacade districtFacade = getBean(DistrictFacadeEjb.class);
		CommunityFacade communityFacade = getBean(CommunityFacadeEjb.class);
		FacilityFacade facilityFacade = getBean(FacilityFacadeEjb.class);
		
		// TODO add create method to CaseFacade that takes a person
		CaseDataDto caze = new CaseDataDto();
		caze.setPerson(cazePerson);
		caze.setReportDate(new Date());
		caze.setReportingUser(user);
		caze.setDisease(Disease.EVD);
		caze.setCaseClassification(CaseClassification.PROBABLE);
		caze.setInvestigationStatus(InvestigationStatus.PENDING);
		caze.setRegion(regionFacade.getRegionByUuid(rdcf.region.getUuid()));
		caze.setDistrict(districtFacade.getDistrictByUuid(rdcf.district.getUuid()));
		caze.setCommunity(communityFacade.getByUuid(rdcf.community.getUuid()));
		caze.setHealthFacility(facilityFacade.getByUuid(rdcf.facility.getUuid()));		
		
		caze = caseFacade.saveCase(caze);
		
		return caze;
	}
	
	private RDCF createRDCF(String regionName, String districtName, String communityName, String facilityName) {
		RegionService regionService = getBean(RegionService.class);
		DistrictService districtService = getBean(DistrictService.class);
		CommunityService communityService = getBean(CommunityService.class);
		FacilityService facilityService = getBean(FacilityService.class);
		
		Region region = new Region();
		region.setUuid(DataHelper.createUuid());
		region.setName(regionName);
		regionService.persist(region);
		
		District district = new District();
		district.setUuid(DataHelper.createUuid());
		district.setName(districtName);
		district.setRegion(region);
		districtService.persist(district);
		
		Community community = new Community();
		community.setUuid(DataHelper.createUuid());
		community.setName("New Community");
		community.setDistrict(district);
		communityService.persist(community);
		
		Facility facility = new Facility();
		facility.setUuid(DataHelper.createUuid());
		facility.setName("New Facility");
		facility.setType(FacilityType.PRIMARY);
		facility.setCommunity(community);
		facility.setDistrict(district);
		facility.setRegion(region);
		facilityService.persist(facility);
		
		return new RDCF(region, district, community, facility);
	}
	
	private class RDCF {
		public Region region;
		public District district;
		public Community community;
		public Facility facility;
		
		public RDCF(Region region, District district, Community community, Facility facility) {
			this.region = region;
			this.district = district;
			this.community = community;
			this.facility = facility;
		}
	}
	

}
