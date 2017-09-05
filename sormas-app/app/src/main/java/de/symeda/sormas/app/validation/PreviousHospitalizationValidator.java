package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.PreviousHospitalizationEditFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 04.09.2017.
 */
public final class PreviousHospitalizationValidator {

    public static boolean validatePreviousHospitalizationData(PreviousHospitalizationEditFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        PreviousHospitalization prevHosp = binding.getPrevHosp();
        boolean success = true;

        // Health facility
        if (prevHosp.getHealthFacility() == null) {
            binding.prevHospHealthFacility.setError(resources.getString(R.string.validation_health_facility));
            success = false;
        }

        // Community/Ward
        if (prevHosp.getCommunity() == null) {
            binding.prevHospCommunity.setError(resources.getString(R.string.validation_community));
            success = false;
        }

        // District/LGA
        if (prevHosp.getDistrict() == null) {
            binding.prevHospDistrict.setError(resources.getString(R.string.validation_district));
            success = false;
        }

        // Region/State
        if (prevHosp.getRegion() == null) {
            binding.prevHospRegion.setError(resources.getString(R.string.validation_region));
            success = false;
        }

        // Discharge date
        if (prevHosp.getDischargeDate() == null) {
            binding.prevHospDischargeDate.setError(resources.getString(R.string.validation_prev_hosp_discharge_date));
            success = false;
        }

        // Admission date
        if (prevHosp.getAdmissionDate() == null) {
            binding.prevHospAdmissionDate.setError(resources.getString(R.string.validation_prev_hosp_admission_date));
            success = false;
        }

        return success;
    }

    public static void clearErrorsForPreviousHospitalization(PreviousHospitalizationEditFragmentLayoutBinding binding) {
        for (PropertyField field : getPreviousHospitalizationFields(binding)) {
            field.clearError();
        }
    }

    public static void setRequiredHintsForPreviousHospitalization(PreviousHospitalizationEditFragmentLayoutBinding binding) {
        for (PropertyField field : getPreviousHospitalizationFields(binding)) {
            field.setRequiredHint(true);
        }
    }

    private static final List<PropertyField<?>> getPreviousHospitalizationFields(PreviousHospitalizationEditFragmentLayoutBinding binding) {
        return Arrays.asList(binding.prevHospHealthFacility, binding.prevHospCommunity, binding.prevHospDistrict,
                binding.prevHospRegion, binding.prevHospDischargeDate, binding.prevHospAdmissionDate);
    }

}
