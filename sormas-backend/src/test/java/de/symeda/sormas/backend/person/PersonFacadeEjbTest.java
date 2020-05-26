package de.symeda.sormas.backend.person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class PersonFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetMatchingNameDtos() throws Exception {
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
		creator.createEventParticipant(activeEvent.toReference(), person3);
		
		CaseDataDto inactiveCase = creator.createCase(user.toReference(), person5.toReference(), rdcf);
		creator.createContact(user.toReference(), person6.toReference(), inactiveCase);
		EventDto inactiveEvent = creator.createEvent(user.toReference());
		creator.createEventParticipant(inactiveEvent.toReference(), person7);
		
		getCaseFacade().archiveOrDearchiveCase(inactiveCase.getUuid(), true);
		getEventFacade().archiveOrDearchiveEvent(inactiveEvent.getUuid(), true);
		
		// Only persons that have active case, contact or event participant associations should be retrieved
		List<String> relevantNameUuids = getPersonFacade().getMatchingNameDtos(user.toReference(), new PersonSimilarityCriteria()).stream().map(dto -> dto.getUuid()).collect(Collectors.toList());
		assertThat(relevantNameUuids, hasSize(3));
		assertThat(relevantNameUuids, containsInAnyOrder(person1.getUuid(), person2.getUuid(), person3.getUuid()));
		
		creator.createCase(user.toReference(), person4.toReference(), rdcf);
		getCaseFacade().archiveOrDearchiveCase(inactiveCase.getUuid(), false);
		getEventFacade().archiveOrDearchiveEvent(inactiveEvent.getUuid(), false);
		
		PersonSimilarityCriteria criteria = new PersonSimilarityCriteria().sex(Sex.MALE).birthdateYYYY(1980).birthdateMM(1).birthdateDD(1);
		List<String> matchingUuids = getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(2));
		assertThat(matchingUuids, containsInAnyOrder(person1.getUuid(), person7.getUuid()));
	
		criteria.birthdateMM(null).birthdateDD(null);
		matchingUuids = getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(3));
		assertThat(matchingUuids, containsInAnyOrder(person1.getUuid(), person3.getUuid(), person7.getUuid()));
		
		criteria.sex(Sex.FEMALE).birthdateYYYY(1984);
		matchingUuids = getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(3));
		assertThat(matchingUuids, containsInAnyOrder(person4.getUuid(), person5.getUuid(), person6.getUuid()));
		
		criteria.sex(null);
		matchingUuids = getPersonFacade().getMatchingNameDtos(user.toReference(), criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingUuids, hasSize(4));
		assertThat(matchingUuids, containsInAnyOrder(person4.getUuid(), person5.getUuid(), person6.getUuid(), person7.getUuid()));
	}

	@Test
	public void testIsValidPersonUuid() {
		final PersonDto person = creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1);
		Assert.assertTrue(getPersonFacade().isValidPersonUuid(person.getUuid()));
		Assert.assertFalse(getPersonFacade().isValidPersonUuid("2341235234534"));
	}
}
