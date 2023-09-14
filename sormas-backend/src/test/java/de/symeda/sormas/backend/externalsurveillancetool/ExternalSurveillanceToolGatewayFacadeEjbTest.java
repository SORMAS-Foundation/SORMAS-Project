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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.share.ExternalShareInfoDto;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.entities.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfiguration;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasTest;
import de.symeda.sormas.backend.sormastosormas.share.ShareRequestAcceptData;

@WireMockTest(httpPort = 8888)
public class ExternalSurveillanceToolGatewayFacadeEjbTest extends SormasToSormasTest {

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
		UserReferenceDto user = creator.createSurveillanceOfficer(rdcf).toReference();
		CaseDataDto case1 = creator.createCase(user, rdcf, null);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(case1.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getExternalSurveillanceToolGatewayFacade().sendCases(Arrays.asList(case1.getUuid()));

		ExternalShareInfoCriteria externalShareInfoCriteria1 = new ExternalShareInfoCriteria().caze(case1.toReference());
		assertThat(getExternalShareInfoFacade().getIndexList(externalShareInfoCriteria1, 0, 100), hasSize(1));
	}

	@Test
	public void testSendingCasesOk() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createSurveillanceOfficer(rdcf).toReference();
		CaseDataDto case1 = creator.createCase(user, rdcf, null);
		CaseDataDto case2 = creator.createCase(user, rdcf, null);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(case1.getUuid()))
				.withRequestBody(containing(case2.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getExternalSurveillanceToolGatewayFacade().sendCases(Arrays.asList(case1.getUuid(), case2.getUuid()));

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
			getExternalSurveillanceToolGatewayFacade().sendCases(Arrays.asList("XRJOEJ-P2OY5E-CA5MYT-LSVCCGVY", "test-not-found"));
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
		getExternalSurveillanceToolGatewayFacade().sendEvents(Arrays.asList(event1.getUuid()));

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
	public void testSetArchiveInExternalSurveillanceToolForCase_WithProperEntity_WithCaseAllowedToShare(WireMockRuntimeInfo wireMockRuntime) {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person = creator.createPerson("Walter", "Schuster").toReference();

		CaseDataDto caze = creator.createCase(user, person, rdcf, c -> c.setDontShareWithReportingTool(false));
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
	public void testSetArchiveInExternalSurveillanceToolForCase_WithProperEntity_WithoutCaseAllowedToShare(WireMockRuntimeInfo wireMockRuntime) {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person = creator.createPerson("Walter", "Schuster").toReference();

		CaseDataDto caze = creator.createCase(user, person, rdcf, c -> c.setDontShareWithReportingTool(true));
		Case case1 = getCaseService().getByUuid(caze.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(case1, ExternalShareStatus.SHARED);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(caze.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getCaseService().setArchiveInExternalSurveillanceToolForEntity(caze.getUuid(), true);

		wireMockRuntime.getWireMock().verify(exactly(0), postRequestedFor(urlEqualTo("/export")));
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
	public void testIsSharedCase() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createSurveillanceOfficer(rdcf).toReference();
		CaseDataDto case1 = creator.createCase(user, rdcf, null);
		CaseDataDto case2 = creator.createCase(user, rdcf, null);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(case1.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));
		stubFor(
			post(urlEqualTo("/delete")).withRequestBody(containing(case1.getUuid()))
				.withRequestBody(containing("cases"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getExternalSurveillanceToolGatewayFacade().sendCases(Arrays.asList(case1.getUuid()));

		boolean shared = getExternalShareInfoFacade().isSharedCase(case1.getUuid());
		assertTrue(shared);
		shared = getExternalShareInfoFacade().isSharedCase(case2.getUuid());
		assertFalse(shared);

		getExternalSurveillanceToolGatewayFacade().deleteCases(Arrays.asList(case1));
		shared = getExternalShareInfoFacade().isSharedCase(case1.getUuid());
		assertFalse(shared);

		getExternalSurveillanceToolGatewayFacade().sendCases(Arrays.asList(case1.getUuid()));
		shared = getExternalShareInfoFacade().isSharedCase(case1.getUuid());
		assertTrue(shared);
	}

	@Test
	public void testIsSharedEvent() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf, c -> c.setDontShareWithReportingTool(true));

		EventDto event1 = creator.createEvent(user.toReference(), caze.getDisease());
		EventDto event2 = creator.createEvent(user.toReference(), caze.getDisease());

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(event1.getUuid()))
				.withRequestBody(containing("eventUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getExternalSurveillanceToolGatewayFacade().sendEvents(Arrays.asList(event1.getUuid()));

		boolean shared = getExternalShareInfoFacade().isSharedEvent(event1.getUuid());
		assertTrue(shared);

		shared = getExternalShareInfoFacade().isSharedCase(event2.getUuid());
		assertFalse(shared);
	}

	@Test
	public void testArchiveAllArchivableCases_WithNotAllowedCaseToShareWithReportingTool(WireMockRuntimeInfo wireMockRuntime) {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person = creator.createPerson("Walter", "Schuster").toReference();

		CaseFacadeEjb.CaseFacadeEjbLocal cut = getBean(CaseFacadeEjb.CaseFacadeEjbLocal.class);
		CaseDataDto case2 = creator.createCase(user, person, rdcf, c -> c.setDontShareWithReportingTool(true));
		assertFalse(cut.isArchived(case2.getUuid()));

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(case2.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		// Case of "yesterday" should be archived
		cut.archiveAllArchivableCases(70, LocalDate.now().plusDays(71));
		assertTrue(cut.isArchived(case2.getUuid()));
		wireMockRuntime.getWireMock().verify(exactly(0), postRequestedFor(urlEqualTo("/export")));

	}

	@Test
	public void testCaseDeletionAndRestoration_WithoutCaseAllowedToBeSharedWithReportingTool(WireMockRuntimeInfo wireMockRuntime)
		throws ExternalSurveillanceToolRuntimeException {
		Date since = new Date();

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		UserDto admin = getUserFacade().getByUserName("AdMin");

		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			c -> c.setDontShareWithReportingTool(true));

		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		TaskDto task = creator.createTask(
			TaskContext.CASE,
			TaskType.CASE_INVESTIGATION,
			TaskStatus.PENDING,
			caze.toReference(),
			null,
			null,
			new Date(),
			user.toReference());
		SampleDto sample = creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		SampleDto sampleAssociatedToContactAndCase =
			creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		ContactDto contact2 =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		sampleAssociatedToContactAndCase.setAssociatedContact(new ContactReferenceDto(contact2.getUuid()));
		getSampleFacade().saveSample(sampleAssociatedToContactAndCase);

		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), caze);
		AdditionalTestDto additionalTest = creator.createAdditionalTest(sample.toReference());

		// Database should contain the created case, contact, task and sample
		assertNotNull(getCaseFacade().getCaseDataByUuid(caze.getUuid()));
		assertNotNull(getContactFacade().getByUuid(contact.getUuid()));
		assertNotNull(getSampleFacade().getSampleByUuid(sample.getUuid()));
		assertNotNull(getPathogenTestFacade().getByUuid(pathogenTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(additionalTest.getUuid()));
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(caze.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getCaseFacade().delete(caze.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));
		wireMockRuntime.getWireMock().verify(exactly(0), postRequestedFor(urlEqualTo("/export")));

		// Deleted flag should be set for case, sample and pathogen test; Additional test should be deleted; Contact should not have the deleted flag; Task should not be deleted
		assertTrue(getCaseFacade().getDeletedUuidsSince(since).contains(caze.getUuid()));
		assertFalse(getContactFacade().getDeletedUuidsSince(since).contains(contact.getUuid()));
		assertTrue(getSampleFacade().getDeletedUuidsSince(since).contains(sample.getUuid()));
		assertFalse(getSampleFacade().getDeletedUuidsSince(since).contains(sampleAssociatedToContactAndCase.getUuid()));
		assertTrue(getPathogenTestFacade().getDeletedUuidsSince(since).contains(pathogenTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(additionalTest.getUuid()));
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));
		assertEquals(DeletionReason.OTHER_REASON, getCaseFacade().getByUuid(caze.getUuid()).getDeletionReason());
		assertEquals("test reason", getCaseFacade().getByUuid(caze.getUuid()).getOtherDeletionReason());

		getCaseFacade().restore(caze.getUuid());

		// Deleted flag should be set for case, sample and pathogen test; Additional test should be deleted; Contact should not have the deleted flag; Task should not be deleted
		assertFalse(getCaseFacade().getDeletedUuidsSince(since).contains(caze.getUuid()));
		assertFalse(getContactFacade().getDeletedUuidsSince(since).contains(contact.getUuid()));
		assertFalse(getSampleFacade().getDeletedUuidsSince(since).contains(sample.getUuid()));
		assertFalse(getSampleFacade().getDeletedUuidsSince(since).contains(sampleAssociatedToContactAndCase.getUuid()));
		assertFalse(getPathogenTestFacade().getDeletedUuidsSince(since).contains(pathogenTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(additionalTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(additionalTest.getUuid()));
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));
		assertNull(getCaseFacade().getByUuid(caze.getUuid()).getDeletionReason());
		assertNull(getCaseFacade().getByUuid(caze.getUuid()).getOtherDeletionReason());
	}

	@Test
	public void testCaseAutomaticDeletion_WithoutCaseAllowedToBeSharedWithReportingTool(WireMockRuntimeInfo wireMockRuntime) throws IOException {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CASE);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf, c -> c.setDontShareWithReportingTool(true));

		creator.createClinicalVisit(caze);
		creator.createTreatment(caze);
		creator.createPrescription(caze);
		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		creator.createSurveillanceReport(user.toReference(), caze.toReference());

		byte[] contentAsBytes =
			("%PDF-1.0\n1 0 obj<</Type/Catalog/Pages " + "2 0 R>>endobj 2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj 3 0 obj<</Ty"
				+ "pe/Page/MediaBox[0 0 3 3]>>endobj\nxref\n0 4\n0000000000 65535 f\n000000001"
				+ "0 00000 n\n0000000053 00000 n\n0000000102 00000 n\ntrailer<</Size 4/Root 1 " + "0 R>>\nstartxref\n149\n%EOF").getBytes();
		creator.createDocument(
			user.toReference(),
			"document.pdf",
			"application/pdf",
			42L,
			DocumentRelatedEntityType.CASE,
			caze.getUuid(),
			contentAsBytes);

		CaseDataDto duplicateCase = creator.createCase(user.toReference(), person.toReference(), rdcf);
		getCaseFacade().deleteAsDuplicate(duplicateCase.getUuid(), caze.getUuid());

		final ContactDto resultingContact = creator.createContact(user.toReference(), person.toReference(), caze);
		assertNull(resultingContact.getRegion());
		ContactDto sourceContact = creator.createContact(
			user.toReference(),
			person.toReference(),
			caze.getDisease(),
			contactDto -> contactDto.setResultingCase(caze.toReference()));
		ContactDto deletedSourceContact = creator.createContact(
			user.toReference(),
			person.toReference(),
			caze.getDisease(),
			contactDto -> contactDto.setResultingCase(caze.toReference()));
		getContactFacade().delete(deletedSourceContact.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));
		EventDto event = creator.createEvent(user.toReference(), caze.getDisease());
		EventParticipantDto eventParticipant = creator.createEventParticipant(
			event.toReference(),
			person,
			"Description",
			user.toReference(),
			eventParticipantDto -> eventParticipantDto.setResultingCase(caze.toReference()),
			rdcf);
		SampleDto multiSample = creator.createSample(
			caze.toReference(),
			user.toReference(),
			rdcf.facility,
			sampleDto -> sampleDto.setAssociatedContact(resultingContact.toReference()));
		TravelEntryDto travelEntry =
			creator.createTravelEntry(person.toReference(), user.toReference(), rdcf, te -> te.setResultingCase(caze.toReference()));
		creator.createTravelEntry(person.toReference(), user.toReference(), rdcf, te -> {
			te.setResultingCase(caze.toReference());
			te.setDeleted(true);
		});
		ImmunizationDto immunization = creator.createImmunization(
			caze.getDisease(),
			person.toReference(),
			user.toReference(),
			rdcf,
			immunizationDto -> immunizationDto.setRelatedCase(caze.toReference()));

		VisitDto visit = creator.createVisit(caze.getDisease(), caze.getPerson(), caze.getReportDate());
		visit.getSymptoms().setAnorexiaAppetiteLoss(SymptomState.YES);
		getVisitFacade().save(visit);

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select c from cases c where c.uuid=:uuid");
			query.setParameter("uuid", caze.getUuid());
			Case singleResult = (Case) query.getSingleResult();
			singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(singleResult);
		});

		assertEquals(2, getCaseService().count());

		useSystemUser();
		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(caze.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getCoreEntityDeletionService().executeAutomaticDeletion();
		wireMockRuntime.getWireMock().verify(exactly(0), postRequestedFor(urlEqualTo("/export")));
		loginWith(user);

		ContactDto resultingContactUpdated = getContactFacade().getByUuid(resultingContact.getUuid());

		assertEquals(1, getCaseService().count());
		assertEquals(duplicateCase.getUuid(), getCaseService().getAll().get(0).getUuid());
		assertEquals(0, getClinicalVisitService().count());
		assertEquals(0, getTreatmentService().count());
		assertEquals(0, getPrescriptionService().count());
		assertEquals(1, getSampleService().count());
		assertEquals(1, getVisitService().count());
		assertNull(getSampleFacade().getSampleByUuid(multiSample.getUuid()).getAssociatedCase());
		assertEquals(0, getSurveillanceReportService().count());
		assertTrue(getDocumentService().getAll().get(0).isDeleted());
		assertNull(resultingContactUpdated.getCaze());
		assertEquals(rdcf.region, resultingContactUpdated.getRegion());
		assertEquals(rdcf.district, resultingContactUpdated.getDistrict());
		assertEquals(rdcf.community, resultingContactUpdated.getCommunity());
		assertNull(getContactFacade().getByUuid(sourceContact.getUuid()).getResultingCase());
		assertNull(getEventParticipantFacade().getByUuid(eventParticipant.getUuid()).getResultingCase());
		assertNull(getTravelEntryFacade().getByUuid(travelEntry.getUuid()).getResultingCase());
		assertNull(getImmunizationFacade().getByUuid(immunization.getUuid()).getRelatedCase());
	}

	@Test
	public void testShareCase_WithCaseNotAllowedToBeSharedWithReportingTool(WireMockRuntimeInfo wireMockRuntime) throws SormasToSormasException {
		UserReferenceDto officer = useSurveillanceOfficerLogin(rdcf).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
			dto.setDontShareWithReportingTool(true);
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setComment("Test comment");

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);
				assertThat(postBody.getCases().size(), is(1));
				SormasToSormasCaseDto sharedCase = postBody.getCases().get(0);

				assertThat(sharedCase.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(sharedCase.getPerson().getLastName(), is(person.getLastName()));

				assertThat(sharedCase.getEntity().getUuid(), is(caze.getUuid()));
				// users should be cleaned up
				assertThat(sharedCase.getEntity().getReportingUser(), is(officer));
				assertThat(sharedCase.getEntity().getSurveillanceOfficer(), is(nullValue()));
				assertThat(sharedCase.getEntity().getClassificationUser(), is(nullValue()));

				// share information
				assertThat(postBody.getOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
				assertThat(postBody.getOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(postBody.getOriginInfo().getComment(), is("Test comment"));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(caze.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);
		wireMockRuntime.getWireMock().verify(exactly(0), postRequestedFor(urlEqualTo("/export")));

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
	}

	@Test
	public void testGetCasesWithExternalToolFilters() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createSurveillanceOfficer(rdcf).toReference();
		CaseDataDto sharedCase1 = creator.createCase(user, rdcf, null);
		CaseDataDto sharedCase2 = creator.createCase(user, rdcf, null);
		CaseDataDto case3 = creator.createCase(user, rdcf, null);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(sharedCase1.getUuid()))
				.withRequestBody(containing(sharedCase2.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		getExternalSurveillanceToolGatewayFacade().sendCases(Arrays.asList(sharedCase1.getUuid(), sharedCase2.getUuid()));

		sharedCase2.setDontShareWithReportingTool(true);
		getCaseFacade().save(sharedCase2);

		//test filter "Only cases not yet shared with reporting tool"
		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.setOnlyEntitiesNotSharedWithExternalSurvTool(true);
		List<CaseIndexDto> caseIndexDtos = getCaseFacade().getIndexList(caseCriteria, 0, 100, Collections.emptyList());
		assertEquals(1, caseIndexDtos.size());
		assertEquals(case3.getUuid(), caseIndexDtos.get(0).getUuid());

		//test filter "Only cases already shared with reporting tool"
		caseCriteria = new CaseCriteria();
		caseCriteria.setOnlyEntitiesSharedWithExternalSurvTool(true);
		caseIndexDtos = getCaseFacade().getIndexList(caseCriteria, 0, 100, Collections.emptyList());
		assertEquals(2, caseIndexDtos.size());
		List<String> casesUuids = caseIndexDtos.stream().map(c -> c.getUuid()).collect(Collectors.toList());
		assertTrue(casesUuids.contains(sharedCase1.getUuid()));
		assertTrue(casesUuids.contains(sharedCase2.getUuid()));

		//test filter "Only cases changed since last shared with reporting tool"
		sharedCase1.setFollowUpComment("new comment");
		getCaseFacade().save(sharedCase1);
		sharedCase2.setFollowUpComment("new comment");
		getCaseFacade().save(sharedCase2);

		caseCriteria = new CaseCriteria();
		caseCriteria.setOnlyEntitiesChangedSinceLastSharedWithExternalSurvTool(true);
		caseIndexDtos = getCaseFacade().getIndexList(caseCriteria, 0, 100, Collections.emptyList());
		assertEquals(1, caseIndexDtos.size());
		assertEquals(sharedCase1.getUuid(), caseIndexDtos.get(0).getUuid());

		//test filter "Only cases marked with 'Don't share with reporting tool'"
		caseCriteria = new CaseCriteria();
		caseCriteria.setOnlyCasesWithDontShareWithExternalSurvTool(true);
		caseIndexDtos = getCaseFacade().getIndexList(caseCriteria, 0, 100, Collections.emptyList());
		assertEquals(1, caseIndexDtos.size());
		assertEquals(sharedCase2.getUuid(), caseIndexDtos.get(0).getUuid());
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
