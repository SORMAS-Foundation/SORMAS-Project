/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.caze.surveillancereport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class SurveillanceReportFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

	@Override
	public void init() {

		super.init();

		UserRoleReferenceDto newUserRole = creator.createUserRole(
			"NoEventNoCaseView",
			JurisdictionLevel.DISTRICT,
			UserRight.CASE_CLINICIAN_VIEW,
			UserRight.CASE_VIEW,
			UserRight.PERSON_VIEW);

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER),
			newUserRole);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER),
			newUserRole);

		loginWith(user2);
	}

	@Test
	public void testGetReportOfCaseOutsideJurisdiction() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson().toReference(), rdcf1);
		SurveillanceReportDto report = createSurveillanceReport(caze, user1, rdcf1);

		assertPseudonymized(getSurveillanceReportFacade().getByUuid(report.getUuid()));
		assertPseudonymized(getSurveillanceReportFacade().getByUuids(Collections.singletonList(report.getUuid())).get(0));
	}

	@Test
	public void testGetReportOfCaseWithSpecialAccess() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson().toReference(), rdcf1);
		SurveillanceReportDto report = createSurveillanceReport(caze, user1, rdcf1);
		creator.createSpecialCaseAccess(caze.toReference(), user1.toReference(), user2.toReference(), DateHelper.addDays(new Date(), 1));

		assertNotPseudonymized(getSurveillanceReportFacade().getByUuid(report.getUuid()));
		assertNotPseudonymized(getSurveillanceReportFacade().getByUuids(Collections.singletonList(report.getUuid())).get(0));
	}

	private void assertPseudonymized(SurveillanceReportDto report) {
		assertThat(report.isPseudonymized(), is(true));
		assertThat(report.getFacilityDetails(), is(""));
		assertThat(report.getNotificationDetails(), is(""));
	}

	private void assertNotPseudonymized(SurveillanceReportDto report) {
		assertThat(report.isPseudonymized(), is(false));
		assertThat(report.getFacilityDetails(), is("Test facility details"));
		assertThat(report.getNotificationDetails(), is("Test notification details"));
	}

	private SurveillanceReportDto createSurveillanceReport(CaseDataDto caze, UserDto user, TestDataCreator.RDCF rdcf) {
		SurveillanceReportDto newReport = SurveillanceReportDto.build(caze.toReference(), user.toReference());
		newReport.setReportDate(new Date());
		newReport.setDateOfDiagnosis(new Date());
		newReport.setReportingType(ReportingType.LABORATORY);
		newReport.setFacilityRegion(rdcf.region);
		newReport.setFacilityDistrict(rdcf.district);
		newReport.setFacility(rdcf.facility);
		newReport.setFacilityDetails("Test facility details");
		newReport.setNotificationDetails("Test notification details");

		return getSurveillanceReportFacade().save(newReport);
	}
}
