package de.symeda.sormas.backend.person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.JournalPersonDto;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFollowUpEndDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class PersonFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetIndexListByPresentCondition() {
		final RDCFEntities rdcf = creator.createRDCFEntities();
		final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		final PersonDto person1 = creator.createPerson("James", "Smith", Sex.MALE, 1920, 1, 1);
		person1.setPresentCondition(PresentCondition.DEAD);
		creator.createPerson("Maria", "Garcia", Sex.FEMALE, 1920, 1, 1);

		getPersonFacade().savePerson(person1);

		assertEquals(1, getPersonFacade().getIndexList(new PersonCriteria().presentCondition(PresentCondition.DEAD), null, null, null).size());
		assertEquals(2, getPersonFacade().getIndexList(new PersonCriteria(), null, null, null).size());
	}

	@Test
	public void testGetIndexListPersonNotConsideredIfAssociatedEntityDeleted() {
		final RDCFEntities rdcf = creator.createRDCFEntities();
		final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		final PersonDto person1 = creator.createPerson("James", "Smith", Sex.MALE, 1920, 1, 1);
		person1.setPresentCondition(PresentCondition.DEAD);
		creator.createPerson("Maria", "Garcia", Sex.FEMALE, 1920, 1, 1);

		CaseDataDto caze = creator.createCase(
			user.toReference(),
			person1.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		CaseDataDto caze2 = creator.createCase(
			user.toReference(),
			person1.toReference(),
			Disease.EVD,
			CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		assertEquals(2, getPersonFacade().getIndexList(new PersonCriteria(), null, null, null).size());

		getCaseFacade().deleteCase(caze.getUuid());

		assertEquals(2, getPersonFacade().getIndexList(new PersonCriteria(), null, null, null).size());

		getCaseFacade().deleteCase(caze2.getUuid());

		assertEquals(1, getPersonFacade().getIndexList(new PersonCriteria(), null, null, null).size());

		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), person1.toReference(), caze, new Date(), new Date(), null);

		assertEquals(2, getPersonFacade().getIndexList(new PersonCriteria(), null, null, null).size());

		getContactFacade().deleteContact(contact.getUuid());

		assertEquals(1, getPersonFacade().getIndexList(new PersonCriteria(), null, null, null).size());
	}

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
	public void testGetFollowUpEndDatesContactsOnly() {
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
	public void testGetFollowUpEndDatesCasesOnly() {
		RDCFEntities rdcfEntities = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcfEntities, UserRole.REST_EXTERNAL_VISITS_USER);

		creator.createPerson(); // Person without contact
		final PersonDto person1 = creator.createPerson();
		final PersonDto person2 = creator.createPerson();
		final CaseDataDto case11 = creator.createCase(user.toReference(), person1.toReference(), rdcfEntities);
		final CaseDataDto case12 = creator.createCase(user.toReference(), person1.toReference(), rdcfEntities);
		final CaseDataDto case2 = creator.createCase(user.toReference(), person2.toReference(), rdcfEntities);

		case11.setOverwriteFollowUpUntil(true);
		case12.setOverwriteFollowUpUntil(true);
		case2.setOverwriteFollowUpUntil(true);

		Date now = new Date();
		case11.setFollowUpUntil(DateHelper.subtractDays(now, 20));
		case12.setFollowUpUntil(DateHelper.subtractDays(now, 8));
		case2.setFollowUpUntil(now);

		getCaseFacade().saveCase(case11);
		getCaseFacade().saveCase(case12);
		getCaseFacade().saveCase(case2);

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
	public void testGetFollowUpEndDatesContactsAndCases() {
		RDCFEntities rdcfEntities = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcfEntities, UserRole.REST_EXTERNAL_VISITS_USER);

		final PersonDto person1 = creator.createPerson();
		final PersonDto person2 = creator.createPerson();
		final ContactDto contact1 = creator.createContact(user.toReference(), person1.toReference());
		final ContactDto contact2 = creator.createContact(user.toReference(), person2.toReference());
		final CaseDataDto case1 = creator.createCase(user.toReference(), person1.toReference(), rdcfEntities);
		final CaseDataDto case2 = creator.createCase(user.toReference(), person2.toReference(), rdcfEntities);

		contact1.setOverwriteFollowUpUntil(true);
		contact2.setOverwriteFollowUpUntil(true);
		case1.setOverwriteFollowUpUntil(true);
		case2.setOverwriteFollowUpUntil(true);

		Date now = new Date();
		contact1.setFollowUpUntil(DateHelper.subtractDays(now, 1));
		case1.setFollowUpUntil(DateHelper.subtractDays(now, 2));
		contact2.setFollowUpUntil(DateHelper.subtractDays(now, 1));
		case2.setFollowUpUntil(now);

		getContactFacade().saveContact(contact1);
		getContactFacade().saveContact(contact2);
		getCaseFacade().saveCase(case1);
		getCaseFacade().saveCase(case2);

		List<PersonFollowUpEndDto> followUpEndDtos = getPersonFacade().getLatestFollowUpEndDates(null, false);

		assertThat(followUpEndDtos, hasSize(2));
		Optional<PersonFollowUpEndDto> result1 = followUpEndDtos.stream().filter(p -> p.getPersonUuid().equals(person1.getUuid())).findFirst();
		assertTrue(result1.isPresent());
		assertTrue(DateHelper.isSameDay(result1.get().getLatestFollowUpEndDate(), DateHelper.subtractDays(now, 1)));
		Optional<PersonFollowUpEndDto> result2 = followUpEndDtos.stream().filter(p -> p.getPersonUuid().equals(person2.getUuid())).findFirst();
		assertTrue(result2.isPresent());
		assertTrue(DateHelper.isSameDay(result2.get().getLatestFollowUpEndDate(), now));
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

	@Test
	public void testGetMostRelevantFollowUpStatusByUuid() {
		RDCFEntities rdcfEntities = creator.createRDCFEntities();
		PersonDto person = creator.createPerson();
		UserDto user = creator.createUser(rdcfEntities, UserRole.REST_EXTERNAL_VISITS_USER);

		ContactDto contact1 = creator.createContact(user.toReference(), person.toReference());

		for (FollowUpStatus status : FollowUpStatus.values()) {
			contact1.setFollowUpStatus(status);
			getContactFacade().saveContact(contact1);

			if (FollowUpStatus.COMPLETED.equals(status) || FollowUpStatus.NO_FOLLOW_UP.equals(status)) {
				// In this case the status is automatically updated to FOLLOW_UP, because the end of the follow up period has not yet been reached.
				assertThat(getPersonFacade().getMostRelevantFollowUpStatusByUuid(person.getUuid()), is(FollowUpStatus.FOLLOW_UP));
				continue;
			}
			assertThat(getPersonFacade().getMostRelevantFollowUpStatusByUuid(person.getUuid()), is(status));
		}

		ContactDto contact2 = creator.createContact(user.toReference(), person.toReference());
		CaseDataDto case1 = creator.createCase(user.toReference(), person.toReference(), rdcfEntities);

		updateFollowUpStatus(contact1, FollowUpStatus.FOLLOW_UP);
		for (FollowUpStatus status : FollowUpStatus.values()) {
			updateFollowUpStatus(case1, status);
			updateFollowUpStatus(contact2, status);
			// Other states must not interfere one with ongoing follow up
			assertThat(getPersonFacade().getMostRelevantFollowUpStatusByUuid(person.getUuid()), is(FollowUpStatus.FOLLOW_UP));
			updateFollowUpStatus(contact1, status);
			if (FollowUpStatus.COMPLETED.equals(status) || FollowUpStatus.NO_FOLLOW_UP.equals(status)) {
				// In this case the status is automatically updated to FOLLOW_UP, because the end of the follow up period has not yet been reached.
				assertThat(getPersonFacade().getMostRelevantFollowUpStatusByUuid(person.getUuid()), is(FollowUpStatus.FOLLOW_UP));
				updateFollowUpStatus(contact1, FollowUpStatus.FOLLOW_UP);
				continue;
			}
			// In that case one clear status can be calculated
			assertThat(getPersonFacade().getMostRelevantFollowUpStatusByUuid(person.getUuid()), is(status));
			updateFollowUpStatus(case1, FollowUpStatus.FOLLOW_UP);
			// Also ongoing case follow up must not be overwritten
			assertThat(getPersonFacade().getMostRelevantFollowUpStatusByUuid(person.getUuid()), is(FollowUpStatus.FOLLOW_UP));
			updateFollowUpStatus(case1, status);
			updateFollowUpStatus(contact1, FollowUpStatus.FOLLOW_UP);
		}

		updateFollowUpStatus(contact1, FollowUpStatus.LOST);
		updateFollowUpStatus(contact2, FollowUpStatus.CANCELED);
		updateFollowUpStatus(case1, FollowUpStatus.LOST);
		assertThat(getPersonFacade().getMostRelevantFollowUpStatusByUuid(person.getUuid()), is(FollowUpStatus.NO_FOLLOW_UP));

	}

	private void updateFollowUpStatus(ContactDto contact, FollowUpStatus status) {
		contact = getContactFacade().getContactByUuid(contact.getUuid());
		contact.setFollowUpStatus(status);
		getContactFacade().saveContact(contact);
	}

	private void updateFollowUpStatus(CaseDataDto caze, FollowUpStatus status) {
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		caze.setFollowUpStatus(status);
		getCaseFacade().saveCase(caze);
	}
}
