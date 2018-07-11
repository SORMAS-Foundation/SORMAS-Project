package de.symeda.sormas.app.visit.read;

import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.databinding.FragmentContactReadVisitInfoLayoutBinding;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;

public class VisitReadFragment extends BaseReadFragment<FragmentContactReadVisitInfoLayoutBinding, Visit, Visit> {

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {

    }

    @Override
    public void onLayoutBinding(FragmentContactReadVisitInfoLayoutBinding contentBinding) {
        contentBinding.setData(getActivityRootData());
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_followup_information);
    }

    @Override
    public Visit getPrimaryData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_visit_info_layout;
    }

    public static VisitReadFragment newInstance(VisitFormNavigationCapsule capsule, Visit activityRootData) {
        return newInstance(VisitReadFragment.class, capsule, activityRootData);
    }
}
