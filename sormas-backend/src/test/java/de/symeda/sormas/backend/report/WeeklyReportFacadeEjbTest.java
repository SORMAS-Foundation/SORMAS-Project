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
package de.symeda.sormas.backend.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.report.WeeklyReportOfficerSummaryDto;
import de.symeda.sormas.api.report.WeeklyReportRegionSummaryDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;

public class WeeklyReportFacadeEjbTest extends AbstractBeanTest {

	private UserDto officer;
	private UserDto informant1;
	private UserDto informant2;
	private UserDto informant3;
	private UserDto informant4;

	@Before
	public void setupData() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		officer = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), null, "Off", "One", UserRole.SURVEILLANCE_OFFICER);

		informant1 =
			creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Info", "One", UserRole.HOSPITAL_INFORMANT);
		informant1.setAssociatedOfficer(officer.toReference());
		getUserFacade().saveUser(informant1);

		informant2 =
			creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Info", "Two", UserRole.HOSPITAL_INFORMANT);
		informant2.setAssociatedOfficer(officer.toReference());
		getUserFacade().saveUser(informant2);

		RDCFEntities rdcf2 = new RDCFEntities(
			rdcf.region,
			rdcf.district,
			rdcf.community,
			creator.createFacility("Facility2", rdcf.region, rdcf.district, rdcf.community));
		informant3 = creator
			.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Info", "Three", UserRole.COMMUNITY_INFORMANT);
		informant3.setCommunity(new CommunityReferenceDto(rdcf.community.getUuid()));
		informant3.setAssociatedOfficer(officer.toReference());
		getUserFacade().saveUser(informant3);

		District district2 = creator.createDistrict("District2", rdcf.region);
		Community community2 = creator.createCommunity("Community2", district2);
		Facility facility3 = creator.createFacility("Facility3", rdcf.region, district2, community2);
		RDCFEntities rdcf3 = new RDCFEntities(rdcf.region, district2, community2, facility3);
		informant4 = creator
			.createUser(rdcf3.region.getUuid(), rdcf3.district.getUuid(), rdcf3.facility.getUuid(), "Info", "Four", UserRole.HOSPITAL_INFORMANT);
		informant4.setAssociatedOfficer(officer.toReference());
		getUserFacade().saveUser(informant4);

	}

	@Test
	public void testGetSummariesPerRegion() {

		EpiWeek previousEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		createFacilityInformantReport(informant1, new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 1);
		createCommunityInformantReport(informant3, new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 1);
		createFacilityInformantReport(informant4, new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 0);

		List<WeeklyReportRegionSummaryDto> summariesPerRegion = getWeeklyReportFacade().getSummariesPerRegion(previousEpiWeek);
		assertEquals(1, summariesPerRegion.size());
		WeeklyReportRegionSummaryDto summary = summariesPerRegion.get(0);
		assertEquals(1, summary.getOfficers());
		assertEquals(1, summary.getOfficerMissingReports());
		assertEquals(0, summary.getOfficerCaseReports());
		assertEquals(0, summary.getOfficerZeroReports());

		assertEquals(4, summary.getInformants());
		assertEquals(2, summary.getInformantCaseReports());
		assertEquals(1, summary.getInformantZeroReports());
		assertEquals(1, summary.getInformantMissingReports());

		createOfficerReport(officer, new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 1);

		summariesPerRegion = getWeeklyReportFacade().getSummariesPerRegion(previousEpiWeek);
		assertEquals(1, summariesPerRegion.size());
		summary = summariesPerRegion.get(0);
		assertEquals(1, summary.getOfficers());
		assertEquals(0, summary.getOfficerMissingReports());
		assertEquals(1, summary.getOfficerCaseReports());
		assertEquals(0, summary.getOfficerZeroReports());
	}

	@Test
	public void testGetSummariesPerOfficer() {

		EpiWeek previousEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		createFacilityInformantReport(informant1, new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 1);
		createCommunityInformantReport(informant3, new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 1);
		createFacilityInformantReport(informant4, new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 0);

		List<WeeklyReportOfficerSummaryDto> summariesPerRegion = getWeeklyReportFacade().getSummariesPerOfficer(officer.getRegion(), previousEpiWeek);
		assertEquals(1, summariesPerRegion.size());
		WeeklyReportOfficerSummaryDto summary = summariesPerRegion.get(0);
		assertEquals(4, summary.getInformants());
		assertEquals(2, summary.getInformantCaseReports());
		assertEquals(1, summary.getInformantZeroReports());
		assertEquals(1, summary.getInformantMissingReports());
	}

	@Test
	public void testGetByEpiWeekAndUser() {

		EpiWeek previousEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		createFacilityInformantReport(informant1, new Date(), previousEpiWeek.getWeek(), previousEpiWeek.getYear(), 1);

		WeeklyReportDto report = getWeeklyReportFacade().getByEpiWeekAndUser(previousEpiWeek, informant1.toReference());
		assertNotNull(report);
		report = getWeeklyReportFacade().getByEpiWeekAndUser(previousEpiWeek, informant2.toReference());
		assertNull(report);
	}

	private WeeklyReportDto createFacilityInformantReport(UserDto reportingUser, Date reportDateTime, int epiWeek, int year, int numberOfCases) {

		WeeklyReportDto report = WeeklyReportDto.build(reportingUser.toReference());
		report.setAssignedOfficer(reportingUser.getAssociatedOfficer());
		report.setDistrict(reportingUser.getDistrict());
		report.setHealthFacility(reportingUser.getHealthFacility());
		report.setReportDateTime(reportDateTime);
		report.setEpiWeek(epiWeek);
		report.setYear(year);
		report.setTotalNumberOfCases(numberOfCases);

		report = getWeeklyReportFacade().saveWeeklyReport(report);
		return report;
	}

	private WeeklyReportDto createCommunityInformantReport(UserDto reportingUser, Date reportDateTime, int epiWeek, int year, int numberOfCases) {

		WeeklyReportDto report = WeeklyReportDto.build(reportingUser.toReference());
		report.setAssignedOfficer(reportingUser.getAssociatedOfficer());
		report.setDistrict(reportingUser.getDistrict());
		report.setCommunity(reportingUser.getCommunity());
		report.setReportDateTime(reportDateTime);
		report.setEpiWeek(epiWeek);
		report.setYear(year);
		report.setTotalNumberOfCases(numberOfCases);

		report = getWeeklyReportFacade().saveWeeklyReport(report);
		return report;
	}

	private WeeklyReportDto createOfficerReport(UserDto reportingUser, Date reportDateTime, int epiWeek, int year, int numberOfCases) {

		WeeklyReportDto report = WeeklyReportDto.build(reportingUser.toReference());
		report.setDistrict(reportingUser.getDistrict());
		report.setReportDateTime(reportDateTime);
		report.setEpiWeek(epiWeek);
		report.setYear(year);
		report.setTotalNumberOfCases(numberOfCases);

		report = getWeeklyReportFacade().saveWeeklyReport(report);
		return report;
	}
}
