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
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.backend.caze.classification.AllOfCriteria.AllOfSubCriteria;

@Singleton
public class CaseClassificationLogic {

	private Map<Disease, Criteria> suspectCriterias = new HashMap<Disease, Criteria>();
	private Map<Disease, Criteria> probableCriterias = new HashMap<Disease, Criteria>();
	private Map<Disease, Criteria> confirmedCriterias = new HashMap<Disease, Criteria>();
	
	
	public CaseClassification getClassification(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		if (suspectCriterias.isEmpty()) {
			buildCriterias();
		}
		
		Disease disease = caze.getDisease();
		
		if (confirmedCriterias.containsKey(disease)
				&& confirmedCriterias.get(disease).eval(caze, sampleTests)) {
			return CaseClassification.CONFIRMED;
		} else if (probableCriterias.containsKey(disease)
				&& probableCriterias.get(disease).eval(caze, sampleTests)) {
			return CaseClassification.PROBABLE;
		} else if (suspectCriterias.containsKey(disease)
				&& suspectCriterias.get(disease).eval(caze, sampleTests)) {
			return CaseClassification.SUSPECT;
		} else {
			return CaseClassification.NOT_CLASSIFIED;
		}
	}

	public String getClassificationDescription(Disease disease, CaseClassification caseClassification) {
		if (suspectCriterias.isEmpty()) {
			buildCriterias();
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		switch (caseClassification) {
		case CONFIRMED:
			if (confirmedCriterias.containsKey(disease)) {
				confirmedCriterias.get(disease).appendDesc(stringBuilder);
			}
			break;
		case PROBABLE:
			if (probableCriterias.containsKey(disease)) {
				probableCriterias.get(disease).appendDesc(stringBuilder);
			}
			break;
		case SUSPECT:
			if (suspectCriterias.containsKey(disease)) {
				suspectCriterias.get(disease).appendDesc(stringBuilder);
			}
			break;
		case NOT_CLASSIFIED:
		case NO_CASE:
			break;
		default:
			throw new IllegalArgumentException(caseClassification.toString());
		}
		
		return stringBuilder.toString();
	}

	private void buildCriterias() {
		Criteria suspect, probable, confirmed;
		
		// EVD
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				oneOf(
						allOfSub(symptom(SymptomsDto.BLOOD_IN_STOOL), symptom(SymptomsDto.DIARRHEA)),
						symptom(SymptomsDto.GUMS_BLEEDING), 
						symptom(SymptomsDto.SKIN_BRUISING),
						allOfSub(symptom(SymptomsDto.EYES_BLEEDING), symptom(SymptomsDto.BLOOD_URINE))));
		probable = allOf(
				caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED), 
				suspect, 
				oneOf(
						epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE),
						epiData(EpiDataDto.PROCESSING_CONFIRMED_CASE_FLUID_UNSAFE), 
						epiData(EpiDataDto.PERCUTANEOUS_CASE_BLOOD),
						allOfSub(epiData(EpiDataDto.AREA_CONFIRMED_CASES), epiData(EpiDataDto.DIRECT_CONTACT_DEAD_UNSAFE))));
		confirmed = allOf(
				suspect, 
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.PCR_RT_PCR, SampleTestType.VIRUS_ISOLATION));
		addCriterias(Disease.EVD, suspect, probable, confirmed);
		
		// CSM
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				oneOf(
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
				oneOf(
						symptom(SymptomsDto.FEVER),
						symptom(SymptomsDto.HEADACHE),
						symptom(SymptomsDto.SORE_THROAT),
						symptom(SymptomsDto.COUGH),
						symptom(SymptomsDto.NAUSEA),
						symptom(SymptomsDto.VOMITING),
						symptom(SymptomsDto.DIARRHEA),
						symptom(SymptomsDto.MUSCLE_PAIN),
						symptom(SymptomsDto.CHEST_PAIN),
						symptom(SymptomsDto.HEARINGLOSS)),
				allOf(
						epiData(EpiDataDto.RODENTS),
						oneOf(
								epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE),
								epiData(EpiDataDto.DIRECT_CONTACT_PROBABLE_CASE))));
		probable = allOf(
				caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
				suspect);
		confirmed = allOf(
				suspect,
				oneOf(
						positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY),
						positiveTestResult(SampleTestType.PCR_RT_PCR),
						positiveTestResult(SampleTestType.VIRUS_ISOLATION)));
		addCriterias(Disease.LASSA, suspect, probable, confirmed);
		
		// Yellow fever
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				symptom(SymptomsDto.JAUNDICE));
		probable = allOf(
				suspect,
				oneOf(
						epiData(EpiDataDto.AREA_CONFIRMED_CASES),
						allOfSub(
								caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
								positiveTestResult(SampleTestType.HISTOPATHOLOGY))));
		confirmed = allOf(
				suspect,
				oneOf(
						allOfSub(
								positiveTestResult(SampleTestType.YELLOW_FEVER_IGM),
								noneOf(
										positiveTestResult(SampleTestType.WEST_NILE_FEVER_IGM),
										positiveTestResult(SampleTestType.DENGUE_FEVER_IGM))),
						allOfSub(
								positiveTestResult(SampleTestType.YELLOW_FEVER_ANTIBODIES),
								noneOf(
										positiveTestResult(SampleTestType.WEST_NILE_FEVER_ANTIBODIES),
										positiveTestResult(SampleTestType.DENGUE_FEVER_ANTIBODIES))),
						positiveTestResult(SampleTestType.PCR_RT_PCR),
						positiveTestResult(SampleTestType.ANTIGEN_DETECTION),
						positiveTestResult(SampleTestType.VIRUS_ISOLATION),
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
						allOfSub(symptom(SymptomsDto.MUSCLE_PAIN), symptom(SymptomsDto.JOINT_PAIN)),
						symptom(SymptomsDto.SKIN_RASH)));
		probable = allOf(
				suspect,
				epiData(EpiDataDto.AREA_CONFIRMED_CASES));
		confirmed = allOf(
				suspect,
				oneOf(
						allOfSub(
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
				oneOf(
						symptom(SymptomsDto.COUGH),
						symptom(SymptomsDto.DIFFICULTY_BREATHING)),
				oneOf(
						oneOf(epiData(EpiDataDto.CLOSE_CONTACT_PROBABLE_CASE), epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE)),
						epiData(EpiDataDto.AREA_INFECTED_ANIMALS),
						epiData(EpiDataDto.EATING_RAW_ANIMALS_IN_INFECTED_AREA),
						epiData(EpiDataDto.PROCESSING_SUSPECTED_CASE_SAMPLE_UNSAFE)));
		probable = allOf(
				suspect,
				caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
				epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE));
		confirmed = allOf(
				suspect,
				oneOf(
						positiveTestResult(SampleTestType.VIRUS_ISOLATION),
						positiveTestResult(SampleTestType.PCR_RT_PCR),
						sampleTest(SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, true)));
		addCriterias(Disease.NEW_INFLUENCA, suspect, probable, confirmed);
				
		// Measles
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				symptom(SymptomsDto.SKIN_RASH),
				oneOf(
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
				oneOf(
						symptom(SymptomsDto.DEHYDRATION),
						allOfSub(
								symptom(SymptomsDto.DIARRHEA),
								oneOf(
										caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
										epiData(EpiDataDto.AREA_CONFIRMED_CASES)))));
		probable = null;
		confirmed = allOf(
				suspect,
				positiveTestResult(SampleTestType.VIRUS_ISOLATION));
		addCriterias(Disease.CHOLERA, suspect, probable, confirmed);
		
		// Monkey pox
		suspect = allOf(symptom(SymptomsDto.FEVER), symptom(SymptomsDto.SKIN_RASH));
		probable = null;
		confirmed = allOf(
				suspect,
				oneOf(
						positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY),
						positiveTestResult(SampleTestType.PCR_RT_PCR),
						positiveTestResult(SampleTestType.VIRUS_ISOLATION)));
		addCriterias(Disease.MONKEYPOX, suspect, probable, confirmed);
		
		// Plague
		suspect = oneOf(
				allOf(
						caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.BUBONIC),
						symptom(SymptomsDto.FEVER),
						symptom(SymptomsDto.PAINFUL_LYMPHADENITIS)),
				allOf(
						caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.PNEUMONIC),
						symptom(SymptomsDto.FEVER),
						oneOf(
								symptom(SymptomsDto.COUGH),
								symptom(SymptomsDto.CHEST_PAIN))),
				allOf(
						caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.SEPTICAEMIC),
						symptom(SymptomsDto.FEVER),
						symptom(SymptomsDto.CHILLS_SWEATS)));
		probable = allOf(
				suspect,
				oneOf(
						epiData(EpiDataDto.AREA_CONFIRMED_CASES),
						positiveTestResult(SampleTestType.YERSINIA_PESTIS_ANTIGEN)));
		confirmed = allOf(
				suspect,
				oneOf(
						positiveTestResult(SampleTestType.VIRUS_ISOLATION),
						positiveTestResult(SampleTestType.PCR_RT_PCR)));
		addCriterias(Disease.PLAGUE, suspect, probable, confirmed);
	}
	
	private void addCriterias(Disease disease, Criteria suspect, Criteria probable, Criteria confirmed) {
		suspectCriterias.put(disease, suspect);
		probableCriterias.put(disease, probable);
		confirmedCriterias.put(disease, confirmed);
	}

	private static AllOfCriteria allOf(Criteria... criterias) {
		return new AllOfCriteria(criterias);
	}

	private static OneOfCriteria oneOf(Criteria... criterias) {
		return new OneOfCriteria(criterias);
	}

	private static AllOfSubCriteria allOfSub(Criteria... criterias) {
		return new AllOfSubCriteria(criterias);
	}
	
	private static NoneOfCriteria noneOf(Criteria... criterias) {
		return new NoneOfCriteria(criterias);
	}
	
	private static XOfCriteria xOf(int requiredAmount, Criteria... criterias) {
		return new XOfCriteria(requiredAmount, criterias);
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

	private static SampleTestPositiveResultCriteria positiveTestResult(SampleTestType... sampleTestTypes) {
		return new SampleTestPositiveResultCriteria(sampleTestTypes);
	}
	
	private static CasePersonAgeCriteria personAge(Integer lowerThreshold, Integer upperThreshold, ApproximateAgeType ageType) {
		return new CasePersonAgeCriteria(lowerThreshold, upperThreshold, ageType);
	}
	
}
