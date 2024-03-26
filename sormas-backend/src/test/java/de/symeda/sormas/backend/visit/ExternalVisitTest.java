/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.visit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.person.JournalPersonDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFollowUpEndDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.ExternalVisitDto;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;

/*
 * If you need to change these tests to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
 * https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support!
 */
public class ExternalVisitTest extends AbstractBeanTest {

	private UserDto externalVisitsUser;
	private UserDto nationalUser;
	private RDCF rdcf;

	public void init() {
		super.init();

		rdcf = creator.createRDCF();
		externalVisitsUser = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.REST_EXTERNAL_VISITS_USER));
		nationalUser = creator.createNationalUser();
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support!
	 */
	public void testGetPersonForJournal() {
		String phoneNumber = "+496211218490";
		String internationalPhoneNumber = "+49 621 1218490";

		final PersonDto person = creator.createPerson();
		person.setFirstName("Klaus");
		person.setLastName("Draufle");
		person.setSex(Sex.MALE);
		person.setEmailAddress("test@test.de");
		person.setPhone(phoneNumber);
		person.setBirthdateYYYY(2000);
		person.setBirthdateMM(6);
		person.setBirthdateDD(1);
		person.setSymptomJournalStatus(SymptomJournalStatus.REGISTERED);
		final ContactDto contact1 =
			creator.createContact(externalVisitsUser.toReference(), person.toReference(), DateHelper.subtractDays(new Date(), 41));
		final ContactDto contact2 =
			creator.createContact(externalVisitsUser.toReference(), person.toReference(), DateHelper.subtractDays(new Date(), 29));
		contact1.setOverwriteFollowUpUntil(true);
		contact2.setOverwriteFollowUpUntil(true);

		Date now = new Date();
		contact1.setFollowUpUntil(DateHelper.subtractDays(now, 20));
		contact2.setFollowUpUntil(DateHelper.subtractDays(now, 8));

		getPersonFacade().save(person);
		getContactFacade().save(contact1);
		getContactFacade().save(contact2);

		loginWith(externalVisitsUser);

		JournalPersonDto exportPerson = getPersonFacade().getPersonForJournal(person.getUuid());
		assertEquals(person.getFirstName(), exportPerson.getFirstName());
		assertEquals(person.getLastName(), exportPerson.getLastName());
		assertEquals(person.getSex(), exportPerson.getSex());
		assertEquals(person.getEmailAddress(), exportPerson.getEmailAddress());
		assertEquals(internationalPhoneNumber, exportPerson.getPhone());
		assertEquals(person.getBirthdateYYYY(), exportPerson.getBirthdateYYYY());
		assertEquals(person.getBirthdateMM(), exportPerson.getBirthdateMM());
		assertEquals(person.getBirthdateDD(), exportPerson.getBirthdateDD());
		assertEquals(contact2.getFollowUpUntil(), exportPerson.getLatestFollowUpEndDate());
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support!
	 */
	public void testIsValidPersonUuid() {
		final PersonDto person = creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1);

		loginWith(externalVisitsUser);

		assertTrue(getPersonFacade().isValidPersonUuid(person.getUuid()));
		assertFalse(getPersonFacade().isValidPersonUuid("2341235234534"));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support!
	 */
	public void testSetSymptomJournalStatus() {
		PersonDto person = creator.createPerson();

		loginWith(externalVisitsUser);
		getPersonFacade().setSymptomJournalStatus(person.getUuid(), SymptomJournalStatus.REGISTERED);

		PersonDto updatedPerson = getPersonFacade().getByUuid(person.getUuid());

		assertEquals(SymptomJournalStatus.REGISTERED, updatedPerson.getSymptomJournalStatus());
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support!
	 */
	public void givenRelevantChangeShouldNotify() {
		PersonFacadeEjb.PersonFacadeEjbLocal personFacade = getPersonFacade();
		personFacade.setExternalJournalService(getExternalJournalService());
		PersonService personService = getPersonService();

		Person person = personService.createPerson();
		setPersonRelevantFields(person);

		// cannot use PersonFacade save since it also calls the method being tested
		personService.persist(person);

		// need to create a case with the person to avoid pseudonymization related errors
		creator.createCase(externalVisitsUser.toReference(), new PersonReferenceDto(person.getUuid()), rdcf);
		JournalPersonDto journalPerson = personFacade.getPersonForJournal(person.getUuid());

		assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

		// Define relevant changes
		HashMap<String, Object> relevantChanges = new HashMap<String, Object>() {

			{
				put(Person.FIRST_NAME, "Heinz");
				put(Person.LAST_NAME, "Müller");
				put(Person.SEX, Sex.FEMALE);
				put(Person.BIRTHDATE_YYYY, 2001);
				put(Person.BIRTHDATE_MM, 7);
				put(Person.BIRTHDATE_DD, 2);
			}
		};

		person.setPhone("+496211218491");
		person.setEmailAddress("heinz@test.de");

		// Apply each change and make sure it makes notification considered necessary
		for (String propertyName : relevantChanges.keySet()) {
			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			setPersonProperty(person, propertyName, relevantChanges.get(propertyName));
			person = executeInTransaction((entityManager, personParam) -> entityManager.merge(personParam), person);
			assertTrue(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

			// Modify the SymptomJournalStatus of the original person
			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.DELETED);
			person = executeInTransaction((entityManager, personParam) -> entityManager.merge(personParam), person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.REJECTED);
			person = executeInTransaction((entityManager, personParam) -> entityManager.merge(personParam), person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.UNREGISTERED);
			person = executeInTransaction((entityManager, personParam) -> entityManager.merge(personParam), person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.ACCEPTED);
			person = executeInTransaction((entityManager, personParam) -> entityManager.merge(personParam), person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

			// Apply any other relevant change and make sure notification is still considered necessary
			for (String secondPropertyName : relevantChanges.keySet()) {
				if (!secondPropertyName.equals(propertyName)) {
					journalPerson = personFacade.getPersonForJournal(person.getUuid());
					setPersonProperty(person, secondPropertyName, relevantChanges.get(secondPropertyName));
					person = executeInTransaction((entityManager, personParam) -> entityManager.merge(personParam), person);
					assertTrue(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());
				}
			}

			setPersonRelevantFields(person);
			person = executeInTransaction((entityManager, personParam) -> entityManager.merge(personParam), person);
		}
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support!
	 */
	public void testCreateExternalVisit() {
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			externalVisitsUser.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator.createContact(
			externalVisitsUser.toReference(),
			externalVisitsUser.toReference(),
			contactPerson.toReference(),
			caze,
			new Date(),
			new Date(),
			null);

		final ExternalVisitDto externalVisitDto = new ExternalVisitDto();
		externalVisitDto.setPersonUuid(contactPerson.getUuid());
		externalVisitDto.setDisease(contact.getDisease());
		externalVisitDto.setVisitDateTime(new Date());
		externalVisitDto.setVisitStatus(VisitStatus.COOPERATIVE);
		final String visitRemarks = "Everything good";
		externalVisitDto.setVisitRemarks(visitRemarks);

		final ExternalVisitDto externalVisitDto2 = new ExternalVisitDto();
		externalVisitDto2.setPersonUuid(cazePerson.getUuid());
		externalVisitDto2.setDisease(caze.getDisease());
		externalVisitDto2.setVisitDateTime(new Date());
		externalVisitDto2.setVisitStatus(VisitStatus.COOPERATIVE);
		final String visitRemarks2 = "Everything good 2";
		externalVisitDto2.setVisitRemarks(visitRemarks2);

		final VisitFacade visitFacade = getVisitFacade();

		loginWith(externalVisitsUser);

		visitFacade.saveExternalVisit(externalVisitDto);
		visitFacade.saveExternalVisit(externalVisitDto2);

		loginWith(nationalUser);

		final VisitCriteria visitCriteria = new VisitCriteria();
		final List<VisitIndexDto> visitIndexList =
			visitFacade.getIndexList(visitCriteria.contact(new ContactReferenceDto(contact.getUuid())), 0, 100, null);
		assertNotNull(visitIndexList);
		assertEquals(1, visitIndexList.size());
		VisitIndexDto visitIndexDto = visitIndexList.get(0);
		assertNotNull(visitIndexDto.getVisitDateTime());
		assertEquals(VisitStatus.COOPERATIVE, visitIndexDto.getVisitStatus());
		assertEquals(visitRemarks, visitIndexDto.getVisitRemarks());
		assertEquals(VisitOrigin.EXTERNAL_JOURNAL, visitIndexDto.getOrigin());

		final VisitCriteria visitCriteria2 = new VisitCriteria();
		final List<VisitIndexDto> visitIndexList2 = visitFacade.getIndexList(visitCriteria2.caze(new CaseReferenceDto(caze.getUuid())), 0, 100, null);
		assertNotNull(visitIndexList2);
		assertEquals(1, visitIndexList2.size());
		VisitIndexDto visitIndexDto2 = visitIndexList2.get(0);
		assertNotNull(visitIndexDto2.getVisitDateTime());
		assertEquals(VisitStatus.COOPERATIVE, visitIndexDto2.getVisitStatus());
		assertEquals(visitRemarks2, visitIndexDto2.getVisitRemarks());
		assertEquals(VisitOrigin.EXTERNAL_JOURNAL, visitIndexDto.getOrigin());
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support!
	 */
	public void testGetFollowUpEndDatesContactsOnly() {
		creator.createPerson(); // Person without contact
		final PersonDto person1 = creator.createPerson();
		final PersonDto person2 = creator.createPerson();
		final ContactDto contact11 =
			creator.createContact(externalVisitsUser.toReference(), person1.toReference(), DateHelper.subtractDays(new Date(), 41));
		final ContactDto contact12 =
			creator.createContact(externalVisitsUser.toReference(), person1.toReference(), DateHelper.subtractDays(new Date(), 29));
		final ContactDto contact2 =
			creator.createContact(externalVisitsUser.toReference(), person2.toReference(), DateHelper.subtractDays(new Date(), 21));

		contact11.setOverwriteFollowUpUntil(true);
		contact12.setOverwriteFollowUpUntil(true);
		contact2.setOverwriteFollowUpUntil(true);

		Date now = new Date();
		contact11.setFollowUpUntil(DateHelper.subtractDays(now, 20));
		contact12.setFollowUpUntil(DateHelper.subtractDays(now, 8));
		contact2.setFollowUpUntil(now);

		getContactFacade().save(contact11);
		getContactFacade().save(contact12);
		getContactFacade().save(contact2);

		loginWith(externalVisitsUser);

		List<PersonFollowUpEndDto> followUpEndDtos = getPersonFacade().getLatestFollowUpEndDates(null, false);

		assertThat(followUpEndDtos, hasSize(2));
		Optional<PersonFollowUpEndDto> result1 = followUpEndDtos.stream().filter(p -> p.getPersonUuid().equals(person1.getUuid())).findFirst();
		assertTrue(result1.isPresent());
		assertTrue(DateHelper.isSameDay(result1.get().getLatestFollowUpEndDate(), DateHelper.subtractDays(now, 8)));
		Optional<PersonFollowUpEndDto> result2 = followUpEndDtos.stream().filter(p -> p.getPersonUuid().equals(person2.getUuid())).findFirst();
		assertTrue(result2.isPresent());
		assertTrue(DateHelper.isSameDay(result2.get().getLatestFollowUpEndDate(), now));
		Date result3 = getPersonFacade().getLatestFollowUpEndDateByUuid(person1.getUuid());
		assertTrue(DateHelper.isSameDay(result3, DateHelper.subtractDays(now, 8)));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support!
	 */
	public void testGetFollowUpEndDatesCasesOnly() {
		createFeatureConfiguration(FeatureType.CASE_FOLLOWUP, true);

		creator.createPerson(); // Person without contact
		final PersonDto person1 = creator.createPerson();
		final PersonDto person2 = creator.createPerson();
		final CaseDataDto case11 = creator.createCase(externalVisitsUser.toReference(), person1.toReference(), rdcf);
		final CaseDataDto case12 = creator.createCase(externalVisitsUser.toReference(), person1.toReference(), rdcf);
		final CaseDataDto case2 = creator.createCase(externalVisitsUser.toReference(), person2.toReference(), rdcf);

		Date now = new Date();
		case11.getSymptoms().setOnsetDate(DateHelper.subtractDays(now, 41));
		case11.setOverwriteFollowUpUntil(true);
		case12.getSymptoms().setOnsetDate(DateHelper.subtractDays(now, 29));
		case12.setOverwriteFollowUpUntil(true);
		case2.getSymptoms().setOnsetDate(DateHelper.subtractDays(now, 21));
		case2.setOverwriteFollowUpUntil(true);

		case11.setFollowUpUntil(DateHelper.subtractDays(now, 20));
		case12.setFollowUpUntil(DateHelper.subtractDays(now, 8));
		case2.setFollowUpUntil(now);

		getCaseFacade().save(case11);
		getCaseFacade().save(case12);
		getCaseFacade().save(case2);

		loginWith(externalVisitsUser);

		List<PersonFollowUpEndDto> followUpEndDtos = getPersonFacade().getLatestFollowUpEndDates(null, false);

		assertThat(followUpEndDtos, hasSize(2));
		Optional<PersonFollowUpEndDto> result1 = followUpEndDtos.stream().filter(p -> p.getPersonUuid().equals(person1.getUuid())).findFirst();
		assertTrue(result1.isPresent());
		assertTrue(DateHelper.isSameDay(result1.get().getLatestFollowUpEndDate(), DateHelper.subtractDays(now, 8)));
		Optional<PersonFollowUpEndDto> result2 = followUpEndDtos.stream().filter(p -> p.getPersonUuid().equals(person2.getUuid())).findFirst();
		assertTrue(result2.isPresent());
		assertTrue(DateHelper.isSameDay(result2.get().getLatestFollowUpEndDate(), now));
		Date result3 = getPersonFacade().getLatestFollowUpEndDateByUuid(person1.getUuid());
		assertTrue(DateHelper.isSameDay(result3, DateHelper.subtractDays(now, 8)));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support!
	 */
	public void testGetFollowUpEndDatesContactsAndCases() {
		Date now = new Date();

		final PersonDto person1 = creator.createPerson();
		final PersonDto person2 = creator.createPerson();
		final PersonDto person3 = creator.createPerson();
		final PersonDto person4 = creator.createPerson();
		final ContactDto contact1 = creator.createContact(externalVisitsUser.toReference(), person1.toReference(), DateHelper.subtractDays(now, 22));
		final ContactDto contact2 = creator.createContact(externalVisitsUser.toReference(), person2.toReference(), DateHelper.subtractDays(now, 22));
		final ContactDto contact3 = creator.createContact(externalVisitsUser.toReference(), person4.toReference());
		final ContactDto contact4 = creator.createContact(externalVisitsUser.toReference(), person4.toReference(), DateHelper.subtractDays(now, 21));
		final CaseDataDto case1 = creator.createCase(externalVisitsUser.toReference(), person1.toReference(), rdcf);
		final CaseDataDto case2 = creator.createCase(externalVisitsUser.toReference(), person2.toReference(), rdcf);
		final CaseDataDto case3 = creator.createCase(externalVisitsUser.toReference(), person2.toReference(), rdcf);
		final CaseDataDto case4 = creator.createCase(externalVisitsUser.toReference(), person3.toReference(), rdcf);
		final CaseDataDto case5 = creator.createCase(externalVisitsUser.toReference(), person3.toReference(), rdcf);

		contact1.setOverwriteFollowUpUntil(true);
		contact2.setOverwriteFollowUpUntil(true);
		case1.setOverwriteFollowUpUntil(true);
		case1.getSymptoms().setOnsetDate(DateHelper.subtractDays(now, 23));
		case2.setOverwriteFollowUpUntil(true);
		case2.getSymptoms().setOnsetDate(DateHelper.subtractDays(now, 21));
		case3.setOverwriteFollowUpUntil(true);
		case3.getSymptoms().setOnsetDate(DateHelper.subtractDays(now, 23));
		case4.setOverwriteFollowUpUntil(true);
		case4.getSymptoms().setOnsetDate(DateHelper.subtractDays(now, 21));
		case5.setOverwriteFollowUpUntil(true);
		case5.getSymptoms().setOnsetDate(DateHelper.subtractDays(now, 21));
		contact3.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
		contact3.setFollowUpUntil(null);
		contact4.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);

		contact1.setFollowUpUntil(DateHelper.subtractDays(now, 1));
		case1.setFollowUpUntil(DateHelper.subtractDays(now, 2));
		contact2.setFollowUpUntil(DateHelper.subtractDays(now, 1));

		case2.setFollowUpUntil(now);
		case2.setFollowUpStatus(FollowUpStatus.CANCELED);
		case3.setFollowUpUntil(DateHelper.subtractDays(now, 2));

		case4.setFollowUpStatus(FollowUpStatus.CANCELED);
		case5.setFollowUpStatus(FollowUpStatus.CANCELED);
		contact4.setFollowUpUntil(now);

		getContactFacade().save(contact1);
		getContactFacade().save(contact2);
		getContactFacade().save(contact3);
		getContactFacade().save(contact4);
		getCaseFacade().save(case1);
		getCaseFacade().save(case2);
		getCaseFacade().save(case3);
		getCaseFacade().save(case4);
		getCaseFacade().save(case5);

		loginWith(externalVisitsUser);

		createFeatureConfiguration(FeatureType.CASE_FOLLOWUP, true);
		List<PersonFollowUpEndDto> followUpEndDtos = getPersonFacade().getLatestFollowUpEndDates(null, false);

		assertThat(followUpEndDtos, hasSize(4));

		Optional<PersonFollowUpEndDto> result1 = followUpEndDtos.stream().filter(p -> p.getPersonUuid().equals(person1.getUuid())).findFirst();
		assertTrue(result1.isPresent());
		assertTrue(DateHelper.isSameDay(result1.get().getLatestFollowUpEndDate(), DateHelper.subtractDays(now, 1)));

		Optional<PersonFollowUpEndDto> result2 = followUpEndDtos.stream().filter(p -> p.getPersonUuid().equals(person2.getUuid())).findFirst();
		assertTrue(result2.isPresent());
		assertNotNull(result2.get().getLatestFollowUpEndDate());
		assertTrue(DateHelper.isSameDay(result2.get().getLatestFollowUpEndDate(), DateHelper.subtractDays(now, 1)));

		Optional<PersonFollowUpEndDto> result3 = followUpEndDtos.stream().filter(p -> p.getPersonUuid().equals(person3.getUuid())).findFirst();
		assertTrue(result3.isPresent());
		assertNull(result3.get().getLatestFollowUpEndDate());
	}

	/*
	 * If you need to change this method to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support!
	 */
	protected void setPersonRelevantFields(Person person) {
		person.setFirstName("Klaus");
		person.setLastName("Draufle");
		person.setSex(Sex.MALE);
		person.setEmailAddress("test@test.de");
		person.setPhone("+496211218490");
		person.setBirthdateYYYY(2000);
		person.setBirthdateMM(6);
		person.setBirthdateDD(1);
		person.setSymptomJournalStatus(SymptomJournalStatus.REGISTERED);
	}

	private void setPersonProperty(Person person, String propertyName, Object propertyValue) {
		try {
			BeanUtils.setProperty(person, propertyName, propertyValue);
		} catch (IllegalAccessException | InvocationTargetException e) {
			fail();
			e.printStackTrace();
		}
	}
}
