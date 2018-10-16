package de.symeda.sormas.backend.caze.classification;

import java.util.Arrays;
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
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;

/**
 * Stateless instead of Singleton. It's ok to have multiple instances with an individual cache. 
 * 
 * @author Martin Wahnschaffe
 */
@Stateless(name = "CaseClassificationFacade")
public class CaseClassificationFacadeEjb implements CaseClassificationFacade {
	
	@EJB
	private PersonFacadeEjbLocal personFacade;
	
	/** local cache */
	private Map<Disease, ClassificationCriteria> suspectCriteria = new HashMap<>();
	private Map<Disease, ClassificationCriteria> probableCriteria = new HashMap<>();
	private Map<Disease, ClassificationCriteria> confirmedCriteria = new HashMap<>();

	@Override
	public CaseClassification getClassification(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		if (suspectCriteria.isEmpty()) {
			buildCriteria();
		}

		Disease disease = caze.getDisease();
		PersonDto person = personFacade.getPersonByUuid(caze.getPerson().getUuid());
		
		if (confirmedCriteria.containsKey(disease) && confirmedCriteria.get(disease).eval(caze, person, sampleTests)) {
			return CaseClassification.CONFIRMED;
		} else if (probableCriteria.containsKey(disease) && probableCriteria.get(disease).eval(caze, person, sampleTests)) {
			return CaseClassification.PROBABLE;
		} else if (suspectCriteria.containsKey(disease) && suspectCriteria.get(disease).eval(caze, person, sampleTests)) {
			return CaseClassification.SUSPECT;
		} else {
			return CaseClassification.NOT_CLASSIFIED;
		}
	}
	
	@Override
	public ClassificationCriteria getSuspectCriteria(Disease disease) {
		if (suspectCriteria.isEmpty()) {
			buildCriteria();
		}
		
		if (suspectCriteria.containsKey(disease)) {
			return suspectCriteria.get(disease);
		} else {
			return null;
		}
	}

	@Override
	public ClassificationCriteria getProbableCriteria(Disease disease) {
		if (suspectCriteria.isEmpty()) {
			buildCriteria();
		}

		if (probableCriteria.containsKey(disease)) {
			return probableCriteria.get(disease);
		} else {
			return null;
		}
	}

	@Override
	public ClassificationCriteria getConfirmedCriteria(Disease disease) {
		if (suspectCriteria.isEmpty()) {
			buildCriteria();
		}

		if (confirmedCriteria.containsKey(disease)) {
			return confirmedCriteria.get(disease);
		} else {
			return null;
		}
	}

	private void buildCriteria() {
		ClassificationCriteria suspect, probable, confirmed;

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
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.PCR_RT_PCR, SampleTestType.VIRUS_ISOLATION));
		addCriteria(Disease.EVD, suspect, probable, confirmed);

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
		addCriteria(Disease.CSM, suspect, probable, confirmed);

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
		addCriteria(Disease.LASSA, suspect, probable, confirmed);

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
						sampleTest(SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, Arrays.asList(new SampleTestType[]{SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.IGG_SERUM_ANTIBODY}), true)));
		addCriteria(Disease.YELLOW_FEVER, suspect, probable, confirmed);

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
						positiveTestResult(SampleTestType.VIRUS_ISOLATION),
						sampleTest(SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, Arrays.asList(new SampleTestType[]{SampleTestType.IGG_SERUM_ANTIBODY}), true)));
		addCriteria(Disease.DENGUE, suspect, probable, confirmed);

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
						sampleTest(SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, Arrays.asList(new SampleTestType[]{SampleTestType.IGG_SERUM_ANTIBODY}), true)));
		addCriteria(Disease.NEW_INFLUENCA, suspect, probable, confirmed);

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
		addCriteria(Disease.MEASLES, suspect, probable, confirmed);

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
		addCriteria(Disease.CHOLERA, suspect, probable, confirmed);

		// Monkey pox
		suspect = allOf(
				symptom(SymptomsDto.FEVER), 
				symptom(SymptomsDto.SKIN_RASH));
		probable = null;
		confirmed = allOf(
				suspect, 
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.PCR_RT_PCR, SampleTestType.VIRUS_ISOLATION));
		addCriteria(Disease.MONKEYPOX, suspect, probable, confirmed);

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
		addCriteria(Disease.PLAGUE, suspect, probable, confirmed);
	}

	private void addCriteria(Disease disease, ClassificationCriteria suspect, ClassificationCriteria probable,
			ClassificationCriteria confirmed) {
		if (suspect != null) {
			suspectCriteria.put(disease, suspect);
		}
		if (probable != null) {
			probableCriteria.put(disease, probable);
		}
		if (confirmed != null) {
			confirmedCriteria.put(disease, confirmed);
		}
	}

	private static ClassificationAllOfCriteria allOf(ClassificationCriteria... criteria) {
		return new ClassificationAllOfCriteria(criteria);
	}
	
	private static ClassificationAllOfCompactCriteria allOfCompact(ClassificationCriteria... criteria) {
		return new ClassificationAllOfCompactCriteria(criteria);
	}
	
	private static ClassificationXOfCriteria xOf(int requiredAmount, ClassificationCriteria... criteria) {
		return new ClassificationXOfCriteria(requiredAmount, criteria);
	}
	
	private static ClassificationXOfSubCriteria xOfSub(int requiredAmount, ClassificationCriteria... criteria) {
		return new ClassificationXOfSubCriteria(requiredAmount, criteria);
	}
	
	private static ClassificationOneOfCompactCriteria oneOfCompact(ClassificationCriteria... criteria) {
		return new ClassificationOneOfCompactCriteria(criteria);
	}

	private static ClassificationNoneOfCriteria noneOf(ClassificationCriteria... criteria) {
		return new ClassificationNoneOfCriteria(criteria);
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

	private static ClassificationSampleTestCriteria sampleTest(String propertyId, List<SampleTestType> testTypes, Object... propertyValues) {
		return new ClassificationSampleTestCriteria(propertyId, testTypes, propertyValues);
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
	
	@LocalBean
	@Stateless
	public static class CaseClassificationFacadeEjbLocal extends CaseClassificationFacadeEjb {
	}
}
