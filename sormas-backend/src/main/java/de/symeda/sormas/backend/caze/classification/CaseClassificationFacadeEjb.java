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
import de.symeda.sormas.api.caze.classification.ClassificationPersonAgeBetweenYearsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationSampleTestCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationSampleTestPositiveResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationSymptomsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto.ClassificationOneOfCompactCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto.ClassificationXOfSubCriteriaDto;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestType;
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
	public CaseClassification getClassification(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		if (criteriaMap.isEmpty()) {
			buildCriteria();
		}

		PersonDto person = personFacade.getPersonByUuid(caze.getPerson().getUuid());
		DiseaseClassificationCriteriaDto criteria = criteriaMap.get(caze.getDisease());

		if (criteria != null && criteria.getConfirmedCriteria() != null
				&& criteria.getConfirmedCriteria().eval(caze, person, sampleTests)) {
			return CaseClassification.CONFIRMED;
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
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				xOf(1, 
						allOfCompact(
								symptom(SymptomsDto.BLOODY_BLACK_STOOL), 
								symptom(SymptomsDto.DIARRHEA)),
						symptom(SymptomsDto.GUMS_BLEEDING),
						symptom(SymptomsDto.SKIN_BRUISING),
						allOfCompact(
								symptom(SymptomsDto.EYES_BLEEDING),
								symptom(SymptomsDto.BLOOD_URINE))));
		probable = allOf(
				caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED), 
				suspect,
				xOf(1, 
						epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE),
						epiData(EpiDataDto.PROCESSING_CONFIRMED_CASE_FLUID_UNSAFE),
						epiData(EpiDataDto.PERCUTANEOUS_CASE_BLOOD),
						allOfCompact(
								epiData(EpiDataDto.AREA_CONFIRMED_CASES),
								epiData(EpiDataDto.DIRECT_CONTACT_DEAD_UNSAFE))));
		confirmed = allOf(
				suspect, 
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.PCR_RT_PCR, SampleTestType.ISOLATION));
		addCriteria(Disease.EVD, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed);

		// CSM
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				xOf(1, 
						symptom(SymptomsDto.NECK_STIFFNESS), 
						symptom(SymptomsDto.ALTERED_CONSCIOUSNESS),
						symptom(SymptomsDto.MENINGEAL_SIGNS),
						symptom(SymptomsDto.BULGING_FONTANELLE)));
		probable = allOf(
				caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
				suspect,
				epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE));
		confirmed = allOf(
				suspect, 
				positiveTestResult(SampleTestType.ISOLATION));
		addCriteria(Disease.CSM, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed);

		// Lassa Fever
		suspect = allOf(
				xOf(1, 
						symptom(SymptomsDto.FATIGUE_WEAKNESS), symptom(SymptomsDto.FEVER), symptom(SymptomsDto.HEADACHE),
						symptom(SymptomsDto.SORE_THROAT), symptom(SymptomsDto.COUGH), symptom(SymptomsDto.NAUSEA),
						symptom(SymptomsDto.VOMITING), symptom(SymptomsDto.DIARRHEA), symptom(SymptomsDto.MUSCLE_PAIN),
						symptom(SymptomsDto.CHEST_PAIN), symptom(SymptomsDto.HEARINGLOSS)),
				epiData(EpiDataDto.RODENTS), 
				xOf(1, 
						epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE),
						epiData(EpiDataDto.DIRECT_CONTACT_PROBABLE_CASE)));
		probable = allOf(
				caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
				suspect);
		confirmed = allOf(
				suspect,
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.PCR_RT_PCR, SampleTestType.ISOLATION));
		addCriteria(Disease.LASSA, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed);

		// Yellow fever
		suspect = allOf(
				symptom(SymptomsDto.FEVER), 
				symptom(SymptomsDto.JAUNDICE));
		probable = allOf(
				suspect,
				xOf(1, 
						epiData(EpiDataDto.AREA_CONFIRMED_CASES),
						allOfCompact(
								caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
								positiveTestResult(SampleTestType.HISTOPATHOLOGY))));
		confirmed = allOf(
				suspect, 
				notInStartDateRange(CaseDataDto.VACCINATION_DATE, 30),
				xOf(1, 
						allOf(positiveTestResult(SampleTestType.YELLOW_FEVER_IGM),
						noneOf(
								positiveTestResult(SampleTestType.WEST_NILE_FEVER_IGM),
								positiveTestResult(SampleTestType.DENGUE_FEVER_IGM))),
						allOf(
								positiveTestResult(SampleTestType.YELLOW_FEVER_ANTIBODIES),
								noneOf(
										positiveTestResult(SampleTestType.WEST_NILE_FEVER_ANTIBODIES),
										positiveTestResult(SampleTestType.DENGUE_FEVER_ANTIBODIES))),
						positiveTestResult(
								SampleTestType.PCR_RT_PCR, SampleTestType.ANTIGEN_DETECTION, SampleTestType.ISOLATION),
						sampleTest(
								SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER,
								Arrays.asList(new SampleTestType[] { SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.IGG_SERUM_ANTIBODY }), true)));
		addCriteria(Disease.YELLOW_FEVER, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed);

		// Dengue fever
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				xOf(2, 
						symptom(SymptomsDto.HEADACHE), symptom(SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE),
						symptom(SymptomsDto.NAUSEA), symptom(SymptomsDto.VOMITING), symptom(SymptomsDto.SWOLLEN_GLANDS),
						allOfCompact(
								symptom(SymptomsDto.MUSCLE_PAIN), 
								symptom(SymptomsDto.JOINT_PAIN)),
						symptom(SymptomsDto.SKIN_RASH)));
		probable = allOf(
				suspect, 
				epiData(EpiDataDto.AREA_CONFIRMED_CASES));
		confirmed = allOf(
				suspect,
				xOf(1,
						allOf(positiveTestResult(SampleTestType.DENGUE_FEVER_IGM),
						noneOf(
								positiveTestResult(SampleTestType.WEST_NILE_FEVER_IGM),
								positiveTestResult(SampleTestType.YELLOW_FEVER_IGM))),
						positiveTestResult(SampleTestType.PCR_RT_PCR), 
						positiveTestResult(SampleTestType.ISOLATION),
						sampleTest(
								SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER,
								Arrays.asList(new SampleTestType[] { SampleTestType.IGG_SERUM_ANTIBODY }), true)));
		addCriteria(Disease.DENGUE, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed);

		// Influenca (new subtype)
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				xOf(1, 
						symptom(SymptomsDto.COUGH), 
						symptom(SymptomsDto.DIFFICULTY_BREATHING)),
				xOf(1, 
						oneOfCompact(epiData(EpiDataDto.CLOSE_CONTACT_PROBABLE_CASE),
						epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE)),
						epiData(EpiDataDto.AREA_INFECTED_ANIMALS),
						epiData(EpiDataDto.EATING_RAW_ANIMALS_IN_INFECTED_AREA),
						epiData(EpiDataDto.PROCESSING_SUSPECTED_CASE_SAMPLE_UNSAFE)));
		probable = allOf(
				suspect,
				caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
				epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE));
		confirmed = allOf(
				suspect,
				xOf(1, 
						positiveTestResult(SampleTestType.ISOLATION, SampleTestType.NEUTRALIZING_ANTIBODIES, SampleTestType.PCR_RT_PCR),
						sampleTest(
								SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER,
								Arrays.asList(new SampleTestType[] { SampleTestType.IGG_SERUM_ANTIBODY }), true)));
		addCriteria(Disease.NEW_INFLUENCA, DateHelper.getDateZero(2018, 12, 13), suspect, probable, confirmed);

		// Measles
		suspect = allOf(
				symptom(SymptomsDto.FEVER), 
				symptom(SymptomsDto.SKIN_RASH), 
				xOf(1, 
						symptom(SymptomsDto.COUGH),
						symptom(SymptomsDto.RUNNY_NOSE),
						symptom(SymptomsDto.CONJUNCTIVITIS)));
		probable = epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE);
		confirmed = allOf(
				suspect, 
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY));
		addCriteria(Disease.MEASLES, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed);

		// Cholera
		suspect = allOf(
				personAgeBetweenYears(5, null),
				xOf(1, 
						symptom(SymptomsDto.DEHYDRATION),
						allOf(
								symptom(SymptomsDto.DIARRHEA),
								xOfSub(1, 
										caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
										epiData(EpiDataDto.AREA_CONFIRMED_CASES)))));
		probable = null;
		confirmed = allOf(
				suspect, 
				positiveTestResult(SampleTestType.ISOLATION));
		addCriteria(Disease.CHOLERA, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed);

		// Monkey pox
		suspect = allOf(
				symptom(SymptomsDto.FEVER), 
				symptom(SymptomsDto.SKIN_RASH));
		probable = null;
		confirmed = allOf(
				suspect, 
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.PCR_RT_PCR, SampleTestType.ISOLATION));
		addCriteria(Disease.MONKEYPOX, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed);

		// Plague
		suspect = allOf(
				xOf(1,
				allOfCompact(
						caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.BUBONIC), 
						symptom(SymptomsDto.FEVER),
						symptom(SymptomsDto.PAINFUL_LYMPHADENITIS)),
				allOf(
						allOfCompact(
								caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.PNEUMONIC), 
								symptom(SymptomsDto.FEVER)),
						xOfSub(1, 
								symptom(SymptomsDto.COUGH), 
								symptom(SymptomsDto.CHEST_PAIN),
								symptom(SymptomsDto.COUGHING_BLOOD))),
				allOfCompact(
						caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.SEPTICAEMIC), 
						symptom(SymptomsDto.FEVER),
						symptom(SymptomsDto.CHILLS_SWEATS))));
		probable = allOf(
				suspect, 
				xOf(1, epiData(EpiDataDto.AREA_CONFIRMED_CASES),
				positiveTestResult(SampleTestType.YERSINIA_PESTIS_ANTIGEN)));
		confirmed = allOf(
				suspect,
				positiveTestResult(SampleTestType.ISOLATION, SampleTestType.PCR_RT_PCR));
		addCriteria(Disease.PLAGUE, DateHelper.getDateZero(2018, 9, 17), suspect, probable, confirmed);
	}

	private void addCriteria(Disease disease, Date changeDate, ClassificationCriteriaDto suspect,
			ClassificationCriteriaDto probable, ClassificationCriteriaDto confirmed) {

		DiseaseClassificationCriteriaDto criteria = new DiseaseClassificationCriteriaDto(disease, changeDate, suspect,
				probable, confirmed);
		criteriaMap.put(disease, criteria);
	}

	private static ClassificationAllOfCriteriaDto allOf(ClassificationCriteriaDto... criteria) {
		return new ClassificationAllOfCriteriaDto(criteria);
	}

	private static ClassificationAllOfCompactCriteriaDto allOfCompact(ClassificationCriteriaDto... criteria) {
		return new ClassificationAllOfCompactCriteriaDto(criteria);
	}

	private static ClassificationXOfCriteriaDto xOf(int requiredAmount, ClassificationCriteriaDto... criteria) {
		return new ClassificationXOfCriteriaDto(requiredAmount, criteria);
	}

	private static ClassificationXOfSubCriteriaDto xOfSub(int requiredAmount, ClassificationCriteriaDto... criteria) {
		return new ClassificationXOfSubCriteriaDto(requiredAmount, criteria);
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

	private static ClassificationSampleTestCriteriaDto sampleTest(String propertyId, List<SampleTestType> testTypes,
			Object... propertyValues) {
		return new ClassificationSampleTestCriteriaDto(propertyId, testTypes, propertyValues);
	}

	private static ClassificationSampleTestPositiveResultCriteriaDto positiveTestResult(
			SampleTestType... sampleTestTypes) {
		return new ClassificationSampleTestPositiveResultCriteriaDto(sampleTestTypes);
	}

	private static ClassificationNotInStartDateRangeCriteriaDto notInStartDateRange(String propertyId,
			int daysBeforeStartDate) {
		return new ClassificationNotInStartDateRangeCriteriaDto(propertyId, daysBeforeStartDate);
	}

	private static ClassificationPersonAgeBetweenYearsCriteriaDto personAgeBetweenYears(Integer lowerYearsThreshold, Integer upperYearsThreshold) {
		return new ClassificationPersonAgeBetweenYearsCriteriaDto(lowerYearsThreshold, upperYearsThreshold);
	}

	@LocalBean
	@Stateless
	public static class CaseClassificationFacadeEjbLocal extends CaseClassificationFacadeEjb {
	}
}
