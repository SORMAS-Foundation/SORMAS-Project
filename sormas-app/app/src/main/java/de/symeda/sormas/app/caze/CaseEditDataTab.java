package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseHelper;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDao;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.databinding.CaseDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseEditDataTab extends FormTab {

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
        final List districtsByRegion = toItems(caze.getRegion() != null ? DatabaseHelper.getDistrictDao().getByRegion(caze.getRegion()) : DataUtils.getItems(emptyList), true);
        final List communitiesByDistrict = toItems(caze.getDistrict() != null ? DatabaseHelper.getCommunityDao().getByDistrict(caze.getDistrict()) : DataUtils.getItems(emptyList), true);

        DataUtils.initFacilitySpinnerField(binding.caseDataHealthFacility);

        DataUtils.initRegionSpinnerField(binding.caseDataRegion, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField districtSpinner = binding.caseDataDistrict;
                Object selectedValue = binding.caseDataRegion.getValue();
                if(districtSpinner != null) {
                    List<District> districtList = emptyList;
                    if(selectedValue != null) {
                        districtList = DatabaseHelper.getDistrictDao().getByRegion((Region)selectedValue);
                    }
                    districtSpinner.setAdapterAndValue(binding.caseDataDistrict.getValue(), DataUtils.getItems(districtList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DataUtils.initSpinnerField(binding.caseDataDistrict, districtsByRegion, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField spinnerField = binding.caseDataCommunity;
                Object selectedValue = binding.caseDataDistrict.getValue();
                if(spinnerField != null) {
                    List<Community> communityList = emptyList;
                    if(selectedValue != null) {
                        communityList = DatabaseHelper.getCommunityDao().getByDistrict((District)selectedValue);
                    }
                    spinnerField.setAdapterAndValue(binding.caseDataCommunity.getValue(), DataUtils.getItems(communityList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        DataUtils.initSpinnerField(binding.caseDataCommunity, communitiesByDistrict, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField spinnerField = binding.caseDataHealthFacility;
                Object selectedValue = binding.caseDataCommunity.getValue();
                if(spinnerField != null) {
                    List<Facility> facilityList = emptyList;
                    if(selectedValue != null) {
                        facilityList = DatabaseHelper.getFacilityDao().getByCommunity((Community)selectedValue);
                    }
                    spinnerField.setAdapterAndValue(binding.caseDataHealthFacility.getValue(), DataUtils.getItems(facilityList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnCaseAdministration = binding.formCdBtnCaseAdministration;
        Iterable<CaseStatus> possibleStatus = CaseHelper.getPossibleStatusChanges(caze.getCaseStatus(), ConfigProvider.getUser().getUserRole());
        if(possibleStatus.iterator().hasNext()) {
            btnCaseAdministration.setVisibility(View.VISIBLE);
            final CaseStatus caseStatus = possibleStatus.iterator().next();
            btnCaseAdministration.setText(caseStatus.getChangeString());
            btnCaseAdministration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    caseDao.changeCaseStatus(caze, caseStatus);
                    reloadFragment();
                }
            });
        }
        else {
            btnCaseAdministration.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        return null;
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getCaze();
    }

}