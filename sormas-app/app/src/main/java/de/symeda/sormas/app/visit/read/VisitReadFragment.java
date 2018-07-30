package de.symeda.sormas.app.visit.read;

import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.databinding.FragmentVisitReadLayoutBinding;

public class VisitReadFragment extends BaseReadFragment<FragmentVisitReadLayoutBinding, Visit, Visit> {

    private Visit record;

    public static VisitReadFragment newInstance(Visit activityRootData) {
        return newInstance(VisitReadFragment.class, null, activityRootData);
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentVisitReadLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_followup_information);
    }

    @Override
    public Visit getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_visit_read_layout;
    }

}
