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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventExportDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.criteria.ExternalShareDateType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.share.ExternalShareInfo;

public class EventFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testEventDeletion() throws ExternalSurveillanceToolException {

		Date since = new Date();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 1),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf.district);
		PersonDto eventPerson = creator.createPerson("Event", "Person");
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), eventPerson, "Description", user.toReference());
		ActionDto action = creator.createAction(event.toReference());

		// Database should contain the created event and event participant
		assertNotNull(getEventFacade().getEventByUuid(event.getUuid()));
		assertNotNull(getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()));
		assertNotNull(getActionFacade().getByUuid(action.getUuid()));

		getEventFacade().deleteEvent(event.getUuid());

		// Event should be marked as deleted; Event participant should be deleted
		assertTrue(getEventFacade().getDeletedUuidsSince(since).contains(event.getUuid()));
		assertTrue(getEventParticipantFacade().getDeletedUuidsSince(since).contains(eventParticipant.getUuid()));
		assertNull(getActionFacade().getByUuid(action.getUuid()));
	}

	@Test
	public void testEventUpdate() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			null,
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			null,
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf.district);

		final String testDescription = "testDescription";
		final Date startDate = DateHelper.subtractDays(new Date(), 1);
		event.setEventDesc(testDescription);
		event.setStartDate(startDate);

		final EventDto updatedEvent = getEventFacade().saveEvent(event);
		Assert.assertEquals(testDescription, updatedEvent.getEventDesc());
		Assert.assertEquals(startDate, updatedEvent.getStartDate());
	}

	@Test
	public void testGetIndexList() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"TitleEv1",
			"DescriptionEv1",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 1),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf.district);

		creator.createEvent(
			EventStatus.EVENT,
			EventInvestigationStatus.PENDING,
			"TitleEv2",
			"DescriptionEv2",
			"First",
			"Name",
			"12345",
			TypeOfPlace.FACILITY,
			DateHelper.subtractDays(new Date(), 1),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf.district);

		EventCriteria eventCriteria = new EventCriteria();
		List<EventIndexDto> results = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		assertEquals(2, results.size());

		eventCriteria.eventStatus(EventStatus.SIGNAL);
		results = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		assertEquals(1, results.size());
		assertEquals("TitleEv1", results.get(0).getEventTitle());

		eventCriteria.eventStatus(null);
		eventCriteria.setTypeOfPlace(TypeOfPlace.FACILITY);
		results = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		assertEquals(1, results.size());
		assertEquals("TitleEv2", results.get(0).getEventTitle());
	}

	@Test
	public void testGetExportList() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 1),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf.district);

		EventCriteria eventCriteria = new EventCriteria();
		eventCriteria.setDisease(Disease.EVD);
		List<EventExportDto> results = getEventFacade().getExportList(eventCriteria, Collections.emptySet(), 0, 100);

		// List should have one entry
		assertThat(results, Matchers.hasSize(1));
	}

	@Test
	public void testArchiveOrDearchiveEvent() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 1),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf.district);
		PersonDto eventPerson = creator.createPerson("Event", "Person");
		creator.createEventParticipant(event.toReference(), eventPerson, "Description", user.toReference());
		Date testStartDate = new Date();

		// getAllActiveEvents/getAllActiveEventParticipants and getAllUuids should return length 1
		assertEquals(1, getEventFacade().getAllActiveEventsAfter(null).size());
		assertEquals(1, getEventFacade().getAllActiveUuids().size());
		assertEquals(1, getEventParticipantFacade().getAllActiveEventParticipantsAfter(null).size());
		assertEquals(1, getEventParticipantFacade().getAllActiveUuids().size());

		getEventFacade().archiveOrDearchiveEvent(event.getUuid(), true);

		// getAllActiveEvents/getAllActiveEventParticipants and getAllUuids should return length 0
		assertEquals(0, getEventFacade().getAllActiveEventsAfter(null).size());
		assertEquals(0, getEventFacade().getAllActiveUuids().size());
		assertEquals(0, getEventParticipantFacade().getAllActiveEventParticipantsAfter(null).size());
		assertEquals(0, getEventParticipantFacade().getAllActiveUuids().size());

		// getArchivedUuidsSince should return length 1
		assertEquals(1, getEventFacade().getArchivedUuidsSince(testStartDate).size());

		getEventFacade().archiveOrDearchiveEvent(event.getUuid(), false);

		// getAllActiveEvents/getAllActiveEventParticipants and getAllUuids should return length 1
		assertEquals(1, getEventFacade().getAllActiveEventsAfter(null).size());
		assertEquals(1, getEventFacade().getAllActiveUuids().size());
		assertEquals(1, getEventParticipantFacade().getAllActiveEventParticipantsAfter(null).size());
		assertEquals(1, getEventParticipantFacade().getAllActiveUuids().size());

		// getArchivedUuidsSince should return length 0
		assertEquals(0, getEventFacade().getArchivedUuidsSince(testStartDate).size());
	}

	@Test
	public void testArchiveAllArchivableEvents() {

		RDCFEntities rdcfEntities = creator.createRDCFEntities();
		RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcfEntities).toReference();

		// One archived event
		EventDto event1 = creator.createEvent(
			EventStatus.EVENT,
			EventInvestigationStatus.PENDING,
			"",
			"",
			"",
			"",
			"",
			TypeOfPlace.MEANS_OF_TRANSPORT,
			new Date(),
			new Date(),
			user,
			user,
			Disease.ANTHRAX,
			rdcf.district);
		EventFacadeEjbLocal cut = getBean(EventFacadeEjbLocal.class);
		cut.archiveOrDearchiveEvent(event1.getUuid(), true);

		// One other event
		EventDto event2 = creator.createEvent(
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

		assertTrue(cut.isArchived(event1.getUuid()));
		assertFalse(cut.isArchived(event2.getUuid()));

		// Event of "today" shouldn't be archived
		cut.archiveAllArchivableEvents(70, LocalDate.now().plusDays(69));
		assertTrue(cut.isArchived(event1.getUuid()));
		assertFalse(cut.isArchived(event2.getUuid()));

		// Event of "yesterday" should be archived
		cut.archiveAllArchivableEvents(70, LocalDate.now().plusDays(71));
		assertTrue(cut.isArchived(event1.getUuid()));
		assertTrue(cut.isArchived(event2.getUuid()));
	}

	@Test
	public void testCreateWithoutUuid() {
		RDCF rdcf = creator.createRDCF();
		EventDto event = new EventDto();
		event.setEventStatus(EventStatus.EVENT);
		event.setReportDateTime(new Date());
		event.setReportingUser(creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference());
		event.setEventTitle("Test event");
		event.setEventLocation(new LocationDto());

		EventDto savedEvent = getEventFacade().saveEvent(event);

		MatcherAssert.assertThat(savedEvent.getUuid(), not(isEmptyOrNullString()));
		MatcherAssert.assertThat(savedEvent.getEventLocation().getUuid(), not(isEmptyOrNullString()));
	}

	@Test
	public void testEventCriteriaSharedWithReportingTool() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.NATIONAL_USER);

		EventDto sharedEvent = creator.createEvent(user.toReference());
		ExternalShareInfo shareInfo = new ExternalShareInfo();
		shareInfo.setEvent(getEventService().getByUuid(sharedEvent.getUuid()));
		shareInfo.setSender(getUserService().getByUuid(user.getUuid()));
		shareInfo.setStatus(ExternalShareStatus.SHARED);
		getExternalShareInfoService().ensurePersisted(shareInfo);

		EventDto notSharedEvent = creator.createEvent(user.toReference());;

		EventCriteria eventCriteriaForShared = new EventCriteria();
		eventCriteriaForShared.setOnlyEntitiesSharedWithExternalSurvTool(true);

		List<EventIndexDto> indexList = getEventFacade().getIndexList(eventCriteriaForShared, 0, 100, null);
		MatcherAssert.assertThat(indexList, hasSize(1));
		MatcherAssert.assertThat(indexList.get(0).getUuid(), is(sharedEvent.getUuid()));

		EventCriteria eventCriteriaForNotShared = new EventCriteria();
		eventCriteriaForNotShared.setOnlyEntitiesNotSharedWithExternalSurvTool(true);

		indexList = getEventFacade().getIndexList(eventCriteriaForNotShared, 0, 100, null);
		MatcherAssert.assertThat(indexList, hasSize(1));
		MatcherAssert.assertThat(indexList.get(0).getUuid(), is(notSharedEvent.getUuid()));
	}

	@Test
	public void testEventCriteriaChangedSinceLastShareWithReportingTool() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.NATIONAL_USER);

		EventDto sharedEvent = creator.createEvent(user.toReference());
		ExternalShareInfo shareInfo = new ExternalShareInfo();
		shareInfo.setCreationDate(Timestamp.valueOf(LocalDateTime.of(2021, Month.APRIL, 20, 12, 31)));
		shareInfo.setEvent(getEventService().getByUuid(sharedEvent.getUuid()));
		shareInfo.setSender(getUserService().getByUuid(user.getUuid()));
		shareInfo.setStatus(ExternalShareStatus.DELETED);
		getExternalShareInfoService().ensurePersisted(shareInfo);

		sharedEvent.setEventDesc("Dummy description");
		getEventFacade().saveEvent(sharedEvent);

		creator.createEvent(user.toReference());
		creator.createEvent(user.toReference());

		EventCriteria eventCriteriaForShared = new EventCriteria();
		eventCriteriaForShared.setOnlyEntitiesChangedSinceLastSharedWithExternalSurvTool(true);

		List<EventIndexDto> indexList = getEventFacade().getIndexList(eventCriteriaForShared, 0, 100, null);
		MatcherAssert.assertThat(indexList, hasSize(1));
		MatcherAssert.assertThat(indexList.get(0).getUuid(), is(sharedEvent.getUuid()));
	}

	@Test
	public void testEventCriteriaLastShareWithReportingToolBetweenDates() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.NATIONAL_USER);

		EventDto sharedEvent = creator.createEvent(user.toReference());
		ExternalShareInfo shareInfoMarch = new ExternalShareInfo();
		shareInfoMarch.setCreationDate(Timestamp.valueOf(LocalDateTime.of(2021, Month.MARCH, 20, 12, 31)));
		shareInfoMarch.setEvent(getEventService().getByUuid(sharedEvent.getUuid()));
		shareInfoMarch.setSender(getUserService().getByUuid(user.getUuid()));
		shareInfoMarch.setStatus(ExternalShareStatus.SHARED);
		getExternalShareInfoService().ensurePersisted(shareInfoMarch);

		ExternalShareInfo shareInfoApril = new ExternalShareInfo();
		shareInfoApril.setCreationDate(Timestamp.valueOf(LocalDateTime.of(2021, Month.APRIL, 20, 12, 31)));
		shareInfoApril.setEvent(getEventService().getByUuid(sharedEvent.getUuid()));
		shareInfoApril.setSender(getUserService().getByUuid(user.getUuid()));
		shareInfoApril.setStatus(ExternalShareStatus.DELETED);
		getExternalShareInfoService().ensurePersisted(shareInfoApril);

		creator.createEvent(user.toReference());
		creator.createEvent(user.toReference());

		EventCriteria eventCriteria = new EventCriteria();
		eventCriteria.setEventDateType(ExternalShareDateType.LAST_EXTERNAL_SURVEILLANCE_TOOL_SHARE);
		eventCriteria.eventDateBetween(
			Date.from(LocalDateTime.of(2021, Month.APRIL, 18, 12, 31).atZone(ZoneId.systemDefault()).toInstant()),
			Date.from(LocalDateTime.of(2021, Month.APRIL, 21, 12, 31).atZone(ZoneId.systemDefault()).toInstant()),
			ExternalShareDateType.LAST_EXTERNAL_SURVEILLANCE_TOOL_SHARE,
			DateFilterOption.DATE);

		List<EventIndexDto> indexList = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		MatcherAssert.assertThat(indexList, hasSize(1));
		MatcherAssert.assertThat(indexList.get(0).getUuid(), is(sharedEvent.getUuid()));

		// range before last share
		eventCriteria.eventDateBetween(
			Date.from(LocalDateTime.of(2021, Month.MARCH, 10, 12, 31).atZone(ZoneId.systemDefault()).toInstant()),
			Date.from(LocalDateTime.of(2021, Month.APRIL, 19, 10, 31).atZone(ZoneId.systemDefault()).toInstant()),
			ExternalShareDateType.LAST_EXTERNAL_SURVEILLANCE_TOOL_SHARE,
			DateFilterOption.DATE);
		indexList = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		MatcherAssert.assertThat(indexList, hasSize(0));

		// range after last share
		eventCriteria.eventDateBetween(
			Date.from(LocalDateTime.of(2021, Month.APRIL, 21, 12, 31).atZone(ZoneId.systemDefault()).toInstant()),
			Date.from(LocalDateTime.of(2021, Month.APRIL, 25, 10, 31).atZone(ZoneId.systemDefault()).toInstant()),
			ExternalShareDateType.LAST_EXTERNAL_SURVEILLANCE_TOOL_SHARE,
			DateFilterOption.DATE);

		indexList = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		MatcherAssert.assertThat(indexList, hasSize(0));

	}
}
