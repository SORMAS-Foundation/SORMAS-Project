/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.externalmessage.labmessage.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.backend.AbstractBeanTest;

public class ExternalMessageMapperTest extends AbstractBeanTest {

	@Test
	public void testHomogenousTestResultTypesInWithNoTestReport() {
		ExternalMessageDto oneSampleReportMessage = ExternalMessageDto.build();
		ExternalMessageMapper oneSampleReportMessageMapper = new ExternalMessageMapper(oneSampleReportMessage, getExternalMessageProcessingFacade());

		SampleDto sample1 = new SampleDto();
		oneSampleReportMessageMapper.mapToSample(sample1, oneSampleReportMessage.getSampleReportsNullSafe().get(0));

		assertNull(sample1.getPathogenTestResult());

		ExternalMessageDto twoSampleReportsMessage = ExternalMessageDto.build();
		twoSampleReportsMessage.addSampleReport(SampleReportDto.build());
		twoSampleReportsMessage.addSampleReport(SampleReportDto.build());
		ExternalMessageMapper twoSampleReportsMessageMapper =
			new ExternalMessageMapper(twoSampleReportsMessage, getExternalMessageProcessingFacade());

		SampleDto sample2 = new SampleDto();
		twoSampleReportsMessageMapper.mapToSample(sample2, twoSampleReportsMessage.getSampleReportsNullSafe().get(0));

		SampleDto sample3 = new SampleDto();
		twoSampleReportsMessageMapper.mapToSample(sample3, twoSampleReportsMessage.getSampleReportsNullSafe().get(1));

		assertNull(sample2.getPathogenTestResult());
		assertNull(sample3.getPathogenTestResult());

	}

	@Test
	public void testHomogenousTestResultTypesInWithHomogenousTestReports() {
		ExternalMessageDto labMessage = ExternalMessageDto.build();
		ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.getSampleReportsNullSafe().get(0).addTestReport(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.getSampleReportsNullSafe().get(0).addTestReport(testReport2);

		SampleDto sample = new SampleDto();
		mapper.mapToSample(sample, labMessage.getSampleReportsNullSafe().get(0));

		assertEquals(PathogenTestResultType.POSITIVE, sample.getPathogenTestResult());
	}

	@Test
	public void testHomogenousTestResultTypesInWithInhomogeneousTestReports() {
		ExternalMessageDto labMessage = ExternalMessageDto.build();

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.getSampleReportsNullSafe().get(0).addTestReport(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.getSampleReportsNullSafe().get(0).addTestReport(testReport2);

		TestReportDto testReport3 = TestReportDto.build();
		testReport3.setTestResult(PathogenTestResultType.NEGATIVE);
		labMessage.getSampleReportsNullSafe().get(0).addTestReport(testReport3);

		ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

		SampleDto sample = new SampleDto();
		mapper.mapToSample(sample, labMessage.getSampleReportsNullSafe().get(0));

		assertNull(sample.getPathogenTestResult());
	}

	@Test
	public void testMigrateDiseaseVariant() {
		ExternalMessageDto labMessage = ExternalMessageDto.build();
		labMessage.setDisease(Disease.CORONAVIRUS);
		TestReportDto testReport = TestReportDto.build();

		ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

		DiseaseVariant diseaseVariant = creator.createDiseaseVariant("BF.1.2", Disease.CORONAVIRUS);

		// No disease variant set
		ImmutableTriple<String, DiseaseVariant, String> result = mapper.migrateDiseaseVariant(testReport);
		assertNull(result.getLeft());
		assertNull(result.getMiddle());
		assertNull(result.getRight());

		// Unknown disease variant set
		testReport.setTestedDiseaseVariant("NON-EXISTENT");
		result = mapper.migrateDiseaseVariant(testReport);
		assertEquals(
			I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE_VARIANT) + ": NON-EXISTENT\n",
			result.getLeft());
		assertNull(result.getMiddle());
		assertNull(result.getRight());

		// Unknown disease variant and disease variant details set
		testReport.setTestedDiseaseVariantDetails("SOME-DETAILS");
		result = mapper.migrateDiseaseVariant(testReport);
		assertEquals(
			I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE_VARIANT) + ": NON-EXISTENT\n"
				+ I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS) + ": SOME-DETAILS\n",
			result.getLeft());
		assertNull(result.getMiddle());
		assertNull(result.getRight());

		// Only disease variant details set
		testReport.setTestedDiseaseVariant(null);
		result = mapper.migrateDiseaseVariant(testReport);
		assertEquals(
			I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS) + ": SOME-DETAILS\n",
			result.getLeft());
		assertNull(result.getMiddle());
		assertNull(result.getRight());

		// Known disease variant and disease variant details set
		testReport.setTestedDiseaseVariant("BF.1.2");
		result = mapper.migrateDiseaseVariant(testReport);
		assertNull(result.getLeft());
		assertEquals(diseaseVariant, result.getMiddle());
		assertEquals("SOME-DETAILS", result.getRight());

		// Known disease variant set
		testReport.setTestedDiseaseVariantDetails(null);
		result = mapper.migrateDiseaseVariant(testReport);
		assertNull(result.getLeft());
		assertEquals(diseaseVariant, result.getMiddle());
		assertNull(result.getRight());
	}

	@Test
	public void testMapToPersonPresentCondition() {
		ExternalMessageDto labMessage = ExternalMessageDto.build();
		PersonDto person = PersonDto.build();
		ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

		// both values null
		List<String[]> result = mapper.mapToPerson(person);
		assertTrue(result.isEmpty());
		assertNull(person.getPresentCondition());

		// person value already set
		person.setPresentCondition(PresentCondition.DEAD);
		result = mapper.mapToPerson(person);
		assertTrue(result.isEmpty());
		assertEquals(PresentCondition.DEAD, person.getPresentCondition());

		// equal values in person and lab message
		labMessage.setPersonPresentCondition(PresentCondition.DEAD);
		result = mapper.mapToPerson(person);
		assertTrue(result.isEmpty());
		assertEquals(PresentCondition.DEAD, person.getPresentCondition());

		// different values in person and lab message
		labMessage.setPersonPresentCondition(PresentCondition.ALIVE);
		result = mapper.mapToPerson(person);
		ArrayList<String[]> expectedResult = new ArrayList();
		expectedResult.add(
			new String[] {
				"presentCondition" });
		assertEquals(expectedResult.size(), result.size());
		assertEquals(expectedResult.get(0).length, result.get(0).length);
		assertEquals(expectedResult.get(0)[0], result.get(0)[0]);
		assertEquals(PresentCondition.ALIVE, person.getPresentCondition());

	}

	@Test
	public void testGetLabReference() {
		var rdcf = creator.createRDCF();
		FacilityFacade facilityFacade = getFacilityFacade();
		final FacilityReferenceDto otherFacilityRef = facilityFacade.getReferenceByUuid(FacilityDto.OTHER_FACILITY_UUID);

		ExternalMessageDto labMessageDto = ExternalMessageDto.build();
		ExternalMessageMapper mapper = new ExternalMessageMapper(labMessageDto, getExternalMessageProcessingFacade());

		assertEquals(otherFacilityRef, mapper.getFacilityReference(Collections.emptyList(), facilityFacade));
		assertEquals(otherFacilityRef, mapper.getFacilityReference(Collections.singletonList("unknown"), facilityFacade));

		FacilityDto one = creator.createFacility("One", rdcf.region, rdcf.district, rdcf.community, FacilityType.LABORATORY);
		one.setExternalID("oneExternal");
		one.setChangeDate(new Date());
		facilityFacade.save(one);

		FacilityDto two = creator.createFacility("Two", rdcf.region, rdcf.district, rdcf.community, FacilityType.LABORATORY);
		two.setExternalID("twoExternal");
		two.setChangeDate(new Date());
		facilityFacade.save(two);

		FacilityReferenceDto oneExternal = mapper.getFacilityReference(Collections.singletonList("oneExternal"), facilityFacade);
		assertEquals(one.toReference(), oneExternal);

		FacilityReferenceDto twoExternal = mapper.getFacilityReference(Collections.singletonList("twoExternal"), facilityFacade);
		assertEquals(two.toReference(), twoExternal);

		assertNull(mapper.getFacilityReference(Arrays.asList("oneExternal", "twoExternal"), facilityFacade));
	}
}
