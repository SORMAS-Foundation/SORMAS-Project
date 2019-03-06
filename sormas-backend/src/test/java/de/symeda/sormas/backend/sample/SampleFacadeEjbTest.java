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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.AdditionalTestingStatus;
import de.symeda.sormas.api.sample.DashboardSampleDto;
import de.symeda.sormas.api.sample.DashboardTestResultDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class SampleFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testDashboardSampleResultListCreation() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);

		List<DashboardSampleDto> dashboardSampleDtos = getSampleFacade().getNewSamplesForDashboard(caze.getRegion(), caze.getDistrict(), caze.getDisease(), DateHelper.subtractDays(new Date(),  1), DateHelper.addDays(new Date(), 1), user.getUuid());

		// List should have one entry
		assertEquals(1, dashboardSampleDtos.size());
	}

	@Test
	public void testDashboardTestResultListCreation() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		creator.createPathogenTest(sample.toReference(), PathogenTestType.MICROSCOPY, new Date(), rdcf.facility, user.toReference(), PathogenTestResultType.POSITIVE, "Positive", true);

		List<DashboardTestResultDto> dashboardTestResultDtos = getSampleTestFacade().getNewTestResultsForDashboard(caze.getRegion(), caze.getDistrict(), caze.getDisease(), DateHelper.subtractDays(new Date(),  1), DateHelper.addDays(new Date(), 1), user.getUuid());

		// List should have one entry
		assertEquals(1, dashboardTestResultDtos.size());
	}

	@Test
	public void testGetIndexList() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto referredSample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		sample.setReferredTo(referredSample.toReference());
		creator.createAdditionalTest(sample.toReference());
		creator.createAdditionalTest(sample.toReference());
		
		List<SampleIndexDto> sampleIndexDtos = getSampleFacade().getIndexList(user.getUuid(), null);
		
		// List should have one entry
		assertEquals(2, sampleIndexDtos.size());
		
		// First sample should have an additional test
		assertEquals(AdditionalTestingStatus.PERFORMED, sampleIndexDtos.get(1).getAdditionalTestingStatus());
	}

	@Test
	public void testMainSampleTestLogic() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);

		// List should have no entry
		SampleCriteria sampleCriteria = new SampleCriteria()
				.testResult(PathogenTestResultType.POSITIVE);
		List<SampleIndexDto> sampleIndexDtos = getSampleFacade().getIndexList(user.getUuid(), sampleCriteria);
		assertEquals(0, sampleIndexDtos.size());
		
		creator.createPathogenTest(sample.toReference(), PathogenTestType.PCR_RT_PCR, new Date(), rdcf.facility, user.toReference(), PathogenTestResultType.POSITIVE, "", false);
		// now we should have one entry
		sampleIndexDtos = getSampleFacade().getIndexList(user.getUuid(), sampleCriteria);
		assertEquals(1, sampleIndexDtos.size());

		creator.createPathogenTest(sample.toReference(), PathogenTestType.PCR_RT_PCR, new Date(), rdcf.facility, user.toReference(), PathogenTestResultType.NEGATIVE, "", false);
		// now 0, because the negative test is the new (latest) main test
		sampleIndexDtos = getSampleFacade().getIndexList(user.getUuid(), sampleCriteria);
		assertEquals(0, sampleIndexDtos.size());
		
		sampleCriteria.testResult(PathogenTestResultType.NEGATIVE);
		sampleIndexDtos = getSampleFacade().getIndexList(user.getUuid(), sampleCriteria);
		assertEquals(1, sampleIndexDtos.size());
		
		creator.createPathogenTest(sample.toReference(), PathogenTestType.PCR_RT_PCR, DateHelper.addDays(new Date(), -1), rdcf.facility, user.toReference(), PathogenTestResultType.POSITIVE, "", false);
		// should still be negative
		sampleIndexDtos = getSampleFacade().getIndexList(user.getUuid(), sampleCriteria);
		assertEquals(PathogenTestResultType.NEGATIVE, sampleIndexDtos.get(0).getPathogenTestResult());
	}

	@Test
	public void testSampleDeletion() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		PathogenTestDto sampleTest = creator.createPathogenTest(sample.toReference(), PathogenTestType.MICROSCOPY, new Date(), rdcf.facility, user.toReference(), PathogenTestResultType.POSITIVE, "Positive", true);

		// Database should contain the created sample and sample test
		assertNotNull(getSampleFacade().getSampleByUuid(sample.getUuid()));
		assertNotNull(getSampleTestFacade().getByUuid(sampleTest.getUuid()));

		getSampleFacade().deleteSample(sample.toReference(), adminUuid);

		// Database should not contain the deleted sample and sample test
		assertNull(getSampleFacade().getSampleByUuid(sample.getUuid()));
		assertNull(getSampleTestFacade().getByUuid(sampleTest.getUuid()));
	}
	
	@Test
	public void testArchivedSampleNotGettingTransfered() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		creator.createPathogenTest(sample.toReference(), PathogenTestType.MICROSCOPY, new Date(), rdcf.facility, user.toReference(), PathogenTestResultType.POSITIVE, "Positive", true);

		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 1
		assertEquals(1, getSampleFacade().getAllActiveSamplesAfter(null, user.getUuid()).size());
		assertEquals(1, getSampleFacade().getAllActiveUuids(user.getUuid()).size());
		assertEquals(1, getSampleTestFacade().getAllActivePathogenTestsAfter(null, user.getUuid()).size());
		assertEquals(1, getSampleTestFacade().getAllActiveUuids(user.getUuid()).size());
		
		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), true);
		
		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 0
		assertEquals(0, getSampleFacade().getAllActiveSamplesAfter(null, user.getUuid()).size());
		assertEquals(0, getSampleFacade().getAllActiveUuids(user.getUuid()).size());
		assertEquals(0, getSampleTestFacade().getAllActivePathogenTestsAfter(null, user.getUuid()).size());
		assertEquals(0, getSampleTestFacade().getAllActiveUuids(user.getUuid()).size());

		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), false);

		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 1
		assertEquals(1, getSampleFacade().getAllActiveSamplesAfter(null, user.getUuid()).size());
		assertEquals(1, getSampleFacade().getAllActiveUuids(user.getUuid()).size());
		assertEquals(1, getSampleTestFacade().getAllActivePathogenTestsAfter(null, user.getUuid()).size());
		assertEquals(1, getSampleTestFacade().getAllActiveUuids(user.getUuid()).size());
	}
}
