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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.AdditionalTestingStatus;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;
import de.symeda.sormas.backend.facility.Facility;

public class SampleFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetIndexList() {
		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto referredSample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		sample.setReferredTo(referredSample.toReference());
		creator.createAdditionalTest(sample.toReference());
		creator.createAdditionalTest(sample.toReference());
		
		List<SampleIndexDto> sampleIndexDtos = getSampleFacade().getIndexList(null, 0, 100, null);
		
		// List should have one entry
		assertEquals(2, sampleIndexDtos.size());
		
		// First sample should have an additional test
		assertEquals(AdditionalTestingStatus.PERFORMED, sampleIndexDtos.get(1).getAdditionalTestingStatus());
	}

	@Test
	public void testSampleDeletion() {
		Date since = new Date();
		
		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		PathogenTestDto sampleTest = creator.createPathogenTest(sample.toReference(), PathogenTestType.MICROSCOPY, caze.getDisease(), new Date(), rdcf.facility, user.toReference(), PathogenTestResultType.POSITIVE, "Positive", true);

		// Database should contain the created sample and sample test
		assertNotNull(getSampleFacade().getSampleByUuid(sample.getUuid()));
		assertNotNull(getSampleTestFacade().getByUuid(sampleTest.getUuid()));

		getSampleFacade().deleteSample(sample.toReference());

		// Sample and pathogen test should be marked as deleted
		assertTrue(getSampleFacade().getDeletedUuidsSince(since).contains(sample.getUuid()));
		assertTrue(getSampleTestFacade().getDeletedUuidsSince(since).contains(sampleTest.getUuid()));
	}
	
	@Test
	public void testArchivedSampleNotGettingTransfered() {
		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		creator.createPathogenTest(sample.toReference(), PathogenTestType.MICROSCOPY, caze.getDisease(), new Date(), rdcf.facility, user.toReference(), PathogenTestResultType.POSITIVE, "Positive", true);

		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 1
		assertEquals(1, getSampleFacade().getAllActiveSamplesAfter(null).size());
		assertEquals(1, getSampleFacade().getAllActiveUuids().size());
		assertEquals(1, getSampleTestFacade().getAllActivePathogenTestsAfter(null).size());
		assertEquals(1, getSampleTestFacade().getAllActiveUuids().size());
		
		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), true);
		
		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 0
		assertEquals(0, getSampleFacade().getAllActiveSamplesAfter(null).size());
		assertEquals(0, getSampleFacade().getAllActiveUuids().size());
		assertEquals(0, getSampleTestFacade().getAllActivePathogenTestsAfter(null).size());
		assertEquals(0, getSampleTestFacade().getAllActiveUuids().size());

		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), false);

		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 1
		assertEquals(1, getSampleFacade().getAllActiveSamplesAfter(null).size());
		assertEquals(1, getSampleFacade().getAllActiveUuids().size());
		assertEquals(1, getSampleTestFacade().getAllActivePathogenTestsAfter(null).size());
		assertEquals(1, getSampleTestFacade().getAllActiveUuids().size());
	}

	@Test
	public void testGetNewTestResultCountByResultType() {
		RDCFEntities rdcf = creator.createRDCFEntities();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person1 = creator.createPerson("Heinz", "First").toReference();
		PersonReferenceDto person2 = creator.createPerson("Heinz", "Second").toReference();
		CaseDataDto case1 = creator.createCase(user, person1, rdcf);
		CaseDataDto case2 = creator.createCase(user, person2, rdcf);
		
		List<Long> caseIds = getCaseService().getAllIds(null);

		// no existing samples
		SampleFacade sampleFacade = getSampleFacade();
		Map<PathogenTestResultType, Long> resultMap = sampleFacade.getNewTestResultCountByResultType(caseIds);
		assertEquals(new Long(0), resultMap.values().stream().collect(Collectors.summingLong(Long::longValue)));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.INDETERMINATE, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.NEGATIVE, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.PENDING, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.POSITIVE, null));

		// one pending sample with in one case
		Facility lab = creator.createFacility("facility", rdcf.region, rdcf.district, rdcf.community);
		creator.createSample(case1.toReference(), user, lab);

		resultMap = sampleFacade.getNewTestResultCountByResultType(caseIds);
		assertEquals(new Long(1), resultMap.values().stream().collect(Collectors.summingLong(Long::longValue)));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.INDETERMINATE, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.NEGATIVE, null));
		assertEquals(new Long(1), resultMap.getOrDefault(PathogenTestResultType.PENDING, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.POSITIVE, null));

		// one pending sample in each of two cases
		creator.createSample(case2.toReference(), user, lab);

		resultMap = sampleFacade.getNewTestResultCountByResultType(caseIds);
		assertEquals(new Long(2), resultMap.values().stream().collect(Collectors.summingLong(Long::longValue)));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.INDETERMINATE, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.NEGATIVE, null));
		assertEquals(new Long(2), resultMap.getOrDefault(PathogenTestResultType.PENDING, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.POSITIVE, null));

		// one pending sample in each of two cases
		// and one positive sample in one of the two cases
		SampleDto sample = creator.createSample(case1.toReference(), user, lab);
		sample.setPathogenTestResult(PathogenTestResultType.POSITIVE);
		sampleFacade.saveSample(sample);

		resultMap = sampleFacade.getNewTestResultCountByResultType(caseIds);
		assertEquals(new Long(2), resultMap.values().stream().collect(Collectors.summingLong(Long::longValue)));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.INDETERMINATE, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.NEGATIVE, null));
		assertEquals(new Long(1), resultMap.getOrDefault(PathogenTestResultType.PENDING, null));
		assertEquals(new Long(1), resultMap.getOrDefault(PathogenTestResultType.POSITIVE, null));
	}
	

	@Test
	public void testGetByCaseUuids() throws Exception {
		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		CaseDataDto caze2 = creator.createCase(user.toReference(), person.toReference(), rdcf);
		CaseDataDto caze3 = creator.createCase(user.toReference(), person.toReference(), rdcf);
		
		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto sample2 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto sample3 = creator.createSample(caze2.toReference(), user.toReference(), rdcf.facility);
		creator.createSample(caze3.toReference(), user.toReference(), rdcf.facility);
		
		List<SampleDto> samples = getSampleFacade().getByCaseUuids(Arrays.asList(caze.getUuid(), caze2.getUuid()));
		
		assertThat(samples, hasSize(3));
		assertThat(samples, contains(sample, sample2, sample3));
	}

}
