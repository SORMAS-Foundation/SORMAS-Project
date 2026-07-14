package de.symeda.sormas.api.epipulse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;

public class EpipulseLaboratoryMapperTest {

	@Test
	public void testMapSampleMaterialToEpipulseCode_DryBlood() {
		assertEquals("DRYBLOSP", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.DRY_BLOOD));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_Blood() {
		assertEquals("SER", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.BLOOD));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_Sera() {
		assertEquals("SER", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.SERA));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_Urine() {
		assertEquals("URINE", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.URINE));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_NasalSwab() {
		assertEquals("NASALSWAB", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.NASAL_SWAB));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_ThroatSwab() {
		assertEquals("OTH", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.THROAT_SWAB));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_RectalSwab() {
		assertEquals("OTH", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.RECTAL_SWAB));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_ClinicalSample() {
		assertEquals("OTH", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.CLINICAL_SAMPLE));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_Other() {
		assertEquals("OTH", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.OTHER));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_Saliva() {
		assertEquals("SALOR", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.SALIVA));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_EdtaWholeBlood() {
		assertEquals("EDTA", EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.EDTA_WHOLE_BLOOD));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_UnmappedMaterial() {
		assertNull(EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.CEREBROSPINAL_FLUID));
		assertNull(EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(SampleMaterial.STOOL));
	}

	@Test
	public void testMapSampleMaterialToEpipulseCode_Null() {
		assertNull(EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(null));
	}

	@Test
	public void testParseMicValue_PlainNumber() {
		assertEquals(0.125f, EpipulseLaboratoryMapper.parseMicValue("0.125"));
	}

	@Test
	public void testParseMicValue_WithSignAndUnit() {
		assertEquals(0.125f, EpipulseLaboratoryMapper.parseMicValue("≤0.125 mg/L"));
		assertEquals(32f, EpipulseLaboratoryMapper.parseMicValue(">32 mg/l"));
	}

	@Test
	public void testParseMicValue_LeadingDotDecimal() {
		assertEquals(0.5f, EpipulseLaboratoryMapper.parseMicValue("≤.5 mg/L"));
	}

	@Test
	public void testParseMicValue_GenotypicText() {
		assertNull(EpipulseLaboratoryMapper.parseMicValue("mecA detected"));
	}

	@Test
	public void testParseMicValue_NullOrBlank() {
		assertNull(EpipulseLaboratoryMapper.parseMicValue(null));
		assertNull(EpipulseLaboratoryMapper.parseMicValue("   "));
	}

	@Test
	public void testMapToMeniDetectionMethod_MethodsMustNotDegradeToOther() {
		// "OTH" is the mapper's catch-all, so asserting "the result is one of the known codes" would always
		// pass. These methods each have a real MENI code and must never fall through to OTH.
		Map<PathogenTestType, String> expected = new EnumMap<>(PathogenTestType.class);
		expected.put(PathogenTestType.LATERAL_FLOW_ASSAY, "ANTIGEN");
		expected.put(PathogenTestType.MICROSCOPY, "MICRO");
		expected.put(PathogenTestType.GRAM_STAIN, "MICRO");
		expected.put(PathogenTestType.CULTURE, "CULT");
		expected.put(PathogenTestType.PCR_RT_PCR, "NUCLACID");
		expected.put(PathogenTestType.CQ_VALUE_DETECTION, "NUCLACID");
		expected.put(PathogenTestType.WHOLE_GENOME_SEQUENCING, "GENOSEQ");
		expected.put(PathogenTestType.MULTILOCUS_SEQUENCE_TYPING, "GENOSEQ");

		expected.forEach(
			(type, code) -> assertEquals(code, EpipulseLaboratoryMapper.mapToMeniDetectionMethod(type.name()), "MENI code for " + type.name()));

		// The catch-all still applies to a method with no MENI equivalent.
		assertEquals("OTH", EpipulseLaboratoryMapper.mapToMeniDetectionMethod(PathogenTestType.OTHER.name()));
	}
}
