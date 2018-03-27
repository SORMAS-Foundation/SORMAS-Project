package de.symeda.sormas.app.caze.read;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadTaskLayoutBinding;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.read.TaskReadActivity;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadTaskListFragment extends BaseReadActivityFragment<FragmentCaseReadTaskLayoutBinding, List<Task>, Case> implements OnListItemClickListener {

    private AsyncTask onResumeTask;
    private String recordUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private List<Task> record;

    private CaseReadTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

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
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case caze = getActivityRootData();
            List<Task> taskList = new ArrayList<Task>();

            //Case caze = DatabaseHelper.getCaseDao().queryUuidReference(recordUuid);
            if (caze != null) {
                if (caze.isUnreadOrChildUnread())
                    DatabaseHelper.getCaseDao().markAsRead(caze);

                if (caze.getPerson() == null) {
                    caze.setPerson(DatabaseHelper.getPersonDao().build());
                }

                taskList = DatabaseHelper.getTaskDao().queryByCase(caze);
            }

            resultHolder.forList().add(taskList);
        } else {
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();
            if (listIterator.hasNext())
                record = listIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadTaskLayoutBinding contentBinding) {
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new CaseReadTaskListAdapter(this.getActivity(), R.layout.row_read_case_task_list_item_layout, this, record);
        getContentBinding().recyclerViewForList.setLayoutManager(linearLayoutManager);
        getContentBinding().recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadTaskLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentCaseReadTaskLayoutBinding contentBinding, List<Task> tasks) {

    }

    @Override
    public void onPageResume(FragmentCaseReadTaskLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getActivityCommunicator().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, true, swiperefresh, null);
                }
            });
        }

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
                    Case caze = getActivityRootData();
                    List<Task> taskList = new ArrayList<Task>();

                    //Case caze = DatabaseHelper.getCaseDao().queryUuidReference(recordUuid);
                    if (caze != null) {
                        if (caze.isUnreadOrChildUnread())
                            DatabaseHelper.getCaseDao().markAsRead(caze);

                        if (caze.getPerson() == null) {
                            caze.setPerson(DatabaseHelper.getPersonDao().build());
                        }

                        taskList = DatabaseHelper.getTaskDao().queryByCase(caze);
                    }

                    resultHolder.forList().add(taskList);
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

                    ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

                    if (listIterator.hasNext())
                        record = listIterator.next();

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
        return r.getString(R.string.caption_case_tasks);
    }

    @Override
    public List<Task> getPrimaryData() {
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //recyclerViewForList.setLayoutManager(linearLayoutManager);
        //recyclerViewForList.setAdapter(adapter);
        //binding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        //binding.recyclerViewForList.setAdapter(adapter);
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_root_list_edit_layout;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_task_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task task = (Task)item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                task.getUuid(), task.getTaskStatus());
        TaskReadActivity.goToActivity(getActivity(), dataCapsule);


        /*Task record = (Task)item;

        if(record != null) {
            Intent intent = new Intent(getActivity(), CaseReadTaskInfoActivity.class);
            //intent.putExtra(ConstantHelper.ARG_FILTER_STATUS, record.getContact().getFollowUpStatus());
            intent.putExtra(ConstantHelper.KEY_DATA_UUID, record.getUuid());
            intent.putExtra(IStatusElaborator.ARG_TASK_STATUS, record.getTaskStatus());
            intent.putExtra(IStatusElaborator.ARG_FOLLOW_UP_STATUS, followUpStatus);

            startActivity(intent);
        }*/
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static CaseReadTaskListFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule, Case activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseReadTaskListFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
