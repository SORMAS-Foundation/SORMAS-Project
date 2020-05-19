package de.symeda.sormas.backend.contact;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.util.DateHelper8;

public class ContactServiceTest extends AbstractBeanTest {

	@Test
	public void testGetAllRelevantContacts() throws Exception {
		UserDto user = creator.createUser(creator.createRDCFEntities(), UserRole.SURVEILLANCE_SUPERVISOR);
		
		Date referenceDate = DateHelper.subtractDays(new Date(), ContactLogic.ALLOWED_CONTACT_DATE_OFFSET * 2);
		PersonDto contactPerson = creator.createPerson();
		Person contactPersonEntity = getPersonService().getByUuid(contactPerson.getUuid());
		
		// Contacts with a report/last contact date after the reference date should not be included
		ContactDto contact1 = creator.createContact(user.toReference(), contactPerson.toReference());
		contact1.setReportDateTime(DateHelper.addDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		getContactFacade().saveContact(contact1);
		
		ContactDto contact2 = creator.createContact(user.toReference(), contactPerson.toReference());
		contact2.setReportDateTime(referenceDate);
		contact2.setLastContactDate(DateHelper.addDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		getContactFacade().saveContact(contact2);
		
		Set<Contact> contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, empty());
		
		// Contacts with a report/last contact date after the reference date but within the offset should be included
		contact1.setReportDateTime(DateHelper.addDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET));
		contact2.setLastContactDate(DateHelper.addDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET));
		getContactFacade().saveContact(contact1);
		getContactFacade().saveContact(contact2);
		
		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, hasSize(2));
		
		// Contacts with a report/last contact date before the reference date should be included
		contact1.setReportDateTime(DateHelper.subtractDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		contact2.setLastContactDate(DateHelper.subtractDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		getContactFacade().saveContact(contact1);
		getContactFacade().saveContact(contact2);
		
		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, hasSize(2));
		
		// Contacts with a follow-up until date before the reference date should not be included
		contact1.setFollowUpUntil(DateHelper.subtractDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		contact1.setOverwriteFollowUpUntil(true);
		contact2.setFollowUpUntil(DateHelper.subtractDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		contact2.setOverwriteFollowUpUntil(true);
		getContactFacade().saveContact(contact1);
		getContactFacade().saveContact(contact2);
		
		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, empty());
		
		// Contacts with a follow-up until date before the reference date but within the offset should be included
		contact1.setFollowUpUntil(DateHelper.subtractDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET));
		contact2.setFollowUpUntil(DateHelper.subtractDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET));
		getContactFacade().saveContact(contact1);
		getContactFacade().saveContact(contact2);
		
		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, hasSize(2));
		
		// Contacts with a follow-up until date after the reference date should be included
		contact1.setFollowUpUntil(DateHelper.addDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		contact2.setFollowUpUntil(DateHelper.addDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET + 1));
		getContactFacade().saveContact(contact1);
		getContactFacade().saveContact(contact2);
		
		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, hasSize(2));
		
		// Contacts without a follow-up until date should be included as long as the report/last contact date is within the threshold
		contact1.setFollowUpStatus(FollowUpStatus.CANCELED);
		contact2.setFollowUpStatus(FollowUpStatus.CANCELED);
		contact1.setOverwriteFollowUpUntil(false);
		contact2.setOverwriteFollowUpUntil(false);
		contact1.setFollowUpUntil(null);
		contact2.setFollowUpUntil(null);
		getContactFacade().saveContact(contact1);
		getContactFacade().saveContact(contact2);

		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, hasSize(2));
		
		contact1.setReportDateTime(DateHelper.subtractDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET * 2));
		contact2.setLastContactDate(DateHelper.subtractDays(referenceDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET * 2));
		getContactFacade().saveContact(contact1);
		getContactFacade().saveContact(contact2);
		
		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, empty());
		
		// Contacts with a different person and/or disease should not be included
		PersonDto contactPerson2 = creator.createPerson();
		ContactDto contact3 = creator.createContact(user.toReference(), contactPerson2.toReference());
		contact3.setReportDateTime(referenceDate);
		getContactFacade().saveContact(contact3);
		
		ContactDto contact4 = creator.createContact(user.toReference(), contactPerson.toReference(), Disease.CSM);
		contact4.setReportDateTime(referenceDate);
		getContactFacade().saveContact(contact4);
		
		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, empty());
	}

	@Test
	public void testUpdateFollowUpUntilAndStatus() {
		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid()
				,"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);

		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));

		VisitDto visit = creator.createVisit(caze.getDisease(), contactPerson.toReference(), DateUtils.addDays(new Date(), 21), VisitStatus.UNAVAILABLE);

		// Follow-up until should be increased by one day
		contact = getContactFacade().getContactByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21 + 1), DateHelper8.toLocalDate(contact.getFollowUpUntil()));

		visit.setVisitStatus(VisitStatus.COOPERATIVE);
		visit = getVisitFacade().saveVisit(visit);

		// Follow-up until should be back at the original date and follow-up should be completed
		contact = getContactFacade().getContactByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.COMPLETED, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));
		
		PersonDto person2 = creator.createPerson();
		ContactDto contact2 = creator.createContact(user.toReference(), person2.toReference());
		contact2.setContactStatus(ContactStatus.CONVERTED);
		contact2 = getContactFacade().saveContact(contact2);
		
		// Follow-up should be canceled when contact is converted to a case and should have a generated follow-up comment
		assertThat(contact2.getFollowUpStatus(), is(FollowUpStatus.CANCELED));
		assertNotNull(contact2.getFollowUpComment());
	}

}
