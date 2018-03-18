package de.symeda.sormas.app.contact.edit.sub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.contact.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactEditVisitInfoLayoutBinding;

/**
 * Created by Orson on 13/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactEditFollowUpVisitInfoFragment extends BaseEditActivityFragment<FragmentContactEditVisitInfoLayoutBinding, Visit> {

    private AsyncTask jobTask;
    private String recordUuid;
    private VisitStatus pageStatus;
    private Visit record;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (VisitStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public Visit getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            if (recordUuid == null || recordUuid.isEmpty()) {
                resultHolder.forItem().add(DatabaseHelper.getVisitDao().build());
            } else {
                resultHolder.forItem().add(DatabaseHelper.getVisitDao().queryUuid(recordUuid));
            }
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            //Item Data
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
        contentBinding.dtpDateTimeOfVisit.initialize(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_edit_visit_info_layout;
    }

    private void setupCallback() {

    }

    public static ContactEditFollowUpVisitInfoFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormFollowUpNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactEditFollowUpVisitInfoFragment.class, capsule);
    }
}
