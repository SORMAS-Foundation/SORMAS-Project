/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.symptoms;

import java.util.Date;

import org.joda.time.DateTimeComparator;

import android.view.View;

import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.databinding.FragmentSymptomsEditLayoutBinding;

final class SymptomsValidator {

	static void initializeSymptomsValidation(final FragmentSymptomsEditLayoutBinding contentBinding, final AbstractDomainObject ado) {
		if (contentBinding.symptomsFever.getVisibility() == View.VISIBLE) {
			contentBinding.symptomsTemperature.addValueChangedListener(
				field -> toggleFeverComponentError(contentBinding, (Float) field.getValue(), (SymptomState) contentBinding.symptomsFever.getValue()));
			contentBinding.symptomsFever.addValueChangedListener(
				field -> toggleFeverComponentError(
					contentBinding,
					(Float) contentBinding.symptomsTemperature.getValue(),
					(SymptomState) field.getValue()));
		}

		if (ado instanceof Case) {
			contentBinding.symptomsOnsetDate.addValueChangedListener(field -> {
				Date value = (Date) field.getValue();
				if (((Case) ado).getHospitalization().getAdmissionDate() != null
					&& DateTimeComparator.getDateOnlyInstance().compare(value, ((Case) ado).getHospitalization().getAdmissionDate()) >= 0) {
					contentBinding.symptomsOnsetDate.enableWarningState(
						I18nProperties.getValidationError(
							Validations.beforeDateSoft,
							contentBinding.symptomsOnsetDate.getCaption(),
							I18nProperties.getPrefixCaption(HospitalizationDto.I18N_PREFIX, HospitalizationDto.ADMISSION_DATE)));
				} else {
					contentBinding.symptomsOnsetDate.disableWarningState();
				}
			});
		}
	}

	private static void toggleFeverComponentError(
		final FragmentSymptomsEditLayoutBinding contentBinding,
		Float temperatureValue,
		SymptomState feverValue) {
		if (temperatureValue != null && temperatureValue >= 38.0f && feverValue != SymptomState.YES) {
			contentBinding.symptomsFever.enableWarningState(
				I18nProperties.getValidationError(
					Validations.feverTemperatureAboveThreshold,
					I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.FEVER)));
		} else if (temperatureValue != null && temperatureValue < 38.0f && feverValue != SymptomState.NO) {
			contentBinding.symptomsFever.enableWarningState(
				I18nProperties.getValidationError(
					Validations.feverTemperatureBelowThreshold,
					I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.FEVER)));
		} else {
			contentBinding.symptomsFever.disableWarningState();
		}
	}

}
