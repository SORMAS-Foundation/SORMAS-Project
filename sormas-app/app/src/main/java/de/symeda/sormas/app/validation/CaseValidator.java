package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.databinding.CaseDataFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.CaseNewFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 20.07.2017.
 */
public class CaseValidator {

    public static void clearErrorsForCaseData(CaseDataFragmentLayoutBinding binding) {
        binding.caseDataDisease.setError(null);
        binding.caseDataHealthFacility.setError(null);
        binding.caseDataFacilityDetails.setError(null);
    }

    public static void clearErrorsForNewCase(CaseNewFragmentLayoutBinding binding) {
        binding.caseDataFirstName.setError(null);
        binding.caseDataLastName.setError(null);
        binding.caseDataDisease.setError(null);
        binding.caseDataRegion.setError(null);
        binding.caseDataDistrict.setError(null);
        binding.caseDataCommunity.setError(null);
        binding.caseDataHealthFacility.setError(null);
        binding.caseDataFacilityDetails.setError(null);
    }

    /**
     * Validates whether the Case Data entered are valid. Fields should be processed from bottom to top according to
     * their arrangement in the layout to make sure that the error message popup is displayed for the first invalid field.
     */
    public static boolean validateCaseData(Case caze, CaseDataFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Health facility & description
        if (caze.getHealthFacility() == null) {
            binding.caseDataHealthFacility.setError(resources.getString(R.string.validation_health_facility));
            success = false;
        } else {
            if (caze.getHealthFacility().getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
                if (caze.getHealthFacilityDetails() == null || caze.getHealthFacilityDetails().trim().isEmpty()) {
                    binding.caseDataFacilityDetails.setError(resources.getString(R.string.validation_health_facility_details));
                    success = false;
                }
            }
        }

        // Disease
        if (caze.getDisease() == null) {
            binding.caseDataDisease.setError(resources.getString(R.string.validation_case_disease));
            success = false;
        }

        return success;
    }

    public static boolean validateNewCase(Case caze, CaseNewFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Health facility & description
        if (caze.getHealthFacility() == null) {
            binding.caseDataHealthFacility.setError(resources.getString(R.string.validation_health_facility));
            success = false;
        } else {
            if (caze.getHealthFacility().getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
                if (caze.getHealthFacilityDetails() == null || caze.getHealthFacilityDetails().trim().isEmpty()) {
                    binding.caseDataFacilityDetails.setError(resources.getString(R.string.validation_health_facility_details));
                    success = false;
                }
            }
        }

        // Community/Ward
        if (caze.getCommunity() == null) {
            binding.caseDataCommunity.setError(resources.getString(R.string.validation_community));
            success = false;
        }

        // District/LGA
        if (caze.getDistrict() == null) {
            binding.caseDataDistrict.setError(resources.getString(R.string.validation_district));
            success = false;
        }

        // Region/State
        if (caze.getRegion() == null) {
            binding.caseDataRegion.setError(resources.getString(R.string.validation_region));
            success = false;
        }

        // Disease
        if (caze.getDisease() == null) {
            binding.caseDataDisease.setError(resources.getString(R.string.validation_case_disease));
            success = false;
        }

        // Last name
        if (caze.getPerson().getLastName() == null || caze.getPerson().getLastName().trim().isEmpty()) {
            binding.caseDataLastName.setError(resources.getString(R.string.validation_person_last_name));
            success = false;
        }

        // First name
        if (caze.getPerson().getFirstName() == null || caze.getPerson().getFirstName().trim().isEmpty()) {
            binding.caseDataFirstName.setError(resources.getString(R.string.validation_person_first_name));
            success = false;
        }

        return success;
    }

}
