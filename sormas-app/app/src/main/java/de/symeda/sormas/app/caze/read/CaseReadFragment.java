package de.symeda.sormas.app.caze.read;

import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.databinding.FragmentCaseReadLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;

public class CaseReadFragment extends BaseReadFragment<FragmentCaseReadLayoutBinding, Case, Case> {

    private Case record;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_case_information);
    }

    @Override
    public Case getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_layout;
    }

    public static CaseReadFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseReadFragment.class, capsule, activityRootData);
    }
}
