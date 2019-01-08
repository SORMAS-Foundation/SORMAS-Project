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

package de.symeda.sormas.app.sample.edit;

import java.util.Date;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentSampleNewLayoutBinding;
import de.symeda.sormas.app.util.Callback;

public class SampleValidator {

    static void initializeSampleValidation(final FragmentSampleEditLayoutBinding contentBinding) {
        Callback sampleDateCallback = new Callback() {
            public void call() {
                // Must not be after date of shipment
                if (contentBinding.sampleSampleDateTime.getValue() != null && contentBinding.sampleShipmentDate.getValue() != null) {
                    if (((Date) contentBinding.sampleSampleDateTime.getValue()).after(contentBinding.sampleShipmentDate.getValue())) {
                        contentBinding.sampleSampleDateTime.enableErrorState(
                                I18nProperties.getValidationError("beforeDate",
                                        contentBinding.sampleSampleDateTime.getCaption(),
                                        contentBinding.sampleShipmentDate.getCaption()));
                    }
                }
            }
        };

        Callback shipmentDateCallback = new Callback() {
            public void call() {
                // Must not be before sample date
                if (contentBinding.sampleShipmentDate.getValue() != null && contentBinding.sampleSampleDateTime.getValue() != null) {
                    if (contentBinding.sampleShipmentDate.getValue().before((Date) contentBinding.sampleSampleDateTime.getValue())) {
                        contentBinding.sampleShipmentDate.enableErrorState(
                                I18nProperties.getValidationError("afterDate",
                                        contentBinding.sampleShipmentDate.getCaption(),
                                        contentBinding.sampleSampleDateTime.getCaption()));
                    }
                }
            }
        };

        contentBinding.sampleSampleDateTime.setValidationCallback(sampleDateCallback);
        contentBinding.sampleShipmentDate.setValidationCallback(shipmentDateCallback);
    }

    static void initializeSampleValidation(final FragmentSampleNewLayoutBinding contentBinding) {
        Callback sampleDateCallback = new Callback() {
            public void call() {
                // Must not be after date of shipment
                if (contentBinding.sampleSampleDateTime.getValue() != null && contentBinding.sampleShipmentDate.getValue() != null) {
                    if (((Date) contentBinding.sampleSampleDateTime.getValue()).after(contentBinding.sampleShipmentDate.getValue())) {
                        contentBinding.sampleSampleDateTime.enableErrorState(
                                I18nProperties.getValidationError("beforeDate",
                                        contentBinding.sampleSampleDateTime.getCaption(),
                                        contentBinding.sampleShipmentDate.getCaption()));
                    }
                }
            }
        };

        Callback shipmentDateCallback = new Callback() {
            public void call() {
                // Must not be before sample date
                if (contentBinding.sampleShipmentDate.getValue() != null && contentBinding.sampleSampleDateTime.getValue() != null) {
                    if (contentBinding.sampleShipmentDate.getValue().before((Date) contentBinding.sampleSampleDateTime.getValue())) {
                        contentBinding.sampleShipmentDate.enableErrorState(
                                I18nProperties.getValidationError("afterDate",
                                        contentBinding.sampleShipmentDate.getCaption(),
                                        contentBinding.sampleSampleDateTime.getCaption()));
                    }
                }
            }
        };

        contentBinding.sampleSampleDateTime.setValidationCallback(sampleDateCallback);
        contentBinding.sampleShipmentDate.setValidationCallback(shipmentDateCallback);
    }

}
