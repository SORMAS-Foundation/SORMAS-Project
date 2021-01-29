package de.symeda.sormas.backend.person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.JournalPersonDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFollowUpEndDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class PersonFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetMatchingNameDtos() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		// 1-3 = Active persons; 4 = Person without reference; 5-7 = Inactive persons
		PersonDto person1 = creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1);
		PersonDto person2 = creator.createPerson("James", "Smith", Sex.MALE, 1979, 5, 12);
		PersonDto person3 = creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 5);
		PersonDto person4 = creator.createPerson("Maria", "Garcia", Sex.FEMALE, 1984, 12, 2);
		PersonDto person5 = creator.createPerson("Maria", "Garcia", null, 1984, 7, 12);
		PersonDto person6 = creator.createPerson("Maria", "Garcia", Sex.FEMALE, 1984, null, null);
		PersonDto person7 = creator.createPerson("James", "Smith", Sex.MALE, null, null, null);

		CaseDataDto activeCase = creator.createCase(user.toReference(), person1.toReference(), rdcf);
		creator.createContact(user.toReference(), person2.toReference(), activeCase);
		EventDto activeEvent = creator.createEvent(user.toReference());
		creator.createEventParticipant(activeEvent.toReference(), person3, user.toReference());

		CaseDataDto inactiveCase = creator.createCase(user.toReference(), person5.toReference(), rdcf);
		creator.createContact(user.toReference(), person6.toReference(), inactiveCase);
		EventDto inactiveEvent = creator.createEvent(user.toReference());
		creator.createEventParticipant(inactiveEvent.toReference(), person7, user.toReference());

		getCaseFacade().archiveOrDearchiveCase(inactiveCase.getUuid(), true);
		getEventFacade().archiveOrDearchiveEvent(inactiveEvent.getUuid(), true);

		// Only persons that have active case, contact or event participant associations should be retrieved
		List<String> relevantNameUuids = getPersonFacade().getMatchingNameDtos(user.toReference(), new PersonSimilarityCriteria())
			.stream()
			.map(dto -> dto.getUuid())
			.collect(Collectors.toList());
		assertThat(relevantNameUuids, hasSize(3));
		assertThat(relevantNameUuids, containsInAnyOrder(person1.getUuid(), person2.getUuid(), person3.getUuid()));

		creator.createCase(user.toReference(), person4.toReference(), rdcf);
		getCaseFacade().archiveOrDearchiveCase(inactiveCase.getUuid(), false);
		getEventFacade().archiveOrDearchiveEvent(inactiveEvent.getUuid(), false);

		PersonSimilarityCriteria criteria = new PersonSimilarityCriteria().sex(Sex.MALE).birthdateYYYY(1980).birthdateMM(1).birthdateDD(1);
		List<String> matchingUuids =
			getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(2));
		assertThat(matchingUuids, containsInAnyOrder(person1.getUuid(), person7.getUuid()));

		criteria.birthdateMM(null).birthdateDD(null);
		matchingUuids =
			getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(3));
		assertThat(matchingUuids, containsInAnyOrder(person1.getUuid(), person3.getUuid(), person7.getUuid()));

		criteria.sex(Sex.FEMALE).birthdateYYYY(1984);
		matchingUuids =
			getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(3));
		assertThat(matchingUuids, containsInAnyOrder(person4.getUuid(), person5.getUuid(), person6.getUuid()));

		criteria.sex(null);
		matchingUuids =
			getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(4));
		assertThat(matchingUuids, containsInAnyOrder(person4.getUuid(), person5.getUuid(), person6.getUuid(), person7.getUuid()));

		final String passportNr = "passportNr";
		final String otherPassportNr = "otherPassportNr";
		final String healthId = "healthId";
		final String otherHealthId = "otherHealthId";
		PersonDto person8 = creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1, passportNr, healthId);
		PersonDto person9 = creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1, null, otherHealthId);
		PersonDto person10 = creator.createPerson("Maria", "Garcia", Sex.FEMALE, 1970, 1, 1, passportNr, null);
		PersonDto person11 = creator.createPerson("John", "Doe", Sex.MALE, 1970, 1, 1, otherPassportNr, null);
		creator.createCase(user.toReference(), person8.toReference(), rdcf);
		creator.createCase(user.toReference(), person9.toReference(), rdcf);
		creator.createCase(user.toReference(), person10.toReference(), rdcf);
		creator.createCase(user.toReference(), person11.toReference(), rdcf);

		criteria.sex(Sex.MALE).birthdateYYYY(1980);
		criteria.passportNumber(passportNr);
		matchingUuids =
			getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(6));
		assertThat(
			matchingUuids,
			containsInAnyOrder(person1.getUuid(), person3.getUuid(), person7.getUuid(), person8.getUuid(), person9.getUuid(), person10.getUuid()));

		criteria.nationalHealthId(healthId).passportNumber(null);
		matchingUuids =
			getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(4));
		assertThat(matchingUuids, containsInAnyOrder(person1.getUuid(), person3.getUuid(), person7.getUuid(), person8.getUuid()));

		criteria.nationalHealthId(otherHealthId);
		matchingUuids =
			getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(4));
		assertThat(matchingUuids, containsInAnyOrder(person1.getUuid(), person3.getUuid(), person7.getUuid(), person9.getUuid()));

		criteria.passportNumber(otherPassportNr);
		matchingUuids =
			getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(5));
		assertThat(matchingUuids, containsInAnyOrder(person1.getUuid(), person3.getUuid(), person7.getUuid(), person9.getUuid(), person11.getUuid()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void testIsValidPersonUuid() {
		final PersonDto person = creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1);
		assertTrue(getPersonFacade().isValidPersonUuid(person.getUuid()));
		Assert.assertFalse(getPersonFacade().isValidPersonUuid("2341235234534"));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void testGetFollowUpEndDates() {
		RDCFEntities rdcfEntities = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcfEntities, UserRole.REST_EXTERNAL_VISITS_USER);

		creator.createPerson(); // Person without contact
		final PersonDto person1 = creator.createPerson();
		final PersonDto person2 = creator.createPerson();
		final ContactDto contact11 = creator.createContact(user.toReference(), person1.toReference());
		final ContactDto contact12 = creator.createContact(user.toReference(), person1.toReference());
		final ContactDto contact2 = creator.createContact(user.toReference(), person2.toReference());

		contact11.setOverwriteFollowUpUntil(true);
		contact12.setOverwriteFollowUpUntil(true);
		contact2.setOverwriteFollowUpUntil(true);

		Date now = new Date();
		contact11.setFollowUpUntil(DateHelper.subtractDays(now, 20));
		contact12.setFollowUpUntil(DateHelper.subtractDays(now, 8));
		contact2.setFollowUpUntil(now);

		getContactFacade().saveContact(contact11);
		getContactFacade().saveContact(contact12);
		getContactFacade().saveContact(contact2);

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
	 * https://gitter.im/SORMAS-Project!
	 */
	public void testGetPersonForJournal() {
		RDCFEntities rdcfEntities = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcfEntities, UserRole.REST_EXTERNAL_VISITS_USER, UserRole.CONTACT_SUPERVISOR);

		final PersonDto person = creator.createPerson();
		person.setFirstName("Klaus");
		person.setLastName("Draufle");
		person.setSex(Sex.MALE);
		person.setEmailAddress("test@test.de");
		person.setPhone("+496211218490");
		person.setBirthdateYYYY(2000);
		person.setBirthdateMM(6);
		person.setBirthdateDD(1);
		person.setSymptomJournalStatus(SymptomJournalStatus.REGISTERED);
		final ContactDto contact1 = creator.createContact(user.toReference(), person.toReference());
		final ContactDto contact2 = creator.createContact(user.toReference(), person.toReference());
		contact1.setOverwriteFollowUpUntil(true);
		contact2.setOverwriteFollowUpUntil(true);

		Date now = new Date();
		contact1.setFollowUpUntil(DateHelper.subtractDays(now, 20));
		contact2.setFollowUpUntil(DateHelper.subtractDays(now, 8));

		getPersonFacade().savePerson(person);
		getContactFacade().saveContact(contact1);
		getContactFacade().saveContact(contact2);

		JournalPersonDto exportPerson = getPersonFacade().getPersonForJournal(person.getUuid());
		assertEquals(person.getFirstName(), exportPerson.getFirstName());
		assertEquals(person.getLastName(), exportPerson.getLastName());
		assertEquals(person.getSex(), exportPerson.getSex());
		assertEquals(person.getEmailAddress(), exportPerson.getEmailAddress());
		assertEquals(person.getPhone(), exportPerson.getPhone());
		assertEquals(person.getBirthdateYYYY(), exportPerson.getBirthdateYYYY());
		assertEquals(person.getBirthdateMM(), exportPerson.getBirthdateMM());
		assertEquals(person.getBirthdateDD(), exportPerson.getBirthdateDD());
		assertEquals(contact2.getFollowUpUntil(), exportPerson.getLatestFollowUpEndDate());
	}

	@Test
	public void testGetPersonsAfter() {
		UserDto natUser = useNationalUserLogin();

		Date t1 = new Date();

		PersonDto person1 = creator.createPerson();
		person1 = getPersonFacade().savePerson(person1);
		final ContactDto contact1 = creator.createContact(natUser.toReference(), person1.toReference());
		getContactFacade().saveContact(contact1);

		List<PersonDto> personsAfterT1 = getPersonFacade().getPersonsAfter(t1);
		assertEquals(1, personsAfterT1.size());
		assertEquals(person1.getUuid(), personsAfterT1.get(0).getUuid());

		Date t2 = new Date();

		PersonDto person2 = creator.createPerson();
		person2 = getPersonFacade().savePerson(person2);
		final ContactDto contact2 = creator.createContact(natUser.toReference(), person2.toReference());
		getContactFacade().saveContact(contact2);

		List<PersonDto> personsAfterT2 = getPersonFacade().getPersonsAfter(t2);
		assertEquals(1, personsAfterT2.size());
		assertEquals(person2.getUuid(), personsAfterT2.get(0).getUuid());

		personsAfterT1 = getPersonFacade().getPersonsAfter(t1);
		assertEquals(2, personsAfterT1.size());
	}

	@Test
	public void testCreateWithoutUuid() {
		PersonDto person = new PersonDto();
		person.setFirstName("Fname");
		person.setLastName("Lname");
		person.setAddress(new LocationDto());
		person.setAddresses(Collections.singletonList(new LocationDto()));

		PersonDto savedPerson = getPersonFacade().savePerson(person);

		assertThat(savedPerson.getUuid(), not(isEmptyOrNullString()));
		assertThat(savedPerson.getAddress().getUuid(), not(isEmptyOrNullString()));
		assertThat(savedPerson.getAddresses().get(0).getUuid(), not(isEmptyOrNullString()));
	}
}
