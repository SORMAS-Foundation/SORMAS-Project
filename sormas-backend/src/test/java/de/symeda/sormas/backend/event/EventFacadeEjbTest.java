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
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventExportDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.criteria.ExternalShareDateType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.share.ExternalShareInfo;

public class EventFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testValidateWithNullReportingUser() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		assertThrows(
			ValidationRuntimeException.class,
			() -> creator.createEvent(
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
				null,
				null,
				Disease.EVD,
				rdcf));
	}

	@Test
	public void testEventDeletion() throws ExternalSurveillanceToolRuntimeException {

		Date since = new Date();

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		UserDto admin = getUserFacade().getByUserName("admin");
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
			rdcf);
		PersonDto eventPerson = creator.createPerson("Event", "Person");
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), eventPerson, "Description", user.toReference());
		ActionDto action = creator.createAction(event.toReference());

		// Database should contain the created event and event participant
		assertNotNull(getEventFacade().getEventByUuid(event.getUuid(), false));
		assertNotNull(getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()));
		assertNotNull(getActionFacade().getByUuid(action.getUuid()));

		getEventFacade().delete(event.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));

		// Event should be marked as deleted; Event participant should be deleted
		assertTrue(getEventFacade().getDeletedUuidsSince(since).contains(event.getUuid()));
		assertTrue(getEventParticipantFacade().getDeletedUuidsSince(since).contains(eventParticipant.getUuid()));
		assertNotNull(getActionFacade().getByUuid(action.getUuid())); // actions get deleted only with permanent delete
		assertEquals(DeletionReason.OTHER_REASON, getEventFacade().getByUuid(event.getUuid()).getDeletionReason());
		assertEquals("test reason", getEventFacade().getByUuid(event.getUuid()).getOtherDeletionReason());

		getEventFacade().restore(event.getUuid());

		assertFalse(getEventFacade().getDeletedUuidsSince(since).contains(event.getUuid()));
		assertFalse(getEventParticipantFacade().getDeletedUuidsSince(since).contains(eventParticipant.getUuid()));
		assertNotNull(getActionFacade().getByUuid(action.getUuid())); // actions get deleted only with permanent delete
		assertNull(getEventFacade().getByUuid(event.getUuid()).getDeletionReason());
		assertNull(getEventFacade().getByUuid(event.getUuid()).getOtherDeletionReason());
	}

	@Test
	public void testEventUpdate() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		UserDto admin = getUserFacade().getByUserName("admin");
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
			rdcf);

		final String testDescription = "testDescription";
		final Date startDate = DateHelper.subtractDays(new Date(), 1);
		event.setEventDesc(testDescription);
		event.setStartDate(startDate);

		final EventDto updatedEvent = getEventFacade().save(event);
		assertEquals(testDescription, updatedEvent.getEventDesc());
		assertEquals(startDate, updatedEvent.getStartDate());
	}

	@Test
	public void testGetIndexList() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
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
			rdcf);

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
			rdcf);

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
	public void testGetIndexListByARestrictedAccessToAssignedEntities() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final EventDto event = creator.createEvent(
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
			rdcf);

		final EventDto event1 = creator.createEvent(
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
			rdcf);

		EventCriteria eventCriteria = new EventCriteria();
		List<EventIndexDto> results = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		assertEquals(2, results.size());

		UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities =
			creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		List<EventIndexDto> results2 = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		assertEquals(0, results2.size());

		loginWith(user);
		event.setResponsibleUser(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getEventFacade().save(event);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertTrue(getCurrentUserService().isRestrictedToAssignedEntities());
		List<EventIndexDto> results3 = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		assertEquals(1, results3.size());
	}

	@Test
	public void testGetExportList() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
			rdcf);

		EventCriteria eventCriteria = new EventCriteria();
		eventCriteria.setDisease(Disease.EVD);
		List<EventExportDto> results = getEventFacade().getExportList(eventCriteria, Collections.emptySet(), 0, 100);

		// List should have one entry
		assertThat(results, Matchers.hasSize(1));
	}

	@Test
	public void testArchiveOrDearchiveEvent() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		EventDto eventDto = creator.createEvent(
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
			rdcf);
		PersonDto eventPerson = creator.createPerson("Event", "Person");
		creator.createEventParticipant(eventDto.toReference(), eventPerson, "Description", user.toReference());
		Date testStartDate = new Date();

		// getAllActiveEvents/getAllActiveEventParticipants and getAllUuids should return length 1
		assertEquals(1, getEventFacade().getAllAfter(null).size());
		assertEquals(1, getEventFacade().getAllActiveUuids().size());
		assertEquals(1, getEventParticipantFacade().getAllAfter(null).size());
		assertEquals(1, getEventParticipantFacade().getAllActiveUuids().size());

		getEventFacade().archive(eventDto.getUuid(), null);

		// getAllActiveEvents/getAllActiveEventParticipants and getAllUuids should return length 0
		assertEquals(0, getEventFacade().getAllAfter(null).size());
		assertEquals(0, getEventFacade().getAllActiveUuids().size());
		assertEquals(0, getEventParticipantFacade().getAllAfter(null).size());
		assertEquals(0, getEventParticipantFacade().getAllActiveUuids().size());

		// getArchivedUuidsSince should return length 1
		assertEquals(1, getEventFacade().getArchivedUuidsSince(testStartDate).size());

		getEventFacade().dearchive(Collections.singletonList(eventDto.getUuid()), null);

		// getAllActiveEvents/getAllActiveEventParticipants and getAllUuids should return length 1
		assertEquals(1, getEventFacade().getAllAfter(null).size());
		assertEquals(1, getEventFacade().getAllActiveUuids().size());
		assertEquals(1, getEventParticipantFacade().getAllAfter(null).size());
		assertEquals(1, getEventParticipantFacade().getAllActiveUuids().size());

		// getArchivedUuidsSince should return length 0
		assertEquals(0, getEventFacade().getArchivedUuidsSince(testStartDate).size());
	}

	@Test
	public void testArchiveAllArchivableEvents() {

		RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();

		// One archived event
		EventDto eventDto1 = creator.createEvent(
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
			rdcf);

		EventFacadeEjbLocal cut = getBean(EventFacadeEjbLocal.class);

		cut.archive(eventDto1.getUuid(), null);

		// One other event
		EventDto eventDto2 = creator.createEvent(
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

		assertTrue(cut.isArchived(eventDto1.getUuid()));
		assertFalse(cut.isArchived(eventDto2.getUuid()));

		// Event of "today" shouldn't be archived
		cut.archiveAllArchivableEvents(70, LocalDate.now().plusDays(69));
		assertTrue(cut.isArchived(eventDto1.getUuid()));
		assertFalse(cut.isArchived(eventDto2.getUuid()));

		// Event of "yesterday" should be archived
		cut.archiveAllArchivableEvents(70, LocalDate.now().plusDays(71));
		assertTrue(cut.isArchived(eventDto1.getUuid()));
		assertTrue(cut.isArchived(eventDto2.getUuid()));
	}

	@Test
	public void testCreateWithoutUuid() {
		RDCF rdcf = creator.createRDCF();
		EventDto event = new EventDto();
		event.setEventStatus(EventStatus.EVENT);
		event.setReportDateTime(new Date());
		event.setReportingUser(creator.createSurveillanceOfficer(rdcf).toReference());
		event.setEventTitle("Test event");
		LocationDto eventLocation = LocationDto.build();
		eventLocation.setRegion(rdcf.region);
		eventLocation.setDistrict(rdcf.district);
		event.setEventLocation(eventLocation);
		event.setEventInvestigationStatus(EventInvestigationStatus.PENDING);
		EventDto savedEvent = getEventFacade().save(event);

		MatcherAssert.assertThat(savedEvent.getUuid(), not(emptyOrNullString()));
		MatcherAssert.assertThat(savedEvent.getEventLocation().getUuid(), not(emptyOrNullString()));
	}

	@Test
	public void testEventCriteriaSharedWithReportingTool() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();

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
		UserDto user = creator.createNationalUser();

		EventDto sharedEvent = creator.createEvent(user.toReference());
		ExternalShareInfo shareInfo = new ExternalShareInfo();
		shareInfo.setCreationDate(Timestamp.valueOf(LocalDateTime.of(2021, Month.APRIL, 20, 12, 31)));
		shareInfo.setEvent(getEventService().getByUuid(sharedEvent.getUuid()));
		shareInfo.setSender(getUserService().getByUuid(user.getUuid()));
		shareInfo.setStatus(ExternalShareStatus.DELETED);
		getExternalShareInfoService().ensurePersisted(shareInfo);

		sharedEvent.setEventDesc("Dummy description");
		getEventFacade().save(sharedEvent);

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
		UserDto user = creator.createNationalUser();

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

	@Test
	public void testGetSubordinateEventUuids() {
		RDCF rdcf = creator.createRDCF();
		UserDto reportingUser = creator.createSurveillanceOfficer(rdcf);

		EventDto event1 = creator.createEvent(reportingUser.toReference());
		EventDto event2 = creator.createEvent(reportingUser.toReference());
		EventDto subordinateEvent_1_1 = creator
			.createEvent(EventStatus.CLUSTER, EventInvestigationStatus.ONGOING, "Sub event 1.1", null, reportingUser.toReference(), null, (e) -> {
				e.setSuperordinateEvent(event1.toReference());
			});
		EventDto subordinateEvent_1_2 = creator
			.createEvent(EventStatus.CLUSTER, EventInvestigationStatus.ONGOING, "Sub event 1.2", null, reportingUser.toReference(), null, (e) -> {
				e.setSuperordinateEvent(event1.toReference());
			});
		EventDto subordinateEvent_2_1 = creator
			.createEvent(EventStatus.CLUSTER, EventInvestigationStatus.ONGOING, "Sub event 2.1", null, reportingUser.toReference(), null, (e) -> {
				e.setSuperordinateEvent(event2.toReference());
			});

		List<String> subordinateEventUuids = getEventFacade().getSubordinateEventUuids(Arrays.asList(event1.getUuid()));
		MatcherAssert.assertThat(
			subordinateEventUuids.toArray(new String[] {}),
			Matchers.arrayContainingInAnyOrder(subordinateEvent_1_1.getUuid(), subordinateEvent_1_2.getUuid()));

		subordinateEventUuids = getEventFacade().getSubordinateEventUuids(Arrays.asList(event1.getUuid(), event2.getUuid()));
		MatcherAssert.assertThat(
			subordinateEventUuids.toArray(new String[] {}),
			Matchers.arrayContainingInAnyOrder(subordinateEvent_1_1.getUuid(), subordinateEvent_1_2.getUuid(), subordinateEvent_2_1.getUuid()));
	}

	@Test
	public void testGetEventUsersWithoutUsesLimitedToOthersDiseses() {
		RDCF rdcf = creator.createRDCF();
		useNationalAdminLogin();
		UserDto userDto = creator.createNationalUser();
		EventDto event = creator.createEvent(userDto.toReference(), Disease.CORONAVIRUS);

		UserDto limitedCovidNationalUser = creator.createUser(
			rdcf,
			"Limited Disease Covid",
			"National User",
			Disease.CORONAVIRUS,
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		UserDto limitedDengueNationalUser = creator
			.createUser(rdcf, "Limited Disease Dengue", "National User", Disease.DENGUE, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));

		List<UserReferenceDto> userReferenceDtos = getUserFacade().getUsersHavingEventInJurisdiction(event.toReference());
		assertNotNull(userReferenceDtos);
		assertTrue(userReferenceDtos.contains(userDto));
		assertTrue(userReferenceDtos.contains(limitedCovidNationalUser));
		assertFalse(userReferenceDtos.contains(limitedDengueNationalUser));
	}

	@Test
	public void testGetEventsByPersonNationalHealthId() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		EventDto event1 = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		PersonReferenceDto person1 = creator.createPerson().toReference();
		PersonDto personDto1 = getPersonFacade().getByUuid(person1.getUuid());
		personDto1.setNationalHealthId("firstNationalId");
		getPersonFacade().save(personDto1);
		creator.createEventParticipant(event1.toReference(), personDto1, "firstPerson", user.toReference());

		EventDto event2 = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		PersonReferenceDto person2 = creator.createPerson().toReference();
		PersonDto personDto2 = getPersonFacade().getByUuid(person2.getUuid());
		personDto2.setNationalHealthId("secondNationalId");
		getPersonFacade().save(personDto2);
		creator.createEventParticipant(event2.toReference(), personDto2, "secondPerson", user.toReference());

		EventDto event3 = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		PersonReferenceDto person3 = creator.createPerson().toReference();
		PersonDto personDto3 = getPersonFacade().getByUuid(person3.getUuid());
		personDto3.setNationalHealthId("third");
		getPersonFacade().save(personDto3);
		creator.createEventParticipant(event3.toReference(), personDto3, "thirdPerson", user.toReference());

		EventCriteria eventCriteria = new EventCriteria();
		eventCriteria.setFreeTextEventParticipants("firstNationalId");

		List<EventIndexDto> eventIndexDtos1 = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		assertEquals(1, eventIndexDtos1.size());
		assertEquals(event1.getUuid(), eventIndexDtos1.get(0).getUuid());

		eventCriteria.setFreeTextEventParticipants("National");
		List<EventIndexDto> eventIndexDtosNational = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		assertEquals(2, eventIndexDtosNational.size());

		eventCriteria.setFreeTextEventParticipants(null);
		List<EventIndexDto> eventIndexDtosAll = getEventFacade().getIndexList(eventCriteria, 0, 100, null);
		assertEquals(3, eventIndexDtosAll.size());
	}
}
