package de.symeda.sormas.app.visit.edit;

import android.content.res.Resources;
import android.os.Bundle;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.databinding.FragmentContactEditVisitInfoLayoutBinding;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;

public class VisitEditFragment extends BaseEditFragment<FragmentContactEditVisitInfoLayoutBinding, Visit, Visit> {

    private Visit record;

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_followup_information);
    }

    @Override
    public Visit getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentContactEditVisitInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setVisitStatusClass(VisitStatus.class);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactEditVisitInfoLayoutBinding contentBinding) {
        contentBinding.visitVisitDateTime.initializeDateTimeField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_edit_visit_info_layout;
    }

    public static VisitEditFragment newInstance(VisitFormNavigationCapsule capsule, Visit activityRootData) {
        return newInstance(VisitEditFragment.class, capsule, activityRootData);
    }
}
