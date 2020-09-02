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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.caze;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class CaseClassificationLogicTest extends AbstractBeanTest {

	@Before
	public void enableAutomaticCaseClassification() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.FEATURE_AUTOMATIC_CASE_CLASSIFICATION, "true");
	}

	@Test
	public void testAutomaticClassificationForEVD() {

		// Suspect
		CaseDataDto caze = buildSuspectCaseBasis(Disease.EVD);
		caze.getSymptoms().setDiarrhea(SymptomState.YES);
		caze.getSymptoms().setBloodyBlackStool(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.EVD);
		caze.getSymptoms().setGumsBleeding(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.EVD);
		caze.getSymptoms().setSkinBruising(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.EVD);
		caze.getSymptoms().setEyesBleeding(SymptomState.YES);
		caze.getSymptoms().setBloodUrine(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Probable
		caze = buildProbableCaseBasis(Disease.EVD);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		caze = buildProbableCaseBasis(Disease.EVD);
		caze.getEpiData().setProcessingConfirmedCaseFluidUnsafe(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		caze = buildProbableCaseBasis(Disease.EVD);
		caze.getEpiData().setPercutaneousCaseBlood(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		caze = buildProbableCaseBasis(Disease.EVD);
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.YES);
		caze.getEpiData().setDirectContactDeadUnsafe(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.EVD));
		creator.createPathogenTest(caze, Disease.EVD, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.EVD));
		creator.createPathogenTest(caze, Disease.EVD, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.EVD));
		creator.createPathogenTest(caze, Disease.EVD, PathogenTestType.ISOLATION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());
	}

	@Test
	public void ruleOutFalsePositivesForEVD() {

		// Suspect
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.EVD);
		fillSymptoms(caze.getSymptoms());
		caze.getSymptoms().setFever(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setFever(SymptomState.YES);
		caze.getSymptoms().setDiarrhea(SymptomState.NO);
		caze.getSymptoms().setGumsBleeding(SymptomState.NO);
		caze.getSymptoms().setSkinBruising(SymptomState.NO);
		caze.getSymptoms().setEyesBleeding(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.EVD);
		caze.setOutcome(CaseOutcome.DECEASED);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		fillEpiData(caze.getEpiData());
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.NO);
		caze.getEpiData().setProcessingConfirmedCaseFluidUnsafe(YesNoUnknown.NO);
		caze.getEpiData().setPercutaneousCaseBlood(YesNoUnknown.NO);
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		caze.setOutcome(CaseOutcome.NO_OUTCOME);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Confirmed
		caze = buildSuspectCase(Disease.EVD);
		caze = getCaseFacade().saveCase(caze);
		createSampleTestsForAllTestTypesExcept(
			caze,
			Disease.EVD,
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestType.ISOLATION);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
	}

	@Test
	public void testAutomaticClassificationForCSM() {

		// Suspect
		CaseDataDto caze = buildSuspectCaseBasis(Disease.CSM);
		caze.getSymptoms().setNeckStiffness(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.CSM);
		caze.getSymptoms().setAlteredConsciousness(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.CSM);
		caze.getSymptoms().setMeningealSigns(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.CSM);
		caze.getSymptoms().setBulgingFontanelle(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.CSM);
		caze.setOutcome(CaseOutcome.DECEASED);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.CSM));
		creator.createPathogenTest(caze, Disease.CSM, PathogenTestType.ISOLATION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

	}

	@Test
	public void ruleOutFalsePositivesForCSM() {

		// Suspect
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.CSM);
		fillSymptoms(caze.getSymptoms());
		caze.getSymptoms().setFever(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setFever(SymptomState.YES);
		caze.getSymptoms().setNeckStiffness(SymptomState.NO);
		caze.getSymptoms().setAlteredConsciousness(SymptomState.NO);
		caze.getSymptoms().setMeningealSigns(SymptomState.NO);
		caze.getSymptoms().setBulgingFontanelle(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.CSM);
		caze.setOutcome(CaseOutcome.DECEASED);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		fillEpiData(caze.getEpiData());
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		caze.setOutcome(CaseOutcome.NO_OUTCOME);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Confirmed
		caze = buildSuspectCase(Disease.CSM);
		caze = getCaseFacade().saveCase(caze);
		createSampleTestsForAllTestTypesExcept(caze, Disease.CSM, PathogenTestType.ISOLATION);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
	}

	@Test
	public void testAutomaticClassificationForLassa() {

		// Suspect
		CaseDataDto caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setFatigueWeakness(SymptomState.YES);
		caze.getEpiData().setDirectContactProbableCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setFever(SymptomState.YES);
		caze.getEpiData().setDirectContactProbableCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setHeadache(SymptomState.YES);
		caze.getEpiData().setDirectContactProbableCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setSoreThroat(SymptomState.YES);
		caze.getEpiData().setDirectContactProbableCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setCough(SymptomState.YES);
		caze.getEpiData().setDirectContactProbableCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setNausea(SymptomState.YES);
		caze.getEpiData().setDirectContactProbableCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setVomiting(SymptomState.YES);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setDiarrhea(SymptomState.YES);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setMusclePain(SymptomState.YES);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setChestPain(SymptomState.YES);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.LASSA);
		caze.getSymptoms().setHearingloss(SymptomState.YES);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.LASSA);
		caze.setOutcome(CaseOutcome.DECEASED);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.LASSA));
		creator.createPathogenTest(caze, Disease.LASSA, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.LASSA));
		creator.createPathogenTest(caze, Disease.LASSA, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.LASSA));
		creator.createPathogenTest(caze, Disease.LASSA, PathogenTestType.ISOLATION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());
	}

	@Test
	public void ruleOutFalsePositivesForLassa() {

		// Suspect
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.LASSA);
		fillSymptoms(caze.getSymptoms());
		caze.getSymptoms().setFatigueWeakness(SymptomState.NO);
		caze.getSymptoms().setFever(SymptomState.NO);
		caze.getSymptoms().setHeadache(SymptomState.NO);
		caze.getSymptoms().setSoreThroat(SymptomState.NO);
		caze.getSymptoms().setCough(SymptomState.NO);
		caze.getSymptoms().setNausea(SymptomState.NO);
		caze.getSymptoms().setVomiting(SymptomState.NO);
		caze.getSymptoms().setDiarrhea(SymptomState.NO);
		caze.getSymptoms().setMusclePain(SymptomState.NO);
		caze.getSymptoms().setChestPain(SymptomState.NO);
		caze.getSymptoms().setHearingloss(SymptomState.NO);
		fillEpiData(caze.getEpiData());
		caze.getEpiData().setRodents(YesNoUnknown.NO);
		caze.getEpiData().setDirectContactProbableCase(YesNoUnknown.NO);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setFever(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setFever(SymptomState.NO);
		caze.getEpiData().setRodents(YesNoUnknown.YES);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());

		// Probable doesn't need to be tested

		// Confirmed
		caze = buildSuspectCase(Disease.LASSA);
		caze = getCaseFacade().saveCase(caze);
		createSampleTestsForAllTestTypesExcept(
			caze,
			Disease.LASSA,
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestType.ISOLATION);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
	}

	@Test
	public void testAutomaticClassificationForYellowFever() {

		// Suspect
		CaseDataDto caze = buildSuspectCaseBasis(Disease.YELLOW_FEVER);
		caze.getSymptoms().setJaundice(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.YELLOW_FEVER);
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		caze = buildSuspectCase(Disease.YELLOW_FEVER);
		caze.setOutcome(CaseOutcome.DECEASED);
		caze = getCaseFacade().saveCase(caze);
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.HISTOPATHOLOGY, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildConfirmedCaseBasis(Disease.YELLOW_FEVER));
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		creator.createPathogenTest(caze, Disease.DENGUE, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE);
		creator.createPathogenTest(caze, Disease.WEST_NILE_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildConfirmedCaseBasis(Disease.YELLOW_FEVER));
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.NEUTRALIZING_ANTIBODIES, PathogenTestResultType.POSITIVE);
		creator.createPathogenTest(caze, Disease.DENGUE, PathogenTestType.NEUTRALIZING_ANTIBODIES, PathogenTestResultType.NEGATIVE);
		creator.createPathogenTest(caze, Disease.WEST_NILE_FEVER, PathogenTestType.NEUTRALIZING_ANTIBODIES, PathogenTestResultType.NEGATIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildConfirmedCaseBasis(Disease.YELLOW_FEVER));
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildConfirmedCaseBasis(Disease.YELLOW_FEVER));
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.ANTIGEN_DETECTION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildConfirmedCaseBasis(Disease.YELLOW_FEVER));
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.ISOLATION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildConfirmedCaseBasis(Disease.YELLOW_FEVER));
		PathogenTestDto sampleTest =
			creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		sampleTest.setFourFoldIncreaseAntibodyTiter(true);
		getSampleTestFacade().savePathogenTest(sampleTest);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildConfirmedCaseBasis(Disease.YELLOW_FEVER));
		sampleTest = creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		sampleTest.setFourFoldIncreaseAntibodyTiter(true);
		getSampleTestFacade().savePathogenTest(sampleTest);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());
	}

	@Test
	public void ruleOutFalsePositivesForYellowFever() {

		// Suspect
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.YELLOW_FEVER);
		fillSymptoms(caze.getSymptoms());
		caze.getSymptoms().setFever(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setFever(SymptomState.YES);
		caze.getSymptoms().setJaundice(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.YELLOW_FEVER);
		fillEpiData(caze.getEpiData());
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		caze.setOutcome(CaseOutcome.DECEASED);
		caze.setVaccinationDate(DateHelper.subtractDays(new Date(), 1));
		caze = getCaseFacade().saveCase(caze);
		createSampleTestsForAllTestTypesExcept(caze, Disease.YELLOW_FEVER, PathogenTestType.HISTOPATHOLOGY);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		caze.setOutcome(CaseOutcome.NO_OUTCOME);
		caze = getCaseFacade().saveCase(caze);
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.HISTOPATHOLOGY, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.YELLOW_FEVER));
		createSampleTestsForAllTestTypesExcept(
			caze,
			Disease.YELLOW_FEVER,
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestType.NEUTRALIZING_ANTIBODIES,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestType.ANTIGEN_DETECTION,
			PathogenTestType.ISOLATION);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.NEUTRALIZING_ANTIBODIES, PathogenTestResultType.POSITIVE);
		creator.createPathogenTest(caze, Disease.WEST_NILE_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		creator.createPathogenTest(caze, Disease.WEST_NILE_FEVER, PathogenTestType.NEUTRALIZING_ANTIBODIES, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.ISOLATION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		caze.setVaccinationDate(DateHelper.subtractDays(new Date(), 1));
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
	}

	@Test
	public void testAutomaticClassificationForDengueFever() {

		// Suspect
		CaseDataDto caze = buildSuspectCaseBasis(Disease.DENGUE);
		caze.getSymptoms().setHeadache(SymptomState.YES);
		caze.getSymptoms().setEyePainLightSensitive(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.DENGUE);
		caze.getSymptoms().setNausea(SymptomState.YES);
		caze.getSymptoms().setVomiting(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.DENGUE);
		caze.getSymptoms().setSwollenGlands(SymptomState.YES);
		caze.getSymptoms().setSkinRash(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.DENGUE);
		caze.getSymptoms().setSkinRash(SymptomState.YES);
		caze.getSymptoms().setMusclePain(SymptomState.YES);
		caze.getSymptoms().setJointPain(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.DENGUE);
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.DENGUE));
		creator.createPathogenTest(caze, Disease.DENGUE, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE);
		creator.createPathogenTest(caze, Disease.WEST_NILE_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.NEGATIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.DENGUE));
		creator.createPathogenTest(caze, Disease.DENGUE, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.DENGUE));
		creator.createPathogenTest(caze, Disease.DENGUE, PathogenTestType.ISOLATION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.DENGUE));
		PathogenTestDto sampleTest =
			creator.createPathogenTest(caze, Disease.DENGUE, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		sampleTest.setFourFoldIncreaseAntibodyTiter(true);
		getSampleTestFacade().savePathogenTest(sampleTest);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());
	}

	@Test
	public void ruleOutFalsePositivesForDengueFever() {

		// Suspect
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.DENGUE);
		fillSymptoms(caze.getSymptoms());
		caze.getSymptoms().setFever(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setFever(SymptomState.YES);
		caze.getSymptoms().setHeadache(SymptomState.NO);
		caze.getSymptoms().setEyePainLightSensitive(SymptomState.NO);
		caze.getSymptoms().setNausea(SymptomState.NO);
		caze.getSymptoms().setVomiting(SymptomState.NO);
		caze.getSymptoms().setSwollenGlands(SymptomState.NO);
		caze.getSymptoms().setJointPain(SymptomState.NO);
		caze.getSymptoms().setSkinRash(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.DENGUE);
		fillEpiData(caze.getEpiData());
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.DENGUE));
		createSampleTestsForAllTestTypesExcept(
			caze,
			Disease.DENGUE,
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestType.ISOLATION);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		creator.createPathogenTest(caze, Disease.DENGUE, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		creator.createPathogenTest(caze, Disease.YELLOW_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
	}

	@Test
	public void testAutomaticClassificationForNewFlu() {

		// Suspect
		CaseDataDto caze = buildSuspectCaseBasis(Disease.NEW_INFLUENZA);
		caze.getEpiData().setCloseContactProbableCase(YesNoUnknown.YES);
		caze.getSymptoms().setCough(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.NEW_INFLUENZA);
		caze.getEpiData().setCloseContactProbableCase(YesNoUnknown.YES);
		caze.getSymptoms().setDifficultyBreathing(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.NEW_INFLUENZA);
		caze.getSymptoms().setCough(SymptomState.YES);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.NEW_INFLUENZA);
		caze.getSymptoms().setCough(SymptomState.YES);
		caze.getEpiData().setAreaInfectedAnimals(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.NEW_INFLUENZA);
		caze.getSymptoms().setCough(SymptomState.YES);
		caze.getEpiData().setEatingRawAnimalsInInfectedArea(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.NEW_INFLUENZA);
		caze.getSymptoms().setCough(SymptomState.YES);
		caze.getEpiData().setProcessingSuspectedCaseSampleUnsafe(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Probable
		caze = buildProbableCaseBasis(Disease.NEW_INFLUENZA);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.NEW_INFLUENZA));
		creator.createPathogenTest(caze, Disease.NEW_INFLUENZA, PathogenTestType.ISOLATION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.NEW_INFLUENZA));
		creator.createPathogenTest(caze, Disease.NEW_INFLUENZA, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.NEW_INFLUENZA));
		PathogenTestDto sampleTest =
			creator.createPathogenTest(caze, Disease.NEW_INFLUENZA, PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		sampleTest.setFourFoldIncreaseAntibodyTiter(true);
		getSampleTestFacade().savePathogenTest(sampleTest);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());
	}

	@Test
	public void ruleOutFalsePositivesForNewFlu() {

		// Suspect
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.NEW_INFLUENZA);
		fillSymptoms(caze.getSymptoms());
		fillEpiData(caze.getEpiData());
		caze.getSymptoms().setFever(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setFever(SymptomState.YES);
		caze.getSymptoms().setCough(SymptomState.NO);
		caze.getSymptoms().setDifficultyBreathing(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setCough(SymptomState.YES);
		caze.getEpiData().setCloseContactProbableCase(YesNoUnknown.NO);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.NO);
		caze.getEpiData().setAreaInfectedAnimals(YesNoUnknown.NO);
		caze.getEpiData().setEatingRawAnimalsInInfectedArea(YesNoUnknown.NO);
		caze.getEpiData().setProcessingSuspectedCaseSampleUnsafe(YesNoUnknown.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.NEW_INFLUENZA);
		caze.setOutcome(CaseOutcome.DECEASED);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		caze.setOutcome(CaseOutcome.NO_OUTCOME);
		fillEpiData(caze.getEpiData());
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Confirmed
		caze = buildSuspectCase(Disease.NEW_INFLUENZA);
		caze = getCaseFacade().saveCase(caze);
		createSampleTestsForAllTestTypesExcept(
			caze,
			Disease.NEW_INFLUENZA,
			PathogenTestType.ISOLATION,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestType.NEUTRALIZING_ANTIBODIES);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
	}

	@Test
	public void testAutomaticClassificationForMeasles() {

		// Suspect
		CaseDataDto caze = buildSuspectCaseBasis(Disease.MEASLES);
		caze.getSymptoms().setCough(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.MEASLES);
		caze.getSymptoms().setRunnyNose(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.MEASLES);
		caze.getSymptoms().setConjunctivitis(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.MEASLES);
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.MEASLES));
		creator.createPathogenTest(caze, Disease.MEASLES, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());
	}

	@Test
	public void ruleOutFalsePositivesForMeasles() {

		// Suspect
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.MEASLES);
		fillSymptoms(caze.getSymptoms());
		caze.getSymptoms().setCough(SymptomState.NO);
		caze.getSymptoms().setRunnyNose(SymptomState.NO);
		caze.getSymptoms().setConjunctivitis(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setCough(SymptomState.YES);
		caze.getSymptoms().setFever(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setFever(SymptomState.YES);
		caze.getSymptoms().setSkinRash(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());

		// Probable
		caze = buildSuspectCase(Disease.MEASLES);
		fillEpiData(caze.getEpiData());
		caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Confirmed
		caze = buildSuspectCase(Disease.MEASLES);
		caze = getCaseFacade().saveCase(caze);
		createSampleTestsForAllTestTypesExcept(caze, Disease.MEASLES, PathogenTestType.IGM_SERUM_ANTIBODY);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
	}

	@Test
	public void testAutomaticClassificationForCholera() {

		// Suspect
		CaseDataDto caze = buildSuspectCaseBasis(Disease.CHOLERA);
		caze.getSymptoms().setDehydration(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.CHOLERA);
		caze.getSymptoms().setDiarrhea(SymptomState.YES);
		caze.setOutcome(CaseOutcome.DECEASED);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.CHOLERA);
		caze.getSymptoms().setDiarrhea(SymptomState.YES);
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.CHOLERA));
		creator.createPathogenTest(caze, Disease.CHOLERA, PathogenTestType.ISOLATION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());
	}

	@Test
	public void ruleOutFalsePositivesForCholera() {

		// Suspect
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.CHOLERA);
		fillSymptoms(caze.getSymptoms());
		fillEpiData(caze.getEpiData());
		caze.getSymptoms().setDehydration(SymptomState.NO);
		caze.getSymptoms().setDiarrhea(SymptomState.NO);
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.NO);
		PersonDto casePerson = getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());
		casePerson.setApproximateAge(5);
		casePerson.setApproximateAgeType(ApproximateAgeType.YEARS);
		getPersonFacade().savePerson(casePerson);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		casePerson.setApproximateAge(0);
		getPersonFacade().savePerson(casePerson);
		caze.setOutcome(CaseOutcome.DECEASED);
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setDiarrhea(SymptomState.YES);
		caze.getSymptoms().setDehydration(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.CHOLERA));
		createSampleTestsForAllTestTypesExcept(caze, Disease.CHOLERA, PathogenTestType.ISOLATION);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
	}

	@Test
	public void testAutomaticClassificationForMonkeypox() {

		// Suspect
		CaseDataDto caze = buildSuspectCaseBasis(Disease.MONKEYPOX);
		caze.getSymptoms().setSkinRash(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.MONKEYPOX));
		creator.createPathogenTest(caze, Disease.MONKEYPOX, PathogenTestType.ISOLATION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.MONKEYPOX));
		creator.createPathogenTest(caze, Disease.MONKEYPOX, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.MONKEYPOX));
		creator.createPathogenTest(caze, Disease.MONKEYPOX, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());
	}

	@Test
	public void ruleOutFalsePositivesForMonkeypox() {

		// Suspect
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.MONKEYPOX);
		fillSymptoms(caze.getSymptoms());
		caze.getSymptoms().setFever(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setFever(SymptomState.YES);
		caze.getSymptoms().setSkinRash(SymptomState.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());

		// Confirmed
		caze = buildSuspectCase(Disease.MONKEYPOX);
		caze = getCaseFacade().saveCase(caze);
		createSampleTestsForAllTestTypesExcept(
			caze,
			Disease.MONKEYPOX,
			PathogenTestType.ISOLATION,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestType.IGM_SERUM_ANTIBODY);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
	}

	@Test
	public void testAutomaticClassificationForPlague() {

		// Suspect
		CaseDataDto caze = buildSuspectCaseBasis(Disease.PLAGUE);
		caze.setPlagueType(PlagueType.BUBONIC);
		caze.getSymptoms().setPainfulLymphadenitis(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.PLAGUE);
		caze.setPlagueType(PlagueType.PNEUMONIC);
		caze.getSymptoms().setCough(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.PLAGUE);
		caze.setPlagueType(PlagueType.PNEUMONIC);
		caze.getSymptoms().setChestPain(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.PLAGUE);
		caze.setPlagueType(PlagueType.BUBONIC);
		caze.getSymptoms().setCoughingBlood(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		caze = buildSuspectCaseBasis(Disease.PLAGUE);
		caze.setPlagueType(PlagueType.SEPTICAEMIC);
		caze.getSymptoms().setChillsSweats(SymptomState.YES);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());

		// Probable
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.PLAGUE));
		creator.createPathogenTest(caze, Disease.PLAGUE, PathogenTestType.ANTIGEN_DETECTION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		caze = buildSuspectCase(Disease.PLAGUE);
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.YES);
		caze = getCaseFacade().saveCase(caze);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.PROBABLE, caze.getCaseClassification());

		// Confirmed
		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.PLAGUE));
		creator.createPathogenTest(caze, Disease.PLAGUE, PathogenTestType.ISOLATION, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());

		caze = getCaseFacade().saveCase(buildSuspectCase(Disease.PLAGUE));
		creator.createPathogenTest(caze, Disease.PLAGUE, PathogenTestType.PCR_RT_PCR, PathogenTestResultType.POSITIVE);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.CONFIRMED, caze.getCaseClassification());
	}

	@Test
	public void ruleOutFalsePositivesForPlague() {

		// Suspect
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PLAGUE);
		fillSymptoms(caze.getSymptoms());
		caze.getSymptoms().setFever(SymptomState.NO);
		caze.setPlagueType(PlagueType.BUBONIC);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.setPlagueType(PlagueType.PNEUMONIC);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.setPlagueType(PlagueType.SEPTICAEMIC);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());
		caze.getSymptoms().setFever(SymptomState.YES);
		caze.getSymptoms().setPainfulLymphadenitis(SymptomState.NO);
		caze.getSymptoms().setCough(SymptomState.NO);
		caze.getSymptoms().setChestPain(SymptomState.NO);
		caze.getSymptoms().setCoughingBlood(SymptomState.NO);
		caze.getSymptoms().setChillsSweats(SymptomState.NO);
		caze.setPlagueType(PlagueType.BUBONIC);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.NOT_CLASSIFIED, caze.getCaseClassification());

		// Probable & Confirmed
		caze = buildSuspectCase(Disease.PLAGUE);
		fillEpiData(caze.getEpiData());
		caze.getEpiData().setAreaConfirmedCases(YesNoUnknown.NO);
		caze = getCaseFacade().saveCase(caze);
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
		createSampleTestsForAllTestTypesExcept(
			caze,
			Disease.PLAGUE,
			PathogenTestType.ANTIGEN_DETECTION,
			PathogenTestType.ISOLATION,
			PathogenTestType.PCR_RT_PCR);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertEquals(CaseClassification.SUSPECT, caze.getCaseClassification());
	}

	/**
	 * Sets all symptoms with the SymptomState type to YES.
	 */
	private void fillSymptoms(SymptomsDto symptoms) {

		Method[] methods = SymptomsDto.class.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("set") && method.getParameterTypes()[0] == SymptomState.class) {
				try {
					method.invoke(symptoms, SymptomState.YES);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Sets all epi data fields with the YesNoUnknown type to YES.
	 */
	private void fillEpiData(EpiDataDto epiData) {

		Method[] methods = EpiDataDto.class.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("set") && method.getParameterTypes()[0] == YesNoUnknown.class) {
				try {
					method.invoke(epiData, YesNoUnknown.YES);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Builds a case for the specified disease that meets all requirements that are needed for
	 * every suspect classification scenario, but that can not be classified as suspect yet
	 * (i.e. there is still at least one requirement missing to classify it as such). Used to
	 * initialize suspect test cases with less code.
	 */
	private CaseDataDto buildSuspectCaseBasis(Disease disease) {

		CaseDataDto caze = creator.createUnclassifiedCase(disease);
		switch (disease) {
		case EVD:
		case CSM:
		case YELLOW_FEVER:
		case DENGUE:
		case NEW_INFLUENZA:
		case MONKEYPOX:
			caze.getSymptoms().setFever(SymptomState.YES);
			break;
		case LASSA:
			caze.getEpiData().setRodents(YesNoUnknown.YES);
			break;
		case MEASLES:
			caze.getSymptoms().setFever(SymptomState.YES);
			caze.getSymptoms().setSkinRash(SymptomState.YES);
			break;
		case CHOLERA:
			PersonDto casePerson = getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());
			casePerson.setApproximateAge(5);
			casePerson.setApproximateAgeType(ApproximateAgeType.YEARS);
			getPersonFacade().savePerson(casePerson);
			caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
			break;
		case PLAGUE:
			caze.setPlagueType(PlagueType.BUBONIC);
			caze.getSymptoms().setFever(SymptomState.YES);
			break;
		default:
			throw new IllegalArgumentException();
		}

		return caze;
	}

	/**
	 * Builds a case for the specified disease that meets all requirements to classify it as suspect.
	 */
	private CaseDataDto buildSuspectCase(Disease disease) {

		CaseDataDto caze = buildSuspectCaseBasis(disease);
		switch (disease) {
		case EVD:
			caze.getSymptoms().setGumsBleeding(SymptomState.YES);
			break;
		case CSM:
			caze.getSymptoms().setNeckStiffness(SymptomState.YES);
			break;
		case LASSA:
			caze.getSymptoms().setFever(SymptomState.YES);
			caze.getEpiData().setDirectContactConfirmedCase(YesNoUnknown.YES);
			break;
		case YELLOW_FEVER:
			caze.getSymptoms().setJaundice(SymptomState.YES);
			break;
		case DENGUE:
			caze.getSymptoms().setHeadache(SymptomState.YES);
			caze.getSymptoms().setNausea(SymptomState.YES);
			break;
		case NEW_INFLUENZA:
			caze.getSymptoms().setCough(SymptomState.YES);
			caze.getEpiData().setCloseContactProbableCase(YesNoUnknown.YES);
			break;
		case MEASLES:
			caze.getSymptoms().setCough(SymptomState.YES);
			break;
		case CHOLERA:
			caze.getSymptoms().setDehydration(SymptomState.YES);
			break;
		case MONKEYPOX:
			caze.getSymptoms().setSkinRash(SymptomState.YES);
			break;
		case PLAGUE:
			caze.getSymptoms().setPainfulLymphadenitis(SymptomState.YES);
			break;
		default:
			throw new IllegalArgumentException();
		}

		return caze;
	}

	/**
	 * Builds a case for the specified disease that meets all requirements to classify it as suspect
	 * and all requirements that are needed for every probable classification scenario, but that can
	 * not be classified as probable yet (i.e. there is still at least one requirement missing to
	 * classify it as such). Used to initialize probable test cases with less code.
	 */
	private CaseDataDto buildProbableCaseBasis(Disease disease) {

		CaseDataDto caze = buildSuspectCase(disease);
		switch (disease) {
		case EVD:
		case NEW_INFLUENZA:
			caze.setOutcome(CaseOutcome.DECEASED);
			break;
		default:
			throw new UnsupportedOperationException("Disease has no constant requirement or variation in probable definition");
		}

		return caze;
	}

	/**
	 * Builds a case for the specified disease that meets all requirements to classify it as suspect
	 * and all requirements that are needed for every confirmed classification scenario, but that can
	 * not be classified as confirmed yet (i.e. there is still at least one requirement missing to
	 * classify it as such). Used to initialize confirmed test cases with less code.
	 */
	private CaseDataDto buildConfirmedCaseBasis(Disease disease) {

		CaseDataDto caze = buildSuspectCase(disease);
		switch (disease) {
		case YELLOW_FEVER:
			caze.setVaccinationDate(DateHelper.subtractDays(new Date(), 31));
			break;
		default:
			throw new UnsupportedOperationException("Disease has no constant requirement or variation in confirmed definition");
		}

		return caze;
	}

	/**
	 * Creates a sample test for all existing test types except those specified.
	 */
	private void createSampleTestsForAllTestTypesExcept(CaseDataDto caze, Disease testedDisease, PathogenTestType... excludedTests) {
		List<PathogenTestType> excludedTestsList = Arrays.asList(excludedTests);
		for (PathogenTestType testType : PathogenTestType.values()) {
			if (!excludedTestsList.contains(testType)) {
				creator.createPathogenTest(caze, testedDisease, testType, PathogenTestResultType.POSITIVE);
			}
		}
	}
}
