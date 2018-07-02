package de.symeda.sormas.app.caze.read;

import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.FragmentCaseReadPatientInfoLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;

public class CaseReadPersonFragment extends BaseReadFragment<FragmentCaseReadPatientInfoLayoutBinding, Person, Case> {

    public static final String TAG = CaseReadPersonFragment.class.getSimpleName();

    private Person record;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Case caze = getActivityRootData();
        record = caze.getPerson();
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadPatientInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_patient_information);
    }

    @Override
    public Person getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_patient_info_layout;
    }

    public static CaseReadPersonFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseReadPersonFragment.class, capsule, activityRootData);
    }
}
