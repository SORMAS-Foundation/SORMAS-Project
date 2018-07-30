package de.symeda.sormas.app.task.list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.searchstrategy.ISearchExecutor;
import de.symeda.sormas.app.searchstrategy.ISearchResultCallback;
import de.symeda.sormas.app.searchstrategy.SearchStrategyFor;
import de.symeda.sormas.app.task.read.TaskReadActivity;
import de.symeda.sormas.app.util.SubheadingHelper;

public class TaskListFragment extends BaseListFragment<TaskListAdapter> implements OnListItemClickListener {

    private AsyncTask searchTask;
    private List<Task> tasks;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    public static TaskListFragment newInstance(TaskStatus listFilter) {
        return newInstance(TaskListFragment.class, null, listFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView) view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public TaskListAdapter getNewListAdapter() {
        return new TaskListAdapter(this.getActivity(), R.layout.row_task_list_item_layout, this, this.tasks);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task t = (Task) item;
        TaskReadActivity.startActivity(getContext(), t.getUuid());
    }

    @Override
    protected int getEmptyListEntityResId() {
        return R.string.entity_task;
    }

    @Override
    public void onResume() {
        super.onResume();

        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), SearchBy.BY_FILTER_STATUS, getListFilter(), "Task"));

        ISearchExecutor<Task> executor = SearchStrategyFor.TASK.selector(SearchBy.BY_FILTER_STATUS, getListFilter(), null);
        searchTask = executor.search(new ISearchResultCallback<Task>() {
            @Override
            public void preExecute() {
                getBaseActivity().showPreloader();
            }

            @Override
            public void searchResult(List<Task> result, BoolResult resultStatus) {
                getBaseActivity().hidePreloader();

                if (!resultStatus.isSuccess()) {
                    String message = String.format(getResources().getString(R.string.notification_records_not_retrieved), "Tasks");
                    NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, message);
                    return;
                }

                tasks = result;

                if (TaskListFragment.this.isResumed()) {
                    TaskListFragment.this.getListAdapter().replaceAll(tasks);
                    TaskListFragment.this.getListAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }

}
