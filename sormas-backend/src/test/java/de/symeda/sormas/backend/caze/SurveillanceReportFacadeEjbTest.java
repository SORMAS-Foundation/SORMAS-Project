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

package de.symeda.sormas.backend.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class SurveillanceReportFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testSaveSurveillanceReport() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf);
		SurveillanceReportDto newReport = SurveillanceReportDto.build(caze.toReference(), user.toReference());
		newReport.setReportDate(new Date());
		newReport.setDateOfDiagnosis(new Date());
		newReport.setReportingType(ReportingType.LABORATORY);
		newReport.setFacilityRegion(rdcf.region);
		newReport.setFacilityDistrict(rdcf.district);
		newReport.setFacility(rdcf.facility);

		SurveillanceReportDto savedReport = getSurveillanceReportFacade().save(newReport);
		assertEquals(newReport.getCaze(), savedReport.getCaze());
		assertEquals(newReport.getReportingUser(), savedReport.getReportingUser());
		assertEquals(newReport.getReportDate(), savedReport.getReportDate());
		assertEquals(newReport.getDateOfDiagnosis(), savedReport.getDateOfDiagnosis());
		assertEquals(newReport.getReportingType(), savedReport.getReportingType());
		assertEquals(newReport.getFacilityRegion(), savedReport.getFacilityRegion());
		assertEquals(newReport.getFacilityDistrict(), savedReport.getFacilityDistrict());
		assertEquals(newReport.getFacility(), savedReport.getFacility());

		SurveillanceReportDto reloadedReport = getSurveillanceReportFacade().getByUuid(savedReport.getUuid());
		assertEquals(newReport.getCaze(), reloadedReport.getCaze());
		assertEquals(newReport.getReportingUser(), reloadedReport.getReportingUser());
		assertEquals(newReport.getReportDate(), reloadedReport.getReportDate());
		assertEquals(newReport.getDateOfDiagnosis(), reloadedReport.getDateOfDiagnosis());
		assertEquals(newReport.getReportingType(), reloadedReport.getReportingType());
		assertEquals(newReport.getFacilityRegion(), reloadedReport.getFacilityRegion());
		assertEquals(newReport.getFacilityDistrict(), reloadedReport.getFacilityDistrict());
		assertEquals(newReport.getFacility(), reloadedReport.getFacility());

	}
}
