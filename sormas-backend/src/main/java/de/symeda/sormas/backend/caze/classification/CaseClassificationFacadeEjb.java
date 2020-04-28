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
package de.symeda.sormas.backend.caze.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.classification.CaseClassificationFacade;
import de.symeda.sormas.api.caze.classification.ClassificationAllOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationAllOfCriteriaDto.ClassificationAllOfCompactCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationCaseCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationEpiDataCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationNoneOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationNotInStartDateRangeCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestNegativeResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestOtherPositiveResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestPositiveResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPersonAgeBetweenYearsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationSymptomsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto.ClassificationOneOfCompactCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto.ClassificationXOfSubCriteriaDto;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;

/**
 * Stateless instead of Singleton. It's ok to have multiple instances with an
 * individual cache.
 * 
 * @author Martin Wahnschaffe
 */
@Stateless(name = "CaseClassificationFacade")
public class CaseClassificationFacadeEjb implements CaseClassificationFacade {

	@EJB
	private PersonFacadeEjbLocal personFacade;

	/** local cache */
	private Map<Disease, DiseaseClassificationCriteriaDto> criteriaMap = new HashMap<>();

	@Override
	public CaseClassification getClassification(CaseDataDto caze, List<PathogenTestDto> sampleTests) {
		if (criteriaMap.isEmpty()) {
			buildCriteria();
		}

		PersonDto person = personFacade.getPersonByUuid(caze.getPerson().getUuid());
		DiseaseClassificationCriteriaDto criteria = criteriaMap.get(caze.getDisease());

		if (criteria != null && criteria.getConfirmedCriteria() != null
				&& criteria.getConfirmedCriteria().eval(caze, person, sampleTests)) {
			return CaseClassification.CONFIRMED;
		} else if (criteria != null && criteria.getNotACaseCriteria() != null
				&& criteria.getNotACaseCriteria().eval(caze, person, sampleTests)) {
			return CaseClassification.NO_CASE;
		} else if (criteria != null && criteria.getProbableCriteria() != null
				&& criteria.getProbableCriteria().eval(caze, person, sampleTests)) {
			return CaseClassification.PROBABLE;
		} else if (criteria != null && criteria.getSuspectCriteria() != null
				&& criteria.getSuspectCriteria().eval(caze, person, sampleTests)) {
			return CaseClassification.SUSPECT;
		} else {
			return CaseClassification.NOT_CLASSIFIED;
		}
	}

	@Override
	public List<DiseaseClassificationCriteriaDto> getAllSince(Date changeDate) {
		if (criteriaMap.isEmpty()) {
			buildCriteria();
		}

		List<DiseaseClassificationCriteriaDto> results = new ArrayList<>();
		for (DiseaseClassificationCriteriaDto criteria : criteriaMap.values()) {
			if (criteria.getChangeDate().after(changeDate)) {
				results.add(criteria);
			}
		}

		return results;
	}

	@Override
	public DiseaseClassificationCriteriaDto getByDisease(Disease disease) {
		if (criteriaMap.isEmpty()) {
			buildCriteria();
		}

		if (criteriaMap.containsKey(disease)) {
			return criteriaMap.get(disease);
		} else {
			return null;
		}
	}

	private void buildCriteria() {
		ClassificationCriteriaDto suspect, probable, confirmed;
		
		// EVD
		suspect = allOf(symptom(SymptomsDto.FEVER),
				xOf(1, allOfCompact(symptom(SymptomsDto.BLOODY_BLACK_STOOL), symptom(SymptomsDto.DIARRHEA)),
						symptom(SymptomsDto.GUMS_BLEEDING), symptom(SymptomsDto.SKIN_BRUISING),
						allOfCompact(symptom(SymptomsDto.EYES_BLEEDING), symptom(SymptomsDto.BLOOD_URINE))));
		probable = allOf(caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED), suspect,
				xOf(1, epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE),
						epiData(EpiDataDto.PROCESSING_CONFIRMED_CASE_FLUID_UNSAFE),
						epiData(EpiDataDto.PERCUTANEOUS_CASE_BLOOD),
						allOfCompact(epiData(EpiDataDto.AREA_CONFIRMED_CASES),
								epiData(EpiDataDto.DIRECT_CONTACT_DEAD_UNSAFE))));
		confirmed = allOf(suspect, positiveTestResult(Disease.EVD, PathogenTestType.IGM_SERUM_ANTIBODY,
				PathogenTestType.PCR_RT_PCR, PathogenTestType.ISOLATION));

		addCriteria(Disease.EVD, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed,
				extracted(Disease.EVD));

		// CSM
		suspect = allOf(symptom(SymptomsDto.FEVER),
				xOf(1, symptom(SymptomsDto.NECK_STIFFNESS), symptom(SymptomsDto.ALTERED_CONSCIOUSNESS),
						symptom(SymptomsDto.MENINGEAL_SIGNS), symptom(SymptomsDto.BULGING_FONTANELLE)));
		probable = allOf(caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED), suspect,
				epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE));
		confirmed = allOf(suspect, positiveTestResult(Disease.CSM, PathogenTestType.ISOLATION));
		addCriteria(Disease.CSM, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed,
				extracted(Disease.CSM));

		// Lassa Fever
		suspect = allOf(
				xOf(1, symptom(SymptomsDto.FATIGUE_WEAKNESS), symptom(SymptomsDto.FEVER), symptom(SymptomsDto.HEADACHE),
						symptom(SymptomsDto.SORE_THROAT), symptom(SymptomsDto.COUGH), symptom(SymptomsDto.NAUSEA),
						symptom(SymptomsDto.VOMITING), symptom(SymptomsDto.DIARRHEA), symptom(SymptomsDto.MUSCLE_PAIN),
						symptom(SymptomsDto.CHEST_PAIN), symptom(SymptomsDto.HEARINGLOSS)),
				epiData(EpiDataDto.RODENTS), xOf(1, epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE),
						epiData(EpiDataDto.DIRECT_CONTACT_PROBABLE_CASE)));
		probable = allOf(caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED), suspect);
		confirmed = allOf(suspect, positiveTestResult(Disease.LASSA, PathogenTestType.IGM_SERUM_ANTIBODY,
				PathogenTestType.PCR_RT_PCR, PathogenTestType.ISOLATION));
		addCriteria(Disease.LASSA, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed,
				extracted(Disease.LASSA));

		// Yellow fever
		suspect = allOf(symptom(SymptomsDto.FEVER), symptom(SymptomsDto.JAUNDICE));
		probable = allOf(suspect,
				xOf(1, epiData(EpiDataDto.AREA_CONFIRMED_CASES),
						allOfCompact(caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
								positiveTestResult(Disease.YELLOW_FEVER, PathogenTestType.HISTOPATHOLOGY))));
		confirmed = allOf(suspect, notInStartDateRange(CaseDataDto.VACCINATION_DATE, 30), xOf(1,
				allOf(positiveTestResult(Disease.YELLOW_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY),
						noneOf(positiveTestResult(Disease.WEST_NILE_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY),
								positiveTestResult(Disease.DENGUE, PathogenTestType.IGM_SERUM_ANTIBODY))),
				allOf(positiveTestResult(Disease.YELLOW_FEVER, PathogenTestType.NEUTRALIZING_ANTIBODIES),
						noneOf(positiveTestResult(Disease.WEST_NILE_FEVER, PathogenTestType.NEUTRALIZING_ANTIBODIES),
								positiveTestResult(Disease.DENGUE, PathogenTestType.NEUTRALIZING_ANTIBODIES))),
				positiveTestResult(Disease.YELLOW_FEVER, PathogenTestType.PCR_RT_PCR,
						PathogenTestType.ANTIGEN_DETECTION, PathogenTestType.ISOLATION),
				sampleTest(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, Arrays.asList(new PathogenTestType[] {
						PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestType.IGG_SERUM_ANTIBODY }), true)));
		addCriteria(Disease.YELLOW_FEVER, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed,
				extracted(Disease.YELLOW_FEVER));

		// Dengue fever
		suspect = allOf(symptom(SymptomsDto.FEVER),
				xOf(2, symptom(SymptomsDto.HEADACHE), symptom(SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE),
						symptom(SymptomsDto.NAUSEA), symptom(SymptomsDto.VOMITING), symptom(SymptomsDto.SWOLLEN_GLANDS),
						allOfCompact(symptom(SymptomsDto.MUSCLE_PAIN), symptom(SymptomsDto.JOINT_PAIN)),
						symptom(SymptomsDto.SKIN_RASH)));
		probable = allOf(suspect, epiData(EpiDataDto.AREA_CONFIRMED_CASES));
		confirmed = allOf(suspect,
				xOf(1, allOf(positiveTestResult(Disease.DENGUE, PathogenTestType.IGM_SERUM_ANTIBODY),
						noneOf(positiveTestResult(Disease.WEST_NILE_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY),
								positiveTestResult(Disease.YELLOW_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY))),
						positiveTestResult(Disease.DENGUE, PathogenTestType.PCR_RT_PCR),
						positiveTestResult(Disease.DENGUE, PathogenTestType.ISOLATION),
						sampleTest(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER,
								Arrays.asList(new PathogenTestType[] { PathogenTestType.IGG_SERUM_ANTIBODY }), true)));
		addCriteria(Disease.DENGUE, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed,
				extracted(Disease.DENGUE));

		// Influenza (new subtype)
		suspect = allOf(symptom(SymptomsDto.FEVER),
				xOf(1, symptom(SymptomsDto.COUGH), symptom(SymptomsDto.DIFFICULTY_BREATHING)),
				xOf(1, oneOfCompact(epiData(EpiDataDto.CLOSE_CONTACT_PROBABLE_CASE),
						epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE)), epiData(EpiDataDto.AREA_INFECTED_ANIMALS),
						epiData(EpiDataDto.EATING_RAW_ANIMALS_IN_INFECTED_AREA),
						epiData(EpiDataDto.PROCESSING_SUSPECTED_CASE_SAMPLE_UNSAFE)));
		probable = allOf(suspect, caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
				epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE));
		confirmed = allOf(suspect,
				xOf(1, positiveTestResult(Disease.NEW_INFLUENZA, PathogenTestType.ISOLATION,
						PathogenTestType.NEUTRALIZING_ANTIBODIES, PathogenTestType.PCR_RT_PCR),
						sampleTest(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER,
								Arrays.asList(new PathogenTestType[] { PathogenTestType.IGG_SERUM_ANTIBODY }), true)));
		addCriteria(Disease.NEW_INFLUENZA, DateHelper.getDateZero(2018, 12, 13), suspect, probable, confirmed,
				extracted(Disease.NEW_INFLUENZA));

		// Measles
		suspect = allOf(symptom(SymptomsDto.FEVER), symptom(SymptomsDto.SKIN_RASH), xOf(1, symptom(SymptomsDto.COUGH),
				symptom(SymptomsDto.RUNNY_NOSE), symptom(SymptomsDto.CONJUNCTIVITIS)));
		probable = epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE);
		confirmed = allOf(suspect, positiveTestResult(Disease.MEASLES, PathogenTestType.IGM_SERUM_ANTIBODY));
		addCriteria(Disease.MEASLES, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed,
				extracted(Disease.MEASLES));

		// Cholera
		suspect = allOf(personAgeBetweenYears(5, null),
				xOf(1, symptom(SymptomsDto.DEHYDRATION),
						allOfCompact(symptom(SymptomsDto.DIARRHEA),
								oneOfCompact(caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
										epiData(EpiDataDto.AREA_CONFIRMED_CASES)))));
		probable = null;
		confirmed = allOf(suspect, positiveTestResult(Disease.CHOLERA, PathogenTestType.ISOLATION));
		addCriteria(Disease.CHOLERA, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed,
				extracted(Disease.CHOLERA));

		// Monkey pox
		suspect = allOf(symptom(SymptomsDto.FEVER), symptom(SymptomsDto.SKIN_RASH));
		probable = null;
		confirmed = allOf(suspect, positiveTestResult(Disease.MONKEYPOX, PathogenTestType.IGM_SERUM_ANTIBODY,
				PathogenTestType.PCR_RT_PCR, PathogenTestType.ISOLATION));
		addCriteria(Disease.MONKEYPOX, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed,
				extracted(Disease.MONKEYPOX));

		// Plague
		suspect = allOf(xOf(1,
				allOfCompact(caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.BUBONIC), symptom(SymptomsDto.FEVER),
						symptom(SymptomsDto.PAINFUL_LYMPHADENITIS)),
				allOf(allOfCompact(caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.PNEUMONIC), symptom(SymptomsDto.FEVER)),
						xOfSub(1, true, symptom(SymptomsDto.COUGH), symptom(SymptomsDto.CHEST_PAIN),
								symptom(SymptomsDto.COUGHING_BLOOD))),
				allOfCompact(caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.SEPTICAEMIC), symptom(SymptomsDto.FEVER),
						symptom(SymptomsDto.CHILLS_SWEATS))));
		probable = allOf(suspect, xOf(1, epiData(EpiDataDto.AREA_CONFIRMED_CASES),
				positiveTestResult(Disease.PLAGUE, PathogenTestType.ANTIGEN_DETECTION)));
		confirmed = allOf(suspect,
				positiveTestResult(Disease.PLAGUE, PathogenTestType.ISOLATION, PathogenTestType.PCR_RT_PCR));
		addCriteria(Disease.PLAGUE, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed,
				extracted(Disease.PLAGUE));

		// Congenital rubella
		suspect = allOf(xOf(1, symptom(SymptomsDto.BILATERAL_CATARACTS), symptom(SymptomsDto.UNILATERAL_CATARACTS),
				symptom(SymptomsDto.CONGENITAL_GLAUCOMA), symptom(SymptomsDto.CONGENITAL_HEART_DISEASE),
				symptom(SymptomsDto.HEARINGLOSS), symptom(SymptomsDto.PIGMENTARY_RETINOPATHY),
				symptom(SymptomsDto.PURPURIC_RASH), symptom(SymptomsDto.SPLENOMEGALY), symptom(SymptomsDto.JAUNDICE),
				symptom(SymptomsDto.MICROCEPHALY), symptom(SymptomsDto.DEVELOPMENTAL_DELAY),
				symptom(SymptomsDto.MENINGOENCEPHALITIS), symptom(SymptomsDto.RADIOLUCENT_BONE_DISEASE)));
		probable = xOf(1,
				xOf(2, oneOfCompact(symptom(SymptomsDto.BILATERAL_CATARACTS), symptom(SymptomsDto.UNILATERAL_CATARACTS),
						symptom(SymptomsDto.CONGENITAL_GLAUCOMA)), symptom(SymptomsDto.CONGENITAL_HEART_DISEASE),
						symptom(SymptomsDto.HEARINGLOSS), symptom(SymptomsDto.PIGMENTARY_RETINOPATHY)),
				allOfTogether(
						xOfSub(1, false, oneOfCompact(symptom(SymptomsDto.BILATERAL_CATARACTS),
								symptom(SymptomsDto.UNILATERAL_CATARACTS), symptom(SymptomsDto.CONGENITAL_GLAUCOMA)),
								symptom(SymptomsDto.CONGENITAL_HEART_DISEASE), symptom(SymptomsDto.HEARINGLOSS),
								symptom(SymptomsDto.PIGMENTARY_RETINOPATHY)),
						xOfSub(1, false, symptom(SymptomsDto.PURPURIC_RASH), symptom(SymptomsDto.SPLENOMEGALY),
								symptom(SymptomsDto.MICROCEPHALY), symptom(SymptomsDto.DEVELOPMENTAL_DELAY),
								symptom(SymptomsDto.MENINGOENCEPHALITIS),
								symptom(SymptomsDto.RADIOLUCENT_BONE_DISEASE))));
		confirmed = allOf(suspect, positiveTestResult(Disease.CONGENITAL_RUBELLA, PathogenTestType.ISOLATION,
				PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestType.PCR_RT_PCR));
		addCriteria(Disease.CONGENITAL_RUBELLA, DateHelper.getDateZero(2019, 6, 3), suspect, probable, confirmed,
				extracted(Disease.CONGENITAL_RUBELLA));
	}

	private ClassificationAllOfCriteriaDto extracted(Disease disease) {
		return allOf(negativeTestResult(disease), otherPositiveTestResult(disease));
	}

	private void addCriteria(Disease disease, Date changeDate, ClassificationCriteriaDto suspect,
			ClassificationCriteriaDto probable, ClassificationCriteriaDto confirmed,
			ClassificationCriteriaDto notACase) {

		DiseaseClassificationCriteriaDto criteria = new DiseaseClassificationCriteriaDto(disease, changeDate, suspect,
				probable, confirmed, notACase);
		criteriaMap.put(disease, criteria);
	}

	private static ClassificationAllOfCriteriaDto allOf(ClassificationCriteriaDto... criteria) {
		return new ClassificationAllOfCriteriaDto(criteria);
	}

	private static ClassificationAllOfCriteriaDto allOfTogether(ClassificationCriteriaDto... criteria) {
		return new ClassificationAllOfCriteriaDto(true, criteria);
	}

	private static ClassificationAllOfCompactCriteriaDto allOfCompact(ClassificationCriteriaDto... criteria) {
		return new ClassificationAllOfCompactCriteriaDto(criteria);
	}

	private static ClassificationXOfCriteriaDto xOf(int requiredAmount, ClassificationCriteriaDto... criteria) {
		return new ClassificationXOfCriteriaDto(requiredAmount, criteria);
	}

	private static ClassificationXOfSubCriteriaDto xOfSub(int requiredAmount, boolean isAddition,
			ClassificationCriteriaDto... criteria) {
		return new ClassificationXOfSubCriteriaDto(requiredAmount, isAddition, criteria);
	}

	private static ClassificationOneOfCompactCriteriaDto oneOfCompact(ClassificationCriteriaDto... criteria) {
		return new ClassificationOneOfCompactCriteriaDto(criteria);
	}

	private static ClassificationNoneOfCriteriaDto noneOf(ClassificationCriteriaDto... criteria) {
		return new ClassificationNoneOfCriteriaDto(criteria);
	}

	private static ClassificationCaseCriteriaDto caseData(String propertyId, Object... propertyValues) {
		return new ClassificationCaseCriteriaDto(propertyId, propertyValues);
	}

	private static ClassificationSymptomsCriteriaDto symptom(String propertyId) {
		return new ClassificationSymptomsCriteriaDto(propertyId);
	}

	private static ClassificationEpiDataCriteriaDto epiData(String propertyId) {
		return new ClassificationEpiDataCriteriaDto(propertyId);
	}

	private static ClassificationPathogenTestCriteriaDto sampleTest(String propertyId, List<PathogenTestType> testTypes,
			Object... propertyValues) {
		return new ClassificationPathogenTestCriteriaDto(propertyId, testTypes, propertyValues);
	}

	private static ClassificationPathogenTestNegativeResultCriteriaDto negativeTestResult(Disease testedDisease) {
		return new ClassificationPathogenTestNegativeResultCriteriaDto(testedDisease);
	}

	private static ClassificationPathogenTestPositiveResultCriteriaDto positiveTestResult(Disease testedDisease,
			PathogenTestType... pathogenTestTypes) {
		return new ClassificationPathogenTestPositiveResultCriteriaDto(testedDisease, pathogenTestTypes);
	}

	private static ClassificationPathogenTestOtherPositiveResultCriteriaDto otherPositiveTestResult(
			Disease testedDisease) {
		return new ClassificationPathogenTestOtherPositiveResultCriteriaDto(testedDisease);
	}

	private static ClassificationNotInStartDateRangeCriteriaDto notInStartDateRange(String propertyId,
			int daysBeforeStartDate) {
		return new ClassificationNotInStartDateRangeCriteriaDto(propertyId, daysBeforeStartDate);
	}

	private static ClassificationPersonAgeBetweenYearsCriteriaDto personAgeBetweenYears(Integer lowerYearsThreshold,
			Integer upperYearsThreshold) {
		return new ClassificationPersonAgeBetweenYearsCriteriaDto(lowerYearsThreshold, upperYearsThreshold);
	}

	@LocalBean
	@Stateless
	public static class CaseClassificationFacadeEjbLocal extends CaseClassificationFacadeEjb {
	}
}
