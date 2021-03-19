package de.symeda.sormas.backend.externalsurveillancetool;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.apache.http.HttpStatus;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
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
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolFacade;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

public class ExternalSurveillanceToolGatewayFacadeEjbTest extends AbstractBeanTest {

	private static final int WIREMOCK_TESTING_PORT = 7777;

	private ExternalSurveillanceToolFacade subjectUnderTest;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(options().port(WIREMOCK_TESTING_PORT), false);

	@Before
	public void setup() {
		configureExternalSurvToolUrlForWireMock();
		subjectUnderTest = getExternalSurveillanceToolGatewayFacade();
	}

	@Test
	public void testFeatureIsEnabledWhenExternalSurvToolUrlIsSet() {
		assertTrue(subjectUnderTest.isFeatureEnabled());
	}

	@Test
	public void testFeatureIsDisabledWhenExternalSurvToolUrlIsEmpty() {
		MockProducer.getProperties().setProperty("survnet.url", "");
		assertFalse(subjectUnderTest.isFeatureEnabled());
	}

	@Test
	public void testSendingCasesOk() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		CaseDataDto case1 = creator.createCase(user, rdcf, null);
		CaseDataDto case2 = creator.createCase(user, rdcf, null);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(case1.getUuid()))
				.withRequestBody(containing(case2.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		subjectUnderTest.sendCases(Arrays.asList(case1.getUuid(), case2.getUuid()));

		assertThat(getExternalShareInfoFacade().getIndexList(new ExternalShareInfoCriteria().caze(case1.toReference()), 0, 100), hasSize(1));
		assertThat(getExternalShareInfoFacade().getIndexList(new ExternalShareInfoCriteria().caze(case2.toReference()), 0, 100), hasSize(1));
	}

	@Test
	public void testSendingCasesNotOk() {
		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY"))
				.withRequestBody(containing("VXAERX-5RCKFA-G5DVXH-DPHPCAFB"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		try {
			subjectUnderTest.sendCases(Arrays.asList("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY", "test-not-found"));
		} catch (ExternalSurveillanceToolException e) {
			assertThat(e.getMessage(), Matchers.is("ExternalSurveillanceToolGateway.notificationErrorSending"));
		}
	}

	@Test
	public void testSendingEventsOk() throws ExternalSurveillanceToolException {
		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY"))
				.withRequestBody(containing("VXAERX-5RCKFA-G5DVXH-DPHPCAFB"))
				.withRequestBody(containing("eventUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		subjectUnderTest.sendEvents(Arrays.asList("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY", "VXAERX-5RCKFA-G5DVXH-DPHPCAFB"));
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

	private void configureExternalSurvToolUrlForWireMock() {
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
