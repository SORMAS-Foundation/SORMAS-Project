/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.exposure.Exposure;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserRole;
import de.symeda.sormas.app.backend.visit.Visit;

public class TestEntityCreator {

	public static Person createPerson() {
		return createPerson("FirstName", "LastName");
	}

	public static Person createPerson(String firstName, String lastName) {
		return createPerson(firstName, lastName, null, null, null, null);
	}

	public static Person createPerson(String firstName, String lastName, Sex sex, Integer birthdateYYYY, Integer birthdateMM, Integer birthdateDD) {
		Person person = DatabaseHelper.getPersonDao().build();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setSex(sex);
		person.setBirthdateYYYY(birthdateYYYY);
		person.setBirthdateMM(birthdateMM);
		person.setBirthdateDD(birthdateDD);

		try {
			DatabaseHelper.getPersonDao().saveAndSnapshot(person);
			DatabaseHelper.getPersonDao().accept(person);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getPersonDao().queryForId(person.getId());
	}

	public static Case createCase(Person person) {
		Disease disease = Disease.EVD;
		Region region = DatabaseHelper.getRegionDao().queryUuid(TestHelper.REGION_UUID);
		District district = DatabaseHelper.getDistrictDao().queryUuid(TestHelper.DISTRICT_UUID);
		Community community = DatabaseHelper.getCommunityDao().queryUuid(TestHelper.COMMUNITY_UUID);
		Facility facility = DatabaseHelper.getFacilityDao().queryUuid(TestHelper.FACILITY_UUID);
		CaseClassification caseClassification = CaseClassification.SUSPECT;
		InvestigationStatus investigationStatus = InvestigationStatus.PENDING;

		Case caze = DatabaseHelper.getCaseDao().build(person);
		caze.setDisease(disease);
		caze.setResponsibleRegion(region);
		caze.setResponsibleDistrict(district);
		caze.setResponsibleCommunity(community);
		caze.setHealthFacility(facility);
		caze.setFacilityType(FacilityType.HOSPITAL);
		caze.setCaseClassification(caseClassification);
		caze.setInvestigationStatus(investigationStatus);
		caze.setReportDate(new Date());

		try {
			DatabaseHelper.getCaseDao().saveAndSnapshot(caze);
			DatabaseHelper.getCaseDao().accept(caze);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getCaseDao().queryForIdWithEmbedded(caze.getId());
	}

	public static Case createCase() {
		return createCase(createPerson());
	}

	public static Contact createContact(Person person, Case caze) {
		if (caze == null) {
			caze = createCase();
		}

		Contact contact = DatabaseHelper.getContactDao().build();
		contact.setPerson(person);
		contact.setCaseUuid(caze.getUuid());
		contact.setDisease(caze.getDisease());
		contact.setDiseaseDetails(caze.getDiseaseDetails());

		try {
			DatabaseHelper.getContactDao().saveAndSnapshot(contact);
			DatabaseHelper.getContactDao().accept(contact);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getContactDao().queryForIdWithEmbedded(contact.getId());
	}

	public static Contact createContact(Case caze) {
		return createContact(createPerson(), caze);
	}

	public static Event createEvent() {
		String eventTitle = "FirstEventTitle";
		String eventDesc = "FirstEventDescription";
		Date eventDate = DateHelper.subtractDays(new Date(), 2);
		Date eventInvestigationStartDate = DateHelper.subtractDays(new Date(), 1);
		Date eventInvestigationEndDate = new Date();
		TypeOfPlace typeOfPlace = TypeOfPlace.PUBLIC_PLACE;
		String srcFirstName = "Emil";
		String srcLastName = "Mpenza";
		String srcTelNo = "0150123123123";

		Event event = DatabaseHelper.getEventDao().build();
		event.setEventTitle(eventTitle);
		event.setEventInvestigationStartDate(eventInvestigationStartDate);
		event.setEventInvestigationEndDate(eventInvestigationEndDate);
		event.setEventDesc(eventDesc);
		event.setStartDate(eventDate);
		event.setTypeOfPlace(typeOfPlace);
		event.setSrcFirstName(srcFirstName);
		event.setSrcLastName(srcLastName);
		event.setSrcTelNo(srcTelNo);

		try {
			DatabaseHelper.getEventDao().saveAndSnapshot(event);
			DatabaseHelper.getEventDao().accept(event);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getEventDao().queryForIdWithEmbedded(event.getId());
	}

	public static Sample createSample(Case caze) {
		if (caze == null) {
			caze = createCase();
		}
		Date sampleDateTime = DateHelper.subtractDays(new Date(), 1);
		Facility lab = DatabaseHelper.getFacilityDao().queryForAll().get(0);
		SampleMaterial material = SampleMaterial.BLOOD;

		Sample sample = DatabaseHelper.getSampleDao().build(caze);
		sample.setSampleDateTime(sampleDateTime);
		sample.setLab(lab);
		sample.setSampleMaterial(material);

		try {
			DatabaseHelper.getSampleDao().saveAndSnapshot(sample);
			DatabaseHelper.getSampleDao().accept(sample);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getSampleDao().queryForIdWithEmbedded(sample.getId());
	}

	public static PreviousHospitalization addPreviousHospitalization(Case caze) {
		PreviousHospitalization prevHosp = DatabaseHelper.getPreviousHospitalizationDao().build();
		prevHosp.setHospitalization(caze.getHospitalization());
		caze.getHospitalization().getPreviousHospitalizations().add(prevHosp);
		return prevHosp;
	}

	public static Exposure createExposure(Case caze) {
		Exposure exposure = DatabaseHelper.getExposureDao().build();
		exposure.setEpiData(caze.getEpiData());

		try {
			DatabaseHelper.getExposureDao().saveAndSnapshot(exposure);
			DatabaseHelper.getExposureDao().accept(exposure);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getExposureDao().queryForIdWithEmbedded(exposure.getId());
	}

	public static Visit createVisit(Contact contact) throws DaoException {
		Visit visit = DatabaseHelper.getVisitDao().build(contact.getUuid());
		Symptoms symptoms = DatabaseHelper.getSymptomsDao().build();
		visit.setSymptoms(symptoms);
		visit.setVisitUser(ConfigProvider.getUser());

		DatabaseHelper.getVisitDao().saveAndSnapshot(visit);
		DatabaseHelper.getVisitDao().accept(visit);

		return DatabaseHelper.getVisitDao().queryForIdWithEmbedded(visit.getId());
	}

	public static EventParticipant createEventParticipant(Person person, Event event) {
		EventParticipant eventParticipant = DatabaseHelper.getEventParticipantDao().build();
		eventParticipant.setEvent(event);
		eventParticipant.setPerson(person);

		try {
			DatabaseHelper.getEventParticipantDao().saveAndSnapshot(eventParticipant);
			DatabaseHelper.getEventParticipantDao().accept(eventParticipant);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getEventParticipantDao().queryForIdWithEmbedded(eventParticipant.getId());
	}

	public static EventParticipant createEventParticipant(Event event) {
		return createEventParticipant(createPerson(), event);
	}

	public static PathogenTest createSampleTest(Sample sample) {
		PathogenTestType pathogenTestType = PathogenTestType.RAPID_TEST;
		PathogenTestResultType pathogenTestResultType = PathogenTestResultType.NEGATIVE;
		Date sampleTestDateTime = new Date();

		PathogenTest pathogenTest = DatabaseHelper.getSampleTestDao().build(sample);
		pathogenTest.setTestType(pathogenTestType);
		pathogenTest.setTestResult(pathogenTestResultType);
		pathogenTest.setTestDateTime(sampleTestDateTime);

		try {
			DatabaseHelper.getSampleTestDao().saveAndSnapshot(pathogenTest);
			DatabaseHelper.getSampleTestDao().accept(pathogenTest);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getSampleTestDao().queryForIdWithEmbedded(pathogenTest.getId());
	}

	public static Task createCaseTask(Case caze, TaskStatus taskStatus, User user) {
		TaskDto taskDto = new TaskDto();
		Task task = new TaskDtoHelper().fillOrCreateFromDto(null, taskDto);
		task.setUuid(DataHelper.createUuid());
		task.setCreationDate(new Date());
		task.setChangeDate(new Date());
		task.setTaskContext(TaskContext.CASE);
		task.setTaskType(TaskType.CASE_INVESTIGATION);
		task.setTaskStatus(taskStatus);
		task.setCaze(caze);
		task.setAssigneeUser(user);

		try {
			DatabaseHelper.getTaskDao().saveAndSnapshot(task);
			DatabaseHelper.getTaskDao().accept(task);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getTaskDao().queryForIdWithEmbedded(task.getId());
	}

	public static Task createEventTask(Event event, TaskStatus taskStatus, User user) {
		TaskDto taskDto = new TaskDto();
		Task task = new TaskDtoHelper().fillOrCreateFromDto(null, taskDto);
		task.setUuid(DataHelper.createUuid());
		task.setCreationDate(new Date());
		task.setChangeDate(new Date());
		task.setTaskContext(TaskContext.CASE);
		task.setTaskType(TaskType.CASE_INVESTIGATION);
		task.setTaskStatus(taskStatus);
		task.setEvent(event);
		task.setAssigneeUser(user);

		try {
			DatabaseHelper.getTaskDao().saveAndSnapshot(task);
			DatabaseHelper.getTaskDao().accept(task);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getTaskDao().queryForIdWithEmbedded(task.getId());
	}

	public static WeeklyReport createWeeklyReport(EpiWeek epiWeek) {
		WeeklyReport weeklyReport;

		try {
			weeklyReport = DatabaseHelper.getWeeklyReportDao().build(epiWeek);
			DatabaseHelper.getWeeklyReportDao().saveAndSnapshot(weeklyReport);
		} catch (DaoException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getWeeklyReportDao().queryForIdWithEmbedded(weeklyReport.getId());
	}

	public static Environment createEnvironment() {
		Environment environment;

		try {
			environment = DatabaseHelper.getEnvironmentDao().build();
			DatabaseHelper.getEnvironmentDao().saveAndSnapshot(environment);
			DatabaseHelper.getEnvironmentDao().accept(environment);
		} catch (DaoException e) {
			throw new RuntimeException();
		}

		return DatabaseHelper.getEnvironmentDao().queryForIdWithEmbedded(environment.getId());
	}

	public static EnvironmentSample createEnvironmentSample() {
		EnvironmentSample environmentSample;

		try {
			environmentSample = DatabaseHelper.getEnvironmentSampleDao().build(createEnvironment());
			DatabaseHelper.getEnvironmentSampleDao().saveAndSnapshot(environmentSample);
			DatabaseHelper.getEnvironmentSampleDao().accept(environmentSample);
		} catch (DaoException e) {
			throw new RuntimeException();
		}

		return DatabaseHelper.getEnvironmentSampleDao().queryForIdWithEmbedded(environmentSample.getId());
	}

	public static User createUser(String username, Region region, District district, UserRole userRole) {

		User user = DatabaseHelper.getUserDao().build();
		user.setRegion(region);
		user.setDistrict(district);
		user.setUserRoles(new HashSet<>(Collections.singletonList(userRole)));
		user.setActive(true);
		user.setUserName(username);
		user.setFirstName(username);
		user.setLastName(username);

		if (district == null) {
			user.setJurisdictionLevel(JurisdictionLevel.REGION);
		} else {
			user.setJurisdictionLevel(JurisdictionLevel.DISTRICT);
		}

		try {
			DatabaseHelper.getUserDao().create(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return DatabaseHelper.getUserDao().queryForId(user.getId());
	}
}
