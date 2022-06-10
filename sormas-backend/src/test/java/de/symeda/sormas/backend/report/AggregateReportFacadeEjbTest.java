/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.report;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.report.AggregatedCaseCountDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class AggregateReportFacadeEjbTest extends AbstractBeanTest {

	TestDataCreator.RDCF rdcf;
	private UserDto officer;
	private UserDto informant1;
	private UserDto informant2;

	@Before
	public void setupData() {

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		officer = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			null,
			"Off",
			"One",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		informant1 = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Info",
			"One",
			creator.getUserRoleReference(DefaultUserRole.HOSPITAL_INFORMANT));
		informant1.setAssociatedOfficer(officer.toReference());
		getUserFacade().saveUser(informant1);

		informant2 = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Info",
			"Two",
			creator.getUserRoleReference(DefaultUserRole.HOSPITAL_INFORMANT));
		informant2.setAssociatedOfficer(officer.toReference());
		getUserFacade().saveUser(informant2);

	}

	@Test
	public void testAggregateReportWithHospitalInformant() {
		loginWith(informant1);

		EpiWeek epiWeek = DateHelper.getEpiWeek(new Date());

		AggregateReportDto aggregateReportDto = AggregateReportDto.build();
		aggregateReportDto.setDisease(Disease.HIV);
		aggregateReportDto.setReportingUser(informant1.toReference());
		aggregateReportDto.setNewCases(1);
		aggregateReportDto.setDeaths(3);
		aggregateReportDto.setLabConfirmations(2);
		aggregateReportDto.setEpiWeek(epiWeek.getWeek());
		aggregateReportDto.setRegion(rdcf.region);
		aggregateReportDto.setDistrict(rdcf.district);
		aggregateReportDto.setHealthFacility(rdcf.facility);
		getAggregateReportFacade().saveAggregateReport(aggregateReportDto);

		List<AggregatedCaseCountDto> indexList = getAggregateReportFacade().getIndexList(new AggregateReportCriteria().healthFacility(rdcf.facility));
		Assert.assertEquals(24, indexList.size());
		Assert.assertEquals(1, indexList.stream().filter(aggregatedCaseCountDto -> aggregatedCaseCountDto.getDeaths() == 3).count());
	}

	@Test
	public void testAggregateReportsSorting() {
		loginWith(informant1);

		EpiWeek epiWeek = DateHelper.getEpiWeek(new Date());

		createAggregateReport(epiWeek, "61Y");
		createAggregateReport(epiWeek, "41Y_60Y");
		createAggregateReport(epiWeek, "21Y_30Y");
		createAggregateReport(epiWeek, "5Y_15Y");
		createAggregateReport(epiWeek, "31Y_40Y");
		createAggregateReport(epiWeek, "60M_4Y");
		createAggregateReport(epiWeek, "0D_30D");
		createAggregateReport(epiWeek, "1M_59M");

		List<AggregatedCaseCountDto> indexList = getAggregateReportFacade().getIndexList(new AggregateReportCriteria().healthFacility(rdcf.facility));
		Assert.assertEquals(31, indexList.size());

		Assert.assertEquals("0D_30D", indexList.get(5).getAgeGroup());
		Assert.assertEquals("1M_59M", indexList.get(6).getAgeGroup());
		Assert.assertEquals("60M_4Y", indexList.get(7).getAgeGroup());
		Assert.assertEquals("5Y_15Y", indexList.get(8).getAgeGroup());
		Assert.assertEquals("21Y_30Y", indexList.get(9).getAgeGroup());
		Assert.assertEquals("31Y_40Y", indexList.get(10).getAgeGroup());
		Assert.assertEquals("41Y_60Y", indexList.get(11).getAgeGroup());
		Assert.assertEquals("61Y", indexList.get(12).getAgeGroup());
	}

	private void createAggregateReport(EpiWeek epiWeek, String ageGroup) {
		AggregateReportDto aggregateReportDto = AggregateReportDto.build();
		aggregateReportDto.setDisease(Disease.HIV);
		aggregateReportDto.setReportingUser(informant1.toReference());
		aggregateReportDto.setNewCases(1);
		aggregateReportDto.setDeaths(3);
		aggregateReportDto.setLabConfirmations(2);
		aggregateReportDto.setEpiWeek(epiWeek.getWeek());
		aggregateReportDto.setRegion(rdcf.region);
		aggregateReportDto.setDistrict(rdcf.district);
		aggregateReportDto.setHealthFacility(rdcf.facility);
		aggregateReportDto.setAgeGroup(ageGroup);
		getAggregateReportFacade().saveAggregateReport(aggregateReportDto);
	}
}
