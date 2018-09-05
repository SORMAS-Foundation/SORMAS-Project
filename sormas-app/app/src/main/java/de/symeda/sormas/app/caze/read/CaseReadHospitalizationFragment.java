package de.symeda.sormas.app.caze.read;

import android.databinding.ObservableArrayList;
import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.databinding.FragmentCaseReadHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;

import static android.view.View.GONE;

public class CaseReadHospitalizationFragment extends BaseReadFragment<FragmentCaseReadHospitalizationLayoutBinding, Hospitalization, Case> {

    public static final String TAG = CaseReadHospitalizationFragment.class.getSimpleName();

    private Case caze;
    private Hospitalization record;

    // Static methods

    public static CaseReadHospitalizationFragment newInstance(Case activityRootData) {
        return newInstance(CaseReadHospitalizationFragment.class, null, activityRootData);
    }

    // Overrides

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        caze = getActivityRootData();
        record = caze.getHospitalization();
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadHospitalizationLayoutBinding contentBinding) {
        ObservableArrayList<PreviousHospitalization> previousHospitalizations = new ObservableArrayList<>();
        previousHospitalizations.addAll(record.getPreviousHospitalizations());

        contentBinding.setData(record);
        contentBinding.setCaze(caze);
        contentBinding.setPreviousHospitalizationList(previousHospitalizations);
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
        return getResources().getString(R.string.caption_case_hospitalization);
    }

    @Override
    public Hospitalization getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_hospitalization_layout;
    }

}
