package de.symeda.sormas.ui.externalmessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomEnumNotFoundException;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumFacadeEjb;
import de.symeda.sormas.ui.AbstractUiBeanTest;

public class ExternalMessageMapperTest extends AbstractUiBeanTest {

	@Test
	public void testHomogenousTestResultTypesInWithNoTestReport() {
		ExternalMessageDto oneSampleReportMessage = ExternalMessageDto.build();
		ExternalMessageMapper oneSampleReportMessageMapper = ExternalMessageMapper.forLabMessage(oneSampleReportMessage);

		SampleDto sample1 = new SampleDto();
		oneSampleReportMessageMapper.mapToSample(sample1, oneSampleReportMessage.getSampleReportsNullSafe().get(0));

		assertNull(sample1.getPathogenTestResult());

		ExternalMessageDto twoSampleReportsMessage = ExternalMessageDto.build();
		twoSampleReportsMessage.addSampleReport(SampleReportDto.build());
		twoSampleReportsMessage.addSampleReport(SampleReportDto.build());
		ExternalMessageMapper twoSampleReportsMessageMapper = ExternalMessageMapper.forLabMessage(twoSampleReportsMessage);

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
		ExternalMessageMapper mapper = ExternalMessageMapper.forLabMessage(labMessage);

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

		ExternalMessageMapper mapper = ExternalMessageMapper.forLabMessage(labMessage);

		SampleDto sample = new SampleDto();
		mapper.mapToSample(sample, labMessage.getSampleReportsNullSafe().get(0));

		assertNull(sample.getPathogenTestResult());
	}

	@Test
	public void testMigrateDiseaseVariant() throws CustomEnumNotFoundException {
		CustomizableEnumFacadeEjb customizableEnumFacade = mock(CustomizableEnumFacadeEjb.class);
		ExternalMessageDto labMessage = ExternalMessageDto.build();
		labMessage.setDisease(Disease.CORONAVIRUS);
		TestReportDto testReport = TestReportDto.build();

		ExternalMessageMapper mapper = ExternalMessageMapper.forLabMessage(labMessage);

		DiseaseVariant diseaseVariant = new DiseaseVariant();
		when(customizableEnumFacade.getEnumValue(CustomizableEnumType.DISEASE_VARIANT, "P.2", Disease.CORONAVIRUS)).thenReturn(diseaseVariant);
		when(customizableEnumFacade.getEnumValue(CustomizableEnumType.DISEASE_VARIANT, "NON-EXISTENT", Disease.CORONAVIRUS))
			.thenThrow(new CustomEnumNotFoundException("not found"));
		when(customizableEnumFacade.getEnumValue(CustomizableEnumType.DISEASE_VARIANT, null, Disease.CORONAVIRUS))
			.thenThrow(new CustomEnumNotFoundException("not found"));

		try (MockedStatic<FacadeProvider> facadeProvider = Mockito.mockStatic(FacadeProvider.class)) {
			facadeProvider.when(FacadeProvider::getCustomizableEnumFacade).thenReturn(customizableEnumFacade);

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
					+ I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS)
					+ ": SOME-DETAILS\n",
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
			testReport.setTestedDiseaseVariant("P.2");
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
	}

	@Test
	public void testMapToPersonPresentCondition() {
		ExternalMessageDto labMessage = ExternalMessageDto.build();
		PersonDto person = PersonDto.build();
		ExternalMessageMapper mapper = ExternalMessageMapper.forLabMessage(labMessage);

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
		final FacilityReferenceDto otherFacilityRef = getFacilityFacade().getReferenceByUuid(FacilityDto.OTHER_FACILITY_UUID);

		ExternalMessageDto labMessageDto = ExternalMessageDto.build();
		ExternalMessageMapper mapper = ExternalMessageMapper.forLabMessage(labMessageDto);

		assertEquals(otherFacilityRef, mapper.getFacilityReference(Collections.emptyList()));
		assertEquals(otherFacilityRef, mapper.getFacilityReference(Collections.singletonList("unknown")));

		FacilityDto one = creator.createFacility("One", rdcf.region, rdcf.district, rdcf.community, FacilityType.LABORATORY);
		one.setExternalID("oneExternal");
		one.setChangeDate(new Date());
		getFacilityFacade().save(one);

		FacilityDto two = creator.createFacility("Two", rdcf.region, rdcf.district, rdcf.community, FacilityType.LABORATORY);
		two.setExternalID("twoExternal");
		two.setChangeDate(new Date());
		getFacilityFacade().save(two);

		FacilityReferenceDto oneExternal = mapper.getFacilityReference(Collections.singletonList("oneExternal"));
		assertEquals(one.toReference(), oneExternal);

		FacilityReferenceDto twoExternal = mapper.getFacilityReference(Collections.singletonList("twoExternal"));
		assertEquals(two.toReference(), twoExternal);

		assertNull(mapper.getFacilityReference(Arrays.asList("oneExternal", "twoExternal")));
	}
}
