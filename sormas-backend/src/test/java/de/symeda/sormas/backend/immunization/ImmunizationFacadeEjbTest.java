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

package de.symeda.sormas.backend.immunization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationSimilarityCriteria;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.immunization.entity.Immunization;

public class ImmunizationFacadeEjbTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;

	private UserDto districtUser1;
	private UserDto districtUser2;
	private UserDto nationalUser;
	private UserDto covidLimitedDistrictUser;

	@Override
	public void init() {
		super.init();
		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		nationalUser = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.community.getUuid(),
			rdcf1.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));

		districtUser1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		districtUser2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		covidLimitedDistrictUser = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"OffCovid",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
		covidLimitedDistrictUser.setLimitedDiseases(Collections.singleton(Disease.CORONAVIRUS));
		getUserFacade().saveUser(covidLimitedDistrictUser, false);
	}

	@Test
	public void testValidateWithNullReportingUser() {
		loginWith(districtUser1);

		PersonDto person = creator.createPerson("John", "Doe");
		assertThrows(
			ValidationRuntimeException.class,
			() -> creator.createImmunization(
				Disease.CORONAVIRUS,
				person.toReference(),
				null,
				ImmunizationStatus.ACQUIRED,
				MeansOfImmunization.VACCINATION,
				ImmunizationManagementStatus.COMPLETED,
				rdcf1));
	}

	@Test
	public void testSaveAndGetByUuid() {
		loginWith(nationalUser);

		PersonDto person = creator.createPerson("John", "Doe");
		ImmunizationDto immunizationDto = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);
		ImmunizationDto actual = getImmunizationFacade().getByUuid(immunizationDto.getUuid());
		assertEquals(immunizationDto.getUuid(), actual.getUuid());
		assertEquals(immunizationDto.getPerson(), actual.getPerson());
	}

	@Test
	public void testGetAllSince() {
		loginWith(nationalUser);

		PersonDto person = creator.createPerson("John", "Doe");
		creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);
		creator.createImmunization(
			Disease.DENGUE,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);
		creator.createImmunization(
			Disease.MONKEYPOX,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);

		List<ImmunizationDto> allAfter = getImmunizationFacade().getAllAfter(UtilDate.yesterday());
		assertEquals(3, allAfter.size());

		List<ImmunizationDto> allAfterBatched = getImmunizationFacade().getAllAfter(UtilDate.yesterday(), 1, null);
		assertEquals(1, allAfterBatched.size());
		assertEquals(Disease.CORONAVIRUS, allAfterBatched.get(0).getDisease());

		List<PersonDto> allPersonsAfter = getPersonFacade().getAllAfter(UtilDate.yesterday());
		assertEquals(1, allPersonsAfter.size());

		List<ImmunizationDto> allAfterWithUuid = getImmunizationFacade().getAllAfter(UtilDate.yesterday(), 5, "AAAAAA-AAAAAA-AAAAAA-AAAAAA");
		assertEquals(3, allAfterWithUuid.size());

		ImmunizationDto immunizationRead = allAfter.get(0);
		List<ImmunizationDto> allAfterOneMillisecondBefore =
			getImmunizationFacade().getAllAfter(new Date(immunizationRead.getChangeDate().getTime() - 1L), 5, EntityDto.NO_LAST_SYNCED_UUID);
		assertEquals(3, allAfterOneMillisecondBefore.size());

		List<ImmunizationDto> allAfterSameTime =
			getImmunizationFacade().getAllAfter(immunizationRead.getChangeDate(), 5, "AAAAAA-AAAAAA-AAAAAA-AAAAAA");
		assertEquals(3, allAfterSameTime.size());

		List<ImmunizationDto> allAfterSameTimeAndUuid =
			getImmunizationFacade().getAllAfter(immunizationRead.getChangeDate(), 5, immunizationRead.getUuid());
		assertEquals(2, allAfterSameTimeAndUuid.size());

		ImmunizationService immunizationService = getBean(ImmunizationService.class);
		Immunization byUuid = immunizationService.getByUuid(immunizationRead.getUuid());

		Immunization immunizationWithNanoseconds = new Immunization();
		Timestamp changeDate = new Timestamp(immunizationRead.getChangeDate().getTime());
		changeDate.setNanos(changeDate.getNanos() + 150000);
		immunizationWithNanoseconds.setChangeDate(changeDate);
		immunizationWithNanoseconds.setUuid("ZZZZZZ-ZZZZZZ-ZZZZZZ-ZZZZZZ");
		immunizationWithNanoseconds.setId(null);
		immunizationWithNanoseconds.setDisease(Disease.RABIES);
		immunizationWithNanoseconds.setPerson(byUuid.getPerson());
		immunizationWithNanoseconds.setReportingUser(byUuid.getReportingUser());
		immunizationWithNanoseconds.setReportDate(byUuid.getReportDate());
		immunizationService.ensurePersisted(immunizationWithNanoseconds);

		Timestamp changeAfterPersistence = immunizationService.getByUuid("ZZZZZZ-ZZZZZZ-ZZZZZZ-ZZZZZZ").getChangeDate();
		assertEquals(changeDate.getTime(), changeAfterPersistence.getTime());

		// Change date timestamps should be persisted with millisecond precision
		assertEquals(0, changeAfterPersistence.getNanos() % 1000000);

		List<ImmunizationDto> allAfterSeveralResultsSameTime =
			getImmunizationFacade().getAllAfter(immunizationRead.getChangeDate(), 5, immunizationRead.getUuid());
		assertEquals(3, allAfterSeveralResultsSameTime.size());

		List<ImmunizationDto> allAfterSeveralResultsSameTimeWithUuid =
			getImmunizationFacade().getAllAfter(immunizationRead.getChangeDate(), 5, "AAAAAA-AAAAAA-AAAAAA-AAAAAA");
		assertEquals(4, allAfterSeveralResultsSameTimeWithUuid.size());

		assertEquals(immunizationRead.getUuid(), allAfterSeveralResultsSameTimeWithUuid.get(0).getUuid());
		assertEquals("ZZZZZZ-ZZZZZZ-ZZZZZZ-ZZZZZZ", allAfterSeveralResultsSameTimeWithUuid.get(1).getUuid());
	}

	@Test
	public void testJurisdictionFiltering() {
		loginWith(nationalUser);

		PersonDto person1 = creator.createPerson("John", "Doe");
		PersonDto person2 = creator.createPerson("John2", "Doe2");

		ImmunizationDto nonSeenImmunization = creator.createImmunization(
			Disease.DENGUE,
			person1.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);
		ImmunizationDto seenImmunization = creator.createImmunization(
			Disease.DENGUE,
			person2.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf2);

		loginWith(districtUser2);
		List<ImmunizationDto> allAfter = getImmunizationFacade().getAllAfter(UtilDate.yesterday());
		assertEquals(1, allAfter.size());
		ImmunizationDto immunizationDto = allAfter.get(0);
		assertEquals(seenImmunization.getUuid(), immunizationDto.getUuid());
		assertEquals(seenImmunization.getPerson().getFirstName(), immunizationDto.getPerson().getFirstName());
		assertEquals(seenImmunization.getPerson().getLastName(), immunizationDto.getPerson().getLastName());

		// assert getting non seen immunization in grid is pseudonymized
		ImmunizationDto byUuid = getImmunizationFacade().getByUuid(nonSeenImmunization.getUuid());
		assertEquals(nonSeenImmunization.getUuid(), byUuid.getUuid());
		assertEquals("", byUuid.getPerson().getLastName());
		assertEquals("", byUuid.getPerson().getFirstName());

		List<PersonDto> allPersonsAfter = getPersonFacade().getAllAfter(UtilDate.yesterday());
		assertEquals(1, allPersonsAfter.size());
		PersonDto personDto = allPersonsAfter.get(0);
		assertEquals(person2.getUuid(), personDto.getUuid());

		loginWith(nationalUser);
		assertEquals(2, getPersonFacade().getAllAfter(UtilDate.yesterday()).size());
	}

	@Test
	public void testImmunizationCreatedByDistrictUserIsVisibleToThatUser() {
		loginWith(districtUser1);

		final PersonDto person = creator.createPerson("John", "Doe");

		final ImmunizationDto immunization = creator.createImmunization(
			Disease.ANTHRAX,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);

		final List<ImmunizationDto> allImmunizations = getImmunizationFacade().getAllAfter(UtilDate.yesterday());
		assertEquals(1, allImmunizations.size());
		final ImmunizationDto immunizationDto = allImmunizations.get(0);
		assertEquals(immunization.getUuid(), immunizationDto.getUuid());
		assertEquals(immunization.getImmunizationStatus(), immunizationDto.getImmunizationStatus());
		assertEquals(immunization.getImmunizationManagementStatus(), immunizationDto.getImmunizationManagementStatus());
		assertEquals(immunization.getMeansOfImmunization(), immunizationDto.getMeansOfImmunization());

		final List<PersonDto> allPersonsAfter = getPersonFacade().getAllAfter(UtilDate.yesterday());
		assertEquals(1, allPersonsAfter.size());
		final PersonDto personDto = allPersonsAfter.get(0);

		assertEquals(person.getUuid(), personDto.getUuid());
		assertEquals("John", personDto.getFirstName());
		assertEquals("Doe", personDto.getLastName());
	}

	@Test
	public void testOverdueImmunizationsFilterEnabled() {
		loginWith(nationalUser);

		final PersonDto person = creator.createPerson("John", "Doe");
		final Date now = new Date();
		final ImmunizationDto ongoingImmunizationThatEndedYesterday = createOngoingImmunizationWithEndDate(person, UtilDate.yesterday());
		createOngoingImmunizationWithEndDate(person, now);

		final ImmunizationCriteria immunizationCriteria = new ImmunizationCriteria();
		immunizationCriteria.setOnlyPersonsWithOverdueImmunization(true);
		final List<ImmunizationIndexDto> onlyOverdueImmunizations = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(1, onlyOverdueImmunizations.size());
		assertEquals(ongoingImmunizationThatEndedYesterday.getUuid(), onlyOverdueImmunizations.get(0).getUuid());
	}

	@Test
	public void testOverdueImmunizationsFilterEnabledAtStartOfDay() {
		loginWith(nationalUser);

		final PersonDto person = creator.createPerson("John", "Doe");
		final Date startOfDay = DateHelper.getStartOfDay(new Date());
		final ImmunizationDto ongoingImmunizationThatEndedYesterday = createOngoingImmunizationWithEndDate(person, UtilDate.yesterday());
		createOngoingImmunizationWithEndDate(person, startOfDay);

		final ImmunizationCriteria immunizationCriteria = new ImmunizationCriteria();
		immunizationCriteria.setOnlyPersonsWithOverdueImmunization(true);
		final List<ImmunizationIndexDto> onlyOverdueImmunizations = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(1, onlyOverdueImmunizations.size());
		assertEquals(ongoingImmunizationThatEndedYesterday.getUuid(), onlyOverdueImmunizations.get(0).getUuid());
	}

	@Test
	public void testOverdueImmunizationsFilterDisabled() {
		loginWith(nationalUser);

		final PersonDto person = creator.createPerson("John", "Doe");
		final Date now = new Date();
		createOngoingImmunizationWithEndDate(person, UtilDate.yesterday());
		createOngoingImmunizationWithEndDate(person, now);

		final ImmunizationCriteria immunizationCriteria = new ImmunizationCriteria();
		immunizationCriteria.setOnlyPersonsWithOverdueImmunization(false);
		final List<ImmunizationIndexDto> allImmunizations = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(2, allImmunizations.size());
	}

	private ImmunizationDto createOngoingImmunizationWithEndDate(PersonDto person, Date endDate) {
		return creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.ONGOING,
			rdcf2,
			UtilDate.from(LocalDate.now().minusDays(10)),
			endDate,
			null,
			UtilDate.tomorrow());
	}

	@Test
	public void testImmunizationAutomation() {
		loginWith(nationalUser);

		PersonDto person = creator.createPerson("John", "Doe");

		ImmunizationDto nonAcquiredImmunization = creator.createImmunization(
			Disease.DENGUE,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.NOT_ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);
		ImmunizationDto pendingImmunization = creator.createImmunization(
			Disease.DENGUE,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.PENDING,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);
		ImmunizationDto expiredImmunization = creator.createImmunization(
			Disease.DENGUE,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.EXPIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);
		ImmunizationDto acquiredImmunizationStillValid = creator.createImmunization(
			Disease.DENGUE,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf2,
			UtilDate.from(LocalDate.now().minusDays(10)),
			null,
			null,
			UtilDate.tomorrow());
		ImmunizationDto acquiredImmunizationNoLongerValid = creator.createImmunization(
			Disease.DENGUE,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf2,
			UtilDate.from(LocalDate.now().minusDays(10)),
			null,
			null,
			UtilDate.from(LocalDate.now().minusDays(2)));

		// immunizations before status automation update
		assertEquals(5, getImmunizationFacade().getAllAfter(UtilDate.from(LocalDate.now().minusDays(2))).size());
		assertEquals(ImmunizationStatus.NOT_ACQUIRED, getImmunizationFacade().getByUuid(nonAcquiredImmunization.getUuid()).getImmunizationStatus());
		assertEquals(ImmunizationStatus.PENDING, getImmunizationFacade().getByUuid(pendingImmunization.getUuid()).getImmunizationStatus());
		assertEquals(ImmunizationStatus.EXPIRED, getImmunizationFacade().getByUuid(expiredImmunization.getUuid()).getImmunizationStatus());
		assertEquals(
			ImmunizationStatus.ACQUIRED,
			getImmunizationFacade().getByUuid(acquiredImmunizationStillValid.getUuid()).getImmunizationStatus());
		assertEquals(
			ImmunizationStatus.ACQUIRED,
			getImmunizationFacade().getByUuid(acquiredImmunizationNoLongerValid.getUuid()).getImmunizationStatus());

		getImmunizationFacade().updateImmunizationStatuses();

		// immunizations after status automation update
		assertEquals(5, getImmunizationFacade().getAllAfter(UtilDate.from(LocalDate.now().minusDays(2))).size());
		assertEquals(ImmunizationStatus.NOT_ACQUIRED, getImmunizationFacade().getByUuid(nonAcquiredImmunization.getUuid()).getImmunizationStatus());
		assertEquals(ImmunizationStatus.PENDING, getImmunizationFacade().getByUuid(pendingImmunization.getUuid()).getImmunizationStatus());
		assertEquals(ImmunizationStatus.EXPIRED, getImmunizationFacade().getByUuid(expiredImmunization.getUuid()).getImmunizationStatus());
		assertEquals(
			ImmunizationStatus.ACQUIRED,
			getImmunizationFacade().getByUuid(acquiredImmunizationStillValid.getUuid()).getImmunizationStatus());
		assertEquals(
			ImmunizationStatus.EXPIRED,
			getImmunizationFacade().getByUuid(acquiredImmunizationNoLongerValid.getUuid()).getImmunizationStatus());
	}

	@Test
	public void testSimilarImmunizationsForNonExistingRange() {

		loginWith(nationalUser);
		final PersonDto person = creator.createPerson("John", "Doe");
		final LocalDateTime now = LocalDateTime.now();
		createImmunizationWithDateRange(person, null, null);

		assertSimilarImmunizationsSize(1, person, now, null);
		assertSimilarImmunizationsSize(1, person, now, now.plusDays(5));
		assertSimilarImmunizationsSize(1, person, now.minusDays(10), now.minusDays(5));
		assertSimilarImmunizationsSize(1, person, now.minusDays(10), null);
		assertSimilarImmunizationsSize(1, person, now.minusDays(30), null);
		assertSimilarImmunizationsSize(1, person, null, now.minusDays(10));
		assertSimilarImmunizationsSize(1, person, null, now.minusDays(30));
		assertSimilarImmunizationsSize(1, person, now.plusDays(1), null);
		assertSimilarImmunizationsSize(1, person, now.plusDays(1), now.plusDays(100));
	}

	@Test
	public void testSimilarImmunizationsForExistingRange() {

		loginWith(nationalUser);
		final PersonDto person = creator.createPerson("John", "Doe");
		final LocalDateTime now = LocalDateTime.now();
		createImmunizationWithDateRange(person, UtilDate.from(now.minusDays(20)), UtilDate.from(now));

		assertSimilarImmunizationsSize(1, person, now, null);
		assertSimilarImmunizationsSize(1, person, now, now.plusDays(5));
		assertSimilarImmunizationsSize(1, person, now.minusDays(10), now.minusDays(5));
		assertSimilarImmunizationsSize(1, person, now.minusDays(10), null);
		assertSimilarImmunizationsSize(1, person, now.minusDays(30), null);
		assertSimilarImmunizationsSize(1, person, null, now.minusDays(10));
		assertSimilarImmunizationsSize(0, person, null, now.minusDays(30));
		assertSimilarImmunizationsSize(0, person, now.plusDays(1), null);
		assertSimilarImmunizationsSize(0, person, now.plusDays(1), now.plusDays(100));
	}

	@Test
	public void testSimilarImmunizationsForExistingStartDate() {

		loginWith(nationalUser);
		final PersonDto person = creator.createPerson("John", "Doe");
		final LocalDateTime now = LocalDateTime.now();
		createImmunizationWithDateRange(person, UtilDate.from(now.minusDays(20)), null);

		assertSimilarImmunizationsSize(1, person, now, null);
		assertSimilarImmunizationsSize(1, person, now, now.plusDays(5));
		assertSimilarImmunizationsSize(1, person, now.minusDays(10), now.minusDays(5));
		assertSimilarImmunizationsSize(1, person, now.minusDays(10), null);
		assertSimilarImmunizationsSize(1, person, now.minusDays(30), null);
		assertSimilarImmunizationsSize(1, person, null, now.minusDays(10));
		assertSimilarImmunizationsSize(0, person, null, now.minusDays(30));
		assertSimilarImmunizationsSize(1, person, now.plusDays(1), null);
		assertSimilarImmunizationsSize(1, person, now.plusDays(1), now.plusDays(100));
	}

	@Test
	public void testSimilarImmunizationsForExistingEndDate() {

		loginWith(nationalUser);
		final PersonDto person = creator.createPerson("John", "Doe");
		final LocalDateTime now = LocalDateTime.now();
		createImmunizationWithDateRange(person, null, UtilDate.from(now));

		assertSimilarImmunizationsSize(1, person, now, null);
		assertSimilarImmunizationsSize(1, person, now, now.plusDays(5));
		assertSimilarImmunizationsSize(1, person, now.minusDays(10), now.minusDays(5));
		assertSimilarImmunizationsSize(1, person, now.minusDays(10), null);
		assertSimilarImmunizationsSize(1, person, now.minusDays(30), null);
		assertSimilarImmunizationsSize(1, person, null, now.minusDays(10));
		assertSimilarImmunizationsSize(1, person, null, now.minusDays(30));
		assertSimilarImmunizationsSize(0, person, now.plusDays(1), null);
		assertSimilarImmunizationsSize(0, person, now.plusDays(1), now.plusDays(100));
	}

	private void createImmunizationWithDateRange(PersonDto person, Date startDate, Date endDate) {
		final ImmunizationDto immunization = creator.createImmunizationDto(
			Disease.ANTHRAX,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);
		immunization.setStartDate(startDate);
		immunization.setEndDate(endDate);
		getImmunizationFacade().save(immunization);
	}

	private void assertSimilarImmunizationsSize(int size, PersonDto person, LocalDateTime startDate, LocalDateTime endDate) {

		final ImmunizationSimilarityCriteria criteria = new ImmunizationSimilarityCriteria.Builder().withDisease(Disease.ANTHRAX)
			.withMeansOfImmunization(MeansOfImmunization.VACCINATION)
			.withPerson(person.getUuid())
			.withStartDate(UtilDate.from(startDate))
			.withEndDate(UtilDate.from(endDate))
			.build();
		final List<ImmunizationDto> similarImmunizations = getImmunizationFacade().getSimilarImmunizations(criteria);
		assertEquals(size, similarImmunizations.size());
	}

	@Test
	public void testUpdateImmunizationStatusBasedOnVaccinationsOngoing() {
		loginWith(nationalUser);

		final PersonDto person = creator.createPerson("John", "Doe");

		final ImmunizationDto immunization = creator.createImmunizationDto(
			Disease.DENGUE,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.PENDING,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.SCHEDULED,
			rdcf1);

		immunization.setNumberOfDoses(2);
		immunization.setStartDate(DateHelper.subtractDays(new Date(), 1));
		immunization.setVaccinations(
			Arrays.asList(creator.createVaccinationDto(nationalUser.toReference(), immunization.toReference(), new HealthConditionsDto())));
		getImmunizationFacade().save(immunization);

		final ImmunizationDto immWithOneVac = getImmunizationFacade().getByUuid(immunization.getUuid());
		assertEquals(1, immWithOneVac.getVaccinations().size());
		assertEquals(ImmunizationManagementStatus.ONGOING, immWithOneVac.getImmunizationManagementStatus());
		assertEquals(ImmunizationStatus.PENDING, immWithOneVac.getImmunizationStatus());
	}

	@Test
	public void testUpdateImmunizationStatusBasedOnVaccinationsCompleted() {
		loginWith(nationalUser);

		final PersonDto person = creator.createPerson("John", "Doe");

		ImmunizationDto immunization = getImmunizationFacade().save(
			creator.createImmunizationDto(
				Disease.DENGUE,
				person.toReference(),
				nationalUser.toReference(),
				ImmunizationStatus.PENDING,
				MeansOfImmunization.VACCINATION,
				ImmunizationManagementStatus.SCHEDULED,
				rdcf1));

		immunization.setNumberOfDoses(2);
		immunization.setStartDate(DateHelper.subtractDays(new Date(), 1));
		immunization.setVaccinations(
			Arrays.asList(
				creator.createVaccinationDto(nationalUser.toReference(), immunization.toReference(), new HealthConditionsDto()),
				creator.createVaccinationDto(nationalUser.toReference(), immunization.toReference(), new HealthConditionsDto())));
		getImmunizationFacade().save(immunization);

		final ImmunizationDto immWithTwoVac = getImmunizationFacade().getByUuid(immunization.getUuid());
		assertEquals(2, immWithTwoVac.getVaccinations().size());
		assertEquals(ImmunizationManagementStatus.COMPLETED, immWithTwoVac.getImmunizationManagementStatus());
		assertEquals(ImmunizationStatus.ACQUIRED, immWithTwoVac.getImmunizationStatus());
	}

	@Test
	public void testNoImmunizationStatusUpdateWhenExpired() {
		loginWith(nationalUser);

		final PersonDto person = creator.createPerson("John", "Doe");

		final ImmunizationDto immunization = creator.createImmunizationDto(
			Disease.DENGUE,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.EXPIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.SCHEDULED,
			rdcf1);

		immunization.setNumberOfDoses(2);
		immunization.setStartDate(DateHelper.subtractDays(new Date(), 1));
		immunization.setVaccinations(
			Arrays.asList(
				creator.createVaccinationDto(nationalUser.toReference(), immunization.toReference(), new HealthConditionsDto()),
				creator.createVaccinationDto(nationalUser.toReference(), immunization.toReference(), new HealthConditionsDto())));
		getImmunizationFacade().save(immunization);

		final ImmunizationDto immWithTwoVac = getImmunizationFacade().getByUuid(immunization.getUuid());
		assertEquals(2, immWithTwoVac.getVaccinations().size());
		assertEquals(ImmunizationManagementStatus.SCHEDULED, immWithTwoVac.getImmunizationManagementStatus());
		assertEquals(ImmunizationStatus.EXPIRED, immWithTwoVac.getImmunizationStatus());
	}

	@Test
	public void testNoImmunizationStatusUpdateWhenNotAcquired() {
		loginWith(nationalUser);

		final PersonDto person = creator.createPerson("John", "Doe");

		final ImmunizationDto immunization = creator.createImmunizationDto(
			Disease.DENGUE,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.NOT_ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.SCHEDULED,
			rdcf1);

		immunization.setNumberOfDoses(2);
		immunization.setStartDate(DateHelper.subtractDays(new Date(), 1));
		immunization.setVaccinations(
			Arrays.asList(
				creator.createVaccinationDto(nationalUser.toReference(), immunization.toReference(), new HealthConditionsDto()),
				creator.createVaccinationDto(nationalUser.toReference(), immunization.toReference(), new HealthConditionsDto())));
		getImmunizationFacade().save(immunization);

		final ImmunizationDto immWithTwoVac = getImmunizationFacade().getByUuid(immunization.getUuid());
		assertEquals(2, immWithTwoVac.getVaccinations().size());
		assertEquals(ImmunizationManagementStatus.SCHEDULED, immWithTwoVac.getImmunizationManagementStatus());
		assertEquals(ImmunizationStatus.NOT_ACQUIRED, immWithTwoVac.getImmunizationStatus());
	}

	@Test
	public void testGetIndexListIsFilteredByCurrentUserLimitedDisease() {
		loginWith(nationalUser);

		final PersonDto person = creator.createPerson("John", "Doe");

		creator.createImmunization(
			Disease.ANTHRAX,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);

		creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);

		loginWith(districtUser1);
		assertEquals(2, getImmunizationFacade().getIndexList(new ImmunizationCriteria(), 0, 100, null).size());

		loginWith(covidLimitedDistrictUser);
		assertEquals(1, getImmunizationFacade().getIndexList(new ImmunizationCriteria(), 0, 100, null).size());
	}

	@Test
	public void testGetIndexListFilterByCriteria() {

		PersonDto personWithDetails = creator.createPerson("personWithDetails", "test");
		PersonDto personWithoutDetails = creator.createPerson("personWithoutDetails", "test");

		PersonContactDetailDto primaryEmail =
			creator.createPersonContactDetail(personWithDetails.toReference(), true, PersonContactDetailType.EMAIL, "test1@email.com");
		PersonContactDetailDto secondaryEmail =
			creator.createPersonContactDetail(personWithDetails.toReference(), false, PersonContactDetailType.EMAIL, "test2@email.com");
		PersonContactDetailDto primaryPhone =
			creator.createPersonContactDetail(personWithDetails.toReference(), true, PersonContactDetailType.PHONE, "111222333");
		PersonContactDetailDto secondaryPhone =
			creator.createPersonContactDetail(personWithDetails.toReference(), false, PersonContactDetailType.PHONE, "444555666");
		PersonContactDetailDto primaryOtherDetail =
			creator.createPersonContactDetail(personWithDetails.toReference(), true, PersonContactDetailType.OTHER, "detail1");
		PersonContactDetailDto secondaryOtherDetail =
			creator.createPersonContactDetail(personWithDetails.toReference(), false, PersonContactDetailType.OTHER, "detail2");

		personWithDetails.getPersonContactDetails().add(primaryEmail);
		personWithDetails.getPersonContactDetails().add(secondaryEmail);
		personWithDetails.getPersonContactDetails().add(primaryPhone);
		personWithDetails.getPersonContactDetails().add(secondaryPhone);
		personWithDetails.getPersonContactDetails().add(primaryOtherDetail);
		personWithDetails.getPersonContactDetails().add(secondaryOtherDetail);
		getPersonFacade().save(personWithDetails);

		Date endDate = UtilDate.from(LocalDate.now().minusDays(1));
		ImmunizationDto immunizationDto1 = createOngoingImmunizationWithEndDate(personWithDetails, endDate);
		ImmunizationDto immunizationDto2 = createOngoingImmunizationWithEndDate(personWithoutDetails, endDate);

		ImmunizationCriteria immunizationCriteria = new ImmunizationCriteria();
		List<ImmunizationIndexDto> immunizationIndexDtos = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(2, immunizationIndexDtos.size());
		List<String> uuids = immunizationIndexDtos.stream().map(c -> c.getUuid()).collect(Collectors.toList());
		assertTrue(uuids.contains(immunizationDto1.getUuid()));
		assertTrue(uuids.contains(immunizationDto2.getUuid()));

		// Email address
		immunizationCriteria.setNameAddressPhoneEmailLike("test1@email.com");
		immunizationIndexDtos = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(1, immunizationIndexDtos.size());
		assertEquals(immunizationDto1.getUuid(), immunizationIndexDtos.get(0).getUuid());

		immunizationCriteria.setNameAddressPhoneEmailLike("test2@email.com");
		immunizationIndexDtos = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(0, immunizationIndexDtos.size());

		// Phone number
		immunizationCriteria.setNameAddressPhoneEmailLike("111222333");
		immunizationIndexDtos = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(1, immunizationIndexDtos.size());
		assertEquals(immunizationDto1.getUuid(), immunizationIndexDtos.get(0).getUuid());

		immunizationCriteria.setNameAddressPhoneEmailLike("444555666");
		immunizationIndexDtos = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(0, immunizationIndexDtos.size());

		// Other contact detail
		immunizationCriteria.setNameAddressPhoneEmailLike("detail1");
		immunizationIndexDtos = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(1, immunizationIndexDtos.size());
		assertEquals(immunizationDto1.getUuid(), immunizationIndexDtos.get(0).getUuid());

		immunizationCriteria.setNameAddressPhoneEmailLike("detail2");
		immunizationIndexDtos = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(0, immunizationIndexDtos.size());

		// Immunization UUID
		immunizationCriteria.setNameAddressPhoneEmailLike(immunizationDto1.getUuid());
		immunizationIndexDtos = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(1, immunizationIndexDtos.size());
		assertEquals(immunizationDto1.getUuid(), immunizationIndexDtos.get(0).getUuid());

		immunizationCriteria.setNameAddressPhoneEmailLike(immunizationDto2.getUuid());
		immunizationIndexDtos = getImmunizationFacade().getIndexList(immunizationCriteria, 0, 100, null);
		assertEquals(1, immunizationIndexDtos.size());
		assertEquals(immunizationDto2.getUuid(), immunizationIndexDtos.get(0).getUuid());
	}
}
