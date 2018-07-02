package de.symeda.sormas.app.contact.read.sub;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadVisitInfoLayoutBinding;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;

/**
 * Created by Orson on 02/01/2018.
 */

public class VisitReadFragment extends BaseReadFragment<FragmentContactReadVisitInfoLayoutBinding, Visit, Visit> {

    private String recordUuid;
    private VisitStatus pageStatus;
    private Visit record;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null) ? savedInstanceState : getArguments();

        pageStatus = (VisitStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Visit visit = getActivityRootData();

            if (visit != null) {
                if (visit.isUnreadOrChildUnread())
                    DatabaseHelper.getVisitDao().markAsRead(visit);
            }

            resultHolder.forItem().add(visit);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadVisitInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadVisitInfoLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentContactReadVisitInfoLayoutBinding contentBinding, Visit visit) {

    }

    @Override
    public void onPageResume(FragmentContactReadVisitInfoLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;
        record = getActivityRootData();
    }

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_visit_info_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static VisitReadFragment newInstance(VisitFormNavigationCapsule capsule, Visit activityRootData) {
        return newInstance(VisitReadFragment.class, capsule, activityRootData);
    }
}
