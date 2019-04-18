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
package de.symeda.sormas.backend.contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.util.DateHelper8;

public class ContactFacadeEjbTest extends AbstractBeanTest  {

	@Test
	public void testUpdateFollowUpUntilAndStatus() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid()
				,"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());

		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));
		
		VisitDto visit = creator.createVisit(caze.getDisease(), contactPerson.toReference(), DateUtils.addDays(new Date(), 21), VisitStatus.UNAVAILABLE);
				
		// should now be one day more
		contact = getContactFacade().getContactByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21+1), DateHelper8.toLocalDate(contact.getFollowUpUntil()));
		
		visit.setVisitStatus(VisitStatus.COOPERATIVE);
		visit = getVisitFacade().saveVisit(visit);
		
		// and now the old date again - and done
		contact =  getContactFacade().getContactByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.COMPLETED, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));
	}
	
	@Test
	public void testUpdateContactStatus() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid()
				,"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		Date contactDate = new Date();
		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), contactDate, contactDate);
		
		assertEquals(ContactStatus.ACTIVE, contact.getContactStatus());
		assertNull(contact.getResultingCase());
		
		// drop
		contact.setContactClassification(ContactClassification.NO_CONTACT);
		contact = getContactFacade().saveContact(contact);
		assertEquals(ContactStatus.DROPPED, contact.getContactStatus());

		// add result case
		CaseDataDto resultingCaze = creator.createCase(user.toReference(), contactPerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, contactDate, rdcf);
		contact.setContactClassification(ContactClassification.CONFIRMED);
		contact.setResultingCase(getCaseFacade().getReferenceByUuid(resultingCaze.getUuid()));
		contact = getContactFacade().saveContact(contact);
		assertEquals(ContactStatus.CONVERTED, contact.getContactStatus());
	}
	
	@Test
	public void testGenerateContactFollowUpTasks() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());

		 getContactFacade().generateContactFollowUpTasks();
		
		// task should have been generated
		List<TaskDto> tasks = getTaskFacade().getAllByContact(contact.toReference());
		assertEquals(1, tasks.size());
		TaskDto task = tasks.get(0);
		assertEquals(TaskType.CONTACT_FOLLOW_UP, task.getTaskType());
		assertEquals(TaskStatus.PENDING, task.getTaskStatus());
		assertEquals(LocalDate.now(), DateHelper8.toLocalDate(task.getDueDate()));

		// task should not be generated multiple times 
		 getContactFacade().generateContactFollowUpTasks();
		tasks = getTaskFacade().getAllByContact(contact.toReference());
		assertEquals(1, tasks.size());
	}
	
	@Test
	public void testMapContactListCreation() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());
		MapCaseDto mapCaseDto = new MapCaseDto(caze.getUuid(), caze.getReportDate(), caze.getCaseClassification(), caze.getDisease(), 
				caze.getPerson().getUuid(), cazePerson.getFirstName(), cazePerson.getLastName(),
				caze.getHealthFacility().getUuid(), 0d, 0d,
				caze.getReportLat(), caze.getReportLon(), caze.getReportLat(), caze.getReportLon());
		
		List<MapContactDto> mapContactDtos =  getContactFacade().getContactsForMap(caze.getRegion(), caze.getDistrict(), caze.getDisease(), DateHelper.subtractDays(new Date(),  1), DateHelper.addDays(new Date(), 1), user.getUuid(), Arrays.asList(mapCaseDto));
		
		// List should have one entry
		assertEquals(1, mapContactDtos.size());
	}
	
	@Test
	public void testContactDeletion() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());
		VisitDto visit = creator.createVisit(caze.getDisease(), contactPerson.toReference(), DateUtils.addDays(new Date(), 21), VisitStatus.UNAVAILABLE);
		TaskDto task = creator.createTask(TaskContext.CONTACT, TaskType.CONTACT_INVESTIGATION, TaskStatus.PENDING, null, contact.toReference(), null, new Date(), user.toReference());
		
		// Database should contain the created contact, visit and task
		assertNotNull(getContactFacade().getContactByUuid(contact.getUuid()));
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));
		assertNotNull(getVisitFacade().getVisitByUuid(visit.getUuid()));
		
		 getContactFacade().deleteContact(contact.toReference(), adminUuid);
		
		// Database should not contain the deleted contact, visit and task
		assertNull(getContactFacade().getContactByUuid(contact.getUuid()));
		assertNull(getTaskFacade().getByUuid(task.getUuid()));
		assertNull(getVisitFacade().getVisitByUuid(visit.getUuid()));
	}
	
	@Test
	public void testGetIndexList() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		String userUuid = user.getUuid();
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());
		
		// Database should contain one contact, associated visit and task
		assertEquals(1, getContactFacade().getIndexList(userUuid, null, 0, 100, null).size());
	}

	@Test
	public void testGetExportList() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		String userUuid = user.getUuid();
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());
		VisitDto visit = creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE);
		
		contactPerson.getAddress().setCity("City");
		getPersonFacade().savePerson(contactPerson);
		
		visit.getSymptoms().setAbdominalPain(SymptomState.YES);
		getVisitFacade().saveVisit(visit);
		
		List<ContactExportDto> results = getContactFacade().getExportList(userUuid, null, 0, 100);
		
		// Database should contain one contact, associated visit and task
		assertEquals(1, results.size());
		
		// Make sure that everything that is added retrospectively (address, last cooperative visit date and symptoms) is present
		ContactExportDto exportDto = results.get(0);
		assertTrue(StringUtils.isNotEmpty(exportDto.getAddress()));
		assertNotNull(exportDto.getLastCooperativeVisitDate());
		assertTrue(StringUtils.isNotEmpty(exportDto.getLastCooperativeVisitSymptoms()));
		assertEquals(exportDto.getLastCooperativeVisitSymptomatic(), YesNoUnknown.YES);
	}

	@Test
	public void testArchiveOrDearchiveContact() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());
		creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE);
		
		// getAllActiveContacts and getAllUuids should return length 1
		assertEquals(1, getContactFacade().getAllActiveContactsAfter(null, user.getUuid()).size());
		assertEquals(1, getContactFacade().getAllActiveUuids(user.getUuid()).size());
		assertEquals(1, getVisitFacade().getAllActiveVisitsAfter(null, user.getUuid()).size());
		assertEquals(1, getVisitFacade().getAllActiveUuids(user.getUuid()).size());
		
		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), true);
		
		// getAllActiveContacts and getAllUuids should return length 0
		assertEquals(0, getContactFacade().getAllActiveContactsAfter(null, user.getUuid()).size());
		assertEquals(0, getContactFacade().getAllActiveUuids(user.getUuid()).size());
		assertEquals(0, getVisitFacade().getAllActiveVisitsAfter(null, user.getUuid()).size());
		assertEquals(0, getVisitFacade().getAllActiveUuids(user.getUuid()).size());

		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), false);
		
		// getAllActiveContacts and getAllUuids should return length 1
		assertEquals(1, getContactFacade().getAllActiveContactsAfter(null, user.getUuid()).size());
		assertEquals(1, getContactFacade().getAllActiveUuids(user.getUuid()).size());
		assertEquals(1, getVisitFacade().getAllActiveVisitsAfter(null, user.getUuid()).size());
		assertEquals(1, getVisitFacade().getAllActiveUuids(user.getUuid()).size());
	}
}
