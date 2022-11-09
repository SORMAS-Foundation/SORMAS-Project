package de.symeda.sormas.backend.externalsurveillancetool;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.share.ExternalShareInfoDto;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventService;

@WireMockTest(httpPort = 8888)
public class ExternalSurveillanceToolGatewayFacadeEjbTest extends AbstractBeanTest {


	@BeforeEach
	public void setup(WireMockRuntimeInfo wireMockRuntime) {
		configureExternalSurvToolUrlForWireMock(wireMockRuntime);
	}

	@AfterEach
	public void teardown() {
		clearExternalSurvToolUrlForWireMock();
	}

	@Test
	public void testFeatureIsEnabledWhenExternalSurvToolUrlIsSet() {
		assertTrue(getExternalSurveillanceToolGatewayFacade().isFeatureEnabled());
	}

	@Test
	public void testFeatureIsDisabledWhenExternalSurvToolUrlIsEmpty() {
		MockProducer.getProperties().setProperty("survnet.url", "");
		assertFalse(getExternalSurveillanceToolGatewayFacade().isFeatureEnabled());
	}

	@Test
	public void testSendingCasesOneCaseOk() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto case1 = creator.createCase(user, rdcf, null);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(case1.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getExternalSurveillanceToolGatewayFacade().sendCases(Arrays.asList(case1.getUuid()), true);

		ExternalShareInfoCriteria externalShareInfoCriteria1 = new ExternalShareInfoCriteria().caze(case1.toReference());
		assertThat(getExternalShareInfoFacade().getIndexList(externalShareInfoCriteria1, 0, 100), hasSize(1));
	}

	@Test
	public void testSendingCasesOk() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto case1 = creator.createCase(user, rdcf, null);
		CaseDataDto case2 = creator.createCase(user, rdcf, null);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(case1.getUuid()))
				.withRequestBody(containing(case2.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getExternalSurveillanceToolGatewayFacade().sendCases(Arrays.asList(case1.getUuid(), case2.getUuid()), true);

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
			getExternalSurveillanceToolGatewayFacade().sendCases(Arrays.asList("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY", "test-not-found"), true);
		} catch (ExternalSurveillanceToolException e) {
			assertThat(e.getMessage(), Matchers.is(I18nProperties.getString("ExternalSurveillanceToolGateway.notificationErrorSending")));
		}
	}

	@Test
	public void testSendingEventsOk() throws ExternalSurveillanceToolException {
		EventDto event1 = createEventDto("event1", "description1", "Event1", "Event1", "123");

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(event1.getUuid()))
				.withRequestBody(containing("eventUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		getExternalSurveillanceToolGatewayFacade().sendEvents(Arrays.asList(event1.getUuid()), true);

		assertThat(getExternalShareInfoFacade().getIndexList(new ExternalShareInfoCriteria().event(event1.toReference()), 0, 100), hasSize(1));
	}

	@Test
	public void testDeleteEvents() throws ExternalSurveillanceToolException {
		stubFor(
			post(urlEqualTo("/delete")).withRequestBody(containing("Test"))
				.withRequestBody(containing("Description"))
				.withRequestBody(containing("John"))
				.withRequestBody(containing("Doe"))
				.withRequestBody(containing("123456"))
				.withRequestBody(containing("events"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		EventDto event = createEventDto("Test", "Description", "John", "Doe", "123456");

		getExternalSurveillanceToolGatewayFacade().deleteEvents(Collections.singletonList(event));

		List<ExternalShareInfoDto> shareInfoList =
			getExternalShareInfoFacade().getIndexList(new ExternalShareInfoCriteria().event(event.toReference()), 0, 100);

		assertThat(shareInfoList, hasSize(1));
		assertThat(shareInfoList.get(0).getStatus(), is(ExternalShareStatus.DELETED));

	}

	@Test
	public void testDeleteEventsNotFound() throws ExternalSurveillanceToolException {
		stubFor(
			post(urlEqualTo("/delete")).withRequestBody(containing("xyz"))
				.withRequestBody(containing("nope"))
				.withRequestBody(containing("Jane"))
				.withRequestBody(containing("D"))
				.withRequestBody(containing("111222333"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		EventDto event = createEventDto("xyz", "nope", "Jane", "D", "111222333");
		getExternalSurveillanceToolGatewayFacade().deleteEvents(Collections.singletonList(event));

		List<ExternalShareInfoDto> shareInfoList =
			getExternalShareInfoFacade().getIndexList(new ExternalShareInfoCriteria().event(event.toReference()), 0, 100);

		assertThat(shareInfoList, hasSize(1));
		assertThat(shareInfoList.get(0).getStatus(), is(ExternalShareStatus.DELETED));

	}

	@Test
	public void testDeleteCases() throws ExternalSurveillanceToolException {
		stubFor(
			post(urlEqualTo("/delete")).withRequestBody(containing("James"))
				.withRequestBody(containing("Smith"))
				.withRequestBody(containing("cases"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		CaseDataDto caze = createCaseDataDto();

		getExternalSurveillanceToolGatewayFacade().deleteCases(Collections.singletonList(caze));

		List<ExternalShareInfoDto> shareInfoList =
			getExternalShareInfoFacade().getIndexList(new ExternalShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(shareInfoList, hasSize(1));
		assertThat(shareInfoList.get(0).getStatus(), is(ExternalShareStatus.DELETED));
	}


	@Test
	public void testSetArchiveInExternalSurveillanceToolForCase_WithProperEntity(WireMockRuntimeInfo wireMockRuntime) {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person = creator.createPerson("Walter", "Schuster").toReference();

		CaseDataDto caze = creator.createCase(user, person, rdcf);
		Case case1 = getCaseService().getByUuid(caze.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(case1, ExternalShareStatus.SHARED);

		stubFor(
				post(urlEqualTo("/export")).withRequestBody(containing(caze.getUuid()))
						.withRequestBody(containing("caseUuids"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getCaseService().setArchiveInExternalSurveillanceToolForEntity(caze.getUuid(), true);

		wireMockRuntime.getWireMock().verify(exactly(1), postRequestedFor(urlEqualTo("/export")));
	}

	@Test
	public void testSetArchiveInExternalSurveillanceToolForCase_WithoutProperEntity(WireMockRuntimeInfo wireMockRuntime) {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person = creator.createPerson("Walter", "Schuster").toReference();

		CaseDataDto caze = creator.createCase(user, person, rdcf);

		stubFor(
				post(urlEqualTo("/export")).withRequestBody(containing(caze.getUuid()))
						.withRequestBody(containing("caseUuids"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		//the case does not have an externalId set and after the filtering the sendCases will not be called
		getCaseService().setArchiveInExternalSurveillanceToolForEntity(caze.getUuid(), true);
		wireMockRuntime.getWireMock().verify(exactly(0), postRequestedFor(urlEqualTo("/export")));
	}

	@Test
	public void testSetArchiveInExternalSurveillanceToolForCase_Exception() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person = creator.createPerson("Walter", "Schuster").toReference();

		CaseDataDto caseDataDto = creator.createCase(user, person, rdcf);
		Case caze = getCaseService().getByUuid(caseDataDto.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(caze, ExternalShareStatus.SHARED);

		stubFor(
				post(urlEqualTo("/export")).withRequestBody(containing(caseDataDto.getUuid()))
						.withRequestBody(containing("caseUuids"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));

		assertThrows(
				ExternalSurveillanceToolRuntimeException.class,
				() -> getCaseService().setArchiveInExternalSurveillanceToolForEntity(caze.getUuid(), true));
	}


	@Test
	public void testSetArchiveInExternalSurveillanceToolForEvent_WithProperEntity(WireMockRuntimeInfo wireMockRuntime) {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();

		EventDto eventDto = creator.createEvent(
				EventStatus.SIGNAL,
				EventInvestigationStatus.PENDING,
				"",
				"",
				"",
				"",
				"",
				TypeOfPlace.FACILITY,
				new Date(),
				new Date(),
				user,
				user,
				Disease.DENGUE,
				rdcf);

		Event event = getEventService().getByUuid(eventDto.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(event, ExternalShareStatus.SHARED);
		EventService eventService = getBean(EventService.class);

		stubFor(
				post(urlEqualTo("/export")).withRequestBody(containing(event.getUuid()))
						.withRequestBody(containing("eventUuids"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		eventService.setArchiveInExternalSurveillanceToolForEntity(event.getUuid(), false);
		wireMockRuntime.getWireMock().verify(exactly(1), postRequestedFor(urlEqualTo("/export")));
	}

	@Test
	public void testSetArchiveInExternalSurveillanceToolForEvent_Exception() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();

		EventDto eventDto = creator.createEvent(
				EventStatus.SIGNAL,
				EventInvestigationStatus.PENDING,
				"",
				"",
				"",
				"",
				"",
				TypeOfPlace.FACILITY,
				new Date(),
				new Date(),
				user,
				user,
				Disease.DENGUE,
				rdcf);

		Event event = getEventService().getByUuid(eventDto.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(event, ExternalShareStatus.SHARED);

		EventService eventService = getBean(EventService.class);

		stubFor(
				post(urlEqualTo("/export")).withRequestBody(containing(eventDto.getUuid()))
						.withRequestBody(containing("eventUuids"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));

		assertThrows(
				ExternalSurveillanceToolRuntimeException.class,
				() -> eventService.setArchiveInExternalSurveillanceToolForEntity(eventDto.getUuid(), true));
	}

	@Test
	public void testIsSharedEntity() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto case1 = creator.createCase(user, rdcf, null);
		CaseDataDto case2 = creator.createCase(user, rdcf, null);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(case1.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getExternalSurveillanceToolGatewayFacade().sendCases(Arrays.asList(case1.getUuid()), false);

		boolean shared = getExternalShareInfoFacade().isSharedEntity(case1.getUuid());
		assertTrue(shared);
		shared = getExternalShareInfoFacade().isSharedEntity(case2.getUuid());
		assertFalse(shared);
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
			rdcf);
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

	private void configureExternalSurvToolUrlForWireMock(WireMockRuntimeInfo wireMockRuntime) {
		MockProducer.getProperties().setProperty("survnet.url", String.format("http://localhost:%s", wireMockRuntime.getHttpPort()));
	}

	private void clearExternalSurvToolUrlForWireMock() {
		MockProducer.getProperties().setProperty("survnet.url", "");
	}
}
