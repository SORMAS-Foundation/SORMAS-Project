package de.symeda.sormas.app.task.list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.searchstrategy.ISearchExecutor;
import de.symeda.sormas.app.searchstrategy.ISearchResultCallback;
import de.symeda.sormas.app.searchstrategy.SearchStrategyFor;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.read.TaskReadActivity;
import de.symeda.sormas.app.util.SubheadingHelper;

/**
 * Created by Orson on 02/12/2017.
 */

public class TaskListFragment extends BaseListActivityFragment<TaskListAdapter> implements OnListItemClickListener {


    private boolean dataLoaded = false;
    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_EVENT_UUID = "eventUuid";

    private AsyncTask searchTask;
    private TaskStatus filterStatus = null;
    private SearchBy searchBy = null;
    String recordUuid = null;

    private List<Task> tasks;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchBy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        filterStatus = (TaskStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView)view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public TaskListAdapter getNewListAdapter() {
        return new TaskListAdapter(this.getActivity(), R.layout.row_task_list_item_layout, this,
                this.tasks);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task t = (Task)item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                t.getUuid(), t.getTaskStatus());
        TaskReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    @Override
    public void cancelTaskExec() {
        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }

    @Override
    protected int getEmptyListEntityResId() {
        return R.string.entity_task;
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO: Orson - reverse this relationship
        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), searchBy, filterStatus, "Task"));

        try {
            dataLoaded = false;
            if (!dataLoaded) {
                ISearchExecutor<Task> executor = SearchStrategyFor.TASK.selector(searchBy, filterStatus, recordUuid);
                searchTask = executor.search(new ISearchResultCallback<Task>() {
                    @Override
                    public void preExecute() {
                        getActivityCommunicator().showPreloader();
                        getActivityCommunicator().hideFragmentView();
                    }

                    @Override
                    public void searchResult(List<Task> result, BoolResult resultStatus) {
                        if (!resultStatus.isSuccess()) {
                            String message = String.format(getResources().getString(R.string.notification_records_not_retrieved), "Tasks");
                            NotificationHelper.showNotification((INotificationContext) getActivity(), NotificationType.ERROR, message);

                            return;
                        }

                        tasks = result;

                        TaskListFragment.this.getListAdapter().replaceAll(tasks);
                        TaskListFragment.this.getListAdapter().notifyDataSetChanged();

                        dataLoaded = true;

                        getActivityCommunicator().hidePreloader();
                        getActivityCommunicator().showFragmentView();
                    }
                    private ISearchResultCallback<Task> init() {
                        getActivityCommunicator().showPreloader();

                        return this;
                    }
                }.init());
            }
        } catch (Exception ex) {
            getActivityCommunicator().hidePreloader();
            getActivityCommunicator().showFragmentView();
            dataLoaded = false;
        }

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getActivityCommunicator().synchronizeData(SynchronizeDataAsync.SyncMode.Changes, true, false, true, swiperefresh, null);
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }

    public static TaskListFragment newInstance(IActivityCommunicator communicator, IListNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(communicator, TaskListFragment.class, capsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }

}
