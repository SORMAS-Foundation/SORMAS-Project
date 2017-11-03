package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.databinding.CaseNewFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.validation.CaseValidator;

public class CaseNewForm extends FormTab {

    public static final String NONE_HEALTH_FACILITY_DETAILS = "noneHealthFacilityDetails";

    private Case caze;
    private Person person;
    private Disease disease;
    private CaseNewFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = this.getArguments();
        // If this case is created from a contact, there's already a disease and person available
        if (arguments.containsKey(CaseNewActivity.DISEASE)) {
            person = (Person) arguments.get(CaseNewActivity.PERSON);
            disease = (Disease) arguments.get(CaseNewActivity.DISEASE);
        } else {
            person = DatabaseHelper.getPersonDao().build();
        }
        caze = DatabaseHelper.getCaseDao().build(person);

        binding = DataBindingUtil.inflate(inflater, R.layout.case_new_fragment_layout, container, false);

        binding.setCaze(caze);

        FieldHelper.initSpinnerField(binding.caseDataDisease, Disease.class);
        FieldHelper.initSpinnerField(binding.caseDataPlagueType, PlagueType.class);

        final List emptyList = new ArrayList<>();
        final List districtsByRegion = DataUtils.toItems(caze.getRegion() != null ? DatabaseHelper.getDistrictDao().getByRegion(caze.getRegion()) : DataUtils.toItems(emptyList), true);
        final List communitiesByDistrict = DataUtils.toItems(caze.getDistrict() != null ? DatabaseHelper.getCommunityDao().getByDistrict(caze.getDistrict()) : DataUtils.toItems(emptyList), true);
        final List facilitiesByCommunity = DataUtils.toItems(caze.getCommunity() != null ? DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(caze.getCommunity(), true) : DataUtils.toItems(emptyList), true);

        FieldHelper.initRegionSpinnerField(binding.caseDataRegion, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField spinnerField = binding.caseDataDistrict;
                Object selectedValue = binding.caseDataRegion.getValue();
                if(spinnerField != null) {
                    List<District> districtList = emptyList;
                    if(selectedValue != null) {
                        districtList = DatabaseHelper.getDistrictDao().getByRegion((Region) selectedValue);
                    }
                    spinnerField.setAdapterAndValue(binding.caseDataDistrict.getValue(), DataUtils.toItems(districtList));
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
                    spinnerField.setAdapterAndValue(binding.caseDataCommunity.getValue(), DataUtils.toItems(communityList));
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
                    spinnerField.setAdapterAndValue(binding.caseDataHealthFacility.getValue(), DataUtils.toItems(facilityList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FieldHelper.initSpinnerField(binding.caseDataHealthFacility, facilitiesByCommunity);

        if (disease != null) {
            binding.caseDataFirstName.setEnabled(false);
            binding.caseDataLastName.setEnabled(false);
            binding.caseDataDisease.setEnabled(false);
            binding.getCaze().setPerson(person);
            binding.getCaze().setDisease(disease);
        }

        binding.caseDataHealthFacility.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                Facility selectedFacility = (Facility) binding.caseDataHealthFacility.getValue();
                if (selectedFacility != null) {
                    boolean otherHealthFacility = selectedFacility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
                    boolean noneHealthFacility = selectedFacility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

                    if (otherHealthFacility) {
                        binding.caseDataFacilityDetails.setVisibility(View.VISIBLE);
                        binding.caseDataFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
                        binding.caseDataFacilityDetails.setRequiredHint(true);
                    } else if (noneHealthFacility) {
                        binding.caseDataFacilityDetails.setVisibility(View.VISIBLE);
                        binding.caseDataFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, NONE_HEALTH_FACILITY_DETAILS));
                        binding.caseDataFacilityDetails.setRequiredHint(true);
                    } else {
                        binding.caseDataFacilityDetails.setVisibility(View.GONE);
                        binding.caseDataFacilityDetails.setValue(null);
                    }
                } else {
                    binding.caseDataFacilityDetails.setVisibility(View.GONE);
                    binding.caseDataFacilityDetails.setValue(null);
                }
            }
        });

        binding.caseDataDisease.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                if (field.getValue() == Disease.OTHER) {
                    binding.caseDataDiseaseDetails.setVisibility(View.VISIBLE);
                } else {
                    binding.caseDataDiseaseDetails.setVisibility(View.GONE);
                }

                if (field.getValue() == Disease.PLAGUE) {
                    binding.caseDataPlagueType.setVisibility(View.VISIBLE);
                } else {
                    binding.caseDataPlagueType.setVisibility(View.GONE);
                }
            }
        });

        CaseValidator.setRequiredHintsForNewCase(binding);

        binding.caseDataRegion.setEnabled(false);
        binding.caseDataDistrict.setEnabled(false);
        User user = ConfigProvider.getUser();
        if (user.getUserRole() == UserRole.INFORMANT
                && user.getHealthFacility() != null) {
            // this is ok, because informants are required to have a community and health facility
            binding.caseDataCommunity.setEnabled(false);
            binding.caseDataHealthFacility.setEnabled(false);
        } else {
            binding.caseDataCommunity.setEnabled(true);
            binding.caseDataHealthFacility.setEnabled(true);
        }

        return binding.getRoot();
    }

    @Override
    public Case getData() {
        return binding == null ? null : binding.getCaze();
    }

    public CaseNewFragmentLayoutBinding getBinding() {
        return binding;
    }

}