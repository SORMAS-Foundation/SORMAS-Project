package de.symeda.sormas.backend;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import info.novatec.beantest.api.BaseBeanTest;

public class TestDataCreator extends BaseBeanTest {
	
	private final UserFacade userFacade;
	private final PersonFacade personFacade;
	private final CaseFacade caseFacade;
	private final ContactFacade contactFacade;
	private final TaskFacade taskFacade;
	private final VisitFacade visitFacade;
	private final WeeklyReportFacade weeklyReportFacade;
	private final RegionFacade regionFacade;
	private final DistrictFacade districtFacade;
	private final CommunityFacade communityFacade;
	private final FacilityFacade facilityFacade;
	
	private final RegionService regionService;
	private final DistrictService districtService;
	private final CommunityService communityService;
	private final FacilityService facilityService;

	public TestDataCreator(UserFacade userFacade, PersonFacade personFacade, CaseFacade caseFacade,
			ContactFacade contactFacade, TaskFacade taskFacade, VisitFacade visitFacade, WeeklyReportFacade weeklyReportFacade,
			RegionFacade regionFacade, DistrictFacade districtFacade, CommunityFacade communityFacade, FacilityFacade facilityFacade,
			RegionService regionService, DistrictService districtService, CommunityService communityService,
			FacilityService facilityService) {
		this.userFacade = userFacade;
		this.personFacade = personFacade;
		this.caseFacade = caseFacade;
		this.contactFacade = contactFacade;
		this.taskFacade = taskFacade;
		this.visitFacade = visitFacade;
		this.weeklyReportFacade = weeklyReportFacade;
		this.regionFacade = regionFacade;
		this.districtFacade = districtFacade;
		this.communityFacade = communityFacade;
		this.facilityFacade = facilityFacade;
		this.regionService = regionService;
		this.districtService = districtService;
		this.communityService = communityService;
		this.facilityService = facilityService;
	}
	
	@Test
	public UserDto createUser(String regionUuid, String districtUuid, String facilityUuid, String firstName, String lastName, UserRole... roles) {
		UserDto user = new UserDto();
		user.setUuid(DataHelper.createUuid());
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserName(firstName + lastName);
		user.setUserRoles(new HashSet<UserRole>(Arrays.asList(roles)));
		user.setRegion(regionFacade.getRegionByUuid(regionUuid));
		user.setDistrict(districtFacade.getDistrictByUuid(districtUuid));
		user.setHealthFacility(facilityFacade.getByUuid(facilityUuid));
		user = userFacade.saveUser(user);
		
		return user;
	}

	@Test
	public PersonDto createPerson(String firstName, String lastName) {
		PersonDto cazePerson = new PersonDto();
		cazePerson.setUuid(DataHelper.createUuid());
		cazePerson.setFirstName(firstName);
		cazePerson.setLastName(lastName);
		cazePerson = personFacade.savePerson(cazePerson);
		
		return cazePerson;
	}

	@Test
	public CaseDataDto createCase(UserDto user, PersonDto cazePerson, Disease disease, CaseClassification caseClassification, 
			InvestigationStatus investigationStatus, Date reportDate, RDCF rdcf) {
		CaseDataDto caze = new CaseDataDto();
		caze.setUuid(DataHelper.createUuid());
		caze.setPerson(cazePerson);
		caze.setReportDate(reportDate);
		caze.setReportingUser(user);
		caze.setDisease(disease);
		caze.setCaseClassification(caseClassification);
		caze.setInvestigationStatus(investigationStatus);
		caze.setRegion(regionFacade.getRegionByUuid(rdcf.region.getUuid()));
		caze.setDistrict(districtFacade.getDistrictByUuid(rdcf.district.getUuid()));
		caze.setCommunity(communityFacade.getByUuid(rdcf.community.getUuid()));
		caze.setHealthFacility(facilityFacade.getByUuid(rdcf.facility.getUuid()));		
		
		caze = caseFacade.saveCase(caze);
		
		return caze;
	}

	@Test
	public ContactDto createContact(UserDto reportingUser, UserDto contactOfficer, PersonDto contactPerson,
			CaseDataDto caze, Date reportDateTime, Date lastContactDate) {
		ContactDto contact = new ContactDto();
		contact.setUuid(DataHelper.createUuid());
		contact.setReportingUser(reportingUser);
		contact.setContactOfficer(contactOfficer);
		contact.setPerson(contactPerson);
		contact.setCaze(caze);
		contact.setReportDateTime(reportDateTime);
		contact.setLastContactDate(lastContactDate);
		
		contact = contactFacade.saveContact(contact);
		
		return contact;
	}

	@Test
	public TaskDto createTask(TaskContext context, TaskType type, TaskStatus status, CaseDataDto caze,
			ContactDto contact, UserDto assigneeUser) {
		TaskDto task = new TaskDto();
		task.setUuid(DataHelper.createUuid());
		task.setTaskContext(context);
		task.setTaskType(type);
		task.setTaskStatus(status);
		if (caze != null) {
			task.setCaze(caze);
		}
		if (contact != null) {
			task.setContact(contact);
		}
		task.setAssigneeUser(assigneeUser);
		
		task = taskFacade.saveTask(task);
		
		return task;
	}
	
	@Test
	public VisitDto createVisit(Disease disease, PersonDto contactPerson, Date visitDateTime, VisitStatus visitStatus) {
		VisitDto visit = new VisitDto();
		visit.setUuid(DataHelper.createUuid());
		visit.setDisease(disease);
		visit.setPerson(contactPerson);
		visit.setVisitDateTime(visitDateTime);
		visit.setVisitStatus(visitStatus);
		
		visit = visitFacade.saveVisit(visit);
		
		return visit;
	}
	
	@Test
	public WeeklyReportDto createWeeklyReport(String facilityUuid, UserDto informant, Date reportDateTime, int epiWeek, int year, int numberOfCases) {
		WeeklyReportDto report = new WeeklyReportDto();
		report.setUuid(DataHelper.createUuid());
		report.setHealthFacility(facilityFacade.getByUuid(facilityUuid));
		report.setInformant(informant);
		report.setReportDateTime(reportDateTime);
		report.setEpiWeek(epiWeek);
		report.setYear(year);
		report.setTotalNumberOfCases(numberOfCases);
		
		report = weeklyReportFacade.saveWeeklyReport(report);
		
		return report;
	}
	
	@Test
	public RDCF createRDCF(String regionName, String districtName, String communityName, String facilityName) {
		Region region = createRegion(regionName);
		District district = createDistrict(districtName, region);
		Community community = createCommunity(communityName, district);
		Facility facility = createFacility(facilityName, region, district, community);

		return new RDCF(region, district, community, facility);
	}
	
	@Test 
	public Region createRegion(String regionName) {
		Region region = new Region();
		region.setUuid(DataHelper.createUuid());
		region.setName(regionName);
		regionService.persist(region);
		
		return region;
	}
	
	@Test
	public District createDistrict(String districtName, Region region) {
		District district = new District();
		district.setUuid(DataHelper.createUuid());
		district.setName(districtName);
		district.setRegion(region);
		districtService.persist(district);
		
		return district;
	}
	
	@Test
	public Community createCommunity(String communityName, District district) {
		Community community = new Community();
		community.setUuid(DataHelper.createUuid());
		community.setName(communityName);
		community.setDistrict(district);
		communityService.persist(community);
		
		return community;
	}
	
	@Test
	public Facility createFacility(String facilityName, Region region, District district, Community community) {
		Facility facility = new Facility();
		facility.setUuid(DataHelper.createUuid());
		facility.setName(facilityName);
		facility.setType(FacilityType.PRIMARY);
		facility.setCommunity(community);
		facility.setDistrict(district);
		facility.setRegion(region);
		facilityService.persist(facility);
		
		return facility;
	}
	
	public class RDCF {
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
