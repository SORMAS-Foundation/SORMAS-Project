package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.EditTeboPropertyField;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentSampleNewLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */

public final class SampleValidator {

    public static boolean validateSampleData(INotificationContext activityContext, Sample sample, FragmentSampleEditLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Shipped
        if (sample.isShipped()) {
            if (sample.getShipmentDate() == null) {
                binding.dtpShipmentDate.enableErrorState(activityContext, R.string.validation_sample_material);
                success = false;
            }
        }

        // Sample lab
        if (sample.getLab() == null) {
            binding.spnLaboratory.enableErrorState(activityContext, R.string.validation_sample_material);
            success = false;
        } else {
            if (sample.getLab().getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID) && sample.getLabDetails().trim().isEmpty()) {
                binding.sampleLabDetails.setError(resources.getString(R.string.validation_sample_lab_details));
                success = false;
            }
        }

        // Sample material & details
        if (sample.getSampleMaterial() == null) {
            binding.spnSampleMaterial.enableErrorState(activityContext, R.string.validation_sample_material);
            success = false;
        } else {
            if (sample.getSampleMaterial().equals(SampleMaterial.OTHER)) {
                if (sample.getSampleMaterialText() == null || sample.getSampleMaterialText().trim().isEmpty()) {
                    binding.spnSampleMaterial.enableErrorState(activityContext, R.string.validation_sample_material_details);
                    success = false;
                }
            }
        }

        // Sample date & time
        if (sample.getSampleDateTime() == null) {
            binding.dtpDateAndTimeOfSampling.enableErrorState(activityContext, R.string.validation_sample_date_time);
            //binding.sampleDateTime.setError(resources.getString(R.string.validation_sample_date_time));
            success = false;
        }

        return success;
    }

    public static void clearErrorsForSampleData(FragmentSampleEditLayoutBinding binding) {
        for (EditTeboPropertyField field : getSampleDataFields(binding)) {
            //field.clearError();
        }
    }

    public static void setRequiredHintsForSampleData(FragmentSampleEditLayoutBinding binding) {
        for (EditTeboPropertyField field : getSampleDataFields(binding)) {
            field.setRequired(true);
        }
    }

    private static final List<? extends EditTeboPropertyField<?>> getSampleDataFields(FragmentSampleEditLayoutBinding binding) {
        return Arrays.asList(binding.dtpDateAndTimeOfSampling, binding.spnSampleMaterial, binding.txtOtherSample,
                binding.dtpShipmentDate, binding.spnLaboratory);
    private static final List<PropertyField<?>> getSampleDataFields(SampleDataFragmentLayoutBinding binding) {
        return Arrays.asList(binding.sampleDateTime, binding.sampleMaterial, binding.sampleMaterialText,
                binding.sampleShipmentDate, binding.sampleLab, binding.sampleLabDetails);
    }




    public static boolean validateSampleData(INotificationContext activityContext, Sample sample, FragmentSampleNewLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Shipped
        if (sample.isShipped()) {
            if (sample.getShipmentDate() == null) {
                binding.dtpShipmentDate.enableErrorState(activityContext, R.string.validation_sample_material);
                success = false;
            }
        }

        // Sample lab
        if (sample.getLab() == null) {
            binding.spnLaboratory.enableErrorState(activityContext, R.string.validation_sample_material);
            success = false;
        }

        // Sample material & details
        if (sample.getSampleMaterial() == null) {
            binding.spnSampleMaterial.enableErrorState(activityContext, R.string.validation_sample_material);
            success = false;
        } else {
            if (sample.getSampleMaterial().equals(SampleMaterial.OTHER)) {
                if (sample.getSampleMaterialText() == null || sample.getSampleMaterialText().trim().isEmpty()) {
                    binding.spnSampleMaterial.enableErrorState(activityContext, R.string.validation_sample_material_details);
                    success = false;
                }
            }
        }

        // Sample date & time
        if (sample.getSampleDateTime() == null) {
            binding.dtpDateAndTimeOfSampling.enableErrorState(activityContext, R.string.validation_sample_date_time);
            //binding.sampleDateTime.setError(resources.getString(R.string.validation_sample_date_time));
            success = false;
        }

        return success;
    }

    public static void clearErrorsForSampleData(FragmentSampleNewLayoutBinding binding) {
        for (EditTeboPropertyField field : getSampleDataFields(binding)) {
            //field.clearError();
        }
    }

    private static final List<? extends EditTeboPropertyField<?>> getSampleDataFields(FragmentSampleNewLayoutBinding binding) {
        return Arrays.asList(binding.dtpDateAndTimeOfSampling, binding.spnSampleMaterial, binding.txtOtherSample,
                binding.dtpShipmentDate, binding.spnLaboratory);
    }

}
