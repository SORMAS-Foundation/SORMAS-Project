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
package de.symeda.sormas.backend.caze.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.caze.classification.CaseClassificationFacade;
import de.symeda.sormas.api.caze.classification.ClassificationAllOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationAllOfCriteriaDto.ClassificationAllOfCompactCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationAllSymptomsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationAnyOfSymptomsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationCaseCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationEpiDataCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationEventClusterCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationExposureCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationNoneOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestNegativeResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestOtherPositiveResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestPositiveResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPersonAgeBetweenYearsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationSymptomsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationVaccinationDateNotInStartDateRangeDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto.ClassificationOneOfCompactCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto.ClassificationXOfSubCriteriaDto;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.TypeOfAnimal;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTestService;

/**
 * Stateless instead of Singleton. It's ok to have multiple instances with an
 * individual cache.
 * 
 * @author Martin Wahnschaffe
 */
@Stateless(name = "CaseClassificationFacade")
public class CaseClassificationFacadeEjb implements CaseClassificationFacade {

	@EJB
	private PersonService personService;
	@EJB
	private EventFacadeEjb.EventFacadeEjbLocal eventFacade;
	@EJB
	private PathogenTestService pathogenTestService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private EventService eventService;
	@EJB
	private ImmunizationService immunizationService;

	/** local cache */
	private final Map<Disease, DiseaseClassificationCriteriaDto> criteriaMap = new HashMap<>();

	@Override
	public CaseClassification getClassification(CaseDataDto caze) {

		if (criteriaMap.isEmpty()) {
			buildCriteria();
		}

		PersonDto person = PersonFacadeEjb.toPersonDto(personService.getByUuid(caze.getPerson().getUuid()));
		List<PathogenTestDto> pathogenTests = pathogenTestService.getAllByCase(caze.getUuid())
			.stream()
			.map(PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal::toDto)
			.collect(Collectors.toList());
		List<EventDto> caseEvents = eventFacade.getAllByCase(caze);
		Date lastVaccinationDate = null;
		if (caze.getDisease() == Disease.YELLOW_FEVER && caze.getVaccinationStatus() == VaccinationStatus.VACCINATED) {
			lastVaccinationDate =
				immunizationService.getLastVaccinationDateBefore(caze.getPerson().getUuid(), caze.getDisease(), CaseLogic.getStartDate(caze));
		}

		DiseaseClassificationCriteriaDto criteria = criteriaMap.get(caze.getDisease());

		if (criteria != null) {
			if (criteria.getConfirmedCriteria() != null
				&& criteria.getConfirmedCriteria().eval(caze, person, pathogenTests, caseEvents, lastVaccinationDate)) {
				return CaseClassification.CONFIRMED;
			} else if (criteria.getNotACaseCriteria() != null
				&& criteria.getNotACaseCriteria().eval(caze, person, pathogenTests, caseEvents, lastVaccinationDate)) {
				return CaseClassification.NO_CASE;
			} else if (criteria.getProbableCriteria() != null
				&& criteria.getProbableCriteria().eval(caze, person, pathogenTests, caseEvents, lastVaccinationDate)) {
				return CaseClassification.PROBABLE;
			} else if (criteria.getSuspectCriteria() != null
				&& criteria.getSuspectCriteria().eval(caze, person, pathogenTests, caseEvents, lastVaccinationDate)) {
				return CaseClassification.SUSPECT;
			} else if (configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
				if (criteria.getConfirmedNoSymptomsCriteria() != null
					&& criteria.getConfirmedNoSymptomsCriteria().eval(caze, person, pathogenTests, caseEvents, lastVaccinationDate)) {
					return CaseClassification.CONFIRMED_NO_SYMPTOMS;
				} else if (criteria.getConfirmedUnknownSymptomsCriteria() != null
					&& criteria.getConfirmedUnknownSymptomsCriteria().eval(caze, person, pathogenTests, caseEvents, lastVaccinationDate)) {
					return CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS;
				}
			}
		}
		return CaseClassification.NOT_CLASSIFIED;
	}

	@Override
	@PermitAll
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

		return criteriaMap.getOrDefault(disease, null);
	}

	private void buildCriteria() {

		ClassificationCriteriaDto suspect;
		ClassificationCriteriaDto probable;
		ClassificationCriteriaDto confirmed;
		ClassificationCriteriaDto confirmedNoSymptoms;
		ClassificationCriteriaDto confirmedUnknownSymptoms;

		// EVD
		suspect = allOf(
			symptom(SymptomsDto.FEVER),
			xOf(
				1,
				allOfCompact(symptom(SymptomsDto.BLOODY_BLACK_STOOL), symptom(SymptomsDto.DIARRHEA)),
				symptom(SymptomsDto.GUMS_BLEEDING),
				symptom(SymptomsDto.SKIN_BRUISING),
				allOfCompact(symptom(SymptomsDto.EYES_BLEEDING), symptom(SymptomsDto.BLOOD_URINE))));
		probable = allOf(
			caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
			suspect,
			xOf(
				1,
				epiData(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN),
				exposure(ExposureDto.HANDLING_SAMPLES, ExposureType.WORK),
				exposure(ExposureDto.PERCUTANEOUS, ExposureType.WORK),
				allOfCompact(
					exposure(ExposureDto.RISK_AREA, ExposureType.TRAVEL),
					exposure(ExposureDto.PHYSICAL_CONTACT_WITH_BODY, ExposureType.BURIAL))));
		confirmed = allOf(
			suspect,
			positiveTestResult(Disease.EVD, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestType.PCR_RT_PCR, PathogenTestType.ISOLATION));

		addCriteria(Disease.EVD, DateHelper.getDateZero(2020, 11, 6), suspect, probable, confirmed, extracted(Disease.EVD));

		// CSM
		suspect = allOf(
			symptom(SymptomsDto.FEVER),
			xOf(
				1,
				symptom(SymptomsDto.NECK_STIFFNESS),
				symptom(SymptomsDto.ALTERED_CONSCIOUSNESS),
				symptom(SymptomsDto.MENINGEAL_SIGNS),
				symptom(SymptomsDto.BULGING_FONTANELLE)));
		probable = allOf(caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED), suspect, epiData(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN));
		confirmed = allOf(suspect, positiveTestResult(Disease.CSM, PathogenTestType.ISOLATION));
		addCriteria(Disease.CSM, DateHelper.getDateZero(2020, 11, 6), suspect, probable, confirmed, extracted(Disease.CSM));

		// Lassa Fever
		suspect = allOf(
			xOf(
				1,
				symptom(SymptomsDto.FATIGUE_WEAKNESS),
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
			exposure(ExposureDto.TYPE_OF_ANIMAL, ExposureType.ANIMAL_CONTACT, TypeOfAnimal.RODENT),
			epiData(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN));
		probable = allOf(caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED), suspect);
		confirmed = allOf(
			suspect,
			positiveTestResult(Disease.LASSA, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestType.PCR_RT_PCR, PathogenTestType.ISOLATION));
		addCriteria(Disease.LASSA, DateHelper.getDateZero(2020, 11, 6), suspect, probable, confirmed, extracted(Disease.LASSA));

		// Yellow fever
		suspect = allOf(symptom(SymptomsDto.FEVER), symptom(SymptomsDto.JAUNDICE));
		probable = allOf(
			suspect,
			xOf(
				1,
				exposure(ExposureDto.RISK_AREA, ExposureType.TRAVEL),
				allOfCompact(
					caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED),
					positiveTestResult(Disease.YELLOW_FEVER, PathogenTestType.HISTOPATHOLOGY))));
		confirmed = allOf(
			suspect,
			vaccinationDateNotInStartDateRange(30),
			xOf(
				1,
				allOf(
					positiveTestResult(Disease.YELLOW_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY),
					noneOf(
						positiveTestResult(Disease.WEST_NILE_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY),
						positiveTestResult(Disease.DENGUE, PathogenTestType.IGM_SERUM_ANTIBODY))),
				allOf(
					positiveTestResult(Disease.YELLOW_FEVER, PathogenTestType.NEUTRALIZING_ANTIBODIES),
					noneOf(
						positiveTestResult(Disease.WEST_NILE_FEVER, PathogenTestType.NEUTRALIZING_ANTIBODIES),
						positiveTestResult(Disease.DENGUE, PathogenTestType.NEUTRALIZING_ANTIBODIES))),
				positiveTestResult(Disease.YELLOW_FEVER, PathogenTestType.PCR_RT_PCR, PathogenTestType.ANTIGEN_DETECTION, PathogenTestType.ISOLATION),
				sampleTest(
					PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER,
					Arrays.asList(
						new PathogenTestType[] {
							PathogenTestType.IGM_SERUM_ANTIBODY,
							PathogenTestType.IGG_SERUM_ANTIBODY }),
					true)));
		addCriteria(Disease.YELLOW_FEVER, DateHelper.getDateZero(2020, 11, 6), suspect, probable, confirmed, extracted(Disease.YELLOW_FEVER));

		// Dengue fever
		suspect = allOf(
			symptom(SymptomsDto.FEVER),
			xOf(
				2,
				symptom(SymptomsDto.HEADACHE),
				symptom(SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE),
				symptom(SymptomsDto.NAUSEA),
				symptom(SymptomsDto.VOMITING),
				symptom(SymptomsDto.SWOLLEN_GLANDS),
				allOfCompact(symptom(SymptomsDto.MUSCLE_PAIN), symptom(SymptomsDto.JOINT_PAIN)),
				symptom(SymptomsDto.SKIN_RASH)));
		probable = allOf(suspect, exposure(ExposureDto.RISK_AREA, ExposureType.TRAVEL));
		confirmed = allOf(
			suspect,
			xOf(
				1,
				allOf(
					positiveTestResult(Disease.DENGUE, PathogenTestType.IGM_SERUM_ANTIBODY),
					noneOf(
						positiveTestResult(Disease.WEST_NILE_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY),
						positiveTestResult(Disease.YELLOW_FEVER, PathogenTestType.IGM_SERUM_ANTIBODY))),
				positiveTestResult(Disease.DENGUE, PathogenTestType.PCR_RT_PCR),
				positiveTestResult(Disease.DENGUE, PathogenTestType.ISOLATION),
				sampleTest(
					PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER,
					Arrays.asList(
						new PathogenTestType[] {
							PathogenTestType.IGG_SERUM_ANTIBODY }),
					true)));
		addCriteria(Disease.DENGUE, DateHelper.getDateZero(2020, 11, 6), suspect, probable, confirmed, extracted(Disease.DENGUE));

		// Influenza (new subtype)
		suspect = allOf(
			symptom(SymptomsDto.FEVER),
			xOf(1, symptom(SymptomsDto.COUGH), symptom(SymptomsDto.DIFFICULTY_BREATHING)),
			xOf(
				1,
				epiData(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN),
				epiData(EpiDataDto.AREA_INFECTED_ANIMALS),
				exposure(ExposureDto.HANDLING_SAMPLES, ExposureType.WORK)));
		probable = allOf(suspect, caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED));
		confirmed = allOf(
			suspect,
			xOf(
				1,
				positiveTestResult(
					Disease.NEW_INFLUENZA,
					PathogenTestType.ISOLATION,
					PathogenTestType.NEUTRALIZING_ANTIBODIES,
					PathogenTestType.PCR_RT_PCR),
				sampleTest(
					PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER,
					Arrays.asList(
						new PathogenTestType[] {
							PathogenTestType.IGG_SERUM_ANTIBODY }),
					true)));
		addCriteria(Disease.NEW_INFLUENZA, DateHelper.getDateZero(2020, 11, 6), suspect, probable, confirmed, extracted(Disease.NEW_INFLUENZA));

		// Measles
		suspect = allOf(
			symptom(SymptomsDto.FEVER),
			symptom(SymptomsDto.SKIN_RASH),
			xOf(1, symptom(SymptomsDto.COUGH), symptom(SymptomsDto.RUNNY_NOSE), symptom(SymptomsDto.CONJUNCTIVITIS)));
		probable = epiData(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN);
		confirmed = allOf(suspect, positiveTestResult(Disease.MEASLES, PathogenTestType.IGM_SERUM_ANTIBODY));
		addCriteria(Disease.MEASLES, DateHelper.getDateZero(2020, 11, 6), suspect, probable, confirmed, extracted(Disease.MEASLES));

		// Cholera
		suspect = allOf(
			personAgeBetweenYears(5, null),
			xOf(
				1,
				symptom(SymptomsDto.DEHYDRATION),
				allOfCompact(
					symptom(SymptomsDto.DIARRHEA),
					oneOfCompact(caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED), exposure(ExposureDto.RISK_AREA, ExposureType.TRAVEL)))));
		probable = null;
		confirmed = allOf(suspect, positiveTestResult(Disease.CHOLERA, PathogenTestType.ISOLATION));
		addCriteria(Disease.CHOLERA, DateHelper.getDateZero(2020, 11, 6), suspect, probable, confirmed, extracted(Disease.CHOLERA));

		// Monkey pox
		suspect = allOf(symptom(SymptomsDto.FEVER), symptom(SymptomsDto.SKIN_RASH));
		probable = null;
		confirmed = allOf(
			suspect,
			positiveTestResult(Disease.MONKEYPOX, PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestType.PCR_RT_PCR, PathogenTestType.ISOLATION));
		addCriteria(Disease.MONKEYPOX, DateHelper.getDateZero(2020, 11, 6), suspect, probable, confirmed, extracted(Disease.MONKEYPOX));

		// Plague
		suspect = allOf(
			xOf(
				1,
				allOfCompact(
					caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.BUBONIC),
					symptom(SymptomsDto.FEVER),
					symptom(SymptomsDto.PAINFUL_LYMPHADENITIS)),
				allOf(
					allOfCompact(caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.PNEUMONIC), symptom(SymptomsDto.FEVER)),
					xOfSub(1, true, symptom(SymptomsDto.COUGH), symptom(SymptomsDto.CHEST_PAIN), symptom(SymptomsDto.COUGHING_BLOOD))),
				allOfCompact(
					caseData(CaseDataDto.PLAGUE_TYPE, PlagueType.SEPTICAEMIC),
					symptom(SymptomsDto.FEVER),
					symptom(SymptomsDto.CHILLS_SWEATS))));
		probable = allOf(
			suspect,
			xOf(1, exposure(ExposureDto.RISK_AREA, ExposureType.TRAVEL), positiveTestResult(Disease.PLAGUE, PathogenTestType.ANTIGEN_DETECTION)));
		confirmed = allOf(suspect, positiveTestResult(Disease.PLAGUE, PathogenTestType.ISOLATION, PathogenTestType.PCR_RT_PCR));
		addCriteria(Disease.PLAGUE, DateHelper.getDateZero(2020, 11, 6), suspect, probable, confirmed, extracted(Disease.PLAGUE));

		// Congenital rubella
		suspect = allOf(
			xOf(
				1,
				symptom(SymptomsDto.BILATERAL_CATARACTS),
				symptom(SymptomsDto.UNILATERAL_CATARACTS),
				symptom(SymptomsDto.CONGENITAL_GLAUCOMA),
				symptom(SymptomsDto.CONGENITAL_HEART_DISEASE),
				symptom(SymptomsDto.HEARINGLOSS),
				symptom(SymptomsDto.PIGMENTARY_RETINOPATHY),
				symptom(SymptomsDto.PURPURIC_RASH),
				symptom(SymptomsDto.SPLENOMEGALY),
				symptom(SymptomsDto.JAUNDICE),
				symptom(SymptomsDto.MICROCEPHALY),
				symptom(SymptomsDto.DEVELOPMENTAL_DELAY),
				symptom(SymptomsDto.MENINGOENCEPHALITIS),
				symptom(SymptomsDto.RADIOLUCENT_BONE_DISEASE)));
		probable = xOf(
			1,
			xOf(
				2,
				oneOfCompact(
					symptom(SymptomsDto.BILATERAL_CATARACTS),
					symptom(SymptomsDto.UNILATERAL_CATARACTS),
					symptom(SymptomsDto.CONGENITAL_GLAUCOMA)),
				symptom(SymptomsDto.CONGENITAL_HEART_DISEASE),
				symptom(SymptomsDto.HEARINGLOSS),
				symptom(SymptomsDto.PIGMENTARY_RETINOPATHY)),
			allOfTogether(
				xOfSub(
					1,
					false,
					oneOfCompact(
						symptom(SymptomsDto.BILATERAL_CATARACTS),
						symptom(SymptomsDto.UNILATERAL_CATARACTS),
						symptom(SymptomsDto.CONGENITAL_GLAUCOMA)),
					symptom(SymptomsDto.CONGENITAL_HEART_DISEASE),
					symptom(SymptomsDto.HEARINGLOSS),
					symptom(SymptomsDto.PIGMENTARY_RETINOPATHY)),
				xOfSub(
					1,
					false,
					symptom(SymptomsDto.PURPURIC_RASH),
					symptom(SymptomsDto.SPLENOMEGALY),
					symptom(SymptomsDto.MICROCEPHALY),
					symptom(SymptomsDto.DEVELOPMENTAL_DELAY),
					symptom(SymptomsDto.MENINGOENCEPHALITIS),
					symptom(SymptomsDto.RADIOLUCENT_BONE_DISEASE))));
		confirmed = allOf(
			suspect,
			positiveTestResult(
				Disease.CONGENITAL_RUBELLA,
				PathogenTestType.ISOLATION,
				PathogenTestType.IGM_SERUM_ANTIBODY,
				PathogenTestType.PCR_RT_PCR));
		addCriteria(
			Disease.CONGENITAL_RUBELLA,
			DateHelper.getDateZero(2020, 11, 6),
			suspect,
			probable,
			confirmed,
			extracted(Disease.CONGENITAL_RUBELLA));

		// CORONAVIRUS
		suspect = xOf(
			1,
			symptom(SymptomsDto.PNEUMONIA_CLINICAL_OR_RADIOLOGIC),
			symptom(SymptomsDto.DIFFICULTY_BREATHING),
			symptom(SymptomsDto.COUGH),
			symptom(SymptomsDto.RUNNY_NOSE),
			symptom(SymptomsDto.RESPIRATORY_DISEASE_VENTILATION),
			symptom(SymptomsDto.ACUTE_RESPIRATORY_DISTRESS_SYNDROME),
			symptom(SymptomsDto.LOSS_OF_TASTE),
			symptom(SymptomsDto.LOSS_OF_SMELL),
			symptom(SymptomsDto.SORE_THROAT));

		probable = allOf(suspect, xOf(1, caseData(CaseDataDto.EPIDEMIOLOGICAL_CONFIRMATION, YesNoUnknown.YES), partOfEventCluster()));

		confirmed = allOf(
			positiveTestResult(
				Disease.CORONAVIRUS,
				PathogenTestType.PCR_RT_PCR,
				PathogenTestType.ANTIGEN_DETECTION,
				PathogenTestType.ISOLATION,
				PathogenTestType.SEQUENCING,
				PathogenTestType.RAPID_TEST),
			suspect);

		// confirmed_no_symptoms = positive test AND at least one symptom set to no AND all covid-relevant symptoms are anything but yes
		confirmedNoSymptoms = allOf(
			positiveTestResult(
				Disease.CORONAVIRUS,
				PathogenTestType.PCR_RT_PCR,
				PathogenTestType.ANTIGEN_DETECTION,
				PathogenTestType.ISOLATION,
				PathogenTestType.SEQUENCING,
				PathogenTestType.RAPID_TEST),
			anyOfSymptoms(SymptomState.NO, Disease.CORONAVIRUS),
			noneOf(suspect));

		// confirmed_unknown_symptoms = positive test AND at least one symptom set to null or unknown AND covid-relevant symptoms are anything but yes or no
		confirmedUnknownSymptoms = allOf(
			positiveTestResult(
				Disease.CORONAVIRUS,
				PathogenTestType.PCR_RT_PCR,
				PathogenTestType.ANTIGEN_DETECTION,
				PathogenTestType.ISOLATION,
				PathogenTestType.SEQUENCING,
				PathogenTestType.RAPID_TEST),
			xOf(1, anyOfSymptoms(SymptomState.UNKNOWN, Disease.CORONAVIRUS), anyOfSymptoms(null, Disease.CORONAVIRUS)),
			noneOf(anyOfSymptoms(SymptomState.NO, Disease.CORONAVIRUS)),
			noneOf(suspect));

		addCriteria(
			Disease.CORONAVIRUS,
			DateHelper.getDateZero(2020, 11, 6),
			suspect,
			probable,
			confirmed,
			confirmedNoSymptoms,
			confirmedUnknownSymptoms,
			extracted(Disease.CORONAVIRUS));
	}

	private ClassificationAllOfCriteriaDto extracted(Disease disease) {
		return allOf(negativeTestResult(disease), otherPositiveTestResult(disease));
	}

	private void addCriteria(
		Disease disease,
		Date changeDate,
		ClassificationCriteriaDto suspect,
		ClassificationCriteriaDto probable,
		ClassificationCriteriaDto confirmed,
		ClassificationCriteriaDto notACase) {

		DiseaseClassificationCriteriaDto criteria =
			new DiseaseClassificationCriteriaDto(disease, changeDate, suspect, probable, confirmed, null, null, notACase);
		criteriaMap.put(disease, criteria);
	}

	private void addCriteria(
		Disease disease,
		Date changeDate,
		ClassificationCriteriaDto suspect,
		ClassificationCriteriaDto probable,
		ClassificationCriteriaDto confirmed,
		ClassificationCriteriaDto confirmedNoSymptoms,
		ClassificationCriteriaDto confirmedUnknownSymptoms,
		ClassificationCriteriaDto notACase) {

		DiseaseClassificationCriteriaDto criteria = new DiseaseClassificationCriteriaDto(
			disease,
			changeDate,
			suspect,
			probable,
			confirmed,
			confirmedNoSymptoms,
			confirmedUnknownSymptoms,
			notACase);
		criteriaMap.put(disease, criteria);
	}

	private ClassificationAllOfCriteriaDto allOf(ClassificationCriteriaDto... criteria) {
		return new ClassificationAllOfCriteriaDto(criteria);
	}

	private ClassificationAllOfCriteriaDto allOfTogether(ClassificationCriteriaDto... criteria) {
		return new ClassificationAllOfCriteriaDto(true, criteria);
	}

	private ClassificationAllOfCompactCriteriaDto allOfCompact(ClassificationCriteriaDto... criteria) {
		return new ClassificationAllOfCompactCriteriaDto(criteria);
	}

	private ClassificationXOfCriteriaDto xOf(int requiredAmount, ClassificationCriteriaDto... criteria) {
		return new ClassificationXOfCriteriaDto(requiredAmount, criteria);
	}

	private ClassificationXOfSubCriteriaDto xOfSub(int requiredAmount, boolean isAddition, ClassificationCriteriaDto... criteria) {
		return new ClassificationXOfSubCriteriaDto(requiredAmount, isAddition, criteria);
	}

	private ClassificationOneOfCompactCriteriaDto oneOfCompact(ClassificationCriteriaDto... criteria) {
		return new ClassificationOneOfCompactCriteriaDto(criteria);
	}

	private ClassificationNoneOfCriteriaDto noneOf(ClassificationCriteriaDto... criteria) {
		return new ClassificationNoneOfCriteriaDto(criteria);
	}

	private ClassificationCaseCriteriaDto caseData(String propertyId, Object... propertyValues) {
		return new ClassificationCaseCriteriaDto(propertyId, propertyValues);
	}

	private ClassificationSymptomsCriteriaDto symptom(String propertyId) {
		return new ClassificationSymptomsCriteriaDto(propertyId);
	}

	private ClassificationEpiDataCriteriaDto epiData(String propertyId) {
		return new ClassificationEpiDataCriteriaDto(propertyId);
	}

	private ClassificationExposureCriteriaDto exposure(String propertyId, ExposureType exposureType, Object... propertyValues) {
		return new ClassificationExposureCriteriaDto(propertyId, exposureType, propertyValues);
	}

	private ClassificationPathogenTestCriteriaDto sampleTest(String propertyId, List<PathogenTestType> testTypes, Object... propertyValues) {
		return new ClassificationPathogenTestCriteriaDto(propertyId, testTypes, propertyValues);
	}

	private ClassificationPathogenTestNegativeResultCriteriaDto negativeTestResult(Disease testedDisease) {
		return new ClassificationPathogenTestNegativeResultCriteriaDto(testedDisease);
	}

	private ClassificationPathogenTestPositiveResultCriteriaDto positiveTestResult(Disease testedDisease, PathogenTestType... pathogenTestTypes) {
		return new ClassificationPathogenTestPositiveResultCriteriaDto(testedDisease, pathogenTestTypes);
	}

	private ClassificationPathogenTestOtherPositiveResultCriteriaDto otherPositiveTestResult(Disease testedDisease) {
		return new ClassificationPathogenTestOtherPositiveResultCriteriaDto(testedDisease);
	}

	private ClassificationVaccinationDateNotInStartDateRangeDto vaccinationDateNotInStartDateRange(int daysBeforeStartDate) {
		return new ClassificationVaccinationDateNotInStartDateRangeDto(daysBeforeStartDate);
	}

	private ClassificationPersonAgeBetweenYearsCriteriaDto personAgeBetweenYears(Integer lowerYearsThreshold, Integer upperYearsThreshold) {
		return new ClassificationPersonAgeBetweenYearsCriteriaDto(lowerYearsThreshold, upperYearsThreshold);
	}

	private ClassificationAnyOfSymptomsCriteriaDto anyOfSymptoms(SymptomState symptomState, Disease disease) {
		return new ClassificationAnyOfSymptomsCriteriaDto(symptomState, disease, configFacade.getCountryLocale());
	}

	private ClassificationAllSymptomsCriteriaDto allOfSymptoms(SymptomState symptomState, Disease disease) {
		return new ClassificationAllSymptomsCriteriaDto(symptomState, disease, configFacade.getCountryLocale());
	}

	private ClassificationEventClusterCriteriaDto partOfEventCluster() {
		return new ClassificationEventClusterCriteriaDto();
	}

	@LocalBean
	@Stateless
	public static class CaseClassificationFacadeEjbLocal extends CaseClassificationFacadeEjb {

	}
}
