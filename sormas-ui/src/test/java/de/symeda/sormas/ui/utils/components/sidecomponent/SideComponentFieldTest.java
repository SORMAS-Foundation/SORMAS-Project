/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.ui.utils.components.sidecomponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.Biotype;
import de.symeda.sormas.api.sample.GenoType;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenStrainCallStatus;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestScale;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.Serotype;
import de.symeda.sormas.api.sample.TargetTest;
import de.symeda.sormas.api.sample.ToxinResult;

/**
 * Unit tests for {@link SideComponentField#determineSideComponentVariant(PathogenTestDto)}, covering every
 * disease registered in {@code VARIANT_MAP} / {@code VARIANT_EXTRACTORS}.
 */
public class SideComponentFieldTest {

	private final SideComponentField sideComponentField = new SideComponentField();

	private static PathogenTestDto test(Disease disease, PathogenTestType testType) {
		PathogenTestDto dto = new PathogenTestDto();
		dto.setTestedDisease(disease);
		dto.setTestType(testType);
		return dto;
	}

	// --- guard clauses on determineSideComponentVariant itself ---

	@Test
	public void nullTestTypeOrDiseaseReturnsNull() {
		PathogenTestDto noType = new PathogenTestDto();
		noType.setTestedDisease(Disease.MALARIA);
		assertNull(sideComponentField.determineSideComponentVariant(noType));

		PathogenTestDto noDisease = new PathogenTestDto();
		noDisease.setTestType(PathogenTestType.THIN_BLOOD_SMEAR);
		assertNull(sideComponentField.determineSideComponentVariant(noDisease));
	}

	@Test
	public void measles_inVariantMapButNoExtractorReturnsNull() {
		// MEASLES has a VARIANT_MAP entry (GENOTYPING) but no VARIANT_EXTRACTORS entry
		assertNull(sideComponentField.determineSideComponentVariant(test(Disease.MEASLES, PathogenTestType.GENOTYPING)));
	}

	@Test
	public void testTypeNotRelevantForDiseaseReturnsNull() {
		// TUBERCULOSIS is mapped, but CULTURE isn't one of its relevant test types
		assertNull(sideComponentField.determineSideComponentVariant(test(Disease.TUBERCULOSIS, PathogenTestType.CULTURE)));
	}

	// --- TUBERCULOSIS: determineTuberculosisVariant ---

	@Test
	public void tuberculosisMicroscopyUsesTestScale() {
		PathogenTestDto dto = test(Disease.TUBERCULOSIS, PathogenTestType.MICROSCOPY);
		dto.setTestScale(PathogenTestScale.THREE_PLUS);
		assertEquals(PathogenTestScale.THREE_PLUS.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void tuberculosisMicroscopyNoTestScaleReturnsEmpty() {
		PathogenTestDto dto = test(Disease.TUBERCULOSIS, PathogenTestType.MICROSCOPY);
		assertEquals("", sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void tuberculosisBeijingGenotypingUsesStrainCallStatus() {
		PathogenTestDto dto = test(Disease.TUBERCULOSIS, PathogenTestType.BEIJINGGENOTYPING);
		dto.setStrainCallStatus(PathogenStrainCallStatus.BEIJING);
		assertEquals(PathogenStrainCallStatus.BEIJING.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void tuberculosisSpoligotypingUsesSpecie() {
		PathogenTestDto dto = test(Disease.TUBERCULOSIS, PathogenTestType.SPOLIGOTYPING);
		dto.setSpecie(PathogenSpecie.MYCOBATERIUM_TUBERCULOSIS);
		assertEquals(PathogenSpecie.MYCOBATERIUM_TUBERCULOSIS.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void tuberculosisMiruPatternCodeUsesPatternProfileVerbatim() {
		PathogenTestDto dto = test(Disease.TUBERCULOSIS, PathogenTestType.MIRU_PATTERN_CODE);
		dto.setPatternProfile("2-5-7-3-2-4-3-3-3-5-3-4-3-2-5-2-4-2-3-3-3-5-4-3");
		assertEquals("2-5-7-3-2-4-3-3-3-5-3-4-3-2-5-2-4-2-3-3-3-5-4-3", sideComponentField.determineSideComponentVariant(dto));
	}

	// --- MALARIA / SHIGELLOSIS: determineSpecieVariant (shared) ---

	@Test
	public void malariaSpecieOtherWithTextUsesSpecieText() {
		PathogenTestDto dto = test(Disease.MALARIA, PathogenTestType.THIN_BLOOD_SMEAR);
		dto.setSpecie(PathogenSpecie.OTHER);
		dto.setSpecieText("Plasmodium knowlesi");
		assertEquals("Plasmodium knowlesi", sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void malariaSpecieOtherWithoutTextFallsBackToEnum() {
		PathogenTestDto dto = test(Disease.MALARIA, PathogenTestType.THIN_BLOOD_SMEAR);
		dto.setSpecie(PathogenSpecie.OTHER);
		assertEquals(PathogenSpecie.OTHER.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void malariaSpecieNotOtherUsesEnumEvenIfTextSet() {
		PathogenTestDto dto = test(Disease.MALARIA, PathogenTestType.THIN_BLOOD_SMEAR);
		dto.setSpecie(PathogenSpecie.FALCIPARUM);
		dto.setSpecieText("ignored");
		assertEquals(PathogenSpecie.FALCIPARUM.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void malariaNoSpecieReturnsEmpty() {
		assertEquals("", sideComponentField.determineSideComponentVariant(test(Disease.MALARIA, PathogenTestType.THIN_BLOOD_SMEAR)));
	}

	@Test
	public void shigellosisSpecieOtherWithTextUsesSpecieText() {
		PathogenTestDto dto = test(Disease.SHIGELLOSIS, PathogenTestType.SEROGROUPING);
		dto.setSpecie(PathogenSpecie.OTHER);
		dto.setSpecieText("S. custom");
		assertEquals("S. custom", sideComponentField.determineSideComponentVariant(dto));
	}

	// --- INVASIVE_PNEUMOCOCCAL_INFECTION: determineSerotypeVariant ---

	@Test
	public void ipiSerotypeTextPresentTakesPriorityOverSerotype() {
		PathogenTestDto dto = test(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, PathogenTestType.SEROGROUPING);
		dto.setSerotype(Serotype.UNKNOWN);
		dto.setSerotypeText("19A");
		assertEquals("19A", sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void ipiNoSerotypeTextFallsBackToSerotype() {
		PathogenTestDto dto = test(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, PathogenTestType.SEROGROUPING);
		dto.setSerotype(Serotype.UNKNOWN);
		assertEquals(Serotype.UNKNOWN.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void ipiNeitherSetReturnsNull() {
		assertNull(sideComponentField.determineSideComponentVariant(test(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, PathogenTestType.SEROGROUPING)));
	}

	// --- DENGUE: determineDengueVariant ---

	@Test
	public void dengueSerotypeOtherWithTextUsesSerotypeText() {
		PathogenTestDto dto = test(Disease.DENGUE, PathogenTestType.NAAT);
		dto.setSerotype(Serotype.OTHER);
		dto.setSerotypeText("DENV-5?");
		assertEquals("DENV-5?", sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void dengueSerotypeNotOtherUsesEnum() {
		PathogenTestDto dto = test(Disease.DENGUE, PathogenTestType.NAAT);
		dto.setSerotype(Serotype.DENV_1);
		assertEquals(Serotype.DENV_1.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	// --- MUMPS: determineGenoTypeVariant ---

	@Test
	public void mumpsGenoTypeTextPresentTakesPriority() {
		PathogenTestDto dto = test(Disease.MUMPS, PathogenTestType.GENOTYPING);
		dto.setGenoType(GenoType.GENOTYPE_A);
		dto.setGenoTypeText("custom genotype");
		assertEquals("custom genotype", sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void mumpsNoGenoTypeTextFallsBackToGenoType() {
		PathogenTestDto dto = test(Disease.MUMPS, PathogenTestType.GENOTYPING);
		dto.setGenoType(GenoType.GENOTYPE_A);
		assertEquals(GenoType.GENOTYPE_A.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void mumpsNeitherSetReturnsNull() {
		assertNull(sideComponentField.determineSideComponentVariant(test(Disease.MUMPS, PathogenTestType.GENOTYPING)));
	}

	// --- DIPHTHERIA: determineDiphtheriaVariant + its per-test-type helpers ---

	@Test
	public void diphtheriaElekTestPositiveUsesToxinProducedCaption() {
		PathogenTestDto dto = test(Disease.DIPHTHERIA, PathogenTestType.ELEK_TEST);
		dto.setTestResult(PathogenTestResultType.POSITIVE);
		assertEquals(I18nProperties.getEnumCaption(ToxinResult.POSITIVE), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void diphtheriaElekTestNegativeUsesToxinNotProducedCaption() {
		PathogenTestDto dto = test(Disease.DIPHTHERIA, PathogenTestType.ELEK_TEST);
		dto.setTestResult(PathogenTestResultType.NEGATIVE);
		assertEquals(I18nProperties.getEnumCaption(ToxinResult.NEGATIVE), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void diphtheriaElekTestOtherResultFallsBackToRawResult() {
		PathogenTestDto dto = test(Disease.DIPHTHERIA, PathogenTestType.ELEK_TEST);
		dto.setTestResult(PathogenTestResultType.INDETERMINATE);
		assertEquals(PathogenTestResultType.INDETERMINATE.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void diphtheriaCulturePositiveCombinesSpecieAndBiotype() {
		PathogenTestDto dto = test(Disease.DIPHTHERIA, PathogenTestType.CULTURE);
		dto.setTestResult(PathogenTestResultType.POSITIVE);
		dto.setSpecie(PathogenSpecie.OTHER);
		dto.setSpecieText("C. ulcerans");
		dto.setBiotype(Biotype.VAR_GRAV);
		assertEquals("C. ulcerans - " + Biotype.VAR_GRAV.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void diphtheriaCulturePositiveNoBiotypeShowsSpecieOnly() {
		PathogenTestDto dto = test(Disease.DIPHTHERIA, PathogenTestType.CULTURE);
		dto.setTestResult(PathogenTestResultType.POSITIVE);
		dto.setSpecie(PathogenSpecie.OTHER);
		dto.setSpecieText("C. ulcerans");
		assertEquals("C. ulcerans", sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void diphtheriaCultureNotPositiveShowsRawResult() {
		PathogenTestDto dto = test(Disease.DIPHTHERIA, PathogenTestType.CULTURE);
		dto.setTestResult(PathogenTestResultType.NEGATIVE);
		dto.setSpecie(PathogenSpecie.OTHER);
		dto.setSpecieText("should be ignored");
		assertEquals(PathogenTestResultType.NEGATIVE.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void diphtheriaPcrPositiveWithTargetTestCombinesTargetAndSpecie() {
		PathogenTestDto dto = test(Disease.DIPHTHERIA, PathogenTestType.PCR_RT_PCR);
		dto.setTestResult(PathogenTestResultType.POSITIVE);
		dto.setTargetTest(TargetTest.OTHER);
		dto.setTargetTestText("tox gene");
		dto.setSpecie(PathogenSpecie.OTHER);
		dto.setSpecieText("C. diphtheriae");
		assertEquals("tox gene - C. diphtheriae", sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void diphtheriaPcrPositiveWithTargetTestNoSpecieShowsTargetTestOnly() {
		PathogenTestDto dto = test(Disease.DIPHTHERIA, PathogenTestType.PCR_RT_PCR);
		dto.setTestResult(PathogenTestResultType.POSITIVE);
		dto.setTargetTest(TargetTest.SPECIES_IDENTIFICATION);
		assertEquals(TargetTest.SPECIES_IDENTIFICATION.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void diphtheriaPcrPositiveWithoutTargetTestShowsRawResult() {
		// this is the case the (testResult != POSITIVE || targetTest == null) guard exists for -
		// without it, a positive result with no target test would render as " - <specie>"
		PathogenTestDto dto = test(Disease.DIPHTHERIA, PathogenTestType.PCR_RT_PCR);
		dto.setTestResult(PathogenTestResultType.POSITIVE);
		dto.setSpecie(PathogenSpecie.OTHER);
		dto.setSpecieText("C. diphtheriae");
		assertEquals(PathogenTestResultType.POSITIVE.toString(), sideComponentField.determineSideComponentVariant(dto));
	}

	@Test
	public void diphtheriaPcrNotPositiveShowsRawResult() {
		PathogenTestDto dto = test(Disease.DIPHTHERIA, PathogenTestType.PCR_RT_PCR);
		dto.setTestResult(PathogenTestResultType.PENDING);
		dto.setTargetTest(TargetTest.SPECIES_IDENTIFICATION);
		assertEquals(PathogenTestResultType.PENDING.toString(), sideComponentField.determineSideComponentVariant(dto));
	}
}
