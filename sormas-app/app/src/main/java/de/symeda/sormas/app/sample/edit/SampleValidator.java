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
                    } else {
                        contentBinding.sampleSampleDateTime.disableErrorState();
                    }
                } else {
                    contentBinding.sampleSampleDateTime.disableErrorState();
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
                    } else {
                        contentBinding.sampleShipmentDate.disableErrorState();
                    }
                } else {
                    contentBinding.sampleShipmentDate.disableErrorState();
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
                    } else {
                        contentBinding.sampleSampleDateTime.disableErrorState();
                    }
                } else {
                    contentBinding.sampleSampleDateTime.disableErrorState();
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
                    } else {
                        contentBinding.sampleShipmentDate.disableErrorState();
                    }
                } else {
                    contentBinding.sampleShipmentDate.disableErrorState();
                }
            }
        };

        contentBinding.sampleSampleDateTime.setValidationCallback(sampleDateCallback);
        contentBinding.sampleShipmentDate.setValidationCallback(shipmentDateCallback);
    }

}
