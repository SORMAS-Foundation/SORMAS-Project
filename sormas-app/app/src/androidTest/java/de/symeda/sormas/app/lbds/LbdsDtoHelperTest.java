package de.symeda.sormas.app.lbds;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.googlecode.openbeans.IntrospectionException;

import androidx.test.rule.ActivityTestRule;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.app.TestBackendActivity;
import de.symeda.sormas.app.TestEntityCreator;
import de.symeda.sormas.app.TestHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;

public class LbdsDtoHelperTest {

	@Rule
	public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

	@Before
	public void initTest() {
		TestHelper.initTestEnvironment(false);
	}

	@Test
	public void testModifiedPersonsLbds() throws IllegalAccessException, IntrospectionException, InvocationTargetException {

		Person person = TestEntityCreator.createPerson("Klaus", "Kinski", Sex.MALE, null, null, null);
		PersonDto personDto = new PersonDtoHelper().adoToDto(person);
		LbdsDtoHelper.stripLbdsDto(personDto);
		assertThat(LbdsDtoHelper.isModifiedLbds(person, personDto, true), is(false));

		Person person2 = TestEntityCreator.createPerson("Werner", "Herzog", Sex.MALE, 1942, 9, 5);
		PersonDto personDto2 = new PersonDtoHelper().adoToDto(person2);
		LbdsDtoHelper.stripLbdsDto(personDto2);
		assertThat(LbdsDtoHelper.isModifiedLbds(person2, personDto2, true), is(true));
		assertThat(LbdsDtoHelper.isModifiedLbds(person2, personDto2, false), is(false));

		personDto2.setLastName("Kinski");
		assertThat(LbdsDtoHelper.isModifiedLbds(person2, personDto2, false), is(true));
	}

	@Test
	public void testModifiedCasesLbds() throws IllegalAccessException, IntrospectionException, InvocationTargetException {
		Case caze = TestEntityCreator.createCase();
		CaseDataDto caseDataDto = new CaseDtoHelper().adoToDto(caze);
		LbdsDtoHelper.stripLbdsDto(caseDataDto);
		assertThat(LbdsDtoHelper.isModifiedLbds(caze, caseDataDto, true), is(false));
	}
}
