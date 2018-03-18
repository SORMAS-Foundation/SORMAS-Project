package de.symeda.sormas.app.contact.read.sub;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.contact.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadVisitInfoLayoutBinding;

/**
 * Created by Orson on 02/01/2018.
 */

public class ContactReadFollowUpVisitInfoFragment extends BaseReadActivityFragment<FragmentContactReadVisitInfoLayoutBinding, Visit> {

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

        pageStatus = (VisitStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            resultHolder.forItem().add(DatabaseHelper.getVisitDao().queryUuid(recordUuid));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();
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
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public Visit getPrimaryData() {
        return record;
    }

    /*@Override
    public FragmentContactReadVisitInfoLayoutBinding getBinding() {
        return binding;
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_visit_info_layout;
    }

    public static ContactReadFollowUpVisitInfoFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormFollowUpNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactReadFollowUpVisitInfoFragment.class, capsule);
    }


}
