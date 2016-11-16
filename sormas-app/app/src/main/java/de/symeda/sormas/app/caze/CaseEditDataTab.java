package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
        initModel();
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

        getModel().put(R.id.case_region_cd, caze.getRegion()!=null?DatabaseHelper.getRegionDao().queryUuid(caze.getRegion().getUuid()):null);
        getModel().put(R.id.case_district_cd, caze.getDistrict()!=null?DatabaseHelper.getDistrictDao().queryUuid(caze.getDistrict().getUuid()):null);
        getModel().put(R.id.case_community_cd, caze.getCommunity()!=null?DatabaseHelper.getCommunityDao().queryUuid(caze.getCommunity().getUuid()):null);
        getModel().put(R.id.case_healthFacility_cd, caze.getHealthFacility()!=null?DatabaseHelper.getFacilityDao().queryUuid(caze.getHealthFacility().getUuid()):null);

        addFacilitySpinnerField(R.id.case_healthFacility_cd);

        addRegionSpinnerField(getModel(), getView(), R.id.case_region_cd, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = binding.caseDistrictCd;
                Object selectedValue = getModel().get(R.id.case_region_cd);
                if(spinner != null) {
                    List<District> districtList = emptyList;
                    if(selectedValue != null) {
                        districtList = DatabaseHelper.getDistrictDao().getByRegion((Region)selectedValue);
                    }
                    setSpinnerValue(getModel().get(R.id.case_district_cd), DataUtils.getItems(districtList), spinner);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        addSpinnerField(R.id.case_district_cd, DataUtils.getItems(emptyList), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) getView().findViewById(R.id.case_community_cd);
                Object selectedValue = getModel().get(R.id.case_district_cd);
                if(spinner != null) {
                    List<Community> communityList = emptyList;
                    if(selectedValue != null) {
                        communityList = DatabaseHelper.getCommunityDao().getByDistrict((District)selectedValue);
                    }
                    setSpinnerValue(getModel().get(R.id.case_community_cd), DataUtils.getItems(communityList), spinner);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addSpinnerField(R.id.case_community_cd, DataUtils.getItems(emptyList), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) getView().findViewById(R.id.case_healthFacility_cd);
                Object selectedValue = getModel().get(R.id.case_community_cd);
                if(spinner != null) {
                    List<Facility> facilityList = emptyList;
                    if(selectedValue != null) {
                        facilityList = DatabaseHelper.getFacilityDao().getByCommunity((Community)selectedValue);
                    }
                    setSpinnerValue(getModel().get(R.id.case_healthFacility_cd), DataUtils.getItems(facilityList), spinner);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnCaseAdministration = (Button) getView().findViewById(R.id.form_cd_btn_case_administration);
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
        Case caze = (Case) ado;

        caze.setRegion((Region)getModel().get(R.id.case_region_cd));
        caze.setDistrict((District)getModel().get(R.id.case_district_cd));
        caze.setCommunity((Community) getModel().get(R.id.case_community_cd));
        caze.setHealthFacility((Facility) getModel().get(R.id.case_healthFacility_cd));

        return caze;
    }

    @Override
    public AbstractDomainObject getData() {
        return commit(binding.getCaze());
    }

}