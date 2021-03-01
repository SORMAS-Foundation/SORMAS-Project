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
package de.symeda.sormas.backend.contact;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
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
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataHelper;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.api.visit.VisitSummaryExportDetailsDto;
import de.symeda.sormas.api.visit.VisitSummaryExportDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.visit.Visit;

public class ContactFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetMatchingContacts() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact1 =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		contact1.setContactClassification(ContactClassification.CONFIRMED);
		getContactFacade().saveContact(contact1);
		ContactDto contact2 = creator.createContact(
			user.toReference(),
			user.toReference(),
			contactPerson.toReference(),
			caze,
			DateHelper.subtractDays(new Date(), 15),
			new Date(),
			null);
		ContactDto contact3 = creator.createContact(
			user.toReference(),
			user.toReference(),
			contactPerson.toReference(),
			caze,
			DateHelper.subtractDays(new Date(), 15),
			DateHelper.subtractDays(new Date(), 31),
			null);

		final ContactSimilarityCriteria contactSimilarityCriteria = new ContactSimilarityCriteria();
		contactSimilarityCriteria.setDisease(Disease.CORONAVIRUS);
		contactSimilarityCriteria.setPerson(new PersonReferenceDto(contactPerson.getUuid()));
		contactSimilarityCriteria.setCaze(new CaseReferenceDto(caze.getUuid()));
		contactSimilarityCriteria.setLastContactDate(new Date());
		contactSimilarityCriteria.setLastContactDate(new Date());
		contactSimilarityCriteria.setReportDate(new Date());

		final List<SimilarContactDto> matchingContacts = getContactFacade().getMatchingContacts(contactSimilarityCriteria);
		Assert.assertNotNull(matchingContacts);
		Assert.assertEquals(2, matchingContacts.size());
		final SimilarContactDto similarContactDto1 = matchingContacts.get(0);
		Assert.assertEquals(contact1.getUuid(), similarContactDto1.getUuid());
		final SimilarContactDto similarContactDto2 = matchingContacts.get(1);
		Assert.assertEquals(contact2.getUuid(), similarContactDto2.getUuid());
	}

	@Test
	public void testUpdateContactStatus() {

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
		Date contactDate = new Date();
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, contactDate, contactDate, null);

		assertEquals(ContactStatus.ACTIVE, contact.getContactStatus());
		assertNull(contact.getResultingCase());

		// drop
		contact.setContactClassification(ContactClassification.NO_CONTACT);
		contact = getContactFacade().saveContact(contact);
		assertEquals(ContactStatus.DROPPED, contact.getContactStatus());

		// add result case
		CaseDataDto resultingCaze = creator.createCase(
			user.toReference(),
			contactPerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			contactDate,
			rdcf);
		contact.setContactClassification(ContactClassification.CONFIRMED);
		contact.setResultingCase(getCaseFacade().getReferenceByUuid(resultingCaze.getUuid()));
		contact = getContactFacade().saveContact(contact);
		assertEquals(ContactStatus.CONVERTED, contact.getContactStatus());
	}

	@Test
	public void testContactFollowUpStatusCanceledWhenContactDropped() {
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
		Date contactDate = new Date();
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, contactDate, contactDate, null);

		assertEquals(ContactStatus.ACTIVE, contact.getContactStatus());
		assertNull(contact.getResultingCase());

		contact.setContactClassification(ContactClassification.CONFIRMED);
		contact.setContactStatus(ContactStatus.DROPPED);
		contact = getContactFacade().saveContact(contact);
		assertEquals(ContactClassification.CONFIRMED, contact.getContactClassification());
		assertEquals(ContactStatus.DROPPED, contact.getContactStatus());
		assertEquals(FollowUpStatus.CANCELED, contact.getFollowUpStatus());
	}

	@Test
	public void testContactFollowUpStatusCanceledWhenContactConvertedToCase() {
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

		Date contactDate = new Date();
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, contactDate, contactDate, null);

		assertEquals(ContactStatus.ACTIVE, contact.getContactStatus());
		assertNull(contact.getResultingCase());

		contact.setContactClassification(ContactClassification.CONFIRMED);
		contact = getContactFacade().saveContact(contact);

		final CaseDataDto resultingCase = creator.createCase(
			user.toReference(),
			contactPerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		contact.setResultingCase(resultingCase.toReference());
		contact = getContactFacade().saveContact(contact);
		assertEquals(ContactClassification.CONFIRMED, contact.getContactClassification());
		assertEquals(ContactStatus.CONVERTED, contact.getContactStatus());
		assertEquals(FollowUpStatus.CANCELED, contact.getFollowUpStatus());
	}

	@Test
	public void testGenerateContactFollowUpTasks() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto contactOfficer =
			creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Cont", "Off", UserRole.CONTACT_OFFICER);
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
			creator.createContact(user.toReference(), contactOfficer.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);

		getContactFacade().generateContactFollowUpTasks();

		// task should have been generated
		List<TaskDto> tasks = getTaskFacade().getAllByContact(contact.toReference())
			.stream()
			.filter(t -> t.getTaskType() == TaskType.CONTACT_FOLLOW_UP)
			.collect(Collectors.toList());
		assertEquals(1, tasks.size());
		TaskDto task = tasks.get(0);
		assertEquals(TaskType.CONTACT_FOLLOW_UP, task.getTaskType());
		assertEquals(TaskStatus.PENDING, task.getTaskStatus());
		assertEquals(LocalDate.now(), DateHelper8.toLocalDate(task.getDueDate()));
		assertEquals(contactOfficer.toReference(), task.getAssigneeUser());

		// task should not be generated multiple times 
		getContactFacade().generateContactFollowUpTasks();
		tasks = getTaskFacade().getAllByContact(contact.toReference())
			.stream()
			.filter(t -> t.getTaskType() == TaskType.CONTACT_FOLLOW_UP)
			.collect(Collectors.toList());
		assertEquals(1, tasks.size());
	}

	@Test
	public void testMapContactListCreation() {

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
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
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		MapCaseDto mapCaseDto = new MapCaseDto(
			caze.getUuid(),
			caze.getReportDate(),
			caze.getCaseClassification(),
			caze.getDisease(),
			caze.getPerson().getUuid(),
			cazePerson.getFirstName(),
			cazePerson.getLastName(),
			caze.getHealthFacility().getUuid(),
			0d,
			0d,
			caze.getReportLat(),
			caze.getReportLon(),
			caze.getReportLat(),
			caze.getReportLon(),
			null,
			null,
			null,
			null,
			null);

		List<MapContactDto> mapContactDtos =
			getContactFacade().getContactsForMap(caze.getRegion(), caze.getDistrict(), caze.getDisease(), Arrays.asList(mapCaseDto));

		// List should have one entry
		assertEquals(1, mapContactDtos.size());
	}

	@Test
	public void testContactDeletion() {

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
		VisitDto visit = creator.createVisit(
			caze.getDisease(),
			contactPerson.toReference(),
			DateUtils.addDays(new Date(), 21),
			VisitStatus.UNAVAILABLE,
			VisitOrigin.USER);
		TaskDto task = creator.createTask(
			TaskContext.CONTACT,
			TaskType.CONTACT_INVESTIGATION,
			TaskStatus.PENDING,
			null,
			contact.toReference(),
			null,
			new Date(),
			user.toReference());
		SampleDto sample =
			creator.createSample(contact.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		SampleDto sample2 =
			creator.createSample(contact.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		sample2.setAssociatedCase(new CaseReferenceDto(caze.getUuid()));
		getSampleFacade().saveSample(sample2);

		// Database should contain the created contact, visit and task
		assertNotNull(getContactFacade().getContactByUuid(contact.getUuid()));
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));
		assertNotNull(getVisitFacade().getVisitByUuid(visit.getUuid()));
		assertNotNull(getSampleFacade().getSampleByUuid(sample.getUuid()));

		getContactFacade().deleteContact(contact.getUuid());

		// Deleted flag should be set for contact; Task should be deleted
		assertTrue(getContactFacade().getDeletedUuidsSince(since).contains(contact.getUuid()));
		// Can't delete visit because it might be associated with other contacts as well
		//		assertNull(getVisitFacade().getVisitByUuid(visit.getUuid()));
		assertNull(getTaskFacade().getByUuid(task.getUuid()));
		assertTrue(getSampleFacade().getDeletedUuidsSince(since).contains(sample.getUuid()));
		assertFalse(getSampleFacade().getDeletedUuidsSince(since).contains(sample2.getUuid()));
	}

	@Test
	public void testGetIndexList() {

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
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);

		// Database should contain one contact, associated visit and task
		assertEquals(1, getContactFacade().getIndexList(null, 0, 100, null).size());
	}

	@Test
	public void testIncludeContactsFromOtherJurisdictionsFilter() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		RegionReferenceDto regionReferenceDto = getRegionFacade().getByName("Region", false).get(0);
		DistrictReferenceDto districtReferenceDto = getDistrictFacade().getByName("District", regionReferenceDto, false).get(0);

		RDCFEntities rdcf2 = creator.createRDCFEntities("NewRegion", "NewDistrict", "Community2", "Facility2");
		RegionReferenceDto region2ReferenceDto = getRegionFacade().getByName("NewRegion", false).get(0);
		DistrictReferenceDto district2ReferenceDto = getDistrictFacade().getByName("NewDistrict", region2ReferenceDto, false).get(0);

		// "mainUser" is the user which executes the grid query
		UserDto mainUser = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		when(MockProducer.getPrincipal().getName()).thenReturn("SurvSup");

		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			mainUser.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		// 1) contact created by main user, jurisdiction same with main user, no case linked
		PersonDto contactPersonSameJurisdictionMainUserCreatorNoCase =
			creator.createPerson("contactSameJurisdictionMainUserCreatorNoCase", "Person1");
		ContactDto contactSameJurisdictionMainUserCreatorNoCase = creator.createContact(
			mainUser.toReference(),
			mainUser.toReference(),
			contactPersonSameJurisdictionMainUserCreatorNoCase.toReference(),
			null,
			new Date(),
			new Date(),
			null);
		updateContactJurisdictionAndCase(contactSameJurisdictionMainUserCreatorNoCase.getUuid(), regionReferenceDto, districtReferenceDto, null);

		// 2) contact created by main user, jurisdiction different from main user, no case linked
		PersonDto contactPersonDiffJurisMainUserCreatorNoCase = creator.createPerson("contactDiffJurisdictionMainUserCreatorNoCase", "Person2");
		ContactDto contactDiffJurisdictionMainUserCreatorNoCase = creator.createContact(
			mainUser.toReference(),
			mainUser.toReference(),
			contactPersonDiffJurisMainUserCreatorNoCase.toReference(),
			null,
			new Date(),
			new Date(),
			null);
		updateContactJurisdictionAndCase(contactDiffJurisdictionMainUserCreatorNoCase.getUuid(), region2ReferenceDto, district2ReferenceDto, null);

		// 3) contact created by main user, jurisdiction null, linked to case from main user's jurisdiction
		PersonDto contactPersonJurisdictionNullMainUserCreatorCaseSameJurisdiction =
			creator.createPerson("contactJurisdictionNullMainUserCreatorCaseSameJurisdiction", "Person3");
		ContactDto contactJurisdictionNullMainUserCreatorCaseSameJurisdiction = creator.createContact(
			mainUser.toReference(),
			mainUser.toReference(),
			contactPersonJurisdictionNullMainUserCreatorCaseSameJurisdiction.toReference(),
			caze,
			new Date(),
			new Date(),
			null);
		updateContactJurisdictionAndCase(
			contactJurisdictionNullMainUserCreatorCaseSameJurisdiction.getUuid(),
			null,
			null,
			new CaseReferenceDto(caze.getUuid()));

		UserDto user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv2",
			"Sup2",
			UserRole.SURVEILLANCE_SUPERVISOR);
		when(MockProducer.getPrincipal().getName()).thenReturn("Surv2Sup2");

		// 4) contact created by different user, jurisdiction same with main user, no case linked
		PersonDto contactPersonSameJurisdictionDiffUserNoCase = creator.createPerson("contactSameJurisdictionDiffUserNoCase", "Person4");
		ContactDto contactSameJurisdictionDiffUserNoCase = creator.createContact(
			user2.toReference(),
			user2.toReference(),
			contactPersonSameJurisdictionDiffUserNoCase.toReference(),
			null,
			new Date(),
			new Date(),
			null);
		updateContactJurisdictionAndCase(contactSameJurisdictionDiffUserNoCase.getUuid(), regionReferenceDto, districtReferenceDto, null);

		// 5) contact created by different user, jurisdiction different from main user, no case linked
		PersonDto contactPersonDiffJurisdictionDiffUserNoCase = creator.createPerson("contactDiffJurisdictionDiffUserNoCase", "Person5");
		ContactDto contactDiffJurisdictionDiffUserNoCase = creator.createContact(
			user2.toReference(),
			user2.toReference(),
			contactPersonDiffJurisdictionDiffUserNoCase.toReference(),
			null,
			new Date(),
			new Date(),
			null);

		// 6) contact created by different user, jurisdiction null, linked to case from main user's jurisdiction
		PersonDto contactPersonDiffJurisdictionDiffUserCaseSameJurisdiction =
			creator.createPerson("contactDiffJurisdictionDiffUserCaseSameJurisdiction", "Person6");
		ContactDto contactDiffJurisdictionDiffUserCaseSameJurisdiction = creator.createContact(
			user2.toReference(),
			user2.toReference(),
			contactPersonDiffJurisdictionDiffUserCaseSameJurisdiction.toReference(),
			null,
			new Date(),
			new Date(),
			null);
		updateContactJurisdictionAndCase(
			contactDiffJurisdictionDiffUserCaseSameJurisdiction.getUuid(),
			null,
			null,
			new CaseReferenceDto(caze.getUuid()));

		// includeContactsFromOtherJurisdictionsFilter = false - return 1, 3, 4, 6
		// includeContactsFromOtherJurisdictionsFilter = true - return 1, 2, 3, 4, 6
		when(MockProducer.getPrincipal().getName()).thenReturn("SurvSup");
		ContactCriteria gridContactCriteria = new ContactCriteria();
		List<ContactIndexDto> contactList = getContactFacade().getIndexList(gridContactCriteria, 0, 100, null);
		List<String> contactListUuids = new ArrayList<>();
		contactList.stream().forEach(contactIndexDto -> contactListUuids.add(contactIndexDto.getUuid()));
		assertEquals(4, getContactFacade().getIndexList(gridContactCriteria, 0, 100, null).size());
		assertFalse(contactListUuids.contains(contactDiffJurisdictionMainUserCreatorNoCase.getUuid()));
		assertFalse(contactListUuids.contains(contactDiffJurisdictionDiffUserNoCase.getUuid()));

		gridContactCriteria.setIncludeContactsFromOtherJurisdictions(true);
		contactListUuids.clear();
		List<ContactIndexDto> newContactList = getContactFacade().getIndexList(gridContactCriteria, 0, 100, null);
		newContactList.stream().forEach(contactIndexDto -> contactListUuids.add(contactIndexDto.getUuid()));
		assertEquals(5, getContactFacade().getIndexList(gridContactCriteria, 0, 100, null).size());
		assertFalse(contactListUuids.contains(contactDiffJurisdictionDiffUserNoCase.getUuid()));
	}

	public void updateContactJurisdictionAndCase(
		String contactUuid,
		RegionReferenceDto regionReferenceDto,
		DistrictReferenceDto districtReferenceDto,
		CaseReferenceDto caze) {

		ContactDto contactDto = getContactFacade().getContactByUuid(contactUuid);
		contactDto.setRegion(regionReferenceDto);
		contactDto.setDistrict(districtReferenceDto);
		contactDto.setCaze(caze);
		contactDto = getContactFacade().saveContact(contactDto);
	}

	@Test
	public void testGetIndexListByEventFreeText() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
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
		creator.createEventParticipant(event1.toReference(), person2, user.toReference());

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

		creator.createContact(user.toReference(), person1.toReference(), case1);
		creator.createContact(user.toReference(), person1.toReference(), case2);

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setIncludeContactsFromOtherJurisdictions(true);
		Assert.assertEquals(2, getContactFacade().getIndexList(null, 0, 100, null).size());
		Assert.assertEquals(2, getContactFacade().getIndexList(contactCriteria.eventLike("signal"), 0, 100, null).size());
		Assert.assertEquals(2, getContactFacade().getIndexList(contactCriteria.eventLike(event1.getUuid()), 0, 100, null).size());
		Assert.assertEquals(2, getContactFacade().getIndexList(contactCriteria.eventLike("signal description"), 0, 100, null).size());
		Assert.assertEquals(
			1,
			getContactFacade()
				.getIndexList(contactCriteria.eventLike("signal description").onlyContactsSharingEventWithSourceCase(true), 0, 100, null)
				.size());
	}

	@Test
	public void testGetIndexDetailedList() {

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setIncludeContactsFromOtherJurisdictions(true);
		List<SortProperty> sortProperties = Collections.emptyList();
		List<ContactIndexDetailedDto> result;

		// 0. No data: empty list
		result = getContactFacade().getIndexDetailedList(contactCriteria, null, null, sortProperties);
		assertThat(result, is(empty()));

		// Create needed structural data
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

		UserReferenceDto reportingUser = new UserReferenceDto(user.getUuid());
		EventDto event1 = creator.createEvent(reportingUser, DateHelper.subtractDays(new Date(), 1));
		EventDto event2 = creator.createEvent(reportingUser, new Date());

		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact1 =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);

		// 1a. one Contact without Event
		result = getContactFacade().getIndexDetailedList(contactCriteria, null, null, sortProperties);
		assertThat(result, hasSize(1));
		{
			ContactIndexDetailedDto dto = result.get(0);
			assertThat(dto.getUuid(), equalTo(contact1.getUuid()));
			assertThat(dto.getEventCount(), equalTo(0L));
			assertNull(dto.getLatestEventId());
			assertNull(dto.getLatestEventTitle());
			assertThat(dto.getVisitCount(), equalTo(0));
		}

		// 1b. one Contact with one Event
		creator.createEventParticipant(new EventReferenceDto(event1.getUuid()), contactPerson, reportingUser);
		result = getContactFacade().getIndexDetailedList(contactCriteria, null, null, sortProperties);
		assertThat(result, hasSize(1));
		{
			ContactIndexDetailedDto dto = result.get(0);
			assertThat(dto.getUuid(), equalTo(contact1.getUuid()));
			assertThat(dto.getEventCount(), equalTo(1L));
			assertThat(dto.getLatestEventId(), equalTo(event1.getUuid()));
			assertThat(dto.getLatestEventTitle(), equalTo(event1.getEventTitle()));
			assertThat(dto.getVisitCount(), equalTo(0));
		}

		// 1c. one Contact with two Events, second is leading
		creator.createEventParticipant(new EventReferenceDto(event2.getUuid()), contactPerson, reportingUser);
		result = getContactFacade().getIndexDetailedList(contactCriteria, null, null, sortProperties);
		assertThat(result, hasSize(1));
		{
			ContactIndexDetailedDto dto = result.get(0);
			assertThat(dto.getUuid(), equalTo(contact1.getUuid()));
			assertThat(dto.getEventCount(), equalTo(2L));
			assertThat(dto.getLatestEventId(), equalTo(event2.getUuid()));
			assertThat(dto.getLatestEventTitle(), equalTo(event2.getEventTitle()));
			assertThat(dto.getVisitCount(), equalTo(0));
		}

		// 1d. one Contact with two Events and one visit
		creator.createVisit(new PersonReferenceDto(contactPerson.getUuid()));
		result = getContactFacade().getIndexDetailedList(contactCriteria, null, null, sortProperties);
		assertThat(result, hasSize(1));
		{
			ContactIndexDetailedDto dto = result.get(0);
			assertThat(dto.getUuid(), equalTo(contact1.getUuid()));
			assertThat(dto.getEventCount(), equalTo(2L));
			assertThat(dto.getLatestEventId(), equalTo(event2.getUuid()));
			assertThat(dto.getLatestEventTitle(), equalTo(event2.getEventTitle()));
			assertThat(dto.getVisitCount(), equalTo(1));
		}

		// 1e. one Contact with two Events and three visits
		creator.createVisit(new PersonReferenceDto(contactPerson.getUuid()));
		creator.createVisit(new PersonReferenceDto(contactPerson.getUuid()));
		result = getContactFacade().getIndexDetailedList(contactCriteria, null, null, sortProperties);
		assertThat(result, hasSize(1));
		{
			ContactIndexDetailedDto dto = result.get(0);
			assertThat(dto.getUuid(), equalTo(contact1.getUuid()));
			assertThat(dto.getEventCount(), equalTo(2L));
			assertThat(dto.getLatestEventId(), equalTo(event2.getUuid()));
			assertThat(dto.getLatestEventTitle(), equalTo(event2.getEventTitle()));
			assertThat(dto.getVisitCount(), equalTo(3));
		}
	}

	@Test
	public void testGetContactCountsByCasesForDashboard() {

		List<Long> ids;

		// test with some random id: returns 0,0,0
		ids = Arrays.asList(5555L);
		int[] result = getContactFacade().getContactCountsByCasesForDashboard(ids);
		assertThat(result[0], equalTo(0));
		assertThat(result[1], equalTo(0));
		assertThat(result[2], equalTo(0));
	}

	@Test
	public void testGetNonSourceCaseCountForDashboard() {

		ContactFacade cut = getBean(ContactFacadeEjbLocal.class);

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto person = creator.createPerson("Case", "Person");
		Disease disease = Disease.OTHER;

		// 1. A case not resulted of a contact: 0
		CaseDataDto caseWithoutContact = creator.createCase(
			user.toReference(),
			person.toReference(),
			disease,
			CaseClassification.CONFIRMED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		assertThat(cut.getNonSourceCaseCountForDashboard(Collections.singletonList(caseWithoutContact.getUuid())), equalTo(0));

		// 2. Another case, but created from a contact: 1
		ContactDto contact = creator.createContact(user.toReference(), person.toReference(), disease);
		CaseDataDto caseWithContact = creator.createCase(
			user.toReference(),
			person.toReference(),
			disease,
			CaseClassification.CONFIRMED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		contact.setResultingCase(caseWithContact.toReference());
		contact = getContactFacade().saveContact(contact);
		assertThat(cut.getNonSourceCaseCountForDashboard(Arrays.asList(caseWithoutContact.getUuid(), caseWithContact.getUuid())), equalTo(1));

		// 3. Some more cases
		{
			CaseDataDto caseWithoutContact2 = creator.createCase(
				user.toReference(),
				person.toReference(),
				disease,
				CaseClassification.CONFIRMED,
				InvestigationStatus.PENDING,
				new Date(),
				rdcf);

			ContactDto contact2 = creator.createContact(user.toReference(), person.toReference(), disease);
			CaseDataDto caseWithContact2 = creator.createCase(
				user.toReference(),
				person.toReference(),
				disease,
				CaseClassification.CONFIRMED,
				InvestigationStatus.PENDING,
				new Date(),
				rdcf);
			contact2.setResultingCase(caseWithContact2.toReference());
			contact2 = getContactFacade().saveContact(contact2);

			ContactDto contact3 = creator.createContact(user.toReference(), person.toReference(), disease);
			CaseDataDto caseWithContact3 = creator.createCase(
				user.toReference(),
				person.toReference(),
				disease,
				CaseClassification.CONFIRMED,
				InvestigationStatus.PENDING,
				new Date(),
				rdcf);
			contact3.setResultingCase(caseWithContact3.toReference());
			contact3 = getContactFacade().saveContact(contact3);

			assertThat(
				cut.getNonSourceCaseCountForDashboard(
					Arrays.asList(
						caseWithoutContact.getUuid(),
						caseWithContact.getUuid(),
						caseWithoutContact2.getUuid(),
						caseWithContact2.getUuid(),
						caseWithContact3.getUuid())),
				equalTo(3));
		}
	}

	@Test
	public void testGetNonSourceCaseCountForDashboardVariousInClauseCount() {

		ContactFacade cut = getBean(ContactFacadeEjbLocal.class);

		// 0. Works for 0 cases
		assertThat(cut.getNonSourceCaseCountForDashboard(Collections.emptyList()), equalTo(0));
		assertThat(cut.getNonSourceCaseCountForDashboard(null), equalTo(0));

		// 1a. Works for 1 case
		assertThat(cut.getNonSourceCaseCountForDashboard(Collections.singletonList(DataHelper.createUuid())), equalTo(0));

		// 1b. Works for 2 cases
		assertThat(cut.getNonSourceCaseCountForDashboard(Arrays.asList(DataHelper.createUuid(), DataHelper.createUuid())), equalTo(0));

		// 1c. Works for 3 cases
		assertThat(
			cut.getNonSourceCaseCountForDashboard(Arrays.asList(DataHelper.createUuid(), DataHelper.createUuid(), DataHelper.createUuid())),
			equalTo(0));

		// 2a. Works for 1_000 cases
		assertThat(cut.getNonSourceCaseCountForDashboard(TestDataCreator.createValuesList(1_000, i -> DataHelper.createUuid())), equalTo(0));

		// 2b. Works for 100_000 cases
		assertThat(cut.getNonSourceCaseCountForDashboard(TestDataCreator.createValuesList(100_000, i -> DataHelper.createUuid())), equalTo(0));
	}

	@Test
	public void testCreatedContactExistWhenValidatedByUUID() {

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

		// database contains the created contact
		assertEquals(true, getContactFacade().isValidContactUuid(contact.getUuid()));
		// database contains the created contact
		assertEquals(false, getContactFacade().isValidContactUuid("nonExistingContactUUID"));
	}

	@Test
	public void testGetExportList() {

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
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
		ContactDto contact = creator.createContact(
			user.toReference(),
			user.toReference(),
			creator.createPerson("Contact", "Person").toReference(),
			caze,
			new Date(),
			new Date(),
			null,
			rdcf);
		PersonDto contactPerson = getPersonFacade().getPersonByUuid(contact.getPerson().getUuid());
		VisitDto visit = creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		EpiDataDto epiData = contact.getEpiData();
		epiData.setExposureDetailsKnown(YesNoUnknown.YES);
		List<ExposureDto> travels = new ArrayList<>();
		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.getLocation().setDetails("Mallorca");
		exposure.setStartDate(DateHelper.subtractDays(new Date(), 15));
		exposure.setEndDate(DateHelper.subtractDays(new Date(), 7));
		caze.getEpiData().getExposures().add(exposure);
		travels.add(exposure);
		epiData.setExposures(travels);
		contact.setEpiData(epiData);
		getContactFacade().saveContact(contact);

		contactPerson.getAddress().setRegion(new RegionReferenceDto(rdcf.region.getUuid(), null, null));
		contactPerson.getAddress().setDistrict(new DistrictReferenceDto(rdcf.district.getUuid(), null, null));
		contactPerson.getAddress().setCity("City");
		contactPerson.getAddress().setStreet("Test street");
		contactPerson.getAddress().setHouseNumber("Test number");
		contactPerson.getAddress().setAdditionalInformation("Test information");
		contactPerson.getAddress().setPostalCode("1234");
		getPersonFacade().savePerson(contactPerson);

		visit.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().saveVisit(visit);

		List<ContactExportDto> results;
		results = getContactFacade().getExportList(null, 0, 100, null, Language.EN);

		// Database should contain one contact, associated visit and task
		assertEquals(1, results.size());

		// Make sure that everything that is added retrospectively (address, last cooperative visit date and symptoms) is present
		ContactExportDto exportDto = results.get(0);

		assertEquals(rdcf.region.getCaption(), exportDto.getAddressRegion());
		assertEquals(rdcf.district.getCaption(), exportDto.getAddressDistrict());
		assertEquals("City", exportDto.getCity());
		assertEquals("Test street", exportDto.getStreet());
		assertEquals("Test number", exportDto.getHouseNumber());
		assertEquals("Test information", exportDto.getAdditionalInformation());
		assertEquals("1234", exportDto.getPostalCode());

		assertNotNull(exportDto.getLastCooperativeVisitDate());
		assertTrue(StringUtils.isNotEmpty(exportDto.getLastCooperativeVisitSymptoms()));
		assertEquals(YesNoUnknown.YES, exportDto.getLastCooperativeVisitSymptomatic());

		assertNotNull(exportDto.getEpiDataId());
		assertTrue(exportDto.isTraveled());
		assertEquals(
			EpiDataHelper.buildDetailedTravelString(
				exposure.getLocation().toString(),
				exposure.getDescription(),
				exposure.getStartDate(),
				exposure.getEndDate(),
				Language.EN),
			exportDto.getTravelHistory());
		assertThat(exportDto.getEventCount(), equalTo(0L));

		// one Contact with 2 Events
		UserReferenceDto reportingUser = new UserReferenceDto(user.getUuid());
		EventDto event1 = creator.createEvent(reportingUser, DateHelper.subtractDays(new Date(), 1));
		EventDto event2 = creator.createEvent(reportingUser, new Date());
		creator.createEventParticipant(new EventReferenceDto(event2.getUuid()), contactPerson, reportingUser);
		creator.createEventParticipant(new EventReferenceDto(event1.getUuid()), contactPerson, reportingUser);

		results = getContactFacade().getExportList(null, 0, 100, null, Language.EN);
		assertThat(results, hasSize(1));
		{
			ContactExportDto dto = results.get(0);
			assertThat(dto.getLatestEventId(), equalTo(event2.getUuid()));
			assertThat(dto.getLatestEventTitle(), equalTo(event2.getEventTitle()));
			assertThat(dto.getEventCount(), equalTo(2L));
		}
	}

	@Test
	public void testGetVisitSummaryExportList() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		String userUuid = user.getUuid();
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
		// Create another contact that should have the same visits as the first one
		ContactDto contact2 =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		VisitDto visit11 = creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit11.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().saveVisit(visit11);
		VisitDto visit12 = creator.createVisit(
			caze.getDisease(),
			contactPerson.toReference(),
			DateHelper.subtractDays(new Date(), 1),
			VisitStatus.COOPERATIVE,
			VisitOrigin.USER);
		visit12.getSymptoms().setChestPain(SymptomState.YES);
		getVisitFacade().saveVisit(visit12);
		PersonDto contactPersonWithoutFollowUp = creator.createPerson();
		creator.createContact(user.toReference(), contactPersonWithoutFollowUp.toReference());

		PersonDto contactPerson2 = creator.createPerson("Contact2", "Person2");
		ContactDto contact3 =
			creator.createContact(user.toReference(), user.toReference(), contactPerson2.toReference(), caze, new Date(), null, null);
		VisitDto visit21 =
			creator.createVisit(caze.getDisease(), contactPerson2.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit21.getSymptoms().setBackache(SymptomState.YES);
		getVisitFacade().saveVisit(visit21);

		final List<VisitSummaryExportDto> results = getContactFacade().getVisitSummaryExportList(null, 0, 100, Language.EN);
		assertNotNull(results);
		assertEquals(3, results.size());

		final VisitSummaryExportDto exportDto1 = results.get(0);
		assertEquals("Contact", exportDto1.getFirstName());
		assertEquals("Person", exportDto1.getLastName());
		assertEquals(contact.getUuid(), exportDto1.getUuid());
		final List<VisitSummaryExportDetailsDto> visitDetails = exportDto1.getVisitDetails();
		assertNotNull(visitDetails);
		assertEquals(2, visitDetails.size());
		final VisitSummaryExportDetailsDto visitDetail11 = visitDetails.get(0);
		assertEquals(VisitStatus.COOPERATIVE, visitDetail11.getVisitStatus());
		assertNotNull(visitDetail11.getVisitDateTime());
		assertEquals(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.CHEST_PAIN), visitDetail11.getSymptoms());
		final VisitSummaryExportDetailsDto visitDetail12 = visitDetails.get(1);
		assertEquals(VisitStatus.COOPERATIVE, visitDetail12.getVisitStatus());
		assertNotNull(visitDetail12.getVisitDateTime());
		assertEquals(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ABDOMINAL_PAIN), visitDetail12.getSymptoms());

		final VisitSummaryExportDto exportDto2 = results.get(1);
		assertEquals("Contact", exportDto2.getFirstName());
		assertEquals("Person", exportDto2.getLastName());
		assertEquals(contact2.getUuid(), exportDto2.getUuid());
		final List<VisitSummaryExportDetailsDto> visitDetails2 = exportDto1.getVisitDetails();
		assertNotNull(visitDetails2);
		assertEquals(2, visitDetails2.size());
		final VisitSummaryExportDetailsDto visitDetail21 = visitDetails2.get(0);
		assertEquals(VisitStatus.COOPERATIVE, visitDetail21.getVisitStatus());
		assertNotNull(visitDetail21.getVisitDateTime());
		assertEquals(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.CHEST_PAIN), visitDetail21.getSymptoms());
		final VisitSummaryExportDetailsDto visitDetail22 = visitDetails2.get(1);
		assertEquals(VisitStatus.COOPERATIVE, visitDetail22.getVisitStatus());
		assertNotNull(visitDetail22.getVisitDateTime());
		assertEquals(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ABDOMINAL_PAIN), visitDetail22.getSymptoms());

		final VisitSummaryExportDto exportDto3 = results.get(2);
		assertEquals("Contact2", exportDto3.getFirstName());
		assertEquals("Person2", exportDto3.getLastName());
		assertEquals(contact3.getUuid(), exportDto3.getUuid());
		final List<VisitSummaryExportDetailsDto> visitDetails3 = exportDto3.getVisitDetails();
		assertNotNull(visitDetails3);
		assertEquals(1, visitDetails3.size());
		final VisitSummaryExportDetailsDto visitDetail31 = visitDetails3.get(0);
		assertEquals(VisitStatus.COOPERATIVE, visitDetail31.getVisitStatus());
		assertNotNull(visitDetail31.getVisitDateTime());
		assertEquals(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.BACKACHE), visitDetail31.getSymptoms());
	}

	@Test
	public void testCountMaximumFollowUpDays() {

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
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		VisitDto visit = creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().saveVisit(visit);

		PersonDto contactPerson2 = creator.createPerson("Contact2", "Person2");
		creator.createContact(user.toReference(), user.toReference(), contactPerson2.toReference(), caze, new Date(), new Date(), null);
		VisitDto visit21 =
			creator.createVisit(caze.getDisease(), contactPerson2.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit21.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().saveVisit(visit21);
		VisitDto visit22 =
			creator.createVisit(caze.getDisease(), contactPerson2.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit22.getSymptoms().setAgitation(SymptomState.YES);
		getVisitFacade().saveVisit(visit22);

		PersonDto contactPerson3 = creator.createPerson("Contact3", "Person3");
		creator.createContact(user.toReference(), user.toReference(), contactPerson3.toReference(), caze, new Date(), new Date(), null);
		for (int i = 0; i < 10; i++) {
			creator.createVisit(caze.getDisease(), contactPerson3.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		}

		assertEquals(10, getContactFacade().countMaximumFollowUpDays(null));
	}

	@Test
	public void testArchiveOrDearchiveContact() {

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
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvSup");

		// getAllActiveContacts and getAllUuids should return length 1
		assertEquals(1, getContactFacade().getAllActiveContactsAfter(null).size());
		assertEquals(1, getContactFacade().getAllActiveUuids().size());
		assertEquals(1, getVisitFacade().getAllActiveVisitsAfter(null).size());
		assertEquals(1, getVisitFacade().getAllActiveUuids().size());

		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), true);

		// getAllActiveContacts and getAllUuids should return length 0
		assertEquals(0, getContactFacade().getAllActiveContactsAfter(null).size());
		assertEquals(0, getContactFacade().getAllActiveUuids().size());
		assertEquals(0, getVisitFacade().getAllActiveVisitsAfter(null).size());
		assertEquals(0, getVisitFacade().getAllActiveUuids().size());

		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), false);

		// getAllActiveContacts and getAllUuids should return length 1
		assertEquals(1, getContactFacade().getAllActiveContactsAfter(null).size());
		assertEquals(1, getContactFacade().getAllActiveUuids().size());
		assertEquals(1, getVisitFacade().getAllActiveVisitsAfter(null).size());
		assertEquals(1, getVisitFacade().getAllActiveUuids().size());
	}

	@Test
	public void testUpdateContactVisitAssociations() {

		UserDto user = creator.createUser(creator.createRDCFEntities(), UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto person = creator.createPerson();
		VisitDto visit = creator.createVisit(Disease.EVD, person.toReference());
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());
		Contact contactEntity = getContactService().getByUuid(contact.getUuid());
		Visit visitEntity = getVisitService().getByUuid(visit.getUuid());

		// Saved contact should have visit association
		assertThat(getVisitService().getAllByContact(contactEntity), hasSize(1));

		// Updating the contact but not changing the report date or last contact date should not alter the association
		contact.setDescription("Description");
		getContactFacade().saveContact(contact);

		assertThat(getVisitService().getAllByContact(contactEntity), hasSize(1));

		// Changing the report date to a value beyond the threshold should remove the association
		contact.setReportDateTime(DateHelper.addDays(visit.getVisitDateTime(), FollowUpLogic.ALLOWED_DATE_OFFSET + 20));
		getContactFacade().saveContact(contact);

		assertThat(getVisitService().getAllByContact(contactEntity), empty());

		// Changing the report date back to a value in the threshold should re-add the association
		contact.setReportDateTime(new Date());
		getContactFacade().saveContact(contact);

		assertThat(getVisitService().getAllByContact(contactEntity), hasSize(1));

		// Adding another contact that matches the visit person, disease and time frame should increase the collection size
		ContactDto contact2 = creator.createContact(user.toReference(), person.toReference());

		assertThat(getContactService().getAllByVisit(visitEntity), hasSize(2));

		// Adding another contact with the same person and disease, but an incompatible time frame should not increase the collection size
		creator.createContact(
			user.toReference(),
			person.toReference(),
			DateHelper.addDays(visit.getVisitDateTime(), FollowUpLogic.ALLOWED_DATE_OFFSET + 1));

		assertThat(getContactService().getAllByVisit(visitEntity), hasSize(2));

		// Adding another contact that is compatible to the time frame, but has a different person and/or disease should not increase the collection size
		PersonDto person2 = creator.createPerson();
		creator.createContact(user.toReference(), person2.toReference());
		creator.createContact(user.toReference(), person.toReference(), Disease.CSM);

		assertThat(getContactService().getAllByVisit(visitEntity), hasSize(2));

		// Changing the contact disease should decrease the collection size
		contact2.setDisease(Disease.CSM);
		getContactFacade().saveContact(contact2);

		assertThat(getContactService().getAllByVisit(visitEntity), hasSize(1));
	}

	@Test
	public void testSearchContactsWithExtendedQuarantine() {
		RDCF rdcf = creator.createRDCF();
		ContactDto contact =
			creator.createContact(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference(), creator.createPerson().toReference());
		contact.setQuarantineExtended(true);
		getContactFacade().saveContact(contact);

		List<ContactIndexDto> indexList = getContactFacade().getIndexList(new ContactCriteria(), 0, 100, Collections.emptyList());
		assertThat(indexList.get(0).getUuid(), is(contact.getUuid()));

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setWithExtendedQuarantine(true);

		List<ContactIndexDto> indexListFiltered = getContactFacade().getIndexList(contactCriteria, 0, 100, Collections.emptyList());
		assertThat(indexListFiltered.get(0).getUuid(), is(contact.getUuid()));
	}

	@Test
	public void testSearchContactsWithReducedQuarantine() {
		RDCF rdcf = creator.createRDCF();
		ContactDto contact =
			creator.createContact(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference(), creator.createPerson().toReference());
		contact.setQuarantineReduced(true);
		getContactFacade().saveContact(contact);

		List<ContactIndexDto> indexList = getContactFacade().getIndexList(new ContactCriteria(), 0, 100, Collections.emptyList());
		assertThat(indexList.get(0).getUuid(), is(contact.getUuid()));

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setWithReducedQuarantine(true);

		List<ContactIndexDto> indexListFiltered = getContactFacade().getIndexList(contactCriteria, 0, 100, Collections.emptyList());
		assertThat(indexListFiltered.get(0).getUuid(), is(contact.getUuid()));
	}

	@Test
	public void testCreateWithoutUuid() {
		RDCF rdcf = creator.createRDCF();

		ContactDto contact = new ContactDto();
		contact.setReportDateTime(new Date());
		contact.setReportingUser(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference());
		contact.setDisease(Disease.CORONAVIRUS);
		contact.setPerson(creator.createPerson().toReference());
		contact.setRegion(rdcf.region);
		contact.setDistrict(rdcf.district);
		contact.setHealthConditions(new HealthConditionsDto());

		ContactDto savedContact = getContactFacade().saveContact(contact);

		assertThat(savedContact.getUuid(), not(isEmptyOrNullString()));
		assertThat(savedContact.getHealthConditions().getUuid(), not(isEmptyOrNullString()));
	}
}
