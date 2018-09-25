package de.symeda.sormas.backend.contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
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
		MapCaseDto mapCaseDto = new MapCaseDto(caze.getUuid(), caze.getReportDate(), caze.getCaseClassification(), caze.getDisease(), caze.getHealthFacility().getUuid(), caze.getPerson().getUuid(), caze.getReportLat(), caze.getReportLon(), caze.getReportLat(), caze.getReportLon());
		
		List<MapContactDto> mapContactDtos =  getContactFacade().getContactsForMap(caze.getRegion(), caze.getDistrict(), caze.getDisease(), DateHelper.subtractDays(new Date(),  1), DateHelper.addDays(new Date(), 1), user.getUuid(), Arrays.asList(mapCaseDto));
		
		// List should have one entry
		assertEquals(1, mapContactDtos.size());
	}
	
	@Test
	public void testContactDeletion() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		String userUuid = user.getUuid();
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());
		creator.createVisit(caze.getDisease(), contactPerson.toReference(), DateUtils.addDays(new Date(), 21), VisitStatus.UNAVAILABLE);
		TaskDto task = creator.createTask(TaskContext.CONTACT, TaskType.CONTACT_INVESTIGATION, TaskStatus.PENDING, null, contact.toReference(), new Date(), user.toReference());
		
		// Database should contain one contact, associated visit and task
		assertEquals(1,  getContactFacade().getAllContactsAfter(null, userUuid).size());
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));
		assertEquals(1, getVisitFacade().getAllVisitsAfter(null, userUuid).size());
		
		 getContactFacade().deleteContact(contact.toReference(), adminUuid);
		
		// Database should contain no contact and associated visit or task
		assertEquals(0,  getContactFacade().getAllContactsAfter(null, userUuid).size());
		assertNull(getTaskFacade().getByUuid(task.getUuid()));
		assertEquals(0, getVisitFacade().getAllVisitsAfter(null, userUuid).size());
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
		assertEquals(1, getContactFacade().getIndexList(userUuid, null).size());
	}
}
