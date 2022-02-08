package de.symeda.sormas.ui.labmessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomEnumNotFoundException;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumFacadeEjb;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.Test;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.AbstractBeanTest;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LabMessageMapperTest extends AbstractBeanTest {

	@Test
	public void testHomogenousTestResultTypesInWithNoTestReport() {
		LabMessageDto labMessageDto = LabMessageDto.build();
		LabMessageMapper mapper = LabMessageMapper.forLabMessage(labMessageDto);

		SampleDto sample = new SampleDto();
		mapper.mapToSample(sample);

		assertNull(sample.getPathogenTestResult());
	}

	@Test
	public void testHomogenousTestResultTypesInWithHomogenousTestReports() {
		LabMessageDto labMessage = LabMessageDto.build();
		LabMessageMapper mapper = LabMessageMapper.forLabMessage(labMessage);

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.addTestReport(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.addTestReport(testReport2);

		SampleDto sample = new SampleDto();
		mapper.mapToSample(sample);

		assertEquals(sample.getPathogenTestResult(), PathogenTestResultType.POSITIVE);
	}

	@Test
	public void testHomogenousTestResultTypesInWithInhomogeneousTestReports() {
		LabMessageDto labMessage = LabMessageDto.build();

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.addTestReport(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.addTestReport(testReport2);

		TestReportDto testReport3 = TestReportDto.build();
		testReport3.setTestResult(PathogenTestResultType.NEGATIVE);
		labMessage.addTestReport(testReport3);

		LabMessageMapper mapper = LabMessageMapper.forLabMessage(labMessage);

		SampleDto sample = new SampleDto();
		mapper.mapToSample(sample);

		assertNull(sample.getPathogenTestResult());
	}

	@Test
	public void testMigrateDiseaseVariant() throws CustomEnumNotFoundException {
		CustomizableEnumFacadeEjb customizableEnumFacade = mock(CustomizableEnumFacadeEjb.class);
		LabMessageDto labMessage = LabMessageDto.build();
		labMessage.setTestedDisease(Disease.CORONAVIRUS);
		TestReportDto testReport = TestReportDto.build();

		LabMessageMapper mapper = LabMessageMapper.forLabMessage(labMessage);

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
}
