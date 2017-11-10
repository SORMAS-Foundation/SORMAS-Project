package de.symeda.sormas.app.hospitalization;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.AbstractFormDialogFragment;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.databinding.PreviousHospitalizationEditFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.validation.PreviousHospitalizationValidator;


public class PreviousHospitalizationForm extends AbstractFormDialogFragment<PreviousHospitalization> {

    private PreviousHospitalizationEditFragmentLayoutBinding binding;

    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.previous_hospitalization_edit_fragment_layout, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setPrevHosp(getFormItem());
        binding.prevHospAdmissionDate.initialize(this);
        binding.prevHospDischargeDate.initialize(this);

        initFacilityFields(binding.getPrevHosp());
    }

    private void initFacilityFields(PreviousHospitalization prevHosp) {

        final List emptyList = new ArrayList<>();
        final List districtsByRegion = DataUtils.toItems(prevHosp.getRegion() != null ? DatabaseHelper.getDistrictDao().getByRegion(prevHosp.getRegion()) : DataUtils.toItems(emptyList), true);
        final List communitiesByDistrict = DataUtils.toItems(prevHosp.getDistrict() != null ? DatabaseHelper.getCommunityDao().getByDistrict(prevHosp.getDistrict()) : DataUtils.toItems(emptyList), true);
        final List facilities = DataUtils.toItems(prevHosp.getCommunity() != null ? DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(prevHosp.getCommunity(), true) :
                prevHosp.getDistrict() != null ? DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict(prevHosp.getDistrict(), true) : DataUtils.toItems(emptyList), true);

        FieldHelper.initRegionSpinnerField(binding.prevHospRegion, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedValue = binding.prevHospRegion.getValue();
                if(binding.prevHospDistrict != null) {
                    List<District> districtList = emptyList;
                    if(selectedValue != null) {
                        districtList = DatabaseHelper.getDistrictDao().getByRegion((Region)selectedValue);
                    } else {
                        binding.prevHospDistrict.setValue(null);
                    }
                    binding.prevHospDistrict.setAdapterAndValue(binding.prevHospDistrict.getValue(), DataUtils.toItems(districtList));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        FieldHelper.initSpinnerField(binding.prevHospDistrict, districtsByRegion, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedValue = binding.prevHospDistrict.getValue();
                if(binding.prevHospCommunity != null) {
                    List<Community> communityList = emptyList;
                    List<Facility> facilityList = emptyList;
                    if(selectedValue != null) {
                        communityList = DatabaseHelper.getCommunityDao().getByDistrict((District)selectedValue);
                        facilityList = DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict((District) selectedValue, true);
                    } else {
                        binding.prevHospCommunity.setValue(null);
                    }
                    binding.prevHospCommunity.setAdapterAndValue(binding.prevHospCommunity.getValue(), DataUtils.toItems(communityList));
                    binding.prevHospHealthFacility.setAdapterAndValue(binding.prevHospHealthFacility.getValue(), DataUtils.toItems(facilityList));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        FieldHelper.initSpinnerField(binding.prevHospCommunity, communitiesByDistrict, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField spinnerField = binding.prevHospHealthFacility;
                Object selectedValue = binding.prevHospCommunity.getValue();
                if (spinnerField != null) {
                    List<Facility> facilityList = emptyList;
                    if (selectedValue != null) {
                        facilityList = DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity((Community) selectedValue, true);
                    } else if (binding.prevHospDistrict.getValue() != null) {
                        facilityList = DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict((District) binding.prevHospDistrict.getValue(), true);
                    } else {
                        spinnerField.setValue(null);
                    }
                    spinnerField.setAdapterAndValue(binding.prevHospHealthFacility.getValue(), DataUtils.toItems(facilityList));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        FieldHelper.initSpinnerField(binding.prevHospHealthFacility, facilities);

        PreviousHospitalizationValidator.setRequiredHintsForPreviousHospitalization(binding);
    }

    public PreviousHospitalizationEditFragmentLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getPrevHosp();
    }
}
