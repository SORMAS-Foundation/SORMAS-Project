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

package de.symeda.sormas.ui.caze;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.vaadin.v7.data.validator.AbstractValidator;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;

public class CaseClassificationValidator extends AbstractValidator<CaseClassification> {

	private final String caseUuid;

	public CaseClassificationValidator(String caseUuid, String errorMessage) {
		super(errorMessage);
		this.caseUuid = caseUuid;
	}

	@Override
	protected boolean isValidValue(CaseClassification caseClassification) {

		switch (caseClassification) {

		case NOT_CLASSIFIED:
		case NO_CASE:
		case PROBABLE:
			return true;
		case SUSPECT: {
			final CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
			return hasCoronavirusSymptom(caseDataDto);
		}
		case CONFIRMED: {
			final CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
			return positiveLabResult(caseDataDto) && hasCoronavirusSymptom(caseDataDto);
		}
		case CONFIRMED_NO_SYMPTOMS: {
			final CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
			final SymptomsDto symptoms = caseDataDto.getSymptoms();
			return positiveLabResult(caseDataDto)
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
			final CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
			final SymptomsDto symptoms = caseDataDto.getSymptoms();
			return positiveLabResult(caseDataDto)
				&& caseDataDto.getDisease() == Disease.CORONAVIRUS
				&& SymptomsHelper.allSymptomsUnknownOrNull(symptoms);
		}
		return false;
	}

	private boolean positiveLabResult(CaseDataDto caseDataDto) {
		final List<SampleDto> samplesOfCase = FacadeProvider.getSampleFacade().getByCaseUuids(Collections.singletonList(caseDataDto.getUuid()));
		final Optional<SampleDto> positiveTestSampleOptional =
			samplesOfCase.stream().filter(sampleDto -> sampleDto.getPathogenTestResult() == PathogenTestResultType.POSITIVE).findAny();
		return positiveTestSampleOptional.isPresent();
	}

	private boolean hasCoronavirusSymptom(CaseDataDto caseDataDto) {
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

	@Override
	public Class<CaseClassification> getType() {
		return CaseClassification.class;
	}
}
