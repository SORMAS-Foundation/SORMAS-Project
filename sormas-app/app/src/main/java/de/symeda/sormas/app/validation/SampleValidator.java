package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.databinding.SampleDataFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */

public final class SampleValidator {

    public static void clearErrorsForSampleData(SampleDataFragmentLayoutBinding binding) {
        binding.sampleDateTime.setError(null);
        binding.sampleMaterial.setError(null);
        binding.sampleMaterialText.setError(null);
        binding.sampleShipmentStatus.setError(null);
        binding.sampleShipmentDate.setError(null);
        binding.sampleLab.setError(null);
    }

    public static boolean validateSampleData(Sample sample, SampleDataFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Shipment status
        if (sample.getShipmentStatus() == null) {
            binding.sampleShipmentStatus.setError(resources.getString(R.string.validation_sample_shipment_status));
            success = false;
        } else {
            if (!sample.getShipmentStatus().equals(ShipmentStatus.NOT_SHIPPED)) {
                if (sample.getShipmentDate() == null) {
                    binding.sampleShipmentDate.setError(resources.getString(R.string.validation_sample_shipment_date));
                    success = false;
                }
            }
        }

        // Sample lab
        if (sample.getLab() == null) {
            binding.sampleLab.setError(resources.getString(R.string.validation_sample_lab));
            success = false;
        }

        // Sample material & details
        if (sample.getSampleMaterial() == null) {
            binding.sampleMaterial.setError(resources.getString(R.string.validation_sample_material));
            success = false;
        } else {
            if (sample.getSampleMaterial().equals(SampleMaterial.OTHER)) {
                if (sample.getSampleMaterialText() == null || sample.getSampleMaterialText().trim().isEmpty()) {
                    binding.sampleMaterialText.setError(resources.getString(R.string.validation_sample_material_details));
                    success = false;
                }
            }
        }

        // Sample date & time
        if (sample.getSampleDateTime() == null) {
            binding.sampleDateTime.setError(resources.getString(R.string.validation_sample_date_time));
            success = false;
        }

        return success;
    }

}
