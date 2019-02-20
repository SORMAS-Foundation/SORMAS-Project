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
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.SampleTestType;
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
	
	public UserDto createUser(RDCF rdcf, UserRole... roles) {
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
		RDCF rdcf = createRDCF("Region", "District", "Community", "Facility");
		UserDto user = createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv",
				"Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = createPerson("Case", "Person");
		return createCase(user.toReference(), cazePerson.toReference(), disease, CaseClassification.NOT_CLASSIFIED,
				InvestigationStatus.PENDING, new Date(), rdcf);
	}

	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto person, RDCF rdcf) {
		return createCase(user, person, Disease.EVD, CaseClassification.SUSPECT, InvestigationStatus.PENDING, new Date(), rdcf);
	}
	
	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto cazePerson, Disease disease,
			CaseClassification caseClassification, InvestigationStatus investigationStatus, Date reportDate,
			RDCF rdcf) {
		CaseDataDto caze = CaseDataDto.build(cazePerson, disease);
		caze.setReportDate(reportDate);
		caze.setReportingUser(user);
		caze.setCaseClassification(caseClassification);
		caze.setInvestigationStatus(investigationStatus);
		caze.setRegion(beanTest.getRegionFacade().getRegionReferenceByUuid(rdcf.region.getUuid()));
		caze.setDistrict(beanTest.getDistrictFacade().getDistrictReferenceByUuid(rdcf.district.getUuid()));
		caze.setCommunity(beanTest.getCommunityFacade().getCommunityReferenceByUuid(rdcf.community.getUuid()));
		caze.setHealthFacility(beanTest.getFacilityFacade().getFacilityReferenceByUuid(rdcf.facility.getUuid()));

		caze = beanTest.getCaseFacade().saveCase(caze);

		return caze;
	}
	
	public ClinicalVisitDto createClinicalVisit(CaseDataDto caze) {
		ClinicalVisitDto clinicalVisit = ClinicalVisitDto.buildClinicalVisit(caze.getClinicalCourse().toReference(), SymptomsDto.build(), caze.getDisease(), caze.getPerson());
		
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

	public EventDto createEvent(EventType eventType, EventStatus eventStatus, String eventDesc, String srcFirstName,
			String srcLastName, String srcTelNo, TypeOfPlace typeOfPlace, Date eventDate, Date reportDateTime,
			UserReferenceDto reportingUser, UserReferenceDto surveillanceOfficer, Disease disease,
			LocationDto eventLocation) {
		EventDto event = new EventDto();
		event.setUuid(DataHelper.createUuid());
		event.setEventType(eventType);
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

	public SampleTestDto createSampleTest(SampleReferenceDto sample, SampleTestType testType, Date testDateTime,
			Facility lab, UserReferenceDto labUser, SampleTestResultType testResult, String testResultText,
			boolean verified) {
		SampleTestDto sampleTest = new SampleTestDto();
		sampleTest.setUuid(DataHelper.createUuid());
		sampleTest.setSample(sample);
		sampleTest.setTestType(testType);
		sampleTest.setTestDateTime(testDateTime);
		sampleTest.setLab(lab != null ? beanTest.getFacilityFacade().getFacilityReferenceByUuid(lab.getUuid()) : null);
		sampleTest.setLabUser(labUser);
		sampleTest.setTestResult(testResult);
		sampleTest.setTestResultText(testResultText);
		sampleTest.setTestResultVerified(verified);

		sampleTest = beanTest.getSampleTestFacade().saveSampleTest(sampleTest);

		return sampleTest;
	}

	public SampleTestDto createSampleTest(CaseDataDto associatedCase, SampleTestType testType,
			SampleTestResultType resultType) {
		RDCF rdcf = createRDCF("Region", "District", "Community", "Facility");
		SampleDto sample = createSample(new CaseReferenceDto(associatedCase.getUuid()), new Date(), new Date(),
				associatedCase.getReportingUser(), SampleMaterial.BLOOD, rdcf.facility);
		return createSampleTest(new SampleReferenceDto(sample.getUuid()), testType, new Date(), rdcf.facility,
				associatedCase.getReportingUser(), resultType, "", true);
	}

	public RDCF createRDCF() {
		return createRDCF("Region", "District", "Community", "Facility");
	}
	
	public RDCF createRDCF(String regionName, String districtName, String communityName, String facilityName) {
		Region region = createRegion(regionName);
		District district = createDistrict(districtName, region);
		Community community = createCommunity(communityName, district);
		Facility facility = createFacility(facilityName, region, district, community);

		return new RDCF(region, district, community, facility);
	}

	public Region createRegion(String regionName) {
		Region region = new Region();
		region.setUuid(DataHelper.createUuid());
		region.setName(regionName);
		beanTest.getRegionService().persist(region);

		return region;
	}

	public District createDistrict(String districtName, Region region) {
		District district = new District();
		district.setUuid(DataHelper.createUuid());
		district.setName(districtName);
		district.setRegion(region);
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

	/**
	 * TODO use DTOs instead
	 */
	public static class RDCF {
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
