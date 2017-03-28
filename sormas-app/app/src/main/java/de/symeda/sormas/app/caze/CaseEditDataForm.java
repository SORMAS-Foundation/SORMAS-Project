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

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.databinding.CaseDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseEditDataForm extends FormTab {

    private CaseDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_data_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String caseUuid = (String) getArguments().getString(Case.UUID);
        final CaseDao caseDao = DatabaseHelper.getCaseDao();
        final Case caze = caseDao.queryUuid(caseUuid);
        binding.setCaze(caze);

        final List emptyList = new ArrayList<>();
        final List districtsByRegion = DataUtils.toItems(caze.getRegion() != null ? DatabaseHelper.getDistrictDao().getByRegion(caze.getRegion()) : DataUtils.toItems(emptyList), true);
        final List communitiesByDistrict = DataUtils.toItems(caze.getDistrict() != null ? DatabaseHelper.getCommunityDao().getByDistrict(caze.getDistrict()) : DataUtils.toItems(emptyList), true);
        final List facilitiesByCommunity = DataUtils.toItems(caze.getCommunity() != null ? DatabaseHelper.getFacilityDao().getByCommunity(caze.getCommunity()) : DataUtils.toItems(emptyList), true);

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
                    districtSpinner.setAdapterAndValue(binding.caseDataDistrict.getValue(), DataUtils.toItems(districtList));
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
                        facilityList = DatabaseHelper.getFacilityDao().getByCommunity((Community)selectedValue);
                    }
                    spinnerField.setAdapterAndValue(binding.caseDataHealthFacility.getValue(), DataUtils.toItems(facilityList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FieldHelper.initSpinnerField(binding.caseDataHealthFacility, facilitiesByCommunity);

        FieldHelper.initSpinnerField(binding.caseDataMeaslesVaccination, Vaccination.class);
        FieldHelper.initSpinnerField(binding.caseDataVaccinationInfoSource, VaccinationInfoSource.class);

        boolean definedOrMissing = Diseases.DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, binding.caseDataMeaslesVaccination.getPropertyId(), binding.getCaze().getDisease());
        binding.caseDataMeaslesVaccination.setVisibility(definedOrMissing ? View.VISIBLE : View.GONE);

        if (binding.getCaze().getPerson().getSex() != Sex.FEMALE) {
            binding.caseDataPregnant.setVisibility(View.GONE);
        }

        toggleMeaslesFields();

        binding.caseDataMeaslesVaccination.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                toggleMeaslesFields();
            }
        });

        if (binding.caseDataPregnant.getVisibility() == View.GONE && binding.caseDataMeaslesVaccination.getVisibility() == View.GONE) {
            binding.caseMedicalInformationHeadline.setVisibility(View.GONE);
        }
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getCaze();
    }

    private void toggleMeaslesFields() {
        if (binding.caseDataMeaslesVaccination.getVisibility() != View.VISIBLE || binding.caseDataMeaslesVaccination.getValue() != Vaccination.VACCINATED) {
            binding.caseDataDoses.setVisibility(View.GONE);
            binding.caseDataVaccinationInfoSource.setVisibility(View.GONE);
        } else {
            binding.caseDataDoses.setVisibility(View.VISIBLE);
            binding.caseDataVaccinationInfoSource.setVisibility(View.VISIBLE);
        }
    }

}