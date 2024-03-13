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

package de.symeda.sormas.backend.travelentry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.DeaContentEntry;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class TravelEntryFacadeEjbTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;

	private UserDto districtUser1;
	private UserDto districtUser2;
	private UserDto nationalUser;

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
	}

	@Test
	public void testSaveAndGetByUuid() {
		loginWith(nationalUser);

		PersonDto person = creator.createPerson("John", "Doe");

		TravelEntryDto travelEntry = creator.createTravelEntry(
			person.toReference(),
			nationalUser.toReference(),
			Disease.CORONAVIRUS,
			rdcf1.region,
			rdcf1.district,
			rdcf1.pointOfEntry);
		TravelEntryDto travelEntryByUuid = getTravelEntryFacade().getByUuid(travelEntry.getUuid());
		assertEquals(travelEntry.getUuid(), travelEntryByUuid.getUuid());
		assertEquals(travelEntry.getPerson(), travelEntry.getPerson());
		assertEquals(travelEntry.getDisease(), travelEntry.getDisease());
	}

	@Test
	public void testGetAllSince() {
		loginWith(nationalUser);

		PersonDto person = creator.createPerson("John", "Doe");
		creator.createTravelEntry(
			person.toReference(),
			nationalUser.toReference(),
			Disease.CORONAVIRUS,
			rdcf1.region,
			rdcf1.district,
			rdcf1.pointOfEntry);
		creator.createTravelEntry(person.toReference(), nationalUser.toReference(), Disease.DENGUE, rdcf2.region, rdcf2.district, rdcf2.pointOfEntry);
		List<TravelEntryDto> allAfter = getTravelEntryFacade().getAllAfter(UtilDate.yesterday());
		assertEquals(2, allAfter.size());
	}

	@Test
	public void testCanSeeByResponsibleJurisdiction() {
		loginWith(nationalUser);

		PersonDto person = creator.createPerson("John", "Doe");

		TravelEntryDto seenTravelEntry = creator.createTravelEntry(
			person.toReference(),
			nationalUser.toReference(),
			Disease.CORONAVIRUS,
			rdcf1.region,
			rdcf1.district,
			rdcf1.pointOfEntry);
		TravelEntryDto notSeenTravelEntry = creator
			.createTravelEntry(person.toReference(), nationalUser.toReference(), Disease.DENGUE, rdcf2.region, rdcf2.district, rdcf2.pointOfEntry);

		loginWith(districtUser1);
		List<TravelEntryDto> allAfter = getTravelEntryFacade().getAllAfter(UtilDate.yesterday());
		assertEquals(1, allAfter.size());
		TravelEntryDto travelEntryDto = allAfter.get(0);
		assertEquals(seenTravelEntry.getUuid(), travelEntryDto.getUuid());
		assertEquals(seenTravelEntry.getPerson().getFirstName(), travelEntryDto.getPerson().getFirstName());
		assertEquals(seenTravelEntry.getPerson().getLastName(), travelEntryDto.getPerson().getLastName());

		TravelEntryDto notInJurisdictionTravelEntry = getTravelEntryFacade().getByUuid(notSeenTravelEntry.getUuid());
		assertEquals(notInJurisdictionTravelEntry.getUuid(), notSeenTravelEntry.getUuid());
		assertThat(notInJurisdictionTravelEntry.getPerson().getLastName(), is(isEmptyString()));
		assertThat(notInJurisdictionTravelEntry.getPerson().getFirstName(), is(isEmptyString()));
		assertEquals(notInJurisdictionTravelEntry.getDisease(), notSeenTravelEntry.getDisease());
	}

	@Test
	public void testFilterByFreeTextFilter() {
		loginWith(nationalUser);

		PersonDto person = creator.createPerson("John", "Doe");
		TravelEntryDto travelEntry = creator.createTravelEntry(
			person.toReference(),
			nationalUser.toReference(),
			Disease.CORONAVIRUS,
			rdcf1.region,
			rdcf1.district,
			rdcf1.pointOfEntry);

		PersonDto person2 = creator.createPerson("Sam", "Johnson");
		TravelEntryDto travelEntry2 = creator.createTravelEntry(
			person2.toReference(),
			nationalUser.toReference(),
			Disease.CORONAVIRUS,
			rdcf1.region,
			rdcf1.district,
			rdcf1.pointOfEntry);

		TravelEntryCriteria criteria = new TravelEntryCriteria();
		criteria.setNameUuidExternalIDLike("Doe");
		List<TravelEntryIndexDto> indexList = getTravelEntryFacade().getIndexList(criteria, null, null, new ArrayList<>());

		assertTrue(!indexList.isEmpty());
		assertEquals(1, indexList.size());
		assertEquals(person.getFirstName(), indexList.get(0).getPersonFirstName());
		assertEquals(person.getLastName(), indexList.get(0).getPersonLastName());
	}

	@Test
	public void testGetIndexList() {
		loginWith(nationalUser);

		PersonDto person1 = creator.createPerson("Peter", "Kruder");
		PersonDto person2 = creator.createPerson("Richard", "Dorfmeister");

		TravelEntryDto travelEntry1 = creator.createTravelEntry(
			person1.toReference(),
			nationalUser.toReference(),
			Disease.CORONAVIRUS,
			rdcf1.region,
			rdcf1.district,
			rdcf1.pointOfEntry);

		TravelEntryDto travelEntry2 = creator.createTravelEntry(
			person2.toReference(),
			nationalUser.toReference(),
			Disease.YELLOW_FEVER,
			rdcf1.region,
			rdcf1.district,
			rdcf1.pointOfEntry);

		List<TravelEntryIndexDto> indexList =
			getTravelEntryFacade().getIndexList(new TravelEntryCriteria().person(person1.toReference()), 0, 5, null);

		assertEquals(1, indexList.size());
		TravelEntryIndexDto indexDto = indexList.get(0);
		assertEquals(travelEntry1.getUuid(), indexDto.getUuid());
		assertEquals("Point of entry 1", indexDto.getPointOfEntryName());
	}

	@Test
	public void testGetTravelEntryUsersWithoutUsesLimitedToOthersDiseses() {
		UserDto limitedCovidNationalUser = creator.createUser(
			rdcf1,
			"Limited Disease Covid",
			"National User",
			Disease.CORONAVIRUS,
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		UserDto limitedDengueNationalUser = creator.createUser(
			rdcf1,
			"Limited Disease Dengue",
			"National User",
			Disease.DENGUE,
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		loginWith(nationalUser);
		PersonDto personDto = creator.createPerson();
		TravelEntryDto travelEntry = creator.createTravelEntry(
			personDto.toReference(),
			nationalUser.toReference(),
			Disease.CORONAVIRUS,
			rdcf1.region,
			rdcf1.district,
			rdcf1.pointOfEntry);

		List<UserReferenceDto> userReferenceDtos = getUserFacade().getUsersHavingTravelEntryInJurisdiction(travelEntry.toReference());
		assertNotNull(userReferenceDtos);
		assertTrue(userReferenceDtos.contains(nationalUser));
		assertTrue(userReferenceDtos.contains(districtUser1));
		assertTrue(userReferenceDtos.contains(limitedCovidNationalUser));
		assertFalse(userReferenceDtos.contains(limitedDengueNationalUser));
	}

	@Test
	public void testGetDeaContentOfLastTravelEntry() throws InterruptedException {

		loginWith(nationalUser);

		// 0. No data available
		assertNull(getTravelEntryFacade().getDeaContentOfLastTravelEntry());

		// 1. First travel entry
		{
			PersonDto personDto = creator.createPerson();
			TravelEntryDto travelEntry = creator.createTravelEntry(
				personDto.toReference(),
				nationalUser.toReference(),
				Disease.CORONAVIRUS,
				rdcf1.region,
				rdcf1.district,
				rdcf1.pointOfEntry);
			travelEntry.setDeaContent(Collections.singletonList(new DeaContentEntry("Something", "feels strange")));
			getTravelEntryFacade().save(travelEntry);

			List<DeaContentEntry> result = getTravelEntryFacade().getDeaContentOfLastTravelEntry();
			assertThat(result, hasSize(1));
			assertThat(result.get(0).getCaption(), equalTo("Something"));
			assertThat(result.get(0).getValue(), equalTo("feels strange"));
		}

		// Make sure creationDate is distinct
		Thread.sleep(1);

		// 2. Second travel entry
		{
			PersonDto personDto = creator.createPerson();
			TravelEntryDto travelEntry = creator.createTravelEntry(
				personDto.toReference(),
				nationalUser.toReference(),
				Disease.CORONAVIRUS,
				rdcf1.region,
				rdcf1.district,
				rdcf1.pointOfEntry);
			travelEntry.setDeaContent(Collections.singletonList(new DeaContentEntry("Hello", "World")));
			getTravelEntryFacade().save(travelEntry);

			List<DeaContentEntry> result = getTravelEntryFacade().getDeaContentOfLastTravelEntry();
			assertThat(result, hasSize(1));
			assertThat(result.get(0).getCaption(), equalTo("Hello"));
			assertThat(result.get(0).getValue(), equalTo("World"));
		}
	}

	@Test
	public void testGetCasesByPersonNationalHealthId() {
		PersonReferenceDto person1 = creator.createPerson().toReference();
		PersonDto personDto1 = getPersonFacade().getByUuid(person1.getUuid());
		personDto1.setNationalHealthId("firstNationalId");
		getPersonFacade().save(personDto1);
		final TravelEntryDto travelEntry1 = getTravelEntryFacade().save(creator.createTravelEntry(person1, districtUser1.toReference(), rdcf1, null));

		PersonReferenceDto person2 = creator.createPerson().toReference();
		PersonDto personDto2 = getPersonFacade().getByUuid(person2.getUuid());
		personDto2.setNationalHealthId("secondNationalId");
		getPersonFacade().save(personDto2);
		getTravelEntryFacade().save(creator.createTravelEntry(person2, districtUser1.toReference(), rdcf1, null));

		PersonReferenceDto person3 = creator.createPerson().toReference();
		PersonDto personDto3 = getPersonFacade().getByUuid(person3.getUuid());
		personDto3.setNationalHealthId("third");
		getPersonFacade().save(personDto3);
		getTravelEntryFacade().save(creator.createTravelEntry(person3, districtUser1.toReference(), rdcf1, null));

		TravelEntryCriteria travelEntryCriteria = new TravelEntryCriteria();
		travelEntryCriteria.setNameUuidExternalIDLike("firstNationalId");

		List<TravelEntryIndexDto> travelEntryIndexDtos1 = getTravelEntryFacade().getIndexList(travelEntryCriteria, 0, 100, null);
		assertEquals(1, travelEntryIndexDtos1.size());
		assertEquals(travelEntry1.getUuid(), travelEntryIndexDtos1.get(0).getUuid());

		travelEntryCriteria.setNameUuidExternalIDLike("National");
		List<TravelEntryIndexDto> travelEntryIndexDtosNational = getTravelEntryFacade().getIndexList(travelEntryCriteria, 0, 100, null);
		assertEquals(2, travelEntryIndexDtosNational.size());

		travelEntryCriteria.setNameUuidExternalIDLike(null);
		List<TravelEntryIndexDto> travelEntryIndexDtosAll = getTravelEntryFacade().getIndexList(travelEntryCriteria, 0, 100, null);
		assertEquals(3, travelEntryIndexDtosAll.size());
	}
}
