/*
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
 */

package de.symeda.sormas.app.caze.edit;

import android.view.View;

import org.joda.time.DateTimeComparator;

import java.util.Date;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.databinding.DialogCaseEpidBurialEditLayoutBinding;
import de.symeda.sormas.app.databinding.DialogCaseEpidTravelEditLayoutBinding;
import de.symeda.sormas.app.databinding.DialogPreviousHospitalizationLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditHospitalizationLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditPortHealthInfoLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

final class CaseValidator {

    static void initializeEpiDataBurialValidation(final DialogCaseEpidBurialEditLayoutBinding contentBinding) {
        ResultCallback<Boolean> burialDateFromCallback = () -> {
            if (contentBinding.epiDataBurialBurialDateFrom.getValue() != null && contentBinding.epiDataBurialBurialDateTo.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.epiDataBurialBurialDateFrom.getValue(), contentBinding.epiDataBurialBurialDateTo.getValue()) > 0) {
                    contentBinding.epiDataBurialBurialDateFrom.enableErrorState(
                            I18nProperties.getValidationError(Validations.beforeDate,
                                    contentBinding.epiDataBurialBurialDateFrom.getCaption(),
                                    contentBinding.epiDataBurialBurialDateTo.getCaption()));
                    return true;
                }
            }

            return false;
        };

        ResultCallback<Boolean> burialDateToCallback = () -> {
            if (contentBinding.epiDataBurialBurialDateTo.getValue() != null && contentBinding.epiDataBurialBurialDateFrom.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.epiDataBurialBurialDateTo.getValue(), contentBinding.epiDataBurialBurialDateFrom.getValue()) < 0) {
                    contentBinding.epiDataBurialBurialDateTo.enableErrorState(
                            I18nProperties.getValidationError(Validations.afterDate,
                                    contentBinding.epiDataBurialBurialDateTo.getCaption(),
                                    contentBinding.epiDataBurialBurialDateFrom.getCaption()));
                    return true;
                }
            }

            return false;
        };

        contentBinding.epiDataBurialBurialDateFrom.setValidationCallback(burialDateFromCallback);
        contentBinding.epiDataBurialBurialDateTo.setValidationCallback(burialDateToCallback);
    }

    static void initializeEpiDataTravelValidation(final DialogCaseEpidTravelEditLayoutBinding contentBinding) {
        ResultCallback<Boolean> travelDateFromCallback = () -> {
            if (contentBinding.epiDataTravelTravelDateFrom.getValue() != null && contentBinding.epiDataTravelTravelDateTo.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.epiDataTravelTravelDateFrom.getValue(), contentBinding.epiDataTravelTravelDateTo.getValue()) > 0) {
                    contentBinding.epiDataTravelTravelDateFrom.enableErrorState(
                            I18nProperties.getValidationError(Validations.beforeDate,
                                    contentBinding.epiDataTravelTravelDateFrom.getCaption(),
                                    contentBinding.epiDataTravelTravelDateTo.getCaption()));
                    return true;
                }
            }

            return false;
        };

        ResultCallback<Boolean> burialDateToCallback = () -> {
            if (contentBinding.epiDataTravelTravelDateTo.getValue() != null && contentBinding.epiDataTravelTravelDateFrom.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.epiDataTravelTravelDateTo.getValue(), contentBinding.epiDataTravelTravelDateFrom.getValue()) < 0) {
                    contentBinding.epiDataTravelTravelDateTo.enableErrorState(
                            I18nProperties.getValidationError(Validations.afterDate,
                                    contentBinding.epiDataTravelTravelDateTo.getCaption(),
                                    contentBinding.epiDataTravelTravelDateFrom.getCaption()));
                    return true;
                }
            }

            return false;
        };

        contentBinding.epiDataTravelTravelDateFrom.setValidationCallback(travelDateFromCallback);
        contentBinding.epiDataTravelTravelDateTo.setValidationCallback(burialDateToCallback);
    }

    static void initializePortHealthInfoValidation(final FragmentCaseEditPortHealthInfoLayoutBinding contentBinding, final Case caze) {
        if (contentBinding.portHealthInfoDepartureDateTime.getVisibility() == View.GONE || contentBinding.portHealthInfoArrivalDateTime.getVisibility() == View.GONE) {
            return;
        }

        ResultCallback<Boolean> departureCallback = () -> {
            if (contentBinding.portHealthInfoDepartureDateTime.getValue() != null && contentBinding.portHealthInfoArrivalDateTime.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.portHealthInfoDepartureDateTime.getValue(), contentBinding.portHealthInfoArrivalDateTime.getValue()) > 0) {
                    contentBinding.portHealthInfoDepartureDateTime.enableErrorState(
                            I18nProperties.getValidationError(Validations.beforeDate,
                                    contentBinding.portHealthInfoDepartureDateTime.getCaption(),
                                    contentBinding.portHealthInfoArrivalDateTime.getCaption()));
                    return true;
                }
            }

            return false;
        };

        ResultCallback<Boolean> arrivalCallback = () -> {
            if (contentBinding.portHealthInfoArrivalDateTime.getValue() != null && contentBinding.portHealthInfoDepartureDateTime.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.portHealthInfoArrivalDateTime.getValue(), contentBinding.portHealthInfoDepartureDateTime.getValue()) < 0) {
                    contentBinding.portHealthInfoArrivalDateTime.enableErrorState(
                            I18nProperties.getValidationError(Validations.beforeDate,
                                    contentBinding.portHealthInfoArrivalDateTime.getCaption(),
                                    contentBinding.portHealthInfoDepartureDateTime.getCaption()));
                    return true;
                }
            }

            return false;
        };

        contentBinding.portHealthInfoDepartureDateTime.setValidationCallback(departureCallback);
        contentBinding.portHealthInfoArrivalDateTime.setValidationCallback(arrivalCallback);
    }

    static void initializeHospitalizationValidation(final FragmentCaseEditHospitalizationLayoutBinding contentBinding, final Case caze) {
        contentBinding.caseHospitalizationAdmissionDate.addValueChangedListener(field -> {
            Date value = (Date) field.getValue();
            if (caze.getSymptoms().getOnsetDate() != null && DateTimeComparator.getDateOnlyInstance().compare(value, caze.getSymptoms().getOnsetDate()) <= 0) {
                contentBinding.caseHospitalizationAdmissionDate.enableWarningState(
                        I18nProperties.getValidationError(Validations.afterDateSoft, contentBinding.caseHospitalizationAdmissionDate.getCaption(),
                                I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE)));
            } else {
                contentBinding.caseHospitalizationAdmissionDate.disableWarningState();
            }
        });

        ResultCallback<Boolean> admissionDateCallback = () -> {
            if (contentBinding.caseHospitalizationAdmissionDate.getValue() != null && contentBinding.caseHospitalizationDischargeDate.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.caseHospitalizationAdmissionDate.getValue(), contentBinding.caseHospitalizationDischargeDate.getValue()) > 0) {
                    contentBinding.caseHospitalizationAdmissionDate.enableErrorState(
                            I18nProperties.getValidationError(Validations.beforeDate,
                                    contentBinding.caseHospitalizationAdmissionDate.getCaption(),
                                    contentBinding.caseHospitalizationDischargeDate.getCaption()));
                    return true;
                }
            }

            return false;
        };

        ResultCallback<Boolean> dischargeDateCallback = () -> {
            if (contentBinding.caseHospitalizationDischargeDate.getValue() != null && contentBinding.caseHospitalizationAdmissionDate.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.caseHospitalizationDischargeDate.getValue(), contentBinding.caseHospitalizationAdmissionDate.getValue()) < 0) {
                    contentBinding.caseHospitalizationDischargeDate.enableErrorState(
                            I18nProperties.getValidationError(Validations.afterDate,
                                    contentBinding.caseHospitalizationDischargeDate.getCaption(),
                                    contentBinding.caseHospitalizationAdmissionDate.getCaption()));
                    return true;
                }
            }

            return false;
        };

        contentBinding.caseHospitalizationAdmissionDate.setValidationCallback(admissionDateCallback);
        contentBinding.caseHospitalizationDischargeDate.setValidationCallback(dischargeDateCallback);
    }

    static void initializePreviousHospitalizationValidation(final DialogPreviousHospitalizationLayoutBinding contentBinding) {
        ResultCallback<Boolean> admissionDateCallback = () -> {
            if (contentBinding.casePreviousHospitalizationAdmissionDate.getValue() != null && contentBinding.casePreviousHospitalizationDischargeDate.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.casePreviousHospitalizationAdmissionDate.getValue(), contentBinding.casePreviousHospitalizationDischargeDate.getValue()) > 0) {
                    contentBinding.casePreviousHospitalizationAdmissionDate.enableErrorState(
                            I18nProperties.getValidationError(Validations.beforeDate,
                                    contentBinding.casePreviousHospitalizationAdmissionDate.getCaption(),
                                    contentBinding.casePreviousHospitalizationDischargeDate.getCaption()));
                    return true;
                }
            }

            return false;
        };

        ResultCallback<Boolean> dischargeDateCallback = () -> {
            if (contentBinding.casePreviousHospitalizationDischargeDate.getValue() != null && contentBinding.casePreviousHospitalizationAdmissionDate.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.casePreviousHospitalizationDischargeDate.getValue(), contentBinding.casePreviousHospitalizationAdmissionDate.getValue()) < 0) {
                    contentBinding.casePreviousHospitalizationDischargeDate.enableErrorState(
                            I18nProperties.getValidationError(Validations.afterDate,
                                    contentBinding.casePreviousHospitalizationDischargeDate.getCaption(),
                                    contentBinding.casePreviousHospitalizationAdmissionDate.getCaption()));
                    return true;
                }
            }

            return false;
        };

        contentBinding.casePreviousHospitalizationAdmissionDate.setValidationCallback(admissionDateCallback);
        contentBinding.casePreviousHospitalizationDischargeDate.setValidationCallback(dischargeDateCallback);
    }

}
