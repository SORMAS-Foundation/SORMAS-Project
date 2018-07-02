package de.symeda.sormas.app.task.read;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.contact.read.ContactReadActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentTaskReadLayoutBinding;
import de.symeda.sormas.app.event.read.EventReadActivity;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;

/**
 * Created by Orson on 31/12/2017.
 */

public class TaskReadFragment extends BaseReadFragment<FragmentTaskReadLayoutBinding, Task, Task> {

    private AsyncTask onResumeTask;
    private String recordUuid = null;
    private TaskStatus pageStatus = null;
    private Task record;

    private OnLinkClickListener caseLinkCallback;
    private OnLinkClickListener contactLinkCallback;
    private OnLinkClickListener eventLinkCallback;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (TaskStatus) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Task task = getActivityRootData();

            if (task != null) {
                if (task.isUnreadOrChildUnread())
                    DatabaseHelper.getTaskDao().markAsRead(task);
            }

            resultHolder.forItem().add(task);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (record == null) {
                getActivity().finish();
            }

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentTaskReadLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setCaseLinkCallback(caseLinkCallback);
        contentBinding.setContactLinkCallback(contactLinkCallback);
        contentBinding.setEventLinkCallback(eventLinkCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentTaskReadLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentTaskReadLayoutBinding contentBinding, Task task) {

    }

    @Override
    public void onPageResume(FragmentTaskReadLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    //getBaseActivity().showPreloader();
                    //
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
                    Task task = getActivityRootData();

                    if (task != null) {
                        if (task.isUnreadOrChildUnread())
                            DatabaseHelper.getTaskDao().markAsRead(task);
                    }

                    resultHolder.forItem().add(task);
                }
            };
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getBaseActivity().hidePreloader();
                    //getBaseActivity().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record =  itemIterator.next();

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getBaseActivity().hidePreloader();
            //getBaseActivity().showFragmentView();
        }
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_task_information);
    }

    @Override
    public Task getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_task_read_layout;
    }

    private void setupCallback() {
        caseLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (item == null)
                    return;

                Task task = (Task)item;
                Case caze = task.getCaze();

                if (caze == null)
                    return;

                CaseFormNavigationCapsule dataCapsule = (CaseFormNavigationCapsule)new CaseFormNavigationCapsule(getContext(),
                        caze.getUuid(), caze.getCaseClassification()).setTaskUuid(task.getUuid());
                CaseReadActivity.goToActivity(getActivity(), dataCapsule);

            }
        };

        contactLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (item == null)
                    return;

                Task task = (Task)item;
                Contact contact = task.getContact();

                if (contact == null)
                    return;

                ContactFormNavigationCapsule dataCapsule = (ContactFormNavigationCapsule)new ContactFormNavigationCapsule(getContext(),
                        contact.getUuid(), contact.getContactClassification()).setTaskUuid(task.getUuid());
                ContactReadActivity.goToActivity(getActivity(), dataCapsule);
            }
        };

        eventLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (item == null)
                    return;

                Task task = (Task)item;
                Event event = task.getEvent();

                if (event == null)
                    return;

                EventFormNavigationCapsule dataCapsule = (EventFormNavigationCapsule)new EventFormNavigationCapsule(getContext(),
                        event.getUuid(), event.getEventStatus()).setTaskUuid(task.getUuid());
                EventReadActivity.goToActivity(getActivity(), dataCapsule);
            }
        };
    }

    public static TaskReadFragment newInstance(TaskFormNavigationCapsule capsule, Task activityRootData) {
        return newInstance(TaskReadFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }

}
