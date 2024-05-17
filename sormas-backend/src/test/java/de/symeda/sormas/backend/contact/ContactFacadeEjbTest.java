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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.contact.MergeContactIndexDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.dashboard.DashboardContactDto;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.api.visit.VisitSummaryExportDetailsDto;
import de.symeda.sormas.api.visit.VisitSummaryExportDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.visit.Visit;

public class ContactFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testValidateWithNullReportingUser() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		PersonDto cazePerson = creator.createPerson("Case", "Person", Sex.MALE, 1980, 1, 1);
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		assertThrows(
			ValidationRuntimeException.class,
			() -> creator.createContact(null, null, contactPerson.toReference(), caze, new Date(), new Date(), null));
	}

	@Test
	public void testGetMatchingContacts() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		getContactFacade().save(contact1);
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
		contactSimilarityCriteria.withCaze(new CaseReferenceDto(caze.getUuid()));
		contactSimilarityCriteria.setLastContactDate(new Date());
		contactSimilarityCriteria.setReportDate(new Date());

		final List<SimilarContactDto> matchingContacts = getContactFacade().getMatchingContacts(contactSimilarityCriteria);
		assertNotNull(matchingContacts);
		assertEquals(2, matchingContacts.size());
		ArrayList<String> uuids = new ArrayList<>();
		uuids.add(contact1.getUuid());
		uuids.add(contact2.getUuid());
		final SimilarContactDto similarContactDto1 = matchingContacts.get(0);
		assertTrue(uuids.contains(similarContactDto1.getUuid()));
		final SimilarContactDto similarContactDto2 = matchingContacts.get(1);
		assertTrue(uuids.contains(similarContactDto2.getUuid()));
	}

	@Test
	public void testUpdateContactStatus() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		contact = getContactFacade().save(contact);
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
		contact = getContactFacade().save(contact);
		assertEquals(ContactStatus.CONVERTED, contact.getContactStatus());
	}

	@Test
	public void testContactFollowUpStatusCanceledWhenContactDropped() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		contact = getContactFacade().save(contact);
		assertEquals(ContactClassification.CONFIRMED, contact.getContactClassification());
		assertEquals(ContactStatus.DROPPED, contact.getContactStatus());
		assertEquals(FollowUpStatus.CANCELED, contact.getFollowUpStatus());
	}

	@Test
	public void testContactFollowUpStatusCanceledWhenContactConvertedToCase() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		contact = getContactFacade().save(contact);

		final CaseDataDto resultingCase = creator.createCase(
			user.toReference(),
			contactPerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		contact.setResultingCase(resultingCase.toReference());
		contact = getContactFacade().save(contact);
		assertEquals(ContactClassification.CONFIRMED, contact.getContactClassification());
		assertEquals(ContactStatus.CONVERTED, contact.getContactStatus());
		assertEquals(FollowUpStatus.CANCELED, contact.getFollowUpStatus());
	}

	@Test
	public void testDeleteContactsOutsideJurisdiction() {
		UserDto creatorUser = creator.createNationalUser();

		Region region = creator.createRegion("Region");
		District district1 = creator.createDistrict("District1", region);
		Community community1 = creator.createCommunity("Community1", district1);
		Facility facility1 = creator.createFacility("Facility1", FacilityType.HOSPITAL, region, district1, community1);

		District district2 = creator.createDistrict("District2", region);
		Community community2 = creator.createCommunity("Community2", district2);
		Facility facility2 = creator.createFacility("Facility2", FacilityType.HOSPITAL, region, district2, community2);

		TestDataCreator.RDCF rdcf1 = new RDCF(new TestDataCreator.RDCFEntities(region, district1, community1, facility1));
		TestDataCreator.RDCF rdcf2 = new RDCF(new TestDataCreator.RDCFEntities(region, district2, community2, facility2));

		PersonDto person2 = creator.createPerson();
		ContactDto contact2 = creator.createContact(rdcf2, creatorUser.toReference(), person2.toReference());

		assertEquals(1, getContactFacade().getAllActiveUuids().size());

		List<String> contactUuidList = new ArrayList<>();
		contactUuidList.add(contact2.getUuid());

		UserDto user = creator.createSurveillanceOfficer(rdcf1);
		loginWith(user);
		List<ProcessedEntity> processedEntities =
			getContactFacade().delete(contactUuidList, new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));
		List<String> deletedUuids = processedEntities.stream()
			.filter(processedEntity -> processedEntity.getProcessedEntityStatus().equals(ProcessedEntityStatus.SUCCESS))
			.map(ProcessedEntity::getEntityUuid)
			.collect(Collectors.toList());

		assertEquals(processedEntities.get(0).getProcessedEntityStatus(), ProcessedEntityStatus.ACCESS_DENIED_FAILURE);
		assertEquals(0, deletedUuids.size());

		loginWith(creatorUser);
		getContactFacade().delete(contactUuidList, new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));
		assertEquals(0, getContactFacade().getAllActiveUuids().size());
	}

	@Test
	public void testContactFollowUpStatusWhenConvertedCaseIsDeleted() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		contact = getContactFacade().save(contact);

		contact.setResultingCase(caze.toReference());
		contact = getContactFacade().save(contact);

		assertEquals(ContactClassification.CONFIRMED, contact.getContactClassification());
		assertEquals(ContactStatus.CONVERTED, contact.getContactStatus());
		assertEquals(FollowUpStatus.CANCELED, contact.getFollowUpStatus());

		getCaseFacade().delete(caze.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));
		List<ContactDto> contactDtos = getContactFacade().getByPersonUuids(Arrays.asList(contactPerson.getUuid()));
		assertEquals(1, contactDtos.size());
		contact = contactDtos.get(0);

		assertEquals(ContactClassification.CONFIRMED, contact.getContactClassification());
		assertEquals(ContactStatus.DROPPED, contact.getContactStatus());
		assertEquals(FollowUpStatus.CANCELED, contact.getFollowUpStatus());

		RegionReferenceDto regionReferenceDto = getRegionFacade().getAllActiveByServerCountry().get(0);
		DistrictReferenceDto districtReferenceDto = getDistrictFacade().getAllActiveAsReference().get(0);
		contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
		contact.setRegion(regionReferenceDto);
		contact.setDistrict(districtReferenceDto);
		contact = getContactFacade().save(contact);
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
	}

	@Test
	public void testGenerateContactFollowUpTasks() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		UserDto contactOfficer = creator.createContactOfficer(rdcf);
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
		assertEquals(LocalDate.now(), UtilDate.toLocalDate(task.getDueDate()));
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
		PersonDto contactPerson = creator.createPerson("Contact", "Person", p -> {
			p.getAddress().setLatitude(0.0);
			p.getAddress().setLongitude(0.0);
		});
		creator.createContact(
			user.toReference(),
			user.toReference(),
			contactPerson.toReference(),
			caze,
			new Date(),
			new Date(),
			caze.getDisease(),
			rdcf);

		Long count = getContactFacade().countContactsForMap(
			caze.getRegion(),
			caze.getDistrict(),
			caze.getDisease(),
			DateHelper.subtractDays(new Date(), 1),
			DateHelper.addDays(new Date(), 1));

		List<MapContactDto> mapContactDtos = getContactFacade().getContactsForMap(
			caze.getRegion(),
			caze.getDistrict(),
			caze.getDisease(),
			DateHelper.subtractDays(new Date(), 1),
			DateHelper.addDays(new Date(), 1));

		// List should have one entry
		assertEquals((long) count, mapContactDtos.size());
		assertEquals((long) 1, mapContactDtos.size());
	}

	@Test
	public void testContactDeletionAndRestoration() {

		Date since = new Date();

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		assertNotNull(getContactFacade().getByUuid(contact.getUuid()));
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));
		assertNotNull(getVisitFacade().getByUuid(visit.getUuid()));
		assertNotNull(getSampleFacade().getSampleByUuid(sample.getUuid()));

		getContactFacade().delete(contact.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));

		// Deleted flag should be set for contact; Task should be deleted
		assertTrue(getContactFacade().getDeletedUuidsSince(since).contains(contact.getUuid()));
		// Can't delete visit because it might be associated with other contacts as well
		//		assertNull(getVisitFacade().getByUuid(visit.getUuid()));
		assertNull(getTaskFacade().getByUuid(task.getUuid()));
		assertTrue(getSampleFacade().getDeletedUuidsSince(since).contains(sample.getUuid()));
		assertFalse(getSampleFacade().getDeletedUuidsSince(since).contains(sample2.getUuid()));
		assertEquals(DeletionReason.OTHER_REASON, getContactFacade().getByUuid(contact.getUuid()).getDeletionReason());
		assertEquals("test reason", getContactFacade().getByUuid(contact.getUuid()).getOtherDeletionReason());

		getContactFacade().restore(contact.getUuid());

		assertFalse(getContactFacade().getDeletedUuidsSince(since).contains(contact.getUuid()));
		assertNull(getTaskFacade().getByUuid(task.getUuid()));
		assertFalse(getSampleFacade().getDeletedUuidsSince(since).contains(sample.getUuid()));
		assertFalse(getSampleFacade().getDeletedUuidsSince(since).contains(sample2.getUuid()));
		assertNull(getContactFacade().getByUuid(contact.getUuid()).getDeletionReason());
		assertNull(getContactFacade().getByUuid(contact.getUuid()).getOtherDeletionReason());
	}

	@Test
	public void testGetIndexList() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
	public void testGetContactsForDuplicateMergingWithPseudonymization() {

		RDCF rdcf = creator.createRDCF();
		//User without SEE_PERSONAL_DATA_IN_JURISDICTION right
		UserDto user = creator.createUser(
			null,
			null,
			null,
			"User",
			"User",
			"User",
			JurisdictionLevel.NATION,
			UserRight.CONTACT_VIEW,
			UserRight.CONTACT_EDIT,
			UserRight.CASE_VIEW,
			UserRight.PERSON_VIEW,
			UserRight.PERSON_EDIT,
			UserRight.CONTACT_MERGE);

		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		PersonDto contactPerson1 = creator.createPerson("Contact", "Person");
		creator.createContact(user.toReference(), user.toReference(), contactPerson1.toReference(), caze, new Date(), new Date(), null);

		PersonDto contactPerson2 = creator.createPerson("Contact", "Person");
		creator.createContact(user.toReference(), user.toReference(), contactPerson2.toReference(), caze, new Date(), new Date(), null);

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setIncludeContactsFromOtherJurisdictions(true);

		loginWith(user);

		List<MergeContactIndexDto[]> contacts = getContactFacade().getContactsForDuplicateMerging(contactCriteria, 100, false);
		MergeContactIndexDto[] contactPair = contacts.get(0);

		assertNotNull(contacts);
		assertEquals(I18nProperties.getCaption(Captions.inaccessibleValue), contactPair[0].getFirstName());
		assertEquals(I18nProperties.getCaption(Captions.inaccessibleValue), contactPair[0].getLastName());
		assertEquals(I18nProperties.getCaption(Captions.inaccessibleValue), contactPair[1].getFirstName());
		assertEquals(I18nProperties.getCaption(Captions.inaccessibleValue), contactPair[1].getLastName());
	}

	@Test
	public void testGetIndexListWithLabUser() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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

		UserDto labUser = creator.createUser(null, null, null, "Lab", "Off", creator.getUserRoleReference(DefaultUserRole.LAB_USER));
		labUser.setLaboratory(rdcf.facility);
		getUserFacade().saveUser(labUser, false);

		loginWith(labUser);
		assertEquals(0, getContactFacade().getIndexList(null, 0, 100, null).size());

		loginWith(user);
		creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSampleDateTime(new Date());
			s.setComment("Test contact sample");
		});

		loginWith(labUser);
		assertEquals(1, getContactFacade().getIndexList(null, 0, 100, null).size());
	}

	@Test
	public void testIncludeContactsFromOtherJurisdictionsFilter() {

		RDCF rdcf = creator.createRDCF();
		RDCF rdcf2 = creator.createRDCF("NewRegion", "NewDistrict", "Community2", "Facility2");

		// "mainUser" is the user which executes the grid query
		UserDto mainUser = creator.createSurveillanceSupervisor(rdcf);
		UserDto user2 = creator.createUser(rdcf2, "Surv", "Sup2", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		loginWith(mainUser);

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
		updateContactJurisdictionAndCase(contactSameJurisdictionMainUserCreatorNoCase.getUuid(), rdcf.region, rdcf.district, null);

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
		updateContactJurisdictionAndCase(contactDiffJurisdictionMainUserCreatorNoCase.getUuid(), rdcf2.region, rdcf2.district, null);

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

		loginWith(user2);

		// 4) contact created by different user, jurisdiction same with main user, no case linked
		PersonDto contactPersonSameJurisdictionDiffUserNoCase = creator.createPerson("contactSameJurisdictionDiffUserNoCase", "Person4");
		ContactDto contactSameJurisdictionDiffUserNoCase = creator.createContact(
			user2.toReference(),
			user2.toReference(),
			contactPersonSameJurisdictionDiffUserNoCase.toReference(),
			null,
			new Date(),
			new Date(),
			null,
			null,
			c -> {
				c.setRegion(rdcf.region);
				c.setDistrict(rdcf.district);
			});

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
			null,
			null,
			c -> {
				c.setCaze(new CaseReferenceDto(caze.getUuid()));
			});

		// includeContactsFromOtherJurisdictionsFilter = false - return 1, 3, 4, 6
		// includeContactsFromOtherJurisdictionsFilter = true - return 1, 2, 3, 4, 6
		loginWith(mainUser);
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

		ContactDto contactDto = getContactFacade().getByUuid(contactUuid);
		contactDto.setRegion(regionReferenceDto);
		contactDto.setDistrict(districtReferenceDto);
		contactDto.setCaze(caze);
		contactDto = getContactFacade().save(contactDto);
	}

	@Test
	public void testGetIndexListByEventFreeText() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = useSurveillanceOfficerLogin(rdcf);

		PersonDto person1 = creator.createPerson();
		PersonDto person2 = creator.createPerson();

		EventDto event1 = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Signal foo",
			"A long description for this event",
			user.toReference(),
			null,
			null);

		EventParticipantDto event1Participant1 = creator.createEventParticipant(event1.toReference(), person1, "Involved", user.toReference(), rdcf);
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
		getEventParticipantFacade().save(event1Participant1);

		creator.createContact(user.toReference(), person1.toReference(), case1);
		creator.createContact(user.toReference(), person1.toReference(), case2);

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setIncludeContactsFromOtherJurisdictions(true);
		assertEquals(2, getContactFacade().getIndexList(null, 0, 100, null).size());
		assertEquals(2, getContactFacade().getIndexList(contactCriteria.eventLike("signal"), 0, 100, null).size());
		assertEquals(2, getContactFacade().getIndexList(contactCriteria.eventLike(event1.getUuid()), 0, 100, null).size());
		assertEquals(2, getContactFacade().getIndexList(contactCriteria.eventLike("signal description"), 0, 100, null).size());
		assertEquals(
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
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
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
	public void testGetIndexListByARestrictedAccessToAssignedEntities() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();

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
		final ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);

		PersonDto contactPerson2 = creator.createPerson("Contact2", "Person2");
		final ContactDto contact2 =
			creator.createContact(user.toReference(), user.toReference(), contactPerson2.toReference(), null, new Date(), new Date(), Disease.EVD);

		assertEquals(2, getContactFacade().getIndexList(null, 0, 100, null).size());

		UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities =
			creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertTrue(getCurrentUserService().isRestrictedToAssignedEntities());
		final List<ContactIndexDto> indexList = getContactFacade().getIndexList(null, 0, 100, null);
		assertEquals(0, indexList.size());

		loginWith(user);
		contact2.setContactOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getContactFacade().save(contact2);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(1, getContactFacade().getIndexList(null, 0, 100, null).size());

		loginWith(user);
		caze.setSurveillanceOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getCaseFacade().save(caze);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(2, getContactFacade().getIndexList(null, 0, 100, null).size());
	}

	@Test
	public void testGetIndexListWithLimitedDisease() {
		RDCF rdcf1 = creator.createRDCF("Region1", "District1", "Community1", "Facility1", "PointOfEntry1");
		RDCF rdcf2 = creator.createRDCF("Region2", "District2", "Community2", "Facility2", "PointOfEntry2");

		final UserDto surveillanceOfficer = creator.createSurveillanceOfficer(rdcf1);

		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		final ContactDto contact = creator.createContact(
			surveillanceOfficer.toReference(),
			null,
			contactPerson.toReference(),
			null,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);

		PersonDto contactPerson2 = creator.createPerson("Contact2", "Person2");
		final ContactDto contact2 = creator
			.createContact(surveillanceOfficer.toReference(), null, contactPerson2.toReference(), null, new Date(), new Date(), Disease.EVD, rdcf1);

		PersonDto contactPerson3 = creator.createPerson("Contact3", "Person3");
		final ContactDto contact3 = creator.createContact(
			surveillanceOfficer.toReference(),
			null,
			contactPerson3.toReference(),
			null,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);

		PersonDto contactPerson4 = creator.createPerson("Contact3", "Person3");
		final ContactDto contact4 = creator
			.createContact(surveillanceOfficer.toReference(), null, contactPerson4.toReference(), null, new Date(), new Date(), Disease.EVD, rdcf2);

		loginWith(nationalAdmin);
		Set<Disease> diseaseList = new HashSet<>();
		diseaseList.add(Disease.CORONAVIRUS);
		surveillanceOfficer.setLimitedDiseases(diseaseList);
		getUserFacade().saveUser(surveillanceOfficer, false);

		loginWith(surveillanceOfficer);
		assertEquals(2, getContactFacade().getIndexList(new ContactCriteria(), 0, 100, null).size());

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setIncludeContactsFromOtherJurisdictions(true);
		assertEquals(4, getContactFacade().getIndexList(contactCriteria, 0, 100, null).size());
	}

	@Test
	public void testGetContactCountsByCasesForDashboard() {

		List<Long> ids;

		// 0a. test with some random id: returns 0,0,0
		ids = Arrays.asList(5555L);
		int[] result = getContactFacade().getContactCountsByCasesForDashboard(ids);
		assertThat(result[0], equalTo(0));
		assertThat(result[1], equalTo(0));
		assertThat(result[2], equalTo(0));

		RDCF rdcf = creator.createRDCF();
		UserDto user = useNationalAdminLogin();

		// 0b. Test with a huge list of contact ids
		List<Long> manyIds = Stream.iterate(1000L, n -> n + 1).limit(123000L).collect(Collectors.toList());
		result = getContactFacade().getContactCountsByCasesForDashboard(manyIds);
		assertThat(result[0], equalTo(0));
		assertThat(result[1], equalTo(0));
		assertThat(result[2], equalTo(0));

		// 1. Test with one contact

		CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson("Case", "Person").toReference(), rdcf);
		ContactDto contact = creator.createContact(user.toReference(), creator.createPerson("Contact", "Person").toReference(), caze, rdcf);

		ids = Arrays.asList(getContactService().getByUuid(contact.getUuid()).getId());
		result = getContactFacade().getContactCountsByCasesForDashboard(ids);
		assertThat(result[0], equalTo(1));
		assertThat(result[1], equalTo(1));
		assertThat(result[2], equalTo(1));

		// 1b. Test with a huge list of contact ids
		ids = new ArrayList<>(ids);
		ids.addAll(manyIds);
		result = getContactFacade().getContactCountsByCasesForDashboard(ids);
		assertThat(result[0], equalTo(1));
		assertThat(result[1], equalTo(1));
		assertThat(result[2], equalTo(1));
	}

	@Test
	public void testGetNonSourceCaseCountForDashboard() {

		ContactFacade cut = getBean(ContactFacadeEjbLocal.class);

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
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
		contact = getContactFacade().save(contact);
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
			contact2 = getContactFacade().save(contact2);

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
			contact3 = getContactFacade().save(contact3);

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

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		assertEquals(true, getContactFacade().exists(contact.getUuid()));
		// database contains the created contact
		assertEquals(false, getContactFacade().exists("nonExistingContactUUID"));
	}

	@Test
	public void testGetExportListWithRelevantVaccinations() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = useNationalAdminLogin();

		CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson("Case", "Person").toReference(), rdcf);
		ContactDto contact = creator.createContact(user.toReference(), creator.createPerson("Contact", "Person").toReference(), caze, rdcf);

		PersonDto contactPerson = getPersonFacade().getByUuid(contact.getPerson().getUuid());
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
		getContactFacade().save(contact);

		contactPerson.getAddress().setRegion(new RegionReferenceDto(rdcf.region.getUuid(), null, null));
		contactPerson.getAddress().setDistrict(new DistrictReferenceDto(rdcf.district.getUuid(), null, null));
		contactPerson.getAddress().setCity("City");
		contactPerson.getAddress().setStreet("Test street");
		contactPerson.getAddress().setHouseNumber("Test number");
		contactPerson.getAddress().setAdditionalInformation("Test information");
		contactPerson.getAddress().setPostalCode("1234");
		getPersonFacade().save(contactPerson);

		visit.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().save(visit);

		ImmunizationDto immunization = creator.createImmunization(
			contact.getDisease(),
			contact.getPerson(),
			contact.getReportingUser(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf,
			DateHelper.subtractDays(new Date(), 10),
			DateHelper.subtractDays(new Date(), 5),
			DateHelper.subtractDays(new Date(), 1),
			null);
		creator.createImmunization(
			contact.getDisease(),
			contact.getPerson(),
			contact.getReportingUser(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf,
			DateHelper.subtractDays(new Date(), 8),
			DateHelper.subtractDays(new Date(), 7),
			null,
			null);
		VaccinationDto firstVaccination = creator.createVaccination(
			contact.getReportingUser(),
			immunization.toReference(),
			HealthConditionsDto.build(),
			DateHelper.subtractDays(new Date(), 7),
			Vaccine.OXFORD_ASTRA_ZENECA,
			VaccineManufacturer.ASTRA_ZENECA);

		VaccinationDto secondVaccination = creator.createVaccination(
			contact.getReportingUser(),
			immunization.toReference(),
			HealthConditionsDto.build(),
			DateHelper.subtractDays(new Date(), 4),
			Vaccine.MRNA_1273,
			VaccineManufacturer.MODERNA);

		VaccinationDto thirdVaccination = creator.createVaccination(
			contact.getReportingUser(),
			immunization.toReference(),
			HealthConditionsDto.build(),
			new Date(),
			Vaccine.COMIRNATY,
			VaccineManufacturer.BIONTECH_PFIZER);

		List<ContactExportDto> results;
		results = getContactFacade().getExportList(null, Collections.emptySet(), 0, 100, null, Language.EN);

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
		assertEquals(VaccinationStatus.VACCINATED, exportDto.getVaccinationStatus());
		assertEquals(firstVaccination.getVaccinationDate(), exportDto.getFirstVaccinationDate());
		assertEquals(secondVaccination.getVaccineName(), exportDto.getVaccineName());
		assertEquals(secondVaccination.getVaccinationDate(), exportDto.getLastVaccinationDate());
		assertEquals(secondVaccination.getVaccinationInfoSource(), exportDto.getVaccinationInfoSource());
		assertEquals(secondVaccination.getVaccineInn(), exportDto.getVaccineInn());
		assertEquals(secondVaccination.getVaccineBatchNumber(), exportDto.getVaccineBatchNumber());
		assertEquals(secondVaccination.getVaccineAtcCode(), exportDto.getVaccineAtcCode());
		assertEquals(secondVaccination.getVaccineDose(), exportDto.getNumberOfDoses());

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
		assertTrue(exportDto.getEventCount().equals(0L));

		// one Contact with 2 Events
		UserReferenceDto reportingUser = new UserReferenceDto(user.getUuid());
		EventDto event1 = creator.createEvent(reportingUser, DateHelper.subtractDays(new Date(), 1));
		EventDto event2 = creator.createEvent(reportingUser, new Date());
		creator.createEventParticipant(new EventReferenceDto(event2.getUuid()), contactPerson, reportingUser);
		creator.createEventParticipant(new EventReferenceDto(event1.getUuid()), contactPerson, reportingUser);

		results = getContactFacade().getExportList(null, Collections.emptySet(), 0, 100, null, Language.EN);
		assertEquals(results.size(), 1);
		{
			ContactExportDto dto = results.get(0);
			assertEquals(dto.getLatestEventId(), event2.getUuid());
			assertEquals(dto.getLatestEventTitle(), event2.getEventTitle());
			assertTrue(dto.getEventCount().equals(2L));
		}
	}

	@Test
	public void testGetExportListWithoutRelevantVaccinations() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = useNationalAdminLogin();

		CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson("Case", "Person").toReference(), rdcf);
		ContactDto contact = creator.createContact(user.toReference(), creator.createPerson("Contact", "Person").toReference(), caze, rdcf);

		PersonDto contactPerson = getPersonFacade().getByUuid(contact.getPerson().getUuid());
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
		getContactFacade().save(contact);

		contactPerson.getAddress().setRegion(new RegionReferenceDto(rdcf.region.getUuid(), null, null));
		contactPerson.getAddress().setDistrict(new DistrictReferenceDto(rdcf.district.getUuid(), null, null));
		contactPerson.getAddress().setCity("City");
		contactPerson.getAddress().setStreet("Test street");
		contactPerson.getAddress().setHouseNumber("Test number");
		contactPerson.getAddress().setAdditionalInformation("Test information");
		contactPerson.getAddress().setPostalCode("1234");
		getPersonFacade().save(contactPerson);

		visit.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().save(visit);

		ImmunizationDto immunization = creator.createImmunization(
			contact.getDisease(),
			contact.getPerson(),
			contact.getReportingUser(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf,
			DateHelper.subtractDays(new Date(), 10),
			DateHelper.subtractDays(new Date(), 5),
			DateHelper.subtractDays(new Date(), 1),
			null);

		VaccinationDto vaccination = creator.createVaccinationWithDetails(
			caze.getReportingUser(),
			immunization.toReference(),
			HealthConditionsDto.build(),
			DateHelper.addDays(new Date(), 1),
			Vaccine.MRNA_1273,
			VaccineManufacturer.MODERNA,
			VaccinationInfoSource.UNKNOWN,
			"inn2",
			"456",
			"code456",
			"2");

		List<ContactExportDto> results;
		results = getContactFacade().getExportList(null, Collections.emptySet(), 0, 100, null, Language.EN);

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
		assertNull(exportDto.getFirstVaccinationDate());
		assertNull(exportDto.getVaccineName());
		assertNull(exportDto.getLastVaccinationDate());
		assertNull(exportDto.getVaccinationInfoSource());
		assertNull(exportDto.getVaccineInn());
		assertNull(exportDto.getVaccineBatchNumber());
		assertNull(exportDto.getVaccineAtcCode());
		assertEquals(exportDto.getNumberOfDoses(), "");
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
		assertTrue(exportDto.getEventCount().equals(0L));

		// one Contact with 2 Events
		UserReferenceDto reportingUser = new UserReferenceDto(user.getUuid());
		EventDto event1 = creator.createEvent(reportingUser, DateHelper.subtractDays(new Date(), 1));
		EventDto event2 = creator.createEvent(reportingUser, new Date());
		creator.createEventParticipant(new EventReferenceDto(event2.getUuid()), contactPerson, reportingUser);
		creator.createEventParticipant(new EventReferenceDto(event1.getUuid()), contactPerson, reportingUser);

		results = getContactFacade().getExportList(null, Collections.emptySet(), 0, 100, null, Language.EN);
		assertEquals(results.size(), 1);
		{
			ContactExportDto dto = results.get(0);
			assertEquals(dto.getLatestEventId(), event2.getUuid());
			assertEquals(dto.getLatestEventTitle(), event2.getEventTitle());
			assertTrue(dto.getEventCount().equals(2L));
		}
	}

	@Test
	public void testGetVisitSummaryExportList() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		getVisitFacade().save(visit11);
		VisitDto visit12 = creator.createVisit(
			caze.getDisease(),
			contactPerson.toReference(),
			DateHelper.subtractDays(new Date(), 1),
			VisitStatus.COOPERATIVE,
			VisitOrigin.USER);
		visit12.getSymptoms().setChestPain(SymptomState.YES);
		getVisitFacade().save(visit12);
		PersonDto contactPersonWithoutFollowUp = creator.createPerson();
		creator.createContact(user.toReference(), contactPersonWithoutFollowUp.toReference());

		PersonDto contactPerson2 = creator.createPerson("Contact2", "Person2");
		ContactDto contact3 =
			creator.createContact(user.toReference(), user.toReference(), contactPerson2.toReference(), caze, new Date(), null, null);
		VisitDto visit21 =
			creator.createVisit(caze.getDisease(), contactPerson2.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit21.getSymptoms().setBackache(SymptomState.YES);
		getVisitFacade().save(visit21);

		final List<VisitSummaryExportDto> results = getContactFacade().getVisitSummaryExportList(null, Collections.emptySet(), 0, 100, Language.EN);
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

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		getVisitFacade().save(visit);

		PersonDto contactPerson2 = creator.createPerson("Contact2", "Person2");
		creator.createContact(user.toReference(), user.toReference(), contactPerson2.toReference(), caze, new Date(), new Date(), null);
		VisitDto visit21 =
			creator.createVisit(caze.getDisease(), contactPerson2.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit21.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().save(visit21);
		VisitDto visit22 =
			creator.createVisit(caze.getDisease(), contactPerson2.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit22.getSymptoms().setAgitation(SymptomState.YES);
		getVisitFacade().save(visit22);

		PersonDto contactPerson3 = creator.createPerson("Contact3", "Person3");
		creator.createContact(user.toReference(), user.toReference(), contactPerson3.toReference(), caze, new Date(), new Date(), null);
		for (int i = 0; i < 10; i++) {
			creator.createVisit(caze.getDisease(), contactPerson3.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		}

		assertEquals(10, getContactFacade().countMaximumFollowUpDays(null));
	}

	@Test
	public void testArchiveOrDearchiveContact() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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

		// getAllActiveContacts and getAllUuids should return length 1
		assertEquals(1, getContactFacade().getAllAfter(null).size());
		assertEquals(1, getContactFacade().getAllActiveUuids().size());
		assertEquals(1, getVisitFacade().getAllActiveVisitsAfter(null).size());
		assertEquals(1, getVisitFacade().getAllActiveUuids().size());

		getCaseFacade().archive(caze.getUuid(), null, true);

		// getAllActiveContacts and getAllUuids should return length 0
		assertEquals(0, getContactFacade().getAllAfter(null).size());
		assertEquals(0, getContactFacade().getAllActiveUuids().size());
		assertEquals(0, getVisitFacade().getAllActiveVisitsAfter(null).size());
		assertEquals(0, getVisitFacade().getAllActiveUuids().size());

		getCaseFacade().dearchive(Collections.singletonList(caze.getUuid()), null, true);

		// getAllActiveContacts and getAllUuids should return length 1
		assertEquals(1, getContactFacade().getAllAfter(null).size());
		assertEquals(1, getContactFacade().getAllActiveUuids().size());
		assertEquals(1, getVisitFacade().getAllActiveVisitsAfter(null).size());
		assertEquals(1, getVisitFacade().getAllActiveUuids().size());
	}

	@Test
	public void testUpdateContactVisitAssociations() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		PersonDto person = creator.createPerson();
		VisitDto visit = creator.createVisit(Disease.EVD, person.toReference());
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());
		Contact contactEntity = getContactService().getByUuid(contact.getUuid());
		Visit visitEntity = getVisitService().getByUuid(visit.getUuid());

		// Saved contact should have visit association
		assertThat(getVisitService().getAllByContact(contactEntity), hasSize(1));

		// Updating the contact but not changing the report date or last contact date should not alter the association
		contact.setDescription("Description");
		getContactFacade().save(contact);

		assertThat(getVisitService().getAllByContact(contactEntity), hasSize(1));

		// Changing the report date to a value beyond the threshold should remove the association
		contact.setReportDateTime(DateHelper.addDays(visit.getVisitDateTime(), FollowUpLogic.ALLOWED_DATE_OFFSET + 20));
		getContactFacade().save(contact);

		assertThat(getVisitService().getAllByContact(contactEntity), empty());

		// Changing the report date back to a value in the threshold should re-add the association
		contact.setReportDateTime(new Date());
		getContactFacade().save(contact);

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
		getContactFacade().save(contact2);

		assertThat(getContactService().getAllByVisit(visitEntity), hasSize(1));
	}

	@Test
	public void testSearchContactsWithExtendedQuarantine() {
		RDCF rdcf = creator.createRDCF();
		ContactDto contact = creator.createContact(creator.createSurveillanceOfficer(rdcf).toReference(), creator.createPerson().toReference());
		contact.setQuarantineExtended(true);
		getContactFacade().save(contact);

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
		ContactDto contact = creator.createContact(creator.createSurveillanceOfficer(rdcf).toReference(), creator.createPerson().toReference());
		contact.setQuarantineReduced(true);
		getContactFacade().save(contact);

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

		ContactDto contact = ContactDto.build();
		contact.setReportDateTime(new Date());
		contact.setReportingUser(creator.createSurveillanceOfficer(rdcf).toReference());
		contact.setDisease(Disease.CORONAVIRUS);
		contact.setPerson(creator.createPerson().toReference());
		contact.setRegion(rdcf.region);
		contact.setDistrict(rdcf.district);
		contact.setHealthConditions(new HealthConditionsDto());

		ContactDto savedContact = getContactFacade().save(contact);

		assertThat(savedContact.getUuid(), not(emptyOrNullString()));
		assertThat(savedContact.getHealthConditions().getUuid(), not(emptyOrNullString()));
	}

	@Test
	public void testGetContactsByPersonUuids() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		PersonReferenceDto person1 = creator.createPerson().toReference();
		ContactDto contact1 = getContactFacade().save(creator.createContact(user.toReference(), person1));

		PersonReferenceDto person2 = creator.createPerson().toReference();
		ContactDto contact2 = getContactFacade().save(creator.createContact(user.toReference(), person2));

		List<ContactDto> contactsByPerson = getContactFacade().getByPersonUuids(Collections.singletonList(person1.getUuid()));

		assertEquals(1, contactsByPerson.size());
		assertEquals(contact1.getUuid(), contactsByPerson.get(0).getUuid());
		assertNotEquals(contact2.getUuid(), contactsByPerson.get(0).getUuid());

		contactsByPerson = getContactFacade().getByPersonUuids(Arrays.asList(person1.getUuid(), person2.getUuid()));

		assertEquals(2, contactsByPerson.size());
		assertEquals(contact1.getUuid(), contactsByPerson.get(0).getUuid());
		assertEquals(contact2.getUuid(), contactsByPerson.get(1).getUuid());
	}

	@Test
	public void testMergeContactDoesNotDuplicateSystemComment() throws IOException {

		UserDto leadUser = creator.createUser("", "", "", "First", "User");
		UserDto otherUser = creator.createUser("", "", "", "Some", "User");
		useNationalUserLogin();

		UserReferenceDto leadUserReference = new UserReferenceDto(leadUser.getUuid());
		PersonDto leadPerson = creator.createPerson("Alex", "Miller");
		PersonReferenceDto leadPersonReference = new PersonReferenceDto(leadPerson.getUuid());
		RDCF leadRdcf = creator.createRDCF();
		CaseDataDto sourceCase = creator.createCase(
			leadUserReference,
			leadPersonReference,
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			leadRdcf);
		ContactDto leadContact = creator.createContact(
			leadUserReference,
			leadUserReference,
			leadPersonReference,
			sourceCase,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			leadRdcf);
		getContactFacade().save(leadContact);

		// Create otherContact
		UserReferenceDto otherUserReference = new UserReferenceDto(otherUser.getUuid());
		PersonDto otherPerson = creator.createPerson("Max", "Smith");
		PersonReferenceDto otherPersonReference = new PersonReferenceDto(otherPerson.getUuid());
		RDCF otherRdcf = creator.createRDCF();
		ContactDto otherContact = creator.createContact(
			otherUserReference,
			otherUserReference,
			otherPersonReference,
			sourceCase,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			otherRdcf);
		ContactReferenceDto otherContactReference = getContactFacade().getReferenceByUuid(otherContact.getUuid());
		ContactDto contact =
			creator.createContact(otherUserReference, otherUserReference, otherPersonReference, sourceCase, new Date(), new Date(), null);
		Region region = creator.createRegion("");
		District district = creator.createDistrict("", region);
		Facility facility = creator.createFacility("", region, district, creator.createCommunity("", district));

		CaseDataDto resultingCase = getCaseFacade().save(
			creator.createCase(
				otherUserReference,
				otherPersonReference,
				Disease.CORONAVIRUS,
				CaseClassification.CONFIRMED_NO_SYMPTOMS,
				InvestigationStatus.DONE,
				new Date(),
				otherRdcf));
		otherContact.setResultingCase(resultingCase.toReference());
		getContactFacade().save(otherContact);

		getContactFacade().merge(leadContact.getUuid(), otherContact.getUuid());

		ContactDto mergedContact = getContactFacade().getByUuid(leadContact.getUuid());
		assertEquals(I18nProperties.getString(Strings.messageSystemFollowUpCanceled), mergedContact.getFollowUpComment());
	}

	@Test
	public void testMergeContact() throws IOException {

		UserDto leadUser = creator.createUser("", "", "", "First", "User");
		UserDto otherUser = creator.createUser("", "", "", "Some", "User");

		useNationalUserLogin();
		// 1. Create

		// Create leadContact
		UserReferenceDto leadUserReference = new UserReferenceDto(leadUser.getUuid());
		PersonDto leadPerson = creator.createPerson("Alex", "Miller");
		PersonContactDetailDto leadContactDetail =
			creator.createPersonContactDetail(leadPerson.toReference(), true, PersonContactDetailType.PHONE, "123");
		leadPerson.setPersonContactDetails(Collections.singletonList(leadContactDetail));
		getPersonFacade().save(leadPerson);
		PersonReferenceDto leadPersonReference = new PersonReferenceDto(leadPerson.getUuid());
		RDCF leadRdcf = creator.createRDCF();
		CaseDataDto sourceCase = creator.createCase(
			leadUserReference,
			leadPersonReference,
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			leadRdcf);
		ContactDto leadContact = creator.createContact(
			leadUserReference,
			leadUserReference,
			leadPersonReference,
			sourceCase,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			leadRdcf,
			(c) -> {
				c.setAdditionalDetails("Test additional details");
				c.setFollowUpComment("Test followup comment");
			});
		getContactFacade().save(leadContact);
		VisitDto leadVisit = creator.createVisit(leadContact.getDisease(), leadContact.getPerson(), leadContact.getReportDateTime());
		getVisitFacade().save(leadVisit);

		// Create otherContact
		UserReferenceDto otherUserReference = new UserReferenceDto(otherUser.getUuid());
		PersonDto otherPerson = creator.createPerson("Max", "Smith");
		PersonContactDetailDto otherContactDetail =
			creator.createPersonContactDetail(otherPerson.toReference(), true, PersonContactDetailType.PHONE, "456");
		otherPerson.setPersonContactDetails(Collections.singletonList(otherContactDetail));
		otherPerson.setBirthWeight(2);
		getPersonFacade().save(otherPerson);
		PersonReferenceDto otherPersonReference = new PersonReferenceDto(otherPerson.getUuid());
		RDCF otherRdcf = creator.createRDCF();
		ContactDto otherContact = creator.createContact(
			otherUserReference,
			otherUserReference,
			otherPersonReference,
			sourceCase,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			otherRdcf,
			(c) -> {
				c.setAdditionalDetails("Test other additional details");
				c.setFollowUpComment("Test other followup comment");
			});
		ContactReferenceDto otherContactReference = getContactFacade().getReferenceByUuid(otherContact.getUuid());
		ContactDto contact =
			creator.createContact(otherUserReference, otherUserReference, otherPersonReference, sourceCase, new Date(), new Date(), null);
		Region region = creator.createRegion("");
		District district = creator.createDistrict("", region);
		Facility facility = creator.createFacility("", region, district, creator.createCommunity("", district));
		SampleDto sample =
			creator.createSample(otherContactReference, otherUserReference, getFacilityFacade().getReferenceByUuid(facility.getUuid()), null);
		TaskDto task = creator.createTask(
			TaskContext.CONTACT,
			TaskType.CONTACT_INVESTIGATION,
			TaskStatus.PENDING,
			null,
			otherContactReference,
			new EventReferenceDto(),
			null,
			new Date(),
			otherUserReference);
		getContactFacade().save(otherContact);
		VisitDto otherVisit = creator.createVisit(otherContact.getDisease(), otherContact.getPerson(), otherContact.getReportDateTime());
		otherVisit.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().save(otherVisit);

		byte[] contentAsBytes =
			("%PDF-1.0\n1 0 obj<</Type/Catalog/Pages " + "2 0 R>>endobj 2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj 3 0 obj<</Ty"
				+ "pe/Page/MediaBox[0 0 3 3]>>endobj\nxref\n0 4\n0000000000 65535 f\n000000001"
				+ "0 00000 n\n0000000053 00000 n\n0000000102 00000 n\ntrailer<</Size 4/Root 1 " + "0 R>>\nstartxref\n149\n%EOF").getBytes();

		DocumentDto document = creator.createDocument(
			leadUserReference,
			"document.pdf",
			"application/pdf",
			42L,
			DocumentRelatedEntityType.CONTACT,
			leadContact.getUuid(),
			contentAsBytes);
		DocumentDto otherDocument = creator.createDocument(
			leadUserReference,
			"other_document.pdf",
			"application/pdf",
			42L,
			DocumentRelatedEntityType.CONTACT,
			otherContact.getUuid(),
			contentAsBytes);

		// 2. Merge

		getContactFacade().merge(leadContact.getUuid(), otherContact.getUuid());

		// 3. Test

		ContactDto mergedContact = getContactFacade().getByUuid(leadContact.getUuid());

		PersonDto mergedPerson = getPersonFacade().getByUuid(mergedContact.getPerson().getUuid());

		// Check no values
		assertNull(mergedPerson.getBirthdateDD());

		// Check 'lead and other have different values'
		assertEquals(leadContact.getPerson().getFirstName(), mergedPerson.getFirstName());

		// Check 'lead has value, other has not'
		assertEquals(leadContact.getPerson().getLastName(), mergedPerson.getLastName());

		// Check 'lead has no value, other has'
		assertEquals(otherPerson.getBirthWeight(), mergedPerson.getBirthWeight());

		// Check merge comments
		assertEquals("Test additional details Test other additional details", mergedContact.getAdditionalDetails());
		assertEquals("Test followup comment Test other followup comment", mergedContact.getFollowUpComment());

		// 4. Test Reference Changes
		// 4.1 Samples
		List<String> sampleUuids = new ArrayList<String>();
		sampleUuids.add(sample.getUuid());
		assertEquals(leadContact.getUuid(), getSampleFacade().getByUuids(sampleUuids).get(0).getAssociatedContact().getUuid());

		// 4.2 Tasks
		List<String> taskUuids = new ArrayList<String>();
		taskUuids.add(task.getUuid());
		assertEquals(leadContact.getUuid(), getTaskFacade().getByUuids(taskUuids).get(0).getContact().getUuid());

		// 4.3 Visits;
		List<String> mergedVisits = getVisitFacade().getIndexList(new VisitCriteria().contact(mergedContact.toReference()), null, null, null)
			.stream()
			.map(VisitIndexDto::getUuid)
			.collect(Collectors.toList());
		assertEquals(2, mergedVisits.size());
		assertTrue(mergedVisits.contains(leadVisit.getUuid()));
		assertTrue(mergedVisits.contains(otherVisit.getUuid()));

		// 5 Documents
		List<DocumentDto> mergedDocuments = getDocumentFacade().getDocumentsRelatedToEntity(DocumentRelatedEntityType.CONTACT, leadContact.getUuid());

		assertEquals(mergedDocuments.size(), 2);
		List<String> documentUuids = mergedDocuments.stream().map(DocumentDto::getUuid).collect(Collectors.toList());
		assertTrue(documentUuids.contains(document.getUuid()));
		assertTrue(documentUuids.contains(otherDocument.getUuid()));

	}

	@Test
	public void testGetContactUsersWithoutUsersLimitedToOthersDiseses() {
		RDCF rdcf = creator.createRDCF();
		UserDto limitedCovidNationalUser = creator.createUser(
			rdcf,
			"Limited Disease Covid",
			"National User",
			Disease.CORONAVIRUS,
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		UserDto limitedDengueNationalUser = creator
			.createUser(rdcf, "Limited Disease Dengue", "National User", Disease.DENGUE, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		UserDto user = useNationalUserLogin();

		PersonDto personDto = creator.createPerson();
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			personDto.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			null);
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), personDto.toReference(), caze, new Date(), new Date(), Disease.CORONAVIRUS);

		List<UserReferenceDto> userReferenceDtos = getUserFacade().getUsersHavingContactInJurisdiction(contact.toReference());
		assertNotNull(userReferenceDtos);
		assertTrue(userReferenceDtos.contains(user));
		assertTrue(userReferenceDtos.contains(limitedCovidNationalUser));
		assertFalse(userReferenceDtos.contains(limitedDengueNationalUser));
	}

	@Test
	public void searchContactsByPersonPhone() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto personWithPhone = creator.createPerson("personWithPhone", "test");
		PersonDto personWithoutPhone = creator.createPerson("personWithoutPhone", "test");

		PersonContactDetailDto primaryPhone =
			creator.createPersonContactDetail(personWithPhone.toReference(), true, PersonContactDetailType.PHONE, "111222333");
		PersonContactDetailDto secondaryPhone =
			creator.createPersonContactDetail(personWithoutPhone.toReference(), false, PersonContactDetailType.PHONE, "444555666");

		personWithPhone.getPersonContactDetails().add(primaryPhone);
		personWithPhone.getPersonContactDetails().add(secondaryPhone);
		getPersonFacade().save(personWithPhone);

		ContactDto contactDto1 = creator.createContact(rdcf, user.toReference(), personWithPhone.toReference());
		ContactDto contactDto2 = creator.createContact(rdcf, user.toReference(), personWithoutPhone.toReference());

		ContactCriteria contactCriteria = new ContactCriteria();
		List<ContactIndexDetailedDto> contactIndexDetailedDtos = getContactFacade().getIndexDetailedList(contactCriteria, 0, 100, null);
		assertEquals(2, contactIndexDetailedDtos.size());
		List<String> uuids = contactIndexDetailedDtos.stream().map(c -> c.getUuid()).collect(Collectors.toList());
		assertTrue(uuids.contains(contactDto1.getUuid()));
		assertTrue(uuids.contains(contactDto2.getUuid()));

		contactCriteria.setPersonLike("111222333");
		contactIndexDetailedDtos = getContactFacade().getIndexDetailedList(contactCriteria, 0, 100, null);
		assertEquals(1, contactIndexDetailedDtos.size());
		assertEquals(contactDto1.getUuid(), contactIndexDetailedDtos.get(0).getUuid());

		contactCriteria.setPersonLike("444555666");
		contactIndexDetailedDtos = getContactFacade().getIndexDetailedList(contactCriteria, 0, 100, null);
		assertEquals(0, contactIndexDetailedDtos.size());
	}

	@Test
	public void searchContactsByPersonEmail() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto personWithEmail = creator.createPerson("personWithEmail", "test");
		PersonDto personWithoutEmail = creator.createPerson("personWithoutEmail", "test");

		PersonContactDetailDto primaryEmail =
			creator.createPersonContactDetail(personWithEmail.toReference(), true, PersonContactDetailType.EMAIL, "test1@email.com");
		PersonContactDetailDto secondaryEmail =
			creator.createPersonContactDetail(personWithoutEmail.toReference(), false, PersonContactDetailType.EMAIL, "test2@email.com");

		personWithEmail.getPersonContactDetails().add(primaryEmail);
		personWithEmail.getPersonContactDetails().add(secondaryEmail);
		getPersonFacade().save(personWithEmail);

		ContactDto contactDto1 = creator.createContact(rdcf, user.toReference(), personWithEmail.toReference());
		ContactDto contactDto2 = creator.createContact(rdcf, user.toReference(), personWithoutEmail.toReference());

		ContactCriteria contactCriteria = new ContactCriteria();
		List<ContactIndexDetailedDto> contactIndexDetailedDtos = getContactFacade().getIndexDetailedList(contactCriteria, 0, 100, null);
		assertEquals(2, contactIndexDetailedDtos.size());
		List<String> uuids = contactIndexDetailedDtos.stream().map(c -> c.getUuid()).collect(Collectors.toList());
		assertTrue(uuids.contains(contactDto1.getUuid()));
		assertTrue(uuids.contains(contactDto2.getUuid()));

		contactCriteria.setPersonLike("test1@email.com");
		contactIndexDetailedDtos = getContactFacade().getIndexDetailedList(contactCriteria, 0, 100, null);
		assertEquals(1, contactIndexDetailedDtos.size());
		assertEquals(contactDto1.getUuid(), contactIndexDetailedDtos.get(0).getUuid());

		contactCriteria.setPersonLike("test2@email.com");
		contactIndexDetailedDtos = getContactFacade().getIndexDetailedList(contactCriteria, 0, 100, null);
		assertEquals(0, contactIndexDetailedDtos.size());
	}

	@Test
	public void searchContactsByPersonOtherDetail() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto personWithOtherDetail = creator.createPerson("personWithOtherDetail", "test");
		PersonDto personWithoutOtherDetail = creator.createPerson("personWithoutOtherDetail", "test");

		PersonContactDetailDto primarOtherDetail =
			creator.createPersonContactDetail(personWithOtherDetail.toReference(), true, PersonContactDetailType.OTHER, "detail1");
		PersonContactDetailDto secondaryOtherDetail =
			creator.createPersonContactDetail(personWithoutOtherDetail.toReference(), false, PersonContactDetailType.OTHER, "detail2");

		personWithOtherDetail.getPersonContactDetails().add(primarOtherDetail);
		personWithOtherDetail.getPersonContactDetails().add(secondaryOtherDetail);
		getPersonFacade().save(personWithOtherDetail);

		ContactDto contactDto1 = creator.createContact(rdcf, user.toReference(), personWithOtherDetail.toReference());
		ContactDto contactDto2 = creator.createContact(rdcf, user.toReference(), personWithoutOtherDetail.toReference());

		ContactCriteria contactCriteria = new ContactCriteria();
		List<ContactIndexDetailedDto> contactIndexDetailedDtos = getContactFacade().getIndexDetailedList(contactCriteria, 0, 100, null);
		assertEquals(2, contactIndexDetailedDtos.size());
		List<String> uuids = contactIndexDetailedDtos.stream().map(c -> c.getUuid()).collect(Collectors.toList());
		assertTrue(uuids.contains(contactDto1.getUuid()));
		assertTrue(uuids.contains(contactDto2.getUuid()));

		contactCriteria.setPersonLike("detail1");
		contactIndexDetailedDtos = getContactFacade().getIndexDetailedList(contactCriteria, 0, 100, null);
		assertEquals(1, contactIndexDetailedDtos.size());
		assertEquals(contactDto1.getUuid(), contactIndexDetailedDtos.get(0).getUuid());

		contactCriteria.setPersonLike("detail2");
		contactIndexDetailedDtos = getContactFacade().getIndexDetailedList(contactCriteria, 0, 100, null);
		assertEquals(0, contactIndexDetailedDtos.size());
	}

	@Test
	public void testGetContactsByPersonNationalHealthId() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		PersonReferenceDto person1 = creator.createPerson().toReference();
		PersonDto personDto1 = getPersonFacade().getByUuid(person1.getUuid());
		personDto1.setNationalHealthId("firstNationalId");
		getPersonFacade().save(personDto1);
		ContactDto contact1 = getContactFacade().save(creator.createContact(user.toReference(), person1));

		PersonReferenceDto person2 = creator.createPerson().toReference();
		PersonDto personDto2 = getPersonFacade().getByUuid(person2.getUuid());
		personDto2.setNationalHealthId("secondNationalId");
		getPersonFacade().save(personDto2);
		getContactFacade().save(creator.createContact(user.toReference(), person2));

		PersonReferenceDto person3 = creator.createPerson().toReference();
		PersonDto personDto3 = getPersonFacade().getByUuid(person3.getUuid());
		personDto3.setNationalHealthId("third");
		getPersonFacade().save(personDto3);
		getContactFacade().save(creator.createContact(user.toReference(), person3));

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria.setPersonLike("firstNationalId");

		List<ContactIndexDto> contactIndexDtos1 = getContactFacade().getIndexList(contactCriteria, 0, 100, null);
		assertEquals(1, contactIndexDtos1.size());
		assertEquals(contact1.getUuid(), contactIndexDtos1.get(0).getUuid());

		contactCriteria.setPersonLike("National");
		List<ContactIndexDto> contactIndexDtosNational = getContactFacade().getIndexList(contactCriteria, 0, 100, null);
		assertEquals(2, contactIndexDtosNational.size());

		contactCriteria.setPersonLike(null);
		List<ContactIndexDto> contactIndexDtosAll = getContactFacade().getIndexList(contactCriteria, 0, 100, null);
		assertEquals(3, contactIndexDtosAll.size());
	}

	@Test
	public void testContactsForDashboard() {

		Date fromDate = UtilDate.from(LocalDate.now().minusYears(1));
		Date toDate = UtilDate.now();

		//search when there is no data in DB
		List<DashboardContactDto> dashboardContactDtos = getContactFacade().getContactsForDashboard(null, null, null, fromDate, toDate);
		assertEquals(0, dashboardContactDtos.size());

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();

		creator.createContact(rdcf, user.toReference(), person.toReference());

		//search when we have a contact and no visit in DB
		dashboardContactDtos = getContactFacade().getContactsForDashboard(null, null, null, fromDate, toDate);
		assertEquals(1, dashboardContactDtos.size());
		assertEquals(null, dashboardContactDtos.get(0).getLastVisitStatus());

		creator.createVisit(Disease.EVD, person.toReference());
		//search when we have a contact and a visit in DB
		dashboardContactDtos = getContactFacade().getContactsForDashboard(null, null, null, fromDate, toDate);
		assertEquals(1, dashboardContactDtos.size());
		assertEquals(VisitStatus.COOPERATIVE, dashboardContactDtos.get(0).getLastVisitStatus());
	}
}
