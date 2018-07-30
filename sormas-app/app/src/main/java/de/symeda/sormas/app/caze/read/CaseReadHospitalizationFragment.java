package de.symeda.sormas.app.caze.read;

import android.databinding.ObservableArrayList;
import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.databinding.FragmentCaseReadHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;

import static android.view.View.GONE;

public class CaseReadHospitalizationFragment extends BaseReadFragment<FragmentCaseReadHospitalizationLayoutBinding, Hospitalization, Case> {

    public static final String TAG = CaseReadHospitalizationFragment.class.getSimpleName();

    private Case caze;
    private Hospitalization record;
    private ObservableArrayList preHospitalizations = new ObservableArrayList();

    public static CaseReadHospitalizationFragment newInstance(Case activityRootData) {
        return newInstance(CaseReadHospitalizationFragment.class, null, activityRootData);
    }


    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        caze = getActivityRootData();
        record = caze.getHospitalization();
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadHospitalizationLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setCaze(caze);
        contentBinding.setPreviousHospitalizations(getHospitalizations());
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadHospitalizationLayoutBinding contentBinding) {
        InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);

        // Previous hospitalizations list
        if (contentBinding.getData().getPreviousHospitalizations().isEmpty()) {
            contentBinding.listPreviousHospitalizationsLayout.setVisibility(GONE);
        }
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_hospitalization_information);
    }

    @Override
    public Hospitalization getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_hospitalization_layout;
    }

    @SuppressWarnings("unchecked")
    private ObservableArrayList getHospitalizations() {
        if (record != null && preHospitalizations != null)
            preHospitalizations.addAll(record.getPreviousHospitalizations());
        return preHospitalizations;
    }
}
