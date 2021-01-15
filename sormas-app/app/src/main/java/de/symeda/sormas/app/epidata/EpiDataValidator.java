/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.epidata;

import org.joda.time.DateTimeComparator;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.app.databinding.DialogEpidBurialEditLayoutBinding;
import de.symeda.sormas.app.databinding.DialogEpidTravelEditLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

final class EpiDataValidator {

    static void initializeEpiDataBurialValidation(final DialogEpidBurialEditLayoutBinding contentBinding) {
        ResultCallback<Boolean> burialDateFromCallback = () -> {
            if (contentBinding.epiDataBurialBurialDateFrom.getValue() != null && contentBinding.epiDataBurialBurialDateTo.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance()
                        .compare(contentBinding.epiDataBurialBurialDateFrom.getValue(), contentBinding.epiDataBurialBurialDateTo.getValue())
                        > 0) {
                    contentBinding.epiDataBurialBurialDateFrom.enableErrorState(
                            I18nProperties.getValidationError(
                                    Validations.beforeDate,
                                    contentBinding.epiDataBurialBurialDateFrom.getCaption(),
                                    contentBinding.epiDataBurialBurialDateTo.getCaption()));
                    return true;
                }
            }

            return false;
        };

        ResultCallback<Boolean> burialDateToCallback = () -> {
            if (contentBinding.epiDataBurialBurialDateTo.getValue() != null && contentBinding.epiDataBurialBurialDateFrom.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance()
                        .compare(contentBinding.epiDataBurialBurialDateTo.getValue(), contentBinding.epiDataBurialBurialDateFrom.getValue())
                        < 0) {
                    contentBinding.epiDataBurialBurialDateTo.enableErrorState(
                            I18nProperties.getValidationError(
                                    Validations.afterDate,
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

    static void initializeEpiDataTravelValidation(final DialogEpidTravelEditLayoutBinding contentBinding) {
        ResultCallback<Boolean> travelDateFromCallback = () -> {
            if (contentBinding.epiDataTravelTravelDateFrom.getValue() != null && contentBinding.epiDataTravelTravelDateTo.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance()
                        .compare(contentBinding.epiDataTravelTravelDateFrom.getValue(), contentBinding.epiDataTravelTravelDateTo.getValue())
                        > 0) {
                    contentBinding.epiDataTravelTravelDateFrom.enableErrorState(
                            I18nProperties.getValidationError(
                                    Validations.beforeDate,
                                    contentBinding.epiDataTravelTravelDateFrom.getCaption(),
                                    contentBinding.epiDataTravelTravelDateTo.getCaption()));
                    return true;
                }
            }

            return false;
        };

        ResultCallback<Boolean> burialDateToCallback = () -> {
            if (contentBinding.epiDataTravelTravelDateTo.getValue() != null && contentBinding.epiDataTravelTravelDateFrom.getValue() != null) {
                if (DateTimeComparator.getDateOnlyInstance()
                        .compare(contentBinding.epiDataTravelTravelDateTo.getValue(), contentBinding.epiDataTravelTravelDateFrom.getValue())
                        < 0) {
                    contentBinding.epiDataTravelTravelDateTo.enableErrorState(
                            I18nProperties.getValidationError(
                                    Validations.afterDate,
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
}
