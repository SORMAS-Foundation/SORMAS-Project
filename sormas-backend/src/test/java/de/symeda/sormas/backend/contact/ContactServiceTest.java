package de.symeda.sormas.backend.contact;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.util.DateHelper8;

public class ContactServiceTest extends AbstractBeanTest {

	@Test
	public void testExistsUuid() {

		assertFalse(getContactService().exists("no-uuid"));

		RDCF rdcf = creator.createRDCF();
		UserReferenceDto reportingUser = creator.createUser(rdcf).toReference();
		PersonReferenceDto contactPerson = creator.createPerson().toReference();
		ContactDto contact = creator.createContact(reportingUser, contactPerson);

		assertFalse(getContactService().exists("no-uuid"));
		assertTrue(getContactService().exists(contact.getUuid()));
	}

	@Test
	public void testGetAllRelevantContacts() {

		UserDto user = creator.createUser(creator.createRDCFEntities(), UserRole.SURVEILLANCE_SUPERVISOR);

		Date referenceDate = DateHelper.subtractDays(new Date(), FollowUpLogic.ALLOWED_DATE_OFFSET * 2);
		PersonDto contactPerson = creator.createPerson();
		Person contactPersonEntity = getPersonService().getByUuid(contactPerson.getUuid());

		// Contacts with a report/last contact date after the reference date should not be included
		ContactDto contact1 = creator.createContact(user.toReference(), contactPerson.toReference());
		contact1.setReportDateTime(DateHelper.addDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET + 1));
		contact1 = getContactFacade().save(contact1);

		ContactDto contact2 = creator.createContact(user.toReference(), contactPerson.toReference());
		contact2.setReportDateTime(referenceDate);
		contact2.setFirstContactDate(DateHelper.addDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET));
		contact2.setLastContactDate(DateHelper.addDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET + 1));
		contact2 = getContactFacade().save(contact2);

		Set<Contact> contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, empty());

		// Contacts with a report/last contact date after the reference date but within the offset should be included
		contact1.setReportDateTime(DateHelper.addDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET));
		contact2.setLastContactDate(DateHelper.addDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET));
		contact1 = getContactFacade().save(contact1);
		contact2 = getContactFacade().save(contact2);

		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, hasSize(2));

		// Contacts with a report/last contact date before the reference date should be included
		contact1.setReportDateTime(DateHelper.subtractDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET + 1));
		contact2.setLastContactDate(DateHelper.subtractDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET + 1));
		contact1 = getContactFacade().save(contact1);
		contact2 = getContactFacade().save(contact2);

		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, hasSize(2));

		// Contacts with a follow-up until date after the reference date should be included
		contact1.setFollowUpUntil(DateHelper.addDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET + 1));
		contact2.setFollowUpUntil(DateHelper.addDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET + 1));
		contact1 = getContactFacade().save(contact1);
		contact2 = getContactFacade().save(contact2);

		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, hasSize(2));

		// move contacts outside of relevant time frame
		contact1.setReportDateTime(DateHelper.subtractDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET * 2));
		contact1.setLastContactDate(DateHelper.subtractDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET * 2));
		contact1.setFollowUpUntil(DateHelper.subtractDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET * 2));
		contact2.setReportDateTime(DateHelper.subtractDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET * 2));
		contact2.setLastContactDate(DateHelper.subtractDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET * 2));
		contact2.setFollowUpUntil(DateHelper.subtractDays(referenceDate, FollowUpLogic.ALLOWED_DATE_OFFSET * 2));
		contact1 = getContactFacade().save(contact1);
		contact2 = getContactFacade().save(contact2);

		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, empty());

		// Contacts with a different person and/or disease should not be included
		PersonDto contactPerson2 = creator.createPerson();
		ContactDto contact3 = creator.createContact(user.toReference(), contactPerson2.toReference());
		contact3.setReportDateTime(referenceDate);
		contact3 = getContactFacade().save(contact3);

		ContactDto contact4 = creator.createContact(user.toReference(), contactPerson.toReference(), Disease.CSM);
		contact4.setReportDateTime(referenceDate);
		contact4 = getContactFacade().save(contact4);

		contacts = getContactService().getAllRelevantContacts(contactPersonEntity, contact1.getDisease(), referenceDate);
		assertThat(contacts, empty());
	}

	@Test
	public void testUpdateFollowUpUntilAndStatus() {

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

		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));

		VisitDto visit = creator.createVisit(
			caze.getDisease(),
			contactPerson.toReference(),
			DateUtils.addDays(new Date(), 21),
			VisitStatus.UNAVAILABLE,
			VisitOrigin.USER);

		// Follow-up until should be increased by one day
		contact = getContactFacade().getByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21 + 1), DateHelper8.toLocalDate(contact.getFollowUpUntil()));

		visit.setVisitStatus(VisitStatus.COOPERATIVE);
		visit = getVisitFacade().saveVisit(visit);

		// Follow-up until should be back at the original date and follow-up should be completed
		contact = getContactFacade().getByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.COMPLETED, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));

		// Manually overwrite and increase the follow-up until date
		contact.setFollowUpUntil(DateUtils.addDays(new Date(), 23));
		contact.setOverwriteFollowUpUntil(true);
		contact = getContactFacade().save(contact);
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertTrue(contact.isOverwriteFollowUpUntil());

		// Add a cooperative visit AFTER the follow-up until date; should set follow-up to completed
		visit.setVisitStatus(VisitStatus.UNAVAILABLE);
		visit.setVisitDateTime(contact.getFollowUpUntil());
		getVisitFacade().saveVisit(visit);
		contact = getContactFacade().getByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		creator.createVisit(
			caze.getDisease(),
			contactPerson.toReference(),
			DateUtils.addDays(new Date(), 24),
			VisitStatus.COOPERATIVE,
			VisitOrigin.USER);
		contact = getContactFacade().getByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.COMPLETED, contact.getFollowUpStatus());
		assertFalse(contact.isOverwriteFollowUpUntil());

		// Increasing the last contact date should extend follow-up
		contact.setLastContactDate(DateHelper.addDays(contact.getLastContactDate(), 10));
		contact = getContactFacade().save(contact);
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21 + 10), DateHelper8.toLocalDate(contact.getFollowUpUntil()));

		PersonDto person2 = creator.createPerson();
		ContactDto contact2 = creator.createContact(user.toReference(), person2.toReference());
		contact2.setContactStatus(ContactStatus.CONVERTED);
		contact2 = getContactFacade().save(contact2);

		// Follow-up should be canceled when contact is converted to a case and should have a generated follow-up comment
		assertThat(contact2.getFollowUpStatus(), is(FollowUpStatus.CANCELED));
		assertNotNull(contact2.getFollowUpComment());
	}
}
