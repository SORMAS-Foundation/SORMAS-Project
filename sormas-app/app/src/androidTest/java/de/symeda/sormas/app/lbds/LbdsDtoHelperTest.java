package de.symeda.sormas.app.lbds;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.hzi.sormas.lbds.core.http.HttpResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.gson.Gson;
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

	/**
	 * The classes in org.hzi.sormas.** need to be excluded from minifaction.
	 * Otherwise gson will not be able to properly parse the incoming message.
	 */
	@Test
	public void testLbdsExcludedFromMinification() {

		String headers =
			"Server\u003dnginx\nDate\u003dFri, 08 Jul 2022 19:03:18 GMT\nContent-Type\u003dapplication/json;charset\u003dUTF-8\nContent-Length\u003d42\nConnection\u003dkeep-alive\nX-Frame-Options\u003dSAMEORIGIN\nStrict-Transport-Security\u003dmax-age\u003d31536000; includeSubDomains\nX-XSS-Protection\u003d1; mode\u003dblock\nReferrer-Policy\u003dno-referrer\nX-Content-Type-Options\u003dnosniff\nAuthorization\u003d Basic SGVpbk1laWU6eHFRV2VqQTNDVDJp\n";
		String body = "[\"TRANSACTION_ROLLED_BACK_EXCEPTION\",\"OK\"]";
		HttpResult httpResult = new HttpResult(200, headers, body);

		String serializedHttpResult = new Gson().toJson(httpResult);
		System.out.println(serializedHttpResult);

		HttpResult deserializedHttpResult = new Gson().fromJson(serializedHttpResult, HttpResult.class);

		assertEquals(httpResult.body, deserializedHttpResult.body);
		assertEquals(httpResult.headers, deserializedHttpResult.headers);

		assertThat(deserializedHttpResult.headers, is(httpResult.headers));
	}
}
