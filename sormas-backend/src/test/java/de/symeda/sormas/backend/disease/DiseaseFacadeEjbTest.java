/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.disease;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class DiseaseFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testDiseaseBurdenForDashboard() {

		Date referenceDate = new Date();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		CommunityDto community2 = creator.createCommunity("Community2", rdcf.district);
		RDCF rdcf2 = new RDCF(
			rdcf.region,
			rdcf.district,
			community2.toReference(),
			creator.createFacility("Facility2", rdcf.region, rdcf.district, community2.toReference()).toReference());

		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			DateHelper.subtractDays(referenceDate, 2),
			rdcf);

		PersonDto cazePerson2 = creator.createPerson("Case", "Person2");
		CaseDataDto caze2 = creator.createCase(
			user.toReference(),
			cazePerson2.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			DateHelper.addDays(referenceDate, 1),
			rdcf2);

		PersonDto cazePerson3 = creator.createPerson("Case", "Person3");
		CaseDataDto caze3 = creator.createCase(
			user.toReference(),
			cazePerson3.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			DateHelper.addDays(referenceDate, 2),
			rdcf);

		PersonDto cazePerson4 = creator.createPerson("Case", "Person4");
		CaseDataDto caze4 = creator.createCase(
			user.toReference(),
			cazePerson4.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			referenceDate,
			rdcf2);

		List<DiseaseBurdenDto> diseaseBurdenForDashboard = getDiseaseFacade().getDiseaseBurdenForDashboard(
			rdcf.region,
			rdcf.district,
			DateHelper.getStartOfDay(referenceDate),
			DateHelper.getEndOfDay(DateHelper.addDays(referenceDate, 10)),
			DateHelper.getStartOfDay(DateHelper.subtractDays(referenceDate, 10)),
			DateHelper.getEndOfDay(DateHelper.subtractDays(referenceDate, 1)));

		DiseaseBurdenDto evdBurden = diseaseBurdenForDashboard.stream().filter(dto -> dto.getDisease() == Disease.EVD).findFirst().get();
		assertEquals(new Long(3), evdBurden.getCaseCount());
		assertEquals(new Long(1), evdBurden.getPreviousCaseCount());
		assertEquals(rdcf.district.getCaption(), evdBurden.getLastReportedDistrictName());
	}
}
