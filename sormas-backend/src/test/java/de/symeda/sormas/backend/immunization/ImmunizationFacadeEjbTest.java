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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
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
	}

	@Test
	public void testCanSeeByResponsibleJurisdiction() {
		loginWith(nationalUser);

		PersonDto person = creator.createPerson("John", "Doe");

		ImmunizationDto seenImmunization = creator.createImmunization(
				Disease.DENGUE,
				person.toReference(),
				nationalUser.toReference(),
				ImmunizationStatus.ACQUIRED,
				MeansOfImmunization.VACCINATION,
				ImmunizationManagementStatus.COMPLETED,
				rdcf2);
		ImmunizationDto nonSeenImmunization = creator.createImmunization(
				Disease.DENGUE,
				person.toReference(),
				nationalUser.toReference(),
				ImmunizationStatus.ACQUIRED,
				MeansOfImmunization.VACCINATION,
				ImmunizationManagementStatus.COMPLETED,
				rdcf1);

		loginWith(districtUser2);
		List<ImmunizationDto> allAfter = getImmunizationFacade().getAllAfter(new DateTime(new Date()).minusDays(1).toDate());
		assertEquals(1, allAfter.size());
		ImmunizationDto immunizationDto = allAfter.get(0);
		assertEquals(seenImmunization.getUuid(), immunizationDto.getUuid());
		assertEquals(seenImmunization.getPerson().getFirstName(), immunizationDto.getPerson().getFirstName());
		assertEquals(seenImmunization.getPerson().getLastName(), immunizationDto.getPerson().getLastName());
	}

}
