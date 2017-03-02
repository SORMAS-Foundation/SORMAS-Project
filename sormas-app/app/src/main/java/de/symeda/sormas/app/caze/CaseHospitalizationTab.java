package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.LabelField;
import de.symeda.sormas.app.databinding.CaseHospitalizationFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

public class CaseHospitalizationTab extends FormTab {

    public static final String KEY_CASE_UUID = "caseUuid";

    private CaseHospitalizationFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_hospitalization_fragment_layout, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final String caseUuid = getArguments().getString(CaseHospitalizationTab.KEY_CASE_UUID);
        final Case caze = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
        if(caze.getHealthFacility()!=null){
            ((LabelField) getView().findViewById(R.id.hospitalization_healthFacility)).setValue(caze.getHealthFacility().toString());
        }




        final String hospitalizationUuid = getArguments().getString(Hospitalization.UUID);
        if(hospitalizationUuid != null) {
            final Hospitalization hospitalization = DatabaseHelper.getHospitalizationDao().queryUuid(hospitalizationUuid);
            binding.setHospitalization(hospitalization);
        } else {
            binding.setHospitalization(new Hospitalization());
        }

        binding.hospitalizationAdmissionDate.initialize(this);
        binding.hospitalizationDischargeDate.initialize(this);
        binding.hospitalization1isolationDate.initialize(this);
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getHospitalization();
    }

}
