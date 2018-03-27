package de.symeda.sormas.app.event.read.sub;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEventReadTaskInfoLayoutBinding;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;

/**
 * Created by Orson on 30/12/2017.
 */

public class EventReadTaskInfoFragment extends BaseReadActivityFragment<FragmentEventReadTaskInfoLayoutBinding, Task, Task> {

    private AsyncTask onResumeTask;
    private String recordUuid = null;
    private TaskStatus pageStatus = null;
    private Task record;

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
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEventReadTaskInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventReadTaskInfoLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentEventReadTaskInfoLayoutBinding contentBinding, Task task) {

    }

    @Override
    public void onPageResume(FragmentEventReadTaskInfoLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
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
                    Task task = getActivityRootData();

                    if (task != null) {
                        if (task.isUnreadOrChildUnread())
                            DatabaseHelper.getTaskDao().markAsRead(task);
                    }

                    resultHolder.forItem().add(task);
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
                        record =  itemIterator.next();

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
        return r.getString(R.string.caption_task_information);
    }

    @Override
    public Task getPrimaryData() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_event_read_task_info_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static EventReadTaskInfoFragment newInstance(IActivityCommunicator activityCommunicator, TaskFormNavigationCapsule capsule, Task activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, EventReadTaskInfoFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
