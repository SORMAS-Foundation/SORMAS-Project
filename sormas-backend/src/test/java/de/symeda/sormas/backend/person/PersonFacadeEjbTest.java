package de.symeda.sormas.backend.person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	public void testGetRelevantNameDtos() throws Exception {
		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);
		
		// 1-3 = Active persons; 4 = Person without reference; 5-7 = Inactive persons
		PersonDto[] persons = creator.createPersons(7);
		
		CaseDataDto activeCase = creator.createCase(user.toReference(), persons[0].toReference(), rdcf);
		creator.createContact(user.toReference(), persons[1].toReference(), activeCase);
		EventDto activeEvent = creator.createEvent(user.toReference());
		creator.createEventParticipant(activeEvent.toReference(), persons[2]);
		
		CaseDataDto inactiveCase = creator.createCase(user.toReference(), persons[4].toReference(), rdcf);
		creator.createContact(user.toReference(), persons[5].toReference(), inactiveCase);
		EventDto inactiveEvent = creator.createEvent(user.toReference());
		creator.createEventParticipant(inactiveEvent.toReference(), persons[6]);
		
		getCaseFacade().archiveOrDearchiveCase(inactiveCase.getUuid(), true);
		getEventFacade().archiveOrDearchiveEvent(inactiveEvent.getUuid(), true);
		
		List<String> relevantNameDtoUuids = getPersonFacade().getRelevantNameDtos(user.toReference()).stream().map(dto -> dto.getUuid()).collect(Collectors.toList());
		assertThat(relevantNameDtoUuids, hasSize(3));
		assertThat(relevantNameDtoUuids, containsInAnyOrder(persons[0].getUuid(), persons[1].getUuid(), persons[2].getUuid()));
	}

	@Test
	public void testGetMatchingPersons() throws Exception {
		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);
		
		PersonDto person1 = creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1);
		PersonDto person2 = creator.createPerson("James", "Smith", Sex.MALE, 1979, 5, 12);
		PersonDto person3 = creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 5);
		PersonDto person4 = creator.createPerson("Maria", "Garcia", Sex.FEMALE, 1984, 12, 2);
		PersonDto person5 = creator.createPerson("Maria", "Garcia", null, 1984, 7, 12);
		PersonDto person6 = creator.createPerson("James", "Smith", Sex.MALE, null, null, null);
		List<String> personUuids = Arrays.asList(person1.getUuid(), person2.getUuid(), person3.getUuid(),
				person4.getUuid(), person5.getUuid(), person6.getUuid());
		
		PersonSimilarityCriteria criteria = new PersonSimilarityCriteria().sex(Sex.MALE).birthdateYYYY(1980).birthdateMM(1).birthdateDD(1);
		List<String> matchingPersonUuids = getPersonFacade().getMatchingPersons(personUuids, criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingPersonUuids, hasSize(2));
		assertThat(matchingPersonUuids, containsInAnyOrder(person1.getUuid(), person6.getUuid()));
	
		criteria.birthdateMM(null).birthdateDD(null);
		matchingPersonUuids = getPersonFacade().getMatchingPersons(personUuids, criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingPersonUuids, hasSize(3));
		assertThat(matchingPersonUuids, containsInAnyOrder(person1.getUuid(), person3.getUuid(), person6.getUuid()));
		
		criteria.sex(Sex.FEMALE).birthdateYYYY(1984);
		matchingPersonUuids = getPersonFacade().getMatchingPersons(personUuids, criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingPersonUuids, hasSize(2));
		assertThat(matchingPersonUuids, containsInAnyOrder(person4.getUuid(), person5.getUuid()));
		
		criteria.sex(null);
		matchingPersonUuids = getPersonFacade().getMatchingPersons(personUuids, criteria).stream().map(person -> person.getUuid()).collect(Collectors.toList());
		assertThat(matchingPersonUuids, hasSize(3));
		assertThat(matchingPersonUuids, containsInAnyOrder(person4.getUuid(), person5.getUuid(), person6.getUuid()));
	}

}
