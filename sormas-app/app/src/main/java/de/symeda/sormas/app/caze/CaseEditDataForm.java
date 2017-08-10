package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.databinding.CaseDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.validation.CaseValidator;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseEditDataForm extends FormTab {

    private CaseDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_data_fragment_layout, container, false);

        final String caseUuid = (String) getArguments().getString(Case.UUID);
        final CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuid(caseUuid);
        DatabaseHelper.getCaseDao().markAsRead(caze);
        caze = caseDao.queryForId(caze.getId());
        binding.setCaze(caze);

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
                        String epidNumber = binding.caseDataEpidNumber.getValue();
                        if (epidNumber.trim().isEmpty() || !epidNumber.matches(DataHelper.getEpidNumberRegexp())) {
                            Calendar calendar = Calendar.getInstance();
                            String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
                            binding.caseDataEpidNumber.setValue(CaseDataDto.COUNTRY_EPID_CODE + "-" + ((Region) binding.caseDataRegion.getValue()).getEpidCode()
                                    + "-" + ((District) binding.caseDataDistrict.getValue()).getEpidCode() + "-" + year + "-");
                        }
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

        // case classification can only be edited by officers, not informants; additionally,
        // the "Not yet classified" classification is hidden from informants
        FieldHelper.initSpinnerField(binding.caseDataCaseOfficerClassification, CaseClassification.class);
        User user = ConfigProvider.getUser();
        if (user.getUserRole() == UserRole.INFORMANT) {
            binding.caseDataCaseOfficerClassification.setVisibility(View.GONE);
            if (binding.getCaze().getCaseClassification() == CaseClassification.NOT_CLASSIFIED) {
                binding.caseDataCaseClassification.setVisibility(View.GONE);
            }
        } else {
            binding.caseDataCaseClassification.setVisibility(View.GONE);
        }

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

        binding.caseDataHealthFacility.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                Facility selectedFacility = (Facility) binding.caseDataHealthFacility.getValue();
                if (selectedFacility != null && selectedFacility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
                    binding.caseDataFacilityDetails.setVisibility(View.VISIBLE);
                } else {
                    binding.caseDataFacilityDetails.setVisibility(View.GONE);
                    binding.caseDataFacilityDetails.setValue(null);
                }
            }
        });

        if (ConfigProvider.getUser().getUserRole() != UserRole.INFORMANT) {
            binding.caseDataEpidNumber.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    String value = (String) field.getValue();
                    if (value.trim().isEmpty()) {
                        field.setErrorWithoutFocus(DatabaseHelper.getContext().getResources().getString(R.string.validation_soft_case_epid_number_empty));
                    } else if (value.matches(DataHelper.getEpidNumberRegexp())) {
                        field.clearError();
                    } else {
                        field.setErrorWithoutFocus(DatabaseHelper.getContext().getResources().getString(R.string.validation_soft_case_epid_number));
                    }
                }
            });
        } else {
            binding.caseDataEpidNumber.setEnabled(false);
        }

        CaseValidator.setRequiredHintsForCaseData(binding);

        return binding.getRoot();
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

    public CaseDataFragmentLayoutBinding getBinding() {
        return binding;
    }
}