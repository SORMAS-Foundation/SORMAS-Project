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

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class ImmunizationFacadeEjbTest extends AbstractBeanTest {

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
		List<ImmunizationDto> allAfter = getImmunizationFacade().getAllAfter(new DateTime(new Date()).minusDays(1).toDate());
		assertEquals(2, allAfter.size());
		List<PersonDto> allPersonsAfter = getPersonFacade().getPersonsAfter(new DateTime(new Date()).minusDays(1).toDate());
		assertEquals(1, allPersonsAfter.size());
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
		List<ImmunizationDto> allAfter = getImmunizationFacade().getAllAfter(new DateTime(new Date()).minusDays(1).toDate());
		assertEquals(1, allAfter.size());
		ImmunizationDto immunizationDto = allAfter.get(0);
		assertEquals(seenImmunization.getUuid(), immunizationDto.getUuid());
		assertEquals(seenImmunization.getPerson().getFirstName(), immunizationDto.getPerson().getFirstName());
		assertEquals(seenImmunization.getPerson().getLastName(), immunizationDto.getPerson().getLastName());

		// assert getting non seen immunization in grid is pseudonymized
		ImmunizationDto byUuid = getImmunizationFacade().getByUuid(nonSeenImmunization.getUuid());
		assertEquals(nonSeenImmunization.getUuid(), byUuid.getUuid());
		assertEquals("Confidential", byUuid.getPerson().getLastName());
		assertEquals("Confidential", byUuid.getPerson().getFirstName());

		List<PersonDto> allPersonsAfter = getPersonFacade().getPersonsAfter(new DateTime(new Date()).minusDays(1).toDate());
		assertEquals(1, allPersonsAfter.size());
		PersonDto personDto = allPersonsAfter.get(0);
		assertEquals(person2.getUuid(), personDto.getUuid());

		loginWith(nationalUser);
		assertEquals(2, getPersonFacade().getPersonsAfter(new DateTime(new Date()).minusDays(1).toDate()).size());
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
			new DateTime(new Date()).minusDays(10).toDate(),
			new DateTime(new Date()).plusDays(1).toDate());
		ImmunizationDto acquiredImmunizationNoLongerValid = creator.createImmunization(
			Disease.DENGUE,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf2,
			new DateTime(new Date()).minusDays(10).toDate(),
			new DateTime(new Date()).minusDays(2).toDate());

		// immunizations before status automation update
		assertEquals(5, getImmunizationFacade().getAllAfter(new DateTime(new Date()).minusDays(2).toDate()).size());
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
		assertEquals(5, getImmunizationFacade().getAllAfter(new DateTime(new Date()).minusDays(2).toDate()).size());
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
}
