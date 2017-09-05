package de.symeda.sormas.app.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.databinding.MoveCaseFragmentLayoutBinding;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.validation.CaseValidator;

/**
 * Created by Mate Strysewske on 24.08.2017.
 */
public class FacilityChangeDialogBuilder extends AlertDialog.Builder {

    public static final String NONE_HEALTH_FACILITY_DETAILS = "noneHealthFacilityDetails";

    private final MoveCaseFragmentLayoutBinding binding;
    private final Consumer callback;
    private final Tracker tracker;

    private Case caze;

    public FacilityChangeDialogBuilder(FragmentActivity activity, final Case formCase, final Consumer callback) {
        super(activity);
        this.setTitle(activity.getResources().getString(R.string.headline_move_case));
        binding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.move_case_fragment_layout, null, false);
        tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();

        // Need to retrieve the case from the database because we don't want to change the actual bounded case
        Case caze = DatabaseHelper.getCaseDao().queryForId(formCase.getId());
        binding.setCaze(caze);

        final View dialogView = binding.getRoot();
        this.setView(dialogView);
        this.callback = callback;
        this.caze = caze;

        final List emptyList = new ArrayList<>();
        final List districtsByRegion = DataUtils.toItems(caze.getRegion() != null ? DatabaseHelper.getDistrictDao().getByRegion(caze.getRegion()) : DataUtils.toItems(emptyList), true);
        final List communitiesByDistrict = DataUtils.toItems(caze.getDistrict() != null ? DatabaseHelper.getCommunityDao().getByDistrict(caze.getDistrict()) : DataUtils.toItems(emptyList), true);
        final List facilitiesByCommunity = DataUtils.toItems(caze.getCommunity() != null ? DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(caze.getCommunity(), true) : DataUtils.toItems(emptyList), true);

        FieldHelper.initRegionSpinnerField(binding.caseDataRegion, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField districtSpinner = binding.caseDataDistrict;
                Object selectedValue = binding.caseDataRegion.getValue();
                if(districtSpinner != null) {
                    List<District> districtList = emptyList;
                    if(selectedValue != null) {
                        districtList = DatabaseHelper.getDistrictDao().getByRegion((Region)selectedValue);
                    }
                    districtSpinner.setAdapterAndValue(districtSpinner.getValue(), DataUtils.toItems(districtList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FieldHelper.initSpinnerField(binding.caseDataDistrict, districtsByRegion, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField spinnerField = binding.caseDataCommunity;
                Object selectedValue = binding.caseDataDistrict.getValue();
                if(spinnerField != null) {
                    List<Community> communityList = emptyList;
                    if(selectedValue != null) {
                        communityList = DatabaseHelper.getCommunityDao().getByDistrict((District)selectedValue);
                    }
                    spinnerField.setAdapterAndValue(spinnerField.getValue(), DataUtils.toItems(communityList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FieldHelper.initSpinnerField(binding.caseDataCommunity, communitiesByDistrict, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField spinnerField = binding.caseDataHealthFacility;
                Object selectedValue = binding.caseDataCommunity.getValue();
                if(spinnerField != null) {
                    List<Facility> facilityList = emptyList;
                    if(selectedValue != null) {
                        facilityList = DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity((Community)selectedValue, true);
                    }
                    spinnerField.setAdapterAndValue(spinnerField.getValue(), DataUtils.toItems(facilityList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FieldHelper.initSpinnerField(binding.caseDataHealthFacility, facilitiesByCommunity);

        binding.caseDataHealthFacility.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                TextField facilityDetailsField = binding.caseDataFacilityDetails;

                Facility selectedFacility = (Facility) field.getValue();
                if (selectedFacility != null) {
                    boolean otherHealthFacility = selectedFacility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
                    boolean noneHealthFacility = selectedFacility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
                    if (otherHealthFacility) {
                        facilityDetailsField.setVisibility(View.VISIBLE);
                        facilityDetailsField.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
                    } else if (noneHealthFacility) {
                        facilityDetailsField.setVisibility(View.VISIBLE);
                        facilityDetailsField.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, NONE_HEALTH_FACILITY_DETAILS));
                    } else {
                        facilityDetailsField.setVisibility(View.GONE);
                    }
                } else {
                    facilityDetailsField.setVisibility(View.GONE);
                }

                facilityDetailsField.setRequiredHint(true);
            }
        });

        this.setPositiveButton(activity.getResources().getString(R.string.action_move), null);
        this.setNegativeButton(activity.getResources().getString(R.string.action_cancel), null);

        CaseValidator.setRequiredHintsForMoveCaseData(binding);
    }

    public void setButtonListeners(final AlertDialog dialog, final Activity activity) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CaseValidator.clearErrorsForMoveCaseData(binding);
                if (CaseValidator.validateMoveCaseData(binding)) {
                    try {
                        DatabaseHelper.getCaseDao().moveCase(caze);
                        callback.accept(true);
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to move case", e);
                        ErrorReportingHelper.sendCaughtException(tracker, e, caze, true);
                        callback.accept(false);
                    }

                    dialog.dismiss();
                }
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

}
