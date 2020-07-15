/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.caze;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;

public class GermanCaseClassificationValidator {

	public static boolean isValidGermanCaseClassification(CaseClassification caseClassification, CaseDataDto caseDataDto, List<SampleDto> samplesOfCase) {
		switch (caseClassification) {

		case NOT_CLASSIFIED:
		case NO_CASE:
		case PROBABLE:
			return true;
		case SUSPECT: {

			return hasCoronavirusSymptom(caseDataDto);
		}
		case CONFIRMED: {
			return hasPositiveLabResult(samplesOfCase) && hasCoronavirusSymptom(caseDataDto);
		}
		case CONFIRMED_NO_SYMPTOMS: {
			final SymptomsDto symptoms = caseDataDto.getSymptoms();
			return hasPositiveLabResult(samplesOfCase)
				&& caseDataDto.getDisease() == Disease.CORONAVIRUS
				&& (SymptomsHelper.allSymptomsFalse(symptoms)
					|| SymptomsHelper.atLeastOnSymptomTrue(
						symptoms.getFever(),
						symptoms.getGeneralSignsOfDisease(),
						symptoms.getDiarrhea(),
						symptoms.getLossOfSmell(),
						symptoms.getLossOfTaste(),
						symptoms.getFastHeartRate(),
						symptoms.getRapidBreathing(),
						symptoms.getOxygenSaturationLower94(),
						symptoms.getVomiting(),
						symptoms.getChillsSweats()));
		}
		case CONFIRMED_UNKNOWN_SYMPTOMS:
			final SymptomsDto symptoms = caseDataDto.getSymptoms();
			return hasPositiveLabResult(samplesOfCase)
				&& caseDataDto.getDisease() == Disease.CORONAVIRUS
				&& SymptomsHelper.allSymptomsUnknownOrNull(symptoms);
		}
		return false;
	}

	private static boolean hasPositiveLabResult(List<SampleDto> samplesOfCase) {
		for (SampleDto sampleDto : samplesOfCase) {
			if (sampleDto.getPathogenTestResult() == PathogenTestResultType.POSITIVE) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasCoronavirusSymptom(CaseDataDto caseDataDto) {
		final SymptomsDto symptoms = caseDataDto.getSymptoms();
		return caseDataDto.getDisease() == Disease.CORONAVIRUS
			&& symptoms.getSymptomatic()
			&& SymptomsHelper.atLeastOnSymptomTrue(
				symptoms.getPneumoniaClinicalOrRadiologic(),
				symptoms.getDifficultyBreathing(),
				symptoms.getSoreThroat(),
				symptoms.getCough(),
				symptoms.getRunnyNose(),
				symptoms.getRespiratoryDiseaseVentilation(),
				symptoms.getAcuteRespiratoryDistressSyndrome());
	}

}
