package de.symeda.sormas.backend.contact;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Set;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.person.Person;

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

}
