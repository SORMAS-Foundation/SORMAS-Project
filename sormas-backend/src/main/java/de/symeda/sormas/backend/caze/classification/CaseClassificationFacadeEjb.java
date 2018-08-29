package de.symeda.sormas.backend.caze.classification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.classification.CaseClassificationFacade;
import de.symeda.sormas.api.caze.classification.ClassificationAllOfCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationAllOfCriteria.ClassificationAllOfCompactCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationCaseCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationEpiDataCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationNoneOfCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationNotInStartDateRangeCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationPersonAgeCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationSampleTestCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationSampleTestPositiveResultCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationSymptomsCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteria.ClassificationOneOfCompactCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteria.ClassificationXOfSubCriteria;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.symptoms.SymptomsDto;

@Singleton(name = "CaseClassificationFacade")
public class CaseClassificationFacadeEjb implements CaseClassificationFacade {

	private Map<Disease, ClassificationCriteria> suspectCriterias = new HashMap<Disease, ClassificationCriteria>();
	private Map<Disease, ClassificationCriteria> probableCriterias = new HashMap<Disease, ClassificationCriteria>();
	private Map<Disease, ClassificationCriteria> confirmedCriterias = new HashMap<Disease, ClassificationCriteria>();

	@Override
	public CaseClassification getClassification(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		if (suspectCriterias.isEmpty()) {
			buildCriterias();
		}

		Disease disease = caze.getDisease();

		if (confirmedCriterias.containsKey(disease) && confirmedCriterias.get(disease).eval(caze, sampleTests)) {
			return CaseClassification.CONFIRMED;
		} else if (probableCriterias.containsKey(disease) && probableCriterias.get(disease).eval(caze, sampleTests)) {
			return CaseClassification.PROBABLE;
		} else if (suspectCriterias.containsKey(disease) && suspectCriterias.get(disease).eval(caze, sampleTests)) {
			return CaseClassification.SUSPECT;
		} else {
			return CaseClassification.NOT_CLASSIFIED;
		}
	}
	
	@Override
	public ClassificationCriteria getSuspectCriteria(Disease disease) {
		if (suspectCriterias.isEmpty()) {
			buildCriterias();
		}
		
		if (suspectCriterias.containsKey(disease)) {
			return suspectCriterias.get(disease);
		} else {
			return null;
		}
	}

	@Override
	public ClassificationCriteria getProbableCriteria(Disease disease) {
		if (suspectCriterias.isEmpty()) {
			buildCriterias();
		}

		if (probableCriterias.containsKey(disease)) {
			return probableCriterias.get(disease);
		} else {
			return null;
		}
	}

	@Override
	public ClassificationCriteria getConfirmedCriteria(Disease disease) {
		if (suspectCriterias.isEmpty()) {
			buildCriterias();
		}

		if (confirmedCriterias.containsKey(disease)) {
			return confirmedCriterias.get(disease);
		} else {
			return null;
		}
	}

	private void buildCriterias() {
		ClassificationCriteria suspect, probable, confirmed;

		// EVD
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				xOf(1,
						allOfCompact(
								symptom(SymptomsDto.BLOOD_IN_STOOL), 
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
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.PCR_RT_PCR, SampleTestType.VIRUS_ISOLATION));
		addCriterias(Disease.EVD, suspect, probable, confirmed);

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
				positiveTestResult(SampleTestType.VIRUS_ISOLATION));
		addCriterias(Disease.CSM, suspect, probable, confirmed);

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
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.PCR_RT_PCR, SampleTestType.VIRUS_ISOLATION));
		addCriterias(Disease.LASSA, suspect, probable, confirmed);

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
						allOf(
								positiveTestResult(SampleTestType.YELLOW_FEVER_IGM),
								noneOf(
										positiveTestResult(SampleTestType.WEST_NILE_FEVER_IGM),
										positiveTestResult(SampleTestType.DENGUE_FEVER_IGM))),
						allOf(
								positiveTestResult(SampleTestType.YELLOW_FEVER_ANTIBODIES),
								noneOf(
										positiveTestResult(SampleTestType.WEST_NILE_FEVER_ANTIBODIES),
										positiveTestResult(SampleTestType.DENGUE_FEVER_ANTIBODIES))),
						positiveTestResult(SampleTestType.PCR_RT_PCR, SampleTestType.ANTIGEN_DETECTION, SampleTestType.VIRUS_ISOLATION),
						sampleTest(SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, true)));
		addCriterias(Disease.YELLOW_FEVER, suspect, probable, confirmed);

		// Dengue fever
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				xOf(2, 
						symptom(SymptomsDto.HEADACHE), 
						symptom(SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE),
						symptom(SymptomsDto.NAUSEA), 
						symptom(SymptomsDto.VOMITING), 
						symptom(SymptomsDto.SWOLLEN_GLANDS),
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
						allOf(
								positiveTestResult(SampleTestType.DENGUE_FEVER_IGM),
								noneOf(
										positiveTestResult(SampleTestType.WEST_NILE_FEVER_IGM),
										positiveTestResult(SampleTestType.YELLOW_FEVER_IGM))),
						positiveTestResult(SampleTestType.PCR_RT_PCR),
						sampleTest(SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, true)));
		addCriterias(Disease.DENGUE, suspect, probable, confirmed);

		// Influenca (new subtype)
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				xOf(1,
						symptom(SymptomsDto.COUGH), 
						symptom(SymptomsDto.DIFFICULTY_BREATHING)),
				xOf(1,
						oneOfCompact(
								epiData(EpiDataDto.CLOSE_CONTACT_PROBABLE_CASE),
								epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE)), 
						epiData(EpiDataDto.AREA_INFECTED_ANIMALS),
						epiData(EpiDataDto.EATING_RAW_ANIMALS_IN_INFECTED_AREA),
						epiData(EpiDataDto.PROCESSING_SUSPECTED_CASE_SAMPLE_UNSAFE)));
		probable = allOf(
				suspect, 
				caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
				epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE));
		confirmed = allOf(suspect,
				xOf(1, 
						positiveTestResult(SampleTestType.VIRUS_ISOLATION, SampleTestType.PCR_RT_PCR),
						sampleTest(SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, true)));
		addCriterias(Disease.NEW_INFLUENCA, suspect, probable, confirmed);

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
		addCriterias(Disease.MEASLES, suspect, probable, confirmed);

		// Cholera
		suspect = allOf(
				personAge(5, null, ApproximateAgeType.YEARS), 
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
				positiveTestResult(SampleTestType.VIRUS_ISOLATION));
		addCriterias(Disease.CHOLERA, suspect, probable, confirmed);

		// Monkey pox
		suspect = allOf(
				symptom(SymptomsDto.FEVER), 
				symptom(SymptomsDto.SKIN_RASH));
		probable = null;
		confirmed = allOf(
				suspect, 
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.PCR_RT_PCR, SampleTestType.VIRUS_ISOLATION));
		addCriterias(Disease.MONKEYPOX, suspect, probable, confirmed);

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
				xOf(1,
						epiData(EpiDataDto.AREA_CONFIRMED_CASES),
						positiveTestResult(SampleTestType.YERSINIA_PESTIS_ANTIGEN)));
		confirmed = allOf(
				suspect, 
				positiveTestResult(SampleTestType.VIRUS_ISOLATION, SampleTestType.PCR_RT_PCR));
		addCriterias(Disease.PLAGUE, suspect, probable, confirmed);
	}

	private void addCriterias(Disease disease, ClassificationCriteria suspect, ClassificationCriteria probable,
			ClassificationCriteria confirmed) {
		if (suspect != null) {
			suspectCriterias.put(disease, suspect);
		}
		if (probable != null) {
			probableCriterias.put(disease, probable);
		}
		if (confirmed != null) {
			confirmedCriterias.put(disease, confirmed);
		}
	}

	private static ClassificationAllOfCriteria allOf(ClassificationCriteria... criterias) {
		return new ClassificationAllOfCriteria(criterias);
	}
	
	private static ClassificationAllOfCompactCriteria allOfCompact(ClassificationCriteria... criterias) {
		return new ClassificationAllOfCompactCriteria(criterias);
	}
	
	private static ClassificationXOfCriteria xOf(int requiredAmount, ClassificationCriteria... criterias) {
		return new ClassificationXOfCriteria(requiredAmount, criterias);
	}
	
	private static ClassificationXOfSubCriteria xOfSub(int requiredAmount, ClassificationCriteria... criterias) {
		return new ClassificationXOfSubCriteria(requiredAmount, criterias);
	}
	
	private static ClassificationOneOfCompactCriteria oneOfCompact(ClassificationCriteria... criterias) {
		return new ClassificationOneOfCompactCriteria(criterias);
	}

	private static ClassificationNoneOfCriteria noneOf(ClassificationCriteria... criterias) {
		return new ClassificationNoneOfCriteria(criterias);
	}
	
	private static ClassificationCaseCriteria caseData(String propertyId, Object... propertyValues) {
		return new ClassificationCaseCriteria(propertyId, propertyValues);
	}

	private static ClassificationSymptomsCriteria symptom(String propertyId) {
		return new ClassificationSymptomsCriteria(propertyId);
	}

	private static ClassificationEpiDataCriteria epiData(String propertyId) {
		return new ClassificationEpiDataCriteria(propertyId);
	}

	private static ClassificationSampleTestCriteria sampleTest(String propertyId, Object... propertyValues) {
		return new ClassificationSampleTestCriteria(propertyId, propertyValues);
	}

	private static ClassificationSampleTestPositiveResultCriteria positiveTestResult(SampleTestType... sampleTestTypes) {
		return new ClassificationSampleTestPositiveResultCriteria(sampleTestTypes);
	}

	private static ClassificationNotInStartDateRangeCriteria notInStartDateRange(String propertyId, int daysBeforeStartDate) {
		return new ClassificationNotInStartDateRangeCriteria(propertyId, daysBeforeStartDate);
	}

	private static ClassificationPersonAgeCriteria personAge(Integer lowerThreshold, Integer upperThreshold, ApproximateAgeType ageType) {
		return new ClassificationPersonAgeCriteria(lowerThreshold, upperThreshold, ageType);
	}
	
}
