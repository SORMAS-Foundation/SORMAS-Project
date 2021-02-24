package de.symeda.sormas.backend.survnet;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.apache.http.HttpStatus;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.survnet.SurvnetGatewayFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

public class SurvnetGatewayFacadeEjbTest extends AbstractBeanTest {

	private static final int WIREMOCK_TESTING_PORT = 7777;

	private SurvnetGatewayFacade subjectUnderTest;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(options().port(WIREMOCK_TESTING_PORT), false);

	@Before
	public void setup() {
		configureSurvNetUrlForWireMock();
		subjectUnderTest = getSurvnetGatewayFacade();
	}

	@Test
	public void testFeatureIsEnabledWhenSurvNetUrlIsSet() {
		assertTrue(subjectUnderTest.isFeatureEnabled());
	}

	@Test
	public void testFeatureIsDisabledWhenSurvNetUrlIsEmpty() {
		MockProducer.getProperties().setProperty("survnet.url", "");
		assertFalse(subjectUnderTest.isFeatureEnabled());
	}

	@Test
	public void testSendingCasesOk() {
		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY"))
				.withRequestBody(containing("VXAERX-5RCKFA-G5DVXH-DPHPCAFB"))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		int result = subjectUnderTest.sendCases(Arrays.asList("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY", "VXAERX-5RCKFA-G5DVXH-DPHPCAFB"));
		assertThat(result, CoreMatchers.is(HttpStatus.SC_OK));
	}

	@Test
	public void testSendingCasesNotOk() {
		stubFor(
			post(urlEqualTo("/export"))
					.withRequestBody(containing("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY"))
					.withRequestBody(containing("VXAERX-5RCKFA-G5DVXH-DPHPCAFB"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		int result = subjectUnderTest.sendCases(Arrays.asList("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY", "test-not-found"));
		assertThat(result, CoreMatchers.is(HttpStatus.SC_NOT_FOUND));
	}

	@Test
	public void testSendingEventsOk() {
		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY"))
				.withRequestBody(containing("VXAERX-5RCKFA-G5DVXH-DPHPCAFB"))
				.withRequestBody(containing("eventUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		int result = subjectUnderTest.sendEvents(Arrays.asList("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY", "VXAERX-5RCKFA-G5DVXH-DPHPCAFB"));
		assertThat(result, CoreMatchers.is(HttpStatus.SC_OK));
	}

	@Test
	public void testDeleteEvents() {
		stubFor(
			post(urlEqualTo("/delete")).withRequestBody(containing("Test"))
				.withRequestBody(containing("Description"))
				.withRequestBody(containing("John"))
				.withRequestBody(containing("Doe"))
				.withRequestBody(containing("123456"))
				.withRequestBody(containing("events"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		int result = subjectUnderTest.deleteEvents(Collections.singletonList(createEventDto("Test", "Description", "John", "Doe", "123456")));
		assertThat(result, CoreMatchers.is(HttpStatus.SC_OK));
	}

	@Test
	public void testDeleteEventsNotFound() {
		stubFor(
			post(urlEqualTo("/delete")).withRequestBody(containing("Test"))
				.withRequestBody(containing("Description"))
				.withRequestBody(containing("John"))
				.withRequestBody(containing("Doe"))
				.withRequestBody(containing("123456"))
				.withRequestBody(containing("events"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		int result = subjectUnderTest.deleteEvents(Collections.singletonList(createEventDto("xyz", "nope", "Jane", "D", "111222333")));
		assertThat(result, CoreMatchers.is(HttpStatus.SC_NOT_FOUND));
	}

	@Test
	public void testDeleteCases() {
		stubFor(
			post(urlEqualTo("/delete")).withRequestBody(containing("James"))
				.withRequestBody(containing("Smith"))
				.withRequestBody(containing("cases"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		int result = subjectUnderTest.deleteCases(Collections.singletonList(createCaseDataDto()));
		assertThat(result, CoreMatchers.is(HttpStatus.SC_OK));
	}

	private void configureSurvNetUrlForWireMock() {
		MockProducer.getProperties().setProperty("survnet.url", String.format("http://localhost:%s", WIREMOCK_TESTING_PORT));
	}

	private EventDto createEventDto(
		final String eventTitle,
		final String description,
		final String firstName,
		final String lastName,
		final String tel) {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		return creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			eventTitle,
			description,
			firstName,
			lastName,
			tel,
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf.district);
	}

	private CaseDataDto createCaseDataDto() {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto personDto = creator.createPerson("James", "Smith", p -> {
			LocationDto homeAddress = p.getAddress();
			homeAddress.setAddressType(PersonAddressType.HOME);
			homeAddress.setStreet("Home street");
			homeAddress.setHouseNumber("11A");
			homeAddress.setCity("Home city");
			homeAddress.setPostalCode("12345");

			p.setPhone("12345678");
			p.setEmailAddress("test@email.com");
			p.setSex(Sex.MALE);

			p.setBirthdateYYYY(1978);
			p.setBirthdateMM(10);
			p.setBirthdateDD(22);

			p.setOccupationType(OccupationType.ACCOMMODATION_AND_FOOD_SERVICES);

			LocationDto workPlaceAddress = LocationDto.build();
			workPlaceAddress.setAddressType(PersonAddressType.PLACE_OF_WORK);
			workPlaceAddress.setStreet("Work street");
			workPlaceAddress.setHouseNumber("12W");
			workPlaceAddress.setCity("Work city");
			workPlaceAddress.setPostalCode("54321");

			p.getAddresses().add(workPlaceAddress);

			LocationDto exposureAddress = LocationDto.build();
			exposureAddress.setAddressType(PersonAddressType.PLACE_OF_EXPOSURE);
			exposureAddress.setStreet("Exposure street");
			exposureAddress.setHouseNumber("13E");
			exposureAddress.setCity("Exposure city");
			exposureAddress.setPostalCode("098765");

			p.getAddresses().add(exposureAddress);

			LocationDto isolationAddress = LocationDto.build();
			isolationAddress.setAddressType(PersonAddressType.PLACE_OF_ISOLATION);
			isolationAddress.setStreet("Isolation street");
			isolationAddress.setHouseNumber("14I");
			isolationAddress.setCity("Isolation city");
			isolationAddress.setPostalCode("76543");

			p.getAddresses().add(isolationAddress);
		});
		return creator.createCase(user.toReference(), personDto.toReference(), rdcf);
	}
}
