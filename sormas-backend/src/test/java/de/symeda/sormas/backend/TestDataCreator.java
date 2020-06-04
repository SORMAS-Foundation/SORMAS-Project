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
package de.symeda.sormas.backend;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryType;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.function.Consumer;

public class TestDataCreator {

	private final AbstractBeanTest beanTest;

	public TestDataCreator(AbstractBeanTest beanTest) {
		this.beanTest = beanTest;
	}
	
	public UserDto createUser(RDCFEntities rdcf, UserRole... roles) {
		return createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "First", "Name", roles);
	}

	public UserDto createUser(RDCF rdcf, UserRole... roles) {
		return createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "First", "Name", roles);
	}
	
	public UserDto createUser(RDCFEntities rdcf, String firstName, String lastName, UserRole... roles) {
		return createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), firstName, lastName, roles);
	}

	public UserDto createUser(String regionUuid, String districtUuid, String facilityUuid, String firstName,
			String lastName, UserRole... roles) {
		UserDto user = UserDto.build();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserName(firstName + lastName);
		user.setUserRoles(new HashSet<UserRole>(Arrays.asList(roles)));
		user.setRegion(beanTest.getRegionFacade().getRegionReferenceByUuid(regionUuid));
		user.setDistrict(beanTest.getDistrictFacade().getDistrictReferenceByUuid(districtUuid));
		user.setHealthFacility(beanTest.getFacilityFacade().getFacilityReferenceByUuid(facilityUuid));
		user = beanTest.getUserFacade().saveUser(user);

		return user;
	}
	
	public PersonDto createPerson() {
		return createPerson("FirstName", "LastName");
	}
	
	public PersonDto createPerson(String firstName, String lastName) {
		PersonDto cazePerson = PersonDto.build();
		cazePerson.setFirstName(firstName);
		cazePerson.setLastName(lastName);
		cazePerson = beanTest.getPersonFacade().savePerson(cazePerson);

		return cazePerson;
	}

	public PersonDto createPerson(String firstName, String lastName, Sex sex, Integer birthdateYYYY, Integer birthdateMM, Integer birthdateDD) {
		return createPerson(firstName, lastName, sex, birthdateYYYY, birthdateMM, birthdateDD, null);
	}

	public PersonDto createPerson(String firstName, String lastName, Sex sex, Integer birthdateYYYY, Integer birthdateMM, Integer birthdateDD, LocationDto address) {
		PersonDto person = PersonDto.build();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setSex(sex);
		person.setBirthdateYYYY(birthdateYYYY);
		person.setBirthdateMM(birthdateMM);
		person.setBirthdateDD(birthdateDD);

		if(address != null){
			person.setAddress(address);
		}

		person = beanTest.getPersonFacade().savePerson(person);
		
		return person;
	}

	public CaseDataDto createUnclassifiedCase(Disease disease) {
		RDCFEntities rdcf = createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv",
				"Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = createPerson("Case", "Person");
		return createCase(user.toReference(), cazePerson.toReference(), disease, CaseClassification.NOT_CLASSIFIED,
				InvestigationStatus.PENDING, new Date(), rdcf);
	}

	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto person, RDCFEntities rdcf) {
		return createCase(user, person, Disease.EVD, CaseClassification.SUSPECT, InvestigationStatus.PENDING, new Date(), rdcf);
	}

	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto person, RDCF rdcf) {
		return createCase(user, person, Disease.EVD, CaseClassification.SUSPECT, InvestigationStatus.PENDING, new Date(), rdcf);
	}

	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto cazePerson, Disease disease,
			CaseClassification caseClassification, InvestigationStatus investigationStatus, Date reportAndOnsetDate,
			RDCFEntities rdcf) {
		return createCase(user, cazePerson, disease, caseClassification, investigationStatus, reportAndOnsetDate, new RDCF(rdcf));
	}

	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto cazePerson, Disease disease,
			CaseClassification caseClassification, InvestigationStatus investigationStatus, Date reportAndOnsetDate,
			RDCFEntities rdcf, String healthFacilityDetails) {
		final CaseDataDto aCase = createCase(user, cazePerson, disease, caseClassification, investigationStatus, reportAndOnsetDate, new RDCF(rdcf));
		aCase.setHealthFacilityDetails(healthFacilityDetails);
		return beanTest.getCaseFacade().saveCase(aCase);
	}
	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto cazePerson, Disease disease,
								  CaseClassification caseClassification, InvestigationStatus investigationStatus, Date reportAndOnsetDate,
								  RDCF rdcf) {
		return createCase(user, cazePerson, disease, caseClassification, investigationStatus, reportAndOnsetDate, rdcf, null);
	}

	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto cazePerson, Disease disease,
								  CaseClassification caseClassification, InvestigationStatus investigationStatus, Date reportAndOnsetDate,
								  RDCF rdcf, Consumer<CaseDataDto> setCustomFields) {
		CaseDataDto caze = CaseDataDto.build(cazePerson, disease);
		caze.setReportDate(reportAndOnsetDate);
		caze.setReportingUser(user);
		caze.getSymptoms().setOnsetDate(reportAndOnsetDate);
		caze.setCaseClassification(caseClassification);
		caze.setInvestigationStatus(investigationStatus);
		caze.setRegion(rdcf.region);
		caze.setDistrict(rdcf.district);
		caze.setCommunity(rdcf.community);
		caze.setHealthFacility(rdcf.facility);
		caze.setPointOfEntry(rdcf.pointOfEntry);

		if(setCustomFields != null){
			setCustomFields.accept(caze);
		}

		caze = beanTest.getCaseFacade().saveCase(caze);

		return caze;
	}
	
	public ClinicalVisitDto createClinicalVisit(CaseDataDto caze) {
		ClinicalVisitDto clinicalVisit = ClinicalVisitDto.build(caze.getClinicalCourse().toReference(), caze.getDisease());
		
		clinicalVisit = beanTest.getClinicalVisitFacade().saveClinicalVisit(clinicalVisit, caze.getUuid());
	
		return clinicalVisit;
	}
	
	public PrescriptionDto createPrescription(CaseDataDto caze) {
		PrescriptionDto prescription = PrescriptionDto.buildPrescription(caze.getTherapy().toReference());
		prescription.setPrescriptionType(TreatmentType.BLOOD_TRANSFUSION);
		
		prescription = beanTest.getPrescriptionFacade().savePrescription(prescription);
		
		return prescription;
	}
	
	public TreatmentDto createTreatment(CaseDataDto caze) {
		TreatmentDto treatment = TreatmentDto.build(caze.getTherapy().toReference());
		treatment.setTreatmentType(TreatmentType.BLOOD_TRANSFUSION);
		
		treatment = beanTest.getTreatmentFacade().saveTreatment(treatment);
		
		return treatment;
	}
	
	public ContactDto createContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson) {
		return createContact(reportingUser, null, contactPerson, null, new Date(), null, null, null);
	}
	
	public ContactDto createContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, Disease disease) {
		return createContact(reportingUser, null, contactPerson, null, new Date(), null, disease, null);
	}
	
	public ContactDto createContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, Date reportDateTime) {
		return createContact(reportingUser, null, contactPerson, null, reportDateTime, null, null, null);
	}

	public ContactDto createContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, CaseDataDto caze) {
		return createContact(reportingUser, null, contactPerson, caze, new Date(), null, null, null);
	}

	public ContactDto createContact(UserReferenceDto reportingUser, UserReferenceDto contactOfficer,
									PersonReferenceDto contactPerson, CaseDataDto caze, Date reportDateTime, Date lastContactDate, Disease disease) {
		return createContact(reportingUser, contactOfficer, contactPerson, caze, reportDateTime, lastContactDate, disease, null);
	}

	public ContactDto createContact(UserReferenceDto reportingUser, UserReferenceDto contactOfficer,
			PersonReferenceDto contactPerson, CaseDataDto caze, Date reportDateTime, Date lastContactDate, Disease disease, RDCF rdcf) {
		ContactDto contact;
		
		if (caze != null) {
			contact = ContactDto.build(caze);
		} else {
			contact = ContactDto.build(null, disease != null ? disease : Disease.EVD, null);
			if(rdcf == null) {
				rdcf = createRDCF();
			}
			contact.setRegion(rdcf.region);
			contact.setDistrict(rdcf.district);
		}
		contact.setReportingUser(reportingUser);
		contact.setContactOfficer(contactOfficer);
		contact.setPerson(contactPerson);
		contact.setReportDateTime(reportDateTime);
		contact.setLastContactDate(lastContactDate);

		contact = beanTest.getContactFacade().saveContact(contact);

		return contact;
	}

	public TaskDto createTask(TaskContext context, TaskType type, TaskStatus status, CaseReferenceDto caze,
			ContactReferenceDto contact, EventReferenceDto event, Date dueDate, UserReferenceDto assigneeUser) {
		
		ReferenceDto entityRef;
		switch (context) {
		case CASE:
			entityRef = caze;
			break;
		case CONTACT:
			entityRef = contact;
			break;
		case EVENT:
			entityRef = event;
			break;
		case GENERAL:
			entityRef = null;
			break;
		default:
			throw new IllegalArgumentException(context.toString());
		}
		
		TaskDto task = TaskDto.build(context, entityRef);
		task.setTaskType(type);
		task.setTaskStatus(status);
		task.setDueDate(dueDate);
		task.setAssigneeUser(assigneeUser);

		task = beanTest.getTaskFacade().saveTask(task);

		return task;
	}
	
	public VisitDto createVisit(PersonReferenceDto person) {
		return createVisit(Disease.EVD, person);
	}
	
	public VisitDto createVisit(Disease disease, PersonReferenceDto person) {
		return createVisit(disease, person, new Date(), VisitStatus.COOPERATIVE);
	}
	
	public VisitDto createVisit(Disease disease, PersonReferenceDto person, Date visitDateTime) {
		return createVisit(disease, person, visitDateTime, VisitStatus.COOPERATIVE);
	}

	public VisitDto createVisit(Disease disease, PersonReferenceDto person, Date visitDateTime,
			VisitStatus visitStatus) {
		VisitDto visit = VisitDto.build(person, disease);
		visit.setVisitDateTime(visitDateTime);
		visit.setVisitStatus(visitStatus);
		visit = beanTest.getVisitFacade().saveVisit(visit);

		return visit;
	}
	
	public EventDto createEvent(UserReferenceDto reportingUser) {
		return createEvent(EventStatus.POSSIBLE, "Description", "FirstName", "LastName", null, null, new Date(), new Date(), reportingUser, null, null, null);
	}

	public EventDto createEvent(EventStatus eventStatus, String eventDesc, String srcFirstName,
			String srcLastName, String srcTelNo, TypeOfPlace typeOfPlace, Date eventDate, Date reportDateTime,
			UserReferenceDto reportingUser, UserReferenceDto surveillanceOfficer, Disease disease, DistrictReferenceDto district) {
		EventDto event = EventDto.build();
		event.setEventStatus(eventStatus);
		event.setEventDesc(eventDesc);
		event.setSrcFirstName(srcFirstName);
		event.setSrcLastName(srcLastName);
		event.setSrcTelNo(srcTelNo);
		event.setTypeOfPlace(typeOfPlace);
		event.setEventDate(eventDate);
		event.setReportDateTime(reportDateTime);
		event.setReportingUser(reportingUser);
		event.setSurveillanceOfficer(surveillanceOfficer);
		event.setDisease(disease);
		event.getEventLocation().setDistrict(district);

		event = beanTest.getEventFacade().saveEvent(event);

		return event;
	}

	public EventParticipantDto createEventParticipant(EventReferenceDto event, PersonDto eventPerson) {
		return createEventParticipant(event, eventPerson, "Description");
	}
	
	public EventParticipantDto createEventParticipant(EventReferenceDto event, PersonDto eventPerson,
			String involvementDescription) {
		EventParticipantDto eventParticipant = EventParticipantDto.build(event);
		eventParticipant.setPerson(eventPerson);
		eventParticipant.setInvolvementDescription(involvementDescription);

		eventParticipant = beanTest.getEventParticipantFacade().saveEventParticipant(eventParticipant);

		return eventParticipant;
	}
	
	public SampleDto createSample(CaseReferenceDto associatedCase, UserReferenceDto reportingUser, Facility lab) {
		return createSample(associatedCase, new Date(), new Date(), reportingUser, SampleMaterial.BLOOD, lab);
	}
	
	public SampleDto createSample(CaseReferenceDto associatedCase, Date sampleDateTime, Date reportDateTime,
			UserReferenceDto reportingUser, SampleMaterial sampleMaterial, Facility lab) {
		SampleDto sample = SampleDto.build(reportingUser, associatedCase);
		sample.setSampleDateTime(sampleDateTime);
		sample.setReportDateTime(reportDateTime);
		sample.setSampleMaterial(sampleMaterial);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(beanTest.getFacilityFacade().getFacilityReferenceByUuid(lab.getUuid()));

		sample = beanTest.getSampleFacade().saveSample(sample);

		return sample;
	}

	public SampleDto createSample(ContactReferenceDto associatedContact, Date sampleDateTime, Date reportDateTime,
			UserReferenceDto reportingUser, SampleMaterial sampleMaterial, Facility lab) {
		SampleDto sample = SampleDto.build(reportingUser, associatedContact);
		sample.setSampleDateTime(sampleDateTime);
		sample.setReportDateTime(reportDateTime);
		sample.setSampleMaterial(sampleMaterial);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(beanTest.getFacilityFacade().getFacilityReferenceByUuid(lab.getUuid()));

		sample = beanTest.getSampleFacade().saveSample(sample);

		return sample;
	}

	public PathogenTestDto createPathogenTest(SampleReferenceDto sample, PathogenTestType testType, Disease testedDisease,
			Date testDateTime, Facility lab, UserReferenceDto labUser, PathogenTestResultType testResult, String testResultText,
			boolean verified) {
		PathogenTestDto sampleTest = PathogenTestDto.build(sample, labUser);
		sampleTest.setTestedDisease(testedDisease);
		sampleTest.setTestType(testType);
		sampleTest.setTestDateTime(testDateTime);
		sampleTest.setLab(lab != null ? beanTest.getFacilityFacade().getFacilityReferenceByUuid(lab.getUuid()) : null);
		sampleTest.setTestResult(testResult);
		sampleTest.setTestResultText(testResultText);
		sampleTest.setTestResultVerified(verified);

		sampleTest = beanTest.getSampleTestFacade().savePathogenTest(sampleTest);

		return sampleTest;
	}
	
	public PathogenTestDto createPathogenTest(CaseDataDto associatedCase,
			PathogenTestType testType, PathogenTestResultType resultType) {
		return createPathogenTest(associatedCase, null, testType, resultType);
	}
	
	public PathogenTestDto createPathogenTest(SampleReferenceDto sample, CaseDataDto associatedCase) {
		RDCFEntities rdcf = createRDCFEntities("LabRegion", "LabDistrict", "LabCommunity", "LabFacilty");
		return createPathogenTest(sample, PathogenTestType.ANTIGEN_DETECTION, associatedCase.getDisease(), new Date(), rdcf.facility,
				associatedCase.getReportingUser(), PathogenTestResultType.PENDING, "", false);
	}
	
	public PathogenTestDto createPathogenTest(CaseDataDto associatedCase, Disease testedDisease,
			PathogenTestType testType, PathogenTestResultType resultType) {
		RDCFEntities rdcf = createRDCFEntities("Region", "District", "Community", "Facility");
		SampleDto sample = createSample(new CaseReferenceDto(associatedCase.getUuid()), new Date(), new Date(),
				associatedCase.getReportingUser(), SampleMaterial.BLOOD, rdcf.facility);
		return createPathogenTest(new SampleReferenceDto(sample.getUuid()), testType, testedDisease, new Date(), rdcf.facility,
				associatedCase.getReportingUser(), resultType, "", true);
	}

	public PathogenTestDto buildPathogenTestDto(RDCFEntities rdcf, UserDto user, SampleDto sample, Disease disease,
												Date testDateTime) {
		final PathogenTestDto newPathogenTest = new PathogenTestDto();

		newPathogenTest.setSample(sample.toReference());
		newPathogenTest.setTestedDisease(disease);
		newPathogenTest.setTestType(PathogenTestType.ISOLATION);

		newPathogenTest.setTestDateTime(testDateTime);
		newPathogenTest.setLab(new FacilityReferenceDto(rdcf.facility.getUuid()));
		newPathogenTest.setLabUser(user.toReference());
		newPathogenTest.setTestResult(PathogenTestResultType.PENDING);
		newPathogenTest.setTestResultText("all bad!");
		newPathogenTest.setTestResultVerified(false);
		return newPathogenTest;
	}
	
	public AdditionalTestDto createAdditionalTest(SampleReferenceDto sample) {
		AdditionalTestDto test = AdditionalTestDto.build(sample);
		test.setTestDateTime(new Date());
		
		test = beanTest.getAdditionalTestFacade().saveAdditionalTest(test);
		
		return test;
	}

	public RDCF createRDCF() {
		return createRDCF("Region", "District", "Community", "Facility");
	}

	public RDCF createRDCF(String regionName, String districtName, String communityName, String facilityName) {
		return createRDCF(regionName, districtName, communityName, facilityName, null);
	}

	public RDCF createRDCF(String regionName, String districtName, String communityName, String facilityName, String pointOfEntryName) {
		Region region = createRegion(regionName);
		District district = createDistrict(districtName, region);
		Community community = createCommunity(communityName, district);
		Facility facility = createFacility(facilityName, region, district, community);

		PointOfEntry pointOfEntry = null;
		if(pointOfEntryName != null){
			pointOfEntry = createPointOfEntry(pointOfEntryName, region, district);
		}

		return new RDCF(new RegionReferenceDto(region.getUuid(), region.getName()),
				new DistrictReferenceDto(district.getUuid(), district.getName()),
				new CommunityReferenceDto(community.getUuid(), community.getName()),
				new FacilityReferenceDto(facility.getUuid(), facility.getName()),
				pointOfEntry != null ? new PointOfEntryReferenceDto(pointOfEntry.getUuid(), pointOfEntry.getName()) : null);
	}

	public RDCFEntities createRDCFEntities() {
		return createRDCFEntities("Region", "District", "Community", "Facility");
	}
	
	public RDCFEntities createRDCFEntities(String regionName, String districtName, String communityName, String facilityName) {
		Region region = createRegion(regionName);
		District district = createDistrict(districtName, region);
		Community community = createCommunity(communityName, district);
		Facility facility = createFacility(facilityName, region, district, community);

		return new RDCFEntities(region, district, community, facility);
	}

	public Region createRegion(String regionName) {
		Region region = new Region();
		region.setUuid(DataHelper.createUuid());
		region.setName(regionName);
		region.setEpidCode("COU-REG");
		beanTest.getRegionService().persist(region);

		return region;
	}

	public District createDistrict(String districtName, Region region) {
		District district = new District();
		district.setUuid(DataHelper.createUuid());
		district.setName(districtName);
		district.setRegion(region);
		district.setEpidCode("DIS");
		beanTest.getDistrictService().persist(district);

		return district;
	}

	public Community createCommunity(String communityName, District district) {
		Community community = new Community();
		community.setUuid(DataHelper.createUuid());
		community.setName(communityName);
		community.setDistrict(district);
		beanTest.getCommunityService().persist(community);

		return community;
	}

	public CommunityDto createCommunity(String communityName, DistrictReferenceDto district) {
		CommunityDto community = CommunityDto.build();
		community.setName(communityName);
		community.setDistrict(district);
		beanTest.getCommunityFacade().saveCommunity(community);
		return community;
	}

	public Facility createFacility(String facilityName, Region region, District district, Community community) {
		return createFacility(facilityName, null, region, district, community);
	}

	public Facility createFacility(String facilityName, FacilityType type, Region region, District district, Community community) {
		Facility facility = new Facility();
		facility.setUuid(DataHelper.createUuid());
		facility.setName(facilityName);
		facility.setCommunity(community);
		facility.setDistrict(district);
		facility.setRegion(region);
		facility.setType(type);
		beanTest.getFacilityService().persist(facility);

		return facility;
	}
	
	public FacilityDto createFacility(String facilityName, RegionReferenceDto region, DistrictReferenceDto district, CommunityReferenceDto community) {
		FacilityDto facility = 	FacilityDto.build();
		facility.setName(facilityName);
		facility.setCommunity(community);
		facility.setDistrict(district);
		facility.setRegion(region);
		beanTest.getFacilityFacade().saveFacility(facility);
		return facility;
	}
	
	public PointOfEntry createPointOfEntry(String pointOfEntryName, Region region, District district) {
		PointOfEntry pointOfEntry = new PointOfEntry();
		pointOfEntry.setUuid(DataHelper.createUuid());
		pointOfEntry.setPointOfEntryType(PointOfEntryType.AIRPORT);
		pointOfEntry.setName(pointOfEntryName);
		pointOfEntry.setDistrict(district);
		pointOfEntry.setRegion(region);
		beanTest.getPointOfEntryService().persist(pointOfEntry);

		return pointOfEntry;
	}

	public PointOfEntryDto createPointOfEntry(String pointOfEntryName, RegionReferenceDto region, DistrictReferenceDto district) {
		PointOfEntryDto pointOfEntry = PointOfEntryDto.build();
		pointOfEntry.setUuid(DataHelper.createUuid());
		pointOfEntry.setPointOfEntryType(PointOfEntryType.AIRPORT);
		pointOfEntry.setName(pointOfEntryName);
		pointOfEntry.setDistrict(district);
		pointOfEntry.setRegion(region);
		beanTest.getPointOfEntryFacade().save(pointOfEntry);

		return pointOfEntry;
	}

	public PopulationDataDto createPopulationData(RegionReferenceDto region, DistrictReferenceDto district, Integer population, Date collectionDate) {
		PopulationDataDto populationData = PopulationDataDto.build(collectionDate);
		populationData.setRegion(region);
		populationData.setDistrict(district);
		populationData.setPopulation(population);
		beanTest.getPopulationDataFacade().savePopulationData(Arrays.asList(populationData));
		return populationData;
	}
	
	public void updateDiseaseConfiguration(Disease disease, Boolean active, Boolean primary, Boolean caseBased) {
		DiseaseConfigurationDto config = DiseaseConfigurationFacadeEjbLocal.toDto(beanTest.getDiseaseConfigurationService().getDiseaseConfiguration(disease));
		config.setActive(active);
		config.setPrimaryDisease(primary);
		config.setCaseBased(caseBased);
		beanTest.getDiseaseConfigurationFacade().saveDiseaseConfiguration(config);
	}

	/**
	 * @deprecated Use RDCF instead
	 * @author MartinWahnschaffe
	 *
	 */
	@Deprecated
	public static class RDCFEntities {
		public Region region;
		public District district;
		public Community community;
		public Facility facility;

		public RDCFEntities(Region region, District district, Community community, Facility facility) {
			this.region = region;
			this.district = district;
			this.community = community;
			this.facility = facility;
		}
	}
	
	public static class RDCF {
		public RegionReferenceDto region;
		public DistrictReferenceDto district;
		public CommunityReferenceDto community;
		public FacilityReferenceDto facility;
		public PointOfEntryReferenceDto pointOfEntry;

		public RDCF(RegionReferenceDto region, DistrictReferenceDto district, CommunityReferenceDto community, FacilityReferenceDto facility) {
			this(region, district, community, facility, null);
		}

		public RDCF(RegionReferenceDto region, DistrictReferenceDto district, CommunityReferenceDto community, FacilityReferenceDto facility, PointOfEntryReferenceDto pointOfEntry) {
			this.region = region;
			this.district = district;
			this.community = community;
			this.facility = facility;
			this.pointOfEntry = pointOfEntry;
		}

		public RDCF(RDCFEntities rdcfEntities) {
			this.region = new RegionReferenceDto(rdcfEntities.region.getUuid(), rdcfEntities.region.getName());
			this.district = new DistrictReferenceDto(rdcfEntities.district.getUuid(), rdcfEntities.district.getName());
			this.community = new CommunityReferenceDto(rdcfEntities.community.getUuid(), rdcfEntities.community.getName());
			this.facility = new FacilityReferenceDto(rdcfEntities.facility.getUuid(), rdcfEntities.facility.getName());
		}
	}
}
