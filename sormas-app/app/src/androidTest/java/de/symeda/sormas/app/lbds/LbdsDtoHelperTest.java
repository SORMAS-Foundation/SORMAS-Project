package de.symeda.sormas.app.lbds;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
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

		assertThat(personDto.getFirstName(), is("Klaus"));
		assertThat(personDto.getLastName(), is("Kinski"));
		assertThat(personDto.getSex(), is(Sex.MALE));

		Person person2 = TestEntityCreator.createPerson("Werner", "Herzog", Sex.MALE, 1942, 9, 5);
		PersonDto personDto2 = new PersonDtoHelper().adoToDto(person2);
		LbdsDtoHelper.stripLbdsDto(personDto2);
		assertNull(personDto2.getBirthdateYYYY());
		assertNull(personDto2.getBirthdateMM());
		assertNull(personDto2.getBirthdateDD());
	}

	@Test
	public void testModifiedCasesLbds() throws IllegalAccessException, IntrospectionException, InvocationTargetException {

		Case caze = TestEntityCreator.createCase();
		CaseDataDto caseDataDto = new CaseDtoHelper().adoToDto(caze);
		caseDataDto.setAdditionalDetails("Some additional detail");
		LbdsDtoHelper.stripLbdsDto(caseDataDto);

		assertThat(caseDataDto.getResponsibleRegion().getUuid(), is(caze.getResponsibleRegion().getUuid()));
		assertThat(caseDataDto.getResponsibleDistrict().getUuid(), is(caze.getResponsibleDistrict().getUuid()));
		assertNull(caseDataDto.getAdditionalDetails());
	}
}
