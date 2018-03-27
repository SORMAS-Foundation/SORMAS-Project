package de.symeda.sormas.app.contact.read;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadPersonInfoLayoutBinding;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadPersonFragment  extends BaseReadActivityFragment<FragmentContactReadPersonInfoLayoutBinding, Person, Contact> {

    private AsyncTask onResumeTask;
    private String recordUuid;
    private FollowUpStatus followUpStatus;
    private ContactClassification contactClassification = null;
    private Person record;
    private Person person;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, followUpStatus);
        SavePageStatusState(outState, contactClassification);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        followUpStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        contactClassification = (ContactClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Person p = DatabaseHelper.getPersonDao().build();
            Contact contact = getActivityRootData();

            if (contact != null) {
                if (contact.isUnreadOrChildUnread())
                    DatabaseHelper.getContactDao().markAsRead(contact);

                p = DatabaseHelper.getPersonDao().queryUuid(contact.getPerson().getUuid());
            }

            resultHolder.forItem().add(contact);
            resultHolder.forItem().add(p);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (itemIterator.hasNext())
                person =  itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadPersonInfoLayoutBinding contentBinding) {

    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadPersonInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    protected void updateUI(FragmentContactReadPersonInfoLayoutBinding contentBinding, Person person) {

    }

    @Override
    public void onPageResume(FragmentContactReadPersonInfoLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    Person p = DatabaseHelper.getPersonDao().build();
                    Contact contact = getActivityRootData();

                    if (contact != null) {
                        if (contact.isUnreadOrChildUnread())
                            DatabaseHelper.getContactDao().markAsRead(contact);

                        p = DatabaseHelper.getPersonDao().queryUuid(contact.getPerson().getUuid());
                    }

                    resultHolder.forItem().add(contact);
                    resultHolder.forItem().add(p);
                }
            });
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record = itemIterator.next();

                    if (itemIterator.hasNext())
                        person = itemIterator.next();

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_person_information);
    }

    @Override
    public Person getPrimaryData() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_person_info_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static ContactReadPersonFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule, Contact activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactReadPersonFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}

