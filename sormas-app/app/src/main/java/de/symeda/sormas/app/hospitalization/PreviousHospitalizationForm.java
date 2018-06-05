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

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
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
import de.symeda.sormas.app.component.TextField;
import de.symeda.sormas.app.databinding.PreviousHospitalizationEditFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.validation.PreviousHospitalizationValidator;

import static de.symeda.sormas.app.component.FacilityChangeDialogBuilder.NONE_HEALTH_FACILITY_DETAILS;


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

        binding.prevHospHealthFacility.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                TextField facilityDetailsField = binding.prevHospHealthFacilityDetails;

                Facility selectedFacility = (Facility) field.getValue();
                if (selectedFacility != null) {
                    boolean otherHealthFacility = selectedFacility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
                    boolean noneHealthFacility = selectedFacility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
                    if (otherHealthFacility) {
                        facilityDetailsField.setVisibility(View.VISIBLE);
                        facilityDetailsField.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
                        PreviousHospitalizationValidator.setRequiredHintsForPreviousHospitalization(binding);
                    } else if (noneHealthFacility) {
                        facilityDetailsField.setVisibility(View.VISIBLE);
                        facilityDetailsField.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, NONE_HEALTH_FACILITY_DETAILS));
                        PreviousHospitalizationValidator.setRequiredHintsForPreviousHospitalization(binding);
                    } else {
                        facilityDetailsField.setVisibility(View.GONE);
                    }
                } else {
                    facilityDetailsField.setVisibility(View.GONE);
                }
            }
        });

        PreviousHospitalizationValidator.setRequiredHintsForPreviousHospitalization(binding);
        binding.prevHospAdmissionDate.makeFieldSoftRequired();
        binding.prevHospDischargeDate.makeFieldSoftRequired();
    }

    public PreviousHospitalizationEditFragmentLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getPrevHosp();
    }
}
