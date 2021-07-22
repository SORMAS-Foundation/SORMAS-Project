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

package de.symeda.sormas.backend.person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class PersonFacadeEjbUserFilterTest extends AbstractBeanTest {

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
			UserRole.NATIONAL_USER);

		districtUser1 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

		districtUser2 = creator
			.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);
	}

	@Test
	public void testGetPersonIndexListWhenNoAssociationToEntities() {
		loginWith(nationalUser);

		PersonDto person1 = creator.createPerson("John", "Doe");
		PersonDto person2 = creator.createPerson("John2", "Doe2");

		assertTrue(getPersonFacade().getIndexList(null, null, null, null).isEmpty());
	}

	@Test
	public void testGetPersonIndexListWhenSeveralAssociations() {
		loginWith(nationalUser);

		PersonDto person1 = creator.createPerson("John", "Doe");
		PersonDto person2 = creator.createPerson("John2", "Doe2");

		CaseDataDto case1 = creator.createCase(
			nationalUser.toReference(),
			person1.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf1,
			null);

		ContactDto contactForPerson1AndCase1 = creator.createContact(nationalUser.toReference(), person1.toReference(), case1);

		CaseDataDto case2 = creator.createCase(
			nationalUser.toReference(),
			person2.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf2,
			null);

		ContactDto contactForPerson1AndCase2 = creator.createContact(nationalUser.toReference(), person1.toReference(), case2);
		TravelEntryDto travelEntryForPerson1 = creator.createTravelEntry(person1.toReference(), nationalUser.toReference(),  Disease.CORONAVIRUS, rdcf1.region, rdcf1.district, rdcf1.pointOfEntry);

		PersonCriteria criteria = new PersonCriteria();
		criteria.setPersonAssociation(PersonAssociation.TRAVEL_ENTRY);
		List<PersonIndexDto> travelEntryPersonsFornationalUser = getPersonFacade().getIndexList(criteria, null, null, null);
		assertEquals(1, travelEntryPersonsFornationalUser.size());
		assertEquals(person1.getUuid(), travelEntryPersonsFornationalUser.get(0).getUuid());

		loginWith(districtUser1);
		List<PersonIndexDto> indexListForDistrictUser1 = getPersonFacade().getIndexList(null, null, null, null);
		assertEquals(1, indexListForDistrictUser1.size());
		assertEquals(person1.getUuid(), indexListForDistrictUser1.get(0).getUuid());

		loginWith(districtUser2);
		List<PersonIndexDto> indexListForDistrictUser2 = getPersonFacade().getIndexList(null, null, null, null);
		assertEquals(2, indexListForDistrictUser2.size());
	}
}
