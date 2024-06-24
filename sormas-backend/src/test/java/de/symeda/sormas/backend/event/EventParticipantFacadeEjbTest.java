/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantExportDto;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class EventParticipantFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testValidateWithNullReportingUser() {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = createUser(rdcf);
		EventDto event = createEvent(user, rdcf);

		PersonDto eventPerson = creator.createPerson("Event", "Organizer");
		assertThrows(
			ValidationRuntimeException.class,
			() -> creator.createEventParticipant(event.toReference(), eventPerson, "event Director", null));
	}

	@Test
	public void testGetEventParticipantByUuidForArchivedParticipant() {
		RDCF rdcf = creator.createRDCF();

		UserDto user1 = creator.createUser(
			null,
			null,
			null,
			"User1",
			"User1",
			"User1",
			JurisdictionLevel.NATION,
			UserRight.CASE_VIEW,
			UserRight.EVENT_VIEW,
			UserRight.CONTACT_VIEW,
			UserRight.CONTACT_EDIT,
			UserRight.PERSON_VIEW,
			UserRight.PERSON_EDIT,
			UserRight.EVENTPARTICIPANT_VIEW,
			UserRight.EVENTPARTICIPANT_VIEW_ARCHIVED);

		UserDto user2 = creator.createUser(
			null,
			null,
			null,
			"User",
			"User",
			"User",
			JurisdictionLevel.NATION,
			UserRight.CASE_VIEW,
			UserRight.EVENT_VIEW,
			UserRight.CONTACT_VIEW,
			UserRight.CONTACT_EDIT,
			UserRight.PERSON_VIEW,
			UserRight.PERSON_EDIT,
			UserRight.EVENTPARTICIPANT_VIEW);

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
			user1.toReference(),
			user1.toReference(),
			Disease.CORONAVIRUS,
			rdcf);

		PersonDto eventPerson = creator.createPerson("Event", "Organizer");
		EventParticipantDto eventParticipant =
			creator.createEventParticipant(event.toReference(), eventPerson, "event Director", user1.toReference());

		EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal cut = getBean(EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal.class);
		cut.archive(eventParticipant.getUuid(), null);

		//user1 has EVENTPARTICIPANT_VIEW_ARCHIVED right
		loginWith(user1);
		assertEquals(getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()).getUuid(), eventParticipant.getUuid());

		//user2 does not have EVENTPARTICIPANT_VIEW_ARCHIVED right
		loginWith(user2);
		assertThrows(AccessDeniedException.class, () -> getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()));
	}

	@Test
	public void testGetExportListWithRelevantVaccinations() {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = createUser(rdcf);
		EventDto event = createEvent(user, rdcf);

		PersonDto eventPerson = creator.createPerson("Event", "Organizer");
		creator.createEventParticipant(event.toReference(), eventPerson, "event Director", user.toReference());

		PersonDto eventPerson1 = creator.createPerson("Event", "Participant");
		creator.createEventParticipant(event.toReference(), eventPerson1, "event fan", user.toReference());

		ImmunizationDto immunization = creator.createImmunization(
			event.getDisease(),
			eventPerson.toReference(),
			event.getReportingUser(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf,
			DateHelper.subtractDays(new Date(), 10),
			DateHelper.subtractDays(new Date(), 5),
			DateHelper.subtractDays(new Date(), 1),
			null);
		creator.createImmunization(
			event.getDisease(),
			eventPerson.toReference(),
			event.getReportingUser(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf,
			DateHelper.subtractDays(new Date(), 8),
			DateHelper.subtractDays(new Date(), 7),
			null,
			null);

		VaccinationDto firstVaccination = creator.createVaccinationWithDetails(
			event.getReportingUser(),
			immunization.toReference(),
			HealthConditionsDto.build(),
			DateHelper.subtractDays(new Date(), 7),
			Vaccine.OXFORD_ASTRA_ZENECA,
			VaccineManufacturer.ASTRA_ZENECA,
			VaccinationInfoSource.UNKNOWN,
			"inn1",
			"123",
			"code123",
			"3");

		VaccinationDto secondVaccination = creator.createVaccinationWithDetails(
			event.getReportingUser(),
			immunization.toReference(),
			HealthConditionsDto.build(),
			DateHelper.subtractDays(new Date(), 4),
			Vaccine.MRNA_1273,
			VaccineManufacturer.MODERNA,
			VaccinationInfoSource.UNKNOWN,
			"inn2",
			"456",
			"code456",
			"2");

		VaccinationDto thirdVaccination = creator.createVaccinationWithDetails(
			event.getReportingUser(),
			immunization.toReference(),
			HealthConditionsDto.build(),
			new Date(),
			Vaccine.COMIRNATY,
			VaccineManufacturer.BIONTECH_PFIZER,
			VaccinationInfoSource.UNKNOWN,
			"inn3",
			"789",
			"code789",
			"1");

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.withEvent(event.toReference());

		List<EventParticipantExportDto> results =
			getEventParticipantFacade().getExportList(eventParticipantCriteria, Collections.emptySet(), 0, 100, Language.EN, null);
		EventParticipantExportDto exportDto = results.get(0);

		// List should have two entries
		assertThat(results, Matchers.hasSize(2));
		assertEquals(VaccinationStatus.VACCINATED, exportDto.getVaccinationStatus());
		assertEquals(firstVaccination.getVaccinationDate(), exportDto.getFirstVaccinationDate());
		assertEquals(secondVaccination.getVaccineName(), exportDto.getVaccineName());
		assertEquals(secondVaccination.getVaccinationDate(), exportDto.getLastVaccinationDate());
		assertEquals(secondVaccination.getVaccinationInfoSource(), exportDto.getVaccinationInfoSource());
		assertEquals(secondVaccination.getVaccineInn(), exportDto.getVaccineInn());
		assertEquals(secondVaccination.getVaccineBatchNumber(), exportDto.getVaccineBatchNumber());
		assertEquals(secondVaccination.getVaccineAtcCode(), exportDto.getVaccineAtcCode());
		assertEquals(secondVaccination.getVaccineDose(), exportDto.getVaccinationDoses());
	}

	@Test
	public void testGetMatchingEventParticipants() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceOfficer(rdcf);
		user.setLaboratory(rdcf.facility);
		getUserFacade().saveUser(user, false);
		loginWith(user);

		EventDto event = createEvent(user, rdcf);
		PersonDto eventPerson = creator.createPerson("Event", "Organizer");
		creator.createEventParticipant(event.toReference(), eventPerson, "event Director", user.toReference());

		EventParticipantCriteria criteria = new EventParticipantCriteria();
		List<SimilarEventParticipantDto> result = getEventParticipantFacade().getMatchingEventParticipants(criteria);
		assertThat(result, hasSize(1));
	}

	@Test
	public void testGetExportListWithoutRelevantVaccinations() {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = createUser(rdcf);
		EventDto event = createEvent(user, rdcf);

		PersonDto eventPerson = creator.createPerson("Event", "Organizer");
		creator.createEventParticipant(event.toReference(), eventPerson, "event Director", user.toReference());

		PersonDto eventPerson1 = creator.createPerson("Event", "Participant");
		creator.createEventParticipant(event.toReference(), eventPerson1, "event fan", user.toReference());

		ImmunizationDto immunization = creator.createImmunization(
			event.getDisease(),
			eventPerson.toReference(),
			event.getReportingUser(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf,
			DateHelper.subtractDays(new Date(), 10),
			DateHelper.subtractDays(new Date(), 5),
			DateHelper.subtractDays(new Date(), 1),
			null);

		VaccinationDto vaccination = creator.createVaccinationWithDetails(
			event.getReportingUser(),
			immunization.toReference(),
			HealthConditionsDto.build(),
			DateHelper.addDays(new Date(), 1),
			Vaccine.MRNA_1273,
			VaccineManufacturer.MODERNA,
			VaccinationInfoSource.UNKNOWN,
			"inn2",
			"456",
			"code456",
			"2");

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.withEvent(event.toReference());

		List<EventParticipantExportDto> results =
			getEventParticipantFacade().getExportList(eventParticipantCriteria, Collections.emptySet(), 0, 100, Language.EN, null);

		// List should have two entries
		assertThat(results, Matchers.hasSize(2));
		EventParticipantExportDto exportDto = results.get(0);

		assertNull(exportDto.getFirstVaccinationDate());
		assertNull(exportDto.getVaccineName());
		assertNull(exportDto.getLastVaccinationDate());
		assertNull(exportDto.getVaccinationInfoSource());
		assertNull(exportDto.getVaccineInn());
		assertNull(exportDto.getVaccineBatchNumber());
		assertNull(exportDto.getVaccineAtcCode());
		assertEquals(exportDto.getVaccinationDoses(), "");
	}

	@Test
	public void testCreateWithoutUuid() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceOfficer(rdcf);
		EventParticipantDto eventParticipant = new EventParticipantDto();
		eventParticipant.setEvent(creator.createEvent(user.toReference()).toReference());
		eventParticipant.setPerson(creator.createPerson());
		eventParticipant.setReportingUser(user.toReference());

		EventParticipantDto savedEventParticipant = getEventParticipantFacade().save(eventParticipant);

		MatcherAssert.assertThat(savedEventParticipant.getUuid(), not(isEmptyOrNullString()));
	}

	@Test
	public void testGetByEventUuids() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		EventDto event1 = creator.createEvent(user.toReference());
		EventDto event2 = creator.createEvent(user.toReference());

		PersonDto person1 = creator.createPerson();
		PersonDto person2 = creator.createPerson();
		creator.createEventParticipant(event1.toReference(), person1, user.toReference());
		creator.createEventParticipant(event1.toReference(), person2, user.toReference());
		creator.createEventParticipant(event2.toReference(), person1, user.toReference());

		List<EventParticipantDto> eps = getEventParticipantFacade().getByEventUuids(Collections.singletonList(event1.getUuid()));
		assertThat(eps, hasSize(2));
		eps = getEventParticipantFacade().getByEventUuids(Arrays.asList(event1.getUuid(), event2.getUuid()));
		assertThat(eps, hasSize(3));
	}

	@Test
	public void testGetByPersonUuids() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		EventDto event1 = creator.createEvent(user.toReference());
		EventDto event2 = creator.createEvent(user.toReference());

		PersonDto person1 = creator.createPerson();
		PersonDto person2 = creator.createPerson();
		creator.createEventParticipant(event1.toReference(), person1, user.toReference());
		creator.createEventParticipant(event1.toReference(), person2, user.toReference());
		creator.createEventParticipant(event2.toReference(), person1, user.toReference());

		List<EventParticipantDto> eps = getEventParticipantFacade().getByPersonUuids(Collections.singletonList(person1.getUuid()));
		assertThat(eps, hasSize(2));
		eps = getEventParticipantFacade().getByPersonUuids(Arrays.asList(person1.getUuid(), person2.getUuid()));
		assertThat(eps, hasSize(3));
	}

	@Test
	public void testExistEventParticipantWithDeletedFalse() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		EventDto event = creator.createEvent(user.toReference());
		PersonDto person = creator.createPerson();

		creator.createEventParticipant(event.toReference(), person, user.toReference());

		boolean exist = getEventParticipantFacade().exists(person.getUuid(), event.getUuid());
		assertTrue(exist);
	}

	@Test
	public void testExistEventParticipantWithDeletedTrue() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		EventDto event = creator.createEvent(user.toReference());
		PersonDto person = creator.createPerson();

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		getEventParticipantFacade().delete(eventParticipant.toReference().getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));

		boolean exist = getEventParticipantFacade().exists(person.getUuid(), event.getUuid());
		assertFalse(exist);
	}

	@Test
	public void testEventParticipantTestResultWithMultipleSamples() {
		RDCF rdcf = new RDCF(creator.createRDCFEntities());
		UserDto user = creator.createNationalUser();
		EventDto event = creator.createEvent(user.toReference());
		PersonDto person = creator.createPerson();

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());

		Calendar calendarDay1 = Calendar.getInstance();
		calendarDay1.set(2022, 7, 1);

		Calendar calendarDay10 = Calendar.getInstance();
		calendarDay10.set(2022, 7, 10);

		Calendar calendarDay15 = Calendar.getInstance();
		calendarDay15.set(2022, 7, 15);

		SampleDto sampleDto = creator.createSample(
			eventParticipant.toReference(),
			calendarDay1.getTime(),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			rdcf.facility,
			s -> s.setPathogenTestResult(PathogenTestResultType.POSITIVE));

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setEvent(event.toReference());
		List<EventParticipantIndexDto> eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(1, eventParticipantIndexDtos.size());
		assertEquals(PathogenTestResultType.POSITIVE, eventParticipantIndexDtos.get(0).getPathogenTestResult());
		assertEquals(calendarDay1.getTime(), eventParticipantIndexDtos.get(0).getSampleDateTime());

		creator.createSample(
			eventParticipant.toReference(),
			calendarDay10.getTime(),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			rdcf.facility,
			s -> s.setPathogenTestResult(PathogenTestResultType.NEGATIVE));

		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(1, eventParticipantIndexDtos.size());
		assertEquals(PathogenTestResultType.POSITIVE, eventParticipantIndexDtos.get(0).getPathogenTestResult());
		assertEquals(calendarDay10.getTime(), eventParticipantIndexDtos.get(0).getSampleDateTime());

		getSampleFacade().delete(sampleDto.getUuid(), new DeletionDetails());

		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(1, eventParticipantIndexDtos.size());
		assertEquals(PathogenTestResultType.NEGATIVE, eventParticipantIndexDtos.get(0).getPathogenTestResult());
		assertEquals(calendarDay10.getTime(), eventParticipantIndexDtos.get(0).getSampleDateTime());
	}

	@Test
	public void testEventParticipantWithArchivedEvent() {
		RDCF rdcf = new RDCF(creator.createRDCFEntities());
		UserDto user = creator.createNationalUser();
		EventDto event = creator.createEvent(user.toReference());
		PersonDto person = creator.createPerson();

		EventParticipantDto eventParticipant1 = creator.createEventParticipant(event.toReference(), person, user.toReference());
		EventParticipantDto eventParticipant2 = creator.createEventParticipant(event.toReference(), person, user.toReference());

		FeatureConfigurationIndexDto editArchivedFeatureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(editArchivedFeatureConfiguration, FeatureType.EDIT_ARCHIVED_ENTITIES);

		getEventFacade().archive(event.getUuid(), null);
		getEventParticipantFacade().dearchive(Arrays.asList(eventParticipant1.getUuid()), null);

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setEvent(event.toReference());
		eventParticipantCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED);

		List<EventParticipantIndexDto> eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(2, eventParticipantIndexDtos.size());
		List<String> eventParticipantUuids = eventParticipantIndexDtos.stream().map(ev -> ev.getUuid()).collect(Collectors.toList());
		assertTrue(eventParticipantUuids.contains(eventParticipant1.getUuid()));
		assertTrue(eventParticipantUuids.contains(eventParticipant2.getUuid()));

		eventParticipantCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(1, eventParticipantIndexDtos.size());
		assertEquals(eventParticipant1.getUuid(), eventParticipantIndexDtos.get(0).getUuid());

		eventParticipantCriteria.relevanceStatus(EntityRelevanceStatus.ARCHIVED);
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(1, eventParticipantIndexDtos.size());
		assertEquals(eventParticipant2.getUuid(), eventParticipantIndexDtos.get(0).getUuid());

		editArchivedFeatureConfiguration.setEnabled(false);
		getFeatureConfigurationFacade().saveFeatureConfiguration(editArchivedFeatureConfiguration, FeatureType.EDIT_ARCHIVED_ENTITIES);

		eventParticipantCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED);
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(2, eventParticipantIndexDtos.size());
		eventParticipantUuids = eventParticipantIndexDtos.stream().map(ev -> ev.getUuid()).collect(Collectors.toList());
		assertTrue(eventParticipantUuids.contains(eventParticipant1.getUuid()));
		assertTrue(eventParticipantUuids.contains(eventParticipant2.getUuid()));

		eventParticipantCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(0, eventParticipantIndexDtos.size());

		eventParticipantCriteria.relevanceStatus(EntityRelevanceStatus.ARCHIVED);
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(2, eventParticipantIndexDtos.size());
		eventParticipantUuids = eventParticipantIndexDtos.stream().map(ev -> ev.getUuid()).collect(Collectors.toList());
		assertTrue(eventParticipantUuids.contains(eventParticipant1.getUuid()));
		assertTrue(eventParticipantUuids.contains(eventParticipant2.getUuid()));
	}

	@Test
	public void testEventParticipantIndexListSorting() {
		UserDto user = creator.createNationalUser();
		EventDto event = creator.createEvent(user.toReference());
		PersonDto person = creator.createPerson();

		creator.createEventParticipant(event.toReference(), person, "Z", user.toReference());
		EventParticipantDto eventParticipant2 = creator.createEventParticipant(event.toReference(), person, "A", user.toReference());

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setEvent(event.toReference());
		SortProperty sortProperty = new SortProperty(EventParticipantDto.INVOLVEMENT_DESCRIPTION, true);
		List<EventParticipantIndexDto> eventParticipantIndexDtos =
			getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, Collections.singletonList(sortProperty));
		assertEquals(2, eventParticipantIndexDtos.size());
		assertEquals(eventParticipant2.getUuid(), eventParticipantIndexDtos.get(0).getUuid());
	}

	@Test
	public void searchEventParticipantsByPersonPhone() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto personWithPhone = creator.createPerson("personWithPhone", "test");
		PersonDto personWithoutPhone = creator.createPerson("personWithoutPhone", "test");

		PersonContactDetailDto primaryPhone =
			creator.createPersonContactDetail(personWithPhone.toReference(), true, PersonContactDetailType.PHONE, "111222333");
		PersonContactDetailDto secondaryPhone =
			creator.createPersonContactDetail(personWithoutPhone.toReference(), false, PersonContactDetailType.PHONE, "444555666");

		personWithPhone.getPersonContactDetails().add(primaryPhone);
		personWithPhone.getPersonContactDetails().add(secondaryPhone);
		getPersonFacade().save(personWithPhone);

		EventDto eventDto = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantDto1 = creator.createEventParticipant(eventDto.toReference(), personWithPhone, user.toReference());
		EventParticipantDto eventParticipantDto2 = creator.createEventParticipant(eventDto.toReference(), personWithoutPhone, user.toReference());

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setEvent(eventDto.toReference());
		List<EventParticipantIndexDto> eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(2, eventParticipantIndexDtos.size());
		List<String> uuids = eventParticipantIndexDtos.stream().map(c -> c.getUuid()).collect(Collectors.toList());
		assertTrue(uuids.contains(eventParticipantDto1.getUuid()));
		assertTrue(uuids.contains(eventParticipantDto2.getUuid()));

		eventParticipantCriteria.setFreeText("111222333");
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(1, eventParticipantIndexDtos.size());
		assertEquals(eventParticipantDto1.getUuid(), eventParticipantIndexDtos.get(0).getUuid());

		eventParticipantCriteria.setFreeText("444555666");
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(0, eventParticipantIndexDtos.size());
	}

	@Test
	public void searchEventParticipantsByPersonEmail() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto personWithEmail = creator.createPerson("personWithEmail", "test");
		PersonDto personWithoutEmail = creator.createPerson("personWithoutEmail", "test");

		PersonContactDetailDto primaryEmail =
			creator.createPersonContactDetail(personWithEmail.toReference(), true, PersonContactDetailType.EMAIL, "test1@email.com");
		PersonContactDetailDto secondaryEmail =
			creator.createPersonContactDetail(personWithoutEmail.toReference(), false, PersonContactDetailType.EMAIL, "test2@email.com");

		personWithEmail.getPersonContactDetails().add(primaryEmail);
		personWithEmail.getPersonContactDetails().add(secondaryEmail);
		getPersonFacade().save(personWithEmail);

		EventDto eventDto = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantDto1 = creator.createEventParticipant(eventDto.toReference(), personWithEmail, user.toReference());
		EventParticipantDto eventParticipantDto2 = creator.createEventParticipant(eventDto.toReference(), personWithoutEmail, user.toReference());

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setEvent(eventDto.toReference());
		List<EventParticipantIndexDto> eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(2, eventParticipantIndexDtos.size());
		List<String> uuids = eventParticipantIndexDtos.stream().map(c -> c.getUuid()).collect(Collectors.toList());
		assertTrue(uuids.contains(eventParticipantDto1.getUuid()));
		assertTrue(uuids.contains(eventParticipantDto2.getUuid()));

		eventParticipantCriteria.setFreeText("test1@email.com");
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(1, eventParticipantIndexDtos.size());
		assertEquals(eventParticipantDto1.getUuid(), eventParticipantIndexDtos.get(0).getUuid());

		eventParticipantCriteria.setFreeText("test2@email.com");
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(0, eventParticipantIndexDtos.size());
	}

	@Test
	public void searchEventParticipantsByPersonOtherDetail() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto personWithOtherDetail = creator.createPerson("personWithOtherDetail", "test");
		PersonDto personWithoutOtherDetail = creator.createPerson("personWithoutOtherDetail", "test");

		PersonContactDetailDto primaryOtherDetail =
			creator.createPersonContactDetail(personWithOtherDetail.toReference(), true, PersonContactDetailType.OTHER, "detail1");
		PersonContactDetailDto secondaryOtherDetail =
			creator.createPersonContactDetail(personWithoutOtherDetail.toReference(), false, PersonContactDetailType.OTHER, "detail2");

		personWithOtherDetail.getPersonContactDetails().add(primaryOtherDetail);
		personWithOtherDetail.getPersonContactDetails().add(secondaryOtherDetail);
		getPersonFacade().save(personWithOtherDetail);

		EventDto eventDto = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantDto1 = creator.createEventParticipant(eventDto.toReference(), personWithOtherDetail, user.toReference());
		EventParticipantDto eventParticipantDto2 =
			creator.createEventParticipant(eventDto.toReference(), personWithoutOtherDetail, user.toReference());

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setEvent(eventDto.toReference());
		List<EventParticipantIndexDto> eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(2, eventParticipantIndexDtos.size());
		List<String> uuids = eventParticipantIndexDtos.stream().map(c -> c.getUuid()).collect(Collectors.toList());
		assertTrue(uuids.contains(eventParticipantDto1.getUuid()));
		assertTrue(uuids.contains(eventParticipantDto2.getUuid()));

		eventParticipantCriteria.setFreeText("detail1");
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(1, eventParticipantIndexDtos.size());
		assertEquals(eventParticipantDto1.getUuid(), eventParticipantIndexDtos.get(0).getUuid());

		eventParticipantCriteria.setFreeText("detail2");
		eventParticipantIndexDtos = getEventParticipantFacade().getIndexList(eventParticipantCriteria, 0, 100, null);
		assertEquals(0, eventParticipantIndexDtos.size());
	}

	@Test
	public void testSaveEventParticipantWithEventArchiving() {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = createUser(rdcf);
		EventDto event = createEvent(user, rdcf);
		// Validate that for a newly created Event Participant the archived status is kept from the Event
		getEventFacade().archive(Collections.singletonList(event.getUuid()));
		PersonDto eventPersonArchived = creator.createPerson("EventArchived", "OrganizerArchived");
		EventParticipantDto archivedEventParticipantDto =
			creator.createEventParticipant(event.toReference(), eventPersonArchived, "event Director", user.toReference());
		assertTrue(getEventParticipantFacade().isArchived(archivedEventParticipantDto.getUuid()));

		// Validate that for a newly created Event Participant the active status is kept from the Event
		getEventFacade().dearchive(Collections.singletonList(event.getUuid()), "reason");
		PersonDto eventPersonActive = creator.createPerson("EventActive", "OrganizerActive");
		EventParticipantDto activeEventParticipantDto =
			creator.createEventParticipant(event.toReference(), eventPersonActive, "event Director", user.toReference());
		assertFalse(getEventParticipantFacade().isArchived(activeEventParticipantDto.getUuid()));

		//Validate that the newly created Event Participant status is in sync with Event and with existing Event Participant
		EventParticipantDto alreadyArchivedParticipantDto =
			creator.createEventParticipant(event.toReference(), eventPersonArchived, "event Director", user.toReference());
		assertFalse(getEventFacade().isArchived(event.getUuid()));
		assertFalse(getEventParticipantFacade().isArchived(archivedEventParticipantDto.getUuid()));
		assertFalse(getEventParticipantFacade().isArchived(alreadyArchivedParticipantDto.getUuid()));
	}

	private UserDto createUser(TestDataCreator.RDCF rdcf) {
		return creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
	}

	private EventDto createEvent(UserDto user, TestDataCreator.RDCF rdcf) {
		return creator.createEvent(
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
	}
}
