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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
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
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
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
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

public class TestDataCreator {

	private final AbstractBeanTest beanTest;

	public TestDataCreator(AbstractBeanTest beanTest) {
		this.beanTest = beanTest;
	}
	
	public UserDto createUser(RDCFEntities rdcf, UserRole... roles) {
		return createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "First", "Name", roles);
	}

	public UserDto createUser(String regionUuid, String districtUuid, String facilityUuid, String firstName,
			String lastName, UserRole... roles) {
		UserDto user = new UserDto();
		user.setUuid(DataHelper.createUuid());
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

	public PersonDto createPerson(String firstName, String lastName) {
		PersonDto cazePerson = new PersonDto();
		cazePerson.setUuid(DataHelper.createUuid());
		cazePerson.setFirstName(firstName);
		cazePerson.setLastName(lastName);
		cazePerson = beanTest.getPersonFacade().savePerson(cazePerson);

		return cazePerson;
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

	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto cazePerson, Disease disease,
			CaseClassification caseClassification, InvestigationStatus investigationStatus, Date reportAndOnsetDate,
			RDCFEntities rdcf) {
		return createCase(user, cazePerson, disease, caseClassification, investigationStatus, reportAndOnsetDate, new RDCF(rdcf));
	}
	
	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto cazePerson, Disease disease,
			CaseClassification caseClassification, InvestigationStatus investigationStatus, Date reportAndOnsetDate,
			RDCF rdcf) {
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

		caze = beanTest.getCaseFacade().saveCase(caze);

		return caze;
	}
	
	public ClinicalVisitDto createClinicalVisit(CaseDataDto caze) {
		ClinicalVisitDto clinicalVisit = ClinicalVisitDto.buildClinicalVisit(caze.getClinicalCourse().toReference(), SymptomsDto.build(), caze.getDisease());
		
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
		TreatmentDto treatment = TreatmentDto.buildTreatment(caze.getTherapy().toReference());
		treatment.setTreatmentType(TreatmentType.BLOOD_TRANSFUSION);
		
		treatment = beanTest.getTreatmentFacade().saveTreatment(treatment);
		
		return treatment;
	}

	public ContactDto createContact(UserReferenceDto reportingUser, UserReferenceDto contactOfficer,
			PersonReferenceDto contactPerson, CaseReferenceDto caze, Date reportDateTime, Date lastContactDate) {
		ContactDto contact = new ContactDto();
		contact.setUuid(DataHelper.createUuid());
		contact.setReportingUser(reportingUser);
		contact.setContactOfficer(contactOfficer);
		contact.setPerson(contactPerson);
		contact.setCaze(caze);
		contact.setReportDateTime(reportDateTime);
		contact.setLastContactDate(lastContactDate);

		contact = beanTest.getContactFacade().saveContact(contact);

		return contact;
	}

	public TaskDto createTask(TaskContext context, TaskType type, TaskStatus status, CaseReferenceDto caze,
			ContactReferenceDto contact, EventReferenceDto event, Date dueDate, UserReferenceDto assigneeUser) {
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
		if (event != null) {
			task.setEvent(event);
		}
		task.setDueDate(dueDate);
		task.setAssigneeUser(assigneeUser);

		task = beanTest.getTaskFacade().saveTask(task);

		return task;
	}

	public VisitDto createVisit(Disease disease, PersonReferenceDto contactPerson, Date visitDateTime,
			VisitStatus visitStatus) {
		VisitDto visit = new VisitDto();
		visit.setUuid(DataHelper.createUuid());
		visit.setDisease(disease);
		visit.setPerson(contactPerson);
		visit.setVisitDateTime(visitDateTime);
		visit.setVisitStatus(visitStatus);

		SymptomsDto symptoms = new SymptomsDto();
		symptoms.setUuid(DataHelper.createUuid());
		visit.setSymptoms(symptoms);

		visit = beanTest.getVisitFacade().saveVisit(visit);

		return visit;
	}

	public EventDto createEvent(EventStatus eventStatus, String eventDesc, String srcFirstName,
			String srcLastName, String srcTelNo, TypeOfPlace typeOfPlace, Date eventDate, Date reportDateTime,
			UserReferenceDto reportingUser, UserReferenceDto surveillanceOfficer, Disease disease,
			LocationDto eventLocation) {
		EventDto event = new EventDto();
		event.setUuid(DataHelper.createUuid());
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
		event.setEventLocation(eventLocation);

		event = beanTest.getEventFacade().saveEvent(event);

		return event;
	}

	public EventParticipantDto createEventParticipant(EventReferenceDto event, PersonDto eventPerson,
			String involvementDescription) {
		EventParticipantDto eventParticipant = new EventParticipantDto();
		eventParticipant.setEvent(event);
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
		SampleDto sample = new SampleDto();
		sample.setUuid(DataHelper.createUuid());
		sample.setAssociatedCase(associatedCase);
		sample.setSampleDateTime(sampleDateTime);
		sample.setReportDateTime(reportDateTime);
		sample.setReportingUser(reportingUser);
		sample.setSampleMaterial(sampleMaterial);
		sample.setLab(beanTest.getFacilityFacade().getFacilityReferenceByUuid(lab.getUuid()));

		sample = beanTest.getSampleFacade().saveSample(sample);

		return sample;
	}

	public PathogenTestDto createPathogenTest(SampleReferenceDto sample, PathogenTestType testType, Disease testedDisease,
			Date testDateTime, Facility lab, UserReferenceDto labUser, PathogenTestResultType testResult, String testResultText,
			boolean verified) {
		PathogenTestDto sampleTest = new PathogenTestDto();
		sampleTest.setUuid(DataHelper.createUuid());
		sampleTest.setSample(sample);
		sampleTest.setTestedDisease(testedDisease);
		sampleTest.setTestType(testType);
		sampleTest.setTestDateTime(testDateTime);
		sampleTest.setLab(lab != null ? beanTest.getFacilityFacade().getFacilityReferenceByUuid(lab.getUuid()) : null);
		sampleTest.setLabUser(labUser);
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
		Region region = createRegion(regionName);
		District district = createDistrict(districtName, region);
		Community community = createCommunity(communityName, district);
		Facility facility = createFacility(facilityName, region, district, community);

		return new RDCF(new RegionReferenceDto(region.getUuid(), region.getName()),
				new DistrictReferenceDto(district.getUuid(), district.getName()),
				new CommunityReferenceDto(community.getUuid(), community.getName()),
				new FacilityReferenceDto(facility.getUuid(), facility.getName()));
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
		Facility facility = new Facility();
		facility.setUuid(DataHelper.createUuid());
		facility.setName(facilityName);
		facility.setType(FacilityType.PRIMARY);
		facility.setCommunity(community);
		facility.setDistrict(district);
		facility.setRegion(region);
		beanTest.getFacilityService().persist(facility);

		return facility;
	}
	
	public FacilityDto createFacility(String facilityName, RegionReferenceDto region, DistrictReferenceDto district, CommunityReferenceDto community) {
		FacilityDto facility = FacilityDto.build();
		facility.setName(facilityName);
		facility.setType(FacilityType.PRIMARY);
		facility.setCommunity(community);
		facility.setDistrict(district);
		facility.setRegion(region);
		beanTest.getFacilityFacade().saveFacility(facility);
		return facility;
	}
	
	public PopulationDataDto createPopulationData(RegionReferenceDto region, DistrictReferenceDto district, Integer population, Date collectionDate) {
		PopulationDataDto populationData = PopulationDataDto.build(collectionDate);
		populationData.setRegion(region);
		populationData.setDistrict(district);
		populationData.setPopulation(population);
		beanTest.getPopulationDataFacade().savePopulationData(Arrays.asList(populationData));
		return populationData;
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

		public RDCF(RegionReferenceDto region, DistrictReferenceDto district, CommunityReferenceDto community, FacilityReferenceDto facility) {
			this.region = region;
			this.district = district;
			this.community = community;
			this.facility = facility;
		}
		
		public RDCF(RDCFEntities rdcfEntities) {
			this.region = new RegionReferenceDto(rdcfEntities.region.getUuid(), rdcfEntities.region.getName());
			this.district = new DistrictReferenceDto(rdcfEntities.district.getUuid(), rdcfEntities.district.getName());
			this.community = new CommunityReferenceDto(rdcfEntities.community.getUuid(), rdcfEntities.community.getName());
			this.facility = new FacilityReferenceDto(rdcfEntities.facility.getUuid(), rdcfEntities.facility.getName());
		}
	}
}
