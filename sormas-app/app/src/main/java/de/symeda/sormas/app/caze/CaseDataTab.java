package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.symeda.sormas.api.caze.CaseHelper;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDao;
import de.symeda.sormas.app.databinding.CazeDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseDataTab extends FormTab {

    private CazeDataFragmentLayoutBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initModel();
        binding = DataBindingUtil.inflate(inflater, R.layout.caze_data_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String caseUuid = (String) getArguments().getString(Case.UUID);
        final CaseDao caseDao = DatabaseHelper.getCaseDao();
        final Case caze = caseDao.queryUuid(caseUuid);

        binding.setCaze(caze);

        FacilityDao facilityDao = DatabaseHelper.getFacilityDao();
        getModel().put(R.id.form_cd_health_facility,facilityDao.queryForId(caze.getHealthFacility().getId()));
        addFacilitySpinnerField(R.id.form_cd_health_facility);

        Button btnCaseAdministration = (Button) getView().findViewById(R.id.form_cd_btn_case_administration);
        Iterable<CaseStatus> possibleStatus = CaseHelper.getPossibleStatusChanges(caze.getCaseStatus(), UserRole.SURVEILLANCE_OFFICER);
        if(possibleStatus.iterator().hasNext()) {
            final CaseStatus caseStatus = possibleStatus.iterator().next();

            btnCaseAdministration.setText("change to " + caseStatus.toString());
            btnCaseAdministration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    caseDao.changeCaseStatus(caze, caseStatus);
                    reloadFragment();
                }
            });
        }
        else {
            btnCaseAdministration.setText("changed to " + caze.getCaseStatus().toString());
            btnCaseAdministration.setEnabled(false);
        }

    }

    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        Case caze = (Case) ado;

        caze.setHealthFacility((Facility) getModel().get(R.id.form_cd_health_facility));

        return caze;
    }

    @Override
    public AbstractDomainObject getData() {
        return commit(binding.getCaze());
    }

}