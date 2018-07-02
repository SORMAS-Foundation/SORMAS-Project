package de.symeda.sormas.app.contact.edit.sub;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactEditVisitInfoLayoutBinding;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;

public class VisitEditFragment extends BaseEditActivityFragment<FragmentContactEditVisitInfoLayoutBinding, Visit, Visit> {

    private String recordUuid;
    private String contactUuid = null;
    private VisitStatus pageStatus;
    private Visit record;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
        saveContactUuidState(outState, contactUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        pageStatus = (VisitStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
        contactUuid = getContactUuidArg(arguments);
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
                record =  itemIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactEditVisitInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setVisitStatusClass(VisitStatus.class);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactEditVisitInfoLayoutBinding contentBinding) {
        contentBinding.dtpDateTimeOfVisit.setFragmentManager(getFragmentManager());
    }

    @Override
    protected void updateUI(FragmentContactEditVisitInfoLayoutBinding contentBinding, Visit visit) {

    }

    @Override
    public void onPageResume(FragmentContactEditVisitInfoLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;
        record = getActivityRootData();
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_edit_visit_info_layout;
    }

    @Override
    public boolean showSaveAction() {
        return true;
    }

    @Override
    public boolean showAddAction() {
        return false;
    }

    private void setupCallback() {

    }

    public static VisitEditFragment newInstance(VisitFormNavigationCapsule capsule, Visit activityRootData) {
        return newInstance(VisitEditFragment.class, capsule, activityRootData);
    }
}
