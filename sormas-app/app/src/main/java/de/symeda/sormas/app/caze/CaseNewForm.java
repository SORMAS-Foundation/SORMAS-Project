package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.databinding.CaseNewFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.FormTab;

public class CaseNewForm extends FormTab {

    private Case caze;
    private Person person;
    private Disease disease;
    private CaseNewFragmentLayoutBinding binding;

    private Tracker tracker;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SormasApplication application = (SormasApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        Bundle arguments = this.getArguments();
        // If this case is created from a contact, there's already a disease and person available
        if (arguments.containsKey(CaseNewActivity.DISEASE)) {
            person = (Person) arguments.get(CaseNewActivity.PERSON);
            disease = (Disease) arguments.get(CaseNewActivity.DISEASE);
        } else {
            person = DataUtils.createNew(Person.class);
        }
        caze = DatabaseHelper.getCaseDao().createCase(person);

        binding = DataBindingUtil.inflate(inflater, R.layout.case_new_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.setCaze(caze);

        FieldHelper.initSpinnerField(binding.caseDataDisease, Disease.class);

        final List emptyList = new ArrayList<>();
        final List districtsByRegion = DataUtils.toItems(caze.getRegion() != null ? DatabaseHelper.getDistrictDao().getByRegion(caze.getRegion()) : DataUtils.toItems(emptyList), true);
        final List communitiesByDistrict = DataUtils.toItems(caze.getDistrict() != null ? DatabaseHelper.getCommunityDao().getByDistrict(caze.getDistrict()) : DataUtils.toItems(emptyList), true);
        final List facilitiesByCommunity = DataUtils.toItems(caze.getCommunity() != null ? DatabaseHelper.getFacilityDao().getByCommunity(caze.getCommunity()) : DataUtils.toItems(emptyList), true);

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

        if (disease != null) {
            binding.caseDataFirstName.setEnabled(false);
            binding.caseDataLastName.setEnabled(false);
            binding.caseDataDisease.setEnabled(false);
            binding.getCaze().setPerson(person);
            binding.getCaze().setDisease(disease);
        }
    }

    @Override
    public Case getData() {
        return binding.getCaze();
    }

}