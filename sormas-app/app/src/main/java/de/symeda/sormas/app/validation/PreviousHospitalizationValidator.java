package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.databinding.PreviousHospitalizationEditFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 04.09.2017.
 */
public final class PreviousHospitalizationValidator {

    public static boolean validatePreviousHospitalizationData(PreviousHospitalizationEditFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        PreviousHospitalization prevHosp = binding.getPrevHosp();
        boolean success = true;

        // Health facility & description
        if (prevHosp.getHealthFacility() == null) {
            binding.prevHospHealthFacility.setError(resources.getString(R.string.validation_health_facility));
            success = false;
        } else {
            if (prevHosp.getHealthFacility().getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
                if (prevHosp.getHealthFacilityDetails() == null || prevHosp.getHealthFacilityDetails().trim().isEmpty()) {
                    binding.prevHospHealthFacilityDetails.setError(resources.getString(R.string.validation_health_facility_details));
                    success = false;
                }
            }
            if (prevHosp.getHealthFacility().getUuid().equals(FacilityDto.NONE_FACILITY_UUID)) {
                if (prevHosp.getHealthFacilityDetails() == null || prevHosp.getHealthFacilityDetails().trim().isEmpty()) {
                    binding.prevHospHealthFacilityDetails.setError(resources.getString(R.string.validation_none_health_facility_details));
                    success = false;
                }
            }
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

    private static final List<PropertyField<? extends Object>> getPreviousHospitalizationFields(PreviousHospitalizationEditFragmentLayoutBinding binding) {
        return Arrays.asList(binding.prevHospHealthFacility, binding.prevHospHealthFacilityDetails, binding.prevHospDistrict, binding.prevHospRegion);
    }

}