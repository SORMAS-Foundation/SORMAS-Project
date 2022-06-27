/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.event;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolFacade;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.MockProducer;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class EventServiceTest extends AbstractBeanTest {

	private static final int WIREMOCK_TESTING_PORT = 8888;
	private ExternalSurveillanceToolFacade subjectUnderTest;

	private TestDataCreator.RDCF rdcf;
	private UserDto nationalUser;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(options().port(WIREMOCK_TESTING_PORT), false);

	@Before
	public void setup() {
		configureExternalSurvToolUrlForWireMock();
		subjectUnderTest = getExternalSurveillanceToolGatewayFacade();
	}

	@After
	public void teardown() {
		clearExternalSurvToolUrlForWireMock();
	}

	@Override
	public void init() {

		super.init();
		rdcf = creator.createRDCF("Region", "District", "Community", "Facility", "Point of entry");
		nationalUser = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.community.getUuid(),
			rdcf.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
	}

	@Test
	public void testHasRegionAndDistrict() {

		EventDto event1 = creator.createEvent(nationalUser.toReference());
		assertFalse(getEventService().hasRegionAndDistrict(event1.getUuid()));

		EventDto event2 = creator.createEvent(nationalUser.toReference(), Disease.EVD, e -> {
			e.getEventLocation().setRegion(rdcf.region);
			e.getEventLocation().setDistrict(rdcf.district);
		});
		assertTrue(getEventService().hasRegionAndDistrict(event2.getUuid()));
	}

	@Test
	public void testHasAnyEventParticipantWithoutJurisdiction() {

		EventDto event = creator.createEvent(nationalUser.toReference());
		PersonDto person1 = creator.createPerson();
		creator.createEventParticipant(event.toReference(), person1, "", nationalUser.toReference(), e -> {
			e.setRegion(rdcf.region);
			e.setDistrict(rdcf.district);
		}, null);
		assertFalse(getEventService().hasAnyEventParticipantWithoutJurisdiction(event.getUuid()));

		PersonDto person2 = creator.createPerson();
		creator.createEventParticipant(event.toReference(), person2, nationalUser.toReference());
		assertTrue(getEventService().hasAnyEventParticipantWithoutJurisdiction(event.getUuid()));
	}

	@Test
	public void testGetEventSummaryDetailsByContactsEventArchivingAndDeletion() {
		TestDataCreator.RDCFEntities rdcfEntities = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		TestDataCreator.RDCF rdcf = new TestDataCreator.RDCF(rdcfEntities);
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");

		ContactDto contact = creator.createContact(rdcf, user.toReference(), contactPerson.toReference());

		EventDto eventDto = creator.createEvent(user.toReference(), contact.getDisease());
		Event event = getEventService().getByUuid(eventDto.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(event, ExternalShareStatus.SHARED);
		creator.createEventParticipant(eventDto.toReference(), contactPerson, user.toReference());

		EventService sut = getEventService();

		List<ContactEventSummaryDetails> result = sut.getEventSummaryDetailsByContacts(Arrays.asList(contact.getUuid()));
		assertEquals(1, result.size());
		assertEquals(event.getUuid(), result.get(0).getEventUuid());
		assertEquals(event.getEventTitle(), result.get(0).getEventTitle());

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(event.getUuid()))
				.withRequestBody(containing("eventUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		// archiving should not have any effect on the export list
		getEventFacade().archive(Collections.singletonList(event.getUuid()));

		result = sut.getEventSummaryDetailsByContacts(Arrays.asList(contact.getUuid()));
		assertEquals(1, result.size());
		assertEquals(event.getUuid(), result.get(0).getEventUuid());
		assertEquals(event.getEventTitle(), result.get(0).getEventTitle());

		// deletion should have an effect on the export list
		getEventFacade().delete(event.getUuid(), new DeletionDetails());

		result = sut.getEventSummaryDetailsByContacts(Arrays.asList(contact.getUuid()));
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetEventSummaryDetailsByCasesEventArchivingAndDeletion() {
		TestDataCreator.RDCFEntities rdcfEntities = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		TestDataCreator.RDCF rdcf = new TestDataCreator.RDCF(rdcfEntities);
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto contactPerson = creator.createPerson("Case", "Person");

		CaseDataDto caze = creator.createCase(user.toReference(), contactPerson.toReference(), rdcf);

		EventDto eventDto = creator.createEvent(user.toReference(), caze.getDisease());
		Event event = getEventService().getByUuid(eventDto.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(event, ExternalShareStatus.SHARED);
		EventParticipantDto participant = creator.createEventParticipant(eventDto.toReference(), contactPerson, user.toReference());
		participant.setResultingCase(caze.toReference());
		getEventParticipantFacade().save(participant);

		EventService sut = getEventService();

		Long cazeId = getCaseService().getIdByUuid(caze.getUuid());
		List<EventSummaryDetails> result = sut.getEventSummaryDetailsByCases(Arrays.asList(cazeId));
		assertEquals(1, result.size());
		assertEquals(eventDto.getUuid(), result.get(0).getEventUuid());
		assertEquals(eventDto.getEventTitle(), result.get(0).getEventTitle());

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(event.getUuid()))
				.withRequestBody(containing("eventUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		// archiving should not have any effect on the export list
		getEventFacade().archive(Collections.singletonList(event.getUuid()));

		result = sut.getEventSummaryDetailsByCases(Arrays.asList(cazeId));
		assertEquals(1, result.size());
		assertEquals(event.getUuid(), result.get(0).getEventUuid());
		assertEquals(event.getEventTitle(), result.get(0).getEventTitle());

		// deletion should have an effect on the export list
		getEventFacade().delete(event.getUuid(), new DeletionDetails());

		result = sut.getEventSummaryDetailsByCases(Arrays.asList(cazeId));
		assertTrue(result.isEmpty());
	}

	@Test
	public void testSetArchiveInExternalSurveillanceToolForEntity_WithProperEntity() {
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
			rdcf.district);

		Event event = getEventService().getByUuid(eventDto.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(event, ExternalShareStatus.SHARED);
		EventService eventService = getBean(EventService.class);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(event.getUuid()))
				.withRequestBody(containing("eventUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		eventService.setArchiveInExternalSurveillanceToolForEntity(event.getUuid(), false);
		wireMockRule.verify(exactly(1), postRequestedFor(urlEqualTo("/export")));
	}

	@Test(expected = ExternalSurveillanceToolRuntimeException.class)
	public void testSetArchiveInExternalSurveillanceToolForEntity_Exception() {
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
			rdcf.district);

		Event event = getEventService().getByUuid(eventDto.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(event, ExternalShareStatus.SHARED);

		EventService eventService = getBean(EventService.class);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(eventDto.getUuid()))
				.withRequestBody(containing("eventUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));

		eventService.setArchiveInExternalSurveillanceToolForEntity(eventDto.getUuid(), true);
	}

	private void configureExternalSurvToolUrlForWireMock() {
		MockProducer.getProperties().setProperty("survnet.url", String.format("http://localhost:%s", WIREMOCK_TESTING_PORT));
	}

	private void clearExternalSurvToolUrlForWireMock() {
		MockProducer.getProperties().setProperty("survnet.url", "");
	}
}
