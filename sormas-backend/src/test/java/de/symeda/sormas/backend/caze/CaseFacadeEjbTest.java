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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.caze;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.MatcherAssert;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.spi.QueryImplementor;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CasePersonDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.OutdatedEntityException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.util.DtoHelper;

public class CaseFacadeEjbTest extends AbstractBeanTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testGetCasesForDuplicateMerging() {

		final Date today = new Date();
		final Date threeDaysAgo = DateUtils.addDays(today, -3);

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person", Sex.MALE, 1980, 1, 1);
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			today,
			rdcf);

		SessionImpl em = (SessionImpl) getEntityManager();
		QueryImplementor query = em.createQuery("select c from cases c where c.uuid=:uuid");
		query.setParameter("uuid", caze.getUuid());
		Case singleResult = (Case) query.getSingleResult();

		singleResult.setCreationDate(new Timestamp(threeDaysAgo.getTime()));
		singleResult.setReportDate(threeDaysAgo);
		em.save(singleResult);

		PersonDto cazePerson2 = creator.createPerson("Case", "Person", Sex.MALE, 1980, 1, 1);
		CaseDataDto case2 = creator.createCase(
			user.toReference(),
			cazePerson2.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			DateUtils.addMinutes(today, -3),
			rdcf);

		final List<CaseIndexDto[]> casesForDuplicateMergingToday =
			getCaseFacade().getCasesForDuplicateMerging(new CaseCriteria().creationDateFrom(today).creationDateTo(today), true);
		final List<CaseIndexDto[]> casesForDuplicateMergingThreeDaysAgo =
			getCaseFacade().getCasesForDuplicateMerging(new CaseCriteria().creationDateFrom(threeDaysAgo).creationDateTo(threeDaysAgo), true);
		Assert.assertEquals(1, casesForDuplicateMergingToday.size());
		Assert.assertEquals(1, casesForDuplicateMergingThreeDaysAgo.size());
	}

	@Test
	public void testGetDuplicateCasesOfSameSexAndDifferentBirthDateIsEmpty() {

		final Date today = new Date();

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person", Sex.MALE, 1980, 1, 1);
		creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			today,
			rdcf);

		PersonDto cazePerson2 = creator.createPerson("Case", "Person", Sex.MALE, 1980, 1, 2);
		creator.createCase(
			user.toReference(),
			cazePerson2.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			DateUtils.addMinutes(today, -3),
			rdcf);

		Assert.assertEquals(
			0,
			getCaseFacade().getCasesForDuplicateMerging(new CaseCriteria().creationDateFrom(today).creationDateTo(today), true).size());
	}

	@Test
	public void testGetDuplicateCasesOfDifferentSexAndSameBirthDateIsEmpty() {

		final Date today = new Date();

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person", Sex.MALE, 1980, 1, 1);
		creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			today,
			rdcf);

		PersonDto cazePerson2 = creator.createPerson("Case", "Person", Sex.FEMALE, 1980, 1, 1);
		creator.createCase(
			user.toReference(),
			cazePerson2.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			DateUtils.addMinutes(today, -3),
			rdcf);

		Assert.assertEquals(
			0,
			getCaseFacade().getCasesForDuplicateMerging(new CaseCriteria().creationDateFrom(today).creationDateTo(today), true).size());
	}

	@Test
	public void testDiseaseChangeUpdatesContacts() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);

		// Follow-up status and duration should be set to the requirements for EVD
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));

		caze.setDisease(Disease.MEASLES);
		caze = getCaseFacade().saveCase(caze);

		// Follow-up status and duration should be set to no follow-up and null
		// respectively because
		// Measles does not require a follow-up
		contact = getContactFacade().getContactByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.NO_FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(null, contact.getFollowUpUntil());
	}

	@Test
	public void testCountCasesWithMisingContactInformation() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		RDCFEntities newRDCF = creator.createRDCFEntities("New Region", "New District", "New Community", "New Facility");
		UserDto user = useSurveillanceOfficerLogin(rdcf);

		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		Assert.assertEquals(1, getCaseFacade().countCasesWithMissingContactInformation(Arrays.asList(caze.getUuid()), MessageType.SMS));

		cazePerson.setPhone("40742140797");
		getPersonFacade().savePerson(cazePerson);

		Assert.assertEquals(0, getCaseFacade().countCasesWithMissingContactInformation(Arrays.asList(caze.getUuid()), MessageType.SMS));
	}

	@Test
	public void testMovingCaseUpdatesTaskAssigneeAndCreatesPreviousHospitalization() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		RDCFEntities newRDCF = creator.createRDCFEntities("New Region", "New District", "New Community", "New Facility");
		UserDto user = useSurveillanceOfficerLogin(rdcf);

		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		UserDto caseOfficer = creator
			.createUser(newRDCF.region.getUuid(), newRDCF.district.getUuid(), newRDCF.facility.getUuid(), "Case", "Officer", UserRole.CASE_OFFICER);
		TaskDto pendingTask = creator.createTask(
			TaskContext.CASE,
			TaskType.CASE_INVESTIGATION,
			TaskStatus.PENDING,
			caze.toReference(),
			null,
			null,
			new Date(),
			user.toReference());
		TaskDto doneTask = creator.createTask(
			TaskContext.CASE,
			TaskType.CASE_INVESTIGATION,
			TaskStatus.DONE,
			caze.toReference(),
			null,
			null,
			new Date(),
			user.toReference());

		caze.setRegion(new RegionReferenceDto(newRDCF.region.getUuid(), null, null));
		caze.setDistrict(new DistrictReferenceDto(newRDCF.district.getUuid(), null, null));
		caze.setCommunity(new CommunityReferenceDto(newRDCF.community.getUuid(), null, null));
		caze.setHealthFacility(new FacilityReferenceDto(newRDCF.facility.getUuid(), null, null));
		caze.setSurveillanceOfficer(caseOfficer.toReference());
		CaseDataDto oldCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		CaseLogic.handleHospitalization(caze, oldCase, true);
		getCaseFacade().saveCase(caze);

		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		pendingTask = getTaskFacade().getByUuid(pendingTask.getUuid());
		doneTask = getTaskFacade().getByUuid(doneTask.getUuid());

		// Case should have the new region, district, community and facility set
		assertEquals(caze.getRegion().getUuid(), newRDCF.region.getUuid());
		assertEquals(caze.getDistrict().getUuid(), newRDCF.district.getUuid());
		assertEquals(caze.getCommunity().getUuid(), newRDCF.community.getUuid());
		assertEquals(caze.getHealthFacility().getUuid(), newRDCF.facility.getUuid());

		// Pending task should've been reassigned to the case officer, done task should
		// still be assigned to the surveillance supervisor
		assertEquals(pendingTask.getAssigneeUser().getUuid(), caseOfficer.getUuid());
		assertEquals(doneTask.getAssigneeUser().getUuid(), user.getUuid());

		// A previous hospitalization with the former facility should have been created
		List<PreviousHospitalizationDto> previousHospitalizations = caze.getHospitalization().getPreviousHospitalizations();
		assertEquals(previousHospitalizations.size(), 1);
	}

	@Test
	public void testGetCasesForDashboard() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		RDCFEntities rdcf2 = creator.createRDCFEntities("Region2", "District2", "Community2", "Facility2");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		CaseDataDto caze2 = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf2);
		caze2.setSharedToCountry(true);
		getCaseFacade().saveCase(caze2);

		CaseCriteria caseCriteria = new CaseCriteria().region(caze.getRegion())
			.district(caze.getDistrict())
			.disease(caze.getDisease())
			.newCaseDateBetween(DateHelper.subtractDays(new Date(), 1), DateHelper.addDays(new Date(), 1), NewCaseDateType.MOST_RELEVANT);

		List<DashboardCaseDto> dashboardCaseDtos = getCaseFacade().getCasesForDashboard(caseCriteria);

		// List should have only one entry; shared case should not appear
		assertEquals(1, dashboardCaseDtos.size());
	}

	@Test
	public void testMapCaseListCreation() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		List<MapCaseDto> mapCaseDtos = getCaseFacade().getCasesForMap(
			caze.getRegion(),
			caze.getDistrict(),
			caze.getDisease(),
			DateHelper.subtractDays(new Date(), 1),
			DateHelper.addDays(new Date(), 1));

		// List should have one entry
		assertEquals(1, mapCaseDtos.size());
	}

	@Test
	public void testGetIndexList() {

		String districtName = "District";
		RDCF rdcf = creator.createRDCF("Region", districtName, "Community", "Facility");
		useSurveillanceOfficerLogin(rdcf);

		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		String lastName = "Person";
		PersonDto cazePerson = creator.createPerson("Case", lastName);
		creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			c -> c.setHealthFacilityDetails("abc"));
		creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			c -> c.setHealthFacilityDetails("xyz"));

		List<CaseIndexDto> results = getCaseFacade().getIndexList(
			null,
			0,
			100,
			Arrays.asList(
				new SortProperty(CaseIndexDto.DISEASE),
				new SortProperty(CaseIndexDto.PERSON_FIRST_NAME),
				new SortProperty(CaseIndexDto.DISTRICT_NAME),
				new SortProperty(CaseIndexDto.HEALTH_FACILITY_NAME, false),
				new SortProperty(CaseIndexDto.SURVEILLANCE_OFFICER_UUID)));

		// List should have one entry
		assertEquals(3, results.size());

		assertEquals(districtName, results.get(0).getDistrictName());
		assertEquals(lastName, results.get(0).getPersonLastName());
		assertEquals("Facility - xyz", results.get(0).getHealthFacilityName());
		assertEquals("Facility - abc", results.get(1).getHealthFacilityName());
		assertEquals("Facility", results.get(2).getHealthFacilityName());
	}

	@Test
	public void testGetIndexListByFreeText() {

		String districtName = "District";
		RDCF rdcf = creator.createRDCF("Region", districtName, "Community", "Facility");
		useSurveillanceOfficerLogin(rdcf);

		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto person1 = creator.createPerson("FirstName1", "LastName1", p -> {
			p.getAddress().setPostalCode("10115");
			p.getAddress().setCity("Berlin");
			p.setPhone("+4930-90-1820");
		});
		creator.createCase(
			user.toReference(),
			person1.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		PersonDto person2 = creator.createPerson("FirstName2", "LastName2", p -> {
			p.getAddress().setPostalCode("20095");
			p.getAddress().setCity("Hamburg");
			p.setPhone("+49-30-901822");
		});
		creator.createCase(
			user.toReference(),
			person2.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		PersonDto person3 = creator.createPerson("FirstName3", "LastName3", p -> {
			p.getAddress().setPostalCode("80331");
			p.getAddress().setCity("Munich");
			p.setPhone("+49 31 9018 20");
		});
		creator.createCase(
			user.toReference(),
			person3.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		Assert.assertEquals(3, getCaseFacade().getIndexList(null, 0, 100, null).size());
		Assert.assertEquals(1, getCaseFacade().getIndexList(new CaseCriteria().nameUuidEpidNumberLike("Munich"), 0, 100, null).size());
		Assert.assertEquals(1, getCaseFacade().getIndexList(new CaseCriteria().nameUuidEpidNumberLike("20095"), 0, 100, null).size());
		Assert.assertEquals(2, getCaseFacade().getIndexList(new CaseCriteria().nameUuidEpidNumberLike("+49-31-901-820"), 0, 100, null).size());
		Assert.assertEquals(1, getCaseFacade().getIndexList(new CaseCriteria().nameUuidEpidNumberLike("4930901822"), 0, 100, null).size());
	}

	@Test
	public void testGetIndexListByEventFreeText() {

		String districtName = "District";
		RDCF rdcf = creator.createRDCF("Region", districtName, "Community", "Facility");
		useSurveillanceOfficerLogin(rdcf);

		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto person1 = creator.createPerson();
		PersonDto person2 = creator.createPerson();

		EventDto event1 = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Signal foo",
			"A long description for this event",
			user.toReference(),
			eventDto -> {
			});

		EventParticipantDto event1Participant1 = creator.createEventParticipant(event1.toReference(), person1, user.toReference());
		EventParticipantDto event1Participant2 = creator.createEventParticipant(event1.toReference(), person2, user.toReference());

		CaseDataDto case1 = creator.createCase(
			user.toReference(),
			person1.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		CaseDataDto case2 = creator.createCase(
			user.toReference(),
			person2.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		event1Participant1.setResultingCase(case1.toReference());
		getEventParticipantFacade().saveEventParticipant(event1Participant1);

		Assert.assertEquals(2, getCaseFacade().getIndexList(null, 0, 100, null).size());
		Assert.assertEquals(1, getCaseFacade().getIndexList(new CaseCriteria().eventLike("signal"), 0, 100, null).size());
		Assert.assertEquals(1, getCaseFacade().getIndexList(new CaseCriteria().eventLike(event1.getUuid()), 0, 100, null).size());
		Assert.assertEquals(1, getCaseFacade().getIndexList(new CaseCriteria().eventLike("signal description"), 0, 100, null).size());
	}

	@Test
	public void testGetExportList() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		cazePerson.getAddress().setCity("City");
		getPersonFacade().savePerson(cazePerson);

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.getLocation().setDetails("Ghana");
		exposure.setStartDate(new Date());
		exposure.setEndDate(new Date());
		caze.getEpiData().getExposures().add(exposure);
		caze.getSymptoms().setAbdominalPain(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		creator.createSample(caze.toReference(), cal.getTime(), cal.getTime(), user.toReference(), SampleMaterial.CRUST, rdcf.facility);
		creator.createPathogenTest(caze, PathogenTestType.ANTIGEN_DETECTION, PathogenTestResultType.POSITIVE);
		creator.createPrescription(caze);

		List<CaseExportDto> results = getCaseFacade().getExportList(new CaseCriteria(), CaseExportType.CASE_SURVEILLANCE, 0, 100, null, Language.EN);

		// List should have one entry
		assertEquals(1, results.size());

		assertEquals(true, results.get(0).getSampleDateTime2().after(results.get(0).getSampleDateTime3()));
		// Make sure that everything that is added retrospectively (symptoms, sample
		// dates, lab results, address, travel history) is present
		CaseExportDto exportDto = results.get(0);
		assertNotNull(exportDto.getSymptoms());
//		assertNotNull(exportDto.getSampleDateTime1());
//		assertNotNull(exportDto.getSampleLab1());
//		assertTrue(StringUtils.isNotEmpty(exportDto.getAddress()));
//		assertTrue(StringUtils.isNotEmpty(exportDto.getTravelHistory()));
	}

	/**
	 * Assure that n cardinalities do not duplicate case
	 */
	@Test
	public void testGetExportListNoDuplicates() {

		CaseFacade cut = getCaseFacade();

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto cazePerson = creator.createPerson("Case", "Person");
		cazePerson.getAddress().setCity("City");
		getPersonFacade().savePerson(cazePerson);

		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		caze.getEpiData().setExposureDetailsKnown(YesNoUnknown.YES);

		{
			ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
			exposure.getLocation().setDetails("Ghana");
			exposure.setStartDate(new Date());
			exposure.setEndDate(new Date());
			caze.getEpiData().getExposures().add(exposure);
		}
		{
			ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
			exposure.getLocation().setDetails("Nigeria");
			exposure.setStartDate(new Date());
			exposure.setEndDate(new Date());
			caze.getEpiData().getExposures().add(exposure);
		}

		caze = cut.saveCase(caze);

		List<CaseExportDto> result = cut.getExportList(new CaseCriteria(), CaseExportType.CASE_SURVEILLANCE, 0, 100, null, Language.EN);
		assertThat(result, hasSize(1));
		CaseExportDto exportDto = result.get(0);
		assertNotNull(exportDto.getEpiDataId());
		assertThat(exportDto.getUuid(), equalTo(caze.getUuid()));
		assertTrue(exportDto.isTraveled());
	}

	@Test
	public void testCaseDeletion() {

		Date since = new Date();

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		TaskDto task = creator.createTask(
			TaskContext.CASE,
			TaskType.CASE_INVESTIGATION,
			TaskStatus.PENDING,
			caze.toReference(),
			null,
			null,
			new Date(),
			user.toReference());
		SampleDto sample = creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		SampleDto sampleAssociatedToContactAndCase =
			creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		ContactDto contact2 =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		sampleAssociatedToContactAndCase.setAssociatedContact(new ContactReferenceDto(contact2.getUuid()));
		getSampleFacade().saveSample(sampleAssociatedToContactAndCase);

		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), caze);
		AdditionalTestDto additionalTest = creator.createAdditionalTest(sample.toReference());

		// Database should contain the created case, contact, task and sample
		assertNotNull(getCaseFacade().getCaseDataByUuid(caze.getUuid()));
		assertNotNull(getContactFacade().getContactByUuid(contact.getUuid()));
		assertNotNull(getSampleFacade().getSampleByUuid(sample.getUuid()));
		assertNotNull(getSampleTestFacade().getByUuid(pathogenTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(additionalTest.getUuid()));
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));

		getCaseFacade().deleteCase(caze.getUuid());

		// Deleted flag should be set for case, contact, sample and pathogen test; Task and additional test should be deleted
		assertTrue(getCaseFacade().getDeletedUuidsSince(since).contains(caze.getUuid()));
		assertTrue(getContactFacade().getDeletedUuidsSince(since).contains(contact.getUuid()));
		assertTrue(getSampleFacade().getDeletedUuidsSince(since).contains(sample.getUuid()));
		assertFalse(getSampleFacade().getDeletedUuidsSince(since).contains(sampleAssociatedToContactAndCase.getUuid()));
		assertTrue(getSampleTestFacade().getDeletedUuidsSince(since).contains(pathogenTest.getUuid()));
		assertNull(getAdditionalTestFacade().getByUuid(additionalTest.getUuid()));
		assertNull(getTaskFacade().getByUuid(task.getUuid()));
	}

	@Test
	public void testOutcomePersonConditionUpdate() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto firstCase = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		// case deceased -> person has to set to dead
		firstCase.setOutcome(CaseOutcome.DECEASED);
		firstCase = getCaseFacade().saveCase(firstCase);
		assertNotNull(firstCase.getOutcomeDate());
		cazePerson = getPersonFacade().getPersonByUuid(cazePerson.getUuid());
		assertEquals(PresentCondition.DEAD, cazePerson.getPresentCondition());
		assertEquals(firstCase.getDisease(), cazePerson.getCauseOfDeathDisease());

		// case has no outcome again -> person should be alive
		firstCase.setOutcome(CaseOutcome.NO_OUTCOME);
		firstCase = getCaseFacade().saveCase(firstCase);
		assertNull(firstCase.getOutcomeDate());
		cazePerson = getPersonFacade().getPersonByUuid(cazePerson.getUuid());
		assertEquals(PresentCondition.ALIVE, cazePerson.getPresentCondition());

		// case deceased -> person has to set to dead
		firstCase.setOutcome(CaseOutcome.DECEASED);
		firstCase = getCaseFacade().saveCase(firstCase);
		cazePerson = getPersonFacade().getPersonByUuid(cazePerson.getUuid());
		assertEquals(PresentCondition.DEAD, cazePerson.getPresentCondition());

		// person alive again -> case has to be reset to no outcome
		cazePerson.setPresentCondition(PresentCondition.ALIVE);
		cazePerson = getPersonFacade().savePerson(cazePerson);

		firstCase = getCaseFacade().getCaseDataByUuid(firstCase.getUuid());
		assertEquals(CaseOutcome.NO_OUTCOME, firstCase.getOutcome());
		assertNull(firstCase.getOutcomeDate());

		// additional case for the the person. set to deceased -> person has to be dead
		// and other no outcome cases have to be set to deceased
		CaseDataDto secondCase = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		secondCase.setOutcome(CaseOutcome.RECOVERED);
		secondCase = getCaseFacade().saveCase(secondCase);
		CaseDataDto thirdCase = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		thirdCase.setOutcome(CaseOutcome.DECEASED);
		thirdCase = getCaseFacade().saveCase(thirdCase);

		cazePerson = getPersonFacade().getPersonByUuid(cazePerson.getUuid());
		assertEquals(PresentCondition.DEAD, cazePerson.getPresentCondition());
		firstCase = getCaseFacade().getCaseDataByUuid(firstCase.getUuid());
		assertEquals(CaseOutcome.DECEASED, firstCase.getOutcome());
		assertNotNull(firstCase.getOutcomeDate());
		secondCase = getCaseFacade().getCaseDataByUuid(secondCase.getUuid());
		assertEquals(CaseOutcome.RECOVERED, secondCase.getOutcome());

		// person alive again -> deceased cases have to be set to no outcome
		cazePerson.setPresentCondition(PresentCondition.ALIVE);
		cazePerson = getPersonFacade().savePerson(cazePerson);
		firstCase = getCaseFacade().getCaseDataByUuid(firstCase.getUuid());
		assertEquals(CaseOutcome.NO_OUTCOME, firstCase.getOutcome());
		secondCase = getCaseFacade().getCaseDataByUuid(secondCase.getUuid());
		assertEquals(CaseOutcome.RECOVERED, secondCase.getOutcome());
		thirdCase = getCaseFacade().getCaseDataByUuid(thirdCase.getUuid());
		assertEquals(CaseOutcome.NO_OUTCOME, thirdCase.getOutcome());
	}

	@Test
	public void testOutcomePersonConditionUpdateForAppSync() throws InterruptedException {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto firstCase = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		// simulate short delay between transmissions
		Thread.sleep(DtoHelper.CHANGE_DATE_TOLERANCE_MS + 1);

		// case deceased -> person has to set to dead
		firstCase.setOutcome(CaseOutcome.DECEASED);
		cazePerson.setPresentCondition(PresentCondition.DEAD);
		cazePerson = getPersonFacade().savePerson(cazePerson);

		// this should throw an exception
		exception.expect(OutdatedEntityException.class);
		firstCase = getCaseFacade().saveCase(firstCase);
	}

	@Test
	public void testArchiveAndDearchiveCase() {
		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		Date testStartDate = new Date();

		// getAllActiveCases and getAllUuids should return length 1
		assertEquals(1, getCaseFacade().getAllActiveCasesAfter(null).size());
		assertEquals(1, getCaseFacade().getAllActiveUuids().size());

		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), true);

		// getAllActiveCases and getAllUuids should return length 0
		assertEquals(0, getCaseFacade().getAllActiveCasesAfter(null).size());
		assertEquals(0, getCaseFacade().getAllActiveUuids().size());

		// getArchivedUuidsSince should return length 1
		assertEquals(1, getCaseFacade().getArchivedUuidsSince(testStartDate).size());

		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), false);

		// getAllActiveCases and getAllUuids should return length 1
		assertEquals(1, getCaseFacade().getAllActiveCasesAfter(null).size());
		assertEquals(1, getCaseFacade().getAllActiveUuids().size());

		// getArchivedUuidsSince should return length 0
		assertEquals(0, getCaseFacade().getArchivedUuidsSince(testStartDate).size());
	}

	@Test
	public void testGetAllActiveCasesIncludeExtendedChangeDateFiltersSample() throws InterruptedException {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		Date date = new Date();
		//the delay is needed in order to ensure the time difference between the date and the case dependent objects update
		Thread.sleep(10L);

		sample.setComment("one comment");
		getSampleFacade().saveSample(sample);

		assertEquals(0, getCaseFacade().getAllActiveCasesAfter(date).size());
		assertEquals(1, getCaseFacade().getAllActiveCasesAfter(date, true).size());
	}

	@Test
	public void testGetAllActiveCasesIncludeExtendedChangeDateFiltersPathogenTest() throws InterruptedException {
		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		PathogenTestDto pathogenTestDto = creator.createPathogenTest(sample.toReference(), caze);

		Date date = new Date();
		//the delay is needed in order to ensure the time difference between the date and the case dependent objects update
		Thread.sleep(10L);

		pathogenTestDto.setTestResultText("test result changed");
		getPathogenTestFacade().savePathogenTest(pathogenTestDto);

		assertEquals(0, getCaseFacade().getAllActiveCasesAfter(date).size());
		assertEquals(1, getCaseFacade().getAllActiveCasesAfter(date, true).size());
	}

	@Test
	public void testGetAllActiveCasesIncludeExtendedChangeDateFiltersPatientTest() throws InterruptedException {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		Date date = new Date();
		//the delay is needed in order to ensure the time difference between the date and the case dependent objects update
		Thread.sleep(10L);

		cazePerson.setBurialDate(new Date());
		getPersonFacade().savePerson(cazePerson);

		assertEquals(0, getCaseFacade().getAllActiveCasesAfter(date).size());
		assertEquals(1, getCaseFacade().getAllActiveCasesAfter(date, true).size());
	}

	@Test
	public void testGetAllActiveCasesIncludeExtendedChangeDateFiltersLocationTest() throws InterruptedException {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = useSurveillanceOfficerLogin(rdcf);

		CaseDataDto caze = creator.createCase(
			user.toReference(),
			creator.createPerson("Case", "Person").toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		Date date = new Date();
		//the delay is needed in order to ensure the time difference between the date and the case dependent objects update
		Thread.sleep(10L);

		PersonDto cazePerson = getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());
		cazePerson.getAddress().setStreet("new Street");
		cazePerson.getAddress().setHouseNumber("new Number");
		cazePerson.getAddress().setAdditionalInformation("new Information");
		getPersonFacade().savePerson(cazePerson);

		assertEquals(0, getCaseFacade().getAllActiveCasesAfter(date).size());
		assertEquals(1, getCaseFacade().getAllActiveCasesAfter(date, true).size());
	}

	@Test
	public void testGenerateEpidNumber() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			UserRole.SURVEILLANCE_SUPERVISOR,
			UserRole.ADMIN);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), rdcf);

		Calendar calendar = Calendar.getInstance();
		String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);

		assertEquals("COU-REG-DIS-" + year + "-001", caze.getEpidNumber());

		CaseDataDto secondCaze = creator.createCase(user.toReference(), cazePerson.toReference(), rdcf);

		assertEquals("COU-REG-DIS-" + year + "-002", secondCaze.getEpidNumber());

		secondCaze.setEpidNumber("COU-REG-DIS-" + year + "-0004");
		getCaseFacade().saveCase(secondCaze);

		CaseDataDto thirdCaze = creator.createCase(user.toReference(), cazePerson.toReference(), rdcf);

		assertEquals("COU-REG-DIS-" + year + "-005", thirdCaze.getEpidNumber());

		thirdCaze.setEpidNumber("COU-REG-DIS-" + year + "-3");
		getCaseFacade().saveCase(thirdCaze);

		CaseDataDto fourthCaze = creator.createCase(user.toReference(), cazePerson.toReference(), rdcf);

		assertEquals("COU-REG-DIS-" + year + "-005", fourthCaze.getEpidNumber());

		fourthCaze.setEpidNumber("COU-REG-DIS-" + year + "-AAA");
		getCaseFacade().saveCase(fourthCaze);
		fourthCaze = getCaseFacade().getCaseDataByUuid(fourthCaze.getUuid());

		assertEquals("COU-REG-DIS-" + year + "-005", fourthCaze.getEpidNumber());

		// Make sure that deleted cases are ignored when searching for the highest existing epid nummber
		getCaseFacade().deleteCase(fourthCaze.getUuid());

		CaseDataDto fifthCaze = creator.createCase(user.toReference(), cazePerson.toReference(), rdcf);

		assertEquals("COU-REG-DIS-" + year + "-005", fifthCaze.getEpidNumber());

	}

	@Test
	public void testMergeCase() {

		useNationalUserLogin();
		// 1. Create

		// Create leadCase
		UserDto leadUser = creator.createUser("", "", "", "", "");
		UserReferenceDto leadUserReference = new UserReferenceDto(leadUser.getUuid());
		PersonDto leadPerson = creator.createPerson("Alex", "Miller");
		PersonReferenceDto leadPersonReference = new PersonReferenceDto(leadPerson.getUuid());
		RDCF leadRdcf = creator.createRDCF();
		CaseDataDto leadCase = creator.createCase(
			leadUserReference,
			leadPersonReference,
			Disease.DENGUE,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			leadRdcf);
		leadCase.setClinicianEmail("mail");
		getCaseFacade().saveCase(leadCase);
		VisitDto leadVisit = creator.createVisit(leadCase.getDisease(), leadCase.getPerson(), leadCase.getReportDate());
		leadVisit.getSymptoms().setAnorexiaAppetiteLoss(SymptomState.YES);
		getVisitFacade().saveVisit(leadVisit);

		// Create otherCase
		UserDto otherUser = creator.createUser("", "", "", "", "");
		UserReferenceDto otherUserReference = new UserReferenceDto(otherUser.getUuid());
		PersonDto otherPerson = creator.createPerson("Max", "Smith");
		otherPerson.setBirthWeight(2);
		getPersonFacade().savePerson(otherPerson);
		PersonReferenceDto otherPersonReference = new PersonReferenceDto(otherPerson.getUuid());
		RDCF otherRdcf = creator.createRDCF();
		CaseDataDto otherCase = creator.createCase(
			otherUserReference,
			otherPersonReference,
			Disease.CHOLERA,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			otherRdcf);
		otherCase.setClinicianName("name");
		CaseReferenceDto otherCaseReference = getCaseFacade().getReferenceByUuid(otherCase.getUuid());
		ContactDto contact =
			creator.createContact(otherUserReference, otherUserReference, otherPersonReference, otherCase, new Date(), new Date(), null);
		Region region = creator.createRegion("");
		District district = creator.createDistrict("", region);
		SampleDto sample = creator.createSample(
			otherCaseReference,
			otherUserReference,
			creator.createFacility("", region, district, creator.createCommunity("", district)));
		TaskDto task = creator.createTask(
			TaskContext.CASE,
			TaskType.CASE_INVESTIGATION,
			TaskStatus.PENDING,
			otherCaseReference,
			new ContactReferenceDto(),
			new EventReferenceDto(),
			new Date(),
			otherUserReference);
		TreatmentDto treatment = creator.createTreatment(otherCase);
		PrescriptionDto prescription = creator.createPrescription(otherCase);
		ClinicalVisitDto visit = creator.createClinicalVisit(otherCase);
		getCaseFacade().saveCase(otherCase);
		VisitDto otherVisit = creator.createVisit(otherCase.getDisease(), otherCase.getPerson(), otherCase.getReportDate());
		otherVisit.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().saveVisit(otherVisit);

		// 2. Merge

		getCaseFacade().mergeCase(leadCase.getUuid(), otherCase.getUuid());

		// 3. Test

		CaseDataDto mergedCase = getCaseFacade().getCaseDataByUuid(leadCase.getUuid());

		// Check no values
		assertNull(mergedCase.getClassificationComment());

		// Check 'lead and other have different values'
		assertEquals(leadCase.getDisease(), mergedCase.getDisease());

		// Check 'lead has value, other has not'
		assertEquals(leadCase.getClinicianEmail(), mergedCase.getClinicianEmail());

		// Check 'lead has no value, other has'
		assertEquals(otherCase.getClinicianName(), mergedCase.getClinicianName());

		PersonDto mergedPerson = getPersonFacade().getPersonByUuid(mergedCase.getPerson().getUuid());

		// Check no values
		assertNull(mergedPerson.getBirthdateDD());

		// Check 'lead and other have different values'
		assertEquals(leadCase.getPerson().getFirstName(), mergedPerson.getFirstName());

		// Check 'lead has value, other has not'
		assertEquals(leadCase.getPerson().getLastName(), mergedPerson.getLastName());

		// Check 'lead has no value, other has'
		assertEquals(otherPerson.getBirthWeight(), mergedPerson.getBirthWeight());

		// 4. Test Reference Changes
		// 4.1 Contacts
		List<String> contactUuids = new ArrayList<String>();
		contactUuids.add(contact.getUuid());
		assertEquals(leadCase.getUuid(), getContactFacade().getByUuids(contactUuids).get(0).getCaze().getUuid());

		// 4.2 Samples
		List<String> sampleUuids = new ArrayList<String>();
		sampleUuids.add(sample.getUuid());
		assertEquals(leadCase.getUuid(), getSampleFacade().getByUuids(sampleUuids).get(0).getAssociatedCase().getUuid());

		// 4.3 Tasks
		List<String> taskUuids = new ArrayList<String>();
		taskUuids.add(task.getUuid());
		assertEquals(leadCase.getUuid(), getTaskFacade().getByUuids(taskUuids).get(0).getCaze().getUuid());

		// 4.4 Treatments
		List<String> treatmentUuids = new ArrayList<String>();
		treatmentUuids.add(treatment.getUuid());
		assertEquals(leadCase.getTherapy().getUuid(), getTreatmentFacade().getByUuids(treatmentUuids).get(0).getTherapy().getUuid());

		// 4.5 Prescriptions
		List<String> prescriptionUuids = new ArrayList<String>();
		prescriptionUuids.add(prescription.getUuid());
		assertEquals(leadCase.getTherapy().getUuid(), getPrescriptionFacade().getByUuids(prescriptionUuids).get(0).getTherapy().getUuid());

		// 4.6 Clinical Visits
		List<String> visitUuids = new ArrayList<String>();
		visitUuids.add(visit.getUuid());
		assertEquals(leadCase.getClinicalCourse().getUuid(), getClinicalVisitFacade().getByUuids(visitUuids).get(0).getClinicalCourse().getUuid());

		// 4.7 Visits;
		List<String> mergedVisits = getVisitFacade().getIndexList(new VisitCriteria().caze(mergedCase.toReference()), null, null, null)
			.stream()
			.map(VisitIndexDto::getUuid)
			.collect(Collectors.toList());
		assertEquals(2, mergedVisits.size());
		assertTrue(mergedVisits.contains(leadVisit.getUuid()));
		assertTrue(mergedVisits.contains(otherVisit.getUuid()));
		// and symptoms
		assertEquals(SymptomState.YES, mergedCase.getSymptoms().getAbdominalPain());
		assertEquals(SymptomState.YES, mergedCase.getSymptoms().getAnorexiaAppetiteLoss());
		assertTrue(mergedCase.getSymptoms().getSymptomatic());
	}

	@Test
	public void testDoesEpidNumberExist() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto cazePerson = creator.createPerson("Horst", "Meyer").toReference();
		CaseDataDto caze =
			creator.createCase(user, cazePerson, Disease.CHOLERA, CaseClassification.NOT_CLASSIFIED, InvestigationStatus.PENDING, new Date(), rdcf);

		// 1. Same case
		assertFalse(getCaseFacade().doesEpidNumberExist(caze.getEpidNumber(), caze.getUuid(), caze.getDisease()));

		// 2. Same disease and epid number
		assertTrue(getCaseFacade().doesEpidNumberExist(caze.getEpidNumber(), "abc", caze.getDisease()));

		// 3. Same disease, different epid number
		assertFalse(getCaseFacade().doesEpidNumberExist("123", "abc", caze.getDisease()));

		// 4. Different disease and same epid number
		assertFalse(getCaseFacade().doesEpidNumberExist(caze.getEpidNumber(), "abc", Disease.ANTHRAX));

		// 5. Different disease and different epid number
		assertFalse(getCaseFacade().doesEpidNumberExist("def", "abc", Disease.ANTHRAX));
	}

	@Test
	public void testSymptomsUpdatedByVisit() {

		RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto cazePerson = creator.createPerson("Foo", "Bar").toReference();
		CaseDataDto caze = creator
			.createCase(user, cazePerson, Disease.CORONAVIRUS, CaseClassification.NOT_CLASSIFIED, InvestigationStatus.PENDING, new Date(), rdcf);
		caze.getSymptoms().setChestPain(SymptomState.YES);

		// Add a new visit to the case
		VisitDto visit = creator.createVisit(caze.getDisease(), caze.getPerson(), caze.getReportDate(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit.getSymptoms().setAbdominalPain(SymptomState.YES);
		visit.getSymptoms().setChestPain(SymptomState.NO);

		getCaseFacade().saveCase(caze);
		getVisitFacade().saveVisit(visit);
		CaseDataDto updatedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertEquals(SymptomState.YES, updatedCase.getSymptoms().getChestPain());
		assertEquals(SymptomState.YES, updatedCase.getSymptoms().getAbdominalPain());

		// Update an existing visit
		visit.getSymptoms().setAcuteRespiratoryDistressSyndrome(SymptomState.YES);
		getVisitFacade().saveVisit(visit);

		updatedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertEquals(SymptomState.YES, updatedCase.getSymptoms().getChestPain());
		assertEquals(SymptomState.YES, updatedCase.getSymptoms().getAbdominalPain());
		assertEquals(SymptomState.YES, updatedCase.getSymptoms().getAcuteRespiratoryDistressSyndrome());
		assertTrue(updatedCase.getSymptoms().getSymptomatic());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDoesEpidNumberExistLargeNumbers() {

		/*
		 * Running into Integer overflow is accepted since epid number follow a certain pattern
		 * and are not supposed to be bigger than Integer maxvalue.
		 */
		getCaseFacade().doesEpidNumberExist("NIE-08034912345", "not-a-uuid", Disease.OTHER);
	}

	@Test
	public void testArchiveAllArchivableCases() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person = creator.createPerson("Walter", "Schuster").toReference();

		// One archived case
		CaseDataDto case1 = creator.createCase(user, person, rdcf);
		CaseFacadeEjbLocal cut = getBean(CaseFacadeEjbLocal.class);
		cut.archiveOrDearchiveCase(case1.getUuid(), true);

		// One other case
		CaseDataDto case2 = creator.createCase(user, person, rdcf);

		assertTrue(cut.isArchived(case1.getUuid()));
		assertFalse(cut.isArchived(case2.getUuid()));

		// Case of "today" shouldn't be archived
		cut.archiveAllArchivableCases(70, LocalDate.now().plusDays(69));
		assertTrue(cut.isArchived(case1.getUuid()));
		assertFalse(cut.isArchived(case2.getUuid()));

		// Case of "yesterday" should be archived
		cut.archiveAllArchivableCases(70, LocalDate.now().plusDays(71));
		assertTrue(cut.isArchived(case1.getUuid()));
		assertTrue(cut.isArchived(case2.getUuid()));
	}

	@Test
	public void testCreateInvestigationTask() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserReferenceDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR).toReference();
		UserReferenceDto surveillanceOfficer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		PersonReferenceDto person = creator.createPerson("Case", "Person").toReference();

		CaseDataDto caze = creator.createCase(user, person, rdcf);

		List<TaskDto> caseTasks = getTaskFacade().getAllPendingByCase(caze.toReference());
		assertEquals(surveillanceOfficer, caseTasks.get(0).getAssigneeUser());
	}

	@Test
	public void testSetResponsibleSurveillanceOfficer() {
		RDCFEntities rdcf = creator.createRDCFEntities();
		RDCFEntities rdcf2 = creator.createRDCFEntities("Region2", "District2", "Community2", "Facility2");
		RDCFEntities rdcf3 = creator.createRDCFEntities("Region3", "District3", "Community3", "Facility3");
		creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		UserReferenceDto survOff2 = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		UserReferenceDto survOff3 = creator.createUser(rdcf2, UserRole.SURVEILLANCE_OFFICER).toReference();
		UserDto informant = creator.createUser(rdcf, UserRole.HOSPITAL_INFORMANT);
		informant.setAssociatedOfficer(survOff3);
		getUserFacade().saveUser(informant);

		// Reporting user is set as surveillance officer
		CaseDataDto caze = creator.createCase(survOff2, creator.createPerson().toReference(), rdcf);
		assertThat(caze.getSurveillanceOfficer(), is(survOff2));

		// Surveillance officer is removed if the district changes
		caze.setRegion(new RegionReferenceDto(rdcf3.region.getUuid(), null, null));
		caze.setDistrict(new DistrictReferenceDto(rdcf3.district.getUuid(), null, null));
		caze.setCommunity(new CommunityReferenceDto(rdcf3.community.getUuid(), null, null));
		caze.setHealthFacility(new FacilityReferenceDto(rdcf3.facility.getUuid(), null, null));
		caze = getCaseFacade().saveCase(caze);
		assertNull(caze.getSurveillanceOfficer());

		// Surveillance officer is set to the associated officer of an informant if available
		caze.setRegion(new RegionReferenceDto(rdcf2.region.getUuid(), null, null));
		caze.setDistrict(new DistrictReferenceDto(rdcf2.district.getUuid(), null, null));
		caze.setCommunity(new CommunityReferenceDto(rdcf2.community.getUuid(), null, null));
		caze.setHealthFacility(new FacilityReferenceDto(rdcf2.facility.getUuid(), null, null));
		caze = getCaseFacade().saveCase(caze);
		assertThat(caze.getSurveillanceOfficer(), is(survOff3));
	}

	@Test
	public void testSearchCasesWithExtendedQuarantine() {
		RDCF rdcf = creator.createRDCF();
		CaseDataDto caze =
			creator.createCase(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference(), creator.createPerson().toReference(), rdcf);
		caze.setQuarantineExtended(true);
		getCaseFacade().saveCase(caze);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, Collections.emptyList());
		assertThat(indexList.get(0).getUuid(), is(caze.getUuid()));

		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.setWithExtendedQuarantine(true);

		List<CaseIndexDto> indexListFiltered = getCaseFacade().getIndexList(caseCriteria, 0, 100, Collections.emptyList());
		assertThat(indexListFiltered.get(0).getUuid(), is(caze.getUuid()));
	}

	@Test
	public void testSearchCasesWithReducedQuarantine() {
		RDCF rdcf = creator.createRDCF();
		CaseDataDto caze =
			creator.createCase(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference(), creator.createPerson().toReference(), rdcf);
		caze.setQuarantineReduced(true);
		getCaseFacade().saveCase(caze);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, Collections.emptyList());
		assertThat(indexList.get(0).getUuid(), is(caze.getUuid()));

		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.setWithReducedQuarantine(true);

		List<CaseIndexDto> indexListFiltered = getCaseFacade().getIndexList(caseCriteria, 0, 100, Collections.emptyList());
		assertThat(indexListFiltered.get(0).getUuid(), is(caze.getUuid()));
	}

	@Test
	public void testGetDuplicates() {
		RDCF rdcf = creator.createRDCF();

		//case and person matching for asserts
		PersonDto person = creator.createPerson("Fname", "Lname", (p) -> {
			p.setBirthdateDD(12);
			p.setBirthdateMM(3);
			p.setBirthdateYYYY(1968);
		});

		CaseDataDto caze = creator.createCase(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference(), rdcf, (c) -> {
			c.setPerson(person.toReference());
			c.setExternalID("test-ext-id");
			c.setExternalToken("test-ext-token");
			c.setDisease(Disease.CORONAVIRUS);
			c.setDistrict(rdcf.district);
			c.setReportDate(new Date());
		});

		// case and person matching for some asserts
		PersonDto person2 = creator.createPerson("Fname", "Lname", (p) -> {
			p.setBirthdateMM(3);
			p.setBirthdateYYYY(1968);
		});
		creator.createCase(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference(), rdcf, (c) -> {
			c.setPerson(person2.toReference());
			c.setDisease(Disease.CORONAVIRUS);
		});

		creator.createCase(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference(), rdcf, (c) -> {
			c.setPerson(creator.createPerson().toReference());
			c.setDisease(Disease.CHOLERA);
		});

		creator.createCase(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference(), rdcf, (c) -> {
			c.setPerson(person.toReference());
			c.setDisease(Disease.CHOLERA);
		});

		CasePersonDto casePerson = new CasePersonDto();
		PersonDto duplicatePerson = PersonDto.build();
		CaseDataDto duplicateCaze = CaseDataDto.build(duplicatePerson.toReference(), Disease.CORONAVIRUS);
		duplicateCaze.setDistrict(rdcf.district);
		duplicateCaze.setReportDate(new Date());

		casePerson.setCaze(duplicateCaze);
		casePerson.setPerson(duplicatePerson);

		List<CasePersonDto> duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(0));

		// match by external ID
		duplicateCaze.setExternalID("test-ext-id");
		duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(1));

		// match by external ID case insensitive + trim
		duplicateCaze.setExternalID(" test-EXT-id ");
		duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(1));

		// match by external token
		duplicateCaze.setExternalID(null);
		duplicateCaze.setExternalToken(caze.getExternalToken());
		duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(1));

		// match by external token case insensitive + trim
		duplicateCaze.setExternalToken(" Test-ext-TOKEN ");
		duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(1));

		// match by first name and last name
		duplicateCaze.setExternalToken(null);
		duplicatePerson.setFirstName("Fname");
		duplicatePerson.setLastName("Lname");
		duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(2));

		// match by name and birth day should match also the one with missing birth day
		duplicatePerson.setBirthdateDD(12);
		duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(2));

		// match by name and birth day / month should match also the one with missing birth day
		duplicatePerson.setBirthdateMM(3);
		duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(2));

		// match by name and birth day / month / year should match also the one with missing birth day
		duplicatePerson.setBirthdateYYYY(1968);
		duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(2));

		// match by name and birth month / year
		duplicatePerson.setBirthdateDD(null);
		duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(2));

		// case insensitive name
		duplicatePerson.setFirstName(" fnamE");
		duplicatePerson.setLastName("lName ");
		duplicates = getCaseFacade().getDuplicates(casePerson);
		MatcherAssert.assertThat(duplicates, hasSize(2));
	}

	@Test
	public void testCreateCaseWithoutUuid() {
		RDCF rdcf = creator.createRDCF();
		CaseDataDto caze = new CaseDataDto();

		caze.setReportDate(new Date());
		caze.setReportingUser(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference());
		caze.setCaseClassification(CaseClassification.PROBABLE);
		caze.setInvestigationStatus(InvestigationStatus.PENDING);
		caze.setDisease(Disease.CORONAVIRUS);
		caze.setPerson(creator.createPerson().toReference());
		caze.setRegion(rdcf.region);
		caze.setDistrict(rdcf.district);
		caze.setFacilityType(FacilityType.HOSPITAL);
		caze.setHealthFacility(rdcf.facility);

		caze.setTherapy(new TherapyDto());
		caze.setSymptoms(new SymptomsDto());
		EpiDataDto epiData = new EpiDataDto();
		ExposureDto exposure = new ExposureDto();
		exposure.setExposureType(ExposureType.WORK);
		epiData.setExposures(Collections.singletonList(exposure));
		caze.setEpiData(epiData);

		CaseDataDto savedCaze = getCaseFacade().saveCase(caze);

		MatcherAssert.assertThat(savedCaze.getUuid(), not(isEmptyOrNullString()));
		MatcherAssert.assertThat(savedCaze.getTherapy().getUuid(), not(isEmptyOrNullString()));
		MatcherAssert.assertThat(savedCaze.getSymptoms().getUuid(), not(isEmptyOrNullString()));
		MatcherAssert.assertThat(savedCaze.getEpiData().getUuid(), not(isEmptyOrNullString()));
		MatcherAssert.assertThat(savedCaze.getEpiData().getExposures().get(0).getUuid(), not(isEmptyOrNullString()));
	}

	@Test
	public void testGetDuplicatesWithReportDateThreshold() {
		RDCF rdcf = creator.createRDCF();
		Date now = new Date();
		//case and person matching for asserts
		PersonDto person = creator.createPerson("Fname", "Lname", (p) -> {
			p.setBirthdateDD(12);
			p.setBirthdateMM(3);
			p.setBirthdateYYYY(1968);
		});

		CaseDataDto caze = creator.createCase(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference(), rdcf, (c) -> {
			c.setPerson(person.toReference());
			c.setDisease(Disease.CORONAVIRUS);
			c.setDistrict(rdcf.district);
			c.setReportDate(new Date());
		});

		PersonDto person2 = creator.createPerson("Fname", "Lname", (p) -> {
			p.setBirthdateMM(3);
			p.setBirthdateYYYY(1968);
		});
		creator.createCase(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference(), rdcf, (c) -> {
			c.setPerson(person2.toReference());
			c.setDisease(Disease.CORONAVIRUS);
			c.setReportDate(new DateTime(now).minusDays(1).toDate());
		});

		CasePersonDto casePerson = new CasePersonDto();
		PersonDto duplicatePerson = PersonDto.build();
		CaseDataDto duplicateCaze = CaseDataDto.build(duplicatePerson.toReference(), Disease.CORONAVIRUS);
		duplicateCaze.setDistrict(rdcf.district);
		duplicateCaze.setReportDate(new Date());

		casePerson.setCaze(duplicateCaze);
		casePerson.setPerson(duplicatePerson);

		List<CasePersonDto> duplicates;

		duplicateCaze.setExternalToken(null);
		duplicatePerson.setFirstName("Fname");
		duplicatePerson.setLastName("Lname");
		duplicates = getCaseFacade().getDuplicates(casePerson, 1);
		MatcherAssert.assertThat(duplicates, hasSize(2));
	}



//	@Test
//	public void testGetSimilarCases() {
//		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
//		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
//				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
//		PersonDto cazePerson = creator.createPerson("Case", "Person");
//		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, 
//				CaseClassification.NOT_CLASSIFIED, InvestigationStatus.PENDING, new Date(), rdcf);
//		
//		// 1: Same disease, same region, similar name, report date in range
//		CaseCriteria caseCriteria = new CaseCriteria()
//				.disease(caze.getDisease())
//				.region(caze.getRegion());
//		CaseSimilarityCriteria criteria = new CaseSimilarityCriteria()
//				.firstName("Person")
//				.lastName("Case")
//				.caseCriteria(caseCriteria)
//				.reportDate(new Date());
//		
//		assertEquals(1, getCaseFacade().getSimilarCases(criteria, user.getUuid()).size());
//		
//		// 2: Different disease
//		caseCriteria.disease(Disease.LASSA);
//		
//		assertEquals(0, getCaseFacade().getSimilarCases(criteria, user.getUuid()).size());
//		
//		// 3: Different region
//		RDCFEntities rdcfDifferent = creator.createRDCFEntities("Other region", "Other district", "Other community", "Other facility");
//		caseCriteria.disease(Disease.EVD);
//		caseCriteria.region(new RegionReferenceDto(rdcf.region.getUuid()));
//
//		assertEquals(0, getCaseFacade().getSimilarCases(criteria, user.getUuid()).size());
//		
//		// 4: Non-similar name
//		caseCriteria.region(caze.getRegion());
//		criteria.firstName("Different");
//		criteria.lastName("Name");
//
//		assertEquals(0, getCaseFacade().getSimilarCases(criteria, user.getUuid()).size());
//		
//		// 5: Region date out of range
//		criteria.firstName("Person");
//		criteria.lastName("Case");
//		criteria.reportDate(DateHelper.subtractDays(new Date(), 60));
//
//		assertEquals(0, getCaseFacade().getSimilarCases(criteria, user.getUuid()).size());
//	}
}
