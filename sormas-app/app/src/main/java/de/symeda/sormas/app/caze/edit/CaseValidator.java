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

package de.symeda.sormas.app.caze.edit;

import android.view.View;

import java.util.Date;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.GermanCaseClassificationValidator;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.validation.ValidationHelper;
import de.symeda.sormas.app.databinding.DialogPreviousHospitalizationLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditHospitalizationLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditPortHealthInfoLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

final class CaseValidator {

    static void initializeGermanCaseClassificationValidation(
            Case caze,
            CaseClassification caseClassification,
            FragmentCaseEditLayoutBinding contentBinding) {
        if (ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
            final CaseDtoHelper caseDtoHelper = new CaseDtoHelper();
            final CaseDataDto caseDataDto = caseDtoHelper.adoToDto(caze);
            final boolean hasPositiveTestResult = DatabaseHelper.getSampleDao().hasPositiveTestResult(caze);
            final boolean validCaseClassification =
                    GermanCaseClassificationValidator.isValidGermanCaseClassification(caseClassification, caseDataDto, hasPositiveTestResult);

            if (validCaseClassification) {
                contentBinding.caseDataCaseClassification.disableErrorState();
            } else {
                contentBinding.caseDataCaseClassification.enableErrorState(R.string.validation_case_classification);
            }

            contentBinding.caseDataCaseClassification.setValidationCallback(() -> {
                if (validCaseClassification) {
                    return false;
                }
                return true;
            });
        }
    }

    static void initializePortHealthInfoValidation(final FragmentCaseEditPortHealthInfoLayoutBinding contentBinding, final Case caze) {
        if (contentBinding.portHealthInfoDepartureDateTime.getVisibility() == View.GONE
                || contentBinding.portHealthInfoArrivalDateTime.getVisibility() == View.GONE) {
            return;
        }

        ResultCallback<Boolean> departureCallback = () -> {
            if (DateHelper.isDateAfter(contentBinding.portHealthInfoDepartureDateTime.getValue(), contentBinding.portHealthInfoArrivalDateTime.getValue())) {
                contentBinding.portHealthInfoDepartureDateTime.enableErrorState(
                        I18nProperties.getValidationError(
                                Validations.beforeDate,
                                contentBinding.portHealthInfoDepartureDateTime.getCaption(),
                                contentBinding.portHealthInfoArrivalDateTime.getCaption()));
                return true;
            }

            return false;
        };

        ResultCallback<Boolean> arrivalCallback = () -> {
            if (DateHelper.isDateBefore(contentBinding.portHealthInfoArrivalDateTime.getValue(), contentBinding.portHealthInfoDepartureDateTime.getValue())) {
                contentBinding.portHealthInfoArrivalDateTime.enableErrorState(
                        I18nProperties.getValidationError(
                                Validations.afterDate,
                                contentBinding.portHealthInfoArrivalDateTime.getCaption(),
                                contentBinding.portHealthInfoDepartureDateTime.getCaption()));
                return true;
            }

            return false;
        };

        contentBinding.portHealthInfoDepartureDateTime.setValidationCallback(departureCallback);
        contentBinding.portHealthInfoArrivalDateTime.setValidationCallback(arrivalCallback);
    }

    static void initializeProhibitionToWorkIntervalValidator(FragmentCaseEditLayoutBinding contentBinding) {
        ValidationHelper.initDateIntervalValidator(contentBinding.caseDataProhibitionToWorkFrom, contentBinding.caseDataProhibitionToWorkUntil);
    }

    static void initializeHospitalizationValidation(final FragmentCaseEditHospitalizationLayoutBinding contentBinding, final Case caze) {
        contentBinding.caseHospitalizationAdmissionDate.addValueChangedListener(field -> {
            if (DateHelper.isDateBefore((Date) field.getValue(), caze.getSymptoms().getOnsetDate())) {
                contentBinding.caseHospitalizationAdmissionDate.enableWarningState(
                        I18nProperties.getValidationError(
                                Validations.afterDateSoft,
                                contentBinding.caseHospitalizationAdmissionDate.getCaption(),
                                I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE)));
            } else {
                contentBinding.caseHospitalizationAdmissionDate.disableWarningState();
            }
        });

        ResultCallback<Boolean> admissionDateCallback = () -> {
            if (DateHelper.isDateAfter(
                    contentBinding.caseHospitalizationAdmissionDate.getValue(),
                    contentBinding.caseHospitalizationDischargeDate.getValue())) {
                contentBinding.caseHospitalizationAdmissionDate.enableErrorState(
                        I18nProperties.getValidationError(
                                Validations.beforeDate,
                                contentBinding.caseHospitalizationAdmissionDate.getCaption(),
                                contentBinding.caseHospitalizationDischargeDate.getCaption()));
                return true;
            }

            if (DateHelper.isDateAfter(
                    contentBinding.caseHospitalizationAdmissionDate.getValue(),
                    contentBinding.caseHospitalizationIntensiveCareUnitStart.getValue())) {
                contentBinding.caseHospitalizationAdmissionDate.enableErrorState(
                        I18nProperties.getValidationError(
                                Validations.beforeDate,
                                contentBinding.caseHospitalizationAdmissionDate.getCaption(),
                                contentBinding.caseHospitalizationIntensiveCareUnitStart.getCaption()));
                return true;
            }

            return false;
        };

        ResultCallback<Boolean> dischargeDateCallback = () -> {
            if (DateHelper.isDateBefore(
                    contentBinding.caseHospitalizationDischargeDate.getValue(),
                    contentBinding.caseHospitalizationAdmissionDate.getValue())) {
                contentBinding.caseHospitalizationDischargeDate.enableErrorState(
                        I18nProperties.getValidationError(
                                Validations.afterDate,
                                contentBinding.caseHospitalizationDischargeDate.getCaption(),
                                contentBinding.caseHospitalizationAdmissionDate.getCaption()));
                return true;
            }
            if (DateHelper.isDateBefore(
                    contentBinding.caseHospitalizationDischargeDate.getValue(),
                    contentBinding.caseHospitalizationIntensiveCareUnitEnd.getValue())) {
                contentBinding.caseHospitalizationDischargeDate.enableErrorState(
                        I18nProperties.getValidationError(
                                Validations.afterDate,
                                contentBinding.caseHospitalizationDischargeDate.getCaption(),
                                contentBinding.caseHospitalizationIntensiveCareUnitEnd.getCaption()));
                return true;
            }

            return false;
        };

        ResultCallback<Boolean> intensiveCareDateCallback = () -> {
            if (DateHelper.isDateAfter(
                    contentBinding.caseHospitalizationIntensiveCareUnitStart.getValue(),
                    contentBinding.caseHospitalizationIntensiveCareUnitEnd.getValue())) {
                contentBinding.caseHospitalizationIntensiveCareUnitStart.enableErrorState(
                        I18nProperties.getValidationError(
                                Validations.beforeDate,
                                contentBinding.caseHospitalizationIntensiveCareUnitStart.getCaption(),
                                contentBinding.caseHospitalizationIntensiveCareUnitEnd.getCaption()));
                return true;
            }
            if (DateHelper.isDateAfter(
                    contentBinding.caseHospitalizationIntensiveCareUnitStart.getValue(),
                    contentBinding.caseHospitalizationDischargeDate.getValue())) {
                contentBinding.caseHospitalizationIntensiveCareUnitStart.enableErrorState(
                        I18nProperties.getValidationError(
                                Validations.beforeDate,
                                contentBinding.caseHospitalizationIntensiveCareUnitStart.getCaption(),
                                contentBinding.caseHospitalizationDischargeDate.getCaption()));
                return true;
            }

            return false;
        };

        contentBinding.caseHospitalizationAdmissionDate.setValidationCallback(admissionDateCallback);
        contentBinding.caseHospitalizationDischargeDate.setValidationCallback(dischargeDateCallback);
        contentBinding.caseHospitalizationIntensiveCareUnitStart.setValidationCallback(intensiveCareDateCallback);
    }

    static void initializePreviousHospitalizationValidation(final DialogPreviousHospitalizationLayoutBinding contentBinding) {
        ValidationHelper.initDateIntervalValidator(
                contentBinding.casePreviousHospitalizationAdmissionDate,
                contentBinding.casePreviousHospitalizationDischargeDate,
                false);
        ValidationHelper.initDateIntervalValidator(
                contentBinding.casePreviousHospitalizationAdmissionDate,
                contentBinding.casePreviousHospitalizationIntensiveCareUnitStart,
                false);
        ValidationHelper.initDateIntervalValidator(
                contentBinding.casePreviousHospitalizationIntensiveCareUnitStart,
                contentBinding.casePreviousHospitalizationIntensiveCareUnitEnd,
                false);
        ValidationHelper.initDateIntervalValidator(
                contentBinding.casePreviousHospitalizationIntensiveCareUnitStart,
                contentBinding.casePreviousHospitalizationDischargeDate,
                false);
        ValidationHelper.initDateIntervalValidator(
                contentBinding.casePreviousHospitalizationIntensiveCareUnitEnd,
                contentBinding.casePreviousHospitalizationDischargeDate,
                false);
    }
}
