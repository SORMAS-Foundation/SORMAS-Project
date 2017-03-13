package de.symeda.sormas.app.hospitalization;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.annotations.Nullable;

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
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.databinding.PreviousHospitalizationEditFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;


public class PreviousHospitalizationTab extends AbstractFormDialogFragment<PreviousHospitalization> {

    private PreviousHospitalizationEditFragmentLayoutBinding binding;
    private boolean facilityFieldsInitialized = false;

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


        FieldHelper.initFacilitySpinnerField(binding.prevHospHealthFacility);
        binding.prevHospHealthFacility.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                if(!facilityFieldsInitialized) {
                    fillFacilityFields();
                }
            }
        });
    }

    private void fillFacilityFields() {
        Facility facility = (Facility) binding.prevHospHealthFacility.getValue();

        final List emptyList = new ArrayList<>();
        List districtList = new ArrayList<>();
        List communityList = new ArrayList<>();

        FieldHelper.initRegionSpinnerField(binding.prevHospFacilityRegion, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedValue = binding.prevHospFacilityRegion.getValue();
                if(binding.prevHospFacilityDistrict != null) {
                    List<District> districtList = emptyList;
                    if(selectedValue != null) {
                        districtList = DatabaseHelper.getDistrictDao().getByRegion((Region)selectedValue);
                    }
                    binding.prevHospFacilityDistrict.setAdapterAndValue(binding.prevHospFacilityDistrict.getValue(), DataUtils.toItems(districtList));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if(facility != null) {
            binding.prevHospFacilityRegion.setValue(facility.getLocation().getRegion());
            districtList = DataUtils.toItems(DatabaseHelper.getDistrictDao().getByRegion(facility.getLocation().getRegion()));
        }

        FieldHelper.initSpinnerField(binding.prevHospFacilityDistrict, districtList, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedValue = binding.prevHospFacilityDistrict.getValue();
                if(binding.prevHospFacilityCommunity != null) {
                    List<Community> communityList = emptyList;
                    if(selectedValue != null) {
                        communityList = DatabaseHelper.getCommunityDao().getByDistrict((District)selectedValue);
                    }
                    binding.prevHospFacilityCommunity.setAdapterAndValue(binding.prevHospFacilityCommunity.getValue(), DataUtils.toItems(communityList));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if(facility != null) {
            binding.prevHospFacilityDistrict.setValue(facility.getLocation().getDistrict());
            communityList = DataUtils.toItems(DatabaseHelper.getCommunityDao().getByDistrict(facility.getLocation().getDistrict()));
        }

        FieldHelper.initSpinnerField(binding.prevHospFacilityCommunity, communityList, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(facilityFieldsInitialized) {
                    SpinnerField spinnerField = binding.prevHospHealthFacility;
                    Object selectedValue = binding.prevHospFacilityCommunity.getValue();
                    if (spinnerField != null) {
                        List<Facility> facilityList = emptyList;
                        if (selectedValue != null) {
                            facilityList = DatabaseHelper.getFacilityDao().getByCommunity((Community) selectedValue);
                        }
                        spinnerField.setAdapterAndValue(binding.prevHospHealthFacility.getValue(), DataUtils.toItems(facilityList));
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if(facility != null) {
            binding.prevHospFacilityCommunity.setValue(facility.getLocation().getCommunity());
        }

        facilityFieldsInitialized = true;
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getPrevHosp();
    }
}
