package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentSampleNewLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */

public final class SampleValidator {

    public static boolean validateSampleData(NotificationContext activityContext, Sample sample, FragmentSampleEditLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Shipped
        if (sample.isShipped()) {
            if (sample.getShipmentDate() == null) {
                binding.sampleShipmentDate.enableErrorState(activityContext, R.string.validation_sample_material);
                success = false;
            }
        }

        // Sample lab
        if (sample.getLab() == null) {
            binding.sampleLab.enableErrorState(activityContext, R.string.validation_sample_material);
            success = false;
        } else {
            if (sample.getLab().getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID) && sample.getLabDetails().trim().isEmpty()) {
                // TODO #558
                //binding.sampleLabDetails.setError(resources.getString(R.string.validation_sample_lab_details));
                success = false;
            }
        }

        // Sample material & details
        if (sample.getSampleMaterial() == null) {
            binding.sampleSampleMaterial.enableErrorState(activityContext, R.string.validation_sample_material);
            success = false;
        } else {
            if (sample.getSampleMaterial().equals(SampleMaterial.OTHER)) {
                if (sample.getSampleMaterialText() == null || sample.getSampleMaterialText().trim().isEmpty()) {
                    binding.sampleSampleMaterial.enableErrorState(activityContext, R.string.validation_sample_material_details);
                    success = false;
                }
            }
        }

        // Sample date & time
        if (sample.getSampleDateTime() == null) {
            binding.sampleSampleDateTime.enableErrorState(activityContext, R.string.validation_sample_date_time);
            //binding.sampleDateTime.setError(resources.getString(R.string.validation_sample_date_time));
            success = false;
        }

        return success;
    }

    public static void clearErrorsForSampleData(FragmentSampleEditLayoutBinding binding) {
        for (ControlPropertyEditField field : getSampleDataFields(binding)) {
            //field.clearError();
        }
    }

    public static void setRequiredHintsForSampleData(FragmentSampleEditLayoutBinding binding) {
        for (ControlPropertyEditField field : getSampleDataFields(binding)) {
            field.setRequired(true);
        }
    }

    private static final List<? extends ControlPropertyEditField<?>> getSampleDataFields(FragmentSampleEditLayoutBinding binding) {
        // TODO #558 sampleDetails
        return Arrays.asList(binding.sampleSampleDateTime, binding.sampleSampleMaterial, binding.sampleSampleMaterialText,
                binding.sampleShipmentDate, binding.sampleLab);
    }

    public static boolean validateSampleData(NotificationContext activityContext, Sample sample, FragmentSampleNewLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Shipped
        if (sample.isShipped()) {
            if (sample.getShipmentDate() == null) {
                binding.sampleShipmentDate.enableErrorState(activityContext, R.string.validation_sample_material);
                success = false;
            }
        }

        // Sample lab
        if (sample.getLab() == null) {
            binding.sampleLab.enableErrorState(activityContext, R.string.validation_sample_material);
            success = false;
        }

        // Sample material & details
        if (sample.getSampleMaterial() == null) {
            binding.sampleSampleMaterial.enableErrorState(activityContext, R.string.validation_sample_material);
            success = false;
        } else {
            if (sample.getSampleMaterial().equals(SampleMaterial.OTHER)) {
                if (sample.getSampleMaterialText() == null || sample.getSampleMaterialText().trim().isEmpty()) {
                    binding.sampleSampleMaterial.enableErrorState(activityContext, R.string.validation_sample_material_details);
                    success = false;
                }
            }
        }

        // Sample date & time
        if (sample.getSampleDateTime() == null) {
            binding.sampleSampleDateTime.enableErrorState(activityContext, R.string.validation_sample_date_time);
            //binding.sampleDateTime.setError(resources.getString(R.string.validation_sample_date_time));
            success = false;
        }

        return success;
    }

    public static void clearErrorsForSampleData(FragmentSampleNewLayoutBinding binding) {
        for (ControlPropertyEditField field : getSampleDataFields(binding)) {
            //field.clearError();
        }
    }

    private static final List<? extends ControlPropertyEditField<?>> getSampleDataFields(FragmentSampleNewLayoutBinding binding) {
        return Arrays.asList(binding.sampleSampleDateTime, binding.sampleSampleMaterial, binding.sampleSampleMaterialText,
                binding.sampleShipmentDate, binding.sampleLab);
    }

}
