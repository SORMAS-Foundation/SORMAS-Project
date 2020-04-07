package de.symeda.sormas.app.backend.person;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.app.TestBackendActivity;
import de.symeda.sormas.app.TestEntityCreator;
import de.symeda.sormas.app.TestHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PersonDaoTest {

    @Rule
    public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

    @Before
    public void initTest() {
        TestHelper.initTestEnvironment(false);
    }

    @Test
    public void getRelevantPersonNames() {
        Person person1 = TestEntityCreator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1);
        Person person2 = TestEntityCreator.createPerson("James", "Smith", Sex.MALE, 1979, 5, 12);
        Person person3 = TestEntityCreator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 5);
        Person person4 = TestEntityCreator.createPerson("Maria", "Garcia", Sex.FEMALE, 1984, 12, 2);
        Person person5 = TestEntityCreator.createPerson("Maria", "Garcia", null, 1984, 7, 12);
        Person person6 = TestEntityCreator.createPerson("Maria", "Garcia", Sex.FEMALE, 1984, null, null);
        Person person7 = TestEntityCreator.createPerson("James", "Smith", Sex.MALE, null, null, null);

        PersonSimilarityCriteria criteria = new PersonSimilarityCriteria().sex(Sex.MALE).birthdateYYYY(1980).birthdateMM(1).birthdateDD(1);
        List<String> matchingPersonUuids = DatabaseHelper.getPersonDao().getRelevantPersonNames(criteria).stream().map(PersonNameDto::getUuid).collect(Collectors.toList());
        assertThat(matchingPersonUuids, hasSize(2));
        assertThat(matchingPersonUuids, containsInAnyOrder(person1.getUuid(), person7.getUuid()));

        criteria.birthdateMM(null).birthdateDD(null);
        matchingPersonUuids = DatabaseHelper.getPersonDao().getRelevantPersonNames(criteria).stream().map(PersonNameDto::getUuid).collect(Collectors.toList());
        assertThat(matchingPersonUuids, hasSize(3));
        assertThat(matchingPersonUuids, containsInAnyOrder(person1.getUuid(), person3.getUuid(), person7.getUuid()));

        criteria.sex(Sex.FEMALE).birthdateYYYY(1984);
        matchingPersonUuids = DatabaseHelper.getPersonDao().getRelevantPersonNames(criteria).stream().map(PersonNameDto::getUuid).collect(Collectors.toList());
        assertThat(matchingPersonUuids, hasSize(3));
        assertThat(matchingPersonUuids, containsInAnyOrder(person4.getUuid(), person5.getUuid(), person6.getUuid()));

        criteria.sex(null);
        matchingPersonUuids = DatabaseHelper.getPersonDao().getRelevantPersonNames(criteria).stream().map(PersonNameDto::getUuid).collect(Collectors.toList());
        assertThat(matchingPersonUuids, hasSize(4));
        assertThat(matchingPersonUuids, containsInAnyOrder(person4.getUuid(), person5.getUuid(), person6.getUuid(), person7.getUuid()));
    }

}