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

import android.content.Context;
import android.content.res.Resources;

import org.joda.time.DateTimeComparator;

import java.util.Date;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.databinding.DialogCaseEpidBurialEditLayoutBinding;
import de.symeda.sormas.app.databinding.DialogCaseEpidTravelEditLayoutBinding;
import de.symeda.sormas.app.databinding.DialogPreviousHospitalizationLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.Callback;

final class CaseValidator {

    static void initializeEpiDataBurialValidation(final DialogCaseEpidBurialEditLayoutBinding contentBinding) {
        Callback burialDateFromCallback = new Callback() {
            public void call() {
                if (contentBinding.epiDataBurialBurialDateFrom.getValue() != null && contentBinding.epiDataBurialBurialDateTo.getValue() != null) {
                    if (contentBinding.epiDataBurialBurialDateFrom.getValue().after(contentBinding.epiDataBurialBurialDateTo.getValue())) {
                        contentBinding.epiDataBurialBurialDateFrom.enableErrorState(
                                I18nProperties.getValidationError("beforeDate",
                                        contentBinding.epiDataBurialBurialDateFrom.getCaption(),
                                        contentBinding.epiDataBurialBurialDateTo.getCaption()));
                    } else {
                        contentBinding.epiDataBurialBurialDateFrom.disableErrorState();
                    }
                } else {
                    contentBinding.epiDataBurialBurialDateFrom.disableErrorState();
                }
            }
        };

        Callback burialDateToCallback = new Callback() {
            public void call() {
                if (contentBinding.epiDataBurialBurialDateTo.getValue() != null && contentBinding.epiDataBurialBurialDateFrom.getValue() != null) {
                    if (contentBinding.epiDataBurialBurialDateTo.getValue().before(contentBinding.epiDataBurialBurialDateFrom.getValue())) {
                        contentBinding.epiDataBurialBurialDateTo.enableErrorState(
                                I18nProperties.getValidationError("afterDate",
                                        contentBinding.epiDataBurialBurialDateTo.getCaption(),
                                        contentBinding.epiDataBurialBurialDateFrom.getCaption()));
                    } else {
                        contentBinding.epiDataBurialBurialDateTo.disableErrorState();
                    }
                } else {
                    contentBinding.epiDataBurialBurialDateTo.disableErrorState();
                }
            }
        };

        contentBinding.epiDataBurialBurialDateFrom.setValidationCallback(burialDateFromCallback);
        contentBinding.epiDataBurialBurialDateTo.setValidationCallback(burialDateToCallback);
    }

    static void initializeEpiDataTravelValidation(final DialogCaseEpidTravelEditLayoutBinding contentBinding) {
        Callback travelDateFromCallback = new Callback() {
            @Override
            public void call() {
                if (contentBinding.epiDataTravelTravelDateFrom.getValue() != null && contentBinding.epiDataTravelTravelDateTo.getValue() != null) {
                    if (contentBinding.epiDataTravelTravelDateFrom.getValue().after(contentBinding.epiDataTravelTravelDateTo.getValue())) {
                        contentBinding.epiDataTravelTravelDateFrom.enableErrorState(
                                I18nProperties.getValidationError("beforeDate",
                                        contentBinding.epiDataTravelTravelDateFrom.getCaption(),
                                        contentBinding.epiDataTravelTravelDateTo.getCaption()));
                    } else {
                        contentBinding.epiDataTravelTravelDateFrom.disableErrorState();
                    }
                } else {
                    contentBinding.epiDataTravelTravelDateFrom.disableErrorState();
                }
            }
        };

        Callback burialDateToCallback = new Callback() {
            @Override
            public void call() {
                if (contentBinding.epiDataTravelTravelDateTo.getValue() != null && contentBinding.epiDataTravelTravelDateFrom.getValue() != null) {
                    if (contentBinding.epiDataTravelTravelDateTo.getValue().before(contentBinding.epiDataTravelTravelDateFrom.getValue())) {
                        contentBinding.epiDataTravelTravelDateTo.enableErrorState(
                                I18nProperties.getValidationError("afterDate",
                                        contentBinding.epiDataTravelTravelDateTo.getCaption(),
                                        contentBinding.epiDataTravelTravelDateFrom.getCaption()));
                    } else {
                        contentBinding.epiDataTravelTravelDateTo.disableErrorState();
                    }
                } else {
                    contentBinding.epiDataTravelTravelDateTo.disableErrorState();
                }
            }
        };

        contentBinding.epiDataTravelTravelDateFrom.setValidationCallback(travelDateFromCallback);
        contentBinding.epiDataTravelTravelDateTo.setValidationCallback(burialDateToCallback);
    }

    static void initializeHospitalizationValidation(final FragmentCaseEditHospitalizationLayoutBinding contentBinding, final Case caze) {
        contentBinding.caseHospitalizationAdmissionDate.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Date value = (Date) field.getValue();
                if (caze.getSymptoms().getOnsetDate() != null && DateTimeComparator.getDateOnlyInstance().compare(value, caze.getSymptoms().getOnsetDate()) <= 0) {
                    contentBinding.caseHospitalizationAdmissionDate.enableWarningState(
                            I18nProperties.getValidationError("afterDateSoft", contentBinding.caseHospitalizationAdmissionDate.getCaption(),
                                    I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE)));
                } else {
                    contentBinding.caseHospitalizationAdmissionDate.disableWarningState();
                }
            }
        });

        Callback admissionDateCallback = new Callback() {
            @Override
            public void call() {
                if (contentBinding.caseHospitalizationAdmissionDate.getValue() != null && contentBinding.caseHospitalizationDischargeDate.getValue() != null) {
                    if (contentBinding.caseHospitalizationAdmissionDate.getValue().after(contentBinding.caseHospitalizationDischargeDate.getValue())) {
                        contentBinding.caseHospitalizationAdmissionDate.enableErrorState(
                                I18nProperties.getValidationError("beforeDate",
                                        contentBinding.caseHospitalizationAdmissionDate.getCaption(),
                                        contentBinding.caseHospitalizationDischargeDate.getCaption()));
                    } else {
                        contentBinding.caseHospitalizationAdmissionDate.disableErrorState();
                    }
                } else {
                    contentBinding.caseHospitalizationAdmissionDate.disableErrorState();
                }
            }
        };

        Callback dischargeDateCallback = new Callback() {
            @Override
            public void call() {
                if (contentBinding.caseHospitalizationDischargeDate.getValue() != null && contentBinding.caseHospitalizationAdmissionDate.getValue() != null) {
                    if (contentBinding.caseHospitalizationDischargeDate.getValue().before(contentBinding.caseHospitalizationAdmissionDate.getValue())) {
                        contentBinding.caseHospitalizationDischargeDate.enableErrorState(
                                I18nProperties.getValidationError("afterDate",
                                        contentBinding.caseHospitalizationDischargeDate.getCaption(),
                                        contentBinding.caseHospitalizationAdmissionDate.getCaption()));
                    } else {
                        contentBinding.caseHospitalizationDischargeDate.disableErrorState();
                    }
                } else {
                    contentBinding.caseHospitalizationDischargeDate.disableErrorState();
                }
            }
        };

        contentBinding.caseHospitalizationAdmissionDate.setValidationCallback(admissionDateCallback);
        contentBinding.caseHospitalizationDischargeDate.setValidationCallback(dischargeDateCallback);
    }

    static void initializePreviousHospitalizationValidation(final DialogPreviousHospitalizationLayoutBinding contentBinding) {
        Callback admissionDateCallback = new Callback() {
            @Override
            public void call() {
                if (contentBinding.casePreviousHospitalizationAdmissionDate.getValue() != null && contentBinding.casePreviousHospitalizationDischargeDate.getValue() != null) {
                    if (contentBinding.casePreviousHospitalizationAdmissionDate.getValue().after(contentBinding.casePreviousHospitalizationDischargeDate.getValue())) {
                        contentBinding.casePreviousHospitalizationAdmissionDate.enableErrorState(
                                I18nProperties.getValidationError("beforeDate",
                                        contentBinding.casePreviousHospitalizationAdmissionDate.getCaption(),
                                        contentBinding.casePreviousHospitalizationDischargeDate.getCaption()));
                    } else {
                        contentBinding.casePreviousHospitalizationAdmissionDate.disableErrorState();
                    }
                } else {
                    contentBinding.casePreviousHospitalizationAdmissionDate.disableErrorState();
                }
            }
        };

        Callback dischargeDateCallback = new Callback() {
            @Override
            public void call() {
                if (contentBinding.casePreviousHospitalizationDischargeDate.getValue() != null && contentBinding.casePreviousHospitalizationAdmissionDate.getValue() != null) {
                    if (contentBinding.casePreviousHospitalizationDischargeDate.getValue().before(contentBinding.casePreviousHospitalizationAdmissionDate.getValue())) {
                        contentBinding.casePreviousHospitalizationDischargeDate.enableErrorState(
                                I18nProperties.getValidationError("afterDate",
                                        contentBinding.casePreviousHospitalizationDischargeDate.getCaption(),
                                        contentBinding.casePreviousHospitalizationAdmissionDate.getCaption()));
                    } else {
                        contentBinding.casePreviousHospitalizationDischargeDate.disableErrorState();
                    }
                } else {
                    contentBinding.casePreviousHospitalizationDischargeDate.disableErrorState();
                }
            }
        };

        contentBinding.casePreviousHospitalizationAdmissionDate.setValidationCallback(admissionDateCallback);
        contentBinding.casePreviousHospitalizationDischargeDate.setValidationCallback(dischargeDateCallback);
    }

}
