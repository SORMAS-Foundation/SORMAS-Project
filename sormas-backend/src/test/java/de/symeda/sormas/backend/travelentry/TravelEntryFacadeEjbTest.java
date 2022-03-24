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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.user.DefaultUserRole;

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
			creator.getUserRoleDtoMap().get(DefaultUserRole.NATIONAL_USER));

		districtUser1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER));

		districtUser2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER));
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
		List<TravelEntryDto> allAfter = getTravelEntryFacade().getAllAfter(new DateTime(new Date()).minusDays(1).toDate());
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
		List<TravelEntryDto> allAfter = getTravelEntryFacade().getAllAfter(new DateTime(new Date()).minusDays(1).toDate());
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

}
