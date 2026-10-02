package de.symeda.sormas.api.epipulse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

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

	// ---------------------------------------------------------------------------------------
	// MIC: both halves come from the stored text, and neither is inferred
	// ---------------------------------------------------------------------------------------

	@Test
	public void testParseMic_PlainNumberHasNoSign() {

		// The rule this class exists to protect: a bare number carries no operator, so none is
		// reported. The sign used to be computed from a hardcoded 0.002-32 dilution range, which
		// turned every mid-range value into "=" whether the laboratory had written one or not.
		assertNull(EpipulseLaboratoryMapper.parseMic("0.125").getSign());
		assertEquals(0.125d, EpipulseLaboratoryMapper.parseMic("0.125").getValue());
	}

	@Test
	public void testParseMic_CensoredValueKeepsItsOperator() {

		assertEquals("<=", EpipulseLaboratoryMapper.parseMic("\u22640.125 mg/L").getSign());
		assertEquals(0.125d, EpipulseLaboratoryMapper.parseMic("\u22640.125 mg/L").getValue());

		assertEquals(">", EpipulseLaboratoryMapper.parseMic("> 32 mg/l").getSign());
		assertEquals(32d, EpipulseLaboratoryMapper.parseMic("> 32 mg/l").getValue());
	}

	@Test
	public void testParseMic_EveryEpipulseSignCodeIsReachable() {

		// EpiPulse accepts exactly these five.
		assertEquals("<", EpipulseLaboratoryMapper.parseMic("<0.5").getSign());
		assertEquals("<=", EpipulseLaboratoryMapper.parseMic("<=0.5").getSign());
		assertEquals("=", EpipulseLaboratoryMapper.parseMic("=0.5").getSign());
		assertEquals(">", EpipulseLaboratoryMapper.parseMic(">0.5").getSign());
		assertEquals(">=", EpipulseLaboratoryMapper.parseMic(">=0.5").getSign());
	}

	@Test
	public void testParseMic_AlternativeSpellingsOfTheSameOperator() {

		for (String lessOrEqual : new String[] {
			"<=0.5",
			"=<0.5",
			"\u22640.5",
			"\u2A7D0.5",
			"\u22660.5",
			"LTE 0.5",
			"le 0.5" }) {
			assertEquals("<=", EpipulseLaboratoryMapper.parseMic(lessOrEqual).getSign(), lessOrEqual);
		}

		for (String greaterOrEqual : new String[] {
			">=0.5",
			"=>0.5",
			"\u22650.5",
			"\u2A7E0.5",
			"\u22670.5",
			"GTE 0.5",
			"ge 0.5" }) {
			assertEquals(">=", EpipulseLaboratoryMapper.parseMic(greaterOrEqual).getSign(), greaterOrEqual);
		}

		assertEquals("<", EpipulseLaboratoryMapper.parseMic("lt 0.5").getSign());
		assertEquals(">", EpipulseLaboratoryMapper.parseMic("gt 0.5").getSign());
		assertEquals("=", EpipulseLaboratoryMapper.parseMic("eq 0.5").getSign());
		assertEquals("=", EpipulseLaboratoryMapper.parseMic("==0.5").getSign());
	}

	@Test
	public void testParseMic_LongerOperatorWinsOverItsPrefix() {

		// "lte" must not be read as "lt", nor the typographic forms as their one-character halves.
		assertEquals("<=", EpipulseLaboratoryMapper.parseMic("lte0.5").getSign());
		assertEquals("<", EpipulseLaboratoryMapper.parseMic("lt0.5").getSign());
		assertEquals(">=", EpipulseLaboratoryMapper.parseMic("gte0.5").getSign());
		assertEquals(">", EpipulseLaboratoryMapper.parseMic("gt0.5").getSign());
	}

	@Test
	public void testParseMic_DecimalComma() {
		assertEquals(0.125d, EpipulseLaboratoryMapper.parseMic("0,125 mg/L").getValue());
		assertEquals(0.5d, EpipulseLaboratoryMapper.parseMic("\u22640,5").getValue());
	}

	@Test
	public void testParseMic_LeadingDecimalSeparator() {
		assertEquals(0.5d, EpipulseLaboratoryMapper.parseMic("\u2264.5 mg/L").getValue());
		assertEquals(0.5d, EpipulseLaboratoryMapper.parseMic(",5").getValue());
	}

	@Test
	public void testParseMic_GenotypicTextYieldsNothing() {

		assertNull(EpipulseLaboratoryMapper.parseMic("mecA detected").getValue());
		assertNull(EpipulseLaboratoryMapper.parseMic("mecA detected").getSign());
	}

	@Test
	public void testParseMic_NumberNotAtTheStartIsNotScraped() {

		// Anchored on purpose: reading a number out of the middle of a sentence would report
		// "serotype 16F" as a concentration of 16.
		assertNull(EpipulseLaboratoryMapper.parseMic("serotype 16F").getValue());
	}

	@Test
	public void testParseMic_SignWithoutAValueReportsNeither() {

		// MICSign qualifies "the value indicated in the following field", so a sign with nothing to
		// qualify is not reported.
		assertNull(EpipulseLaboratoryMapper.parseMic("<=").getSign());
		assertNull(EpipulseLaboratoryMapper.parseMic("<=").getValue());
	}

	@Test
	public void testParseMic_NullOrBlank() {

		assertNull(EpipulseLaboratoryMapper.parseMic(null).getValue());
		assertNull(EpipulseLaboratoryMapper.parseMic(null).getSign());
		assertNull(EpipulseLaboratoryMapper.parseMic("   ").getValue());
	}

}
